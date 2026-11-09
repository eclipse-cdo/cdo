/*
 * Copyright (c) 2019, 2025-2026 Eike Stepper and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

/**
 * An executor that runs submitted tasks one at a time, in the order in which they are queued, using a delegate executor.
 * The delegate remains externally owned. Before the first activation, {@link #execute(Runnable)} and all {@code submit}
 * methods retain tasks until activation. After deactivation, {@link #execute(Runnable)} continues to retain tasks until
 * a later activation, while {@code submit} rejects new tasks. Tasks that were queued when deactivation begins are
 * discarded, and their submitted futures are cancelled.
 *
 * @author Eike Stepper
 * @since 3.9
 */
public class SerializingExecutor extends Lifecycle implements Executor, Runnable
{
  private final Object lock = new Object();

  private final Queue<Task> tasks = new ArrayDeque<>();

  private boolean running;

  private boolean workerExecuting;

  private boolean deactivated;

  private Executor delegate;

  public SerializingExecutor(Executor delegate)
  {
    setDelegate(delegate);
  }

  public SerializingExecutor()
  {
    this(null);
  }

  public final Executor getDelegate()
  {
    return delegate;
  }

  public final void setDelegate(Executor delegate)
  {
    checkInactive();
    this.delegate = delegate == null ? SynchronousExecutor.INSTANCE : delegate;
  }

  /**
   * Executes the given command in queue order. Before the first activation, and after deactivation, the command is
   * retained until a later activation. A command queued when deactivation begins is discarded.
   *
   * @param command
   *          the command to execute
   * @throws NullPointerException
   *           if the command is {@code null}
   */
  @Override
  public final void execute(Runnable command)
  {
    Objects.requireNonNull(command);
    enqueue(new RunnableTask(command), false);
  }

  /**
   * Queues a task for serial execution and returns a future that completes when the task completes. Before the first
   * activation, the task is retained until activation. After deactivation, this method rejects the task. If scheduling
   * the worker is rejected, this method throws the rejection and cancels any futures that were already accepted but
   * cannot be scheduled as a result.
   *
   * @param task
   *          the task to execute
   * @return the future representing the task
   * @throws java.util.concurrent.RejectedExecutionException
   *           if this executor is deactivated or the delegate rejects the worker submission
   * @throws NullPointerException
   *           if the task is {@code null}
   * @since 3.31
   */
  public final Future<?> submit(Runnable task)
  {
    return submit(task, null);
  }

  /**
   * Queues a task for serial execution and returns a future that yields the given result after successful completion.
   * Before the first activation, the task is retained until activation. After deactivation, this method rejects the
   * task. Cancellation follows the standard {@link Future} contract.
   *
   * @param <T>
   *          the result type
   * @param task
   *          the task to execute
   * @param result
   *          the result to return after successful execution
   * @return the future representing the task
   * @throws java.util.concurrent.RejectedExecutionException
   *           if this executor is deactivated or the delegate rejects the worker submission
   * @throws NullPointerException
   *           if the task is {@code null}
   * @since 3.31
   */
  public final <T> Future<T> submit(Runnable task, T result)
  {
    FutureTask<T> future = new FutureTask<>(task, result);
    enqueue(new FutureSubmission<>(future), true);
    return future;
  }

  /**
   * Queues a callable for serial execution and returns a future for its result or failure. Before the first activation,
   * the task is retained until activation. After deactivation, this method rejects the task.
   *
   * @param <T>
   *          the result type
   * @param task
   *          the callable to execute
   * @return the future representing the task
   * @throws java.util.concurrent.RejectedExecutionException
   *           if this executor is deactivated or the delegate rejects the worker submission
   * @throws NullPointerException
   *           if the task is {@code null}
   * @since 3.31
   */
  public final <T> Future<T> submit(Callable<T> task)
  {
    FutureTask<T> future = new FutureTask<>(task);
    enqueue(new FutureSubmission<>(future), true);
    return future;
  }

