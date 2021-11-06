/*
 * Copyright (c) 2009-2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking2;
import org.eclipse.emf.cdo.server.IStoreAccessor.Raw2;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader5;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Simon McDuff
 */
public class MEMStoreAccessor extends LongIDStoreAccessor implements Raw2, DurableLocking2, BranchLoader5
{
  private final MEMStore store;

  private List<InternalCDORevision> newRevisions;

  public MEMStoreAccessor(MEMStore store, ISession session)
  {
    super(store, session);
    this.store = store;
  }

  /**
   * @since 2.0
   */
  public MEMStoreAccessor(MEMStore store, ITransaction transaction)
  {
    super(store, transaction);
    this.store = store;
  }

  @Override
  public MEMStore getStore()
  {
    return store;
  }

  /**
   * @since 2.0
   */
  @Override
  public MEMStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new MEMStoreChunkReader(this, revision, feature);
  }

  @Override
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return Collections.emptySet();
  }

  @Override
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    return store.createBranch(branchID, branchInfo);
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    return store.loadBranch(branchID);
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return store.loadSubBranches(branchID);
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    return store.loadBranches(startID, endID, branchHandler);
  }

  @Override
  public CDOBranch[] deleteBranches(int branchID, OMMonitor monitor)
  {
    return store.deleteBranches(branchID, monitor);
  }

  @Override
  @Deprecated
  public void deleteBranch(int branchID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void renameBranch(int branchID, String newName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void renameBranch(int branchID, String oldName, String newName)
  {
    store.renameBranch(branchID, oldName, newName);
  }

  @Override
  public CDOBranchPoint changeTag(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    // This store is not persistent.
    // The accessor will initially be asked to load stored tags and return none.
    // Later the repository will only serve and modify the cached tags.
    return null;
  }

  @Override
  public void loadTags(String name, Consumer<BranchInfo> handler)
  {
    // This store is not persistent.
    // The accessor will initially be asked to load stored tags and return none.
    // Later the repository will only serve and modify the cached tags.
  }

  @Override
  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    store.loadCommitInfos(branch, startTime, endTime, handler);
  }

  @Override
  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    return store.readChangeSet(segments);
  }

  @Override
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk, CDORevisionCacheAdder cache)
  {
    return store.getRevision(id, branchPoint);
  }

  @Override
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk, CDORevisionCacheAdder cache)
  {
    return store.getRevisionByVersion(id, branchVersion);
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    store.handleRevisions(eClass, branch, timeStamp, exactTime, handler);
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doCommit(OMMonitor monitor)
  {
    newRevisions = null;
  }

  @Override
  public void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    synchronized (store)
    {
      super.doWrite(context, monitor);
    }
  }

  @Deprecated
  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, CDOBranchPoint mergeSource,
      OMMonitor monitor)
  {
    store.addCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, mergeSource);
  }

  @Override
  protected void doRollback(CommitContext context)
  {
    if (newRevisions != null)
    {
      synchronized (store)
      {
        for (InternalCDORevision revision : newRevisions)
        {
          store.rollbackRevision(revision);
        }
      }
    }
  }

  @Override
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    for (InternalCDORevision revision : revisions)
    {
      writeRevision(revision);
    }
  }

  protected void writeRevision(InternalCDORevision revision)
  {
    if (newRevisions == null)
    {
      newRevisions = new ArrayList<>();
    }

    newRevisions.add(revision);
    store.addRevision(revision, false);
  }

  /**
   * @since 2.0
   */
  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created, OMMonitor monitor)
  {
    for (InternalCDORevisionDelta revisionDelta : revisionDeltas)
    {
      writeRevisionDelta(revisionDelta, branch, created);
    }
  }

  /**
   * @since 2.0
   */
  protected void writeRevisionDelta(InternalCDORevisionDelta revisionDelta, CDOBranch branch, long created)
  {
    CDOID id = revisionDelta.getID();
    InternalCDORevision revision = store.getRevisionByVersion(id, revisionDelta);
    if (revision == null)
    {
      throw new ConcurrentModificationException("Trying to update object " + id //$NON-NLS-1$
          + " that was already modified"); //$NON-NLS-1$
    }

    InternalCDORevision newRevision = revision.copy();
    newRevision.adjustForCommit(branch, created);

    revisionDelta.applyTo(newRevision);
    writeRevision(newRevision);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    for (CDOID id : detachedObjects)
    {
      detachObject(id, branch, timeStamp);
    }
  }

  /**
   * @since 3.0
   */
  protected void detachObject(CDOID id, CDOBranch branch, long timeStamp)
  {
    store.detachObject(id, branch, timeStamp);
  }

  /**
   * @since 2.0
   */
  @Override
  public void queryResources(QueryResourcesContext context)
  {
    store.queryResources(context);
  }

  @Override
  public void queryXRefs(QueryXRefsContext context)
  {
    store.queryXRefs(context);
  }

  @Override
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return null;
  }

  @Override
  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime) throws IOException
  {
    store.rawExport(out, fromBranchID, toBranchID, fromCommitTime, toCommitTime);
  }

  @Override
  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    store.rawImport(in, fromBranchID, toBranchID, fromCommitTime, toCommitTime, monitor);
  }

  @Override
  public void rawStore(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    writePackageUnits(packageUnits, monitor);
  }

  @Override
  public void rawStore(InternalCDORevision revision, OMMonitor monitor)
  {
    store.addRevision(revision, true);
  }

  @Override
  public void rawStore(byte[] id, long size, InputStream inputStream) throws IOException
  {
    writeBlob(id, size, inputStream);
  }

  @Override
  public void rawStore(byte[] id, long size, Reader reader) throws IOException
  {
    writeClob(id, size, reader);
  }

  @Override
  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, OMMonitor monitor)
  {
    writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, null, monitor);
  }

  @Override
  public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment, CDOBranchPoint mergeSource, OMMonitor monitor)
  {
    writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, mergeSource, monitor);
  }

  @Override
  public void rawDelete(CDOID id, int version, CDOBranch branch, EClass eClass, OMMonitor monitor)
  {
    store.rawDelete(id, version, branch);
  }

  @Override
  public void rawCommit(double commitWork, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    return store.createLockArea(userID, branchPoint, readOnly, locks);
  }

  @Override
  public LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    return store.createLockArea(durableLockingID, userID, branchPoint, readOnly, locks);
  }

  @Override
  public void updateLockArea(LockArea lockArea)
  {
    store.updateLockArea(lockArea);
  }

  @Override
  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    return store.getLockArea(durableLockingID);
  }

  @Override
  public void getLockAreas(String userIDPrefix, Handler handler)
  {
    store.getLockAreas(userIDPrefix, handler);
  }

  @Override
  public void deleteLockArea(String durableLockingID)
  {
    store.deleteLockArea(durableLockingID);
  }

  @Override
  public void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    store.lock(durableLockingID, type, objectsToLock);
  }

  @Override
  public void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    store.unlock(durableLockingID, type, objectsToUnlock);
  }

  @Override
  public void unlock(String durableLockingID)
  {
    store.unlock(durableLockingID);
  }

  @Override
  public void queryLobs(List<byte[]> ids)
  {
    store.queryLobs(ids);
  }

  @Override
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    store.handleLobs(fromTime, toTime, handler);
  }

  @Override
  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    store.loadLob(id, out);
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    store.writeBlob(id, size, inputStream);
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    store.writeClob(id, size, reader);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (newRevisions != null)
    {
      newRevisions.clear();
      newRevisions = null;
    }

    super.doDeactivate();
  }
}
