/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.bugzilla.*;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AllConfigs extends ConfigTestSuite
{
  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    // General
    testClasses.add(InitialTest.class);
    testClasses.add(CDOIDTest.class);
    testClasses.add(ComplexTest.class);
    testClasses.add(AttributeTest.class);
    testClasses.add(EnumTest.class);
    testClasses.add(EMapTest.class);
    testClasses.add(UnsetTest.class);
    testClasses.add(StateMachineTest.class);
    testClasses.add(SessionTest.class);
    testClasses.add(RevisionManagerTest.class);
    testClasses.add(RevisionManagerClientSideTest.class);
    testClasses.add(BranchingTest.class);
    testClasses.add(BranchingSameSessionTest.class);
    testClasses.add(BranchingWithCacheClearTest.class);
    testClasses.add(MergingTest.class);
    testClasses.add(ViewTest.class);
    testClasses.add(TransactionTest.class);
    testClasses.add(PushTransactionTest.class);
    testClasses.add(CommitInfoTest.class);
    testClasses.add(AuditTest.class);
    testClasses.add(AuditSameSessionTest.class);
    testClasses.add(ResourceTest.class);
    testClasses.add(ContainmentTest.class);
    testClasses.add(InvalidationTest.class);
    testClasses.add(RollbackTest.class);
    testClasses.add(CrossReferenceTest.class);
    testClasses.add(ChunkingTest.class);
    testClasses.add(ChunkingWithMEMTest.class);
    testClasses.add(MEMStoreQueryTest.class);
    testClasses.add(PackageRegistryTest.class);
    testClasses.add(PartialCommitTest.class);
    testClasses.add(MetaTest.class);
    testClasses.add(RevisionDeltaTest.class);
    testClasses.add(RevisionDeltaInBranchTest.class);
    testClasses.add(RevisionDeltaCascadingBranchesTest.class);
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
    testClasses.add(LockingManagerRestartTransactionTest.class);
    testClasses.add(LockingManagerRestartSessionTest.class);
    testClasses.add(LockingManagerRestartRepositoryTest.class);
    testClasses.add(LockingNotificationsTest.class);
    testClasses.add(MultiValuedOfAttributeTest.class);
    testClasses.add(MapTest.class);
    testClasses.add(FeatureMapTest.class);
    testClasses.add(AdapterManagerTest.class);
    testClasses.add(ConflictResolverTest.class);
    testClasses.add(DynamicXSDTest.class);
    testClasses.add(SetFeatureTest.class);
    testClasses.add(DynamicPackageTest.class);
    testClasses.add(LegacyTest.class);
    testClasses.add(XRefTest.class);
    testClasses.add(StickyViewsTest.class);
    testClasses.add(LobTest.class);
    testClasses.add(OCLQueryTest.class);
    testClasses.add(ViewProviderTest.class);
    testClasses.add(WorkspaceTest.class);
    testClasses.add(BackupTest.class);
    testClasses.add(ResourceModificationTrackingTest.class);

    // TODO testClasses.add(RemoteSessionManagerTest.class);
    // TODO testClasses.add(ConflictResolverMergingTest.class);
    // TODO testClasses.add(NonCDOResourceTest.class);
    // TODO testClasses.add(GeneratedEcoreTest.class);

    // Bugzilla verifications

    testClasses.add(Bugzilla_241464_Test.class);
    testClasses.add(Bugzilla_243310_Test.class);
    testClasses.add(Bugzilla_246442_Test.class);
    testClasses.add(Bugzilla_246622_Test.class);
    testClasses.add(Bugzilla_247141_Test.class);
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
    testClasses.add(Bugzilla_279565_Test.class);
    testClasses.add(Bugzilla_279982_Test.class);
    testClasses.add(Bugzilla_280102_Test.class);
    testClasses.add(Bugzilla_283131_Test.class);
    testClasses.add(Bugzilla_283985_1_Test.class);
    testClasses.add(Bugzilla_283985_2_Test.class);
    testClasses.add(Bugzilla_283985_3_Test.class);
    testClasses.add(Bugzilla_285008_Test.class);
    testClasses.add(Bugzilla_289932_Test.class);
    testClasses.add(Bugzilla_293283_Test.class);
    testClasses.add(Bugzilla_294859_Test.class);
    testClasses.add(Bugzilla_296561_Test.class);
    testClasses.add(Bugzilla_298561_Test.class);
    testClasses.add(Bugzilla_299190_Test.class);
    testClasses.add(Bugzilla_302233_Test.class);
    testClasses.add(Bugzilla_302414_Test.class);
    testClasses.add(Bugzilla_303466_Test.class);
    testClasses.add(Bugzilla_303807_Test.class);
    testClasses.add(Bugzilla_305527_Test.class);
    testClasses.add(Bugzilla_306710_Test.class);
    testClasses.add(Bugzilla_306998_Test.class);
    testClasses.add(Bugzilla_308895_Test.class);
    testClasses.add(Bugzilla_310574_Test.class);
    testClasses.add(Bugzilla_313326_Test.class);
    testClasses.add(Bugzilla_313913_Test.class);
    testClasses.add(Bugzilla_314186_Test.class);
    testClasses.add(Bugzilla_314264_Test.class);
    testClasses.add(Bugzilla_315043_Test.class);
    testClasses.add(Bugzilla_316145_Test.class);
    testClasses.add(Bugzilla_316273_Test.class);
    testClasses.add(Bugzilla_316434_Test.class);
    testClasses.add(Bugzilla_316444_Test.class);
    testClasses.add(Bugzilla_316887_Test.class);
    testClasses.add(Bugzilla_318518_Test.class);
    testClasses.add(Bugzilla_318844_Test.class);
    testClasses.add(Bugzilla_318876_Test.class);
    testClasses.add(Bugzilla_318919_Test.class);
    testClasses.add(Bugzilla_318998_Test.class);
    testClasses.add(Bugzilla_319836_Test.class);
    testClasses.add(Bugzilla_320690_Test.class);
    testClasses.add(Bugzilla_320837_Test.class);
    testClasses.add(Bugzilla_321699_Test.class);
    testClasses.add(Bugzilla_321986_Test.class);
    testClasses.add(Bugzilla_322754_Test.class);
    testClasses.add(Bugzilla_322804_Test.class);
    testClasses.add(Bugzilla_323930_Test.class);
    testClasses.add(Bugzilla_323958_Test.class);
    testClasses.add(Bugzilla_324084_Test.class);
    testClasses.add(Bugzilla_324585_Test.class);
    testClasses.add(Bugzilla_324635_Test.class);
    testClasses.add(Bugzilla_324756_Test.class);
    testClasses.add(Bugzilla_325603_Test.class);
    testClasses.add(Bugzilla_325866_Test.class);
    testClasses.add(Bugzilla_326518_Test.class);
    testClasses.add(Bugzilla_326743_Test.class);
    testClasses.add(Bugzilla_327604_Test.class);
    testClasses.add(Bugzilla_328790_Test.class);
    testClasses.add(Bugzilla_329254_Test.class);
    testClasses.add(Bugzilla_329752_Test.class);
    testClasses.add(Bugzilla_329753_Test.class);
    testClasses.add(Bugzilla_329869_Test.class);
    testClasses.add(Bugzilla_330052_Test.class);
    testClasses.add(Bugzilla_331619_Test.class);
    testClasses.add(Bugzilla_333157_Test.class);
    testClasses.add(Bugzilla_333299_Test.class);
    testClasses.add(Bugzilla_333950_Test.class);
    testClasses.add(Bugzilla_334608_Test.class);
    testClasses.add(Bugzilla_334995_Test.class);
    testClasses.add(Bugzilla_335004_Test.class);
    testClasses.add(Bugzilla_335675_Test.class);
    testClasses.add(Bugzilla_335772_Test.class);
    testClasses.add(Bugzilla_336314_Test.class);
    testClasses.add(Bugzilla_336382_Test.class);
    testClasses.add(Bugzilla_336590_Test.class);
    testClasses.add(Bugzilla_337054_Test.class);
    testClasses.add(Bugzilla_337523_Test.class);
    testClasses.add(Bugzilla_337587_Test.class);
    testClasses.add(Bugzilla_338779_Test.class);
    testClasses.add(Bugzilla_338884_Test.class);
    testClasses.add(Bugzilla_339313_Test.class);
    testClasses.add(Bugzilla_339461_Test.class);
    testClasses.add(Bugzilla_339908_Test.class);
    testClasses.add(Bugzilla_340961_Test.class);
    testClasses.add(Bugzilla_341875_Test.class);
    testClasses.add(Bugzilla_341995_Test.class);
    testClasses.add(Bugzilla_342130_Test.class);
    testClasses.add(Bugzilla_342135_Test.class);
    testClasses.add(Bugzilla_343332_Test.class);
    testClasses.add(Bugzilla_343471_Test.class);
    testClasses.add(Bugzilla_349793_Test.class);
    testClasses.add(Bugzilla_349804_Test.class);
    testClasses.add(Bugzilla_350027_Test.class);
    testClasses.add(Bugzilla_351067_Test.class);
    testClasses.add(Bugzilla_351096_Test.class);
    testClasses.add(Bugzilla_338921_Test.class);
    testClasses.add(Bugzilla_351393_Test.class);
    testClasses.add(Bugzilla_351921_Test.class);
    testClasses.add(Bugzilla_352303_Test.class);
    testClasses.add(Bugzilla_354395_Test.class);
    testClasses.add(Bugzilla_355915_Test.class);
  }
}
