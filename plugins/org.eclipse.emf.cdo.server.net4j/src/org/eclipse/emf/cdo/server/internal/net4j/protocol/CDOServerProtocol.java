/*
 * Copyright (c) 2009-2013, 2015, 2016, 2018-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233273
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Christian W. Damus (CEA LIST) - bug 399306
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.signal.security.AuthenticationRequest;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends SignalProtocol<InternalSession> implements ISessionProtocol
{
  public static final long DEFAULT_NEGOTIATION_TIMEOUT = 15 * 1000;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CDOServerProtocol.class);

  private static final boolean COMPRESS_PACKAGE_URIS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.protocol.compressPackageURIs",
      !StringCompressor.BYPASS);

  private StringIO packageURICompressor = COMPRESS_PACKAGE_URIS ? new StringCompressor(false) : StringIO.DIRECT;

  private long negotiationTimeout = DEFAULT_NEGOTIATION_TIMEOUT;

  private IRepositoryProvider repositoryProvider;

  public CDOServerProtocol(IRepositoryProvider repositoryProvider)
  {
    super(PROTOCOL_NAME);
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public int getVersion()
  {
    // See org.eclipse.emf.cdo.common.protocol.CDOProtocolVersion.
    return PROTOCOL_VERSION;
  }

  @Override
  public InternalSession getSession()
  {
    return getInfraStructure();
  }

  public IRepositoryProvider getRepositoryProvider()
  {
    return repositoryProvider;
  }

  public StringIO getPackageURICompressor()
  {
    return packageURICompressor;
  }

  public long getNegotiationTimeout()
  {
    return negotiationTimeout;
  }

  public void setNegotiationTimeout(long negotiationTimeout)
  {
    this.negotiationTimeout = negotiationTimeout;
  }

  @Override
  @Deprecated
  public org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Response sendAuthenticationChallenge(Challenge challenge) throws Exception
  {
    return new AuthenticationRequest(this, CDOProtocolConstants.SIGNAL_AUTHENTICATION, challenge).send(negotiationTimeout, new Monitor());
  }

  @Override
  public Response sendCredentialsChallenge(Challenge challenge, String userID, CredentialsUpdateOperation operation) throws Exception
  {
    return new CredentialsChallengeRequest(this, challenge, userID, operation).send(negotiationTimeout, new Monitor());
  }

  @Override
  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RepositoryTypeNotificationRequest(this, oldType, newType).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Override
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState) throws Exception
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  @Override
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState, CDOID rootResourceID) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RepositoryStateNotificationRequest(this, oldState, newState, rootResourceID).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Override
  @Deprecated
  public void sendBranchNotification(InternalCDOBranch branch) throws Exception
  {
    sendBranchNotification(branch, ChangeKind.CREATED);
  }

  @Override
  public void sendBranchNotification(InternalCDOBranch branch, ChangeKind changeKind) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new BranchNotificationRequest(this, branch, changeKind).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Override
  public void sendTagNotification(int modCount, String oldName, String newName, CDOBranchPoint branchPoint) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new TagNotificationRequest(this, modCount, oldName, newName, branchPoint).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Override
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo, boolean clearResourcePathCache) throws Exception
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendCommitNotification(CommitNotificationInfo info) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new CommitNotificationRequest(this, info).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Override
  public void sendRemoteSessionNotification(InternalSession sender, byte opcode) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RemoteSessionNotificationRequest(this, sender, opcode).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Override
  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new RemoteMessageNotificationRequest(this, sender, message).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  @Deprecated
  @Override
  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo) throws Exception
  {
    sendLockNotification(lockChangeInfo, null);
  }

  @Override
  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo, Set<CDOID> filter) throws Exception
  {
    if (LifecycleUtil.isActive(getChannel()))
    {
      new LockNotificationRequest(this, lockChangeInfo, filter).sendAsync();
    }
    else
    {
      handleInactiveSession();
    }
  }

  protected void handleInactiveSession()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Session channel is inactive: " + this); //$NON-NLS-1$
    }
  }

  @Override
  protected StringCompressor getStringCompressor()
  {
    if (COMPRESS_PACKAGE_URIS)
    {
      return (StringCompressor)packageURICompressor;
    }

    return super.getStringCompressor();
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication(this);

    case SIGNAL_OPEN_VIEW:
      return new OpenViewIndication(this);

    case SIGNAL_SWITCH_TARGET:
      return new SwitchTargetIndication(this);

    case SIGNAL_CLOSE_VIEW:
      return new CloseViewIndication(this);

    case SIGNAL_LOAD_PACKAGES:
      return new LoadPackagesIndication(this);

    case SIGNAL_CREATE_BRANCH:
      return new CreateBranchIndication(this);

    case SIGNAL_LOAD_BRANCH:
      return new LoadBranchIndication(this);

    case SIGNAL_LOAD_SUB_BRANCHES:
      return new LoadSubBranchesIndication(this);

    case SIGNAL_LOAD_BRANCHES:
      return new LoadBranchesIndication(this);

    case SIGNAL_LOAD_REVISIONS:
      return new LoadRevisionsIndication(this);

    case SIGNAL_LOAD_REVISION_BY_VERSION:
      return new LoadRevisionByVersionIndication(this);

    case SIGNAL_LOAD_CHUNK:
      return new LoadChunkIndication(this);

    case SIGNAL_QUERY_LOBS:
      return new QueryLobsIndication(this);

    case SIGNAL_LOAD_LOB:
      return new LoadLobIndication(this);

    case SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication(this);

    case SIGNAL_COMMIT_DELEGATION:
      return new CommitDelegationIndication(this);

    case SIGNAL_XA_COMMIT_TRANSACTION_PHASE1:
      return new CommitXATransactionPhase1Indication(this);

    case SIGNAL_XA_COMMIT_TRANSACTION_PHASE2:
      return new CommitXATransactionPhase2Indication(this);

    case SIGNAL_XA_COMMIT_TRANSACTION_PHASE3:
      return new CommitXATransactionPhase3Indication(this);

    case SIGNAL_XA_COMMIT_TRANSACTION_CANCEL:
      return new CommitXATransactionCancelIndication(this);

    case SIGNAL_QUERY:
      return new QueryIndication(this);

    case SIGNAL_QUERY_CANCEL:
      return new QueryCancelIndication(this);

    case SIGNAL_REFRESH_SESSION:
      return new RefreshSessionIndication(this);

    case SIGNAL_DISABLE_PASSIVE_UPDATE:
      return new DisablePassiveUpdateIndication(this);

    case SIGNAL_SET_PASSIVE_UPDATE_MODE:
      return new SetPassiveUpdateModeIndication(this);

    case SIGNAL_CHANGE_SUBSCRIPTION:
      return new ChangeSubscriptionIndication(this);

    case SIGNAL_REPOSITORY_TIME:
      return new RepositoryTimeIndication(this);

    case SIGNAL_LOCK_OBJECTS:
      return new LockObjectsIndication(this);

    case SIGNAL_UNLOCK_OBJECTS:
      return new UnlockObjectsIndication(this);

    case SIGNAL_LOCK_DELEGATION:
      return new LockDelegationIndication(this);

    case SIGNAL_UNLOCK_DELEGATION:
      return new UnlockDelegationIndication(this);

    case SIGNAL_OBJECT_LOCKED:
      return new ObjectLockedIndication(this);

    case SIGNAL_LOCK_AREA:
      return new LockAreaIndication(this);

    case SIGNAL_GET_REMOTE_SESSIONS:
      return new GetRemoteSessionsIndication(this);

    case SIGNAL_UNSUBSCRIBE_REMOTE_SESSIONS:
      return new UnsubscribeRemoteSessionsIndication(this);

    case SIGNAL_REMOTE_MESSAGE:
      return new RemoteMessageIndication(this);

    case SIGNAL_LOAD_COMMIT_INFOS:
      return new LoadCommitInfosIndication(this);

    case SIGNAL_LOAD_COMMIT_DATA:
      return new LoadCommitDataIndication(this);

    case SIGNAL_REPLICATE_REPOSITORY:
      return new ReplicateRepositoryIndication(this);

    case SIGNAL_REPLICATE_REPOSITORY_RAW:
      return new ReplicateRepositoryRawIndication(this);

    case SIGNAL_LOAD_CHANGE_SETS:
      return new LoadChangeSetsIndication(this);

    case SIGNAL_LOAD_MERGE_DATA:
      return new LoadMergeDataIndication(this);

    case SIGNAL_HANDLE_REVISIONS:
      return new HandleRevisionsIndication(this);

    case SIGNAL_LOCK_STATE:
      return new LockStateIndication(this);

    case SIGNAL_ENABLE_LOCK_NOTIFICATION:
      return new EnableLockNotificationIndication(this);

    case SIGNAL_SET_LOCK_NOTIFICATION_MODE:
      return new SetLockNotificationModeIndication(this);

    case SIGNAL_LOAD_PERMISSIONS:
      return new LoadPermissionsIndication(this);

    case SIGNAL_CHANGE_CREDENTIALS:
      return new ChangeCredentialsIndication(this);

    case SIGNAL_RENAME_BRANCH:
      return new RenameBranchIndication(this);

    case SIGNAL_LOAD_OBJECT_LIFETIME:
      return new LoadObjectLifetimeIndication(this);

    case SIGNAL_OPENED_SESSION:
      return new OpenedSessionNotification(this);

    case SIGNAL_RESET_TRANSACTION:
      return new ResetTransactionIndication(this);

    case SIGNAL_UNIT:
      return new UnitIndication(this);

    case SIGNAL_CHANGE_TAG:
      return new ChangeTagIndication(this);

    case SIGNAL_LOAD_TAGS:
      return new LoadTagsIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }
}
