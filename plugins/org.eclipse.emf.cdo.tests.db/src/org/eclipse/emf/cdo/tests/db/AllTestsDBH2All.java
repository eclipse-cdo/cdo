/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - introduced variable mapping strategies
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2All extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBH2All().getTestSuite("CDO Tests (DBStore H2 All)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenarios(parent, IDGenerationLocation.STORE);
    addScenarios(parent, IDGenerationLocation.CLIENT);
  }

  private void addScenarios(TestSuite parent, IDGenerationLocation idGenerationLocation)
  {
    // Non-audit
    addScenario(parent, COMBINED, new H2Config(false, false, false, false, idGenerationLocation), JVM, NATIVE);

    // Audit
    addScenario(parent, COMBINED, new H2Config(true, false, false, false, idGenerationLocation), JVM, NATIVE);
    addScenario(parent, COMBINED, new H2Config(true, false, true, false, idGenerationLocation), JVM, NATIVE);

    // Branching
    addScenario(parent, COMBINED, new H2Config(true, true, false, false, idGenerationLocation), JVM, NATIVE);
    addScenario(parent, COMBINED, new H2Config(true, true, true, false, idGenerationLocation), JVM, NATIVE);
    addScenario(parent, COMBINED, new H2Config(true, true, true, true, idGenerationLocation), JVM, NATIVE);
  }
}
