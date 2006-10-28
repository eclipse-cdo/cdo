/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMTest extends TestCase
{
  protected AbstractOMTest()
  {
  }

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    System.out.print("================================= ");
    System.out.print(getName());
    System.out.println(" =================================");

    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);
  }

  @Override
  protected void tearDown() throws Exception
  {
    System.out.println();
    System.out.println();
    Thread.sleep(20);
    super.tearDown();
  }
}