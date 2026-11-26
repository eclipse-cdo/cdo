/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2014, 2016, 2019, 2021, 2024-2026 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.ExceptionHandler;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents the sender side of a two-way {@link IndicationWithResponse signal} with additional support for remote progress monitoring.
 * <p>
 * Cancellation delivery uses the selected protocol executor. Configure enough capacity for cancellation reporting alongside active signal
 * operations.
 *
 * @param <RESULT>
 *          the response type
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class RequestWithMonitoring<RESULT> extends RequestWithConfirmation<RESULT>
{
  /**
   * @since 2.0
   */
  public static final long DEFAULT_CANCELATION_POLL_INTERVAL = OMPlatform.INSTANCE
      .getProperty("org.eclipse.net4j.signal.RequestWithMonitoring.DEFAULT_CANCELATION_POLL_INTERVAL", 100L);

  /**
   * @since 2.0
   */
  public static final int DEFAULT_MONITOR_PROGRESS_SECONDS = OMPlatform.INSTANCE
      .getProperty("org.eclipse.net4j.signal.RequestWithMonitoring.DEFAULT_MONITOR_PROGRESS_SECONDS", 1);

  /**
   * @since 2.0
   */
  public static final int DEFAULT_MONITOR_TIMEOUT_SECONDS = OMPlatform.INSTANCE
      .getProperty("org.eclipse.net4j.signal.RequestWithMonitoring.DEFAULT_MONITOR_TIMEOUT_SECONDS", 10);

  private volatile OMMonitor mainMonitor;

  private OMMonitor remoteMonitor;

  private Object monitorLock = new Object();

  private boolean isMonitoring;

  private volatile TimerTask cancelationPollTask;

  private final AtomicBoolean cancelationReported = new AtomicBoolean();

  private volatile boolean signalCompleted;

  /**
   * @since 2.0
   */
  public RequestWithMonitoring(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public RequestWithMonitoring(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public RequestWithMonitoring(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  public Future<RESULT> sendAsync()
  {
    initMainMonitor(null);
    return super.sendAsync();
  }

  public Future<RESULT> sendAsync(OMMonitor monitor)
  {
    initMainMonitor(monitor);
    return super.sendAsync();
  }

  @Override
  public RESULT send() throws Exception, RemoteException
  {
    initMainMonitor(null);
    return super.send();
  }

  @Override
  public RESULT send(long timeout) throws Exception, RemoteException
  {
    initMainMonitor(null);
    return super.send(timeout);
  }

  public RESULT send(OMMonitor monitor) throws Exception, RemoteException
  {
    initMainMonitor(monitor);
    return super.send();
  }

  public RESULT send(long timeout, OMMonitor monitor) throws Exception, RemoteException
  {
    initMainMonitor(monitor);
    return super.send(timeout);
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out) throws Exception
  {
    double remoteWorkPercent = OMMonitor.HUNDRED - getRequestingWorkPercent() - getConfirmingWorkPercent();
    if (remoteWorkPercent < OMMonitor.ZERO)
    {
      throw new ImplementationError("Remote work must not be negative: " + remoteWorkPercent); //$NON-NLS-1$
    }

    mainMonitor.begin(OMMonitor.HUNDRED);
    OMMonitor subMonitor = mainMonitor.fork(remoteWorkPercent);

    synchronized (monitorLock)
    {
      remoteMonitor = subMonitor;
    }

    OMMonitor monitor = mainMonitor;

    ExecutorService executorService = isMonitoring ? getCancelationExecutorService() : null;
    if (executorService != null)
    {
      SignalProtocol<?> protocol = getProtocol();
      int correlationID = getCorrelationID();
      long interval = Math.max(1, getCancelationPollInterval());

      // The timer only inspects cancellation; all signal output runs on the selected executor.
      cancelationPollTask = Monitor.scheduleAtFixedRate(() -> {
        if (!signalCompleted && monitor.isCanceled() && cancelationReported.compareAndSet(false, true))
        {
          TimerTask task = cancelationPollTask;
          if (task != null)
          {
            task.cancel();
          }

          try
          {
            executorService.execute(() -> {
              if (!signalCompleted && mainMonitor == monitor && monitor.isCanceled())
              {
                try
                {
                  new MonitorCanceledRequest(protocol, correlationID).sendAsync();
                }
                catch (Exception ex)
                {
                  if (ex instanceof SignalProtocolDeactivatedException)
                  {
                    SignalProtocol.traceDeactivation("Monitor cancellation reporting stopped during protocol deactivation", ex); //$NON-NLS-1$
                  }
                  else
                  {
                    ExceptionHandler.Factory.handle(RequestWithMonitoring.this, ex, "MonitorCanceledRequest failed", OM.LOG); //$NON-NLS-1$
                  }
                }
              }
            });
          }
          catch (RejectedExecutionException ex)
          {
            ExceptionHandler.Factory.handle(RequestWithMonitoring.this, ex, "MonitorCanceledRequest failed", OM.LOG); //$NON-NLS-1$
          }
        }
      }, interval, interval);
    }

    int monitorTimeoutSeconds = getMonitorTimeoutSeconds();
    int monitorProgressSeconds = getMonitorProgressSeconds();

    if (!isMonitoring)
    {
      monitorProgressSeconds = Math.max(monitorProgressSeconds, (int)Math.floor(monitorTimeoutSeconds * 0.8));
    }

    out.writeInt(monitorProgressSeconds);
    out.writeInt(monitorTimeoutSeconds);

    requesting(out, mainMonitor.fork(getRequestingWorkPercent()));
  }

  @Override
  protected final RESULT confirming(ExtendedDataInputStream in) throws Exception
  {
    return confirming(in, mainMonitor.fork(getConfirmingWorkPercent()));
  }

  protected abstract void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception;

  /**
   * <b>Important Note:</b> The confirmation must not be empty, i.e. the stream must be used at least to read a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract RESULT confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception;

  /**
   * @since 2.0
   */
  protected ExecutorService getCancelationExecutorService()
  {
    return getProtocol().getExecutorService();
  }

  /**
   * @since 2.0
   */
  protected long getCancelationPollInterval()
  {
    return DEFAULT_CANCELATION_POLL_INTERVAL;
  }

  /**
   * @since 2.0
   */
  protected int getMonitorProgressSeconds()
  {
    return DEFAULT_MONITOR_PROGRESS_SECONDS;
  }

  /**
   * @since 2.0
   */
  protected int getMonitorTimeoutSeconds()
  {
    return DEFAULT_MONITOR_TIMEOUT_SECONDS;
  }

  /**
   * @since 2.0
   */
  protected int getRequestingWorkPercent()
  {
    return 2;
  }

  /**
   * @since 2.0
   */
  protected int getConfirmingWorkPercent()
  {
    return 1;
  }

  @Override
  void doExecute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    try
    {
      super.doExecute(in, out);
    }
    finally
    {
      // Queued cancellation work observes completion before monitor references are released.
      signalCompleted = true;

      TimerTask task = cancelationPollTask;
      if (task != null)
      {
        task.cancel();
        cancelationPollTask = null;
      }

      synchronized (monitorLock)
      {
        try
        {
          if (remoteMonitor != null)
          {
            remoteMonitor.done();
          }
        }
        catch (Exception ex)
        {
          ExceptionHandler.Factory.handle(RequestWithMonitoring.this, ex, "MonitorremoteMonitor.done() failed", OM.LOG);
        }
        finally
        {
          remoteMonitor = null;
        }
      }

      try
      {
        if (mainMonitor != null)
        {
          mainMonitor.done();
        }
      }
      finally
      {
        mainMonitor = null;
      }
    }
  }

  @Override
  void setMonitorProgress(double totalWork, double work)
  {
    super.setMonitorProgress(totalWork, work);

    synchronized (monitorLock)
    {
      if (remoteMonitor != null)
      {
        if (!remoteMonitor.hasBegun())
        {
          remoteMonitor.begin(totalWork);
          remoteMonitor.worked(work);
        }
        else
        {
          double oldRatio = remoteMonitor.getWork() / remoteMonitor.getTotalWork();
          double newRatio = work / totalWork;

          double newWork = newRatio - oldRatio;
          newWork *= remoteMonitor.getTotalWork();
          if (newWork >= OMMonitor.ZERO)
          {
            remoteMonitor.worked(newWork);
          }
        }
      }
    }
  }

  private void initMainMonitor(OMMonitor monitor)
  {
    isMonitoring = monitor != null;
    mainMonitor = isMonitoring ? monitor : new Monitor();
  }
}
