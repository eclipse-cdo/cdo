/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal, The Netherlands.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal 
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;

import java.util.HashMap;
import java.util.Map;

public class HbResourceTest extends ResourceTest
{
  private String hbm2ddlValue = null;

  public HbResourceTest()
  {
    StoreRepositoryProvider.setInstance(HbStoreRepositoryProvider.getInstance());
    setHbm2ddlValue("update");
  }

  @Override
  protected void restartContainer() throws Exception
  {
    try
    {
      super.restartContainer();
    }
    finally
    {
      setHbm2ddlValue("create-drop");
    }
  }

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

  public String getHbm2ddlValue()
  {
    return hbm2ddlValue;
  }

  public void setHbm2ddlValue(String hbm2ddlValue)
  {
    this.hbm2ddlValue = hbm2ddlValue;
  }

}
