/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
    addScenario(parent, MEM, JVM, NATIVE);
    addScenario(parent, MEM_AUDITS, JVM, NATIVE);
    addScenario(parent, MEM_BRANCHES, JVM, NATIVE);
    addScenario(parent, MEM_BRANCHES_UUIDS, JVM, NATIVE);

    addScenario(parent, MEM, JVM, LEGACY);
    addScenario(parent, MEM_AUDITS, JVM, LEGACY);
    addScenario(parent, MEM_BRANCHES, JVM, LEGACY);

    addScenario(parent, MEM_BRANCHES, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.addAll(getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.performance"));
  }
}
