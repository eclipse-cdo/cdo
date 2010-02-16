/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTests().getTestSuite(AllTestsAllConfigs.class.getName());
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, JVM, NATIVE);
    addScenario(parent, COMBINED, MEM_AUDITS, JVM, NATIVE);
    addScenario(parent, COMBINED, MEM_BRANCHES, JVM, NATIVE);
  }
}
