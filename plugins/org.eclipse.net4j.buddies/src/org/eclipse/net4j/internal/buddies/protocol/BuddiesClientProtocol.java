/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.spi.net4j.Protocol;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 */
public class BuddiesClientProtocol extends SignalProtocol<ClientSession>
{
  private static final long GET_SESSION_TIMEOUT = 20000;

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

  @Override
  public synchronized void setInfraStructure(ClientSession infraStructure)
  {
    super.setInfraStructure(infraStructure);
    notifyAll();
  }

  public ClientSession getSession()
  {
    synchronized (this)
    {
      ClientSession session = getInfraStructure();
      if (session != null)
      {
        return session;
      }

      IListener listener = new IListener()
      {
        @Override
        public void notifyEvent(IEvent event)
        {
          if (event instanceof Protocol.InfraStructureChangedEvent)
          {
            synchronized (BuddiesClientProtocol.this)
            {
              BuddiesClientProtocol.this.notifyAll();
            }
          }
        }
      };

      addListener(listener);
      try
      {
        long deadline = java.lang.System.nanoTime() + GET_SESSION_TIMEOUT * 1000000L;
        while ((session = getInfraStructure()) == null)
        {
          long remaining = deadline - java.lang.System.nanoTime();
          if (remaining <= 0)
          {
            break;
          }

          try
          {
            long millis = remaining / 1000000L;
            int nanos = (int)(remaining % 1000000L);
            wait(millis, nanos);
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for session", ex); //$NON-NLS-1$
          }
        }

        if (session != null)
        {
          return session;
        }
      }
      finally
      {
        removeListener(listener);
      }
    }

    throw new IllegalStateException("No session after " + GET_SESSION_TIMEOUT + " milliseconds"); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
