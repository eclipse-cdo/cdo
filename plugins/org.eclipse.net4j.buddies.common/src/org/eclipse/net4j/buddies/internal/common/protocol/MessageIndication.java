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
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.ICollaborationProvider;
import org.eclipse.net4j.buddies.common.IMessage;
import org.eclipse.net4j.buddies.spi.common.Facility;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class MessageIndication extends Indication
{
  private ICollaborationProvider collaborationProvider;

  public MessageIndication(SignalProtocol<?> protocol, ICollaborationProvider collaborationProvider)
  {
    super(protocol, ProtocolConstants.SIGNAL_MESSAGE);
    this.collaborationProvider = collaborationProvider;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    long collaborationID = in.readLong();
    String facilityType = in.readString();
    Facility facility = getFacility(collaborationID, facilityType);
    if (facility != null)
    {
      IMessage message = ProtocolUtil.readMessage(in, facility.getClass().getClassLoader());
      facility.handleMessage(message);
    }
  }

  private Facility getFacility(long collaborationID, String facilityType)
  {
    ICollaboration collaboration = collaborationProvider.getCollaboration(collaborationID);
    if (collaboration == null)
    {
      return null;
    }

    return (Facility)collaboration.getFacility(facilityType);
  }
}
