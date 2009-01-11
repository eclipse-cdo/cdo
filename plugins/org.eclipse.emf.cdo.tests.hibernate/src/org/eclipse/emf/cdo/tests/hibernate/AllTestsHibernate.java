/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsHibernate extends AllTestsAllConfigs
{
  public static final RepositoryConfig HIBERNATE = HibernateConfig.INSTANCE;

  public static Test suite()
  {
    return new AllTestsHibernate().getTestSuite("CDO Tests (Hibernate)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, HIBERNATE, TCP, NATIVE);
  }
}
