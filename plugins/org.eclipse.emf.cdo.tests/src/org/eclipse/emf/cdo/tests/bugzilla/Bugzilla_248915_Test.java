/*
 * Copyright (c) 2008-2012, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Paul Richardson - initial API and implementation
 *    Simon McDuff - maintenance
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * Resources fetched using CDOViewImpl.getResource(getResourcePath(CDOID)) not added to ResourceSet
 * <p>
 * See bug 248915
 *
 * @author Paul Richardson
 */
public class Bugzilla_248915_Test extends AbstractCDOTest
{
  public void testBugzilla_248915_IncompleteResource() throws Exception
  {
    /* 1) Open first session ready to populate the CDO Server with the data */
    CDOSession session1 = openSession();
    /* 2) Open first transaction ready to populate the CDO server with the data */
    CDOTransaction transaction1 = session1.openTransaction();

    /*
     * Session has been established so 3) create the Supplier resource and 4) create the Purchase Order resource.
     */
    CDOResource supplierResource = transaction1.createResource(getResourcePath("/supplierResource"));
    CDOResource poResource = transaction1.createResource(getResourcePath("/poResource"));

    /* Create the supplier and add it to its respective resource */
    Supplier mySupplier = getModel1Factory().createSupplier();
    supplierResource.getContents().add(mySupplier);

    /* Create the purchase order and add it to its respective resource */
    PurchaseOrder myPurchaseOrder = getModel1Factory().createPurchaseOrder();
    poResource.getContents().add(myPurchaseOrder);

    /* 5) Reference the purchase order from the supplier */
    mySupplier.getPurchaseOrders().add(myPurchaseOrder);

    /* 6) Commit the transaction thereby saving all the data to the CDO server */
    transaction1.commit();

    /* This transaction and session are now redundent and should be discarded */
    transaction1.close();
    session1.close();
    transaction1 = null;
    session1 = null;
    supplierResource = null;
    poResource = null;
    mySupplier = null;
    myPurchaseOrder = null;

    /* End first phase of persisting the data in the CDO Server */

    /* Start of second phase where the data is fetched from the CDO Server */

    /* 7) Open a completely new session and transaction onto the persisted data */
    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();

    /* 8) Load the supplier from transaction2, fetching it into transaction2's empty resourceSet */
    CDOResource supplierResource2 = transaction2.getResource(getResourcePath("/supplierResource"));
    Supplier savedSupplier = (Supplier)supplierResource2.getContents().get(0);

    if (!isConfig(LEGACY))
    {
      /* Confirm the presence of supplierResource2 in transaction2's resourceSet */
      assertEquals(1, transaction2.getResourceSet().getResources().size());
    }
    else
    {// legacy mode loads the whole tree. So there will be 3 resources
      assertEquals(2, transaction2.getResourceSet().getResources().size());
    }

    for (PurchaseOrder po : savedSupplier.getPurchaseOrders())
    {
      /* I believe that the Purchase Order's resource will be set but that its URI is null */
      assertEquals(true, po.eResource().getURI() != null);
    }

    /*
     * I believe that only supplierResource2 is in transaction2's resourceSet still despite finding the Purchase Order
     * and its resource.
     */
    assertEquals(2, transaction2.getResourceSet().getResources().size());

    transaction2.close();
    session2.close();
    transaction2 = null;
    session2 = null;
  }

  public void testBugzilla_248915_DuplicateID() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();

    CDOResource supplierResource = transaction1.createResource(getResourcePath("/supplierResource"));

    transaction1.commit();
    CDOID resID = CDOUtil.getCDOObject(supplierResource).cdoID();
    transaction1.close();
    session1.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource = (CDOResource)transaction2.getObject(resID);
    CDOResource resource1 = transaction2.getResource(getResourcePath("/supplierResource"));
    assertSame(resource, resource1);
    transaction2.close();
    session2.close();
  }
}
