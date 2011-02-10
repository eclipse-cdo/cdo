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
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditSameSessionTest;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingSameSessionTest;
import org.eclipse.emf.cdo.tests.CommitInfoTest;
import org.eclipse.emf.cdo.tests.ComplexTest;
import org.eclipse.emf.cdo.tests.ContainmentTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.LobTest;
import org.eclipse.emf.cdo.tests.LockingManagerTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MergingTest;
import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;
import org.eclipse.emf.cdo.tests.OCLQueryTest;
import org.eclipse.emf.cdo.tests.PartialCommitTest;
import org.eclipse.emf.cdo.tests.RepositoryTest;
import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.SetFeatureTest;
import org.eclipse.emf.cdo.tests.UnsetTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.XRefTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_272861_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_273565_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_279982_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_283985_1_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_283985_2_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_308895_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_316444_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_319836_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_322804_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.util.CommitException;

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
    testClasses.add(XRefTest.class);
    testClasses.add(LobTest.class);
    testClasses.add(RepositoryTest.class);

    testClasses.add(Hibernate_Bugzilla_279982_Test.class);
    testClasses.add(Hibernate_ContainmentTest.class);
    testClasses.add(HibernateXATransactionTest.class);
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
    testClasses.add(Hibernate_SetFeatureTest.class);
    testClasses.add(Hibernate_ResourceTest.class);
    testClasses.add(Hibernate_ComplexTest.class);
    testClasses.add(Hibernate_PartialCommitTest.class);
    testClasses.add(Hibernate_Bugzilla_316444_Test.class);

    super.initTestClasses(testClasses);

    // Teneo does not yet support lists of int arrays:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=330212
    testClasses.remove(Bugzilla_322804_Test.class);

    testClasses.remove(Bugzilla_279982_Test.class);

    // are replaced by Hibernate specific ones, mostly
    // to prevent tests doing move from one container to another
    testClasses.remove(ContainmentTest.class);
    testClasses.remove(ComplexTest.class);
    testClasses.remove(ResourceTest.class);
    testClasses.remove(SetFeatureTest.class);
    testClasses.remove(PartialCommitTest.class);
    testClasses.remove(Bugzilla_316444_Test.class);
    testClasses.remove(Bugzilla_308895_Test.class);

    // contains a lot of containment move, which is not supported by Hibernate
    testClasses.remove(Bugzilla_283985_1_Test.class);
    testClasses.remove(Bugzilla_283985_2_Test.class);
    testClasses.remove(Bugzilla_319836_Test.class);

    // OCL querying not supported
    testClasses.remove(OCLQueryTest.class);

    // Branching not supported
    testClasses.remove(BranchingTest.class);
    testClasses.remove(MergingTest.class);
    testClasses.remove(BranchingSameSessionTest.class);

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
    testClasses.remove(AuditSameSessionTest.class);
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

  // unsettable is hardly supported by the Hibernate Store
  public static class Hibernate_SetFeatureTest extends SetFeatureTest
  {
    @Override
    public void testUnsettableDateNoDefault_SetDefault() throws Exception
    {
    }

    @Override
    public void testUnsettableStringNoDefault_SetDefault() throws Exception
    {
    }
  }

  // disable some container move tests, containment move is not supported
  // by hibernate
  public static class Hibernate_ResourceTest extends ResourceTest
  {
    @Override
    public void testChangePathFromDepth3ToDepth0() throws Exception
    {
    }

    @Override
    public void testChangeResourceURI() throws Exception
    {
    }

    @Override
    public void testChangeResourceFolderURI() throws Exception
    {
    }
  }

  public static class Hibernate_ComplexTest extends ComplexTest
  {
    @Override
    public void testMigrateContainmentMulti()
    {
    }

  }

  public static class Hibernate_PartialCommitTest extends PartialCommitTest
  {
    @Override
    public void testMove() throws CommitException
    {
    }

    @Override
    public void testDoubleMove() throws CommitException
    {
    }
  }

  public static class Hibernate_Bugzilla_316444_Test extends Bugzilla_316444_Test
  {
    @Override
    public void testLockParentWithEAttributeChange() throws Exception
    {
    }

    @Override
    public void testMovingSubtree() throws Exception
    {
    }

  }

  public static class Hibernate_ContainmentTest extends ContainmentTest
  {
    // this testcase is overridden because it uses an ereference which should be
    // annotated with @External, but which can't be mapped like that because it is
    // also used non-externally by other testcases
    @Override
    public void testObjectNotSameResourceThanItsContainerCDOANDXMI() throws Exception
    {
    }

    // see:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=330207#c1
    @Override
    public void testModeledBackPointer_Transient() throws Exception
    {
    }

    // see:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=330207#c1
    @Override
    public void testModeledBackPointer_Transient_Load() throws Exception
    {
    }
  }

  // overridden because Hibernate will treat all stale references as an exception
  public static class Hibernate_Bugzilla_279982_Test extends Bugzilla_279982_Test
  {
    @Override
    public void testBugzilla_279982_Single() throws Exception
    {
      try
      {
        super.testBugzilla_279982_Single();
      }
      catch (Exception e)
      {
        assertEquals(true, e instanceof CommitException);
        assertEquals(true, e.getMessage().contains("org.hibernate.ObjectNotFoundException"));
      }
    }

    @Override
    public void testBugzilla_279982_Multi_RevisionPrefetchingPolicy() throws Exception
    {
      try
      {
        super.testBugzilla_279982_Multi_RevisionPrefetchingPolicy();
      }
      catch (Exception e)
      {
        assertEquals(true, e instanceof CommitException);
        assertEquals(true, e.getMessage().contains("org.hibernate.ObjectNotFoundException"));
      }
    }
  }
}
