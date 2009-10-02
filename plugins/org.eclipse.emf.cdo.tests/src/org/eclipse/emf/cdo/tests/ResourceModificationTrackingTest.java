/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper            - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * @author Victor Roldan Betancort
 */
public class ResourceModificationTrackingTest extends AbstractCDOTest
{
  private boolean notification;

  public void testResourceModificationTracking() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource1");
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    // Modification is not enabled by default
    company.setName("foobar");
    assertEquals(false, resource.isModified());
    transaction.commit();

    // Enable modification

    resource.setTrackingModification(true);
    company.setName("foobar2");
    assertEquals(true, resource.isModified());
    transaction.commit();
    assertEquals(false, resource.isModified());
    session.close();
  }

  public void testResourceModificationTrackingNotification() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource1");
    resource.setTrackingModification(true);
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    notification = false;

    // Add a Listener
    resource.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        notification = true;
        super.notifyChanged(msg);
      }
    });

    // Modification is disabled by default
    company.setName("foobar");
    assertEquals(true, notification);
    transaction.commit();
    session.close();
  }

  public void testResourceSetTrackingManagement() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource("/my/resource1");
    CDOResource resource2 = transaction.createResource("/my/resource2");
    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    resource2.getContents().add(company2);
    transaction.commit();

    // It should be disabled by default
    assertEquals(false, ((CDOTransactionImpl)transaction).isTrackingModification());

    resource1.setTrackingModification(true);
    resource2.setTrackingModification(true);

    resource1.setTrackingModification(false);

    // Since there is still another Resource with Modification Tracking enabled, the flag should be enabled
    assertEquals(true, ((CDOTransactionImpl)transaction).isTrackingModification());

    resource2.setTrackingModification(false);
    // There are no more Resource with Modification Tracking enabled, the flag should be disabled
    assertEquals(false, ((CDOTransactionImpl)transaction).isTrackingModification());

    session.close();
  }
}
