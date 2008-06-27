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
import org.eclipse.emf.cdo.query.CDOQueryResult;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;

import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;
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
  static String LANGUAGE_TEST = "TEST";
  
  public void testBasicQuery() throws Exception
  {

    Set<Object> objects = new HashSet<Object>();
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/test1");
    objects.add(resource1);
    Company company1 = Model1Factory.eINSTANCE.createCompany();
    Category category1 = Model1Factory.eINSTANCE.createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    objects.add(company1);
    objects.add(category1);

    company1.setName("TEST");

    transaction.commit();

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE_TEST, "TEST");
    List<Object> queryResult = cdoQuery.getResultList(Object.class);
    assertEquals(3, queryResult.size());
    for (Object object : queryResult)
    {
      assertEquals(true, objects.contains(object));
    }

    transaction.close();
    session.close();
  }
  
  public void testBasicQuery_EClassParameter() throws Exception
  {

    Set<Object> objects = new HashSet<Object>();
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/test1");
    objects.add(resource1);
    Company company1 = Model1Factory.eINSTANCE.createCompany();
    Category category1 = Model1Factory.eINSTANCE.createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    objects.add(company1);
    objects.add(category1);

    company1.setName("TEST");

    transaction.commit();

    System.out.println(category1.eClass().getEPackage().getNsURI());
    
    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE_TEST, "TEST");
    
    cdoQuery.setParameter("context", Model1Package.eINSTANCE.getCategory());
    
    List<Category> queryResult = cdoQuery.getResultList(Category.class);
    
    assertEquals(1, queryResult.size());
    assertEquals(category1, queryResult.get(0));
    
    transaction.close();
    session.close();
  }
  public void testQueryCancel_successful() throws Exception
  {
    CDOTransaction transaction = initialize(100);

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE_TEST, "TEST");
    CloseableIterator<Object> queryResult = ((CDOQueryImpl)cdoQuery).getResultIterator(Object.class);

    queryResult.close();

    assertEquals(false, ((Repository)getRepository()).getQueryManager().isRunning(
        ((CDOQueryResultIteratorImpl)queryResult).getQueryID()));

    transaction.close();
    transaction.getSession().close();
  }

  public void testQueryCancel_ViewClose() throws Exception
  {
    CDOTransaction transaction = initialize(100);

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE_TEST, "TEST");
    CloseableIterator<Object> queryResult = cdoQuery.getResultIterator(Object.class);

    Thread.sleep(300);
    // queryResult.cancel();
    transaction.close();
    assertEquals(false, ((Repository)getRepository()).getQueryManager().isRunning(
        ((CDOQueryResultIteratorImpl)queryResult).getQueryID()));
    transaction.getSession().close();
  }

  public void testQueryCancel_SessionClose() throws Exception
  {
    CDOTransaction transaction = initialize(100);

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE_TEST, "TEST");
    CloseableIterator<Object> queryResult = cdoQuery.getResultIterator(Object.class);

    Thread.sleep(300);

    transaction.getSession().close();

    assertEquals(false, ((Repository)getRepository()).getQueryManager().isRunning(
        ((CDOQueryResultIteratorImpl)queryResult).getQueryID()));

  }

  public void testQueryCancel_exception() throws Exception
  {
    CDOTransaction transaction = initialize(2);

    CDOQuery cdoQuery = transaction.createQuery(LANGUAGE_TEST, "TEST");
    CDOQueryResult<Object> queryResult = cdoQuery.getResultIterator(Object.class);

    while (!queryResult.isDone())
    {
      Thread.sleep(10);
    }
    try
    {
      queryResult.close();
      assertEquals(false, true);
    }
    catch (Exception exception)
    {

    }

    transaction.close();
    transaction.getSession().close();
  }

  CDOTransaction initialize(int number)
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/test1");

    for (int i = 0; i < number; i++)
    {
      Category category1 = Model1Factory.eINSTANCE.createCategory();
      resource1.getContents().add(category1);
    }

    transaction.commit();
    return transaction;
  }
}
