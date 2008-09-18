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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;

import org.eclipse.net4j.util.transaction.TransactionException;

/**
 * @author Eike Stepper
 */
public class RollbackTest extends AbstractCDOTest
{
  public void testRollbackSameSession() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    CDOTransaction transaction2 = session.openTransaction();
    flow1(transaction1, transaction2);
  }

  public void testRollbackSeparateSession() throws Exception
  {
    // Client1
    CDOSession session1 = openModel1Session();
    CDOTransaction transaction1 = session1.openTransaction();

    // Client2
    CDOSession session2 = openModel1Session();
    CDOTransaction transaction2 = session2.openTransaction();

    flow1(transaction1, transaction2);
  }

  protected void flow1(CDOTransaction transaction1, CDOTransaction transaction2)
  {
    // Client1
    CDOResource resource1 = transaction1.createResource("/test1");
    Company company1 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    Category category1 = getModel1Factory().createCategory();
    company1.getCategories().add(category1);
    transaction1.commit();

    // Client2
    CDOResource resource2 = transaction2.getResource("/test1");
    Company company2 = (Company)resource2.getContents().get(0);
    Category category2 = company2.getCategories().get(0);
    category2.setName("client2");

    // Client1
    category1.setName("client1");
    transaction1.commit();
    sleep(500);

    // Client2
    assertEquals(true, transaction2.isDirty());
    assertEquals(true, transaction2.hasConflict());

    try
    {
      transaction2.commit();
      fail("Commit on transaction2 should fail");
    }
    catch (TransactionException ex)
    {
      transaction2.rollback(true);
    }

    assertEquals(false, transaction2.isDirty());
    assertEquals(false, transaction2.hasConflict());
    assertEquals("client1", category2.getName());
    category2.setName("client2");
    transaction2.commit();
    assertEquals(false, transaction2.isDirty());
    assertEquals(false, transaction2.hasConflict());
    assertEquals("client2", category2.getName());
    sleep(500);

    // Client1
    assertEquals(false, transaction1.isDirty());
    assertEquals(false, transaction1.hasConflict());
    assertEquals("client2", category1.getName());
  }
}
