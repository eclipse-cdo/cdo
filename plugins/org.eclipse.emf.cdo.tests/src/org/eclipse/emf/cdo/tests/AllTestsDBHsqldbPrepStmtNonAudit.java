/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Stefan Winkler
 */
public class AllTestsDBHsqldbPrepStmtNonAudit extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsDBHsqldbPrepStmtNonAudit().getTestSuite("CDO Tests (DB Hsql Horizontal PrepStmt Non-Audit)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, DB_HSQL_HORIZONTAL_PREPSTMT_NONAUDIT, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // non-audit mode - remove audit tests
    testClasses.remove(AuditTest.class);
    testClasses.remove(AuditTest.LocalAuditTest.class);
    testClasses.remove(Bugzilla_252214_Test.class);

    // this takes ages - so for now, we disable it
    testClasses.remove(Bugzilla_261218_Test.class);
  }
}
