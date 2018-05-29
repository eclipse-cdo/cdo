/*
 * Copyright (c) 2009-2013, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBDerby extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBDerby().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, new DerbyConfig().supportingBranches(true), JVM, NATIVE);
    // addScenario(parent, AllTestsDBDerby.Derby.INSTANCE, TCP, NATIVE);
  }
}
