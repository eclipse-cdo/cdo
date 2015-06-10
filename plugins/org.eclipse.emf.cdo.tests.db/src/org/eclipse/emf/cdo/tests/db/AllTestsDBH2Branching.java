/*
 * Copyright (c) 2010-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2Branching extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBH2Branching().getTestSuite();
  }

  public static void initConfigSuites(ConfigTestSuite suite, TestSuite parent,
      IDGenerationLocation idGenerationLocation)
  {
    // Without ranges
    suite.addScenario(parent, new H2Config(true, true, false, false, idGenerationLocation), JVM, NATIVE);

    // With ranges
    suite.addScenario(parent, new H2Config(true, true, true, false, idGenerationLocation), JVM, NATIVE);

    // With ranges and copy-on-branch
    // suite.addScenario(parent, new H2Config(true, true, true, true, idGenerationLocation), JVM, NATIVE);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    initConfigSuites(this, parent, IDGenerationLocation.STORE);
  }
}
