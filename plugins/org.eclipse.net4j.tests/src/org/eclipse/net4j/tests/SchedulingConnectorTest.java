/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.internal.net4j.RoundRobinScheduler;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.SchedulingConnector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests channel fairness and current-buffer retention in the shared scheduling implementation.
 *
 * @author Eike Stepper
 */
public class SchedulingConnectorTest extends AbstractOMTest
{
  public void testEmptyScheduler()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    assertTrue(scheduler.isEmpty());
    assertNull(scheduler.select());
  }

  public void testDuplicateOffersAndUnequalQueueFairness()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel a = new TestChannel("A", 3);
    TestChannel b = new TestChannel("B", 2);
    TestChannel c = new TestChannel("C", 3);
    scheduler.offer(a.channel);
    scheduler.offer(a.channel);
    scheduler.offer(b.channel);
    scheduler.offer(c.channel);
    scheduler.offer(c.channel);

    assertSequence(scheduler, a, b, c, a, b, c, a, c);
    assertTrue(scheduler.isEmpty());
  }

  public void testPartialBufferKeepsCurrentChannel()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel a = new TestChannel("A", 2);
    TestChannel b = new TestChannel("B", 1);
    scheduler.offer(a.channel);
    scheduler.offer(b.channel);

    assertSame(a.channel, scheduler.select());
    scheduler.complete(a.channel, false);
    assertSame(a.channel, scheduler.select());
    a.removeBuffer();
    scheduler.complete(a.channel, true);
    assertSame(b.channel, scheduler.select());
    b.removeBuffer();
    scheduler.complete(b.channel, true);
    assertSame(a.channel, scheduler.select());
  }

  public void testInactiveChannelRemovalAndClear()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel a = new TestChannel("A", 1);
    scheduler.offer(a.channel);
    a.active = false;
    assertNull(scheduler.select());
    assertTrue(scheduler.isEmpty());

    a.active = true;
    scheduler.offer(a.channel);
    assertSame(a.channel, scheduler.select());
    scheduler.clear();
    assertTrue(scheduler.isEmpty());
    assertNull(scheduler.select());
  }

  public void testFirstOfferRequestsScheduling()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel channel = new TestChannel("A", 1);
    assertTrue(scheduler.offer(channel.channel));
    assertSame(channel.channel, scheduler.select());
  }

  public void testConcurrentDuplicateOffers()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel channel = new TestChannel("A", 1);
    CountDownLatch ready = new CountDownLatch(8);
    CountDownLatch start = new CountDownLatch(1);
    AtomicInteger idleTransitions = new AtomicInteger();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread[] threads = new Thread[8];
    for (int i = 0; i < threads.length; i++)
    {
      threads[i] = new Thread(() -> {
        try
        {
          ready.countDown();
          assertTrue(awaitLatch(start));
          if (scheduler.offer(channel.channel))
          {
            idleTransitions.incrementAndGet();
          }
        }
        catch (Throwable ex)
        {
          failure.compareAndSet(null, ex);
        }
      }, "scheduler-offer-" + i); //$NON-NLS-1$
      threads[i].setDaemon(true);
      threads[i].start();
    }

    assertTrue(awaitLatch(ready));
    start.countDown();
    for (Thread thread : threads)
    {
      join(thread);
    }

    assertNull(failure.get());
    assertEquals(1, idleTransitions.get());
    assertSame(channel.channel, scheduler.select());
  }

  public void testConcurrentEnqueueDuringCompletion()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel channel = new TestChannel("A", 1);
    scheduler.offer(channel.channel);
    assertSame(channel.channel, scheduler.select());
    channel.removeBuffer();

    CountDownLatch start = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread producer = worker("scheduler-producer", start, failure, () -> { //$NON-NLS-1$
      channel.addBuffer();
      scheduler.offer(channel.channel);
    });
    producer.start();
    start.countDown();
    scheduler.complete(channel.channel, true);
    join(producer);

    assertNull(failure.get());
    assertSame(channel.channel, scheduler.select());
    assertEquals(1, channel.queue.size());
  }

  public void testEnqueueRacingWithEmptySelectionIsNotLost()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    ControlledQueue queue = new ControlledQueue();
    TestChannel channel = new TestChannel("A", 1, queue);
    scheduler.offer(channel.channel);
    channel.removeBuffer();

    queue.arm();
    CountDownLatch producerAttempting = new CountDownLatch(1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicReference<InternalChannel> selected = new AtomicReference<>();
    Thread selector = worker("scheduler-selector", new CountDownLatch(0), failure, () -> selected.set(scheduler.select())); //$NON-NLS-1$
    selector.start();
    assertTrue(awaitLatch(queue.emptyObserved));

    Thread producer = new Thread(() -> {
      producerAttempting.countDown();
      try
      {
        synchronized (queue)
        {
          queue.add((IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, new BufferHandler()));
        }

        scheduler.offer(channel.channel);
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, "scheduler-queue-producer"); //$NON-NLS-1$
    producer.setDaemon(true);
    producer.start();
    assertTrue(awaitLatch(producerAttempting));
    queue.continueSelection.countDown();
    join(producer);
    join(selector);

    assertNull(failure.get());
    assertEquals(1, channel.queue.size());
    assertSame(channel.channel, scheduler.select());
  }

  public void testStaleAndNoncurrentCompletionDoNotReschedule()
  {
    RoundRobinScheduler scheduler = new RoundRobinScheduler();
    TestChannel a = new TestChannel("A", 1);
    TestChannel b = new TestChannel("B", 1);
    scheduler.offer(a.channel);
    scheduler.offer(b.channel);
    assertSame(a.channel, scheduler.select());
    assertFalse(scheduler.complete(b.channel, true));
    assertSame(a.channel, scheduler.select());

    scheduler.clear();
    assertFalse(scheduler.complete(a.channel, true));
    assertTrue(scheduler.isEmpty());

    scheduler.offer(a.channel);
    assertSame(a.channel, scheduler.select());
    scheduler.remove(a.channel);
    assertFalse(scheduler.complete(a.channel, true));
    assertTrue(scheduler.isEmpty());
  }

  public void testInFlightOfferCannotResurrectWorkAfterDeactivation() throws Exception
  {
    BlockingScheduler scheduler = new BlockingScheduler();
    TestSchedulingConnector connector = new TestSchedulingConnector();
    connector.setScheduler(scheduler);
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));
    LifecycleUtil.activate(connector);
    connector.setState(ConnectorState.CONNECTED);
    TestChannel channel = new TestChannel("A", 1);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread producer = worker("connector-offer", new CountDownLatch(0), failure, () -> connector.offer(channel.channel)); //$NON-NLS-1$
    producer.start();
    assertTrue(awaitLatch(scheduler.offerEntered));

    Thread deactivation = worker("connector-deactivation", new CountDownLatch(0), failure, () -> LifecycleUtil.deactivate(connector)); //$NON-NLS-1$
    deactivation.start();
    boolean deactivationBlocked;
    try
    {
      deactivationBlocked = awaitBlocked(deactivation);
    }
    finally
    {
      scheduler.continueOffer.countDown();
    }

    join(producer);
    join(deactivation);

    assertNull(failure.get());
    assertTrue("Deactivation did not wait for the in-flight offer", deactivationBlocked); //$NON-NLS-1$
    assertTrue(connector.getScheduler().isEmpty());
    assertNull(connector.select());
    assertEquals(0, connector.getNotificationCount());

    connector.offer(channel.channel);
    assertTrue(connector.getScheduler().isEmpty());
    assertEquals(0, connector.getNotificationCount());
  }

  public void testPendingNotificationIsInvalidatedByDeactivation() throws Exception
  {
    TestSchedulingConnector connector = new TestSchedulingConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));
    LifecycleUtil.activate(connector);
    connector.setState(ConnectorState.CONNECTED);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Object notificationLock = getNotificationLock(connector);
    Thread notification;
    Thread deactivation;
    try
    {
      TestChannel channel = new TestChannel("A", 1);
      synchronized (notificationLock)
      {
        notification = worker("connector-pending-notification", new CountDownLatch(0), failure, () -> connector.offer(channel.channel)); //$NON-NLS-1$
        notification.start();
        assertTrue("Notification did not reach the notification lock", awaitBlocked(notification)); //$NON-NLS-1$
        long revision = getNotificationRevision(connector);

        deactivation = worker("connector-notification-deactivation", new CountDownLatch(0), failure, connector::deactivate); //$NON-NLS-1$
        deactivation.start();
        if (!awaitRevisionChange(connector, revision))
        {
          throw new AssertionError("revision=" + revision + ", failure=" + failure.get() + ", state=" + deactivation.getState()); //$NON-NLS-1$
        }
      }

      join(notification);
      join(deactivation);

      assertNull(failure.get());
      assertEquals("A notification calculated before deactivation must be discarded", 0, connector.getNotificationCount()); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testAsyncSenderContinuesAfterCompletionWithAnotherChannelPending() throws Exception
  {
    TestSchedulingConnector connector = new TestSchedulingConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));
    LifecycleUtil.activate(connector);
    try
    {
      TestChannel a = new TestChannel("A", 1);
      TestChannel b = new TestChannel("B", 1);
      connector.offer(a.channel);
      connector.offer(b.channel);
      assertEquals(1, connector.getNotificationCount());

      InternalChannel first = connector.select();
      assertSame(a.channel, first);
      a.removeBuffer();
      assertTrue(connector.complete(first));

      // Completion requests one asynchronous continuation because channel B still has work.
      assertEquals(2, connector.getNotificationCount());
      InternalChannel second = connector.select();
      assertSame(b.channel, second);
      b.removeBuffer();
      assertFalse(connector.complete(second));
      assertNull(connector.select());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testClosingSelectedChannelContinuesWithNextChannel() throws Exception
  {
    TestSchedulingConnector connector = new TestSchedulingConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));
    LifecycleUtil.activate(connector);
    connector.setState(ConnectorState.CONNECTED);
    try
    {
      TestChannel a = new TestChannel("A", 1);
      TestChannel b = new TestChannel("B", 1);
      connector.offer(a.channel);
      connector.offer(b.channel);
      assertEquals(1, connector.getNotificationCount());

      InternalChannel first = connector.select();
      assertSame(a.channel, first);
      connector.closeChannel(first);

      assertEquals(2, connector.getNotificationCount());
      assertSame(b.channel, connector.select());
      assertFalse("A stale completion must be ignored", connector.complete(first)); //$NON-NLS-1$
      assertEquals(2, connector.getNotificationCount());
      assertSame(b.channel, connector.select());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }
  }

  public void testConnectorControlChannelLifecycleAndScheduling() throws Exception
  {
    TestSchedulingConnector connector = new TestSchedulingConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));
    LifecycleUtil.activate(connector);
    connector.setState(ConnectorState.CONNECTED);
    try
    {
      assertNotNull(connector.getControlChannel());
      assertEquals(0, connector.getControlChannel().getID());

      IBuffer buffer = connector.provideBuffer();
      buffer.startPutting((short)0).put((byte)1);
      connector.getControlChannel().sendBuffer(buffer);

      InternalChannel scheduled = connector.select();
      assertSame(connector.getControlChannel(), scheduled);
      assertEquals(1, connector.getNotificationCount());
      synchronized (scheduled.getSendQueue())
      {
        assertSame(buffer, scheduled.getSendQueue().poll());
      }

      buffer.release();
      connector.complete(scheduled);
      assertTrue(connector.getScheduler().isEmpty());
    }
    finally
    {
      LifecycleUtil.deactivate(connector);
    }

    assertNull(connector.getControlChannel());
    assertTrue(connector.getScheduler().isEmpty());
  }

  public void testDeactivationWhileTransmissionIsPending() throws Exception
  {
    TestSchedulingConnector connector = new TestSchedulingConnector();
    connector.getConfig().setBufferProvider(Net4jUtil.createBufferFactory((short)64));
    LifecycleUtil.activate(connector);
    connector.setState(ConnectorState.CONNECTED);
    InternalChannel control = (InternalChannel)connector.getControlChannel();
    Queue<IBuffer> queue = control.getSendQueue();
    IBuffer buffer = connector.provideBuffer();
    buffer.startPutting((short)0).put((byte)1);
    control.sendBuffer(buffer);
    assertSame(control, connector.select());
    assertEquals(1, queue.size());

    LifecycleUtil.deactivate(connector);

    assertTrue(queue.isEmpty());
    assertTrue(connector.getScheduler().isEmpty());
    assertNull(connector.getControlChannel());
  }

  private void assertSequence(RoundRobinScheduler scheduler, TestChannel... channels)
  {
    List<String> actual = new ArrayList<>();
    for (int i = 0; i < channels.length; i++)
    {
      InternalChannel channel = scheduler.select();
      TestChannel expected = channels[i];
      assertSame(expected.channel, channel);
      actual.add(expected.name + expected.removeBuffer());
      scheduler.complete(channel, true);
    }

    assertEquals("[A1, B1, C1, A2, B2, C2, A3, C3]", actual.toString());
  }

  private static boolean awaitLatch(CountDownLatch latch)
  {
    try
    {
      return latch.await(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }

  private static Thread worker(String name, CountDownLatch start, AtomicReference<Throwable> failure, Runnable runnable)
  {
    Thread thread = new Thread(() -> {
      try
      {
        assertTrue(awaitLatch(start));
        runnable.run();
      }
      catch (Throwable ex)
      {
        failure.compareAndSet(null, ex);
      }
    }, name);
    thread.setDaemon(true);
    return thread;
  }

  private static void join(Thread thread)
  {
    try
    {
      thread.join(TimeUnit.SECONDS.toMillis(5));
      if (thread.isAlive())
      {
        thread.interrupt();
        throw new AssertionError("Worker did not terminate: " + thread.getName()); //$NON-NLS-1$
      }
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }

  private static boolean awaitBlocked(Thread thread)
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (thread.isAlive() && thread.getState() != Thread.State.BLOCKED && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    return thread.getState() == Thread.State.BLOCKED;
  }

  private static Object getNotificationLock(SchedulingConnector connector) throws Exception
  {
    Field field = SchedulingConnector.class.getDeclaredField("schedulingNotificationLock"); //$NON-NLS-1$
    field.setAccessible(true);
    return field.get(connector);
  }

  private static long getNotificationRevision(SchedulingConnector connector) throws Exception
  {
    Field field = SchedulingConnector.class.getDeclaredField("schedulingNotificationRevision"); //$NON-NLS-1$
    field.setAccessible(true);
    return field.getLong(connector);
  }

  private static boolean awaitRevisionChange(SchedulingConnector connector, long revision) throws Exception
  {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (getNotificationRevision(connector) == revision && System.nanoTime() < deadline)
    {
      Thread.yield();
    }

    return getNotificationRevision(connector) != revision;
  }

  /**
   * Supplies a queue and lifecycle state through the internal channel contract.
   *
   * @author Eike Stepper
   */
  private static final class TestChannel
  {
    private final String name;

    private final Queue<IBuffer> queue;

    private final InternalChannel channel;

    private int next;

    private boolean active = true;

    public TestChannel(String name, int bufferCount)
    {
      this(name, bufferCount, new LinkedList<>());
    }

    public TestChannel(String name, int bufferCount, Queue<IBuffer> queue)
    {
      this.name = name;
      this.queue = queue;
      for (int i = 0; i < bufferCount; i++)
      {
        queue.add((IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, new BufferHandler()));
      }

      InvocationHandler handler = (proxy, method, args) -> {
        switch (method.getName())
        {
        case "isActive": //$NON-NLS-1$
          return active;
        case "getSendQueue": //$NON-NLS-1$
          return queue;
        case "toString": //$NON-NLS-1$
          return name;
        default:
          return defaultValue(method.getReturnType());
        }
      };
      channel = (InternalChannel)Proxy.newProxyInstance(InternalChannel.class.getClassLoader(), new Class<?>[] { InternalChannel.class }, handler);
    }

    public int removeBuffer()
    {
      synchronized (queue)
      {
        queue.poll();
      }

      return ++next;
    }

    public void addBuffer()
    {
      synchronized (queue)
      {
        queue.add((IBuffer)Proxy.newProxyInstance(IBuffer.class.getClassLoader(), new Class<?>[] { IBuffer.class }, new BufferHandler()));
      }
    }
  }

  /**
   * Pauses the first empty observation while the selector holds the queue monitor.
   *
   * @author Eike Stepper
   */
  private static final class ControlledQueue extends LinkedList<IBuffer>
  {
    private static final long serialVersionUID = 1L;

    private final CountDownLatch emptyObserved = new CountDownLatch(1);

    private final CountDownLatch continueSelection = new CountDownLatch(1);

    private volatile boolean armed;

    public void arm()
    {
      armed = true;
    }

    @Override
    public boolean isEmpty()
    {
      boolean empty = super.isEmpty();
      if (empty && armed)
      {
        armed = false;
        emptyObserved.countDown();
        try
        {
          if (!continueSelection.await(5, TimeUnit.SECONDS))
          {
            throw new AssertionError("Timed out releasing the empty queue observation"); //$NON-NLS-1$
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new AssertionError(ex);
        }
      }

      return empty;
    }
  }

  /**
   * Pauses the first offer before it reaches the round-robin set.
   *
   * @author Eike Stepper
   */
  private static final class BlockingScheduler extends RoundRobinScheduler
  {
    private final CountDownLatch offerEntered = new CountDownLatch(1);

    private final CountDownLatch continueOffer = new CountDownLatch(1);

    @Override
    public boolean offer(InternalChannel channel)
    {
      offerEntered.countDown();
      assertTrue(awaitLatch(continueOffer));
      return super.offer(channel);
    }
  }

  /**
   * Supplies inert buffer instances for queue-order tests.
   *
   * @author Eike Stepper
   */
  private static final class BufferHandler implements InvocationHandler
  {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
      return defaultValue(method.getReturnType());
    }
  }

  private static Object defaultValue(Class<?> type)
  {
    if (type == boolean.class)
    {
      return false;
    }

    if (type == short.class)
    {
      return (short)0;
    }

    if (type == int.class)
    {
      return 0;
    }

    if (type == long.class)
    {
      return 0L;
    }

    return null;
  }

  /**
   * Exposes the common connector hooks for a local lifecycle and scheduling check.
   *
   * @author Eike Stepper
   */
  private static final class TestSchedulingConnector extends SchedulingConnector
  {
    private int notificationCount;

    @Override
    protected void onSendingRequired()
    {
      ++notificationCount;
    }

    @Override
    protected INegotiationContext createNegotiationContext()
    {
      return new NegotiationContext()
      {
        @Override
        public ByteBuffer getBuffer()
        {
          return null;
        }

        @Override
        public void transmitBuffer(ByteBuffer buffer)
        {
        }

        @Override
        public void setUserID(String userID)
        {
        }
      };
    }

    public int getNotificationCount()
    {
      return notificationCount;
    }

    public InternalChannel select()
    {
      return selectScheduledChannel();
    }

    public void offer(InternalChannel channel)
    {
      multiplexChannel(channel);
    }

    public boolean complete(InternalChannel channel)
    {
      return completeScheduledChannel(channel, true);
    }
  }
}
