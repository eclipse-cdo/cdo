/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

/**
 * [DB] Range-based list mappings can be very slow.
 * <p>
 * See bug 357441
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class Bugzilla_357441_Test extends AbstractCDOTest
{
  private final int NUM_PRODUCTS = 200;

  @Skips("DB.ranges")
  // Too slow in DB.ranges (11 minutes), see bug 357441
  public void testManyListChanges() throws Exception
  {
    disableConsole();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    Category catA = getModel1Factory().createCategory();
    catA.setName("CatA");
    company.getCategories().add(catA);
    Category catB = getModel1Factory().createCategory();
    catB.setName("CatB");
    company.getCategories().add(catB);
    resource.getContents().add(company);

    for (int i = 0; i < NUM_PRODUCTS; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("Product" + i);
      catA.getProducts().add(product);
    }

    transaction.commit();

    // Do a lot of changes
    for (int i = 0; i < NUM_PRODUCTS; i++)
    {
      Product1 p = catA.getProducts().remove(0);
      catB.getProducts().add(p);
      catB.getProducts().move(0, p);

      long start = System.currentTimeMillis();
      transaction.commit();

      System.out.println(i + "," + (System.currentTimeMillis() - start));
    }

    catA.setName(catA.getName() + " empty");
    transaction.commit();
  }

  @Override
  public void disableConsole()
  {
    OMPlatform.INSTANCE.setDebugging(false);
    OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
    // OMPlatform.INSTANCE.removeLogHandler(PrintLogHandler.CONSOLE);
  }
}
