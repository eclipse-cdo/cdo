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
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBDerby extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBDerby().getTestSuite("CDO Tests (DBStore Derby Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, new DerbyConfig(true, true, IDGenerationLocation.STORE), JVM, NATIVE);
    // addScenario(parent, COMBINED, AllTestsDBDerby.Derby.INSTANCE, TCP, NATIVE);
  }
}
