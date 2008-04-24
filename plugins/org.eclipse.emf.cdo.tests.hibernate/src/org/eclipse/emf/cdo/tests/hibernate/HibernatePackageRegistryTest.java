/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.PackageRegistryTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Overrides the real testclass because it needs some specific create-drop settings for specific testcases. This is not
 * the nicest way of doing this but it is short and easy to understand.
 */
public class HibernatePackageRegistryTest extends PackageRegistryTest
{

  private String hbm2ddlValue = null;

  // allows a testcase to pass specific properties
  @Override
  protected Map<String, String> getTestProperties()
  {
    final Map<String, String> testProperties = new HashMap<String, String>();
    if (getHbm2ddlValue() != null)
    {
      testProperties.put("hibernate.hbm2ddl.auto", getHbm2ddlValue());
    }
    return testProperties;
  }

  @Override
  // this testcase can't handle create-drop because in the middle of the
  // testcase a new package is written to the db
  public void testCommitUnrelatedPackage() throws Exception
  {
    setHbm2ddlValue("update");
    // this needs to be extra because in doSetup it is unknown which testcase is being run
    doTearDown();
    doSetUp();
    try
    {
      super.testCommitUnrelatedPackage();
    }
    finally
    {
      setHbm2ddlValue(null);
    }
  }

  @Override
  protected void doSetUp() throws Exception
  {
    System.err.println("Called dosetup");
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    System.err.println("Called doteardown");
    super.doTearDown();
  }

  public String getHbm2ddlValue()
  {
    return hbm2ddlValue;
  }

  public void setHbm2ddlValue(String hbm2ddlValue)
  {
    this.hbm2ddlValue = hbm2ddlValue;
  }
}
