/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class InitiateCollaborationIndication extends IndicationWithResponse
{
  private ICollaboration collaboration;

  public InitiateCollaborationIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return ProtocolConstants.SIGNAL_INITIATE_COLLABORATION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    String[] userIDs = ProtocolUtil.readUserIDs(in);
    ISession session = (ISession)getProtocol().getInfraStructure();
    IBuddy initiator = session.getSelf();
    collaboration = IBuddyAdmin.INSTANCE.initiateCollaboration(initiator, userIDs);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeLong(collaboration.getID());
  }
}
