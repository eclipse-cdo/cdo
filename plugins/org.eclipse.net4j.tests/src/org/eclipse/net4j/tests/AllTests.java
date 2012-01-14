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
package org.eclipse.net4j.tests;

import org.eclipse.net4j.tests.bugzilla.Bugzilla_241463_Test;
import org.eclipse.net4j.tests.bugzilla.Bugzilla_262875_Test;
import org.eclipse.net4j.util.tests.ExpectedIOTest;
import org.eclipse.net4j.util.tests.ExtendedIOTest;
import org.eclipse.net4j.util.tests.MultiMapTest;
import org.eclipse.net4j.util.tests.QueueWorkerWorkSerializerTest;
import org.eclipse.net4j.util.tests.ReferenceValueMapTest;
import org.eclipse.net4j.util.tests.SecurityTest;
import org.eclipse.net4j.util.tests.SortedFileMapTest;
import org.eclipse.net4j.util.tests.StringCompressorTest;
import org.eclipse.net4j.util.tests.SynchronizingCorrelatorTest;
import org.eclipse.net4j.util.tests.UUIDGeneratorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for Net4j"); //$NON-NLS-1$
    suite.addTestSuite(UUIDGeneratorTest.class);
    suite.addTestSuite(MultiMapTest.class);
    suite.addTestSuite(SortedFileMapTest.class);
    suite.addTestSuite(SynchronizingCorrelatorTest.class);
    suite.addTestSuite(ReferenceValueMapTest.class);
    suite.addTestSuite(BufferPoolTest.class);
    suite.addTestSuite(ExtendedIOTest.class);
    suite.addTestSuite(StringCompressorTest.class);
    suite.addTestSuite(ChannelTest.JVM.class);
    suite.addTestSuite(ChannelTest.TCP.class);
    // suite.addTestSuite(ChannelTest.SSL.class);
    suite.addTestSuite(TCPConnectorTest.TCP.class);
    // suite.addTestSuite(TCPConnectorTest.SSL.class);
    suite.addTestSuite(TransportTest.JVM.class);
    suite.addTestSuite(TransportTest.TCP.class);
    // suite.addTestSuite(TransportTest.SSL.class);
    suite.addTestSuite(SignalTest.TCP.class);
    // suite.addTestSuite(SignalTest.SSL.class);
    suite.addTestSuite(SignalMonitorTest.TCP.class);
    // suite.addTestSuite(SignalMonitorTest.SSL.class);
    suite.addTestSuite(ExceptionTest.TCP.class);
    // suite.addTestSuite(ExceptionTest.SSL.class);
    suite.addTestSuite(SecurityTest.class);
    suite.addTestSuite(QueueWorkerWorkSerializerTest.class);
    suite.addTestSuite(ExpectedIOTest.class);

    // Bugzillas
    suite.addTestSuite(Bugzilla_241463_Test.TCP.class);
    // suite.addTestSuite(Bugzilla_241463_Test.SSL.class);
    suite.addTestSuite(Bugzilla_262875_Test.class);

    // Defs
    // suite.addTestSuite(TestDefTest.class);
    // suite.addTestSuite(TCPAcceptorDefImplTest.class);
    // suite.addTestSuite(TCPConnectorDefImplTest.class);
    // suite.addTestSuite(JVMAcceptorDefImplTest.class);
    // suite.addTestSuite(JVMConnectorDefImplTest.class);

    return suite;
  }
}
