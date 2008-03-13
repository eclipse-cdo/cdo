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
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.ChunkingTest;
import org.eclipse.emf.cdo.tests.ChunkingWithMEMTest;
import org.eclipse.emf.cdo.tests.ContainmentTest;
import org.eclipse.emf.cdo.tests.CrossReferenceTest;
import org.eclipse.emf.cdo.tests.EnumTest;
import org.eclipse.emf.cdo.tests.IndexReconstructionTest;
import org.eclipse.emf.cdo.tests.InitialTest;
import org.eclipse.emf.cdo.tests.InvalidationTest;
import org.eclipse.emf.cdo.tests.NoLegacyTest;
import org.eclipse.emf.cdo.tests.PackageRegistryTest;
import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.RevisionDeltaTest;
import org.eclipse.emf.cdo.tests.RollbackTest;
import org.eclipse.emf.cdo.tests.StateMachineTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;
import org.eclipse.emf.cdo.tests.TransactionDeadLockTest;
import org.eclipse.emf.cdo.tests.ViewTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests
{
  public static Test suite()
  {
    StoreRepositoryProvider.setInstance(HbStoreRepositoryProvider.getInstance());

    TestSuite suite = new TestSuite("Tests for CDO using Hibernate");

    if (true)
    {
      return suite;
    }
    // PASS>>>
    suite.addTestSuite(ResourceTest.class);
    suite.addTestSuite(ContainmentTest.class);
    suite.addTestSuite(StateMachineTest.class);
    suite.addTestSuite(ViewTest.class);
    suite.addTestSuite(CrossReferenceTest.class);
    suite.addTestSuite(PackageRegistryTest.class);
    suite.addTestSuite(IndexReconstructionTest.class);
    suite.addTestSuite(NoLegacyTest.class);

    // FAIL>>>
    suite.addTestSuite(InitialTest.class);
    // failures: testCommitDirty
    suite.addTestSuite(InvalidationTest.class);
    // failures: testSeparateView, testSeparateViewNotification, testSeparateSession
    suite.addTestSuite(RollbackTest.class);
    // failes: testRollbackSameSession, testRollbackSeparateSession
    suite.addTestSuite(ChunkingTest.class);
    // failures: /testWriteNative, testChunkWithTemporaryObject
    suite.addTestSuite(ChunkingWithMEMTest.class);
    // failures testReadNative, testWriteNative
    suite.addTestSuite(TransactionDeadLockTest.class);
    // failures: testCreateManySession, testCreateManyTransaction
    suite.addTestSuite(RevisionDeltaTest.class);
    // Remark: I don't think hibernate won't support this.
    suite.addTestSuite(EnumTest.class);
    // failures: testTransient, testAttached

    // $JUnit-BEGIN$
    // TODO suite.addTestSuite(GeneratedEcoreTest.class);
    // $JUnit-END$

    return suite;
  }
}
