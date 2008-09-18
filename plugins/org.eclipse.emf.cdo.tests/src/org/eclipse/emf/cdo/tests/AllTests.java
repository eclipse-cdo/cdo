/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_241464_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_243310_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246442_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246456_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246622_Test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for CDO");

    // $JUnit-BEGIN$
    suite.addTestSuite(InitialTest.class);

    suite.addTestSuite(ComplexTest.class);
    suite.addTestSuite(AttributeTest.class);
    suite.addTestSuite(EnumTest.class);
    suite.addTestSuite(StateMachineTest.class);
    suite.addTestSuite(ViewTest.class);
    suite.addTestSuite(ResourceTest.class);
    suite.addTestSuite(NonCDOResourceTest.class);
    suite.addTestSuite(ContainmentTest.class);
    suite.addTestSuite(InvalidationTest.class);
    suite.addTestSuite(RollbackTest.class);
    suite.addTestSuite(CrossReferenceTest.class);
    suite.addTestSuite(ChunkingTest.class);
    suite.addTestSuite(ChunkingWithMEMTest.class);
    suite.addTestSuite(TransactionDeadLockTest.class);
    suite.addTestSuite(PackageRegistryTest.class);
    suite.addTestSuite(MetaTest.class);
    suite.addTestSuite(RevisionDeltaWithDeltaSupportTest.class);
    suite.addTestSuite(RevisionDeltaWithoutDeltaSupportTest.class);
    suite.addTestSuite(IndexReconstructionTest.class);
    suite.addTestSuite(AutoAttacherTest.class);
    suite.addTestSuite(SavepointTest.class);
    suite.addTestSuite(ChangeSubscriptionTest.class);
    suite.addTestSuite(DetachTest.class);
    suite.addTestSuite(ExternalReferenceTest.class);
    suite.addTestSuite(XATransactionTest.class);
    suite.addTestSuite(TransactionHandlerTest.class);

    // Specific for MEMStore
    suite.addTestSuite(QueryTest.class);

    // Bugzilla verifications
    suite.addTestSuite(Bugzilla_241464_Test.class);
    suite.addTestSuite(Bugzilla_243310_Test.class);
    suite.addTestSuite(Bugzilla_246442_Test.class);
    suite.addTestSuite(Bugzilla_246456_Test.class);
    suite.addTestSuite(Bugzilla_246622_Test.class);

    // TODO suite.addTestSuite(GeneratedEcoreTest.class);
    // $JUnit-END$

    return suite;
  }
}
