/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CollaborationInitiatedNotification extends Request
{
  private long collaborationID;

  private Collection<IBuddy> buddies;

  private String[] facilityTypes;

  /**
   * @since 2.0
   */
  public CollaborationInitiatedNotification(BuddiesServerProtocol protocol, long collaborationID, Collection<IBuddy> buddies, String[] facilityTypes)
  {
    super(protocol, ProtocolConstants.SIGNAL_COLLABORATION_INITIATED);
    this.collaborationID = collaborationID;
    this.buddies = buddies;
    this.facilityTypes = facilityTypes;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(collaborationID);
    ProtocolUtil.writeBuddies(out, buddies);
    ProtocolUtil.writeFacilityTypes(out, facilityTypes);
  }
}
