/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.core.CDOCorePackage;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackage;
import org.eclipse.emf.cdo.spi.common.InternalCDOPackageManager;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevisionDelta;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class TransactionCommitContextImpl implements IStoreAccessor.CommitContext, Transaction.InternalCommitContext
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION,
      TransactionCommitContextImpl.class);

  private TransactionPackageManager packageManager;

  private IStoreAccessor accessor;

  private long timeStamp = CDORevision.UNSPECIFIED_DATE;

  private CDOPackage[] newPackages;

  private CDORevision[] newObjects;

  private CDORevision[] dirtyObjects;

  private CDOID[] detachedObjects;

  private List<CDOID> lockedObjects = new ArrayList<CDOID>();

  private List<InternalCDORevision> detachedRevisions = new ArrayList<InternalCDORevision>();;

  private CDORevisionDelta[] dirtyObjectDeltas;

  private List<CDOIDMetaRange> metaIDRanges = new ArrayList<CDOIDMetaRange>();

  private ConcurrentMap<CDOIDTemp, CDOID> idMappings = new ConcurrentHashMap<CDOIDTemp, CDOID>();

  private CDOIDMapper idMapper = new CDOIDMapper(idMappings);

  private String rollbackMessage;

  private Transaction transaction;

  private boolean autoReleaseLocksEnabled;

  public TransactionCommitContextImpl(Transaction transaction)
  {
    this.transaction = transaction;
    packageManager = new TransactionPackageManager();
  }

  public int getTransactionID()
  {
    return transaction.getViewID();
  }

  public Transaction getTransaction()
  {
    return transaction;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public TransactionPackageManager getPackageManager()
  {
    return packageManager;
  }

  public CDOPackage[] getNewPackages()
  {
    return newPackages;
  }

  public CDORevision[] getNewObjects()
  {
    return newObjects;
  }

  public CDORevision[] getDirtyObjects()
  {
    return dirtyObjects;
  }

  public CDOID[] getDetachedObjects()
  {
    return detachedObjects;
  }

  public CDORevisionDelta[] getDirtyObjectDeltas()
  {
    return dirtyObjectDeltas;
  }

  public List<CDOIDMetaRange> getMetaIDRanges()
  {
    return Collections.unmodifiableList(metaIDRanges);
  }

  public Map<CDOIDTemp, CDOID> getIDMappings()
  {
    return Collections.unmodifiableMap(idMappings);
  }

  public void addIDMapping(CDOIDTemp oldID, CDOID newID)
  {
    if (CDOIDUtil.isNull(newID) || newID.isTemporary())
    {
      throw new IllegalStateException("newID=" + newID);
    }

    CDOID previousMapping = idMappings.putIfAbsent(oldID, newID);
    if (previousMapping != null)
    {
      throw new IllegalStateException("previousMapping != null");
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
        monitor.worked(1);
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
  }

  public void setNewPackages(CDOPackage[] newPackages)
  {
    this.newPackages = newPackages;
  }

  public void setNewObjects(CDORevision[] newObjects)
  {
    this.newObjects = newObjects;
  }

  public void setDirtyObjectDeltas(CDORevisionDelta[] dirtyObjectDeltas)
  {
    this.dirtyObjectDeltas = dirtyObjectDeltas;
  }

  public void setDetachedObjects(CDOID[] detachedObjects)
  {
    this.detachedObjects = detachedObjects;
  }

  public boolean setAutoReleaseLocksEnabled(boolean on)
  {
    try
    {
      return autoReleaseLocksEnabled;
    }
    finally
    {
      autoReleaseLocksEnabled = on;
    }
  }

  public boolean isAutoReleaseLocksEnabled()
  {
    return autoReleaseLocksEnabled;
  }

  public void commit(OMMonitor monitor)
  {
    try
    {
      monitor.begin(101);
      accessor.commit(monitor.fork(100));
      updateInfraStructure(monitor.fork(1));
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

  /**
   * @since 2.0
   */
  public void write(OMMonitor monitor)
  {
    try
    {
      monitor.begin(106);

      // Could throw an exception
      timeStamp = createTimeStamp();
      dirtyObjects = new CDORevision[dirtyObjectDeltas.length];

      adjustMetaRanges(monitor.fork(1));
      adjustTimeStamps(monitor.fork(1));

      Repository repository = (Repository)transaction.getRepository();
      computeDirtyObjects(!repository.isSupportingRevisionDeltas(), monitor.fork(1));

      lockObjects();
      monitor.worked(1);

      repository.notifyWriteAccessHandlers(transaction, this, monitor.fork(1));
      detachObjects(monitor.fork(1));

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

  private void handleException(Throwable ex)
  {
    OM.LOG.error(ex);
    String storeClass = transaction.getRepository().getStore().getClass().getSimpleName();
    rollback("Rollback in " + storeClass + ": " + StringUtil.formatException(ex));
  }

  protected long createTimeStamp()
  {
    Repository repository = (Repository)transaction.getSession().getSessionManager().getRepository();
    return repository.createCommitTimeStamp();
  }

  public void postCommit(boolean success)
  {
    try
    {
      if (success)
      {
        transaction.getRepository().getNotificationManager().notifyCommit(transaction.getSession(), this);
      }
    }
    finally
    {
      StoreThreadLocal.release();
      accessor = null;
      timeStamp = CDORevision.UNSPECIFIED_DATE;
      packageManager.clear();
      metaIDRanges.clear();
      idMappings.clear();
      rollbackMessage = null;
      newPackages = null;
      newObjects = null;
      dirtyObjectDeltas = null;
      dirtyObjects = null;
    }
  }

  private void adjustTimeStamps(OMMonitor monitor)
  {
    try
    {
      monitor.begin(newObjects.length);
      for (CDORevision newObject : newObjects)
      {
        InternalCDORevision revision = (InternalCDORevision)newObject;
        revision.setCreated(timeStamp);
        monitor.worked(1);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void adjustMetaRanges(OMMonitor monitor)
  {
    try
    {
      monitor.begin(newPackages.length);
      for (CDOPackage newPackage : newPackages)
      {
        if (newPackage.getParentURI() == null)
        {
          adjustMetaRange(newPackage, monitor.fork(1));
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void adjustMetaRange(CDOPackage newPackage, OMMonitor monitor)
  {
    CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
    if (!oldRange.isTemporary())
    {
      throw new IllegalStateException("!oldRange.isTemporary()");
    }

    try
    {
      monitor.begin(oldRange.size());
      CDOIDMetaRange newRange = transaction.getRepository().getMetaIDRange(oldRange.size());
      ((InternalCDOPackage)newPackage).setMetaIDRange(newRange);
      for (int l = 0; l < oldRange.size(); l++)
      {
        CDOIDTemp oldID = (CDOIDTemp)oldRange.get(l);
        CDOID newID = newRange.get(l);
        if (TRACER.isEnabled())
        {
          TRACER.format("Mapping meta ID: {0} --> {1}", oldID, newID);
        }

        idMappings.put(oldID, newID);
        monitor.worked(1);
      }

      metaIDRanges.add(newRange);
    }
    finally
    {
      monitor.done();
    }
  }

  private void lockObjects() throws InterruptedException
  {
    lockedObjects.clear();
    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      lockedObjects.add(dirtyObjectDeltas[i].getID());
    }

    for (int i = 0; i < detachedObjects.length; i++)
    {
      lockedObjects.add(detachedObjects[i]);
    }

    try
    {
      LockManager lockManager = ((Repository)transaction.getRepository()).getLockManager();
      lockManager.lock(RWLockManager.LockType.WRITE, transaction, lockedObjects, 1000);
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
      LockManager lockManager = ((Repository)transaction.getRepository()).getLockManager();
      lockManager.unlock(RWLockManager.LockType.WRITE, transaction, lockedObjects);
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
          throw new IllegalStateException("Can not retrieve origin revision for " + dirtyObjectDeltas[i]);
        }

        monitor.worked(1);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private CDORevision computeDirtyObject(CDORevisionDelta dirtyObjectDelta, boolean loadOnDemand)
  {
    CDOID id = dirtyObjectDelta.getID();
    int version = dirtyObjectDelta.getOriginVersion();

    CDORevisionResolver revisionResolver = transaction.getRepository().getRevisionManager();
    CDORevision originObject = revisionResolver.getRevisionByVersion(id, CDORevision.UNCHUNKED, version, loadOnDemand);
    if (originObject != null)
    {
      InternalCDORevision dirtyObject = (InternalCDORevision)originObject.copy();
      dirtyObjectDelta.apply(dirtyObject);
      dirtyObject.setCreated(timeStamp);
      // dirtyObject.setVersion(originObject.getVersion() + 1);
      return dirtyObject;
    }

    return null;
  }

  private void applyIDMappings(CDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (CDORevision revision : revisions)
      {
        if (revision != null)
        {
          InternalCDORevision internalRevision = (InternalCDORevision)revision;
          CDOID newID = idMappings.get(internalRevision.getID());
          if (newID != null)
          {
            internalRevision.setID(newID);
          }

          internalRevision.adjustReferences(idMapper);
          monitor.worked(1);
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
    // Check if we already rolledBack
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
          OM.LOG.warn("Problem while rolling back  the transaction", ex);
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
      addNewPackages(monitor.fork(1));
      addRevisions(newObjects, monitor.fork(1));
      addRevisions(dirtyObjects, monitor.fork(1));
      revisedDetachObjects(monitor.fork(1));
      unlockObjects();
      monitor.worked(1);

      if (isAutoReleaseLocksEnabled())
      {
        ((Repository)transaction.getRepository()).getLockManager().unlock(transaction);
      }

      monitor.worked(1);
    }
    catch (RuntimeException ex)
    {
      // TODO Rethink this case
      OM.LOG.error("FATAL: Memory infrastructure corrupted after successful commit operation of the store");
    }
    finally
    {
      monitor.done();
    }
  }

  private void addNewPackages(OMMonitor monitor)
  {
    try
    {
      monitor.begin(newPackages.length);
      PackageManager packageManager = (PackageManager)transaction.getRepository().getPackageManager();
      for (int i = 0; i < newPackages.length; i++)
      {
        CDOPackage cdoPackage = newPackages[i];
        packageManager.addPackage(cdoPackage);
        monitor.worked(1);
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void addRevisions(CDORevision[] revisions, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      RevisionManager revisionManager = (RevisionManager)transaction.getRepository().getRevisionManager();
      for (CDORevision revision : revisions)
      {
        if (revision != null)
        {
          revisionManager.addCachedRevision((InternalCDORevision)revision);
        }

        monitor.worked(1);
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
      for (InternalCDORevision revision : detachedRevisions)
      {
        revision.setRevised(getTimeStamp() - 1);
        monitor.worked(1);
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
    RevisionManager revisionManager = (RevisionManager)transaction.getRepository().getRevisionManager();
    CDOID[] detachedObjects = getDetachedObjects();

    try
    {
      monitor.begin(detachedObjects.length);
      for (CDOID id : detachedObjects)
      {
        InternalCDORevision revision = revisionManager.getRevision(id, CDORevision.UNCHUNKED, false);
        if (revision != null)
        {
          detachedRevisions.add(revision);
        }

        monitor.worked(1);
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
  public final class TransactionPackageManager implements InternalCDOPackageManager
  {
    private List<CDOPackage> newPackages = new ArrayList<CDOPackage>();

    private PackageManager repositoryPackageManager = (PackageManager)transaction.getRepository().getPackageManager();

    public TransactionPackageManager()
    {
    }

    public void addPackage(CDOPackage cdoPackage)
    {
      newPackages.add(cdoPackage);
    }

    public void clear()
    {
      newPackages.clear();
    }

    public CDOIDObjectFactory getCDOIDObjectFactory()
    {
      return repositoryPackageManager.getCDOIDObjectFactory();
    }

    public CDOPackage lookupPackage(String uri)
    {
      for (CDOPackage cdoPackage : newPackages)
      {
        if (ObjectUtil.equals(cdoPackage.getPackageURI(), uri))
        {
          return cdoPackage;
        }
      }

      return repositoryPackageManager.lookupPackage(uri);
    }

    public CDOCorePackage getCDOCorePackage()
    {
      return repositoryPackageManager.getCDOCorePackage();
    }

    public CDOResourcePackage getCDOResourcePackage()
    {
      return repositoryPackageManager.getCDOResourcePackage();
    }

    public void loadPackage(CDOPackage cdoPackage)
    {
      repositoryPackageManager.loadPackage(cdoPackage);
    }

    public void loadPackageEcore(CDOPackage cdoPackage)
    {
      repositoryPackageManager.loadPackageEcore(cdoPackage);
    }

    public int getPackageCount()
    {
      throw new UnsupportedOperationException();
    }

    public CDOPackage[] getPackages()
    {
      throw new UnsupportedOperationException();
    }

    public CDOPackage[] getElements()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isEmpty()
    {
      throw new UnsupportedOperationException();
    }

    public void addListener(IListener listener)
    {
      throw new UnsupportedOperationException();
    }

    public void removeListener(IListener listener)
    {
      throw new UnsupportedOperationException();
    }
  }
}
