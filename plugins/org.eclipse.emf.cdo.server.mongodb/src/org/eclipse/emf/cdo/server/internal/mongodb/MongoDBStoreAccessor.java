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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorBase;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MongoDBStoreAccessor extends StoreAccessorBase implements IMongoDBStoreAccessor
{
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
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
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
    DBCollection packageUnitsCollection = getStore().getPackageUnitsCollection();
    for (DBObject doc : marshallPackageUnits(packageUnits))
    {
      packageUnitsCollection.insert(doc);
    }
  }

  public void write(InternalCommitContext context, OMMonitor monitor)
  {
    CDOBranchPoint branchPoint = context.getBranchPoint();

    DBObject doc = new BasicDBObject();
    doc.put("_id", branchPoint.getTimeStamp());
    doc.put("previous", context.getPreviousTimeStamp());
    doc.put("branch", branchPoint.getBranch().getID());
    doc.put("user", context.getUserID());
    doc.put("comment", context.getCommitComment());

    InternalCDOPackageUnit[] newPackageUnits = context.getNewPackageUnits();
    if (newPackageUnits != null)
    {
      doc.put("meta", marshallPackageUnits(newPackageUnits));
    }

    InternalCDORevision[] newObjects = context.getNewObjects();
    if (newObjects != null)
    {
      doc.put("new", marshallRevisions(newObjects));
    }

    InternalCDORevisionDelta[] dirtyObjectDeltas = context.getDirtyObjectDeltas();
    if (dirtyObjectDeltas != null)
    {
      doc.put("changed", marshallRevisionDeltas(dirtyObjectDeltas));
    }

    CDOID[] detachedObjects = context.getDetachedObjects();
    if (detachedObjects != null)
    {
      doc.put("detached", marshallCDOIDs(detachedObjects));
    }

    getStore().getCommitInfosCollection().insert(doc);
  }

  private DBObject[] marshallPackageUnits(InternalCDOPackageUnit[] packageUnits)
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

  private DBObject[] marshallRevisions(InternalCDORevision[] revisions)
  {
    DBObject[] result = new DBObject[revisions.length];
    for (int i = 0; i < revisions.length; i++)
    {
      InternalCDORevision revision = revisions[i];
    }

    return result;
  }

  private DBObject[] marshallRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  private DBObject[] marshallCDOIDs(CDOID[] ids)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
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
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }
}
