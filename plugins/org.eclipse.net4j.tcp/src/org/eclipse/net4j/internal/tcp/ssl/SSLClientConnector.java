/*
 * Copyright (c) 2011, 2012, 2015, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPSelector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

/**
 * SSLClientConnector responses to be connector on client side and it handles about connecting to the server connector.
 *
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLClientConnector extends SSLConnector
{
  public SSLClientConnector()
  {
    try
    {
      SocketChannel socketChannel = SocketChannel.open();
      socketChannel.socket().setReuseAddress(true);
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
      return MessageFormat.format("SSLClientConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }

    return MessageFormat.format("SSLClientConnector[{2}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
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
  protected IBufferProvider createBufferProvider(SSLEngineManager sslEngineManager)
  {
    SSLBufferFactory bufferFactory = new SSLBufferFactory(sslEngineManager);
    return Net4jUtil.createBufferPool(bufferFactory);
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
