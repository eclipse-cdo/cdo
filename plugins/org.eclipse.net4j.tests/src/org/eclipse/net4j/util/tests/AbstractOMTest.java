/*
 * Copyright (c) 2007-2016, 2018-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.internal.util.test.CurrentTestName;
import org.eclipse.net4j.internal.util.test.TestExecuter;
import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.util.ConsumerWithException;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.concurrent.TrackableTimerTask;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.FileLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.OMLogger.Level;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMTest extends TestCase
{
  /**
   * Timeout duration in milliseconds if timeout <b>is not</b> expected.
   */
  public static final long DEFAULT_TIMEOUT = 15 * 1000;

  /**
   * Timeout duration in milliseconds if timeout <b>is</b> expected.
   */
  public static final long DEFAULT_TIMEOUT_EXPECTED = 5 * 1000;

  public static boolean EXTERNAL_LOG;

  public static boolean SUPPRESS_OUTPUT;

  private static final IListener DUMPER = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      IOUtil.OUT().println(event);
    }
  };

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, AbstractOMTest.class);

  private static final boolean PHYSICALLY_DELETE_FILES = OMPlatform.INSTANCE.isProperty("PHYSICALLY_DELETE_FILES", true);

  private static boolean consoleEnabled;

  private static String testName;

  private transient final List<File> filesToDelete = new ArrayList<>();

  private transient String codeLink;

  static
  {
    try
    {
      if (EXTERNAL_LOG)
      {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String prefix = AbstractOMTest.class.getName() + "-" + formatter.format(new Date()) + "-";
        File logFile = TMPUtil.createTempFile(prefix, ".log");

        OMPlatform.INSTANCE.addLogHandler(new FileLogHandler(logFile, OMLogger.Level.WARN)
        {
          @Override
          protected void writeLog(OMLogger logger, Level level, String msg, Throwable t) throws Throwable
          {
            super.writeLog(logger, level, "--> " + testName + "\n" + msg, t);
          }
        });

        IOUtil.ERR().println("Logging errors and warnings to " + logFile);
        IOUtil.ERR().println();
      }
    }
    catch (Throwable ex)
    {
      IOUtil.print(ex);
    }
  }

  protected AbstractOMTest()
  {
  }

  public String getCodeLink()
  {
    return codeLink;
  }

  public void determineCodeLink()
  {
    if (codeLink == null)
    {
      codeLink = determineCodeLink(getName());
      if (codeLink == null)
      {
        codeLink = determineCodeLink("doSetUp");
        if (codeLink == null)
        {
          codeLink = getClass().getName() + "." + getName() + "(" + getClass().getSimpleName() + ".java:1)";
        }
      }
    }
  }

  protected String determineCodeLink(String methodName)
  {
    String className = getClass().getName();
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement frame : stackTrace)
    {
      if (frame.getClassName().equals(className) && frame.getMethodName().equals(methodName))
      {
        return frame.toString();
      }
    }

    return null;
  }

  protected boolean logSetUpAndTearDown()
  {
    return false;
  }

  @Override
  public void setUp() throws Exception
  {
    testName = getClass().getName() + "." + getName() + "()";
    CurrentTestName.set(testName);
    codeLink = null;

    PrintTraceHandler.CONSOLE.setShortContext(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    enableConsole();

    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println("*******************************************************\n" + this //$NON-NLS-1$
          + "\n*******************************************************"); //$NON-NLS-1$
    }

    if (!logSetUpAndTearDown())
    {
      disableConsole();
    }

    super.setUp();
    doSetUp();

    if (!SUPPRESS_OUTPUT && logSetUpAndTearDown())
    {
      IOUtil.OUT().println("\n------------------------ START ------------------------"); //$NON-NLS-1$
    }

    enableConsole();
  }

  @Override
  public void tearDown() throws Exception
  {
    if (logSetUpAndTearDown())
    {
      enableConsole();
    }
    else
    {
      disableConsole();
    }

    if (!SUPPRESS_OUTPUT && logSetUpAndTearDown())
    {
      IOUtil.OUT().println("------------------------- END -------------------------\n"); //$NON-NLS-1$
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

    try
    {
      TrackableTimerTask.logConstructionStackTraces(2 * DEFAULT_TIMEOUT);
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    try
    {
      clearReferences(getClass());
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

  protected void clearReferences(Class<?> c)
  {
    if (c != AbstractOMTest.class)
    {
      for (Field field : c.getDeclaredFields())
      {
        if (Modifier.isStatic(field.getModifiers()))
        {
          continue;
        }

        if (field.getType().isPrimitive())
        {
          continue;
        }

        ReflectUtil.makeAccessible(field);
        ReflectUtil.setValue(field, this, null);
      }

      clearReferences(c.getSuperclass());
    }
  }

  @Override
  public void runBare() throws Throwable
  {
    TestExecuter.execute(this, new TestExecuter.Executable()
    {
      @Override
      public void execute() throws Throwable
      {
        try
        {
          Throwable exception = null;

          try
          {
            setUp();

            // Don't call super.runBare() because it does not clean up after exceptions from setUp()
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
          OM.LOG.info("Skipped " + this + "\n");
        }
        catch (Throwable t)
        {
          // AssertionFailedError assertionFailedError = getAssertionFailedError(t);
          // if (assertionFailedError != null)
          // {
          // t = assertionFailedError;
          // }

          if (!SUPPRESS_OUTPUT)
          {
            t.printStackTrace(IOUtil.OUT());
          }

          throw t;
        }
      }

      @Override
      public String toString()
      {
        return getCurrrentTest().toString();
      }
    });
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
      OM.LOG.info("Skipped " + this + "\n");
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
      // AssertionFailedError assertionFailedError = getAssertionFailedError(err);
      // if (assertionFailedError != null)
      // {
      // err = assertionFailedError;
      // }

      if (!SUPPRESS_OUTPUT)
      {
        err.printStackTrace(IOUtil.OUT());
      }

      throw err;
    }
  }

  // private AssertionFailedError getAssertionFailedError(Throwable err)
  // {
  // if (err.getClass() == AssertionError.class)
  // {
  // // JUnit4 seems to throw java.lang.AssertionError, which the JUNit view displays as error rather than failure
  // AssertionFailedError replacementError = new AssertionFailedError(err.getMessage());
  // replacementError.initCause(err);
  // return replacementError;
  // }
  //
  // if (err instanceof AssertionFailedError)
  // {
  // return (AssertionFailedError)err;
  // }
  //
  // return null;
  // }

  protected void enableConsole()
  {
    if (!SUPPRESS_OUTPUT)
    {
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
      // OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
      // OMPlatform.INSTANCE.removeLogHandler(PrintLogHandler.CONSOLE);
    }
  }

  protected void doSetUp() throws Exception
  {
  }

  protected void doTearDown() throws Exception
  {
    deleteFiles();
  }

  public void deleteFiles()
  {
    synchronized (filesToDelete)
    {
      if (PHYSICALLY_DELETE_FILES)
      {
        for (File file : filesToDelete)
        {
          IOUtil.delete(file);
        }
      }

      filesToDelete.clear();
    }
  }

  public void addFileToDelete(File file)
  {
    synchronized (filesToDelete)
    {
      filesToDelete.add(file);
    }
  }

  public File getTempName() throws IORuntimeException
  {
    File name = TMPUtil.getTempName();
    addFileToDelete(name);
    return name;
  }

  public File getTempName(String prefix) throws IORuntimeException
  {
    File name = TMPUtil.getTempName(prefix);
    addFileToDelete(name);
    return name;
  }

  public File getTempName(String prefix, String suffix) throws IORuntimeException
  {
    File name = TMPUtil.getTempName(prefix, suffix);
    addFileToDelete(name);
    return name;
  }

  public File getTempName(String prefix, String suffix, File directory) throws IORuntimeException
  {
    File name = TMPUtil.getTempName(prefix, suffix, directory);
    addFileToDelete(name);
    return name;
  }

  public File createTempFolder() throws IORuntimeException
  {
    File folder = TMPUtil.createTempFolder();
    addFileToDelete(folder);
    return folder;
  }

  public File createTempFolder(String prefix) throws IORuntimeException
  {
    File folder = TMPUtil.createTempFolder(prefix);
    addFileToDelete(folder);
    return folder;
  }

  public File createTempFolder(String prefix, String suffix) throws IORuntimeException
  {
    File folder = TMPUtil.createTempFolder(prefix, suffix);
    addFileToDelete(folder);
    return folder;
  }

  public File createTempFolder(String prefix, String suffix, File directory) throws IORuntimeException
  {
    File folder = TMPUtil.createTempFile(prefix, suffix, directory);
    addFileToDelete(folder);
    return folder;
  }

  public File createTempFile() throws IORuntimeException
  {
    File file = TMPUtil.createTempFile();
    addFileToDelete(file);
    return file;
  }

  public File createTempFile(String prefix) throws IORuntimeException
  {
    File file = TMPUtil.createTempFile(prefix);
    addFileToDelete(file);
    return file;
  }

  public File createTempFile(String prefix, String suffix) throws IORuntimeException
  {
    File file = TMPUtil.createTempFile(prefix, suffix);
    addFileToDelete(file);
    return file;
  }

  public File createTempFile(String prefix, String suffix, File directory) throws IORuntimeException
  {
    File file = TMPUtil.createTempFile(prefix, suffix, directory);
    addFileToDelete(file);
    return file;
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "." + getName();
  }

  public static AbstractOMTest getCurrrentTest()
  {
    return (AbstractOMTest)TestExecuter.getCurrrentTest();
  }

  public static void assertTrue(String message, boolean condition)
  {
    assertEquals(message, true, condition);
  }

  public static void assertTrue(boolean condition)
  {
    assertEquals(true, condition);
  }

  public static void assertFalse(String message, boolean condition)
  {
    assertEquals(message, false, condition);
  }

  public static void assertFalse(boolean condition)
  {
    assertEquals(false, condition);
  }

  public static void assertEquals(Object[] expected, Object[] actual)
  {
    if (!Arrays.deepEquals(expected, actual))
    {
      throw new AssertionFailedError("expected:" + Arrays.deepToString(expected) + " but was:" + Arrays.deepToString(actual));
    }
  }

  public static void assertEquals(Object expected, Object actual)
  {
    if (actual == expected)
    {
      return;
    }

    try
    {
      Assert.assertEquals(expected, actual);
    }
    catch (AssertionError ex)
    {
      AssertionFailedError error = new AssertionFailedError(ex.getMessage());
      error.initCause(ex);
      throw error;
    }
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

  public static void assertEqualsIgnoreCase(String actual, String expected)
  {
    if (actual == null)
    {
      assertNull(expected);
    }

    if (!actual.equalsIgnoreCase(expected))
    {
      fail("Expected (ignoring case): '" + expected + "', but was: '" + actual + "'");
    }
  }

  public static void assertInstanceOf(Class<?> expected, Object object)
  {
    assertEquals("Not an instance of " + expected + ": " + object.getClass().getName(), true, expected.isInstance(object));
  }

  public static void assertNotInstanceOf(Class<?> expected, Object object)
  {
    assertEquals("An instance of " + expected + ": " + object.getClass().getName(), false, expected.isInstance(object));
  }

  public static void assertException(Class<? extends Throwable> type, RunnableWithException runnable)
  {
    try
    {
      runnable.run();
    }
    catch (Throwable ex)
    {
      assertInstanceOf(type, ex);
      return;
    }

    fail("Expected " + type);
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
        return;
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

  public static void assertNoTimeout(BooleanSupplier success)
  {
    assertNoTimeout(DEFAULT_TIMEOUT, success);
  }

  public static void assertNoTimeout(long timeout, BooleanSupplier success)
  {
    withTimeOuter(success, timeOuter -> timeOuter.assertNoTimeOut(timeout));
  }

  public static void assertTimeout(BooleanSupplier success)
  {
    assertTimeout(DEFAULT_TIMEOUT_EXPECTED, success);
  }

  public static void assertTimeout(long timeout, BooleanSupplier success)
  {
    withTimeOuter(success, timeOuter -> timeOuter.assertTimeOut(timeout));
  }

  private static void withTimeOuter(BooleanSupplier success, ConsumerWithException<TimeOuter, InterruptedException> consumer)
  {
    try
    {
      TimeOuter timeOuter = new PollingTimeOuter()
      {
        @Override
        protected boolean successful()
        {
          return success.getAsBoolean();
        }
      };

      consumer.accept(timeOuter);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      throw WrappedException.wrap(ex);
    }
  }

  public static void sleep(long millis)
  {
    msg("Sleeping " + millis);
    ConcurrencyUtil.sleep(millis);
  }

  public static void msg(Object m)
  {
    if (!SUPPRESS_OUTPUT)
    {
      if (consoleEnabled && TRACER.isEnabled())
      {
        TRACER.trace(String.valueOf(m));
      }
    }
  }

  public static void dumpEvents(Object notifier)
  {
    dumpEvents(notifier, true);
  }

  public static void dumpEvents(Object notifier, boolean on)
  {
    if (notifier instanceof INotifier)
    {
      INotifier iNotifier = (INotifier)notifier;
      IListener[] listeners = iNotifier.getListeners();

      boolean wasOn = false;
      for (int i = 0; i < listeners.length; i++)
      {
        if (listeners[i] == DUMPER)
        {
          wasOn = true;
          break;
        }
      }

      if (on && !wasOn)
      {
        iNotifier.addListener(DUMPER);
      }
      else if (!on && wasOn)
      {
        iNotifier.removeListener(DUMPER);
      }
    }
  }

  public static void skipTest(boolean skip)
  {
    if (skip)
    {
      throw new SkipTestException();
    }
  }

  public static void skipTest()
  {
    skipTest(true);
  }

  public static void triggerGC()
  {
    List<byte[]> bigdata = new ArrayList<>();

    try
    {
      while (true)
      {
        try
        {
          bigdata.add(new byte[1024 * 1024]);
        }
        catch (Throwable ex)
        {
          break;
        }
      }
    }
    catch (Throwable ex)
    {
      //$FALL-THROUGH$
    }
  }

  public static void disableLog4j()
  {
    BasicConfigurator.configure(new Appender()
    {
      @Override
      public void setName(String arg0)
      {
      }

      @Override
      public void setLayout(Layout arg0)
      {
      }

      @Override
      public void setErrorHandler(ErrorHandler arg0)
      {
      }

      @Override
      public boolean requiresLayout()
      {
        return false;
      }

      @Override
      public String getName()
      {
        return null;
      }

      @Override
      public Layout getLayout()
      {
        return null;
      }

      @Override
      public Filter getFilter()
      {
        return null;
      }

      @Override
      public ErrorHandler getErrorHandler()
      {
        return null;
      }

      @Override
      public void doAppend(LoggingEvent arg0)
      {
      }

      @Override
      public void close()
      {
      }

      @Override
      public void clearFilters()
      {
      }

      @Override
      public void addFilter(Filter arg0)
      {
      }
    });
  }

  public static void await(CountDownLatch latch) throws TimeoutRuntimeException
  {
    await(latch, DEFAULT_TIMEOUT);
  }

  public static void await(CountDownLatch latch, long millis) throws TimeoutRuntimeException
  {
    try
    {
      if (!latch.await(millis, TimeUnit.MILLISECONDS))
      {
        throw new TimeoutRuntimeException("Latch timed out: " + latch);
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SkipTestException extends RuntimeException
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

    @Override
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

    @Override
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

    @Override
    public boolean timedOut(long timeoutMillis) throws InterruptedException
    {
      return !latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ThreadTimeOuter extends LatchTimeOuter implements Runnable
  {
    private RuntimeException exception;

    public ThreadTimeOuter()
    {
      super(1);
    }

    @Override
    public boolean timedOut(long timeoutMillis) throws InterruptedException
    {
      Thread thread = new Thread(this, "ThreadTimeOuter")
      {
        @Override
        public void run()
        {
          try
          {
            ThreadTimeOuter.this.run();
          }
          catch (RuntimeException ex)
          {
            exception = ex;
          }
          finally
          {
            countDown();
          }
        }
      };

      thread.setDaemon(true);
      thread.start();

      boolean result = super.timedOut(timeoutMillis);

      if (exception != null)
      {
        throw exception;
      }

      return result;
    }
  }
}
