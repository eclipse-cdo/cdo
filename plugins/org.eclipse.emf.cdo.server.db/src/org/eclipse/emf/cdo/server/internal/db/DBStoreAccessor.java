/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMeta;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.StoreAccessor;
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOFeature;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

  public DBStoreAccessor(DBStore store, ITransaction transaction) throws DBException
  {
    super(store, transaction);
    initConnection();
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

  public PreparedStatement prepareStatement(String sql)
  {
    try
    {
      return getConnection().prepareStatement(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public DBStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature)
  {
    return new DBStoreChunkReader(this, revision, feature);
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
        long lowerBound = (Long)values[2];
        long upperBound = (Long)values[3];
        CDOIDMetaRange metaIDRange = lowerBound == 0 ? null : CDOIDUtil.createMetaRange(CDOIDUtil
            .createMeta(lowerBound), (int)(upperBound - lowerBound) + 1);
        String parentURI = (String)values[4];

        result.add(new CDOPackageInfo(packageURI, dynamic, metaIDRange, parentURI));
        return true;
      }
    };

    DBUtil.select(getConnection(), rowHandler, CDODBSchema.PACKAGES_URI, CDODBSchema.PACKAGES_DYNAMIC,
        CDODBSchema.PACKAGES_RANGE_LB, CDODBSchema.PACKAGES_RANGE_UB, CDODBSchema.PACKAGES_PARENT);
    return result;
  }

  public void readPackage(CDOPackage cdoPackage)
  {
    String where = CDODBSchema.PACKAGES_URI.getName() + " = '" + cdoPackage.getPackageURI() + "'";
    Object[] values = DBUtil.select(getConnection(), where, CDODBSchema.PACKAGES_ID, CDODBSchema.PACKAGES_NAME);
    PackageServerInfo.setDBID(cdoPackage, (Integer)values[0]);
    ((InternalCDOPackage)cdoPackage).setName((String)values[1]);
    readClasses(cdoPackage);
    mapPackages(cdoPackage);
  }

  protected void readClasses(final CDOPackage cdoPackage)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int classID = (Integer)values[0];
        int classifierID = (Integer)values[1];
        String name = (String)values[2];
        boolean isAbstract = getBoolean(values[3]);
        CDOClass cdoClass = CDOModelUtil.createClass(cdoPackage, classifierID, name, isAbstract);
        ClassServerInfo.setDBID(cdoClass, classID);
        ((InternalCDOPackage)cdoPackage).addClass(cdoClass);
        readSuperTypes(cdoClass, classID);
        readFeatures(cdoClass, classID);
        return true;
      }
    };

    String where = CDODBSchema.CLASSES_PACKAGE.getName() + "=" + ServerInfo.getDBID(cdoPackage);
    DBUtil.select(getConnection(), rowHandler, where, CDODBSchema.CLASSES_ID, CDODBSchema.CLASSES_CLASSIFIER,
        CDODBSchema.CLASSES_NAME, CDODBSchema.CLASSES_ABSTRACT);
  }

  protected void readSuperTypes(final CDOClass cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        String packageURI = (String)values[0];
        int classifierID = (Integer)values[1];
        ((InternalCDOClass)cdoClass).addSuperType(CDOModelUtil.createClassRef(packageURI, classifierID));
        return true;
      }
    };

    String where = CDODBSchema.SUPERTYPES_TYPE.getName() + "=" + classID;
    DBUtil.select(getConnection(), rowHandler, where, CDODBSchema.SUPERTYPES_SUPERTYPE_PACKAGE,
        CDODBSchema.SUPERTYPES_SUPERTYPE_CLASSIFIER);
  }

  protected void readFeatures(final CDOClass cdoClass, int classID)
  {
    IDBRowHandler rowHandler = new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        int featureID = (Integer)values[1];
        String name = (String)values[2];
        CDOType type = CDOModelUtil.getType((Integer)values[3]);
        boolean many = getBoolean(values[6]);

        CDOFeature feature;
        if (type == CDOType.OBJECT)
        {
          String packageURI = (String)values[4];
          int classifierID = (Integer)values[5];
          boolean containment = getBoolean(values[7]);
          CDOClassRef classRef = CDOModelUtil.createClassRef(packageURI, classifierID);
          CDOClassProxy referenceType = new CDOClassProxy(classRef, cdoClass.getPackageManager());
          feature = CDOModelUtil.createReference(cdoClass, featureID, name, referenceType, many, containment);
        }
        else
        {
          feature = CDOModelUtil.createAttribute(cdoClass, featureID, name, type, many);
        }

        FeatureServerInfo.setDBID(feature, (Integer)values[0]);
        ((InternalCDOClass)cdoClass).addFeature(feature);
        return true;
      }
    };

    String where = CDODBSchema.FEATURES_CLASS.getName() + "=" + classID;
    DBUtil.select(getConnection(), rowHandler, where, CDODBSchema.FEATURES_ID, CDODBSchema.FEATURES_FEATURE,
        CDODBSchema.FEATURES_NAME, CDODBSchema.FEATURES_TYPE, CDODBSchema.FEATURES_REFERENCE_PACKAGE,
        CDODBSchema.FEATURES_REFERENCE_CLASSIFIER, CDODBSchema.FEATURES_MANY, CDODBSchema.FEATURES_CONTAINMENT);
  }

  public void readPackageEcore(CDOPackage cdoPackage)
  {
    String where = CDODBSchema.PACKAGES_URI.getName() + " = '" + cdoPackage.getPackageURI() + "'";
    Object[] values = DBUtil.select(getConnection(), where, CDODBSchema.PACKAGES_ECORE);
    ((InternalCDOPackage)cdoPackage).setEcore((String)values[0]);
  }

  public String readPackageURI(int packageID)
  {
    String where = CDODBSchema.PACKAGES_ID.getName() + "=" + packageID;
    Object[] uri = DBUtil.select(getConnection(), where, CDODBSchema.PACKAGES_URI);
    return (String)uri[0];
  }

  public CloseableIterator<CDOID> readObjectIDs()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Selecting object IDs");
    }

    return getStore().getMappingStrategy().readObjectIDs(this);
  }

  public CDOClassRef readObjectType(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting object type: {0}", id);
    }

    return getStore().getMappingStrategy().readObjectType(this, id);
  }

  public CDOClassRef readClassRef(int classID)
  {
    String where = CDODBSchema.CLASSES_ID.getName() + "=" + classID;
    Object[] res = DBUtil.select(getConnection(), where, CDODBSchema.CLASSES_CLASSIFIER, CDODBSchema.CLASSES_PACKAGE);
    int classifierID = (Integer)res[0];
    String packageURI = readPackageURI((Integer)res[1]);
    return CDOModelUtil.createClassRef(packageURI, classifierID);
  }

  public CDORevision readRevision(CDOID id, int referenceChunk)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Selecting revision: {0}", id);
    }

    CDOClass cdoClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(cdoClass, id);

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

    CDOClass cdoClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(cdoClass, id);

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

    CDOClass cdoClass = getObjectType(id);
    InternalCDORevision revision = (InternalCDORevision)CDORevisionUtil.create(cdoClass, id);

    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
    mapping.readRevisionByVersion(this, revision, version, referenceChunk);
    return revision;
  }

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    mappingStrategy.queryResources(this, context);
  }

  /**
   * @since 2.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    // TODO: implement DBStoreAccessor.executeQuery(info, context)
    throw new UnsupportedOperationException();
  }

  protected CDOClass getObjectType(CDOID id)
  {
    // TODO Replace calls to getObjectType by optimized calls to RevisionManager.getObjectType (cache!)
    IRepository repository = getStore().getRepository();
    IPackageManager packageManager = repository.getPackageManager();
    CDOClassRef type = readObjectType(id);
    return type.resolve(packageManager);
  }

  public CloseableIterator<Object> createQueryIterator(CDOQueryInfo queryInfo)
  {
    throw new UnsupportedOperationException();
  }

  public void refreshRevisions()
  {
  }

  public void commit()
  {
    try
    {
      getConnection().commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void rollback(IStoreAccessor.CommitContext context)
  {
    try
    {
      getConnection().rollback();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void writePackages(CDOPackage[] cdoPackages)
  {
    new PackageWriter(cdoPackages)
    {
      @Override
      protected void writePackage(InternalCDOPackage cdoPackage)
      {
        int id = getStore().getNextPackageID();
        PackageServerInfo.setDBID(cdoPackage, id);
        if (TRACER.isEnabled())
        {
          TRACER.format("Writing package: {0} --> {1}", cdoPackage, id);
        }

        String packageURI = cdoPackage.getPackageURI();
        String name = cdoPackage.getName();
        String ecore = cdoPackage.getEcore();
        boolean dynamic = cdoPackage.isDynamic();
        CDOIDMetaRange metaIDRange = cdoPackage.getMetaIDRange();
        long lowerBound = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getLowerBound()).getLongValue();
        long upperBound = metaIDRange == null ? 0L : ((CDOIDMeta)metaIDRange.getUpperBound()).getLongValue();
        String parentURI = cdoPackage.getParentURI();

        String sql = "INSERT INTO " + CDODBSchema.PACKAGES + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        DBUtil.trace(sql);
        PreparedStatement pstmt = null;

        try
        {
          pstmt = getConnection().prepareStatement(sql);
          pstmt.setInt(1, id);
          pstmt.setString(2, packageURI);
          pstmt.setString(3, name);
          pstmt.setString(4, ecore);
          pstmt.setBoolean(5, dynamic);
          pstmt.setLong(6, lowerBound);
          pstmt.setLong(7, upperBound);
          pstmt.setString(8, parentURI);

          if (pstmt.execute())
          {
            throw new DBException("No result set expected");
          }

          if (pstmt.getUpdateCount() == 0)
          {
            throw new DBException("No row inserted into table " + CDODBSchema.PACKAGES);
          }
        }
        catch (SQLException ex)
        {
          throw new DBException(ex);
        }
        finally
        {
          DBUtil.close(pstmt);
        }
      }

      @Override
      protected void writeClass(InternalCDOClass cdoClass)
      {
        int id = getStore().getNextClassID();
        ClassServerInfo.setDBID(cdoClass, id);

        CDOPackage cdoPackage = cdoClass.getContainingPackage();
        int packageID = ServerInfo.getDBID(cdoPackage);
        int classifierID = cdoClass.getClassifierID();
        String name = cdoClass.getName();
        boolean isAbstract = cdoClass.isAbstract();
        DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.CLASSES, id, packageID, classifierID,
            name, isAbstract);
      }

      @Override
      protected void writeSuperType(InternalCDOClass type, CDOClassProxy superType)
      {
        int id = ClassServerInfo.getDBID(type);
        String packageURI = superType.getPackageURI();
        int classifierID = superType.getClassifierID();
        DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.SUPERTYPES, id, packageURI,
            classifierID);
      }

      @Override
      protected void writeFeature(InternalCDOFeature feature)
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
        DBUtil.insertRow(getConnection(), getStore().getDBAdapter(), CDODBSchema.FEATURES, id, classID, featureID,
            name, type, packageURI, classifierID, many, containment, idx);
      }
    }.run();

    Set<IDBTable> affectedTables = mapPackages(cdoPackages);
    getStore().getDBAdapter().createTables(affectedTables, getConnection());
  }

  @Override
  protected void writeRevisionDeltas(CDORevisionDelta[] revisionDeltas, long created)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeRevisions(CDORevision[] revisions)
  {
    for (CDORevision revision : revisions)
    {
      writeRevision(revision);
    }
  }

  protected void writeRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing revision: {0}", revision);
    }

    CDOClass cdoClass = revision.getCDOClass();
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(cdoClass);
    mapping.writeRevision(this, revision);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, long revised)
  {
    for (CDOID id : detachedObjects)
    {
      detachObject(id, revised);
    }
  }

  /**
   * @param revised
   * @since 2.0
   */
  protected void detachObject(CDOID id, long revised)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Detaching object: {0}", id);
    }

    CDOClass cdoClass = getObjectType(id);
    IClassMapping mapping = getStore().getMappingStrategy().getClassMapping(cdoClass);
    mapping.detachObject(this, id, revised);
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

  protected Set<IDBTable> mapPackages(CDOPackage... cdoPackages)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoPackages != null && cdoPackages.length != 0)
    {
      for (CDOPackage cdoPackage : cdoPackages)
      {
        Set<IDBTable> tables = mapClasses(cdoPackage.getClasses());
        affectedTables.addAll(tables);
      }
    }

    return affectedTables;
  }

  protected Set<IDBTable> mapClasses(CDOClass... cdoClasses)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoClasses != null && cdoClasses.length != 0)
    {
      IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
      for (CDOClass cdoClass : cdoClasses)
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

  protected void initConnection()
  {
    try
    {
      connection = getStore().getDBConnectionProvider().getConnection();
      connection.setAutoCommit(isReader());
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    // Do nothing
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    DBUtil.close(statement);
    DBUtil.close(connection);
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // Do nothing
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // Do nothing
  }
}
