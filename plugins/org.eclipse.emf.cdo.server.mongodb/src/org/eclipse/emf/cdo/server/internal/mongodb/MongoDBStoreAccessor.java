/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorBase;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

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
  private Commits commits;

  public MongoDBStoreAccessor(Store store, ISession session)
  {
    super(store, session);
    commits = getStore().getCommits();
  }

  public MongoDBStoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
    commits = getStore().getCommits();
  }

  @Override
  public MongoDBStore getStore()
  {
    return (MongoDBStore)super.getStore();
  }

  @Override
  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    // Partial collection loading not supported, yet.
    return null;
  }

  @Override
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return commits.readPackageUnits();
  }

  @Override
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return commits.loadPackageUnit(packageUnit);
  }

  @Override
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      @SuppressWarnings("deprecation") org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder cache)
  {
    return commits.readRevision(id, branchPoint, listChunk);
  }

  @Override
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      @SuppressWarnings("deprecation") org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder cache)
  {
    return commits.readRevisionByVersion(id, branchVersion, listChunk);
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public void queryResources(QueryResourcesContext context)
  {
    commits.queryResources(context);
  }

  @Override
  public void queryXRefs(QueryXRefsContext context)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public void queryLobs(List<byte[]> ids)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return null;
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  @Override
  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    commits.loadCommitInfos(branch, startTime, endTime, handler);
  }

  @Override
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    commits.writePackageUnits(this, packageUnits, monitor);
  }

  @Override
  protected void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    commits.write(this, context, monitor);
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void doRollback(CommitContext commitContext)
  {
    // Do nothing
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    return getStore().getIDHandler().getNextCDOID(revision);
  }
}
