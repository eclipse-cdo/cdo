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
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.mongodb.bundle.OM;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorBase;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MongoDBStoreAccessor extends StoreAccessorBase implements IMongoDBStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, MongoDBStoreAccessor.class);

  private static final boolean ZIP_PACKAGE_BYTES = true;

  public MongoDBStoreAccessor(Store store, ISession session)
  {
    super(store, session);
  }

  public MongoDBStoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public MongoDBStore getStore()
  {
    return (MongoDBStore)super.getStore();
  }

  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    // Partial collection loading not supported, yet.
    return null;
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public CDOID readResourceID(CDOID folderID, String name, CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void queryResources(QueryResourcesContext context)
  {
    // Only support timestamp in audit mode
    if (context.getTimeStamp() != CDORevision.UNSPECIFIED_DATE && !getStore().getRepository().isSupportingAudits())
    {
      throw new IllegalArgumentException("Auditing not supported");
    }

    EresourcePackage resourcesPackage = EresourcePackage.eINSTANCE;

    // First query folders
    boolean shallContinue = queryResources(context, resourcesPackage.getCDOResourceFolder());

    // Not enough results? -> query resources
    if (shallContinue)
    {
      queryResources(context, resourcesPackage.getCDOResource());
    }
  }

  private boolean queryResources(QueryResourcesContext context, EClass eClass)
  {
    IDHandler idHandler = getStore().getIDHandler();
    Mapper mapper = getStore().getMapper();

    DBCollection collection = mapper.getCollection(eClass);
    DBCursor cursor = null;

    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();

    try
    {

      DBObject query = getStore().getMode().createResourcesQuery(folderID, name, exactMatch, context);
      DBObject keys = new BasicDBObject("_cdoid", 1);
      cursor = collection.find(query, keys);
      while (cursor.hasNext())
      {
        DBObject doc = cursor.next();

        CDOID id = idHandler.read(doc, "_cdoid");
        if (TRACER.isEnabled())
        {
          TRACER.trace("Resources query returned ID " + id); //$NON-NLS-1$
        }

        if (!context.addResource(id))
        {
          // No more results allowed
          return false; // Don't continue
        }
      }

      return true; // Continue with other results
    }
    finally
    {
    }
  }

  public void queryXRefs(QueryXRefsContext context)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void queryLobs(List<byte[]> ids)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public BranchInfo loadBranch(int branchID)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public CDOCommitData loadCommitData(long timeStamp)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    MongoDBStore store = getStore();
    DBCollection packageUnitsCollection = store.getPackageUnitsCollection();
    DBObject[] docs = marshallPackageUnits(packageUnits);

    for (DBObject doc : docs)
    {
      packageUnitsCollection.insert(doc);
    }
  }

  public void write(InternalCommitContext context, OMMonitor monitor)
  {
    try
    {
      monitor.begin(107);
      CDOBranchPoint branchPoint = context.getBranchPoint();

      DBObject doc = new BasicDBObject();
      doc.put("_id", branchPoint.getTimeStamp());

      long previous = context.getPreviousTimeStamp();
      if (previous != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        doc.put("previous", previous);
      }

      if (getStore().getRepository().isSupportingBranches())
      {
        doc.put("branch", branchPoint.getBranch().getID());
      }

      String user = context.getUserID();
      if (user != null)
      {
        doc.put("user", user);
      }

      String comment = context.getCommitComment();
      if (comment != null)
      {
        doc.put("comment", comment);
      }

      InternalCDOPackageUnit[] newPackageUnits = context.getNewPackageUnits();
      if (!ObjectUtil.isEmpty(newPackageUnits))
      {
        doc.put("meta", marshallPackageUnits(newPackageUnits));
      }

      monitor.worked();
      addIDMappings(context, monitor.fork());
      context.applyIDMappings(monitor.fork());

      InternalCDORevision[] newObjects = context.getNewObjects();
      if (!ObjectUtil.isEmpty(newObjects))
      {
        doc.put("new", marshallRevisions(newObjects));
      }

      monitor.worked();
      InternalCDORevisionDelta[] dirtyObjectDeltas = context.getDirtyObjectDeltas();
      if (!ObjectUtil.isEmpty(dirtyObjectDeltas))
      {
        doc.put("changed", marshallRevisionDeltas(dirtyObjectDeltas));
      }

      monitor.worked();
      Map<CDOID, EClass> detachedObjectTypes = context.getDetachedObjectTypes();
      if (!ObjectUtil.isEmpty(detachedObjectTypes))
      {
        doc.put("detached", marshallObjectTypes(detachedObjectTypes));
      }

      monitor.worked();
      getStore().getCommitInfosCollection().insert(doc);
      monitor.worked(100);

      CDOCommitInfo commitInfo = context.createCommitInfo();
      getStore().getMapper().addWork(commitInfo);
    }
    finally
    {
      monitor.done();
    }
  }

  public void commit(OMMonitor monitor)
  {
    // Do nothing
  }

  public void rollback()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    return getStore().getIDHandler().getNextCDOID(revision);
  }

  protected DBObject[] marshallPackageUnits(InternalCDOPackageUnit[] packageUnits)
  {
    DBObject[] result = new DBObject[packageUnits.length];
    InternalCDOPackageRegistry packageRegistry = getStore().getRepository().getPackageRegistry();

    for (int i = 0; i < packageUnits.length; i++)
    {
      InternalCDOPackageUnit packageUnit = packageUnits[i];
      EPackage ePackage = packageUnit.getTopLevelPackageInfo().getEPackage();
      byte[] bytes = EMFUtil.getEPackageBytes(ePackage, ZIP_PACKAGE_BYTES, packageRegistry);

      DBObject doc = new BasicDBObject();
      doc.put("_id", packageUnit.getID());
      doc.put("originalType", packageUnit.getOriginalType().toString());
      doc.put("timeStamp", packageUnit.getTimeStamp());
      doc.put("packageData", bytes);

      result[i] = doc;
    }

    return result;
  }

  protected DBObject[] marshallRevisions(InternalCDORevision[] revisions)
  {
    DBObject[] result = new DBObject[revisions.length];
    for (int i = 0; i < revisions.length; i++)
    {
      InternalCDORevision revision = revisions[i];
      result[i] = marshallRevision(revision);
    }

    return result;
  }

  protected DBObject marshallRevision(InternalCDORevision revision)
  {
    IDHandler idHandler = getStore().getIDHandler();

    DBObject doc = new BasicDBObject();
    idHandler.write(doc, "cdo_id", revision.getID());
    if (getStore().getRepository().isSupportingBranches())
    {
      int branch = revision.getBranch().getID();
      if (branch != 0)
      {
        doc.put("cdo_branch", branch);
      }
    }

    doc.put("cdo_version", revision.getVersion());
    doc.put("cdo_created", revision.getTimeStamp());

    long revised = revision.getRevised();
    if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      doc.put("cdo_revised", revised);
    }

    doc.put("cdo_class", new CDOClassifierRef(revision.getEClass()).getURI());

    CDOID resourceID = revision.getResourceID();
    if (!CDOIDUtil.isNull(resourceID))
    {
      idHandler.write(doc, "cdo_resource", resourceID);
    }

    CDOID containerID = (CDOID)revision.getContainerID();
    if (!CDOIDUtil.isNull(containerID))
    {
      idHandler.write(doc, "cdo_container", containerID);
      int featureID = revision.getContainingFeatureID();
      if (featureID != 0)
      {
        doc.put("cdo_feature", featureID);
      }
    }

    return doc;
  }

  protected DBObject[] marshallRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas)
  {
    DBObject[] result = new DBObject[revisionDeltas.length];
    for (int i = 0; i < revisionDeltas.length; i++)
    {
      InternalCDORevisionDelta revisionDelta = revisionDeltas[i];
      result[i] = marshallRevisionDelta(revisionDelta);
    }

    return result;
  }

  protected DBObject marshallRevisionDelta(InternalCDORevisionDelta revisionDelta)
  {
    IDHandler idHandler = getStore().getIDHandler();

    DBObject doc = new BasicDBObject();
    idHandler.write(doc, "cdo_id", revisionDelta.getID());
    if (getStore().getRepository().isSupportingBranches())
    {
      doc.put("cdo_branch", revisionDelta.getBranch().getID());
    }

    // doc.put("cdo_version", revisionDelta.getVersion());

    return doc;
  }

  private DBObject[] marshallObjectTypes(Map<CDOID, EClass> objectTypes)
  {
    IDHandler idHandler = getStore().getIDHandler();
    Iterator<Entry<CDOID, EClass>> it = objectTypes.entrySet().iterator();

    DBObject[] result = new DBObject[objectTypes.size()];
    for (int i = 0; i < result.length; i++)
    {
      Entry<CDOID, EClass> entry = it.next();
      CDOID id = entry.getKey();
      EClass type = entry.getValue();

      DBObject doc = new BasicDBObject();
      idHandler.write(doc, "id", id);
      doc.put("type", new CDOClassifierRef(type).getURI());

      result[i] = doc;
    }

    return result;
  }
}
