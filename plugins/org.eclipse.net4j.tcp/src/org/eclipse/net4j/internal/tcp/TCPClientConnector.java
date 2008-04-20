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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPSelector;

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
      // socketChannel.socket().setReuseAddress(true);
      // socketChannel.socket().setKeepAlive(true);
      socketChannel.configureBlocking(false);
      setSocketChannel(socketChannel);
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
    }
  }

  public ConnectorLocation getLocation()
  {
    return ConnectorLocation.CLIENT;
  }

  @Override
  public void setHost(String host)
  {
    super.setHost(host);
  }

  @Override
  public void setPort(int port)
  {
    super.setPort(port);
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("ClientTCPConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }

    return MessageFormat.format("ClientTCPConnector[{3}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
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
  public void handleRegistration(ITCPSelector selector, SocketChannel socketChannel)
  {
    super.handleRegistration(selector, socketChannel);

    try
    {
      InetAddress addr = InetAddress.getByName(getHost());
      InetSocketAddress sAddr = new InetSocketAddress(addr, getPort());
      getSocketChannel().connect(sAddr);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivate();
    }
  }
}
