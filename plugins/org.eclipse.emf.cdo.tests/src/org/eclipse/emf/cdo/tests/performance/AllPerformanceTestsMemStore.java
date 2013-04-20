/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance;

import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllPerformanceTestsMemStore extends AllPerformanceTests
{
  public static Test suite()
  {
    return new AllPerformanceTestsMemStore().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, JVM, NATIVE);
    addScenario(parent, COMBINED, MEM_AUDITS, JVM, NATIVE);
    addScenario(parent, COMBINED, MEM_BRANCHES, JVM, NATIVE);
    addScenario(parent, COMBINED, MEM_BRANCHES_UUIDS, JVM, NATIVE);

    addScenario(parent, COMBINED, MEM, JVM, LEGACY);
    addScenario(parent, COMBINED, MEM_AUDITS, JVM, LEGACY);
    addScenario(parent, COMBINED, MEM_BRANCHES, JVM, LEGACY);

    addScenario(parent, COMBINED, MEM_BRANCHES, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.addAll(getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.performance"));
  }
}
