/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eike Stepper
 */
public class InvalidationTest extends AbstractCDOTest
{
  public void testSeparateView() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //

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

    msg("Opening transaction");
    final CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    // ************************************************************* //

    msg("Opening view");
    final CDOView view = session.openTransaction();

    msg("Loading resource");
    final CDOResource resourceB = view.getResource("/test1");
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

    msg("Changing name");
    category1A.setName("CHANGED NAME");
    sleep(500);

    msg("Checking before commit");
    assertEquals("category1", category1B.getName());

    msg("Committing");
    transaction.commit();

    msg("Checking after commit");
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
    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //

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

    msg("Attaching transaction");
    final CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    // ************************************************************* //

    msg("Attaching viewB");
    final CDOView viewB = session.openTransaction();

    msg("Loading resource");
    final CDOResource resourceB = viewB.getResource("/test1");
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

    final boolean unlocked[] = { false };
    final Lock lock = new ReentrantLock();
    lock.lock();
    viewB.getSession().addListener(new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
          if (e.getView() == viewB)
          {
            msg("CDOSessionInvalidationEvent: " + e);
            // TODO This code has no influence
            unlocked[0] = true;
            lock.unlock();
          }
        }
      }
    });

    msg("Changing name");
    category1A.setName("CHANGED NAME");

    msg("Checking before commit");
    LockTimeOuter timeOuter = new LockTimeOuter(lock);
    boolean timedOut = timeOuter.timedOut(10000);
    assertEquals(true, timedOut);
    assertEquals(false, unlocked[0]);

    msg("Committing");
    transaction.commit();

    msg("Checking after commit");
    if (!unlocked[0])
    {
      timedOut = timeOuter.timedOut(10000);
      assertEquals(true, timedOut);
    }
  }

  public void testConflictSameSession() throws InterruptedException, IOException
  {
    CDOSession session = openSession(getModel1Package());
    CDOTransaction trans1 = session.openTransaction();
    CDOTransaction trans2 = session.openTransaction();
    testConflict(trans1, trans2);
  }

  public void testConflictDifferentSession() throws InterruptedException, IOException
  {
    CDOSession session1 = openSession(getModel1Package());
    CDOTransaction trans1 = session1.openTransaction();

    CDOSession session2 = openSession(getModel1Package());
    CDOTransaction trans2 = session2.openTransaction();

    testConflict(trans1, trans2);
  }

  private void testConflict(CDOTransaction trans1, CDOTransaction trans2) throws InterruptedException, IOException
  {
    final CDOResource res1 = trans1.getOrCreateResource("/test");
    trans1.commit();

    final CDOResource res2 = trans2.getOrCreateResource("/test");

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

  public void testDetachedConflictSameSession() throws InterruptedException, IOException
  {
    CDOSession session = openSession(getModel1Package());
    CDOTransaction trans1 = session.openTransaction();
    CDOTransaction trans2 = session.openTransaction();
    testDetachedConflict(trans1, trans2);
  }

  public void testDetachedConflictDifferentSession() throws InterruptedException, IOException
  {
    CDOSession session1 = openSession(getModel1Package());
    CDOTransaction trans1 = session1.openTransaction();

    CDOSession session2 = openSession(getModel1Package());
    CDOTransaction trans2 = session2.openTransaction();

    testDetachedConflict(trans1, trans2);
  }

  private void testDetachedConflict(CDOTransaction trans1, CDOTransaction trans2) throws InterruptedException,
      IOException
  {
    final CDOResource res1 = trans1.getOrCreateResource("/test");
    trans1.commit();

    final CDOResource res2 = trans2.getResource("/test");

    res1.delete(null);

    final Customer customerB2 = getModel1Factory().createCustomer();
    res2.getContents().add(customerB2);
    assertTrue(res2.isExisting());

    trans1.commit();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return CDOUtil.getCDOObject(res2).cdoState() == CDOState.INVALID_CONFLICT;
      }
    }.assertNoTimeOut();

    trans2.rollback();
    assertEquals(CDOState.INVALID, CDOUtil.getCDOObject(res2).cdoState());
    assertFalse(res2.isExisting());

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
    final CDOSession sessionA = openModel1Session();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openModel1Session();

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();

    msg("Loading resource");
    final CDOResource resourceB = viewB.getResource("/test1");
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

    msg("Changing name");
    category1A.setName("CHANGED NAME");

    msg("Checking before commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1B.getName());
      }
    }.assertTimeOut();

    msg("Committing");
    transaction.commit();

    msg("Checking after commit");
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
   * @see bug 236784
   */
  public void testInvalidateAndCache() throws Exception
  {
    msg("Opening sessionA");
    CDOSession sessionA = openModel1Session();

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
      CDOResource resourceA = transactionA.createResource("/test1");

      msg("Adding companyA");
      resourceA.getContents().add(companyA);

      msg("Committing");
      transactionA.commit();

      cdoidA = CDOUtil.getCDOObject(categoryA).cdoID();
      ((InternalCDOTransaction)transactionA).removeObject(cdoidA);
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
    msg("Opening session");
    final CDOSession session = openModel1Session();
    assertEquals(0, session.refresh().size());
    session.close();
  }

  public void testSeparateSession_PassiveUpdateDisable() throws Exception
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
    final CDOSession sessionA = openModel1Session();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    URI uriCategory1 = EcoreUtil.getURI(category1A);

    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openModel1Session();

    sessionB.options().setPassiveUpdateEnabled(false);

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();

    final Category category1B = (Category)viewB.getResourceSet().getEObject(uriCategory1, true);

    // ************************************************************* //

    msg("Changing name");
    category1A.setName("CHANGED NAME");

    msg("Checking before commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1B.getName());
      }
    }.assertTimeOut();

    msg("Committing");
    transaction.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return "CHANGED NAME".equals(category1B.getName());
      }
    }.assertTimeOut();

    assertEquals(1, sessionB.refresh().size());

    msg("Checking after sync");
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
    final CDOSession sessionA = openModel1Session();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(companyA);

    msg("Committing");
    transaction.commit();

    URI uriCategory1 = EcoreUtil.getURI(category1A);
    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openModel1Session();

    sessionB.options().setPassiveUpdateEnabled(false);

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();

    final Category category1B = (Category)viewB.getResourceSet().getEObject(uriCategory1, true);

    // ************************************************************* //
    msg("Opening sessionB");
    final CDOSession sessionC = openModel1Session();

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

    msg("Checking before commit");
    new TimeOuterB().assertTimeOut();
    new TimeOuterC().assertTimeOut();

    msg("Committing");
    transaction.commit();

    msg("Checking after commit");
    new TimeOuterB().assertTimeOut();
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

    msg("Checking after sync");
    new TimeOuterB_2().assertTimeOut();
    new TimeOuterC_2().assertTimeOut();

    msg("Committing");
    transaction.commit();

    msg("Checking after sync");
    new TimeOuterB_2().assertNoTimeOut();
    new TimeOuterC_2().assertNoTimeOut();
  }

  public void testDetach() throws Exception
  {
    msg("Creating category1");
    final Category categoryA = getModel1Factory().createCategory();
    categoryA.setName("category1");

    msg("Opening sessionA");
    final CDOSession sessionA = openModel1Session();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(categoryA);

    msg("Committing");
    transaction.commit();

    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openModel1Session();

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();
    viewB.options().setInvalidationNotificationEnabled(true);

    msg("Loading resource");
    final CDOResource resourceB = viewB.getResource("/test1");
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Category categoryB = (Category)contents.get(0);
    final TestAdapter testAdapter = new TestAdapter();
    categoryB.eAdapters().add(testAdapter);

    // ************************************************************* //

    resourceA.getContents().remove(categoryA);

    msg("Checking before commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertTimeOut();

    assertEquals(0, testAdapter.getNotifications().size());
    msg("Committing");
    transaction.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertNoTimeOut();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return testAdapter.getNotifications().size() == 1;
      }
    }.assertNoTimeOut();
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
    final CDOSession sessionA = openModel1Session();

    msg("Attaching transaction");
    final CDOTransaction transaction = sessionA.openTransaction();

    msg("Creating resource");
    final CDOResource resourceA = transaction.createResource("/test1");

    msg("Adding company");
    resourceA.getContents().add(categoryA);

    msg("Committing");
    transaction.commit();

    // ************************************************************* //

    msg("Opening sessionB");
    final CDOSession sessionB = openModel1Session();
    sessionB.options().setPassiveUpdateEnabled(false);

    msg("Attaching viewB");
    final CDOView viewB = sessionB.openTransaction();
    viewB.options().setInvalidationNotificationEnabled(true);

    msg("Loading resource");
    final CDOResource resourceB = viewB.getResource("/test1");
    assertProxy(resourceB);

    EList<EObject> contents = resourceB.getContents();
    final Category categoryB = (Category)contents.get(0);

    final TestAdapter testAdapter = new TestAdapter();
    categoryB.eAdapters().add(testAdapter);

    // ************************************************************* //

    resourceA.getContents().remove(categoryA);

    msg("Checking before commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertTimeOut();

    assertEquals(0, testAdapter.getNotifications().size());
    msg("Committing");
    transaction.commit();

    final Category categoryA2 = getModel1Factory().createCategory();
    categoryA2.setName("categoryA2");
    resourceA.getContents().add(categoryA2);
    transaction.commit();

    if (isRemoveRevision)
    {
      clearCache(getRepository().getRevisionManager());
      getRepository().getRevisionManager().getCache().removeRevision(resourceA.cdoID(),
          resourceA.cdoRevision().getBranch().getVersion(1));
      getRepository().getRevisionManager().getCache().removeRevision(resourceA.cdoID(),
          resourceA.cdoRevision().getBranch().getVersion(2));
    }

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertTimeOut();

    assertEquals(0, testAdapter.getNotifications().size());

    sessionB.refresh();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return FSMUtil.isInvalid(CDOUtil.getCDOObject(categoryB));
      }
    }.assertNoTimeOut();

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return testAdapter.getNotifications().size() == 1;
      }
    }.assertNoTimeOut();
  }

  /**
   * @author Simon McDuff
   */
  private static class TestAdapter implements Adapter
  {
    private List<Notification> notifications = new ArrayList<Notification>();

    private Notifier notifier;

    public TestAdapter()
    {
    }

    public Notifier getTarget()
    {
      return notifier;
    }

    public List<Notification> getNotifications()
    {
      return notifications;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      notifications.add(notification);
    }

    public void setTarget(Notifier newTarget)
    {
      notifier = newTarget;
    }
  }
}
