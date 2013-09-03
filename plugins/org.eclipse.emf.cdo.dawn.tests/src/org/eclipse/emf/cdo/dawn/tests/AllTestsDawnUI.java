/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests;

import org.eclipse.emf.cdo.dawn.tests.ui.emf.DawnBasicEMFUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.DawnBasicGMFUITest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.DawnCreationWizardTest;
import org.eclipse.emf.cdo.tests.AllTests;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test suite should be executed as JUnit Plug-in test.
 * 
 * @author Martin Fluegge
 */
public class AllTestsDawnUI extends AllTests
{
  public static Test suite()
  {
    return new AllTestsDawnUI().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, MEM, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(DawnCreationWizardTest.class);
    testClasses.add(DawnBasicGMFUITest.class);
    testClasses.add(DawnBasicEMFUITest.class);
  }
}
