/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
