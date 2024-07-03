/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.tests;

/**
 * @author Eike Stepper
 */
public class TestCase
{
  private String name;

  public TestCase(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public void fail()
  {
    throw new RuntimeException("Test failed");
  }

  protected void setUp() throws Exception
  {
  }

  protected void tearDown() throws Exception
  {
  }
}
