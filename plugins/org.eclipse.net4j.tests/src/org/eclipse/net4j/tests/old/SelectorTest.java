/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.test;


import java.io.IOException;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import junit.framework.TestCase;


public class SelectorTest extends TestCase
{
  protected class Server extends Thread
  {
    protected ServerSocketChannel serverChannel;

    public Server(String listen, int port) throws IOException
    {
      serverChannel = ServerSocketChannel.open();

      InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(listen), port);
      serverChannel.socket().bind(addr);
      start();
    }

    public void run()
    {
      try
      {
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        readSocketChannel(socketChannel);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        throw new RuntimeException(ex);
      }
    }

    public void readSocketChannel(SocketChannel socketChannel)
    {
    }
  }

  public void testFlooding()
  {

  }
}
