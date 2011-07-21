/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Database preparation before test execution:
 * <ol>
 * <li>create user sa (pass: sa)
 * <li>create databases cdodb1, authrepo, repo2
 * </ol>
 * Database creation/removal is avoided because it takes too long and makes the test-suite impractical.
 * 
 * @author Victor Roldan Betancort
 */
public class AllTestsDBPsql extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBPsql().getTestSuite("CDO Tests (DBStore PSQL Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, new PostgresqlConfig(false, false, IDGenerationLocation.STORE), JVM, NATIVE);
  }
}
