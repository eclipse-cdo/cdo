/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class InstallFacilityRequest extends RequestWithConfirmation<Boolean>
{
  private long collaborationID;

  private String facilityType;

  public InstallFacilityRequest(BuddiesClientProtocol protocol, long collaborationID, String facilityType)
  {
    super(protocol, ProtocolConstants.SIGNAL_INSTALL_FACILITY);
    this.collaborationID = collaborationID;
    this.facilityType = facilityType;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(collaborationID);
    out.writeString(facilityType);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
