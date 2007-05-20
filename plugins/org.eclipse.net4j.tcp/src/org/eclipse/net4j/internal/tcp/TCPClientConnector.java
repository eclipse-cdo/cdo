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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.transport.ConnectorLocation;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class TCPClientConnector extends TCPConnector
{
  public TCPClientConnector()
  {
    try
    {
      SocketChannel socketChannel = SocketChannel.open();
      socketChannel.configureBlocking(false);
      setSocketChannel(socketChannel);
    }
    catch (IOException ex)
    {
      Net4j.LOG.error(ex);
    }
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("ClientTCPConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }
    else
    {
      return MessageFormat.format("ClientTCPConnector[{3}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getHost() == null)
    {
      throw new IllegalStateException("host == null");
    }

    if (getPort() == 0)
    {
      throw new IllegalStateException("port == 0");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    InetAddress addr = InetAddress.getByName(getHost());
    InetSocketAddress sAddr = new InetSocketAddress(addr, getPort());
    getSocketChannel().connect(sAddr);
  }
}
