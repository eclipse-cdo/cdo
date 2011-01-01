/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditTestSameSession;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingTestSameSession;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_303807_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class ObjyDBConfigs extends AllConfigs
{
  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // remove BranchingTests because most mappings do not support it
    // Subclasses should add Banching tests if supported
    if (!hasBranchingSupport())
    {
      testClasses.remove(BranchingTest.class);
      testClasses.remove(BranchingTestSameSession.class);
      testClasses.remove(MergingTest.class);
      testClasses.remove(Bugzilla_303807_Test.class);
    }

    if (!hasAuditSupport())
    {
      // non-audit mode - remove audit tests
      testClasses.remove(AuditTest.class);
      testClasses.remove(AuditTestSameSession.class);
      testClasses.remove(Bugzilla_252214_Test.class);
    }

    // testClasses.add(DBStoreTest.class);
    // testClasses.add(SQLQueryTest.class);
    // testClasses.add(DBAnnotationsTest.class);

    // sometime cause a crash (Investigate!!)
    testClasses.remove(XATransactionTest.class);
  }

  protected abstract boolean hasBranchingSupport();

  protected abstract boolean hasAuditSupport();
}
