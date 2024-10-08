/*
 * Copyright (c) 2006-2009, 2011, 2012, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.ExceptionHandler;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.monitor.TimeoutMonitor;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.TimerTask;

/**
 * Represents the receiver side of a {@link Signal signal}.
 *
 * @author Eike Stepper
 */
public abstract class SignalReactor extends Signal
{
  private ReportingMonitor monitor;

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
    if (monitor != null)
    {
      throw new IllegalStateException("Illegal attempt to create a second monitor");
    }

    monitor = new ReportingMonitor(monitorProgressSeconds, monitorTimeoutSeconds);
    return monitor;
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
      if (monitor != null)
      {
        monitor.done();
      }
    }
  }

  void setMonitorCanceled()
  {
    monitor.cancel();
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
    private TimerTask sendProgressTask = new TimerTask()
    {
      @Override
      public void run()
      {
        try
        {
          sendProgress();
        }
        catch (Throwable ex)
        {
          ExceptionHandler.Factory.handle(SignalReactor.this, ex, "ReportingMonitorTask failed", OM.LOG);
          cancel();
        }
      }
    };

    public ReportingMonitor(int monitorProgressSeconds, int monitorTimeoutSeconds)
    {
      super(1000L * monitorTimeoutSeconds);
      long period = 1000L * monitorProgressSeconds;
      scheduleAtFixedRate(sendProgressTask, period, period);
    }

    @Override
    public void cancel(RuntimeException cancelException)
    {
      sendProgressTask.cancel();
      super.cancel(cancelException);
    }

    @Override
    public void done()
    {
      try
      {
        sendProgressTask.cancel();
        super.done();
      }
      finally
      {
        monitor = null;
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
        IChannel channel = protocol.getChannel();
        if (LifecycleUtil.isActive(channel))
        {
          ExceptionHandler.Factory.handle(SignalReactor.this, ex, "MonitorProgressRequest failed", OM.LOG);
        }
      }
    }
  }
}
