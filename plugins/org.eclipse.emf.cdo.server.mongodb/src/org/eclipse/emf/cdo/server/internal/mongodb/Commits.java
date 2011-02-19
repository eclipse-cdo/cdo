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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;
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

  public static final String UNITS_TIME = "time";

  public static final String UNITS_DATA = "data";

  public static final String PACKAGES = "packages";

  public static final String PACKAGES_URI = "uri";

  public static final String PACKAGES_PARENT = "parent";

  public static final String CLASSIFIER_PREFIX = "c";

  public static final String REVISIONS = "revisions";

  public static final String REVISIONS_ID = "cdo_id";

  public static final String REVISIONS_BRANCH = "cdo_branch";

  public static final String REVISIONS_VERSION = "cdo_version";

  public static final String REVISIONS_CLASS = "cdo_class";

  public static final String REVISIONS_RESOURCE = "cdo_resource";

  public static final String REVISIONS_CONTAINER = "cdo_container";

  public static final String REVISIONS_FEATURE = "cdo_feature";

  private static final boolean ZIP_PACKAGE_BYTES = true;

  private IDHandler idHandler;

  private int resourceFolderClassID;

  private int resourceClassID;

  private InternalCDOPackageUnit[] systemPackageUnits;

  public Commits(MongoDBStore store)
  {
    super(store, COMMITS);

    ensureIndex(UNITS, UNITS_ID);
    ensureIndex(UNITS, UNITS_TIME);

    if (store.isBranching())
    {
      ensureIndex(REVISIONS, REVISIONS_ID, REVISIONS_BRANCH, REVISIONS_VERSION);
    }
    else
    {
      ensureIndex(REVISIONS, REVISIONS_ID, REVISIONS_VERSION);
    }

    idHandler = store.getIDHandler();

    Classes classes = getStore().getClasses();
    resourceFolderClassID = classes.getResourceFolderClassID();
    resourceClassID = classes.getResourceClassID();
  }

  public void writePackageUnits(MongoDBStoreAccessor mongoDBStoreAccessor, InternalCDOPackageUnit[] packageUnits,
      OMMonitor monitor)
  {
    systemPackageUnits = packageUnits;
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
      doc.put(UNITS_TIME, packageUnit.getTimeStamp());
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
    if (store.isBranching())
    {
      int branch = revision.getBranch().getID();
      doc.put(REVISIONS_BRANCH, branch);
    }

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
          if (feature instanceof EReference)
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

  public void queryResources(final QueryResourcesContext context)
  {

    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    DBObject query = new BasicDBObject();
    query.put("$or", new Object[] { new BasicDBObject(REVISIONS + "." + REVISIONS_CLASS, resourceFolderClassID),
        new BasicDBObject(REVISIONS + "." + REVISIONS_CLASS, resourceClassID) });

    query.put(REVISIONS + "." + REVISIONS_VERSION, new BasicDBObject("$gt", 0)); // Not detached

    if (CDOIDUtil.isNull(folderID))
    {
      query.put(REVISIONS + "." + REVISIONS_CONTAINER, new BasicDBObject("$exists", false));
    }
    else
    {
      query.put(REVISIONS + "." + REVISIONS_CONTAINER, getStore().getIDHandler().toValue(folderID));
    }

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

    new Revisions(query)
    {
      @Override
      protected void handleRevision(DBObject revision)
      {
        int classID = (Integer)revision.get(REVISIONS_CLASS);
        if (classID != resourceFolderClassID && classID != resourceClassID)
        {
          return;
        }

        CDOID id = idHandler.read(revision, REVISIONS_ID);

        if (!context.addResource(id))
        {
          // No more results allowed
          return;
        }
      }
    }.execute();
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Query
  {
    private DBObject ref;

    public Query(DBObject ref)
    {
      this.ref = ref;
    }

    @Deprecated
    public DBObject pair(String key, Object value)
    {
      return new BasicDBObject(key, value);
    }

    public final int execute()
    {
      return execute(collection.find(ref));
    }

    public final int execute(DBObject keys)
    {
      return execute(collection.find(ref, keys));
    }

    private int execute(DBCursor cursor)
    {
      try
      {
        int i = 0;
        while (cursor.hasNext())
        {
          DBObject doc = cursor.next();
          handleDoc(i++, doc);
        }

        return i;
      }
      finally
      {
        cursor.close();
      }
    }

    protected abstract void handleDoc(int i, DBObject doc);
  }

  /**
   * @author Eike Stepper
   */
  public abstract class ListQuery<RESULT> extends Query
  {
    private List<RESULT> results = new ArrayList<RESULT>();

    public ListQuery(DBObject ref)
    {
      super(ref);
    }

    public final List<RESULT> getResults()
    {
      execute();
      return results;
    }

    @Override
    protected final void handleDoc(int i, DBObject doc)
    {
      handleDoc(i, doc, results);
    }

    protected abstract void handleDoc(int i, DBObject doc, List<RESULT> results);
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Revisions extends Query
  {
    public Revisions(DBObject ref)
    {
      super(ref);
    }

    @Override
    protected void handleDoc(int i, DBObject doc)
    {
      BasicDBList list = (BasicDBList)doc.get(REVISIONS);
      for (Object object : list)
      {
        DBObject revision = (DBObject)object;
        handleRevision(revision);
      }
    }

    protected abstract void handleRevision(DBObject revision);
  }
}
