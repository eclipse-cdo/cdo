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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
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
import org.eclipse.emf.spi.cdo.InternalCDOSession;
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
      throw new IllegalArgumentException("Repository name does not match: " + repositoryName);
    }

    activate();
    return serverSessionProtocol.openSession(repository, passiveUpdateEnabled);
  }

  public void loadLibraries(Set<String> missingLibraries, File cacheFolder)
  {
    throw new UnsupportedOperationException("Should not be called");
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
    result.setRequested(timeStamp);
    result.setIndicated(timeStamp);
    return result;
  }

  public void setPassiveUpdate(Map<CDOID, CDOIDAndVersion> idAndVersions, int initialChunkSize,
      boolean passiveUpdateEnabled)
  {
  }

  public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    return null;
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return null;
  }

  public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    return null;
  }

  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    return null;
  }

  public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDOIDAndVersion> allRevisions, int initialChunkSize)
  {
    return null;
  }

  public void openView(int viewId, byte protocolViewType, long timeStamp)
  {
  }

  public void closeView(int viewId)
  {
  }

  public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
  {
  }

  public List<Object> query(int viewID, AbstractQueryIterator<?> queryResult)
  {
    return null;
  }

  public boolean cancelQuery(int queryId)
  {
    return false;
  }

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    return false;
  }

  public void lockObjects(CDOView view, Map<CDOID, CDOIDAndVersion> objects, long timeout, LockType lockType)
      throws InterruptedException
  {
  }

  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType)
  {
  }

  public boolean[] setAudit(int viewId, long timeStamp, List<InternalCDOObject> invalidObjects)
  {
    return null;
  }

  public List<InternalCDORevision> verifyRevision(List<InternalCDORevision> revisions)
  {
    return null;
  }

  public CommitTransactionResult commitTransaction(InternalCDOCommitContext commitContext, OMMonitor monitor)
  {
    return null;
  }

  public CommitTransactionResult commitTransactionCancel(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return null;
  }

  public CommitTransactionResult commitTransactionPhase1(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return null;
  }

  public CommitTransactionResult commitTransactionPhase2(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return null;
  }

  public CommitTransactionResult commitTransactionPhase3(InternalCDOXACommitContext xaContext, OMMonitor monitor)
  {
    return null;
  }

  public List<CDORemoteSession> getRemoteSessions(InternalCDORemoteSessionManager manager, boolean subscribe)
  {
    return null;
  }

  public void unsubscribeRemoteSessions()
  {
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
    super.doDeactivate();
  }

  public CDOAuthenticationResult handleAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    InternalCDOSession session = getSession();
    CDOAuthenticator authenticator = session.getAuthenticator();
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
