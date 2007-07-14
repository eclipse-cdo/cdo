/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.internal.util.om.log.PrintLogHandler;
import org.eclipse.net4j.internal.util.om.trace.PrintTraceHandler;
import org.eclipse.net4j.util.om.OMPlatform;

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
  protected void setUp() throws Exception
  {
    super.setUp();
    System.out.println("************************************************");
    System.out.println("START " + getName());
    System.out.println("************************************************");

    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);
    enableConsole();
  }

  @Override
  protected void tearDown() throws Exception
  {
    Thread.sleep(20);
    super.tearDown();
    System.out.println();
    System.out.println();
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

  @SuppressWarnings("unused")
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

  protected static void msg(String m)
  {
    if (consoleEnabled)
    {
      System.out.println();
      System.out.println("--> " + m);
    }
  }
}