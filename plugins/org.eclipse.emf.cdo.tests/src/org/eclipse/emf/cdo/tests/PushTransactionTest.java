/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gonzague Reydet - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.transaction.TransactionException;

import java.io.File;

/**
 * @author Gonzague Reydet
 */
public class PushTransactionTest extends AbstractCDOTest
{
  private String resourcePath = "res1";

  private String supplierName = "supplier0";

  private File file;

  @Override
  protected void doSetUp() throws Exception
  {
    OMPlatform.INSTANCE.setDebugging(false);
    super.doSetUp();
    populateRepository();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    if (file != null)
    {
      file.delete();
    }

    super.doTearDown();
  }

  public void testUpdateExistingObject() throws Exception
  {
    String newName = "supplier" + System.currentTimeMillis();

    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(resourcePath);
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(newName);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction, file);
      assertTrue(transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      msg("Publish previous modifications");
      pushTransaction.push();
      assertFalse(transaction.isDirty());
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(resourcePath);
    assertEquals(2, resource.getContents().size());
    Supplier supplier = (Supplier)resource.getContents().get(0);
    assertNotNull(supplier);
    assertEquals(newName, supplier.getName());
  }

  public void testDeleteExistingObject() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(resourcePath);
      msg("Remove an existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      resource.getContents().remove(supplier);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction, file);
      assertTrue(transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      msg("Publish previous modifications");
      pushTransaction.push();
      assertFalse(transaction.isDirty());
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(resourcePath);
    assertEquals(1, resource.getContents().size());
  }

  public void testConflictWithRemovedObject() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(resourcePath);
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(null);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Open transaction & commit changes to repo");
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(resourcePath);
      resource.getContents().remove(0);
      transaction.commit();
      session.close();
    }

    msg("Reload previous local session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    try
    {
      new CDOPushTransaction(transaction, file);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
      // SUCCESS
    }
  }

  public void testConflictWithModifiedObject() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(resourcePath);
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName("" + System.currentTimeMillis());
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Open transaction & commit changes to repo");
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(resourcePath);
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName("" + System.currentTimeMillis());
      transaction.commit();
      session.close();
    }

    msg("Reload previous local session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOPushTransaction pushTransaction = new CDOPushTransaction(transaction, file);
    assertTrue(transaction.hasConflict());

    try
    {
      pushTransaction.push();
      fail("TransactionException expected");
    }
    catch (TransactionException expected)
    {
      // SUCCESS
    }
  }

  private void populateRepository()
  {
    msg("Populate the repository the classic way");
    msg("Create resource");
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(resourcePath);
    msg("Populate resource");
    Supplier supplier0 = getModel1Factory().createSupplier();
    supplier0.setName(supplierName);
    resource.getContents().add(supplier0);
    Company company0 = getModel1Factory().createCompany();
    resource.getContents().add(company0);
    msg("Commit");
    transaction.commit();
    session.close();
  }
}
