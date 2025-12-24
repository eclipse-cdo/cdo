/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOViewSet;

/**
 * Bug 465115 about StackOverflowError on CDOUtil.getViewSet() call with a CDOResource.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_465115_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test {@link CDOUtil#getViewSet(org.eclipse.emf.common.notify.Notifier)} on a {@link CDOResource}.
   */
  public void testCDOUtilGetViewSet() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setLockNotificationEnabled(true);
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction1.commit();

    CDOViewSet viewSet = CDOUtil.getViewSet(resource1);
    assertNotNull(viewSet);
    assertEquals(1, viewSet.getViews().length);
    assertEquals(transaction1, viewSet.getViews()[0]);
  }
}
