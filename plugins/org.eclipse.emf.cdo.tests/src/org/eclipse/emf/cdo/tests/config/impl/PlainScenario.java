/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;

import java.util.Collections;
import java.util.Set;

/**
 * Synthetic scenario for tests that do not use repository, session, or model configuration.
 *
 * @author Eike Stepper
 */
final class PlainScenario implements IScenario
{
  private static final long serialVersionUID = 1L;

  public PlainScenario()
  {
  }

  @Override
  public IRepositoryConfig getRepositoryConfig()
  {
    return null;
  }

  @Override
  public IScenario setRepositoryConfig(IRepositoryConfig repositoryConfig)
  {
    return this;
  }

  @Override
  public ISessionConfig getSessionConfig()
  {
    return null;
  }

  @Override
  public IScenario setSessionConfig(ISessionConfig sessionConfig)
  {
    return this;
  }

  @Override
  public IModelConfig getModelConfig()
  {
    return null;
  }

  @Override
  public IScenario setModelConfig(IModelConfig modelConfig)
  {
    return this;
  }

  @Override
  public Set<IConfig> getConfigs()
  {
    return Collections.emptySet();
  }

  @Override
  public Set<String> getCapabilities()
  {
    return Collections.emptySet();
  }

  @Override
  public boolean isValid()
  {
    return true;
  }

  @Override
  public boolean alwaysCleanRepositories()
  {
    return false;
  }

  @Override
  public void save()
  {
  }

  @Override
  public ConfigTest getCurrentTest()
  {
    return null;
  }

  @Override
  public void setCurrentTest(ConfigTest currentTest)
  {
  }

  @Override
  public void setUp()
  {
  }

  @Override
  public void tearDown()
  {
  }

  @Override
  public void mainSuiteFinished()
  {
  }

  @Override
  public String toString()
  {
    return "Scenario[PLAIN]";
  }
}
