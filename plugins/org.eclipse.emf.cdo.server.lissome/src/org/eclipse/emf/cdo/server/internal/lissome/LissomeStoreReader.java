/*
 * Copyright (c) 2012, 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.lissome.db.Index;
import org.eclipse.emf.cdo.server.internal.lissome.db.IndexReader;
import org.eclipse.emf.cdo.server.internal.lissome.db.IndexReader.RevisionInfo;
import org.eclipse.emf.cdo.server.internal.lissome.file.Journal;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.CommitTransactionTask;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.CreateBranchTask;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.Optimizer;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.OptimizerTask;
import org.eclipse.emf.cdo.server.lissome.ILissomeStoreAccessor;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.DelegatingQueryResourcesContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.StoreAccessorBase;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LissomeStoreReader extends StoreAccessorBase implements ILissomeStoreAccessor
{
  protected Journal journal;

  private LissomeFileHandle journalReader;

  private LissomeFileHandle vobReader;

  private IndexReader indexReader;

  public LissomeStoreReader(LissomeStore store, ISession session)
  {
    super(store, session);
    init(store);
  }

  protected LissomeStoreReader(LissomeStore store, ITransaction transaction)
  {
    super(store, transaction);
    init(store);
  }

  protected void init(LissomeStore store)
  {
    journal = getStore().getJournal();
    indexReader = store.getIndex().createReader();
  }

  protected LissomeFileHandle getJournalReader()
  {
    if (journalReader == null)
    {
      journalReader = getStore().getJournal().openReader();
    }

    return journalReader;
  }

  protected LissomeFileHandle getVobReader()
  {
    if (vobReader == null)
    {
      vobReader = getStore().getVob().openReader();
    }

    return vobReader;
  }

  @Override
  public LissomeStore getStore()
  {
    return (LissomeStore)super.getStore();
  }

  @Override
  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    // return new LissomeStoreChunkReader(this, revision, feature);
    return null;
  }

  @Override
  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    Journal journal = getStore().getJournal();
    return journal.readPackageUnits();
  }

  @Override
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    Journal journal = getStore().getJournal();
    return journal.loadPackageUnit(packageUnit);
  }

  @Override
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      @SuppressWarnings("deprecation") org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder cache)
  {
    Optimizer optimizer = getStore().getOptimizer();
    InternalCDORevision revision = optimizer.readRevision(id, branchPoint);
    if (revision != null)
    {
      if (getStore().getRepository().isSupportingAudits())
      {
        return revision;
      }

      if (revision instanceof DetachedCDORevision || revision.getRevised() != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        return null;
      }

      return revision;
    }

    RevisionInfo info = indexReader.readRevision(id, branchPoint);
    return readRevision(info);
  }

  @Override
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      @SuppressWarnings("deprecation") org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder cache)
  {
    RevisionInfo info = indexReader.readRevisionByVersion(id, branchVersion);
    return readRevision(info);
  }

  protected InternalCDORevision readRevision(RevisionInfo info)
  {
    if (info == null)
    {
      return null;
    }

    long pointer = info.getPointer();
    if (pointer == Index.NULL_POINTER)
    {
      return null;
    }

    LissomeFileHandle reader;
    if (pointer < 0)
    {
      reader = getJournalReader();
      pointer = -pointer;
    }
    else
    {
      reader = getVobReader();
    }

    try
    {
      reader.seek(pointer);

      InternalCDORevision revision = (InternalCDORevision)reader.readCDORevision();
      if (revision != null)
      {
        long revised = info.getRevised();
        if (revised != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          revision.setRevised(revised);
        }
      }

      return revision;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, final CDORevisionHandler handler)
  {
    indexReader.handleRevisions(eClass, branch, timeStamp, exactTime, new RevisionInfo.Handler()
    {
      @Override
      public void handleRevisionInfo(CDOID id, RevisionInfo info)
      {
        // TODO Check if optimizer isn't about to delete the id

        InternalCDORevision revision = readRevision(info);
        if (revision != null)
        {
          handler.handleRevision(revision);
        }
      }
    });

    // TODO Check if optimizer is about to add applicable revisions
    Optimizer optimizer = getStore().getOptimizer();
    optimizer.handleRevisions(eClass, branch, timeStamp, exactTime, handler);
  }

  @Override
  public Set<CDOID> readChangeSet(OMMonitor monitor, CDOChangeSetSegment... segments)
  {
    // TODO: implement LissomeStoreReader.readChangeSet(monitor, segments)
    throw new UnsupportedOperationException();
  }

  @Override
  public void queryResources(final QueryResourcesContext delegate)
  {
    QueryResourcesContext context = new DelegatingQueryResourcesContext()
    {
      private Set<CDOID> queried = new HashSet<>();

      @Override
      protected QueryResourcesContext getDelegate()
      {
        return delegate;
      }

      @Override
      public boolean addResource(CDOID resourceID)
      {
        if (queried.add(resourceID))
        {
          return super.addResource(resourceID);
        }

        return true;
      }
    };

    Optimizer optimizer = getStore().getOptimizer();
    boolean moreResults = optimizer.queryResources(context);

    if (moreResults)
    {
      indexReader.queryResources(context);
    }
  }

  @Override
  public void queryXRefs(QueryXRefsContext context)
  {
    // TODO: implement LissomeStoreReader.queryXRefs(context)
    throw new UnsupportedOperationException();
  }

  @Override
  public void queryLobs(List<byte[]> ids)
  {
    // TODO: implement LissomeStoreReader.queryLobs(ids)
  }

  @Override
  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    // TODO: implement LissomeStoreReader.loadLob(id, out)
    throw new UnsupportedOperationException();
  }

  @Override
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    // TODO: implement LissomeStoreReader.handleLobs(fromTime, toTime, handler)
  }

  @Override
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    String queryLanguage = info.getQueryLanguage();
    if (StringUtil.equalsUpperOrLowerCase(queryLanguage, LissomeQueryHandler.QUERY_LANGUAGE))
    {
      return new LissomeQueryHandler(this);
    }

    return null;
  }

  @Override
  public BranchInfo loadBranch(int branchID)
  {
    return indexReader.loadBranch(branchID);
  }

  @Override
  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return indexReader.loadSubBranches(branchID);
  }

  @Override
  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    return indexReader.loadBranches(startID, endID, branchHandler);
  }

  @Override
  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, final CDOCommitInfoHandler handler)
  {
    if (endTime < CDOBranchPoint.UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Counting not supported");
    }

    InternalCDOCommitInfoManager commitInfoManager = getStore().getRepository().getCommitInfoManager();
    Optimizer optimizer = getStore().getOptimizer();
    OptimizerTask[] tasks = optimizer.getTasks();

    final Journal journal = getStore().getJournal();
    final LissomeFileHandle journalReader = getJournalReader();
    final long[] latestIndexTime = { CDOBranchPoint.UNSPECIFIED_DATE };

    indexReader.loadCommitInfos(branch, startTime, endTime, new IndexReader.PointerHandler()
    {
      @Override
      public void handlePointer(long pointer)
      {
        CDOCommitInfo commitInfo = journal.readCommitInfo(journalReader, pointer);
        handler.handleCommitInfo(commitInfo);
        latestIndexTime[0] = commitInfo.getTimeStamp();
      }
    });

    for (OptimizerTask task : tasks)
    {
      if (task instanceof CommitTransactionTask)
      {
        CommitTransactionTask commitTask = (CommitTransactionTask)task;

        CDOBranchPoint commitBranchPoint = commitTask.getBranchPoint();
        long commitTime = commitBranchPoint.getTimeStamp();
        if (commitTime <= latestIndexTime[0])
        {
          // Commit has already been handled through indexReader
          continue;
        }

        if (branch != null && commitBranchPoint.getBranch() != branch)
        {
          // Not in branch
          continue;
        }

        if (startTime != CDOBranchPoint.UNSPECIFIED_DATE && commitTime < startTime)
        {
          // Before startTime
          continue;
        }

        if (endTime != CDOBranchPoint.UNSPECIFIED_DATE && commitTime > endTime)
        {
          // After endTime
          break;
        }

        CDOCommitInfo commitInfo = commitTask.createCommitInfo(commitInfoManager);
        handler.handleCommitInfo(commitInfo);
      }
    }
  }

  @Override
  public LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    // TODO: implement LissomeStoreReader.getLockArea(durableLockingID)
    throw new UnsupportedOperationException();
  }

  @Override
  public void getLockAreas(String userIDPrefix, Handler handler)
  {
    // TODO: implement LissomeStoreReader.getLockAreas(userIDPrefix, handler)
  }

  @Override
  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    if (branchID == NEW_BRANCH)
    {
      branchID = getStore().getNextBranchID();
    }
    else if (branchID == NEW_LOCAL_BRANCH)
    {
      branchID = getStore().getNextLocalBranchID();
    }

    long pointer = journal.createBranch(branchID, branchInfo);

    InternalCDOBranchManager branchManager = getStore().getRepository().getBranchManager();
    InternalCDOBranch baseBranch = branchManager.getBranch(branchInfo.getBaseBranchID());
    CDOBranchPoint base = baseBranch.getPoint(branchInfo.getBaseTimeStamp());

    CreateBranchTask task = new CreateBranchTask(branchID, branchInfo.getName(), base, pointer);
    Optimizer optimizer = getStore().getOptimizer();
    optimizer.addTask(task);

    return Pair.create(branchID, branchInfo.getBaseTimeStamp());
  }

  @Override
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks) throws LockAreaAlreadyExistsException
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public void updateLockArea(LockArea lockArea)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteLockArea(String durableLockingID)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public void unlock(String durableLockingID)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doRollback(CommitContext commitContext)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    // Implemented in LissomeStoreWriter
    throw new UnsupportedOperationException();
  }
}
