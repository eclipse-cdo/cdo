/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.concurrent.RWOLockManager;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Collections;

/**
 * @author Eike Stepper
 */
public class Bugzilla_385268_Test extends AbstractCDOTest
{
  public void testDetachment() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    supplier.getPurchaseOrders().add(purchaseOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    EList<EObject> contents = resource.getContents();
    contents.add(supplier);
    contents.add(purchaseOrder);

    transaction.commit();

    contents.remove(purchaseOrder);
    assertDirty(supplier, transaction); // supplier must be dirty so that a posible ext-ref can be committed later.
  }

  public void testReattachment() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    supplier.getPurchaseOrders().add(purchaseOrder);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    EList<EObject> contents = resource.getContents();
    contents.add(supplier);
    contents.add(purchaseOrder);

    transaction.commit();

    contents.remove(purchaseOrder);
    contents.add(purchaseOrder);
    assertClean(supplier, transaction);
  }

  public void testReattachment2() throws Exception
  {
    System.err.println("\nMain");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    Company company = getModel1Factory().createCompany();
    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    EList<PurchaseOrder> purchaseOrders = company.getPurchaseOrders();
    purchaseOrders.add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);
    company.getSuppliers().add(supplier);

    resource.getContents().add(company);
    transaction.commit();

    System.err.println("\nRemoteUser");
    RemoteUser remoteUser = new RemoteUser();
    remoteUser.changeSupplierAndLockIt();

    System.err.println("\nMain");
    for (int i = 0; i < 1000; i++)
    {
      purchaseOrders.clear();
      purchaseOrders.add(purchaseOrder);
    }

    transaction.commit();
    remoteUser.dispose();
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteUser
  {
    private CDOTransaction transaction;

    private CDOResource resource;

    public RemoteUser()
    {
      CDOSession session = openSession();
      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("test1"));
    }

    public void dispose()
    {
      transaction = null;
      resource = null;
    }

    public void changeSupplierAndLockIt() throws InterruptedException
    {
      Company company = (Company)resource.getContents().get(0);
      Supplier supplier = company.getSuppliers().get(0);
      CDOObject cdoObject = CDOUtil.getCDOObject(supplier);

      transaction.lockObjects(Collections.singleton(cdoObject), RWOLockManager.LockType.WRITE, DEFAULT_TIMEOUT);
    }
  }
}