  @Override
  public final void run()
  {
    synchronized (lock)
    {
      if (workerExecuting)
      {
        return;
      }

      workerExecuting = true;
    }

    try
    {
      while (true)
      {
        Task task;

        synchronized (lock)
        {
          if (!isActive())
          {
            return;
          }

          task = tasks.poll();
          if (task == null)
          {
            return;
          }
        }

        try
        {
          task.run();
        }
        catch (RuntimeException ex)
        {
          handleFailedTask(task.getRunnable(), ex);
        }
      }
    }
    finally
    {
      boolean schedule;

      synchronized (lock)
      {
        workerExecuting = false;
        running = false;

        schedule = isActive() && !tasks.isEmpty();
        if (schedule)
        {
          running = true;
        }
      }

      if (schedule)
      {
        scheduleWorker(null, false);
      }
    }
  }

  protected void handleFailedTask(Runnable task, Throwable failure)
  {
    OM.LOG.error("Execution of task failed: " + task, failure);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    synchronized (lock)
    {
      deactivated = false;
    }
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();
    scheduleQueuedTasks();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    List<Task> pending = new ArrayList<>();

    synchronized (lock)
    {
      deactivated = true;
      Task task;

      while ((task = tasks.poll()) != null)
      {
        pending.add(task);
      }
    }

    for (Task task : pending)
    {
      task.cancel();
    }

    super.doDeactivate();
  }

  private void enqueue(Task task, boolean futureSubmission)
  {
    boolean schedule;

    synchronized (lock)
    {
      if (futureSubmission && (deactivated || getLifecycleState() == LifecycleState.DEACTIVATING))
      {
        task.cancel();
        throw new RejectedExecutionException("Serializing executor is deactivated"); //$NON-NLS-1$
      }

      tasks.add(task);
      schedule = reserveWorker();
    }

    if (schedule)
    {
      scheduleWorker(task, futureSubmission);
    }
  }

  private boolean reserveWorker()
  {
    if (isActive() && !running)
    {
      running = true;
      return true;
    }

    return false;
  }

  private void scheduleQueuedTasks()
  {
    boolean schedule;

    synchronized (lock)
    {
      schedule = reserveWorker() && !tasks.isEmpty();
      if (!schedule && running && tasks.isEmpty() && !workerExecuting)
      {
        running = false;
      }
    }

    if (schedule)
    {
      scheduleWorker(null, false);
    }
  }

  private void scheduleWorker(Task rejectedSubmission, boolean propagateRejection)
  {
    try
    {
      delegate.execute(this);
    }
    catch (RuntimeException | Error ex)
    {
      List<Task> rejected = new ArrayList<>();

      synchronized (lock)
      {
        running = false;
        Task task;

        while ((task = tasks.poll()) != null)
        {
          rejected.add(task);
        }
      }

      for (Task task : rejected)
      {
        if (task == rejectedSubmission && propagateRejection)
        {
          task.cancel();
        }
        else
        {
          task.reject(ex);
        }
      }

      if (propagateRejection)
      {
        throw ex;
      }

      OM.LOG.warn("Serializing executor could not schedule queued tasks", ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  private interface Task
  {
    public Runnable getRunnable();

    public void run();

    public void cancel();

    public void reject(Throwable failure);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RunnableTask implements Task
  {
    private final Runnable runnable;

    public RunnableTask(Runnable runnable)
    {
      this.runnable = runnable;
    }

    @Override
    public Runnable getRunnable()
    {
      return runnable;
    }

    @Override
    public void run()
    {
      runnable.run();
    }

    @Override
    public void cancel()
    {
      // Preserve execute-only behavior: deactivation discards queued runnables.
    }

    @Override
    public void reject(Throwable failure)
    {
      // Preserve execute-only failure reporting through the executor log.
      OM.LOG.warn("Queued execute task was discarded because its worker was rejected", failure);
    }

    @Override
    public String toString()
    {
      return String.valueOf(runnable);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FutureSubmission<T> implements Task
  {
    private final FutureTask<T> future;

    public FutureSubmission(FutureTask<T> future)
    {
      this.future = future;
    }

    @Override
    public Runnable getRunnable()
    {
      return future;
    }

    @Override
    public void run()
    {
      future.run();
    }

    @Override
    public void cancel()
    {
      future.cancel(false);
    }

    @Override
    public void reject(Throwable failure)
    {
      future.cancel(false);
    }
  }
}
