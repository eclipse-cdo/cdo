/*
 * Copyright (c) 2009-2013, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
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
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    // add branching tests for this testsuite
    testClasses.add(BranchingTest.class);
    testClasses.add(BranchingSameSessionTest.class);
    testClasses.add(MergingTest.class);

    super.initTestClasses(testClasses, scenario);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, new MysqlConfig(), JVM, NATIVE);
    addScenario(parent, new MysqlConfig().idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);

    addScenario(parent, new MysqlConfig().supportingAudits(true).withRanges(true), JVM, NATIVE);
    addScenario(parent, new MysqlConfig().supportingAudits(true).withRanges(true).idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);

    addScenario(parent, new MysqlConfig().supportingBranches(true).withRanges(true), JVM, NATIVE);
    addScenario(parent, new MysqlConfig().supportingBranches(true).withRanges(true).idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
  }
}
