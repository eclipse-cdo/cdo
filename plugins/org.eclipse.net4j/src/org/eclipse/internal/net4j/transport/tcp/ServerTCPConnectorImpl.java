/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.tcp;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.tcp.TCPSelector;
import org.eclipse.net4j.util.registry.IRegistry;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ServerTCPConnectorImpl extends AbstractTCPConnector
{
  public ServerTCPConnectorImpl(SocketChannel socketChannel, ExecutorService receiveExecutor,
      IRegistry<String, ProtocolFactory> protocolFactoryRegistry, BufferProvider bufferProvider,
      TCPSelector selector)
  {
    super(socketChannel);
    setReceiveExecutor(receiveExecutor);
    setProtocolFactoryRegistry(protocolFactoryRegistry);
    setBufferProvider(bufferProvider);
    setSelector(selector);
  }

  public Type getType()
  {
    return Type.SERVER;
  }

  public String getHost()
  {
    return getSocketChannel().socket().getInetAddress().getHostAddress();
  }

  public int getPort()
  {
    return getSocketChannel().socket().getPort();
  }

  @Override
  public String toString()
  {
    try
    {
      SocketAddress address = getSocketChannel().socket().getRemoteSocketAddress();
      return "ServerTCPConnector[" + address + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
    catch (Exception ex)
    {
      return "ServerTCPConnector"; //$NON-NLS-1$
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    super.onDeactivate();
  }
}
