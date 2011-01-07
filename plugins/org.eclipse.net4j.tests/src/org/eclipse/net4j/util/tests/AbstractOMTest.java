/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.FileLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMTest extends TestCase
{
  public static final long DEFAULT_TIMEOUT = 120 * 1000;

  public static final long DEFAULT_TIMEOUT_EXPECTED = 2 * 1000;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractOMTest.class);

  public static boolean SUPPRESS_OUTPUT;

  private static boolean consoleEnabled;

  static
  {
    try
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
      String prefix = AbstractOMTest.class.getName() + "-" + formatter.format(new Date()) + "-";
      File logFile = File.createTempFile(prefix, ".log");
      OMPlatform.INSTANCE.addLogHandler(new FileLogHandler(logFile, OMLogger.Level.WARN));
      IOUtil.ERR().println("Logging errors and warnings to " + logFile);
      IOUtil.ERR().println();
    }
    catch (Throwable ex)
    {
      IOUtil.print(ex);
    }
  }

  protected AbstractOMTest()
  {
  }

  @Override
  public void setUp() throws Exception
  {
    enableConsole();
    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println("*******************************************************"); //$NON-NLS-1$
      Thread.yield();
      Thread.sleep(2L);
      IOUtil.ERR().println(this);
      Thread.yield();
      Thread.sleep(2L);
      IOUtil.OUT().println("*******************************************************"); //$NON-NLS-1$
    }

    super.setUp();
    doSetUp();

    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println("------------------------ START ------------------------"); //$NON-NLS-1$
    }
  }

  @Override
  public void tearDown() throws Exception
  {
    enableConsole();
    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println("------------------------- END -------------------------"); //$NON-NLS-1$
      IOUtil.OUT().println();
    }

    try
    {
      doTearDown();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    try
    {
      super.tearDown();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println();
    }
  }

  @Override
  public void runBare() throws Throwable
  {
    try
    {
      // Don't call super.runBare() because it does not clean up after exceptions from setUp()
      Throwable exception = null;

      try
      {
        setUp();
        runTest();
      }
      catch (Throwable running)
      {
        exception = running;
      }
      finally
      {
        try
        {
          tearDown();
        }
        catch (Throwable tearingDown)
        {
          if (exception == null)
          {
            exception = tearingDown;
          }
        }
      }

      if (exception != null)
      {
        throw exception;
      }
    }
    catch (SkipTestException ex)
    {
      OM.LOG.info("Skipped " + this); //$NON-NLS-1$
    }
    catch (Throwable t)
    {
      if (!SUPPRESS_OUTPUT)
      {
        t.printStackTrace(IOUtil.OUT());
      }

      throw t;
    }
  }

  @Override
  public void run(TestResult result)
  {
    try
    {
      super.run(result);
    }
    catch (SkipTestException ex)
    {
      OM.LOG.info("Skipped " + this); //$NON-NLS-1$
    }
    catch (RuntimeException ex)
    {
      if (!SUPPRESS_OUTPUT)
      {
        ex.printStackTrace(IOUtil.OUT());
      }

      throw ex;
    }
    catch (Error err)
    {
      if (!SUPPRESS_OUTPUT)
      {
        err.printStackTrace(IOUtil.OUT());
      }

      throw err;
    }
  }

  protected void enableConsole()
  {
    if (!SUPPRESS_OUTPUT)
    {
      PrintTraceHandler.CONSOLE.setShortContext(true);
      OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
      OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
      OMPlatform.INSTANCE.setDebugging(true);
      consoleEnabled = true;
    }
  }

  protected void disableConsole()
  {
    if (!SUPPRESS_OUTPUT)
    {
      consoleEnabled = false;
      OMPlatform.INSTANCE.setDebugging(false);
      OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
      OMPlatform.INSTANCE.removeLogHandler(PrintLogHandler.CONSOLE);
    }
  }

  protected void doSetUp() throws Exception
  {
  }

  protected void doTearDown() throws Exception
  {
  }

  @Deprecated
  public static void assertTrue(String message, boolean condition)
  {
    throw new UnsupportedOperationException("Use assertEquals()");
  }

  @Deprecated
  public static void assertTrue(boolean condition)
  {
    throw new UnsupportedOperationException("Use assertEquals()");
  }

  @Deprecated
  public static void assertFalse(String message, boolean condition)
  {
    throw new UnsupportedOperationException("Use assertEquals()");
  }

  @Deprecated
  public static void assertFalse(boolean condition)
  {
    throw new UnsupportedOperationException("Use assertEquals()");
  }

  public static void assertEquals(Object[] expected, Object[] actual)
  {
    if (!Arrays.deepEquals(expected, actual))
    {
      throw new AssertionFailedError("expected:" + Arrays.deepToString(expected) + " but was:"
          + Arrays.deepToString(actual));
    }
  }

  public static void assertEquals(Object expected, Object actual)
  {
    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    Assert.assertEquals(expected, actual);
  }

  public static void assertEquals(String message, Object expected, Object actual)
  {
    if (expected == null && actual == null)
    {
      return;
    }

    if (expected != null && expected.equals(actual))
    {
      return;
    }

    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    failNotEquals(message, expected, actual);
  }

  public static void sleep(long millis)
  {
    msg("Sleeping " + millis);
    ConcurrencyUtil.sleep(millis);
  }

  public static void assertInstanceOf(Class<?> expected, Object object)
  {
    assertEquals("Not an instance of " + expected + ": " + object.getClass().getName(), true,
        expected.isInstance(object));
  }

  public static void assertActive(Object object) throws InterruptedException
  {
    final LatchTimeOuter timeOuter = new LatchTimeOuter();
    IListener listener = new LifecycleEventAdapter()
    {
      @Override
      protected void onActivated(ILifecycle lifecycle)
      {
        timeOuter.countDown();
      }
    };

    EventUtil.addListener(object, listener);

    try
    {
      if (LifecycleUtil.isActive(object))
      {
        timeOuter.countDown();
      }

      timeOuter.assertNoTimeOut();
    }
    finally
    {
      EventUtil.removeListener(object, listener);
    }
  }

  public static void assertInactive(Object object) throws InterruptedException
  {
    final LatchTimeOuter timeOuter = new LatchTimeOuter();
    IListener listener = new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        timeOuter.countDown();
      }
    };

    EventUtil.addListener(object, listener);

    try
    {
      if (!LifecycleUtil.isActive(object))
      {
        timeOuter.countDown();
      }

      timeOuter.assertNoTimeOut();
    }
    finally
    {
      EventUtil.removeListener(object, listener);
    }
  }

  public static void assertSimilar(double expected, double actual, int precision)
  {
    final double factor = 10 * precision;
    if (Math.round(expected * factor) != Math.round(actual * factor))
    {
      assertEquals(expected, actual);
    }
  }

  public static void assertSimilar(float expected, float actual, int precision)
  {
    final float factor = 10 * precision;
    if (Math.round(expected * factor) != Math.round(actual * factor))
    {
      assertEquals(expected, actual);
    }
  }

  protected static void msg(Object m)
  {
    if (!SUPPRESS_OUTPUT)
    {
      if (consoleEnabled && TRACER.isEnabled())
      {
        TRACER.trace("--> " + m); //$NON-NLS-1$
      }
    }
  }

  protected static void skipTest(boolean skip)
  {
    if (skip)
    {
      throw new SkipTestException();
    }
  }

  protected static void skipTest()
  {
    skipTest(true);
  }

  /**
   * @author Eike Stepper
   */
  private static final class SkipTestException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
  }

  /**
   * @author Eike Stepper
   */
  public static class AsyncResult<T>
  {
    private volatile T value;

    private CountDownLatch latch = new CountDownLatch(1);

    public AsyncResult()
    {
    }

    public void setValue(T value)
    {
      this.value = value;
      latch.countDown();
    }

    public T getValue(long timeout) throws Exception
    {
      if (!latch.await(timeout, TimeUnit.MILLISECONDS))
      {
        throw new TimeoutException("Result value not available after " + timeout + " milli seconds");
      }

      return value;
    }

    public T getValue() throws Exception
    {
      return getValue(DEFAULT_TIMEOUT);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static interface ITimeOuter
  {
    public boolean timedOut(long timeoutMillis) throws InterruptedException;
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class TimeOuter implements ITimeOuter
  {
    public boolean timedOut() throws InterruptedException
    {
      return timedOut(DEFAULT_TIMEOUT);
    }

    public void assertTimeOut(long timeoutMillis) throws InterruptedException
    {
      assertEquals("Timeout expected", true, timedOut(timeoutMillis));
    }

    public void assertTimeOut() throws InterruptedException
    {
      assertTimeOut(DEFAULT_TIMEOUT_EXPECTED);
    }

    public void assertNoTimeOut(long timeoutMillis) throws InterruptedException
    {
      assertEquals("Timeout after " + timeoutMillis + " millis", false, timedOut(timeoutMillis));
    }

    public void assertNoTimeOut() throws InterruptedException
    {
      assertNoTimeOut(DEFAULT_TIMEOUT);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class PollingTimeOuter extends TimeOuter
  {
    public static final long DEFAULT_SLEEP_MILLIS = 1;

    private long sleepMillis = DEFAULT_SLEEP_MILLIS;

    public PollingTimeOuter(long sleepMillis)
    {
      this.sleepMillis = sleepMillis;
    }

    public PollingTimeOuter()
    {
    }

    public boolean timedOut(long timeoutMillis) throws InterruptedException
    {
      int retries = (int)Math.round(timeoutMillis / sleepMillis + .5d);
      for (int i = 0; i < retries; i++)
      {
        if (successful())
        {
          return false;
        }

        sleep(sleepMillis);
      }

      return true;
    }

    protected abstract boolean successful();
  }

  /**
   * @author Eike Stepper
   */
  public static class LockTimeOuter extends TimeOuter
  {
    private Lock lock;

    public LockTimeOuter(Lock lock)
    {
      this.lock = lock;
    }

    public Lock getLock()
    {
      return lock;
    }

    public boolean timedOut(long timeoutMillis) throws InterruptedException
    {
      Condition condition = lock.newCondition();
      return !condition.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class LatchTimeOuter extends TimeOuter
  {
    private CountDownLatch latch;

    public LatchTimeOuter(CountDownLatch latch)
    {
      this.latch = latch;
    }

    public LatchTimeOuter(int count)
    {
      this(new CountDownLatch(count));
    }

    public LatchTimeOuter()
    {
      this(1);
    }

    public CountDownLatch getLatch()
    {
      return latch;
    }

    public long getCount()
    {
      return latch.getCount();
    }

    public void countDown()
    {
      latch.countDown();
    }

    public void countDown(int n)
    {
      for (int i = 0; i < n; i++)
      {
        countDown();
      }
    }

    public boolean timedOut(long timeoutMillis) throws InterruptedException
    {
      return !latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
  }
}
