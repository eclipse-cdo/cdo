/*
 * Copyright (c) 2026 Eike Stepper and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.concurrent.SerializingExecutor;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Publishes branch and tag changes after the canonical server manager has applied them locally.
 *
 * @author Eike Stepper
 */
public class ServerBranchManager extends CDOBranchManagerImpl
{
  private final Repository repository;

  private final Object notificationLock = new Object();

  private final Deque<Notification> notifications = new ArrayDeque<>();

  private SerializingExecutor notificationExecutor;

  private boolean notificationsScheduled;

  public ServerBranchManager(Repository repository)
  {
    this.repository = repository;
  }

  @Override
  public void handleBranchChanged(InternalCDOBranch branch, ChangeKind changeKind, int... branchIDs)
  {
    Notification notification = null;

    if (changeKind != ChangeKind.DELETED)
    {
      InternalSession sender = getSender();
      int branchID = branch.getID();
      String branchName = changeKind == ChangeKind.RENAMED ? branch.getName() : null;
      CDOBranch notificationBranch = changeKind == ChangeKind.RENAMED ? new CDOBranchImpl(this, branchID, branchName, branch.getBase()) : branch;

      notification = enqueueNotification(() -> {
        InternalSessionManager sessionManager = repository.getSessionManager();

        if (changeKind == ChangeKind.RENAMED)
        {
          sessionManager.sendBranchNotification(sender, changeKind, notificationBranch);
        }
        else
        {
          sessionManager.sendBranchNotification(sender, changeKind, branch);
        }
      });
    }

    try
    {
      super.handleBranchChanged(branch, changeKind, branchIDs);
    }
    finally
    {
      if (notification != null)
      {
        Notification currentNotification = notification;

        afterTagEvents(() -> {
          scheduleNotifications();
          awaitNotification(currentNotification);
        });
      }
    }
  }

  @Override
  public CDOBranch[] deleteBranches(int id, OMMonitor monitor)
  {
    CDOBranch[] branches = super.deleteBranches(id, monitor);
    if (branches.length != 0)
    {
      int[] branchIDs = new int[branches.length];

      for (int i = 0; i < branches.length; i++)
      {
        branchIDs[i] = branches[i].getID();
      }

      fireBranchChangedEvent((InternalCDOBranch)branches[0], ChangeKind.DELETED, branchIDs);
    }

    return branches;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    SerializingExecutor executor = createNotificationExecutor();
    LifecycleUtil.activate(executor);
    notificationExecutor = executor;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    SerializingExecutor executor;

    synchronized (notificationLock)
    {
      executor = notificationExecutor;
      notificationExecutor = null;
      notificationsScheduled = false;
      failPendingNotifications(new IllegalStateException("Branch notification manager is deactivating")); //$NON-NLS-1$
    }

    if (executor != null)
    {
      LifecycleUtil.deactivate(executor);
    }

    super.doDeactivate();
  }

  @Override
  public CDOBranchPoint changeTagWithModCount(AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    AtomicReference<Runnable> notificationCompletion = new AtomicReference<>();

    try
    {
      return super.changeTagWithModCount(modCount, oldName, newName, branchPoint, notificationCompletion);
    }
    finally
    {
      completeTagNotification(notificationCompletion);
    }
  }

  /**
   * Creates the serial executor used to publish branch and tag notifications. The default executor delegates to the
   * repository executor, which remains owned by the repository. Subclasses may provide a different delegate when the
   * repository executor does not have sufficient capacity for bounded notification waits.
   *
   * @return the inactive serial executor to activate for this manager
   */
  protected SerializingExecutor createNotificationExecutor()
  {
    return new SerializingExecutor(repository.getExecutorService());
  }

  @Override
  protected Runnable branchesDeleted(CDOBranch[] branches)
  {
    InternalSession sender = getSender();
    CDOBranch[] notificationBranches = new CDOBranch[branches.length];

    for (int i = 0; i < branches.length; i++)
    {
      CDOBranch branch = branches[i];
      notificationBranches[i] = new CDOBranchImpl(this, branch.getID(), branch.getName(), branch.getBase());
    }

    Notification notification = enqueueNotification(() -> {
      InternalSessionManager sessionManager = repository.getSessionManager();
      sessionManager.sendBranchNotification(sender, ChangeKind.DELETED, notificationBranches);
    });

    return () -> afterTagEvents(() -> {
      scheduleNotifications();
      awaitNotification(notification);
    });
  }

