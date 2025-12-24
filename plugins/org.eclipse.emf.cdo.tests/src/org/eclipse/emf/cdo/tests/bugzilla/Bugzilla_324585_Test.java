/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class Bugzilla_324585_Test extends AbstractCDOTest
{
  /**
   * To see the problem it may be necessary to limit the stack size with e.g. -Xss256k
   */
  public void testUpdate() throws CommitException
  {
    // user 1
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("test"));

    Category category1 = getModel1Factory().createCategory();
    resource1.getContents().add(category1);
    resource1.getContents().add(category1);
    transaction1.commit();

    // user 2
    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath("test"));

    EObject category2 = resource2.getContents().get(0);
    category2.eAdapters().add(new AdapterImpl());

    // user1
    EList<Product1> products = category1.getProducts();
    for (int i = 0; i < 10000; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Product" + i);
      products.add(product);
    }

    transaction1.commit();

    // user2
    session2.refresh();
  }
}
