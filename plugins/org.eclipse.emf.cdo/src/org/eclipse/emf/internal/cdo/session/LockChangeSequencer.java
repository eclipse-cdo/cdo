/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Serializes server-authoritative lock changes by their session lock modification count.
 *
 * @author Eike Stepper
 */
final class LockChangeSequencer
{
  private final TreeMap<Long, Entry> pending = new TreeMap<>();

  private final TreeMap<Long, List<BaselineAction>> pendingBaselineActions = new TreeMap<>();

  private final List<Runnable> pendingSnapshotCoveredActions = new ArrayList<>();

  private long currentLockModCount;

  private Entry executing;

  private Thread executingThread;

  // A selected action is no longer pending, but has not yet been admitted for execution.
  private BaselineAction selectedBaselineAction;

  // Closure waits for an admitted action to finish before it returns.
  private BaselineAction executingBaselineAction;

  private Thread executingBaselineThread;

  private boolean draining;

  private Throwable failure;

  private boolean suspended;

  private boolean closed;

  private long staleCutoff;

  private Consumer<Throwable> failureHandler;

  public LockChangeSequencer()
  {
  }

  public synchronized long getCurrentLockModCount()
  {
    return currentLockModCount;
  }

  public synchronized boolean isSuspended()
  {
    return suspended;
  }

  public synchronized boolean registerBaselineAction(long baseline, Runnable action)
  {
    return registerBaselineAction(baseline, action, null);
  }

  public synchronized boolean registerBaselineAction(long baseline, Runnable action, CompletableFuture<Boolean> completion)
  {
    if (closed //
        || failure != null //
        || baseline < 0 //
        || baseline < currentLockModCount //
        || executing != null && executing.lockModCount > baseline)
    {
      return false;
    }

    pendingBaselineActions.computeIfAbsent(baseline, key -> new ArrayList<>()).add(new BaselineAction(action, completion));
    notifyAll();
    return true;
  }

  public BaselineActionResult registerBaselineActionAndRunIfImmediatelyAdmissible(long baseline, Runnable action)
  {
    BaselineAction baselineAction = null;

    synchronized (this)
    {
      if (!registerBaselineAction(baseline, action))
      {
        return BaselineActionResult.REJECTED;
      }

      if (baseline == currentLockModCount //
          && !draining //
          && !suspended //
          && !closed //
          && failure == null //
          && executing == null //
          && selectedBaselineAction == null //
          && executingBaselineAction == null)
      {
        List<BaselineAction> actions = pendingBaselineActions.get(baseline);
        if (actions != null && !actions.isEmpty() && actions.get(0).action == action)
        {
          baselineAction = actions.remove(0);

          if (actions.isEmpty())
          {
            pendingBaselineActions.remove(baseline);
          }

          draining = true;
          executingBaselineAction = baselineAction;
          executingBaselineThread = Thread.currentThread();
          executingThread = Thread.currentThread();
        }
      }
    }

    if (baselineAction == null)
    {
      return BaselineActionResult.QUEUED;
    }

    executeImmediateBaselineAction(baselineAction);
    return BaselineActionResult.APPLIED;
  }

  public synchronized void setFailureHandler(Consumer<Throwable> failureHandler)
  {
    this.failureHandler = failureHandler;
  }

  public synchronized void suspend(long staleCutoff)
  {
    if (closed)
    {
      return;
    }

    suspended = true;
    this.staleCutoff = Math.max(this.staleCutoff, staleCutoff);
  }

  public synchronized List<Runnable> installSnapshotAndCollect(long baseline, Runnable cacheReplacement)
  {
    while (draining || executing != null)
    {
      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while waiting for lock changes to stop", ex); //$NON-NLS-1$
      }
    }

    if (closed)
    {
      throw new IllegalStateException("Lock change sequencer is closed"); //$NON-NLS-1$
    }

    cacheReplacement.run();

    List<Runnable> snapshotCoveredActions = new ArrayList<>(pendingSnapshotCoveredActions);
    List<BaselineAction> supersededBaselineActions = new ArrayList<>();

