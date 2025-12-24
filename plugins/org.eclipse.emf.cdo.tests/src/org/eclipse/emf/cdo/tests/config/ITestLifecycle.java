/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

/**
 * @author Eike Stepper
 */
public interface ITestLifecycle
{
  public ConfigTest getCurrentTest();

  public void setCurrentTest(ConfigTest currentTest);

  public void setUp() throws Exception;

  public void tearDown() throws Exception;

  public void mainSuiteFinished() throws Exception;
}
