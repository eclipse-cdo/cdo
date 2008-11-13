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
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
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
import org.eclipse.net4j.util.om.monitor.IMonitor;
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

  public void applyIDMappings()
  {
    applyIDMappings(newObjects);
    applyIDMappings(dirtyObjects);
    for (CDORevisionDelta dirtyObjectDelta : dirtyObjectDeltas)
    {
      ((InternalCDORevisionDelta)dirtyObjectDelta).adjustReferences(idMapper);
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

  public void commit(IMonitor monitor)
  {
    monitor.begin(5);

    try
    {
      accessor.commit(monitor.fork(4));
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
  public void write(IMonitor monitor)
  {
    monitor.begin(10);

    try
    {
      // Could throw an exception
      timeStamp = createTimeStamp();
      dirtyObjects = new CDORevision[dirtyObjectDeltas.length];

      adjustMetaRanges();
      monitor.worked(1);

      adjustTimeStamps();
      monitor.worked(1);

      Repository repository = (Repository)transaction.getRepository();
      computeDirtyObjects(!repository.isSupportingRevisionDeltas());
      lockObjects();
      monitor.worked(1);

      repository.notifyWriteAccessHandlers(transaction, this);
      monitor.worked(1);

      detachObjects();
      monitor.worked(1);

      accessor.write(this, monitor.fork(5));
    }
    catch (RuntimeException ex)
    {
      handleException(ex);
    }
    catch (InterruptedException ex)
    {
      handleException(ex);
    }
    catch (Throwable ex)
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

  private void adjustTimeStamps()
  {
    for (CDORevision newObject : newObjects)
    {
      InternalCDORevision revision = (InternalCDORevision)newObject;
      revision.setCreated(timeStamp);
    }
  }

  private void adjustMetaRanges()
  {
    for (CDOPackage newPackage : newPackages)
    {
      if (newPackage.getParentURI() == null)
      {
        adjustMetaRange(newPackage);
      }
    }
  }

  private void adjustMetaRange(CDOPackage newPackage)
  {
    CDOIDMetaRange oldRange = newPackage.getMetaIDRange();
    if (!oldRange.isTemporary())
    {
      throw new IllegalStateException("!oldRange.isTemporary()");
    }

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
    }

    metaIDRanges.add(newRange);
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

  synchronized private void unlockObjects()
  {
    if (!lockedObjects.isEmpty())
    {
      LockManager lockManager = ((Repository)transaction.getRepository()).getLockManager();
      lockManager.unlock(RWLockManager.LockType.WRITE, transaction, lockedObjects);
      lockedObjects.clear();
    }
  }

  private void computeDirtyObjects(boolean failOnNull)
  {
    for (int i = 0; i < dirtyObjectDeltas.length; i++)
    {
      dirtyObjects[i] = computeDirtyObject(dirtyObjectDeltas[i], failOnNull);
      if (dirtyObjects[i] == null && failOnNull)
      {
        throw new IllegalStateException("Can not retrieve origin revision for " + dirtyObjectDeltas[i]);
      }
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
      InternalCDORevision dirtyObject = (InternalCDORevision)CDORevisionUtil.copy(originObject);
      dirtyObjectDelta.apply(dirtyObject);
      dirtyObject.setCreated(timeStamp);
      // dirtyObject.setVersion(originObject.getVersion() + 1);
      return dirtyObject;
    }

    return null;
  }

  private void applyIDMappings(CDORevision[] revisions)
  {
    for (CDORevision revision : revisions)
    {
      if (revision != null)
      {
        InternalCDORevision internal = (InternalCDORevision)revision;
        CDOID newID = idMappings.get(internal.getID());
        if (newID != null)
        {
          internal.setID(newID);
        }

        internal.adjustReferences(idMapper);
      }
    }
  }

  synchronized public void rollback(String message)
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

  private void updateInfraStructure(IMonitor monitor)
  {
    monitor.begin(4);

    try
    {
      addNewPackages();
      monitor.worked(1);

      addRevisions(newObjects);
      monitor.worked(1);

      addRevisions(dirtyObjects);
      monitor.worked(1);

      revisedDetachObjects();
      unlockObjects();

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

  private void addNewPackages()
  {
    PackageManager packageManager = (PackageManager)transaction.getRepository().getPackageManager();
    for (int i = 0; i < newPackages.length; i++)
    {
      CDOPackage cdoPackage = newPackages[i];
      packageManager.addPackage(cdoPackage);
    }
  }

  private void addRevisions(CDORevision[] revisions)
  {
    RevisionManager revisionManager = (RevisionManager)transaction.getRepository().getRevisionManager();
    for (CDORevision revision : revisions)
    {
      if (revision != null)
      {
        revisionManager.addCachedRevision((InternalCDORevision)revision);
      }
    }
  }

  private void revisedDetachObjects()
  {
    for (InternalCDORevision revision : detachedRevisions)
    {
      revision.setRevised(getTimeStamp() - 1);
    }
  }

  private void detachObjects()
  {
    detachedRevisions.clear();
    RevisionManager revisionManager = (RevisionManager)transaction.getRepository().getRevisionManager();
    for (CDOID id : getDetachedObjects())
    {
      InternalCDORevision revision = revisionManager.getRevision(id, CDORevision.UNCHUNKED, false);
      if (revision != null)
      {
        detachedRevisions.add(revision);
      }
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
