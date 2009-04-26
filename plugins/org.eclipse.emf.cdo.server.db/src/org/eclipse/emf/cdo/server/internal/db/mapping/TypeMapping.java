/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.MetaDataManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;

import org.eclipse.emf.ecore.EStructuralFeature;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This is a default implementation for the {@link ITypeMapping} interface which provides default behavor for all common
 * types.
 * 
 * @author Eike Stepper
 */
public abstract class TypeMapping implements ITypeMapping
{
  private IMappingStrategy mappingStrategy;

  private EStructuralFeature feature;

  private IDBField field;

  /**
   * Create a new type mapping
   * 
   * @param mappingStrategy
   *          the associated mapping strategy.
   * @param feature
   *          the feature to be mapped.
   */
  public TypeMapping(IMappingStrategy mappingStrategy, EStructuralFeature feature)
  {
    this.mappingStrategy = mappingStrategy;
    this.feature = feature;
  }

  public final void setValueFromRevision(PreparedStatement stmt, int index, InternalCDORevision revision)
      throws SQLException
  {
    setValue(stmt, index, getRevisionValue(revision));
  }

  public final void setValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    if (value == null)
    {
      stmt.setNull(index, getSqlType());
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

  public final IDBField getField()
  {
    return field;
  }

  public final void readValueToRevision(ResultSet resultSet, int index, InternalCDORevision revision)
      throws SQLException
  {
    Object value = readValue(resultSet, index);
    revision.setValue(getFeature(), value);
  }

  public final Object readValue(ResultSet resultSet, int index) throws SQLException
  {
    Object value = getResultSetValue(resultSet, index);
    if (resultSet.wasNull())
    {
      value = null;
    }
    return value;
  }

  public final EStructuralFeature getFeature()
  {
    return feature;
  }

  protected final Object getRevisionValue(InternalCDORevision revision)
  {
    return revision.getValue(getFeature());
  }

  protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
  {
    stmt.setObject(index, value, getSqlType());
  }

  /**
   * Returns the SQL type of this TypeMapping. The default implementation considers the type map hold by the meta-data
   * manager (@see {@link MetaDataManager#getDBType(org.eclipse.emf.ecore.EClassifier)} Subclasses may override.
   * 
   * @return The sql type of this TypeMapping.
   */
  protected int getSqlType()
  {
    return getDBType().getCode();
  }

  protected DBType getDBType()
  {
    return mappingStrategy.getStore().getMetaDataManager().getDBType(feature.getEType());
  }

  protected int getDBLength(DBType type)
  {
    // TODO: implement DBAdapter.getDBLength
    // mappingStrategy.getStore().getDBAdapter().getDBLength(type);
    return type == DBType.VARCHAR ? 32672 : IDBField.DEFAULT;
  }

  protected abstract Object getResultSetValue(ResultSet resultSet, int column) throws SQLException;

  /**
   * @author Eike Stepper
   */
  public static class TMEnum extends TypeMapping
  {
    public TMEnum(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      // see Bug 271941
      return resultSet.getInt(column);
      // EEnum type = (EEnum)getFeature().getEType();
      // int value = resultSet.getInt(column);
      // return type.getEEnumLiteral(value);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      super.doSetValue(stmt, index, value);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMString extends TypeMapping
  {
    public TMString(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getString(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMShort extends TypeMapping
  {
    public TMShort(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getShort(column);
    }
  }

  /**
   * @author Eike Stepper <br>
   *         TODO add mapping/unmapping calls for external references here
   */
  public static class TMObject extends TypeMapping
  {
    public TMObject(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      long id = resultSet.getLong(column);
      if (resultSet.wasNull())
      {
        return null;
      }

      return CDOIDUtil.createLong(id);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      super.doSetValue(stmt, index, CDODBUtil.getLong((CDOID)value));
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMLong extends TypeMapping
  {
    public TMLong(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getLong(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMInteger extends TypeMapping
  {
    public TMInteger(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getInt(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMFloat extends TypeMapping
  {
    public TMFloat(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getFloat(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMDouble extends TypeMapping
  {
    public TMDouble(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getDouble(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMDate extends TypeMapping
  {
    public TMDate(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getTimestamp(column);
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      stmt.setTimestamp(index, new Timestamp(((Date)value).getTime()));
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMCharacter extends TypeMapping
  {
    public TMCharacter(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      String str = resultSet.getString(column);
      if (resultSet.wasNull())
      {
        return null;
      }

      return str.charAt(0);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMByte extends TypeMapping
  {
    public TMByte(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getByte(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBytes extends TypeMapping
  {
    public TMBytes(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getBytes(column);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMBoolean extends TypeMapping
  {
    public TMBoolean(IMappingStrategy strategy, EStructuralFeature feature)
    {
      super(strategy, feature);
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet, int column) throws SQLException
    {
      return resultSet.getBoolean(column);
    }
  }
}
