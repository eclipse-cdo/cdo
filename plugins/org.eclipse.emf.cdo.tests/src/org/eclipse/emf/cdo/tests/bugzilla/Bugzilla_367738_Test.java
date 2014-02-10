/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Notification;

/**
 * Bug 367738 - getOldValue call on Notification from CDO returns null as opposed to old value.
 *
 * @author Eike Stepper
 */
public class Bugzilla_367738_Test extends AbstractCDOTest
{
  public void testNotificationOldValue() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    company.setName("ESC");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    TestAdapter adapter = new TestAdapter();
    company.eAdapters().add(adapter);

    company.setName(null);
    Notification[] notifications = adapter.getNotifications();
    assertEquals("ESC", notifications[0].getOldValue());
  }
}
