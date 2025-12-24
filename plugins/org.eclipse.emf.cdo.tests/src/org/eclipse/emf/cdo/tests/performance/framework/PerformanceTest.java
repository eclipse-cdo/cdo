/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.tests.AbstractCDOTest;

/**
 * @author Stefan Winkler
 */
public abstract class PerformanceTest extends AbstractCDOTest
{
  private static final long NOT_PROBING = -1;

  private PerformanceTestSuite suite;

  private long probe;

  private long startTimeMillis;

  public PerformanceTest()
  {
  }

  void setSuite(PerformanceTestSuite suite)
  {
    this.suite = suite;
  }

  protected final void startProbing()
  {
    assertEquals("Already probing", NOT_PROBING, startTimeMillis);
    startTimeMillis = System.currentTimeMillis();

    if (probe == NOT_PROBING)
    {
      probe = 0L;
    }
  }

  protected final void stopProbing()
  {
    if (startTimeMillis == NOT_PROBING)
    {
      fail("Not probing");
    }

    long stopTimeMillis = System.currentTimeMillis();
    probe += stopTimeMillis - startTimeMillis;

    startTimeMillis = NOT_PROBING;
  }

  @Override
  public void runBare() throws Throwable
  {
    runBareBasic();

    int runsPerTestCase = suite.getRunsPerTestCase();
    PerformanceRecord performanceRecord = suite.createPerformanceRecord(getScenario(), getClass().getName(), getName(), runsPerTestCase);

    for (int i = 0; i < runsPerTestCase; i++)
    {
      runBareBasic();

      if (probe == NOT_PROBING)
      {
        fail("No probe");
      }

      performanceRecord.getProbes()[i] = probe;
    }
  }

  private void runBareBasic() throws Throwable
  {
    probe = NOT_PROBING;
    startTimeMillis = NOT_PROBING;
    super.runBare();
  }
}
