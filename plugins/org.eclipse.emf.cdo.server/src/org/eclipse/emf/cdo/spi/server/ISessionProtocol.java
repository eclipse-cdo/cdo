/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISessionProtocol extends CDOProtocol
{
  /**
   * @since 4.0
   * @deprecated As of 4.2 {@link #sendAuthenticationChallenge(Challenge)} is called.
   */
  @Deprecated
  public org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken)
      throws Exception;

  /**
   * @since 4.2
   */
  public Response sendAuthenticationChallenge(Challenge challenge) throws Exception;

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
      throws Exception;

  /**
   * @deprecated
   */
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
      throws Exception;

  /**
   * @since 4.1
   */
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState,
      CDOID rootResourceID) throws Exception;

  public void sendBranchNotification(InternalCDOBranch branch) throws Exception;

  /**
   * @deprecated As of 4.2 use {@link #sendCommitNotification(CDOCommitInfo, boolean)}
   */
  @Deprecated
  public void sendCommitNotification(CDOCommitInfo commitInfo) throws Exception;

  /**
   * @since 4.2
   */
  public void sendCommitNotification(CDOCommitInfo commitInfo, boolean clearResourcePathCache) throws Exception;

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode) throws Exception;

  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message) throws Exception;

  /**
   * @since 4.1
   */
  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo) throws Exception;
}
