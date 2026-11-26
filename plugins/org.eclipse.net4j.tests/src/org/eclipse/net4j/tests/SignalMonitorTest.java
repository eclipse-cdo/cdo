/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019, 2020, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.MonitorCanceledIndication;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalProtocolDeactivatedException;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.util.concurrent.DelegatingExecutorService;
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
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

  public static final short SIGNAL_4 = 4;

  public static final short SIGNAL_5 = 5;

  public static final short SIGNAL_6 = 6;

  public static final short SIGNAL_7 = 7;

  public static final short SIGNAL_8 = 8;

  public static final short SIGNAL_9 = 9;

  private CountDownLatch pendingConfirmationStarted;

  private CountDownLatch releasePendingConfirmation;

  private CountDownLatch pendingIndicationStarted;

  private CountDownLatch releasePendingIndication;

  private CountDownLatch serverProtocolDeactivated;

  private CountDownLatch indicationFailureHandled;

  private AtomicReference<Throwable> indicationFailure;

  private AtomicReference<Throwable> expectedIndicationFailure;

  private AtomicReference<Throwable> expectedPropagationFailure;

  private CountDownLatch cancellationIndicationStarted;

  private CountDownLatch cancellationObserved;

  private CountDownLatch releaseCancellationIndication;

  private CountDownLatch progressIndicationStarted;

  private CountDownLatch releaseProgressIndication;

  private CountDownLatch progressOutputEntered;

  private CountDownLatch releaseProgressOutput;

  private AtomicBoolean rejectNextProgress;

  private AtomicInteger progressReportSubmissions;

  private AtomicBoolean holdNextSignalReactor;

  private CountDownLatch signalReactorQueued;

  private CountDownLatch releaseSignalReactor;

  private CountDownLatch cancellationProcessed;

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
        return new ServerProtocol(pendingConfirmationStarted, releasePendingConfirmation, pendingIndicationStarted, releasePendingIndication,
            serverProtocolDeactivated, indicationFailureHandled, indicationFailure, expectedIndicationFailure, expectedPropagationFailure,
            cancellationIndicationStarted, cancellationObserved, releaseCancellationIndication, progressIndicationStarted, releaseProgressIndication,
            progressOutputEntered, releaseProgressOutput, rejectNextProgress, progressReportSubmissions, holdNextSignalReactor, signalReactorQueued,
            releaseSignalReactor, cancellationProcessed);
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

  public void testRemoteCancellationIsReportedOnceWithoutPollingWorkers() throws Exception
  {
    cancellationIndicationStarted = new CountDownLatch(1);
    cancellationObserved = new CountDownLatch(1);
    releaseCancellationIndication = new CountDownLatch(1);
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    Monitor monitor = new Monitor();
    AtomicInteger cancellationSubmissions = new AtomicInteger();
    CountDownLatch cancellationSubmitted = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_6)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor requestMonitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor requestMonitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected ExecutorService getCancelationExecutorService()
      {
        return new DelegatingExecutorService(super.getCancelationExecutorService())
        {
          @Override
          public void execute(Runnable command)
          {
            cancellationSubmissions.incrementAndGet();
            cancellationSubmitted.countDown();
            super.execute(command);
          }
        };
      }

      @Override
      protected long getCancelationPollInterval()
      {
        return 5;
      }
    };

    Thread requestThread = new Thread(() -> {
      try
      {
        request.send(monitor);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Monitored cancellation request"); //$NON-NLS-1$

    try
    {
      requestThread.start();
      assertTrue("Server did not start the monitored indication", cancellationIndicationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Cancellation polling occupied the selected executor before cancellation", 0, cancellationSubmissions.get()); //$NON-NLS-1$

      monitor.cancel();
      assertTrue("Cancellation was not submitted", cancellationSubmitted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("Remote indication did not observe cancellation", cancellationObserved.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Cancellation was reported more than once", 1, cancellationSubmissions.get()); //$NON-NLS-1$
      releaseCancellationIndication.countDown();
      requestThread.join(5000);

      assertFalse("Canceled request did not finish", requestThread.isAlive()); //$NON-NLS-1$
      assertNotNull("Canceled indication did not fail the request", failure.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseCancellationIndication.countDown();
      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testInternalMonitorAndDisabledCancellationExecutor() throws Exception
  {
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicInteger cancellationHookCalls = new AtomicInteger();

    try
    {
      RequestWithMonitoring<Boolean> unmonitoredRequest = createSimpleMonitoringRequest(protocol, cancellationHookCalls, false);
      assertTrue("Unmonitored request failed", unmonitoredRequest.send()); //$NON-NLS-1$
      assertEquals("Internal monitor started cancellation polling", 0, cancellationHookCalls.get()); //$NON-NLS-1$

      RequestWithMonitoring<Boolean> disabledRequest = createSimpleMonitoringRequest(protocol, cancellationHookCalls, true);
      assertTrue("Request with disabled cancellation reporting failed", disabledRequest.send(new Monitor())); //$NON-NLS-1$
      assertEquals("Cancellation executor hook was not called", 1, cancellationHookCalls.get()); //$NON-NLS-1$
    }
    finally
    {
      protocol.close();
    }
  }

  private RequestWithMonitoring<Boolean> createSimpleMonitoringRequest(SignalProtocol<?> protocol, AtomicInteger cancellationHookCalls,
      boolean disableCancellationReporting)
  {
    return new RequestWithMonitoring<>(protocol, SIGNAL_7)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected ExecutorService getCancelationExecutorService()
      {
        cancellationHookCalls.incrementAndGet();
        return disableCancellationReporting ? null : super.getCancelationExecutorService();
      }
    };
  }

  public void testProgressReportsCoalesceAndRecoverAfterRejection() throws Exception
  {
    progressIndicationStarted = new CountDownLatch(1);
    releaseProgressIndication = new CountDownLatch(1);
    progressOutputEntered = new CountDownLatch(1);
    releaseProgressOutput = new CountDownLatch(1);
    rejectNextProgress = new AtomicBoolean(true);
    progressReportSubmissions = new AtomicInteger();
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicReference<Throwable> failure = new AtomicReference<>();
    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_8)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected int getMonitorProgressSeconds()
      {
        return 1;
      }
    };
    Thread requestThread = new Thread(() -> {
      try
      {
        request.send(new Monitor());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Progress reporting regression request"); //$NON-NLS-1$
    TimerTask timerProbe = null;

    try
    {
      requestThread.start();
      assertTrue("Monitored indication did not start", progressIndicationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("Progress reporting did not recover after executor rejection", progressOutputEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Expected one rejected and one accepted progress submission", 2, progressReportSubmissions.get()); //$NON-NLS-1$

      CountDownLatch timerTicks = new CountDownLatch(110);
      timerProbe = Monitor.scheduleAtFixedRate(timerTicks::countDown, 1, 20);
      assertTrue("Shared monitor timer stopped while progress output was blocked", timerTicks.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Progress ticks accumulated while a report was outstanding", 2, progressReportSubmissions.get()); //$NON-NLS-1$

      releaseProgressOutput.countDown();
      releaseProgressIndication.countDown();
      requestThread.join(5000);
      assertFalse("Monitored request did not complete", requestThread.isAlive()); //$NON-NLS-1$
      assertNull("Monitored request failed", failure.get()); //$NON-NLS-1$

      CountDownLatch completionTicks = new CountDownLatch(60);
      timerProbe.cancel();
      timerProbe = Monitor.scheduleAtFixedRate(completionTicks::countDown, 1, 20);
      assertTrue("Timer probe did not complete after signal completion", completionTicks.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Completed signal continued to submit progress reports", 2, progressReportSubmissions.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseProgressOutput.countDown();
      releaseProgressIndication.countDown();
      if (timerProbe != null)
      {
        timerProbe.cancel();
      }

      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testExceptionalCompletionStopsProgressSubmissions() throws Exception
  {
    progressOutputEntered = new CountDownLatch(1);
    releaseProgressOutput = new CountDownLatch(1);
    rejectNextProgress = new AtomicBoolean(false);
    progressReportSubmissions = new AtomicInteger();
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicReference<Throwable> failure = new AtomicReference<>();
    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_9)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected int getMonitorProgressSeconds()
      {
        return 1;
      }
    };
    Thread requestThread = new Thread(() -> {
      try
      {
        request.send(new Monitor());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Exceptional monitored request"); //$NON-NLS-1$
    TimerTask timerProbe = null;

    try
    {
      requestThread.start();
      requestThread.join(5000);
      assertFalse("Exceptional request did not finish", requestThread.isAlive()); //$NON-NLS-1$
      assertNotNull("Expected remote indication failure", failure.get()); //$NON-NLS-1$

      CountDownLatch timerTicks = new CountDownLatch(70);
      timerProbe = Monitor.scheduleAtFixedRate(timerTicks::countDown, 1, 20);
      assertTrue("Timer probe did not complete after exceptional signal completion", timerTicks.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Exceptional signal continued to submit progress reports", 0, progressReportSubmissions.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseProgressOutput.countDown();
      if (timerProbe != null)
      {
        timerProbe.cancel();
      }

      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testCancellationBeforeReportingMonitorCreation() throws Exception
  {
    holdNextSignalReactor = new AtomicBoolean(true);
    signalReactorQueued = new CountDownLatch(1);
    releaseSignalReactor = new CountDownLatch(1);
    cancellationProcessed = new CountDownLatch(1);
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    Monitor monitor = new Monitor();
    AtomicInteger cancellationSubmissions = new AtomicInteger();
    CountDownLatch cancellationSubmitted = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_6)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor requestMonitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor requestMonitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected ExecutorService getCancelationExecutorService()
      {
        return new DelegatingExecutorService(super.getCancelationExecutorService())
        {
          @Override
          public void execute(Runnable command)
          {
            cancellationSubmissions.incrementAndGet();
            cancellationSubmitted.countDown();
            super.execute(command);
          }
        };
      }

      @Override
      protected long getCancelationPollInterval()
      {
        return 5;
      }
    };
    Thread requestThread = new Thread(() -> {
      try
      {
        request.send(monitor);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Early monitored cancellation request"); //$NON-NLS-1$

    try
    {
      requestThread.start();
      assertTrue("Server reactor was not queued", signalReactorQueued.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      monitor.cancel();
      assertTrue("Early cancellation was not submitted", cancellationSubmitted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("Server did not process early cancellation", cancellationProcessed.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Early cancellation was submitted more than once", 1, cancellationSubmissions.get()); //$NON-NLS-1$

      releaseSignalReactor.countDown();
      requestThread.join(5000);
      assertFalse("Early canceled request did not finish", requestThread.isAlive()); //$NON-NLS-1$
      assertNotNull("Early cancellation did not cancel the newly created reporting monitor", failure.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseSignalReactor.countDown();
      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testProtocolDeactivationStopsProgressReporting() throws Exception
  {
    progressIndicationStarted = new CountDownLatch(1);
    releaseProgressIndication = new CountDownLatch(1);
    progressOutputEntered = new CountDownLatch(1);
    releaseProgressOutput = new CountDownLatch(1);
    rejectNextProgress = new AtomicBoolean(false);
    progressReportSubmissions = new AtomicInteger();
    serverProtocolDeactivated = new CountDownLatch(1);
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicReference<Throwable> failure = new AtomicReference<>();
    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_8)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected int getMonitorProgressSeconds()
      {
        return 1;
      }
    };
    Thread requestThread = new Thread(() -> {
      try
      {
        request.send(new Monitor());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, "Deactivated monitored request"); //$NON-NLS-1$
    TimerTask timerProbe = null;

    try
    {
      requestThread.start();
      assertTrue("Monitored indication did not start", progressIndicationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertTrue("Progress output did not enter its blocking gate", progressOutputEntered.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Expected one in-flight progress report", 1, progressReportSubmissions.get()); //$NON-NLS-1$

      protocol.close();
      releaseProgressOutput.countDown();
      releaseProgressIndication.countDown();
      assertTrue("Server protocol did not deactivate", serverProtocolDeactivated.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      requestThread.join(5000);
      assertFalse("Deactivated monitored request did not finish", requestThread.isAlive()); //$NON-NLS-1$
      assertNotNull("Protocol deactivation did not fail the pending request", failure.get()); //$NON-NLS-1$

      CountDownLatch timerTicks = new CountDownLatch(70);
      timerProbe = Monitor.scheduleAtFixedRate(timerTicks::countDown, 1, 20);
      assertTrue("Timer probe did not finish after protocol deactivation", timerTicks.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Deactivated signal continued to submit progress reports", 1, progressReportSubmissions.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseProgressOutput.countDown();
      releaseProgressIndication.countDown();
      if (timerProbe != null)
      {
        timerProbe.cancel();
      }

      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testLateCancellationAfterCompletionIsNotReported() throws Exception
  {
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    Monitor monitor = new Monitor();
    AtomicInteger cancellationSubmissions = new AtomicInteger();
    RequestWithMonitoring<Boolean> request = new RequestWithMonitoring<>(protocol, SIGNAL_7)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor requestMonitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor requestMonitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected ExecutorService getCancelationExecutorService()
      {
        return new DelegatingExecutorService(super.getCancelationExecutorService())
        {
          @Override
          public void execute(Runnable command)
          {
            cancellationSubmissions.incrementAndGet();
            super.execute(command);
          }
        };
      }

      @Override
      protected long getCancelationPollInterval()
      {
        return 5;
      }
    };

    try
    {
      assertTrue("Monitored request failed", request.send(monitor)); //$NON-NLS-1$
      monitor.cancel();

      CountDownLatch timerTicks = new CountDownLatch(25);
      TimerTask timerProbe = Monitor.scheduleAtFixedRate(timerTicks::countDown, 1, 5);
      try
      {
        assertTrue("Timer probe did not finish after signal completion", timerTicks.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      }
      finally
      {
        timerProbe.cancel();
      }

      assertEquals("Late cancellation submitted a stale cancellation request", 0, cancellationSubmissions.get()); //$NON-NLS-1$
    }
    finally
    {
      protocol.close();
    }
  }

  public void testConcurrentMonitoredRequestsDoNotSubmitPollingWorkers() throws Exception
  {
    progressIndicationStarted = new CountDownLatch(2);
    releaseProgressIndication = new CountDownLatch(1);
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicInteger cancellationSubmissions = new AtomicInteger();
    AtomicReference<Throwable> firstFailure = new AtomicReference<>();
    AtomicReference<Throwable> secondFailure = new AtomicReference<>();
    RequestWithMonitoring<Boolean> firstRequest = createConcurrentRequest(protocol, cancellationSubmissions);
    RequestWithMonitoring<Boolean> secondRequest = createConcurrentRequest(protocol, cancellationSubmissions);
    Thread firstThread = createConcurrentRequestThread(firstRequest, firstFailure, "First monitored request"); //$NON-NLS-1$
    Thread secondThread = createConcurrentRequestThread(secondRequest, secondFailure, "Second monitored request"); //$NON-NLS-1$

    try
    {
      firstThread.start();
      secondThread.start();
      assertTrue("Concurrent monitored indications did not both start", progressIndicationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertEquals("Cancellation polling occupied executor workers", 0, cancellationSubmissions.get()); //$NON-NLS-1$

      releaseProgressIndication.countDown();
      firstThread.join(5000);
      secondThread.join(5000);
      assertFalse("First monitored request did not finish", firstThread.isAlive()); //$NON-NLS-1$
      assertFalse("Second monitored request did not finish", secondThread.isAlive()); //$NON-NLS-1$
      assertNull("First monitored request failed", firstFailure.get()); //$NON-NLS-1$
      assertNull("Second monitored request failed", secondFailure.get()); //$NON-NLS-1$
      assertEquals("Completed requests submitted cancellation reports", 0, cancellationSubmissions.get()); //$NON-NLS-1$
    }
    finally
    {
      releaseProgressIndication.countDown();
      protocol.close();
      firstThread.join(1000);
      secondThread.join(1000);
    }
  }

  private RequestWithMonitoring<Boolean> createConcurrentRequest(SignalProtocol<?> protocol, AtomicInteger cancellationSubmissions)
  {
    return new RequestWithMonitoring<>(protocol, SIGNAL_8)
    {
      @Override
      protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor)
      {
        // This test signal has no request payload.
      }

      @Override
      protected Boolean confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
      {
        return in.readBoolean();
      }

      @Override
      protected ExecutorService getCancelationExecutorService()
      {
        return new DelegatingExecutorService(super.getCancelationExecutorService())
        {
          @Override
          public void execute(Runnable command)
          {
            cancellationSubmissions.incrementAndGet();
            super.execute(command);
          }
        };
      }
    };
  }

  private Thread createConcurrentRequestThread(RequestWithMonitoring<Boolean> request, AtomicReference<Throwable> failure, String name)
  {
    return new Thread(() -> {
      try
      {
        request.send(new Monitor());
      }
      catch (Throwable ex)
      {
        failure.set(ex);
      }
    }, name);
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
      assertTrue("The pending request did not receive a local protocol deactivation failure", //$NON-NLS-1$
          failure.get() instanceof SignalProtocolDeactivatedException);
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

  public void testOngoingIndicationFailureAfterProtocolDeactivation() throws Exception
  {
    pendingIndicationStarted = new CountDownLatch(1);
    releasePendingIndication = new CountDownLatch(1);
    serverProtocolDeactivated = new CountDownLatch(1);
    indicationFailureHandled = new CountDownLatch(1);
    indicationFailure = new AtomicReference<>();
    expectedIndicationFailure = new AtomicReference<>();
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    AtomicReference<Throwable> clientFailure = new AtomicReference<>();
    Thread requestThread = new Thread(() -> {
      try
      {
        new RequestWithConfirmation<Boolean>(protocol, SIGNAL_4)
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
        clientFailure.set(ex);
      }
    }, "Indication failing during protocol deactivation"); //$NON-NLS-1$

    try
    {
      requestThread.start();
      assertTrue("Server did not start the pending indication", pendingIndicationStarted.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      protocol.close();
      assertTrue("Server protocol did not deactivate", serverProtocolDeactivated.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      releasePendingIndication.countDown();

      assertTrue("The indication failure was not handled", indicationFailureHandled.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(expectedIndicationFailure.get(), indicationFailure.get());
      assertEquals(0, indicationFailure.get().getSuppressed().length);
      requestThread.join(1000);
      assertFalse("Protocol deactivation left the request reader blocked", requestThread.isAlive()); //$NON-NLS-1$
      assertTrue(clientFailure.get() instanceof SignalProtocolDeactivatedException);
    }
    finally
    {
      releasePendingIndication.countDown();
      protocol.close();
      requestThread.join(1000);
    }
  }

  public void testIndicationFailureRetainsSecondaryTransmissionFailure() throws Exception
  {
    indicationFailureHandled = new CountDownLatch(1);
    indicationFailure = new AtomicReference<>();
    expectedIndicationFailure = new AtomicReference<>();
    expectedPropagationFailure = new AtomicReference<>(new IllegalStateException("Secondary transmission failure")); //$NON-NLS-1$
    startTransport();

    SignalProtocol<Object> protocol = new ClientProtocol();
    protocol.open(getConnector());
    Thread requestThread = new Thread(() -> {
      try
      {
        new RequestWithConfirmation<Boolean>(protocol, SIGNAL_5)
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
      catch (Exception ignore)
      {
        // The caller closes the protocol after checking the server-side failure.
      }
    }, "Indication with secondary propagation failure"); //$NON-NLS-1$

    try
    {
      requestThread.start();
      assertTrue("The indication failure was not handled", indicationFailureHandled.await(5, TimeUnit.SECONDS)); //$NON-NLS-1$
      assertSame(expectedIndicationFailure.get(), indicationFailure.get());
      assertEquals(1, indicationFailure.get().getSuppressed().length);
      assertSame(expectedPropagationFailure.get(), indicationFailure.get().getSuppressed()[0]);
    }
    finally
    {
      protocol.close();
      requestThread.join(1000);
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

    private final CountDownLatch pendingIndicationStarted;

    private final CountDownLatch releasePendingIndication;

    private final CountDownLatch serverProtocolDeactivated;

    private final CountDownLatch indicationFailureHandled;

    private final AtomicReference<Throwable> indicationFailure;

    private final AtomicReference<Throwable> expectedIndicationFailure;

    private final AtomicReference<Throwable> expectedPropagationFailure;

    private volatile boolean failExceptionPropagation;

    private final CountDownLatch cancellationIndicationStarted;

    private final CountDownLatch cancellationObserved;

    private final CountDownLatch releaseCancellationIndication;

    private final CountDownLatch progressIndicationStarted;

    private final CountDownLatch releaseProgressIndication;

    private final CountDownLatch progressOutputEntered;

    private final CountDownLatch releaseProgressOutput;

    private final AtomicBoolean rejectNextProgress;

    private final AtomicInteger progressReportSubmissions;

    private final ThreadLocal<Boolean> reportingOutput = new ThreadLocal<>();

    private final AtomicBoolean holdNextSignalReactor;

    private final CountDownLatch signalReactorQueued;

    private final CountDownLatch releaseSignalReactor;

    private final CountDownLatch cancellationProcessed;

    public ServerProtocol(CountDownLatch pendingConfirmationStarted, CountDownLatch releasePendingConfirmation, CountDownLatch pendingIndicationStarted,
        CountDownLatch releasePendingIndication, CountDownLatch serverProtocolDeactivated, CountDownLatch indicationFailureHandled,
        AtomicReference<Throwable> indicationFailure, AtomicReference<Throwable> expectedIndicationFailure,
        AtomicReference<Throwable> expectedPropagationFailure, CountDownLatch cancellationIndicationStarted, CountDownLatch cancellationObserved,
        CountDownLatch releaseCancellationIndication, CountDownLatch progressIndicationStarted, CountDownLatch releaseProgressIndication,
        CountDownLatch progressOutputEntered, CountDownLatch releaseProgressOutput, AtomicBoolean rejectNextProgress, AtomicInteger progressReportSubmissions,
        AtomicBoolean holdNextSignalReactor, CountDownLatch signalReactorQueued, CountDownLatch releaseSignalReactor, CountDownLatch cancellationProcessed)
    {
      super(PROTOCOL_TYPE);
      this.pendingConfirmationStarted = pendingConfirmationStarted;
      this.releasePendingConfirmation = releasePendingConfirmation;
      this.pendingIndicationStarted = pendingIndicationStarted;
      this.releasePendingIndication = releasePendingIndication;
      this.serverProtocolDeactivated = serverProtocolDeactivated;
      this.indicationFailureHandled = indicationFailureHandled;
      this.indicationFailure = indicationFailure;
      this.expectedIndicationFailure = expectedIndicationFailure;
      this.expectedPropagationFailure = expectedPropagationFailure;
      this.cancellationIndicationStarted = cancellationIndicationStarted;
      this.cancellationObserved = cancellationObserved;
      this.releaseCancellationIndication = releaseCancellationIndication;
      this.progressIndicationStarted = progressIndicationStarted;
      this.releaseProgressIndication = releaseProgressIndication;
      this.progressOutputEntered = progressOutputEntered;
      this.releaseProgressOutput = releaseProgressOutput;
      this.rejectNextProgress = rejectNextProgress;
      this.progressReportSubmissions = progressReportSubmissions;
      this.holdNextSignalReactor = holdNextSignalReactor;
      this.signalReactorQueued = signalReactorQueued;
      this.releaseSignalReactor = releaseSignalReactor;
      this.cancellationProcessed = cancellationProcessed;
    }

    @Override
    public ExecutorService getExecutorService()
    {
      ExecutorService delegate = super.getExecutorService();
      boolean gateEnabled = progressOutputEntered != null || holdNextSignalReactor != null;
      if (delegate == null || !gateEnabled)
      {
        return delegate;
      }

      return new DelegatingExecutorService(delegate)
      {
        @Override
        public void execute(Runnable command)
        {
          if (command instanceof SignalReactor)
          {
            if (command instanceof MonitorCanceledIndication)
            {
              super.execute(() -> {
                command.run();
                cancellationProcessed.countDown();
              });
              return;
            }

            if (holdNextSignalReactor != null && holdNextSignalReactor.compareAndSet(true, false))
            {
              signalReactorQueued.countDown();
              super.execute(() -> {
                try
                {
                  releaseSignalReactor.await();
                  command.run();
                }
                catch (InterruptedException ex)
                {
                  Thread.currentThread().interrupt();
                }
              });
            }
            else
            {
              super.execute(command);
            }

            return;
          }

          if (progressOutputEntered == null)
          {
            super.execute(command);
            return;
          }

          progressReportSubmissions.incrementAndGet();
          if (rejectNextProgress != null && rejectNextProgress.compareAndSet(true, false))
          {
            throw new RejectedExecutionException("Rejected one progress report for regression coverage"); //$NON-NLS-1$
          }

          super.execute(() -> {
            reportingOutput.set(Boolean.TRUE);
            try
            {
              command.run();
            }
            finally
            {
              reportingOutput.remove();
            }
          });
        }
      };
    }

    @Override
    public IBufferProvider getBufferProvider()
    {
      if (Boolean.TRUE.equals(reportingOutput.get()))
      {
        progressOutputEntered.countDown();
        try
        {
          releaseProgressOutput.await();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException("Interrupted while holding progress output", ex); //$NON-NLS-1$
        }
      }

      if (failExceptionPropagation)
      {
        return new IBufferProvider()
        {
          @Override
          public short getBufferCapacity()
          {
            return 0;
          }

          @Override
          public IBuffer provideBuffer()
          {
            throw (RuntimeException)expectedPropagationFailure.get();
          }

          @Override
          public void retainBuffer(IBuffer buffer)
          {
            buffer.release();
          }
        };
      }

      return super.getBufferProvider();
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

      case SIGNAL_4:
        return new IndicationWithResponse(this, SIGNAL_4)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            in.readBoolean();
            pendingIndicationStarted.countDown();
            releasePendingIndication.await();

            IllegalStateException exception = new IllegalStateException("Application indication failure"); //$NON-NLS-1$
            expectedIndicationFailure.set(exception);
            throw exception;
          }

          @Override
          protected void responding(ExtendedDataOutputStream out) throws Exception
          {
            out.writeBoolean(true);
          }

          @Override
          protected void handleRunException(Throwable ex) throws Throwable
          {
            indicationFailure.set(ex);
            indicationFailureHandled.countDown();
          }
        };

      case SIGNAL_5:
        return new IndicationWithResponse(this, SIGNAL_5)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            in.readBoolean();
            IllegalStateException exception = new IllegalStateException("Application indication failure"); //$NON-NLS-1$
            expectedIndicationFailure.set(exception);
            failExceptionPropagation = true;
            throw exception;
          }

          @Override
          protected void responding(ExtendedDataOutputStream out) throws Exception
          {
            out.writeBoolean(true);
          }

          @Override
          protected void handleRunException(Throwable ex) throws Throwable
          {
            indicationFailure.set(ex);
            indicationFailureHandled.countDown();
          }
        };

      case SIGNAL_6:
        return new IndicationWithMonitoring(this, SIGNAL_6)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
          {
            monitor.begin();
            cancellationIndicationStarted.countDown();
            TimerTask cancellationObserver = Monitor.scheduleAtFixedRate(() -> {
              if (monitor.isCanceled())
              {
                cancellationObserved.countDown();
              }
            }, 1, 5);

            try
            {
              releaseCancellationIndication.await();
              monitor.checkCanceled();
            }
            finally
            {
              cancellationObserver.cancel();
              monitor.done();
            }
          }

          @Override
          protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
          {
            out.writeBoolean(true);
          }
        };
      case SIGNAL_7:
        return new IndicationWithMonitoring(this, SIGNAL_7)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
          {
            monitor.begin();
            monitor.worked();
            monitor.done();
          }

          @Override
          protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
          {
            out.writeBoolean(true);
          }
        };
      case SIGNAL_8:
        return new IndicationWithMonitoring(this, SIGNAL_8)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
          {
            monitor.begin();
            progressIndicationStarted.countDown();
            try
            {
              releaseProgressIndication.await();
              monitor.worked();
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

      case SIGNAL_9:
        return new IndicationWithMonitoring(this, SIGNAL_9)
        {
          @Override
          protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
          {
            monitor.begin();
            throw new IllegalStateException("Expected monitored indication failure"); //$NON-NLS-1$
          }

          @Override
          protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
          {
            out.writeBoolean(true);
          }
        };
      default:
        return super.createSignalReactor(signalID);
      }
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      try
      {
        super.doDeactivate();
      }
      finally
      {
        if (serverProtocolDeactivated != null)
        {
          serverProtocolDeactivated.countDown();
        }
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

    public TestMonitor()
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
