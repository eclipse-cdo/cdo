/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.security.IUserManager;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalSessionManager extends ISessionManager
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  public void setUserManager(IUserManager userManager);

  public InternalSession[] getSessions();

  public InternalSession getSession(int sessionID);

  /**
   * @return Never <code>null</code>
   */
  public InternalSession openSession(ISessionProtocol sessionProtocol);

  public void sessionClosed(InternalSession session);

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType);

  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState);

  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch);

  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo);

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode);

  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message,
      int[] recipients);
}
