/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;
import org.eclipse.emf.cdo.tests.general.LockStateCacheTest;
import org.eclipse.emf.cdo.tests.general.OCLQueryTest;

import java.util.Enumeration;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the scenario routing and discovery used by {@link AllConfigs}.
 *
 * @author Eike Stepper
 */
public class PlainTestSuiteTest extends PlainTest
{
  public void testPlainTestsRunOnceUnderPlainScenario()
  {
    AllConfigs allConfigs = new AllConfigs();
    TestSuite suite = (TestSuite)allConfigs.getTestSuite();
    Counts counts = new Counts();
    collect(suite, null, counts);

    assertFalse(allConfigs.getGeneralTests().isEmpty());
    assertTrue(allConfigs.getGeneralTests().contains(LockStateCacheTest.WithSubBranch.class));
    assertTrue(allConfigs.getGeneralTests().contains(OCLQueryTest.Lazy.class));
    assertFalse(allConfigs.getBugzillaTests().isEmpty());
    assertFalse(allConfigs.getIssueTests().isEmpty());
    assertEquals(1, counts.plainScenarios);
    assertEquals(51, counts.plainCases);
    assertEquals(6, counts.modelElementRefCases);
    assertEquals(1, counts.nonCDOResourceCases);
    assertEquals(0, counts.unregisteredPlainCases);
    assertTrue(counts.generalCasesInRealScenario > 0);
  }

  public void testRegisteredPlainTestOutsidePlainPackageRoutesAsPlain()
  {
    RoutingSuite routingSuite = new RoutingSuite();
    TestSuite suite = (TestSuite)routingSuite.getTestSuite();
    Counts counts = new Counts();
    collect(suite, null, counts);

    assertEquals(1, counts.plainScenarios);
    assertEquals(1, counts.registeredOutsidePlainPackageCases);
    assertEquals(1, counts.ordinaryCasesInRealScenario);
    assertEquals(0, counts.unregisteredPlainCases);
  }

  private static void collect(Test test, String scenarioName, Counts counts)
  {
    if (test instanceof TestSuite)
    {
      TestSuite suite = (TestSuite)test;
      String name = suite.getName();
      if ("Scenario[PLAIN]".equals(name))
      {
        counts.plainScenarios++;
        scenarioName = name;
      }
      else if (name != null && name.startsWith("Scenario["))
      {
        scenarioName = name;
      }

      Enumeration<?> tests = suite.tests();
      while (tests.hasMoreElements())
      {
        collect((Test)tests.nextElement(), scenarioName, counts);
      }
    }
    else if (test instanceof ConfigTest)
    {
      Class<?> testClass = test.getClass();
      if (PlainTest.class.isAssignableFrom(testClass))
      {
        assertEquals("PlainTest must only run under Scenario[PLAIN]", "Scenario[PLAIN]", scenarioName);
        counts.plainCases++;
      }
      else
      {
        assertFalse("Ordinary ConfigTest must not run under Scenario[PLAIN]", "Scenario[PLAIN]".equals(scenarioName));
      }

      if (testClass == ModelElementRefTest.class)
      {
        counts.modelElementRefCases++;
      }
      else if (testClass == NonCDOResourceTest.class)
      {
        counts.nonCDOResourceCases++;
      }
      else if (testClass == PlainTestSuiteTestFixtures.RegisteredOutsidePlainPackageTest.class)
      {
        assertEquals("Scenario[PLAIN]", scenarioName);
        counts.registeredOutsidePlainPackageCases++;
      }
      else if (testClass == PlainTestSuiteTestFixtures.UnregisteredPlainTest.class)
      {
        counts.unregisteredPlainCases++;
      }
      else if (testClass == PlainTestSuiteTestFixtures.OrdinaryConfigTest.class)
      {
        assertFalse("Ordinary config test must use a real scenario", "Scenario[PLAIN]".equals(scenarioName));
        counts.ordinaryCasesInRealScenario++;
      }
      else if (testClass.getName().equals("org.eclipse.emf.cdo.tests.general.AdapterManagerTest") && !"Scenario[PLAIN]".equals(scenarioName))
      {
        counts.generalCasesInRealScenario++;
      }
    }
  }

  private static final class RoutingSuite extends ConfigTestSuite
  {
    @Override
    protected void initConfigSuites(TestSuite parent)
    {
      addScenario(parent, MEM_BRANCHES, JVM, NATIVE);
    }

    @Override
    protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
    {
      testClasses.add(PlainTestSuiteTestFixtures.RegisteredOutsidePlainPackageTest.class);
      testClasses.add(PlainTestSuiteTestFixtures.OrdinaryConfigTest.class);
    }
  }

  private static final class Counts
  {
    int plainScenarios;

    int plainCases;

    int modelElementRefCases;

    int nonCDOResourceCases;

    int registeredOutsidePlainPackageCases;

    int unregisteredPlainCases;

    int ordinaryCasesInRealScenario;

    int generalCasesInRealScenario;
  }
}
