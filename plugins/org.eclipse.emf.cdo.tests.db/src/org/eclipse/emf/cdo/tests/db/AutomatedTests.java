/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AutomatedTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite(AutomatedTests.class.getName());
    suite.addTest(AllTestsDBH2NonAudit.suite());
    suite.addTest(AllTestsDBH2Audit.suite());
    suite.addTest(AllTestsDBH2Branching.suite());
    // suite.addTest(AllTestsDBH2Offline.suite());
    // suite.addTest(AllTestsDBHsqldb.suite());
    // suite.addTest(AllTestsDBHsqldbNonAudit.suite());
    return suite;
  }
}
