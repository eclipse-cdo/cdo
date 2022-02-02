/*
 * Copyright (c) 2017, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.List;

/**
 * Bug 517168: Multiple revision instances are loaded during branch switchCDOView.queryXRef
 * returns invalid values when an object is removed on a newly created CDO branch.
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_517168_Test extends AbstractCDOTest
{
  public void testDeletedXRef_SingleValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Initialize main branch with two objects, an order detail with a given product.
    Product1 product = getModel1Factory().createProduct1();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);

    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);
    transaction.commit();

    // Create a branch and open a transaction on it.
    CDOBranch derivedBranch = transaction.getBranch().createBranch(getBranchName("Derived"));
    transaction = session.openTransaction(derivedBranch);
    orderDetail = transaction.getObject(orderDetail);
    product = transaction.getObject(product);

    // Remove the order detail and commit the removal.
    EcoreUtil.delete(orderDetail);
    transaction.commit();

    // Check XRef API on derived branch after object removal:
    // An empty result is supposed to be retrieved at this point.
    List<CDOObjectReference> xRefs = transaction.queryXRefs(CDOUtil.getCDOObject(product));
    assertEquals(0, xRefs.size());
  }

  public void testDeletedXRef_ManyValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Initialize main branch with two objects, an order detail with a given product.
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.getPurchaseOrders().add(purchaseOrder);

    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(purchaseOrder);
    resource.getContents().add(salesOrder);
    transaction.commit();

    // Create a branch and open a transaction on it.
    CDOBranch derivedBranch = transaction.getBranch().createBranch(getBranchName("Derived"));
    transaction = session.openTransaction(derivedBranch);
    salesOrder = transaction.getObject(salesOrder);
    purchaseOrder = transaction.getObject(purchaseOrder);

    // Check XRef API on derived branch:
    // The customer is supposed to be retrieved at this point.
    List<CDOObjectReference> xRefs = transaction.queryXRefs(CDOUtil.getCDOObject(purchaseOrder));
    assertEquals(1, xRefs.size());
    assertEquals(salesOrder, xRefs.get(0).getSourceObject());

    // Remove the order detail and commit the removal.
    EcoreUtil.delete(salesOrder);
    transaction.commit();

    // Check XRef API on derived branch after object removal:
    // An empty result is supposed to be retrieved at this point.
    xRefs = transaction.queryXRefs(CDOUtil.getCDOObject(purchaseOrder));
    assertEquals(0, xRefs.size());
  }

  public void testDuplicateXRef_SingleValue() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Initialize main branch with two objects, an order detail with a given product.
    Product1 product = getModel1Factory().createProduct1();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);

    CDOResource resource = transaction.createResource(getResourcePath("res"));
    resource.getContents().add(product);
    resource.getContents().add(orderDetail);
    transaction.commit();

    // Create a branch and open a transaction on it.
    CDOBranch derivedBranch = transaction.getBranch().createBranch(getBranchName("Derived"));
    transaction = session.openTransaction(derivedBranch);
    orderDetail = transaction.getObject(orderDetail);
    product = transaction.getObject(product);

    // Change an attribute of the order detail and commit the change.
    orderDetail.setPrice(47.11f);
    transaction.commit();

    // Check XRef API on derived branch after object removal:
    // An empty result is supposed to be retrieved at this point.
    List<CDOObjectReference> xRefs = transaction.queryXRefs(CDOUtil.getCDOObject(product));
    assertEquals(1, xRefs.size());
  }
}
