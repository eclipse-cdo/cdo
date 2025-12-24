/*
 * Copyright (c) 2010-2012, 2016, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.Date;

/**
 * Bug 318844 - CDONotificationBuilder cannot handle mixed OID's/CDOObjects when processing CDOClearFeatureDelta</p>
 * https://bugs.eclipse.org/318844</p>
 *
 * @author Caspar De Groot
 * @since 4.0
 */
public class Bugzilla_318844_Test extends AbstractCDOTest
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

    tx.commit();

    // Create a new supplier but don't commit it
    Supplier supplier = getModel1Factory().createSupplier();
    r1.getContents().add(supplier);

    // Create a reference to po1 but don't commit it
    supplier.getPurchaseOrders().add(po1);

    // Add an adapter so that notification mechanism will be invoked
    r1.eAdapters().add(new TestAdapter());

    // Remove po2 in another session
    doSecondSession();

    // The following call wthrows a CCE if bug 318844 is not fixed
    session.refresh();

    tx.close();
    session.close();
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(getResourcePath("/r1")); //$NON-NLS-1$

    // Detach the purchaseOrder
    r1.getContents().remove(0);

    tx.commit();
    tx.close();
    session.close();
  }
}
