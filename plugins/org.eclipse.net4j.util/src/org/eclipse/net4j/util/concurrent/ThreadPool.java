/*
 * Copyright (c) 2015, 2019, 2020, 2022, 2024-2026 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
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

  public static final int DEFAULT_MAXIMUM_POOL_SIZE = Math.max(DEFAULT_CORE_POOL_SIZE, Runtime.getRuntime().availableProcessors() * 4);

  public static final long DEFAULT_KEEP_ALIVE_SECONDS = 60;

  private static final Class<?> LINKED_BLOCKING_DEQUE_CLASS;

  private static final Method OFFER_LAST_METHOD;

  private static final int NO_DEADLOCK_DETECTION = 0;

  private static final int deadlockDetectionInterval = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.util.concurrent.ThreadPool.deadlockDetectionInterval",
      NO_DEADLOCK_DETECTION);

  private final Object admissionLock = new Object();

  private final ThreadLocal<Submission> submission = new ThreadLocal<>();

  private final BlockingQueue<Runnable> queueView;

  private final AtomicInteger runTasks = new AtomicInteger();

  private int pendingTaskCount;

  private int lastRunTasks = -1;

  private boolean potentialDeadlockReported;

  private volatile RejectedExecutionHandler userHandler;

  public ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveSeconds, ThreadFactory threadFactory)
  {
    super(corePoolSize, maximumPoolSize, keepAliveSeconds, TimeUnit.SECONDS, createWorkQueue(), threadFactory);

    WorkQueue workQueue = (WorkQueue)super.getQueue();
    workQueue.setThreadPool(this);

    queueView = new QueueView(this, workQueue);

    // Call super setter because the setter in this class is overridden to set the userHandler field.
    super.setRejectedExecutionHandler(this);

    if (deadlockDetectionInterval > NO_DEADLOCK_DETECTION)
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

  /**
   * Returns a live view of this executor's task queue. Queue operations have the same blocking and ordering behavior as
   * the underlying queue, and removals through this view are reflected in the pool's pending-task accounting.
   *
   * @return the live task-queue view
   */
  @Override
  public BlockingQueue<Runnable> getQueue()
  {
    BlockingQueue<Runnable> queue = queueView;
    return queue == null ? super.getQueue() : queue;
  }

  /**
   * Handles a task that the executor could not assign to a worker. The task is retained in the work queue only while
   * the executor is running and either has reached its maximum size or has an idle worker; otherwise the configured
   * user handler is invoked, or the task is rejected when no user handler is configured.
   *
   * @param task the task that could not be assigned to a worker
   * @param executor the executor that rejected the task
   */
  @Override
  public void rejectedExecution(Runnable task, ThreadPoolExecutor executor)
  {
    WorkQueue queue = (WorkQueue)super.getQueue();
    boolean queued = false;

    if (!executor.isShutdown())
    {
      synchronized (admissionLock)
      {
        int poolSize = getPoolSize();

        if (!executor.isShutdown() && (poolSize >= getMaximumPoolSize() || super.getActiveCount() < poolSize))
        {
          queued = queue.offerLastDirect(task);
          if (queued)
          {
            noteQueueAddition(task);
          }
        }
      }
    }

    if (!queued)
    {
      rejectTask(task);

      Submission currentSubmission = submission.get();
      if (currentSubmission != null //
          && currentSubmission.task == task //
          && currentSubmission.reservationPending //
          && !currentSubmission.queued)
      {
        removePendingTask(task);
      }
    }
  }

  /**
   * Submits a task through the executor's normal worker creation and queueing sequence.
   *
   * @param command the task to execute
   * @throws RejectedExecutionException if the task cannot be accepted and the configured rejection handler rejects it
   * @throws NullPointerException if the task is {@code null}
   */
  @Override
  public void execute(Runnable command)
  {
    Submission currentSubmission = new Submission(command);
    addPendingTask();
    Submission previousSubmission = submission.get();
    submission.set(currentSubmission);

    try
    {
      super.execute(command);
    }
    catch (RuntimeException | Error ex)
    {
      if (currentSubmission.reservationPending && !currentSubmission.queued)
      {
        removePendingTask(command);
      }

      throw ex;
    }
    finally
    {
      submission.set(previousSubmission);
    }
  }

  @Override
  protected void beforeExecute(Thread worker, Runnable task)
  {
    synchronized (admissionLock)
    {
      removePendingTask(task);
    }

    incrementRunTasks();
  }

  @Override
  protected void afterExecute(Runnable task, Throwable ex)
  {
    super.afterExecute(task, ex);
  }

  /**
   * Initiates an orderly shutdown. Task admission and the shutdown transition share a lock so a concurrent task is
   * either admitted before shutdown or rejected after shutdown begins.
   */
  @Override
  public void shutdown()
  {
    synchronized (admissionLock)
    {
      super.shutdown();
    }
  }

  /**
   * Attempts to stop active tasks and removes waiting tasks. Queue reservations for the returned waiting tasks are
   * released before this method returns.
   *
   * @return the tasks that were waiting to execute
   */
  @Override
  public List<Runnable> shutdownNow()
  {
    synchronized (admissionLock)
    {
      return super.shutdownNow();
    }
  }

  @Override
  protected void terminated()
  {
    if (deadlockDetectionInterval > NO_DEADLOCK_DETECTION)
    {
      DeadlockDetector.INSTANCE.unregister(this);
    }

    super.terminated();
  }

  /**
   * @since 3.9
   */
  protected void potentialDeadlockDetected()
  {
    int size = super.getQueue().size();
    if (size > 0)
    {
      OM.LOG.warn("Potential deadlock detected in " + this + ". " + size + " tasks are queued."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

  private boolean offer(Runnable task, WorkQueue queue)
  {
    boolean isSubmittingTask = isSubmittingTask(task);

    synchronized (admissionLock)
    {
      if (super.isShutdown())
      {
        return false;
      }

      int poolSize = getPoolSize();
      int activeCount = super.getActiveCount();
      int additionalPendingTasks = isSubmittingTask ? 0 : 1;

      if (poolSize < getMaximumPoolSize() && activeCount + pendingTaskCount + additionalPendingTasks > poolSize)
      {
        // A task has no available worker and the executor should create another one.
        return false;
      }

      boolean offered = queue.offerDirect(task);
      if (offered)
      {
        noteQueueAddition(task);
      }

      return offered;
    }
  }

  private void addPendingTask()
  {
    synchronized (admissionLock)
    {
      ++pendingTaskCount;
    }
  }

  private void noteQueueAddition(Runnable task)
  {
    Submission currentSubmission = submission.get();
    if (currentSubmission != null && currentSubmission.task == task)
    {
      currentSubmission.queued = true;
    }
    else
    {
      addPendingTask();
    }
  }

  private void removePendingTask(Runnable task)
  {
    synchronized (admissionLock)
    {
      if (pendingTaskCount > 0)
      {
        --pendingTaskCount;

        Submission currentSubmission = submission.get();
        if (currentSubmission != null && currentSubmission.task == task)
        {
          currentSubmission.reservationPending = false;
        }
      }
    }
  }

  private boolean isSubmittingTask(Runnable task)
  {
    Submission currentSubmission = submission.get();
    return currentSubmission != null && currentSubmission.task == task;
  }

  private void queueTaskRemoved(Runnable task)
  {
    removePendingTask(task);
  }

  private void queueTasksRemoved(Collection<? extends Runnable> tasks)
  {
    for (Runnable task : tasks)
    {
      removePendingTask(task);
    }
  }

  private void queueTaskTaken(Runnable task)
  {
    removePendingTask(task);
  }

  private void rejectTask(Runnable task)
  {
    RejectedExecutionHandler handler = userHandler;
    if (handler != null)
    {
      handler.rejectedExecution(task, this);
    }
    else
    {
      throw new RejectedExecutionException("Thread pool has rejected the task " + task); //$NON-NLS-1$
    }
  }

  private static ThreadFactory createThreadFactory(String threadGroupName)
  {
    if (threadGroupName == null)
    {
      threadGroupName = DEFAULT_THREAD_GROUP_NAME;
    }

    ThreadGroup threadGroup = new ThreadGroup(threadGroupName);

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

  /**
   * @author Eike Stepper
   */
  private interface WorkQueue extends BlockingQueue<Runnable>
  {
    public void setThreadPool(ThreadPool threadPool);

    public boolean offerDirect(Runnable task);

    public boolean offerLastDirect(Runnable task);

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
    public boolean offerDirect(Runnable task)
    {
      // Call the super method because the method in this class is overridden.
      return super.offer(task);
    }

    @Override
    public boolean offerLastDirect(Runnable task)
    {
      return super.offer(task);
    }

    @Override
    public void put(Runnable task) throws InterruptedException
    {
      synchronized (threadPool.admissionLock)
      {
        super.put(task);
        threadPool.noteQueueAddition(task);
      }
    }

    @Override
    public boolean offer(Runnable task, long timeout, TimeUnit unit) throws InterruptedException
    {
      synchronized (threadPool.admissionLock)
      {
        boolean offered = super.offer(task, timeout, unit);
        if (offered)
        {
          threadPool.noteQueueAddition(task);
        }

        return offered;
      }
    }

    @Override
    public boolean remove(Object task)
    {
      boolean removed = super.remove(task);
      if (removed)
      {
        threadPool.queueTaskRemoved((Runnable)task);
      }

      return removed;
    }

    @Override
    public Runnable poll()
    {
      Runnable task = super.poll();
      if (task != null)
      {
        threadPool.queueTaskRemoved(task);
      }

      return task;
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException
    {
      return super.poll(timeout, unit);
    }

    @Override
    public Runnable take() throws InterruptedException
    {
      return super.take();
    }

    @Override
    public int drainTo(Collection<? super Runnable> collection)
    {
      if (collection == this)
      {
        throw new IllegalArgumentException();
      }

      List<Runnable> removed = new ArrayList<>();
      int count = super.drainTo(removed);
      threadPool.queueTasksRemoved(removed);
      collection.addAll(removed);
      return count;
    }

    @Override
    public int drainTo(Collection<? super Runnable> collection, int maxElements)
    {
      if (collection == this)
      {
        throw new IllegalArgumentException();
      }

      List<Runnable> removed = new ArrayList<>();
      int count = super.drainTo(removed, maxElements);
      threadPool.queueTasksRemoved(removed);
      collection.addAll(removed);
      return count;
    }

    @Override
    public void clear()
    {
      drainTo(new ArrayList<>());
    }

    @Override
    public Iterator<Runnable> iterator()
    {
      Iterator<Runnable> iterator = super.iterator();

      return new Iterator<>()
      {
        private Runnable current;

        @Override
        public boolean hasNext()
        {
          return iterator.hasNext();
        }

        @Override
        public Runnable next()
        {
          current = iterator.next();
          return current;
        }

        @Override
        public void remove()
        {
          iterator.remove();
          threadPool.queueTaskRemoved(current);
          current = null;
        }
      };
    }

    @Override
    public boolean offer(Runnable task)
    {
      return threadPool.offer(task, this);
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
    public boolean offerDirect(Runnable task)
    {
      return delegate.offer(task);
    }

    @Override
    public boolean offerLastDirect(Runnable task)
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
    public boolean remove(Object task)
    {
      boolean removed = delegate.remove(task);
      if (removed)
      {
        threadPool.queueTaskRemoved((Runnable)task);
      }

      return removed;
    }

    @Override
    public boolean offer(Runnable task)
    {
      return threadPool.offer(task, this);
    }

    @Override
    public boolean offer(Runnable taske, long timeout, TimeUnit unit) throws InterruptedException
    {
      synchronized (threadPool.admissionLock)
      {
        boolean offered = delegate.offer(taske, timeout, unit);
        if (offered)
        {
          threadPool.noteQueueAddition(taske);
        }

        return offered;
      }
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
      Runnable task = delegate.poll();
      if (task != null)
      {
        threadPool.queueTaskRemoved(task);
      }

      return task;
    }

    @Override
    public Iterator<Runnable> iterator()
    {
      Iterator<Runnable> iterator = delegate.iterator();

      return new Iterator<>()
      {
        private Runnable current;

        @Override
        public boolean hasNext()
        {
          return iterator.hasNext();
        }

        @Override
        public Runnable next()
        {
          current = iterator.next();
          return current;
        }

        @Override
        public void remove()
        {
          iterator.remove();
          threadPool.queueTaskRemoved(current);
          current = null;
        }
      };
    }

    @Override
    public Runnable peek()
    {
      return delegate.peek();
    }

    @Override
    public void put(Runnable task) throws InterruptedException
    {
      synchronized (threadPool.admissionLock)
      {
        delegate.put(task);
        threadPool.noteQueueAddition(task);
      }
    }

    @Override
    public int remainingCapacity()
    {
      return delegate.remainingCapacity();
    }

    @Override
    public int drainTo(Collection<? super Runnable> c)
    {
      if (c == this)
      {
        throw new IllegalArgumentException();
      }

      List<Runnable> removed = new ArrayList<>();
      int count = delegate.drainTo(removed);
      threadPool.queueTasksRemoved(removed);
      c.addAll(removed);
      return count;
    }

    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements)
    {
      if (c == this)
      {
        throw new IllegalArgumentException();
      }

      List<Runnable> removed = new ArrayList<>();
      int count = delegate.drainTo(removed, maxElements);
      threadPool.queueTasksRemoved(removed);
      c.addAll(removed);
      return count;
    }

    @Override
    public void clear()
    {
      drainTo(new ArrayList<>());
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

    private final CopyOnWriteArrayList<ThreadPool> pools = new CopyOnWriteArrayList<>();

    private DeadlockDetector()
    {
      setDaemon(true);
      activate();
    }

    public void register(ThreadPool pool)
    {
      pools.addIfAbsent(pool);
    }

    private void unregister(ThreadPool pool)
    {
      pools.remove(pool);
    }

    @Override
    protected String getThreadName()
    {
      return DeadlockDetector.class.getSimpleName();
    }

    @Override
    protected void work(WorkContext context) throws Exception
    {
      for (ThreadPool pool : pools)
      {
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
        pool.potentialDeadlockReported = false;
      }
      else
      {
        if (!pool.potentialDeadlockReported //
            && pool.getPoolSize() == pool.getMaximumPoolSize() //
            && pool.getActiveCount() == pool.getPoolSize())
        {
          pool.potentialDeadlockReported = true;
          pool.potentialDeadlockDetected();
        }
      }
    }
  }

  /**
   * Exposes the executor queue while accounting for removals made through its public accessor.
   *
   * @author Eike Stepper
   */
  private static final class QueueView extends AbstractQueue<Runnable> implements BlockingQueue<Runnable>
  {
    private final ThreadPool threadPool;

    private final BlockingQueue<Runnable> delegate;

    public QueueView(ThreadPool threadPool, BlockingQueue<Runnable> delegate)
    {
      this.threadPool = threadPool;
      this.delegate = delegate;
    }

    @Override
    public boolean offer(Runnable task)
    {
      return delegate.offer(task);
    }

    @Override
    public void put(Runnable task) throws InterruptedException
    {
      delegate.put(task);
    }

    @Override
    public boolean offer(Runnable task, long timeout, TimeUnit unit) throws InterruptedException
    {
      return delegate.offer(task, timeout, unit);
    }

    @Override
    public Runnable take() throws InterruptedException
    {
      Runnable task = delegate.take();
      threadPool.queueTaskTaken(task);
      return task;
    }

    @Override
    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException
    {
      Runnable task = delegate.poll(timeout, unit);
      if (task != null)
      {
        threadPool.queueTaskTaken(task);
      }

      return task;
    }

    @Override
    public Runnable poll()
    {
      return delegate.poll();
    }

    @Override
    public Runnable peek()
    {
      return delegate.peek();
    }

    @Override
    public int size()
    {
      return delegate.size();
    }

    @Override
    public int remainingCapacity()
    {
      return delegate.remainingCapacity();
    }

    @Override
    public Iterator<Runnable> iterator()
    {
      return delegate.iterator();
    }

    @Override
    public boolean remove(Object task)
    {
      return delegate.remove(task);
    }

    @Override
    public int drainTo(Collection<? super Runnable> collection)
    {
      if (collection == this)
      {
        throw new IllegalArgumentException();
      }

      return delegate.drainTo(collection);
    }

    @Override
    public int drainTo(Collection<? super Runnable> collection, int maxElements)
    {
      if (collection == this)
      {
        throw new IllegalArgumentException();
      }

      return delegate.drainTo(collection, maxElements);
    }

    @Override
    public void clear()
    {
      delegate.clear();
    }
  }

  /**
   * Tracks the reservation associated with one call to {@link ThreadPool#execute(Runnable)}.
   *
   * @author Eike Stepper
   */
  private static final class Submission
  {
    private final Runnable task;

    private boolean reservationPending = true;

    private boolean queued;

    public Submission(Runnable task)
    {
      this.task = task;
    }
  }
}
