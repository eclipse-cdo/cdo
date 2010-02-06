/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.offline;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.spi.common.CDOCloningContext;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class CloneSynchronizer extends QueueRunner
{
  public static final long NEVER_SYNCHRONIZED = -1;

  public static final int DEFAULT_RETRY_INTERVAL = 3;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, CloneSynchronizer.class);

  private int retryInterval = DEFAULT_RETRY_INTERVAL;

  private CDOSessionConfiguration sessionConfiguration;

  private InternalCDOSession session;

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
        sync();
      }
    }
  };

  private MutableLong syncedTimeStamp = new MutableLong();

  public CloneSynchronizer()
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

    long masterTimeStamp = session.getLastUpdateTime();
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

  public long getSyncedTimeStamp()
  {
    return syncedTimeStamp.getValue();
  }

  public void setSyncedTimeStamp(long syncedTimeStamp)
  {
    this.syncedTimeStamp.setValue(syncedTimeStamp);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(sessionConfiguration, "sessionConfiguration");
    checkState(syncedTimeStamp.isSpecified(), "syncedTimeStamp");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    connect();
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
    addWork(new Runnable()
    {
      public void run()
      {
        try
        {
          checkActive();
          if (TRACER.isEnabled())
          {
            TRACER.format("Connecting to master ({0})...", CDOCommonUtil.formatTimeStamp()); //$NON-NLS-1$
          }

          session = (InternalCDOSession)sessionConfiguration.openSession();

          OM.LOG.info("Connected to master.");
          session.addListener(deactivationListener);

          syncedTimeStamp.setValue(NEVER_SYNCHRONIZED);
          session.addListener(invalidationListener);

          sync();
        }
        catch (Exception ex)
        {
          OM.LOG.warn("Connection attempt failed. Retrying in " + retryInterval + " seconds...");

          checkActive();
          ConcurrencyUtil.sleep(1000L * retryInterval); // TODO Respect deactivation

          checkActive();
          connect();
        }
      }
    });
  }

  private void sync()
  {
    addWork(new Runnable()
    {
      public void run()
      {
        checkActive();
        final long masterTimeStamp = session.getLastUpdateTime();
        final long syncedTimeStamp = getSyncedTimeStamp();
        if (syncedTimeStamp < masterTimeStamp)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Synchronizing with master ({0})...", CDOCommonUtil.formatTimeStamp(masterTimeStamp)); //$NON-NLS-1$
          }

          session.cloneRepository(new CDOCloningContext()
          {
            public long getStartTime()
            {
              return syncedTimeStamp;
            }

            public long getEndTime()
            {
              return masterTimeStamp;
            }

            public int getBranchID()
            {
              return 0;
            }

            public void addPackageUnit(String id)
            {
              InternalCDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(id);
              sync(packageUnit);
            }

            public void addBranch(int id)
            {
              InternalCDOBranch branch = session.getBranchManager().getBranch(id);
              sync(branch);
            }

            public void addRevision(InternalCDORevision revision)
            {
              sync(revision);
            }
          });

          OM.LOG.info("Synchronized with master.");
        }
      }
    });
  }

  private void sync(InternalCDOPackageUnit packageUnit)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Syncronized package unit {0}", packageUnit); //$NON-NLS-1$
    }
  }

  private void sync(InternalCDOBranch branch)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Syncronized branch {0}", branch); //$NON-NLS-1$
    }
  }

  private void sync(InternalCDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Syncronized revision {0}", revision); //$NON-NLS-1$
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
    private long value = CDORevision.UNSPECIFIED_DATE;

    public MutableLong()
    {
    }

    public synchronized boolean isSpecified()
    {
      return value != CDORevision.UNSPECIFIED_DATE;
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
