package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Eike Stepper
 * @since 3.9
 */
public class SerializingExecutor extends Lifecycle implements Executor, Runnable
{
  private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();

  private final AtomicBoolean running = new AtomicBoolean();

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

  public final void execute(Runnable task)
  {
    tasks.add(task);

    if (isActive())
    {
      schedule(task);
    }
  }

  public final void run()
  {
    Runnable task;

    try
    {
      while ((task = tasks.poll()) != null)
      {
        if (!isActive())
        {
          // Bypass trySchedule() below.
          return;
        }

        try
        {
          task.run();
        }
        catch (RuntimeException ex)
        {
          handleFailedTask(task, ex);
        }
      }
    }
    finally
    {
      running.set(false);
    }

    trySchedule();
  }

  protected void handleFailedTask(Runnable task, Throwable failure)
  {
    OM.LOG.error("Execution of task failed: " + task, failure);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    trySchedule();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    tasks.clear();
    super.doDeactivate();
  }

  private void trySchedule()
  {
    if (!tasks.isEmpty())
    {
      schedule(null);
    }
  }

  private void schedule(Runnable task)
  {
    if (running.compareAndSet(false, true))
    {
      try
      {
        delegate.execute(this);
      }
      catch (RuntimeException ex)
      {
        cleanup(task);
      }
      catch (Error ex)
      {
        cleanup(task);
      }
    }
  }

  private void cleanup(Runnable task)
  {
    if (task != null)
    {
      tasks.remove(task);
    }

    running.set(false);
  }
}
