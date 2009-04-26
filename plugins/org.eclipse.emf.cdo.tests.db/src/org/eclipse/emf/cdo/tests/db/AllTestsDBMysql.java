/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Simon McDuff
 */
public class AllTestsDBMysql extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsDBMysql().getTestSuite("CDO Tests (DBStoreRepositoryConfig MySql Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    // addScenario(parent, COMBINED, DB_MYSQL_HORIZONTAL, TCP, NATIVE);
  }
}
