/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.internal.server;

import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.http.internal.common.HTTPConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.WrappedException;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class HTTPServerConnector extends HTTPConnector implements IServerConnector
{
  private HTTPAcceptor acceptor;

  public HTTPServerConnector(HTTPAcceptor acceptor)
  {
    this.acceptor = acceptor;
  }

  @Override
  public HTTPAcceptor getAcceptor()
  {
    return acceptor;
  }

  @Override
  public Location getLocation()
  {
    return Location.SERVER;
  }

  @Override
  public String getURL()
  {
    return "agent://connector:" + getConnectorID(); //$NON-NLS-1$
  }

  @Override
  public int getMaxIdleTime()
  {
    return acceptor.getMaxIdleTime();
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("HTTPServerConnector[{0}]", getConnectorID()); //$NON-NLS-1$
    }

    return MessageFormat.format("HTTPServerConnector[{1}@{0}]", getConnectorID(), getUserID()); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    leaveConnecting();
  }

  @Override
  protected void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol) throws ChannelException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean pollAgain()
  {
    try
    {
      Thread.sleep(100);
      return true;
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
