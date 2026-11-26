/*
 * Copyright (c) 2006-2009, 2011, 2012, 2021, 2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.ExceptionHandler;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.monitor.TimeoutMonitor;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.TimerTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents the receiver side of a {@link Signal signal}.
 * <p>
 * For monitored signals, periodic progress reports use the protocol executor. That executor needs enough available
 * capacity to run reporting work alongside active signal reactors; delayed reports can cause the peer's signal input
 * timeout to expire.
 *
 * @author Eike Stepper
 */
public abstract class SignalReactor extends Signal
{
  private volatile ReportingMonitor monitor;

  private boolean monitorCanceled;

  /**
   * @since 2.0
   */
  public SignalReactor(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public SignalReactor(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public SignalReactor(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  /**
   * @since 4.13
   */
  protected final OMMonitor createMonitor(int monitorProgressSeconds, int monitorTimeoutSeconds)
  {
    synchronized (this)
    {
      if (monitor != null)
      {
        throw new IllegalStateException("Illegal attempt to create a second monitor");
      }

      monitor = new ReportingMonitor(monitorProgressSeconds, monitorTimeoutSeconds);

      if (monitorCanceled)
      {
        monitor.cancel();
      }

      return monitor;
    }
  }

  /**
   * @since 4.15
   */
  protected final void monitor(int monitorProgressSeconds, int monitorTimeoutSeconds, RunnableWithException runnable) throws Exception
  {
    OMMonitor monitor = createMonitor(monitorProgressSeconds, monitorTimeoutSeconds);
    monitor.begin();

    Async async = monitor.forkAsync();

    try
    {
      runnable.run();
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  void runSync() throws Exception
  {
    try
    {
      super.runSync();
    }
    finally
    {
      ReportingMonitor current;

      synchronized (this)
      {
        current = monitor;
      }

      if (current != null)
      {
        current.done();
      }
    }
  }

  void setMonitorCanceled()
  {
    ReportingMonitor current;

    synchronized (this)
    {
      monitorCanceled = true;
      current = monitor;
    }

    if (current != null)
    {
      current.cancel();
    }
  }

  @Override
  String getInputMeaning()
  {
    return "Indicating"; //$NON-NLS-1$
  }

  @Override
  String getOutputMeaning()
  {
    return "Responding"; //$NON-NLS-1$
  }

  /**
   * @author Eike Stepper
   */
  private final class ReportingMonitor extends TimeoutMonitor
  {
    // Keep the queued and running states under one flag so timer ticks cannot accumulate reports.
    private final AtomicBoolean reporting = new AtomicBoolean();

    private volatile boolean active = true;

    private final TimerTask sendProgressTask;

    public ReportingMonitor(int monitorProgressSeconds, int monitorTimeoutSeconds)
    {
      super(1000L * monitorTimeoutSeconds);
      long period = 1000L * monitorProgressSeconds;

      // Timer ticks only enqueue work; network output must never occupy the shared timer thread.
      sendProgressTask = Monitor.scheduleAtFixedRate(this::submitProgress, period, period);
    }

    @Override
    public void cancel(RuntimeException cancelException)
    {
      active = false;
      sendProgressTask.cancel();
      super.cancel(cancelException);
    }

    @Override
    public void done()
    {
      active = false;

      try
      {
        sendProgressTask.cancel();
        super.done();
      }
      finally
      {
        synchronized (SignalReactor.this)
        {
          if (monitor == this)
          {
            monitor = null;
          }
        }
      }
    }

    private void submitProgress()
    {
      if (!active || isCanceledState() || !reporting.compareAndSet(false, true))
      {
        return;
      }

      try
      {
        getProtocol().getExecutorService().execute(() -> {
          try
          {
            if (active && !isCanceledState())
            {
              sendProgress();
            }
          }
          catch (Throwable ex)
          {
            ExceptionHandler.Factory.handle(SignalReactor.this, ex, "ReportingMonitorTask failed", OM.LOG);
          }
          finally
          {
            reporting.set(false);
          }
        });
      }
      catch (RejectedExecutionException ex)
      {
        reporting.set(false);

        if (active)
        {
          ExceptionHandler.Factory.handle(SignalReactor.this, ex, "ReportingMonitorTask failed", OM.LOG);
        }
      }
    }

    private void sendProgress()
    {
      SignalProtocol<?> protocol = getProtocol();

      try
      {
        int correlationID = -getCorrelationID();
        double totalWork = getTotalWork();
        double work = getWork();

        new MonitorProgressRequest(protocol, correlationID, totalWork, work).sendAsync();
      }
      catch (Exception ex)
      {
        if (ex instanceof SignalProtocolDeactivatedException)
        {
          SignalProtocol.traceDeactivation("Monitor progress reporting stopped during protocol deactivation", ex); //$NON-NLS-1$
        }
        else
        {
          ExceptionHandler.Factory.handle(SignalReactor.this, ex, "MonitorProgressRequest failed", OM.LOG); //$NON-NLS-1$
        }
      }
    }
  }
}
