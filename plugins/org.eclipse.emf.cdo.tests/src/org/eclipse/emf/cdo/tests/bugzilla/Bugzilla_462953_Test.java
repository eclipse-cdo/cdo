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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;

/**
 * Bug 462953 : ArrayIndexOutOfBoundsException on EObject.eIsSet() call after rollback.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_462953_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test {@link CDOTransaction#rollback()} with a custom {@link EContentAdapter} which call {@link EObject#eIsSet(org.eclipse.emf.ecore.EStructuralFeature)}.
   */
  public void testRollBackWithACustomEContentAdapter() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    resource1.getContents().add(company);
    transaction1.commit();
    transaction1.close();
    session1.close();

    session1 = openSession();
    transaction1 = session1.openTransaction();
    resource1 = transaction1.getResource(getResourcePath(RESOURCE_NAME));
    // To test EObject.eIsSet() during rollback
    resource1.eAdapters().add(new CustomECrossReferenceAdapter());

    company = (Company)resource1.getContents().get(0);
    Category category = getModel1Factory().createCategory();

    // Test EObject.eIsSet() before category attachement to the CDOResource
    for (EReference eReference : category.eClass().getEReferences())
    {
      category.eIsSet(eReference);
    }

    company.getCategories().add(category);

    // Test EObject.eIsSet() before rollback
    for (EReference eReference : category.eClass().getEReferences())
    {
      category.eIsSet(eReference);
    }

    transaction1.rollback();

    // Test EObject.eIsSet() after rollback
    for (EReference eReference : category.eClass().getEReferences())
    {
      category.eIsSet(eReference);
    }

    CDOObject cdoObject = CDOUtil.getCDOObject(category);
    assertEquals(CDOState.TRANSIENT, cdoObject.cdoState());
    assertNull(cdoObject.cdoID());
    assertNull(cdoObject.cdoView());
  }

  private static class CustomECrossReferenceAdapter extends ECrossReferenceAdapter
  {

    @Override
    protected void handleContainment(Notification notification)
    {
      super.handleContainment(notification);
      switch (notification.getEventType())
      {
      case Notification.REMOVE:
      {
        Object oldValue = notification.getOldValue();
        if (oldValue instanceof EObject)
        {
          EObject eObject = (EObject)oldValue;
          for (EReference eReference : eObject.eClass().getEReferences())
          {
            eObject.eIsSet(eReference);
          }
        }
        break;
      }
      }
    }
  }

}
