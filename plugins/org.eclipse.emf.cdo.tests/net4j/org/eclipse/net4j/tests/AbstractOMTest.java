/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.emf.cdo.tests.bundle.OM;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.FileLogHandler;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMTest extends TestCase
{
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
      IOUtil.OUT().println("*******************************************************");
      sleep(1L);
      IOUtil.ERR().println(this);
      sleep(1L);
      IOUtil.OUT().println("*******************************************************");
    }

    super.setUp();
    doSetUp();

    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println("------------------------ START ------------------------");
    }
  }

  @Override
  public void tearDown() throws Exception
  {
    enableConsole();
    if (!SUPPRESS_OUTPUT)
    {
      IOUtil.OUT().println("------------------------- END -------------------------");
      IOUtil.OUT().println();
    }

    doTearDown();
    super.tearDown();

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
      super.runBare();
    }
    catch (SkipTestException ex)
    {
      OM.LOG.info("Skipped " + this);
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
      OM.LOG.info("Skipped " + this);
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

  public static void sleep(long millis)
  {
    ConcurrencyUtil.sleep(millis);
  }

  public static void assertActive(Object object)
  {
    assertEquals(true, LifecycleUtil.isActive(object));
  }

  public static void assertInactive(Object object)
  {
    assertEquals(false, LifecycleUtil.isActive(object));
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
      if (consoleEnabled)
      {
        IOUtil.OUT().println("--> " + m);
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
}
