/*
 * Copyright (c) 2010-2013, 2018, 2023 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.om.OMPlatform;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2All extends DBConfigs
{
  private static final boolean ALL_SCENARIOS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.tests.db.AllTestsDBH2All.ALL_SCENARIOS");

  public static Test suite()
  {
    return new AllTestsDBH2All().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    // OMPlatform.INSTANCE.setDebugging(true);
    // org.eclipse.emf.cdo.server.internal.db.bundle.OM.BUNDLE.getDebugSupport().setDebugging(true);
    // org.eclipse.emf.cdo.server.internal.db.bundle.OM.DEBUG.setEnabled(true);
    // org.eclipse.emf.cdo.server.internal.db.bundle.OM.DEBUG_UNITS.setEnabled(true);

    addScenario(parent, new H2Config(), JVM, NATIVE);

    if (ALL_SCENARIOS)
    {
      addScenario(parent, new H2Config().idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
      addScenario(parent, new H2Config().supportingAudits(true), JVM, NATIVE);
      addScenario(parent, new H2Config().supportingAudits(true).idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
    }

    addScenario(parent, new H2Config().supportingAudits(true).withRanges(true), JVM, NATIVE);

    if (ALL_SCENARIOS)
    {
      addScenario(parent, new H2Config().supportingAudits(true).withRanges(true).idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
      addScenario(parent, new H2Config().supportingBranches(true), JVM, NATIVE);
      addScenario(parent, new H2Config().supportingBranches(true).idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
    }

    addScenario(parent, new H2Config().supportingBranches(true).withRanges(true), JVM, NATIVE);
    addScenario(parent, new H2Config().supportingBranches(true).withRanges(true).idGenerationLocation(IDGenerationLocation.CLIENT), JVM, NATIVE);
  }
}
