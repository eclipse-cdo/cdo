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

import org.eclipse.emf.cdo.tests.TransactionDeadLockTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @see https://bugs.eclipse.org/213782
 * @see https://bugs.eclipse.org/201366
 * @author Simon McDuff
 */
public class HbTransactionDeadLockTest extends TransactionDeadLockTest
{

  // allows a testcase to pass specific properties
  @Override
  protected Map<String, String> getTestProperties()
  {
    final Map<String, String> testProperties = new HashMap<String, String>();
    testProperties.put("hibernate.hbm2ddl.auto", "update");
    return testProperties;
  }
}
