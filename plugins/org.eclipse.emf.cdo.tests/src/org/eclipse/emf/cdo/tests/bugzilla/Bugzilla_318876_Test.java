/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * Bugzilla 318876 - Mechanism for avoiding dangling refs can introduce spurious conflicts</p>
 * http://bugs.eclipse.org/318876</p>
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_318876_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    final CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource(getResourcePath("/r1")); //$NON-NLS-1$

    PurchaseOrder po1 = getModel1Factory().createPurchaseOrder();
    po1.setDate(new Date());
    r1.getContents().add(po1);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.getPurchaseOrders().add(po1);
    r1.getContents().add(supplier);

    tx.commit();

    // Remove po2 in another session
    doSecondSession();

    // Make the supplier dirty so that it will be scanned for dangling refs
    // during processing of the refresh results
    supplier.setName("Supplier");

    session.refresh();

    CDOState state = CDOUtil.getCDOObject(po1).cdoState();
    System.out.println("--> purchaseOrder state (should be INVALID) = " + state);
    assertEquals(CDOState.INVALID, state);

    tx.close();
    session.close();
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(getResourcePath("/r1")); //$NON-NLS-1$

    // Detach the po
    PurchaseOrder po = (PurchaseOrder)r1.getContents().get(0);
    EcoreUtil.delete(po);

    tx.commit();
    tx.close();
    session.close();
  }
}
