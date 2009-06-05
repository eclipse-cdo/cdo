/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal.heartbeat;

import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.Timeouter;
import org.eclipse.net4j.util.concurrent.TimerLifecycle;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HeartBeatProtocol extends SignalProtocol<Object>
{
  public static final String TYPE = "heartbeat"; //$NON-NLS-1$

  private static final short SIGNAL_START = 1;

  private static final short SIGNAL_HEART_BEAT = 2;

  private static final boolean HEART_BEAT = true;

  private Timeouter timeouter;

  private Timer timer;

  public HeartBeatProtocol(IConnector connector, Timer timer)
  {
    super(TYPE);
    checkArg(timer, "timer"); //$NON-NLS-1$
    checkArg(connector, "connector"); //$NON-NLS-1$
    this.timer = timer;
    open(connector);
  }

  public HeartBeatProtocol(IConnector connector)
  {
    this(connector, getDefaultTimer(IPluginContainer.INSTANCE));
  }

  public Timer getTimer()
  {
    return timer;
  }

  /**
   *Same as <code>start(rate, 2 * rate)</code>.
   * 
   * @see #start(long, long)
   */
  public void start(final long rate)
  {
    start(rate, 2L * rate);
  }

  public void start(final long rate, long timeout)
  {
    checkActive();
    checkArg(rate > 0, "rate"); //$NON-NLS-1$
    checkArg(timeout >= rate, "timeout"); //$NON-NLS-1$

    try
    {
      new Request(this, SIGNAL_START, "Start") //$NON-NLS-1$
      {
        @Override
        protected void requesting(ExtendedDataOutputStream out) throws Exception
        {
          out.writeLong(rate);
        }
      }.sendAsync();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    if (timeouter == null)
    {
      timeouter = new Timeouter(getTimer(), timeout)
      {
        @Override
        protected void handleTimeout(long untouched)
        {
          HeartBeatProtocol.this.handleTimeout(untouched);
        }
      };
    }
    else
    {
      timeouter.setTimeout(timeout);
      timeouter.touch();
    }
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    if (signalID == SIGNAL_HEART_BEAT)
    {
      return new Indication(HeartBeatProtocol.this, SIGNAL_HEART_BEAT, "HeartBeat") //$NON-NLS-1$
      {
        @Override
        protected void indicating(ExtendedDataInputStream in) throws Exception
        {
          checkState(in.readBoolean() == HEART_BEAT, "Invalid heart beat"); //$NON-NLS-1$
        }
      };
    }

    return null;
  }

  protected void handleTimeout(long untouched)
  {
    IChannelMultiplexer multiplexer = getChannel().getMultiplexer();
    LifecycleUtil.deactivate(multiplexer, OMLogger.Level.DEBUG);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (timeouter != null)
    {
      timeouter.dispose();
      timeouter = null;
    }

    super.doDeactivate();
  }

  public static Timer getDefaultTimer(IManagedContainer container)
  {
    return TimerLifecycle.DaemonFactory.getTimer(container, null);
  }

  /**
   * @author Eike Stepper
   */
  public static class Server extends SignalProtocol<Object>
  {
    private long heartBeatRate;

    private Timer heartBeatTimer;

    private TimerTask heartBeatTimerTask;

    public Server()
    {
      super(TYPE);
    }

    public Timer getHeartBeatTimer()
    {
      return heartBeatTimer;
    }

    public void setHeartBeatTimer(Timer heartBeatTimer)
    {
      checkInactive();
      this.heartBeatTimer = heartBeatTimer;
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      if (signalID == SIGNAL_HEART_BEAT)
      {
        return new Indication(Server.this, SIGNAL_HEART_BEAT, "HeartBeat") //$NON-NLS-1$
        {
          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            heartBeatRate = in.readLong();
            cancelHeartBeatTask();
            scheduleHeartBeatTask();
          }
        };
      }

      return null;
    }

    @Override
    protected void doBeforeActivate() throws Exception
    {
      super.doBeforeActivate();
      checkState(heartBeatTimer, "heartBeatTimer"); //$NON-NLS-1$
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      cancelHeartBeatTask();
      super.doDeactivate();
    }

    private void scheduleHeartBeatTask()
    {
      heartBeatTimerTask = new TimerTask()
      {
        @Override
        public void run()
        {
          try
          {
            new Request(Server.this, SIGNAL_HEART_BEAT, "HeartBeat") //$NON-NLS-1$
            {
              @Override
              protected void requesting(ExtendedDataOutputStream out) throws Exception
              {
                out.writeBoolean(HEART_BEAT);
              }
            }.sendAsync();
          }
          catch (Exception ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      };

      heartBeatTimer.schedule(heartBeatTimerTask, 0L, heartBeatRate);
    }

    private void cancelHeartBeatTask()
    {
      if (heartBeatTimerTask != null)
      {
        heartBeatTimerTask.cancel();
        heartBeatTimerTask = null;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends ServerProtocolFactory
    {
      public Factory()
      {
        super(TYPE);
      }

      public Object create(String description) throws ProductCreationException
      {
        return new HeartBeatProtocol.Server();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class TimerInjector implements IElementProcessor
    {
      public TimerInjector()
      {
      }

      public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
          Object element)
      {
        if (element instanceof Server)
        {
          Server server = (Server)element;
          if (server.getHeartBeatTimer() == null)
          {
            server.setHeartBeatTimer(getTimer(container));
          }
        }

        return element;
      }

      protected Timer getTimer(IManagedContainer container)
      {
        return getDefaultTimer(container);
      }
    }
  }
}
