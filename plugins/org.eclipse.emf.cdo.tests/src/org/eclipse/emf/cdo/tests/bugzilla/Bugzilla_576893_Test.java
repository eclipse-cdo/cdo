/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOPrefetcherManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

/**
 * Bug 576893 - Implement a CDOPrefetcherManager to prefetch and cache all valid revisions for a CDOViewSet.
 *
 * @author Eike Stepper
 */
public class Bugzilla_576893_Test extends AbstractCDOTest
{
  public void testPrefetcherManager() throws Exception
  {
    IOUtil.OUT().println("Creating...");
    Category root = getModel1Factory().createCategory();
    int created = 1 + createModel(root, 2, 5, 10);

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(root);

    IOUtil.OUT().println("Committing...");
    transaction.commit();

    IOUtil.OUT().println("Testing...");
    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    int[] cached = { 0 };
    int[] errors = { 0 };
    int[] size = { 0 };

    CDOPrefetcherManager prefetcherManager = new CDOPrefetcherManager(view.getResourceSet())
    {
      @Override
      protected Prefetcher createPrefetcher(CDOView view)
      {
        return new Prefetcher(view)
        {
          @Override
          protected CDORevision cacheRevision(CDORevision revision)
          {
            // Cache.
            CDORevision oldRevision = super.cacheRevision(revision);

            // Cache a copy.
            if (super.cacheRevision(revision.copy()) != revision)
            {
              ++errors[0];
            }

            // Cache original again.
            super.cacheRevision(revision);

            ++cached[0];
            size[0] = getSize();
            return oldRevision;
          }
        };
      }
    };

    prefetcherManager.activate();
    prefetcherManager.waitUntilPrefetched();
    assertEquals(0, errors[0]);
    assertEquals(3 + created, cached[0]); // Root resource + test folder + res1
    assertEquals(3 + created, size[0]); // Root resource + test folder + res1

    IOUtil.OUT().println("Testing passive update...");
    errors[0] = 0;
    cached[0] = 0;
    int oldSize = size[0];

    session2.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    root.setName("changed");
    commitAndSync(transaction, view);

    prefetcherManager.waitUntilPrefetched();
    assertEquals(0, errors[0]);
    assertEquals(1, cached[0]); // v2.
    assertEquals(1 + oldSize, size[0]);

    view.close();
    prefetcherManager.deactivate();
  }
}
