/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.performance.framework.PerformanceTestSuite;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AllPerformanceTests extends PerformanceTestSuite
{
  public AllPerformanceTests()
  {
  }

  public AllPerformanceTests(int runsPerTestCase)
  {
    super(runsPerTestCase);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.addAll(getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.performance"));
  }
}
