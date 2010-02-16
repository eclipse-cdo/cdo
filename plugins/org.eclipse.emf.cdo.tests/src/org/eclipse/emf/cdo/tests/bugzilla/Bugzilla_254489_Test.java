/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.util.ArrayList;
import java.util.List;

/**
 * CDOTransaction.postCommit not adjusting the Transaction/View reference
 * <p>
 * See bug 254489
 * 
 * @author Simon McDuff
 */
public class Bugzilla_254489_Test extends AbstractCDOTest
{
  public void testBugzilla_254489() throws Exception
  {
    msg("Opening session");
    final CDOSession session = openModel1Session();

    // ************************************************************* //
    msg("Opening transaction");
    final CDOTransaction transaction1 = session.openTransaction();
    final CDOTransaction transaction2 = session.openTransaction();

    CDOResource res1 = transaction1.createResource("/res1");
    final Company companyA1 = getModel1Factory().createCompany();
    res1.getContents().add(companyA1);

    transaction1.commit();

    transaction2.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
    CDOResource res2 = transaction2.getResource("/res1");
    Company companyA2 = (Company)res2.getContents().get(0);
    final TestAdapter companyA2Adapter = new TestAdapter();
    companyA2.eAdapters().add(companyA2Adapter);

    final Category category1A = getModel1Factory().createCategory();
    category1A.setName("category1");

    msg("Adding categories");
    companyA1.getCategories().add(category1A);
    transaction1.commit();

    msg("Checking after commit");
    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return companyA2Adapter.getNotifications().size() == 1;
      }
    }.assertNoTimeOut();

    Category category2 = (Category)companyA2Adapter.getNotifications().get(0).getNewValue();
    assertNotSame(category2, category1A);
    assertSame(transaction2, CDOUtil.getCDOObject(category2).cdoView());
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
