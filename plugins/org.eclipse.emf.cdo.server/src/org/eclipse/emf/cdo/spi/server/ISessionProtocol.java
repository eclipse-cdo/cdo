/*
 * Copyright (c) 2009-2013, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;

import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISessionProtocol extends CDOProtocol, IAuthenticationProtocol, INotifier
{
  /**
   * @since 4.0
   * @deprecated As of 4.2 {@link #sendAuthenticationChallenge(Challenge)} is called.
   */
  @Deprecated
  public org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception;

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType) throws Exception;

  /**
   * @deprecated
   */
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState) throws Exception;

  /**
   * @since 4.1
   */
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState, CDOID rootResourceID) throws Exception;

  /**
   * @deprecated As of 4.3 use {@link #sendBranchNotification(InternalCDOBranch, ChangeKind)}.
   */
  @Deprecated
  public void sendBranchNotification(InternalCDOBranch branch) throws Exception;

  /**
   * @since 4.3
   * @deprecated As of 4.15 use {@link #sendBranchNotification(ChangeKind, CDOBranch...)}.
   */
  @Deprecated
  public void sendBranchNotification(InternalCDOBranch branch, ChangeKind changeKind) throws Exception;

  /**
   * @since 4.15
   */
  public void sendBranchNotification(ChangeKind changeKind, CDOBranch... branches) throws Exception;

  /**
   * @since 4.10
   */
  public void sendTagNotification(int modCount, String oldName, String newName, CDOBranchPoint branchPoint) throws Exception;

  /**
   * @deprecated As of 4.2 use {@link #sendCommitNotification(CDOCommitInfo, boolean)}.
   */
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo) throws Exception;

  /**
   * @since 4.2
   * @deprecated As of 4.3 use {@link #sendCommitNotification(CommitNotificationInfo)}.
   */
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo, boolean clearResourcePathCache) throws Exception;

  /**
   * @since 4.3
   */
  public void sendCommitNotification(CommitNotificationInfo info) throws Exception;

  /**
   *@deprecated As of 4.8 use {@link #sendRemoteSessionNotification(InternalSession, InternalTopic, byte)}.
   */
  @Deprecated
  public void sendRemoteSessionNotification(InternalSession sender, byte opcode) throws Exception;

  /**
   * @since 4.17
   */
  public void sendRemoteSessionNotification(InternalSession sender, InternalTopic topic, byte opcode) throws Exception;

  /**
   * @deprecated As of 4.8 use {@link #sendRemoteMessageNotification(InternalSession, InternalTopic, CDORemoteSessionMessage)}.
   */
  @Deprecated
  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message) throws Exception;

  /**
   * @since 4.17
   */
  public void sendRemoteMessageNotification(InternalSession sender, InternalTopic topic, CDORemoteSessionMessage message) throws Exception;

  /**
   * @since 4.1
   * @deprecated As of 4.11 use {@link #sendLockNotification(CDOLockChangeInfo, Set)}.
   */
  @Deprecated
  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo) throws Exception;

  /**
   * @since 4.11
   */
  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo, Set<CDOID> filter) throws Exception;

  /**
   * @since 4.15
   */
  public void sendViewClosedNotification(int viewID) throws Exception;
}
