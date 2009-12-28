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
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
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

    // if (true)
    // {
    // testClasses.clear();
    // // // current failing tests to solve:
    // testClasses.add(ContainmentTest.class);
    // return;
    // }

    // add a testcase which has an annotation file
    testClasses.add(HibernateExternalAnnotationTest.class);

    // audit support to do
    // bug 244141
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

    // replace test case to do external mapping
    testClasses.remove(XATransactionTest.class);
    testClasses.add(HibernateXATransactionTest.class);

    // replace test case with one, disabling some non working testcases
    // see the HibernateExternalReferenceTest for a description
    testClasses.remove(ExternalReferenceTest.class);
    testClasses.add(HibernateExternalReferenceTest.class);

    // Feature map is not yet supported
    // bug 282711
    testClasses.remove(FeatureMapTest.class);

    // this testcases removes and creates a resource with the
    // same path in one transaction, that's not supported
    // by hibernate.. because of unique key constraints
    testClasses.remove(Bugzilla_272861_Test.class);

    // add the hibernate query test
    testClasses.add(HibernateQueryTest.class);

    // override a testcase because the hibernate store
    // has a different meaning of unset
    testClasses.remove(Bugzilla_258933_Test.class);
    testClasses.add(HibernateBugzilla_258933_Test.class);
  }
}