  @Override
  protected Runnable tagChanged(int modCount, String oldName, String newName, CDOBranchPoint branchPoint, CDOBranchPoint result)
  {
    CDOBranchPoint notificationPoint = result instanceof CDOBranchTag ? getBranchPoint((CDOBranchTag)result) : branchPoint;
    InternalSession sender = getSender();

    Notification notification = enqueueNotification(() -> {
      InternalSessionManager sessionManager = repository.getSessionManager();
      sessionManager.sendTagNotification(sender, modCount, oldName, newName, notificationPoint);
    });

    return () -> {
      scheduleNotifications();
      awaitNotification(notification);
    };
  }

  @Override
  protected CDOBranchPoint changeTagWithCurrentModCount(CDOBranchTag tag, String oldName, String newName, CDOBranchPoint branchPoint,
      AtomicReference<Runnable> notificationCompletion)
  {
    AtomicReference<Runnable> completion = notificationCompletion == null ? new AtomicReference<>() : notificationCompletion;

    try
    {
      return super.changeTagWithCurrentModCount(tag, oldName, newName, branchPoint, completion);
    }
    finally
    {
      completeTagNotification(completion);
    }
  }

  private void completeTagNotification(AtomicReference<Runnable> notificationCompletion)
  {
    Runnable completion = notificationCompletion.get();
    if (completion != null)
    {
      super.afterTagEvents(completion);
    }
  }

  private Notification enqueueNotification(Runnable action)
  {
    Notification notification = new Notification(action);

    synchronized (notificationLock)
    {
      notifications.addLast(notification);
    }

    return notification;
  }

  private void scheduleNotifications()
  {
    SerializingExecutor executor;

    synchronized (notificationLock)
    {
      if (notifications.isEmpty() || notificationsScheduled)
      {
        return;
      }

      executor = notificationExecutor;
      if (executor == null)
      {
        failPendingNotifications(new IllegalStateException("Branch notification executor is unavailable")); //$NON-NLS-1$
        return;
      }

      notificationsScheduled = true;
    }

    try
    {
      Future<?> dispatch = executor.submit(this::dispatchNotifications);

      synchronized (notificationLock)
      {
        if (notificationExecutor != executor)
        {
          dispatch.cancel(false);
        }
      }
    }
    catch (RuntimeException ex)
    {
      synchronized (notificationLock)
      {
        notificationsScheduled = false;
        failPendingNotifications(ex);
      }

      OM.LOG.warn("Branch notification dispatcher rejected queued notifications", ex); //$NON-NLS-1$
    }
  }

  private void dispatchNotifications()
  {
    try
    {
      while (true)
      {
        Notification notification;

        synchronized (notificationLock)
        {
          notification = notifications.pollFirst();
        }

        if (notification == null)
        {
          return;
        }

        notification.publish();
      }
    }
    finally
    {
      boolean schedule;

      synchronized (notificationLock)
      {
        notificationsScheduled = false;
        schedule = notificationExecutor != null && !notifications.isEmpty();
      }

      if (schedule)
      {
        scheduleNotifications();
      }
    }
  }

  private void failPendingNotifications(Throwable failure)
  {
    Notification notification;

    while ((notification = notifications.pollFirst()) != null)
    {
      notification.completion.completeExceptionally(failure);
    }
  }

  private void awaitNotification(Notification notification)
  {
    try
    {
      notification.completion.get(15, TimeUnit.SECONDS);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      OM.LOG.warn("Interrupted while waiting for a branch or tag notification to be published", ex); //$NON-NLS-1$
    }
    catch (ExecutionException ex)
    {
      OM.LOG.warn("Branch or tag notification could not be published", ex.getCause()); //$NON-NLS-1$
    }
    catch (TimeoutException ex)
    {
      OM.LOG.warn("Timed out waiting for a branch or tag notification to be published", ex); //$NON-NLS-1$
    }
  }

  private static CDOBranchPoint getBranchPoint(CDOBranchTag tag)
  {
    return tag == null ? null : tag.getBranch().getPoint(tag.getTimeStamp());
  }

  private static InternalSession getSender()
  {
    return ServerOperationContext.getSession();
  }

  /**
   * Represents one notification registered in mutation order and its asynchronous publication result.
   *
   * @author Eike Stepper
   */
  private static final class Notification
  {
    private final Runnable action;

    private final CompletableFuture<Void> completion = new CompletableFuture<>();

    public Notification(Runnable action)
    {
      this.action = action;
    }

    public void publish()
    {
      try
      {
        action.run();
        completion.complete(null);
      }
      catch (RuntimeException | Error ex)
      {
        completion.completeExceptionally(ex);
        OM.LOG.error("Branch or tag notification failed", ex); //$NON-NLS-1$

        if (ex instanceof Error)
        {
          throw (Error)ex;
        }
      }
    }
  }
}
