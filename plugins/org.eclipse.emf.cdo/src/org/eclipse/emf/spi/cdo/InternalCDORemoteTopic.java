/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.session.remote.CDORemoteTopic;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.17
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDORemoteTopic extends CDORemoteTopic
{
  @Override
  public InternalCDORemoteSessionManager getManager();

  @Override
  public InternalCDORemoteSession[] getRemoteSessions();

  public void handleRemoteSessionSubscribed(int sessionID, boolean subscribed);

  public void handleRemoteSessionMessage(InternalCDORemoteSession remoteSession, CDORemoteSessionMessage message);
}
