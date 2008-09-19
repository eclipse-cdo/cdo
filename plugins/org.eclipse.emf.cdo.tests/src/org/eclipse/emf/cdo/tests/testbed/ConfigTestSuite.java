/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.testbed;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTestSuite
{
  // public static final String[] DIMENSIONS = { //
  // ContainerConfig.DIMENSION, //
  // RepositoryConfig.DIMENSION, //
  // SessionConfig.DIMENSION, //
  // ModelConfig.DIMENSION };

  public static final Config[][] CONFIGS = { //
  ContainerConfig.CONFIGS, //
      RepositoryConfig.CONFIGS, //
      SessionConfig.CONFIGS, //
      ModelConfig.CONFIGS };

  public ConfigTestSuite()
  {
  }

  protected Test getTestSuite(String name)
  {
    TestSuite suite = new TestSuite(name);
    initConfigSuites(suite);
    return suite;
  }

  private void initConfigSuites(TestSuite parent)
  {
    for (ContainerConfig containerConfig : ContainerConfig.CONFIGS)
    {
      for (RepositoryConfig repositoryConfig : RepositoryConfig.CONFIGS)
      {
        for (SessionConfig sessionConfig : SessionConfig.CONFIGS)
        {
          for (ModelConfig modelConfig : ModelConfig.CONFIGS)
          {
            initConfigSuite(parent, containerConfig, repositoryConfig, sessionConfig, modelConfig);
          }
        }
      }
    }
  }

  private void initConfigSuite(TestSuite parent, ContainerConfig containerConfig, RepositoryConfig repositoryConfig,
      SessionConfig sessionConfig, ModelConfig modelConfig)
  {
    Set<Config> configs = new HashSet<Config>();
    configs.add(containerConfig);
    configs.add(repositoryConfig);
    configs.add(sessionConfig);
    configs.add(modelConfig);

    if (containerConfig.isValid(configs) && repositoryConfig.isValid(configs) && sessionConfig.isValid(configs)
        && modelConfig.isValid(configs))
    {
      String name = MessageFormat.format("Config = [{0}, {1}, {2}, {3}]", containerConfig, repositoryConfig,
          sessionConfig, modelConfig);
      TestSuite suite = new TestSuite(name);

      List<Class<? extends ConfigTest>> testClasses = new ArrayList<Class<? extends ConfigTest>>();
      initTestClasses(testClasses);

      for (Class<? extends ConfigTest> testClass : testClasses)
      {
        ConfigSuite configSuite = new ConfigSuite(testClass, containerConfig, repositoryConfig, sessionConfig,
            modelConfig);
        suite.addTest(configSuite);
      }

      parent.addTest(suite);
    }
  }

  protected abstract void initTestClasses(List<Class<? extends ConfigTest>> testClasses);

  /**
   * @author Eike Stepper
   */
  private static final class ConfigSuite extends TestSuite
  {
    private ContainerConfig containerConfig;

    private RepositoryConfig repositoryConfig;

    private SessionConfig sessionConfig;

    private ModelConfig modelConfig;

    public ConfigSuite(Class<? extends ConfigTest> testClass, ContainerConfig containerConfig,
        RepositoryConfig repositoryConfig, SessionConfig sessionConfig, ModelConfig modelConfig)
    {
      super(testClass, testClass.getSimpleName());
      this.containerConfig = containerConfig;
      this.repositoryConfig = repositoryConfig;
      this.sessionConfig = sessionConfig;
      this.modelConfig = modelConfig;
    }

    public ContainerConfig getContainerConfig()
    {
      return containerConfig;
    }

    public RepositoryConfig getRepositoryConfig()
    {
      return repositoryConfig;
    }

    public SessionConfig getSessionConfig()
    {
      return sessionConfig;
    }

    public ModelConfig getModelConfig()
    {
      return modelConfig;
    }

    @Override
    public void runTest(Test test, TestResult result)
    {
      ConfigTest configTest = (ConfigTest)test;
      configTest.setContainerConfig(containerConfig);
      configTest.setRepositoryConfig(repositoryConfig);
      configTest.setSessionConfig(sessionConfig);
      configTest.setModelConfig(modelConfig);
      if (configTest.isValid())
      {
        super.runTest(configTest, result);
      }
    }
  }
}
