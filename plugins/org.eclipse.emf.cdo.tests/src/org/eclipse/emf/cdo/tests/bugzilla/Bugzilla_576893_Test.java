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
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
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

import org.eclipse.emf.ecore.resource.ResourceSet;

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
    Category root = getModel1Factory().createCategory();
    int created = 1 + createModel(root, 2, 3, 2);

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res1"));
    resource.getContents().add(root);

    transaction.commit();
    CDOID rootID = CDOUtil.getCDOObject(root).cdoID();

    // =======================================================================================
    IOUtil.OUT().println("Testing open view...");
    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    TestPrefetcherManager prefetcherManager = new TestPrefetcherManager(view.getResourceSet(), rootID);
    prefetcherManager.activate();
    waitUntilPrefetched(prefetcherManager);

    Prefetcher prefetcher = prefetcherManager.getPrefetcher(view);
    assertEquals(false, prefetcher.isDisposed());

    int oldSize = prefetcher.getSize();
    assertEquals(0, prefetcherManager.errors);
    assertEquals(created, prefetcherManager.added);
    assertEquals(created, oldSize);

    // =======================================================================================
    IOUtil.OUT().println("\nTesting passive update...");
    prefetcherManager.reset();

    session2.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    root.setName("changed");
    commitAndSync(transaction, view);

    assertPrefetchedRevisionsAreCached(prefetcher);
    assertEquals(0, prefetcherManager.errors);
    assertEquals(1, prefetcherManager.added); // Added Category@OID4:0v2.
    assertEquals(1 + oldSize, prefetcher.getSize()); // Added Category@OID4:0v2.

    // =======================================================================================
    IOUtil.OUT().println("\nTesting cleanup...");
    prefetcherManager.cleanup();

    assertPrefetchedRevisionsAreCached(prefetcher);
    assertEquals(oldSize, prefetcher.getSize()); // Removed Category@OID4:0v1.

    // =======================================================================================
    IOUtil.OUT().println("\nTesting view target switch...");
    CDOBranch subBranch = view.getBranch().createBranch("subBranch");
    view.setBranch(subBranch);

    waitUntilPrefetched(prefetcherManager);
    assertEquals(2 * oldSize, prefetcher.getSize()); // Main branch revisions
                                                     // + subBranch pointer revisions.

    // =======================================================================================
    IOUtil.OUT().println("\nTesting committing in sub branch...");
    prefetcherManager.reset();

    transaction.setBranch(subBranch);
    root.setName("changed again");
    commitAndSync(transaction, view);

    assertPrefetchedRevisionsAreCached(prefetcher);
    assertEquals(0, prefetcherManager.errors);
    assertEquals(1, prefetcherManager.added); // Added Category@OID4:1v1.
    assertEquals(2 * oldSize + 1, prefetcher.getSize()); // Main branch revisions
                                                         // + subBranch pointer revisions
                                                         // + Category@OID4:1v1.

    // =======================================================================================
    IOUtil.OUT().println("\nTesting cleanup in sub branch...");
    prefetcherManager.cleanup();
    assertPrefetchedRevisionsAreCached(prefetcher);
    assertEquals(2 * oldSize - 2 + 1, prefetcher.getSize()); // Main branch revisions
                                                             // + subBranch pointer revisions
                                                             // - Category@OID4:0v2
                                                             // - Category@OID4:1v0->0v2
                                                             // + Category@OID4:1v1.

    // =======================================================================================
    IOUtil.OUT().println("\nTesting close view...");

    // Remember because the prefetcher manager is bound to the view set of this resource set.
    ResourceSet resourceSet = view.getResourceSet();

    view.close();
    assertEquals(true, prefetcher.isDisposed());
    assertEquals(null, prefetcherManager.getPrefetcher(view));

    // =======================================================================================
    IOUtil.OUT().println("\nTesting new view in sub branch...");
    view = session2.openView(subBranch.getHead(), resourceSet);

    waitUntilPrefetched(prefetcherManager);
    prefetcher = prefetcherManager.getPrefetcher(view);
    assertEquals(false, prefetcher.isDisposed());
    assertEquals(2 * oldSize - 2 + 1, prefetcher.getSize()); // Main branch revisions
                                                             // + subBranch pointer revisions
                                                             // - Category@OID4:0v2
                                                             // - Category@OID4:1v0->0v2
                                                             // + Category@OID4:1v1.

    // =======================================================================================
    IOUtil.OUT().println("\nTesting new session and new view in sub branch...");
    session2.close();
    session2 = openSession();
    view = session2.openView(subBranch.getHead(), resourceSet);

    waitUntilPrefetched(prefetcherManager);
    prefetcher = prefetcherManager.getPrefetcher(view);
    assertEquals(false, prefetcher.isDisposed());
    assertEquals(2 * oldSize - 2 + 1, prefetcher.getSize()); // Main branch revisions
                                                             // + subBranch pointer revisions
                                                             // - Category@OID4:0v2
                                                             // - Category@OID4:1v0->0v2
                                                             // + Category@OID4:1v1.

    // =======================================================================================
    view.close();
    assertEquals(true, prefetcher.isDisposed());
    assertEquals(null, prefetcherManager.getPrefetcher(view));

    prefetcherManager.deactivate();
  }

  private static void waitUntilPrefetched(TestPrefetcherManager prefetcherManager)
  {
    assertTrue(prefetcherManager.waitUntilPrefetched(DEFAULT_TIMEOUT));

    for (Prefetcher prefetcher : prefetcherManager.getPrefetchers())
    {
      assertPrefetchedRevisionsAreCached(prefetcher);
    }
  }

  private static void assertPrefetchedRevisionsAreCached(Prefetcher prefetcher)
  {
    CDORevisionCache cache = ((InternalCDORevisionManager)prefetcher.getView().getSession().getRevisionManager()).getCache();

    prefetcher.handleRevisions(r -> {
      CDORevision cachedRevision = cache.getRevisionByVersion(r.getID(), r);
      assertTrue(cachedRevision == r);
      return true;
    });
  }

  @SuppressWarnings("unused")
  private static List<CDORevision> getRevisionsFromPrefetcher(CDOID id, Prefetcher prefetcher)
  {
    List<CDORevision> result = new ArrayList<>();
    prefetcher.handleRevisions(r -> {
      if (r.getID() == id)
      {
        result.add(r);
      }

      return true;
    });

    return result;
  }

  @SuppressWarnings("unused")
  private static List<InternalCDORevision> getRevisionsFromCache(CDOID id, CDOSession session)
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

  /**
   * @author Eike Stepper
   */
  private static final class TestPrefetcherManager extends CDOPrefetcherManager
  {
    public int added;

    public int errors;

    private final CDOID rootID;

    public TestPrefetcherManager(ResourceSet resourceSet, CDOID rootID)
    {
      super(resourceSet);
      this.rootID = rootID;
    }

    public void reset()
    {
      added = 0;
      errors = 0;
    }

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
        protected CDORevision addRevision(CDORevision revision)
        {
          IOUtil.OUT().println("  Adding  " + revision);

          // Add.
          CDORevision oldRevision = super.addRevision(revision);

          if (!(revision instanceof SyntheticCDORevision))
          {
            // Add a copy.
            if (super.addRevision(revision.copy()) != revision)
            {
              ++errors;
            }

            // Add original again.
            super.addRevision(revision);
          }

          ++added;
          return oldRevision;
        }

        @Override
        protected void revisionRemoved(CDORevision revision)
        {
          IOUtil.OUT().println("  Removed " + revision);
        }

        @Override
        protected void revisionIgnored(CDORevision revision)
        {
          // IOUtil.OUT().println(" Ignored " + revision);
        }
      };
    }
  }
}
