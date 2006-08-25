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
package org.eclipse.emf.cdo.tests;


import org.eclipse.emf.cdo.tests.topology.AbstractTopologyTest;
import org.eclipse.emf.cdo.tests.topology.ITopologyConstants;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllSuites extends TestSuite
{
  public static Test suite()
  {
    return new AllSuites("CDO Build JUnit Test Suite");
  }

  public AllSuites()
  {
    super();
    populateSuite();
  }

  public AllSuites(Class theClass)
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
    for (String mode : ITopologyConstants.SELF_CONTAINED_MODES)
    {
      TestSuite topologySuite = new TestSuite("Mode " + mode);
      Test[] suites = createPackageSuites();
      for (int i = 0; i < suites.length; i++)
      {
        topologySuite.addTest(suites[i]);
      }

      recursivelySetMode(topologySuite, mode);
      addTest(topologySuite);
    }
  }

  protected void recursivelySetMode(Test test, String mode)
  {
    if (test instanceof AbstractTopologyTest)
    {
      AbstractTopologyTest topologyTest = (AbstractTopologyTest) test;
      topologyTest.setMode(mode);
    }
    else if (test instanceof TestSuite)
    {
      TestSuite suite = (TestSuite) test;
      Enumeration enumeration = suite.tests();
      while (enumeration.hasMoreElements())
      {
        Test child = (Test) enumeration.nextElement();
        recursivelySetMode(child, mode);
      }
    }
  }

  protected Test[] createPackageSuites()
  {
    return new Test[] { //
    org.eclipse.emf.cdo.tests.model1.AllTests.suite() //
    };
  }
}
