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
package org.eclipse.emf.cdo.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsMEMBranchesSSL extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsMEMBranchesSSL().getTestSuite(AllConfigs.class.getName());
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    // addScenario(parent, COMBINED, MEM, JVM, NATIVE);
    // addScenario(parent, COMBINED, MEM_AUDITS, JVM, NATIVE);
    addScenario(parent, SEPARATED, MEM_BRANCHES, SSL, NATIVE);
  }
}
