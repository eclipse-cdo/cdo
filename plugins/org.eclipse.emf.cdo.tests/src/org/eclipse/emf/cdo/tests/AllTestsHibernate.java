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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.config.ContainerConfig;
import org.eclipse.emf.cdo.tests.config.ModelConfig;
import org.eclipse.emf.cdo.tests.config.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.SessionConfig;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsHibernate extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsHibernate().getTestSuite("CDO Tests (Hibernate)");
  }

  @Override
  protected void initConfigSuite(TestSuite parent, ContainerConfig containerConfig, RepositoryConfig repositoryConfig,
      SessionConfig sessionConfig, ModelConfig modelConfig)
  {
    if (repositoryConfig == HIBERNATE)
    {
      super.initConfigSuite(parent, containerConfig, repositoryConfig, sessionConfig, modelConfig);
    }
  }
}
