/*
 * Copyright (c) 2009-2012, 2017, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteSessionNotificationIndication extends CDOClientIndication
{
  public RemoteSessionNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_SESSION_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int sessionID = in.readXInt();
    String topicID = in.readString();
    byte opcode = in.readByte();

    InternalCDORemoteSessionManager remoteSessionManager = getSession().getRemoteSessionManager();
    
    switch (opcode)
    {
    case CDOProtocolConstants.REMOTE_SESSION_OPENED:
      String userID = in.readString();
      remoteSessionManager.handleRemoteSessionOpened(sessionID, userID);
      break;

    case CDOProtocolConstants.REMOTE_SESSION_CLOSED:
      remoteSessionManager.handleRemoteSessionClosed(sessionID);
      break;

    case CDOProtocolConstants.REMOTE_SESSION_SUBSCRIBED:
      remoteSessionManager.handleRemoteSessionSubscribed(sessionID, topicID, true);
      break;

    case CDOProtocolConstants.REMOTE_SESSION_UNSUBSCRIBED:
      remoteSessionManager.handleRemoteSessionSubscribed(sessionID, topicID, false);
      break;
    }
  }
}
