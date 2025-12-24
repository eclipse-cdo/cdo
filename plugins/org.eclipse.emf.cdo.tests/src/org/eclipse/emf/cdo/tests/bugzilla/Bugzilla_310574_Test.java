/*
 * Copyright (c) 2010-2014, 2017 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOCommonRepository.ListOrdering;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;

import java.util.Random;

/**
 * CDOAddFeatureDelta with null value.
 * <p>
 * See bug 310574
 *
 * @author Eike Stepper
 */
public class Bugzilla_310574_Test extends AbstractCDOTest
{
  private SalesOrder[] createSalesOrders(int number)
  {
    SalesOrder orders[] = new SalesOrder[number];
    for (int i = 0; i < number; i++)
    {
      orders[i] = getModel1Factory().createSalesOrder();
      orders[i].setId(i);
    }

    return orders;
  }

  public void testRemoveFromContainerThenFromReferenceList() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");
    Company company = getModel1Factory().createCompany();

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(7);
    int[] positions;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      resource.getContents().add(company);

      company.getSalesOrders().clear();
      customer.getSalesOrders().clear();

      for (int i = 0; i < order.length; i++)
      {
        company.getSalesOrders().add(order[i]);
        customer.getSalesOrders().add(order[i]);
      }

      transaction.commit();

      company.getSalesOrders().remove(5);
      company.getSalesOrders().remove(3);

      customer.getSalesOrders().remove(5);
      customer.getSalesOrders().remove(3);

      positions = new int[customer.getSalesOrders().size()];
      for (int i = 0; i < customer.getSalesOrders().size(); i++)
      {
        positions[i] = customer.getSalesOrders().get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      for (SalesOrder o : orders)
      {
        System.out.println("b: " + o.getId());
      }

      for (int i = 0; i < orders.size(); i++)
      {
        assertEquals(positions[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }

  /**
   * @since 4.0
   */
  public void testAddAndRemoveWithNull() throws Exception
  {
    // setup connection1.
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    // add initial model.
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    // add and remove an object from category list of company.
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(0, category);
    company.getCategories().remove(0);

    // Inspect the transaction.
    CDORevisionDelta delta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(company).cdoID());
    if (delta != null)
    {
      for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta listFeatureDelta = (CDOListFeatureDelta)featureDelta;
          for (CDOFeatureDelta featureDelta2 : listFeatureDelta.getListChanges())
          {
            if (featureDelta2 instanceof CDOAddFeatureDelta)
            {
              CDOAddFeatureDelta addFeatureDelta = (CDOAddFeatureDelta)featureDelta2;
              assertNotSame(CDOID.NULL, addFeatureDelta.getValue());
            }
          }
        }
      }
    }

    // ignore the changes.
    transaction.rollback();

    // cleanup.
    session.close();
  }

