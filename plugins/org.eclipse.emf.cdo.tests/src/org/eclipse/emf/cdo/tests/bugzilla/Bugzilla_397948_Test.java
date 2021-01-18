/*
 * Copyright (c) 2013, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOQuery;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Bug 397948: UnsupportedOperationException CDOQueryResultIteratorImpl$QueryResultList.contains(CDOQueryResultIteratorImpl.java:204)
 *
 * @author Eike Stepper
 */
@Requires("MEM")
public class Bugzilla_397948_Test extends AbstractCDOTest
{
  public void testContains() throws Exception
  {
    List<Category> result = executeQuery();
    result.contains(getModel1Factory().createCategory());
  }

  public void testGet() throws Exception
  {
    List<Category> result = executeQuery();
    result.get(0);
    result.get(1);
  }

  public void testIndexOf() throws Exception
  {
    List<Category> result = executeQuery();
    result.indexOf(getModel1Factory().createCategory());
  }

  public void testLastIndexOf() throws Exception
  {
    List<Category> result = executeQuery();
    result.lastIndexOf(getModel1Factory().createCategory());
  }

  public void testIsEmpty() throws Exception
  {
    List<Category> result = executeQuery();
    result.isEmpty();
  }

  public void testIterator() throws Exception
  {
    List<Category> result = executeQuery();
    Iterator<Category> iterator = result.iterator();
    iterator.next();
    iterator.next();
  }

  public void testListIterator() throws Exception
  {
    List<Category> result = executeQuery();
    ListIterator<Category> iterator = result.listIterator();
    iterator.next();
    iterator.next();
    iterator.previous();
    iterator.previous();
  }

  public void testListIteratorFromEnd() throws Exception
  {
    List<Category> result = executeQuery();
    ListIterator<Category> iterator = result.listIterator(2);
    iterator.previous();
    iterator.previous();
    iterator.next();
    iterator.next();
  }

  public void testSubList() throws Exception
  {
    List<Category> result = executeQuery();
    assertEquals(2, result.subList(0, 2).size());
    assertEquals(1, result.subList(1, 2).size());
    assertEquals(0, result.subList(2, 2).size());
  }

  public void testToArray() throws Exception
  {
    List<Category> result = executeQuery();
    result.toArray();
    result.toArray(new Category[result.size()]);
  }

  private List<Category> executeQuery() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Company company = getModel1Factory().createCompany();
    company.setName("TEST");
    resource.getContents().add(company);

    Category category1 = getModel1Factory().createCategory();
    company.getCategories().add(category1);

    Category category2 = getModel1Factory().createCategory();
    company.getCategories().add(category2);

    transaction.commit();

    CDOQuery query = transaction.createQuery(MEMConfig.TEST_QUERY_LANGUAGE, "QUERYSTRING");
    query.setParameter("context", getModel1Package().getCategory());

    List<Category> result = query.getResult(Category.class);
    assertEquals(2, result.size());
    return result;
  }
}
