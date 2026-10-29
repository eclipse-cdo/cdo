/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019, 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Test;

/**
 * @author Eike Stepper
 */
public class SignalMonitorTest extends AbstractConfigTest
{
  public static final String PROTOCOL_TYPE = "protocol"; //$NON-NLS-1$

  public static final short SIGNAL_1 = 1;

  public static final short SIGNAL_2 = 2;

  public static final short SIGNAL_3 = 3;

  private CountDownLatch pendingConfirmationStarted;

  private CountDownLatch releasePendingConfirmation;

  /**
   * Creates this test class under every supported transport configuration.
   *
   * @return the configured test suite
   */
  public static Test suite()
  {
    @SuppressWarnings("unchecked")
    Net4jTestSuite suite = new Net4jTestSuite(SignalMonitorTest.class.getName(), JVM.class, TCP.class, SSL.class, WS.class, WSS.class);
    suite.addTestSuite(SignalMonitorTest.class);
    return suite;
  }

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new ServerProtocolFactory(PROTOCOL_TYPE)
    {
      @Override
      public Object create(String description) throws ProductCreationException
      {
        return new ServerProtocol(pendingConfirmationStarted, releasePendingConfirmation);
      }
    });

    return container;
  }

  public void testMonitorProgress() throws Exception
  {
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());

    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_1)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
      {
        monitor.begin();

        try
        {
          out.writeBoolean(true);
          monitor.worked();
        }
        finally
        {
          monitor.done();
        }
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        monitor.begin();

        try
        {
          boolean result = in.readBoolean();
          monitor.worked();
          return result;
        }
        finally
        {
          monitor.done();
        }
      }
    };

    TestMonitor monitor = new TestMonitor(Thread.currentThread());

    try
    {
      Boolean result = request.send(4000L, monitor);
      monitor.operationCompleted();

      assertEquals("Unexpected request result", Boolean.TRUE, result); //$NON-NLS-1$
      assertTrue("No intermediate remote progress update was received", monitor.hasIntermediateRemoteProgress()); //$NON-NLS-1$
      assertTrue("Monitor work regressed", monitor.isMonotonic()); //$NON-NLS-1$
      assertEquals("Monitor did not reach completion", monitor.getTotalWork(), monitor.getWork(), 0.0); //$NON-NLS-1$
      assertTrue("Monitor.done() was not called", monitor.isDone()); //$NON-NLS-1$
    }
    finally
    {
      protocol.close();
    }
  }

  public void testProtocolDeactivationUnblocksPendingConfirmation() throws Exception
  {
    pendingConfirmationStarted = new CountDownLatch(1);
    releasePendingConfirmation = new CountDownLatch(1);
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread requestThread = new Thread(() -> {
      try
      {
        new RequestWithConfirmation<Boolean>(protocol, SIGNAL_3)
        {
          @Override
          protected void requesting(ExtendedDataOutputStream out) throws Exception
          {
            out.writeBoolean(true);
          }

          @Override
          protected Boolean confirming(ExtendedDataInputStream in) throws Exception
          {
            return in.readBoolean();
          }
        }.send();
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Pending signal confirmation"); //$NON-NLS-1$

    try
    {
      requestThread.start();
      assertTrue("Server did not receive the pending request", pendingConfirmationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      protocol.close();
      requestThread.join(1000);
      assertFalse("Protocol deactivation left the confirmation reader blocked", requestThread.isAlive()); //$NON-NLS-1$
      assertTrue("The pending request did not receive a terminal protocol failure", failure.get() instanceof RemoteException); //$NON-NLS-1$
    }
    finally
    {
      releasePendingConfirmation.countDown();
      protocol.close();
      if (requestThread.isAlive())
      {
        requestThread.interrupt();
        requestThread.join(1000);
      }
    }
  }

  public void _testMonitorProgressAsync() throws Exception
  {
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    sleep(500);
    protocol.setTimeout(2000L);

    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_2)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
      {
        out.writeBoolean(true); // Async.
        out.writeLong(30000L); // Sleep.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        return in.readBoolean();
      }
    };

    request.send(2000L, new TestMonitor());
    sleep(200);
    protocol.close();
  }

  /**
   * @author Eike Stepper
   */
  private static final class ServerProtocol extends SignalProtocol<Object>
  {
    private final CountDownLatch pendingConfirmationStarted;

    private final CountDownLatch releasePendingConfirmation;

    private ServerProtocol(CountDownLatch pendingConfirmationStarted, CountDownLatch releasePendingConfirmation)
    {
      super(PROTOCOL_TYPE);
      this.pendingConfirmationStarted = pendingConfirmationStarted;
      this.releasePendingConfirmation = releasePendingConfirmation;
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      switch (signalID)
      {
      case SIGNAL_1:
        return new IndicationWithMonitoring(this, SIGNAL_1)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
          {
            monitor.begin(1 + 100);

            try
            {
              in.readBoolean();
              monitor.worked();

              for (int i = 0; i < 100; i++)
              {
                sleep(25);
                monitor.worked();
              }
            }
            finally
            {
              monitor.done();
            }
          }

          @Override
          protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
          {
            monitor.begin();

            try
            {
              out.writeBoolean(true);
              monitor.worked();
            }
            finally
            {
              monitor.done();
            }
          }
        };

      case SIGNAL_2:
        return new IndicationWithMonitoring(this, SIGNAL_2)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
          {
            monitor.begin();

            try
            {
              Async async = in.readBoolean() ? monitor.forkAsync() : null;

              try
              {
                long sleep = in.readLong();
                Thread.sleep(sleep);
              }
              finally
              {
                if (async != null)
                {
                  async.stop();
                }
              }
            }
            finally
            {
              monitor.done();
            }
          }

          @Override
          protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
          {
            out.writeBoolean(true);
          }
        };

      case SIGNAL_3:
        return new IndicationWithResponse(this, SIGNAL_3)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            in.readBoolean();
            pendingConfirmationStarted.countDown();
            releasePendingConfirmation.await();
          }

          @Override
          protected void responding(ExtendedDataOutputStream out) throws Exception
          {
            out.writeBoolean(true);
          }
        };

      default:
        return super.createSignalReactor(signalID);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ClientProtocol extends SignalProtocol<Object>
  {
    public ClientProtocol()
    {
      super(PROTOCOL_TYPE);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestMonitor extends Monitor
  {
    private final Thread callerThread;

    private final List<Double> progress = Collections.synchronizedList(new ArrayList<>());

    private final AtomicBoolean operationCompleted = new AtomicBoolean();

    private final AtomicBoolean done = new AtomicBoolean();

    private volatile boolean intermediateRemoteProgress;

    private TestMonitor()
    {
      this(Thread.currentThread());
    }

    private TestMonitor(Thread callerThread)
    {
      this.callerThread = callerThread;
    }

    @Override
    public OMMonitor begin(double totalWork)
    {
      synchronized (progress)
      {
        super.begin(totalWork);
        progress.add(getWork());
        return this;
      }
    }

    @Override
    public void worked(double work)
    {
      synchronized (progress)
      {
        super.worked(work);
        double currentWork = getWork();
        progress.add(currentWork);

        if (Thread.currentThread() != callerThread && !operationCompleted.get() && currentWork > 0 && currentWork < getTotalWork())
        {
          intermediateRemoteProgress = true;
        }
      }
    }

    @Override
    public void done()
    {
      synchronized (progress)
      {
        super.done();
        progress.add(getWork());
        done.set(true);
      }
    }

    private void operationCompleted()
    {
      operationCompleted.set(true);
    }

    private boolean hasIntermediateRemoteProgress()
    {
      return intermediateRemoteProgress;
    }

    private boolean isMonotonic()
    {
      synchronized (progress)
      {
        double previous = 0;
        for (double current : progress)
        {
          if (Double.isNaN(current) || Double.isInfinite(current) || current < previous || current < 0 || current > getTotalWork())
          {
            return false;
          }

          previous = current;
        }

        return true;
      }
    }

    private boolean isDone()
    {
      return done.get();
    }
  }
}
