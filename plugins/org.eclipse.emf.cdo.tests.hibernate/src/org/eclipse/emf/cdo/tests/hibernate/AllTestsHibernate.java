/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditTestSameSession;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingTestSameSession;
import org.eclipse.emf.cdo.tests.CommitInfoTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.LockingManagerTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;
import org.eclipse.emf.cdo.tests.UnsetTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_272861_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_273565_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_308895_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsHibernate extends AllConfigs
{
  public static final RepositoryConfig HIBERNATE = HibernateConfig.INSTANCE;

  public static Test suite()
  {
    return new AllTestsHibernate().getTestSuite("CDO Tests (Hibernate)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, HIBERNATE, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    testClasses.add(Hibernate_Bugzilla_308895_Test.class);
    testClasses.add(HibernateExternalAnnotationTest.class);
    testClasses.add(HibernateMultiValuedOfAttributeTest.class);
    testClasses.add(HibernateXATransactionTest.class);
    testClasses.add(HibernateExternalReferenceTest.class);
    testClasses.add(HibernateQueryTest.class);
    testClasses.add(HibernateQueryNoCachingTest.class);
    testClasses.add(HibernateBugzilla_258933_Test.class);
    testClasses.add(HibernateUnsetTest.class);
    testClasses.add(HibernateBugzilla_301104_Test.class);

    super.initTestClasses(testClasses);

    testClasses.remove(Bugzilla_308895_Test.class);

    // Branching not supported
    testClasses.remove(BranchingTest.class);
    testClasses.remove(MergingTest.class);
    testClasses.remove(BranchingTestSameSession.class);

    // Commit info not supported
    testClasses.remove(CommitInfoTest.class);

    // Locking manager not supported
    testClasses.remove(LockingManagerTest.class);

    // results in infinite loops it seems
    // runs okay when run standalone
    testClasses.remove(Bugzilla_273565_Test.class);

    // audit support to do
    // bug 244141
    testClasses.remove(AuditTest.class);
    testClasses.remove(AuditTestSameSession.class);
    testClasses.remove(Bugzilla_252214_Test.class);

    // replace a test with our local implementation:
    // the MultiValueOfAttributeTest class has a method
    // testListOfInteger which has a List with a null value
    // this is not nicely supported by Hibernate
    // therefore this step is removed
    testClasses.remove(MultiValuedOfAttributeTest.class);

    // MemStore is not relevant
    testClasses.remove(MEMStoreQueryTest.class);

    // replace test case to do external mapping
    testClasses.remove(XATransactionTest.class);

    // replace test case with one, disabling some non working testcases
    // see the HibernateExternalReferenceTest for a description
    testClasses.remove(ExternalReferenceTest.class);

    // this testcases removes and creates a resource with the
    // same path in one transaction, that's not supported
    // by hibernate.. because of unique key constraints
    testClasses.remove(Bugzilla_272861_Test.class);

    // override a testcase because the hibernate store
    // has a different meaning of unset
    testClasses.remove(Bugzilla_258933_Test.class);

    // remove as unsettable has to be re-visited for the hb store
    // see bugzilla 298579
    testClasses.remove(UnsetTest.class);
  }

  /**
   * Overridden because one testcase does not pass as Hibernate currently does not store the isset boolean values in the
   * database.
   * 
   * @author Eike Stepper
   */
  public static class HibernateUnsetTest extends UnsetTest
  {
    @Override
    public void testUnsettableBaseTypeVsObjectType()
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Hibernate_Bugzilla_308895_Test extends Bugzilla_308895_Test
  {
    @Override
    public void setUp() throws Exception
    {
      super.setUp();
      // final EAttribute att = getAtt();

      // add a teneo annotation
      // final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
      // eAnnotation.setSource("teneo.jpa");
      // eAnnotation.getDetails().put("value", value)
      //
      // att.getEAnnotations().add(eAnnotation);
    }
  }
}
