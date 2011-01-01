/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;

import java.util.Iterator;

/**
 * @author Simon McDuff
 */
public class ChunkingWithMEMTest extends AbstractCDOTest
{
  public void testReadNative() throws Exception
  {
    CDORevision revisionToRemove = null;

    {
      msg("Opening session");
      CDOSession session = openSession();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);
      revisionToRemove = CDOUtil.getCDOObject(customer).cdoRevision();
      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      msg("Committing");
      transaction.commit();
      session.close();
    }

    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    revisionManager.getCache().removeRevision(revisionToRemove.getID(), revisionToRemove);

    msg("Opening session");
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();
    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    int i = 0;
    for (Iterator<SalesOrder> it = salesOrders.iterator(); it.hasNext();)
    {
      msg(i++);
      SalesOrder salesOrder = it.next();
      msg(salesOrder);
    }

    session.close();
  }

  public void testWriteNative() throws Exception
  {
    CDORevision revisionToRemove = null;

    {
      msg("Opening session");
      CDOSession session = openSession();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      msg("Committing");
      transaction.commit();
      revisionToRemove = CDOUtil.getCDOObject(customer).cdoRevision();
      session.close();
    }

    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    revisionManager.getCache().removeRevision(revisionToRemove.getID(), revisionToRemove);

    msg("Opening session");
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(salesOrder);
      salesOrders.set(i, salesOrder);
    }

    transaction.commit();
    session.close();
  }
}
