/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.tests.bugzilla.Bugzilla_241463_Test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 */
public class AllSSLTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for SSL Net4j"); //$NON-NLS-1$
    suite.addTestSuite(ChannelTest.SSL.class);
    suite.addTestSuite(TCPConnectorTest.SSL.class);
    suite.addTestSuite(TransportTest.SSL.class);
    suite.addTestSuite(SignalTest.SSL.class);
    suite.addTestSuite(SignalMonitorTest.SSL.class);
    suite.addTestSuite(ExceptionTest.SSL.class);
    // Bugzillas
    suite.addTestSuite(Bugzilla_241463_Test.SSL.class);

    return suite;
  }
}
