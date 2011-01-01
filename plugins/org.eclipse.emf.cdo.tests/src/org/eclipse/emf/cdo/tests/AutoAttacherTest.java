/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOAutoAttacher;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Simon McDuff
 */
public class AutoAttacherTest extends AbstractCDOTest
{
  public AutoAttacherTest()
  {
  }

  public void testSimple() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    new CDOAutoAttacher(transaction);
    CDOResource resource1 = transaction.getOrCreateResource("/test1");
    Product1 product = getModel1Factory().createProduct1();
    product.setName("product");

    {
      assertTransient(product);
      resource1.getContents().add(product);
      assertEquals(resource1, product.eResource());
      assertNew(product, transaction);
    }

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();

    {
      assertTransient(orderDetail);
      product.getOrderDetails().add(orderDetail);
      assertNew(orderDetail, transaction);
    }

    transaction.close();
    session.close();
  }

  public void testAddingObjectAndCrawl() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    new CDOAutoAttacher(transaction);
    CDOResource resource1 = transaction.getOrCreateResource("/test1");

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    supplier.getPurchaseOrders().add(purchaseOrder);

    assertTransient(supplier);

    resource1.getContents().add(supplier);

    assertNew(supplier, transaction);
    assertNew(purchaseOrder, transaction);

    transaction.close();
    session.close();
  }
}
