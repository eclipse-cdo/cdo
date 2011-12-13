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
public abstract class PerformanceTest extends AbstractCDOTest
{
  private IProbeCollector probeCollector;

  private long startTimeMillis;

  public PerformanceTest()
  {
  }

  void setProbeCollector(IProbeCollector probeCollector)
  {
    this.probeCollector = probeCollector;
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    startTimeMillis = System.currentTimeMillis();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    long stopTimeMillis = System.currentTimeMillis();
    long probe = stopTimeMillis - startTimeMillis;

    // Output test data
    probeCollector.addProbe(getScenario(), getClass().getName(), getName(), probe);

    super.doTearDown();
  }
}
