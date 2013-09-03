/*
 * Copyright (c) 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IScenario extends ITestLifecycle, Serializable
{
  public IRepositoryConfig getRepositoryConfig();

  public IScenario setRepositoryConfig(IRepositoryConfig repositoryConfig);

  public ISessionConfig getSessionConfig();

  public IScenario setSessionConfig(ISessionConfig sessionConfig);

  public IModelConfig getModelConfig();

  public IScenario setModelConfig(IModelConfig modelConfig);

  public Set<IConfig> getConfigs();

  public Set<String> getCapabilities();

  public boolean isValid();

  public void save();
}
