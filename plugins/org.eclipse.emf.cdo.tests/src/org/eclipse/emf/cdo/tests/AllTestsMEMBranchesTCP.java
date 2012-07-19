/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Caspar De Groot
 */
public class AllTestsMEMBranchesTCP extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsMEMBranchesTCP().getTestSuite(AllConfigs.class.getName());
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM_BRANCHES, TCP, NATIVE);
  }
}
