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

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public class ServerTCPConnectorImpl extends AbstractTCPConnector
{
  public ServerTCPConnectorImpl(SocketChannel socketChannel)
  {
    super(socketChannel);
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
