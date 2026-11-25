/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache.AdditionEvent;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.common.revision.AbstractCDORevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheAuditing;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheNonAuditing;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader4;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.spi.cdo.CDOPermissionUpdater;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
@Requires({ IRepositoryConfig.CAPABILITY_BRANCHING, "MEM" })
public class RevisionManagerTest extends AbstractCDOTest
{
  private static final EClass CLASS = EcorePackage.eINSTANCE.getEAnnotation();

  private static final int DETACH = -1;

  protected InternalRepository repository;

  private MEMStore store;

  protected InternalCDOSession session;

  private InternalSession serverSession;

  private InternalCDOBranchManager branchManager;

  private CDOID objectID;

  private int branchID;

  private CDOBranch branch0;

  private CDOBranch branch1;

  private CDOBranch branch2;

  private CDOBranch branch3;

  private CDOBranch branch4;

  private InternalCDORevision[] revisions0;

  private InternalCDORevision[] revisions1;

  private InternalCDORevision[] revisions4;

  private TestRevisionManager revisionManager;

  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    properties.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    if (getRepositoryConfig().idGenerationLocation() == IDGenerationLocation.STORE)
    {
      objectID = CDOIDUtil.createLong(2);
    }
    else
    {
      objectID = CDOIDUtil.createUUID();
    }

    Field disableGC = ReflectUtil.getField(AbstractCDORevisionCache.class, "disableGC");
    ReflectUtil.setValue(disableGC, null, true);

    repository = getRepository();
    store = (MEMStore)repository.getStore();

    session = (InternalCDOSession)openSession();
    serverSession = repository.getSessionManager().getSession(session.getSessionID());
    StoreThreadLocal.setSession(serverSession);

    branchManager = repository.getBranchManager();
    branchID = 0;

    branch0 = branchManager.getMainBranch();
    revisions0 = fillBranch(branch0, 10, 10, 20, 50, 20, DETACH);

    revisions1 = createBranch(revisions0[1], 5, 10, 20, 10, DETACH);
    branch1 = revisions1[0].getBranch();

    branch2 = createBranch(revisions1[3]);

    branch3 = createBranch(revisions1[1]);

    revisions4 = createBranch(branch3, branch3.getBase().getTimeStamp() + 10, 30, DETACH);
    branch4 = revisions4[0].getBranch();

    dumpRevisions("MEMStore", store.getAllRevisions());
    revisionManager = (TestRevisionManager)getRevisionManager(repository, session);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    StoreThreadLocal.release();
    IOUtil.close(session);

    Field disableGC = ReflectUtil.getField(AbstractCDORevisionCache.class, "disableGC");
    ReflectUtil.setValue(disableGC, null, false);

