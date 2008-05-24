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
package org.eclipse.net4j.internal.http;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.internal.http.bundle.OM;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.connector.Connector;

/**
 * @author Eike Stepper
 */
public abstract class HTTPConnector extends Connector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HTTPConnector.class);

  public HTTPConnector()
  {
  }

  public void multiplexChannel(IChannel channel)
  {
  }

  @Override
  protected void registerChannelWithPeer(int channelID, short channelIndex, IProtocol protocol)
      throws ConnectorException
  {
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    throw new UnsupportedOperationException();
  }
}
