/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.CDOIDRangeImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassProxy;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.model.CDOTypeImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IStoreWriter;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IMapping;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor implements IStoreReader, IStoreWriter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreAccessor.class);

  protected DBStore store;

  protected Object context;

  protected boolean reader;

  protected Connection connection;

  private DBStoreAccessor(DBStore store, Object context, boolean reader) throws DBException
  {
    this.store = store;
    this.context = context;
    this.reader = reader;

    try
    {
      connection = store.getConnectionProvider().getConnection();
      connection.setAutoCommit(reader);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    this(store, session, true);
  }

  public DBStoreAccessor(DBStore store, IView view) throws DBException
  {
    this(store, view, false);
  }

  public void release() throws DBException
  {
    try
    {
      if (!reader)
      {
        connection.commit();
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public DBStore getStore()
  {
    return store;
  }

  public boolean isReader()
  {
    return reader;
  }

  public ISession getSession()
  {
    if (context instanceof IView)
    {
      return ((IView)context).getSession();
    }

    return (ISession)context;
  }

  public IView getView()
  {
    if (context instanceof IView)
    {
      return (IView)context;
    }

    return null;
  }

  public Connection getConnection()
  {
    return connection;
  }

  public void writePackages(CDOPackageImpl... cdoPackages)
  {
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      if (!cdoPackage.isSystem())
      {
        int id = store.getNextPackageID();
        cdoPackage.setServerInfo(new PackageServerInfo(id));
        if (TRACER.isEnabled())
        {
          TRACER.format("Inserting package: {0} --> {1}", cdoPackage, id);
        }

        String packageURI = cdoPackage.getPackageURI();
        String name = cdoPackage.getName();
        String ecore = cdoPackage.getEcore();
        boolean dynamic = cdoPackage.isDynamic();
        CDOIDRange metaIDRange = cdoPackage.getMetaIDRange();
        long lb = metaIDRange == null ? 0L : metaIDRange.getLowerBound().getValue();
        long ub = metaIDRange == null ? 0L : metaIDRange.getUpperBound().getValue();
        DBUtil.insertRow(connection, CDODBSchema.PACKAGES, id, packageURI, name, ecore, dynamic, lb, ub);

        for (CDOClassImpl cdoClass : cdoPackage.getClasses())
        {
          writeClass(cdoClass);
        }
      }
    }

    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    Collection<IDBTable> affectedTables = mappingStrategy.map(cdoPackages);
    store.getDBAdapter().createTables(affectedTables, connection);
  }

  protected void writeClass(CDOClassImpl cdoClass)
  {
    int id = store.getNextClassID();
    cdoClass.setServerInfo(new ClassServerInfo(id));

    CDOPackageImpl cdoPackage = cdoClass.getContainingPackage();
    int packageID = ServerInfo.getDBID(cdoPackage);
    int classifierID = cdoClass.getClassifierID();
    String name = cdoClass.getName();
    boolean isAbstract = cdoClass.isAbstract();
    DBUtil.insertRow(connection, CDODBSchema.CLASSES, id, packageID, classifierID, name, isAbstract);

    for (CDOClassProxy superType : cdoClass.getSuperTypeProxies())
    {
      writeSuperType(id, superType);
    }

    for (CDOFeatureImpl feature : cdoClass.getFeatures())
    {
      writeFeature(feature);
    }
  }

  protected void writeSuperType(int type, CDOClassProxy superType)
  {
    String packageURI = superType.getPackageURI();
    int classifierID = superType.getClassifierID();
    DBUtil.insertRow(connection, CDODBSchema.SUPERTYPES, type, packageURI, classifierID);
  }

  protected void writeFeature(CDOFeatureImpl feature)
  {
    int id = store.getNextFeatureID();
    feature.setServerInfo(new FeatureServerInfo(id));

    int classID = ServerInfo.getDBID(feature.getContainingClass());
    String name = feature.getName();
    int featureID = feature.getFeatureID();
    int type = feature.getType().getTypeID();
    CDOClassProxy reference = feature.getReferenceTypeProxy();
    String packageURI = reference == null ? null : reference.getPackageURI();
    int classifierID = reference == null ? 0 : reference.getClassifierID();
    boolean many = feature.isMany();
    boolean containment = feature.isContainment();
    int idx = feature.getFeatureIndex();
    DBUtil.insertRow(connection, CDODBSchema.FEATURES, id, classID, featureID, name, type, packageURI, classifierID,
        many, containment, idx);
  }

  public Collection<CDOPackageInfo> readPackageInfos()
  {
    final Collection<CDOPackageInfo> result = new ArrayList(0);
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, final Object... values)
      {
        String packageURI = (String)values[0];
        boolean dynamic = getBoolean(values[1]);
        long rangeLB = (Long)values[2];
        long rangeUB = (Long)values[3];
        CDOIDRange metaIDRange = rangeLB == 0L && rangeUB == 0L ? null : CDOIDRangeImpl.create(rangeLB, rangeUB);
        result.add(new CDOPackageInfo(packageURI, dynamic, metaIDRange));
        return true;
      }
    };

    DBUtil.select(connection, rowHandler, CDODBSchema.PACKAGES_URI, CDODBSchema.PACKAGES_DYNAMIC,
        CDODBSchema.PACKAGES_RANGE_LB, CDODBSchema.PACKAGES_RANGE_UB);
    return result;
  }

  public void readPackage(final CDOPackageImpl cdoPackage)
  {
    String where = CDODBSchema.PACKAGES_URI.getName() + " = '" + cdoPackage.getPackageURI() + "'";
    Object[] values = DBUtil.select(connection, where, CDODBSchema.PACKAGES_ID, CDODBSchema.PACKAGES_NAME,
        CDODBSchema.PACKAGES_ECORE);
    cdoPackage.setServerInfo(new PackageServerInfo((Integer)values[0]));
    cdoPackage.setName((String)values[1]);
    cdoPackage.setEcore((String)values[2]);
    readClasses(cdoPackage);

    CDOPackageImpl[] cdoPackages = { cdoPackage };
    store.getMappingStrategy().map(cdoPackages);
  }

  protected void readClasses(final CDOPackageImpl cdoPackage)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int classID = (Integer)values[0];
        int classifierID = (Integer)values[1];
        String name = (String)values[2];
        boolean isAbstract = getBoolean(values[3]);
        CDOClassImpl cdoClass = new CDOClassImpl(cdoPackage, classifierID, name, isAbstract);
        cdoClass.setServerInfo(new ClassServerInfo(classID));
        cdoPackage.addClass(cdoClass);
        readSuperTypes(cdoClass, classID);
        readFeatures(cdoClass, classID);
        return true;
      }
    };

    String where = CDODBSchema.CLASSES_PACKAGE.getName() + " = " + ServerInfo.getDBID(cdoPackage);
    DBUtil.select(connection, rowHandler, where, CDODBSchema.CLASSES_ID, CDODBSchema.CLASSES_CLASSIFIER,
        CDODBSchema.CLASSES_NAME, CDODBSchema.CLASSES_ABSTRACT);
  }

  protected void readSuperTypes(final CDOClassImpl cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        String packageURI = (String)values[0];
        int classifierID = (Integer)values[1];
        cdoClass.addSuperType(new CDOClassRefImpl(packageURI, classifierID));
        return true;
      }
    };

    String where = CDODBSchema.SUPERTYPES_TYPE.getName() + " = " + classID;
    DBUtil.select(connection, rowHandler, where, CDODBSchema.SUPERTYPES_SUPERTYPE_PACKAGE,
        CDODBSchema.SUPERTYPES_SUPERTYPE_CLASSIFIER);
  }

  protected void readFeatures(final CDOClassImpl cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int featureID = (Integer)values[1];
        String name = (String)values[2];
        CDOTypeImpl type = CDOTypeImpl.getType((Integer)values[3]);
        boolean many = getBoolean(values[6]);

        CDOFeatureImpl feature;
        if (type == CDOType.OBJECT)
        {
          String packageURI = (String)values[4];
          int classifierID = (Integer)values[5];
          boolean containment = getBoolean(values[7]);
          CDOClassRefImpl classRef = new CDOClassRefImpl(packageURI, classifierID);
          CDOClassProxy referenceType = new CDOClassProxy(classRef, cdoClass.getPackageManager());
          feature = new CDOFeatureImpl(cdoClass, featureID, name, referenceType, many, containment);
        }
        else
        {
          feature = new CDOFeatureImpl(cdoClass, featureID, name, type, many);
        }

        feature.setServerInfo(new FeatureServerInfo((Integer)values[0]));
        cdoClass.addFeature(feature);
        return true;
      }
    };

    String where = CDODBSchema.FEATURES_CLASS.getName() + " = " + classID;
    DBUtil.select(connection, rowHandler, where, CDODBSchema.FEATURES_ID, CDODBSchema.FEATURES_FEATURE,
        CDODBSchema.FEATURES_NAME, CDODBSchema.FEATURES_TYPE, CDODBSchema.FEATURES_REFERENCE_PACKAGE,
        CDODBSchema.FEATURES_REFERENCE_CLASSIFIER, CDODBSchema.FEATURES_MANY, CDODBSchema.FEATURES_CONTAINMENT);
  }

  public void writeRevision(CDORevisionImpl revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Inserting revision: {0}", revision);
    }

    CDOClassImpl cdoClass = revision.getCDOClass();
    IMapping mapping = ClassServerInfo.getMapping(cdoClass);

    mapping.writeRevision(this, revision);
  }

  public CDORevision readRevision(CDOID id)
  {
    // TODO Implement method DBStoreAccessor.readRevision()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDORevision readRevision(CDOID id, long timeStamp)
  {
    // TODO Implement method DBStoreAccessor.enclosing_method()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDOID readResourceID(String path)
  {
    // TODO Implement method DBStoreAccessor.readResourceID()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public String readResourcePath(CDOID id)
  {
    // TODO Implement method DBStoreAccessor.readResourcePath()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    // TODO Implement method DBStoreAccessor.readObjectType()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * TODO Move this somehow to DBAdapter
   */
  protected Boolean getBoolean(Object value)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof Boolean)
    {
      return (Boolean)value;
    }

    if (value instanceof Number)
    {
      return ((Number)value).intValue() != 0;
    }

    throw new IllegalArgumentException("Not a boolean value: " + value);
  }
}
