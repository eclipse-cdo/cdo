/*
 * Copyright (c) 2009-2013, 2017, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.AuditEMapTest;
import org.eclipse.emf.cdo.tests.AuditSameSessionTest;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.BranchingSameSessionTest;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_303807_Test;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.db.bugzilla.Bugzilla_527002_Test;
import org.eclipse.emf.cdo.tests.db.bundle.OM;

import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DBConfigs extends AllConfigs
{
  @Override
  public List<Class<? extends ConfigTest>> getBugzillaTests()
  {
    List<Class<? extends ConfigTest>> tests = super.getBugzillaTests();
    tests.addAll(getTestClasses(OM.BUNDLE, "org.eclipse.emf.cdo.tests.db.bugzilla"));
    return tests;
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    testClasses.add(Net4jDBTest.class);
    testClasses.add(DBAnnotationsTest.class);
    testClasses.add(DBStoreTest.class);
    testClasses.add(CustomTypeMappingTest.class);
    testClasses.add(SQLQueryTest.class);
    testClasses.add(EvolutionTest.class);

    super.initTestClasses(testClasses, scenario);
    testClasses.remove(MEMStoreQueryTest.class);

    if (scenario.getRepositoryConfig().supportingBranches())
    {
      testClasses.remove(Bugzilla_527002_Test.class);
    }
    else
    {
      testClasses.remove(BranchingTest.class);
      testClasses.remove(BranchingSameSessionTest.class);
      testClasses.remove(MergingTest.class);
      testClasses.remove(Bugzilla_303807_Test.class);
    }

    if (scenario.getRepositoryConfig().supportingAudits())
    {
      testClasses.remove(Bugzilla_527002_Test.class);
    }
    else
    {
      // non-audit mode - remove audit tests
      testClasses.remove(AuditTest.class);
      testClasses.remove(AuditEMapTest.class);
      testClasses.remove(AuditSameSessionTest.class);
      testClasses.remove(Bugzilla_252214_Test.class);
    }

    // // fails because of Bug 284109
    // testClasses.remove(XATransactionTest.class);
    // testClasses.add(DISABLE_XATransactionTest.class);

    // XXX Range-based audit mapping does not support queryXRefs for now
    // testClasses.remove(XRefTest.class);
    // testClasses.add(DISABLE_XRefTest.class);

    // ------------ tests below only fail for PostgreSQL
    // ------------ therefore they are overridden and
    // ------------ skipConfig for PSQL is used temporarily

    // // XXX [PSQL] disabled because of Bug 289445
    // testClasses.remove(AttributeTest.class);
    // testClasses.add(DISABLE_AttributeTest.class);

    // testClasses.remove(FeatureMapTest.class);
    // testClasses.add(DISABLE_FeatureMapTest.class);

    // XXX [PSQL] disabled because of Bug 290095
    // using skipconfig in DBAnnotationTest

    // // XXX [PSQL] disabled because of Bug 290097
    // testClasses.remove(ExternalReferenceTest.class);
    // testClasses.add(DISABLE_ExternalReferenceTest.class);
  }
}
