/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelInputStream;
import org.eclipse.net4j.channel.ChannelOutputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.data.HugeData;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.eclipse.spi.net4j.Connector;
import org.eclipse.spi.net4j.Protocol;
import org.eclipse.spi.net4j.ServerProtocolFactory;

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
public abstract class TransportTest extends AbstractProtocolTest
{
  public TransportTest()
  {
  }

  @Override
  protected abstract boolean useJVMTransport();

  protected IBuffer provideBuffer()
  {
    return provideBuffer(null);
  }

  protected IBuffer provideBuffer(IConnector iConnector)
  {
    IBuffer buffer = null;
    if (!useJVMTransport() && useSSLTransport())
    {
      // cannot use buffer provider from net4j need to use SSL Buffer inside the SSLConnector.
      buffer = ((Connector)iConnector).provideBuffer();
    }
    else
    {
      IBufferProvider bufferProvider = Net4jUtil.getBufferProvider(container);
      buffer = bufferProvider.provideBuffer();
    }

    return buffer;
  }

  private void registerClientFactory(IFactory factory)
  {
    if (!useJVMTransport() && useSSLTransport())
    {
      // need separate container between client and server for SSL.
      separateContainer.registerFactory(factory);
    }
    else
    {
      container.registerFactory(factory);
    }
  }

  protected IBufferProvider provideBufferProvider(IConnector iConnector)
  {
    IBufferProvider bufferProvider = null;
    if (!useJVMTransport() && useSSLTransport())
    {
      // cannot use buffer provider from net4j need to use SSL Buffer inside the SSLConnector.
      bufferProvider = ((Connector)iConnector).getConfig().getBufferProvider();
    }
    else
    {
      bufferProvider = Net4jUtil.getBufferProvider(container);
    }

    return bufferProvider;
  }

  public void testConnect() throws Exception
  {
    startTransport();
  }

  public void testSendBuffer() throws Exception
  {
    startTransport();
    IConnector connecter = getConnector();
    IChannel channel = connecter.openChannel();
    for (int i = 0; i < 3; i++)
    {
      IBuffer buffer = provideBuffer(connecter);

      ByteBuffer byteBuffer = buffer.startPutting(channel.getID());
      byteBuffer.putInt(1970);
      channel.sendBuffer(buffer);
    }
  }

  public void testSendEmptyBuffer() throws Exception
  {
    startTransport();
    IConnector connecter = getConnector();
    IChannel channel = connecter.openChannel();
    for (int i = 0; i < 3; i++)
    {
      IBuffer buffer = provideBuffer(connecter);
      buffer.startPutting(channel.getID());
      channel.sendBuffer(buffer);
    }
  }

  public void testSendEmptyBuffer2() throws Exception
  {
    startTransport();
    IConnector connecter = getConnector();
    IChannel channel = connecter.openChannel();
    for (int i = 0; i < 3; i++)
    {
      IBuffer buffer = provideBuffer(connecter);
      channel.sendBuffer(buffer);
    }
  }

  public void testHandleBuffer() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    container.registerFactory(new TestProtocol.ServerFactory(counter));
    // need to handle about separating container between client and server for SSL.
    registerClientFactory(new TestProtocol.ClientFactory());
    startTransport();
    IConnector iConnecter = getConnector();
    IChannel channel = iConnecter.openChannel(TestProtocol.ClientFactory.TYPE, null);
    for (int i = 0; i < COUNT; i++)
    {
      IBuffer buffer = provideBuffer(iConnecter);
      ByteBuffer byteBuffer = buffer.startPutting(channel.getID());
      byteBuffer.putInt(1970);
      channel.sendBuffer(buffer);
      sleep(50);
    }

