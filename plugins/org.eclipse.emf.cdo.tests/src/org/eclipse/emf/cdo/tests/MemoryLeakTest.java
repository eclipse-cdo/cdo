/*
 * Copyright (c) 2010-2012, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Eike Stepper
 */
public class MemoryLeakTest extends AbstractCDOTest
{
  private static final int LEVELS = 5;

  private static final int CATEGORIES = 5;

  private static final int PRODUCTS = 10;

  protected Company company;

  @Override
  protected void doSetUp() throws Exception
  {
    // disableConsole();
    super.doSetUp();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource(getResourcePath("res1"));

    company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    commit();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    // enableConsole();
    super.doTearDown();
  }

  protected void commit()
  {
    try
    {
      CDOTransaction transaction = (CDOTransaction)CDOUtil.getCDOObject(company).cdoView();
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public void testLargeModel() throws Exception
  {
    Category category = getModel1Factory().createCategory();
    category.setName("ROOT");
    company.getCategories().add(category);
    commit();

    try
    {
      createModel(category, LEVELS, CATEGORIES, PRODUCTS, this::commit);
    }
    catch (OutOfMemoryError error)
    {
      error.printStackTrace();
    }
  }

  @SuppressWarnings("unused")
  private Category selectCategory(EList<Category> categories)
  {
    int size = categories.size();
    if (size == 0)
    {
      return null;
    }

    int index = (int)(Math.random() * size);
    Category category = categories.get(index);
    if (Math.random() >= .5d)
    {
      Category recurse = selectCategory(category.getCategories());
      if (recurse != null)
      {
        return recurse;
      }
    }

    return category;
  }
}
