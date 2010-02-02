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
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommit;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 * @since 3.0
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

    dirty = delegate.isDirty();
    delegate.addTransactionHandler(delegateHandler);

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

  public boolean isDirty()
  {
    return dirty;
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

          public Type getType()
          {
            return Type.COMMITTED;
          }

          public Map<CDOIDTemp, CDOID> getIDMappings()
          {
            return Collections.emptyMap();
          }
        });
      }
    }
  }

  public CDOCommit commit() throws TransactionException
  {
    return commit(null);
  }

  public CDOCommit commit(IProgressMonitor progressMonitor) throws TransactionException
  {
    OutputStream out = null;

    try
    {
      out = new FileOutputStream(file);
      delegate.exportChanges(out);
      setDirty(false);
      return null;
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

  public void rollback()
  {
    throw new UnsupportedOperationException("Rollback not supported for push transactions");
  }

  public void push() throws TransactionException
  {
    push(null);
  }

  public void push(IProgressMonitor progressMonitor) throws TransactionException
  {
    delegate.commit(progressMonitor);
    file.delete();
  }

  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException
  {
    return delegate.exportChanges(out);
  }

  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException
  {
    return delegate.importChanges(in, reconstructSavepoints);
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

  public void addTransactionHandler(CDOTransactionHandler handler)
  {
    delegate.addTransactionHandler(handler);
  }

  public void close()
  {
    delegate.removeTransactionHandler(delegateHandler);
    delegate.close();
  }

  public int compareTo(CDOBranchPoint o)
  {
    return delegate.compareTo(o);
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

  public CDOSavepoint getLastSavepoint()
  {
    return delegate.getLastSavepoint();
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

  public static File createTempFile(CDOTransaction transaction) throws IOException
  {
    String prefix = "cdo_tx_" + transaction.getSession().getSessionID() + "_" + transaction.getViewID() + "__";
    return File.createTempFile(prefix, null);
  }

  public boolean isReadOnly()
  {
    return delegate.isReadOnly();
  }

  public CDOBranch getBranch()
  {
    return delegate.getBranch();
  }

  public boolean setBranchPoint(CDOBranch branch, long timeStamp)
  {
    return delegate.setBranchPoint(branch, timeStamp);
  }

  public boolean setBranch(CDOBranch branch)
  {
    return delegate.setBranch(branch);
  }

  public boolean setTimeStamp(long timeStamp)
  {
    return delegate.setTimeStamp(timeStamp);
  }

  public URIHandler getURIHandler()
  {
    return delegate.getURIHandler();
  }
}
