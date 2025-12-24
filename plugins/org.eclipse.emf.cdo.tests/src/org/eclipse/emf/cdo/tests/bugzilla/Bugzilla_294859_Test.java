/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.internal.cdo.transaction.CDOSavepointImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Date;

/**
 * Bug 294859 - Deltas for local changes to objects in CONFLICT state, are not added to tx
 *
 * @author Caspar De Groot
 */
public class Bugzilla_294859_Test extends AbstractCDOTest
{
  private static String RESOURCE_NAME = "/r1";

  public void test() throws CommitException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction tx = session.openTransaction();

    // Do NOT use a conflict resolver

    CDOResource r1 = tx.createResource(getResourcePath(RESOURCE_NAME));

    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    company1.getPurchaseOrders().add(purchaseOrder);
    r1.getContents().add(company1);
    r1.getContents().add(company2);
    tx.commit();

    // Touch the purchaseOrder
    purchaseOrder.setDate(new Date(1000L));

    doSecondSession();

    // Convenience
    CDOObject cdoPurchaseOrder = CDOUtil.getCDOObject(purchaseOrder);

    // The purchaseOrder was touched in both this and 2nd session;
    // we refresh now go cause a conflict
    session.refresh();
    assertEquals(true, cdoPurchaseOrder.cdoState() == CDOState.CONFLICT);

    // Touch it again
    purchaseOrder.setDate(new Date(3000L));

    // Verify that the delta representing the LAST LOCAL change is in the TX
    //
    CDOSavepointImpl savepoint = (CDOSavepointImpl)((InternalCDOTransaction)tx).getLastSavepoint();
    CDORevisionDelta revDelta = savepoint.getRevisionDeltas2().get(cdoPurchaseOrder.cdoID());
    assertNotNull(revDelta);
    assertEquals(true, revDelta.getFeatureDeltas().size() == 1);
    CDOFeatureDelta featDelta = revDelta.getFeatureDeltas().get(0);
    EAttribute eAttr = getModel1Factory().getModel1Package().getPurchaseOrder_Date();
    assertEquals(true, featDelta instanceof CDOSetFeatureDelta);
    CDOSetFeatureDelta setFeatDelta = (CDOSetFeatureDelta)featDelta;
    assertSame(eAttr, setFeatDelta.getFeature());
    assertSame(CDOFeatureDelta.Type.SET, setFeatDelta.getType());
    assertEquals(new Date(3000L), setFeatDelta.getValue());

    try
    {
      tx.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // Correct
    }

    tx.close();
    session.close();
  }

  private void doSecondSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getResource(getResourcePath(RESOURCE_NAME));
    Company c = (Company)r1.getContents().get(0);

    // Touch the purchaseOrder
    c.getPurchaseOrders().get(0).setDate(new Date(2000L));

    tx.commit();
    tx.close();
    session.close();
  }
}
