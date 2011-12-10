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

import java.text.MessageFormat;

/**
 * Preliminary result collector which prints directly to system out. In the future this should be replaced by a
 * collector which can write to a file or something.
 * 
 * @author Stefan Winkler
 */
public class PerformanceResultCollector implements IPerformanceResultCollector
{
  public void addRecord(IScenario scenario, String testName, String testCaseName, long time)
  {
    String message = MessageFormat.format("{0};{1};{2};{3};{4};{5};{6}", scenario.getContainerConfig().getName(),
        scenario.getRepositoryConfig().getName(), scenario.getSessionConfig().getName(), scenario.getModelConfig()
            .getName(), testName, testCaseName, time);
    System.out.println(message);
  }
}
