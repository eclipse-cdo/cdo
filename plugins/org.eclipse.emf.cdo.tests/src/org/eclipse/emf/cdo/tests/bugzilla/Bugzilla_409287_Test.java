/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
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
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EReference;

/**
 * Bug 409287: ArrayIndexOutOfBoundsException on rollback
 *
 * @author Jack Lechner
 */
public class Bugzilla_409287_Test extends AbstractCDOTest
{
  public void testListenersOnRollback() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    assertEquals(false, transaction.isDirty());
    assertEquals(false, transaction.hasConflict());

    final Category category = getModel1Factory().createCategory();

    // Add CDO Object handler to mimic placement of regular EMF listeners
    transaction.addObjectHandler(new CDOObjectHandler()
    {
      @Override
      public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
      {
        if (newState.equals(CDOState.TRANSIENT))
        {
          try
          {
            EReference feature = getModel1Package().getCategory_Categories();
            category.eIsSet(feature);
          }
          catch (ArrayIndexOutOfBoundsException ex)
          {
            // Found my exception
            throw ex;
          }
        }
      }
    });

    company.getCategories().add(category);
    transaction.rollback();
  }
}
