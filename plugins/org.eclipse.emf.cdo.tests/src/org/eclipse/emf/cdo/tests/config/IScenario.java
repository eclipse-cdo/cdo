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
package org.eclipse.emf.cdo.tests.config;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IScenario extends ITestLifecycle, Serializable
{
  public IContainerConfig getContainerConfig();

  public void setContainerConfig(IContainerConfig containerConfig);

  public IRepositoryConfig getRepositoryConfig();

  public void setRepositoryConfig(IRepositoryConfig repositoryConfig);

  public ISessionConfig getSessionConfig();

  public void setSessionConfig(ISessionConfig sessionConfig);

  public IModelConfig getModelConfig();

  public void setModelConfig(IModelConfig modelConfig);

  public Set<IConfig> getConfigs();

  public Set<String> getCapabilities();

  public boolean isValid();

  public void save();
}
