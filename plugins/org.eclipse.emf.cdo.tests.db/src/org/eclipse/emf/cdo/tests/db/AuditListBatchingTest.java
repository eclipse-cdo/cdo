/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Focused Audit list batching integration tests.
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
public class AuditListBatchingTest extends AuditTest
{
  public void testIndependentListsAndHistory() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource(getResourcePath("/res1")); //$NON-NLS-1$

    Company company = getModel1Factory().createCompany();
    Product1 first = getModel1Factory().createProduct1();
    first.setName("duplicate"); //$NON-NLS-1$
    Product1 second = getModel1Factory().createProduct1();
    second.setName("duplicate"); //$NON-NLS-1$
    Product1 third = getModel1Factory().createProduct1();
    third.setName("replacement"); //$NON-NLS-1$

    List<Category> categories = new ArrayList<>();

    for (int i = 0; i < 4; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("Category-" + i); //$NON-NLS-1$
      company.getCategories().add(category);
      categories.add(category);
    }

    categories.get(0).getProducts().add(first);
    categories.get(0).getProducts().add(second);
    categories.get(0).getProducts().add(third);

    for (Category category : categories)
    {
      category.getTopProducts().add(first);
    }

    resource.getContents().add(company);
    CDOCommitInfo initial = transaction.commit();

    for (Category category : categories)
    {
      category.getTopProducts().add(1, second);
    }

    CDOCommitInfo batched = transaction.commit();

    for (Category category : categories)
    {
      category.getTopProducts().set(1, third);
    }

    categories.get(1).getTopProducts().remove(0);
    categories.get(2).getTopProducts().move(0, 1);
    categories.get(3).getTopProducts().clear();
    CDOCommitInfo changed = transaction.commit();

    assertEquals(2, categories.get(0).getTopProducts().size());
    assertEquals(1, categories.get(1).getTopProducts().size());
    assertEquals(third, categories.get(2).getTopProducts().get(0));
    assertEquals(0, categories.get(3).getTopProducts().size());

    closeSession1();
    session = openSession2();

    try
    {
      assertTopProductNames(session, initial.getTimeStamp(), new String[] { "duplicate" }, new String[] { "duplicate" }, //$NON-NLS-1$ //$NON-NLS-2$
          new String[] { "duplicate" }, new String[] { "duplicate" }); //$NON-NLS-1$ //$NON-NLS-2$

      assertTopProductNames(session, batched.getTimeStamp(), new String[] { "duplicate", "duplicate" }, //$NON-NLS-1$ //$NON-NLS-2$
          new String[] { "duplicate", "duplicate" }, new String[] { "duplicate", "duplicate" }, //$NON-NLS-1$ //$NON-NLS-2$
          new String[] { "duplicate", "duplicate" }); //$NON-NLS-1$ //$NON-NLS-2$

      assertTopProductNames(session, changed.getTimeStamp(), new String[] { "duplicate", "replacement" }, //$NON-NLS-1$ //$NON-NLS-2$
          new String[] { "replacement" }, new String[] { "replacement", "duplicate" }, new String[0]); //$NON-NLS-1$
    }
    finally
    {
      session.close();
    }
  }

  private void assertTopProductNames(CDOSession session, long timeStamp, String[]... expectedNames)
  {
    CDOView view = session.openView(timeStamp);

    try
    {
      CDOResource resource = view.getResource(getResourcePath("/res1")); //$NON-NLS-1$
      Company company = (Company)resource.getContents().get(0);

      for (int i = 0; i < expectedNames.length; i++)
      {
        List<Product1> products = company.getCategories().get(i).getTopProducts();
        assertEquals(expectedNames[i].length, products.size());

        for (int j = 0; j < products.size(); j++)
        {
          assertEquals(expectedNames[i][j], products.get(j).getName());
        }
      }
    }
    finally
    {
      view.close();
    }
  }
}
