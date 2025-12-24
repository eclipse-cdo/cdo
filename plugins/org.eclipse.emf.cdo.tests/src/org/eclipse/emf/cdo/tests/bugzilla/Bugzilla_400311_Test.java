/*
 * Copyright (c) 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * Bug 400311 - CDOObject modifies Store even for Touch notifications.
 *
 * @author Eike Stepper
 */
public class Bugzilla_400311_Test extends AbstractCDOTest
{
  public void testOneFeature() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    company.setName("Eclipse");
    resource.getContents().add(company);

    transaction.commit();
    assertDirty(company, transaction, false);

    company.setName("XYZ");
    assertDirty(company, transaction, true);

    company.setName("Eclipse");
    assertDirty(company, transaction, false);

    transaction.commit();
    assertDirty(company, transaction, false);

    company.setName("ABC");
    assertDirty(company, transaction, true);

    transaction.commit();
    assertDirty(company, transaction, false);
  }

  public void testTwoFeaturesUndoOne() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    company.setName("Eclipse");
    company.setStreet("1");
    resource.getContents().add(company);

    transaction.commit();
    assertDirty(company, transaction, false);

    company.setName("XYZ");
    company.setStreet("2");
    assertDirty(company, transaction, true);

    company.setName("Eclipse");
    assertDirty(company, transaction, true);

    company.setStreet("1");
    assertDirty(company, transaction, false);

    transaction.commit();
    assertDirty(company, transaction, false);
  }

  private static void assertDirty(EObject eObject, CDOView view, boolean dirty)
  {
    if (dirty)
    {
      assertDirty(eObject, view);
    }
    else
    {
      assertClean(eObject, view);
    }

    assertEquals(dirty, view.isDirty());
  }
}
