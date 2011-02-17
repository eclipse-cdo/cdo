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
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
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

      if (getStore().isBranching())
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
    InternalCDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();

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
        int classifierID = getStore().mapNewClassifier(classifier);
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
    IDHandler idHandler = getStore().getIDHandler();

    DBObject doc = new BasicDBObject();
    idHandler.write(doc, REVISIONS_ID, revision.getID());
    if (getStore().isBranching())
    {
      int branch = revision.getBranch().getID();
      if (branch != 0)
      {
        doc.put(REVISIONS_BRANCH, branch);
      }
    }

    doc.put(REVISIONS_VERSION, revision.getVersion());
    doc.put(REVISIONS_CLASS, store.getClassifierID(revision.getEClass()));

    CDOID resourceID = revision.getResourceID();
    if (!CDOIDUtil.isNull(resourceID))
    {
      idHandler.write(doc, REVISIONS_RESOURCE, resourceID);
    }

    CDOID containerID = (CDOID)revision.getContainerID();
    if (!CDOIDUtil.isNull(containerID))
    {
      idHandler.write(doc, REVISIONS_CONTAINER, containerID);
      int featureID = revision.getContainingFeatureID();
      if (featureID != 0)
      {
        doc.put(REVISIONS_FEATURE, featureID);
      }
    }

    return doc;
  }
}
