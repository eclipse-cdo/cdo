/*
 * Copyright (c) 2009-2012, 2016, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteMessageNotificationRequest extends CDOServerRequest
{
  private int senderID;

  private CDORemoteSessionMessage message;

  public RemoteMessageNotificationRequest(CDOServerProtocol serverProtocol, InternalSession sender, CDORemoteSessionMessage message)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE_NOTIFICATION);
    senderID = sender.getSessionID();
    this.message = message;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(senderID);
    message.write(out);
  }
}
