/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance.framework;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;

/**
 * @author Stefan Winkler
 */
public abstract class AbstractCDOPerformanceMeasurement extends AbstractCDOTest
{
  private long startTime;

  private IPerformanceResultCollector performanceResultCollector;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    /*
    * TODO Eike: is there a way to inject the result collector here
    *      by setting it in the test properties before the tests are executed?
    */
    Object collector = getTestProperties().get("performanceResultCollector");
    if (collector instanceof IPerformanceResultCollector)
    {
      performanceResultCollector = (IPerformanceResultCollector)collector;
    }
    else
    {
      performanceResultCollector = new PerformanceResultCollector();
    }
    startTime = System.currentTimeMillis();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    long time = System.currentTimeMillis() - startTime;

    // Output test data
    performanceResultCollector.addRecord(getScenario(), getClass().getName(), getName(), time);

    super.doTearDown();
  }
}
