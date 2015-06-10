/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.common.protocol;

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class CollaborationLeftNotification extends Request
{
  private long collaborationID;

  private String userID;

  public CollaborationLeftNotification(SignalProtocol<?> protocol, long collaborationID, String userID)
  {
    super(protocol, ProtocolConstants.SIGNAL_COLLABORATION_LEFT);
    this.collaborationID = collaborationID;
    this.userID = userID;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(collaborationID);
    out.writeString(userID);
  }
}
