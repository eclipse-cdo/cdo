/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.IFacility;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.Collaboration;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.BuddyAdmin;
import org.eclipse.net4j.buddies.internal.server.bundle.OM;
import org.eclipse.net4j.buddies.spi.common.ServerFacilityFactory;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class InstallFacilityIndication extends IndicationWithResponse
{
  private static final String FACILITY_GROUP = ServerFacilityFactory.PRODUCT_GROUP;

  private boolean success;

  /**
   * @since 2.0
   */
  public InstallFacilityIndication(BuddiesServerProtocol protocol)
  {
    super(protocol, ProtocolConstants.SIGNAL_INSTALL_FACILITY);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    long collaborationID = in.readLong();
    String facilityType = in.readString();

    try
    {
      String description = String.valueOf(collaborationID);
      IFacility facility = (IFacility)IPluginContainer.INSTANCE.getElement(FACILITY_GROUP, facilityType, description);

      Collaboration collaboration = (Collaboration)BuddyAdmin.INSTANCE.getCollaboration(collaborationID);
      if (collaboration != null)
      {
        facility.setCollaboration(collaboration);
        collaboration.addFacility(facility, true);

        ISession session = (ISession)getProtocol().getInfraStructure();
        IBuddy initiator = session.getSelf();

        for (IBuddy buddy : collaboration.getBuddies())
        {
          if (buddy != initiator)
          {
            try
            {
              BuddiesServerProtocol protocol = (BuddiesServerProtocol)buddy.getSession().getProtocol();
              new FacilityInstalledNotification(protocol, collaborationID, facilityType).sendAsync();
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        }

        success = true;
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(success);
  }
}
