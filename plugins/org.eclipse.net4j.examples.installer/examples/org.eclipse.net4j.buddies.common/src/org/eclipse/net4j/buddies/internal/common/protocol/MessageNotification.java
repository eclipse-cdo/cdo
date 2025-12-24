/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.buddies.common.IMessage;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class MessageNotification extends Request
{
  private long collaborationID;

  private String facilityType;

  private IMessage message;

  public MessageNotification(SignalProtocol<?> protocol, long collaborationID, String facilityType, IMessage message)
  {
    super(protocol, ProtocolConstants.SIGNAL_MESSAGE);
    this.collaborationID = collaborationID;
    this.facilityType = facilityType;
    this.message = message;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(collaborationID);
    out.writeString(facilityType);
    ProtocolUtil.writeMessage(out, message);
  }
}
