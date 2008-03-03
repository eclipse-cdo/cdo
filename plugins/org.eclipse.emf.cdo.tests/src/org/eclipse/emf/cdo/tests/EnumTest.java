/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.VAT;

/**
 * @author Eike Stepper
 */
public class EnumTest extends AbstractCDOTest
{
  public void testTransient() throws Exception
  {
    {
      Product product = Model1Factory.eINSTANCE.createProduct();
      product.setName("Normal Product");
      product.setVat(VAT.VAT7);

      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(product);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/my/resource");
      Product product = (Product)resource.getContents().get(0);
      assertEquals(VAT.VAT7, product.getVat());
      session.close();
    }
  }

  public void testAttached() throws Exception
  {
    {
      Category category = Model1Factory.eINSTANCE.createCategory();
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(category);

      Product product = Model1Factory.eINSTANCE.createProduct();
      product.setName("Normal Product");
      product.setVat(VAT.VAT7);
      category.getProducts().add(product);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/my/resource");
      Category category = (Category)resource.getContents().get(0);
      Product product = category.getProducts().get(0);
      assertEquals(VAT.VAT7, product.getVat());
      session.close();
    }
  }
}
