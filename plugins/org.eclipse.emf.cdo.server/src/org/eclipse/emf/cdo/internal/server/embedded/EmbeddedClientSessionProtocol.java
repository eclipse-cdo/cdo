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
package org.eclipse.emf.cdo.internal.server.embedded;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalAudit;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public class EmbeddedClientSessionProtocol extends Lifecycle implements CDOSessionProtocol
{
  private EmbeddedClientSession session;

  // A separate session protocol instance is required because the getSession() methods are ambiguous!
  private EmbeddedServerSessionProtocol serverSessionProtocol;

  private InternalRepository repository;

  public EmbeddedClientSessionProtocol(EmbeddedClientSession session)
  {
    this.session = session;
  }

  public EmbeddedClientSession getSession()
  {
    return session;
  }

  public EmbeddedServerSessionProtocol getServerSessionProtocol()
  {
    return serverSessionProtocol;
  }

  public OpenSessionResult openSession(String repositoryName, boolean passiveUpdateEnabled)
  {
    repository = session.getRepository();
    if (!ObjectUtil.equals(repository.getName(), repositoryName))
    {
      throw new IllegalArgumentException("CDORepositoryInfo name does not match: " + repositoryName);
    }

    activate();
    return serverSessionProtocol.openSession(repository, passiveUpdateEnabled);
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public RepositoryTimeResult getRepositoryTime()
  {
    RepositoryTimeResult result = new RepositoryTimeResult();
    long timeStamp = System.currentTimeMillis();
    result.setRequested(timeStamp);
    result.setIndicated(timeStamp);
    result.setResponded(timeStamp);
    result.setConfirmed(timeStamp);
    return result;
  }

  public void setPassiveUpdate(Map<CDOID, CDOIDAndVersion> idAndVersions, int initialChunkSize,
      boolean passiveUpdateEnabled)
  {
    // serverSessionProtocol.getSession().setPassiveUpdateEnabled(passiveUpdateEnabled);
    // TODO: implement EmbeddedClientSessionProtocol.setPassiveUpdate(idAndVersions, initialChunkSize,
    // passiveUpdateEnabled)
    throw new UnsupportedOperationException();
  }

  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    throw new UnsupportedOperationException();
  }

  public InternalCDORevision loadRevision(CDOID id, int referenceChunk)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    throw new UnsupportedOperationException("Should not be called");
  }

  public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDOIDAndVersion> allRevisions, int initialChunkSize)
  {
    throw new UnsupportedOperationException();
  }

  public void openView(int viewID, CDOCommonView.Type viewType, long timeStamp)
  {
    switch (viewType)
    {
    case AUDIT:
      serverSessionProtocol.getSession().openAudit(viewID, timeStamp);
      break;

    case READONLY:
      serverSessionProtocol.getSession().openView(viewID);
      break;

    case TRANSACTION:
      serverSessionProtocol.getSession().openTransaction(viewID);
      break;
    }
  }

  public void closeView(int viewID)
  {
    InternalView view = serverSessionProtocol.getSession().getView(viewID);
    if (view != null)
    {
      view.close();
    }
  }

  public void changeSubscription(int viewID, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
  {
    throw new UnsupportedOperationException();
  }

  public List<Object> query(int viewID, AbstractQueryIterator<?> queryResult)
  {
    throw new UnsupportedOperationException();
  }

  public boolean cancelQuery(int queryID)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    throw new UnsupportedOperationException();
  }

  public void lockObjects(CDOView view, Map<CDOID, CDOIDAndVersion> objects, long timeout, LockType lockType)
      throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType)
  {
    throw new UnsupportedOperationException();
  }

  public boolean[] setAudit(int viewID, long timeStamp, List<InternalCDOObject> invalidObjects)
  {
    List<CDOID> ids = new ArrayList<CDOID>(invalidObjects.size());
    for (InternalCDOObject object : invalidObjects)
    {
      ids.add(object.cdoID());
    }

    InternalAudit audit = (InternalAudit)serverSessionProtocol.getSession().getView(viewID);
    return audit.setTimeStamp(timeStamp, ids);
  }

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext clientCommitContext, OMMonitor monitor)
  {
    InternalCDOTransaction clientTransaction = clientCommitContext.getTransaction();
    int viewID = clientTransaction.getViewID();

    InternalTransaction serverTransaction = (InternalTransaction)serverSessionProtocol.getSession().getView(viewID);
    InternalCommitContext serverCommitContext = serverTransaction.createCommitContext();
    serverCommitContext.preCommit();
    serverCommitContext.setAutoReleaseLocksEnabled(clientTransaction.options().isAutoReleaseLocksEnabled());

    List<CDOPackageUnit> npu = clientCommitContext.getNewPackageUnits();
    serverCommitContext.setNewPackageUnits(npu.toArray(new InternalCDOPackageUnit[npu.size()]));

    Collection<CDOResource> nr = clientCommitContext.getNewResources().values();
    Collection<CDOObject> no = clientCommitContext.getNewObjects().values();
    InternalCDORevision[] array = new InternalCDORevision[nr.size() + no.size()];
    int index = 0;
    for (CDOResource resource : nr)
    {
      array[index++] = (InternalCDORevision)resource.cdoRevision();
    }

    for (CDOObject object : no)
    {
      array[index++] = (InternalCDORevision)object.cdoRevision();
    }

    serverCommitContext.setNewObjects(array);

    Collection<CDORevisionDelta> rd = clientCommitContext.getRevisionDeltas().values();
    serverCommitContext.setDirtyObjectDeltas(rd.toArray(new InternalCDORevisionDelta[rd.size()]));

    Set<CDOID> detachedObjects = clientCommitContext.getDetachedObjects().keySet();
    serverCommitContext.setDetachedObjects(detachedObjects.toArray(new CDOID[detachedObjects.size()]));

    serverCommitContext.write(monitor.fork());
    if (serverCommitContext.getRollbackMessage() == null)
    {
      serverCommitContext.commit(monitor.fork());
    }
    else
    {
      monitor.worked();
    }

    CommitTransactionResult result = new CommitTransactionResult(clientCommitContext, serverCommitContext
        .getTimeStamp());
    for (Entry<CDOIDTemp, CDOID> entry : serverCommitContext.getIDMappings().entrySet())
    {
      result.addIDMapping(entry.getKey(), entry.getValue());
    }

    return result;
  }

  public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
  {
    throw new UnsupportedOperationException();
  }

  public void unsubscribeRemoteSessions()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    serverSessionProtocol = new EmbeddedServerSessionProtocol(this);
    serverSessionProtocol.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSessionProtocol.deactivate();
    serverSessionProtocol = null;
    super.doDeactivate();
  }

  public CDOAuthenticationResult handleAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    CDOAuthenticator authenticator = getSession().getConfiguration().getAuthenticator();
    if (authenticator == null)
    {
      throw new IllegalStateException("No authenticator configured"); //$NON-NLS-1$
    }

    CDOAuthenticationResult result = authenticator.authenticate(randomToken);
    if (result == null)
    {
      throw new SecurityException("Not authenticated"); //$NON-NLS-1$
    }

    String userID = result.getUserID();
    if (userID == null)
    {
      throw new SecurityException("No user ID"); //$NON-NLS-1$
    }

    byte[] cryptedToken = result.getCryptedToken();
    if (cryptedToken == null)
    {
      throw new SecurityException("No crypted token"); //$NON-NLS-1$
    }

    return result;
  }
}
