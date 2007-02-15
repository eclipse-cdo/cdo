/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.container.Container;
import org.eclipse.net4j.stream.ChannelInputStream;
import org.eclipse.net4j.stream.ChannelOutputStream;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.transport.AcceptorConnectorsEvent;
import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ConnectorChannelsEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class TCPTransportTest extends AbstractTCPTest
{
  @Override
  protected Container createContainer()
  {
    Container container = super.createContainer();
    container.register(new TestSignalProtocol.Factory());
    return container;
  }

  protected Buffer provideBuffer()
  {
    return container.getBufferProvider().provideBuffer();
  }

  public void testConnect() throws Exception
  {
    startTransport();
  }

  public void testSendBuffer() throws Exception
  {
    startTransport();
    Channel channel = getConnector().openChannel();
    for (int i = 0; i < 3; i++)
    {
      Buffer buffer = provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelIndex());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }
  }

  public void testHandleBuffer() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    container.register(new TestProtocolFactory(counter));
    startTransport();

    Channel channel = getConnector().openChannel(TestProtocolFactory.PROTOCOL_ID);
    for (int i = 0; i < COUNT; i++)
    {
      Buffer buffer = provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelIndex());
      byteBuffer.putInt(1970);
      channel.handleBuffer(buffer);
    }

    assertTrue(counter.await(2, TimeUnit.SECONDS));
  }

  // public void testLocalRegistry() throws Exception
  // {
  // final int COUNT = 3;
  // final CountDownLatch counter = new CountDownLatch(COUNT);
  //
  // IRegistry<String, ProtocolFactory> global = new HashMapRegistry();
  // IRegistry<String, ProtocolFactory> local = new HashCacheRegistry(global);
  // local.register(new TestProtocolFactory(counter));
  // assertEquals(0, global.size());
  // assertEquals(1, local.size());
  //
  // acceptor.setProtocolFactoryRegistry(local);
  // acceptor.activate();
  // assertTrue(acceptor.isActive());
  // assertTrue(connector.connect(5000));
  //
  // Channel channel = connector.openChannel(TestProtocolFactory.PROTOCOL_ID);
  // for (int i = 0; i < COUNT; i++)
  // {
  // Buffer buffer = provideBuffer();
  // ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelIndex());
  // byteBuffer.putInt(1970);
  // channel.handleBuffer(buffer);
  // }
  //
  // assertTrue(counter.await(2, TimeUnit.SECONDS));
  // }

  // public void testGlobalRegistry() throws Exception
  // {
  // final int COUNT = 3;
  // final CountDownLatch counter = new CountDownLatch(COUNT);
  //
  // IRegistry<String, ProtocolFactory> global = new HashMapRegistry();
  // IRegistry<String, ProtocolFactory> local = new HashCacheRegistry(global);
  // global.register(new TestProtocolFactory(counter));
  // assertEquals(1, global.size());
  // assertEquals(1, local.size());
  //
  // acceptor.setProtocolFactoryRegistry(local);
  // acceptor.activate();
  // assertTrue(acceptor.isActive());
  // assertTrue(connector.connect(5000));
  //
  // Channel channel = connector.openChannel(TestProtocolFactory.PROTOCOL_ID);
  // for (int i = 0; i < COUNT; i++)
  // {
  // Buffer buffer = provideBuffer();
  // ByteBuffer byteBuffer = buffer.startPutting(channel.getChannelIndex());
  // byteBuffer.putInt(1970);
  // channel.handleBuffer(buffer);
  // }
  //
  // assertTrue(counter.await(2, TimeUnit.SECONDS));
  // }

  public void testStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    getAcceptor().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof AcceptorConnectorsEvent)
        {
          AcceptorConnectorsEvent e = (AcceptorConnectorsEvent)event;
          e.getAcceptedConnector().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof ConnectorChannelsEvent)
              {
                ConnectorChannelsEvent e = (ConnectorChannelsEvent)event;
                if (e.getType() == ConnectorChannelsEvent.Type.OPENED)
                {
                  inputStream[0] = new ChannelInputStream(e.getChannel(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    Channel channel = getConnector().openChannel();
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

  /**
   * TODO Fails occasionally ;-( Caused by: java.lang.IllegalStateException:
   * selectionKey == null
   */
  public void testTextStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    getAcceptor().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof AcceptorConnectorsEvent)
        {
          AcceptorConnectorsEvent e = (AcceptorConnectorsEvent)event;
          e.getAcceptedConnector().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof ConnectorChannelsEvent)
              {
                ConnectorChannelsEvent e = (ConnectorChannelsEvent)event;
                if (e.getType() == ConnectorChannelsEvent.Type.OPENED)
                {
                  inputStream[0] = new ChannelInputStream(e.getChannel(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    Channel channel = getConnector().openChannel();
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

    getAcceptor().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof AcceptorConnectorsEvent)
        {
          AcceptorConnectorsEvent e = (AcceptorConnectorsEvent)event;
          e.getAcceptedConnector().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof ConnectorChannelsEvent)
              {
                ConnectorChannelsEvent e = (ConnectorChannelsEvent)event;
                if (e.getType() == ConnectorChannelsEvent.Type.OPENED)
                {
                  inputStream[0] = new ChannelInputStream(e.getChannel(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    final Channel channel = getConnector().openChannel();
    assertTrue(counter.await(2, TimeUnit.SECONDS));
    assertNotNull(inputStream[0]);

    new Thread()
    {
      public void run()
      {
        try
        {
          ChannelOutputStream outputStream = new ChannelOutputStream(channel, container.getBufferProvider());
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

    getAcceptor().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof AcceptorConnectorsEvent)
        {
          AcceptorConnectorsEvent e = (AcceptorConnectorsEvent)event;
          e.getAcceptedConnector().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof ConnectorChannelsEvent)
              {
                ConnectorChannelsEvent e = (ConnectorChannelsEvent)event;
                if (e.getType() == ConnectorChannelsEvent.Type.OPENED)
                {
                  inputStream[0] = new ChannelInputStream(e.getChannel(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    Channel channel = getConnector().openChannel();
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
