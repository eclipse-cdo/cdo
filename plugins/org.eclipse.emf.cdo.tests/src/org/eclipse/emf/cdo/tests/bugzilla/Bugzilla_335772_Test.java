/*
 * Copyright (c) 2011-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Bugzilla_335772_Test extends AbstractCDOTest
{
  private Model1Factory factory;

  private Customer customer;

  private Product1 product1;

  private Product1 product2;

  private SalesOrder order1;

  private SalesOrder order2;

  private HashMap<Product1, SalesOrder> map;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    factory = getModel1Factory();

    customer = factory.createCustomer();
    product1 = factory.createProduct1();
    product1.setName("product1");
    product2 = factory.createProduct1();
    product2.setName("product2");
    order1 = factory.createSalesOrder();
    order1.setId(1);
    order2 = factory.createSalesOrder();
    order2.setId(2);

    map = new HashMap<>();
    map.put(product1, order1);
    customer.eSet(getModel1Package().getCustomer_OrderByProduct(), map);

    // delegateEList is not empty at this point
    customer.getOrderByProduct().get(0);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    factory = null;
    customer = null;
    product1 = null;
    product2 = null;
    order1 = null;
    order2 = null;
    map = null;
    super.doTearDown();
  }

  public void testMapEntryReplacement() throws Exception
  {
    replaceMapEntry();

    // if delegateEList is empty at this point following line will throw IndexOutOfBoundsException
    customer.getOrderByProduct().get(0);
  }

  public void testMapEntryAddition() throws Exception
  {
    addMapEntry();

    // if delegateEList has only one element at this point following line will throw IndexOutOfBoundsException
    customer.getOrderByProduct().get(1);
  }

  public void testMapEntryReplacementPersistence() throws Exception
  {
    replaceMapEntry();
    assertEquals((Integer)2, persistAndRetrieveOrderIdForProduct1());
  }

  public void testMapEntryAdditionPersistence() throws Exception
  {
    addMapEntry();
    assertEquals((Integer)1, persistAndRetrieveOrderIdForProduct1());
  }

  private void replaceMapEntry()
  {
    map.put(product1, order2);
    assertEquals(1, map.size());
    customer.eSet(getModel1Package().getCustomer_OrderByProduct(), map);
  }

  private void addMapEntry()
  {
    map.put(product2, order2);
    assertEquals(2, map.size());
    customer.eSet(getModel1Package().getCustomer_OrderByProduct(), map);
  }

  private Integer persistAndRetrieveOrderIdForProduct1() throws CommitException
  {
    CDOSession session = openSession();

    try
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource customerResource;

      try
      {
        customerResource = transaction.getOrCreateResource(getResourcePath("/customer")); //$NON-NLS-1$
        EList<EObject> customerResourceContents = customerResource.getContents();
        customerResourceContents.clear();
        customerResourceContents.add(customer);
        customerResourceContents.add(product1);
        customerResourceContents.add(product2);
        customerResourceContents.add(order1);
        customerResourceContents.add(order2);

        transaction.commit();
      }
      finally
      {
        transaction.close();
      }

      transaction = session.openTransaction();

      try
      {
        customerResource = transaction.getResource(getResourcePath("/customer")); //$NON-NLS-1$

        Customer customer = (Customer)customerResource.getContents().get(0);
        EMap<Product1, SalesOrder> orderByProduct = customer.getOrderByProduct();
        orderByProduct.get(product1);

        CDOID product1CDOID = CDOUtil.getCDOObject(product1).cdoID();

        for (Map.Entry<Product1, SalesOrder> entry : orderByProduct.entrySet())
        {
          if (CDOUtil.getCDOObject(entry.getKey()).cdoID() == product1CDOID)
          {
            return entry.getValue().getId();
          }
        }

        return null;
      }
      finally
      {
        transaction.close();
      }
    }
    finally
    {
      session.close();
    }
  }
}
