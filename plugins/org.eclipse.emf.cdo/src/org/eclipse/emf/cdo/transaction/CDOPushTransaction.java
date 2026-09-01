/*
 * Copyright (c) 2009-2016, 2018-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
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
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.CriticalSection;
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
import java.util.function.Consumer;

/**
 * A {@link CDOTransaction transaction} that persists changes to the object graph locally on commit and can later load
 * these changes and push them to the {@link CDOCommonRepository repository}.
 *
 * @author Eike Stepper
 * @since 3.0
 * @deprecated As of 4.30, use {@link CDOFileTransaction}.
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@Deprecated
public class CDOPushTransaction extends Notifier implements CDOTransaction
{
  private CDOTransaction delegate;

  private File file;

  private boolean dirty;

  private CDOTransactionHandler delegateHandler = new CDOTransactionHandler()
  {
    @Override
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      setDirty(true);
    }

    @Override
    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      setDirty(true);
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      setDirty(true);
    }

    @Override
    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
    }
  };

  private final IRegistry<String, Object> properties = new HashMapRegistry<>()
  {
    @Override
    public void setAutoCommit(boolean autoCommit)
    {
      throw new UnsupportedOperationException();
    }
  };

  @Deprecated
  public CDOPushTransaction(CDOTransaction delegate) throws IOException
  {
    this(delegate, createTempFile(delegate));
  }

  @Deprecated
  public CDOPushTransaction(CDOTransaction delegate, File file) throws IOException
  {
    this(delegate, file, true);
  }

  @Deprecated
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

  @Deprecated
  public CDOTransaction getDelegate()
  {
    return delegate;
  }

  @Deprecated
  public File getFile()
  {
    return file;
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  @Deprecated
  @Override
  public boolean isDirty()
  {
    return dirty || delegate.isDirty();
  }

  @Deprecated
  protected void setDirty(boolean dirty)
  {
    if (this.dirty != dirty)
    {
      this.dirty = dirty;
      if (dirty)
      {
        fireEvent(new CDOTransactionStartedEvent()
        {
          @Override
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
          @Override
          public CDOView getSource()
          {
            return CDOPushTransaction.this;
          }

          @Override
          @Deprecated
          public Type getType()
          {
            return Type.COMMITTED;
          }

          @Override
          public Cause getCause()
          {
            return Cause.COMMITTED;
          }

          @Override
          public Map<CDOID, CDOID> getIDMappings()
          {
            return Collections.emptyMap();
          }
        });
      }
    }
  }

  @Deprecated
  @Override
  public CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  @Deprecated
  @Override
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

  @Override
  @Deprecated
  public <T> CommitResult<T> commit(Callable<T> callable, org.eclipse.net4j.util.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    return delegate.commit(callable, retry, monitor);
  }

  @Deprecated
  @Override
  public <T> CommitResult<T> commit(Callable<T> callable, java.util.function.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception
  {
    return delegate.commit(callable, retry, monitor);
  }

  @Deprecated
  @Override
  public <T> CommitResult<T> commit(Callable<T> callable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException, Exception
  {
    return delegate.commit(callable, attempts, monitor);
  }

  @Override
  @Deprecated
  public CDOCommitInfo commit(Runnable runnable, org.eclipse.net4j.util.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    return delegate.commit(runnable, retry, monitor);
  }

  @Deprecated
  @Override
  public CDOCommitInfo commit(Runnable runnable, java.util.function.Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException
  {
    return delegate.commit(runnable, retry, monitor);
  }

  @Deprecated
  @Override
  public CDOCommitInfo commit(Runnable runnable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException
  {
    return delegate.commit(runnable, attempts, monitor);
  }

  @Deprecated
  @Override
  public CDOCommitInfo commitAndClose(IProgressMonitor monitor, boolean keepOpenAfterCommitProblem) throws CommitException
  {
    return delegate.commitAndClose(monitor, keepOpenAfterCommitProblem);
  }

  @Deprecated
  @Override
  public void rollback()
  {
    throw new UnsupportedOperationException("Rollback not supported for push transactions");
  }

  @Deprecated
  public void push() throws CommitException
  {
    push(null);
  }

  @Deprecated
  public void push(IProgressMonitor progressMonitor) throws CommitException
  {
    delegate.commit(progressMonitor);
    file.delete();
    setDirty(false);
  }

  @Deprecated
  @Override
  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException
  {
    return delegate.exportChanges(out);
  }

  @Deprecated
  @Override
  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException
  {
    return delegate.importChanges(in, reconstructSavepoints);
  }

  @Deprecated
  @Override
  public long getLastCommitTime()
  {
    return delegate.getLastCommitTime();
  }

  @Deprecated
  @Override
  public void addListener(IListener listener)
  {
    super.addListener(listener);
    delegate.addListener(listener);
  }

  @Deprecated
  @Override
  public void removeListener(IListener listener)
  {
    super.removeListener(listener);
    delegate.removeListener(listener);
  }

  @Deprecated
  @Override
  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  @Deprecated
  @Override
  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  @Deprecated
  @Override
  public void addObjectHandler(CDOObjectHandler handler)
  {
    delegate.addObjectHandler(handler);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public void addRegistrationHandler(CDORegistrationHandler handler)
  {
    delegate.addRegistrationHandler(handler);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public void addTransactionHandler(CDOTransactionHandlerBase handler)
  {
    delegate.addTransactionHandler(handler);
  }

  @Deprecated
  @Override
  public void close()
  {
    delegate.removeTransactionHandler(delegateHandler);
    delegate.close();
  }

  @Deprecated
  @Override
  public CDOQuery createQuery(String language, String queryString)
  {
    return createQuery(language, queryString, null, false);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOQuery createQuery(String language, String queryString, Object context)
  {
    return createQuery(language, queryString, context, false);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOQuery createQuery(String language, String queryString, boolean considerDirtyState)
  {
    return createQuery(language, queryString, null, considerDirtyState);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOQuery createQuery(String language, String queryString, Object context, boolean considerDirtyState)
  {
    return delegate.createQuery(language, queryString, context, considerDirtyState);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOResourceFolder createResourceFolder(String path)
  {
    return delegate.createResourceFolder(path);
  }

  @Deprecated
  @Override
  public CDOResource createResource(String path)
  {
    return delegate.createResource(path);
  }

  @Override
  @Deprecated
  public boolean isLegacyModeEnabled()
  {
    return delegate.isLegacyModeEnabled();
  }

  @Deprecated
  @Override
  public long getLastUpdateTime()
  {
    return delegate.getLastUpdateTime();
  }

  @Deprecated
  @Override
  public void waitForUpdate(long updateTime)
  {
    delegate.waitForUpdate(updateTime);
  }

  @Deprecated
  @Override
  public boolean waitForUpdate(long updateTime, long timeoutMillis)
  {
    return delegate.waitForUpdate(updateTime, timeoutMillis);
  }

  /**
   * @since 4.3
   */
  @Deprecated
  @Override
  public boolean runAfterUpdate(long updateTime, Runnable runnable)
  {
    return delegate.runAfterUpdate(updateTime, runnable);
  }

  @Deprecated
  @Override
  public Set<CDOObject> getConflicts()
  {
    return delegate.getConflicts();
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOChangeSetData getChangeSetData()
  {
    return delegate.getChangeSetData();
  }

  @Deprecated
  @Override
  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return delegate.getDetachedObjects();
  }

  @Deprecated
  @Override
  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return delegate.getDirtyObjects();
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDORevision getRevision(CDOID id)
  {
    return delegate.getRevision(id);
  }

  /**
   * @since 4.1
   */
  @Deprecated
  @Override
  public CDOSavepoint getFirstSavepoint()
  {
    return delegate.getFirstSavepoint();
  }

  @Deprecated
  @Override
  public CDOSavepoint getLastSavepoint()
  {
    return delegate.getLastSavepoint();
  }

  /**
   * @since 4.0
   * @deprecated
   */
  @Override
  @Deprecated
  public boolean isInvalidationRunnerActive()
  {
    return isInvalidating();
  }

  /**
   * @since 4.7
   */
  @Deprecated
  @Override
  public boolean isInvalidating()
  {
    return delegate.isInvalidating();
  }

  @Deprecated
  @Override
  public Map<CDOID, CDOObject> getNewObjects()
  {
    return delegate.getNewObjects();
  }

  /**
   * @since 4.13
   */
  @Deprecated
  @Override
  public Map<CDOID, CDOObject> getObjects(Collection<CDOID> ids)
  {
    return delegate.getObjects(ids);
  }

  @Deprecated
  @Override
  public CDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    return delegate.getObject(id, loadOnDemand);
  }

  @Deprecated
  @Override
  public CDOObject getObject(CDOID id)
  {
    return delegate.getObject(id);
  }

  @Deprecated
  @Override
  public <T extends EObject> T getObject(T objectFromDifferentView)
  {
    return delegate.getObject(objectFromDifferentView);
  }

  @Deprecated
  @Override
  public CDOObjectHandler[] getObjectHandlers()
  {
    return delegate.getObjectHandlers();
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public CDORegistrationHandler[] getRegistrationHandlers()
  {
    return delegate.getRegistrationHandlers();
  }

  @Deprecated
  @Override
  public CDOResource getOrCreateResource(String path)
  {
    return delegate.getOrCreateResource(path);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOResourceFolder getOrCreateResourceFolder(String path)
  {
    return delegate.getOrCreateResourceFolder(path);
  }

  @Deprecated
  @Override
  public CDOResource getResource(String path, boolean loadOnDemand) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResource(path, loadOnDemand);
  }

  @Deprecated
  @Override
  public CDOResource getResource(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResource(path);
  }

  @Deprecated
  @Override
  public CDOResourceNode getResourceNode(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResourceNode(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOTextResource createTextResource(String path)
  {
    return delegate.createTextResource(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOTextResource getOrCreateTextResource(String path)
  {
    return delegate.getOrCreateTextResource(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOBinaryResource createBinaryResource(String path)
  {
    return delegate.createBinaryResource(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOBinaryResource getOrCreateBinaryResource(String path)
  {
    return delegate.getOrCreateBinaryResource(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOTextResource getTextResource(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getTextResource(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOBinaryResource getBinaryResource(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getBinaryResource(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOResourceFolder getResourceFolder(String path) throws CDOResourceNodeNotFoundException
  {
    return delegate.getResourceFolder(path);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public void setResourcePathCache(Map<String, CDOID> resourcePathCache)
  {
    delegate.setResourcePathCache(resourcePathCache);
  }

  @Deprecated
  @Override
  public ResourceSet getResourceSet()
  {
    return delegate.getResourceSet();
  }

  @Deprecated
  @Override
  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return delegate.getRevisionDeltas();
  }

  @Deprecated
  @Override
  public CDOResource getRootResource()
  {
    return delegate.getRootResource();
  }

  @Deprecated
  @Override
  public CDOSession getSession()
  {
    return delegate.getSession();
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public CDOViewProvider getProvider()
  {
    return delegate.getProvider();
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public URI createResourceURI(String path)
  {
    return delegate.createResourceURI(path);
  }

  @Deprecated
  @Override
  public long getTimeStamp()
  {
    return delegate.getTimeStamp();
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public String getDurableLockingID()
  {
    return delegate.getDurableLockingID();
  }

  @Deprecated
  @Override
  public CDOTransactionHandler[] getTransactionHandlers()
  {
    return delegate.getTransactionHandlers();
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOTransactionHandler1[] getTransactionHandlers1()
  {
    return delegate.getTransactionHandlers1();
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOTransactionHandler2[] getTransactionHandlers2()
  {
    return delegate.getTransactionHandlers2();
  }

  /**
   * @since 4.1
   */
  @Deprecated
  @Override
  public int getSessionID()
  {
    return delegate.getSessionID();
  }

  /**
   * @since 4.1
   */
  @Deprecated
  @Override
  public boolean isDurableView()
  {
    return delegate.isDurableView();
  }

  /**
   * @since 4.15
   */
  @Deprecated
  @Override
  public CDOLockOwner getLockOwner()
  {
    return delegate.getLockOwner();
  }

  @Deprecated
  @Override
  public URI getURI()
  {
    return delegate.getURI();
  }

  @Deprecated
  @Override
  public int getViewID()
  {
    return delegate.getViewID();
  }

  @Deprecated
  @Override
  public CDOViewSet getViewSet()
  {
    return delegate.getViewSet();
  }

  @Deprecated
  @Override
  public CriticalSection sync()
  {
    return delegate.sync();
  }

  /**
   * @since 4.5
   */
  @Override
  @Deprecated
  public Lock getViewLock()
  {
    return delegate.getViewLock();
  }

  /**
   * @since 4.5
   */
  @Override
  @Deprecated
  public void syncExec(Runnable runnable)
  {
    delegate.syncExec(runnable);
  }

  /**
   * @since 4.5
   */
  @Override
  @Deprecated
  public <V> V syncExec(Callable<V> callable) throws Exception
  {
    return delegate.syncExec(callable);
  }

  @Deprecated
  @Override
  public boolean hasConflict()
  {
    return delegate.hasConflict();
  }

  @Deprecated
  @Override
  public boolean hasResource(String path)
  {
    return delegate.hasResource(path);
  }

  @Deprecated
  @Override
  public boolean isClosed()
  {
    return delegate.isClosed();
  }

  @Deprecated
  @Override
  public boolean isObjectRegistered(CDOID id)
  {
    return delegate.isObjectRegistered(id);
  }

  /**
   * @since 4.12
   * @deprecated
   */
  @Deprecated
  @Override
  public void refreshLockStates(Consumer<CDOLockState> consumer)
  {
    delegate.refreshLockStates(consumer);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public CDOLockState[] getLockStates(Collection<CDOID> ids)
  {
    return delegate.getLockStates(ids);
  }

  @Deprecated
  @Override
  public CDOLockState[] getLockStates(Collection<CDOID> ids, boolean loadOnDemand)
  {
    return delegate.getLockStates(ids, loadOnDemand);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public CDOLockState[] getLockStatesOfObjects(Collection<? extends CDOObject> objects)
  {
    return delegate.getLockStatesOfObjects(objects);
  }

  @Deprecated
  @Override
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout) throws InterruptedException
  {
    delegate.lockObjects(objects, lockType, timeout);
  }

  @Deprecated
  @Override
  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout, boolean recursive) throws InterruptedException
  {
    delegate.lockObjects(objects, lockType, timeout, recursive);
  }

  @Deprecated
  @Override
  public Options options()
  {
    return delegate.options();
  }

  @Deprecated
  @Override
  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return delegate.queryResources(folder, name, exactMatch);
  }

  @Deprecated
  @Override
  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return delegate.queryResourcesAsync(folder, name, exactMatch);
  }

  /**
   * @since 4.3
   */
  @Deprecated
  @Override
  public <T extends EObject> List<T> queryInstances(EClass type)
  {
    return delegate.queryInstances(type);
  }

  /**
   * @since 4.3
   */
  @Deprecated
  @Override
  public <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type)
  {
    return delegate.queryInstancesAsync(type);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public <T extends EObject> CloseableIterator<T> queryInstancesAsync(EClass type, boolean exact)
  {
    return delegate.queryInstancesAsync(type, exact);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public List<CDOObjectReference> queryXRefs(CDOObject targetObject, EReference... sourceReferences)
  {
    return delegate.queryXRefs(targetObject, sourceReferences);
  }

  @Deprecated
  @Override
  public List<CDOObjectReference> queryXRefs(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    return delegate.queryXRefs(targetObjects, sourceReferences);
  }

  @Deprecated
  @Override
  public CloseableIterator<CDOObjectReference> queryXRefsAsync(Set<CDOObject> targetObjects, EReference... sourceReferences)
  {
    return delegate.queryXRefsAsync(targetObjects, sourceReferences);
  }

  @Override
  @Deprecated
  public int reload(CDOObject... objects)
  {
    return delegate.reload(objects);
  }

  @Deprecated
  @Override
  public void removeObjectHandler(CDOObjectHandler handler)
  {
    delegate.removeObjectHandler(handler);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public void removeRegistrationHandler(CDORegistrationHandler handler)
  {
    delegate.removeRegistrationHandler(handler);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public void removeTransactionHandler(CDOTransactionHandlerBase handler)
  {
    delegate.removeTransactionHandler(handler);
  }

  /**
   * @since 4.15
   */
  @Deprecated
  @Override
  public CDOChangeSetData revertTo(CDOBranchPoint branchPoint)
  {
    return delegate.revertTo(branchPoint);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public CDOChangeSetData merge(CDOBranch source, CDOMerger merger)
  {
    return delegate.merge(source, merger);
  }

  @Deprecated
  @Override
  public CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger)
  {
    return delegate.merge(source, merger);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOMerger merger)
  {
    return delegate.merge(source, sourceBase, merger);
  }

  /**
   * @since 4.6
   */
  @Deprecated
  @Override
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOBranchPoint targetBase, CDOMerger merger)
  {
    return delegate.merge(sourceBase, sourceBase, targetBase, merger);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public CDOChangeSetData compareRevisions(CDOBranchPoint source)
  {
    return delegate.compareRevisions(source);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOCommitHistory getHistory()
  {
    return delegate.getHistory();
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOObjectHistory getHistory(CDOObject object)
  {
    return delegate.getHistory(object);
  }

  @Deprecated
  @Override
  public CDOSavepoint setSavepoint()
  {
    return delegate.setSavepoint();
  }

  @Deprecated
  @Override
  public void unlockObjects()
  {
    delegate.unlockObjects();
  }

  @Deprecated
  @Override
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType)
  {
    delegate.unlockObjects(objects, lockType);
  }

  @Deprecated
  @Override
  public void unlockObjects(Collection<? extends CDOObject> objects, LockType lockType, boolean recursive)
  {
    delegate.unlockObjects(objects, lockType, recursive);
  }

  /**
   * @since 4.0
   * @deprecated Use {@link #enableDurableLocking()} instead or {@link #disableDurableLocking(boolean)}, respectively.
   */
  @Override
  @Deprecated
  public String enableDurableLocking(boolean enable)
  {
    return delegate.enableDurableLocking(enable);
  }

  /**
   * @since 4.1
   */
  @Deprecated
  @Override
  public String enableDurableLocking()
  {
    return delegate.enableDurableLocking();
  }

  /**
   * @since 4.1
   */
  @Deprecated
  @Override
  public void disableDurableLocking(boolean releaseLocks)
  {
    delegate.disableDurableLocking(releaseLocks);
  }

  @Deprecated
  @Override
  public boolean isReadOnly()
  {
    return delegate.isReadOnly();
  }

  /**
   * @since 4.5
   */
  @Deprecated
  @Override
  public boolean isHistorical()
  {
    return delegate.isHistorical();
  }

  @Deprecated
  @Override
  public CDOBranch getBranch()
  {
    return delegate.getBranch();
  }

  @Deprecated
  @Override
  public boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    return delegate.setBranchPoint(branch, timeStamp);
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public boolean setBranchPoint(CDOBranch branch, long timeStamp, IProgressMonitor monitor)
  {
    return delegate.setBranchPoint(branch, timeStamp, monitor);
  }

  @Deprecated
  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    return delegate.setBranchPoint(branchPoint);
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public boolean setBranchPoint(CDOBranchPoint branchPoint, IProgressMonitor monitor)
  {
    return delegate.setBranchPoint(branchPoint, monitor);
  }

  @Deprecated
  @Override
  public boolean setBranch(CDOBranch branch)
  {
    return delegate.setBranch(branch);
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public boolean setBranch(CDOBranch branch, IProgressMonitor monitor)
  {
    return delegate.setBranch(branch, monitor);
  }

  @Deprecated
  @Override
  public boolean setTimeStamp(long timeStamp)
  {
    return delegate.setTimeStamp(timeStamp);
  }

  /**
   * @since 4.4
   */
  @Deprecated
  @Override
  public boolean setTimeStamp(long timeStamp, IProgressMonitor monitor)
  {
    return delegate.setTimeStamp(timeStamp, monitor);
  }

  @Deprecated
  @Override
  public URIHandler getURIHandler()
  {
    return delegate.getURIHandler();
  }

  /**
   * @since 4.5
   */
  @Deprecated
  @Override
  public CDOUnitManager getUnitManager()
  {
    return delegate.getUnitManager();
  }

  @Deprecated
  @Override
  public String getCommitComment()
  {
    return delegate.getCommitComment();
  }

  @Deprecated
  @Override
  public void setCommitComment(String comment)
  {
    delegate.setCommitComment(comment);
  }

  @Deprecated
  @Override
  public String getCommitProperty(String key)
  {
    return delegate.getCommitProperty(key);
  }

  @Deprecated
  @Override
  public String setCommitProperty(String key, String value)
  {
    return delegate.setCommitProperty(key, value);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public void setCommittables(Set<? extends EObject> committables)
  {
    delegate.setCommittables(committables);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public Set<? extends EObject> getCommittables()
  {
    return delegate.getCommittables();
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  public CDOResourceNode[] getElements()
  {
    return delegate.getElements();
  }

  @Deprecated
  @Override
  public String toString()
  {
    return delegate.toString();
  }

  @Deprecated
  @Override
  public CDOTransactionScope openScope()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public CDOTransactionScope getOutermostScope()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public CDOTransactionScope getInnermostScope()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public List<CDOTransactionScope> getScopes()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @since 4.2
   */
  @Deprecated
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Deprecated
  public static File createTempFile(CDOTransaction transaction) throws IOException
  {
    String prefix = "cdo_tx_" + transaction.getSession().getSessionID() + "_" + transaction.getViewID() + "__";
    return File.createTempFile(prefix, null);
  }
}
