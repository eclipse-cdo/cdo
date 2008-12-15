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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.MonitorCanceledException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class IndicationWithMonitoring extends IndicationWithResponse
{
  private LastAccessMonitor monitor;

  private Object monitorLock = new Object();

  private Future<?> monitorFuture;

  private long lastMonitorAccess;

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public IndicationWithMonitoring(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    try
    {
      super.execute(in, out);
    }
    finally
    {
      synchronized (monitorLock)
      {
        if (monitor != null)
        {
          monitor.done();
          monitor = null;
          if (monitorFuture != null)
          {
            monitorFuture.cancel(true);
          }
        }
      }
    }
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in) throws Exception
  {
    final long monitorProgressInterval = in.readLong();
    ExecutorService executorService = getMonitoringExecutorService();
    if (executorService != null)
    {
      monitor = new LastAccessMonitor();
      setLastMonitorAccess();
      monitorFuture = executorService.submit(new Runnable()
      {
        public void run()
        {
          while (monitor != null)
          {
            long passedMillis = System.currentTimeMillis() - lastMonitorAccess;
            if (passedMillis > monitorProgressInterval)
            {
              setMonitorCanceled("Timeout after " + passedMillis + " millis");
              break;
            }

            synchronized (monitorLock)
            {
              if (monitor != null)
              {
                // Keep sendProgress into the locks... otherwise when interrupt it seems to freeze.
                sendProgress(monitor.getTotalWork(), (int)monitor.getWork());
              }
            }

            if (monitor != null)
            {
              ConcurrencyUtil.sleep(monitorProgressInterval);
            }
          }
        }

        private void sendProgress(int totalWork, int work)
        {
          try
          {
            new MonitorProgressRequest(getProtocol(), -getCorrelationID(), totalWork, work).sendAsync();
          }
          catch (Exception ex)
          {
            if (LifecycleUtil.isActive(getProtocol().getChannel()))
            {
              OM.LOG.error(ex);
            }
          }
        }
      });
    }

    monitor.begin(100);
    indicating(in, monitor.fork(getIndicatingWorkPercent()));
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out) throws Exception
  {
    responding(out, monitor.fork(100 - getIndicatingWorkPercent()));
  }

  protected abstract void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception;

  protected abstract void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception;

  /**
   * @since 2.0
   */
  protected ExecutorService getMonitoringExecutorService()
  {
    return getProtocol().getExecutorService();
  }

  protected int getIndicatingWorkPercent()
  {
    return 100;
  }

  void setMonitorCanceled(String message)
  {
    synchronized (monitorLock)
    {
      if (monitor != null)
      {
        monitor.setCancelationMessage(message);
        monitor.cancel();
      }
    }
  }

  void setLastMonitorAccess()
  {
    lastMonitorAccess = System.currentTimeMillis();
  }

  /**
   * @author Eike Stepper
   */
  private final class LastAccessMonitor extends Monitor
  {
    private String cancelationMessage;

    public LastAccessMonitor()
    {
    }

    @Override
    public synchronized void begin(int totalWork)
    {
      setLastMonitorAccess();
      super.begin(totalWork);
    }

    @Override
    public synchronized void worked(double work)
    {
      setLastMonitorAccess();
      super.worked(work);
    }

    public void setCancelationMessage(String message)
    {
      cancelationMessage = message;
    }

    @Override
    public synchronized boolean isCanceled()
    {
      setLastMonitorAccess();
      return super.isCanceled();
    }

    @Override
    public synchronized void checkCanceled() throws MonitorCanceledException
    {
      try
      {
        setLastMonitorAccess();
        super.checkCanceled();
      }
      catch (MonitorCanceledException ex)
      {
        if (cancelationMessage != null)
        {
          throw new MonitorCanceledException(cancelationMessage);
        }

        throw ex;
      }
    }
  }
}
