/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelInputStream;
import org.eclipse.net4j.channel.ChannelOutputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;

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
@SuppressWarnings("unchecked")
public abstract class TransportTest extends AbstractProtocolTest
{
  public TransportTest()
  {
  }

  @Override
  protected abstract boolean useJVMTransport();

  protected IBuffer provideBuffer()
  {
    IBufferProvider bufferProvider = Net4jUtil.getBufferProvider(container);
    return bufferProvider.provideBuffer();
  }

  public void testConnect() throws Exception
  {
    startTransport();
  }

  public void testSendBuffer() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel();
    for (int i = 0; i < 3; i++)
    {
      IBuffer buffer = provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getID());
      byteBuffer.putInt(1970);
      channel.sendBuffer(buffer);
    }
  }

  public void testHandleBuffer() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    container.registerFactory(new ServerTestProtocolFactory(counter));
    container.registerFactory(new ClientTestProtocolFactory());
    startTransport();

    IChannel channel = getConnector().openChannel(ClientTestProtocolFactory.TYPE, null);
    for (int i = 0; i < COUNT; i++)
    {
      IBuffer buffer = provideBuffer();
      ByteBuffer byteBuffer = buffer.startPutting(channel.getID());
      byteBuffer.putInt(1970);
      channel.sendBuffer(buffer);
      sleep(50);
    }

    assertEquals(true, counter.await(2, TimeUnit.SECONDS));
  }

  public void testStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    getAcceptor().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof IContainerEvent)
        {
          IContainerEvent<IConnector> e = (IContainerEvent)event;
          e.getDeltaElement().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent)
              {
                IContainerEvent<IChannel> e = (IContainerEvent)event;
                if (e.getDeltaKind() == IContainerDelta.Kind.ADDED)
                {
                  inputStream[0] = new ChannelInputStream(e.getDeltaElement(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    IChannel channel = getConnector().openChannel();
    assertEquals(true, counter.await(2, TimeUnit.SECONDS));
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
        msg(line);
      }

      isr.close();
    }
    catch (RuntimeException ex)
    {
      IOUtil.print(ex);
    }
  }

  /**
   * TODO Fails occasionally ;-( Caused by: java.lang.IllegalStateException: selectionKey == null
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
        if (event instanceof IContainerEvent)
        {
          IContainerEvent<IConnector> e = (IContainerEvent)event;
          e.getDeltaElement().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent)
              {
                IContainerEvent<IChannel> e = (IContainerEvent)event;
                if (e.getDeltaKind() == IContainerDelta.Kind.ADDED)
                {
                  inputStream[0] = new ChannelInputStream(e.getDeltaElement(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    IChannel channel = getConnector().openChannel();
    assertEquals(true, counter.await(2, TimeUnit.SECONDS));
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
        msg(line);
      }

      isr.close();
    }
    catch (RuntimeException ex)
    {
      IOUtil.print(ex);
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
        if (event instanceof IContainerEvent)
        {
          IContainerEvent<IConnector> e = (IContainerEvent)event;
          e.getDeltaElement().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent)
              {
                IContainerEvent<IChannel> e = (IContainerEvent)event;
                if (e.getDeltaKind() == IContainerDelta.Kind.ADDED)
                {
                  inputStream[0] = new ChannelInputStream(e.getDeltaElement(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    final IChannel channel = getConnector().openChannel();
    assertEquals(true, counter.await(2, TimeUnit.SECONDS));
    assertNotNull(inputStream[0]);

    new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          IBufferProvider bufferProvider = Net4jUtil.getBufferProvider(container);
          ChannelOutputStream outputStream = new ChannelOutputStream(channel, bufferProvider);
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
          IOUtil.print(ex);
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
        msg(line);
      }

      isr.close();
    }
    catch (RuntimeException ex)
    {
      IOUtil.print(ex);
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
        if (event instanceof IContainerEvent)
        {
          IContainerEvent<IConnector> e = (IContainerEvent)event;
          e.getDeltaElement().addListener(new IListener()
          {
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent)
              {
                IContainerEvent<IChannel> e = (IContainerEvent)event;
                if (e.getDeltaKind() == IContainerDelta.Kind.ADDED)
                {
                  inputStream[0] = new ChannelInputStream(e.getDeltaElement(), 2000);
                  counter.countDown();
                }
              }
            }
          });
        }
      }
    });

    IChannel channel = getConnector().openChannel();
    assertEquals(true, counter.await(2, TimeUnit.SECONDS));

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

    msg(new String(b));
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends TransportTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class JVM extends TransportTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return true;
    }
  }
}
