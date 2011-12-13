/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance.framework;

import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

/**
 * @author Eike Stepper
 */
public abstract class PerformanceTestSuite extends ConfigTestSuite
{
  private IProbeCollector probeCollector = createProbeCollector();

  @Override
  protected void prepareTest(ConfigTest configTest)
  {
    super.prepareTest(configTest);
    if (configTest instanceof PerformanceTest)
    {
      PerformanceTest performanceTest = (PerformanceTest)configTest;
      performanceTest.setProbeCollector(probeCollector);
    }
  }

  /**
   * Can be overridden by subclasses.
   */
  protected IProbeCollector createProbeCollector()
  {
    return new PrintStreamProbeCollector();
  }
}
