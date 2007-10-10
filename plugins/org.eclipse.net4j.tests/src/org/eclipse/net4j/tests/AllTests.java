/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.util.tests.MonitorTest;
import org.eclipse.net4j.util.tests.SortedFileMapTest;
import org.eclipse.net4j.util.tests.SynchronizingCorrelatorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for Net4j");

    // $JUnit-BEGIN$
    suite.addTestSuite(MonitorTest.class);
    suite.addTestSuite(SortedFileMapTest.class);
    suite.addTestSuite(SynchronizingCorrelatorTest.class);
    suite.addTestSuite(TestBufferPool.class);
    // suite.addTestSuite(SecurityTest.class);
    // TODO suite.addTestSuite(ConnectorTest.class);
    // $JUnit-END$

    return suite;
  }
}
