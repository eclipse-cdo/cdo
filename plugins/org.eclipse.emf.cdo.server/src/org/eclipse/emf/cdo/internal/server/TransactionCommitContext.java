/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class TransactionCommitContext implements InternalCommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, TransactionCommitContext.class);

  private TransactionPackageRegistry packageRegistry;

  private IStoreAccessor accessor;

  private long timeStamp = CDORevision.UNSPECIFIED_DATE;

  private String commitComment;

  private InternalCDOPackageUnit[] newPackageUnits;

  private InternalCDORevision[] newObjects;

  private InternalCDORevision[] dirtyObjects;

  private CDOID[] detachedObjects;

  private List<Object> lockedObjects = new ArrayList<Object>();

  private List<InternalCDORevision> detachedRevisions = new ArrayList<InternalCDORevision>();

  private InternalCDORevisionDelta[] dirtyObjectDeltas;

  private List<CDOIDMetaRange> metaIDRanges = new ArrayList<CDOIDMetaRange>();

  private ConcurrentMap<CDOID, CDOID> idMappings = new ConcurrentHashMap<CDOID, CDOID>();

  private CDOReferenceAdjuster idMapper = new CDOIDMapper(idMappings);

  private String rollbackMessage;

  private InternalTransaction transaction;

  private boolean autoReleaseLocksEnabled;

  public TransactionCommitContext(InternalTransaction transaction)
  {
    this.transaction = transaction;
    InternalRepository repository = transaction.getRepository();
    packageRegistry = new TransactionPackageRegistry(repository.getPackageRegistry(false));
    packageRegistry.activate();
  }

  public int getTransactionID()
  {
    return transaction.getViewID();
  }

  public InternalTransaction getTransaction()
  {
    return transaction;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return CDOBranchUtil.createBranchPoint(transaction.getBranch(), timeStamp);
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public InternalCDOPackageUnit[] getNewPackageUnits()
  {
    return newPackageUnits;
  }

  public InternalCDORevision[] getNewObjects()
  {
    return newObjects;
  }

  public InternalCDORevision[] getDirtyObjects()
  {
    return dirtyObjects;
  }

  public CDOID[] getDetachedObjects()
  {
    return detachedObjects;
  }

  public InternalCDORevisionDelta[] getDirtyObjectDeltas()
  {
    return dirtyObjectDeltas;
  }

  public List<CDOIDMetaRange> getMetaIDRanges()
  {
    return Collections.unmodifiableList(metaIDRanges);
  }

  public Map<CDOID, CDOID> getIDMappings()
  {
    return Collections.unmodifiableMap(idMappings);
  }

  public void addIDMapping(CDOID oldID, CDOID newID)
  {
    if (CDOIDUtil.isNull(newID) || newID.isTemporary())
    {
      throw new IllegalStateException("newID=" + newID); //$NON-NLS-1$
    }

    CDOID previousMapping = idMappings.putIfAbsent(oldID, newID);
    if (previousMapping != null)
    {
      throw new IllegalStateException("previousMapping != null"); //$NON-NLS-1$
    }
  }

  public void applyIDMappings(OMMonitor monitor)
  {
    try
    {
      monitor.begin(newObjects.length + dirtyObjects.length + dirtyObjectDeltas.length);
      applyIDMappings(newObjects, monitor.fork(newObjects.length));
      applyIDMappings(dirtyObjects, monitor.fork(dirtyObjects.length));
      for (CDORevisionDelta dirtyObjectDelta : dirtyObjectDeltas)
      {
        ((InternalCDORevisionDelta)dirtyObjectDelta).adjustReferences(idMapper);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  public String getRollbackMessage()
  {
    return rollbackMessage;
  }

  public void preCommit()
  {
    // Allocate a store writer
    accessor = transaction.getRepository().getStore().getWriter(transaction);

    // Make the store writer available in a ThreadLocal variable
    StoreThreadLocal.setAccessor(accessor);
    StoreThreadLocal.setCommitContext(this);
  }

  public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits)
  {
    this.newPackageUnits = newPackageUnits;
  }

  public void setNewObjects(InternalCDORevision[] newObjects)
  {
    this.newObjects = newObjects;
  }

  public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas)
  {
    this.dirtyObjectDeltas = dirtyObjectDeltas;
  }

  public void setDetachedObjects(CDOID[] detachedObjects)
  {
    this.detachedObjects = detachedObjects;
  }

  public void setAutoReleaseLocksEnabled(boolean on)
  {
    autoReleaseLocksEnabled = on;
  }

  public boolean isAutoReleaseLocksEnabled()
  {
    return autoReleaseLocksEnabled;
  }

  public String getUserID()
  {
    return getTransaction().getSession().getUserID();
  }

  public String getCommitComment()
  {
    return commitComment;
  }

  public void setCommitComment(String commitComment)
  {
    this.commitComment = commitComment;
  }

  /**
   * @since 2.0
   */
  public void write(OMMonitor monitor)
  {
    try
    {
      monitor.begin(106);

      adjustMetaRanges();
      monitor.worked();

      lockObjects();

      // Could throw an exception
      timeStamp = createTimeStamp();
      dirtyObjects = new InternalCDORevision[dirtyObjectDeltas.length];

      adjustForCommit();
      monitor.worked();

      InternalRepository repository = transaction.getRepository();
      computeDirtyObjects(!repository.isSupportingRevisionDeltas(), monitor.fork());

      monitor.worked();

      repository.notifyWriteAccessHandlers(transaction, this, monitor.fork());
      detachObjects(monitor.fork());

      accessor.write(this, monitor.fork(100));
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

  public void commit(OMMonitor monitor)
  {
    try
    {
      monitor.begin(101);
      accessor.commit(monitor.fork(100));
      updateInfraStructure(monitor.fork());
    }
    catch (RuntimeException ex)
    {
      handleException(ex);
    }
    catch (Error ex)
    {
      handleException(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  private void handleException(Throwable ex)
  {
    OM.LOG.error(ex);
    String storeClass = transaction.getRepository().getStore().getClass().getSimpleName();
    rollback("Rollback in " + storeClass + ": " + StringUtil.formatException(ex)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected long createTimeStamp()
  {
    InternalRepository repository = transaction.getSession().getManager().getRepository();
    return repository.createCommitTimeStamp();
  }

  public void postCommit(boolean success)
  {
    try
    {
      if (success)
      {
        InternalSessionManager sessionManager = transaction.getRepository().getSessionManager();
        sessionManager.sendCommitNotification(transaction.getSession(), createCommitInfo());
      }
    }
    catch (Exception ex)
    {
      OM.LOG.warn("A problem occured while notifying other sessions", ex);
    }
    finally
    {
      StoreThreadLocal.release();
      accessor = null;
      timeStamp = CDORevision.UNSPECIFIED_DATE;

      packageRegistry.deactivate();
      packageRegistry = null;

      metaIDRanges.clear();
      metaIDRanges = null;

      idMappings.clear();
      idMappings = null;

      rollbackMessage = null;
      newPackageUnits = null;
      newObjects = null;
      dirtyObjectDeltas = null;
      dirtyObjects = null;
    }
  }

  private CDOCommitInfo createCommitInfo()
  {
    InternalCDOCommitInfoManager commitInfoManager = transaction.getRepository().getCommitInfoManager();

    CDOBranch branch = transaction.getBranch();
    String userID = transaction.getSession().getUserID();
    CDOCommitData commitData = createCommitData();
    return commitInfoManager.createCommitInfo(branch, timeStamp, userID, commitComment, commitData);
  }

  private CDOCommitData createCommitData()
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
        return detachedRevisions.get(i);
      }

      @Override
      public int size()
      {
        return detachedRevisions.size();
      }
    };

    return new CDOCommitDataImpl(newPackageUnitsCollection, newObjectsCollection, changedObjectsCollection,
        detachedObjectsCollection);
  }

  private void adjustForCommit()
  {
    for (InternalCDOPackageUnit newPackageUnit : newPackageUnits)
    {
      newPackageUnit.setTimeStamp(timeStamp);
    }

    for (InternalCDORevision newObject : newObjects)
    {
      newObject.adjustForCommit(transaction.getBranch(), timeStamp);
    }
  }

  protected void adjustMetaRanges()
  {
    for (InternalCDOPackageUnit newPackageUnit : newPackageUnits)
    {
      for (InternalCDOPackageInfo packageInfo : newPackageUnit.getPackageInfos())
      {
        adjustMetaRange(packageInfo);
      }
    }
  }

  private void adjustMetaRange(InternalCDOPackageInfo packageInfo)
  {
    CDOIDMetaRange oldRange = packageInfo.getMetaIDRange();
    if (!oldRange.isTemporary())
    {
      throw new IllegalStateException("!oldRange.isTemporary()"); //$NON-NLS-1$
    }

    int count = oldRange.size();
    CDOIDMetaRange newRange = transaction.getRepository().getStore().getNextMetaIDRange(count);
    packageInfo.setMetaIDRange(newRange);
    packageRegistry.getMetaInstanceMapper().mapMetaInstances(packageInfo.getEPackage(), newRange);

    for (int i = 0; i < count; i++)
    {
      CDOIDTemp oldID = (CDOIDTemp)oldRange.get(i);
      CDOID newID = newRange.get(i);
      idMappings.put(oldID, newID);
    }

    metaIDRanges.add(newRange);
    if (TRACER.isEnabled())
    {
      TRACER.format("Mapping meta ID range: {0} --> {1}", oldRange, newRange); //$NON-NLS-1$
    }
  }

  private void lockObjects() throws InterruptedException
  {
    lockedObjects.clear();
    boolean supportingBranches = transaction.getRepository().isSupportingBranches();

    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      CDOID id = dirtyObjectDeltas[i].getID();
      Object key = supportingBranches ? CDOIDUtil.createIDAndBranch(id, transaction.getBranch()) : id;
      lockedObjects.add(key);
    }

    for (int i = 0; i < detachedObjects.length; i++)
    {
      CDOID id = detachedObjects[i];
      Object key = supportingBranches ? CDOIDUtil.createIDAndBranch(id, transaction.getBranch()) : id;
      lockedObjects.add(key);
    }

    try
    {
      if (!lockedObjects.isEmpty())
      {
        InternalLockManager lockManager = transaction.getRepository().getLockManager();
        lockManager.lock(LockType.WRITE, transaction, lockedObjects, 1000);
      }
    }
    catch (TimeoutRuntimeException exception)
    {
      lockedObjects.clear();
      throw exception;
    }
  }

  private synchronized void unlockObjects()
  {
    if (!lockedObjects.isEmpty())
    {
      InternalLockManager lockManager = transaction.getRepository().getLockManager();
      lockManager.unlock(LockType.WRITE, transaction, lockedObjects);
      lockedObjects.clear();
    }
  }

  private void computeDirtyObjects(boolean failOnNull, OMMonitor monitor)
  {
    try
    {
      monitor.begin(dirtyObjectDeltas.length);
      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        dirtyObjects[i] = computeDirtyObject(dirtyObjectDeltas[i], failOnNull);
        if (dirtyObjects[i] == null && failOnNull)
        {
          throw new IllegalStateException("Can not retrieve origin revision for " + dirtyObjectDeltas[i]); //$NON-NLS-1$
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private InternalCDORevision computeDirtyObject(InternalCDORevisionDelta delta, boolean loadOnDemand)
  {
    CDOID id = delta.getID();

    InternalRepository repository = transaction.getRepository();
    InternalCDORevisionManager revisionManager = repository.getRevisionManager();

    InternalCDORevision oldRevision = revisionManager.getRevisionByVersion(id, delta, CDORevision.UNCHUNKED, true);
    if (oldRevision == null)
    {
      throw new IllegalStateException("Origin revision not found for " + delta);
    }

    if (oldRevision.isHistorical())
    {
      throw new ConcurrentModificationException("Trying to update object " + id + " that was already modified");
    }

    if (loadOnDemand)
    {
      // Make sure all chunks are loaded
      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(oldRevision.getEClass()))
      {
        if (feature.isMany())
        {
          repository.ensureChunk(oldRevision, feature, 0, oldRevision.getList(feature).size());
        }
      }
    }

    InternalCDORevision newRevision = oldRevision.copy();
    newRevision.adjustForCommit(transaction.getBranch(), timeStamp);

    delta.apply(newRevision);
    return newRevision;
  }

  private void applyIDMappings(InternalCDORevision[] revisions, OMMonitor monitor)
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

  public synchronized void rollback(String message)
  {
    // Check if we already rolled back
    if (rollbackMessage == null)
    {
      rollbackMessage = message;
      unlockObjects();
      if (accessor != null)
      {
        try
        {
          accessor.rollback();
        }
        catch (RuntimeException ex)
        {
          OM.LOG.warn("Problem while rolling back  the transaction", ex); //$NON-NLS-1$
        }
      }
    }
  }

  protected IStoreAccessor getAccessor()
  {
    return accessor;
  }

  private void updateInfraStructure(OMMonitor monitor)
  {
    try
    {
      monitor.begin(6);
      addNewPackageUnits(monitor.fork());
      addRevisions(newObjects, monitor.fork());
      addRevisions(dirtyObjects, monitor.fork());
      revisedDetachObjects(monitor.fork());
      unlockObjects();
      monitor.worked();

      if (isAutoReleaseLocksEnabled())
      {
        transaction.getRepository().getLockManager().unlock(transaction);
      }

      monitor.worked();
    }
    finally
    {
      monitor.done();
    }
  }

  private void addNewPackageUnits(OMMonitor monitor)
  {
    InternalRepository repository = transaction.getRepository();
    InternalCDOPackageRegistry repositoryPackageRegistry = repository.getPackageRegistry(false);
    synchronized (repositoryPackageRegistry)
    {
      try
      {
        monitor.begin(newPackageUnits.length);
        for (int i = 0; i < newPackageUnits.length; i++)
        {
          newPackageUnits[i].setState(CDOPackageUnit.State.LOADED);
          repositoryPackageRegistry.putPackageUnit(newPackageUnits[i]);
          monitor.worked();
        }

        repositoryPackageRegistry.getMetaInstanceMapper().mapMetaInstances(packageRegistry.getMetaInstanceMapper());
      }
      finally
      {
        monitor.done();
      }
    }
  }

  private void addRevisions(CDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      InternalCDORevisionManager revisionManager = transaction.getRepository().getRevisionManager();
      for (CDORevision revision : revisions)
      {
        if (revision != null)
        {
          if (!revisionManager.addRevision(revision))
          {
            throw new IllegalStateException("Revision was not registered: " + revision);
          }
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void revisedDetachObjects(OMMonitor monitor)
  {
    try
    {
      monitor.begin(detachedRevisions.size());
      long revised = getBranchPoint().getTimeStamp() - 1;
      for (InternalCDORevision revision : detachedRevisions)
      {
        revision.setRevised(revised);
        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void detachObjects(OMMonitor monitor)
  {
    detachedRevisions.clear();
    InternalCDORevisionManager revisionManager = transaction.getRepository().getRevisionManager();
    CDOID[] detachedObjects = getDetachedObjects();

    try
    {
      monitor.begin(detachedObjects.length);
      for (CDOID id : detachedObjects)
      {
        InternalCDORevision revision = (InternalCDORevision)revisionManager.getCache().getRevision(id, transaction);
        if (revision != null)
        {
          detachedRevisions.add(revision);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
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
  }
}
