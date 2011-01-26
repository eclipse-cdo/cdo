/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - 289360: [DB] [maintenance] Support FeatureMaps
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsObjyBranching extends ObjyDBConfigs
{
  public static Test suite()
  {
    return new AllTestsObjyBranching().getTestSuite("CDO Tests (ObjectivityStore - branching mode)"); //$NON-NLS-1$
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    ObjyStoreRepositoryConfig repoConfig = ObjyBranchingConfig.INSTANCE;
    addScenario(parent, COMBINED, repoConfig, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // // add branching tests for this testsuite
    // testClasses.add(BranchingTest.class);
    // testClasses.add(BranchingSameSessionTest.class);
    // testClasses.add(MergingTest.class);
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return true;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return true;
  }

  public static class ObjyBranchingConfig extends ObjyStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsObjyBranching.ObjyBranchingConfig INSTANCE = new ObjyBranchingConfig(
        "ObjectivityStore: (branching)"); //$NON-NLS-1$

    public ObjyBranchingConfig(String name)
    {
      super(name);

      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.DEBUG.setEnabled(true);
      org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.INFO.setEnabled(true);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true"); //$NON-NLS-1$
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "true"); //$NON-NLS-1$
    }
  }
}
