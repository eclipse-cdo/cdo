/*
 * Copyright (c) 2011, 2012, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.tcp.ITCPSelector;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

/**
 * SSLServerConnector responses to be connector on server side and it handles about registration from the
 * SSLClientConnector.
 *
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLServerConnector extends SSLConnector implements IServerConnector
{
  private SSLAcceptor acceptor;

  public SSLServerConnector(SSLAcceptor acceptor)
  {
    this.acceptor = acceptor;
  }

  public SSLAcceptor getAcceptor()
  {
    return acceptor;
  }

  @Override
  public Location getLocation()
  {
    return Location.SERVER;
  }

  @Override
  public String getHost()
  {
    try
    {
      return getSocketChannel().socket().getInetAddress().getHostAddress();
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  @Override
  public int getPort()
  {
    try
    {
      return getSocketChannel().socket().getPort();
    }
    catch (RuntimeException ex)
    {
      return 0;
    }
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("SSLServerConnector[{0}:{1}]", getHost(), getPort()); //$NON-NLS-1$
    }

    return MessageFormat.format("SSLServerConnector[{2}@{0}:{1}]", getHost(), getPort(), getUserID()); //$NON-NLS-1$
  }

  @Override
  public void handleRegistration(ITCPSelector selector, SocketChannel socketChannel)
  {
    super.handleRegistration(selector, socketChannel);

    try
    {
      acceptor.addConnector(this);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      deactivateAsync();
    }
  }

  @Override
  protected IBufferProvider createBufferProvider(SSLEngineManager sslEngineManager)
  {
    return new SSLBufferFactory(sslEngineManager);
  }
}
