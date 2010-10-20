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
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.lob.CDOBlob;
import org.eclipse.emf.cdo.common.model.lob.CDOClob;
import org.eclipse.emf.cdo.common.model.lob.CDOLobUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IExternalReferenceManager;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMappingFactory;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a default implementation for the {@link ITypeMapping} interface which provides default behavor for all common
 * types.
 * 
 * @author Eike Stepper
 */
public class CoreTypeMappings
{
  public static final String ID_PREFIX = "org.eclipse.emf.cdo.server.db.CoreTypeMappings";

  /**
   * @author Eike Stepper
   */
  public static class TMEnum extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Enum";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEEnum(), DBType.INTEGER));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMEnum();
      }
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
        return literal.getValue();
      }

      Enumerator enumerator = (Enumerator)eenum.getDefaultValue();
      return enumerator.getValue();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMString extends AbstractTypeMapping
  {
    public static final String ID_VARCHAR = ID_PREFIX + ".StringVarchar";

    public static final Factory FACTORY_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_VARCHAR,
        EcorePackage.eINSTANCE.getEString(), DBType.VARCHAR));

    public static final String ID_CLOB = ID_PREFIX + ".StringClob";

    public static final Factory FACTORY_CLOB = new Factory(TypeMappingUtil.createDescriptor(ID_CLOB,
        EcorePackage.eINSTANCE.getEString(), DBType.CLOB));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMString();
      }
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
  public static class TMBlob extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".BlobStream";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EtypesPackage.eINSTANCE.getBlob(), DBType.VARCHAR));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBlob();
      }
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      CDOBlob blob = (CDOBlob)value;
      stmt.setString(index, HexUtil.bytesToHex(blob.getID()) + "-" + blob.getSize());
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String str = resultSet.getString(getField().getName());
      int pos = str.indexOf('-');

      byte[] id = HexUtil.hexToBytes(str.substring(0, pos));
      long size = Long.parseLong(str.substring(pos + 1));
      return CDOLobUtil.createBlob(id, size);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMClob extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".ClobStream";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EtypesPackage.eINSTANCE.getClob(), DBType.VARCHAR));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMClob();
      }
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      CDOClob clob = (CDOClob)value;
      stmt.setString(index, HexUtil.bytesToHex(clob.getID()) + "-" + clob.getSize());
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String str = resultSet.getString(getField().getName());
      int pos = str.indexOf('-');

      byte[] id = HexUtil.hexToBytes(str.substring(0, pos));
      long size = Long.parseLong(str.substring(pos + 1));
      return CDOLobUtil.createClob(id, size);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMShort extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Short";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEShort(), DBType.SMALLINT));

    public static final String ID_OBJECT = ID_PREFIX + ".ShortObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getEShortObject(), DBType.SMALLINT));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMShort();
      }
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
  public static class TMObject extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Object";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEClass(), DBType.BIGINT));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMObject();
      }
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
      long commitTime = commitContext != null ? commitContext.getBranchPoint().getTimeStamp()
          : CDOBranchPoint.UNSPECIFIED_DATE; // Happens on rawStore for workspace checkouts
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
  public static class TMLong extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Long";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getELong(), DBType.BIGINT));

    public static final String ID_OBJECT = ID_PREFIX + ".LongObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getELongObject(), DBType.BIGINT));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMLong();
      }
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
  public static class TMInteger extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Integer";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEInt(), DBType.INTEGER));

    public static final String ID_OBJECT = ID_PREFIX + ".IntegerObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getEIntegerObject(), DBType.INTEGER));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMInteger();
      }
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
  public static class TMFloat extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Float";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEFloat(), DBType.FLOAT));

    public static final String ID_OBJECT = ID_PREFIX + ".FloatObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getEFloatObject(), DBType.FLOAT));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMFloat();
      }
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
  public static class TMDouble extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Double";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEDouble(), DBType.DOUBLE));

    public static final String ID_OBJECT = ID_PREFIX + ".DoubleObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getEDoubleObject(), DBType.DOUBLE));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDouble();
      }
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
  public static class TMDate2Timestamp extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Timestamp";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEDate(), DBType.TIMESTAMP));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDate2Timestamp();
      }
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
  public static class TMDate2Date extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Date";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEDate(), DBType.DATE));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDate2Date();
      }
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getDate(getField().getName(), Calendar.getInstance());
    }
  }

  /**
   * @author Heiko Ahlig
   */
  public static class TMDate2Time extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Time";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEDate(), DBType.TIME));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMDate2Time();
      }
    }

    @Override
    public Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      return resultSet.getTime(getField().getName(), Calendar.getInstance());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TMCharacter extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Character";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEChar(), DBType.CHAR));

    public static final String ID_OBJECT = ID_PREFIX + ".CharacterObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getECharacterObject(), DBType.CHAR));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMCharacter();
      }
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
  public static class TMByte extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Byte";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEByte(), DBType.SMALLINT));

    public static final String ID_OBJECT = ID_PREFIX + ".ByteObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getEByteObject(), DBType.SMALLINT));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMByte();
      }
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
  public static class TMBytes extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".ByteArray";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEByteArray(), DBType.BLOB));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBytes();
      }
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
  public static class TMBoolean extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".Boolean";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEBoolean(), DBType.BOOLEAN));

    public static final String ID_OBJECT = ID_PREFIX + ".BooleanObject";

    public static final Factory FACTORY_OBJECT = new Factory(TypeMappingUtil.createDescriptor(ID_OBJECT,
        EcorePackage.eINSTANCE.getEBooleanObject(), DBType.BOOLEAN));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBoolean();
      }
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
  public static class TMBigInteger extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".BigInteger";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEBigInteger(), DBType.VARCHAR));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBigInteger();
      }
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
  public static class TMBigDecimal extends AbstractTypeMapping
  {
    public static final String ID = ID_PREFIX + ".BigDecimal";

    public static final Factory FACTORY = new Factory(TypeMappingUtil.createDescriptor(ID,
        EcorePackage.eINSTANCE.getEBigDecimal(), DBType.VARCHAR));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMBigDecimal();
      }
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
  public static class TMCustom extends AbstractTypeMapping
  {
    public static final String ID_VARCHAR = ID_PREFIX + ".CustomVarchar";

    public static final Factory FACTORY_VARCHAR = new Factory(TypeMappingUtil.createDescriptor(ID_VARCHAR,
        EcorePackage.eINSTANCE.getEDataType(), DBType.VARCHAR));

    public static final String ID_CLOB = ID_PREFIX + ".CustomClob";

    public static final Factory FACTORY_CLOB = new Factory(TypeMappingUtil.createDescriptor(ID_CLOB,
        EcorePackage.eINSTANCE.getEDataType(), DBType.CLOB));

    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory(Descriptor descriptor)
      {
        super(descriptor);
      }

      @Override
      public ITypeMapping create(String description) throws ProductCreationException
      {
        return new TMCustom();
      }
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
    protected Object getDefaultValue()
    {
      Object defaultValue = getFeature().getDefaultValue();
      EFactory factory = getFeature().getEType().getEPackage().getEFactoryInstance();
      return factory.convertToString((EDataType)getFeature().getEType(), defaultValue);
    }
  }
}
