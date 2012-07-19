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
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.tests.BranchingWithCacheClearTest;
import org.eclipse.emf.cdo.tests.InitialTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.PartialCommitTest;
import org.eclipse.emf.cdo.tests.RevisionDeltaCascadingBranchesTest;
import org.eclipse.emf.cdo.tests.RevisionDeltaInBranchTest;
import org.eclipse.emf.cdo.tests.UnsetTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class SampleTestsObjy extends ObjyDBConfigs
{
  public static Test suite()
  {
    return new SampleTestsObjy().getTestSuite("CDO Tests (DBStoreRepositoryConfig Objectivity/DB)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, new ObjyConfig(false, false), JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.clear();

    // testClasses.add(ComplexTest.class);
    testClasses.add(InitialTest.class);
    testClasses.add(UnsetTest.class);
    testClasses.add(BranchingWithCacheClearTest.class);
    testClasses.add(MergingTest.class);
    testClasses.add(PartialCommitTest.class);
    testClasses.add(RevisionDeltaInBranchTest.class);
    testClasses.add(RevisionDeltaCascadingBranchesTest.class);
    // testClasses.add(ExternalReferenceTest.class);

    // testClasses.add(FeatureMapTest.class);
    // testClasses.add(ComplexTest.class);
    // testClasses.add(AttributeTest.class);
    // testClasses.add(UnsetTest.class); // keep
    // testClasses.add(BranchingTest.class);
    // testClasses.add(BranchingSameSessionTest.class);
    // testClasses.add(MergingTest.class); // keep
    // testClasses.add(PushTransactionTest.class);
    // testClasses.add(CommitInfoTest.class); // keep (testLogThroughClient and some others fail).
    // testClasses.add(AuditTest.class);
    // testClasses.add(AuditSameSessionTest.class);
    // testClasses.add(ResourceTest.class); // keep
    // testClasses.add(InvalidationTest.class);
    // testClasses.add(ChunkingTest.class);
    // testClasses.add(ChunkingWithMEMTest.class);
    // testClasses.add(DetachTest.class);
    // testClasses.add(ExternalReferenceTest.class); // keep
    // // testClasses.add(XATransactionTest.class);
    // testClasses.add(RepositoryTest.class); // keep
    // testClasses.add(LockingManagerTest.class); // keep
    // testClasses.add(MultiValuedOfAttributeTest.class); // keep
    // testClasses.add(Bugzilla_248124_Test.class); // keep
    // testClasses.add(Bugzilla_258933_Test.class); // keep
    // testClasses.add(Bugzilla_259869_Test.class); // analyse?!! (long and hang for XA).
    // testClasses.add(Bugzilla_259949_Test.class);
    // testClasses.add(Bugzilla_272861_Test.class);
    // testClasses.add(Bugzilla_279982_Test.class);
    // testClasses.add(Bugzilla_298561_Test.class);
    // testClasses.add(Bugzilla_302233_Test.class);
    // testClasses.add(Bugzilla_303807_Test.class);
    // testClasses.add(Bugzilla_306998_Test.class);
    // testClasses.add(Bugzilla_308895_Test.class);
    // testClasses.add(Bugzilla_314264_Test.class);

    // testClasses.add(LockingManagerTest.class);
    // testClasses.add(MapTest.class);
    // testClasses.add(FeatureMapTest.class);
    // testClasses.add(AdapterManagerTest.class);
    // testClasses.add(ConflictResolverTest.class);
    // testClasses.add(DynamicXSDTest.class);
    // testClasses.add(SetFeatureTest.class);
    // testClasses.add(DynamicPackageTest.class);
    // testClasses.add(LegacyTest.class);
    // testClasses.add(Bugzilla_250757_Test.class);
    // testClasses.add(Bugzilla_252909_Test.class);
    // testClasses.add(Bugzilla_259949_Test.class);
    testClasses.add(Bugzilla_261218_Test.class);
  }
}
