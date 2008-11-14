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
package org.eclipse.emf.cdo.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBHsqldbPrepStmt extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsDBHsqldbPrepStmt().getTestSuite("CDO Tests (DB Hsql Horizontal PrepStmt)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, DB_HSQL_HORIZONTAL_PREPSTMT, TCP, NATIVE);
  }
}
