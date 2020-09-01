/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public class ThreadPool extends ThreadPoolExecutor implements RejectedExecutionHandler
{
  public static final String DEFAULT_THREAD_GROUP_NAME = ExecutorServiceFactory.DEFAULT_THREAD_GROUP_NAME;

  public static final int DEFAULT_CORE_POOL_SIZE = 10;

  public static final int DEFAULT_MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;

  public static final long DEFAULT_KEEP_ALIVE_SECONDS = 60;

  private static final Class<?> LINKED_BLOCKING_DEQUE_CLASS;

  private static final Method OFFER_LAST_METHOD;

  private static final int NO_DEADLOCK_DETECTION = 0;

  private static final int deadlockDetectionInterval = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.util.concurrent.ThreadPool.deadlockDetectionInterval",
      NO_DEADLOCK_DETECTION);

  private final AtomicInteger runningTasks = new AtomicInteger();

  private final AtomicInteger runTasks = new AtomicInteger();

  private int lastRunTasks = -1;

  private RejectedExecutionHandler userHandler;

  public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, ThreadFactory threadFactory)
  {
    super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, createWorkQueue(), threadFactory);
    ((WorkQueue)getQueue()).setThreadPool(this);

    // Call super setter because the setter in this class is overridden to set the userHandler field.
    super.setRejectedExecutionHandler(this);

    if (deadlockDetectionInterval != NO_DEADLOCK_DETECTION)
    {
      DeadlockDetector.INSTANCE.register(this);
    }
  }

  @Override
  public void setRejectedExecutionHandler(RejectedExecutionHandler handler)
  {
    userHandler = handler;
  }

  @Override
  public RejectedExecutionHandler getRejectedExecutionHandler()
  {
    return userHandler;
  }

  @Override
  public void rejectedExecution(Runnable task, ThreadPoolExecutor executor)
  {
    WorkQueue queue = (WorkQueue)getQueue();
    if (!queue.offerLast(task))
    {
      if (userHandler != null)
      {
        userHandler.rejectedExecution(task, this);
      }
      else
      {
        OM.LOG.error("Thread pool has rejected the task " + task);
      }
    }
  }

  @Override
  public int getActiveCount()
  {
    return runningTasks.get();
  }

  @Override
  protected void beforeExecute(Thread worker, Runnable task)
  {
    runningTasks.incrementAndGet();
    incrementRunTasks();
  }

  @Override
  protected void afterExecute(Runnable task, Throwable ex)
  {
    runningTasks.decrementAndGet();
  }

  /**
   * @since 3.9
   */
  protected void potentialDeadlockDetected()
  {
    BlockingQueue<Runnable> queue = getQueue();
    int size = queue.size();
    if (size > 0)
    {
      String poolName = toString();

      ExecutorService executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 100L, TimeUnit.MICROSECONDS, new SynchronousQueue<Runnable>());
      Runnable task;
      boolean first = true;

      while ((task = queue.poll()) != null)
      {
        if (first)
        {
          OM.LOG.warn("Potential deadlock detected in " + poolName + ". Executing " + size + " tasks...");
          first = false;
        }

        incrementRunTasks();
        executor.execute(task);
      }
    }
  }

  private void incrementRunTasks()
  {
    int current;
    int next;

    do
    {
      current = runTasks.get();
      next = current == Integer.MAX_VALUE ? 0 : current + 1;
    } while (!runTasks.compareAndSet(current, next));
  }

  /**
   * This method decides whether a new task will be added to the {@link WorkQueue} (and eventually picked up by
   * an existing worker), or assigned to a new worker.
   * <p>
   * It is called from {@link WorkQueue#offer(Runnable)}, which, in turn, is called from {@link #execute(Runnable)}.
   * When this method is called the core workers are already created, i.e., {@link #getPoolSize() pool size} >=
   * {@link #getCorePoolSize() core pool size}.
   * <p>
   * Note that, due to the unsynchronized calls to the various metric-providing methods,
   * it can happen that the thread pool will not be able to actually create a new worker at the time it is supposed
   * to do it. In this case the {@link #rejectedExecution(Runnable, ThreadPoolExecutor) rejectedExecution()} method
   * will be called, which, as a last resort, adds the new task to the work queue (even though here
   * it was decided not to do so).
   */
  private boolean shallEnqueue()
  {
    int poolSize = getPoolSize();
    if (getQueue().size() < poolSize - getActiveCount())
    {
      // More inactive workers exist than there are tasks in the queue; the task should be enqueued.
      return true;
    }

    if (poolSize >= getMaximumPoolSize())
    {
      // Pool is full; the task should be enqueued.
      return true;
    }

    // A new worker should be created.
    return false;
  }

  public static ThreadPool create()
  {
    return create(null, DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_SECONDS);
  }

  public static ThreadPool create(String description)
  {
    String threadGroupName = null;
    int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;
    long keepAliveSeconds = DEFAULT_KEEP_ALIVE_SECONDS;

    if (!StringUtil.isEmpty(description))
    {
      String[] tokens = description.split(":");
      if (tokens.length > 0)
      {
        threadGroupName = tokens[0];
        if (tokens.length > 1)
        {
          try
          {
            corePoolSize = Integer.parseInt(tokens[1]);
          }
          catch (NumberFormatException ex)
          {
            //$FALL-THROUGH$
          }

          if (tokens.length > 2)
          {
            try
            {
              maximumPoolSize = Integer.parseInt(tokens[2]);
            }
            catch (NumberFormatException ex)
            {
              //$FALL-THROUGH$
            }

            if (tokens.length > 3)
            {
              try
              {
                keepAliveSeconds = Long.parseLong(tokens[3]);
              }
              catch (NumberFormatException ex)
              {
                //$FALL-THROUGH$
              }
            }
          }
        }
      }
    }

    return create(threadGroupName, corePoolSize, maximumPoolSize, keepAliveSeconds);
  }

  public static ThreadPool create(String threadGroupName, int corePoolSize, int maximumPoolSize, long keepAliveSeconds)
  {
    ThreadFactory threadFactory = createThreadFactory(threadGroupName);
    return new ThreadPool(corePoolSize, maximumPoolSize, keepAliveSeconds, threadFactory);
  }

  private static ThreadFactory createThreadFactory(String threadGroupName)
  {
    if (threadGroupName == null)
    {
      threadGroupName = DEFAULT_THREAD_GROUP_NAME;
    }

    final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);

    ThreadFactory threadFactory = new ThreadFactory()
    {
      private final AtomicInteger num = new AtomicInteger();

      @Override
      public Thread newThread(Runnable task)
      {
        Thread thread = new Thread(threadGroup, task, threadGroup.getName() + "-thread-" + num.incrementAndGet());
        thread.setDaemon(true);
        return thread;
      }
    };

    return threadFactory;
  }

  private static WorkQueue createWorkQueue()
  {
    if (LINKED_BLOCKING_DEQUE_CLASS != null)
    {
      try
      {
        return new WorkQueueJRE16();
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }
    }

    return new WorkQueueJRE15();
  }

  static
  {
    Class<?> c = null;
    Method m = null;

    try
    {
      c = Class.forName("java.util.concurrent.LinkedBlockingDeque");
      m = c.getMethod("offerLast", Object.class);
    }
    catch (Throwable ex)
    {
      c = null;
      m = null;
    }

    LINKED_BLOCKING_DEQUE_CLASS = c;
    OFFER_LAST_METHOD = m;
  }

  /**
   * @author Eike Stepper
   */
  private interface WorkQueue extends BlockingQueue<Runnable>
  {
    public void setThreadPool(ThreadPool threadPool);

    public boolean offerLast(Runnable task);

  }

  /**
   * @author Eike Stepper
   */
  private static final class WorkQueueJRE15 extends LinkedBlockingQueue<Runnable> implements WorkQueue
  {
    private static final long serialVersionUID = 1L;

    private ThreadPool threadPool;

    public WorkQueueJRE15()
    {
    }

    @Override
    public void setThreadPool(ThreadPool threadPool)
    {
      this.threadPool = threadPool;
    }

    @Override
    public boolean offerLast(Runnable task)
    {
      // Call the super method because the method in this class is overridden.
      return super.offer(task);
    }

    @Override
    public boolean offer(Runnable task)
    {
      if (threadPool.shallEnqueue())
      {
        return super.offer(task);
      }

      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class WorkQueueJRE16 extends AbstractQueue<Runnable> implements WorkQueue
  {
    private final BlockingQueue<Runnable> delegate = createDelegate();

    private ThreadPool threadPool;

    public WorkQueueJRE16()
    {
    }

    @Override
    public void setThreadPool(ThreadPool threadPool)
    {
      this.threadPool = threadPool;
    }

    @Override
    public boolean offerLast(Runnable task)
    {
      try
      {
        // Call the LinkedBlockingDeque.offerLast() method because it does NOT call
        // the overridden offer() method in this class.
        return (Boolean)OFFER_LAST_METHOD.invoke(delegate, task);
      }
      catch (Throwable ex)
      {
        return false;
      }
    }

    @Override
    public boolean offer(Runnable task)
    {
      if (threadPool.shallEnqueue())
      {
        return delegate.offer(task);
      }

      return false;
    }

    @Override
    public boolean offer(Runnable taske, long timeout, TimeUnit unit) throws InterruptedException
    {
      return delegate.offer(taske, timeout, unit);
    }

    @Override
    public int size()
    {
      return delegate.size();
    }

    @Override
    public Runnable take() throws InterruptedException
    {
      return delegate.take();
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException
    {
      return delegate.poll(timeout, unit);
    }

    @Override
    public Runnable poll()
    {
      return delegate.poll();
    }

    @Override
    public Iterator<Runnable> iterator()
    {
      return delegate.iterator();
    }

    @Override
    public Runnable peek()
    {
      return delegate.peek();
    }

    @Override
    public void put(Runnable task) throws InterruptedException
    {
      delegate.put(task);
    }

    @Override
    public int remainingCapacity()
    {
      return delegate.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super Runnable> c)
    {
      return delegate.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements)
    {
      return delegate.drainTo(c, maxElements);
    }

    @SuppressWarnings("unchecked")
    private static BlockingQueue<Runnable> createDelegate()
    {
      try
      {
        Constructor<?> constructor = LINKED_BLOCKING_DEQUE_CLASS.getConstructor();
        return (BlockingQueue<Runnable>)constructor.newInstance();
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      return new LinkedBlockingQueue<>();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DeadlockDetector extends Worker
  {
    public static final DeadlockDetector INSTANCE = new DeadlockDetector();

    private volatile ArrayList<ThreadPool> pools = new ArrayList<>();

    private DeadlockDetector()
    {
      setDaemon(true);
      activate();
    }

    public void register(ThreadPool pool)
    {
      ArrayList<ThreadPool> newList = new ArrayList<>(pools);
      newList.add(pool);
      pools = newList;
    }

    private void unregister(ThreadPool pool)
    {
      ArrayList<ThreadPool> newList = new ArrayList<>(pools);
      newList.remove(pool);
      pools = newList;
    }

    @Override
    protected String getThreadName()
    {
      return DeadlockDetector.class.getSimpleName();
    }

    @Override
    protected void work(WorkContext context) throws Exception
    {
      ArrayList<ThreadPool> list = pools;
      int size = list.size();

      for (int i = 0; i < size; i++)
      {
        ThreadPool pool = list.get(i);
        if (pool.isShutdown())
        {
          unregister(pool);
          continue;
        }

        work(pool);
      }

      context.nextWork(deadlockDetectionInterval);
    }

    private void work(ThreadPool pool)
    {
      int lastRunTasks = pool.runTasks.get();
      if (lastRunTasks != pool.lastRunTasks)
      {
        pool.lastRunTasks = lastRunTasks;
      }
      else
      {
        if (pool.getPoolSize() == pool.getMaximumPoolSize())
        {
          pool.potentialDeadlockDetected();
        }
      }
    }
  }
}
