/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.internal.db.BatchingContext;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

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
 * <li>{@code cdo.listTableBenchmark.appendsPerList}
 * <li>{@code cdo.listTableBenchmark.tailRemovesPerList}
 * <li>{@code cdo.listTableBenchmark.tailReplacementsPerList}
 * <li>{@code cdo.listTableBenchmark.sparseSetsPerList}
 * <li>{@code cdo.listTableBenchmark.clearAddsPerList}
 * </ul>
 *
 * @author Eike Stepper
 */
@Requires(DBConfig.CAPABILITY)
public class ListTableBenchmarkTest extends AbstractCDOTest
{
  private static final int LIST_COUNT = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.listCount", 100);

  private static final int LIST_SIZE = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.listSize", 1000);

  private static final int CHANGES_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.changesPerList", 100);

  private static final int APPENDS_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.appendsPerList", 10);

  private static final int TAIL_REMOVES_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.tailRemovesPerList", 10);

  private static final int TAIL_REPLACEMENTS_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.tailReplacementsPerList", 10);

  private static final int SPARSE_SETS_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.sparseSetsPerList", 10);

  private static final int CLEAR_ADDS_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.clearAddsPerList", 10);

  private static final int WARMUP_CHANGES_PER_LIST = OMPlatform.INSTANCE.getProperty("cdo.listTableBenchmark.warmupChangesPerList",
      Math.min(CHANGES_PER_LIST, 10));

  private CDOTransaction transaction;

  private Category root;

  private boolean wasStatisticsEnabled;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    wasStatisticsEnabled = BatchingContext.isStatisticsEnabled();
    BatchingContext.setStatisticsEnabled(true);

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

  @Override
  protected void doTearDown() throws Exception
  {
    BatchingContext.setStatisticsEnabled(wasStatisticsEnabled);
    super.doTearDown();
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

  public void testManyListAppends() throws Exception
  {
    if (APPENDS_PER_LIST < 1)
    {
      fail("cdo.listTableBenchmark.appendsPerList must be at least 1");
    }

    applyListAppends(APPENDS_PER_LIST);

    long appendCount = (long)LIST_COUNT * APPENDS_PER_LIST;
    log("Created list appends:");
    log("  Lists:            " + LIST_COUNT);
    log("  Initial size/list: " + LIST_SIZE);
    log("  Appends/list:     " + APPENDS_PER_LIST);
    log("  Total appends:    " + appendCount);

    double commitSeconds = measure("  Commit took:      ", () -> transaction.commit());
    if (commitSeconds > 0.0)
    {
      log("");
      log("Appends/second:     " + Math.round(appendCount / commitSeconds));
    }
  }

  public void testManyListTailRemoves() throws Exception
  {
    requirePositive(TAIL_REMOVES_PER_LIST, "cdo.listTableBenchmark.tailRemovesPerList");
    requireEnoughElements(TAIL_REMOVES_PER_LIST);

    for (Category category : root.getCategories())
    {
      EList<Product1> products = category.getProducts();
      for (int remove = 0; remove < TAIL_REMOVES_PER_LIST; remove++)
      {
        products.remove(products.size() - 1);
      }
    }

    measureListCommit("Created tail removals:", "Removes/list:", TAIL_REMOVES_PER_LIST, "Tail removals/second:", (long)LIST_COUNT * TAIL_REMOVES_PER_LIST);
  }

  public void testManyListTailReplacements() throws Exception
  {
    requirePositive(TAIL_REPLACEMENTS_PER_LIST, "cdo.listTableBenchmark.tailReplacementsPerList");

    for (Category category : root.getCategories())
    {
      EList<Product1> products = category.getProducts();
      for (int replacement = 0; replacement < TAIL_REPLACEMENTS_PER_LIST; replacement++)
      {
        int index = products.size() - 1;
        products.set(index, createProduct("TailReplacement-" + category.getName() + "-" + replacement));
      }
    }

    measureListCommit("Created tail replacements:", "Replacements/list:", TAIL_REPLACEMENTS_PER_LIST, "Tail replacements/second:",
        (long)LIST_COUNT * TAIL_REPLACEMENTS_PER_LIST);
  }

  public void testManyListSparseSets() throws Exception
  {
    requirePositive(SPARSE_SETS_PER_LIST, "cdo.listTableBenchmark.sparseSetsPerList");
    requireEnoughElements(SPARSE_SETS_PER_LIST);

    for (Category category : root.getCategories())
    {
      EList<Product1> products = category.getProducts();
      for (int set = 0; set < SPARSE_SETS_PER_LIST; set++)
      {
        int index = set * products.size() / SPARSE_SETS_PER_LIST;
        products.set(index, createProduct("SparseSet-" + category.getName() + "-" + set));
      }
    }

    measureListCommit("Created sparse sets:", "Sets/list:", SPARSE_SETS_PER_LIST, "Sparse sets/second:", (long)LIST_COUNT * SPARSE_SETS_PER_LIST);
  }

  public void testManyListClearAndAdds() throws Exception
  {
    requirePositive(CLEAR_ADDS_PER_LIST, "cdo.listTableBenchmark.clearAddsPerList");

    for (Category category : root.getCategories())
    {
      EList<Product1> products = category.getProducts();
      products.clear();
      for (int add = 0; add < CLEAR_ADDS_PER_LIST; add++)
      {
        products.add(createProduct("ClearAdd-" + category.getName() + "-" + add));
      }
    }

    measureListCommit("Created clear/add changes:", "Adds/list:", CLEAR_ADDS_PER_LIST, "Clear/add rows/second:", (long)LIST_COUNT * CLEAR_ADDS_PER_LIST);
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

  private void applyListAppends(int appendsPerList)
  {
    for (Category category : root.getCategories())
    {
      EList<Product1> products = category.getProducts();

      for (int append = 0; append < appendsPerList; append++)
      {
        products.add(createProduct("Append-" + category.getName() + "-" + append));
      }
    }
  }

  private Product1 createProduct(String name)
  {
    Product1 product = getModel1Factory().createProduct1();
    product.setName(name);
    return product;
  }

  private void measureListCommit(String title, String countLabel, int count, String rateLabel, long totalCount) throws Exception
  {
    log(title);
    log("  Lists:            " + LIST_COUNT);
    log("  Initial size/list: " + LIST_SIZE);
    log("  " + countLabel + "     " + count);
    log("  Total changes:    " + totalCount);

    double commitSeconds = measure("  Commit took:      ", () -> transaction.commit());
    if (commitSeconds > 0.0)
    {
      log("");
      log(rateLabel + " " + Math.round(totalCount / commitSeconds));
    }
  }

  private void requirePositive(int value, String property)
  {
    if (value < 1)
    {
      fail(property + " must be at least 1");
    }
  }

  private void requireEnoughElements(int count)
  {
    if (LIST_SIZE < count)
    {
      fail("cdo.listTableBenchmark.listSize must be at least " + count);
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
