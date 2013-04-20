/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.all;

import org.eclipse.emf.cdo.tests.AllTests;
import org.eclipse.emf.cdo.tests.db.AllTestsDBH2All;
import org.eclipse.emf.cdo.tests.db.offline.AllTestsDBH2Offline;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AutomatedTests extends TestSuite
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite(AutomatedTests.class.getName());
    suite.addTest(new AllTests().getTestSuite());
    suite.addTest(new AllTestsDBH2All().getTestSuite());
    suite.addTest(new AllTestsDBH2Offline().getTestSuite());
    return suite;
  }
}
