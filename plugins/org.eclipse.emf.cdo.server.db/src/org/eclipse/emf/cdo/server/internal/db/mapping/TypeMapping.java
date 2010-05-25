/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 271444: [DB] Multiple refactorings
 *    Stefan Winkler - bug 275303: [DB] DBStore does not handle BIG_INTEGER and BIG_DECIMAL
 *    Kai Schlamp - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 282976: [DB] Influence Mappings through EAnnotations
 *    Stefan Winkler - bug 285270: [DB] Support XSD based models
 *    Heiko Ahlig - bug 309461
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IExternalReferenceManager;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;
import org.eclipse.emf.cdo.server.internal.db.MetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a default implementation for the {@link ITypeMapping} interface which provides default behavor for all common
 * types.
 * 
 * @author Eike Stepper
 */
public abstract class TypeMapping implements ITypeMapping
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TypeMapping.class);

  private IMappingStrategy mappingStrategy;

  private EStructuralFeature feature;

  private IDBField field;

  private DBType dbType;

  /**
   * Create a new type mapping
   * 
   * @param mappingStrategy
   *          the associated mapping strategy.
   * @param feature
   *          the feature to be mapped.
   */
  protected TypeMapping(IMappingStrategy mappingStrategy, EStructuralFeature feature, DBType type)
  {
    this.mappingStrategy = mappingStrategy;
    this.feature = feature;
    dbType = type;
  }

  public final IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  public final void setValueFromRevision(PreparedStatement stmt, int index, InternalCDORevision revision)
      throws SQLException
  {
    setValue(stmt, index, getRevisionValue(revision));
  }

  public void setDefaultValue(PreparedStatement stmt, int index) throws SQLException
  {
    setValue(stmt, index, getDefaultValue());
  }

  public final void setValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    if (value == CDORevisionData.NIL)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("TypeMapping for {0}: converting Revision.NIL to DB-null", feature.getName()); //$NON-NLS-1$
      }

      stmt.setNull(index, getSQLType());
    }
    else if (value == null)
    {
      if (feature.isMany() || getDefaultValue() == null)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("TypeMapping for {0}: writing Revision.null as DB.null", feature.getName()); //$NON-NLS-1$
        }

        stmt.setNull(index, getSQLType());
      }
      else
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("TypeMapping for {0}: converting Revision.null to default value", feature.getName()); //$NON-NLS-1$
        }

        setDefaultValue(stmt, index);
      }
    }
    else
    {
      doSetValue(stmt, index, value);
    }
  }

  public final void createDBField(IDBTable table)
  {
    createDBField(table, mappingStrategy.getFieldName(feature));
  }

  public final void createDBField(IDBTable table, String fieldName)
  {
    DBType fieldType = getDBType();
    int fieldLength = getDBLength(fieldType);
    field = table.addField(fieldName, fieldType, fieldLength);
  }

  public final void setDBField(IDBTable table, String fieldName)
  {
    field = table.getField(fieldName);
  }

  public final IDBField getField()
  {
    return field;
  }

  public final void readValueToRevision(ResultSet resultSet, InternalCDORevision revision) throws SQLException
  {
    Object value = readValue(resultSet);
    revision.setValue(getFeature(), value);
  }

  public final Object readValue(ResultSet resultSet) throws SQLException
  {
    Object value = getResultSetValue(resultSet);
    if (resultSet.wasNull())
    {
      if (feature.isMany())
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("TypeMapping for {0}: read db.null - setting Revision.null", feature.getName()); //$NON-NLS-1$
        }

        value = null;
      }
      else
      {
        if (getDefaultValue() == null)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format(
                "TypeMapping for {0}: read db.null - setting Revision.null, because of default", feature.getName()); //$NON-NLS-1$
          }

          value = null;
        }
        else
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("TypeMapping for {0}: read db.null - setting Revision.NIL", feature.getName()); //$NON-NLS-1$
          }

          value = CDORevisionData.NIL;
        }
      }
    }

    return value;
  }

  protected Object getDefaultValue()
  {
    return feature.getDefaultValue();
  }

  protected final Object getRevisionValue(InternalCDORevision revision)
  {
    return revision.getValue(getFeature());
  }

  protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    stmt.setObject(index, value, getSQLType());
  }

  /**
   * Returns the SQL type of this TypeMapping. The default implementation considers the type map hold by the meta-data
   * manager (@see {@link MetaDataManager#getDBType(org.eclipse.emf.ecore.EClassifier)} Subclasses may override.
   * 
   * @return The sql type of this TypeMapping.
   */
  protected int getSQLType()
  {
    return getDBType().getCode();
  }

  public DBType getDBType()
  {
    return dbType;
  }

  protected int getDBLength(DBType type)
  {
    String value = DBAnnotation.COLUMN_LENGTH.getValue(feature);
    if (value != null)
    {
      try
      {
        return Integer.parseInt(value);
      }
      catch (NumberFormatException e)
      {
        OM.LOG.error("Illegal columnLength annotation of feature " + feature.getName());
      }
    }

    // TODO: implement DBAdapter.getDBLength
    // mappingStrategy.getStore().getDBAdapter().getDBLength(type);
    // which should then return the correct default field length for the db type
    return type == DBType.VARCHAR ? 32672 : IDBField.DEFAULT;
  }

  protected abstract Object getResultSetValue(ResultSet resultSet) throws SQLException;

  /**
   * @author Eike Stepper
   */
  public static class TMEnum extends TypeMapping
  {
    public TMEnum(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      // see Bug 271941
      return resultSet.getInt(getField().getName());
      // EEnum type = (EEnum)getFeature().getEType();
      // int value = resultSet.getInt(column);
      // return type.getEEnumLiteral(value);
    }

    @Override
    protected Object getDefaultValue()
    {
      EEnum eenum = (EEnum)getFeature().getEType();

      String defaultValueLiteral = getFeature().getDefaultValueLiteral();
      if (defaultValueLiteral != null)
      {
        EEnumLiteral literal = eenum.getEEnumLiteralByLiteral(defaultValueLiteral);
        if (literal == null)
        {
          OM.LOG.warn(MessageFormat.format(
              Messages.getString("DBStore.13"), getFeature().getDefaultValueLiteral(), getFeature())); //$NON-NLS-1$
          literal = (EEnumLiteral)eenum.getDefaultValue();
        }

        return literal.getValue();
      }

      Enumerator enumerator = (Enumerator)eenum.getDefaultValue();
      return enumerator.getValue();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMString extends TypeMapping
  {
    public TMString(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getString(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMShort extends TypeMapping
  {
    public TMShort(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getShort(getField().getName());
    }
  }

  /**
   * @author Eike Stepper <br>
   */
  public static class TMObject extends TypeMapping
  {
    public TMObject(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      long id = resultSet.getLong(getField().getName());
      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      IExternalReferenceManager externalRefs = getMappingStrategy().getStore().getExternalReferenceManager();
      return CDODBUtil.convertLongToCDOID(externalRefs, getAccessor(), id);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      IDBStore store = getMappingStrategy().getStore();
      IExternalReferenceManager externalReferenceManager = store.getExternalReferenceManager();
      CommitContext commitContext = StoreThreadLocal.getCommitContext();
      long commitTime = commitContext.getBranchPoint().getTimeStamp();
      long id = CDODBUtil.convertCDOIDToLong(externalReferenceManager, getAccessor(), (CDOID)value, commitTime);
      super.doSetValue(stmt, index, id);
    }

    private IDBStoreAccessor getAccessor()
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      if (accessor == null)
      {
        throw new IllegalStateException("Can only be called from within a valid IDBStoreAccessor context");
      }

      return (IDBStoreAccessor)accessor;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMLong extends TypeMapping
  {
    public TMLong(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getLong(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMInteger extends TypeMapping
  {
    public TMInteger(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getInt(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMFloat extends TypeMapping
  {
    public TMFloat(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getFloat(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMDouble extends TypeMapping
  {
    public TMDouble(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getDouble(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMDate2Timestamp extends TypeMapping
  {
    public TMDate2Timestamp(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getTimestamp(getField().getName());
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setTimestamp(index, new Timestamp(((Date)value).getTime()));
    }
  }

  /**
   * @author Heiko Ahlig
   */
  public static class TMDate2Date extends TypeMapping
  {
    public TMDate2Date(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getDate(getField().getName(), Calendar.getInstance());
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setDate(index, new java.sql.Date(((Date)value).getTime()), Calendar.getInstance());
    }
  }

  /**
   * @author Heiko Ahlig
   */
  public static class TMDate2Time extends TypeMapping
  {
    public TMDate2Time(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getTime(getField().getName(), Calendar.getInstance());
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setTime(index, new Time(((Date)value).getTime()), Calendar.getInstance());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMCharacter extends TypeMapping
  {
    public TMCharacter(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String str = resultSet.getString(getField().getName());
      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return str.charAt(0);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setString(index, ((Character)value).toString());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMByte extends TypeMapping
  {
    public TMByte(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getByte(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBytes extends TypeMapping
  {
    public TMBytes(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getBytes(getField().getName());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBoolean extends TypeMapping
  {
    public TMBoolean(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getBoolean(getField().getName());
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class TMBigInteger extends TypeMapping
  {
    public TMBigInteger(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String val = resultSet.getString(getField().getName());

      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return new BigInteger(val);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setString(index, ((BigInteger)value).toString());
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class TMBigDecimal extends TypeMapping
  {
    public TMBigDecimal(IMappingStrategy strategy, EStructuralFeature feature, DBType type)
    {
      super(strategy, feature, type);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String val = resultSet.getString(getField().getName());

      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return new BigDecimal(val);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setString(index, ((BigDecimal)value).toPlainString());
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class TMCustom extends TypeMapping
  {
    public TMCustom(IMappingStrategy mappingStrategy, EStructuralFeature feature, DBType type)
    {
      super(mappingStrategy, feature, type);
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String val = resultSet.getString(getField().getName());
      if (resultSet.wasNull())
      {
        return getFeature().isUnsettable() ? CDORevisionData.NIL : null;
      }

      return val;
    }

    @Override
    public void setDefaultValue(PreparedStatement stmt, int index) throws SQLException
    {
      setValue(stmt, index, getFeature().getDefaultValueLiteral());
    }
  }
}
