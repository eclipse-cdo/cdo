/*
 * Copyright (c) 2010-2016, 2019-2021, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Martin Fluegge - maintenance, bug 318518
 *    Christian W. Damus (CEA LIST) - bug 399487
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitData;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.server.LockingManager.LockDeltaCollector;
import org.eclipse.emf.cdo.internal.server.LockingManager.LockStateCollector;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.NewIDSupport;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.StubCDORevision;
import org.eclipse.emf.cdo.spi.server.ICommitConflictResolver;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalTransaction.CommitAttempt;
import org.eclipse.emf.cdo.spi.server.InternalUnitManager;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class TransactionCommitContext implements InternalCommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, TransactionCommitContext.class);

  private static final InternalCDORevision DETACHED = new StubCDORevision(null);

  private static final LockType[] ALL_LOCK_TYPES = LockType.values();

  private static final Map<String, String> NO_COMMIT_PROPERTIES = Collections.emptyMap();

  private final InternalTransaction transaction;

  private final CDOBranch branch;

  private InternalRepository repository;

  private InternalCDOPackageRegistry repositoryPackageRegistry;

  private boolean packageRegistryLocked;

  private TransactionPackageRegistry packageRegistry;

  private IStoreAccessor accessor;

  private long lastUpdateTime;

  private long lastTreeRestructuringCommit;

  private Boolean treeRestructuring;

  private long timeStamp = CDORevision.UNSPECIFIED_DATE;

  private long previousTimeStamp = CDORevision.UNSPECIFIED_DATE;

  private int commitNumber;

  private String commitComment;

  private Map<String, String> commitProperties;

  private CDOBranchPoint commitMergeSource;

  private boolean usingEcore;

  private boolean usingEtypes;

  private InternalCDOPackageUnit[] newPackageUnits = new InternalCDOPackageUnit[0];

  private InternalCDORevision[] newObjects = new InternalCDORevision[0];

  private InternalCDORevisionDelta[] dirtyObjectDeltas = new InternalCDORevisionDelta[0];

  private CDOID[] detachedObjects = new CDOID[0];

  private Map<CDOID, EClass> detachedObjectTypes;

  private CDOBranchVersion[] detachedObjectVersions;

  private InternalCDORevision[] dirtyObjects = new InternalCDORevision[0];

  private InternalCDORevision[] cachedDetachedRevisions = new InternalCDORevision[0];

  private Map<CDOID, InternalCDORevision> cachedRevisions;

  private Map<CDOID, InternalCDORevision> oldRevisions = CDOIDUtil.createMap();

  private Map<CDOID, InternalCDORevision> newRevisions;

  private CommitData originalCommmitData;

  private Map<CDOID, CDOID> idMappings = CDOIDUtil.createMap();

  private CDOReferenceAdjuster idMapper = new CDOIDMapper(idMappings);

  private byte rollbackReason = CDOProtocolConstants.ROLLBACK_REASON_UNKNOWN;

  private String rollbackMessage;

  private List<CDOIDReference> xRefs;

  private boolean hasChanges;

  private boolean serializingCommits;

  private boolean ensuringReferentialIntegrity;

  private ExtendedDataInputStream lobs;

  private long optimisticLockingTimeout;

  private InternalLockManager lockManager;

  /**
   * The keys of objects that are locked in lockObjects().
   */
  private Set<Object> lockedObjects = new HashSet<>();

  /**
   * The IDs of referenced objects that are locked in lockObjects(), only relevant if ensuringReferentialIntegrity.
   */
  private List<CDOID> lockedTargets;

  /**
   * The lock states on new objects that the client wants to keep after the commit.
   */
  private CDOLockState[] locksOnNewObjects = CDOLockUtil.NO_LOCK_STATES;

  /**
   * The IDs of objects that the client wants to release after the commit, as per autoReleaseLocks.
   */
  private CDOID[] idsToUnlock = new CDOID[0];

  /**
   * The lock deltas that are visible after the commit.
   */
  private final LockDeltaCollector lockDeltas = new LockDeltaCollector();

  /**
   * The lock states that are visible after the commit.
   */
  private final LockStateCollector lockStates = new LockStateCollector();

  /**
   * The lock state changes that are visible after the commit.
   */
  private CDOLockChangeInfo lockChangeInfo;

  private Map<Object, Object> data;

  private CommitNotificationInfo commitNotificationInfo = new CommitNotificationInfo();

  private boolean allowModify;

  public TransactionCommitContext(InternalTransaction transaction)
  {
    this.transaction = transaction;

    branch = transaction.getBranch();
    repository = transaction.getRepository();
    lockManager = repository.getLockingManager();
    serializingCommits = repository.isSerializingCommits();
    ensuringReferentialIntegrity = repository.isEnsuringReferentialIntegrity();

    repositoryPackageRegistry = repository.getPackageRegistry(false);
  }

  @Override
  public InternalTransaction getTransaction()
  {
    return transaction;
  }

  @Override
  public CDOBranchPoint getBranchPoint()
  {
    return branch.getPoint(timeStamp);
  }

  @Override
  public String getUserID()
  {
    return transaction.getSession().getUserID();
  }

  public int getCommitNumber()
  {
    return commitNumber;
  }

  @Override
  public String getCommitComment()
  {
    return commitComment;
  }

  @Override
  public Map<String, String> getCommitProperties()
  {
    return commitProperties == null ? NO_COMMIT_PROPERTIES : commitProperties;
  }

  @Override
  public CDOBranchPoint getCommitMergeSource()
  {
    return commitMergeSource;
  }

  @Override
  public long getLastUpdateTime()
  {
    return lastUpdateTime;
  }

  @Override
  public byte getRollbackReason()
  {
    return rollbackReason;
  }

  @Override
  public String getRollbackMessage()
  {
    return rollbackMessage;
  }

  @Override
  public List<CDOIDReference> getXRefs()
  {
    return xRefs;
  }

  @Override
  public InternalCDOPackageRegistry getPackageRegistry()
  {
    if (packageRegistry == null)
    {
      packageRegistry = new TransactionPackageRegistry(repositoryPackageRegistry);
      packageRegistry.activate();
    }

    return packageRegistry;
  }

  @Override
  public boolean isClearResourcePathCache()
  {
    return commitNotificationInfo.isClearResourcePathCache();
  }

  @Override
  public byte getSecurityImpact()
  {
    return commitNotificationInfo.getSecurityImpact();
  }

  @Override
  public boolean isUsingEcore()
  {
    return usingEcore;
  }

  @Override
  public boolean isUsingEtypes()
  {
    return usingEtypes;
  }

  @Override
  public InternalCDOPackageUnit[] getNewPackageUnits()
  {
    return newPackageUnits;
  }

  @Override
  public InternalCDORevision[] getNewObjects()
  {
    return newObjects;
  }

  @Override
  public InternalCDORevision[] getDirtyObjects()
  {
    return dirtyObjects;
  }

  @Override
  public CDOID[] getDetachedObjects()
  {
    return detachedObjects;
  }

  @Override
  public Map<CDOID, EClass> getDetachedObjectTypes()
  {
    return detachedObjectTypes;
  }

  @Override
  public CDOBranchVersion[] getDetachedObjectVersions()
  {
    return detachedObjectVersions;
  }

  @Override
  public InternalCDORevision[] getDetachedRevisions()
  {
    return getDetachedRevisions(true);
  }

  @Override
  public InternalCDORevision[] getDetachedRevisions(boolean check)
  {
    if (check)
    {
      // This array can contain null values as they only come from the cache!
      for (InternalCDORevision cachedDetachedRevision : cachedDetachedRevisions)
      {
        if (cachedDetachedRevision == null)
        {
          throw new AssertionError("Detached revisions are incomplete");
        }
      }
    }

    return cachedDetachedRevisions;
  }

  @Override
  public InternalCDORevisionDelta[] getDirtyObjectDeltas()
  {
    return dirtyObjectDeltas;
  }

  @Override
  public Map<CDOID, InternalCDORevision> getOldRevisions()
  {
    return oldRevisions;
  }

  @Override
  public Map<CDOID, InternalCDORevision> getNewRevisions()
  {
    if (newRevisions == null)
    {
      newRevisions = CDOIDUtil.createMap();

      for (int i = 0; i < newObjects.length; i++)
      {
        InternalCDORevision revision = newObjects[i];
        newRevisions.put(revision.getID(), revision);
      }
    }

    return newRevisions;
  }

  @Override
  public CommitData getOriginalCommmitData()
  {
    return originalCommmitData;
  }

  @Override
  public InternalCDORevision getRevision(CDOID id)
  {
    if (cachedRevisions == null)
    {
      cachedRevisions = cacheRevisions();
    }

    // Try "after state"
    InternalCDORevision revision = cachedRevisions.get(id);
    if (revision == DETACHED)
    {
      return null;
    }

    if (revision != null)
    {
      return revision;
    }

    // Fall back to "before state"
    return (InternalCDORevision)transaction.getRevision(id);
  }

  protected Map<CDOID, InternalCDORevision> cacheRevisions()
  {
    Map<CDOID, InternalCDORevision> cache = CDOIDUtil.createMap();
    if (newObjects != null)
    {
      for (int i = 0; i < newObjects.length; i++)
      {
        InternalCDORevision revision = newObjects[i];
        cache.put(revision.getID(), revision);
      }
    }

    if (dirtyObjects != null)
    {
      for (int i = 0; i < dirtyObjects.length; i++)
      {
        InternalCDORevision revision = dirtyObjects[i];
        cache.put(revision.getID(), revision);
      }
    }

    if (detachedObjects != null)
    {
      for (int i = 0; i < detachedObjects.length; i++)
      {
        cache.put(detachedObjects[i], DETACHED);
      }
    }

    return cache;
  }

  @Override
  public Map<CDOID, CDOID> getIDMappings()
  {
    return Collections.unmodifiableMap(idMappings);
  }

  @Override
  public void addIDMapping(CDOID oldID, CDOID newID)
  {
    if (CDOIDUtil.isNull(newID) || newID.isTemporary())
    {
      throw new IllegalStateException("newID=" + newID); //$NON-NLS-1$
    }

    CDOID previousMapping = idMappings.put(oldID, newID);
    if (previousMapping != null && previousMapping != newID)
    {
      throw new IllegalStateException("previousMapping != null && previousMapping != newID"); //$NON-NLS-1$
    }
  }

  @Override
  public void applyIDMappings(OMMonitor monitor)
  {
    boolean mapIDs = !idMappings.isEmpty();
    monitor.begin(1 + (mapIDs ? newObjects.length + dirtyObjects.length + dirtyObjectDeltas.length : 0));

    try
    {
      if (mapIDs)
      {
        applyIDMappings(newObjects, monitor.fork(newObjects.length));
        applyIDMappings(dirtyObjects, monitor.fork(dirtyObjects.length));
        for (CDORevisionDelta dirtyObjectDelta : dirtyObjectDeltas)
        {
          ((InternalCDORevisionDelta)dirtyObjectDelta).adjustReferences(idMapper);
          monitor.worked();
        }
      }

      // Do not notify handlers before the IDs are fully mapped!
      notifyBeforeCommitting(monitor);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void applyIDMappings(InternalCDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (InternalCDORevision revision : revisions)
      {
        if (revision != null)
        {
          CDOID newID = idMappings.get(revision.getID());
          if (newID != null)
          {
            revision.setID(newID);
          }

          revision.adjustReferences(idMapper);
          monitor.worked();
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void notifyBeforeCommitting(OMMonitor monitor)
  {
    try
    {
      allowModify = true;
      repository.notifyWriteAccessHandlers(transaction, this, true, monitor.fork());
    }
    finally
    {
      allowModify = false;
    }
  }

  @Override
  public void preWrite()
  {
    // Allocate a store writer
    accessor = repository.getStore().getWriter(transaction);

    // Make the store writer available in a ThreadLocal variable
    StoreThreadLocal.setAccessor(accessor);
    StoreThreadLocal.setCommitContext(this);
  }

  @Override
  public boolean isTreeRestructuring()
  {
    if (treeRestructuring == null)
    {
      treeRestructuring = CDORevisionUtil.isTreeRestructuring(dirtyObjectDeltas);
    }

    return treeRestructuring;
  }

  @Override
  public void setLastTreeRestructuringCommit(long lastTreeRestructuringCommit)
  {
    this.lastTreeRestructuringCommit = lastTreeRestructuringCommit;
  }

  @Override
  public void setClearResourcePathCache(boolean clearResourcePathCache)
  {
    commitNotificationInfo.setClearResourcePathCache(clearResourcePathCache);
  }

  @Override
  public void setSecurityImpact(byte securityImpact, Set<? extends Object> impactedRules)
  {
    commitNotificationInfo.setSecurityImpact(securityImpact);
    commitNotificationInfo.setImpactedRules(impactedRules);
  }

  @Override
  public void setUsingEcore(boolean usingEcore)
  {
    this.usingEcore = usingEcore;
  }

  @Override
  public void setUsingEtypes(boolean usingEtypes)
  {
    this.usingEtypes = usingEtypes;
  }

  @Override
  public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits)
  {
    this.newPackageUnits = newPackageUnits;
  }

  @Override
  public void setNewObjects(InternalCDORevision[] newObjects)
  {
    this.newObjects = newObjects;
  }

  @Override
  public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas)
  {
    this.dirtyObjectDeltas = dirtyObjectDeltas;
  }

  @Override
  public void setDetachedObjects(CDOID[] detachedObjects)
  {
    this.detachedObjects = detachedObjects;
  }

  @Override
  public void setDetachedObjectTypes(Map<CDOID, EClass> detachedObjectTypes)
  {
    this.detachedObjectTypes = detachedObjectTypes;
  }

  @Override
  public void setDetachedObjectVersions(CDOBranchVersion[] detachedObjectVersions)
  {
    this.detachedObjectVersions = detachedObjectVersions;
  }

  @Override
  public void setLastUpdateTime(long lastUpdateTime)
  {
    this.lastUpdateTime = lastUpdateTime;
  }

  @Override
  public void setOptimisticLockingTimeout(long optimisticLockingTimeout)
  {
    this.optimisticLockingTimeout = optimisticLockingTimeout;
  }

  @Override
  public void setCommitNumber(int commitNumber)
  {
    this.commitNumber = commitNumber;
  }

  @Override
  public void setCommitComment(String commitComment)
  {
    this.commitComment = commitComment;
  }

  @Override
  public void setCommitProperties(Map<String, String> commitProperties)
  {
    this.commitProperties = commitProperties;
  }

  @Override
  public void setCommitMergeSource(CDOBranchPoint commitMergeSource)
  {
    this.commitMergeSource = commitMergeSource;
  }

  @Override
  public ExtendedDataInputStream getLobs()
  {
    return lobs;
  }

  @Override
  public void setLobs(ExtendedDataInputStream in)
  {
    lobs = in;
  }

  @Override
  public CDOLockState[] getLocksOnNewObjects()
  {
    return locksOnNewObjects;
  }

  @Override
  public void setLocksOnNewObjects(CDOLockState[] locksOnNewObjects)
  {
    this.locksOnNewObjects = locksOnNewObjects;
  }

  @Override
  public CDOID[] getIDsToUnlock()
  {
    return idsToUnlock;
  }

  @Override
  public void setIDsToUnlock(CDOID[] idsToUnlock)
  {
    this.idsToUnlock = idsToUnlock;
  }

  @Override
  public <T> T getData(Object key)
  {
    if (data == null)
    {
      return null;
    }

    @SuppressWarnings("unchecked")
    T result = (T)data.get(key);
    return result;
  }

  @Override
  public synchronized <T extends Object> T setData(Object key, T value)
  {
    if (data == null)
    {
      data = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    T old = (T)data.put(key, value);
    return old;
  }

  protected InternalCDOPackageUnit[] lockPackageRegistry(InternalCDOPackageUnit[] packageUnits) throws InterruptedException
  {
    if (!packageRegistryLocked)
    {
      repository.getPackageRegistryCommitLock().acquire();
      packageRegistryLocked = true;
    }

    List<InternalCDOPackageUnit> noDuplicates = new ArrayList<>();
    for (InternalCDOPackageUnit packageUnit : packageUnits)
    {
      String id = packageUnit.getID();
      if (!repositoryPackageRegistry.containsKey(id))
      {
        noDuplicates.add(packageUnit);
      }
    }

    int newSize = noDuplicates.size();
    if (packageUnits.length != newSize)
    {
      return noDuplicates.toArray(new InternalCDOPackageUnit[newSize]);
    }

    return packageUnits;
  }

  /**
   * @since 2.0
   */
  @Override
  public void write(OMMonitor monitor)
  {
    try
    {
      monitor.begin(106);

      hasChanges = newPackageUnits.length != 0 || newObjects.length != 0 || dirtyObjectDeltas.length != 0;
      if (!hasChanges)
      {
        return;
      }

      dirtyObjects = new InternalCDORevision[dirtyObjectDeltas.length];

      if (newPackageUnits.length != 0)
      {
        newPackageUnits = lockPackageRegistry(newPackageUnits);
      }

      lockObjects(); // Can take long and must come before setTimeStamp()
      monitor.worked();

      setTimeStamp(monitor.fork());

      adjustForCommit();
      monitor.worked();

      computeDirtyObjects(monitor.fork());

      checkContainmentCycles();
      checkXRefs();
      checkUnitMoves();
      monitor.worked();

      detachObjects(monitor.fork());
      writeAccessor(monitor.fork(100));
    }
    catch (RollbackException ex)
    {
      rollbackReason = ex.getRollbackReason();
      rollback(ex.getRollbackMessage());
    }
    catch (Throwable t)
    {
      handleException(t);
    }
    finally
    {
      finishMonitor(monitor);
    }
  }

  @Override
  public void commit(OMMonitor monitor)
  {
    try
    {
      monitor.begin(101);
      if (hasChanges)
      {
        accessor.commit(monitor.fork(100));
      }
      else
      {
        monitor.worked(100);
      }

      updateInfraStructure(monitor.fork());

      if (hasChanges)
      {
        // Bug 297940
        repository.endCommit(timeStamp);
      }
    }
    catch (Throwable ex)
    {
      handleException(ex);
    }
    finally
    {
      finishMonitor(monitor);
    }
  }

  protected void handleException(Throwable throwable)
  {
    try
    {
      if (throwable instanceof IRepository.WriteAccessHandler.TransactionValidationException)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(throwable);
        }

        rollbackReason = CDOProtocolConstants.ROLLBACK_REASON_VALIDATION_ERROR;
        rollback(throwable.getLocalizedMessage());
      }
      else
      {
        OM.LOG.error(throwable);

        String storeClass = repository.getStore().getClass().getSimpleName();
        rollback("Rollback in " + storeClass + ": " + StringUtil.formatException(throwable)); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
    catch (Exception ex)
    {
      if (rollbackMessage == null)
      {
        rollbackMessage = ex.getMessage();
      }

      try
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
      catch (Exception ignore)
      {
      }
    }
  }

  protected void finishMonitor(OMMonitor monitor)
  {
    try
    {
      monitor.done();
    }
    catch (Exception ex)
    {
      try
      {
        OM.LOG.warn(ex);
      }
      catch (Exception ignore)
      {
      }
    }
  }

  protected void setTimeStamp(OMMonitor monitor)
  {
    long[] times = createTimeStamp(monitor); // Could throw an exception
    timeStamp = times[0];
    previousTimeStamp = times[1];
    CheckUtil.checkState(timeStamp != CDOBranchPoint.UNSPECIFIED_DATE, "Commit timestamp must not be 0"); //$NON-NLS-1$

    transaction.setLastCommitAttempt(new CommitAttempt(commitNumber, timeStamp, previousTimeStamp));
  }

  protected long[] createTimeStamp(OMMonitor monitor)
  {
    return repository.createCommitTimeStamp(monitor);
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public long getPreviousTimeStamp()
  {
    return previousTimeStamp;
  }

  @Override
  public List<CDOLockDelta> getLockDeltas()
  {
    return lockDeltas;
  }

  @Override
  public List<CDOLockState> getLockStates()
  {
    return lockStates;
  }

  @Override
  public void postCommit(boolean success)
  {
    try
    {
      if (packageRegistryLocked)
      {
        repository.getPackageRegistryCommitLock().release();
      }
    }
    catch (Throwable ex)
    {
      OM.LOG.warn("A problem occured while releasing the package registry commit lock", ex);
    }

    try
    {
      // Send notifications (in particular FailureCommitInfos) only if timeStamp had been allocated
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE || lockChangeInfo != null)
      {
        sendCommitNotifications(success);
      }
    }
    catch (Throwable ex)
    {
      OM.LOG.warn("A problem occured while notifying other sessions", ex);
    }
    finally
    {
      StoreThreadLocal.release();
      accessor = null;
      lockedTargets = null;

      if (packageRegistry != null)
      {
        packageRegistry.deactivate();
        packageRegistry = null;
      }
    }
  }

  protected void sendCommitNotifications(boolean success)
  {
    commitNotificationInfo.setSender(transaction.getSession());
    commitNotificationInfo.setModifiedByServer(originalCommmitData != null);
    commitNotificationInfo.setRevisionProvider(this);
    commitNotificationInfo.setLockChangeInfo(lockChangeInfo);

    if (success)
    {
      commitNotificationInfo.setCommitInfo(createCommitInfo());
    }
    else
    {
      commitNotificationInfo.setCommitInfo(createFailureCommitInfo());
    }

    repository.sendCommitNotification(commitNotificationInfo);
  }

  @Override
  public CDOCommitInfo createCommitInfo()
  {
    String userID = transaction.getSession().getUserID();
    CDOCommitData commitData = createCommitData();

    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, commitComment, commitMergeSource, commitData);
  }

  public CDOCommitInfo createFailureCommitInfo()
  {
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    return new FailureCommitInfo(commitInfoManager, timeStamp, previousTimeStamp);
  }

  protected CDOCommitData createCommitData()
  {
    List<CDOPackageUnit> newPackageUnitsCollection = new IndexedList.ArrayBacked<CDOPackageUnit>()
    {
      @Override
      protected CDOPackageUnit[] getArray()
      {
        return newPackageUnits;
      }
    };

    List<CDOIDAndVersion> newObjectsCollection = new IndexedList.ArrayBacked<CDOIDAndVersion>()
    {
      @Override
      protected CDOIDAndVersion[] getArray()
      {
        return newObjects;
      }
    };

    List<CDORevisionKey> changedObjectsCollection = new IndexedList.ArrayBacked<CDORevisionKey>()
    {
      @Override
      protected CDORevisionKey[] getArray()
      {
        return dirtyObjectDeltas;
      }
    };

    List<CDOIDAndVersion> detachedObjectsCollection = new IndexedList<CDOIDAndVersion>()
    {
      @Override
      public CDOIDAndVersion get(int i)
      {
        if (cachedDetachedRevisions[i] != null)
        {
          return cachedDetachedRevisions[i];
        }

        return CDOIDUtil.createIDAndVersion(detachedObjects[i], CDORevision.UNSPECIFIED_VERSION);
      }

      @Override
      public int size()
      {
        return detachedObjects.length;
      }
    };

    return CDOCommitInfoUtil.createCommitData(newPackageUnitsCollection, newObjectsCollection, changedObjectsCollection, detachedObjectsCollection);
  }

  protected void adjustForCommit()
  {
    for (InternalCDOPackageUnit newPackageUnit : newPackageUnits)
    {
      newPackageUnit.setTimeStamp(timeStamp);
    }

    for (InternalCDORevision newObject : newObjects)
    {
      newObject.adjustForCommit(branch, timeStamp);
    }
  }

  protected void lockObjects() throws InterruptedException
  {
    lockedObjects.clear();
    lockedTargets = null;

    try
    {
      CDOFeatureDeltaVisitor deltaTargetLocker = null;
      if (ensuringReferentialIntegrity && !serializingCommits)
      {
        final Set<CDOID> newIDs = new HashSet<>();
        for (int i = 0; i < newObjects.length; i++)
        {
          InternalCDORevision newRevision = newObjects[i];
          CDOID newID = newRevision.getID();
          if (newID instanceof CDOIDObject)
          {
            // After merges newObjects may contain non-TEMP ids
            newIDs.add(newID);
          }
        }

        final boolean supportingBranches = repository.isSupportingBranches();
        deltaTargetLocker = new CDOFeatureDeltaVisitorImpl()
        {
          @Override
          public void visit(CDOAddFeatureDelta delta)
          {
            lockTarget(delta.getValue(), newIDs, supportingBranches);
          }

          @Override
          public void visit(CDOSetFeatureDelta delta)
          {
            lockTarget(delta.getValue(), newIDs, supportingBranches);
          }
        };

        CDOReferenceAdjuster revisionTargetLocker = new CDOReferenceAdjuster()
        {
          @Override
          public Object adjustReference(Object value, EStructuralFeature feature, int index)
          {
            lockTarget(value, newIDs, supportingBranches);
            return value;
          }
        };

        for (int i = 0; i < newObjects.length; i++)
        {
          InternalCDORevision newRevision = newObjects[i];
          newRevision.adjustReferences(revisionTargetLocker);
        }
      }

      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        InternalCDORevisionDelta delta = dirtyObjectDeltas[i];
        CDOID id = delta.getID();
        Object key = lockManager.getLockKey(id, branch);
        lockedObjects.add(key);
      }

      if (deltaTargetLocker != null)
      {
        for (int i = 0; i < dirtyObjectDeltas.length; i++)
        {
          InternalCDORevisionDelta delta = dirtyObjectDeltas[i];
          delta.accept(deltaTargetLocker);
        }
      }

      for (int i = 0; i < detachedObjects.length; i++)
      {
        CDOID id = detachedObjects[i];
        Object key = lockManager.getLockKey(id, branch);
        lockedObjects.add(key);
      }

      if (!lockedObjects.isEmpty())
      {
        try
        {
          long timeout = optimisticLockingTimeout == CDOProtocolConstants.DEFAULT_OPTIMISTIC_LOCKING_TIMEOUT //
              ? repository.getOptimisticLockingTimeout() //
              : optimisticLockingTimeout;

          // First lock all objects (incl. possible ref targets).
          // This is a transient operation, it does not check for existence of the ref targets!
          lockManager.lock(transaction, lockedObjects, LockType.WRITE, 1, timeout, false, false, null, null);
        }
        catch (Exception ex)
        {
          throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_OPTIMISTIC_LOCKING, ex);
        }

        // If all locks could be acquired, check if locked targets do still exist
        if (lockedTargets != null)
        {
          for (CDOID id : lockedTargets)
          {
            CDORevision revision = transaction.getRevision(id);
            if (revision == null || revision instanceof DetachedCDORevision)
            {
              throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_REFERENTIAL_INTEGRITY,
                  "Attempt by " + transaction + " to introduce a stale reference");
            }
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      lockedObjects.clear();
      lockedTargets = null;
      throw ex;
    }
  }

  protected void lockTarget(Object value, Set<CDOID> newIDs, boolean supportingBranches)
  {
    if (value instanceof CDOIDObject)
    {
      CDOIDObject id = (CDOIDObject)value;
      if (id.isNull())
      {
        return;
      }

      if (newIDs.contains(id))
      {
        // After merges newObjects may contain non-TEMP ids
        return;
      }

      if (detachedObjectTypes != null && detachedObjectTypes.containsKey(id))
      {
        throw new IllegalStateException("This commit deletes object " + id + " and adds a reference at the same time");
      }

      // Let this object be locked
      Object key = lockManager.getLockKey(id, branch);
      lockedObjects.add(key);

      // Let this object be checked for existence after it has been locked
      if (lockedTargets == null)
      {
        lockedTargets = new ArrayList<>();
      }

      lockedTargets.add(id);
    }
  }

  protected void computeDirtyObjects(OMMonitor monitor)
  {
    try
    {
      monitor.begin(dirtyObjectDeltas.length);
      List<InternalCDORevisionDelta> conflicts = new ArrayList<>();

      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        InternalCDORevision newRevision = computeDirtyObject(dirtyObjectDeltas[i]);
        if (newRevision == null)
        {
          conflicts.add(dirtyObjectDeltas[i]);
        }
        else if (!newRevision.isWritable())
        {
          throw new NoPermissionException(newRevision, true);
        }

        dirtyObjects[i] = newRevision;
        monitor.worked();
      }

      if (!conflicts.isEmpty())
      {
        dirtyObjects = new InternalCDORevision[dirtyObjectDeltas.length];
        mergeConflicts(conflicts);
      }

      if (!conflicts.isEmpty())
      {
        throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_COMMIT_CONFLICT,
            "Attempt by " + transaction + " to modify historical revisions: " + conflicts);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected InternalCDORevision computeDirtyObject(InternalCDORevisionDelta delta)
  {
    try
    {
      CDOID id = delta.getID();

      InternalCDORevision oldRevision = (InternalCDORevision)transaction.getRevision(id);
      if (oldRevision == null)
      {
        throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_UNKNOWN, "Revision " + id + " not found by " + transaction);
      }

      repository.ensureChunks(oldRevision, CDORevision.UNCHUNKED);
      oldRevisions.put(id, oldRevision);

      if (oldRevision.getBranch() != delta.getBranch() || oldRevision.getVersion() != delta.getVersion())
      {
        // Commit conflict!
        return null;
      }

      InternalCDORevision newRevision = oldRevision.copy();
      newRevision.adjustForCommit(branch, timeStamp);

      delta.applyTo(newRevision);
      return newRevision;
    }
    catch (RollbackException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);

      String rollbackMessage = ex.getMessage();
      if (rollbackMessage == null)
      {
        rollbackMessage = ex.getClass().getName();
      }

      throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_UNKNOWN, rollbackMessage);
    }
  }

  @Override
  public void modify(Consumer<ModificationContext> modifier)
  {
    if (!allowModify)
    {
      throw new UnsupportedOperationException("Commit modification not allowed");
    }

    List<CDOIDAndVersion> _newObjects = new ArrayList<>();
    if (newObjects != null)
    {
      for (InternalCDORevision revision : newObjects)
      {
        InternalCDORevision copy = revision.copy();
        if (copy instanceof BaseCDORevision)
        {
          ((BaseCDORevision)copy).unfreeze();
        }

        _newObjects.add(copy);
      }
    }

    List<CDORevisionKey> _changedObjects = new ArrayList<>();
    if (dirtyObjectDeltas != null)
    {
      for (InternalCDORevisionDelta revisionDelta : dirtyObjectDeltas)
      {
        _changedObjects.add(revisionDelta.copy());
      }
    }

    List<CDOIDAndVersion> _detachedObjects = new ArrayList<>();
    if (detachedObjects != null)
    {
      for (int i = 0; i < detachedObjects.length; i++)
      {
        CDOID id = detachedObjects[i];
        CDOBranchVersion branchVersion = detachedObjectVersions[i];
        _detachedObjects.add(CDOIDUtil.createIDAndVersion(id, branchVersion.getVersion()));
      }
    }

    CDOChangeSetData changeSetData = CDORevisionUtil.createChangeSetData(_newObjects, _changedObjects, _detachedObjects);
    AtomicBoolean canceled = new AtomicBoolean();

    ModificationContext modificationContext = new ModificationContext()
    {
      @Override
      public CDORevision attachNewObject(CDOID containerID, EReference containingReference, EClass eClass)
      {
        Function<CDORevision, CDOID> idGenerator = repository.getIDGenerator();
        if (idGenerator == null)
        {
          idGenerator = this::getNewID;
        }

        CDOChangeSetData changeSetData = getChangeSetData();
        List<CDOIDAndVersion> newObjects = new ArrayList<>();
        List<CDORevisionKey> changedObjects = new ArrayList<>();

        CDORevisionFactory revisionFactory = repository.getRevisionManager().getFactory();
        InternalCDORevision newObject = (InternalCDORevision)revisionFactory.createRevision(eClass);
        newObject.setBranchPoint(getBranchPoint());
        newObject.setVersion(CDOBranchVersion.FIRST_VERSION);
        newObject.setContainerID(containerID);

        CDOID newObjectID = idGenerator.apply(newObject); // Must come after newObject.setBranchPoint().
        if (CDOIDUtil.isNull(newObjectID))
        {
          throw new IllegalStateException("New id is null for " + newObject);
        }

        newObject.setID(newObjectID);
        newObjects.add(newObject);

        InternalCDORevisionDelta revisionDelta = null;

        // Is the container already in newObjects?
        InternalCDORevision container = (InternalCDORevision)changeSetData.getNewObject(containerID);
        if (container == null)
        {
          // No, the container is an existing object.
          container = Objects.requireNonNull(getRevision(containerID));

          // Is the existing container already in changedObjects?
          revisionDelta = (InternalCDORevisionDelta)changeSetData.getChangedObject(containerID);
          if (revisionDelta == null)
          {
            // No, the existing container is not yet changed in the current commit.
            revisionDelta = (InternalCDORevisionDelta)CDORevisionUtil.createDelta(container);
            changedObjects.add(revisionDelta);
          }
        }

        newObject.setContainingReference(containingReference);

        if (containingReference.isMany())
        {
          CDOList list = container.getOrCreateList(containingReference);
          list.add(newObjectID);

          if (revisionDelta != null)
          {
            CDOListFeatureDelta featureDelta = (CDOListFeatureDelta)revisionDelta.getFeatureDelta(containingReference);
            List<CDOFeatureDelta> listChanges = featureDelta.getListChanges();
            listChanges.add(new CDOAddFeatureDeltaImpl(containingReference, list.size() - 1, newObjectID));
          }
        }
        else
        {
          container.setValue(containingReference, newObjectID);

          if (revisionDelta != null)
          {
            revisionDelta.addFeatureDelta(new CDOSetFeatureDeltaImpl(containingReference, 0, newObjectID), null);
          }
        }

        cachedRevisions.put(containerID, container);
        cachedRevisions.put(newObjectID, newObject);

        CDOChangeSetData additionalChanges = CDORevisionUtil.createChangeSetData(newObjects, changedObjects, null);
        changeSetData.merge(additionalChanges);

        return newObject;
      }

      @Override
      public CDOChangeSetData getChangeSetData()
      {
        return changeSetData;
      }

      @Override
      public void cancelModification()
      {
        canceled.set(true);
      }

      private CDOID getNewID(CDORevision revision)
      {
        if (repository.getIDGenerationLocation() == IDGenerationLocation.CLIENT)
        {
          // The calling modifier is responsible for creating an adequate client-side ID.
          return null;
        }

        if (accessor instanceof NewIDSupport)
        {
          return ((NewIDSupport)accessor).getNewID(revision);
        }

        // The calling modifier is responsible for creating an adequate server-side ID.
        return null;
      }
    };

    modifier.accept(modificationContext);

    if (!canceled.get())
    {
      overrideCommitData(changeSetData, true);
    }
  }

  /**
   * When this method is called, the oldRevisions map is filled with the latest valid revisions, chunks ensured.
   */
  protected void mergeConflicts(List<InternalCDORevisionDelta> conflicts)
  {
    ICommitConflictResolver commitConflictResolver = repository.getCommitConflictResolver();
    if (commitConflictResolver != null)
    {
      CDOChangeSetData result = commitConflictResolver.resolveConflicts(this, conflicts);
      if (result != null)
      {
        overrideCommitData(result, false);
      }
    }
  }

  protected void overrideCommitData(CDOChangeSetData newChangeSetData, boolean force)
  {
    if (originalCommmitData == null)
    {
      originalCommmitData = new CommitData(newObjects, dirtyObjectDeltas, detachedObjects);
    }

    // Apply new objects.
    {
      List<InternalCDORevision> newObjectsList = new ArrayList<>();

      List<CDOIDAndVersion> idAndVersions = newChangeSetData.getNewObjects();
      if (idAndVersions != null)
      {
        for (CDOIDAndVersion idAndVersion : idAndVersions)
        {
          if (idAndVersion instanceof InternalCDORevision)
          {
            InternalCDORevision newObject = (InternalCDORevision)idAndVersion;
            if (force || newObject.getID().isTemporary())
            {
              newObjectsList.add(newObject);
            }
          }
        }
      }

      newObjects = newObjectsList.toArray(new InternalCDORevision[newObjectsList.size()]);
    }

    // Apply detached objects.
    {
      List<CDOID> detachedObjectsList = new ArrayList<>();
      List<CDOBranchVersion> detachedObjectVersionsList = new ArrayList<>();
      detachedObjectTypes = new HashMap<>();

      List<CDOIDAndVersion> idAndVersions = newChangeSetData.getDetachedObjects();
      if (idAndVersions != null)
      {
        for (CDOIDAndVersion idAndVersion : idAndVersions)
        {
          CDOID id = idAndVersion.getID();

          InternalCDORevision oldObject = (InternalCDORevision)transaction.getRevision(id);
          if (oldObject != null)
          {
            detachedObjectsList.add(id);
            detachedObjectVersionsList.add(oldObject.getBranch().getVersion(oldObject.getVersion()));
            detachedObjectTypes.put(id, oldObject.getEClass());
          }
        }
      }

      detachedObjects = detachedObjectsList.toArray(new CDOID[detachedObjectsList.size()]);
      detachedObjectVersions = detachedObjectVersionsList.toArray(new CDOBranchVersion[detachedObjectVersionsList.size()]);
    }

    // Apply changed objects.
    {
      List<InternalCDORevisionDelta> dirtyObjectDeltasList = new ArrayList<>();
      List<InternalCDORevision> dirtyObjectsList = new ArrayList<>();
      InternalCDORevisionManager revisionManager = repository.getRevisionManager();

      List<CDORevisionKey> revisionKeys = newChangeSetData.getChangedObjects();
      if (revisionKeys != null)
      {
        for (CDORevisionKey revisionKey : revisionKeys)
        {
          if (revisionKey instanceof InternalCDORevisionDelta)
          {
            InternalCDORevisionDelta ancestorDelta = (InternalCDORevisionDelta)revisionKey;
            CDOID id = ancestorDelta.getID();

            InternalCDORevision ancestorRevision = revisionManager.getRevisionByVersion(id, ancestorDelta, CDORevision.UNCHUNKED, true);
            InternalCDORevision newRevision = ancestorRevision.copy();
            ancestorDelta.applyTo(newRevision);

            InternalCDORevision oldRevision = (InternalCDORevision)transaction.getRevision(id);

            newRevision.setVersion(oldRevision.getVersion()); // Needed in adjustForCommit().
            newRevision.adjustForCommit(branch, timeStamp);

            InternalCDORevisionDelta dirtyObjectDelta = newRevision.compare(oldRevision);
            dirtyObjectDeltasList.add(dirtyObjectDelta);
            dirtyObjectsList.add(newRevision);
          }
        }
      }

      dirtyObjectDeltas = dirtyObjectDeltasList.toArray(new InternalCDORevisionDelta[dirtyObjectDeltasList.size()]);
      dirtyObjects = dirtyObjectsList.toArray(new InternalCDORevision[dirtyObjectsList.size()]);
    }
  }

  protected void checkContainmentCycles()
  {
    if (lastTreeRestructuringCommit == 0)
    {
      // If this was a tree-restructuring commit then lastTreeRestructuringCommit would be initialized.
      return;
    }

    if (lastUpdateTime == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      // Happens during replication (see CommitDelegationRequest). Commits are checked in the master repo.
      return;
    }

    if (lastTreeRestructuringCommit <= lastUpdateTime)
    {
      // If this client's original state includes the state of the last tree-restructuring commit there's no danger.
      return;
    }

    Set<CDOID> objectsThatReachTheRoot = new HashSet<>();
    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      InternalCDORevisionDelta revisionDelta = dirtyObjectDeltas[i];
      CDOFeatureDelta containerDelta = revisionDelta.getFeatureDelta(CDOContainerFeatureDelta.CONTAINER_FEATURE);
      if (containerDelta != null)
      {
        InternalCDORevision revision = dirtyObjects[i];
        if (!isTheRootReachable(revision, objectsThatReachTheRoot, new HashSet<>()))
        {
          throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_CONTAINMENT_CYCLE,
              "Attempt by " + transaction + " to introduce a containment cycle");
        }
      }
    }
  }

  protected boolean isTheRootReachable(InternalCDORevision revision, Set<CDOID> objectsThatReachTheRoot, Set<CDOID> visited)
  {
    CDOID id = revision.getID();
    if (!visited.add(id))
    {
      // Cycle detected on the way up to the root.
      return false;
    }

    if (!objectsThatReachTheRoot.add(id))
    {
      // Has already been checked before.
      return true;
    }

    CDOID containerID = (CDOID)revision.getContainerID();
    if (CDOIDUtil.isNull(containerID))
    {
      // The tree root has been reached.
      return true;
    }

    // Use this commit context as CDORevisionProvider for the container revisions.
    // This is safe because Repository.commit() serializes all tree-restructuring commits.
    InternalCDORevision containerRevision = getRevision(containerID);

    // Recurse Up
    return isTheRootReachable(containerRevision, objectsThatReachTheRoot, visited);
  }

  protected void checkXRefs()
  {
    if (ensuringReferentialIntegrity && detachedObjectTypes != null)
    {
      XRefContext context = new XRefContext();
      xRefs = context.getXRefs(accessor);
      if (!xRefs.isEmpty())
      {
        throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_REFERENTIAL_INTEGRITY,
            "Attempt by " + transaction + " to introduce a stale reference");
      }
    }
  }

  protected void checkUnitMoves()
  {
    if (repository.isSupportingUnits() && isTreeRestructuring())
    {
      String checkUnitMoves = repository.getProperties().get(IRepository.Props.CHECK_UNIT_MOVES);
      if ("true".equalsIgnoreCase(checkUnitMoves))
      {
        InternalUnitManager unitManager = repository.getUnitManager();

        List<InternalCDORevisionDelta> unitMoves = unitManager.getUnitMoves(dirtyObjectDeltas, transaction, this);
        if (!unitMoves.isEmpty())
        {
          StringBuilder builder = new StringBuilder("Attempt by " + transaction + " to move objects between units: ");
          CDOIDUtil.write(builder, unitMoves);

          throw new RollbackException(CDOProtocolConstants.ROLLBACK_REASON_UNIT_INTEGRITY, builder.toString());
        }
      }
    }
  }

  @Override
  public synchronized void rollback(String message)
  {
    // Check if we already rolled back
    if (rollbackMessage == null)
    {
      rollbackMessage = message;

      if (accessor != null)
      {
        try
        {
          accessor.rollback();
        }
        catch (RuntimeException ex)
        {
          OM.LOG.warn("Problem while rolling back the transaction", ex); //$NON-NLS-1$
        }
        finally
        {
          repository.failCommit(timeStamp);
        }
      }

      releaseImplicitLocks();
    }
  }

  @Override
  public IStoreAccessor getAccessor()
  {
    return accessor;
  }

  /**
   * Called from commit().
   * Remembers the lockChangeInfo for postCommit().
   */
  protected void updateInfraStructure(OMMonitor monitor)
  {
    try
    {
      monitor.begin(9);
      addNewPackageUnits(monitor.fork());
      addRevisions(newObjects, monitor.fork());
      addRevisions(dirtyObjects, monitor.fork());
      reviseDetachedObjects(monitor.fork());

      InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
      commitInfoManager.setLastCommitOfBranch(branch, timeStamp);

      releaseImplicitLocks(); // These have no visible impact outside of this commit.
      monitor.worked();

      CDOLockOwner lockOwner = CDOLockUtil.createLockOwner(transaction);

      acquireLocksOnNewObjects(lockOwner);
      monitor.worked();

      autoReleaseExplicitLocks(lockOwner);
      monitor.worked();

      if (!lockDeltas.isEmpty())
      {
        CDOBranchPoint branchPoint = getBranchPoint();
        lockChangeInfo = CDOLockUtil.createLockChangeInfo(branchPoint, lockOwner, lockDeltas, lockStates);
      }

      repository.notifyWriteAccessHandlers(transaction, this, false, monitor.fork());
    }
    catch (Throwable t)
    {
      handleException(t);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void releaseImplicitLocks()
  {
    // Unlock objects locked during commit
    if (!lockedObjects.isEmpty())
    {
      lockManager.unlock(transaction, lockedObjects, LockType.WRITE, 1, false, false, null, null);
      lockedObjects.clear();
    }
  }

  protected void acquireLocksOnNewObjects(CDOLockOwner lockOwner) throws InterruptedException
  {
    boolean mapIDs = transaction.getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE;
    lockDeltas.setOperation(Operation.LOCK);

    for (CDOLockState lockStateOnNewObject : locksOnNewObjects)
    {
      Object target = lockStateOnNewObject.getLockedObject();

      if (mapIDs)
      {
        CDOIDAndBranch idAndBranch = target instanceof CDOIDAndBranch ? (CDOIDAndBranch)target : null;
        CDOID id = idAndBranch != null ? ((CDOIDAndBranch)target).getID() : (CDOID)target;
        CDOID newID = idMappings.get(id);
        CheckUtil.checkNull(newID, "newID");

        target = idAndBranch != null ? CDOIDUtil.createIDAndBranch(newID, idAndBranch.getBranch()) : newID;
      }

      for (LockType type : ALL_LOCK_TYPES)
      {
        if (lockStateOnNewObject.isLocked(type, lockOwner, false))
        {
          Set<Object> objects = Collections.singleton(target);
          lockManager.lock(transaction, objects, type, 1, IRWOLockManager.NO_TIMEOUT, false, true, lockDeltas, lockStates);
        }
      }
    }
  }

  protected void autoReleaseExplicitLocks(CDOLockOwner lockOwner) throws InterruptedException
  {
    List<Object> targets = new ArrayList<>();

    // Release locks that have been sent from the client.
    for (CDOID id : idsToUnlock)
    {
      Object target = lockManager.getLockKey(id, branch);
      targets.add(target);
    }

    // Release durable locks that have been acquired on detached objects.
    for (CDOID id : detachedObjects)
    {
      Object target = lockManager.getLockKey(id, branch);
      if (lockManager.hasLock(LockType.WRITE, transaction, target))
      {
        // We only need to consider detached objects that have been explicitly locked
        targets.add(target);
      }
    }

    lockDeltas.setOperation(Operation.UNLOCK);
    lockManager.unlock(transaction, targets, null, IRWOLockManager.ALL_LOCKS, false, true, lockDeltas, lockStates);
  }

  protected void addNewPackageUnits(OMMonitor monitor)
  {
    InternalCDOPackageRegistry repositoryPackageRegistry = repository.getPackageRegistry(false);
    synchronized (repositoryPackageRegistry)
    {
      try
      {
        monitor.begin(newPackageUnits.length);
        for (int i = 0; i < newPackageUnits.length; i++)
        {
          InternalCDOPackageUnit packageUnit = newPackageUnits[i];
          packageUnit.setState(CDOPackageUnit.State.LOADED);

          repositoryPackageRegistry.putPackageUnit(packageUnit);
          monitor.worked();
        }
      }
      finally
      {
        monitor.done();
      }
    }
  }

  protected void addRevisions(CDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      InternalCDORevisionManager revisionManager = repository.getRevisionManager();

      for (int i = 0; i < revisions.length; i++)
      {
        if (revisions[i] != null)
        {
          revisions[i] = revisionManager.internRevision(revisions[i]);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void reviseDetachedObjects(OMMonitor monitor)
  {
    try
    {
      monitor.begin(cachedDetachedRevisions.length);
      long revised = getBranchPoint().getTimeStamp() - 1;
      for (InternalCDORevision revision : cachedDetachedRevisions)
      {
        if (revision != null)
        {
          revision.setRevised(revised);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void detachObjects(OMMonitor monitor)
  {
    int size = detachedObjects.length;
    cachedDetachedRevisions = new InternalCDORevision[size];

    CDOID[] detachedObjects = getDetachedObjects();

    try
    {
      monitor.begin(size);
      InternalCDORevisionCache cache = repository.getRevisionManager().getCache();

      for (int i = 0; i < size; i++)
      {
        CDOID id = detachedObjects[i];

        // Remember the cached revision that must be revised after successful commit through updateInfraStructure
        cachedDetachedRevisions[i] = (InternalCDORevision)cache.getRevision(id, transaction);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeAccessor(OMMonitor monitor)
  {
    accessor.write(this, monitor);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("TransactionCommitContext[{0}, {1}, {2}]", transaction.getSession(), transaction, //$NON-NLS-1$
        CDOCommonUtil.formatTimeStamp(timeStamp));
  }

  @Override
  @Deprecated
  public boolean isAutoReleaseLocksEnabled()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void setAutoReleaseLocksEnabled(boolean on)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public List<LockState<Object, IView>> getPostCommmitLockStates()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @deprecated Does not seem to be used.
   */
  @Deprecated
  protected void setTimeStamp(long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  public static final class TransactionPackageRegistry extends CDOPackageRegistryImpl
  {
    private static final long serialVersionUID = 1L;

    public TransactionPackageRegistry(InternalCDOPackageRegistry repositoryPackageRegistry)
    {
      delegateRegistry = repositoryPackageRegistry;
      setPackageLoader(repositoryPackageRegistry.getPackageLoader());
    }

    @Override
    public synchronized void putPackageUnit(InternalCDOPackageUnit packageUnit)
    {
      LifecycleUtil.checkActive(this);
      packageUnit.setPackageRegistry(this);

      for (InternalCDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        EPackage ePackage = packageInfo.getEPackage();
        basicPut(ePackage.getNsURI(), ePackage);
      }

      resetInternalCaches();
    }

    @Override
    protected void disposePackageUnits()
    {
      // Do nothing
    }

    @Override
    public Collection<Object> values()
    {
      Collection<Object> values1 = super.values();
      Collection<Object> values2 = delegateRegistry.values();

      if (values1.isEmpty())
      {
        return values2;
      }

      if (values2.isEmpty())
      {
        return values1;
      }

      Set<Object> union = new HashSet<>();
      union.addAll(values1);
      union.addAll(values2);
      return union;
    }

    @Override
    public String toString()
    {
      return "TransactionPackageRegistry";
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class RollbackException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    private final byte rollbackReason;

    private final String rollbackMessage;

    public RollbackException(byte rollbackReason, String rollbackMessage)
    {
      this.rollbackReason = rollbackReason;
      this.rollbackMessage = rollbackMessage;
    }

    public RollbackException(byte rollbackReason, Throwable cause)
    {
      super(cause);
      this.rollbackReason = rollbackReason;
      rollbackMessage = cause.getMessage();
    }

    public byte getRollbackReason()
    {
      return rollbackReason;
    }

    public String getRollbackMessage()
    {
      return rollbackMessage;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class XRefContext implements QueryXRefsContext
  {
    private Map<EClass, List<EReference>> sourceCandidates = new HashMap<>();

    private Set<CDOID> detachedIDs = new HashSet<>();

    private Set<CDOID> dirtyIDs = new HashSet<>();

    private List<CDOIDReference> result = new ArrayList<>();

    public XRefContext()
    {
      XRefsQueryHandler.collectSourceCandidates(transaction, detachedObjectTypes.values(), sourceCandidates);

      for (CDOID id : detachedObjects)
      {
        detachedIDs.add(id);
      }

      for (InternalCDORevision revision : dirtyObjects)
      {
        dirtyIDs.add(revision.getID());
      }
    }

    public List<CDOIDReference> getXRefs(IStoreAccessor accessor)
    {
      accessor.queryXRefs(this);
      checkDirtyObjects();
      return result;
    }

    private void checkDirtyObjects()
    {
      final CDOID[] dirtyID = { null };
      CDOReferenceAdjuster dirtyObjectChecker = new CDOReferenceAdjuster()
      {
        @Override
        public Object adjustReference(Object targetID, EStructuralFeature feature, int index)
        {
          if (!(feature instanceof EReference && ((EReference)feature).isContainer()))
          {
            if (detachedIDs.contains(targetID))
            {
              result.add(new CDOIDReference((CDOID)targetID, dirtyID[0], feature, index));
            }
          }

          return targetID;
        }
      };

      for (InternalCDORevision dirtyObject : dirtyObjects)
      {
        dirtyID[0] = dirtyObject.getID();
        dirtyObject.adjustReferences(dirtyObjectChecker);
      }
    }

    @Override
    public long getTimeStamp()
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    @Override
    public CDOBranch getBranch()
    {
      return branch;
    }

    @Override
    public Map<CDOID, EClass> getTargetObjects()
    {
      return detachedObjectTypes;
    }

    @Override
    public EReference[] getSourceReferences()
    {
      return new EReference[0];
    }

    @Override
    public Map<EClass, List<EReference>> getSourceCandidates()
    {
      return sourceCandidates;
    }

    @Override
    public int getMaxResults()
    {
      return CDOQueryInfo.UNLIMITED_RESULTS;
    }

    @Override
    public boolean addXRef(CDOID targetID, CDOID sourceID, EReference sourceReference, int sourceIndex)
    {
      if (CDOIDUtil.isNull(targetID))
      {
        // Compensate potential issues with the XRef implementation in the store accessor.
        return true;
      }

      if (detachedIDs.contains(sourceID))
      {
        // Ignore XRefs from objects that are about to be detached themselves by this commit.
        return true;
      }

      if (dirtyIDs.contains(sourceID))
      {
        // Ignore XRefs from objects that are about to be modified by this commit. They're handled later in getXRefs().
        return true;
      }

      result.add(new CDOIDReference(targetID, sourceID, sourceReference, sourceIndex));
      return true;
    }
  }
}
