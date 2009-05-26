/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/226778
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Victor Roldan Betancort - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.RWLockManager.LockType;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.ExceptionHandler;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionImpl extends CDOSessionImpl implements org.eclipse.emf.cdo.net4j.CDOSession
{
  private CDOSessionProtocol protocol;

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      CDONet4jSessionImpl.this.deactivate();
    }
  };

  public CDONet4jSessionImpl(ExceptionHandler exceptionHandler)
  {
    if (exceptionHandler == null)
    {
      protocol = createProtocol();
    }
    else
    {
      protocol = new DelegatingSessionProtocol(exceptionHandler);
    }
  }

  public CDOSessionProtocol getSessionProtocol()
  {
    return protocol;
  }

  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  protected CDOClientProtocol createProtocol()
  {
    CDOClientProtocol protocol = new CDOClientProtocol();
    protocol.setInfraStructure(this);
    return protocol;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    EventUtil.addListener(protocol, protocolListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    EventUtil.removeListener(protocol, protocolListener);
    super.doDeactivate();
    LifecycleUtil.deactivate(protocol);
    protocol = null;
  }

  /**
   * @author Eike Stepper
   */
  private class DelegatingSessionProtocol extends Lifecycle implements CDOSessionProtocol
  {
    private ExceptionHandler exceptionHandler;

    private CDOClientProtocol delegate;

    @ExcludeFromDump
    private IListener delegateListener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        DelegatingSessionProtocol.this.deactivate();
      }
    };

    public DelegatingSessionProtocol(ExceptionHandler exceptionHandler)
    {
      this.exceptionHandler = exceptionHandler;
      delegate = createProtocol();
      activate();
    }

    public CDOClientProtocol getDelegate()
    {
      return delegate;
    }

    public boolean cancelQuery(int queryId)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.cancelQuery(queryId);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.changeSubscription(viewId, cdoIDs, subscribeMode, clear);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void closeView(int viewId)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.closeView(viewId);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransaction(commitContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionCancel(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionPhase1(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionPhase2(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.commitTransactionPhase3(xaContext, monitor);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.getRemoteSessions(manager, subscribe);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public RepositoryTimeResult getRepositoryTime()
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.getRepositoryTime();
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.isObjectLocked(view, object, lockType, byOthers);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
        int fromIndex, int toIndex)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadChunk(revision, feature, accessIndex, fetchIndex, fromIndex, toIndex);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void loadLibraries(Set<String> missingLibraries, File cacheFolder)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.loadLibraries(missingLibraries, cacheFolder);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public EPackage[] loadPackages(CDOPackageUnit packageUnit)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadPackages(packageUnit);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisionByVersion(id, referenceChunk, version);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisions(ids, referenceChunk);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.loadRevisionsByTime(ids, referenceChunk, timeStamp);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void lockObjects(CDOView view, Map<CDOID, CDOIDAndVersion> objects, long timeout, LockType lockType)
        throws InterruptedException
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.lockObjects(view, objects, timeout, lockType);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public OpenSessionResult openSession(String repositoryName, boolean passiveUpdateEnabled)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.openSession(repositoryName, passiveUpdateEnabled);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void openView(int viewId, byte protocolViewType, long timeStamp)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.openView(viewId, protocolViewType, timeStamp);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<Object> query(int viewID, AbstractQueryIterator<?> queryResult)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.query(viewID, queryResult);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public boolean[] setAudit(int viewId, long timeStamp, List<InternalCDOObject> invalidObjects)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.setAudit(viewId, timeStamp, invalidObjects);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void setPassiveUpdate(Map<CDOID, CDOIDAndVersion> idAndVersions, int initialChunkSize,
        boolean passiveUpdateEnabled)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.setPassiveUpdate(idAndVersions, initialChunkSize, passiveUpdateEnabled);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDOIDAndVersion> allRevisions, int initialChunkSize)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.syncRevisions(allRevisions, initialChunkSize);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.unlockObjects(view, objects, lockType);
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public void unsubscribeRemoteSessions()
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          delegate.unsubscribeRemoteSessions();
          return;
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    public List<InternalCDORevision> verifyRevision(List<InternalCDORevision> revisions)
    {
      int attempt = 0;
      for (;;)
      {
        try
        {
          return delegate.verifyRevision(revisions);
        }
        catch (Exception ex)
        {
          handleException(++attempt, ex);
        }
      }
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();
      EventUtil.addListener(delegate, delegateListener);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      EventUtil.removeListener(delegate, delegateListener);
      LifecycleUtil.deactivate(delegate);
      delegate = null;
      super.doDeactivate();
    }

    private void handleException(int attempt, Exception exception)
    {
      try
      {
        exceptionHandler.handleException(attempt, exception);
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  protected class OptionsImpl extends org.eclipse.emf.internal.cdo.session.CDOSessionImpl.OptionsImpl implements
      org.eclipse.emf.cdo.net4j.CDOSession.Options
  {
    public OptionsImpl()
    {
    }

    public CDOClientProtocol getProtocol()
    {
      if (protocol instanceof DelegatingSessionProtocol)
      {
        return ((DelegatingSessionProtocol)protocol).getDelegate();
      }

      return (CDOClientProtocol)protocol;
    }
  }

}
