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
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IScenario;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTestSuite implements IConstants
{
  public ConfigTestSuite()
  {
  }

  protected Test getTestSuite(String name)
  {
    TestSuite suite = new TestSuite(name);
    initConfigSuites(suite);
    return suite;
  }

  // protected void initConfigSuites(TestSuite parent)
  // {
  // for (ContainerConfig containerConfig : ContainerConfig.CONFIGS)
  // {
  // for (RepositoryConfig repositoryConfig : RepositoryConfig.CONFIGS)
  // {
  // for (SessionConfig sessionConfig : SessionConfig.CONFIGS)
  // {
  // for (ModelConfig modelConfig : ModelConfig.CONFIGS)
  // {
  // initConfigSuite(parent, containerConfig, repositoryConfig, sessionConfig, modelConfig);
  // }
  // }
  // }
  // }
  // }

  protected void addScenario(TestSuite parent, ContainerConfig containerConfig, RepositoryConfig repositoryConfig,
      SessionConfig sessionConfig, ModelConfig modelConfig)
  {
    IScenario scenario = new Scenario();
    scenario.setContainerConfig(containerConfig);
    scenario.setRepositoryConfig(repositoryConfig);
    scenario.setSessionConfig(sessionConfig);
    scenario.setModelConfig(modelConfig);

    if (scenario.isValid())
    {
      TestSuite suite = new TestSuite(scenario.toString());

      List<Class<? extends ConfigTest>> testClasses = new ArrayList<Class<? extends ConfigTest>>();
      initTestClasses(testClasses, scenario);

      for (Class<? extends ConfigTest> testClass : testClasses)
      {
        TestWrapper configSuite = new TestWrapper(testClass, scenario);
        suite.addTest(configSuite);
      }

      parent.addTest(suite);
    }
  }

  protected abstract void initConfigSuites(TestSuite parent);

  // protected void initConfigSuites(TestSuite parent)
  // {
  // for (ContainerConfig containerConfig : ContainerConfig.CONFIGS)
  // {
  // for (RepositoryConfig repositoryConfig : RepositoryConfig.CONFIGS)
  // {
  // for (SessionConfig sessionConfig : SessionConfig.CONFIGS)
  // {
  // for (ModelConfig modelConfig : ModelConfig.CONFIGS)
  // {
  // initConfigSuite(parent, containerConfig, repositoryConfig, sessionConfig, modelConfig);
  // }
  // }
  // }
  // }
  // }

  protected abstract void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario);

  /**
   * @author Eike Stepper
   */
  private static final class TestWrapper extends TestSuite
  {
    private IScenario scenario;

    public TestWrapper(Class<? extends ConfigTest> testClass, IScenario scenario)
    {
      super(testClass, testClass.getName()); // Important for the UI to set the *qualified* class name!
      this.scenario = scenario;
    }

    @Override
    public void runTest(Test test, TestResult result)
    {
      if (test instanceof ConfigTest)
      {
        scenario.save();
        ConfigTest configTest = (ConfigTest)test;
        configTest.setScenario(scenario);
        if (configTest.isValid())
        {
          super.runTest(configTest, result);
        }
      }
      else
      {
        super.runTest(test, result);
      }
    }
  }
}
