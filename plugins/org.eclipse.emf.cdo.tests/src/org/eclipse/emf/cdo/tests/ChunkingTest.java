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
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class ChunkingTest extends AbstractCDOTest
{
  public void testReadNative() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();

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
    }

    // ************************************************************* //

    msg("Opening session");
    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(10);

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
  }

  public void testWriteNative() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();

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
    }

    // ************************************************************* //

    msg("Opening session");
    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(10);

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
  }

  public void testChunkWithTemporaryObject() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      msg("Committing");
      transaction.commit();
    }

    // ************************************************************* //

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.setLoadRevisionCollectionChunkSize(10);

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");
    resource.getContents().add(customer);
    for (EObject element : resource.getContents())
    {
      msg(element);
    }

    transaction.commit();
  }
}
