/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.AttributeTest;
import org.eclipse.emf.cdo.tests.BackupTest;
import org.eclipse.emf.cdo.tests.CommitInfoTest;
import org.eclipse.emf.cdo.tests.DynamicXSDTest;
import org.eclipse.emf.cdo.tests.EMFCompareTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.FeatureMapTest;
import org.eclipse.emf.cdo.tests.LockingManagerRestartRepositoryTest;
import org.eclipse.emf.cdo.tests.LockingManagerRestartSessionTest;
import org.eclipse.emf.cdo.tests.LockingManagerRestartTransactionTest;
import org.eclipse.emf.cdo.tests.LockingManagerTest;
import org.eclipse.emf.cdo.tests.LockingNotificationsTest;
import org.eclipse.emf.cdo.tests.MEMStoreQueryTest;
import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;
import org.eclipse.emf.cdo.tests.PackageRegistryTest;
import org.eclipse.emf.cdo.tests.SecurityManagerTest;
import org.eclipse.emf.cdo.tests.UnsetTest;
import org.eclipse.emf.cdo.tests.WorkspaceTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_272861_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_279982_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_303466_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_306998_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_322804_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_329254_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_334995_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_347964_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_351393_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_352204_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_359966_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_362270_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_365832_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_381472_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_390185_Test;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsHibernate extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsHibernate().getTestSuite("CDO Tests (Hibernate)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, HibernateConfig.INSTANCE, JVM, NATIVE);
    addScenario(parent, COMBINED, HibernateConfig.AUDIT_INSTANCE, JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    // testClasses.clear();
    // testClasses.add(HibernateBugzilla_380987_Test.class);
    // if (true)
    // {
    // return;
    // }

    testClasses.add(Hibernate_Failure_Test.class);
    testClasses.add(Hibernate_Export_Test.class);
    testClasses.add(HibernateBugzilla_381013_Test.class);
    testClasses.add(HibernateBugzilla_380987_Test.class);
    testClasses.add(HibernateBugzilla_392653_Test.class);
    testClasses.add(HibernateBugzilla_387752_Test.class);
    testClasses.add(HibernateBugzilla_387752_True_Test.class);

    // testClasses.add(HibernateBugzilla_387752_Test.class);

    testClasses.add(HibernateBugzilla_333473_Test.class);

    testClasses.add(HibernateTimeStampTest.class);
    // removed stalls
    // testClasses.add(HibernateXATransactionTest.class);
    testClasses.add(HibernateExternalAnnotationTest.class);
    testClasses.add(HibernateQueryTest.class);
    testClasses.add(HibernateQueryNoCachingTest.class);
    testClasses.add(HibernateBugzilla_301104_Test.class);

    testClasses.add(HibernateBugzilla_362270_Test.class);

    super.initTestClasses(testClasses, scenario);

    // for some reason this test needs to be done first...
    testClasses.remove(Bugzilla_306998_Test.class);
    testClasses.add(0, Bugzilla_306998_Test.class);

    testClasses.add(HibernateBugzilla_356181_Test.class);

    // the hb store throws an error on deadlocked transaction
    // and does not block
    testClasses.remove(Bugzilla_390185_Test.class);

    testClasses.add(HibernateBugzilla_398057_Test.class);
    testClasses.add(HibernateBugzilla_397682_Test.class);

    if (scenario.getCapabilities().contains(IRepositoryConfig.CAPABILITY_AUDITING))
    {
      testClasses.add(HibernateBugzilla_395684_Test.class);

      testClasses.add(CDOObjectHistoryTest.class);

      // the security model inherits from the ecore model
      // not so well supported for now for auditing
      testClasses.remove(SecurityManagerTest.class);

      // the package registry count changes when auditing
      // as auditing adds epackages
      testClasses.remove(PackageRegistryTest.class);
      testClasses.add(HibernatePackageRegistryTest.class);
      testClasses.remove(Bugzilla_303466_Test.class);
      testClasses.add(Hibernate_Bugzilla_303466_Test.class);

      // feature maps are not handled correctly in CDO with auditing
      testClasses.remove(FeatureMapTest.class);
    }
    else
    {
      // these testcases uses commitinfo
      // only supported with auditing
      testClasses.remove(Bugzilla_329254_Test.class);
      testClasses.remove(Hibernate_Bugzilla_329254_Test.class);

      // Commit info only works with auditing
      testClasses.remove(CommitInfoTest.class);
    }

    // renaming a resource is not possible in the hibernate store.
    testClasses.remove(Bugzilla_334995_Test.class);

    // repository restart is not supported in the hibernate store
    // as it clears the database
    testClasses.remove(Bugzilla_347964_Test.class);

    // workspaces are not supported
    testClasses.remove(WorkspaceTest.class);

    testClasses.remove(DynamicXSDTest.class);

    // delete repo is not yet supported
    testClasses.remove(Bugzilla_381472_Test.class);

    testClasses.remove(Bugzilla_362270_Test.class);

    // persisting models in a resource is not supported
    testClasses.remove(Bugzilla_365832_Test.class);
    testClasses.remove(Bugzilla_352204_Test.class);
    testClasses.remove(Bugzilla_359966_Test.class);

    // external reference in a resource not supported
    testClasses.remove(Bugzilla_351393_Test.class);

    // hibernate does not support persisting
    // java class and object
    testClasses.add(HibernateAttributeTest.class);
    testClasses.remove(AttributeTest.class);

    // Use a hibernate specific test class
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=339492
    testClasses.add(Hibernate_BackupTest.class);
    testClasses.remove(BackupTest.class);

    // Teneo does not yet support lists of int arrays:
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=330212
    testClasses.remove(Bugzilla_322804_Test.class);

    // overridden because Hibernate will treat all stale references as an exception
    testClasses.add(Hibernate_Bugzilla_279982_Test.class);
    testClasses.remove(Bugzilla_279982_Test.class);

    // locking not supported
    testClasses.remove(LockingManagerRestartRepositoryTest.class);
    testClasses.remove(LockingManagerRestartSessionTest.class);
    testClasses.remove(LockingManagerRestartTransactionTest.class);
    testClasses.remove(LockingNotificationsTest.class);
    testClasses.remove(LockingManagerRestartRepositoryTest.class);

    // Locking manager not supported
    testClasses.remove(LockingManagerTest.class);

    // problem with wrong version of EMF Compare
    testClasses.remove(EMFCompareTest.class);

    // replace a test with our local implementation:
    // the MultiValueOfAttributeTest class has a method
    // testListOfInteger which has a List with a null value
    // this is not nicely supported by Hibernate
    // therefore this step is removed
    testClasses.add(HibernateMultiValuedOfAttributeTest.class);
    testClasses.remove(MultiValuedOfAttributeTest.class);

    // MemStore is not relevant
    testClasses.remove(MEMStoreQueryTest.class);

    // replace test case to do external mapping
    testClasses.remove(XATransactionTest.class);

    // replace test case with one, disabling some non working testcases
    // see the HibernateExternalReferenceTest for a description
    testClasses.add(HibernateExternalReferenceTest.class);
    testClasses.remove(ExternalReferenceTest.class);

    // this testcases removes and creates a resource with the
    // same path in one transaction, that's not supported
    // by hibernate.. because of unique key constraints
    testClasses.remove(Bugzilla_272861_Test.class);

    // override a testcase because the hibernate store
    // has a different meaning of unset
    testClasses.add(HibernateBugzilla_258933_Test.class);
    testClasses.remove(Bugzilla_258933_Test.class);

    // replace as unsettable has to be re-visited for the hb store
    // see Bug 298579, it does not work for object types
    testClasses.add(HibernateUnsetTest.class);
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

  public static class HibernatePackageRegistryTest extends PackageRegistryTest
  {

    @Override
    @CleanRepositoriesBefore
    public void testCommitNestedPackages() throws Exception
    {
    }

    @Override
    @CleanRepositoriesBefore
    public void testCommitTopLevelPackages() throws Exception
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

  public static class Hibernate_BackupTest extends BackupTest
  {

    @Override
    protected void doSetUp() throws Exception
    {
      final IRepositoryConfig repConfig = getRepositoryConfig();
      final HibernateConfig hbConfig = (HibernateConfig)repConfig;
      final String persistenceXML = "org/eclipse/emf/cdo/tests/hibernate/external_model1_4.persistence.xml";
      hbConfig.getAdditionalProperties().put(HibernateStore.PERSISTENCE_XML, persistenceXML);

      super.doSetUp();
    }

    @Override
    protected void doTearDown() throws Exception
    {
      final IRepositoryConfig repConfig = getRepositoryConfig();
      final HibernateConfig hbConfig = (HibernateConfig)repConfig;
      hbConfig.getAdditionalProperties().clear();
      super.doTearDown();
    }
  }

  public static class Hibernate_Bugzilla_303466_Test extends Bugzilla_303466_Test
  {

    @Override
    @CleanRepositoriesBefore
    public void test_missingDependency() throws Exception
    {
    }

  }

  public static class Hibernate_Bugzilla_329254_Test extends Bugzilla_329254_Test
  {

    // does not work for non audited cases
    @Override
    public void testCommitTimeStampUpdateOnError() throws Exception
    {
    }

  }
}
