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
import java.util.function.Consumer;

/**
 * Serializes server-authoritative lock changes by their session lock modification count.
 */
final class LockChangeSequencer
{
  private final TreeMap<Long, Entry> pending = new TreeMap<>();

  private final List<Runnable> pendingSnapshotCoveredActions = new ArrayList<>();

  private long currentLockModCount;

  private Entry executing;

  private Thread executingThread;

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
    pendingSnapshotCoveredActions.clear();
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
      synchronized (this)
      {
        entry = pending.remove(currentLockModCount + 1L);
        if (entry == null)
        {
          draining = false;
          notifyAll();
          return;
        }

        executing = entry;
        executingThread = Thread.currentThread();
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
    pendingSnapshotCoveredActions.clear();
    notifyAll();
    if (executing != null)
    {
      executing.completion.complete(failure);
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
      handler = failureHandler;
      notifyAll();
    }

    if (handler != null)
    {
      handler.accept(cause);
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
}
