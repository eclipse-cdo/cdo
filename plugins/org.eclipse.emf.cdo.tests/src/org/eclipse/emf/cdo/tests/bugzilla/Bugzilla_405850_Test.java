/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;

/**
 * Bug 405850 - CDORevision.get(feature, -1) should throw an IndexOutOfBoundsException for many-valued features
 *
 * @author Eike Stepper
 */
public class Bugzilla_405850_Test extends AbstractCDOTest
{
  public void testIndexOutOfBounds() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    resource.getContents().add(purchaseOrder);

    Supplier supplier = getModel1Factory().createSupplier();
    EList<PurchaseOrder> purchaseOrders = supplier.getPurchaseOrders();
    purchaseOrders.add(purchaseOrder);
    resource.getContents().add(supplier);

    transaction.commit();

    try
    {
      purchaseOrders.get(-1);
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // SUCCEED
    }

    try
    {
      purchaseOrders.get(1);
      fail("IndexOutOfBoundsException expected");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // SUCCEED
    }
  }
}
