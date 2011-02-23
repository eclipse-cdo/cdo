/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.net4j.internal.tcp;

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

  @Override
  public Location getLocation()
  {
    return Location.CLIENT;
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("TCPClientConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }

    return MessageFormat.format("TCPClientConnector[{2}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getHost() == null)
    {
      throw new IllegalStateException("host == null"); //$NON-NLS-1$
    }

    if (getPort() == 0)
    {
      throw new IllegalStateException("port == 0"); //$NON-NLS-1$
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
      deactivateAsync();
    }
  }
}
