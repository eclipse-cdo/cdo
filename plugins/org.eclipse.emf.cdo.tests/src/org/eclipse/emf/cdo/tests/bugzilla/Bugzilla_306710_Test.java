/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * IndexOutOfBoundsException in buildNotification.
 * <p>
 * See bug 306710
 */
public class Bugzilla_306710_Test extends AbstractCDOTest
{
  public void testBugzilla_306710_remove() throws Exception
  {
    final NotifyCounter counter = new NotifyCounter();

    // setup connection1.
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));

    // add initial model.
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1a = getModel1Factory().createCategory();
    company1.getCategories().add(category1a);
    transaction1.commit();

    // sleep(100);

    // setup connection2.
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/test1"));

    // add adapter to company2 to have sendDeltaNotification being called.
    Company company2 = (Company)resource2.getContents().get(0);
    company2.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        if (msg.getEventType() == Notification.ADD)
        {
          counter.incAdds();
        }
        else if (msg.getEventType() == Notification.REMOVE)
        {
          counter.incRemoves();
        }
      }
    });

    // add and remove an object from category list of company to have the CDONotificationBuilder call remove with an
    // index not known to the oldRevision.
    Category category1b = getModel1Factory().createCategory();
    company1.getCategories().add(0, category1b);
    company1.getCategories().remove(1);

    // commit the changes.
    transaction1.commit();

    // wait for the invalidation to arrive on transaction2.
    transaction2.waitForUpdate(transaction1.getLastCommitTime(), DEFAULT_TIMEOUT);

    // cleanup.
    session1.close();
    session2.close();

    // check if the notifications arrived (which is not the case because of the exception).
    assertEquals(1, counter.getAdds());
    assertEquals(1, counter.getRemoves());
  }

  /**
   * @since 4.0
   */
  public void testBugzilla_306710_addRemove() throws Exception
  {
    final NotifyCounter counter = new NotifyCounter();

    // setup connection1.
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));

    // add initial model.
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    transaction1.commit();

    // sleep(100);

    // setup connection2.
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    transaction2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("/test1"));

    // add adapter to company2 to have sendDeltaNotification being called.
    Company company2 = (Company)resource2.getContents().get(0);
    company2.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        if (msg.getEventType() == Notification.ADD)
        {
          counter.incAdds();
        }
        else if (msg.getEventType() == Notification.REMOVE)
        {
          counter.incRemoves();
        }
      }
    });

    // add and remove an object from category list of company to have the CDONotificationBuilder call remove with an
    // index not known to the oldRevision.
    Category category1a = getModel1Factory().createCategory();
    company1.getCategories().add(0, category1a);
    company1.getCategories().remove(0);

    // commit the changes.
    transaction1.commit();

    // wait for the invalidation to arrive on transaction2.
    transaction2.waitForUpdate(transaction1.getLastCommitTime(), DEFAULT_TIMEOUT);

    // cleanup.
    session1.close();
    session2.close();

    // check if the notifications arrived (which is not the case because of the exception).
    assertEquals(0, counter.getAdds());
    assertEquals(0, counter.getRemoves());
  }

  /**
   * Helper class to count the notifications.
   */
  public static class NotifyCounter
  {
    int adds;

    int removes;

    public int getAdds()
    {
      return adds;
    }

    public int getRemoves()
    {
      return removes;
    }

    public void incAdds()
    {
      adds++;
    }

    public void incRemoves()
    {
      removes++;
    }
  }
}
