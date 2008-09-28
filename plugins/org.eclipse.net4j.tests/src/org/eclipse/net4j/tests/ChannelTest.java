/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.MonitoredThread;
import org.eclipse.net4j.util.concurrent.MonitoredThread.MultiThreadMonitor;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

import org.eclipse.spi.net4j.InternalConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class ChannelTest extends AbstractProtocolTest
{
  private IConnector connector;

  private List<TestSignalProtocol> protocols;

  public ChannelTest()
  {
  }

  public void testSingleThreadNoData() throws Exception
  {
    final DeactivationListener deactivationListener = new DeactivationListener();

    TestSignalProtocol protocol = openTestSignalProtocol();
    protocol.addListener(deactivationListener);
    assertActive(protocol);

    IChannel channel = protocol.getChannel();
    channel.addListener(deactivationListener);
    assertActive(channel);

    InternalConnector serverConnector = (InternalConnector)getAcceptor().getAcceptedConnectors()[0];
    List<IChannel> serverChannels = serverConnector.getChannels();
    assertEquals(1, serverChannels.size());

    IChannel serverChannel = serverChannels.get(0);
    serverChannel.addListener(deactivationListener);
    assertActive(serverChannel);

    TestSignalProtocol serverProtocol = (TestSignalProtocol)serverChannel.getReceiveHandler();
    serverProtocol.addListener(deactivationListener);
    assertActive(serverProtocol);

    protocol.close();
    assertInactive(protocol);
    assertEquals(0, serverConnector.getChannels().size());
    assertInactive(channel);
    assertInactive(protocol);
    assertInactive(serverChannel);
    assertInactive(serverProtocol);

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

  public void testSingleThreadNoData1000() throws Exception
  {
    disableConsole();
    for (int i = 0; i < 1000; i++)
    {
      IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
      testSingleThreadNoData();
    }
  }

  public void testSingleThreadTinyData() throws Exception
  {
    TestSignalProtocol protocol = openTestSignalProtocol();
    assertActive(protocol);

    byte[] data = TinyData.getBytes();
    byte[] result = new ArrayRequest(protocol, data).send();
    assertTrue(Arrays.equals(data, result));

    protocol.close();
    assertInactive(protocol);
  }

  public void testSingleThreadTinyData1000() throws Exception
  {
    disableConsole();
    for (int i = 0; i < 1000; i++)
    {
      IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
      testSingleThreadTinyData();
    }
  }

  public void testMultiThreadNoData() throws Exception
  {
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(5000L);
    for (int i = 0; i < 100; i++)
    {
      threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor)
      {
        @Override
        protected void doRun() throws Exception
        {
          for (int i = 0; i < 100; i++)
          {
            IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
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
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(5000L);

    for (int i = 0; i < 100; i++)
    {
      threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor)
      {
        @Override
        protected void doRun() throws Exception
        {
          for (int i = 0; i < 100; i++)
          {
            IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
            TestSignalProtocol protocol = openTestSignalProtocol();
            assertActive(protocol);
            heartBeat();

            byte[] data = TinyData.getBytes();
            byte[] result = new ArrayRequest(protocol, data).send();
            assertTrue(Arrays.equals(data, result));
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
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(5000L, 10L);
    for (int i = 0; i < 100; i++)
    {
      threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor)
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
              assertTrue(Arrays.equals(data, result));

              heartBeat();
              ConcurrencyUtil.sleep(10L);
            }

            protocol.close();
            assertInactive(protocol);
            long stop = System.currentTimeMillis();
            IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i + " (" + (stop - start) + ")");
          }
        }
      });
    }

    disableConsole();
    threadMonitor.run();
    enableConsole();
  }

  @Override
  protected abstract boolean useJVMTransport();

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    connector = startTransport();
    connector.setChannelTimeout(100000L);
    protocols = new ArrayList<TestSignalProtocol>();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    for (TestSignalProtocol protocol : protocols)
    {
      protocol.close();
    }

    connector.disconnect();
    connector = null;
    protocols = null;
    super.doTearDown();
  }

  private TestSignalProtocol openTestSignalProtocol()
  {
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
            protocol.getChannel().removeListener(this);
            boolean removed = protocols.remove(protocol);
            assertEquals(true, removed);
          }
        }
      });
    }

    return protocol;
  }

  /**
   * @author Eike Stepper
   */
  private static final class DeactivationListener extends LifecycleEventAdapter
  {
    private Set<ILifecycle> deactivatedSet = new HashSet<ILifecycle>();

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
      deactivatedSet.add(lifecycle);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends ChannelTest
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
  public static final class JVM extends ChannelTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return true;
    }
  }
}
