/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.protocol.Collaboration;
import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.internal.server.ServerFacilityFactory;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.protocol.IFacility;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class InstallFacilityIndication extends IndicationWithResponse
{
  private static final String FACILITY_GROUP = ServerFacilityFactory.PRODUCT_GROUP;

  private boolean success;

  public InstallFacilityIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_INSTALL_FACILITY;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    long collaborationID = in.readLong();
    String facilityType = in.readString();

    try
    {
      String description = String.valueOf(collaborationID);
      IFacility facility = (IFacility)IPluginContainer.INSTANCE.getElement(FACILITY_GROUP, facilityType, description);
      Collaboration collaboration = (Collaboration)BuddyAdmin.INSTANCE.getCollaboration(collaborationID);
      facility.setCollaboration(collaboration);
      collaboration.addFacility(facility);
      success = true;
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeBoolean(success);
  }
}
