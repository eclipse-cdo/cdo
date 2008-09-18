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
package org.eclipse.emf.cdo.tests.testbed;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class Config
{
  private static final boolean LONG_FORMAT = false;

  private String dimension;

  private String name;

  private ConfigTest currentTest;

  public Config(String dimension, String name)
  {
    this.dimension = dimension;
    this.name = name;
  }

  public String getDimension()
  {
    return dimension;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    if (LONG_FORMAT)
    {
      return dimension + "=" + name;
    }

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
