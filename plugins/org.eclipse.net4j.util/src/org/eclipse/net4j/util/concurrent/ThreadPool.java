/*
 * Copyright (c) 2004-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.StringUtil;

import java.lang.reflect.Method;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
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

  public static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;

  public static final long DEFAULT_KEEP_ALIVE_SECONDS = 60;

  private static final Class<?> LINKED_BLOCKING_DEQUE_CLASS;

  private static final Method ADD_FIRST_METHOD;

  private final Executor defaultExecutor = new Executor()
  {
    public void execute(Runnable runnable)
    {
      ThreadPool.super.execute(runnable);
    }
  };

  private final Executor namingExecutor = new Executor()
  {
    public void execute(Runnable runnable)
    {
      if (runnable instanceof RunnableWithName)
      {
        String name = ((RunnableWithName)runnable).getName();
        if (name != null)
        {
          Thread thread = new Thread(runnable, name);
          thread.setDaemon(true);
          thread.start();
          return;
        }
      }

      ThreadPool.super.execute(runnable);
    }
  };

  private volatile Executor executor = defaultExecutor;

  public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, ThreadFactory threadFactory)
  {
    super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, createWorkQueue(), threadFactory);
    ((WorkQueue)getQueue()).setThreadPool(this);
    setRejectedExecutionHandler(this);
  }

  @Override
  public void execute(final Runnable runnable)
  {
    executor.execute(runnable);
  }

  public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor)
  {
    ((WorkQueue)getQueue()).addFirst(runnable);
  }

  final void setNaming(boolean naming)
  {
    executor = naming ? namingExecutor : defaultExecutor;
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

      public Thread newThread(Runnable r)
      {
        Thread thread = new Thread(threadGroup, r, threadGroup.getName() + "-thread-" + num.incrementAndGet());
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
      m = c.getMethod("addFirst", Object.class);
    }
    catch (Throwable ex)
    {
      c = null;
      m = null;
    }

    LINKED_BLOCKING_DEQUE_CLASS = c;
    ADD_FIRST_METHOD = m;
  }

  /**
   * @author Eike Stepper
   */
  private interface WorkQueue extends BlockingQueue<Runnable>
  {
    public void setThreadPool(ThreadPool threadPool);

    public void addFirst(Runnable runnable);
  }

  /**
   * @author Eike Stepper
   */
  private static final class WorkQueueJRE15 extends LinkedBlockingQueue<Runnable>implements WorkQueue
  {
    private static final long serialVersionUID = 1L;

    private ThreadPool threadPool;

    public WorkQueueJRE15()
    {
    }

    public void setThreadPool(ThreadPool threadPool)
    {
      this.threadPool = threadPool;
    }

    public void addFirst(Runnable runnable)
    {
      super.offer(runnable);
    }

    @Override
    public boolean offer(Runnable runnable)
    {
      if (threadPool.getPoolSize() < threadPool.getMaximumPoolSize())
      {
        return false;
      }

      return super.offer(runnable);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class WorkQueueJRE16 extends AbstractQueue<Runnable>implements WorkQueue
  {
    private final BlockingQueue<Runnable> delegate = createDelegate();

    private ThreadPool threadPool;

    public WorkQueueJRE16()
    {
    }

    public void setThreadPool(ThreadPool threadPool)
    {
      this.threadPool = threadPool;
    }

    public void addFirst(Runnable runnable)
    {
      try
      {
        ADD_FIRST_METHOD.invoke(delegate, runnable);
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      delegate.offer(runnable);
    }

    public boolean offer(Runnable r)
    {
      if (threadPool.getPoolSize() < threadPool.getMaximumPoolSize())
      {
        return false;
      }

      return delegate.offer(r);
    }

    @Override
    public int size()
    {
      return delegate.size();
    }

    public Runnable poll()
    {
      return delegate.poll();
    }

    @Override
    public Iterator<Runnable> iterator()
    {
      return delegate.iterator();
    }

    public Runnable peek()
    {
      return delegate.peek();
    }

    public void put(Runnable e) throws InterruptedException
    {
      delegate.put(e);
    }

    public boolean offer(Runnable e, long timeout, TimeUnit unit) throws InterruptedException
    {
      return delegate.offer(e, timeout, unit);
    }

    public Runnable take() throws InterruptedException
    {
      return delegate.take();
    }

    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException
    {
      return delegate.poll(timeout, unit);
    }

    public int remainingCapacity()
    {
      return delegate.remainingCapacity();
    }

    public int drainTo(Collection<? super Runnable> c)
    {
      return delegate.drainTo(c);
    }

    public int drainTo(Collection<? super Runnable> c, int maxElements)
    {
      return delegate.drainTo(c, maxElements);
    }

    @SuppressWarnings("unchecked")
    private static BlockingQueue<Runnable> createDelegate()
    {
      try
      {
        return (BlockingQueue<Runnable>)LINKED_BLOCKING_DEQUE_CLASS.newInstance();
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      return new LinkedBlockingQueue<Runnable>();
    }
  }
}
