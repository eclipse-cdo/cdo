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
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Eike Stepper
 */
public class Bugzilla_323958_Test extends AbstractCDOTest
{
  public void testModificationFromTransactionHandler() throws Exception
  {
    {
      final Company company = getModel1Factory().createCompany();
      company.setName("X");

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.addTransactionHandler(new CDODefaultTransactionHandler()
      {
        @Override
        public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
          company.setName("Y");
        }
      });

      CDOResource resource = transaction.createResource(getResourcePath("/test"));
      resource.getContents().add(company);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test"));

    Company company = (Company)resource.getContents().get(0);
    assertEquals("Y", company.getName());
  }

  public void testAdditionFromTransactionHandler() throws Exception
  {
    {
      final Company company = getModel1Factory().createCompany();
      company.setName("X");

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.addTransactionHandler(new CDODefaultTransactionHandler()
      {
        @Override
        public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
          company.getCategories().add(getModel1Factory().createCategory());
        }
      });

      CDOResource resource = transaction.createResource(getResourcePath("/test"));
      resource.getContents().add(company);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test"));

    Company company = (Company)resource.getContents().get(0);
    assertEquals(1, company.getCategories().size());
  }
}
