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

import org.eclipse.emf.cdo.tests.ContainmentTest;
import org.eclipse.emf.cdo.tests.CrossReferenceTest;
import org.eclipse.emf.cdo.tests.EnumTest;
import org.eclipse.emf.cdo.tests.IndexReconstructionTest;
import org.eclipse.emf.cdo.tests.InitialTest;
import org.eclipse.emf.cdo.tests.InvalidationTest;
import org.eclipse.emf.cdo.tests.MangoTest;
import org.eclipse.emf.cdo.tests.NoLegacyTest;
import org.eclipse.emf.cdo.tests.PackageRegistryTest;
import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.RevisionDeltaTest;
import org.eclipse.emf.cdo.tests.RevisionHolderTest;
import org.eclipse.emf.cdo.tests.RollbackTest;
import org.eclipse.emf.cdo.tests.StateMachineTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;
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
    suite.addTestSuite(RevisionDeltaTest.class);
    suite.addTestSuite(HbTransactionDeadLockTest.class);
    suite.addTestSuite(RollbackTest.class);
    suite.addTestSuite(StateMachineTest.class);
    suite.addTestSuite(RevisionHolderTest.class);
    suite.addTestSuite(CrossReferenceTest.class);
    suite.addTestSuite(MangoTest.class);
    suite.addTestSuite(EnumTest.class);
    suite.addTestSuite(NoLegacyTest.class);
    suite.addTestSuite(ResourceTest.class);
    suite.addTestSuite(InvalidationTest.class);
    suite.addTestSuite(ContainmentTest.class);
    suite.addTestSuite(InitialTest.class);
    suite.addTestSuite(ViewTest.class);
    suite.addTestSuite(IndexReconstructionTest.class);
    suite.addTestSuite(PackageRegistryTest.class);

    // These fail for standard cdo >>>
    // suite.addTestSuite(DymamicEcoreTest.class);
    // suite.addTestSuite(ContentAdapterTest.class);
    // suite.addTestSuite(FetchRuleAnalyzerTest.class);
    // suite.addTestSuite(GeneratedEcoreTest.class);

    // Chunking is not supported by Hibernate
    // suite.addTestSuite(ChunkingTest.class);
    // failures: /testWriteNative, testChunkWithTemporaryObject
    // suite.addTestSuite(ChunkingWithMEMTest.class);
    // failures testReadNative, testWriteNative

    // $JUnit-BEGIN$
    // TODO suite.addTestSuite(GeneratedEcoreTest.class);
    // $JUnit-END$

    return suite;
  }
}
