/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.common.ISession;
import org.eclipse.net4j.buddies.internal.common.protocol.MessageIndication;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.buddies.ClientSession;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Eike Stepper
 */
public class BuddiesClientProtocol extends SignalProtocol<ClientSession>
{
  private static final long GET_SESSION_TIMEOUT = 20000;

  private static final int GET_SESSION_INTERVAL = 100;

  public BuddiesClientProtocol(IConnector connector)
  {
    super(ProtocolConstants.PROTOCOL_NAME);
    open(connector);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case ProtocolConstants.SIGNAL_BUDDY_ADDED:
      return new BuddyAddedIndication(this);

    case ProtocolConstants.SIGNAL_BUDDY_REMOVED:
      return new BuddyRemovedIndication(this);

    case ProtocolConstants.SIGNAL_BUDDY_STATE:
      return new ClientBuddyStateIndication(this);

    case ProtocolConstants.SIGNAL_COLLABORATION_INITIATED:
      return new CollaborationInitiatedIndication(this);

    case ProtocolConstants.SIGNAL_COLLABORATION_LEFT:
      return new ClientCollaborationLeftIndication(this, getSelf());

    case ProtocolConstants.SIGNAL_FACILITY_INSTALLED:
      return new FacilityInstalledIndication(this);

    case ProtocolConstants.SIGNAL_MESSAGE:
      return new MessageIndication(this, getSelf());

    default:
      return super.createSignalReactor(signalID);
    }
  }

  protected Self getSelf()
  {
    ISession session = getInfraStructure();
    return (Self)session.getSelf();
  }

  public ClientSession getSession()
  {
    int max = (int)(GET_SESSION_TIMEOUT / GET_SESSION_INTERVAL);
    for (int i = 0; i < max; i++)
    {
      ClientSession session = getInfraStructure();
      if (session == null)
      {
        ConcurrencyUtil.sleep(GET_SESSION_INTERVAL);
      }
      else
      {
        return session;
      }
    }

    throw new IllegalStateException("No session after " + max + " milliseconds"); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
