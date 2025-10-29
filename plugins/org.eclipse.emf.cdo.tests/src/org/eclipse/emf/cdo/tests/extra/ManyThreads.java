/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.extra;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.transaction.CDOTransactionImpl;

/**
 * @author Eike Stepper
 */
public class ManyThreads extends AbstractCDOTest
{
  private static final int COMMITTERS = 250;

  // @Override
  // protected void initTestProperties(Map<String, Object> properties)
  // {
  // super.initTestProperties(properties);
  // properties.put(IRepository.Props.OPTIMISTIC_LOCKING_TIMEOUT, "30000");
  // }
  public void testManyParallelCommits() throws Exception
  {
    disableConsole();

    final CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test"));

    final Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    Committer[] threads = new Committer[COMMITTERS];
    for (int i = 0; i < COMMITTERS; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("Category-" + i);
      company.getCategories().add(category);

      threads[i] = new Committer(session, category, i);
    }

    transaction.commit();
    sleep(1000);

    for (Committer thread : threads)
    {
      thread.start();
    }

    for (Committer thread : threads)
    {
      thread.join();
    }

    for (Committer thread : threads)
    {
      if (thread.exception != null)
      {
        throw thread.exception;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Committer extends Thread
  {
    private final CDOSession session;

    private Category category;

    private Exception exception;

    public Committer(CDOSession session, Category category, int i)
    {
      super("Committer-" + i);
      this.session = session;
      this.category = category;
    }

    @Override
    public void run()
    {
      CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
      // transaction.options().setCommitInfoTimeout(40000);
      category = transaction.getObject(category);

      Category subCategory = null;

      for (int j = 0; j < 20000; j++)
      {
        if (j % 100 == 0)
        {
          subCategory = getModel1Factory().createCategory();
          subCategory.setName("Subcategory-" + j);
          category.getCategories().add(subCategory);
        }

        for (int k = 0; k < 10; k++)
        {
          Product1 product = getModel1Factory().createProduct1();
          product.setName("Product-" + k);
          subCategory.getProducts().add(product);
        }

        try
        {
          transaction.commit();
        }
        catch (Exception ex)
        {
          exception = ex;
          return;
        }
      }
    }
  }
}