    pendingSnapshotCoveredActions.clear();

    for (List<BaselineAction> actions : pendingBaselineActions.headMap(baseline, true).values())
    {
      supersededBaselineActions.addAll(actions);
    }

    pendingBaselineActions.headMap(baseline, true).clear();

    List<Long> stale = new ArrayList<>(pending.headMap(baseline, true).keySet());
    for (Long count : stale)
    {
      Entry entry = pending.remove(count);
      if (entry.snapshotCoveredAction != null)
      {
        snapshotCoveredActions.add(entry.snapshotCoveredAction);
      }

      entry.completion.complete(null);
    }

    currentLockModCount = baseline;
    staleCutoff = Math.max(staleCutoff, baseline);
    failure = null;
    suspended = true;

    for (BaselineAction action : supersededBaselineActions)
    {
      action.complete(false);
    }

    notifyAll();
    return snapshotCoveredActions;
  }

  public synchronized void resume()
  {
    if (!closed)
    {
      failure = null;
      suspended = false;
      notifyAll();
    }
  }

  public synchronized boolean isExecuting(long lockModCount)
  {
    return executing != null && executing.lockModCount == lockModCount && executingThread == Thread.currentThread();
  }

  public void enqueueAndDrain(long lockModCount, Runnable action)
  {
    enqueue(lockModCount, action);
    drain();
  }

  public void enqueueDrainAndWait(long lockModCount, Runnable action)
  {
    Completion completion = enqueue(lockModCount, action);
    drain();
    completion.awaitUninterruptibly();
  }

  private void executeImmediateBaselineAction(BaselineAction baselineAction)
  {
    try
    {
      baselineAction.action.run();
      baselineAction.complete(true);
    }
    catch (Throwable ex)
    {
      baselineAction.completeExceptionally(ex);
      failAndRequestResync(ex);

      if (ex instanceof Error)
      {
        throw (Error)ex;
      }

      if (ex instanceof RuntimeException)
      {
        throw (RuntimeException)ex;
      }

      throw new RuntimeException(ex);
    }
    finally
    {
      synchronized (this)
      {
        executingBaselineAction = null;
        executingBaselineThread = null;
        executingThread = null;
        draining = false;
        notifyAll();
      }
    }
  }

  public synchronized boolean needsDrain()
  {
    if (draining || suspended || closed || failure != null)
    {
      return false;
    }

    List<BaselineAction> actions = pendingBaselineActions.get(currentLockModCount);
    return actions != null && !actions.isEmpty() || pending.containsKey(currentLockModCount + 1L);
  }

  public synchronized Completion enqueue(long lockModCount, Runnable action)
  {
    return enqueue(lockModCount, action, null);
  }

  public synchronized Completion enqueue(long lockModCount, Runnable action, Runnable snapshotCoveredAction)
  {
    if (lockModCount <= 0L)
    {
      throw new IllegalArgumentException("A sequenced lock change must have a positive lock modification count"); //$NON-NLS-1$
    }

    Completion completion = new Completion();
    if (closed)
    {
      completion.complete(failure == null ? new IllegalStateException("Lock change sequencer is closed") : failure); //$NON-NLS-1$
      return completion;
    }

    if (failure != null)
    {
      completion.complete(failure);
    }

    if (lockModCount <= Math.max(currentLockModCount, staleCutoff))
    {
      if (suspended && snapshotCoveredAction != null)
      {
        pendingSnapshotCoveredActions.add(snapshotCoveredAction);
      }

      completion.complete(null);
      return completion;
    }

    if (executing != null && executing.lockModCount == lockModCount)
    {
      return executing.completion;
    }

    Entry entry = pending.get(lockModCount);
    if (entry != null)
    {
      if (entry.action != action)
      {
        throw new IllegalStateException("Different lock changes claim lock modification count " + lockModCount); //$NON-NLS-1$
      }

      return entry.completion;
    }

    entry = new Entry(lockModCount, action, snapshotCoveredAction, completion);
    pending.put(lockModCount, entry);
    notifyAll();
    return completion;
  }

  public void drain()
  {
    synchronized (this)
    {
      if (draining || suspended || closed)
      {
        return;
      }

      draining = true;
    }

    for (;;)
    {
      Entry entry;
      BaselineAction baselineAction;

      synchronized (this)
      {
        if (suspended || closed || failure != null)
        {
          draining = false;
          notifyAll();
          return;
        }

        List<BaselineAction> actions = pendingBaselineActions.get(currentLockModCount);
        if (actions != null && !actions.isEmpty())
        {
          baselineAction = actions.remove(0);
          selectedBaselineAction = baselineAction;
          if (actions.isEmpty())
          {
            pendingBaselineActions.remove(currentLockModCount);
          }

          entry = null;
        }
        else
        {
          baselineAction = null;
          entry = pending.remove(currentLockModCount + 1L);
        }

        if (baselineAction != null)
        {
          // The monitor owns the boundary before the next mutation is selected.
          executingThread = Thread.currentThread();
        }
        else if (entry == null)
        {
          draining = false;
          notifyAll();
          return;
        }

        if (entry != null)
        {
          executing = entry;
          executingThread = Thread.currentThread();
        }
      }

      if (baselineAction != null)
      {
        boolean startBaselineAction;

        synchronized (this)
        {
          startBaselineAction = !closed && selectedBaselineAction == baselineAction;
          if (selectedBaselineAction == baselineAction)
          {
            selectedBaselineAction = null;
          }

          if (startBaselineAction)
          {
            executingBaselineAction = baselineAction;
            executingBaselineThread = Thread.currentThread();
          }
          else
          {
            executingThread = null;
            notifyAll();
          }
        }

        if (!startBaselineAction)
        {
          continue;
        }

        try
        {
          baselineAction.action.run();
          baselineAction.complete(true);
        }
        catch (Throwable ex)
        {
          baselineAction.completeExceptionally(ex);
          failAndRequestResync(ex);

          if (ex instanceof Error)
          {
            throw (Error)ex;
          }

          if (ex instanceof RuntimeException)
          {
            throw (RuntimeException)ex;
          }

          throw new RuntimeException(ex);
        }
        finally
        {
          synchronized (this)
          {
            executingBaselineAction = null;
            executingBaselineThread = null;
            executingThread = null;
            notifyAll();
          }
        }

        continue;
      }

      try
      {
        entry.action.run();
      }
      catch (Throwable ex)
      {
        entry.completion.complete(ex);
        failAndRequestResync(ex);
        if (ex instanceof Error)
        {
          throw (Error)ex;
        }

        if (ex instanceof RuntimeException)
        {
          throw (RuntimeException)ex;
        }

        throw new RuntimeException(ex);
      }

      synchronized (this)
      {
        if (failure != null || closed || suspended)
        {
          executing = null;
          executingThread = null;
          draining = false;
          notifyAll();
          return;
        }

        currentLockModCount = entry.lockModCount;
        executing = null;
        executingThread = null;
        notifyAll();
      }

      entry.completion.complete(null);
    }
  }

  public void await(long lockModCount)
  {
    Completion completion;
    synchronized (this)
    {
      if (failure != null)
      {
        throw new RuntimeException(failure);
      }

      while (lockModCount > currentLockModCount && !closed && pending.get(lockModCount) == null
          && (executing == null || executing.lockModCount != lockModCount))
      {
        try
        {
          wait();
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ex);
        }
      }

      if (closed)
      {
        throw new RuntimeException(failure);
      }

      if (lockModCount <= currentLockModCount)
      {
        return;
      }

      Entry entry = pending.get(lockModCount);
      completion = entry == null ? executing.completion : entry.completion;
    }

    drain();
    completion.awaitUninterruptibly();
  }

  public synchronized void close(Throwable cause)
  {
    closed = true;
    suspended = true;

    if (failure == null)
    {
      failure = cause == null ? new IllegalStateException("Lock change sequencer is closed") : cause; //$NON-NLS-1$
    }

    for (Map.Entry<Long, Entry> entry : pending.entrySet())
    {
      entry.getValue().completion.complete(failure);
    }

    pending.clear();
    completePendingBaselineActions(failure);
    pendingBaselineActions.clear();

    // Selection and admission are separate so closure can cancel an action in that interval.
    if (selectedBaselineAction != null)
    {
      selectedBaselineAction.completeExceptionally(failure);
      selectedBaselineAction = null;
    }

    if (executingBaselineAction != null)
    {
      // Release its waiter now; the close call below still waits for cache mutation to finish.
      executingBaselineAction.completeExceptionally(failure);
    }

    pendingSnapshotCoveredActions.clear();
    notifyAll();

    if (executing != null)
    {
      executing.completion.complete(failure);
    }

    boolean interrupted = false;

    while (executingBaselineAction != null && executingBaselineThread != Thread.currentThread())
    {
      try
      {
        wait();
      }
      catch (InterruptedException ex)
      {
        interrupted = true;
      }
    }

    if (interrupted)
    {
      Thread.currentThread().interrupt();
    }
  }

  private void failAndRequestResync(Throwable cause)
  {
    Consumer<Throwable> handler;

    synchronized (this)
    {
      executing = null;
      executingThread = null;
      draining = false;
      failure = cause;
      suspended = true;

      for (Entry entry : pending.values())
      {
        entry.completion.complete(cause);
      }

      completePendingBaselineActions(cause);
      pendingBaselineActions.clear();
      handler = failureHandler;

      notifyAll();
    }

    if (handler != null)
    {
      handler.accept(cause);
    }
  }

  private void completePendingBaselineActions(Throwable cause)
  {
    for (List<BaselineAction> actions : pendingBaselineActions.values())
    {
      for (BaselineAction action : actions)
      {
        action.completeExceptionally(cause);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  static final class Completion
  {
    private boolean done;

    private Throwable failure;

    public Completion()
    {
    }

    public synchronized void await() throws InterruptedException
    {
      while (!done)
      {
        wait();
      }

      rethrowFailure();
    }

    public void awaitUninterruptibly()
    {
      boolean interrupted = false;
      synchronized (this)
      {
        while (!done)
        {
          try
          {
            wait();
          }
          catch (InterruptedException ex)
          {
            interrupted = true;
          }
        }

        if (interrupted)
        {
          Thread.currentThread().interrupt();
        }

        rethrowFailure();
      }

      if (interrupted)
      {
        Thread.currentThread().interrupt();
      }
    }

    public synchronized void complete(Throwable failure)
    {
      if (!done)
      {
        this.failure = failure;
        done = true;
        notifyAll();
      }
    }

    private void rethrowFailure()
    {
      if (failure instanceof Error)
      {
        throw (Error)failure;
      }

      if (failure instanceof RuntimeException)
      {
        throw (RuntimeException)failure;
      }

      if (failure != null)
      {
        throw new RuntimeException(failure);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Entry
  {
    public final long lockModCount;

    public final Runnable action;

    public final Runnable snapshotCoveredAction;

    public final Completion completion;

    public Entry(long lockModCount, Runnable action, Runnable snapshotCoveredAction, Completion completion)
    {
      this.lockModCount = lockModCount;
      this.action = action;
      this.snapshotCoveredAction = snapshotCoveredAction;
      this.completion = completion;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class BaselineAction
  {
    public final Runnable action;

    private final CompletableFuture<Boolean> completion;

    public BaselineAction(Runnable action, CompletableFuture<Boolean> completion)
    {
      this.action = action;
      this.completion = completion;
    }

    public void complete(boolean applied)
    {
      if (completion != null)
      {
        completion.complete(applied);
      }
    }

    public void completeExceptionally(Throwable cause)
    {
      if (completion != null)
      {
        completion.completeExceptionally(cause);
      }
    }
  }

  /**
   * Describes how a query baseline action was admitted.
   *
   * @author Eike Stepper
   */
  public static enum BaselineActionResult
  {
    REJECTED, QUEUED, APPLIED
  }
}
