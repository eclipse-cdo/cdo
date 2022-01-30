/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
