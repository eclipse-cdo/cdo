/*
 * Copyright (c) 2006-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsWS extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsWS().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    SessionConfig sessionConfig = WS;

    addScenario(parent, MEM, sessionConfig, NATIVE);
    addScenario(parent, MEM_AUDITS, sessionConfig, NATIVE);
    addScenario(parent, MEM_BRANCHES, sessionConfig, NATIVE);
    addScenario(parent, MEM_BRANCHES_UUIDS, sessionConfig, NATIVE);

    addScenario(parent, MEM, sessionConfig, LEGACY);
    addScenario(parent, MEM_AUDITS, sessionConfig, LEGACY);
    addScenario(parent, MEM_BRANCHES, sessionConfig, LEGACY);
  }
}
