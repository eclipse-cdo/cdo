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
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOPrefetcherManager;
import org.eclipse.emf.cdo.view.CDOPrefetcherManager.Prefetcher;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Bug 576893 - Implement a CDOPrefetcherManager to prefetch and cache all valid revisions for a CDOViewSet.
 *
 * @author Eike Stepper
 */
public class Bugzilla_576893_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testPrefetcherManager() throws Exception
  {
    IOUtil.OUT().println("Creating...");
    Category root = getModel1Factory().createCategory();
    int created = 1 + createModel(root, 2, 3, 2);

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(root);

    IOUtil.OUT().println("Committing...");
    transaction.commit();
    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();

    IOUtil.OUT().println("\nTesting...");
    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    int[] cached = { 0 };
    int[] errors = { 0 };

    CDOPrefetcherManager prefetcherManager = new CDOPrefetcherManager(view.getResourceSet())
    {
      @Override
      protected Prefetcher createPrefetcher(CDOView view)
      {
        return new Prefetcher(view, rootID)
        {
          @Override
          protected void prefetch()
          {
            IOUtil.OUT().println("Prefetching " + getView().getBranch().getName() + "/" + CDOCommonUtil.formatTimeStamp(getView().getTimeStamp()));
            super.prefetch();
          }

          @Override
          protected CDORevision cacheRevision(CDORevision revision)
          {
            IOUtil.OUT().println("Caching " + revision);

            // Cache.
            CDORevision oldRevision = super.cacheRevision(revision);

            if (!(revision instanceof SyntheticCDORevision))
            {
              // Cache a copy.
              if (super.cacheRevision(revision.copy()) != revision)
              {
                ++errors[0];
              }

              // Cache original again.
              super.cacheRevision(revision);
            }

            ++cached[0];
            return oldRevision;
          }

          @Override
          protected void revisionEvicted(CDORevision revision)
          {
            System.out.println("Evicted: " + revision);
          }
        };
      }
    };

    prefetcherManager.activate();
    prefetcherManager.waitUntilPrefetched(DEFAULT_TIMEOUT);

    Prefetcher prefetcher = prefetcherManager.getPrefetcher(view);
    assertEquals(false, prefetcher.isDisposed());

    int oldSize = prefetcher.getSize();
    assertEquals(0, errors[0]);
    assertEquals(created, cached[0]);
    assertEquals(created, oldSize);

    IOUtil.OUT().println("\nTesting passive update...");
    errors[0] = 0;
    cached[0] = 0;

    session2.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    root.setName("changed");
    commitAndSync(transaction, view);
    assertEquals(0, errors[0]);
    assertEquals(1, cached[0]); // Added Category@OID4:0v2.
    assertEquals(1 + oldSize, prefetcher.getSize()); // Added Category@OID4:0v2.

    IOUtil.OUT().println("\nTesting cleanup...");
    prefetcherManager.cleanup();
    assertEquals(oldSize, prefetcher.getSize()); // Removed Category@OID4:0v1.

    IOUtil.OUT().println("\nTesting view target switch...");
    CDOBranch subBranch = view.getBranch().createBranch("subBranch");
    view.setBranch(subBranch);
    prefetcherManager.waitUntilPrefetched(DEFAULT_TIMEOUT);
    assertEquals(2 * oldSize, prefetcher.getSize()); // Main branch revisions
                                                     // + subBranch pointer revisions.

    IOUtil.OUT().println("\nTesting commits in sub branch...");
    errors[0] = 0;
    cached[0] = 0;

    transaction.setBranch(subBranch);
    root.setName("changed again");
    commitAndSync(transaction, view);

    assertEquals(0, errors[0]);
    assertEquals(1, cached[0]); // Added Category@OID4:1v1.
    assertEquals(2 * oldSize + 1, prefetcher.getSize()); // Main branch revisions
                                                         // + subBranch pointer revisions
                                                         // + Category@OID4:1v1.

    IOUtil.OUT().println("\nTesting cleanup in sub branch...");
    prefetcherManager.cleanup();

    List<CDORevision> prefetchedRevisions = prefetcher.getRevisions(rootID);
    List<InternalCDORevision> cachedRevisions = getRevisionsFromCache(rootID, session2);

    assertEquals(2 * oldSize - 2 + 1, prefetcher.getSize()); // Main branch revisions
                                                             // + subBranch pointer revisions
                                                             // - Category@OID4:0v2
                                                             // - Category@OID4:1v0->0v2
                                                             // + Category@OID4:1v1.

    view.close();
    assertEquals(true, prefetcher.isDisposed());
    assertEquals(null, prefetcherManager.getPrefetcher(view));

    prefetcherManager.deactivate();
  }

  private List<InternalCDORevision> getRevisionsFromCache(CDOID id, CDOSession session)
  {
    class IDList extends ArrayList<InternalCDORevision>
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean add(InternalCDORevision revision)
      {
        if (revision.getID() == id)
        {
          return super.add(revision);
        }

        return false;
      }
    }

    IDList result = new IDList();
    ((InternalCDORevisionManager)session.getRevisionManager()).getCache().getAllRevisions(result);
    return result;
  }
}
