/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Test EMap in combination with auditing.
 *
 * @author Martin Taal
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class AuditEMapTest extends AbstractCDOTest
{

  public void testAuditedEMap() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    final Map<Long, Map<String, Integer>> testResult = createTestSet(session);

    for (Long timeStamp : testResult.keySet())
    {

      CDOView audit = session.openView(timeStamp);
      {
        CDOResource auditResource = audit.getResource(getResourcePath("/res1"));
        Customer customer = (Customer)auditResource.getContents().get(0);
        checkResult(customer, testResult.get(timeStamp));
      }
    }

    enableConsole();
  }

  private void checkResult(Customer customer, Map<String, Integer> checkMap)
  {
    assertEquals(customer.getOrderByProduct().size(), checkMap.size());
    for (Product1 p : customer.getOrderByProduct().keySet())
    {
      assertEquals(true, checkMap.containsKey(p.getName()));
      assertEquals(checkMap.get(p.getName()), (Integer)customer.getOrderByProduct().get(p).getId());
    }
  }

  private Map<Long, Map<String, Integer>> createTestSet(CDOSession session) throws CommitException
  {
    final Map<Long, Map<String, Integer>> result = new HashMap<>();

    disableConsole();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/res1"));

    final Customer customer = getModel1Factory().createCustomer();
    customer.setCity("City");
    customer.setName("Name");
    customer.setStreet("Street");
    resource.getContents().add(customer);

    {
      long commitTime = transaction.commit().getTimeStamp();
      result.put(commitTime, getResultMap(customer.getOrderByProduct()));
    }

    int cnt = 0;

    // add 2 entries
    {
      addProductAndOrder(cnt++, customer);
      addProductAndOrder(cnt++, customer);
      long commitTime = transaction.commit().getTimeStamp();
      result.put(commitTime, getResultMap(customer.getOrderByProduct()));
    }

    // remove one entry
    {
      customer.getOrderByProduct().remove(1);
      long commitTime = transaction.commit().getTimeStamp();
      result.put(commitTime, getResultMap(customer.getOrderByProduct()));
    }

    // add one entry
    {
      addProductAndOrder(cnt++, customer);
      long commitTime = transaction.commit().getTimeStamp();
      result.put(commitTime, getResultMap(customer.getOrderByProduct()));
    }

    enableConsole();
    return result;
  }

  private Map<String, Integer> getResultMap(EMap<Product1, SalesOrder> orderByProduct)
  {
    final Map<String, Integer> result = new HashMap<>();
    for (Product1 p : orderByProduct.keySet())
    {
      result.put(p.getName(), orderByProduct.get(p).getId());
    }
    return result;
  }

  private void addProductAndOrder(int cnt, Customer customer)
  {
    Product1 product = getModel1Factory().createProduct1();
    product.setDescription("Description " + cnt);
    product.setName("" + cnt);
    product.setVat(VAT.VAT15);

    customer.eResource().getContents().add(product);

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    salesOrder.setId(cnt);
    customer.getOrderByProduct().put(product, salesOrder);

    customer.eResource().getContents().add(salesOrder);
  }
}
