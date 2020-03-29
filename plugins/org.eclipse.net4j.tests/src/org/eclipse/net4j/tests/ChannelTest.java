/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.net4j.ITransportConfigAware;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.SignalFinishedEvent;
import org.eclipse.net4j.signal.SignalProtocol.InvalidSignalIDException;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.data.HugeData;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.PartialReadRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.concurrent.MonitoredThread;
import org.eclipse.net4j.util.concurrent.MonitoredThread.MultiThreadMonitor;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import org.eclipse.internal.net4j.buffer.BufferPoolFactory;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.InternalConnector;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public class ChannelTest extends AbstractConfigTest
{
  private static final long TIMEOUT = 20000;

  private static final int THREADS = 40;

  private List<TestSignalProtocol> protocols;

  public void testAllBufferSizes() throws Exception
  {
    disableConsole();

    TestSignalProtocol protocol = openTestSignalProtocol();
    assertActive(protocol);

    short bufferCapacity = ((ITransportConfigAware)getConnector()).getConfig().getBufferProvider().getBufferCapacity();

    byte[] data = HugeData.getBytes();
    assertEquals(true, data.length > 2 * bufferCapacity);

    for (int i = 1; i < data.length; i++)
    {
      // System.out.println(i);

      byte[] dataToSend = new byte[i];
      System.arraycopy(data, 0, dataToSend, 0, i);

      byte[] result = new ArrayRequest(protocol, dataToSend).send();
      assertEquals(true, Arrays.equals(dataToSend, result));
    }

    protocol.close();
    assertInactive(protocol);
    enableConsole();
  }

  public void testSingleThreadNoData() throws Exception
  {
    final LatchTimeOuter timeOuter = new LatchTimeOuter(4);
    final DeactivationListener deactivationListener = new DeactivationListener()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        super.onDeactivated(lifecycle);
        timeOuter.countDown();
      }
    };

    TestSignalProtocol protocol = openTestSignalProtocol();
    protocol.addListener(deactivationListener);
    assertActive(protocol);

    IChannel channel = protocol.getChannel();
    channel.addListener(deactivationListener);
    assertActive(channel);

    InternalConnector serverConnector = (InternalConnector)getAcceptor().getAcceptedConnectors()[0];
    Collection<IChannel> serverChannels = serverConnector.getChannels();
    assertEquals(1, serverChannels.size());

    IChannel serverChannel = serverChannels.iterator().next();
    serverChannel.addListener(deactivationListener);
    assertActive(serverChannel);

    TestSignalProtocol serverProtocol = (TestSignalProtocol)serverChannel.getReceiveHandler();
    serverProtocol.addListener(deactivationListener);
    assertActive(serverProtocol);

    protocol.close();
    assertInactive(protocol);
    assertInactive(channel);

    assertInactive(serverChannel);
    assertInactive(serverProtocol);
    assertEquals(0, serverConnector.getChannels().size());

    timeOuter.assertNoTimeOut();
    Set<ILifecycle> deactivatedSet = deactivationListener.getDeactivatedSet();
    assertEquals(true, deactivatedSet.contains(channel));
    assertEquals(true, deactivatedSet.contains(protocol));
    assertEquals(true, deactivatedSet.contains(serverChannel));
    assertEquals(true, deactivatedSet.contains(serverProtocol));

    synchronized (protocols)
    {
      assertEquals(0, protocols.size());
    }
  }

  public void testSingleThreadNoData100() throws Exception
  {
    disableConsole();
    for (int i = 0; i < 100; i++)
    {
      log(Thread.currentThread().getName() + ": " + i); //$NON-NLS-1$
      testSingleThreadNoData();
    }
  }

  public void testSingleThreadTinyData() throws Exception
  {
    TestSignalProtocol protocol = openTestSignalProtocol();
    assertActive(protocol);

    byte[] data = TinyData.getBytes();
    byte[] result = new ArrayRequest(protocol, data).send();
    assertEquals(true, Arrays.equals(data, result));

    protocol.close();
    assertInactive(protocol);
  }

  public void testSingleThreadTinyData100() throws Exception
  {
    disableConsole();
    for (int i = 0; i < 100; i++)
    {
      log(Thread.currentThread().getName() + ": " + i); //$NON-NLS-1$
      testSingleThreadTinyData();
    }
  }

  public void testSingleThreadHugeDataLong() throws Exception
  {
    TestSignalProtocol protocol = openTestSignalProtocol();
    assertActive(protocol);

    byte[] data = HugeData.getBytes();

    // Warm-up
    for (int i = 0; i < 1000; i++)
    {
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }

    final int SIGNALS = 10000;

    long start = System.currentTimeMillis();
    for (int i = 0; i < SIGNALS; i++)
    {
      byte[] result = new ArrayRequest(protocol, data).send();
      assertArrayEquals(data, result);
    }

    long duration = System.currentTimeMillis() - start;
    log("Millis for " + SIGNALS + " signals: " + duration);

    InternalChannel channel = (InternalChannel)protocol.getChannel();
    log("Sent buffers: " + channel.getSentBuffers());
    log("Received buffers: " + channel.getReceivedBuffers());

    protocol.close();
    assertInactive(protocol);
  }

  public void testMultiThreadNoData() throws Exception
  {
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(TIMEOUT);
    for (int i = 0; i < THREADS; i++)
    {
      threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor) //$NON-NLS-1$
      {
        @Override
        protected void doRun() throws Exception
        {
          for (int i = 0; i < 100; i++)
          {
            log(Thread.currentThread().getName() + ": " + i); //$NON-NLS-1$
            TestSignalProtocol protocol = openTestSignalProtocol();
            assertActive(protocol);

            protocol.close();
            assertInactive(protocol);
            heartBeat();
          }
        }
      });
    }

    disableConsole();
    threadMonitor.run();
    enableConsole();
  }

  public void testMultiThreadTinyData() throws Exception
  {
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(TIMEOUT);

    for (int i = 0; i < THREADS; i++)
    {
      threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor) //$NON-NLS-1$
      {
        @Override
        protected void doRun() throws Exception
        {
          for (int i = 0; i < 100; i++)
          {
            log(Thread.currentThread().getName() + ": " + i); //$NON-NLS-1$
            TestSignalProtocol protocol = openTestSignalProtocol();
            assertActive(protocol);
            heartBeat();

            byte[] data = TinyData.getBytes();
            byte[] result = new ArrayRequest(protocol, data).send();
            assertEquals(true, Arrays.equals(data, result));
            heartBeat();

            protocol.close();
            assertInactive(protocol);
            heartBeat();
          }
        }
      });
    }

    disableConsole();
    threadMonitor.run();
    enableConsole();
  }

  public void testMultiThreadDataLoop() throws Exception
  {
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(1000 * TIMEOUT, 10L);
    for (int i = 0; i < THREADS; i++)
    {
      threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor) //$NON-NLS-1$
      {
        @Override
        protected void doRun() throws Exception
        {
          for (int i = 0; i < 10; i++)
          {
            long start = System.currentTimeMillis();
            TestSignalProtocol protocol = openTestSignalProtocol();
            assertActive(protocol);

            for (int j = 0; j < 50; j++)
            {
              byte[] data = TinyData.getBytes();
              byte[] result = new ArrayRequest(protocol, data).send();
              assertEquals(true, Arrays.equals(data, result));

              heartBeat();
            }

            protocol.close();
            assertInactive(protocol);
            long stop = System.currentTimeMillis();
            log(Thread.currentThread().getName() + ": " + i + " (" + (stop - start) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          }
        }
      });
    }

    disableConsole();
    threadMonitor.run();
    enableConsole();
  }

  /**
   * This test case tests the following situation:
   * <p>
   * A client sends a signal
   */
  public void testStreamFitsInOneBuffer() throws Exception
  {
    CountDownLatch signalFinished = new CountDownLatch(1);
    AtomicReference<BufferUnderflowException> exception = new AtomicReference<>();

    acceptorContainer.registerFactory(new TestSignalProtocol.Factory()
    {
      @Override
      public TestSignalProtocol create(String description) throws ProductCreationException
      {
        TestSignalProtocol protocol = new TestSignalProtocol()
        {
          private int receivedBuffers;

          @Override
          public void handleBuffer(IBuffer buffer)
          {
            System.out.println("Received buffer " + receivedBuffers + " --> eos=" + buffer.isEOS());

            // After the first buffer wait until the signal has finished.
            if (receivedBuffers++ == 1)
            {
              await(signalFinished);
            }

            try
            {
              super.handleBuffer(buffer);
            }
            catch (BufferUnderflowException ex)
            {
              ex.printStackTrace();
              exception.set(ex);
            }
          }
        };

        protocol.setVersion(version);
        return protocol;
      }
    });

    TestSignalProtocol protocol = openTestSignalProtocol();

    InternalConnector serverConnector = (InternalConnector)getAcceptor().getAcceptedConnectors()[0];
    IChannel serverChannel = serverConnector.getChannels().iterator().next();
    TestSignalProtocol serverProtocol = (TestSignalProtocol)serverChannel.getReceiveHandler();
    serverProtocol.addListener(event -> {
      if (event instanceof SignalFinishedEvent)
      {
        signalFinished.countDown();
      }
    });

    byte[] hugeData = HugeData.getBytes();

    int headerSize = IBuffer.HEADER_SIZE;
    headerSize += 4; // Correlation ID.
    headerSize += 2; // Signal ID.
    headerSize += 4; // Array length.

    byte[] data = new byte[BufferPoolFactory.BUFFER_CAPACITY - headerSize];
    System.arraycopy(hugeData, 0, data, 0, data.length);

    new ArrayRequest(protocol, data, true).send(); // <-- Flush without EOS!
    assertSame(null, exception.get());
  }

  /**
   * This test case tests the following situation:
   * <p>
   * A client sends a signal
   */
  public void testPartialRead() throws Exception
  {
    CountDownLatch signalFinished = new CountDownLatch(1);
    AtomicReference<InvalidSignalIDException> exception = new AtomicReference<>();

    acceptorContainer.registerFactory(new TestSignalProtocol.Factory()
    {
      @Override
      public TestSignalProtocol create(String description) throws ProductCreationException
      {
        TestSignalProtocol protocol = new TestSignalProtocol()
        {
          private int receivedBuffers;

          @Override
          public void handleBuffer(IBuffer buffer)
          {
            System.out.println("Received buffer " + receivedBuffers + " --> eos=" + buffer.isEOS());

            // After the first buffer wait until the signal has finished.
            if (receivedBuffers++ == 1)
            {
              await(signalFinished);
            }

            try
            {
              super.handleBuffer(buffer);
            }
            catch (InvalidSignalIDException ex)
            {
              ex.printStackTrace();
              exception.set(ex);
            }
          }
        };

        protocol.setVersion(version);
        return protocol;
      }
    });

    TestSignalProtocol protocol = openTestSignalProtocol();

    InternalConnector serverConnector = (InternalConnector)getAcceptor().getAcceptedConnectors()[0];
    IChannel serverChannel = serverConnector.getChannels().iterator().next();
    TestSignalProtocol serverProtocol = (TestSignalProtocol)serverChannel.getReceiveHandler();
    serverProtocol.addListener(event -> {
      if (event instanceof SignalFinishedEvent)
      {
        signalFinished.countDown();
      }
    });

    new PartialReadRequest(protocol).send();
    assertSame(null, exception.get());
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    startTransport();
    getConnector().setOpenChannelTimeout(TIMEOUT);
    protocols = new ArrayList<>();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    try
    {
      if (protocols != null)
      {
        for (TestSignalProtocol protocol : new ArrayList<>(protocols))
        {
          protocol.close();
        }

        protocols = null;
      }

      if (connector != null)
      {
        connector.close();
        connector = null;
      }
    }
    finally
    {
      super.doTearDown();
    }
  }

  private TestSignalProtocol openTestSignalProtocol()
  {
    IConnector connector = getConnector();
    final TestSignalProtocol protocol = new TestSignalProtocol(connector);

    synchronized (protocols)
    {
      protocols.add(protocol);
      protocol.getChannel().addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          synchronized (protocols)
          {
            IChannel channel = protocol.getChannel();
            if (channel != null)
            {
              channel.removeListener(this);
            }

            boolean removed = protocols.remove(protocol);
            assertEquals(true, removed);
          }
        }
      });
    }

    return protocol;
  }

  private static void log(String message)
  {
    IOUtil.OUT().println(message);
  }

  /**
   * @author Eike Stepper
   */
  private static class DeactivationListener extends LifecycleEventAdapter
  {
    private Set<ILifecycle> deactivatedSet = new HashSet<>();

    public DeactivationListener()
    {
    }

    public Set<ILifecycle> getDeactivatedSet()
    {
      return deactivatedSet;
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      synchronized (deactivatedSet)
      {
        deactivatedSet.add(lifecycle);
        deactivatedSet.notifyAll();
      }
    }
  }
}
