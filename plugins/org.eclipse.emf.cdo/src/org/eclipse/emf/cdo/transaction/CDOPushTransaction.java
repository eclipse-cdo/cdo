/*
 * Copyright (c) 2009-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitHistory;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDORegistrationHandler;
import org.eclipse.emf.cdo.view.CDOUnitManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.Predicate;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

/**
 * A {@link CDOTransaction transaction} that persists changes to the object graph locally on commit and can later load
 * these changes and push them to the {@link CDOCommonRepository repository}.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOPushTransaction extends Notifier implements CDOTransaction
{
  private CDOTransaction delegate;

  private File file;

  private boolean dirty;

  private CDOTransactionHandler delegateHandler = new CDOTransactionHandler()
  {
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      setDirty(true);
    }

    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      setDirty(true);
    }

    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      setDirty(true);
    }

    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    public void rolledBackTransaction(CDOTransaction transaction)
    {
    }
  };

  private final IRegistry<String, Object> properties = new HashMapRegistry<String, Object>()
  {
    @Override
    public void setAutoCommit(boolean autoCommit)
    {
      throw new UnsupportedOperationException();
    }
  };

  public CDOPushTransaction(CDOTransaction delegate) throws IOException
  {
    this(delegate, createTempFile(delegate));
  }

  public CDOPushTransaction(CDOTransaction delegate, File file) throws IOException
  {
    this(delegate, file, true);
  }

  public CDOPushTransaction(CDOTransaction delegate, File file, boolean reconstructSavepoints) throws IOException
  {
    this.delegate = delegate;
    this.file = file;

    boolean delegateWasDirty = delegate.isDirty();
    delegate.addTransactionHandler(delegateHandler);

    if (file.isDirectory())
    {
      throw new IllegalArgumentException("Not a file: " + file.getAbsolutePath());
    }

    OM.LOG.info("Using " + file.getAbsolutePath() + " for push transaction " + delegate.getSession().getRepositoryInfo().getName() + ":" + //
        delegate.getSession().getSessionID() + ":" + //
        delegate.getViewID());

    if (file.exists())
    {
      InputStream in = null;

      try
      {
        in = new FileInputStream(file);
        delegate.importChanges(in, reconstructSavepoints);
      }
      finally
      {
        IOUtil.close(in);
      }
    }

    dirty = delegateWasDirty;
  }

  public CDOTransaction getDelegate()
  {
    return delegate;
  }

  public File getFile()
  {
    return file;
  }

  /**
   * @since 4.4
   */
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  public boolean isDirty()
  {
    return dirty || delegate.isDirty();
  }

  protected void setDirty(boolean dirty)
  {
    if (this.dirty != dirty)
    {
      this.dirty = dirty;
      if (dirty)
      {
        fireEvent(new CDOTransactionStartedEvent()
        {
          public CDOView getSource()
          {
            return CDOPushTransaction.this;
          }
        });
      }
      else
      {
        fireEvent(new CDOTransactionFinishedEvent()
        {
          public CDOView getSource()
          {
            return CDOPushTransaction.this;
          }

          @Deprecated
          public Type getType()
          {
            return Type.COMMITTED;
          }

          public Cause getCause()
          {
            return Cause.COMMITTED;
          }

          public Map<CDOID, CDOID> getIDMappings()
          {
            return Collections.emptyMap();
          }
        });
      }
    }
  }

  public CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  public CDOCommitInfo commit(IProgressMonitor monitor) throws CommitException
  {
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(file);
      delegate.exportChanges(out);
      setDirty(false);
      return null;
    }
    catch (Exception ex)
    {
      throw new CommitException("A problem occured while exporting changes to " + file.getAbsolutePath(), ex);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  public <T> CommitResult<T> commit(Callable<T> callable, Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    return delegate.commit(callable, retry, monitor);
  }

  public <T> CommitResult<T> commit(Callable<T> callable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException, Exception
  {
    return delegate.commit(callable, attempts, monitor);
  }

  public CDOCommitInfo commit(Runnable runnable, Predicate<Long> retry, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return delegate.commit(runnable, retry, monitor);
  }

  public CDOCommitInfo commit(Runnable runnable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return delegate.commit(runnable, attempts, monitor);
  }

  public void rollback()
  {
    throw new UnsupportedOperationException("Rollback not supported for push transactions");
  }

  public void push() throws CommitException
  {
    push(null);
  }

  public void push(IProgressMonitor progressMonitor) throws CommitException
  {
    delegate.commit(progressMonitor);
    file.delete();
    setDirty(false);
  }

  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException
  {
    return delegate.exportChanges(out);
  }

  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException
  {
    return delegate.importChanges(in, reconstructSavepoints);
  }

  public long getLastCommitTime()
  {
    return delegate.getLastCommitTime();
  }

  @Override
  public void addListener(IListener listener)
  {
    super.addListener(listener);
    delegate.addListener(listener);
  }

  @Override
  public void removeListener(IListener listener)
  {
    super.removeListener(listener);
    delegate.removeListener(listener);
  }

  @Override
  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  @Override
  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  public void addObjectHandler(CDOObjectHandler handler)
  {
    delegate.addObjectHandler(handler);
  }

  /**
   * @since 4.6
   */
  public void addRegistrationHandler(CDORegistrationHandler handler)
  {
    delegate.addRegistrationHandler(handler);
  }

  /**
   * @since 4.0
   */
  public void addTransactionHandler(CDOTransactionHandlerBase handler)
  {
    delegate.addTransactionHandler(handler);
  }

  public void close()
  {
    delegate.removeTransactionHandler(delegateHandler);
    delegate.close();
  }

  public CDOQuery createQuery(String language, String queryString)
  {
    return createQuery(language, queryString, null, false);
  }

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, Object context)
  {
    return createQuery(language, queryString, context, false);
  }

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, boolean considerDirtyState)
  {
    return createQuery(language, queryString, null, considerDirtyState);
  }

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, Object context, boolean considerDirtyState)
  {
    return delegate.createQuery(language, queryString, context, considerDirtyState);
  }

  /**
   * @since 4.0
   */
  public CDOResourceFolder createResourceFolder(String path)
  {
    return delegate.createResourceFolder(path);
  }

  public CDOResource createResource(String path)
  {
    return delegate.createResource(path);
  }

  @Deprecated
  public boolean isLegacyModeEnabled()
  {
    return delegate.isLegacyModeEnabled();
  }

  public long getLastUpdateTime()
  {
    return delegate.getLastUpdateTime();
  }

  public void waitForUpdate(long updateTime)
  {
    delegate.waitForUpdate(updateTime);
  }

  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    return delegate.waitForUpdate(updateTime, timeoutMillis);
  }

  /**
   * @since 4.3
   */
  public boolean runAfterUpdate(long updateTime, Runnable runnable)
  {
    return delegate.runAfterUpdate(updateTime, runnable);
  }

  public Set<CDOObject> getConflicts()
  {
    return delegate.getConflicts();
  }

  /**
   * @since 4.0
   */
  public CDOChangeSetData getChangeSetData()
  {
    return delegate.getChangeSetData();
  }

  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return delegate.getDetachedObjects();
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return delegate.getDirtyObjects();
  }

  /**
   * @since 4.0
   */
  public CDORevision getRevision(CDOID id)
  {
    return delegate.getRevision(id);
  }

  /**
   * @since 4.1
   */
  public CDOSavepoint getFirstSavepoint()
  {
    return delegate.getFirstSavepoint();
  }

  public CDOSavepoint getLastSavepoint()
  {
    return delegate.getLastSavepoint();
  }

  /**
   * @since 4.0
   * @deprecated
   */
  @Deprecated
  public boolean isInvalidationRunnerActive()
  {
    return isInvalidating();
  }

  /**
   * @since 4.7
   */
  public boolean isInvalidating()
  {
    return delegate.isInvalidating();
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return delegate.getNewObjects();
  }

  public CDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    return delegate.getObject(id, loadOnDemand);
  }

  public CDOObject getObject(CDOID id)
  {
    return delegate.getObject(id);
  }

  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    return delegate.getObject(objectFromDifferentView);
  }

  public CDOObjectHandler[] getObjectHandlers()
  {
    return delegate.getObjectHandlers();
  }

  /**
   * @since 4.6
   */
  public CDORegistrationHandler[] getRegistrationHandlers()
  {
    return delegate.getRegistrationHandlers();
  }

  public CDOResource getOrCreateResource(String path)
  {
    return delegate.getOrCreateResource(path);
  }

  /**
   * @since 4.0
   */
  public CDOResourceFolder getOrCreateResourceFolder(String path)
  {
    return delegate.getOrCreateResourceFolder(path);
  }

  public CDOResource getResource(String path, boolean loadOnDemand) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResource(path, loadOnDemand);
  }

  public CDOResource getResource(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResource(path);
  }

  public CDOResourceNode getResourceNode(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResourceNode(path);
  }

  /**
   * @since 4.2
   */
  public CDOTextResource createTextResource(String path)
  {
    return delegate.createTextResource(path);
  }

  /**
   * @since 4.2
   */
  public CDOTextResource getOrCreateTextResource(String path)
  {
    return delegate.getOrCreateTextResource(path);
  }

  /**
   * @since 4.2
   */
  public CDOBinaryResource createBinaryResource(String path)
  {
    return delegate.createBinaryResource(path);
  }

  /**
   * @since 4.2
   */
  public CDOBinaryResource getOrCreateBinaryResource(String path)
  {
    return delegate.getOrCreateBinaryResource(path);
  }

  /**
   * @since 4.2
   */
  public CDOTextResource getTextResource(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getTextResource(path);
  }

  /**
   * @since 4.2
   */
  public CDOBinaryResource getBinaryResource(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getBinaryResource(path);
  }

  /**
   * @since 4.2
   */
  public CDOResourceFolder getResourceFolder(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResourceFolder(path);
  }

  /**
   * @since 4.2
   */
  public void setResourcePathCache(Map<String, CDOID> resourcePathCache)
  {
    delegate.setResourcePathCache(resourcePathCache);
  }

  public ResourceSet getResourceSet()
  {
    return delegate.getResourceSet();
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return delegate.getRevisionDeltas();
  }

  public CDOResource getRootResource()
  {
    return delegate.getRootResource();
  }

  public CDOSession getSession()
  {
    return delegate.getSession();
  }

  /**
   * @since 4.4
   */
  public CDOViewProvider getProvider()
  {
    return delegate.getProvider();
  }

  /**
   * @since 4.4
   */
  public URI createResourceURI(String path)
  {
    return delegate.createResourceURI(path);
  }

  public long getTimeStamp()
  {
    return delegate.getTimeStamp();
  }

  /**
   * @since 4.0
   */
  public String getDurableLockingID()
  {
    return delegate.getDurableLockingID();
  }

  public CDOTransactionHandler[] getTransactionHandlers()
  {
    return delegate.getTransactionHandlers();
  }

  /**
   * @since 4.0
   */
  public CDOTransactionHandler1[] getTransactionHandlers1()
  {
    return delegate.getTransactionHandlers1();
  }

  /**
   * @since 4.0
   */
  public CDOTransactionHandler2[] getTransactionHandlers2()
  {
    return delegate.getTransactionHandlers2();
  }

  /**
   * @since 4.1
   */
  public int getSessionID()
  {
    return delegate.getSessionID();
  }

  /**
   * @since 4.1
   */
  public boolean isDurableView()
  {
    return delegate.isDurableView();
  }

  public int getViewID()
  {
    return delegate.getViewID();
  }

  public CDOViewSet getViewSet()
  {
    return delegate.getViewSet();
  }

  /**
   * @since 4.5
   */
  public Lock getViewLock()
  {
    return delegate.getViewLock();
  }

  /**
   * @since 4.5
   */
  public void syncExec(Runnable runnable)
  {
    delegate.syncExec(runnable);
  }

  /**
   * @since 4.5
   */
  public <V> V syncExec(Callable<V> callable) throws Exception
  {
    return delegate.syncExec(callable);
  }

  public boolean hasConflict()
  {
    return delegate.hasConflict();
  }

  public boolean hasResource(String path)
  {
    return delegate.hasResource(path);
  }

  public boolean isClosed()
  {
    return delegate.isClosed();
  }

  public boolean isObjectRegistered(CDOID id)
  {
    return delegate.isObjectRegistered(id);
  }

  /**
   * @since 4.6
   */
  public CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    return delegate.getLockStates(ids);
  }

  /**
   * @since 4.6
   */
  public CDOLockState[] getLockStatesOfObjects(Collection<? extends CDOObject> objects)
  {
    return delegate.getLockStatesOfObjects(objects);
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout) throws InterruptedException
  {
    delegate.lockObjects(objects, lockType, timeout);
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive) throws InterruptedException
  {
    delegate.lockObjects(objects, lockType, timeout, recursive);
  }

  public Options options()
  {
    return delegate.options();
  }

  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return delegate.queryResources(folder, name, exactMatch);
  }

  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return delegate.queryResourcesAsync(folder, name, exactMatch);
  }

  /**
   * @since 4.3
   */
  public <T extends EObject> List<T> queryInstances(EClass type)
  {
    return delegate.queryInstances(type);
  }

  /**
   * @since 4.3
   */
  public <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type)
  {
    return delegate.queryInstancesAsync(type);
  }

  /**
   * @since 4.6
   */
  public <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type, boolean exact)
  {
    return delegate.queryInstancesAsync(type, exact);
  }

  /**
   * @since 4.0
   */
  public List<CDOObjectReference> queryXRefs(CDOObject targetObject, EReference... sourceReferences)
  {
    return delegate.queryXRefs(targetObject, sourceReferences);
  }

  public List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    return delegate.queryXRefs(targetObjects, sourceReferences);
  }

  public CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    return delegate.queryXRefsAsync(targetObjects, sourceReferences);
  }

  @Deprecated
  public int reload(CDOObject... objects)
  {
    return delegate.reload(objects);
  }

  public void removeObjectHandler(CDOObjectHandler handler)
  {
    delegate.removeObjectHandler(handler);
  }

  /**
   * @since 4.6
   */
  public void removeRegistrationHandler(CDORegistrationHandler handler)
  {
    delegate.removeRegistrationHandler(handler);
  }

  /**
   * @since 4.0
   */
  public void removeTransactionHandler(CDOTransactionHandlerBase handler)
  {
    delegate.removeTransactionHandler(handler);
  }

  /**
   * @since 4.6
   */
  public CDOChangeSetData merge(CDOBranch source, CDOMerger merger)
  {
    return delegate.merge(source, merger);
  }

  public CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger)
  {
    return delegate.merge(source, merger);
  }

  /**
   * @since 4.0
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOMerger merger)
  {
    return delegate.merge(source, sourceBase, merger);
  }

  /**
   * @since 4.6
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOBranchPoint targetBase, CDOMerger merger)
  {
    return delegate.merge(sourceBase, sourceBase, targetBase, merger);
  }

  /**
   * @since 4.0
   */
  public CDOChangeSetData compareRevisions(CDOBranchPoint source)
  {
    return delegate.compareRevisions(source);
  }

  /**
   * @since 4.2
   */
  public CDOCommitHistory getHistory()
  {
    return delegate.getHistory();
  }

  /**
   * @since 4.2
   */
  public CDOObjectHistory getHistory(CDOObject object)
  {
    return delegate.getHistory(object);
  }

  public CDOSavepoint setSavepoint()
  {
    return delegate.setSavepoint();
  }

  public void unlockObjects()
  {
    delegate.unlockObjects();
  }

  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    delegate.unlockObjects(objects, lockType);
  }

  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive)
  {
    delegate.unlockObjects(objects, lockType, recursive);
  }

  /**
   * @since 4.0
   * @deprecated Use {@link #enableDurableLocking()} instead or {@link #disableDurableLocking(boolean)}, respectively.
   */
  @Deprecated
  public String enableDurableLocking(boolean enable)
  {
    return delegate.enableDurableLocking(enable);
  }

  /**
   * @since 4.1
   */
  public String enableDurableLocking()
  {
    return delegate.enableDurableLocking();
  }

  /**
   * @since 4.1
   */
  public void disableDurableLocking(boolean releaseLocks)
  {
    delegate.disableDurableLocking(releaseLocks);
  }

  public boolean isReadOnly()
  {
    return delegate.isReadOnly();
  }

  /**
   * @since 4.5
   */
  public boolean isHistorical()
  {
    return delegate.isHistorical();
  }

  public CDOBranch getBranch()
  {
    return delegate.getBranch();
  }

  public boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    return delegate.setBranchPoint(branch, timeStamp);
  }

  /**
   * @since 4.4
   */
  public boolean setBranchPoint(CDOBranch branch, long timeStamp, IProgressMonitor monitor)
  {
    return delegate.setBranchPoint(branch, timeStamp, monitor);
  }

  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    return delegate.setBranchPoint(branchPoint);
  }

  /**
   * @since 4.4
   */
  public boolean setBranchPoint(CDOBranchPoint branchPoint, IProgressMonitor monitor)
  {
    return delegate.setBranchPoint(branchPoint, monitor);
  }

  public boolean setBranch(CDOBranch branch)
  {
    return delegate.setBranch(branch);
  }

  /**
   * @since 4.4
   */
  public boolean setBranch(CDOBranch branch, IProgressMonitor monitor)
  {
    return delegate.setBranch(branch, monitor);
  }

  public boolean setTimeStamp(long timeStamp)
  {
    return delegate.setTimeStamp(timeStamp);
  }

  /**
   * @since 4.4
   */
  public boolean setTimeStamp(long timeStamp, IProgressMonitor monitor)
  {
    return delegate.setTimeStamp(timeStamp, monitor);
  }

  public URIHandler getURIHandler()
  {
    return delegate.getURIHandler();
  }

  /**
   * @since 4.5
   */
  public CDOUnitManager getUnitManager()
  {
    return delegate.getUnitManager();
  }

  public String getCommitComment()
  {
    return delegate.getCommitComment();
  }

  public void setCommitComment(String comment)
  {
    delegate.setCommitComment(comment);
  }

  /**
   * @since 4.0
   */
  public void setCommittables(Set<? extends EObject> committables)
  {
    delegate.setCommittables(committables);
  }

  /**
   * @since 4.0
   */
  public Set<? extends EObject> getCommittables()
  {
    return delegate.getCommittables();
  }

  /**
   * @since 4.2
   */
  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }

  /**
   * @since 4.2
   */
  public CDOResourceNode[] getElements()
  {
    return delegate.getElements();
  }

  @Override
  public String toString()
  {
    return delegate.toString();
  }

  /**
   * @since 4.2
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  public static File createTempFile(CDOTransaction transaction) throws IOException
  {
    String prefix = "cdo_tx_" + transaction.getSession().getSessionID() + "_" + transaction.getViewID() + "__";
    return File.createTempFile(prefix, null);
  }
}
