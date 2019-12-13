/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.server.protocol;

import org.eclipse.net4j.buddies.internal.common.protocol.MessageIndication;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.server.ServerSession;
import org.eclipse.net4j.buddies.server.IBuddyAdmin;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class BuddiesServerProtocol extends SignalProtocol<ServerSession>
{
  public BuddiesServerProtocol()
  {
    super(ProtocolConstants.PROTOCOL_NAME);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication(this);

    case ProtocolConstants.SIGNAL_LOAD_ACCOUNT:
      return new LoadAccountIndication(this);

    case ProtocolConstants.SIGNAL_BUDDY_STATE:
      return new ServerBuddyStateIndication(this);

    case ProtocolConstants.SIGNAL_INSTALL_FACILITY:
      return new InstallFacilityIndication(this);

    case ProtocolConstants.SIGNAL_INITIATE_COLLABORATION:
      return new InitiateCollaborationIndication(this);

    case ProtocolConstants.SIGNAL_INVITE_BUDDIES:
      return new InviteBuddiesIndication(this);

    case ProtocolConstants.SIGNAL_COLLABORATION_LEFT:
      return new ServerCollaborationLeftIndication(this);

    case ProtocolConstants.SIGNAL_MESSAGE:
      return new MessageIndication(this, IBuddyAdmin.INSTANCE);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.spi.net4j.ServerProtocolFactory
  {
    public Factory()
    {
      super(ProtocolConstants.PROTOCOL_NAME);
    }

    @Override
    public BuddiesServerProtocol create(String description)
    {
      return new BuddiesServerProtocol();
    }

    public static BuddiesServerProtocol get(IManagedContainer container, String description)
    {
      return (BuddiesServerProtocol)container.getElement(PRODUCT_GROUP, ProtocolConstants.PROTOCOL_NAME, description);
    }
  }
}
