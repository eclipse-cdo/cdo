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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jFactory;
import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.tcp.TCPAcceptor;
import org.eclipse.net4j.transport.tcp.TCPAcceptorListener;
import org.eclipse.net4j.transport.tcp.TCPConnector;
import org.eclipse.net4j.transport.util.ChannelInputStream;
import org.eclipse.net4j.transport.util.ChannelOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.HashCacheRegistry;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.transport.tcp.AbstractTCPConnector;
import org.eclipse.internal.net4j.transport.tcp.TCPAcceptorImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPSelectorImpl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public class TCPTransportTest extends TestCase
{
  private BufferProvider bufferPool;

  private TCPSelectorImpl selector;

  private TCPAcceptorImpl acceptor;

  private AbstractTCPConnector connector;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    System.out.print("================================= ");
    System.out.print(getName());
    System.out.println(" =================================");

    bufferPool = Net4jFactory.createBufferPool();
    LifecycleUtil.activate(bufferPool);
    assertTrue(LifecycleUtil.isActive(bufferPool));

    selector = (TCPSelectorImpl)Net4jFactory.createTCPSelector();
    selector.activate();
    assertTrue(selector.isActive());

    acceptor = (TCPAcceptorImpl)Net4jFactory.createTCPAcceptor(bufferPool, selector);
    connector = (AbstractTCPConnector)Net4jFactory.createTCPConnector(bufferPool, selector,
        "localhost");
  }

  @Override
  protected void tearDown() throws Exception
  {
    try
    {
      if (connector != null)
      {
        connector.disconnect();
        assertFalse(connector.isActive());
        assertFalse(connector.isConnected());
        connector = null;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    try
    {
      acceptor.deactivate();
      assertFalse(acceptor.isActive());
      acceptor = null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    try
    {
      selector.deactivate();
      selector = null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    try
    {
      LifecycleUtil.deactivate(bufferPool);
      bufferPool = null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    System.out.println();
    System.out.println();
    Thread.sleep(10);
    super.tearDown();
  }

  public void testConnect() throws Exception
  {
    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));
  }

  public void testSendBuffer() throws Exception
  {
    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel();
    for (int i = 0; i < 3; i++)
    {
      Buffer buffer = bufferPool.provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelID());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }
  }

  public void testHandleBuffer() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);

    IRegistry<String, ProtocolFactory> protocolFactoryRegistry = new HashMapRegistry();
    protocolFactoryRegistry.register(new TestProtocolFactory(counter));

    acceptor.setProtocolFactoryRegistry(protocolFactoryRegistry);
    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel(TestProtocolFactory.PROTOCOL_ID);
    for (int i = 0; i < COUNT; i++)
    {
      Buffer buffer = bufferPool.provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelID());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }

    assertTrue(counter.await(2, TimeUnit.SECONDS));
  }

  public void testLocalRegistry() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);

    IRegistry<String, ProtocolFactory> global = new HashMapRegistry();
    IRegistry<String, ProtocolFactory> local = new HashCacheRegistry(global);
    local.register(new TestProtocolFactory(counter));
    assertEquals(0, global.size());
    assertEquals(1, local.size());

    acceptor.setProtocolFactoryRegistry(local);
    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel(TestProtocolFactory.PROTOCOL_ID);
    for (int i = 0; i < COUNT; i++)
    {
      Buffer buffer = bufferPool.provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelID());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }

    assertTrue(counter.await(2, TimeUnit.SECONDS));
  }

  public void testGlobalRegistry() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);

    IRegistry<String, ProtocolFactory> global = new HashMapRegistry();
    IRegistry<String, ProtocolFactory> local = new HashCacheRegistry(global);
    global.register(new TestProtocolFactory(counter));
    assertEquals(1, global.size());
    assertEquals(1, local.size());

    acceptor.setProtocolFactoryRegistry(local);
    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel(TestProtocolFactory.PROTOCOL_ID);
    for (int i = 0; i < COUNT; i++)
    {
      Buffer buffer = bufferPool.provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelID());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }

    assertTrue(counter.await(2, TimeUnit.SECONDS));
  }

  public void testReceiveThreadPool() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);

    IRegistry<String, ProtocolFactory> protocolFactoryRegistry = new HashMapRegistry();
    protocolFactoryRegistry.register(new TestProtocolFactory(counter));

    acceptor.setProtocolFactoryRegistry(protocolFactoryRegistry);
    acceptor.setReceiveExecutor(Executors.newCachedThreadPool());
    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel(TestProtocolFactory.PROTOCOL_ID);
    for (int i = 0; i < COUNT; i++)
    {
      Buffer buffer = bufferPool.provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelID());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }

    assertTrue(counter.await(2, TimeUnit.SECONDS));
  }

  public void testStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    acceptor.setReceiveExecutor(Executors.newCachedThreadPool());
    acceptor.addListener(new TCPAcceptorListener()
    {
      public void notifyConnectorAccepted(TCPAcceptor acceptor, TCPConnector connector)
      {
        connector.addChannelListener(new Connector.ChannelListener()
        {
          public void notifyChannelAboutToOpen(Channel channel)
          {
          }

          public void notifyChannelClosing(Channel channel)
          {
          }

          public void notifyChannelOpened(Channel channel)
          {
            inputStream[0] = new ChannelInputStream(channel, 2000);
            counter.countDown();
          }
        });
      }
    });

    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel();
    assertTrue(counter.await(2, TimeUnit.SECONDS));
    assertNotNull(inputStream[0]);

    ChannelOutputStream outputStream = new ChannelOutputStream(channel);
    outputStream.write(HugeData.getBytes());
    outputStream.flushWithEOS();
    outputStream.close();

    try
    {
      InputStreamReader isr = new InputStreamReader(inputStream[0]);
      BufferedReader reader = new BufferedReader(isr);
      String line;
      while ((line = reader.readLine()) != null)
      {
        System.out.println(line);
      }

      isr.close();
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
    }
  }

  public void testTextStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    acceptor.setReceiveExecutor(Executors.newCachedThreadPool());
    acceptor.addListener(new TCPAcceptorListener()
    {
      public void notifyConnectorAccepted(TCPAcceptor acceptor, TCPConnector connector)
      {
        connector.addChannelListener(new Connector.ChannelListener()
        {
          public void notifyChannelAboutToOpen(Channel channel)
          {
          }

          public void notifyChannelClosing(Channel channel)
          {
          }

          public void notifyChannelOpened(Channel channel)
          {
            inputStream[0] = new ChannelInputStream(channel, 2000);
            counter.countDown();
          }
        });
      }
    });

    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel();
    assertTrue(counter.await(2, TimeUnit.SECONDS));
    assertNotNull(inputStream[0]);

    ChannelOutputStream outputStream = new ChannelOutputStream(channel);
    PrintStream printer = new PrintStream(outputStream);
    StringTokenizer tokenizer = HugeData.getTokenizer();
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      printer.println(token);
    }

    outputStream.flushWithEOS();
    outputStream.close();

    try
    {
      InputStreamReader isr = new InputStreamReader(inputStream[0]);
      BufferedReader reader = new BufferedReader(isr);
      String line;
      while ((line = reader.readLine()) != null)
      {
        System.out.println(line);
      }

      isr.close();
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
    }
  }

  public void testTextStreamingDecoupled() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    acceptor.setReceiveExecutor(Executors.newCachedThreadPool());
    acceptor.addListener(new TCPAcceptorListener()
    {
      public void notifyConnectorAccepted(TCPAcceptor acceptor, TCPConnector connector)
      {
        connector.addChannelListener(new Connector.ChannelListener()
        {
          public void notifyChannelAboutToOpen(Channel channel)
          {
          }

          public void notifyChannelClosing(Channel channel)
          {
          }

          public void notifyChannelOpened(Channel channel)
          {
            inputStream[0] = new ChannelInputStream(channel, 2000);
            counter.countDown();
          }
        });
      }
    });

    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    final Channel channel = connector.openChannel();
    assertTrue(counter.await(2, TimeUnit.SECONDS));
    assertNotNull(inputStream[0]);

    new Thread()
    {
      public void run()
      {
        try
        {
          ChannelOutputStream outputStream = new ChannelOutputStream(channel, bufferPool);
          PrintStream printer = new PrintStream(outputStream);
          StringTokenizer tokenizer = HugeData.getTokenizer();
          while (tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            printer.println(token);
          }

          outputStream.flushWithEOS();
          outputStream.close();
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
          fail(ex.getLocalizedMessage());
        }
      }
    }.start();

    try
    {
      InputStreamReader isr = new InputStreamReader(inputStream[0]);
      BufferedReader reader = new BufferedReader(isr);
      String line;
      while ((line = reader.readLine()) != null)
      {
        System.out.println(line);
      }

      isr.close();
    }
    catch (RuntimeException ex)
    {
      ex.printStackTrace();
    }
  }

  public void testDataStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    acceptor.setReceiveExecutor(Executors.newCachedThreadPool());
    acceptor.addListener(new TCPAcceptorListener()
    {
      public void notifyConnectorAccepted(TCPAcceptor acceptor, TCPConnector connector)
      {
        connector.addChannelListener(new Connector.ChannelListener()
        {
          public void notifyChannelAboutToOpen(Channel channel)
          {
          }

          public void notifyChannelClosing(Channel channel)
          {
          }

          public void notifyChannelOpened(Channel channel)
          {
            inputStream[0] = new ChannelInputStream(channel, 2000);
            counter.countDown();
          }
        });
      }
    });

    acceptor.activate();
    assertTrue(acceptor.isActive());
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel();
    assertTrue(counter.await(2, TimeUnit.SECONDS));

    ChannelOutputStream outputStream = new ChannelOutputStream(channel);
    DataOutputStream dataOutput = new DataOutputStream(outputStream);
    byte[] data = HugeData.getBytes();
    dataOutput.writeInt(data.length);
    dataOutput.write(data);
    dataOutput.flush();
    dataOutput.close();
    outputStream.flush();

    DataInputStream dataInput = new DataInputStream(inputStream[0]);
    int size = dataInput.readInt();
    byte[] b = new byte[size];
    dataInput.read(b);
    dataInput.close();

    System.out.println(new String(b));
  }
}
