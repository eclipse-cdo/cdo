/*
 * Copyright (c) 2012, 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import org.junit.AfterClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
@Requires("excluded")
public class MartinsPerformanceTest extends AbstractCDOTest
{
  protected static EClass mapContainerEClass;

  private static Map<String, TimeTaker> results = new HashMap<>();

  public void testSimpleTestNoCommit() throws Exception
  {
    TimeTaker t = start("testSimpleTestNoCommit");

    getModel1Factory().createCustomer();
    for (int i = 0; i < 50000; i++)
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setCity("Barovia");
      customer.setName("Strahd von Zarovich");
      customer.setStreet("Necromancer Road 5");

      for (int c = 0; c < 50; c++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setId(c);
      }
    }

    finish(t);
    msg(t.getTiming());
  }

  public void testSimpleTestWithCommit() throws Exception
  {
    TimeTaker t = start("testBasicCommitPerformance");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    getModel1Factory().createCustomer();
    for (int i = 0; i < 500; i++)
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setCity("Barovia");
      customer.setName("Strahd von Zarovich");
      customer.setStreet("Necromancer Road 5");

      for (int c = 0; c < 5; c++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setCustomer(customer);
        salesOrder.setId(c);
        resource.getContents().add(salesOrder);
      }

      resource.getContents().add(customer);
    }

    transaction.commit();

    finish(t);
    msg(t.getTiming());
  }

  // public void testDynamicPerformance() throws Exception
  // {
  //
  // TimeTaker t = start("testDynamicPerformance");
  // CDOSession session = openSession();
  // CDOTransaction transaction = session.openTransaction();
  // EPackage dynamicMapEPackge = createPackage();
  // EFactory dynamicMapEFactoryInstance = dynamicMapEPackge.getEFactoryInstance();
  //
  // CDOResource resource = transaction.createResource(getResourcePath("/test1"));
  // for (int i = 0; i < 5000; i++)
  // {
  //
  // EObject mapContainer = dynamicMapEFactoryInstance
  // .create((EClass)dynamicMapEPackge.getEClassifier("MapContainer"));
  //
  // resource.getContents().add(mapContainer);
  //
  // }
  // transaction.commit();
  //
  // finish(t);
  // }

  public void testLoadAnStoreDataPerformance() throws Exception
  {
    TimeTaker t = start("testLoadAnStoreDataPerformance");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      getModel1Factory().createCustomer();
      for (int i = 0; i < 500; i++)
      {
        Customer customer = getModel1Factory().createCustomer();
        customer.setCity("Barovia");
        customer.setName("Strahd von Zarovich");
        customer.setStreet("Necromancer Road 5");

        for (int c = 0; c < 5; c++)
        {
          SalesOrder salesOrder = getModel1Factory().createSalesOrder();
          salesOrder.setCustomer(customer);
          salesOrder.setId(c);
          resource.getContents().add(salesOrder);
        }

        resource.getContents().add(customer);
      }

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      int i = 0;
      for (EObject o : resource.getContents())
      {
        if (o instanceof Customer)
        {
          Customer customer = (Customer)o;
          customer.setCity("Dargaard");
          customer.setName("Lord Soth");
          customer.setStreet("Death Knight Alley 7");

          for (SalesOrder salesOrder : customer.getSalesOrders())
          {
            salesOrder.setId(salesOrder.getId() + 1);
          }

          i++;
        }
      }

      assertEquals(500, i);

      transaction.commit();
      session.close();
    }

    finish(t);
    msg(t.getTiming());
  }

  public void testLoadAnStoreDataPreAddPerformance() throws Exception
  {
    TimeTaker t = start("testLoadAnStoreDataPreAddPerformance");

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      getModel1Factory().createCustomer();
      for (int i = 0; i < 500; i++)
      {
        Customer customer = getModel1Factory().createCustomer();
        resource.getContents().add(customer);
        customer.setCity("Barovia");
        customer.setName("Strahd von Zarovich");
        customer.setStreet("Necromancer Road 5");

        for (int c = 0; c < 5; c++)
        {
          SalesOrder salesOrder = getModel1Factory().createSalesOrder();
          resource.getContents().add(salesOrder);
          salesOrder.setCustomer(customer);
          salesOrder.setId(c);
        }
      }

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      int i = 0;
      for (EObject o : resource.getContents())
      {
        if (o instanceof Customer)
        {
          Customer customer = (Customer)o;
          customer.setCity("Dargaard");
          customer.setName("Lord Soth");
          customer.setStreet("Death Knight Alley 7");

          for (SalesOrder salesOrder : customer.getSalesOrders())
          {
            salesOrder.setId(salesOrder.getId() + 1);
          }

          i++;
        }
      }

      assertEquals(500, i);

      transaction.commit();
      session.close();
    }

    finish(t);
    msg(t.getTiming());
  }

  public void testAddRemove() throws Exception
  {
    TimeTaker t = start("testAddRemove");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    for (int i = 0; i < 100; i++)
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("Azalin Rex");
      customer.setCity("Darkon");

      resource.getContents().add(customer);
      transaction.commit();

      resource.getContents().remove(resource.getContents().size() - 1);
      transaction.commit();

      resource.getContents().add(customer);
      transaction.commit();
    }

    finish(t);
    msg(t.getTiming());
  }

  public EPackage createPackage()
  {
    EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
    EcorePackage theCorePackage = EcorePackage.eINSTANCE;

    mapContainerEClass = theCoreFactory.createEClass();
    mapContainerEClass.setName("MapContainer");

    EPackage dynamicMapEPackage = createUniquePackage();
    dynamicMapEPackage.getEClassifiers().add(mapContainerEClass);

    EStructuralFeature name = theCoreFactory.createEAttribute();
    name.setName("name");
    name.setEType(theCorePackage.getEString());

    mapContainerEClass.getEStructuralFeatures().add(name);

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(dynamicMapEPackage);
    }

    return dynamicMapEPackage;
  }

  private TimeTaker start(String testName)
  {
    TimeTaker t = new TimeTaker();
    results.put(testName, t);
    t.setStart(System.currentTimeMillis());
    return t;
  }

  private void finish(TimeTaker t)
  {
    t.setEnd(System.currentTimeMillis());
  }

  /**
   * @author Martin Fluegge
   */
  private static class TimeTaker
  {
    private long start;

    private long end;

    public void setStart(long start)
    {
      this.start = start;
    }

    public void setEnd(long end)
    {
      this.end = end;
    }

    public long getTiming()
    {
      return end - start;
    }
  }

  // A fake because afterClass does not seem to work :(
  @AfterClass
  public void testTearDown() throws IOException
  {
    String ext = "NATIVE";
    if (isConfig(LEGACY))
    {
      ext = "LEGACY";
    }

    String location = System.getProperty("user.home") + "/cdo_Performance_" + ext + "_" + new Date().getTime() + ".csv";
    System.out.println("Writing performance results to: " + location);
    File file = new File(location);
    FileWriter fileWriter = new FileWriter(file);

    try
    {
      StringBuffer stringBuffer = new StringBuffer();
      for (String key : results.keySet())
      {
        stringBuffer.append(key + ";" + results.get(key).getTiming() + "\n");
      }

      fileWriter.write(stringBuffer.toString());
    }
    finally
    {
      IOUtil.close(fileWriter);
    }
  }
}
