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
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditTestSameSession;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingTestSameSession;
import org.eclipse.emf.cdo.tests.ChunkingTest;
import org.eclipse.emf.cdo.tests.ChunkingWithMEMTest;
import org.eclipse.emf.cdo.tests.CommitInfoTest;
import org.eclipse.emf.cdo.tests.ComplexTest;
import org.eclipse.emf.cdo.tests.DetachTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.InvalidationTest;
import org.eclipse.emf.cdo.tests.LockingManagerTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;
import org.eclipse.emf.cdo.tests.PushTransactionTest;
import org.eclipse.emf.cdo.tests.RepositoryTest;
import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.UnsetTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_248124_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259949_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_272861_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_279982_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_298561_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_302233_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_303807_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_306998_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_308895_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_314264_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;
import java.util.Map;

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
    ObjyStoreRepositoryConfig repConfig = ObjySampleConfig.INSTANCE;
    addScenario(parent, COMBINED, repConfig, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    // super.initTestClasses(testClasses);
    // testClasses.remove(XATransactionTest.class);

    testClasses.clear();

    testClasses.add(ComplexTest.class);
    // testClasses.add(AttributeTest.class);
    testClasses.add(UnsetTest.class);
    testClasses.add(BranchingTest.class);
    testClasses.add(BranchingTestSameSession.class);
    testClasses.add(MergingTest.class);
    testClasses.add(PushTransactionTest.class);
    testClasses.add(CommitInfoTest.class);
    testClasses.add(AuditTest.class);
    testClasses.add(AuditTestSameSession.class);
    testClasses.add(ResourceTest.class);
    testClasses.add(InvalidationTest.class);
    testClasses.add(ChunkingTest.class);
    testClasses.add(ChunkingWithMEMTest.class);
    testClasses.add(DetachTest.class);
    testClasses.add(ExternalReferenceTest.class);
    // testClasses.add(XATransactionTest.class);
    testClasses.add(RepositoryTest.class);
    testClasses.add(LockingManagerTest.class);
    testClasses.add(MultiValuedOfAttributeTest.class);
    testClasses.add(Bugzilla_248124_Test.class);
    testClasses.add(Bugzilla_258933_Test.class);
    testClasses.add(Bugzilla_259869_Test.class);
    testClasses.add(Bugzilla_259949_Test.class);
    testClasses.add(Bugzilla_272861_Test.class);
    testClasses.add(Bugzilla_279982_Test.class);
    testClasses.add(Bugzilla_298561_Test.class);
    testClasses.add(Bugzilla_302233_Test.class);
    testClasses.add(Bugzilla_303807_Test.class);
    testClasses.add(Bugzilla_306998_Test.class);
    testClasses.add(Bugzilla_308895_Test.class);
    testClasses.add(Bugzilla_314264_Test.class);

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
    // testClasses.add(Bugzilla_261218_Test.class);
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return true;
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return true;
  }

  public static class ObjySampleConfig extends ObjyStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final SampleTestsObjy.ObjySampleConfig INSTANCE = new ObjySampleConfig("ObjectivityStore: (sample)"); //$NON-NLS-1$

    public ObjySampleConfig(String name)
    {
      super(name);

      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.DEBUG.setEnabled(true);
      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.INFO.setEnabled(true);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    }
  }

}
