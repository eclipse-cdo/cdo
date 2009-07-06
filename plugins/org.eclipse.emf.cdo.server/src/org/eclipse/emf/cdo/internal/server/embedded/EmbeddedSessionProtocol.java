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
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.transaction.CDOTimeStampContext;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
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
public class EmbeddedSessionProtocol extends Lifecycle implements CDOSessionProtocol, ISessionProtocol
{
  public EmbeddedSessionProtocol(EmbeddedSession session)
  {
  }

  public boolean cancelQuery(int queryId)
  {
    return false;
  }

  public void changeSubscription(int viewId, List<CDOID> cdoIDs, boolean subscribeMode, boolean clear)
  {
  }

  public void closeView(int viewId)
  {
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

  public RepositoryTimeResult getRepositoryTime()
  {
    return null;
  }

  public boolean isObjectLocked(CDOView view, CDOObject object, LockType lockType, boolean byOthers)
  {
    return false;
  }

  public Object loadChunk(InternalCDORevision revision, EStructuralFeature feature, int accessIndex, int fetchIndex,
      int fromIndex, int toIndex)
  {
    return null;
  }

  public void loadLibraries(Set<String> missingLibraries, File cacheFolder)
  {
  }

  public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return null;
  }

  public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    return null;
  }

  public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp)
  {
    return null;
  }

  public void lockObjects(CDOView view, Map<CDOID, CDOIDAndVersion> objects, long timeout, LockType lockType)
      throws InterruptedException
  {
  }

  public OpenSessionResult openSession(String repositoryName, boolean passiveUpdateEnabled)
  {
    return null;
  }

  public void openView(int viewId, byte protocolViewType, long timeStamp)
  {
  }

  public List<Object> query(int viewID, AbstractQueryIterator<?> queryResult)
  {
    return null;
  }

  public boolean[] setAudit(int viewId, long timeStamp, List<InternalCDOObject> invalidObjects)
  {
    return null;
  }

  public void setPassiveUpdate(Map<CDOID, CDOIDAndVersion> idAndVersions, int initialChunkSize,
      boolean passiveUpdateEnabled)
  {
  }

  public Collection<CDOTimeStampContext> syncRevisions(Map<CDOID, CDOIDAndVersion> allRevisions, int initialChunkSize)
  {
    return null;
  }

  public void unlockObjects(CDOView view, Collection<? extends CDOObject> objects, LockType lockType)
  {
  }

  public void unsubscribeRemoteSessions()
  {
  }

  public List<InternalCDORevision> verifyRevision(List<InternalCDORevision> revisions)
  {
    return null;
  }

  public CDOCommonSession getSession()
  {
    return null;
  }

  public EPackage[] loadPackages(CDOPackageUnit packageUnit)
  {
    return null;
  }

  public CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    return null;
  }

  public void sendCommitNotification(long timeStamp, CDOPackageUnit[] packageUnits, List<CDOIDAndVersion> dirtyIDs,
      List<CDOID> detachedObjects, List<CDORevisionDelta> newDeltas)
  {
  }

  public void sendRemoteSessionNotification(byte opcode, ISession session)
  {
  }
}
