/*
 * Copyright (c) 2008-2013, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig.MEMStoreAccessor_UT;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig.MEMStore_UT;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.emf.internal.cdo.query.CDOQueryResultIteratorImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 */
@Requires("MEM")
public class MEMStoreQueryTest extends AbstractCDOTest
{
  public void testBasicQuery() throws Exception
  {
    Set<Object> objects = new HashSet<>();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));
    objects.add(resource1);
    Company company1 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    objects.add(company1);
    objects.add(category1);
    objects.add(transaction.getRootResource());
    objects.add(transaction.getResourceNode(getResourcePath(null)));
    company1.setName("TEST");

    transaction.commit();

    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    List<Object> result = query.getResult(Object.class);
    assertEquals(5, result.size());
    for (Object object : result)
    {
      assertEquals(true, objects.contains(object));
    }

    transaction.close();
    session.close();
  }

  public void testQueryException() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    query.setParameter("error", 5000);
    query.setParameter("integers", 10000);

    CloseableIterator<Object> it = query.getResultAsync(Object.class);
    int i = 0;

    for (;;)
    {
      ++i;

      try
      {
        if (!it.hasNext())
        {
          break;
        }
      }
      catch (Exception expected)
      {
        expected.printStackTrace();
        break;
      }

      it.next();
    }

    assertEquals("Query should have stopped at the 5000th result", 5000, i);
  }

  public void testBasicQuery_EClassParameter() throws Exception
  {
    Set<Object> objects = new HashSet<>();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));
    objects.add(resource1);
    Company company1 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    objects.add(company1);
    objects.add(category1);

    company1.setName("TEST");

    transaction.commit();
    System.out.println(category1.eClass().getEPackage().getNsURI());

    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    query.setParameter("context", getModel1Package().getCategory());

    List<Category> result = query.getResult(Category.class);
    assertEquals(1, result.size());
    assertEquals(category1, result.get(0));

    transaction.close();
    session.close();
  }

  public void testQueryCancel_successful() throws Exception
  {
    CDOTransaction transaction = initialize(500);
    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    query.setParameter("sleep", 1000L);
    final CloseableIterator<Object> result = query.getResultAsync(Object.class);
    result.close();

    assertNoTimeout(() -> !getRepository().getQueryManager().isRunning(((CDOQueryResultIteratorImpl<?>)result).getQueryID()));

    CDOSession session = transaction.getSession();
    transaction.close();
    session.close();
  }

  public void testQueryCancel_ViewClose() throws Exception
  {
    CDOTransaction transaction = initialize(500);
    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    query.setParameter("sleep", 1000L);
    final CloseableIterator<Object> result = query.getResultAsync(Object.class);
    CDOSession session = transaction.getSession();
    transaction.close();

    assertNoTimeout(() -> !getRepository().getQueryManager().isRunning(((CDOQueryResultIteratorImpl<?>)result).getQueryID()));

    session.close();
  }

  public void testQueryCancel_SessionClose() throws Exception
  {
    CDOTransaction transaction = initialize(500);
    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    query.setParameter("sleep", 1000L);
    final CloseableIterator<Object> result = query.getResultAsync(Object.class);
    transaction.getSession().close();

    assertNoTimeout(() -> !getRepository().getQueryManager().isRunning(((CDOQueryResultIteratorImpl<?>)result).getQueryID()));
  }

  public void testQueryCancel_storeAccessorLeak() throws Exception
  {
    MEMStoreAccessor_UT.testQueryLatchCreate();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, null);

    Set<MEMStoreAccessor_UT> accessors = ((MEMStore_UT)getRepository().getStore()).getStoreAccessors();
    int originalAccessors = accessors.size();

    CloseableIterator<Object> result = query.getResultAsync(Object.class);
    sleep(100); // Give the store accessor of this query a chance to become active.
    result.close();

    MEMStoreAccessor_UT.testQueryLatchCountDown();

    int leakedAccessors = accessors.size() - originalAccessors;
    assertEquals("Store accessor leak detected", 0, leakedAccessors);
  }

  public void testQueryAsync_UnsupportedLanguage() throws Exception
  {
    CDOTransaction transaction = initialize(100);
    CDOQuery query = transaction.createQuery("TESTss", "QUERYSTRING");

    try
    {
      CloseableIterator<Object> result = query.getResultAsync(Object.class);
      for (int i = 0; i < 20; i++)
      {
        result.hasNext();
        sleep(10);
      }

      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // SUCCESS.
    }
  }

  public void testQuerySync_UnsupportedLanguage() throws Exception
  {
    CDOTransaction transaction = initialize(100);
    CDOQuery query = transaction.createQuery("TESTss", "QUERYSTRING");

    try
    {
      query.getResult(Object.class);
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // SUCCESS.
    }
  }

  private CDOTransaction initialize(int number)
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

    for (int i = 0; i < number; i++)
    {
      Category category1 = getModel1Factory().createCategory();
      resource1.getContents().add(category1);
    }

    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    return transaction;
  }
}
