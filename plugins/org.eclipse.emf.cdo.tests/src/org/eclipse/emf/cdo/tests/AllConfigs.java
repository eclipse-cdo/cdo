/*
 * Copyright (c) 2010-2016, 2019-2022, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

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

  public List<Class<? extends ConfigTest>> getIssueTests()
  {
    return getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.issues");
  }

  /**
   * Discovers the general config tests stored in the general test package. The returned classes are sorted by fully
   * qualified name by the inherited {@link ConfigTestSuite#getTestClasses} discovery helper.
   *
   * @return the config-test classes in {@code org.eclipse.emf.cdo.tests.general}
   */
  public List<Class<? extends ConfigTest>> getGeneralTests()
  {
    return getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.general");
  }

  /**
   * Discovers plain config tests stored in the plain test package. Package membership is a storage convention; plain
   * execution is determined by {@link PlainTest} inheritance.
   *
   * @return the config-test classes in {@code org.eclipse.emf.cdo.tests.plain}
   */
  public List<Class<? extends ConfigTest>> getPlainTests()
  {
    return getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.plain");
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.addAll(getPlainTests());
    testClasses.addAll(getGeneralTests());
    testClasses.addAll(getBugzillaTests());
    testClasses.addAll(getIssueTests());
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, MEM_BRANCHES, JVM, NATIVE);
  }
}
