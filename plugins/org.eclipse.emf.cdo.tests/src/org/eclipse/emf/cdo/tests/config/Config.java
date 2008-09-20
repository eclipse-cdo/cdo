/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.config;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Config
{
  private String name;

  private ConfigTest currentTest;

  public Config(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return name;
  }

  protected ConfigTest getCurrentTest()
  {
    return currentTest;
  }

  protected void setCurrentTest(ConfigTest currentTest)
  {
    this.currentTest = currentTest;
  }

  protected boolean isValid(Set<Config> configs)
  {
    return true;
  }

  protected void setUp() throws Exception
  {
    if (currentTest == null)
    {
      throw new IllegalStateException("currentTest == null");
    }
  }

  protected void tearDown() throws Exception
  {
  }
}
