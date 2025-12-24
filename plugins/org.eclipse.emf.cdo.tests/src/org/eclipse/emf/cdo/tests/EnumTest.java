/*
 * Copyright (c) 2008-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Eike Stepper
 */
public class EnumTest extends AbstractCDOTest
{
  public void testTransient() throws Exception
  {
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Normal Product");
      product.setVat(VAT.VAT7);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(product);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Product1 product = (Product1)resource.getContents().get(0);
      assertEquals(VAT.VAT7, product.getVat());

      product.setVat(VAT.VAT15);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Product1 product = (Product1)resource.getContents().get(0);
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }
  }

  public void testTransientFresh() throws Exception
  {
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Normal Product");
      product.setVat(VAT.VAT7);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(product);
      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Product1 product = (Product1)resource.getContents().get(0);
      assertEquals(VAT.VAT7, product.getVat());

      product.setVat(VAT.VAT15);
      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Product1 product = (Product1)resource.getContents().get(0);
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }
  }

  public void testAttached() throws Exception
  {
    {
      Category category = getModel1Factory().createCategory();
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(category);

      Product1 product = getModel1Factory().createProduct1();
      product.setName("Normal Product");
      product.setVat(VAT.VAT7);
      category.getProducts().add(product);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Category category = (Category)resource.getContents().get(0);
      Product1 product = category.getProducts().get(0);
      assertEquals(VAT.VAT7, product.getVat());

      product.setVat(VAT.VAT15);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Category category = (Category)resource.getContents().get(0);
      Product1 product = category.getProducts().get(0);
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }
  }

  public void testAttachedFresh() throws Exception
  {
    {
      Category category = getModel1Factory().createCategory();
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      resource.getContents().add(category);

      Product1 product = getModel1Factory().createProduct1();
      product.setName("Normal Product");
      product.setVat(VAT.VAT7);
      category.getProducts().add(product);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Category category = (Category)resource.getContents().get(0);
      Product1 product = category.getProducts().get(0);
      assertEquals(VAT.VAT7, product.getVat());

      product.setVat(VAT.VAT15);
      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
      Category category = (Category)resource.getContents().get(0);
      Product1 product = category.getProducts().get(0);
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }
  }
}