    assertEquals(true, counter.await(2, TimeUnit.SECONDS));
  }

  public void testHandleEmptyBuffer() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    container.registerFactory(new TestProtocol.ServerFactory(counter));
    // need to handle about separating container between client and server for SSL.
    registerClientFactory(new TestProtocol.ClientFactory());

    startTransport();
    IConnector connecter = getConnector();
    IChannel channel = connecter.openChannel(TestProtocol.ClientFactory.TYPE, null);
    for (int i = 0; i < COUNT; i++)
    {
      IBuffer buffer = provideBuffer(connecter);
      buffer.startPutting(channel.getID());
      channel.sendBuffer(buffer);
      sleep(50);
    }

    assertEquals(COUNT, counter.getCount());
  }

  public void testHandleEmptyBuffer2() throws Exception
  {
    final int COUNT = 3;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    container.registerFactory(new TestProtocol.ServerFactory(counter));
    // need to handle about separating container between client and server for SSL.
    registerClientFactory(new TestProtocol.ClientFactory());

    startTransport();
    IConnector connecter = getConnector();
    IChannel channel = connecter.openChannel(TestProtocol.ClientFactory.TYPE, null);
    for (int i = 0; i < COUNT; i++)
    {
      IBuffer buffer = provideBuffer(connecter);
      channel.sendBuffer(buffer);
      sleep(50);
    }

    assertEquals(COUNT, counter.getCount());
  }

  public void testStreaming() throws Exception
  {
    final int COUNT = 1;
    final CountDownLatch counter = new CountDownLatch(COUNT);
    final ChannelInputStream[] inputStream = new ChannelInputStream[1];

    getAcceptor().addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof IContainerEvent<?>)
        {
          @SuppressWarnings("unchecked")
          IContainerEvent<IConnector> e = (IContainerEvent<IConnector>)event;
          e.getDeltaElement().addListener(new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent<?>)
              {
                @SuppressWarnings("unchecked")
                IContainerEvent<IChannel> e = (IContainerEvent<IChannel>)event;
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
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof IContainerEvent<?>)
        {
          @SuppressWarnings("unchecked")
          IContainerEvent<IConnector> e = (IContainerEvent<IConnector>)event;
          e.getDeltaElement().addListener(new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent<?>)
              {
                @SuppressWarnings("unchecked")
                IContainerEvent<IChannel> e = (IContainerEvent<IChannel>)event;
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
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof IContainerEvent<?>)
        {
          @SuppressWarnings("unchecked")
          IContainerEvent<IConnector> e = (IContainerEvent<IConnector>)event;
          e.getDeltaElement().addListener(new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent<?>)
              {
                @SuppressWarnings("unchecked")
                IContainerEvent<IChannel> e = (IContainerEvent<IChannel>)event;
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

    final IConnector iConnector = getConnector();
    final IChannel channel = iConnector.openChannel();
    assertEquals(true, counter.await(2, TimeUnit.SECONDS));
    assertNotNull(inputStream[0]);

    new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          IBufferProvider bufferProvider = provideBufferProvider(iConnector);
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
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof IContainerEvent<?>)
        {
          @SuppressWarnings("unchecked")
          IContainerEvent<IConnector> e = (IContainerEvent<IConnector>)event;
          e.getDeltaElement().addListener(new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              if (event instanceof IContainerEvent<?>)
              {
                @SuppressWarnings("unchecked")
                IContainerEvent<IChannel> e = (IContainerEvent<IChannel>)event;
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
  public static final class TestProtocol extends Protocol<CountDownLatch>
  {
    public TestProtocol(CountDownLatch counter)
    {
      super(ServerFactory.TYPE);
      setInfraStructure(counter);
    }

    @Override
    public void handleBuffer(IBuffer buffer)
    {
      IOUtil.OUT().println("BUFFER ARRIVED"); //$NON-NLS-1$
      buffer.release();
      getInfraStructure().countDown();
    }

    /**
     * @author Eike Stepper
     */
    public static class ServerFactory extends ServerProtocolFactory
    {
      public static final String TYPE = "test.protocol"; //$NON-NLS-1$

      private CountDownLatch counter;

      public ServerFactory(CountDownLatch counter)
      {
        super(TYPE);
        this.counter = counter;
      }

      @Override
      public TestProtocol create(String description) throws ProductCreationException
      {
        return new TestProtocol(counter);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class ClientFactory extends ClientProtocolFactory
    {
      public static final String TYPE = ServerFactory.TYPE;

      public ClientFactory()
      {
        super(TYPE);
      }

      @Override
      public TestProtocol create(String description) throws ProductCreationException
      {
        return new TestProtocol(null);
      }
    }
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

    @Override
    protected boolean useSSLTransport()
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

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static final class SSL extends TransportTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return true;
    }
  }
}
