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
import org.eclipse.emf.cdo.internal.server.StoreAccessor;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDRange;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.model.CDOPackageInfo;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.CloseableIterator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor extends StoreAccessor implements IDBStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreAccessor.class);

  private Connection connection;

  private Statement statement;

  public DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    super(store, session);
    initConnection();
  }

  public DBStoreAccessor(DBStore store, IView view) throws DBException
  {
    super(store, view);
    initConnection();
  }

  protected void initConnection()
  {
    try
    {
      connection = getStore().getConnectionProvider().getConnection();
      connection.setAutoCommit(isReader());
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public void release() throws DBException
  {
    try
    {
      if (!isReader())
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
      DBUtil.close(statement);
      DBUtil.close(connection);
    }
  }

  @Override
  public DBStore getStore()
  {
    return (DBStore)super.getStore();
  }

  public Connection getConnection()
  {
    return connection;
  }

  public Statement getStatement()
  {
    if (statement == null)
    {
      try
      {
        statement = getConnection().createStatement();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    return statement;
  }

  public IStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new DBStoreChunkReader(this, revision, feature);
  }

  public void writePackages(CDOPackageImpl... cdoPackages)
  {
    for (CDOPackageImpl cdoPackage : cdoPackages)
    {
      int id = getStore().getNextPackageID();
      PackageServerInfo.setDBID(cdoPackage, id);
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
      DBUtil.insertRow(connection, getStore().getDBAdapter(), CDODBSchema.PACKAGES, id, packageURI, name, ecore,
          dynamic, lb, ub);

      for (CDOClassImpl cdoClass : cdoPackage.getClasses())
      {
        writeClass(cdoClass);
      }
    }

    Set<IDBTable> affectedTables = mapPackages(cdoPackages);
    getStore().getDBAdapter().createTables(affectedTables, connection);
  }

  protected void writeClass(CDOClassImpl cdoClass)
  {
    int id = getStore().getNextClassID();
    ClassServerInfo.setDBID(cdoClass, id);

    CDOPackageImpl cdoPackage = cdoClass.getContainingPackage();
    int packageID = ServerInfo.getDBID(cdoPackage);
    int classifierID = cdoClass.getClassifierID();
    String name = cdoClass.getName();
    boolean isAbstract = cdoClass.isAbstract();
    DBUtil.insertRow(connection, getStore().getDBAdapter(), CDODBSchema.CLASSES, id, packageID, classifierID, name,
        isAbstract);

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
    DBUtil.insertRow(connection, getStore().getDBAdapter(), CDODBSchema.SUPERTYPES, type, packageURI, classifierID);
  }

  protected void writeFeature(CDOFeatureImpl feature)
  {
    int id = getStore().getNextFeatureID();
    FeatureServerInfo.setDBID(feature, id);

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
    DBUtil.insertRow(connection, getStore().getDBAdapter(), CDODBSchema.FEATURES, id, classID, featureID, name, type,
        packageURI, classifierID, many, containment, idx);
  }

  public Collection<CDOPackageInfo> readPackageInfos()
  {
    final Collection<CDOPackageInfo> result = new ArrayList<CDOPackageInfo>(0);
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
    PackageServerInfo.setDBID(cdoPackage, (Integer)values[0]);
    cdoPackage.setName((String)values[1]);
    cdoPackage.setEcore((String)values[2]);
    readClasses(cdoPackage);

    mapPackages(cdoPackage);
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
        ClassServerInfo.setDBID(cdoClass, classID);
        cdoPackage.addClass(cdoClass);
        readSuperTypes(cdoClass, classID);
        readFeatures(cdoClass, classID);
        return true;
      }
    };

    String where = CDODBSchema.CLASSES_PACKAGE.getName() + "=" + ServerInfo.getDBID(cdoPackage);
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

    String where = CDODBSchema.SUPERTYPES_TYPE.getName() + "=" + classID;
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

        FeatureServerInfo.setDBID(feature, (Integer)values[0]);
        cdoClass.addFeature(feature);
        return true;
      }
    };

    String where = CDODBSchema.FEATURES_CLASS.getName() + "=" + classID;
    DBUtil.select(connection, rowHandler, where, CDODBSchema.FEATURES_ID, CDODBSchema.FEATURES_FEATURE,
        CDODBSchema.FEATURES_NAME, CDODBSchema.FEATURES_TYPE, CDODBSchema.FEATURES_REFERENCE_PACKAGE,
        CDODBSchema.FEATURES_REFERENCE_CLASSIFIER, CDODBSchema.FEATURES_MANY, CDODBSchema.FEATURES_CONTAINMENT);
  }

  public String readPackageURI(int packageID)
  {
    String where = CDODBSchema.PACKAGES_ID.getName() + "=" + packageID;
    Object[] uri = DBUtil.select(connection, where, CDODBSchema.PACKAGES_URI);
    return (String)uri[0];
  }

  public CDOClassRef readClassRef(int classID)
  {
    String where = CDODBSchema.CLASSES_ID.getName() + "=" + classID;
    Object[] ids = DBUtil.select(connection, where, CDODBSchema.CLASSES_CLASSIFIER, CDODBSchema.CLASSES_PACKAGE);
    String packageURI = readPackageURI((Integer)ids[1]);
    return new CDOClassRefImpl(packageURI, (Integer)ids[0]);
  }

  public void writeRevision(CDORevisionImpl revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Inserting revision: {0}", revision);
    }

    CDOClassImpl cdoClass = revision.getCDOClass();

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    mapping.writeRevision(this, revision);
  }

  public CloseableIterator<CDOID> readObjectIDs(boolean withTypes)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Selecting object IDs");
    }

    return getStore().getMappingStrategy().readObjectIDs(this, withTypes);
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting object type: {0}", id);
    }

    return getStore().getMappingStrategy().readObjectType(this, id);
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}", id);
    }

    IRevisionManager revisionManager = getStore().getRepository().getRevisionManager();
    CDOClassImpl cdoClass = getObjectType(id);
    CDORevisionImpl revision = new CDORevisionImpl(revisionManager, cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    mapping.readRevision(this, revision, referenceChunk);
    return revision;
  }

  public CDORevision readRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}, timestamp={1,date} {1,time}", id, timeStamp);
    }

    IRevisionManager revisionManager = getStore().getRepository().getRevisionManager();
    CDOClassImpl cdoClass = getObjectType(id);
    CDORevisionImpl revision = new CDORevisionImpl(revisionManager, cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    mapping.readRevisionByTime(this, revision, timeStamp, referenceChunk);
    return revision;
  }

  public CDORevision readRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}, version={1}", id, version);
    }

    IRevisionManager revisionManager = getStore().getRepository().getRevisionManager();
    CDOClassImpl cdoClass = getObjectType(id);
    CDORevisionImpl revision = new CDORevisionImpl(revisionManager, cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    mapping.readRevisionByVersion(this, revision, version, referenceChunk);
    return revision;
  }

  public CDOID readResourceID(String path)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    return mappingStrategy.readResourceID(this, path);
  }

  public String readResourcePath(CDOID id)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    return mappingStrategy.readResourcePath(this, id);
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

  protected CDOClassImpl getObjectType(CDOID id)
  {
    IRepository repository = getStore().getRepository();
    IPackageManager packageManager = repository.getPackageManager();
    CDOClassRef type = repository.getTypeManager().getObjectType(this, id);
    return (CDOClassImpl)type.resolve(packageManager);
  }

  protected Set<IDBTable> mapPackages(CDOPackageImpl... cdoPackages)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoPackages != null && cdoPackages.length != 0)
    {
      for (CDOPackageImpl cdoPackage : cdoPackages)
      {
        Set<IDBTable> tables = mapClasses(cdoPackage.getClasses());
        affectedTables.addAll(tables);
      }
    }

    return affectedTables;
  }

  protected Set<IDBTable> mapClasses(CDOClassImpl... cdoClasses)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoClasses != null && cdoClasses.length != 0)
    {
      IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
      for (CDOClassImpl cdoClass : cdoClasses)
      {
        IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
        if (mapping != null)
        {
          affectedTables.addAll(mapping.getAffectedTables());
        }
      }
    }

    return affectedTables;
  }
}
