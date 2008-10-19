/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - http://bugs.eclipse.org/208689    
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
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
import org.eclipse.emf.cdo.server.IPackageManager;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreReader;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOClass;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class DBStoreReader extends DBStoreAccessor implements IDBStoreReader
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DBStoreReader.class);

  public DBStoreReader(DBStore store, ISession session) throws DBException
  {
    super(store, session);
  }

  protected DBStoreReader(DBStore store, IView view) throws DBException
  {
    super(store, view);
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
    Object[] values = DBUtil.select(getConnection(), where, CDODBSchema.PACKAGES_ID, CDODBSchema.PACKAGES_NAME,
        CDODBSchema.PACKAGES_ECORE);
    PackageServerInfo.setDBID(cdoPackage, (Integer)values[0]);
    ((InternalCDOPackage)cdoPackage).setName((String)values[1]);
    ((InternalCDOPackage)cdoPackage).setEcore((String)values[2]);
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

  public CDOID readResourceID(CDOID folderID, String name, long timeStamp)
  {
    IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
    return mappingStrategy.readResourceID(this, folderID, name, timeStamp);
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
    // TODO: implement DBStoreReader.executeQuery(info, context)
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
}
