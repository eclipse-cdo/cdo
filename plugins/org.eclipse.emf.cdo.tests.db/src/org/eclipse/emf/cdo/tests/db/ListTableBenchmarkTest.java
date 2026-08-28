/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A small benchmark-style test for commits with many list changes.
 * <p>
 * The initial model is committed before the timed part. The benchmark then performs
 * many {@link EList#move(int, int)} operations so that the measured commit is dominated
 * by list deltas rather than creation of new model objects.
 * <p>
 * The workload can be adjusted with system properties:
 * <ul>
 * <li>{@code cdo.listTableBenchmark.listCount}
 * <li>{@code cdo.listTableBenchmark.listSize}
 * <li>{@code cdo.listTableBenchmark.changesPerList}
 * <li>{@code cdo.listTableBenchmark.warmupChangesPerList}
 * </ul>
 *
 * @author Eike Stepper
 */
public class ListTableBenchmarkTest extends AbstractCDOTest
{
  private static final int LIST_COUNT = Integer.getInteger("cdo.listTableBenchmark.listCount", 100);

  private static final int LIST_SIZE = Integer.getInteger("cdo.listTableBenchmark.listSize", 1000);

  private static final int CHANGES_PER_LIST = Integer.getInteger("cdo.listTableBenchmark.changesPerList", 100);

  private static final int WARMUP_CHANGES_PER_LIST = Integer.getInteger("cdo.listTableBenchmark.warmupChangesPerList",
      Math.min(CHANGES_PER_LIST, 10));

  private CDOTransaction transaction;

  private Category root;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    root = getModel1Factory().createCategory();
    root.setName("ROOT");

    createModel();

    CDOSession session = openSession();
    transaction = session.openTransaction();

    Resource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(root);

    log("Creating benchmark model:");
    log("  Lists:            " + LIST_COUNT);
    log("  Elements/list:    " + LIST_SIZE);
    log("  Total elements:   " + (long)LIST_COUNT * LIST_SIZE);

    measure("  Commit took:      ", () -> transaction.commit());
    log("");

    if (WARMUP_CHANGES_PER_LIST > 0)
    {
      log("Warming up list-delta persistence:");
      log("  Changes/list:     " + WARMUP_CHANGES_PER_LIST);
      applyListChanges(WARMUP_CHANGES_PER_LIST);
      transaction.commit();
      log("");
    }
  }

  public void testManyListChanges() throws Exception
  {
    if (LIST_SIZE < 2)
    {
      fail("cdo.listTableBenchmark.listSize must be at least 2");
    }

    applyListChanges(CHANGES_PER_LIST);

    long changeCount = (long)LIST_COUNT * CHANGES_PER_LIST;
    log("Created list changes:");
    log("  Changes/list:     " + CHANGES_PER_LIST);
    log("  Total changes:    " + changeCount);

    double commitSeconds = measure("  Commit took:      ", () -> transaction.commit());
    if (commitSeconds > 0.0)
    {
      log("");
      log("Changes/second:     " + Math.round(changeCount / commitSeconds));
    }
  }

  private void applyListChanges(int changesPerList)
  {
    for (Category category : root.getCategories())
    {
      EList<Product1> products = category.getProducts();

      for (int change = 0; change < changesPerList; change++)
      {
        // Move an element over a large part of the list. This deliberately creates
        // index shifts and exercises the ListTable delta persistence code.
        int fromIndex = change % products.size();
        int toIndex = (fromIndex + products.size() / 2) % products.size();

        if (fromIndex == toIndex)
        {
          toIndex = (toIndex + 1) % products.size();
        }

        products.move(toIndex, fromIndex);
      }
    }
  }

  private void createModel()
  {
    EList<Category> categories = root.getCategories();

    for (int listIndex = 0; listIndex < LIST_COUNT; listIndex++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("Category-" + listIndex);
      categories.add(category);

      EList<Product1> products = category.getProducts();
      for (int elementIndex = 0; elementIndex < LIST_SIZE; elementIndex++)
      {
        Product1 product = getModel1Factory().createProduct1();
        product.setName("Product-" + listIndex + "-" + elementIndex);
        products.add(product);
      }
    }
  }

  private static double measure(String task, RunnableWithException runnable) throws Exception
  {
    long start = System.nanoTime();
    runnable.run();

    long end = System.nanoTime();
    double seconds = seconds(end - start);
    log(task + seconds + " seconds");
    return seconds;
  }

  private static double seconds(long nanos)
  {
    return nanos / 1_000_000_000d;
  }

  private static void log(String message)
  {
    IOUtil.OUT().println(message);
  }
}
