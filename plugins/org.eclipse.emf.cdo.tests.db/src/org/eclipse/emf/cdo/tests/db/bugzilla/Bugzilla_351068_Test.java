/*
 * Copyright (c) 2018, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.net4j.db.DBUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bug 351068 [DB] Consider deleting old revisions when audits disabled
 *
 * @author Eike Stepper
 */
@Skips({ IRepositoryConfig.CAPABILITY_AUDITING, "DB.inverse.lists" })
public class Bugzilla_351068_Test extends AbstractCDOTest
{
  @CleanRepositoriesBefore(reason = "Row counting")
  public void testDelete() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    assertEquals(1, countRows("model1_Company"));
    assertEquals(5, countRows("cdo_objects"));

    final URI uriC1 = EcoreUtil.getURI(company);
    final CDOID id = CDOUtil.getCDOObject(company).cdoID();
    assertEquals(company, transaction.getResourceSet().getEObject(uriC1, false));

    resource.getContents().remove(company);
    assertTransient(company);
    assertSame(company, CDOUtil.getEObject(transaction.getObject(id)));
    assertSame(company, transaction.getResourceSet().getEObject(uriC1, false));

    transaction.commit();
    assertTransient(company);

    try
    {
      transaction.getObject(id);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
      // SUCCESS
    }

    assertNull(transaction.getResourceSet().getEObject(uriC1, false));
    assertEquals(0, countRows("model1_Company"));
    assertEquals(4, countRows("cdo_objects"));
  }

  @CleanRepositoriesBefore(reason = "Row counting")
  public void testDeleteWithChildren() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    resource.getContents().add(company);
    transaction.commit();

    assertEquals(1, countRows("model1_Company"));
    assertEquals(3, countRows("model1_Company_categories_list"));
    assertEquals(3, countRows("model1_Category"));
    assertEquals(8, countRows("cdo_objects"));

    final URI uriC1 = EcoreUtil.getURI(company);
    final CDOID id = CDOUtil.getCDOObject(company).cdoID();
    assertEquals(company, transaction.getResourceSet().getEObject(uriC1, false));

    resource.getContents().remove(company);
    assertTransient(company);
    assertSame(company, CDOUtil.getEObject(transaction.getObject(id)));
    assertSame(company, transaction.getResourceSet().getEObject(uriC1, false));

    transaction.commit();
    assertTransient(company);

    try
    {
      transaction.getObject(id);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
      // SUCCESS
    }

    assertNull(transaction.getResourceSet().getEObject(uriC1, false));
    assertEquals(0, countRows("model1_Company"));
    assertEquals(0, countRows("model1_Company_categories_list"));
    assertEquals(0, countRows("model1_Category"));
    assertEquals(4, countRows("cdo_objects"));
  }

  @CleanRepositoriesBefore(reason = "Row counting")
  public void testDeleteWithReferences() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();
    SalesOrder salesOrder1 = getModel1Factory().createSalesOrder();
    SalesOrder salesOrder2 = getModel1Factory().createSalesOrder();
    SalesOrder salesOrder3 = getModel1Factory().createSalesOrder();

    customer.getSalesOrders().add(salesOrder1);
    customer.getSalesOrders().add(salesOrder2);
    customer.getSalesOrders().add(salesOrder3);

    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder1);
    company.getSalesOrders().add(salesOrder2);
    company.getSalesOrders().add(salesOrder3);
    resource.getContents().add(company);
    transaction.commit();

    assertEquals(1, countRows("model1_Company"));
    assertEquals(1, countRows("model1_Customer"));
    assertEquals(3, countRows("model1_Customer_salesOrders_list"));
    assertEquals(3, countRows("model1_SalesOrder"));
    assertEquals(9, countRows("cdo_objects"));

    final URI uriC1 = EcoreUtil.getURI(company);
    final CDOID id = CDOUtil.getCDOObject(company).cdoID();
    assertEquals(company, transaction.getResourceSet().getEObject(uriC1, false));

    resource.getContents().remove(company);
    assertTransient(company);
    assertSame(company, CDOUtil.getEObject(transaction.getObject(id)));
    assertSame(company, transaction.getResourceSet().getEObject(uriC1, false));

    transaction.commit();
    assertTransient(company);

    try
    {
      transaction.getObject(id);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
      // SUCCESS
    }

    assertNull(transaction.getResourceSet().getEObject(uriC1, false));
    assertEquals(0, countRows("model1_Company"));
    assertEquals(0, countRows("model1_Customer"));
    assertEquals(0, countRows("model1_Customer_salesOrders_list"));
    assertEquals(0, countRows("model1_SalesOrder"));
    assertEquals(4, countRows("cdo_objects"));
  }

  private int countRows(String table) throws SQLException
  {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      InternalRepository repository = getRepository();
      IDBStore store = (IDBStore)repository.getStore();

      connection = store.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery("SELECT COUNT(1) FROM " + DBUtil.quoted(table));

      if (resultSet.next())
      {
        return resultSet.getInt(1);
      }

      return 0;
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
      DBUtil.close(connection);
    }
  }
}
