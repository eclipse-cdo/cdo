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
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchCreatedEvent;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.revision.cache.noop.NOOPRevisionCache;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.QueueRunner;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class CloneSynchronizer extends QueueRunner
{
  public static final int DEFAULT_RETRY_INTERVAL = 3;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, CloneSynchronizer.class);

  private CloneRepository clone;

  private int retryInterval = DEFAULT_RETRY_INTERVAL;

  private Object connectLock = new Object();

  private CDOSessionConfigurationFactory masterFactory;

  private InternalCDOSession master;

  private MasterListener masterListener = new MasterListener();

  public CloneSynchronizer()
  {
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

  public int getRetryInterval()
  {
    return retryInterval;
  }

  public void setRetryInterval(int retryInterval)
  {
    this.retryInterval = retryInterval;
  }

  public CDOSessionConfigurationFactory getMasterFactory()
  {
    return masterFactory;
  }

  public void setMasterFactory(CDOSessionConfigurationFactory masterFactory)
  {
    checkArg(masterFactory, "masterFactory"); //$NON-NLS-1$
    this.masterFactory = masterFactory;
  }

  public CDOSession getMaster()
  {
    return master;
  }

  @Override
  protected String getThreadName()
  {
    return "CloneSynchronizer"; //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(masterFactory, "masterFactory"); //$NON-NLS-1$
    checkState(clone, "clone"); //$NON-NLS-1$
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();
    scheduleConnect();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (master != null)
    {
      master.removeListener(masterListener);
      master.getBranchManager().removeListener(masterListener);
      master.close();
      master = null;
    }

    super.doDeactivate();
  }

  private void disconnect()
  {
    OM.LOG.info("Disconnected from master.");
    if (clone.getRootResourceID() == null)
    {
      clone.setState(CloneRepository.State.INITIAL);
    }
    else
    {
      clone.setState(CloneRepository.State.OFFLINE);
    }
  
    master.getBranchManager().removeListener(masterListener);
    master.removeListener(masterListener);
    master = null;
  
    reconnect();
  }

  private void reconnect()
  {
    clearQueue();
    long end = System.currentTimeMillis() + 1000L * retryInterval;

    for (;;)
    {
      long now = System.currentTimeMillis();
      if (now >= end || !isActive())
      {
        break;
      }

      ConcurrencyUtil.sleep(Math.min(100L, end - now));
    }

    if (isActive())
    {
      scheduleConnect();
    }
  }

  private void scheduleConnect()
  {
    synchronized (connectLock)
    {
      if (!isActive())
      {
        return;
      }

      if (clone.getState() == CloneRepository.State.SYNCING)
      {
        return;
      }

      if (clone.getState() == CloneRepository.State.ONLINE)
      {
        return;
      }

      addWork(new ConnectRunnable());
    }
  }

  private void scheduleSync()
  {
    addWork(new SynRunnable());
  }

  /**
   * @author Eike Stepper
   */
  private final class MasterListener implements IListener
  {
    public void notifyEvent(IEvent event)
    {
      if (!isActive())
      {
        return;
      }

      if (event instanceof CDOBranchCreatedEvent)
      {
        CDOBranchCreatedEvent e = (CDOBranchCreatedEvent)event;
        addWork(new BranchRunnable(e.getBranch()));
      }
      else if (event instanceof CDOSessionInvalidationEvent)
      {
        CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
        if (e.isRemote())
        {
          final CDOCommitInfo commitInfo = e;
          addWork(new CommitRunnable(commitInfo));
        }
      }
      else if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED && e.getSource() == master)
        {
          disconnect();
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConnectRunnable implements Runnable
  {
    public void run()
    {
      synchronized (connectLock)
      {
        checkActive();
        if (TRACER.isEnabled())
        {
          TRACER.format("Connecting to master ({0})...", CDOCommonUtil.formatTimeStamp()); //$NON-NLS-1$
        }

        try
        {
          CDOSessionConfiguration masterConfiguration = masterFactory.createSessionConfiguration();
          masterConfiguration.setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);

          master = (InternalCDOSession)masterConfiguration.openSession();

          // Ensure that incoming revisions are not cached!
          InternalCDORevisionCache cache = master.getRevisionManager().getCache();
          if (!(cache instanceof NOOPRevisionCache))
          {
            throw new IllegalStateException("Master session does not use a NOOPRevisionCache: "
                + cache.getClass().getName());
          }

          if (clone.getState() == CloneRepository.State.INITIAL)
          {
            CDOID rootResourceID = master.getRepositoryInfo().getRootResourceID();
            clone.setRootResourceID(rootResourceID);
            clone.setState(CloneRepository.State.OFFLINE);
          }
        }
        catch (Exception ex)
        {
          if (isActive())
          {
            OM.LOG.warn("Connection attempt failed. Retrying in " + retryInterval + " seconds...");
            reconnect();
          }

          return;
        }

        OM.LOG.info("Connected to master.");
        master.addListener(masterListener);
        master.getBranchManager().addListener(masterListener);

        scheduleSync();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SynRunnable implements Runnable
  {
    public void run()
    {
      checkActive();
      OM.LOG.info("Synchronizing with master...");
      clone.setState(CloneRepository.State.SYNCING);

      CDOSessionProtocol sessionProtocol = master.getSessionProtocol();
      sessionProtocol.syncRepository(clone);

      clone.setState(CloneRepository.State.ONLINE);
      OM.LOG.info("Synchronized with master.");
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class BranchRunnable implements Runnable
  {
    private CDOBranch branch;

    private BranchRunnable(CDOBranch branch)
    {
      this.branch = branch;
    }

    public void run()
    {
      clone.handleBranch(branch);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class CommitRunnable implements Runnable
  {
    private CDOCommitInfo commitInfo;

    private CommitRunnable(CDOCommitInfo commitInfo)
    {
      this.commitInfo = commitInfo;
    }

    public void run()
    {
      clone.handleCommitInfo(commitInfo);
    }
  }
}
