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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
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
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessor;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MongoDBStoreAccessor extends StoreAccessor implements IMongoDBStoreAccessor
{
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

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
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

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void rollback(CommitContext commitContext)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  protected void doActivate() throws Exception
  {
  }

  @Override
  protected void doDeactivate() throws Exception
  {
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // Pooling not supported
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // Pooling not supported
    throw new UnsupportedOperationException();
  }
}
