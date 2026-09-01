/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class AllTestsDBH2BranchingDIAG extends DBConfigs
{
  static
  {
    // Enables exhaustive diagnostic output in CDO.
    System.setProperty("cdo.diagnostic", "true");
  }

  public static Test suite()
  {
    return new AllTestsDBH2BranchingDIAG().getTestSuite();
  }

  public static void initConfigSuites(ConfigTestSuite suite, TestSuite parent, IDGenerationLocation idGenerationLocation)
  {
    suite.addScenario(parent, //
        new H2Config() //
            .supportingBranches(true) //
            .idGenerationLocation(idGenerationLocation) //
            .withRanges(true), //
        JVM, NATIVE);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    initConfigSuites(this, parent, IDGenerationLocation.STORE);
  }
}
