/*
 * Copyright (c) 2009-2013, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Gonzague Reydet - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOPushTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.IOException;
import java.util.Date;

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
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
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
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
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
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
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
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(1, resource.getContents().size());
  }

  public void testAddNewObjectInResource() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("supplier" + System.currentTimeMillis());
      resource.getContents().add(supplier);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(3, resource.getContents().size());
  }

  public void testAddNewObjectInObject() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      Company company = (Company)resource.getContents().get(1);
      company.getCategories().add(category);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(2, resource.getContents().size());
  }

  public void testAddNewObjectInObjectBetweenReloadAndPush() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      Company company = (Company)resource.getContents().get(1);
      company.getCategories().add(category);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);

      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      CDOResource resource = transaction.getResource(getResourcePath(resourcePath));
      resource.getContents().add(category);
      msg("Commit");
      pushTransaction.commit();

      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(3, resource.getContents().size());
  }

  public void testAddNewObjectInObjectTwiceBetweenReloadAndPush() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      Company company = (Company)resource.getContents().get(1);
      company.getCategories().add(category);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);

      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      CDOResource resource = transaction.getResource(getResourcePath(resourcePath));
      resource.getContents().add(category);
      msg("Commit");
      pushTransaction.commit();

      msg("Create a new element");
      category = getModel1Factory().createCategory();
      resource.getContents().add(category);
      msg("Commit");
      pushTransaction.commit();

      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(4, resource.getContents().size());
  }

  public void testAddNewHierarchy() throws Exception
  {
    final String currentSupplierName = "supplier" + System.currentTimeMillis();
    final Date orderDate = new Date();
    final Float orderDetailPrice = 10.5F;

    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName(currentSupplierName);
      resource.getContents().add(supplier);
      msg("Create a new child");
      PurchaseOrder order = getModel1Factory().createPurchaseOrder();
      order.setDate(orderDate);
      supplier.getPurchaseOrders().add(order);
      resource.getContents().add(order);
      OrderDetail detail = getModel1Factory().createOrderDetail();
      detail.setPrice(orderDetailPrice);
      order.getOrderDetails().add(detail);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      assertEquals(4, resource.getContents().size());
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(4, resource.getContents().size());
    Supplier supplier = (Supplier)resource.getContents().get(2);
    assertEquals(currentSupplierName, supplier.getName());
    assertEquals(1, supplier.getPurchaseOrders().size());
    PurchaseOrder order = supplier.getPurchaseOrders().get(0);
    assertNotNull(order);
    assertEquals(orderDate, order.getDate());
    assertEquals(1, order.getOrderDetails().size());
    OrderDetail detail = order.getOrderDetails().get(0);
    assertNotNull(detail);
    assertEquals(orderDetailPrice, detail.getPrice());
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testMoveObject() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new elements");
      Supplier supplier2 = getModel1Factory().createSupplier();
      supplier2.setName("supplier" + System.currentTimeMillis());
      resource.getContents().add(supplier2);
      msg("Move first supplier");
      Supplier supplier1 = (Supplier)resource.getContents().get(0);
      resource.getContents().move(1, supplier1);
      assertEquals(supplierName, ((Supplier)resource.getContents().get(1)).getName());
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      assertEquals(supplierName, ((Supplier)resource.getContents().get(1)).getName());
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(supplierName, ((Supplier)resource.getContents().get(1)).getName());
  }

  public void testClearObjects() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new elements");
      Supplier supplier2 = getModel1Factory().createSupplier();
      supplier2.setName("supplier" + System.currentTimeMillis());
      resource.getContents().add(supplier2);
      msg("Clear contents");
      resource.getContents().clear();
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      assertEquals(0, resource.getContents().size());
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    assertEquals(0, resource.getContents().size());
  }

  public void testUnsetValue() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(null);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      assertNull(supplier.getName());
      push(transaction, pushTransaction);
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    Supplier supplier = (Supplier)resource.getContents().get(0);
    assertNull(supplier.getName());
  }

  public void testUnsetValueWithoutPushTX() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(null);
      msg("Commit");
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(resourcePath));
    Supplier supplier = (Supplier)resource.getContents().get(0);
    assertNull(supplier.getName());
  }

  public void testConflictWithRemovedObject() throws Exception
  {
    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(null);
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Open transaction & commit changes to repo");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      resource.getContents().remove(0);
      transaction.commit();
      session.close();
    }

    msg("Reload previous local session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    try
    {
      openPushTransaction(transaction, file);
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
      CDOPushTransaction pushTransaction = openPushTransaction(transaction);
      file = pushTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName("" + System.currentTimeMillis());
      msg("Commit");
      pushTransaction.commit();
      session.close();
    }

    {
      msg("Open transaction & commit changes to repo");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName("" + System.currentTimeMillis());
      transaction.commit();
      session.close();
    }

    msg("Reload previous local session");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOPushTransaction pushTransaction = openPushTransaction(transaction, file);
    assertEquals(true, transaction.hasConflict());

    try
    {
      pushTransaction.push();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // SUCCESS
    }
  }

  protected CDOPushTransaction openPushTransaction(CDOTransaction transaction) throws IOException
  {
    return new CDOPushTransaction(transaction);
  }

  protected CDOPushTransaction openPushTransaction(CDOTransaction transaction, File file) throws IOException
  {
    return new CDOPushTransaction(transaction, file);
  }

  protected void push(CDOTransaction transaction, CDOPushTransaction pushTransaction) throws CommitException
  {
    msg("Publish previous modifications");
    pushTransaction.push();
    assertEquals(false, transaction.isDirty());
  }

  private void populateRepository()
  {
    msg("Populate the repository the classic way");
    msg("Create resource");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(resourcePath));

    msg("Populate resource");
    Supplier supplier0 = getModel1Factory().createSupplier();
    supplier0.setName(supplierName);
    resource.getContents().add(supplier0);
    Company company0 = getModel1Factory().createCompany();
    resource.getContents().add(company0);

    try
    {
      msg("Commit");
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    session.close();
  }
}
