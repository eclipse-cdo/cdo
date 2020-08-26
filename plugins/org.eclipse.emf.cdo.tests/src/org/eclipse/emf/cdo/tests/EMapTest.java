/*
 * Copyright (c) 2009-2012, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Test EMap with an EObject in both the key and value
 *
 * @author Martin Taal
 */
public class EMapTest extends AbstractCDOTest
{
  private static final int NUM_OF_CUSTOMERS = 5;

  private static final int NUM_OF_SALES_ORDERS = 5;

  public void testEMap() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    createTestSet(session);

    msg("Opening transaction for querying");
    CDOTransaction transaction = session.openTransaction();

    Resource res = transaction.getResource(getResourcePath("/test1"));
    for (EObject eObject : res.getContents())
    {
      if (eObject instanceof Customer)
      {
        Customer customer = (Customer)eObject;
        EMap<Product1, SalesOrder> orderByProduct = customer.getOrderByProduct();
        for (Product1 product : orderByProduct.keySet())
        {
          assertEquals(true, res.getContents().contains(product));
          SalesOrder order = orderByProduct.get(product);
          assertNotNull(order);
          assertEquals(product.getName(), order.getId() + "");
          assertEquals(true, res.getContents().contains(order));
        }
      }
    }

    transaction.commit();
    enableConsole();
  }

  private void createTestSet(CDOSession session)
  {
    disableConsole();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    fillResource(resource);

    try
    {
      msg("Committing");
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    enableConsole();
  }

  private void fillResource(Resource resource)
  {
    msg("Creating Testset");
    for (int i = 0; i < NUM_OF_CUSTOMERS; i++)
    {
      final Customer customer = getModel1Factory().createCustomer();
      customer.setCity("City " + i);
      customer.setName(i + "");
      customer.setStreet("Street " + i);
      resource.getContents().add(customer);

      for (int k = 0; k < NUM_OF_SALES_ORDERS; k++)
      {
        Product1 product = getModel1Factory().createProduct1();
        product.setDescription("Description " + k);
        product.setName("" + (i * 10 + k));
        product.setVat(VAT.VAT15);

        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setId(i * 10 + k);

        resource.getContents().add(salesOrder);
        resource.getContents().add(product);

        customer.getOrderByProduct().put(product, salesOrder);
      }
    }
  }
}
