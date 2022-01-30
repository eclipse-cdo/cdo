/*
 * Copyright (c) 2009-2012, 2016, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteMessageNotificationIndication extends CDOClientIndication
{
  public RemoteMessageNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int senderID = in.readXInt();
    String topicID = in.readString();
    CDORemoteSessionMessage message = new CDORemoteSessionMessage(in);

    InternalCDORemoteSessionManager remoteSessionManager = getSession().getRemoteSessionManager();
    remoteSessionManager.handleRemoteSessionMessage(senderID, topicID, message);
  }
}
