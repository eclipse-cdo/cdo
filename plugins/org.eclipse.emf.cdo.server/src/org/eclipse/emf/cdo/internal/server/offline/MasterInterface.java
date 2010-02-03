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
package org.eclipse.emf.cdo.internal.server.offline;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class MasterInterface extends QueueRunner
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, MasterInterface.class);

  private static final int NEVER_SYNCHRONIZED = -1;

  private int retryInterval = 3;

  private CDOSessionConfiguration sessionConfiguration;

  private CDOSession session;

  private IListener deactivationListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      OM.LOG.info("Disconnected from master.");
      session.removeListener(deactivationListener);
      session.removeListener(invalidationListener);
      session = null;
      connect();
    }
  };

  private IListener invalidationListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
        long timeStamp = e.getTimeStamp();
        if (TRACER.isEnabled())
        {
          TRACER.format("{0}", e);
        }
        masterTimeStamp.setValue(timeStamp);
        sync();
      }
    }
  };

  private MutableLong masterTimeStamp = new MutableLong();

  private MutableLong syncedTimeStamp = new MutableLong();

  public MasterInterface()
  {
  }

  public int getRetryInterval()
  {
    return retryInterval;
  }

  public void setRetryInterval(int retryInterval)
  {
    this.retryInterval = retryInterval;
  }

  public CDOSessionConfiguration getSessionConfiguration()
  {
    return sessionConfiguration;
  }

  public void setSessionConfiguration(CDOSessionConfiguration sessionConfiguration)
  {
    checkInactive();
    this.sessionConfiguration = sessionConfiguration;
  }

  public State getState()
  {
    if (session == null)
    {
      return State.OFFLINE;
    }

    long masterTimeStamp = getMasterTimeStamp();
    long syncedTimeStamp = getSyncedTimeStamp();
    if (masterTimeStamp > syncedTimeStamp)
    {
      return State.SYNCING;
    }

    return State.ONLINE;
  }

  public CDOSession getSession()
  {
    return session;
  }

  public long getMasterTimeStamp()
  {
    return masterTimeStamp.getValue();
  }

  public long getSyncedTimeStamp()
  {
    return syncedTimeStamp.getValue();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(sessionConfiguration, "sessionConfiguration");
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (session != null)
    {
      session.removeListener(deactivationListener);
      session.removeListener(invalidationListener);
      session.close();
      session = null;
    }

    super.doDeactivate();
  }

  private void connect()
  {
    if (session == null)
    {
      addWork(new Runnable()
      {
        public void run()
        {
          try
          {
            OM.LOG.info("Connecting to master...");
            session = sessionConfiguration.openSession();

            OM.LOG.info("Connected to master");
            session.addListener(deactivationListener);

            masterTimeStamp.setValue(0);
            syncedTimeStamp.setValue(NEVER_SYNCHRONIZED);
            session.addListener(invalidationListener);

            sync();
          }
          catch (Exception ex)
          {
            OM.LOG.warn("Connection attempt failed. Retrying in " + retryInterval + " seconds...");
            ConcurrencyUtil.sleep(1000L * retryInterval); // TODO Respect deactivation
            connect();
          }
        }
      });

    }
  }

  private void sync()
  {
    if (getState() == State.SYNCING)
    {
      addWork(new Runnable()
      {
        public void run()
        {
          OM.LOG.info("Synchronizing with master...");

          OM.LOG.info("Synchronized with master.");
        }
      });
    }
  }

  /**
   * @author Eike Stepper
   */
  public static enum State
  {
    OFFLINE, SYNCING, ONLINE
  }

  /**
   * @author Eike Stepper
   */
  private static final class MutableLong
  {
    private long value;

    public MutableLong()
    {
    }

    public synchronized long getValue()
    {
      return value;
    }

    public synchronized void setValue(long value)
    {
      this.value = value;
    }

    @Override
    public String toString()
    {
      return String.valueOf(value);
    }
  }
}
