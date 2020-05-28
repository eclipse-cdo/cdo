/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Eike Stepper
 */
public class BigModelTest extends AbstractCDOTest
{
  private static final int LEVELS = 5;

  private static final int CATEGORIES = 5;

  private static final int PRODUCTS = 100;

  private Category root;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    root = getModel1Factory().createCategory();
    root.setName("ROOT");
    createModel(root, LEVELS);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(root);
    log("Committing new objects: " + transaction.getLastSavepoint().getNewObjects().size());
  }

  private void createModel(Category parent, int levels)
  {
    EList<Category> categories = parent.getCategories();
    for (int i = 0; i < CATEGORIES; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("Category" + levels + "-" + i);
      categories.add(category);
    }

    EList<Product1> products = parent.getProducts();
    for (int i = 0; i < PRODUCTS; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Product" + levels + "-" + i);
      products.add(product);
    }

    if (levels > 0)
    {
      for (Category category : categories)
      {
        createModel(category, levels - 1);
      }
    }
  }

  public void testBigModel() throws Exception
  {
    CDOTransaction transaction = (CDOTransaction)CDOUtil.getCDOObject(root).cdoView();
    long start = System.currentTimeMillis();

    transaction.commit();
    long end = System.currentTimeMillis();

    double seconds = (end - start) / 1000d;
    log("Took: " + seconds + " seconds");
  }

  protected void log(String message)
  {
    IOUtil.OUT().println(message);
  }

  /**
   * @author Eike Stepper
   */
  public static final class WarmUp extends BigModelTest
  {

    @Override
    protected void log(String message)
    {
      // Do nothing.
    }
  }
}
