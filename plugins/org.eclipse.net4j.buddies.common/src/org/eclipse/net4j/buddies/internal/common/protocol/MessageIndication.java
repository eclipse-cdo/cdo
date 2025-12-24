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
