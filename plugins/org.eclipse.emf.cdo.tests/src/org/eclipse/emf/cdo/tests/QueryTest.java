/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.query.CDOQuery;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;

import org.eclipse.emf.internal.cdo.query.CDOQueryResultIteratorImpl;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 */
public class QueryTest extends AbstractCDOTest
{
  private static final String LANGUAGE = "TEST";

  public void testBasicQuery() throws Exception
  {
    skipUnlessConfig(MEM);

    Set<Object> objects = new HashSet<Object>();
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/test1");
    objects.add(resource1);
    Company company1 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    objects.add(company1);
    objects.add(category1);
    objects.add(transaction.getRootResource());
    company1.setName("TEST");

    transaction.commit();

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE, "QUERYSTRING");
    List<Object> queryResult = cdoQuery.getResult(Object.class);
    assertEquals(4, queryResult.size());
    for (Object object : queryResult)
    {
      assertEquals(true, objects.contains(object));
    }

    transaction.close();
    session.close();
  }

  public void testBasicQuery_EClassParameter() throws Exception
  {
    skipUnlessConfig(MEM);

    Set<Object> objects = new HashSet<Object>();
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/test1");
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

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE, "QUERYSTRING");
    cdoQuery.setParameter("context", getModel1Package().getCategory());

    List<Category> queryResult = cdoQuery.getResult(Category.class);
    assertEquals(1, queryResult.size());
    assertEquals(category1, queryResult.get(0));

    transaction.close();
    session.close();
  }

  public void testQueryCancel_successful() throws Exception
  {
    skipUnlessConfig(MEM);

    CDOTransaction transaction = initialize(500);
    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE, "QUERYSTRING");
    cdoQuery.setParameter("sleep", 1000L);
    final CloseableIterator<Object> queryResult = cdoQuery.getResultAsync(Object.class);
    queryResult.close();

    boolean timedOut = new PollingTimeOuter(500, 100)
    {
      @Override
      protected boolean successful()
      {
        return !((Repository)getRepository()).getQueryManager().isRunning(
            ((CDOQueryResultIteratorImpl<?>)queryResult).getQueryID());
      }
    }.timedOut();

    assertEquals(false, timedOut);

    CDOSession session = transaction.getSession();
    transaction.close();
    session.close();
  }

  public void testQueryCancel_ViewClose() throws Exception
  {
    skipUnlessConfig(MEM);

    CDOTransaction transaction = initialize(500);
    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE, "QUERYSTRING");
    cdoQuery.setParameter("sleep", 1000L);
    final CloseableIterator<Object> queryResult = cdoQuery.getResultAsync(Object.class);
    CDOSession session = transaction.getSession();
    transaction.close();
    boolean timedOut = new PollingTimeOuter(500, 100)
    {
      @Override
      protected boolean successful()
      {
        return !((Repository)getRepository()).getQueryManager().isRunning(
            ((CDOQueryResultIteratorImpl<?>)queryResult).getQueryID());
      }
    }.timedOut();

    assertEquals(false, timedOut);
    session.close();
  }

  public void testQueryCancel_SessionClose() throws Exception
  {
    skipUnlessConfig(MEM);

    CDOTransaction transaction = initialize(500);
    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE, "QUERYSTRING");
    cdoQuery.setParameter("sleep", 1000L);
    final CloseableIterator<Object> queryResult = cdoQuery.getResultAsync(Object.class);
    transaction.getSession().close();

    boolean timedOut = new PollingTimeOuter(500, 100)
    {
      @Override
      protected boolean successful()
      {
        return !((Repository)getRepository()).getQueryManager().isRunning(
            ((CDOQueryResultIteratorImpl<?>)queryResult).getQueryID());
      }
    }.timedOut();

    assertEquals(false, timedOut);
  }

  public void testQueryAsync_UnsupportedLanguage() throws Exception
  {
    CDOTransaction transaction = initialize(100);
    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE + "ss", "QUERYSTRING");

    try
    {
      CloseableIterator<Object> queryResult = cdoQuery.getResultAsync(Object.class);
      queryResult.hasNext();
      fail("Should throw an exception");
    }
    catch (Exception expected)
    {
    }
  }

  public void testQuerySync_UnsupportedLanguage() throws Exception
  {
    CDOTransaction transaction = initialize(100);
    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE + "ss", "QUERYSTRING");

    try
    {
      cdoQuery.getResult(Object.class);
      fail("Should throw an exception");
    }
    catch (Exception expected)
    {

    }
  }

  private CDOTransaction initialize(int number)
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource("/test1");

    for (int i = 0; i < number; i++)
    {
      Category category1 = getModel1Factory().createCategory();
      resource1.getContents().add(category1);
    }

    transaction.commit();
    return transaction;
  }
}
