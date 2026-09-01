/*
 * Copyright (c) 2018, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.db.DBConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Map;

/**
 * Bug 527002: ClassCastException in mapping strategy.
 *
 * @author Eike Stepper
 */
@Skips({ IRepositoryConfig.CAPABILITY_AUDITING, IRepositoryConfig.CAPABILITY_BRANCHING })
public class Bugzilla_527002_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(DBConfig.PROP_TEST_MAPPING_STRATEGY, CDODBUtil.createHorizontalMappingStrategy());
  }

  public void testDelegatingMappingStrategy() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    Product1 first = getModel1Factory().createProduct1();
    Product1 second = getModel1Factory().createProduct1();
    category.getProducts().add(first);
    category.getProducts().add(second);
    category.getTopProducts().add(second);
    company.getCategories().add(category);
    resource.getContents().add(company);
    transaction.commit();

    session.close();
    session = openSession();
    CDOView view = session.openView();

    try
    {
      CDOResource persisted = view.getResource(getResourcePath("res"));
      Company persistedCompany = (Company)persisted.getContents().get(0);
      Category persistedCategory = persistedCompany.getCategories().get(0);
      assertEquals(2, persistedCategory.getProducts().size());
      assertEquals(persistedCategory.getProducts().get(1), persistedCategory.getTopProducts().get(0));
    }
    finally
    {
      view.close();
      session.close();
    }
  }
}
