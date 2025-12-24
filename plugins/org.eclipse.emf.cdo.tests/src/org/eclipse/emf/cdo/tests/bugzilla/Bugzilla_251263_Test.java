/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * EOpposite reference is not removed
 * <p>
 * See bug 251263
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_251263_Test extends AbstractCDOTest
{
  public void testEOpposite_AdjustMany() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());

    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);

    assertEquals(0, CDOUtil.getCDOObject(purchaseOrder).cdoRevision().getVersion());
    transaction1.commit();
    assertEquals(1, CDOUtil.getCDOObject(purchaseOrder).cdoRevision().getVersion());

    res.getContents().remove(purchaseOrder);
    Supplier supplier2 = purchaseOrder.getSupplier();
    PurchaseOrder purchaseOrder2 = supplier.getPurchaseOrders().get(0);
    assertSame(purchaseOrder2, purchaseOrder);
    assertSame(supplier2, supplier);
    assertTransient(purchaseOrder2);

    try
    {
      transaction1.commit();
      fail("Dangling Reference Exception expected");
    }
    catch (Exception expected)
    {
      // Success
    }

    assertTransient(purchaseOrder2);

    CDOResource resB = transaction1.createResource(getResourcePath("testB"));
    resB.getContents().add(purchaseOrder2);

    assertEquals(1, CDOUtil.getCDOObject(purchaseOrder2).cdoRevision().getVersion());
    transaction1.commit();
    assertEquals(2, CDOUtil.getCDOObject(purchaseOrder2).cdoRevision().getVersion());
  }

  public void testEOpposite_AdjustSingleRef() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);
    transaction1.commit();

    res.getContents().remove(supplier);

    Supplier supplier2 = purchaseOrder.getSupplier();
    PurchaseOrder purchaseOrder2 = supplier.getPurchaseOrders().get(0);
    assertSame(purchaseOrder2, purchaseOrder);
    assertSame(supplier2, supplier);

    try
    {
      transaction1.commit();
      fail("Should have dangling reference");
    }
    catch (Exception ignore)
    {
    }

    CDOResource resB = transaction1.createResource(getResourcePath("testB"));
    resB.getContents().add(supplier2);
    transaction1.commit();
  }

  public void testEOpposite_AdjustSingleRef_NoCommit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);
    res.getContents().remove(supplier);
    Supplier supplier2 = purchaseOrder.getSupplier();
    PurchaseOrder purchaseOrder2 = supplier.getPurchaseOrders().get(0);
    assertSame(purchaseOrder2, purchaseOrder);
    assertSame(supplier2, supplier);

    try
    {
      transaction1.commit();
      fail("Should have dangling reference");
    }
    catch (Exception ignore)
    {
    }

    CDOResource resB = transaction1.createResource(getResourcePath("testB"));
    resB.getContents().add(supplier2);
    transaction1.commit();
  }

  public void testEOpposite_AdjustMany_Persisted() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);

    transaction1.commit();

    supplier.getPurchaseOrders().add(purchaseOrder);
    res.getContents().remove(purchaseOrder);

    Supplier supplier2 = purchaseOrder.getSupplier();
    PurchaseOrder purchaseOrder2 = supplier.getPurchaseOrders().get(0);
    assertSame(purchaseOrder2, purchaseOrder);
    assertSame(supplier2, supplier);

    try
    {
      transaction1.commit();
      fail("Should have dangling reference");
    }
    catch (Exception ignore)
    {
    }

    CDOResource resB = transaction1.createResource(getResourcePath("testB"));
    resB.getContents().add(purchaseOrder2);
    transaction1.commit();
  }
}
