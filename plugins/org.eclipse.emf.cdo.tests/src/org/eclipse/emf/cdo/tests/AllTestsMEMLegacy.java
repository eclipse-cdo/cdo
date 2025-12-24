/*
 * Copyright (c) 2010-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Martin Fluegge
 */
public class AllTestsMEMLegacy extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsMEMLegacy().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, MEM, JVM, LEGACY);
    addScenario(parent, MEM_AUDITS, JVM, LEGACY);
    addScenario(parent, MEM_BRANCHES, JVM, LEGACY);
  }
}
