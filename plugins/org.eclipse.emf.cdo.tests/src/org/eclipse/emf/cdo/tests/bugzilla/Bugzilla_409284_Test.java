/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.ContainmentCycleException;

import org.eclipse.emf.common.util.EList;

/**
 * @author Eike Stepper
 */
public class Bugzilla_409284_Test extends AbstractCDOTest
{
  @SuppressWarnings("unused")
  public void testContainmentCycle() throws Exception
  {
    // Client1 - Init
    Category a1 = createCategory("A");
    Category b1 = createCategory("B");
    Category c1 = createCategory("C");
    Category d1 = createCategory("D");
    Category e1 = createCategory("E");

    Company r1 = getModel1Factory().createCompany();
    r1.setName("R");

    r1.getCategories().add(a1);
    a1.getCategories().add(b1);
    b1.getCategories().add(c1);

    r1.getCategories().add(d1);
    d1.getCategories().add(e1);

    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("res"));

    resource1.getContents().add(r1);
    transaction1.commit();

    // Client2 - Load
    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false); // Important!
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getResource(getResourcePath("res"));

    Company r2 = (Company)resource2.getContents().get(0);

    Category a2 = loadCategory(r2.getCategories(), 0, "A");
    Category b2 = loadCategory(a2.getCategories(), 0, "B");
    Category c2 = loadCategory(b2.getCategories(), 0, "C");

    Category d2 = loadCategory(r2.getCategories(), 1, "D");
    Category e2 = loadCategory(d2.getCategories(), 0, "E");

    // Client1 - First tree move (element B from A to E)
    e1.getCategories().add(b1);
    transaction1.commit();

    // Client2 - Second tree move (element D from R to C)
    c2.getCategories().add(d2);

    try
    {
      transaction2.commit();
      fail("ContainmentCycleException expected");
    }
    catch (ContainmentCycleException expected)
    {
      // SUCCESS
    }
  }

  private Category createCategory(String name)
  {
    Category category = getModel1Factory().createCategory();
    category.setName(name);
    return category;
  }

  private Category loadCategory(EList<Category> categories, int i, String name)
  {
    Category category = categories.get(i);
    assertEquals(name, category.getName());
    return category;
  }
}
