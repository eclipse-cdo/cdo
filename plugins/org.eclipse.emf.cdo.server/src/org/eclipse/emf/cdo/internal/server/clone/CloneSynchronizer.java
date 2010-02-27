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
package org.eclipse.emf.cdo.internal.server.clone;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.revision.cache.noop.NOOPRevisionCache;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
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

  private CDOSessionConfiguration masterConfiguration;

  private InternalCDOSession master;

  private IListener masterListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
        if (e.isRemote())
        {
          replicate(e);
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          OM.LOG.info("Disconnected from master.");
          master.removeListener(masterListener);
          master = null;
          connect();
        }
      }
    }
  };

  private MutableLong syncedTimeStamp = new MutableLong();

  private CloneRepository clone;

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

  public CDOSessionConfiguration getMasterConfiguration()
  {
    return masterConfiguration;
  }

  public void setMasterConfiguration(CDOSessionConfiguration masterConfiguration)
  {
    checkInactive();
    this.masterConfiguration = masterConfiguration;
  }

  public CloneRepository getClone()
  {
    return clone;
  }

  public void setClone(CloneRepository clone)
  {
    checkInactive();
    this.clone = clone;
  }

  public CDOSession getMaster()
  {
    return master;
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
    checkState(masterConfiguration, "masterConfiguration"); //$NON-NLS-1$
    checkState(syncedTimeStamp.isSpecified(), "syncedTimeStamp"); //$NON-NLS-1$
    checkState(clone, "clone"); //$NON-NLS-1$
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
    if (master != null)
    {
      master.removeListener(masterListener);
      master.close();
      master = null;
    }

    super.doDeactivate();
  }

  private void connect()
  {
    addWork(new Runnable()
    {
      public void run()
      {
        checkActive();
        if (TRACER.isEnabled())
        {
          TRACER.format("Connecting to master ({0})...", CDOCommonUtil.formatTimeStamp()); //$NON-NLS-1$
        }

        syncedTimeStamp.setValue(NEVER_SYNCHRONIZED);

        try
        {
          masterConfiguration.setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);
          master = (InternalCDOSession)masterConfiguration.openSession();

          // Ensure that incoming revisions are not cached!
          InternalCDORevisionCache cache = master.getRevisionManager().getCache();
          if (!(cache instanceof NOOPRevisionCache))
          {
            throw new IllegalStateException("Master session does not use a NOOPRevisionCache: "
                + cache.getClass().getName());
          }
        }
        catch (Exception ex)
        {
          if (isActive())
          {
            OM.LOG.warn("Connection attempt failed. Retrying in " + retryInterval + " seconds...", ex);

            ConcurrencyUtil.sleep(1000L * retryInterval); // TODO Respect deactivation

            checkActive();
            connect();
          }

          return;
        }

        OM.LOG.info("Connected to master.");
        master.addListener(masterListener);
        sync();
      }
    });
  }

  private void sync()
  {
    // addWork(new Runnable()
    // {
    // public void run()
    // {
    // checkActive();
    // final long masterTimeStamp = master.getLastUpdateTime();
    // final long syncedTimeStamp = getSyncedTimeStamp();
    // if (syncedTimeStamp < masterTimeStamp)
    // {
    // if (TRACER.isEnabled())
    // {
    //            TRACER.format("Synchronizing with master ({0})...", CDOCommonUtil.formatTimeStamp(masterTimeStamp)); //$NON-NLS-1$
    // }
    //
    // master.cloneRepository(new CDOCloningContext()
    // {
    // public long getStartTime()
    // {
    // return syncedTimeStamp;
    // }
    //
    // public long getEndTime()
    // {
    // return masterTimeStamp;
    // }
    //
    // public int getBranchID()
    // {
    // return 0;
    // }
    //
    // public void addPackageUnit(String id)
    // {
    // InternalCDOPackageUnit packageUnit = master.getPackageRegistry().getPackageUnit(id);
    // sync(packageUnit);
    // }
    //
    // public void addBranch(int id)
    // {
    // InternalCDOBranch branch = master.getBranchManager().getBranch(id);
    // sync(branch);
    // }
    //
    // public void addRevision(InternalCDORevision revision)
    // {
    // sync(revision);
    // }
    // });
    //
    // OM.LOG.info("Synchronized with master.");
    // }
    // }
    // });
  }

  // private void sync(InternalCDOPackageUnit packageUnit)
  // {
  // if (TRACER.isEnabled())
  // {
  //      TRACER.format("Syncronized package unit {0}", packageUnit); //$NON-NLS-1$
  // }
  // }
  //
  // private void sync(InternalCDOBranch branch)
  // {
  // if (TRACER.isEnabled())
  // {
  //      TRACER.format("Syncronized branch {0}", branch); //$NON-NLS-1$
  // }
  // }
  //
  // private void sync(InternalCDORevision revision)
  // {
  // if (TRACER.isEnabled())
  // {
  //      TRACER.format("Syncronized revision {0}", revision); //$NON-NLS-1$
  // }
  // }

  protected void replicate(CDOCommitInfo commitInfo)
  {
    clone.replicate(commitInfo);
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
