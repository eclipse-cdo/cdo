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
    suite.addTestSuite(StateMachineTest.class);
    suite.addTestSuite(ViewTest.class);
    suite.addTestSuite(ResourceTest.class);
    suite.addTestSuite(ContainmentTest.class);
    suite.addTestSuite(InvalidationTest.class);
    suite.addTestSuite(RollbackTest.class);
    suite.addTestSuite(CrossReferenceTest.class);
    suite.addTestSuite(ChunkingTest.class);
    suite.addTestSuite(ChunkingWithMEMTest.class);
    suite.addTestSuite(TransactionDeadLockTest.class);
    suite.addTestSuite(PackageRegistryTest.class);
    suite.addTestSuite(RevisionDeltaTest.class);
    suite.addTestSuite(IndexReconstructionTest.class);
    // TODO suite.addTestSuite(GeneratedEcoreTest.class);
    // $JUnit-END$

    return suite;
  }
}
