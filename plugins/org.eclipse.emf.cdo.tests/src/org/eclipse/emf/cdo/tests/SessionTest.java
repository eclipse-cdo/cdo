/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

/**
 * @author Eike Stepper
 */
public class SessionTest extends AbstractCDOTest
{
  public void testInvalidateAndCache() throws Exception
  {
    msg("Opening sessionA");
    CDOSession sessionA = openModel1Session();

    msg("Opening transactionA");
    final CDOTransactionImpl transactionA = (CDOTransactionImpl)sessionA.openTransaction();
    final CDOID cdoidA;

    // *************************************************************
    {
      msg("Creating categoryA");
      Category categoryA = Model1Factory.eINSTANCE.createCategory();
      categoryA.setName("categoryA");

      msg("Creating companyA");
      Company companyA = Model1Factory.eINSTANCE.createCompany();

      msg("Adding categories");
      companyA.getCategories().add(categoryA);

      msg("Creating resource");
      CDOResource resourceA = transactionA.createResource("/test1");

      msg("Adding companyA");
      resourceA.getContents().add(companyA);

      msg("Committing");
      transactionA.commit();

      cdoidA = categoryA.cdoID();
      transactionA.removeObject(cdoidA);
    }

    // *************************************************************
    msg("Opening sessionB");
    CDOSession sessionB = openModel1Session();

    msg("Opening transactionB");
    CDOTransaction transactionB = sessionB.openTransaction();
    Category categoryB = (Category)transactionB.getObject(cdoidA, true);

    msg("Changing name");
    categoryB.setName("CHANGED NAME");

    msg("\n\n\n\n\n\n\n\n\n\n\nCommitting");
    transactionB.commit();

    // Thread.sleep(1000);

    msg("Checking after commit");
    boolean timedOut = new PollingTimeOuter(200, 100)
    {
      @Override
      protected boolean successful()
      {
        Category categoryA = (Category)transactionA.getObject(cdoidA, true);
        String name = categoryA.getName();
        return "CHANGED NAME".equals(name);
      }
    }.timedOut();

    assertEquals(false, timedOut);
  }
}
