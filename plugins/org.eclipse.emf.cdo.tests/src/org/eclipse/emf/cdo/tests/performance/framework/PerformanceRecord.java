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
package org.eclipse.emf.cdo.tests.performance.framework;

import org.eclipse.emf.cdo.tests.config.IScenario;

/**
 * @author Eike Stepper
 */
public class PerformanceRecord
{
  private final IScenario scenario;

  private final String testName;

  private final String testCaseName;

  private final long[] probes;

  public PerformanceRecord(IScenario scenario, String testName, String testCaseName, int runs)
  {
    this.scenario = scenario;
    this.testName = testName;
    this.testCaseName = testCaseName;
    probes = new long[runs];
  }

  public IScenario getScenario()
  {
    return scenario;
  }

  public String getTestName()
  {
    return testName;
  }

  public String getTestCaseName()
  {
    return testCaseName;
  }

  public long[] getProbes()
  {
    return probes;
  }

  public long getDurationSum()
  {
    long durationSum = 0L;
    for (long probe : probes)
    {
      durationSum += probe;
    }

    return durationSum;
  }

  public long getDurationAvg()
  {
    return getDurationSum() / probes.length;
  }

  public long getDurationMin()
  {
    long durationMin = Long.MAX_VALUE;
    for (long probe : probes)
    {
      durationMin = Math.min(durationMin, probe);
    }
    return durationMin;
  }

  public long getDurationMax()
  {
    long durationMax = Long.MIN_VALUE;
    for (long probe : probes)
    {
      durationMax = Math.max(durationMax, probe);
    }
    return durationMax;
  }
}
