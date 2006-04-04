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
package org.eclipse.net4j.tests;


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.core.Connector;
import org.eclipse.net4j.core.Request;
import org.eclipse.net4j.tests.protocol.Net4jTestProtocol;
import org.eclipse.net4j.tests.protocol.TestRequest;
import org.eclipse.net4j.util.thread.DeadlockDetector;

import junit.framework.TestCase;


public class SocketTest extends TestCase
{
  private static final boolean PERFORMANCE = true;

  // ValueHelper.sizeOf(VALUES) --> 363 Bytes
  private static final Object[] VALUES = { 1, 2, 3, 4, 5, 6, 7, 8, 9, "A rose is a rose is a...",
      1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9, "Eclipse 3", 'a', 'b', 'c', 'd', 'e', 'f', 'g',
      1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, "Net4j: An extensible signalling framework", true, false};

  private TestContainer serverContainer;

  private TestContainer[] clientContainers;

  private int workers;

  private Exception exception;

  private Thread deadlockDetector;

  //  static
  //  {
  //    System.out.println(ValueHelper.sizeOf(VALUES));
  //  }

  public SocketTest(String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    super.setUp();
    workers = 0;
    exception = null;

    if (DeadlockDetector.DETECTION)
    {
      deadlockDetector = new Thread("DeadlockDetector")
      {
        @Override
        public void run()
        {
          for (;;)
          {
            try
            {
              Thread.sleep(5000);
            }
            catch (InterruptedException ex)
            {
              return;
            }
            DeadlockDetector.dump();
          }
        }
      };
      deadlockDetector.setDaemon(true);
      deadlockDetector.start();
    }
  }

  protected void tearDown() throws Exception
  {
    if (clientContainers != null)
    {
      for (int i = 0; i < clientContainers.length; i++)
      {
        if (clientContainers[i] != null)
        {
          clientContainers[i].stop();
          clientContainers[i] = null;
        }
      }
    }

    if (serverContainer != null)
    {
      serverContainer.stop();
      serverContainer = null;
    }

    //    if (DeadlockDetector.DETECTION)
    //    {
    //      DeadlockDetector.dump();
    //    }

    if (deadlockDetector != null)
    {
      deadlockDetector.interrupt();
      deadlockDetector = null;
    }

    workers = 0;
    exception = null;
    super.tearDown();
  }

  protected void startContainers(int numberOfClients)
  {
    serverContainer = new TestContainer.SocketServer();
    clientContainers = new TestContainer.SocketClient[numberOfClients];
    for (int i = 0; i < numberOfClients; i++)
    {
      clientContainers[i] = new TestContainer.SocketClient("client"
          + (numberOfClients == 1 ? "" : "" + (i + 1)));
    }
  }

  protected void joinWorkers() throws InterruptedException, Exception
  {
    for (;;)
    {
      Thread.sleep(2000);
      synchronized (this)
      {
        if (workers == 0) break;
        if (exception != null)
        {
          exception.printStackTrace();
          throw exception;
        }
      }
    }
  }

  public static void echo(Channel channel, Object[] values)
  {
    Request request = new TestRequest(values);
    Object[] echo = (Object[]) channel.transmit(request);
    assertEquals(values, echo);
  }

  public static void assertEquals(Object[] values, Object[] echo)
  {
    assertEquals(values.length, echo.length);
    for (int i = 0; i < values.length; i++)
    {
      assertEquals(values[i], echo[i]);
    }
  }


  public class ChannelWorker extends Thread
  {
    private Channel channel;

    private Object[] values;

    private int numberOfRequests;

    public ChannelWorker(Connector connector, Object[] values, int numberOfRequests)
    {
      channel = connector.addChannel(Net4jTestProtocol.PROTOCOL_NAME);
      this.values = values;
      this.numberOfRequests = numberOfRequests;

      synchronized (SocketTest.this)
      {
        workers++;
      }

      start();
    }

    @Override
    public void run()
    {
      for (int i = 0; i < numberOfRequests; i++)
      {
        try
        {
          if (exception != null) return;
          echo(channel, values);
        }
        catch (Exception ex)
        {
          exception = ex;
        }
      }

      synchronized (SocketTest.this)
      {
        workers--;
      }
    }
  }

  public void stressTest() throws Exception
  {
    final int NUMBER_OF_CLIENTS = 4;
    final int CHANNELS_PER_CLIENT = 4;
    final int REQUESTS_PER_CHANNEL = 10000;

    startContainers(NUMBER_OF_CLIENTS);
    long t0 = System.currentTimeMillis();
    for (int i = 0; i < NUMBER_OF_CLIENTS; i++)
    {
      for (int j = 0; j < CHANNELS_PER_CLIENT; j++)
      {
        new ChannelWorker(clientContainers[i].getConnector(), VALUES, REQUESTS_PER_CHANNEL);
        Thread.sleep(200);
      }
    }

    joinWorkers();
    long t1 = System.currentTimeMillis();
    System.out.println("Duration: " + (t1 - t0) + " millisec");
  }

  public void performanceTest() throws Exception
  {
    startContainers(1);
    Connector connector = clientContainers[0].getConnector();
    Channel channel = connector.addChannel(Net4jTestProtocol.PROTOCOL_NAME);

    double total = 0.0;
    for (int i = 1; i <= 1000; i++)
    {
      long t0 = System.nanoTime();
      echo(channel, VALUES);
      long t1 = System.nanoTime();

      double duration = t1 - t0;
      total += duration;
      System.out.println("Run " + i + ": " + (duration / 1000000d));
    }

    System.out.println("Average: " + (total / 1000000000d) + " millisec");
  }

  public final void testSocket() throws Exception
  {
    if (!PERFORMANCE)
    {
      stressTest();
    }
    else
    {
      performanceTest();
    }
  }

  public final void testEmbedded() throws Exception
  {
    serverContainer = new TestContainer.Embedded();
    Connector connector = serverContainer.getConnector();
    Channel channel = connector.addChannel(Net4jTestProtocol.PROTOCOL_NAME);

    double total = 0.0;
    for (int i = 1; i <= 1000; i++)
    {
      long t0 = System.nanoTime();
      echo(channel, VALUES);
      long t1 = System.nanoTime();

      double duration = t1 - t0;
      total += duration;
      System.out.println("Run " + i + ": " + (duration / 1000000d));
    }

    System.out.println("Average: " + (total / 1000000000d) + " millisec");
  }
}
