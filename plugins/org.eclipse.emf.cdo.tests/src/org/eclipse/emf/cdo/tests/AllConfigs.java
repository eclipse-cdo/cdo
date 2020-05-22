/*
 * Copyright (c) 2010-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - test suite for partial/conditional persistence
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllConfigs extends ConfigTestSuite
{
  public static Test suite()
  {
    return new AllConfigs().getTestSuite();
  }

  public List<Class<? extends ConfigTest>> getBugzillaTests()
  {
    return getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.bugzilla");
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    // testClasses.add(ConflictResolverExtendedTest.class);

    // General
    testClasses.add(InitialTest.class);
    testClasses.add(CDOIDTest.class);
    testClasses.add(ComplexTest.class);
    testClasses.add(AttributeTest.class);
    // testClasses.add(CommitPerformanceTest.class);
    // testClasses.add(BigModelTest.class);
    testClasses.add(EnumTest.class);
    testClasses.add(EMapTest.class);
    testClasses.add(UnsetTest.class);
    testClasses.add(StateMachineTest.class);
    testClasses.add(SessionTest.class);
    testClasses.add(ReconnectingSessionTest.class);
    testClasses.add(RevisionManagerTest.class);
    testClasses.add(RevisionManagerClientSideTest.class);
    testClasses.add(BranchingTest.class);
    testClasses.add(BranchingSameSessionTest.class);
    testClasses.add(BranchingWithCacheClearTest.class);
    testClasses.add(MergingTest.class);
    testClasses.add(ViewTest.class);
    testClasses.add(TransactionTest.class);
    testClasses.add(PushTransactionTest.class);
    testClasses.add(PushTransactionWithoutReconstructSavepointsTest.class);
    testClasses.add(CommitInfoTest.class);
    testClasses.add(SecurityTest.class);
    testClasses.add(AuditTest.class);
    testClasses.add(AuditEMapTest.class);
    testClasses.add(AuditSameSessionTest.class);
    testClasses.add(ResourceTest.class);
    testClasses.add(ContainmentTest.class);
    testClasses.add(InvalidationTest.class);
    testClasses.add(RollbackTest.class);
    testClasses.add(CrossReferenceTest.class);
    testClasses.add(ChunkingTest.class);
    testClasses.add(ChunkingClearCachedRevisionTest.class);
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
    testClasses.add(LockingSequenceTest.class);
    testClasses.add(MultiValuedOfAttributeTest.class);
    testClasses.add(MapTest.class);
    testClasses.add(AdapterManagerTest.class);
    testClasses.add(ConflictResolverTest.class);
    testClasses.add(ConflictResolverExtendedTest.class);
    testClasses.add(DynamicXSDTest.class);
    testClasses.add(SetFeatureTest.class);
    testClasses.add(DynamicPackageTest.class);
    testClasses.add(XRefTest.class);
    testClasses.add(StickyViewsTest.class);
    testClasses.add(LobTest.class);
    testClasses.add(EMFCompareTest.class);
    testClasses.add(OCLQueryTest.class);
    testClasses.add(OCLQueryTest.Lazy.class);
    testClasses.add(ViewProviderTest.class);
    testClasses.add(WorkspaceTest.class);
    testClasses.add(BackupTest.class);
    testClasses.add(BackupBinaryTest.class);
    testClasses.add(ResourceModificationTrackingTest.class);
    testClasses.add(CDOStaleReferencePolicyTest.class);

    // TODO testClasses.add(RemoteSessionManagerTest.class);
    // TODO testClasses.add(NonCDOResourceTest.class);
    // TODO testClasses.add(GeneratedEcoreTest.class);

    // Bugzilla verifications
    testClasses.addAll(getBugzillaTests());
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, MEM_BRANCHES, JVM, NATIVE);
  }
}
