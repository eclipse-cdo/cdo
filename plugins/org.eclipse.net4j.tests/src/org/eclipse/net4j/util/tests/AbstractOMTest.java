/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMTest extends TestCase
{
  private static boolean consoleEnabled;

  protected AbstractOMTest()
  {
  }

  @Override
  public final void setUp() throws Exception
  {
    super.setUp();
    IOUtil.OUT().println("************************************************");
    IOUtil.OUT().println(getName());
    IOUtil.OUT().println("************************************************");

    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);
    enableConsole();

    doSetUp();
    IOUtil.OUT().println();
    IOUtil.OUT().println("------------------------ START ------------------------");
  }

  @Override
  public final void tearDown() throws Exception
  {
    sleep(200);
    IOUtil.OUT().println("------------------------ END --------------------------");
    IOUtil.OUT().println();

    doTearDown();
    super.tearDown();
    IOUtil.OUT().println();
    IOUtil.OUT().println();
  }

  @Override
  protected void runTest() throws Throwable
  {
    try
    {
      super.runTest();
    }
    catch (Throwable t)
    {
      t.printStackTrace(IOUtil.OUT());
      throw t;
    }
  }

  protected void enableConsole()
  {
    if (!consoleEnabled)
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
    if (consoleEnabled)
    {
      consoleEnabled = false;
      OMPlatform.INSTANCE.setDebugging(false);
      OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
      OMPlatform.INSTANCE.removeLogHandler(PrintLogHandler.CONSOLE);
    }
  }

  protected void doSetUp() throws Exception
  {
    // Guaranteed to be empty
  }

  protected void doTearDown() throws Exception
  {
    // Guaranteed to be empty
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
    if (consoleEnabled)
    {
      IOUtil.OUT().println("--> " + m);
    }
  }
}
