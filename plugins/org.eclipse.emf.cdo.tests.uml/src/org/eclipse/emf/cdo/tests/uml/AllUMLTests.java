/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.uml;

import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests legacy-mode support for UML models and profiles.
 */
public class AllUMLTests extends ConfigTestSuite
{
  public static Test suite()
  {
    return new AllUMLTests().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, JVM, LEGACY);
    addScenario(parent, COMBINED, MEM_AUDITS, JVM, LEGACY);
    addScenario(parent, COMBINED, MEM_BRANCHES, JVM, LEGACY);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(LegacyDynamicPackageTest.class);
    testClasses.add(DynamicProfileTest.class);
  }
}
