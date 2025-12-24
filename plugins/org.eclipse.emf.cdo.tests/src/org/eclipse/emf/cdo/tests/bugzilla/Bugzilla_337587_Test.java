/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * @author Egidijus Vaisnora
 */
public class Bugzilla_337587_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testRevisionCompare() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 10; i++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    // ************************************************************* //

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 2));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("test1"));

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    salesOrders.set(5, salesOrder);

    CDOObject cdoResource = CDOUtil.getCDOObject(resource);
    InternalCDORevision cleanRevision = ((InternalCDOTransaction)transaction).getCleanRevisions().get(cdoResource);

    try
    {
      cdoResource.cdoRevision().compare(cleanRevision);
      fail("Expected IllegalStateException during comparing EList with CDOProxyElement");
    }
    catch (IllegalStateException expected)
    {
      // expected exception
    }

    transaction.commit();
  }
}
