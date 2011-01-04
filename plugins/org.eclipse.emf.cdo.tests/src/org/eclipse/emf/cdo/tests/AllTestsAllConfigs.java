/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
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
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258850_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259695_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259949_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_260756_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_260764_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_265114_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_266857_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_266982_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_267050_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_267352_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_270429_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_272861_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_273233_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_273565_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_273758_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_276696_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_278900_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_283985_CDOTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_283985_CDOTest2;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_283985_SavePointTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_285008_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_289932_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_293283_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_294850_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_294859_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_299190_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_301671_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_302414_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_316175_Test;
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
    testClasses.add(CDOIDTest.class);
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
    testClasses.add(ResourceModificationTrackingTest.class);
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
    testClasses.add(BackupTest.class);

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
    testClasses.add(Bugzilla_258850_Test.class);
    testClasses.add(Bugzilla_258933_Test.class);
    testClasses.add(Bugzilla_259695_Test.class);
    testClasses.add(Bugzilla_259869_Test.class);
    testClasses.add(Bugzilla_259949_Test.class);
    testClasses.add(Bugzilla_260756_Test.class);
    testClasses.add(Bugzilla_260764_Test.class);
    testClasses.add(Bugzilla_261218_Test.class);
    testClasses.add(Bugzilla_265114_Test.class);
    testClasses.add(Bugzilla_266857_Test.class);
    testClasses.add(Bugzilla_266982_Test.class);
    testClasses.add(Bugzilla_267050_Test.class);
    testClasses.add(Bugzilla_267352_Test.class);
    testClasses.add(Bugzilla_270429_Test.class);
    testClasses.add(Bugzilla_272861_Test.class);
    testClasses.add(Bugzilla_273233_Test.class);
    testClasses.add(Bugzilla_273565_Test.class);
    testClasses.add(Bugzilla_273758_Test.class);
    testClasses.add(Bugzilla_276696_Test.class);
    testClasses.add(Bugzilla_278900_Test.class);
    testClasses.add(Bugzilla_289932_Test.class);
    testClasses.add(Bugzilla_283985_CDOTest.class);
    testClasses.add(Bugzilla_283985_CDOTest2.class);
    testClasses.add(Bugzilla_283985_SavePointTest.class);
    testClasses.add(Bugzilla_285008_Test.class);
    testClasses.add(Bugzilla_293283_Test.class);
    testClasses.add(Bugzilla_294850_Test.class);
    testClasses.add(Bugzilla_294859_Test.class);
    testClasses.add(Bugzilla_299190_Test.class);
    testClasses.add(Bugzilla_301671_Test.class);
    testClasses.add(Bugzilla_302414_Test.class);
    testClasses.add(Bugzilla_316175_Test.class);

    // TODO testClasses.add(NonCDOResourceTest.class);
    // TODO testClasses.add(GeneratedEcoreTest.class);
  }
}
