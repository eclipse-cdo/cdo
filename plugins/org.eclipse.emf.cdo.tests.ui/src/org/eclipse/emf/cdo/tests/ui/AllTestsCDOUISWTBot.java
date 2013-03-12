/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.ui;

import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test suite should be executed as SWTBot test.
 *
 * @author Martin Fluegge
 */
public class AllTestsCDOUISWTBot extends ConfigTestSuite
{
  public static Test suite()
  {
    return new AllTestsCDOUISWTBot().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, TCP, NATIVE);
    addScenario(parent, COMBINED, MEM_BRANCHES, TCP, NATIVE);
    addScenario(parent, COMBINED, MEM_BRANCHES, TCP, LEGACY);
    addScenario(parent, COMBINED, MEM_BRANCHES, TCP, LEGACY);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(CDOSessionsViewTest.class);
  }
}
