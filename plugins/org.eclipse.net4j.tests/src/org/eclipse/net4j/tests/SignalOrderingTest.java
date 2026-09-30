/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.concurrent.OrderedExecution;
import org.eclipse.net4j.util.concurrent.SerializingExecutor;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class SignalOrderingTest extends AbstractConfigTest
{
  private static final long TIMEOUT_SECONDS = 10;

  public void testEqualKeysSerializeAndUseEquality() throws Exception
  {
    startTransport();
    OrderingProtocol protocol = new OrderingProtocol();
    protocol.open(getConnector());

    try
    {
      List<String> events = Collections.synchronizedList(new ArrayList<>());
      CountDownLatch firstStarted = new CountDownLatch(1);
      CountDownLatch releaseFirst = new CountDownLatch(1);
      CountDownLatch finished = new CountDownLatch(3);
      Executor firstLane = protocol.executor(new OrderedReactor(protocol, new String("shared")));
      Executor equalLane = protocol.executor(new OtherReactor(protocol, new String("shared")));

      assertSame(firstLane, equalLane);

      firstLane.execute(() -> run(events, "A1", firstStarted, releaseFirst, finished));
      await(firstStarted, "first task did not start");
      equalLane.execute(() -> run(events, "A2", null, null, finished));
      firstLane.execute(() -> run(events, "A3", null, null, finished));
      assertFalse("later task started before the first task was released", finished.await(100, TimeUnit.MILLISECONDS));
      releaseFirst.countDown();
      await(finished, "ordered tasks did not finish");

      assertEquals("[A1 start, A1 end, A2 start, A2 end, A3 start, A3 end]", events.toString());
      protocol.close();
      assertFalse("protocol-owned lane remained active after protocol close", ((SerializingExecutor)firstLane).isActive());
    }
    finally
    {
      protocol.close();
    }
  }

  public void testDifferentAndUnorderedLanesCanRunConcurrently() throws Exception
  {
    startTransport();
    OrderingProtocol protocol = new OrderingProtocol();
    protocol.open(getConnector());

    try
    {
      CountDownLatch orderedStarted = new CountDownLatch(1);
      CountDownLatch releaseOrdered = new CountDownLatch(1);
      CountDownLatch otherLaneStarted = new CountDownLatch(1);
      CountDownLatch unorderedStarted = new CountDownLatch(1);
      Executor laneA = protocol.executor(new OrderedReactor(protocol, "A"));
      Executor laneB = protocol.executor(new OrderedReactor(protocol, "B"));
      Executor unordered = protocol.executor(new PlainReactor(protocol));

      laneA.execute(() -> {
        orderedStarted.countDown();
        await(releaseOrdered, "ordered task was not released");
      });
      await(orderedStarted, "ordered task did not start");
      laneB.execute(otherLaneStarted::countDown);
      unordered.execute(unorderedStarted::countDown);

      await(otherLaneStarted, "different order key was blocked by the first lane");
      await(unorderedStarted, "unordered signal was blocked by an ordered lane");
      releaseOrdered.countDown();
    }
    finally
    {
      protocol.close();
    }
  }

  public void testOrderedExecutionDefaultAndProtocolOverride() throws Exception
  {
    startTransport();
    OrderingProtocol protocol = new OrderingProtocol();
    protocol.open(getConnector());

    try
    {
      Executor defaultLane = protocol.executor(new DefaultOrderedReactor(protocol));
      assertSame(defaultLane, protocol.executor(new DefaultOrderedReactor(protocol)));

      Executor overrideLane = protocol.executor(new OverriddenReactor(protocol));
      assertSame(overrideLane, protocol.executor(new OverriddenReactor(protocol)));
    }
    finally
    {
      protocol.close();
    }
  }

  private static void run(List<String> events, String name, CountDownLatch started, CountDownLatch release, CountDownLatch finished)
  {
    events.add(name + " start");
    if (started != null)
    {
      started.countDown();
    }

    if (release != null)
    {
      await(release, "first task was not released");
    }

    events.add(name + " end");
    finished.countDown();
  }

  private static void await(CountDownLatch latch, String failure)
  {
    try
    {
      assertTrue(failure, latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw new AssertionError(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class OrderingProtocol extends TestSignalProtocol
  {
    public Executor executor(SignalReactor signal)
    {
      return getSignalReactorExecutor(signal);
    }

    @Override
    protected Object getSignalReactorOrderKey(SignalReactor signal)
    {
      if (signal instanceof OverriddenReactor)
      {
        return OverriddenReactor.class;
      }

      return super.getSignalReactorOrderKey(signal);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class OrderedReactor extends Indication implements OrderedExecution
  {
    private final Object key;

    public OrderedReactor(SignalProtocol<?> protocol, Object key)
    {
      super(protocol, (short)100);
      this.key = key;
    }

    @Override
    public Object getExecutionOrderKey()
    {
      return key;
    }

    @Override
    protected void indicating(ExtendedDataInputStream in) throws Exception
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class OtherReactor extends OrderedReactor
  {
    public OtherReactor(SignalProtocol<?> protocol, Object key)
    {
      super(protocol, key);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class DefaultOrderedReactor extends OrderedReactor
  {
    public DefaultOrderedReactor(SignalProtocol<?> protocol)
    {
      super(protocol, SignalOrderingTest.class);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class OverriddenReactor extends Indication
  {
    public OverriddenReactor(SignalProtocol<?> protocol)
    {
      super(protocol, (short)101);
    }

    @Override
    protected void indicating(ExtendedDataInputStream in) throws Exception
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class PlainReactor extends Indication
  {
    public PlainReactor(SignalProtocol<?> protocol)
    {
      super(protocol, (short)102);
    }

    @Override
    protected void indicating(ExtendedDataInputStream in) throws Exception
    {
    }
  }
}
