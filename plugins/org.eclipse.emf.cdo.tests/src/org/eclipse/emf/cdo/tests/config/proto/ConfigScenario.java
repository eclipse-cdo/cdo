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
package org.eclipse.emf.cdo.tests.config.proto;

import org.eclipse.emf.cdo.tests.config.Config;
import org.eclipse.emf.cdo.tests.config.ContainerConfig;
import org.eclipse.emf.cdo.tests.config.ModelConfig;
import org.eclipse.emf.cdo.tests.config.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.SessionConfig;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class ConfigScenario
{
  private ContainerConfig containerConfig;

  private RepositoryConfig repositoryConfig;

  private SessionConfig sessionConfig;

  private ModelConfig modelConfig;

  public ConfigScenario()
  {
  }

  public ContainerConfig getContainerConfig()
  {
    return containerConfig;
  }

  public void setContainerConfig(ContainerConfig containerConfig)
  {
    this.containerConfig = containerConfig;
  }

  public RepositoryConfig getRepositoryConfig()
  {
    return repositoryConfig;
  }

  public void setRepositoryConfig(RepositoryConfig repositoryConfig)
  {
    this.repositoryConfig = repositoryConfig;
  }

  public SessionConfig getSessionConfig()
  {
    return sessionConfig;
  }

  public void setSessionConfig(SessionConfig sessionConfig)
  {
    this.sessionConfig = sessionConfig;
  }

  public ModelConfig getModelConfig()
  {
    return modelConfig;
  }

  public void setModelConfig(ModelConfig modelConfig)
  {
    this.modelConfig = modelConfig;
  }

  public Config[] getConfigs()
  {
    return new Config[] { containerConfig, repositoryConfig, sessionConfig, modelConfig };
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("[{0}, {1}, {2}, {3}]", containerConfig, repositoryConfig, sessionConfig, modelConfig);
  }
}
