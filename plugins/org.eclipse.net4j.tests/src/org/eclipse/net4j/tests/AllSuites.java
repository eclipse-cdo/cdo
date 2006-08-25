/**
 * <copyright>
 *
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: AllSuites.java,v 1.4 2006-08-25 09:38:15 estepper Exp $
 */

package org.eclipse.net4j.tests;


import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSuites extends TestSuite
{
  private static Test[] suites = new Test []{ 
    // list all test suites here
	  org.eclipse.net4j.tests.AllTests.suite()
	  ,org.eclipse.net4j.spring.tests.AllTests.suite()
  };

  public static Test suite()
  {
    return new AllSuites("Net4j Build JUnit Test Suite");
  }

  public AllSuites()
  {
    super();
    populateSuite();
  }

  public AllSuites(Class<?> theClass)
  {
    super(theClass);
    populateSuite();
  }

  public AllSuites(String name)
  {
    super(name);
    populateSuite();
  }

  protected void populateSuite()
  {
    for (int i = 0; i < suites.length; i++)
    {
      addTest(suites[i]);
    }
  }
}