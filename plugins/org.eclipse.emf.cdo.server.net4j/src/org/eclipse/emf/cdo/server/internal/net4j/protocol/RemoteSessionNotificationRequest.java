/*
 * Copyright (c) 2009-2012, 2017, 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTopic;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteSessionNotificationRequest extends CDOServerRequest
{
  private InternalSession sender;

  private InternalTopic topic;

  private byte opcode;

  public RemoteSessionNotificationRequest(CDOServerProtocol serverProtocol, InternalSession sender, InternalTopic topic, byte opcode)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_REMOTE_SESSION_NOTIFICATION);
    this.sender = sender;
    this.topic = topic;
    this.opcode = opcode;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(sender.getSessionID());
    out.writeString(topic == null ? null : topic.getID());
    out.writeByte(opcode);
    if (opcode == CDOProtocolConstants.REMOTE_SESSION_OPENED)
    {
      out.writeString(sender.getUserID());
    }
  }
}
