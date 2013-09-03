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

import org.eclipse.emf.cdo.dawn.tests.common.DawnCodeGenGMFFragmentTest;
import org.eclipse.emf.cdo.dawn.tests.common.DawnWrapperResourceTest;
import org.eclipse.emf.cdo.dawn.tests.common.GMFTest;
import org.eclipse.emf.cdo.dawn.tests.common.TestFrameworkTest;
import org.eclipse.emf.cdo.tests.AllTests;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This test suite should be executed as plain JUnit test.
 * 
 * @author Martin Fluegge
 */
public class AllTestsDawn extends AllTests
{
  public static Test suite()
  {
    return new AllTestsDawn().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, MEM, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(TestFrameworkTest.class);
    testClasses.add(GMFTest.class);
    testClasses.add(DawnWrapperResourceTest.class);
    testClasses.add(DawnCodeGenGMFFragmentTest.class);
  }
}
