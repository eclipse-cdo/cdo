/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.tests.BranchingSameSessionTest;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Simon McDuff
 */
public class AllTestsDBMysql extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBMysql().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, new MysqlConfig(false, false, IDGenerationLocation.STORE), JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    // add branching tests for this testsuite
    testClasses.add(BranchingTest.class);
    testClasses.add(BranchingSameSessionTest.class);
    testClasses.add(MergingTest.class);

    super.initTestClasses(testClasses, scenario);
  }
}
