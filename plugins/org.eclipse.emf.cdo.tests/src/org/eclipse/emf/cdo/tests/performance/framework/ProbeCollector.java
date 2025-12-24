/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance.framework;

import org.eclipse.emf.cdo.tests.config.IScenario;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Winkler
 */
public class ProbeCollector
{
  private final List<PerformanceRecord> performanceRecords = new ArrayList<>();

  protected PerformanceRecord createPerformanceRecord(IScenario scenario, String testName, String testCaseName, int runs)
  {
    PerformanceRecord performanceRecord = new PerformanceRecord(scenario, testName, testCaseName, runs);
    performanceRecords.add(performanceRecord);
    return performanceRecord;
  }

  /**
   * @author Eike Stepper
   */
  public static class PerformanceRecord
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
  }
}
