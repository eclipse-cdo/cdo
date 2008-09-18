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

import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;

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
    suite.addTestSuite(HbViewTest.class);
    suite.addTestSuite(HbCDOAutomaticPackageRefTest.class);
    suite.addTestSuite(HbCDOPackageRefTest.class);
    suite.addTestSuite(HbPackageRegistryTest.class);
    suite.addTestSuite(HbContainmentTest.class);
    suite.addTestSuite(HbResourceTest.class);
    suite.addTestSuite(HbComplexTest.class);
    suite.addTestSuite(HbTransactionDeadLockTest.class);
    suite.addTestSuite(HbRollbackTest.class);
    suite.addTestSuite(HbStateMachineTest.class);
    suite.addTestSuite(HbRevisionHolderTest.class);
    suite.addTestSuite(HbCrossReferenceTest.class);
    suite.addTestSuite(HbMangoTest.class);
    suite.addTestSuite(HbEnumTest.class);
    suite.addTestSuite(HbNonCDOResourceTest.class);
    suite.addTestSuite(HbInvalidationTest.class);
    suite.addTestSuite(HbIndexReconstructionTest.class);
    suite.addTestSuite(HbInitialTest.class);

    // These fail for standard cdo >>>
    // suite.addTestSuite(DymamicEcoreTest.class);
    // suite.addTestSuite(ContentAdapterTest.class);
    // suite.addTestSuite(FetchRuleAnalyzerTest.class);
    // suite.addTestSuite(GeneratedEcoreTest.class);

    // Chunking is not supported by Hibernate
    // suite.addTestSuite(Bugzilla241464_Test.class);
    // failures: /testWriteNative, testChunkWithTemporaryObject
    // suite.addTestSuite(ChunkingWithMEMTest.class);
    // failures testReadNative, testWriteNative
    // suite.addTestSuite(HbRevisionDeltaTest.class);

    // $JUnit-BEGIN$
    // TODO suite.addTestSuite(GeneratedEcoreTest.class);
    // $JUnit-END$

    return suite;
  }
}