    super.doTearDown();
  }

  protected InternalCDORevisionManager getRevisionManager(InternalRepository repository, InternalCDOSession session)
  {
    return repository.getRevisionManager();
  }

  protected String getLocation()
  {
    return "Server";
  }

  protected void dumpCache(CDOBranchPoint branchPoint)
  {
    dumpRevisions(getLocation() + "Cache: Getting " + branchPoint, revisionManager.getCache().getAllRevisions());
  }

  protected CDOBranchPoint getBranchPoint(CDOBranch branch, long timeStamp)
  {
    return branch.getPoint(timeStamp);
  }

  private InternalCDORevision[] fillBranch(CDOBranch branch, long offset, long... durations)
  {
    InternalCDORevision[] revisions = new InternalCDORevision[durations.length];
    long timeStamp = branch.getBase().getTimeStamp() + offset;
    for (int i = 0; i < durations.length; i++)
    {
      long duration = durations[i];
      CDOBranchPoint branchPoint = branch.getPoint(timeStamp);

      if (duration == DETACH)
      {
        revisions[i] = store.detachObject(objectID, branch, timeStamp - 1);
      }
      else
      {
        timeStamp += duration;

        revisions[i] = new CDORevisionImpl(CLASS);
        revisions[i].setID(objectID);
        revisions[i].setBranchPoint(branchPoint);
        revisions[i].setRevised(timeStamp - 1);
        revisions[i].setVersion(i + 1);
        store.addRevision(revisions[i], false);
      }
    }

    return revisions;
  }

  private InternalCDORevision[] createBranch(CDOBranch baseBranch, long baseTimeStamp, long offset, long... durations)
  {
    CDOBranch branch = doCreateBranch(baseBranch, baseTimeStamp);
    return fillBranch(branch, offset, durations);
  }

  private InternalCDORevision[] createBranch(InternalCDORevision revision, long offset, long... durations)
  {
    CDOBranch baseBranch = revision.getBranch();
    long baseTimeStamp = getMiddleOfValidity(revision);
    return createBranch(baseBranch, baseTimeStamp, offset, durations);
  }

  private CDOBranch createBranch(InternalCDORevision revision)
  {
    CDOBranch baseBranch = revision.getBranch();
    long baseTimeStamp = getMiddleOfValidity(revision);
    return doCreateBranch(baseBranch, baseTimeStamp);
  }

  private CDOBranch doCreateBranch(CDOBranch baseBranch, long baseTimeStamp)
  {
    while (repository.getTimeStamp() < baseTimeStamp)
    {
      sleep(1);
    }

    String name = "branch" + ++branchID;
    if (baseBranch.isMainBranch())
    {
      name = getBranchName(name);
    }

    return baseBranch.createBranch(name, baseTimeStamp);
  }

  private long getMiddleOfValidity(InternalCDORevision revision)
  {
    long timeStamp = revision.getTimeStamp();
    long revised = revision.getRevised();
    if (revised == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      revised = timeStamp + 100;
    }

    return timeStamp / 2 + revised / 2;
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  protected InternalCDORevision getRevision(CDOBranch branch, long timeStamp)
  {
    CDOBranchPoint branchPoint = branch.getPoint(timeStamp);
    dumpCache(branchPoint);
    return revisionManager.getRevision(objectID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  private void prefetchBaseline(CDOBranch branch, int levelsUp)
  {
    long timeStamp = 0;
    while (--levelsUp >= 0)
    {
      timeStamp = branch.getBase().getTimeStamp();
      branch = branch.getBase().getBranch();
    }

    getRevision(branch, timeStamp);
    assertLoads(1);
  }

  private static void assertRevision(InternalCDORevision expected, InternalCDORevision actual)
  {
    if (expected == null)
    {
      assertEquals(null, actual);
    }
    else
    {
      assertEquals(expected.getID(), actual.getID());
      assertEquals(expected.getBranch().getID(), actual.getBranch().getID());
      assertEquals(expected.getVersion(), actual.getVersion());
    }
  }

  private void assertLoads(int expected)
  {
    assertEquals(expected, revisionManager.getLoadCounter());
    revisionManager.resetLoadCounter();
  }

  public void testRevisionRequestLookupModes() throws Exception
  {
    assertEquals(LookupMode.CACHE_THEN_LOADER, Request.Config.DEFAULT.getLookupMode());
    assertEquals(CDORevision.DEPTH_NONE, Request.Config.DEFAULT.getPrefetchDepth());
    assertFalse(Request.Config.DEFAULT.isPrefetchLockStates());

    CDOBranchPoint branchPoint = getBranchPoint(branch0, getMiddleOfValidity(revisions0[0]));
    revisionManager.getCache().clear();

    CDORevision missing = revisionManager.request().lookupCacheOnly().getRevision(objectID, branchPoint);
    assertNull(missing);
    assertLoads(0);

    CDORevision loaded = revisionManager.request().lookupMode(LookupMode.CACHE_THEN_LOADER).prefetchDepth(CDORevision.DEPTH_NONE).prefetchLockStates(true)
        .getRevision(objectID, branchPoint);
    assertNotNull(loaded);
    assertLoads(1);

    CDORevision cached = revisionManager.request().lookupMode(LookupMode.CACHE_ONLY).getRevision(objectID, branchPoint);
    assertNotNull(cached);
    assertLoads(0);

    CDORevision loadedOnly = revisionManager.request().lookupMode(LookupMode.LOADER_ONLY).getRevision(objectID, branchPoint);
    assertNotNull(loadedOnly);
    assertLoads(1);

    cached = revisionManager.request().lookupMode(LookupMode.CACHE_ONLY).getRevision(objectID, branchPoint);
    assertNotNull(cached);
    assertLoads(0);
  }

  @SuppressWarnings("deprecation")
  public void testHistoricalRevisionLookupAdapters()
  {
    CDOBranchPoint branchPoint = getBranchPoint(branch0, getMiddleOfValidity(revisions0[0]));
    revisionManager.getCache().clear();

    assertNull(revisionManager.getRevision(objectID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, false));
    assertEquals(LookupMode.CACHE_ONLY, revisionManager.getLastConfig().getLookupMode());
    assertEquals(CDORevision.UNCHUNKED, revisionManager.getLastConfig().getReferenceChunk());
    assertLoads(0);

    assertNotNull(revisionManager.getRevision(objectID, branchPoint, 0, CDORevision.DEPTH_NONE, true));
    assertEquals(LookupMode.CACHE_THEN_LOADER, revisionManager.getLastConfig().getLookupMode());
    assertEquals(0, revisionManager.getLastConfig().getReferenceChunk());
    assertLoads(1);

    revisionManager.getCache().clear();
    assertNotNull(revisionManager.getRevision(objectID, branchPoint, 7, CDORevision.DEPTH_NONE, true));
    assertEquals(LookupMode.CACHE_THEN_LOADER, revisionManager.getLastConfig().getLookupMode());
    assertEquals(7, revisionManager.getLastConfig().getReferenceChunk());
    assertLoads(1);
  }

  public void testBranch0_Initial() throws Exception
  {
    CDOBranch branch = branch0;
    long timeStamp = revisions0[0].getTimeStamp() - 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1); // TODO Shouldn't this be 0?
  }

  public void testBranch0_Normal() throws Exception
  {
    CDOBranch branch = branch0;
    for (int i = 0; i < revisions0.length - 1; i++)
    {
      InternalCDORevision expected = revisions0[i];
      long timeStamp = getMiddleOfValidity(expected);

      InternalCDORevision revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(1);

      revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(0);
    }
  }

  public void testBranch0_Detached() throws Exception
  {
    CDOBranch branch = branch0;
    long timeStamp = revisions0[4].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testCacheHitIsNotResurrectedByDetach() throws Exception
  {
    CDOBranch branch = branch0;
    CDOBranchPoint head = branch.getHead();
    InternalCDORevision captured = new CDORevisionImpl(CLASS);
    captured.setID(objectID);
    captured.setBranchPoint(head);
    captured.setVersion(100);
    revisionManager.internRevision(captured);

    RevisionInfo.Available.Normal result = new RevisionInfo.Available.Normal(objectID, head, captured);
    revisionManager.reviseLatest(objectID, branch);
    assertNull(revisionManager.getCache().getRevision(objectID, head));

    result.processResult(revisionManager, null, null, 0);

    assertNull("A captured cache hit must not be re-interned after detach", revisionManager.getCache().getRevision(objectID, head));
  }

  public void testNonAuditingCacheHitIsNotResurrectedByDetach() throws Exception
  {
    CDORevisionCacheNonAuditing cache = new CDORevisionCacheNonAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setCache(cache);

    CDOBranch branch = branch0;
    CDOBranchPoint head = branch.getHead();
    InternalCDORevision captured = new CDORevisionImpl(CLASS);
    captured.setID(objectID);
    captured.setBranchPoint(head);
    captured.setVersion(100);
    manager.internRevision(captured);

    RevisionInfo.Available.Normal result = new RevisionInfo.Available.Normal(objectID, head, captured);
    manager.reviseLatest(objectID, branch);
    assertNull(cache.getRevision(objectID, head));

    result.processResult(manager, null, null, 0);

    assertNull("A captured cache hit must not be re-interned after detach", cache.getRevision(objectID, head));
  }

  public void testPermissionUpdaterRevisionLoadFailsPromptlyAndReleasesCoordination() throws Exception
  {
    CDOPermissionUpdater originalUpdater = session.options().getPermissionUpdater();
    AtomicBoolean loadAttempted = new AtomicBoolean();
    session.options().setPermissionUpdater(new CDOPermissionUpdater()
    {
      @Override
      public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession updaterSession, Set<InternalCDORevision> revisions)
      {
        loadAttempted.set(true);
        updaterSession.getRevisionManager().request().lookupMode(LookupMode.LOADER_ONLY).getRevision(objectID, branch0.getHead());
        return Collections.emptyMap();
      }
    });

    try
    {
      try
      {
        session.updatePermissions();
        fail("A permission updater's synchronous revision load must fail promptly");
      }
      catch (IllegalStateException ex)
      {
        assertTrue("The failure must explain the permission-updater restriction", ex.getMessage().contains("permission updater"));
      }
    }
    finally
    {
      session.options().setPermissionUpdater(originalUpdater);
    }

    assertTrue("The custom updater must attempt the prohibited load", loadAttempted.get());
    CDORevision loaded = revisionManager.request().lookupMode(LookupMode.LOADER_ONLY).getRevision(objectID, branch0.getPoint(revisions0[0].getTimeStamp()));
    assertNotNull("Revision loading must work after the updater has terminated", loaded);
  }

  public void testPermissionUpdaterCanPerformCacheOnlyLookup()
  {
    CDORevision revision = session.getRevisionManager().request().getRevision(objectID, branch0.getPoint(revisions0[0].getTimeStamp()));
    assertNotNull("The test revision must be present in the session cache", revision);
    CDOPermissionUpdater originalUpdater = session.options().getPermissionUpdater();
    AtomicBoolean lookedUp = new AtomicBoolean();
    session.options().setPermissionUpdater(new CDOPermissionUpdater()
    {
      @Override
      public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession updaterSession, Set<InternalCDORevision> revisions)
      {
        CDORevision cached = updaterSession.getRevisionManager().request().lookupCacheOnly().getRevision(revision.getID(),
            revision.getBranch().getPoint(revision.getTimeStamp()));
        assertSame(revision, cached);
        lookedUp.set(true);
        return Collections.emptyMap();
      }
    });

    try
    {
      session.updatePermissions();
    }
    finally
    {
      session.options().setPermissionUpdater(originalUpdater);
    }

    assertTrue("The updater must complete a cache-only lookup", lookedUp.get());
  }

  public void testChangedKeyInvalidationDuringLoad() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();

    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<InternalCDORevision> load = null;
    Future<?> invalidation = null;

    try
    {
      CDOBranch branch = branch0;
      CDOBranchPoint head = branch.getHead();
      long commitTimeStamp = branch.getBase().getTimeStamp() + 1000;
      loader.setRevision(objectID, head, 1);
      load = executor.submit(() -> manager.getRevision(objectID, head, Request.Config.DEFAULT));
      assertTrue("The controlled loader did not prepare its result", resultPrepared.await(10, TimeUnit.SECONDS));

      CountDownLatch invalidationDone = new CountDownLatch(1);
      invalidation = executor.submit(() -> {
        try
        {
          manager.reviseVersion(objectID, branch.getVersion(1), commitTimeStamp);
        }
        finally
        {
          invalidationDone.countDown();
        }
      });
      assertTrue("Invalidation waited for the pending load", invalidationDone.await(10, TimeUnit.SECONDS));

      loader.setRevision(objectID, branch.getPoint(commitTimeStamp), 2);
      releaseLoader.countDown();
      InternalCDORevision loaded = load.get(10, TimeUnit.SECONDS);
      invalidation.get(10, TimeUnit.SECONDS);
      assertNotNull(loaded);
      assertEquals(2, loaded.getVersion());
      assertEquals(commitTimeStamp, loaded.getTimeStamp());
      assertSame(loaded, manager.getCache().getRevision(objectID, head));
      assertEquals(commitTimeStamp - 1, manager.getCache().getRevisionByVersion(objectID, branch.getVersion(1)).getRevised());
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      if (invalidation != null)
      {
        invalidation.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testDetachedHeadLoadIsResolvedAsMissing() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    CDORevisionManagerImpl manager = createControlledManager(loader);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<InternalCDORevision> load = null;
    CDOBranch branch = branch0;
    long commitTimeStamp = branch.getBase().getTimeStamp() + 1000;

    try
    {
      loader.setRevision(objectID, branch.getHead(), 1);
      load = executor.submit(() -> manager.getRevision(objectID, branch.getHead(), Request.Config.DEFAULT));
      assertTrue("The controlled loader did not prepare its result", resultPrepared.await(10, TimeUnit.SECONDS));
      manager.reviseDetached(objectID, branch.getVersion(1), commitTimeStamp);
      loader.setMissing(true);
      releaseLoader.countDown();

      assertNull("A detached object must not be returned as HEAD", load.get(10, TimeUnit.SECONDS));
      assertNull(manager.getCache().getRevision(objectID, branch.getHead()));
      assertNotNull(manager.getCache().getRevisionByVersion(objectID, branch.getVersion(1)));
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testHistoricalLoadOverlappingChangedKeyInvalidationRemainsValid() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    CDORevisionManagerImpl manager = createControlledManager(loader);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<InternalCDORevision> load = null;
    CDOBranch branch = branch0;
    long commitTimeStamp = branch.getBase().getTimeStamp() + 1000;
    CDOBranchPoint historicalPoint = branch.getPoint(commitTimeStamp - 2);

    try
    {
      loader.setRevision(objectID, branch.getPoint(commitTimeStamp - 100), 1);
      load = executor.submit(() -> manager.getRevision(objectID, historicalPoint, Request.Config.DEFAULT));
      assertTrue("The controlled loader did not prepare its result", resultPrepared.await(10, TimeUnit.SECONDS));
      manager.reviseVersion(objectID, branch.getVersion(1), commitTimeStamp);
      releaseLoader.countDown();

      InternalCDORevision historical = load.get(10, TimeUnit.SECONDS);
      assertNotNull(historical);
      assertEquals(1, historical.getVersion());
      assertTrue(historical.isValid(historicalPoint));
      assertSame(historical, manager.getCache().getRevision(objectID, historicalPoint));
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testHistoricalPrefetchKeepsRememberedAndInheritedRevisions() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    CDORevisionManagerImpl manager = createControlledManager(loader);
    CDOBranch childBranch = branch1;
    CDOBranchPoint historicalPoint = childBranch.getPoint(childBranch.getBase().getTimeStamp());
    long revised = revisions1[0].getRevised();
    CDOID rememberedID = CDOIDUtil.createUUID();
    CDOID prefetchedID = CDOIDUtil.createUUID();
    InternalCDORevision rememberedRevision = createInheritedRevision(rememberedID, revised);
    InternalCDORevision prefetchedRevision = createInheritedRevision(prefetchedID, revised);
    PointerCDORevision pointer = new PointerCDORevision(CLASS, rememberedID, childBranch, revised, rememberedRevision);
    RevisionInfo.Missing rememberedInfo = new RevisionInfo.Missing(rememberedID, null);
    rememberedInfo.setResult(rememberedRevision);
    rememberedInfo.setSynthetic(pointer);
    RevisionInfo.Missing prefetchedInfo = new RevisionInfo.Missing(prefetchedID, null);
    prefetchedInfo.setResult(prefetchedRevision);
    loader.setRevision(objectID, historicalPoint, 1);
    loader.setAdditionalInfos(java.util.Arrays.asList(rememberedInfo, prefetchedInfo));
    manager.internRevision(rememberedRevision);
    Request.Config config = new Request.Config(LookupMode.CACHE_THEN_LOADER, 1, false);
    List<CDORevision> additional = new ArrayList<>();
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<List<CDORevision>> load = null;

    try
    {
      load = executor
          .submit(() -> manager.getRevisions(Collections.singletonList(objectID), historicalPoint, config, (SyntheticCDORevision[])null, additional));
      assertTrue("The controlled historical loader did not prepare its response", resultPrepared.await(10, TimeUnit.SECONDS));
      releaseLoader.countDown();

      List<CDORevision> primary = load.get(10, TimeUnit.SECONDS);
      assertNotNull(primary.get(0));
      assertEquals(2, additional.size());
      assertSame(rememberedRevision, additional.get(0));
      assertSame(prefetchedRevision, additional.get(1));
      assertSame(pointer, rememberedInfo.getSynthetic());
      assertSame(rememberedRevision, manager.getCache().getRevision(rememberedID, historicalPoint));
      assertEquals("Historical additional revisions must not trigger spurious HEAD retries", 1, loader.getAttemptCount());
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  private InternalCDORevision createInheritedRevision(CDOID id, long revised)
  {
    InternalCDORevision revision = new CDORevisionImpl(CLASS);
    revision.setID(id);
    revision.setBranchPoint(branch0.getPoint(branch1.getBase().getTimeStamp()));
    revision.setVersion(revisions0[1].getVersion());
    revision.setRevised(revised);
    return revision;
  }

  private CDORevisionManagerImpl createControlledManager(ControlledRevisionLoader loader)
  {
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();
    return manager;
  }

  public void testPrefetchedUnknownIDInvalidationDuringLoad() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    CDOID prefetchedID = CDOIDUtil.createUUID();
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    loader.setPrefetchedID(prefetchedID);
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();

    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<List<CDORevision>> load = null;

    try
    {
      CDOBranch branch = branch0;
      CDOBranchPoint head = branch.getHead();
      long commitTimeStamp = branch.getBase().getTimeStamp() + 1000;
      loader.setRevision(objectID, head, 1);
      Request.Config config = new Request.Config(LookupMode.CACHE_THEN_LOADER, 1, false);
      load = executor.submit(() -> manager.getRevisions(java.util.Collections.singletonList(objectID), head, config));
      assertTrue("The controlled loader did not prepare its result", resultPrepared.await(10, TimeUnit.SECONDS));

      CountDownLatch invalidationDone = new CountDownLatch(1);
      executor.submit(() -> {
        try
        {
          manager.reviseVersion(prefetchedID, branch.getVersion(1), commitTimeStamp);
        }
        finally
        {
          invalidationDone.countDown();
        }
      });
      assertTrue("Invalidation waited for the pending load", invalidationDone.await(10, TimeUnit.SECONDS));

      loader.setPrefetchedID(null);
      releaseLoader.countDown();
      assertNotNull(load.get(10, TimeUnit.SECONDS));
      assertNull(manager.getCache().getRevision(prefetchedID, head));
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testCacheClearDuringLoadDoesNotRepublishResponse() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<InternalCDORevision> load = null;
    try
    {
      CDOBranchPoint head = branch0.getHead();
      loader.setRevision(objectID, head, 1);
      load = executor.submit(() -> manager.getRevision(objectID, head, Request.Config.DEFAULT));
      assertTrue("The controlled loader did not prepare its result", resultPrepared.await(10, TimeUnit.SECONDS));

      manager.clearCache();
      releaseLoader.countDown();
      assertNotNull(load.get(10, TimeUnit.SECONDS));
      assertNull(manager.getCache().getRevision(objectID, head));
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testBranchRemovalDuringLoadDoesNotRepublishResponse() throws Exception
  {
    CountDownLatch resultPrepared = new CountDownLatch(1);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultPrepared, releaseLoader);
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<InternalCDORevision> load = null;
    try
    {
      CDOBranchPoint head = branch0.getHead();
      loader.setRevision(objectID, head, 1);
      load = executor.submit(() -> manager.getRevision(objectID, head, Request.Config.DEFAULT));
      assertTrue("The controlled loader did not prepare its result", resultPrepared.await(10, TimeUnit.SECONDS));

      manager.removeRevisions(branch0);
      releaseLoader.countDown();
      assertNotNull(load.get(10, TimeUnit.SECONDS));
      assertNull(manager.getCache().getRevision(objectID, head));
    }
    finally
    {
      releaseLoader.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testConcurrentLoadsShareCanonicalRevision() throws Exception
  {
    CountDownLatch resultsPrepared = new CountDownLatch(2);
    CountDownLatch releaseLoader = new CountDownLatch(1);
    ControlledRevisionLoader loader = new ControlledRevisionLoader(resultsPrepared, releaseLoader);
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();

    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<InternalCDORevision> firstLoad = null;
    Future<InternalCDORevision> secondLoad = null;

    try
    {
      CDOBranchPoint head = branch0.getHead();
      loader.setRevision(objectID, head, 1);
      firstLoad = executor.submit(() -> manager.getRevision(objectID, head, Request.Config.DEFAULT));
      secondLoad = executor.submit(() -> manager.getRevision(objectID, head, Request.Config.DEFAULT));
      assertTrue("Both controlled loads did not reach the response barrier", resultsPrepared.await(10, TimeUnit.SECONDS));

      releaseLoader.countDown();
      InternalCDORevision first = firstLoad.get(10, TimeUnit.SECONDS);
      InternalCDORevision second = secondLoad.get(10, TimeUnit.SECONDS);
      assertSame(first, manager.getCache().getRevision(objectID, head));
      assertSame(second, manager.getCache().getRevision(objectID, head));
      assertSame(first, second);
    }
    finally
    {
      releaseLoader.countDown();
      if (firstLoad != null)
      {
        firstLoad.cancel(true);
      }

      if (secondLoad != null)
      {
        secondLoad.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testSuccessorPublicationReconcilesPendingPredecessorLoad() throws Exception
  {
    CountDownLatch predecessorPrepared = new CountDownLatch(1);
    CountDownLatch releasePredecessor = new CountDownLatch(1);
    long predecessorTimeStamp = branch0.getBase().getTimeStamp() + 1000;
    long successorTimeStamp = predecessorTimeStamp + 1000;
    CDOBranchPoint predecessorPoint = branch0.getPoint(predecessorTimeStamp);
    CDOBranchPoint successorPoint = branch0.getPoint(successorTimeStamp);
    SuccessorRevisionLoader loader = new SuccessorRevisionLoader(objectID, predecessorPoint, successorPoint, predecessorPrepared, releasePredecessor);
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(new CDORevisionCacheAuditing());
    manager.setRevisionLoader(loader);
    manager.activate();

    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<InternalCDORevision> predecessorLoad = null;
    Future<InternalCDORevision> successorLoad = null;
    try
    {
      predecessorLoad = executor.submit(() -> manager.getRevision(objectID, predecessorPoint, Request.Config.DEFAULT));
      assertTrue("The predecessor response did not reach its barrier", predecessorPrepared.await(10, TimeUnit.SECONDS));

      successorLoad = executor.submit(() -> manager.getRevision(objectID, successorPoint, Request.Config.DEFAULT));
      InternalCDORevision successor = successorLoad.get(10, TimeUnit.SECONDS);
      assertNotNull(successor);
      assertEquals(2, successor.getVersion());

      releasePredecessor.countDown();
      InternalCDORevision predecessor = predecessorLoad.get(10, TimeUnit.SECONDS);
      assertNotNull(predecessor);
      assertEquals(successorTimeStamp - 1, predecessor.getRevised());
      assertSame(successor, manager.getCache().getRevision(objectID, successorPoint));
      assertSame(successor, manager.getCache().getRevision(objectID, branch0.getHead()));
    }
    finally
    {
      releasePredecessor.countDown();
      if (predecessorLoad != null)
      {
        predecessorLoad.cancel(true);
      }

      if (successorLoad != null)
      {
        successorLoad.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testSecurityUpdateDuringLoadDiscardsOldPermissionResponse() throws Exception
  {
    CountDownLatch responsePrepared = new CountDownLatch(1);
    CountDownLatch releaseResponse = new CountDownLatch(1);
    SecurityRevisionLoader loader = new SecurityRevisionLoader(objectID, branch0.getHead(), CDOPermission.WRITE, new CountDownLatch[] { responsePrepared },
        new CountDownLatch[] { releaseResponse });
    CDORevisionManagerImpl manager = createSecurityTestManager(loader);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<InternalCDORevision> load = null;

    try
    {
      load = executor.submit(() -> manager.getRevision(objectID, branch0.getHead(), Request.Config.DEFAULT));
      assertTrue("The old-permission response was not prepared", responsePrepared.await(10, TimeUnit.SECONDS));
      assertNull(manager.getCache().getRevision(objectID, branch0.getHead()));

      manager.beginPermissionUpdate();
      try
      {
        loader.setPermission(CDOPermission.NONE);
      }
      finally
      {
        manager.endPermissionUpdate();
      }

      releaseResponse.countDown();
      InternalCDORevision revision = load.get(10, TimeUnit.SECONDS);
      assertEquals(CDOPermission.NONE, revision.getPermission());
      assertEquals(CDOPermission.WRITE, loader.getResponse(0).getPermission());
      assertNotSame(loader.getResponse(0), revision);
      assertSame(revision, manager.getCache().getRevision(objectID, branch0.getHead()));
      assertEquals(2, loader.getAttemptCount());
    }
    finally
    {
      releaseResponse.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Security-load worker did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testSecurityUpdateDuringReplacementLoadRetriesAgain() throws Exception
  {
    CountDownLatch firstResponsePrepared = new CountDownLatch(1);
    CountDownLatch releaseFirstResponse = new CountDownLatch(1);
    CountDownLatch replacementPrepared = new CountDownLatch(1);
    CountDownLatch releaseReplacement = new CountDownLatch(1);
    SecurityRevisionLoader loader = new SecurityRevisionLoader(objectID, branch0.getHead(), CDOPermission.WRITE,
        new CountDownLatch[] { firstResponsePrepared, replacementPrepared }, new CountDownLatch[] { releaseFirstResponse, releaseReplacement });
    CDORevisionManagerImpl manager = createSecurityTestManager(loader);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<InternalCDORevision> load = null;

    try
    {
      load = executor.submit(() -> manager.getRevision(objectID, branch0.getHead(), Request.Config.DEFAULT));
      assertTrue("The first response was not prepared", firstResponsePrepared.await(10, TimeUnit.SECONDS));

      manager.beginPermissionUpdate();
      try
      {
        loader.setPermission(CDOPermission.NONE);
      }
      finally
      {
        manager.endPermissionUpdate();
      }

      releaseFirstResponse.countDown();
      assertTrue("The replacement response was not prepared", replacementPrepared.await(10, TimeUnit.SECONDS));
      assertEquals(CDOPermission.NONE, loader.getResponse(1).getPermission());

      manager.beginPermissionUpdate();
      try
      {
        loader.setPermission(CDOPermission.READ);
      }
      finally
      {
        manager.endPermissionUpdate();
      }

      releaseReplacement.countDown();
      InternalCDORevision revision = load.get(10, TimeUnit.SECONDS);
      assertEquals(CDOPermission.READ, revision.getPermission());
      assertNotSame(loader.getResponse(0), revision);
      assertNotSame(loader.getResponse(1), revision);
      assertSame(revision, manager.getCache().getRevision(objectID, branch0.getHead()));
      assertEquals(3, loader.getAttemptCount());
    }
    finally
    {
      releaseFirstResponse.countDown();
      releaseReplacement.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      executor.shutdownNow();
      assertTrue("Security-load worker did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  private CDORevisionManagerImpl createSecurityTestManager(SecurityRevisionLoader loader)
  {
    return createSecurityTestManager(loader, new CDORevisionCacheAuditing());
  }

  private CDORevisionManagerImpl createSecurityTestManager(SecurityRevisionLoader loader, CDORevisionCacheAuditing cache)
  {
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setSupportingBranches(true);
    manager.setCache(cache);
    manager.setRevisionLoader(loader);
    manager.activate();
    return manager;
  }

  public void testCacheEvictionDoesNotReviseHistory()
  {
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setCache(cache);

    CDOBranchPoint head = branch0.getHead();
    InternalCDORevision revision = new CDORevisionImpl(CLASS);
    revision.setID(objectID);
    revision.setBranchPoint(head);
    revision.setVersion(100);
    manager.internRevision(revision);

    long revised = revision.getRevised();
    manager.evictRevision(revision);

    assertEquals(revised, revision.getRevised());
    assertNull(cache.getRevision(objectID, head));
  }

  public void testAuditedDetachRetainsExactHistoricalBoundary()
  {
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setCache(cache);

    CDOBranch branch = branch0;
    long commitTimeStamp = branch.getBase().getTimeStamp() + 1000;
    InternalCDORevision revision = new CDORevisionImpl(CLASS);
    revision.setID(objectID);
    revision.setBranchPoint(branch.getPoint(commitTimeStamp - 100));
    revision.setVersion(100);
    manager.internRevision(revision);

    manager.reviseDetached(objectID, branch.getVersion(100), commitTimeStamp);

    assertEquals(commitTimeStamp - 1, revision.getRevised());
    assertSame(revision, cache.getRevision(objectID, branch.getPoint(commitTimeStamp - 2)));
    assertNull(cache.getRevision(objectID, branch.getHead()));
  }

  public void testConsumersRetainCanonicalHistoricalRevisionAfterDetach()
  {
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setSupportingAudits(true);
    manager.setCache(cache);

    CDOBranch branch = branch0;
    long commitTimeStamp = branch.getBase().getTimeStamp() + 1000;
    CDOBranchPoint historicalPoint = branch.getPoint(commitTimeStamp - 2);
    InternalCDORevision revision = new CDORevisionImpl(CLASS);
    revision.setID(objectID);
    revision.setBranchPoint(branch.getPoint(commitTimeStamp - 100));
    revision.setVersion(100);
    manager.internRevision(revision);

    Request.Config cacheOnly = new Request.Config(LookupMode.CACHE_ONLY, CDORevision.DEPTH_NONE, false);
    InternalCDORevision firstConsumer = manager.getRevision(objectID, historicalPoint, cacheOnly);
    InternalCDORevision secondConsumer = manager.getRevision(objectID, historicalPoint, cacheOnly);
    manager.reviseDetached(objectID, branch.getVersion(100), commitTimeStamp);
    InternalCDORevision retainedHistorical = manager.getRevision(objectID, historicalPoint, cacheOnly);

    assertSame(firstConsumer, secondConsumer);
    assertSame(firstConsumer, retainedHistorical);
    assertNull(manager.getRevision(objectID, branch.getHead(), cacheOnly));
  }

  public void testCacheAdditionListenersRunOutsidePublicationLock() throws Exception
  {
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setCache(cache);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    AtomicBoolean submittedReentrantPublication = new AtomicBoolean();
    AtomicInteger eventCount = new AtomicInteger();
    List<CDORevision> eventRevisions = new CopyOnWriteArrayList<>();

    InternalCDORevision secondRevision = new CDORevisionImpl(CLASS);
    secondRevision.setID(CDOIDUtil.createUUID());
    secondRevision.setBranchPoint(branch0.getHead());
    secondRevision.setVersion(1);

    IListener listener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        eventCount.incrementAndGet();
        eventRevisions.add(((AdditionEvent)event).getRevision());

        if (submittedReentrantPublication.compareAndSet(false, true))
        {
          try
          {
            executor.submit(() -> manager.internRevision(secondRevision)).get(5, TimeUnit.SECONDS);
          }
          catch (Exception ex)
          {
            throw new IllegalStateException("A cache listener could not re-enter revision publication", ex);
          }
        }
      }
    };

    cache.addListener(listener);

    try
    {
      InternalCDORevision firstRevision = new CDORevisionImpl(CLASS);
      firstRevision.setID(objectID);
      firstRevision.setBranchPoint(branch0.getHead());
      firstRevision.setVersion(1);
      manager.internRevision(firstRevision);
      assertEquals("Unexpected addition events: " + eventRevisions, 2, eventCount.get());
      manager.internRevision(firstRevision);

      assertEquals(2, eventCount.get());
      assertSame(firstRevision, eventRevisions.get(0));
      assertSame(secondRevision, eventRevisions.get(1));
    }
    finally
    {
      cache.removeListener(listener);
      executor.shutdownNow();
      assertTrue("Listener worker did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
    }
  }

  public void testConcurrentCacheAdditionsRemainOrderedWhileListenerIsRunning() throws Exception
  {
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setCache(cache);
    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch firstListenerEntered = new CountDownLatch(1);
    CountDownLatch releaseFirstListener = new CountDownLatch(1);
    AtomicBoolean firstEvent = new AtomicBoolean(true);
    List<CDORevision> eventRevisions = new CopyOnWriteArrayList<>();

    InternalCDORevision firstRevision = new CDORevisionImpl(CLASS);
    firstRevision.setID(CDOIDUtil.createUUID());
    firstRevision.setBranchPoint(branch0.getHead());
    firstRevision.setVersion(1);

    InternalCDORevision secondRevision = new CDORevisionImpl(CLASS);
    secondRevision.setID(CDOIDUtil.createUUID());
    secondRevision.setBranchPoint(branch0.getHead());
    secondRevision.setVersion(1);

    IListener listener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        eventRevisions.add(((AdditionEvent)event).getRevision());
        if (firstEvent.compareAndSet(true, false))
        {
          firstListenerEntered.countDown();
          try
          {
            if (!releaseFirstListener.await(10, TimeUnit.SECONDS))
            {
              throw new IllegalStateException("The first cache listener was not released");
            }
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
          }
        }
      }
    };

    cache.addListener(listener);
    Future<CDORevision> firstPublication = null;
    Future<CDORevision> secondPublication = null;
    try
    {
      firstPublication = executor.submit(() -> manager.internRevision(firstRevision));
      assertTrue("The first addition listener did not start", firstListenerEntered.await(10, TimeUnit.SECONDS));

      secondPublication = executor.submit(() -> manager.internRevision(secondRevision));
      assertSame(secondRevision, secondPublication.get(5, TimeUnit.SECONDS));
      assertSame(secondRevision, cache.getRevision(secondRevision.getID(), branch0.getHead()));

      releaseFirstListener.countDown();
      assertSame(firstRevision, firstPublication.get(10, TimeUnit.SECONDS));
      assertEquals(2, eventRevisions.size());
      assertSame(firstRevision, eventRevisions.get(0));
      assertSame(secondRevision, eventRevisions.get(1));
    }
    finally
    {
      releaseFirstListener.countDown();
      if (firstPublication != null)
      {
        firstPublication.cancel(true);
      }

      if (secondPublication != null)
      {
        secondPublication.cancel(true);
      }

      cache.removeListener(listener);
      executor.shutdownNow();
      assertTrue("Listener workers did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
    }
  }

  public void testCacheListenerFailureDoesNotCorruptPublication()
  {
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = new CDORevisionManagerImpl();
    manager.setCache(cache);
    InternalCDORevision revision = new CDORevisionImpl(CLASS);
    revision.setID(objectID);
    revision.setBranchPoint(branch0.getHead());
    revision.setVersion(1);

    IListener listener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        throw new IllegalStateException("Expected listener failure");
      }
    };

    cache.addListener(listener);
    try
    {
      manager.internRevision(revision);
    }
    finally
    {
      cache.removeListener(listener);
    }

    assertSame(revision, cache.getRevision(objectID, branch0.getHead()));
    assertSame(revision, manager.internRevision(revision));
  }

  public void testPermissionGenerationRetryPreservesPublishedCanonicalRevision() throws Exception
  {
    CountDownLatch listenerEntered = new CountDownLatch(1);
    CountDownLatch releaseListener = new CountDownLatch(1);
    CDOBranchPoint head = branch0.getHead();
    SecurityRevisionLoader loader = new SecurityRevisionLoader(objectID, head, CDOPermission.READ, new CountDownLatch[0], new CountDownLatch[0]);
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = createSecurityTestManager(loader, cache);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    AtomicBoolean firstEvent = new AtomicBoolean(true);
    IListener listener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (firstEvent.compareAndSet(true, false))
        {
          listenerEntered.countDown();
          try
          {
            if (!releaseListener.await(10, TimeUnit.SECONDS))
            {
              throw new IllegalStateException("The cache listener was not released");
            }
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
          }
        }
      }
    };

    cache.addListener(listener);
    Future<InternalCDORevision> load = null;
    try
    {
      load = executor.submit(() -> manager.getRevision(objectID, head, Request.Config.DEFAULT));
      assertTrue("The first cache addition did not enter its listener", listenerEntered.await(10, TimeUnit.SECONDS));

      InternalCDORevision retainedByConcurrentCaller = manager.getRevision(objectID, head,
          new Request.Config(LookupMode.CACHE_ONLY, CDORevision.DEPTH_NONE, false));
      assertSame(loader.getResponse(0), retainedByConcurrentCaller);
      assertEquals(CDOPermission.READ, retainedByConcurrentCaller.getPermission());

      manager.beginPermissionUpdate();
      try
      {
        retainedByConcurrentCaller.setPermission(CDOPermission.WRITE);
      }
      finally
      {
        manager.endPermissionUpdate();
      }

      releaseListener.countDown();
      InternalCDORevision loaded = load.get(10, TimeUnit.SECONDS);
      assertSame("A generation retry must preserve the already adopted canonical instance", retainedByConcurrentCaller, loaded);
      assertEquals("The new generation's permission must remain visible", CDOPermission.WRITE, loaded.getPermission());
      assertSame(loaded, cache.getRevision(objectID, head));
      assertEquals("A valid cached canonical revision must not be loaded again", 1, loader.getAttemptCount());
    }
    finally
    {
      releaseListener.countDown();
      if (load != null)
      {
        load.cancel(true);
      }

      cache.removeListener(listener);
      executor.shutdownNow();
      assertTrue("Worker threads did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testCacheAdditionListenerCanLoadDuringPermissionUpdate() throws Exception
  {
    CDOID addedID = CDOIDUtil.createUUID();
    CDOID loadedID = CDOIDUtil.createUUID();
    CDOBranchPoint head = branch0.getHead();
    SecurityRevisionLoader loader = new SecurityRevisionLoader(loadedID, head, CDOPermission.READ, new CountDownLatch[0], new CountDownLatch[0]);
    CDORevisionCacheAuditing cache = new CDORevisionCacheAuditing();
    CDORevisionManagerImpl manager = createSecurityTestManager(loader, cache);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    AtomicBoolean loadFromListener = new AtomicBoolean(true);
    CountDownLatch listenerLoadCompleted = new CountDownLatch(1);
    IListener listener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (loadFromListener.compareAndSet(true, false))
        {
          Future<InternalCDORevision> load = executor.submit(() -> manager.getRevision(loadedID, head, Request.Config.DEFAULT));
          try
          {
            assertNotNull("The listener's uncached revision load must complete", load.get(5, TimeUnit.SECONDS));
            listenerLoadCompleted.countDown();
          }
          catch (Exception ex)
          {
            throw new IllegalStateException("The cache listener could not load an uncached revision", ex);
          }
        }
      }
    };

    cache.addListener(listener);
    try
    {
      manager.beginPermissionUpdate();
      InternalCDORevision addedRevision = new CDORevisionImpl(CLASS);
      addedRevision.setID(addedID);
      addedRevision.setBranchPoint(head);
      addedRevision.setVersion(1);
      manager.internRevision(addedRevision);

      assertEquals("Cache events must remain deferred until the permission generation is stable", 1L, listenerLoadCompleted.getCount());
      manager.endPermissionUpdate();
      assertTrue("The listener's revision load did not complete after permission updating", listenerLoadCompleted.await(10, TimeUnit.SECONDS));
      assertEquals(1, loader.getAttemptCount());
    }
    finally
    {
      cache.removeListener(listener);
      executor.shutdownNow();
      assertTrue("Listener worker did not terminate", executor.awaitTermination(10, TimeUnit.SECONDS));
      manager.deactivate();
    }
  }

  public void testBranch0_Head() throws Exception
  {
    CDOBranch branch = branch0;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch1_Initial() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions0[1];

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch1_Normal() throws Exception
  {
    CDOBranch branch = branch1;
    for (int i = 0; i < revisions1.length - 1; i++)
    {
      InternalCDORevision expected = revisions1[i];
      long timeStamp = getMiddleOfValidity(expected);

      InternalCDORevision revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(1);

      revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(0);
    }
  }

  public void testBranch1_Detached() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[3].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch1_Head() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch2_Initial() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = branch2.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch2_Head() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision1 = getRevision(branch, timeStamp);
    assertRevision(expected, revision1);
    assertLoads(1);

    InternalCDORevision revision2 = getRevision(branch, timeStamp);
    assertRevision(expected, revision2);
    assertLoads(0);
  }

  public void testBranch3_Initial() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = branch3.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = revisions1[1];

    InternalCDORevision revision1 = getRevision(branch, timeStamp);
    assertRevision(expected, revision1);
    assertLoads(1);

    InternalCDORevision revision2 = getRevision(branch, timeStamp);
    assertRevision(expected, revision2);
    assertLoads(0);
  }

  public void testBranch3_Head() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = revisions1[1];

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testBranch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch1_Initial() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions0[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch1_Normal() throws Exception
  {
    CDOBranch branch = branch1;

    prefetchBaseline(branch, 1);

    for (int i = 0; i < revisions1.length - 1; i++)
    {
      InternalCDORevision expected = revisions1[i];
      long timeStamp = getMiddleOfValidity(expected);

      InternalCDORevision revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(1);

      revision = getRevision(branch, timeStamp);
      assertRevision(expected, revision);
      assertLoads(0);
    }
  }

  public void testAvailableUp1_Branch1_Detached() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = revisions1[3].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch1_Head() throws Exception
  {
    CDOBranch branch = branch1;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch2_Initial() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = branch2.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch2_Head() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch3_Initial() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = branch3.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch3_Head() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp1_Branch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 1);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch2_Initial() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = branch2.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch2_Head() throws Exception
  {
    CDOBranch branch = branch2;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch3_Initial() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = branch3.getBase().getTimeStamp() + 2;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch3_Head() throws Exception
  {
    CDOBranch branch = branch3;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp2_Branch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 2);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp3_Branch4_Initial() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() - 1;
    InternalCDORevision expected = revisions1[1];

    prefetchBaseline(branch, 3);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp3_Branch4_Detached() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = revisions4[0].getTimeStamp() + 1;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 3);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  public void testAvailableUp3_Branch4_Head() throws Exception
  {
    CDOBranch branch = branch4;
    long timeStamp = CDOBranchPoint.UNSPECIFIED_DATE;
    InternalCDORevision expected = null;

    prefetchBaseline(branch, 3);

    InternalCDORevision revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(1);

    revision = getRevision(branch, timeStamp);
    assertRevision(expected, revision);
    assertLoads(0);
  }

  /**
   * Blocks one batch response after its revision has been prepared.
   *
   * @author Eike Stepper
   */
  private static final class ControlledRevisionLoader implements RevisionLoader4
  {
    private final CountDownLatch resultPrepared;

    private final CountDownLatch releaseLoader;

    private volatile CDOID id;

    private volatile CDOBranchPoint branchPoint;

    private volatile int version;

    private volatile boolean missing;

    private volatile CDOID prefetchedID;

    private volatile List<RevisionInfo> additionalInfos;

    private final AtomicInteger attemptCount = new AtomicInteger();

    private ControlledRevisionLoader(CountDownLatch resultPrepared, CountDownLatch releaseLoader)
    {
      this.resultPrepared = resultPrepared;
      this.releaseLoader = releaseLoader;
    }

    private void setRevision(CDOID id, CDOBranchPoint branchPoint, int version)
    {
      this.id = id;
      this.branchPoint = branchPoint;
      this.version = version;
    }

    private void setPrefetchedID(CDOID prefetchedID)
    {
      this.prefetchedID = prefetchedID;
    }

    private void setAdditionalInfos(List<RevisionInfo> additionalInfos)
    {
      this.additionalInfos = additionalInfos;
    }

    private int getAttemptCount()
    {
      return attemptCount.get();
    }

    private void setMissing(boolean missing)
    {
      this.missing = missing;
    }

    @Override
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config)
    {
      return null;
    }

    @Override
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, Request.Config config)
    {
      attemptCount.incrementAndGet();
      InternalCDORevision revision = missing ? null : createRevision(id);
      infos.get(0).setResult(revision);
      resultPrepared.countDown();

      try
      {
        if (!releaseLoader.await(10, TimeUnit.SECONDS))
        {
          throw new IllegalStateException("Timed out waiting for the test to release the loader");
        }
      }
      catch (InterruptedException ex)
      {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(ex);
      }

      if (additionalInfos != null)
      {
        return additionalInfos;
      }

      if (prefetchedID != null)
      {
        RevisionInfo.Missing prefetchedInfo = new RevisionInfo.Missing(prefetchedID, branchPoint);
        prefetchedInfo.setResult(missing ? null : createRevision(prefetchedID));
        return java.util.Collections.singletonList(prefetchedInfo);
      }

      return null;
    }

    private InternalCDORevision createRevision(CDOID id)
    {
      InternalCDORevision revision = new CDORevisionImpl(CLASS);
      revision.setID(id);
      revision.setBranchPoint(branchPoint);
      revision.setVersion(version);
      return revision;
    }

    @Override
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
    {
      return null;
    }

    @Override
    public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
    {
    }

    @Deprecated
    @Override
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
    {
      return infos;
    }

    @Override
    public String toString()
    {
      return "ControlledRevisionLoader";
    }
  }

  /**
   * Holds a predecessor response until its successor has been published.
   *
   * @author Eike Stepper
   */
  private static final class SuccessorRevisionLoader implements RevisionLoader4
  {
    private final CDOID id;

    private final CDOBranchPoint predecessorPoint;

    private final CDOBranchPoint successorPoint;

    private final CountDownLatch predecessorPrepared;

    private final CountDownLatch releasePredecessor;

    private SuccessorRevisionLoader(CDOID id, CDOBranchPoint predecessorPoint, CDOBranchPoint successorPoint, CountDownLatch predecessorPrepared,
        CountDownLatch releasePredecessor)
    {
      this.id = id;
      this.predecessorPoint = predecessorPoint;
      this.successorPoint = successorPoint;
      this.predecessorPrepared = predecessorPrepared;
      this.releasePredecessor = releasePredecessor;
    }

    @Override
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, Request.Config config)
    {
      int version = branchPoint.getTimeStamp() == predecessorPoint.getTimeStamp() ? 1 : 2;
      CDOBranchPoint revisionPoint = version == 1 ? predecessorPoint : successorPoint;
      InternalCDORevision revision = new CDORevisionImpl(CLASS);
      revision.setID(id);
      revision.setBranchPoint(revisionPoint);
      revision.setVersion(version);
      infos.get(0).setResult(revision);

      if (version == 1)
      {
        predecessorPrepared.countDown();
        try
        {
          if (!releasePredecessor.await(10, TimeUnit.SECONDS))
          {
            throw new IllegalStateException("Timed out waiting for the test to release the predecessor");
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      return null;
    }

    @Override
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config)
    {
      return null;
    }

    @Override
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
    {
      return null;
    }

    @Override
    public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
    {
    }

    @Deprecated
    @Override
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
    {
      return infos;
    }
  }

  /**
   * Produces revision responses with controlled permissions and optional delivery barriers.
   *
   * @author Eike Stepper
   */
  private static final class SecurityRevisionLoader implements RevisionLoader4
  {
    private final CDOID id;

    private final CDOBranchPoint branchPoint;

    private final CountDownLatch[] responsePrepared;

    private final CountDownLatch[] releaseResponse;

    private final AtomicInteger attemptCount = new AtomicInteger();

    private final List<InternalCDORevision> responses = new CopyOnWriteArrayList<>();

    private volatile CDOPermission permission;

    private SecurityRevisionLoader(CDOID id, CDOBranchPoint branchPoint, CDOPermission permission, CountDownLatch[] responsePrepared,
        CountDownLatch[] releaseResponse)
    {
      this.id = id;
      this.branchPoint = branchPoint;
      this.permission = permission;
      this.responsePrepared = responsePrepared;
      this.releaseResponse = releaseResponse;
    }

    private void setPermission(CDOPermission permission)
    {
      this.permission = permission;
    }

    private int getAttemptCount()
    {
      return attemptCount.get();
    }

    private InternalCDORevision getResponse(int index)
    {
      return responses.get(index);
    }

    @Override
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, Request.Config config)
    {
      int attempt = attemptCount.getAndIncrement();
      InternalCDORevision revision = new CDORevisionImpl(CLASS);
      revision.setID(id);
      revision.setBranchPoint(this.branchPoint);
      revision.setVersion(1);
      revision.setPermission(permission);
      responses.add(revision);
      infos.get(0).setResult(revision);

      if (attempt < responsePrepared.length)
      {
        responsePrepared[attempt].countDown();
        try
        {
          if (!releaseResponse[attempt].await(10, TimeUnit.SECONDS))
          {
            throw new IllegalStateException("Timed out waiting for the test to release a security response");
          }
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(ex);
        }
      }

      return null;
    }

    @Override
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, Request.Config config)
    {
      return null;
    }

    @Override
    public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
    {
      return null;
    }

    @Override
    public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
    {
    }

    @Deprecated
    @Override
    public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
    {
      return infos;
    }
  }
}
