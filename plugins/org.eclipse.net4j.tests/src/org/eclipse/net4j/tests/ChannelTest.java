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

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.concurrent.MonitoredThread;
import org.eclipse.net4j.util.concurrent.MonitoredThread.MultiThreadMonitor;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ChannelTest extends AbstractProtocolTest
{
  private IConnector connector;

  private List<TestSignalProtocol> protocols;

  public ChannelTest()
  {
  }

  @Override
  protected boolean useJVMTransport()
  {
    return false;
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    connector = startTransport();
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

  protected TestSignalProtocol openTestSignalProtocol()
  {
    TestSignalProtocol protocol = new TestSignalProtocol(connector);
    synchronized (protocols)
    {
      protocols.add(protocol);
    }

    return protocol;
  }

  // public void testOpenChannel() throws Exception
  // {
  // TestSignalProtocol protocol = openTestSignalProtocol();
  // assertActive(protocol);
  // }
  //
  // public void testOpenChannel1000() throws Exception
  // {
  // disableConsole();
  // for (int i = 0; i < 1000; i++)
  // {
  // IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
  // testOpenChannel();
  // }
  // }
  //
  // public void testCloseChannel() throws Exception
  // {
  // TestSignalProtocol protocol = openTestSignalProtocol();
  // assertActive(protocol);
  //
  // protocol.close();
  // assertInactive(protocol);
  // }
  //
  // public void testCloseChannel1000() throws Exception
  // {
  // disableConsole();
  // for (int i = 0; i < 1000; i++)
  // {
  // IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
  // testCloseChannel();
  // }
  // }

  // public void testOpenChannel() throws Exception
  // {
  // TestSignalProtocol protocol = openTestSignalProtocol();
  // assertActive(protocol);
  // }
  //
  // public void testOpenChannel1000() throws Exception
  // {
  // disableConsole();
  // for (int i = 0; i < 1000; i++)
  // {
  // IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
  // testOpenChannel();
  // }
  // }
  //
  // public void testCloseChannel() throws Exception
  // {
  // TestSignalProtocol protocol = openTestSignalProtocol();
  // assertActive(protocol);
  //
  // protocol.close();
  // assertInactive(protocol);
  // }
  //
  // public void testCloseChannel1000() throws Exception
  // {
  // disableConsole();
  // for (int i = 0; i < 1000; i++)
  // {
  // IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
  // testCloseChannel();
  // }
  // }
  //
  // public void testMultiThread() throws Exception
  // {
  // MultiThreadMonitor threadMonitor = new MultiThreadMonitor(2000L);
  // for (int i = 0; i < 100; i++)
  // {
  // threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor)
  // {
  // @Override
  // protected void doRun() throws Exception
  // {
  // for (int i = 0; i < 100; i++)
  // {
  // IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i);
  // TestSignalProtocol protocol = openTestSignalProtocol();
  // assertActive(protocol);
  //
  // protocol.close();
  // assertInactive(protocol);
  // heartBeat();
  // }
  // }
  // });
  // }
  //
  // disableConsole();
  // threadMonitor.run();
  // enableConsole();
  // }

  public void testMultiThreadWithTinyData() throws Exception
  {
    MultiThreadMonitor threadMonitor = new MultiThreadMonitor(2000L)
    {
      @Override
      protected void handleTimeoutExpiration(MonitoredThread thread)
      {
        String name = thread.getName();
        System.out.println("DEADLOCK: " + name);
        super.handleTimeoutExpiration(thread);
      }
    };

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

            byte[] data = TinyData.getBytes();
            byte[] result = new ArrayRequest(protocol, data).send();
            assertTrue(Arrays.equals(data, result));

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

  // public void testMultiThreadWithDataLoop() throws Exception
  // {
  // MultiThreadMonitor threadMonitor = new MultiThreadMonitor(2000L, 10L);
  // for (int i = 0; i < 100; i++)
  // {
  // threadMonitor.addThread(new MonitoredThread("TEST-THREAD-" + i, threadMonitor)
  // {
  // @Override
  // protected void doRun() throws Exception
  // {
  // for (int i = 0; i < 10; i++)
  // {
  // long start = System.currentTimeMillis();
  // TestSignalProtocol protocol = openTestSignalProtocol();
  // assertActive(protocol);
  //
  // for (int j = 0; j < 50; j++)
  // {
  // byte[] data = TinyData.getBytes();
  // byte[] result = new ArrayRequest(protocol, data).send();
  // assertTrue(Arrays.equals(data, result));
  //
  // heartBeat();
  // ConcurrencyUtil.sleep(10L);
  // }
  //
  // protocol.close();
  // assertInactive(protocol);
  // long stop = System.currentTimeMillis();
  // IOUtil.OUT().println(Thread.currentThread().getName() + ": " + i + " (" + (stop - start) + ")");
  // }
  // }
  // });
  // }
  //
  // disableConsole();
  // threadMonitor.run();
  // enableConsole();
  // }
}
