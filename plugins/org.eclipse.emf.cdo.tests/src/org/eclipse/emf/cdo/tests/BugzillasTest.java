/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOFeature;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

import junit.framework.Assert;

/**
 * @author Eike Stepper
 */
public class BugzillasTest extends AbstractCDOTest
{
  /**
   * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=214374
   */
  public void test214374() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    Customer customer = Model1Factory.eINSTANCE.createCustomer();
    salesOrder.setCustomer(customer);

    resource.getContents().add(customer);
    transaction.commit();
    transaction.close();

    CDOTransactionImpl transaction2 = (CDOTransactionImpl)session.openTransaction();
    SalesOrder salesOrder2 = (SalesOrder)transaction2.getObject(salesOrder.cdoID(), true);
    CDORevision salesRevision = salesOrder2.cdoRevision();
    CDOFeature customerFeature = salesRevision.getCDOClass().lookupFeature(
        Model1Package.eINSTANCE.getSalesOrder_Customer().getFeatureID());
    Object value = salesRevision.getData().get(customerFeature, 0);
    Assert.assertEquals(true, value instanceof CDOID);
    transaction2.close();
  }
}
