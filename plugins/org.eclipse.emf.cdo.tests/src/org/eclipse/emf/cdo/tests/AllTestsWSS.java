/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
public class AllTestsWSS extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsWSS().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    SessionConfig sessionConfig = WSS;

    addScenario(parent, MEM, sessionConfig, NATIVE);
    addScenario(parent, MEM_AUDITS, sessionConfig, NATIVE);
    addScenario(parent, MEM_BRANCHES, sessionConfig, NATIVE);
    addScenario(parent, MEM_BRANCHES_UUIDS, sessionConfig, NATIVE);

    addScenario(parent, MEM, sessionConfig, LEGACY);
    addScenario(parent, MEM_AUDITS, sessionConfig, LEGACY);
    addScenario(parent, MEM_BRANCHES, sessionConfig, LEGACY);
  }
}
