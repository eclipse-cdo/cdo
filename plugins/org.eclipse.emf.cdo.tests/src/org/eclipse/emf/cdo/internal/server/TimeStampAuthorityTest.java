/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.Monitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

/**
 * Tests the timestamp advancement and interruption behavior of {@link TimeStampAuthority}.
 *
 * @author Eike Stepper
 */
public class TimeStampAuthorityTest extends TestCase
{
  /**
   * Verifies that equal provider values do not satisfy branch-base advancement.
   */
  public void testGetMaxBaseTimeForNewBranchWaitsForAdvancement()
  {
    ScriptedTimeProvider timeProvider = new ScriptedTimeProvider(100, 100, 100, 101);
    TimeStampAuthority authority = createAuthority(timeProvider);

    assertEquals(100L, authority.getMaxBaseTimeForNewBranch());
  }

  /**
   * Verifies that provider values below the sampled value do not satisfy branch-base advancement.
   */
  public void testGetMaxBaseTimeForNewBranchIgnoresRollback()
  {
    ScriptedTimeProvider timeProvider = new ScriptedTimeProvider(100, 99, 98, 101);
    TimeStampAuthority authority = createAuthority(timeProvider);

    assertEquals(100L, authority.getMaxBaseTimeForNewBranch());
  }

  /**
   * Verifies that a generated commit timestamp is strictly greater than the previously issued timestamp.
   */
  public void testStartCommitIgnoresRollback()
  {
    ScriptedTimeProvider timeProvider = new ScriptedTimeProvider(100, 100, 99, 100, 101);
    TimeStampAuthority authority = createAuthority(timeProvider);
    Monitor monitor = new Monitor();

    long[] first = authority.startCommit(CDOBranchPoint.UNSPECIFIED_DATE, monitor);
    long[] second = authority.startCommit(CDOBranchPoint.UNSPECIFIED_DATE, monitor);
    authority.failCommit(second[0]);
    authority.failCommit(first[0]);

    assertEquals(100L, first[0]);
    assertEquals(101L, second[0]);
  }

  /**
   * Verifies that interrupting branch-base advancement aborts and preserves the interrupt status.
   */
  public void testGetMaxBaseTimeForNewBranchInterrupts() throws Exception
  {
    ScriptedTimeProvider timeProvider = new ScriptedTimeProvider(100, 100);
    TimeStampAuthority authority = createAuthority(timeProvider);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicBoolean interrupted = new AtomicBoolean();
    Thread thread = new Thread(() -> runGetMaxBaseTime(authority, failure, interrupted));

    thread.start();
    assertTrue(timeProvider.awaitWaiting());
    thread.interrupt();
    thread.join(5000L);

    assertFalse(thread.isAlive());
    assertInterrupted(failure.get(), interrupted.get());
  }

  /**
   * Verifies that interrupting commit timestamp advancement aborts and preserves the interrupt status.
   */
  public void testStartCommitInterrupts() throws Exception
  {
    ScriptedTimeProvider timeProvider = new ScriptedTimeProvider(100, 100);
    TimeStampAuthority authority = createAuthority(timeProvider);
    Monitor monitor = new Monitor();
    long[] first = authority.startCommit(CDOBranchPoint.UNSPECIFIED_DATE, monitor);
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicBoolean interrupted = new AtomicBoolean();
    Thread thread = new Thread(() -> runStartCommit(authority, failure, interrupted));

    try
    {
      thread.start();
      assertTrue(timeProvider.awaitWaiting());
      thread.interrupt();
      thread.join(5000L);

      assertFalse(thread.isAlive());
      assertInterrupted(failure.get(), interrupted.get());
    }
    finally
    {
      authority.failCommit(first[0]);
    }
  }

  private static TimeStampAuthority createAuthority(CDOTimeProvider timeProvider)
  {
    InternalStore store = (InternalStore)Proxy.newProxyInstance(InternalStore.class.getClassLoader(), new Class<?>[] { InternalStore.class },
        (proxy, method, args) -> null);
    InvocationHandler handler = (proxy, method, args) ->
    {
      if (method.getName().equals("getTimeStamp"))
      {
        return timeProvider.getTimeStamp();
      }

      if (method.getName().equals("getStore"))
      {
        return store;
      }

      return null;
    };
    InternalRepository repository = (InternalRepository)Proxy.newProxyInstance(InternalRepository.class.getClassLoader(),
        new Class<?>[] { InternalRepository.class }, handler);
    return new TimeStampAuthority(repository);
  }

  private static void runGetMaxBaseTime(TimeStampAuthority authority, AtomicReference<Throwable> failure, AtomicBoolean interrupted)
  {
    try
    {
      authority.getMaxBaseTimeForNewBranch();
    }
    catch (Throwable ex)
    {
      failure.set(ex);
      interrupted.set(Thread.currentThread().isInterrupted());
    }
  }

  private static void runStartCommit(TimeStampAuthority authority, AtomicReference<Throwable> failure, AtomicBoolean interrupted)
  {
    try
    {
      authority.startCommit(CDOBranchPoint.UNSPECIFIED_DATE, new Monitor());
    }
    catch (Throwable ex)
    {
      failure.set(ex);
      interrupted.set(Thread.currentThread().isInterrupted());
    }
  }

  private static void assertInterrupted(Throwable failure, boolean interrupted)
  {
    assertTrue(failure instanceof WrappedException);
    assertTrue(failure.getCause() instanceof InterruptedException);
    assertTrue(interrupted);
  }

  /**
   * A deterministic time provider that can signal when a caller has entered a wait.
   */
  private static final class ScriptedTimeProvider implements CDOTimeProvider
  {
    private final long[] values;

    private final CountDownLatch waiting = new CountDownLatch(1);

    private int index;

    ScriptedTimeProvider(long... values)
    {
      this.values = values;
    }

    @Override
    public long getTimeStamp()
    {
      int current = index++;
      if (current >= 1)
      {
        waiting.countDown();
      }

      return values[Math.min(current, values.length - 1)];
    }

    boolean awaitWaiting() throws InterruptedException
    {
      return waiting.await(5, TimeUnit.SECONDS);
    }
  }
}
