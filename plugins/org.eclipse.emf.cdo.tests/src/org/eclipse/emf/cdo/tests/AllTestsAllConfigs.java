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

import org.eclipse.emf.cdo.tests.AuditTest.LocalAuditTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_241464_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_243310_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246442_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246456_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_246622_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_248052_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_248124_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_248915_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_250036_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_250757_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_250910_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_251087_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_251263_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_251544_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_251752_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252909_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_254489_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_255662_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_256141_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258278_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AllTestsAllConfigs extends ConfigTestSuite
{
  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {

    // General
    testClasses.add(InitialTest.class);
    testClasses.add(ComplexTest.class);
    testClasses.add(AttributeTest.class);
    testClasses.add(EnumTest.class);
    testClasses.add(StateMachineTest.class);
    testClasses.add(SessionTest.class);
    testClasses.add(ViewTest.class);
    testClasses.add(TransactionTest.class);
    testClasses.add(AuditTest.class);
    testClasses.add(LocalAuditTest.class);
    testClasses.add(ResourceTest.class);
    testClasses.add(ContainmentTest.class);
    testClasses.add(InvalidationTest.class);
    testClasses.add(RollbackTest.class);
    testClasses.add(CrossReferenceTest.class);
    testClasses.add(ChunkingTest.class);
    testClasses.add(ChunkingWithMEMTest.class);
    testClasses.add(PackageRegistryTest.class);
    testClasses.add(MetaTest.class);
    testClasses.add(RevisionDeltaWithDeltaSupportTest.class);
    testClasses.add(RevisionDeltaWithoutDeltaSupportTest.class);
    testClasses.add(RevisionHolderTest.class);
    testClasses.add(IndexReconstructionTest.class);
    testClasses.add(AutoAttacherTest.class);
    testClasses.add(SavePointTest.class);
    testClasses.add(ChangeSubscriptionTest.class);
    testClasses.add(DetachTest.class);
    testClasses.add(ExternalReferenceTest.class);
    testClasses.add(XATransactionTest.class);
    testClasses.add(TransactionHandlerTest.class);
    testClasses.add(RepositoryTest.class);
    testClasses.add(LockingManagerTest.class);
    testClasses.add(MultiValuedOfAttributeTest.class);
    testClasses.add(AdapterManagerTest.class);
    testClasses.add(ConflictResolverTest.class);

    // Specific for MEMStore
    testClasses.add(MEMStoreQueryTest.class);

    // Specific for DBStore
    testClasses.add(DBStoreTest.class);

    // Bugzilla verifications
    testClasses.add(Bugzilla_241464_Test.class);
    testClasses.add(Bugzilla_243310_Test.class);
    testClasses.add(Bugzilla_246442_Test.class);
    testClasses.add(Bugzilla_246456_Test.class);
    testClasses.add(Bugzilla_246622_Test.class);
    testClasses.add(Bugzilla_248052_Test.class);
    testClasses.add(Bugzilla_248124_Test.class);
    testClasses.add(Bugzilla_248915_Test.class);
    testClasses.add(Bugzilla_250036_Test.class);
    testClasses.add(Bugzilla_250757_Test.class);
    testClasses.add(Bugzilla_250910_Test.class);
    testClasses.add(Bugzilla_251087_Test.class);
    testClasses.add(Bugzilla_251263_Test.class);
    testClasses.add(Bugzilla_251544_Test.class);
    testClasses.add(Bugzilla_251752_Test.class);
    testClasses.add(Bugzilla_252214_Test.class);
    testClasses.add(Bugzilla_252909_Test.class);
    testClasses.add(Bugzilla_254489_Test.class);
    testClasses.add(Bugzilla_255662_Test.class);
    testClasses.add(Bugzilla_256141_Test.class);
    testClasses.add(Bugzilla_258278_Test.class);

    // TODO testClasses.add(NonCDOResourceTest.class);
    // TODO testClasses.add(GeneratedEcoreTest.class);
  }
}
