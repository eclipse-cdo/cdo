/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Ronald Krijgsheld - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * @author Ronald Krijgsheld
 */
public class Test368331 extends AbstractCDOTest
{
  public void testDetach() throws CommitException
  {
    final CDOSession session = openSession();
    InternalCDOTransaction tx1 = (InternalCDOTransaction)session.openTransaction();
    final CDOResource resourceA = tx1.createResource(getResourcePath("/test1"));
    Company c = createCompanyWithCategories(resourceA);
    tx1.commit();

    tx1.addObjectHandler(new CDOObjectHandler()
    {
      @Override
      public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
      {
        if (object instanceof Company && newState == CDOState.TRANSIENT)
        {
          Company c = (Company)object;
          c.getCategories().clear();
        }
      }
    });

    resourceA.getContents().remove(c);
  }

  private Company createCompanyWithCategories(final CDOResource resourceA)
  {
    final Company companyA = getModel1Factory().createCompany();
    resourceA.getContents().add(companyA);
    for (int i = 0; i < 10; i++)
    {
      Category c = getModel1Factory().createCategory();
      companyA.getCategories().add(c);
    }
    return companyA;
  }
}
