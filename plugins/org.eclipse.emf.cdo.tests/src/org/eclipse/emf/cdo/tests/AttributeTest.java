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
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.VAT;

/**
 * @author Eike Stepper
 */
public class AttributeTest extends AbstractCDOTest
{
  public void testPrimitiveDefaults() throws Exception
  {
    {
      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Preferred Supplier");
      assertEquals(true, supplier.isPreferred());

      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(supplier);
      assertEquals(true, supplier.isPreferred());
      transaction.commit();
      assertEquals(true, supplier.isPreferred());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
      Supplier supplier = (Supplier)resource.getContents().get(0);
      assertEquals(true, supplier.isPreferred());
      view.close();
      session.close();
    }
  }

  public void testEnumDefaults() throws Exception
  {
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Test Product");
      assertEquals(VAT.VAT15, product.getVat());

      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");
      resource.getContents().add(product);
      assertEquals(VAT.VAT15, product.getVat());
      transaction.commit();
      assertEquals(VAT.VAT15, product.getVat());
      session.close();
    }

    {
      CDOSession session = openModel1Session();
      CDOView view = session.openView();
      CDOResource resource = view.getResource("/my/resource");
      Product1 product = (Product1)resource.getContents().get(0);
      assertEquals(VAT.VAT15, product.getVat());
      view.close();
      session.close();
    }
  }
}