  /**
   * @since 4.0
   */
  public void testOptimizeAddAndRemove() throws Exception
  {
    // setup connection1.
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

    // add initial model.
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction.commit();

    // add and remove an object from category list of company.
    Category category = getModel1Factory().createCategory();

    // add and remove the same object repeatedly.
    for (int i = 0; i < 100; i++)
    {
      company.getCategories().add(0, category);
      company.getCategories().remove(0);
    }

    // Inspect the transaction.
    CDORevisionDelta delta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(company).cdoID());
    if (delta != null)
    {
      for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta listFeatureDelta = (CDOListFeatureDelta)featureDelta;
          assertEquals(0, listFeatureDelta.getListChanges().size());
        }
      }
    }

    // ignore the changes.
    transaction.rollback();

    // cleanup.
    session.close();
  }

  /**
   * @since 4.0
   */
  public void testOptimizeInterleavedAddMoveAndRemove() throws Exception
  {
    // setup connection1.
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    // add initial model.
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    // add and remove an object from category list of company.
    Category aCategory = getModel1Factory().createCategory();
    Category bCategory = getModel1Factory().createCategory();

    // add and remove the same object repeatedly.
    for (int i = 0; i < 100; i++)
    {
      company.getCategories().add(aCategory);
      company.getCategories().add(bCategory);
      company.getCategories().move(0, 1);
      company.getCategories().remove(aCategory);
      company.getCategories().remove(bCategory);
    }

    // Inspect the transaction.
    CDORevisionDelta delta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(company).cdoID());
    if (delta != null)
    {
      for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta listFeatureDelta = (CDOListFeatureDelta)featureDelta;
          assertEquals(0, listFeatureDelta.getListChanges().size());
        }
      }
    }

    // ignore the changes.
    transaction.rollback();

    // cleanup.
    session.close();
  }

  public void testAddAndModifyAndRemoveFromPersistedList() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    res.getContents().add(supplier);
    res.getContents().add(purchaseOrder);
    supplier.getPurchaseOrders().add(purchaseOrder);
    transaction1.commit();

    // This remove will generate a CDOSetFeatureDelta that will be added to a CDOListFeatureDelta. Why?
    res.getContents().remove(purchaseOrder);
    supplier.getPurchaseOrders().remove(purchaseOrder);
    purchaseOrder.setSupplier(null);
    transaction1.commit();
  }

  public void testListChanges07() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(5);
    int[] positions = new int[5];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(order[2]);

      transaction.commit();

      orders.add(0, order[0]);
      orders.move(1, 0);
      orders.add(0, order[1]);
      orders.remove(1);
      orders.add(1, order[2]);
      orders.remove(2);
      orders.move(1, 0);
      orders.move(0, 1);
      orders.remove(1);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      for (int i = 0; i < orders.size(); i++)
      {
        assertEquals(positions[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }

  public void testListChanges06() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(5);
    int[] positions = new int[5];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.add(0, order[2]);
      orders.add(2, order[3]);
      orders.remove(3);
      orders.move(2, 0);
      orders.add(1, order[0]);
      orders.move(2, 1);
      orders.move(3, 0);
      orders.remove(0);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        for (int i = 0; i < orders.size(); i++)
        {
          assertEquals(positions[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testListChanges05() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(5);
    int[] positions = new int[5];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.move(1, 0);
      orders.add(1, order[2]);
      orders.add(0, order[3]);
      orders.move(1, 3);
      orders.move(2, 3);
      orders.add(1, order[4]);
      orders.remove(4);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        for (int i = 0; i < orders.size(); i++)
        {
          assertEquals(positions[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testListChanges04() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);
    int[] positions = new int[4];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.add(0, order[2]);
      orders.move(1, 2);
      orders.move(1, 2);
      orders.remove(2);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        for (int i = 0; i < orders.size(); i++)
        {
          assertEquals(positions[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testListChanges03() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);
    int[] positions = new int[4];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.add(0, order[2]);
      orders.move(0, 1);
      orders.remove(1);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        for (int i = 0; i < orders.size(); i++)
        {
          assertEquals(positions[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testListChanges02() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);
    int[] positions = new int[4];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.move(0, 1);
      orders.add(0, order[2]);
      orders.move(2, 0);
      orders.remove(1);
      orders.remove(0);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      for (int i = 0; i < orders.size(); i++)
      {
        assertEquals(positions[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }

  public void testListChanges01() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);
    int[] positions = new int[4];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.add(0, order[2]);
      orders.move(2, 0);
      orders.remove(0);
      orders.remove(0);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      for (int i = 0; i < orders.size(); i++)
      {
        assertEquals(positions[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }

  public void testAddMoveMoveRemove() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);
    int[] positions = new int[4];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(0, order[0]);
      orders.add(0, order[1]);
      orders.add(1, order[2]);
      orders.add(2, order[3]);
      orders.remove(0);
      orders.add(2, order[1]);
      orders.remove(3);

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        for (int i = 0; i < orders.size(); i++)
        {
          assertEquals(positions[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testMultipleMove() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(5);
    int[] positions = new int[5];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      orders.add(order[0]);
      // 0
      orders.add(order[1]);
      // 0,1
      orders.add(order[2]);
      // 0,1,2
      orders.move(2, 0);
      // 1,2,0
      orders.move(1, 2);
      // 1,0,2
      orders.move(0, 2);
      // 2,1,0
      orders.remove(1);
      // 2,0

      for (int i = 0; i < orders.size(); i++)
      {
        positions[i] = orders.get(i).getId();
      }

      transaction.commit();
      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      // assertEquals(4, orders.size());

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        for (int i = 0; i < orders.size(); i++)
        {
          assertEquals(positions[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testRemoveAdd() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();
      for (int i = 0; i < order.length; i++)
      {
        orders.add(order[i]);
      }

      transaction.commit();

      orders.remove(order[2]);
      orders.add(order[2]);

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      assertEquals(4, orders.size());

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        int[] array = { 0, 1, 3, 2 };
        for (int i = 0; i < array.length; i++)
        {
          assertEquals(array[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testAddRemoveWithAdditionalMoves() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder[] order = createSalesOrders(4);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();
      orders.add(order[0]); // 0
      orders.add(order[1]); // 0,1
      orders.add(order[2]); // 0,1,2
      orders.add(order[3]); // 0,1,2,3
      orders.remove(order[1]); // 0,2,3
      // We should have [order3, order0, order2] after the next move.
      orders.move(0, 2); // 3,0,2
      orders.add(1, order[1]); // 3,1,0,2
      orders.remove(order[1]); // 3,0,2

      transaction.commit();
      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      assertEquals(3, orders.size());

      if (getRepositoryConfig().listOrdering() == ListOrdering.ORDERED)
      {
        int[] array = { 3, 0, 2 };
        for (int i = 0; i < array.length; i++)
        {
          assertEquals(array[i], orders.get(i).getId());
        }
      }

      transaction.close();
      session.close();
    }
  }

  public void testAddRemoveWithAdditionalAdds() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder[] order = createSalesOrders(3);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      customer.getSalesOrders().add(order[0]);
      customer.getSalesOrders().add(order[1]);
      customer.getSalesOrders().remove(order[0]);
      customer.getSalesOrders().add(order[2]);

      transaction.commit();
      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      assertEquals(2, orders.size());

      int[] array = { 1, 2 };
      for (int i = 0; i < array.length; i++)
      {
        assertEquals(array[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }

  public void testOptimizeAddRemove() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order = createSalesOrders(1)[0];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      resource.getContents().add(order);

      for (int i = 0; i < 100; i++)
      {
        customer.getSalesOrders().add(order);
        customer.getSalesOrders().remove(order);
      }

      transaction.commit();
      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      assertEquals(0, orders.size());

      transaction.close();
      session.close();
    }
  }

  public void testOptimizeInterleavedAddRemove() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder[] order = createSalesOrders(2);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      for (int i = 0; i < 100; i++)
      {
        customer.getSalesOrders().add(order[0]);
        customer.getSalesOrders().add(order[1]);
        customer.getSalesOrders().remove(order[0]);
        customer.getSalesOrders().remove(order[1]);
      }

      transaction.commit();
      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      assertEquals(0, orders.size());

      transaction.close();
      session.close();
    }
  }

  public void testOptimizeMove() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    SalesOrder order[] = createSalesOrders(4);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();
      for (int i = 0; i < order.length; i++)
      {
        orders.add(order[i]);
      }

      // 0,1,2,3
      orders.move(0, 2);
      // 2,0,1,3
      orders.remove(0);
      // 0,1,3
      orders.remove(0);
      // 1,3

      transaction.commit();

      session.close();
    }

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      assertEquals(2, orders.size());

      int[] array = { 1, 3 };
      for (int i = 0; i < array.length; i++)
      {
        assertEquals(array[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }

  /**
   * Use this test to generate random list changes. It will loop until and exception is thrown. So DO NOT ENABLE it
   * except if you are working on this bug.
   */
  public void _testRandomAddRemoveMove() throws Exception
  {
    tearDown();
    while (true)
    {
      setUp();
      generateRandomAddRemoveMove();
      tearDown();
    }
  }

  private void generateRandomAddRemoveMove() throws Exception
  {
    // Creates a customer and commits.
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    // Creates a new order and adds/removes it several times before committing.
    int orderSize = 10;
    SalesOrder order[] = createSalesOrders(orderSize);
    int[] positions = new int[orderSize];

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);
      transaction.commit();

      for (int i = 0; i < order.length; i++)
      {
        resource.getContents().add(order[i]);
      }

      EList<SalesOrder> orders = customer.getSalesOrders();

      // Random but not too much.
      long time = System.currentTimeMillis();
      System.out.println("Seed: " + time);
      Random rnd = new Random(time);

      // Pre-feed the list.
      System.out.print("Original list: [ ");
      for (int i = 0; i < orderSize; i++)
      {
        if (rnd.nextBoolean())
        {
          System.out.print(i + " ");
          orders.add(order[i]);
        }
      }

      System.out.println("]");
      transaction.commit();

      for (int i = 0; i < 30; i++)
      {
        // Add/Remove
        if (rnd.nextBoolean())
        {
          // Add
          if (rnd.nextBoolean())
          {
            boolean success = false;
            for (int j = 0; j < order.length; j++)
            {
              if (!orders.contains(order[j]))
              {
                int index = rnd.nextInt(orders.size() == 0 ? 1 : orders.size());
                System.out.println("ADD " + order[j].getId() + " at " + index);
                orders.add(index, order[j]);
                success = true;
                break;
              }
            }
            if (!success)
            {
              int nextInt = rnd.nextInt(orderSize);
              System.out.println("REMOVE " + nextInt);
              orders.remove(nextInt);
            }
          }
          // Remove
          else
          {
            if (orders.size() > 0)
            {
              int nextInt = rnd.nextInt(orders.size());
              System.out.println("REMOVE " + nextInt);
              orders.remove(nextInt);
            }
            else
            { // Not super random but who cares?
              int index = rnd.nextInt(orders.size() == 0 ? 1 : orders.size());
              System.out.println("ADD " + order[0].getId() + " at " + index);
              orders.add(index, order[0]);
            }
          }
        }
        // Move
        else
        {
          int size = orders.size();
          if (size > 1)
          {
            int to = rnd.nextInt(size);
            int from = rnd.nextInt(size);
            System.out.println("MOVE " + from + " => " + to);
            orders.move(to, from);
          }
        }
      }

      // System.out.println("==========");

      // And the result is...
      for (int i = 0; i < positions.length; i++)
      {
        positions[i] = i < orders.size() ? orders.get(i).getId() : -1;
        // System.out.println(i + " => " + positions[i]);
      }

      transaction.commit();

      session.close();
    }

    System.out.println("==========");

    // Checks that the other transaction got the right invalidation.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Customer testCustomer = (Customer)resource.getContents().get(0);
      EList<SalesOrder> orders = testCustomer.getSalesOrders();

      for (int i = 0; i < positions.length && positions[i] != -1; i++)
      {
        System.out.println(positions[i] + " => " + orders.get(i).getId());
      }

      for (int i = 0; i < positions.length && positions[i] != -1; i++)
      {
        assertEquals(positions[i], orders.get(i).getId());
      }

      transaction.close();
      session.close();
    }
  }
}
