/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject.EStore;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

  public static final String REVISIONS = "revisions";

  public static final String REVISIONS_ID = "cdo_id";

  public static final String REVISIONS_VERSION = "cdo_version";

  public static final String REVISIONS_CLASS = "cdo_class";

  public static final String REVISIONS_RESOURCE = "cdo_resource";

  public static final String REVISIONS_CONTAINER = "cdo_container";

  public static final String REVISIONS_FEATURE = "cdo_feature";

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private IDHandler idHandler;

  private InternalCDOPackageUnit[] systemPackageUnits;

  public Commits(MongoDBStore store)
  {
    super(store, COMMITS);

    ensureIndex(UNITS, UNITS_ID);
    // ensureIndex(UNITS, UNITS_TIME);

    if (store.isBranching())
    {
      DBObject index = new BasicDBObject();
      index.put(REVISIONS + "." + REVISIONS_ID, 1);
      index.put(COMMITS_BRANCH, 1);
      index.put(REVISIONS + "." + REVISIONS_VERSION, 1);

      collection.ensureIndex(index);
    }
    else
    {
      ensureIndex(REVISIONS, REVISIONS_ID, REVISIONS_VERSION);
    }

    idHandler = store.getIDHandler();
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    Collection<InternalCDOPackageUnit> packageUnits = new ArrayList<InternalCDOPackageUnit>();

    DBObject query = new BasicDBObject();
    query.put(UNITS, new BasicDBObject("$exists", true));

    DBCursor cursor = collection.find(query);
    while (cursor.hasNext())
    {
      DBObject doc = cursor.next();
      readPackageUnits(doc, packageUnits);
    }

    return packageUnits;
  }

  private void readPackageUnits(DBObject doc, Collection<InternalCDOPackageUnit> packageUnits)
  {
    // DBObject units = (DBObject)doc.get(UNITS);
    Object object = doc.get(UNITS);
    System.out.println(object);

    // TODO: implement readPackageUnits(doc, packageUnits)
    throw new UnsupportedOperationException();
  }

  public void writePackageUnits(MongoDBStoreAccessor mongoDBStoreAccessor, InternalCDOPackageUnit[] packageUnits,
      OMMonitor monitor)
  {
    systemPackageUnits = packageUnits;
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
      // doc.put(UNITS_TIME, packageUnit.getTimeStamp());
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

      List<DBObject> docs = new ArrayList<DBObject>();
      marshalRevisions(docs, context.getNewObjects());
      marshalRevisions(docs, context.getDirtyObjects());
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

  private void marshalRevisions(List<DBObject> docs, InternalCDORevision[] revisions)
  {
    for (InternalCDORevision revision : revisions)
    {
      DBObject doc = marshallRevision(revision);
      docs.add(doc);
    }
  }

  private DBObject marshallRevision(InternalCDORevision revision)
  {
    DBObject doc = new BasicDBObject();
    idHandler.write(doc, REVISIONS_ID, revision.getID());
    // if (store.isBranching())
    // {
    // int branch = revision.getBranch().getID();
    // doc.put(REVISIONS_BRANCH, branch);
    // }

    doc.put(REVISIONS_VERSION, revision.getVersion());

    EClass eClass = revision.getEClass();
    doc.put(REVISIONS_CLASS, store.getClasses().getClassifierID(eClass));

    CDOID resourceID = revision.getResourceID();
    idHandler.write(doc, REVISIONS_RESOURCE, resourceID);

    CDOID containerID = (CDOID)revision.getContainerID();
    idHandler.write(doc, REVISIONS_CONTAINER, containerID);

    int featureID = revision.getContainingFeatureID();
    doc.put(REVISIONS_FEATURE, featureID);

    CDOClassInfo classInfo = CDOModelUtil.getClassInfo(eClass); // TODO Cache id-->classInfo
    for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
    {
      Object value = revision.getValue(feature);
      if (value == null)
      {
        continue;
      }

      if (feature.isMany())
      {
        List<?> list = (List<?>)value;
        Object[] array = new Object[list.size()];
        int i = 0;
        for (Object element : list)
        {
          if (feature instanceof EReference) // TODO Remove loop invariant
          {
            CDOID id = (CDOID)element;
            element = idHandler.toValue(id);
          }

          array[i++] = element;
        }

        value = array;
      }
      else if (feature instanceof EReference)
      {
        CDOID id = (CDOID)value;
        value = idHandler.toValue(id);
      }

      doc.put(feature.getName(), value);
    }

    return doc;
  }

  public void queryResources(final QueryResourcesContext context)
  {
    Classes classes = getStore().getClasses();
    final int resourceFolderClassID = classes.getResourceFolderClassID();
    final int resourceClassID = classes.getResourceClassID();

    final CDOID folderID = context.getFolderID();
    final String name = context.getName();
    final boolean exactMatch = context.exactMatch();

    DBObject query = new BasicDBObject();
    query.put("$or", new Object[] { new BasicDBObject(REVISIONS + "." + REVISIONS_CLASS, resourceFolderClassID),
        new BasicDBObject(REVISIONS + "." + REVISIONS_CLASS, resourceClassID) });

    query.put(REVISIONS + "." + REVISIONS_VERSION, new BasicDBObject("$gt", 0)); // Not detached
    query.put(REVISIONS + "." + REVISIONS_CONTAINER, idHandler.toValue(folderID));

    if (name == null)
    {
      query.put(REVISIONS + "." + "name", new BasicDBObject("$exists", false));
    }
    else if (exactMatch)
    {
      query.put(REVISIONS + "." + "name", name);
    }
    else
    {
      query.put(REVISIONS + "." + "name", new BasicDBObject("$regex", "/^" + name + "/"));
    }

    new Revisions<Boolean>(query)
    {
      @Override
      protected Boolean handleRevision(DBObject doc, DBObject revision)
      {
        int classID = (Integer)revision.get(REVISIONS_CLASS);
        if (classID != resourceFolderClassID && classID != resourceClassID)
        {
          return null;
        }

        int version = (Integer)revision.get(REVISIONS_VERSION);
        if (version <= 0)
        {
          return null;
        }

        CDOID container = idHandler.read(revision, REVISIONS_CONTAINER);
        if (!ObjectUtil.equals(container, folderID))
        {
          return null;
        }

        if (name == null)
        {
          if (revision.containsField("name"))
          {
            return null;
          }
        }
        else
        {
          String revisionName = (String)revision.get("name");
          if (revisionName == null)
          {
            return null;
          }

          if (exactMatch)
          {
            if (!revisionName.equals(name))
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
        }

        CDOID id = idHandler.read(revision, REVISIONS_ID);
        if (!context.addResource(id))
        {
          // No more results allowed
          return true;
        }

        return null;
      }
    }.execute();
  }

  public InternalCDORevision readRevision(final CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    final int branch = branchPoint.getBranch().getID();
    final long timeStamp = branchPoint.getTimeStamp();

    DBObject query = new BasicDBObject();
    idHandler.write(query, REVISIONS + "." + REVISIONS_ID, id);

    if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      query.put(COMMITS_ID, new BasicDBObject("$lte", timeStamp));
    }

    if (store.isBranching())
    {
      query.put(COMMITS_BRANCH, branch);
    }

    return new Revisions<InternalCDORevision>(query)
    {
      @Override
      public InternalCDORevision execute()
      {
        return execute(collection.find(getRef()).sort(new BasicDBObject(COMMITS_ID, -1)).limit(1));
      }

      @Override
      protected InternalCDORevision handleRevision(DBObject doc, DBObject revision)
      {
        CDOID revisionID = idHandler.read(revision, REVISIONS_ID);
        if (!ObjectUtil.equals(revisionID, id))
        {
          return null;
        }

        int revisionBranch = CDOBranch.MAIN_BRANCH_ID;
        if (store.isBranching())
        {
          revisionBranch = (Integer)doc.get(COMMITS_BRANCH);
        }

        long revisionTime = (Long)doc.get(COMMITS_ID);

        int classID = (Integer)revision.get(REVISIONS_CLASS);
        EClass eClass = store.getClasses().getClass(classID);

        int version = (Integer)revision.get(REVISIONS_VERSION);
        CDOID resourceID = idHandler.read(revision, REVISIONS_RESOURCE);
        CDOID containerID = idHandler.read(revision, REVISIONS_CONTAINER);
        int featureID = (Integer)revision.get(REVISIONS_FEATURE);

        InternalCDOBranchManager branchManager = store.getRepository().getBranchManager();
        CDOBranchPoint revisionBranchPoint = branchManager.getBranch(revisionBranch).getPoint(revisionTime);

        InternalCDORevision result = store.createRevision(eClass, id);
        result.setBranchPoint(revisionBranchPoint);
        result.setVersion(version);
        result.setResourceID(resourceID);
        result.setContainerID(containerID);
        result.setContainingFeatureID(featureID);

        CDOClassInfo classInfo = CDOModelUtil.getClassInfo(eClass); // TODO Cache id-->classInfo
        for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
        {
          Object value = revision.get(feature.getName());
          if (feature.isMany())
          {
            if (value != null)
            {
              List<?> list = (List<?>)value;
              CDOList revisionList = result.getList(feature, list.size());
              for (Object element : list)
              {
                if (element != null && feature instanceof EReference)
                {
                  element = idHandler.fromValue(element);
                }

                revisionList.add(element);
              }
            }
          }
          else
          {
            if (value != null && feature instanceof EReference)
            {
              value = idHandler.fromValue(value);
            }

            result.set(feature, EStore.NO_INDEX, value);
          }
        }

        return result;
      }
    }.execute();
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Revisions<RESULT> extends Query<RESULT>
  {
    public Revisions(DBObject ref)
    {
      super(ref);
    }

    @Override
    protected RESULT handleDoc(DBObject doc)
    {
      BasicDBList list = (BasicDBList)doc.get(REVISIONS);
      for (Object object : list)
      {
        DBObject revision = (DBObject)object;
        RESULT result = handleRevision(doc, revision);
        if (result != null)
        {
          return result;
        }
      }

      return null;
    }

    protected abstract RESULT handleRevision(DBObject doc, DBObject revision);
  }

}
