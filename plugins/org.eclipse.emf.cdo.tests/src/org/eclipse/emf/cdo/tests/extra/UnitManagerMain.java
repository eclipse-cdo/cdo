/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.extra;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class UnitManagerMain
{
  private static final String RESOURCE_NAME = "test";

  private static final boolean ADD_UNIQUE = true;

  private static long start;

  /**
   * @author Eike Stepper
   */
  public static final class FillRepository
  {
    public static void main(String[] args) throws Exception
    {
      CDOSession session = openSession();

      try
      {
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.createResource(RESOURCE_NAME);

        for (int i = 0; i < 1; i++)
        {
          start("Fill   " + i);
          Company company = Model1Factory.eINSTANCE.createCompany();
          add(resource.getContents(), company);
          fillCompany(company);
          stop();

          start("Commit " + i);
          transaction.commit();
          stop();
        }
      }
      finally
      {
        session.close();
      }
    }

    private static void fillCompany(Company company)
    {
      for (int i = 0; i < 5; i++)
      {
        Category category = Model1Factory.eINSTANCE.createCategory();
        add(company.getCategories(), category);
        fillCategory(category, 5);
      }

      for (int i = 0; i < 1000; i++)
      {
        Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
        add(company.getSuppliers(), supplier);
      }

      for (int i = 0; i < 1000; i++)
      {
        Customer customer = Model1Factory.eINSTANCE.createCustomer();
        add(company.getCustomers(), customer);
      }

      for (int i = 0; i < 1000; i++)
      {
        PurchaseOrder order = Model1Factory.eINSTANCE.createPurchaseOrder();
        order.setSupplier(company.getSuppliers().get(i));
        add(company.getPurchaseOrders(), order);

        for (int j = 0; j < 100; j++)
        {
          OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();
          add(order.getOrderDetails(), orderDetail);
        }
      }

      for (int i = 0; i < 1000; i++)
      {
        SalesOrder order = Model1Factory.eINSTANCE.createSalesOrder();
        order.setCustomer(company.getCustomers().get(i));
        add(company.getSalesOrders(), order);

        for (int j = 0; j < 100; j++)
        {
          OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();
          add(order.getOrderDetails(), orderDetail);
        }
      }
    }

    private static void fillCategory(Category category, int depth)
    {
      for (int i = 0; i < 5; i++)
      {
        Category child = Model1Factory.eINSTANCE.createCategory();
        add(category.getCategories(), child);
        if (depth > 1)
        {
          fillCategory(child, depth - 1);
        }
      }

      for (int i = 0; i < 20; i++)
      {
        Product1 product = Model1Factory.eINSTANCE.createProduct1();
        add(category.getProducts(), product);
      }
    }

    private static <T extends EObject> void add(EList<T> list, T object)
    {
      if (ADD_UNIQUE)
      {
        ((InternalEList<T>)list).addUnique(object);
      }
      else
      {
        list.add(object);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class PrefetchResource
  {
    public static void main(String[] args) throws Exception
    {
      CDOSession session = openSession();

      try
      {
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.getResource(RESOURCE_NAME);

        start("Prefetch Resource");
        resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
        stop();

        iterateResource(resource);
      }
      finally
      {
        session.close();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CreateUnit
  {
    public static void main(String[] args) throws Exception
    {
      CDOSession session = openSession();

      try
      {
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.getResource(RESOURCE_NAME);

        start("Create Unit");
        resource.cdoView().getUnitManager().createUnit(resource, true, null);
        stop();

        iterateResource(resource);
      }
      finally
      {
        session.close();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class OpenUnit
  {
    public static void main(String[] args) throws Exception
    {
      CDOSession session = openSession();

      try
      {
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.getResource(RESOURCE_NAME);

        start("Open Unit");
        resource.cdoView().getUnitManager().openUnit(resource, false, null);
        stop();

        iterateResource(resource);
      }
      finally
      {
        session.close();
      }
    }
  }

  private static CDOSession openSession()
  {
    IConnector connector = TCPUtil.getConnector(IPluginContainer.INSTANCE, "localhost");

    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName("repo1");
    return configuration.openNet4jSession();
  }

  private static void iterateResource(CDOResource resource)
  {
    start("Iterate");
    for (Iterator<EObject> it = resource.eAllContents(); it.hasNext();)
    {
      it.next();
    }

    stop();
  }

  private static void start(String msg)
  {
    start = System.currentTimeMillis();
    System.out.print(msg + ": ");
  }

  private static void stop()
  {
    long stop = System.currentTimeMillis();
    System.out.println(stop - start);
    start = stop;
  }

  static
  {
    Model1Package.eINSTANCE.getClass();

    Net4jUtil.prepareContainer(IPluginContainer.INSTANCE);
    TCPUtil.prepareContainer(IPluginContainer.INSTANCE);
    CDONet4jServerUtil.prepareContainer(IPluginContainer.INSTANCE);
    CDONet4jUtil.prepareContainer(IPluginContainer.INSTANCE);
    IPluginContainer.INSTANCE.activate();
  }
}
