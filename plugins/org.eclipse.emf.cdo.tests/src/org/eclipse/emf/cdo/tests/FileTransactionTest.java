/*
 * Copyright (c) 2009-2013, 2017, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOFileTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Gonzague Reydet
 */
public class FileTransactionTest extends AbstractCDOTest
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

  public void testLocalCommitAndPushSafety() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    File file = File.createTempFile("cdo-file-test-", ".dat");
    file.delete();

    try
    {
      CDOFileTransaction fileTransaction = CDOUtil.createFileTransaction(transaction, file);
      assertSame(file, fileTransaction.getFile());
      fileTransaction.commit();
      assertTrue(file.exists());

      try
      {
        Callable<Object> callable = () -> null;
        fileTransaction.commit(callable, 1, null);
        fail("Callable commit must be rejected");
      }
      catch (UnsupportedOperationException expected)
      {
        // SUCCESS
      }
    }
    finally
    {
      file.delete();
      session.close();
    }
  }

  public void testUpdateExistingObject() throws Exception
  {
    String newName = "supplier" + System.currentTimeMillis();

    {
      msg("Open session & local transaction");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(newName);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Remove an existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      resource.getContents().remove(supplier);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("supplier" + System.currentTimeMillis());
      resource.getContents().add(supplier);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      Company company = (Company)resource.getContents().get(1);
      company.getCategories().add(category);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      Company company = (Company)resource.getContents().get(1);
      company.getCategories().add(category);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);

      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      CDOResource resource = transaction.getResource(getResourcePath(resourcePath));
      resource.getContents().add(category);
      msg("Commit");
      fileTransaction.commit();

      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      Company company = (Company)resource.getContents().get(1);
      company.getCategories().add(category);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDORevisionDelta delta = transaction.getRevisionDeltas().values().iterator().next();
      assertNotNull(delta);

      msg("Create a new element");
      Category category = getModel1Factory().createCategory();
      CDOResource resource = transaction.getResource(getResourcePath(resourcePath));
      resource.getContents().add(category);
      msg("Commit");
      fileTransaction.commit();

      msg("Create a new element");
      category = getModel1Factory().createCategory();
      resource.getContents().add(category);
      msg("Commit");
      fileTransaction.commit();

      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
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
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      assertEquals(1, transaction.getRevisionDeltas().size());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      assertEquals(4, resource.getContents().size());
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
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
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      assertEquals(supplierName, ((Supplier)resource.getContents().get(1)).getName());
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Create a new elements");
      Supplier supplier2 = getModel1Factory().createSupplier();
      supplier2.setName("supplier" + System.currentTimeMillis());
      resource.getContents().add(supplier2);
      msg("Clear contents");
      resource.getContents().clear();
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      assertEquals(0, resource.getContents().size());
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(null);
      msg("Commit");
      fileTransaction.commit();
      session.close();
    }

    {
      msg("Reload previous local session");
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
      assertEquals(true, transaction.isDirty());
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      Supplier supplier = (Supplier)resource.getContents().get(0);
      assertNull(supplier.getName());
      push(transaction, fileTransaction);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName(null);
      msg("Commit");
      fileTransaction.commit();
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
      openFileTransaction(transaction, file);
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
      CDOFileTransaction fileTransaction = openFileTransaction(transaction);
      file = fileTransaction.getFile();
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      msg("Make a diff in existing element");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      supplier.setName("" + System.currentTimeMillis());
      msg("Commit");
      fileTransaction.commit();
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
    CDOFileTransaction fileTransaction = openFileTransaction(transaction, file);
    assertEquals(true, transaction.hasConflict());

    try
    {
      fileTransaction.push();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // SUCCESS
    }
  }

  public void testFileTransactionDirtyEventsAndUnsafeCommitOverloads() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOFileTransaction fileTransaction = openFileTransaction(transaction);
    file = fileTransaction.getFile();
    List<IEvent> events = new ArrayList<>();
    fileTransaction.addListener(events::add);

    try
    {
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(resourcePath));
      EObject supplier = resource.getContents().get(0);
      supplier.eSet(supplier.eClass().getEStructuralFeature("name"), "dirty-" + System.currentTimeMillis());

      assertTrue(events.stream().anyMatch(CDOTransactionStartedEvent.class::isInstance));
      fileTransaction.commit();
      assertTrue(events.stream().anyMatch(CDOTransactionFinishedEvent.class::isInstance));
      for (IEvent event : events)
      {
        if (event.getSource() == fileTransaction)
        {
          assertTrue(event instanceof CDOTransactionStartedEvent || event instanceof CDOTransactionFinishedEvent);
        }
        else
        {
          assertSame(transaction, event.getSource());
        }
      }

      Callable<Object> callable = () -> null;
      assertUnsupported(() -> fileTransaction.commit(callable, 1, null));
      assertUnsupported(() -> fileTransaction.commit((Runnable)() -> {
      }, 1, null));
      assertUnsupported(() -> fileTransaction.commitAndClose(null, false));
      assertUnsupported(fileTransaction::rollback);
    }
    finally
    {
      session.close();
    }
  }

  private static void assertUnsupported(CheckedOperation operation) throws Exception
  {
    try
    {
      operation.run();
      fail("UnsupportedOperationException expected");
    }
    catch (UnsupportedOperationException expected)
    {
      // SUCCESS
    }
  }

  @FunctionalInterface
  private interface CheckedOperation
  {
    void run() throws Exception;
  }

  protected CDOFileTransaction openFileTransaction(CDOTransaction transaction) throws IOException
  {
    return CDOUtil.createFileTransaction(transaction);
  }

  protected CDOFileTransaction openFileTransaction(CDOTransaction transaction, File file) throws IOException
  {
    return CDOUtil.createFileTransaction(transaction, file);
  }

  protected void push(CDOTransaction transaction, CDOFileTransaction fileTransaction) throws CommitException
  {
    msg("Publish previous modifications");
    fileTransaction.push();
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
