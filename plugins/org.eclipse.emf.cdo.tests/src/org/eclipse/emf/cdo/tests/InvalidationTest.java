/*
 * Copyright (c) 2007-2012, 2015, 2016, 2018, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.view.CDOInvalidationPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.FSMUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
public class InvalidationTest extends AbstractCDOTest
{
  public void _testIvalidationMemoryLeak() throws Exception
  {
    final CDOSession session = openSession();

    /**
     * @author Eike Stepper
     */
    class MyTransTest implements Runnable
    {
      private int nr;

      public MyTransTest(int nr)
      {
        this.nr = nr;
      }

      @Override
      public void run()
      {
        for (int i = 0; i < 10; i++)
        {
          Category category = getModel1Factory().createCategory();
          category.setName("Category " + System.currentTimeMillis());

          CDOTransaction transaction = session.openTransaction();

          try
          {
            CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1_" + nr));
            resource.getContents().add(category);
            transaction.commit();
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
          finally
          {
            transaction.close();
          }
        }
      }
    }

    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < 20; i++)
    {
      threads.add(new Thread(new MyTransTest(i + 1)));
    }

    for (Thread thread : threads)
    {
      thread.start();
    }

    for (Thread thread : threads)
    {
      thread.join();
    }

    // session.refresh();
    // sleep(2000);

    new MyTransTest(0).run();

    sleep(60000);

    // session.refresh();

    Field field = ReflectUtil.getField(CDOSessionImpl.class, "outOfSequenceInvalidations");
    Map<?, ?> outOfSequenceInvalidations = (Map<?, ?>)ReflectUtil.getValue(field, session);

    int size = outOfSequenceInvalidations.size();
    assertEquals(0, size);
  }

  public void testSeparateView() throws Exception
  {
    final CDOSession session = openSession();

    // ************************************************************* //

    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Category category2A = getModel1Factory().createCategory();
    category2A.setName("category2");

    final Category category3A = getModel1Factory().createCategory();
    category3A.setName("category3");

    final Company companyA = getModel1Factory().createCompany();

    companyA.getCategories().add(category1A);
    category1A.getCategories().add(category2A);
    category2A.getCategories().add(category3A);

    final CDOTransaction transaction = session.openTransaction();
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);
    transaction.commit();

    // ************************************************************* //

    final CDOView view = session.openTransaction();

    final CDOResource resourceB = view.getResource(getResourcePath("/test1"));
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Company companyB = (Company)contents.get(0);
    assertClean(companyB, view);
    assertClean(resourceB, view);
    assertContent(resourceB, companyB);

    final Category category1B = companyB.getCategories().get(0);
    assertClean(category1B, view);
    assertClean(companyB, view);
    assertContent(companyB, category1B);

    final Category category2B = category1B.getCategories().get(0);
    assertClean(category2B, view);
    assertClean(category1B, view);
    assertContent(category1B, category2B);

    final Category category3B = category2B.getCategories().get(0);
    assertClean(category3B, view);
    assertClean(category2B, view);
    assertContent(category2B, category3B);
    assertClean(category3B, view);

    // ************************************************************* //

    category1A.setName("CHANGED NAME");
    assertEquals("category1", category1B.getName());
    transaction.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        String name = category1B.getName();
        return "CHANGED NAME".equals(name);
      }
    }.assertNoTimeOut();
  }

  public void testSeparateViewNotification() throws Exception
  {
    final CDOSession session = openSession();

    // ************************************************************* //

    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Category category2A = getModel1Factory().createCategory();
    category2A.setName("category2");

    final Category category3A = getModel1Factory().createCategory();
    category3A.setName("category3");

    final Company companyA = getModel1Factory().createCompany();

    companyA.getCategories().add(category1A);
    category1A.getCategories().add(category2A);
    category2A.getCategories().add(category3A);

    final CDOTransaction transaction = session.openTransaction();
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);
    transaction.commit();

    // ************************************************************* //

    final CDOView viewB = session.openTransaction();
    final CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Company companyB = (Company)contents.get(0);
    assertClean(companyB, viewB);
    assertClean(resourceB, viewB);
    assertContent(resourceB, companyB);

    final Category category1B = companyB.getCategories().get(0);
    assertClean(category1B, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, category1B);

    final Category category2B = category1B.getCategories().get(0);
    assertClean(category2B, viewB);
    assertClean(category1B, viewB);
    assertContent(category1B, category2B);

    final Category category3B = category2B.getCategories().get(0);
    assertClean(category3B, viewB);
    assertClean(category2B, viewB);
    assertContent(category2B, category3B);
    assertClean(category3B, viewB);

    // ************************************************************* //

    final CountDownLatch latch = new CountDownLatch(1);
    viewB.getSession().addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
          if (e.getLocalTransaction() == transaction)
          {
            msg("CDOSessionInvalidationEvent: " + e);
            latch.countDown();
          }
        }
      }
    });

    category1A.setName("CHANGED NAME");
    transaction.commit();

    await(latch);
  }

  public void testConflictSameSession() throws Exception, IOException
  {
    CDOSession session = openSession();
    CDOTransaction trans1 = session.openTransaction();
    CDOTransaction trans2 = session.openTransaction();
    testConflict(trans1, trans2);
  }

  public void testConflictDifferentSession() throws Exception, IOException
  {
    CDOSession session1 = openSession();
    CDOTransaction trans1 = session1.openTransaction();

    CDOSession session2 = openSession();
    CDOTransaction trans2 = session2.openTransaction();

    testConflict(trans1, trans2);
  }

  private void testConflict(CDOTransaction trans1, CDOTransaction trans2) throws Exception
  {
    final CDOResource res1 = trans1.getOrCreateResource(getResourcePath("/test"));
    trans1.commit();

    final CDOResource res2 = trans2.getOrCreateResource(getResourcePath("/test"));

    final Customer customerA1 = getModel1Factory().createCustomer();
    res1.getContents().add(customerA1);

    final Customer customerB2 = getModel1Factory().createCustomer();
    res2.getContents().add(customerB2);

    trans1.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return CDOUtil.getCDOObject(res2).cdoState() == CDOState.CONFLICT;
      }
    }.assertNoTimeOut();

    final Customer customerA2 = getModel1Factory().createCustomer();
    res1.getContents().add(customerA2);
    trans1.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return CDOUtil.getCDOObject(res2).cdoState() == CDOState.CONFLICT;
      }
    }.assertNoTimeOut();

    trans2.rollback();
    assertEquals(2, res1.getContents().size());
  }

  public void testDetachedConflictSameSession() throws Exception, IOException
  {
    CDOSession session = openSession();

    CDOTransaction trans1 = session.openTransaction();
    trans1.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);

    CDOTransaction trans2 = session.openTransaction();
    trans2.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);

    testDetachedConflict(trans1, trans2);
  }

  public void testDetachedConflictDifferentSession() throws Exception, IOException
  {
    CDOSession session1 = openSession();
    CDOTransaction trans1 = session1.openTransaction();
    trans1.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);

    CDOSession session2 = openSession();
    CDOTransaction trans2 = session2.openTransaction();
    trans2.options().setInvalidationPolicy(CDOInvalidationPolicy.STRICT);

    testDetachedConflict(trans1, trans2);
  }

  private void testDetachedConflict(CDOTransaction trans1, CDOTransaction trans2) throws Exception
  {
    final CDOResource res1 = trans1.getOrCreateResource(getResourcePath("/test"));
    trans1.commit();

    final CDOResource res2 = trans2.getResource(getResourcePath("/test"));

    res1.delete(null);

    res2.getContents().add(getModel1Factory().createCustomer());
    assertEquals(true, res2.isExisting());

    commitAndSync(trans1, trans2);
    assertEquals(CDOState.INVALID_CONFLICT, CDOUtil.getCDOObject(res2).cdoState());

    trans2.rollback();
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(res2).cdoState());
    assertEquals(false, res2.isExisting());

    try
    {
      res2.getContents().get(0);
      fail("InvalidObjectException expected");
    }
    catch (InvalidObjectException expected)
    {
      // SUCCESS
    }
  }

  public void testSeparateSession() throws Exception
  {
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    final Category category2A = getModel1Factory().createCategory();
    category2A.setName("category2");

    final Category category3A = getModel1Factory().createCategory();
    category3A.setName("category3");

    final Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);
    category1A.getCategories().add(category2A);
    category2A.getCategories().add(category3A);

    final CDOSession sessionA = openSession();
    final CDOTransaction transaction = sessionA.openTransaction();
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);
    transaction.commit();

    // ************************************************************* //

    final CDOSession sessionB = openSession();
    final CDOView viewB = sessionB.openTransaction();
    final CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Company companyB = (Company)contents.get(0);
    assertClean(companyB, viewB);
    assertClean(resourceB, viewB);
    assertContent(resourceB, companyB);

    final Category category1B = companyB.getCategories().get(0);
    assertClean(category1B, viewB);
    assertClean(companyB, viewB);
    assertContent(companyB, category1B);

    final Category category2B = category1B.getCategories().get(0);
    assertClean(category2B, viewB);
    assertClean(category1B, viewB);
    assertContent(category1B, category2B);

    final Category category3B = category2B.getCategories().get(0);
    assertClean(category3B, viewB);
    assertClean(category2B, viewB);
    assertContent(category2B, category3B);
    assertClean(category3B, viewB);

    // ************************************************************* //

    category1A.setName("CHANGED NAME");
    transaction.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1B.getName());
      }
    }.assertNoTimeOut();
  }

  /**
   * See bug 236784
   */
  public void testInvalidateAndCache() throws Exception
  {
    msg("Opening sessionA");
    CDOSession sessionA = openSession();

    msg("Opening transactionA");
    final CDOTransaction transactionA = sessionA.openTransaction();
    final CDOID cdoidA;

    // *************************************************************
    {
      msg("Creating categoryA");
      Category categoryA = getModel1Factory().createCategory();
      categoryA.setName("categoryA");

      msg("Creating companyA");
      Company companyA = getModel1Factory().createCompany();

      msg("Adding categories");
      companyA.getCategories().add(categoryA);

      msg("Creating resource");
      CDOResource resourceA = transactionA.createResource(getResourcePath("/test1"));

      msg("Adding companyA");
      resourceA.getContents().add(companyA);

      msg("Committing");
      transactionA.commit();

      cdoidA = CDOUtil.getCDOObject(categoryA).cdoID();
      ((CDOViewImpl)transactionA).removeObject(cdoidA);
    }

    // *************************************************************
    msg("Opening sessionB");
    CDOSession sessionB = openSession();

    msg("Opening transactionB");
    CDOTransaction transactionB = sessionB.openTransaction();
    Category categoryB;

    categoryB = (Category)CDOUtil.getEObject(transactionB.getObject(cdoidA, true));
    msg("Changing name");
    categoryB.setName("CHANGED NAME");

    msg("\n\n\n\n\n\n\n\n\n\n\nCommitting");
    transactionB.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        Category categoryA = (Category)CDOUtil.getEObject(transactionA.getObject(cdoidA, true));
        String name = categoryA.getName();
        return "CHANGED NAME".equals(name);
      }
    }.assertNoTimeOut();
  }

  public void testRefreshEmptyRepository() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(0, session.refresh());
    session.close();
  }

  public void testSeparateSession_PassiveUpdateDisable() throws Exception
  {
    Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    Category category2A = getModel1Factory().createCategory();
    category2A.setName("category2");

    Category category3A = getModel1Factory().createCategory();
    category3A.setName("category3");

    Company companyA = getModel1Factory().createCompany();
    companyA.getCategories().add(category1A);

    category1A.getCategories().add(category2A);
    category2A.getCategories().add(category3A);

    CDOSession sessionA = openSession();
    CDOTransaction transaction = sessionA.openTransaction();
    CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);

    transaction.commit();
    URI uriCategory1 = EcoreUtil.getURI(category1A);

    // ************************************************************* //

    CDOSession sessionB = openSession();
    sessionB.options().setPassiveUpdateEnabled(false);

    CDOView viewB = sessionB.openTransaction();
    final Category category1B = (Category)viewB.getResourceSet().getEObject(uriCategory1, true);

    // ************************************************************* //

    category1A.setName("CHANGED NAME");
    transaction.commit();

    sessionB.refresh();

    // TODO Why poll? refresh is synchonous...
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1B.getName());
      }
    }.assertNoTimeOut();
  }

  public void testPassiveUpdateOnAndOff() throws Exception
  {
    msg("Creating category1");
    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Creating category2");
    final Category category2A = getModel1Factory().createCategory();
    category2A.setName("category2");

    msg("Creating category3");
    final Category category3A = getModel1Factory().createCategory();
    category3A.setName("category3");

    msg("Creating company");
    final Company companyA = getModel1Factory().createCompany();

    msg("Adding categories");
    companyA.getCategories().add(category1A);
    category1A.getCategories().add(category2A);
    category2A.getCategories().add(category3A);

    msg("Opening sessionA");
    final CDOSession sessionA = openSession();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    URI uriCategory1 = EcoreUtil.getURI(category1A);
    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openSession();

    sessionB.options().setPassiveUpdateEnabled(false);

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();

    final Category category1B = (Category)viewB.getResourceSet().getEObject(uriCategory1, true);

    // ************************************************************* //
    msg("Opening sessionB");
    final CDOSession sessionC = openSession();

    assertEquals(true, sessionC.options().isPassiveUpdateEnabled());

    msg("Attaching viewB");
    final CDOView viewC = sessionC.openTransaction();

    final Category category1C = (Category)viewC.getResourceSet().getEObject(uriCategory1, true);

    msg("Changing name");
    category1A.setName("CHANGED NAME");

    class TimeOuterB extends PollingTimeOuter
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1B.getName());
      }
    }

    class TimeOuterC extends PollingTimeOuter
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1C.getName());
      }
    }

    transaction.commit();

    new TimeOuterC().assertNoTimeOut();

    // It should refresh the session
    sessionB.options().setPassiveUpdateEnabled(true);

    msg("Checking after sync");
    new TimeOuterB().assertNoTimeOut();
    new TimeOuterC().assertNoTimeOut();

    category1A.setName("CHANGED NAME-VERSION2");

    class TimeOuterB_2 extends PollingTimeOuter
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME-VERSION2".equals(category1B.getName());
      }
    }

    class TimeOuterC_2 extends PollingTimeOuter
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME-VERSION2".equals(category1C.getName());
      }
    }

    transaction.commit();

    new TimeOuterB_2().assertNoTimeOut();
    new TimeOuterC_2().assertNoTimeOut();
  }

  public void testDetach() throws Exception
  {
    final Category categoryA = getModel1Factory().createCategory();
    categoryA.setName("category1");

    final CDOSession sessionA = openSession();
    final CDOTransaction transaction = sessionA.openTransaction();
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    resourceA.getContents().add(categoryA);
    transaction.commit();

    // ************************************************************* //

    final CDOSession sessionB = openSession();
    final CDOView viewB = sessionB.openTransaction();
    viewB.options().setInvalidationNotificationEnabled(true);

    final CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Category categoryB = (Category)contents.get(0);
    final TestAdapter testAdapter = new TestAdapter(categoryB);

    // ************************************************************* //

    resourceA.getContents().remove(categoryA);
    testAdapter.assertNotifications(0);

    transaction.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertNoTimeOut();

    testAdapter.assertNoTimeout(1);
  }

  public void testDetachAndPassiveUpdate() throws Exception
  {
    detachAndPassiveUpdate(false);
  }

  public void testDetachAndPassiveUpdateWithoutRevisionTimestamp() throws Exception
  {
    detachAndPassiveUpdate(true);
  }

  private void detachAndPassiveUpdate(boolean isRemoveRevision) throws Exception
  {
    msg("Creating category1");
    final Category categoryA = getModel1Factory().createCategory();
    categoryA.setName("category1");

    msg("Opening sessionA");
    final CDOSession sessionA = openSession();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    resourceA.getContents().add(categoryA);

    msg("Committing");
    transaction.commit();

    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openSession();
    sessionB.options().setPassiveUpdateEnabled(false);

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();
    viewB.options().setInvalidationNotificationEnabled(true);

    msg("Loading resource");
    final CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Category categoryB = (Category)contents.get(0);

    final TestAdapter testAdapter = new TestAdapter(categoryB);

    // ************************************************************* //

    resourceA.getContents().remove(categoryA);
    testAdapter.assertNotifications(0);

    transaction.commit();

    final Category categoryA2 = getModel1Factory().createCategory();
    categoryA2.setName("categoryA2");
    resourceA.getContents().add(categoryA2);
    transaction.commit();

    if (isRemoveRevision)
    {
      clearCache(getRepository().getRevisionManager());
      getRepository().getRevisionManager().getCache().removeRevision(resourceA.cdoID(), resourceA.cdoRevision().getBranch().getVersion(1));
      getRepository().getRevisionManager().getCache().removeRevision(resourceA.cdoID(), resourceA.cdoRevision().getBranch().getVersion(2));
    }

    testAdapter.assertNotifications(0);
    sessionB.refresh();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertNoTimeOut();

    testAdapter.assertNoTimeout(1);
  }

  public void testPassiveUpdateMode_CHANGES() throws Exception
  {
    Category categoryA = getModel1Factory().createCategory();
    categoryA.setName("category1");

    CDOSession sessionA = openSession();
    CDOTransaction transaction = sessionA.openTransaction();
    CDOResource resourceA = transaction.createResource(getResourcePath("/test1"));

    resourceA.getContents().add(categoryA);
    transaction.commit();

    // ************************************************************* //

    CDOSession sessionB = openSession();
    sessionB.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    CDOView viewB = sessionB.openTransaction();
    CDOResource resourceB = viewB.getResource(getResourcePath("/test1"));

    Category categoryB = (Category)resourceB.getContents().get(0);

    final TestAdapter testAdapter = new TestAdapter(categoryB);

    // ************************************************************* //

    categoryA.setName("CHANGED");
    testAdapter.assertNotifications(0);

    transaction.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        Notification[] notifications = testAdapter.getNotifications();
        if (notifications.length != 0)
        {
          if (!ObjectUtil.equals(notifications[0].getOldStringValue(), "category1"))
          {
            fail("No old value");
          }

          if (!ObjectUtil.equals(notifications[0].getNewStringValue(), "CHANGED"))
          {
            fail("No new value");
          }

          return true;
        }

        return false;
      }
    }.assertNoTimeOut();
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testDeleteFromOtherBranch() throws Exception
  {
    Company companyA = getModel1Factory().createCompany();
    Category categoryA = getModel1Factory().createCategory();
    companyA.getCategories().add(categoryA);

    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(true);
    session.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    CDOTransaction transactionA = session.openTransaction();
    CDOResource resourceA = transactionA.createResource(getResourcePath("/test1"));
    resourceA.getContents().add(companyA);

    CDOBranch mainBranch = transactionA.getBranch();
    transactionA.commit(); // company v1
    assertEquals(mainBranch, CDOUtil.getCDOObject(companyA).cdoRevision().getBranch());
    assertEquals(1, CDOUtil.getCDOObject(companyA).cdoRevision().getVersion());

    companyA.setName("ESC");
    transactionA.commit(); // company v2
    assertEquals(mainBranch, CDOUtil.getCDOObject(companyA).cdoRevision().getBranch());
    assertEquals(2, CDOUtil.getCDOObject(companyA).cdoRevision().getVersion());

    CDOTransaction transactionB = session.openTransaction();
    CDOResource resourceB = transactionB.getResource(getResourcePath("/test1"));
    Company companyB = (Company)resourceB.getContents().get(0);
    companyB.getCategories().get(0);
    CDOObject cdoCompanyB = CDOUtil.getCDOObject(companyB);

    CDOBranch branch1 = mainBranch.createBranch(getBranchName("branch1"));
    transactionB.setBranch(branch1);
    transactionA.setBranch(branch1);

    assertEquals(mainBranch, cdoCompanyB.cdoRevision().getBranch());
    assertEquals(2, cdoCompanyB.cdoRevision().getVersion());

    companyA.getCategories().remove(0);
    commitAndSync(transactionA, transactionB);

    assertEquals(CDOState.CLEAN, cdoCompanyB.cdoState());
    assertEquals(branch1, cdoCompanyB.cdoRevision().getBranch());
    assertEquals(1, cdoCompanyB.cdoRevision().getVersion());
  }
}
