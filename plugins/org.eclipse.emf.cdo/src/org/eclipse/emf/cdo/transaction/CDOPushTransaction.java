/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.EObject;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class CDOPushTransaction implements CDOTransaction
{
  private CDOTransaction delegate;

  private File file;

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

    if (file.isDirectory())
    {
      throw new IllegalArgumentException("Not a file: " + file.getAbsolutePath());
    }

    OM.LOG.info("Using " + file.getAbsolutePath() + " for push transaction "
        + delegate.getSession().getRepositoryInfo().getName() + ":" + //
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
  }

  public CDOTransaction getDelegate()
  {
    return delegate;
  }

  public File getFile()
  {
    return file;
  }

  public void commit() throws TransactionException
  {
    commit(null);
  }

  public void commit(IProgressMonitor progressMonitor) throws TransactionException
  {
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(file);
      delegate.exportChanges(out);
    }
    catch (IOException ex)
    {
      throw new TransactionException("A problem occured while exporting changes to " + file.getAbsolutePath(), ex);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  public void push() throws TransactionException
  {
    push(null);
  }

  public void push(IProgressMonitor progressMonitor) throws TransactionException
  {
    delegate.commit(progressMonitor);
  }

  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException
  {
    return delegate.exportChanges(out);
  }

  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException
  {
    return delegate.importChanges(in, reconstructSavepoints);
  }

  public void addListener(IListener listener)
  {
    delegate.addListener(listener);
  }

  public void addObjectHandler(CDOObjectHandler handler)
  {
    delegate.addObjectHandler(handler);
  }

  public void addTransactionHandler(CDOTransactionHandler handler)
  {
    delegate.addTransactionHandler(handler);
  }

  public void close()
  {
    delegate.close();
  }

  public CDOQuery createQuery(String language, String queryString)
  {
    return delegate.createQuery(language, queryString);
  }

  public CDOResource createResource(String path)
  {
    return delegate.createResource(path);
  }

  public Set<CDOObject> getConflicts()
  {
    return delegate.getConflicts();
  }

  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return delegate.getDetachedObjects();
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return delegate.getDirtyObjects();
  }

  public long getLastCommitTime()
  {
    return delegate.getLastCommitTime();
  }

  public CDOSavepoint getLastSavepoint()
  {
    return delegate.getLastSavepoint();
  }

  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  public ReentrantLock getLock()
  {
    return delegate.getLock();
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return delegate.getNewObjects();
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    return delegate.getNewResources();
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

  public CDOResource getOrCreateResource(String path)
  {
    return delegate.getOrCreateResource(path);
  }

  public CDOResource getResource(String path, boolean loadOnDemand)
  {
    return delegate.getResource(path, loadOnDemand);
  }

  public CDOResource getResource(String path)
  {
    return delegate.getResource(path);
  }

  public CDOResourceNode getResourceNode(String path)
  {
    return delegate.getResourceNode(path);
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

  public long getTimeStamp()
  {
    return delegate.getTimeStamp();
  }

  public CDOTransactionHandler[] getTransactionHandlers()
  {
    return delegate.getTransactionHandlers();
  }

  public int getViewID()
  {
    return delegate.getViewID();
  }

  public CDOViewSet getViewSet()
  {
    return delegate.getViewSet();
  }

  public Type getViewType()
  {
    return delegate.getViewType();
  }

  public boolean hasConflict()
  {
    return delegate.hasConflict();
  }

  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  public boolean hasResource(String path)
  {
    return delegate.hasResource(path);
  }

  public boolean isClosed()
  {
    return delegate.isClosed();
  }

  public boolean isDirty()
  {
    return delegate.isDirty();
  }

  public boolean isObjectRegistered(CDOID id)
  {
    return delegate.isObjectRegistered(id);
  }

  public void lockObjects(Collection<? extends CDOObject> objects, LockType lockType, long timeout)
      throws InterruptedException
  {
    delegate.lockObjects(objects, lockType, timeout);
  }

  public Options options()
  {
    return delegate.options();
  }

  public List<CDOResourceNode> queryResources(CDOResourceFolder folder, String name, boolean exactMatch)
  {
    return delegate.queryResources(folder, name, exactMatch);
  }

  public CloseableIterator<CDOResourceNode> queryResourcesAsync(CDOResourceFolder folder, String name,
      boolean exactMatch)
  {
    return delegate.queryResourcesAsync(folder, name, exactMatch);
  }

  public int reload(CDOObject... objects)
  {
    return delegate.reload(objects);
  }

  public void removeListener(IListener listener)
  {
    delegate.removeListener(listener);
  }

  public void removeObjectHandler(CDOObjectHandler handler)
  {
    delegate.removeObjectHandler(handler);
  }

  public void removeTransactionHandler(CDOTransactionHandler handler)
  {
    delegate.removeTransactionHandler(handler);
  }

  public void resolveConflicts(CDOConflictResolver... resolver)
  {
    delegate.resolveConflicts(resolver);
  }

  public void rollback()
  {
    delegate.rollback();
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

  @Deprecated
  public URIHandler getURIHandler()
  {
    return delegate.getURIHandler();
  }

  @Deprecated
  public void addHandler(CDOTransactionHandler handler)
  {
    delegate.addHandler(handler);
  }

  @Deprecated
  public void removeHandler(CDOTransactionHandler handler)
  {
    delegate.removeHandler(handler);
  }

  @Deprecated
  public CDOTransactionHandler[] getHandlers()
  {
    return delegate.getHandlers();
  }

  public static File createTempFile(CDOTransaction transaction) throws IOException
  {
    String prefix = "cdo_tx_" + transaction.getSession().getSessionID() + "_" + transaction.getViewID() + "__";
    return File.createTempFile(prefix, null);
  }
}
