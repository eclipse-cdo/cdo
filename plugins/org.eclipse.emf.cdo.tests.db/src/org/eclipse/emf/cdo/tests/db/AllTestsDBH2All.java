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
    addScenario(parent, COMBINED, AllTestsDBH2NonAudit.H2NonAudit.ReusableFolder.INSTANCE, JVM, NATIVE);
    addScenario(parent, COMBINED, AllTestsDBH2.H2.ReusableFolder.RANGE_INSTANCE, JVM, NATIVE);
    addScenario(parent, COMBINED, AllTestsDBH2Branching.H2Branching.ReusableFolder.INSTANCE, JVM, NATIVE);
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return true;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return false;
  }
}
