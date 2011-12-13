/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

  public void analyze(List<PerformanceRecord> performanceRecords)
  {
  }

  @Deprecated
  public void addProbe(IScenario scenario, String testName, String testCaseName, int run, long millis)
  {
    String message = MessageFormat.format("{0};{1};{2};{3};{4};{5};{6}", //
        scenario.getContainerConfig().getName(), //
        scenario.getRepositoryConfig().getName(), //
        scenario.getSessionConfig().getName(), //
        scenario.getModelConfig().getName(), //
        testName, //
        testCaseName, //
        millis);

    out.println(message);
  }
}
