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
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_342130_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    Model1Factory factory = getModel1Factory();
    Category cat = factory.createCategory();

    // List length must be 1 to reproduce problem
    Product1 product1 = factory.createProduct1();
    product1.setName("product1");
    cat.getProducts().add(product1);
    resource.getContents().add(cat);
    tx.commit();

    // Add 0 (makes list length 2)
    Product1 product2 = factory.createProduct1();
    product2.setName("product2");
    cat.getProducts().add(0, product2);

    // Set 1 (leaves list length 2)
    Product1 product3 = factory.createProduct1();
    product3.setName("product3");
    cat.getProducts().set(1, product3);

    // Remove 0 (makes list length 1)
    cat.getProducts().remove(0);

    tx.commit();

    tx.close();
    session.close();
  }
}
