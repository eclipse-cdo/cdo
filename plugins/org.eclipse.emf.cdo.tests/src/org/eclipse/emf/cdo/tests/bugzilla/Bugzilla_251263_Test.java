/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * EOpposite reference is not removed
 * <p>
 * See https://bugs.eclipse.org/251263
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_251263_Test extends AbstractCDOTest
{

  public void testEOpposite_AdjustMany() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);
    transaction1.commit();

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
    CDOResource resB = transaction1.createResource("testB");
    resB.getContents().add(purchaseOrder2);
    transaction1.commit();
  }

  public void testEOpposite_AdjustSingleRef() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
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
    CDOResource resB = transaction1.createResource("testB");
    resB.getContents().add(supplier2);
    transaction1.commit();
  }

  public void testEOpposite_AdjustSingleRef_NoCommit() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
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
    CDOResource resB = transaction1.createResource("testB");
    resB.getContents().add(supplier2);
    transaction1.commit();
  }

  public void testEOpposite_AdjustMany_Persisted() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(resourcePath);
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
    CDOResource resB = transaction1.createResource("testB");
    resB.getContents().add(purchaseOrder2);
    transaction1.commit();
  }

}
