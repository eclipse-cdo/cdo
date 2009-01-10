/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;
import org.eclipse.emf.cdo.tests.TransactionTest;

import java.util.Map;

/**
 * @author Martin Taal
 */
public class HbTransactionDeadLockTest extends TransactionTest
{
  public static void main(String[] args) throws Exception
  {
    HbTransactionDeadLockTest test = new HbTransactionDeadLockTest();
    test.setUp();
    test.testCreateManyTransactions();
    test.tearDown();
  }

  public HbTransactionDeadLockTest()
  {
    StoreRepositoryProvider.setInstance(HbStoreRepositoryProvider.getInstance());
  }

  @Override
  public Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put("hibernate.hbm2ddl.auto", "update");
    return testProperties;
  }
}
