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
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.IMonitor;
import org.eclipse.net4j.util.om.monitor.Monitor;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class RequestWithMonitoring<RESULT> extends RequestWithConfirmation<RESULT>
{
  /**
   * @since 2.0
   */
  public static final long DEFAULT_CANCELATION_POLL_INTERVAL = 100L;

  /**
   * @since 2.0
   */
  public static final long DEFAULT_MONITOR_PROGRESS_INTERVAL = 2000;

  private IMonitor mainMonitor;

  private IMonitor remoteMonitor;

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
    return sendAsync(null);
  }

  @Override
  public RESULT send() throws Exception, RemoteException
  {
    return send(null);
  }

  @Override
  public RESULT send(long timeout) throws Exception, RemoteException
  {
    return send(timeout, null);
  }

  public Future<RESULT> sendAsync(IMonitor monitor)
  {
    initMainMonitor(monitor);
    return super.sendAsync();
  }

  public RESULT send(IMonitor monitor) throws Exception, RemoteException
  {
    initMainMonitor(monitor);
    return super.send();
  }

  public RESULT send(long timeout, IMonitor monitor) throws Exception, RemoteException
  {
    initMainMonitor(monitor);
    return super.send(timeout);
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
      remoteMonitor.done();
      remoteMonitor = null;

      mainMonitor.done();
      mainMonitor = null;
    }
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out) throws Exception
  {
    int remoteWork = 100 - getRequestingWorkPercent() - getConfirmingWorkPercent();
    if (remoteWork < 0)
    {
      throw new ImplementationError("Remote work must not be negative: " + remoteWork);
    }

    mainMonitor.begin(100);
    remoteMonitor = mainMonitor.fork(remoteWork);

    ExecutorService executorService = getCancelationExecutorService();
    if (executorService != null)
    {
      executorService.execute(new Runnable()
      {
        public void run()
        {
          while (mainMonitor != null)
          {
            ConcurrencyUtil.sleep(getCancelationPollInterval());
            if (mainMonitor != null && mainMonitor.isCanceled())
            {
              try
              {
                new MonitorCanceledRequest(getProtocol(), getCorrelationID()).sendAsync();
              }
              catch (Exception ex)
              {
                OM.LOG.error(ex);
              }

              return;
            }
          }
        }
      });
    }

    out.writeLong(getMonitorProgressInterval());
    requesting(out, mainMonitor.fork(getRequestingWorkPercent()));
  }

  @Override
  protected final RESULT confirming(ExtendedDataInputStream in) throws Exception
  {
    return confirming(in, mainMonitor.fork(getConfirmingWorkPercent()));
  }

  protected abstract void requesting(ExtendedDataOutputStream out, IMonitor monitor) throws Exception;

  /**
   * <b>Important Note:</b> The confirmation must not be empty, i.e. the stream must be used at least to read a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract RESULT confirming(ExtendedDataInputStream in, IMonitor monitor) throws Exception;

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
  protected long getMonitorProgressInterval()
  {
    return DEFAULT_MONITOR_PROGRESS_INTERVAL;
  }

  /**
   * @since 2.0
   */
  protected int getRequestingWorkPercent()
  {
    return 25;
  }

  /**
   * @since 2.0
   */
  protected int getConfirmingWorkPercent()
  {
    return 25;
  }

  void setMonitorProgress(int totalWork, int work)
  {
    getBufferInputStream().restartTimeout();
    if (remoteMonitor != null)
    {
      if (remoteMonitor.getTotalWork() == 0)
      {
        remoteMonitor.begin(totalWork);
        remoteMonitor.worked(work);
      }
      else
      {
        float oldRatio = remoteMonitor.getWork();
        oldRatio /= remoteMonitor.getTotalWork();

        float newRatio = work;
        newRatio /= totalWork;

        float newWork = newRatio - oldRatio;
        newWork *= remoteMonitor.getTotalWork();
        if (newWork >= 1.0)
        {
          remoteMonitor.worked((int)newWork);
        }
      }
    }
  }

  private void initMainMonitor(IMonitor monitor)
  {
    mainMonitor = monitor == null ? new Monitor() : monitor;
  }
}
