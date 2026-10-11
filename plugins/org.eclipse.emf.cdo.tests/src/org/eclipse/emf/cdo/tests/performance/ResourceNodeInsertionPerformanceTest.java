/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.performance;

import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Measures repeated node insertion and bulk insertion into one new folder without committing.
 * Set {@code cdo.resourceNodeBenchmark.output} to an absolute file path to save the measurements.
 *
 * @author Eike Stepper
 */
public class ResourceNodeInsertionPerformanceTest extends AbstractCDOTest
{
  private static final int CHILD_COUNT = 330;

  public void testResourceNodeInsertionBenchmark() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    long baselineAdd = measure(transaction, "baseline-add", true, false);
    long optimizedAdd = measure(transaction, "optimized-add", false, false);
    long baselineAddAll = measure(transaction, "baseline-addAll", true, true);
    long optimizedAddAll = measure(transaction, "optimized-addAll", false, true);

    msg("330 child nodes, no commit; baseline emulates one full path lookup per insertion");
    msg("add(): baseline=" + baselineAdd / 1_000_000.0 + " ms, optimized=" + optimizedAdd / 1_000_000.0 + " ms");
    msg("addAll(): baseline=" + baselineAddAll / 1_000_000.0 + " ms, optimized=" + optimizedAddAll / 1_000_000.0 + " ms");
    msg("Path lookups: baseline=330 per case; optimized=0 for the transient-folder fast path");

    String outputPath = System.getProperty("cdo.resourceNodeBenchmark.output");
    if (outputPath != null)
    {
      Files.writeString(Path.of(outputPath),
          "scenario=" + getScenario() + "\n" + "children=" + CHILD_COUNT + "\n" + "baseline.add.ns=" + baselineAdd + "\n" + "optimized.add.ns=" + optimizedAdd
              + "\n" + "baseline.addAll.ns=" + baselineAddAll + "\n" + "optimized.addAll.ns=" + optimizedAddAll + "\n"
              + "baseline.pathLookups=330 per case\noptimized.pathLookups=0 for transient-folder fast path\n");
    }

    session.close();
  }

  private long measure(CDOTransaction transaction, String prefix, boolean baseline, boolean bulk)
  {
    CDOResourceFolder folder = transaction.createResourceFolder(getResourcePath("/" + prefix));
    List<CDOResourceNode> children = createChildren(transaction, prefix);
    EList<CDOResourceNode> nodes = folder.getNodes();

    long start = System.nanoTime();
    for (CDOResourceNode child : children)
    {
      if (baseline)
      {
        String path = folder.getPath() + "/" + child.getName();
        try
        {
          ((InternalCDOTransaction)transaction).getResourceNodeID(path);
          fail("Expected the new child path to be absent: " + path);
        }
        catch (CDOResourceNodeNotFoundException expected)
        {
          // The baseline validation performs this path lookup before inserting the child.
        }
      }

      if (!bulk)
      {
        nodes.add(child);
      }
    }

    if (bulk)
    {
      nodes.addAll(children);
    }

    long elapsed = System.nanoTime() - start;
    assertEquals(CHILD_COUNT, nodes.size());
    return elapsed;
  }

  private List<CDOResourceNode> createChildren(CDOTransaction transaction, String prefix)
  {
    List<CDOResourceNode> children = new ArrayList<>(CHILD_COUNT);

    for (int i = 0; i < CHILD_COUNT; i++)
    {
      CDOResource child = transaction.createResource(getResourcePath("/" + prefix + "-child-" + i));
      children.add(child);
    }

    return children;
  }
}
