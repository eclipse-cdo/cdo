/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsLegacy extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsLegacy().getTestSuite(AllTestsAllConfigs.class.getName());
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    /**
     * TODO Martin: currently there is only one test cases for Legacy models. All the other testcases must get become
     * aware of CDOLegacyWrapper and the other testcases must be converted.
     */
    testClasses.add(MangoTest.class);
    // testClasses.add(InvalidationTest.class);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, JVM, LEGACY);
  }
}
