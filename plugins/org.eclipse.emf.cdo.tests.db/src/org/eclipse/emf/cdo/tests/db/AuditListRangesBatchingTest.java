/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.general.AuditTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercises audit list batching with the range-based DB mapping.
 *
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_AUDITING, DBConfig.CAPABILITY_RANGES })
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
public class AuditListRangesBatchingTest extends AuditTest
{
  public void testLargeRangeShiftsAndHistoricalOrder() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource(getResourcePath("/largeRanges")); //$NON-NLS-1$
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    resource.getContents().add(company);

    List<Product1> values = new ArrayList<>();
    for (int i = 0; i < 134; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName(i == 2 || i == 3 ? "equal" : "value-" + i); //$NON-NLS-1$ //$NON-NLS-2$
      category.getProducts().add(product);
      values.add(product);
    }

    EList<Product1> list = category.getTopProducts();
    List<Product1> expected = new ArrayList<>();
    List<Long> timeStamps = new ArrayList<>();
    List<List<CDOID>> history = new ArrayList<>();

    commitAndCheck(transaction, list, expected, timeStamps, history);

    expected.add(values.get(0));
    list.add(values.get(0));
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.clear();
    expected.clear();
    for (int i = 0; i < 129; i++)
    {
      Product1 product = values.get(i);
      expected.add(product);
      list.add(product);
    }
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.add(0, values.get(129));
    expected.add(0, values.get(129));
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.add(64, values.get(130));
    expected.add(64, values.get(130));
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.add(values.get(131));
    expected.add(values.get(131));
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.remove(0);
    expected.remove(0);
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.remove(64);
    expected.remove(64);
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.remove(list.size() - 1);
    expected.remove(expected.size() - 1);
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.move(4, 120);
    Product1 moved = expected.remove(120);
    expected.add(4, moved);
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.move(119, 3);
    moved = expected.remove(3);
    expected.add(119, moved);
    commitAndCheck(transaction, list, expected, timeStamps, history);

    // The first insertion versions indexes 20..end. The second overlaps that range but also reaches
    // old-version rows at indexes 10..19, so one shift spans both row kinds.
    list.add(20, values.get(132));
    expected.add(20, values.get(132));
    list.add(10, values.get(133));
    expected.add(10, values.get(133));
    list.remove(18);
    expected.remove(18);
    list.set(70, values.get(131));
    expected.set(70, values.get(131));
    list.move(6, 100);
    moved = expected.remove(100);
    expected.add(6, moved);
    commitAndCheck(transaction, list, expected, timeStamps, history);

    assertEquals(expected.size(), list.size());
    closeSession1();
    session = openSession2();
    try
    {
      for (int i = 0; i < timeStamps.size(); i++)
      {
        assertProductIDs(session, timeStamps.get(i), history.get(i));
      }

      assertProductIDs(session, timeStamps.get(timeStamps.size() - 1), toIDs(expected));
    }
    finally
    {
      session.close();
    }
  }

  public void testAddThenSetBeforeStructuralShift() throws Exception
  {
    CDOSession session = openSession1();
    CDOTransaction transaction = session.openTransaction();
    Resource resource = transaction.createResource(getResourcePath("/addThenSet")); //$NON-NLS-1$
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    resource.getContents().add(company);

    List<Product1> values = new ArrayList<>();
    for (int i = 0; i < 131; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName("value-" + i); //$NON-NLS-1$
      category.getProducts().add(product);
      values.add(product);
    }

    EList<Product1> list = category.getTopProducts();
    List<Product1> expected = new ArrayList<>();
    for (int i = 0; i < 128; i++)
    {
      expected.add(values.get(i));
      list.add(values.get(i));
    }

    List<Long> timeStamps = new ArrayList<>();
    List<List<CDOID>> history = new ArrayList<>();
    commitAndCheck(transaction, list, expected, timeStamps, history);

    list.add(20, values.get(128));
    expected.add(20, values.get(128));
    list.set(20, values.get(129));
    expected.set(20, values.get(129));
    list.add(0, values.get(130));
    expected.add(0, values.get(130));
    commitAndCheck(transaction, list, expected, timeStamps, history);

    closeSession1();
    session = openSession2();
    try
    {
      for (int i = 0; i < timeStamps.size(); i++)
      {
        assertProductIDs(session, timeStamps.get(i), history.get(i), "/addThenSet"); //$NON-NLS-1$
      }
    }
    finally
    {
      session.close();
    }
  }

  private void commitAndCheck(CDOTransaction transaction, EList<Product1> list, List<Product1> expected, List<Long> timeStamps, List<List<CDOID>> history)
      throws Exception
  {
    long timeStamp = transaction.commit().getTimeStamp();
    timeStamps.add(timeStamp);
    List<CDOID> ids = toIDs(expected);
    history.add(ids);
    assertProductIDs(list, ids);
  }

  private void assertProductIDs(EList<Product1> actual, List<CDOID> expectedIDs)
  {
    assertEquals(expectedIDs.size(), actual.size());
    for (int i = 0; i < expectedIDs.size(); i++)
    {
      assertEquals("List item at index " + i, expectedIDs.get(i), CDOUtil.getCDOObject(actual.get(i)).cdoID()); //$NON-NLS-1$
    }
  }

  private void assertProductIDs(CDOSession session, long timeStamp, List<CDOID> expectedIDs)
  {
    assertProductIDs(session, timeStamp, expectedIDs, "/largeRanges"); //$NON-NLS-1$
  }

  private void assertProductIDs(CDOSession session, long timeStamp, List<CDOID> expectedIDs, String resourcePath)
  {
    CDOView view = session.openView(timeStamp);
    try
    {
      CDOResource resource = view.getResource(getResourcePath(resourcePath));
      Company company = (Company)resource.getContents().get(0);
      assertProductIDs(company.getCategories().get(0).getTopProducts(), expectedIDs);
    }
    finally
    {
      view.close();
    }
  }

  private List<CDOID> toIDs(List<Product1> products)
  {
    List<CDOID> ids = new ArrayList<>();
    for (Product1 product : products)
    {
      ids.add(CDOUtil.getCDOObject(product).cdoID());
    }

    return ids;
  }
}
