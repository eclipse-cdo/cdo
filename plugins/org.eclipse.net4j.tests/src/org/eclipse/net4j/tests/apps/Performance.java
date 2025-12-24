/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.apps;

import org.eclipse.net4j.util.io.IOUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class Performance
{
  public static void main(String[] args) throws Exception
  {
    testInetAddress();
    testServerSocket();
    // testRouter();
    testSocket();
    testSelector();
  }

  public static void testInetAddress() throws Exception
  {
    System.out.println(InetAddress.class.getName());
    for (int i = 0; i < 2; i++)
    {
      long start = System.currentTimeMillis();
      InetAddress inet = InetAddress.getByName("localhost"); //$NON-NLS-1$
      inet.getHostAddress();
      long duration = System.currentTimeMillis() - start;

      System.out.println(duration);
    }
  }

  public static void testServerSocket() throws IOException
  {
    System.out.println(ServerSocket.class.getName());
    for (int i = 0; i < 2; i++)
    {
      long start = System.currentTimeMillis();
      ServerSocket serverSocket = new ServerSocket(2036);
      long duration = System.currentTimeMillis() - start;

      System.out.println(duration);
      serverSocket.close();
    }
  }

  public static void testRouter() throws Exception
  {
    System.out.println(Socket.class.getName() + " (ROUTER)"); //$NON-NLS-1$
    for (int i = 0; i < 2; i++)
    {
      final SocketAddress endpoint = new InetSocketAddress(InetAddress.getByName("192.168.1.1"), 80); //$NON-NLS-1$
      Socket socket = new Socket(Proxy.NO_PROXY);

      long start = System.currentTimeMillis();
      socket.connect(endpoint);
      long duration = System.currentTimeMillis() - start;

      System.out.println(duration);
      socket.close();
      Thread.sleep(500);
    }
  }

  public static void testSocket() throws Exception
  {
    System.out.println(Socket.class.getName() + " (LOOPBACK)"); //$NON-NLS-1$
    for (int i = 0; i < 2; i++)
    {
      final SocketAddress endpoint = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 2036); //$NON-NLS-1$
      final CountDownLatch latch = new CountDownLatch(1);
      new Thread()
      {
        @Override
        public void run()
        {
          try
          {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(endpoint);
            latch.countDown();

            Socket socket = serverSocket.accept();
            socket.close();
            serverSocket.close();
          }
          catch (IOException ex)
          {
            IOUtil.print(ex);
            latch.countDown();
          }
        }
      }.start();

      latch.await();
      Thread.sleep(500);
      Socket socket = new Socket(Proxy.NO_PROXY);

      long start = System.currentTimeMillis();
      socket.connect(endpoint);
      long duration = System.currentTimeMillis() - start;

      System.out.println(duration);
      socket.close();
      Thread.sleep(500);
    }
  }

  public static void testSelector() throws IOException
  {
    SelectorProvider provider = SelectorProvider.provider();
    System.out.println(provider.getClass().getName());
    for (int i = 0; i < 2; i++)
    {
      long start = System.currentTimeMillis();
      Selector selector = provider.openSelector();
      long duration = System.currentTimeMillis() - start;

      System.out.println(duration);
      selector.close();
    }
  }
}
