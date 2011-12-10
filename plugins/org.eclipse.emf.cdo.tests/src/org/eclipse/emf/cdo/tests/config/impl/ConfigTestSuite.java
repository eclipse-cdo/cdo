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

import org.eclipse.net4j.internal.util.bundle.AbstractBundle;
import org.eclipse.net4j.util.om.OMBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
      initTestClasses(testClasses);

      for (Class<? extends ConfigTest> testClass : testClasses)
      {
        TestWrapper configSuite = new TestWrapper(testClass, scenario);
        suite.addTest(configSuite);
      }

      parent.addTest(suite);
    }
  }

  protected List<Class<? extends ConfigTest>> getTestClasses(OMBundle bundle, String packageName)
  {
    List<Class<? extends ConfigTest>> result = new ArrayList<Class<? extends ConfigTest>>();

    for (Iterator<Class<?>> it = ((AbstractBundle)bundle).getClasses(); it.hasNext();)
    {
      Class<?> c = it.next();
      if (ConfigTest.class.isAssignableFrom(c) && c.getName().startsWith(packageName))
      {
        @SuppressWarnings("unchecked")
        Class<? extends ConfigTest> configTest = (Class<? extends ConfigTest>)c;
        result.add(configTest);
      }
    }

    Collections.sort(result, new Comparator<Class<? extends ConfigTest>>()
    {
      public int compare(Class<? extends ConfigTest> c1, Class<? extends ConfigTest> c2)
      {
        return c1.getName().compareTo(c2.getName());
      }
    });

    return result;
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

  protected abstract void initTestClasses(List<Class<? extends ConfigTest>> testClasses);

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
