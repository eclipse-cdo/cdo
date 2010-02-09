/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface ISessionProtocol extends CDOProtocol
{
  public CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception;

  public void sendBranchNotification(InternalCDOBranch branch);

  public void sendCommitNotification(CDOCommitInfo commitInfo);

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode);

  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message);
}
