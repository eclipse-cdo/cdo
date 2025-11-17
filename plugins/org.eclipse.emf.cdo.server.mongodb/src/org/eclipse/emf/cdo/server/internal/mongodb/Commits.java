/*
 * Copyright (c) 2011, 2012, 2016-2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.internal.mongodb.MongoDBStore.ValueHandler;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public class Commits extends Coll
{
  public static final String COMMITS = "commits";

  public static final String COMMITS_ID = "_id";

  public static final String COMMITS_PREVIOUS = "previous";

  public static final String COMMITS_BRANCH = "branch";

  public static final String COMMITS_USER = "user";

  public static final String COMMITS_COMMENT = "comment";

  public static final String UNITS = "units";

  public static final String UNITS_ID = "id";

  public static final String UNITS_TYPE = "type";

  public static final String UNITS_DATA = "data";

  public static final String PACKAGES = "packages";

  public static final String PACKAGES_URI = "uri";

  public static final String PACKAGES_PARENT = "parent";

  public static final String CLASSIFIER_PREFIX = "c";

  public static final String SET_SUFFIX = "___set";

  public static final String REVISIONS = "revisions";

  public static final String REVISIONS_ID = "cdo_id";

  public static final String REVISIONS_VERSION = "cdo_version";

  private static final String REVISIONS_REVISED = "cdo_revised";

  public static final String REVISIONS_CLASS = "cdo_class";

  public static final String REVISIONS_RESOURCE = "cdo_resource";

  public static final String REVISIONS_CONTAINER = "cdo_container";

  public static final String REVISIONS_FEATURE = "cdo_feature";

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private InternalCDOPackageRegistry packageRegistry;

  private IDHandler idHandler;

  private InternalCDOPackageUnit[] systemPackageUnits;

  private EStructuralFeature resourceNameFeature;

  public Commits(MongoDBStore store)
  {
    super(store, COMMITS);
    ensureIndex(UNITS, UNITS_ID);

    if (store.isBranching())
    {
      DBObject index = new BasicDBObject();
      index.put(REVISIONS + "." + REVISIONS_ID, 1);
      index.put(COMMITS_BRANCH, 1);
      index.put(REVISIONS + "." + REVISIONS_VERSION, 1);

      collection.createIndex(index);
    }
    else
    {
      ensureIndex(REVISIONS, REVISIONS_ID, REVISIONS_VERSION);
    }

    packageRegistry = store.getRepository().getPackageRegistry();
    idHandler = store.getIDHandler();
  }

  public void writePackageUnits(MongoDBStoreAccessor mongoDBStoreAccessor, InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // This must be the first commit into the repo.
    systemPackageUnits = packageUnits;

    for (int i = 0; i < packageUnits.length; i++)
    {
      InternalCDOPackageUnit packageUnit = packageUnits[i];
      InternalCDOPackageInfo[] packageInfos = packageUnit.getPackageInfos();
      for (int j = 0; j < packageInfos.length; j++)
      {
        InternalCDOPackageInfo packageInfo = packageInfos[j];
        for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
        {
          store.getClasses().mapNewClassifier(classifier);
        }
      }
    }
  }

  private DBObject[] marshallUnits(InternalCDOPackageUnit[] packageUnits)
  {
    DBObject[] result = new DBObject[packageUnits.length];
    InternalCDOPackageRegistry packageRegistry = store.getRepository().getPackageRegistry();

    for (int i = 0; i < packageUnits.length; i++)
    {
      InternalCDOPackageUnit packageUnit = packageUnits[i];
      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      byte[] bytes = EMFUtil.getEPackageBytes(ePackage, ZIP_PACKAGE_BYTES, packageRegistry);
      DBObject[] packages = marshallPackages(packageUnit.getPackageInfos());

      DBObject doc = new BasicDBObject();
      doc.put(UNITS_ID, packageUnit.getID());
      doc.put(UNITS_TYPE, packageUnit.getOriginalType().toString());
      doc.put(UNITS_DATA, bytes);
      doc.put(PACKAGES, packages);

      result[i] = doc;
    }

    return result;
  }

  private DBObject[] marshallPackages(InternalCDOPackageInfo[] packageInfos)
  {
    DBObject[] result = new DBObject[packageInfos.length];
    for (int i = 0; i < packageInfos.length; i++)
    {
      InternalCDOPackageInfo packageInfo = packageInfos[i];

      DBObject doc = new BasicDBObject();
      doc.put(PACKAGES_URI, packageInfo.getPackageURI());
      String parent = packageInfo.getParentURI();
      if (!StringUtil.isEmpty(parent))
      {
        doc.put(PACKAGES_PARENT, parent);
      }

      for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
      {
        int classifierID = store.getClasses().mapNewClassifier(classifier);
        doc.put(CLASSIFIER_PREFIX + classifierID, classifier.getName());
      }

      result[i] = doc;
    }

    return result;
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    final Collection<InternalCDOPackageUnit> packageUnits = new ArrayList<>();

    DBObject query = new BasicDBObject();
    query.put(UNITS, new BasicDBObject("$exists", true));

    new QueryEmbeddedUnits<Object>(query)
    {
      @Override
      protected Object handleEmbedded(DBObject doc, DBObject embedded)
      {
        long time = (Long)doc.get(COMMITS_ID);
        CDOPackageUnit.Type type = CDOPackageUnit.Type.valueOf((String)embedded.get(UNITS_TYPE));
        InternalCDOPackageInfo[] infos = readPackageInfos(embedded);

        InternalCDOPackageUnit packageUnit = createPackageUnit();
        packageUnit.setOriginalType(type);
        packageUnit.setTimeStamp(time);
        packageUnit.setPackageInfos(infos);

        packageUnits.add(packageUnit);
        return null;
      }

      private InternalCDOPackageInfo[] readPackageInfos(DBObject embedded)
      {
        BasicDBList infos = (BasicDBList)embedded.get(PACKAGES);
        InternalCDOPackageInfo[] result = new InternalCDOPackageInfo[infos.size()];
        int i = 0;

        for (Object info : infos)
        {
          DBObject infoObject = (DBObject)info;
          String uri = (String)infoObject.get(PACKAGES_URI);
          String parent = (String)infoObject.get(PACKAGES_PARENT);

          InternalCDOPackageInfo packageInfo = createPackageInfo();
          packageInfo.setPackageURI(uri);
          packageInfo.setParentURI(parent);

          result[i++] = packageInfo;
        }

        return result;
      }

      private InternalCDOPackageUnit createPackageUnit()
      {
        return (InternalCDOPackageUnit)CDOModelUtil.createPackageUnit();
      }

      private InternalCDOPackageInfo createPackageInfo()
      {
        return (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
      }
    }.execute();

    return packageUnits;
  }

  public EPackage[] loadPackageUnit(final InternalCDOPackageUnit packageUnit)
  {
    DBObject query = new BasicDBObject();
    query.put(UNITS + "." + UNITS_ID, packageUnit.getID());

    return new QueryEmbeddedUnits<EPackage[]>(query)
    {
      @Override
      protected EPackage[] handleEmbedded(DBObject doc, DBObject embedded)
      {
        byte[] data = (byte[])embedded.get(UNITS_DATA);
        EPackage ePackage = createEPackage(packageUnit, data);
        return EMFUtil.getAllPackages(ePackage);
      }

      private EPackage createEPackage(InternalCDOPackageUnit packageUnit, byte[] bytes)
      {
        ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(packageRegistry);
        return EMFUtil.createEPackage(packageUnit.getID(), bytes, ZIP_PACKAGE_BYTES, resourceSet, false);
      }
    }.execute();
  }

  public void initializeClassifiers()
  {
    final Classes classes = store.getClasses();

    DBObject query = new BasicDBObject();
    query.put(UNITS, new BasicDBObject("$exists", true));

    new QueryEmbeddedUnits<Object>(query)
    {
      @Override
      protected Object handleEmbedded(DBObject doc, DBObject embedded)
      {
        BasicDBList infos = (BasicDBList)embedded.get(PACKAGES);
        for (Object info : infos)
        {
          DBObject infoObject = (DBObject)info;
          String uri = (String)infoObject.get(PACKAGES_URI);
          handleClassifiers(infoObject, uri);
        }

        return null;
      }

      private void handleClassifiers(DBObject embedded, String packageURI)
      {
        Set<String> keys = embedded.keySet();
        for (String key : keys)
        {
          if (key.startsWith(CLASSIFIER_PREFIX))
          {
            int id = Integer.parseInt(key.substring(CLASSIFIER_PREFIX.length()));
            String classifierName = (String)embedded.get(key);

            CDOClassifierRef classifierRef = new CDOClassifierRef(packageURI, classifierName);
            EClassifier classifier = classifierRef.resolve(packageRegistry);

            classes.mapClassifier(classifier, id);
          }
        }
      }
    }.execute();
  }

  public void write(MongoDBStoreAccessor accessor, InternalCommitContext context, OMMonitor monitor)
  {
    try
    {
      monitor.begin(104);
      CDOBranchPoint branchPoint = context.getBranchPoint();

      DBObject doc = new BasicDBObject();
      doc.put(COMMITS_ID, branchPoint.getTimeStamp());

      long previous = context.getPreviousTimeStamp();
      boolean firstCommit = previous == CDOBranchPoint.UNSPECIFIED_DATE;
      if (!firstCommit)
      {
        doc.put(COMMITS_PREVIOUS, previous);
      }

      if (store.isBranching())
      {
        doc.put(COMMITS_BRANCH, branchPoint.getBranch().getID());
      }

      String user = context.getUserID();
      if (!StringUtil.isEmpty(user))
      {
        doc.put(COMMITS_USER, user);
      }

      String comment = context.getCommitComment();
      if (!StringUtil.isEmpty(comment))
      {
        doc.put(COMMITS_COMMENT, comment);
      }

      InternalCDOPackageUnit[] newPackageUnits = firstCommit ? systemPackageUnits : context.getNewPackageUnits();
      if (!ObjectUtil.isEmpty(newPackageUnits))
      {
        doc.put(UNITS, marshallUnits(newPackageUnits));
      }

      monitor.worked();
      accessor.addIDMappings(context, monitor.fork());
      context.applyIDMappings(monitor.fork());

      List<DBObject> docs = new ArrayList<>();
      marshalRevisions(docs, context, context.getNewObjects(), CDOChangeKind.NEW);
      marshalRevisions(docs, context, context.getDirtyObjects(), CDOChangeKind.CHANGED);
      marshalRevisions(docs, context, context.getDetachedRevisions(), CDOChangeKind.DETACHED);

      if (!docs.isEmpty())
      {
        doc.put(REVISIONS, docs);
      }

      monitor.worked();

      collection.insert(doc);
      monitor.worked(100);
    }
    finally
    {
      monitor.done();
    }
  }

  private void marshalRevisions(List<DBObject> docs, InternalCommitContext context, InternalCDORevision[] revisions, CDOChangeKind changeKind)
  {
    for (InternalCDORevision revision : revisions)
    {
      DBObject doc = marshallRevision(context, revision, changeKind);
      docs.add(doc);
    }
  }

  private DBObject marshallRevision(InternalCommitContext context, InternalCDORevision revision, CDOChangeKind changeKind)
  {
    boolean resource = !(revision instanceof SyntheticCDORevision) && revision.isResource();
    if (resource && resourceNameFeature == null)
    {
      resourceNameFeature = revision.getEClass().getEStructuralFeature(CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
    }

    DBObject doc = new BasicDBObject();
    idHandler.write(doc, REVISIONS_ID, revision.getID());

    EClass eClass = revision.getEClass();
    doc.put(REVISIONS_CLASS, store.getClasses().getClassifierID(eClass));

    if (changeKind == CDOChangeKind.DETACHED)
    {
      doc.put(REVISIONS_VERSION, -revision.getVersion() - 1);
      return doc;
    }

    doc.put(REVISIONS_VERSION, revision.getVersion());

    CDOID resourceID = revision.getResourceID();
    idHandler.write(doc, REVISIONS_RESOURCE, resourceID);

    CDOID containerID = (CDOID)revision.getContainerID();
    idHandler.write(doc, REVISIONS_CONTAINER, containerID);

    int featureID = revision.getContainerFeatureID();
    doc.put(REVISIONS_FEATURE, featureID);

    if (resource && changeKind != CDOChangeKind.DETACHED)
    {
      String name = (String)revision.data().get(resourceNameFeature, 0);
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();

      CDOID existingID = accessor.readResourceID(containerID, name, revision);
      if (existingID != null && !existingID.equals(revision.getID()) && !isBeingDetached(context, existingID))
      {
        throw new IllegalStateException("Duplicate resource: name=" + name + ", folderID=" + containerID); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }

    CDOClassInfo classInfo = revision.getClassInfo();
    for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
    {
      Object value = revision.getValue(feature);

      CDOType type = CDOModelUtil.getType(feature);
      ValueHandler valueHandler = store.getValueHandler(type);

      if (feature.isUnsettable())
      {
        if (value == null)
        {
          doc.put(feature.getName() + SET_SUFFIX, false);
          doc.put(feature.getName(), valueHandler.getMongoDefaultValue(feature));
          continue;
        }

        doc.put(feature.getName() + SET_SUFFIX, true);
      }

      if (value == CDORevisionData.NIL)
      {
        doc.put(feature.getName(), null);
      }
      else if (value == null)
      {
        if (feature.isMany() || feature.getDefaultValue() == null)
        {
          doc.put(feature.getName(), null);
        }
        else
        {
          doc.put(feature.getName(), valueHandler.getMongoDefaultValue(feature));
        }
      }
      else
      {
        if (feature.isMany())
        {
          List<?> cdoList = (List<?>)value;
          BasicDBList mongoList = new BasicDBList();
          for (Object element : cdoList)
          {
            element = valueHandler.toMongo(element);
            mongoList.add(element);
          }

          value = mongoList;
        }
        else
        {
          value = valueHandler.toMongo(value);
        }

        doc.put(feature.getName(), value);
      }
    }

    return doc;
  }

  private boolean isBeingDetached(InternalCommitContext context, CDOID id)
  {
    for (CDOID idBeingDetached : context.getDetachedObjects())
    {
      if (id.equals(idBeingDetached))
      {
        return true;
      }
    }

    return false;
  }

  public void queryResources(final QueryResourcesContext context)
  {
    Classes classes = getStore().getClasses();
    final int folderCID = classes.getResourceFolderClassID();
    final int resourceCID = classes.getResourceClassID();

    final CDOID folderID = context.getFolderID();
    final String name = context.getName();
    final boolean exactMatch = context.exactMatch();
    final long timeStamp = context.getTimeStamp();

    DBObject query = new BasicDBObject();

    query.put(REVISIONS + "." + REVISIONS_CLASS, new BasicDBObject("$in", new int[] { folderCID, resourceCID }));

    addToQuery(query, context);
    query.put(REVISIONS + "." + REVISIONS_CONTAINER, idHandler.toValue(folderID));

    if (name == null || exactMatch)
    {
      query.put(REVISIONS + "." + "name", name);
    }
    else
    {
      query.put(REVISIONS + "." + "name", Pattern.compile("^" + name));
    }

    new QueryEmbeddedRevisions<Boolean>(query)
    {
      @Override
      protected Boolean handleEmbedded(DBObject doc, DBObject embedded)
      {
        int classID = (Integer)embedded.get(REVISIONS_CLASS);
        if (classID != folderCID && classID != resourceCID)
        {
          return null;
        }

        int version = (Integer)embedded.get(REVISIONS_VERSION);
        if (version <= 0)
        {
          return null;
        }

        CDOID container = idHandler.read(embedded, REVISIONS_CONTAINER);
        if (!ObjectUtil.equals(container, folderID))
        {
          return null;
        }

        String revisionName = (String)embedded.get("name");
        if (name == null || exactMatch)
        {
          if (!ObjectUtil.equals(revisionName, name))
          {
            return null;
          }
        }
        else
        {
          if (!revisionName.startsWith(name))
          {
            return null;
          }
        }

        CDOID id = idHandler.read(embedded, REVISIONS_ID);

        long created = (Long)doc.get(COMMITS_ID);
        long revised = getRevised(id, context.getBranch(), version, doc, embedded);
        if (!CDOCommonUtil.isValidTimeStamp(timeStamp, created, revised))
        {
          return null;
        }

        if (!context.addResource(id))
        {
          // No more results allowed
          return true;
        }

        return null;
      }
    }.execute();
  }

  private long getRevised(CDOID id, CDOBranch branch, int version, DBObject doc, DBObject revision)
  {
    Object value = revision.get(REVISIONS_REVISED);
    if (value instanceof Long)
    {
      return (Long)value;
    }

    DBObject query = new BasicDBObject();
    idHandler.write(query, REVISIONS + "." + REVISIONS_ID, id);
    if (store.isBranching())
    {
      query.put(COMMITS_BRANCH, branch.getID());
    }

    int nextVersion = version + 1;
    query.put(REVISIONS + "." + REVISIONS_VERSION, new BasicDBObject("$in", new int[] { nextVersion, -nextVersion }));

    Long result = new Query<Long>(query)
    {
      @Override
      protected Long handleDoc(DBObject doc)
      {
        return (Long)doc.get(COMMITS_ID);
      }
    }.execute();

    if (result != null)
    {
      long revised = result - 1;

      // try
      // {
      // revision.put(REVISIONS_REVISED, revised);
      // collection.save(doc);
      // }
      // catch (Exception ex)
      // {
      // OM.LOG.warn(ex);
      // }

      return revised;
    }

    return CDOBranchPoint.UNSPECIFIED_DATE;
  }

  public InternalCDORevision readRevision(final CDOID id, final CDOBranchPoint branchPoint, int listChunk)
  {
    final CDOBranch branch = branchPoint.getBranch();
    final long timeStamp = branchPoint.getTimeStamp();

    DBObject query = new BasicDBObject();
    idHandler.write(query, REVISIONS + "." + REVISIONS_ID, id);

    addToQuery(query, branchPoint);

    return new QueryEmbeddedRevisions<InternalCDORevision>(query)
    {
      @Override
      public InternalCDORevision execute()
      {
        return execute(collection.find(getRef()).sort(new BasicDBObject(COMMITS_ID, -1)).limit(1));
      }

      @Override
      protected InternalCDORevision handleEmbedded(DBObject doc, DBObject embedded)
      {
        CDOID embeddedID = idHandler.read(embedded, REVISIONS_ID);
        if (!ObjectUtil.equals(embeddedID, id))
        {
          return null;
        }

        long created = (Long)doc.get(COMMITS_ID);
        CDOBranchPoint revisionBranchPoint = branch.getPoint(created);

        InternalCDORevision revision = unmarshallRevision(doc, embedded, id, revisionBranchPoint);

        long revised = revision.getRevised();
        if (!CDOCommonUtil.isValidTimeStamp(timeStamp, created, revised))
        {
          return null;
        }

        return revision;
      }
    }.execute();
  }

  private void addToQuery(DBObject query, CDOBranchPoint branchPoint)
  {
    // Exclude detached objects
    query.put(REVISIONS + "." + REVISIONS_VERSION, new BasicDBObject("$gte", CDOBranchVersion.FIRST_VERSION));

    long timeStamp = branchPoint.getTimeStamp();
    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      query.put(COMMITS_ID, new BasicDBObject("$lte", timeStamp));
    }

    if (store.isBranching())
    {
      int branch = branchPoint.getBranch().getID();
      query.put(COMMITS_BRANCH, branch);
    }
  }

  public InternalCDORevision readRevisionByVersion(final CDOID id, CDOBranchVersion branchVersion, int listChunk)
  {
    DBObject query = new BasicDBObject();
    idHandler.write(query, REVISIONS + "." + REVISIONS_ID, id);

    int version = branchVersion.getVersion();
    query.put(REVISIONS + "." + REVISIONS_VERSION, new BasicDBObject("$in", new int[] { version, -version }));

    final CDOBranch branch = branchVersion.getBranch();
    if (store.isBranching())
    {
      query.put(COMMITS_BRANCH, branch.getID());
    }

    return new QueryEmbeddedRevisions<InternalCDORevision>(query)
    {
      @Override
      protected InternalCDORevision handleEmbedded(DBObject doc, DBObject embedded)
      {
        CDOID revisionID = idHandler.read(embedded, REVISIONS_ID);
        if (!ObjectUtil.equals(revisionID, id))
        {
          return null;
        }

        long revisionTime = (Long)doc.get(COMMITS_ID);
        CDOBranchPoint branchPoint = branch.getPoint(revisionTime);

        return unmarshallRevision(doc, embedded, id, branchPoint);
      }
    }.execute();
  }

  private InternalCDORevision unmarshallRevision(DBObject doc, DBObject embedded, CDOID id, CDOBranchPoint branchPoint)
  {
    int classID = (Integer)embedded.get(REVISIONS_CLASS);
    EClass eClass = store.getClasses().getClass(classID);

    CDOBranch branch = branchPoint.getBranch();
    int version = (Integer)embedded.get(REVISIONS_VERSION);
    long revised = getRevised(id, branch, Math.abs(version), doc, embedded);

    if (version < CDOBranchVersion.FIRST_VERSION)
    {
      long timeStamp = branchPoint.getTimeStamp();
      return new DetachedCDORevision(eClass, id, branch, -version, timeStamp, revised);
    }

    CDOID resourceID = idHandler.read(embedded, REVISIONS_RESOURCE);
    CDOID containerID = idHandler.read(embedded, REVISIONS_CONTAINER);
    int featureID = (Integer)embedded.get(REVISIONS_FEATURE);

    InternalCDORevision result = store.createRevision(eClass, id);
    result.setBranchPoint(branchPoint);
    result.setRevised(revised);
    result.setVersion(version);
    result.setResourceID(resourceID);
    result.setContainerID(containerID);
    result.setContainerFeatureID(featureID);

    unmarshallRevision(embedded, result);

    return result;
  }

  private void unmarshallRevision(DBObject doc, InternalCDORevision revision)
  {
    CDOClassInfo classInfo = revision.getClassInfo();
    for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
    {
      Object value = doc.get(feature.getName());

      if (feature.isUnsettable())
      {
        boolean set = (Boolean)doc.get(feature.getName() + SET_SUFFIX);
        if (!set)
        {
          continue;
        }
      }

      if (value == null)
      {
        if (!feature.isMany())
        {
          if (feature.getDefaultValue() != null)
          {
            value = CDORevisionData.NIL;
          }
        }
      }

      CDOType type = CDOModelUtil.getType(feature);
      ValueHandler valueHandler = store.getValueHandler(type);

      if (feature.isMany())
      {
        if (value != null)
        {
          List<?> list = (List<?>)value;
          CDOList revisionList = revision.getOrCreateList(feature, list.size());
          for (Object element : list)
          {
            element = valueHandler.fromMongo(element);
            revisionList.add(element);
          }
        }
      }
      else
      {
        value = valueHandler.fromMongo(value);
        revision.set(feature, EStore.NO_INDEX, value);
      }
    }
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, final CDOCommitInfoHandler handler)
  {
    if (endTime < CDOBranchPoint.UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Counting not supported");
    }

    DBObject query = new BasicDBObject();

    if (branch != null && store.isBranching())
    {
      query.put(COMMITS_BRANCH, branch.getID());
    }

    BasicDBList list = new BasicDBList();
    if (startTime != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      list.add(new BasicDBObject(QueryOperators.GTE, startTime));
    }

    if (endTime != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      list.add(new BasicDBObject(QueryOperators.LTE, endTime));
    }

    int size = list.size();
    if (size == 2)
    {
      query.put(COMMITS_ID, list);
    }
    else if (size == 1)
    {
      query.put(COMMITS_ID, list.get(0));
    }

    InternalRepository repository = store.getRepository();
    final InternalCDOBranchManager branchManager = repository.getBranchManager();
    final InternalCDOCommitInfoManager commitManager = repository.getCommitInfoManager();

    new Query<Object>(query)
    {
      @Override
      public Object execute()
      {
        return execute(collection.find(getRef()).sort(new BasicDBObject(COMMITS_ID, 1)));
      }

      @Override
      protected Object handleDoc(DBObject doc)
      {
        long time = (Long)doc.get(COMMITS_ID);
        Object value = doc.get(COMMITS_PREVIOUS);
        long previous = value == null ? 0L : (Long)value;

        CDOBranch commitBranch;
        if (store.isBranching())
        {
          int branchID = (Integer)doc.get(COMMITS_BRANCH);
          commitBranch = branchManager.getBranch(branchID);
        }
        else
        {
          commitBranch = branchManager.getMainBranch();
        }

        String user = (String)doc.get(COMMITS_USER);
        String comment = (String)doc.get(COMMITS_COMMENT);

        CDOCommitInfo commitInfo = commitManager.createCommitInfo(commitBranch, time, previous, user, comment, null, null);
        handler.handleCommitInfo(commitInfo);
        return null;
      }
    }.execute();
  }

  /**
   * @author Eike Stepper
   */
  public abstract class QueryEmbedded<RESULT> extends Query<RESULT>
  {
    private String field;

    public QueryEmbedded(DBObject ref, String field)
    {
      super(ref);
      this.field = field;
    }

    @Override
    protected RESULT handleDoc(DBObject doc)
    {
      BasicDBList list = (BasicDBList)doc.get(field);
      for (Object object : list)
      {
        DBObject embedded = (DBObject)object;
        RESULT result = handleEmbedded(doc, embedded);
        if (result != null)
        {
          return result;
        }
      }

      return null;
    }

    protected abstract RESULT handleEmbedded(DBObject doc, DBObject embedded);
  }

  /**
   * @author Eike Stepper
   */
  public abstract class QueryEmbeddedUnits<RESULT> extends QueryEmbedded<RESULT>
  {
    public QueryEmbeddedUnits(DBObject ref)
    {
      super(ref, UNITS);
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract class QueryEmbeddedRevisions<RESULT> extends QueryEmbedded<RESULT>
  {
    public QueryEmbeddedRevisions(DBObject ref)
    {
      super(ref, REVISIONS);
    }
  }
}
