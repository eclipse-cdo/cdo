/*
 * Copyright (c) 2009-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.security.DiffieHellman;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.IUserManager;

import java.util.Collection;
import java.util.List;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalSessionManager extends ISessionManager, IExecutorServiceProvider
{
  @Override
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  /**
   * @since 4.17
   */
  @Override
  public InternalTopicManager getTopicManager();

  /**
   * @since 4.1
   * @deprecated As of 4.2 use {@link #getAuthenticator()}
   */
  @Deprecated
  public IUserManager getUserManager();

  /**
   * @deprecated As of 4.2 use {@link #setAuthenticator(IAuthenticator)}
   */
  @Deprecated
  public void setUserManager(IUserManager userManager);

  /**
   * @since 4.2
   */
  public DiffieHellman.Server getAuthenticationServer();

  /**
   * @since 4.2
   */
  public void setAuthenticationServer(DiffieHellman.Server authenticationServer);

  /**
   * Initiates the change-credentials protocol with the client and processes the
   * client response to update the user's credentials.
   *
   * @since 4.3
   */
  public void changeUserCredentials(IAuthenticationProtocol sessionProtocol, String userID);

  /**
   * Initiates the administrative reset-credentials protocol with the client and
   * processes the client response to reset the specified {@code userID}'s credentials.
   *
   * @since 4.3
   */
  public void resetUserCredentials(IAuthenticationProtocol sessionProtocol, String userID);

  /**
   * Challenges the connected user to authenticate the connection.
   *
   * @param sessionProtocol the authenticatable session protocol
   * @return the user ID with which the user authenticated herself, or {@code null}
   *         if the server does not require authentication for this connection
   *
   * @throws SecurityException on failure to authenticate
   *
   * @since 4.3
   */
  public String authenticateUser(IAuthenticationProtocol sessionProtocol) throws SecurityException;

  /**
   * @since 4.1
   */
  public IPermissionManager getPermissionManager();

  /**
   * @since 4.1
   */
  public void setPermissionManager(IPermissionManager permissionManager);

  @Override
  public InternalSession[] getSessions();

  @Override
  public InternalSession getSession(int sessionID);

  /**
   * @return Never <code>null</code>
   */
  public InternalSession openSession(ISessionProtocol sessionProtocol);

  /**
   * @return Never <code>null</code>
   * @since 4.14
   */
  public InternalSession openSession(ISessionProtocol protocol, int sessionID);

  public void sessionClosed(InternalSession session);

  /**
   * @since 4.5
   */
  public void openedOnClientSide(InternalSession session);

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType);

  /**
   * @deprecated use
   *             {@link #sendRepositoryStateNotification(org.eclipse.emf.cdo.common.CDOCommonRepository.State, org.eclipse.emf.cdo.common.CDOCommonRepository.State, CDOID)}
   *             instead
   */
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState);

  /**
   * @since 4.1
   */
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState, CDOID rootResourceID);

  /**
   * @deprecated As of 4.3 use {@link #sendBranchNotification(InternalSession, InternalCDOBranch, ChangeKind)}.
   */
  @Deprecated
  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch);

  /**
   * @since 4.3
   * @deprecated As of 4.15 use {@link #sendBranchNotification(InternalSession, ChangeKind, CDOBranch...)}.
   */
  @Deprecated
  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch, ChangeKind changeKind);

  /**
   * @since 4.15
   */
  public void sendBranchNotification(InternalSession sender, ChangeKind changeKind, CDOBranch... branches);

  /**
   * @since 4.10
   */
  public void sendTagNotification(InternalSession sender, int modCount, String oldName, String newName, CDOBranchPoint branchPoint);

  /**
   * @deprecated As of 4.2 use {@link #sendCommitNotification(InternalSession, CDOCommitInfo, boolean)}.
   */
  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo);

  /**
   * @since 4.2
   * @deprecated As of 4.3 use {@link #sendCommitNotification(ISessionProtocol.CommitNotificationInfo)}.
   */
  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo, boolean clearResourcePathCache);

  /**
   * @since 4.3
   */
  public void sendCommitNotification(CommitNotificationInfo info);

  /**
   * @since 4.1
   */
  public void sendLockNotification(InternalSession sender, CDOLockChangeInfo lockChangeInfo);

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode);

  /**
   * @since 4.17
   */
  public void sendRemoteSessionNotification(InternalSession sender, Collection<InternalSession> recipients, InternalTopic topic, byte opcode);

  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message, int[] recipients);

  /**
   * @since 4.17
   */
  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message, InternalTopic topic);
}
