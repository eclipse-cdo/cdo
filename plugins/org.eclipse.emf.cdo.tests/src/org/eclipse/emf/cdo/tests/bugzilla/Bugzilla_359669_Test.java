/*
 * Copyright (c) 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_359669_Test extends AbstractCDOTest
{
  public void testReloadFromIndex() throws Exception
  {
    Category category = getModel1Factory().createCategory();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    resource.getContents().add(category);
    transaction.commit();

    EObject reloaded = resource.getEObject("/0");
    assertEquals(category, reloaded);
  }

  public void testReloadFromIndexComplex() throws Exception
  {
    Product1 product0 = getModel1Factory().createProduct1();
    product0.setName("test1");
    Product1 product1 = getModel1Factory().createProduct1();
    product1.setName("test2");
    Product1 product2 = getModel1Factory().createProduct1();
    product2.setName("test3");

    Category category = getModel1Factory().createCategory();
    category.getProducts().add(product0);
    category.getProducts().add(product1);
    category.getProducts().add(product2);

    Company company = getModel1Factory().createCompany();
    company.getCategories().add(category);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    resource.getContents().add(company);
    transaction.commit();

    EObject reloaded = resource.getEObject("//@categories.0/@products.2");
    assertEquals(product2, reloaded);
  }
}
