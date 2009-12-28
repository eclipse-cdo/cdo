/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends SignalProtocol<InternalSession> implements ISessionProtocol
{
  public static final long DEFAULT_NEGOTIATION_TIMEOUT = 15 * 1000;

  private long negotiationTimeout = DEFAULT_NEGOTIATION_TIMEOUT;

  private IRepositoryProvider repositoryProvider;

  private StringIO packageURICompressor = StringCompressor.BYPASS ? StringIO.DIRECT : new StringCompressor(false);

  public CDOServerProtocol(IRepositoryProvider repositoryProvider)
  {
    super(CDOProtocolConstants.PROTOCOL_NAME);
    this.repositoryProvider = repositoryProvider;
  }

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

  public CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    return new AuthenticationRequest(this, randomToken).send(negotiationTimeout);
  }

  public void sendCommitNotification(long timeStamp, CDOPackageUnit[] packageUnits, List<CDOIDAndVersion> dirtyIDs,
      List<CDOID> detachedObjects, List<CDORevisionDelta> newDeltas)
  {
    try
    {
      IChannel channel = getChannel();
      if (LifecycleUtil.isActive(channel))
      {
        new CommitNotificationRequest(channel, timeStamp, packageUnits, dirtyIDs, detachedObjects, newDeltas)
            .sendAsync();
      }
      else
      {
        OM.LOG.warn("Session channel is inactive: " + this); //$NON-NLS-1$
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public void sendRemoteSessionNotification(byte opcode, ISession session)
  {
    try
    {
      IChannel channel = getChannel();
      if (LifecycleUtil.isActive(channel))
      {
        new RemoteSessionNotificationRequest(channel, opcode, session).sendAsync();
      }
      else
      {
        OM.LOG.warn("Session channel is inactive: " + this); //$NON-NLS-1$
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  public boolean sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message)
  {
    try
    {
      IChannel channel = getChannel();
      if (LifecycleUtil.isActive(channel))
      {
        new RemoteMessageNotificationRequest(channel, sender, message).sendAsync();
        return true;
      }
      else
      {
        OM.LOG.warn("Session channel is inactive: " + this); //$NON-NLS-1$
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }

    return false;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication(this);

    case CDOProtocolConstants.SIGNAL_VIEWS_CHANGED:
      return new ViewsChangedIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_PACKAGES:
      return new LoadPackagesIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION:
      return new LoadRevisionIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_TIME:
      return new LoadRevisionByTimeIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION:
      return new LoadRevisionByVersionIndication(this);

    case CDOProtocolConstants.SIGNAL_LOAD_CHUNK:
      return new LoadChunkIndication(this);

    case CDOProtocolConstants.SIGNAL_VERIFY_REVISION:
      return new VerifyRevisionIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE1:
      return new CommitTransactionPhase1Indication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE2:
      return new CommitTransactionPhase2Indication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_PHASE3:
      return new CommitTransactionPhase3Indication(this);

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION_CANCEL:
      return new CommitTransactionCancelIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY:
      return new QueryIndication(this);

    case CDOProtocolConstants.SIGNAL_QUERY_CANCEL:
      return new QueryCancelIndication(this);

    case CDOProtocolConstants.SIGNAL_SYNC_REVISIONS:
      return new SyncRevisionsIndication(this);

    case CDOProtocolConstants.SIGNAL_PASSIVE_UPDATE:
      return new SetPassiveUpdateIndication(this);

    case CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION:
      return new ChangeSubscriptionIndication(this);

    case CDOProtocolConstants.SIGNAL_SET_AUDIT:
      return new SetAuditIndication(this);

    case CDOProtocolConstants.SIGNAL_REPOSITORY_TIME:
      return new RepositoryTimeIndication(this);

    case CDOProtocolConstants.SIGNAL_LOCK_OBJECTS:
      return new LockObjectsIndication(this);

    case CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS:
      return new UnlockObjectsIndication(this);

    case CDOProtocolConstants.SIGNAL_OBJECT_LOCKED:
      return new ObjectLockedIndication(this);

    case CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS:
      return new GetRemoteSessionsIndication(this);

    case CDOProtocolConstants.SIGNAL_UNSUBSCRIBE_REMOTE_SESSIONS:
      return new UnsubscribeRemoteSessionsIndication(this);

    case CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE:
      return new RemoteMessageIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }
}
