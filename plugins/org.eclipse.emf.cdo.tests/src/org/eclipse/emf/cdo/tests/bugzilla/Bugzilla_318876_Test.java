/*
 * Copyright (c) 2010-2013, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Date;

/**
 * Bug 318876 - Mechanism for avoiding dangling refs can introduce spurious conflicts.
 *
 * @author Caspar De Groot
 */
public class Bugzilla_318876_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    purchaseOrder.setDate(new Date());
    resource.getContents().add(purchaseOrder);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.getPurchaseOrders().add(purchaseOrder);
    resource.getContents().add(supplier);

    transaction.commit();

    // Remove purchaseOrder in another session
    doSecondSession();

    // Make the supplier dirty so that it will be scanned for dangling refs
    // in supplier.getPurchaseOrders() during processing of the refresh results
    supplier.setName("Supplier");

    session.refresh();

    CDOState state = CDOUtil.getCDOObject(purchaseOrder).cdoState();
    assertEquals(CDOState.INVALID_CONFLICT, state);
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/res"));

    // Detach the purchaseOrder
    PurchaseOrder purchaseOrder = (PurchaseOrder)resource.getContents().get(0);
    EcoreUtil.delete(purchaseOrder);

    transaction.commit();
  }
}
