/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Maxime Porhel - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Date;

/**
 * Bug 492898 - CDOObject modifies Store even for Touch notifications.
 *
 * @author Maxime Porhel
 */
public class Bugzilla_492898_Test extends AbstractCDOTest
{
  public void testTouchModificationOnReferenceToExternalObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));

    ResourceSet resourceSet = resource.getResourceSet();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

    URI uri = URI.createFileURI(createTempFile("tempFile", ".xmi").getCanonicalPath());
    Resource externalResource = resourceSet.createResource(uri);

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    Supplier externalSupplier = getModel1Factory().createSupplier();

    resource.getContents().add(purchaseOrder);
    externalResource.getContents().add(externalSupplier);

    purchaseOrder.setDate(new Date());
    externalSupplier.getPurchaseOrders().add(purchaseOrder);
    externalSupplier.setName("TheExternalSupplier");

    transaction.commit();

    assertDirty(purchaseOrder, transaction, false);
    assertTransient(externalSupplier);

    purchaseOrder.setSupplier(externalSupplier);

    assertDirty(purchaseOrder, transaction, false);
    assertTransient(externalSupplier);

    transaction.commit();

    assertDirty(purchaseOrder, transaction, false);
    assertTransient(externalSupplier);
  }

  public void testReferenceToExternalObject() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));

    ResourceSet resourceSet = resource.getResourceSet();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

    URI uri = URI.createFileURI(createTempFile("tempFile", ".xmi").getCanonicalPath());
    Resource externalResource = resourceSet.createResource(uri);

    Supplier externalSupplier = getModel1Factory().createSupplier();
    externalSupplier.setName("TheExternalSupplier");
    externalResource.getContents().add(externalSupplier);

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    purchaseOrder.setDate(new Date());
    purchaseOrder.setSupplier(externalSupplier);
    resource.getContents().add(purchaseOrder);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("TheSupplier");
    resource.getContents().add(supplier);

    transaction.commit();

    assertDirty(purchaseOrder, transaction, false);
    assertDirty(supplier, transaction, false);
    assertTransient(externalSupplier);

    // First modification of the reference
    purchaseOrder.setSupplier(supplier);

    assertDirty(purchaseOrder, transaction, true);
    assertDirty(supplier, transaction, true);
    assertTransient(externalSupplier);

    // Second modification (equivalent to undo, same value than last clean revision)
    purchaseOrder.setSupplier(externalSupplier);

    assertDirty(purchaseOrder, transaction, false);
    assertDirty(supplier, transaction, false);
    assertTransient(externalSupplier);

    transaction.commit();

    assertDirty(purchaseOrder, transaction, false);
    assertDirty(supplier, transaction, false);
    assertTransient(externalSupplier);

    purchaseOrder.setSupplier(supplier);

    assertDirty(purchaseOrder, transaction, true);
    assertDirty(supplier, transaction, true);
    assertTransient(externalSupplier);

    transaction.commit();

    assertDirty(purchaseOrder, transaction, false);
    assertDirty(supplier, transaction, false);
    assertTransient(externalSupplier);
  }

  private static void assertDirty(EObject eObject, CDOView view, boolean dirty)
  {
    if (dirty)
    {
      assertDirty(eObject, view);
    }
    else
    {
      assertClean(eObject, view);
    }

    assertEquals(dirty, view.isDirty());
  }
}
