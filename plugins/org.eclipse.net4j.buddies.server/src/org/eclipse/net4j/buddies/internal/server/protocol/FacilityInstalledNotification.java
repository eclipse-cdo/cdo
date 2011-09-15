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
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class FacilityInstalledNotification extends Request
{
  private long collaborationID;

  private String facilityType;

  /**
   * @since 2.0
   */
  public FacilityInstalledNotification(BuddiesServerProtocol protocol, long collaborationID, String facilityType)
  {
    super(protocol, ProtocolConstants.SIGNAL_FACILITY_INSTALLED);
    this.collaborationID = collaborationID;
    this.facilityType = facilityType;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(collaborationID);
    out.writeString(facilityType);
  }
}
