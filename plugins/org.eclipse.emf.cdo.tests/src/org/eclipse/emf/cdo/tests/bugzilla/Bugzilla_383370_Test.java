/*
 * Copyright (c) 2012, 2013, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * Problem with resource after transaction rollback
 * <p>
 * See bug 383370
 *
 * @author Eike Stepper
 */
public class Bugzilla_383370_Test extends AbstractCDOTest
{
  public void testTopLevel() throws Exception
  {
    Category category = getModel1Factory().createCategory();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    transaction.commit();

    EList<EObject> contents = resource.getContents();
    contents.add(category);
    assertEquals(1, contents.size());
    assertEquals(resource, category.eResource());

    transaction.rollback();
    assertEquals(0, contents.size());
    assertEquals(null, category.eResource());

    contents.add(category);
    assertEquals(1, contents.size());
    assertEquals(resource, category.eResource());
  }

  public void testTopLevelWithChildren() throws Exception
  {
    Category category = getModel1Factory().createCategory();
    Category child = getModel1Factory().createCategory();
    category.getCategories().add(child);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    transaction.commit();

    EList<EObject> contents = resource.getContents();
    contents.add(category);
    assertEquals(1, contents.size());
    assertEquals(resource, category.eResource());
    assertEquals(category, child.eContainer());

    transaction.rollback();
    assertEquals(0, contents.size());
    assertEquals(null, category.eResource());
    assertEquals(category, child.eContainer());

    contents.add(category);
    assertEquals(1, contents.size());
    assertEquals(resource, category.eResource());
    assertEquals(category, child.eContainer());
  }

  public void testSecondLevel() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    resource.getContents().add(company);
    transaction.commit();

    EList<Category> categories = company.getCategories();
    categories.add(category);
    assertEquals(1, categories.size());
    assertEquals(company, category.eContainer());

    transaction.rollback();
    assertEquals(0, categories.size());
    assertEquals(null, category.eContainer());

    categories.add(category);
    assertEquals(1, categories.size());
    assertEquals(company, category.eContainer());
  }

  public void testSecondLevelWithChildren() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    Category child = getModel1Factory().createCategory();
    category.getCategories().add(child);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));
    resource.getContents().add(company);
    transaction.commit();

    EList<Category> categories = company.getCategories();
    categories.add(category);
    assertEquals(1, categories.size());
    assertEquals(company, category.eContainer());
    assertEquals(category, child.eContainer());

    transaction.rollback();
    assertEquals(0, categories.size());
    assertEquals(null, category.eContainer());
    assertEquals(category, child.eContainer());

    categories.add(category);
    assertEquals(1, categories.size());
    assertEquals(company, category.eContainer());
    assertEquals(category, child.eContainer());
  }
}
