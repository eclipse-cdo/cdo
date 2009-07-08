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
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.FeatureMapTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.AuditTest.LocalAuditTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_250036_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_251752_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259695_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_272861_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsHibernate extends AllTestsAllConfigs
{
  public static final RepositoryConfig HIBERNATE = HibernateConfig.INSTANCE;

  public static Test suite()
  {
    return new AllTestsHibernate().getTestSuite("CDO Tests (Hibernate)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, HIBERNATE, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // audit support to do
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=244141
    testClasses.remove(AuditTest.class);
    testClasses.remove(LocalAuditTest.class);
    testClasses.remove(Bugzilla_252214_Test.class);

    // replace a test with our local implementation:
    // the MultiValueOfAttributeTest class has a method
    // testListOfInteger which has a List with a null value
    // this is not nicely supported by Hibernate
    // therefore this step is removed
    testClasses.remove(MultiValuedOfAttributeTest.class);
    testClasses.add(HibernateMultiValuedOfAttributeTest.class);

    // MemStore is not relevant
    testClasses.remove(MEMStoreQueryTest.class);

    // XATransactions are not yet supported
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282649
    testClasses.remove(XATransactionTest.class);
    testClasses.remove(Bugzilla_259869_Test.class);
    testClasses.remove(Bugzilla_251752_Test.class);

    // External refs are supported but they need @External annotations
    // also this testcase contains XA Transaction things
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282651
    testClasses.remove(ExternalReferenceTest.class);

    // Feature map is not yet supported
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282711
    testClasses.remove(FeatureMapTest.class);

    // EMap is not yet supported
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=282748
    testClasses.remove(Bugzilla_250036_Test.class);
    testClasses.remove(Bugzilla_259695_Test.class);

    // this testcases removes and creates a resource with the
    // same path in one transaction, that's not supported
    // by hibernate.. because of unique key constraints
    testClasses.remove(Bugzilla_272861_Test.class);

    // add the hibernate query test
    testClasses.add(HibernateQueryTest.class);

    // override two testcases because the hibernate store
    // has a different meaning of unset
    testClasses.remove(Bugzilla_258933_Test.class);
    testClasses.add(HibernateBugzilla_258933_Test.class);

    // testClasses.clear();
    // testClasses.add(ComplexTest.class);
    // testClasses.add(HibernateQueryTest.class);
    // testClasses.add(Bugzilla_252909_Test.class);
    // testClasses.add(Bugzilla_258933_Test.class);
  }
}
