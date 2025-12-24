/*
 * Copyright (c) 2011-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.performance.framework;

import org.eclipse.emf.cdo.tests.config.IScenario;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.List;

/**
 * A performance record analyzer that prints directly to a {@link PrintStream}, such as {@link System#out}.
 *
 * @author Stefan Winkler
 */
public class PrintStreamPerformanceRecordAnalyzer implements IPerformanceRecordAnalyzer
{
  private PrintStream out;

  public PrintStreamPerformanceRecordAnalyzer(PrintStream out)
  {
    this.out = out;
  }

  public PrintStreamPerformanceRecordAnalyzer()
  {
    this(System.out);
  }

  @Override
  public void analyze(List<PerformanceRecord> performanceRecords)
  {
    printHeader();

    for (PerformanceRecord performanceRecord : performanceRecords)
    {
      printRecord(performanceRecord);
    }
  }

  protected void printHeader()
  {
    String recordString = MessageFormat.format("{0};{1};{2};{3};{4};{5};{6}", //
        "ContainerConfig", //
        "RepositoryConfig", //
        "SessionConfig", //
        "ModelConfig", //
        "Test name", //
        "Test Case Name", //
        "Average Duration");

    out.println(recordString);
  }

  protected void printRecord(PerformanceRecord performanceRecord)
  {
    IScenario scenario = performanceRecord.getScenario();

    String recordString = MessageFormat.format("{0};{1};{2};{3};{4};{5};{6};{7}", //
        scenario.getRepositoryConfig().getName(), //
        scenario.getSessionConfig().getName(), //
        scenario.getModelConfig().getName(), //
        performanceRecord.getTestName(), //
        performanceRecord.getTestCaseName(), //
        performanceRecord.getDurationAvg(), performanceRecord.getDurationMin(), performanceRecord.getDurationMax());

    out.println(recordString);
  }
}
