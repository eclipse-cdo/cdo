/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Change subscription are not registered correctly for new CDOResource objects
 * 
 * @see bug 266857
 * @author Simon McDuff
 */
public class Bugzilla_266857_Test extends AbstractCDOTest
{
  public void testBugzilla_266857() throws InterruptedException
  {
    CDOSession session = openSession();

    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();
    transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);

    CDOResource resource1 = transaction.createResource("test1");
    final TestAdapter testAdapterForResource = new TestAdapter();
    resource1.eAdapters().add(testAdapterForResource);
    transaction.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource("test1");
    Company company2 = getModel1Factory().createCompany();
    resource2.getContents().add(company2);
    assertEquals(0, testAdapterForResource.getNotifications().size());
    transaction2.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return testAdapterForResource.getNotifications().size() == 1;
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
