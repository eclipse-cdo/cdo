/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.DefaultRepositoryProtector;
import org.eclipse.emf.cdo.internal.server.ServerOperationContext;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.server.internal.embedded.EmbeddedSession;
import org.eclipse.emf.cdo.server.internal.embedded.EmbeddedStoreContext;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMConfig.MEMStore_UT;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.operations.OperationAuthorizer;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests embedded repository sessions, revisions, and security in Plain Java configurations.
 *
 * @author Eike Stepper
 */
public class EmbeddedRepositoryContainerTest extends PlainTest
{
  public void testSharedRevisionPermissionRevocationClearsCanonicalData() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-shared-revision-permission", container, false, false, ModelConfig.Native.INSTANCE); //$NON-NLS-1$

    try
    {
      LifecycleUtil.activate(config);

      CDONet4jSession sessionA = config.openClientSession();
      CDONet4jSession sessionB = config.openClientSession();

      try
      {
        CDOTransaction transaction = sessionA.openTransaction();
        CDOResource resource = transaction.createResource(getResourcePath("/permission-revision")); //$NON-NLS-1$
        Category category = ModelConfig.Native.INSTANCE.getModel1Factory().createCategory();
        category.setName("private value"); //$NON-NLS-1$
        resource.getContents().add(category);
        transaction.commit();

        CDOID id = CDOUtil.getCDOObject(category).cdoID();
        CDOBranchPoint branchPoint = sessionA.getBranchManager().getMainBranch().getHead();
        InternalCDORevisionCache cache = ((InternalCDORevisionManager)sessionA.getRevisionManager()).getCache();
        InternalCDORevision revisionA = (InternalCDORevision)cache.getRevision(id, branchPoint);
        InternalCDORevision revisionB = (InternalCDORevision)((InternalCDORevisionManager)sessionB.getRevisionManager()).getCache().getRevision(id,
            branchPoint);

        assertNotNull(revisionA);
        assertSame("Both embedded sessions resolve the canonical server revision", revisionA, revisionB); //$NON-NLS-1$
        assertEquals("private value", revisionA.getValue(category.eClass().getEStructuralFeature("name"))); //$NON-NLS-1$ //$NON-NLS-2$

        // This is the mutation performed by LoadPermissionsRequest.confirming() after loading a permission.
        revisionB.setPermission(CDOPermission.NONE);
        assertEquals(CDOPermission.NONE, revisionA.getPermission());

        revisionB.setPermission(CDOPermission.READ);
        assertNull("Revoking one shared revision has already cleared canonical values", //$NON-NLS-1$
            revisionA.getValue(category.eClass().getEStructuralFeature("name"))); //$NON-NLS-1$
      }
      finally
      {
        LifecycleUtil.deactivate(sessionA);
        LifecycleUtil.deactivate(sessionB);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testRevisionsAcrossEmbeddedSessions_NativeNonAuditing() throws Exception
  {
    assertRevisionVisibility(ModelConfig.Native.INSTANCE, false, false, false);

  }

  public void testRevisionsAcrossEmbeddedSessions_LegacyNonAuditing() throws Exception
  {
    assertRevisionVisibility(ModelConfig.Legacy.INSTANCE, false, false, false);

  }

  public void testHistoricalRevisionAcrossEmbeddedSessions() throws Exception
  {
    assertRevisionVisibility(ModelConfig.Native.INSTANCE, true, false, true);

  }

  public void testRevisionsAcrossEmbeddedSessions_Branching() throws Exception
  {
    assertRevisionVisibility(ModelConfig.Legacy.INSTANCE, false, true, false);

  }

  private void assertRevisionVisibility(IModelConfig modelConfig, boolean auditing, boolean branching, boolean checkHistory) throws Exception
  {
    String repositoryName = "embedded-revisions-" + modelConfig.getName() + "-" + auditing + "-" + branching; //$NON-NLS-1$
    // $NON-NLS-2$
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig(repositoryName, container, branching, auditing, modelConfig);
    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession sessionA = config.openClientSession();
      CDONet4jSession sessionB = config.openClientSession();
      CDOTransaction transactionA = sessionA.openTransaction();
      CDOTransaction transactionB = sessionB.openTransaction();
      String resourcePath = getResourcePath("/revision-check"); //$NON-NLS-1$
      try
      {
        CDOResource resourceA = transactionA.createResource(resourcePath);
        Category categoryA = modelConfig.getModel1Factory().createCategory();
        categoryA.setName("first version"); //$NON-NLS-1$
        resourceA.getContents().add(categoryA);
        CDOCommitInfo firstCommit = transactionA.commit();
        CDOID categoryID = CDOUtil.getCDOObject(categoryA).cdoID();
        int firstVersion = CDOUtil.getCDOObject(categoryA).cdoRevision().getVersion();
        if (auditing)
        {
          assertEquals(0, sessionA.getBranchManager().getTagList().getTags().length);
          assertEquals(0, sessionB.getBranchManager().getTagList().getTags().length);

        }
        CDOResource resourceB = transactionB.getResource(resourcePath);
        Category categoryB = (Category)resourceB.getContents().get(0);
        assertEquals(categoryID, CDOUtil.getCDOObject(categoryB).cdoID());
        assertEquals(firstVersion, CDOUtil.getCDOObject(categoryB).cdoRevision().getVersion());
        assertEquals("first version", categoryB.getName()); //$NON-NLS-1$
        assertEquals("first version", ((Category)resourceB.getContents().get(0)).getName()); //$NON-NLS-1$
        categoryA.setName("second version"); //$NON-NLS-1$
        CDOCommitInfo secondCommit = transactionA.commit();
        int secondVersionA = CDOUtil.getCDOObject(categoryA).cdoRevision().getVersion();
        assertTrue(secondVersionA > firstVersion);
        assertTrue("Session B did not process the committed update", sessionB.waitForUpdate(secondCommit.getTimeStamp(), 10000)); //$NON-NLS-1$
        transactionB.close();
        transactionB = sessionB.openTransaction();
        resourceB = transactionB.getResource(resourcePath);
        categoryB = (Category)resourceB.getContents().get(0);
        int secondVersion = CDOUtil.getCDOObject(categoryB).cdoRevision().getVersion();
        assertTrue(secondVersion > firstVersion);
        assertEquals(categoryID, CDOUtil.getCDOObject(categoryB).cdoID());
        assertEquals("second version", categoryB.getName()); //$NON-NLS-1$
        LifecycleUtil.deactivate(sessionA);
        categoryB.setName("third version"); //$NON-NLS-1$
        CDOCommitInfo thirdCommit = transactionB.commit();
        LifecycleUtil.deactivate(sessionB);
        CDONet4jSession sessionC = config.openClientSession();
        CDOTransaction transactionC = sessionC.openTransaction();
        try
        {
          assertTrue("Session C did not process the committed update", sessionC.waitForUpdate(thirdCommit.getTimeStamp(), 10000)); //$NON-NLS-1$
          CDOResource resourceC = transactionC.getResource(resourcePath);
          Category categoryC = (Category)resourceC.getContents().get(0);
          assertEquals(categoryID, CDOUtil.getCDOObject(categoryC).cdoID());
          assertTrue(CDOUtil.getCDOObject(categoryC).cdoRevision().getVersion() > secondVersionA);
          assertEquals("third version", categoryC.getName()); //$NON-NLS-1$
          assertSame(((InternalCDORevisionManager)config.getRepository().getRevisionManager()).getCache(),
              ((InternalCDORevisionManager)sessionC.getRevisionManager()).getCache());
          if (checkHistory)
          {
            CDOView historicalView = sessionC.openView(firstCommit.getTimeStamp());
            try
            {
              CDOResource historicalResource = historicalView.getResource(resourcePath);
              Category historicalCategory = (Category)historicalResource.getContents().get(0);
              assertEquals("first version", historicalCategory.getName()); //$NON-NLS-1$
              assertEquals(firstVersion, CDOUtil.getCDOObject(historicalCategory).cdoRevision().getVersion());

            }
            finally
            {
              LifecycleUtil.deactivate(historicalView);

            }

          }

        }
        finally
        {
          LifecycleUtil.deactivate(transactionC);
          LifecycleUtil.deactivate(sessionC);

        }

      }
      finally
      {
        LifecycleUtil.deactivate(transactionA);
        LifecycleUtil.deactivate(transactionB);
        LifecycleUtil.deactivate(sessionA);
        LifecycleUtil.deactivate(sessionB);

      }

    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);

    }

  }

  public void testEmbeddedSessionsShareManagersAndRemainIsolated()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-shared-managers", container, true); //$NON-NLS-1$
    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession firstSession = config.openClientSession();
      CDONet4jSession secondSession = config.openClientSession();
      InternalCDORevisionManager repositoryRevisionManager = (InternalCDORevisionManager)config.getRepository().getRevisionManager();
      CDORevisionCache repositoryCache = repositoryRevisionManager.getCache();
      try
      {
        InternalCDORevisionManager firstRevisionManager = (InternalCDORevisionManager)firstSession.getRevisionManager();
        InternalCDORevisionManager secondRevisionManager = (InternalCDORevisionManager)secondSession.getRevisionManager();
        assertNotSame(firstRevisionManager, secondRevisionManager);
        assertSame(repositoryCache, firstRevisionManager.getCache());
        assertSame(repositoryCache, secondRevisionManager.getCache());
        CDOBranch firstMainBranch = firstSession.getBranchManager().getMainBranch();
        CDOBranch secondMainBranch = secondSession.getBranchManager().getMainBranch();
        assertSame(firstMainBranch, secondMainBranch);
        assertSame(config.getRepository().getBranchManager().getMainBranch(), firstMainBranch);
        CDOBranch createdBranch = firstMainBranch.createBranch("embedded-shared-branch"); //$NON-NLS-1$
        assertSame(createdBranch, secondSession.getBranchManager().getBranch(createdBranch.getID()));
        assertSame(createdBranch, secondSession.getBranchManager().getBranch(createdBranch.getPathName()));
        LifecycleUtil.deactivate(firstSession);
        assertTrue(LifecycleUtil.isActive(secondSession));
        assertTrue(config.getRepository().isActive());
        assertSame(repositoryCache, ((InternalCDORevisionManager)secondSession.getRevisionManager()).getCache());
        assertSame(createdBranch, secondSession.getBranchManager().getBranch(createdBranch.getID()));

      }
      finally
      {
        LifecycleUtil.deactivate(firstSession);
        LifecycleUtil.deactivate(secondSession);

      }
      assertTrue(config.getRepository().isActive());
      CDONet4jSession thirdSession = config.openClientSession();
      try
      {
        assertSame(repositoryCache, ((InternalCDORevisionManager)thirdSession.getRevisionManager()).getCache());
        assertSame(config.getRepository().getBranchManager().getMainBranch(), thirdSession.getBranchManager().getMainBranch());

      }
      finally
      {
        LifecycleUtil.deactivate(thirdSession);

      }

    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);

    }

  }

  public void testNestedRevisionOperationsRestoreSessionAndStoreContext() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-nested-session-context", container, true, false, ModelConfig.Native.INSTANCE); //$NON-NLS-1$
    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession sessionA = config.openClientSession();
      CDONet4jSession sessionB = config.openClientSession();
      CDOTransaction transaction = sessionA.openTransaction();
      try
      {
        CDOResource resource = transaction.createResource(getResourcePath("/nested-session-context")); //$NON-NLS-1$
        Category category = ModelConfig.Native.INSTANCE.getModel1Factory().createCategory();
        category.setName("nested revision context"); //$NON-NLS-1$
        resource.getContents().add(category);
        transaction.commit();
        sessionA.getBranchManager().getMainBranch().createBranch("nested-store-context-branch"); //$NON-NLS-1$
        CDOID categoryID = CDOUtil.getCDOObject(category).cdoID();
        InternalSession serverSessionA = (InternalSession)config.getRepository().getSessionManager().getSession(sessionA.getSessionID());
        InternalSession serverSessionB = (InternalSession)config.getRepository().getSessionManager().getSession(sessionB.getSessionID());
        InternalCDORevisionCache cache = ((InternalCDORevisionManager)config.getRepository().getRevisionManager()).getCache();
        int[] matchingRevisions = { 0 };
        int[] handledBranches = { 0 };
        AtomicReference<InternalSession> nestedOperationSession = new AtomicReference<>();
        IListener nestedBranchListener = event -> {
          if (event instanceof CDOBranchChangedEvent && "nested-session-B-branch".equals(((CDOBranchChangedEvent)event).getBranch().getName())) //$NON-NLS-1$
          {
            nestedOperationSession.set(ServerOperationContext.getSession());
          }
        };
        sessionA.getBranchManager().addListener(nestedBranchListener);
        try
        {
          sessionA.getRevisionManager().handleRevisions(null, sessionA.getBranchManager().getMainBranch(), true, CDOBranchPoint.UNSPECIFIED_DATE, false,
              new CDORevisionHandler()
              {
                @Override
                public boolean handleRevision(CDORevision revision)
                {
                  if (categoryID.equals(revision.getID()))
                  {
                    assertSame(serverSessionA, getCurrentEmbeddedServerSession());
                    IStoreAccessor outerAccessor = StoreThreadLocal.getAccessor();
                    cache.removeRevision(categoryID, revision.getBranch().getVersion(revision.getVersion()));
                    CDORevision nestedRevision = sessionB.getRevisionManager().getRevision(categoryID, transaction.getBranch().getHead(),
                        new Request.Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, CDORevision.UNCHUNKED));
                    assertNotNull(nestedRevision);
                    assertSame(serverSessionA, getCurrentEmbeddedServerSession());
                    assertSame(outerAccessor, StoreThreadLocal.getAccessor());
                    sessionB.getBranchManager().getBranches(0, Integer.MAX_VALUE, branch -> {
                      assertSame(serverSessionA, StoreThreadLocal.getSession());
                      assertSame(serverSessionB, getCurrentEmbeddedServerSession());
                      ++handledBranches[0];

                    });
                    ((InternalCDOBranchManager)sessionB.getBranchManager()).createBranch(InternalCDOBranchManager.BranchLoader.NEW_BRANCH,
                        "nested-session-B-branch", (InternalCDOBranch)sessionB.getBranchManager().getMainBranch(), CDOBranchPoint.UNSPECIFIED_DATE); //$NON-NLS-1$
                    assertSame(serverSessionA, StoreThreadLocal.getSession());
                    assertSame(outerAccessor, StoreThreadLocal.getAccessor());
                    assertSame(serverSessionB, nestedOperationSession.get());
                    assertSame(serverSessionA, StoreThreadLocal.getSession());
                    assertSame(outerAccessor, StoreThreadLocal.getAccessor());
                    ++matchingRevisions[0];

                  }
                  return true;

                }

              });
        }
        finally
        {
          sessionA.getBranchManager().removeListener(nestedBranchListener);
        }
        assertEquals(1, matchingRevisions[0]);
        assertTrue("No persisted branches were loaded", handledBranches[0] > 0); //$NON-NLS-1$
        assertFalse(StoreThreadLocal.hasSession());
        assertFalse(StoreThreadLocal.hasAccessor());
        RuntimeException expected = new RuntimeException("expected revision-handler failure"); //$NON-NLS-1$
        try
        {
          sessionA.getRevisionManager().handleRevisions(null, sessionA.getBranchManager().getMainBranch(), true, CDOBranchPoint.UNSPECIFIED_DATE, false,
              revision -> {
                throw expected;

              });
          fail("The revision handler failure was not propagated"); //$NON-NLS-1$

        }
        catch (RuntimeException ex)
        {
          assertSame(expected, ex);

        }
        assertNull(getCurrentEmbeddedServerSession());
        assertFalse(StoreThreadLocal.hasSession());
        assertFalse(StoreThreadLocal.hasAccessor());

      }
      finally
      {
        LifecycleUtil.deactivate(transaction);
        LifecycleUtil.deactivate(sessionA);
        LifecycleUtil.deactivate(sessionB);

      }

    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);

    }

  }

  public void testBranchManagerOperationUsesCallingEmbeddedSession() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-branch-session-context", container, true); //$NON-NLS-1$
    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession sessionA = config.openClientSession();
      CDONet4jSession sessionB = config.openClientSession();
      try
      {
        sessionA.getBranchManager().getMainBranch().createBranch("stored-branch"); //$NON-NLS-1$
        InternalSession serverSessionB = (InternalSession)config.getRepository().getSessionManager().getSession(sessionB.getSessionID());
        int[] handledBranches = { 0 };
        CDOBranchHandler handler = branch -> {
          assertSame(serverSessionB, getCurrentEmbeddedServerSession());
          ++handledBranches[0];

        };
        sessionB.getBranchManager().getBranches(0, Integer.MAX_VALUE, handler);
        assertTrue("No branches were passed to the handler", handledBranches[0] > 0); //$NON-NLS-1$
        assertNull(getCurrentEmbeddedServerSession());

      }
      finally
      {
        LifecycleUtil.deactivate(sessionA);
        LifecycleUtil.deactivate(sessionB);

      }

    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);

    }

  }

  public void testSharedBranchAndTagChangesAreAppliedOnceAcrossEmbeddedSessions() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-branch-tag-publication", container, true, true, ModelConfig.Native.INSTANCE); //$NON-NLS-1$
    CDONet4jSession sessionA = null;
    CDONet4jSession sessionB = null;
    CDONet4jSession sessionC = null;
    IListener branchListenerA = null;
    IListener branchListenerB = null;
    IListener branchListenerC = null;
    IListener tagListenerA = null;
    IListener tagListenerB = null;
    IListener tagListenerC = null;
    CDOTagList tagList = null;

    try
    {
      LifecycleUtil.activate(config);
      sessionA = config.openClientSession();
      sessionB = config.openClientSession();
      CDOTransaction transaction = sessionA.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/branch-tag-publication")); //$NON-NLS-1$
      Category category = ModelConfig.Native.INSTANCE.getModel1Factory().createCategory();
      category.setName("branch tag publication"); //$NON-NLS-1$
      resource.getContents().add(category);
      CDOCommitInfo commitInfo = transaction.commit();
      LifecycleUtil.deactivate(transaction);
      AtomicInteger branchEventsA = new AtomicInteger();
      AtomicInteger branchEventsB = new AtomicInteger();
      AtomicInteger branchEventsC = new AtomicInteger();
      branchListenerA = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          branchEventsA.incrementAndGet();
        }
      };
      branchListenerB = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          branchEventsB.incrementAndGet();
        }
      };
      branchListenerC = event -> {
        if (event instanceof CDOBranchChangedEvent)
        {
          branchEventsC.incrementAndGet();
        }
      };
      sessionA.getBranchManager().addListener(branchListenerA);
      sessionB.getBranchManager().addListener(branchListenerB);

      tagList = sessionA.getBranchManager().getTagList();
      assertSame(tagList, sessionB.getBranchManager().getTagList());
      AtomicInteger tagEventsA = new AtomicInteger();
      AtomicInteger tagEventsB = new AtomicInteger();
      AtomicInteger tagEventsC = new AtomicInteger();
      tagListenerA = event -> tagEventsA.incrementAndGet();
      tagListenerB = event -> tagEventsB.incrementAndGet();
      tagListenerC = event -> tagEventsC.incrementAndGet();
      tagList.addListener(tagListenerA);
      tagList.addListener(tagListenerB);

      InternalCDOBranchManager clientBranchManager = (InternalCDOBranchManager)sessionA.getBranchManager();
      InternalCDOBranch clientBranch = clientBranchManager.createBranch(InternalCDOBranchManager.BranchLoader.NEW_BRANCH, "client-created-branch", //$NON-NLS-1$
          clientBranchManager.getMainBranch(), CDOBranchPoint.UNSPECIFIED_DATE);
      assertEquals(1, branchEventsA.get());
      assertEquals(1, branchEventsB.get());
      clientBranch.setName("client-renamed-branch"); //$NON-NLS-1$
      assertEquals(2, branchEventsA.get());
      assertEquals(2, branchEventsB.get());
      CDOBranchPoint mainPoint = clientBranchManager.getMainBranch().getPoint(commitInfo.getTimeStamp());
      CDOBranchTag clientTag = clientBranchManager.createTag("client-tag", mainPoint); //$NON-NLS-1$
      clientBranchManager.renameTag("client-tag", "client-renamed-tag"); //$NON-NLS-1$
      clientTag = clientBranchManager.getTag("client-renamed-tag"); //$NON-NLS-1$
      clientBranchManager.moveTag(clientTag, clientBranch.getPoint(commitInfo.getTimeStamp()));
      clientBranchManager.deleteTag(clientTag);
      assertEquals(4, tagEventsA.get());
      assertEquals(4, tagEventsB.get());
      clientBranchManager.deleteBranches(clientBranch.getID(), new Monitor());
      assertEquals(3, branchEventsA.get());
      assertEquals(3, branchEventsB.get());

      InternalSession serverSessionA = (InternalSession)config.getRepository().getSessionManager().getSession(sessionA.getSessionID());
      InternalCDOBranchManager sharedBranchManager = ((InternalRepository)config.getRepository()).getBranchManager();
      StoreThreadLocal.wrap(serverSessionA, () -> {
        InternalCDOBranch directBranch = sharedBranchManager.getMainBranch().createBranch("direct-created-branch"); //$NON-NLS-1$
        directBranch.setName("direct-renamed-branch"); //$NON-NLS-1$
        CDOBranchTag directTag = sharedBranchManager.createTag("direct-tag", sharedBranchManager.getMainBranch().getPoint(commitInfo.getTimeStamp())); //$NON-NLS-1$
        directTag.setName("direct-renamed-tag"); //$NON-NLS-1$
        directTag.move(directBranch.getPoint(commitInfo.getTimeStamp()));
        directTag.delete();
        sharedBranchManager.deleteBranches(directBranch.getID(), new Monitor());
      }).run();
      assertEquals(6, branchEventsA.get());
      assertEquals(6, branchEventsB.get());
      assertEquals(8, tagEventsA.get());
      assertEquals(8, tagEventsB.get());

      sessionA.getBranchManager().removeListener(branchListenerA);
      tagList.removeListener(tagListenerA);
      LifecycleUtil.deactivate(sessionA);
      sessionA = null;

      InternalSession serverSessionB = (InternalSession)config.getRepository().getSessionManager().getSession(sessionB.getSessionID());
      InternalCDOBranch branchAfterClose = StoreThreadLocal.wrap(serverSessionB, () -> sharedBranchManager.getMainBranch().createBranch("branch-after-close")) //$NON-NLS-1$
          .call();
      assertEquals(7, branchEventsB.get());

      sessionC = config.openClientSession();
      sessionC.getBranchManager().addListener(branchListenerC);
      StoreThreadLocal.wrap(serverSessionB, () -> branchAfterClose.setName("branch-after-new-session")) //$NON-NLS-1$
          .run();
      assertEquals(8, branchEventsB.get());
      assertEquals(1, branchEventsC.get());
    }
    finally
    {
      if (sessionA != null)
      {
        if (branchListenerA != null)
        {
          sessionA.getBranchManager().removeListener(branchListenerA);
        }

        LifecycleUtil.deactivate(sessionA);
      }

      if (sessionB != null)
      {
        if (branchListenerB != null)
        {
          sessionB.getBranchManager().removeListener(branchListenerB);
        }

        LifecycleUtil.deactivate(sessionB);
      }

      if (sessionC != null)
      {
        if (branchListenerC != null)
        {
          sessionC.getBranchManager().removeListener(branchListenerC);
        }

        LifecycleUtil.deactivate(sessionC);
      }

      if (tagList != null)
      {
        if (tagListenerA != null)
        {
          tagList.removeListener(tagListenerA);
        }

        if (tagListenerB != null)
        {
          tagList.removeListener(tagListenerB);
        }

        if (tagListenerC != null)
        {
          tagList.removeListener(tagListenerC);
        }
      }

      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testOperationAuthorizerIsRejected()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-branch-authorization", container, true, true, null); //$NON-NLS-1$
    config.setOperationAuthorizer((session, operation) -> {
      return null;
    });
    try
    {
      LifecycleUtil.activate(config);
      fail("Embedded CDO must reject operation authorizers"); //$NON-NLS-1$
    }
    catch (IllegalStateException expected)
    {
      assertTrue(expected.getMessage().contains("unsupported in Embedded CDO")); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);

    }
  }

  public void testSharedBranchAndTagOperationsAfterAllSessionsClose() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-sessionless-branch-operations", container, true, true, ModelConfig.Native.INSTANCE); //$NON-NLS-1$

    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession sessionOne = config.openClientSession();
      CDONet4jSession sessionTwo = config.openClientSession();
      InternalCDOBranchManager branchManager = ((InternalRepository)config.getRepository()).getBranchManager();
      InternalCDOBranch mainBranch = branchManager.getMainBranch();
      CDOTransaction initialTransaction = sessionOne.openTransaction();
      CDOResource initialResource = initialTransaction.createResource(getResourcePath("/sessionless-branch-commit")); //$NON-NLS-1$
      Category category = ModelConfig.Native.INSTANCE.getModel1Factory().createCategory();
      category.setName("before branch operations"); //$NON-NLS-1$
      initialResource.getContents().add(category);
      CDOCommitInfo initialCommit = initialTransaction.commit();
      category.setName("before branch operations update"); //$NON-NLS-1$
      CDOCommitInfo secondCommit = initialTransaction.commit();
      LifecycleUtil.deactivate(sessionOne);
      LifecycleUtil.deactivate(sessionTwo);

      InternalCDOBranch childBranch = mainBranch.createBranch("sessionless-branch"); //$NON-NLS-1$
      childBranch.setName("renamed-sessionless-branch"); //$NON-NLS-1$
      assertEquals("renamed-sessionless-branch", branchManager.getBranch(childBranch.getID()).getName()); //$NON-NLS-1$
      assertTrue(branchManager.getBranches(mainBranch.getID()).contains(childBranch));

      CDOBranchTag tag = branchManager.createTag("sessionless-tag", mainBranch.getPoint(initialCommit.getTimeStamp())); //$NON-NLS-1$
      branchManager.renameTag("sessionless-tag", "renamed-sessionless-tag"); //$NON-NLS-1$
      tag = branchManager.getTag("renamed-sessionless-tag"); //$NON-NLS-1$
      assertNotNull(tag);
      tag.move(mainBranch.getPoint(secondCommit.getTimeStamp()));
      tag.delete();
      assertNull(branchManager.getTag("renamed-sessionless-tag")); //$NON-NLS-1$
      branchManager.deleteBranches(childBranch.getID(), new Monitor());
      assertTrue(childBranch.isDeleted());

      CDONet4jSession sessionThree = config.openClientSession();
      try
      {
        CDOTransaction transaction = sessionThree.openTransaction();
        CDOResource resource = transaction.getResource(getResourcePath("/sessionless-branch-commit")); //$NON-NLS-1$
        Category loadedCategory = (Category)resource.getContents().get(0);
        loadedCategory.setName("after branch operations"); //$NON-NLS-1$
        CDOCommitInfo laterCommit = transaction.commit();
        assertEquals("after branch operations", loadedCategory.getName()); //$NON-NLS-1$
        assertTrue(laterCommit.getTimeStamp() > secondCommit.getTimeStamp());
      }
      finally
      {
        LifecycleUtil.deactivate(sessionThree);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testEmbeddedStoreContextPreservesAndOwnsAccessors() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-store-context", container); //$NON-NLS-1$
    MEMStore_UT store = new MEMStore_UT();
    config.store = store;

    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession clientSession = config.openClientSession();
      InternalRepository repository = (InternalRepository)config.getRepository();
      InternalSession serverSession = repository.getSessionManager().getSessions()[0];
      InternalTransaction transaction = serverSession.openTransaction(Integer.MAX_VALUE, repository.getBranchManager().getMainBranch().getHead());
      IStoreAccessor.CommitContext enclosingCommit = transaction.createCommitContext();

      try
      {
        IStoreAccessor reusedAccessor = store.getReader(null);
        StoreThreadLocal.setAccessor(reusedAccessor);
        StoreThreadLocal.setCommitContext(enclosingCommit);

        try (AutoCloseable ignored = EmbeddedStoreContext.install(null, store))
        {
          assertSame(reusedAccessor, StoreThreadLocal.getAccessor());
          assertSame(enclosingCommit, StoreThreadLocal.getCommitContext());
          assertFalse(StoreThreadLocal.hasSession());
        }

        assertSame(reusedAccessor, StoreThreadLocal.getAccessor());
        assertSame(enclosingCommit, StoreThreadLocal.getCommitContext());
        assertTrue(store.getStoreAccessors().contains(reusedAccessor));

        StoreThreadLocal.remove();
        reusedAccessor.release();
        assertFalse(store.getStoreAccessors().contains(reusedAccessor));

        try
        {
          try (AutoCloseable outer = EmbeddedStoreContext.install(null, store))
          {
            IStoreAccessor acquiredAccessor = StoreThreadLocal.getAccessor();
            assertTrue(store.getStoreAccessors().contains(acquiredAccessor));

            try (AutoCloseable inner = EmbeddedStoreContext.install(null, store))
            {
              assertSame(acquiredAccessor, StoreThreadLocal.getAccessor());
            }

            assertSame(acquiredAccessor, StoreThreadLocal.getAccessor());
            assertTrue(store.getStoreAccessors().contains(acquiredAccessor));
            throw new IllegalStateException("exercise exceptional outer cleanup"); //$NON-NLS-1$
          }
        }
        catch (IllegalStateException expected)
        {
          assertEquals("exercise exceptional outer cleanup", expected.getMessage()); //$NON-NLS-1$
        }

        assertFalse(StoreThreadLocal.hasAccessor());
        assertFalse(StoreThreadLocal.hasSession());
        assertNull(StoreThreadLocal.getCommitContext());
        assertTrue(store.getStoreAccessors().isEmpty());

        try (AutoCloseable context = EmbeddedStoreContext.install(null, store))
        {
          IStoreAccessor ownedAccessor = StoreThreadLocal.getAccessor();
          IStoreAccessor unexpectedAccessor = store.getReader(null);
          StoreThreadLocal.setAccessor(unexpectedAccessor);

          try
          {
            context.close();
            fail("A replaced embedded accessor must be reported"); //$NON-NLS-1$
          }
          catch (IllegalStateException expected)
          {
            assertTrue(expected.getMessage().contains("accessor context changed")); //$NON-NLS-1$
          }

          assertFalse(store.getStoreAccessors().contains(ownedAccessor));
          assertTrue(store.getStoreAccessors().contains(unexpectedAccessor));
          StoreThreadLocal.remove();
          unexpectedAccessor.release();
        }

        StoreThreadLocal.setCommitContext(enclosingCommit);

        try
        {
          EmbeddedStoreContext.install(null, store);
          fail("A commit context without a session or accessor must be rejected"); //$NON-NLS-1$
        }
        catch (IllegalStateException expected)
        {
          assertEquals("Cannot install a sessionless accessor over a partial commit context.", expected.getMessage()); //$NON-NLS-1$
        }

        assertSame(enclosingCommit, StoreThreadLocal.getCommitContext());
        assertFalse(StoreThreadLocal.hasAccessor());
        assertFalse(StoreThreadLocal.hasSession());
        StoreThreadLocal.remove();

        IStoreAccessor sessionAccessor = store.getReader(serverSession);
        StoreThreadLocal.setAccessor(sessionAccessor);
        StoreThreadLocal.setCommitContext(enclosingCommit);

        try (AutoCloseable ignored = EmbeddedStoreContext.install(serverSession, store))
        {
          assertSame(serverSession, StoreThreadLocal.getSession());
          assertSame(sessionAccessor, StoreThreadLocal.getAccessor());
          assertSame(enclosingCommit, StoreThreadLocal.getCommitContext());
        }

        assertSame(serverSession, StoreThreadLocal.getSession());
        assertSame(sessionAccessor, StoreThreadLocal.getAccessor());
        assertSame(enclosingCommit, StoreThreadLocal.getCommitContext());
        StoreThreadLocal.remove();
        sessionAccessor.release();

        CountDownLatch contextsInstalled = new CountDownLatch(2);
        CountDownLatch continueOperations = new CountDownLatch(1);
        CountDownLatch contextsClosed = new CountDownLatch(2);
        AtomicReference<Throwable> concurrentFailure = new AtomicReference<>();
        Thread threadOne = new Thread(() -> exerciseConcurrentStoreContext(store, contextsInstalled, continueOperations, contextsClosed, concurrentFailure),
            "embedded-store-context-one"); //$NON-NLS-1$
        Thread threadTwo = new Thread(() -> exerciseConcurrentStoreContext(store, contextsInstalled, continueOperations, contextsClosed, concurrentFailure),
            "embedded-store-context-two"); //$NON-NLS-1$
        int baselineAccessorCount = store.getStoreAccessors().size();
        threadOne.start();
        threadTwo.start();
        boolean bothInstalled = contextsInstalled.await(10, TimeUnit.SECONDS);
        int installedAccessorCount = store.getStoreAccessors().size();
        continueOperations.countDown();
        boolean bothClosed = contextsClosed.await(10, TimeUnit.SECONDS);
        assertTrue("Both isolated contexts were not installed", bothInstalled); //$NON-NLS-1$
        assertTrue("Concurrent store contexts did not close", bothClosed); //$NON-NLS-1$
        assertEquals(baselineAccessorCount + 2, installedAccessorCount);
        assertEquals(baselineAccessorCount, store.getStoreAccessors().size());

        if (concurrentFailure.get() != null)
        {
          throw new AssertionError(concurrentFailure.get());
        }
      }
      finally
      {
        StoreThreadLocal.remove();
        LifecycleUtil.deactivate(transaction);
        LifecycleUtil.deactivate(clientSession);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testAuthenticationIsPerEmbeddedSession() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-authentication", container, false, false, ModelConfig.Native.INSTANCE); //$NON-NLS-1$
    config.setCredentialsProviders(new PasswordCredentialsProvider("user-one", "password-one"), //$NON-NLS-1$
        new PasswordCredentialsProvider("user-two", "password-two")); //$NON-NLS-1$

    try
    {
      LifecycleUtil.activate(config);
      InternalSessionManager sessionManager = (InternalSessionManager)config.getRepository().getSessionManager();
      sessionManager.setAuthenticator((userID, password) -> {
        if (!(userID.equals("user-one") && String.valueOf(password).equals("password-one") //$NON-NLS-1$
            || userID.equals("user-two") && String.valueOf(password).equals("password-two"))) //$NON-NLS-1$
        {
          throw new SecurityException("Invalid credentials"); //$NON-NLS-1$
        }
      });

      CDONet4jSession sessionOne = config.openClientSession();
      CDONet4jSession sessionTwo = config.openClientSession();
      try
      {
        assertEquals("user-one", sessionOne.getUserID()); //$NON-NLS-1$
        assertEquals("user-two", sessionTwo.getUserID()); //$NON-NLS-1$
        assertSame(((InternalCDORevisionManager)config.getRepository().getRevisionManager()).getCache(),
            ((InternalCDORevisionManager)sessionOne.getRevisionManager()).getCache());
        assertSame(sessionOne.getBranchManager().getMainBranch(), sessionTwo.getBranchManager().getMainBranch());

        Category categoryOne = ModelConfig.Native.INSTANCE.getModel1Factory().createCategory();
        categoryOne.setName("one"); //$NON-NLS-1$
        CDOTransaction transactionOne = sessionOne.openTransaction();
        CDOResource resourceOne = transactionOne.createResource(getResourcePath("/auth-one")); //$NON-NLS-1$
        resourceOne.getContents().add(categoryOne);
        CDOCommitInfo commitOne = transactionOne.commit();
        assertEquals("user-one", commitOne.getUserID()); //$NON-NLS-1$

        Category categoryTwo = ModelConfig.Native.INSTANCE.getModel1Factory().createCategory();
        categoryTwo.setName("two"); //$NON-NLS-1$
        CDOTransaction transactionTwo = sessionTwo.openTransaction();
        CDOResource resourceTwo = transactionTwo.createResource(getResourcePath("/auth-two")); //$NON-NLS-1$
        resourceTwo.getContents().add(categoryTwo);
        CDOCommitInfo commitTwo = transactionTwo.commit();
        assertEquals("user-two", commitTwo.getUserID()); //$NON-NLS-1$

        LifecycleUtil.deactivate(sessionOne);
        CDOTransaction transaction = sessionTwo.openTransaction();
        assertEquals("two", ((Category)transaction.getResource(getResourcePath("/auth-two")).getContents().get(0)).getName()); //$NON-NLS-1$
      }
      finally
      {
        LifecycleUtil.deactivate(sessionOne);
        LifecycleUtil.deactivate(sessionTwo);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testPermissionManagerIsRejectedAfterActivation()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-permission-manager", container); //$NON-NLS-1$

    try
    {
      LifecycleUtil.activate(config);
      IPermissionManager permissionManager = new DefaultRepositoryProtector();
      try
      {
        ((InternalSessionManager)config.getRepository().getSessionManager()).setPermissionManager(permissionManager);
        fail("Embedded CDO must reject permission managers"); //$NON-NLS-1$
      }
      catch (UnsupportedOperationException expected)
      {
        assertTrue(expected.getMessage().contains("unsupported in Embedded CDO")); //$NON-NLS-1$
      }

      CDONet4jSession session = config.openClientSession();
      LifecycleUtil.deactivate(session);
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testIncompatibleSessionManagerIsRejected()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-custom-session-manager", container); //$NON-NLS-1$
    config.setCustomSessionManager(true);
    config.setInitialPermissionManager(new DefaultRepositoryProtector());

    try
    {
      try
      {
        LifecycleUtil.activate(config);
        fail("Embedded CDO must reject incompatible custom session managers"); //$NON-NLS-1$
      }
      catch (IllegalStateException expected)
      {
        assertTrue(expected.getMessage().contains("Custom session managers are unsupported in Embedded CDO")); //$NON-NLS-1$
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  public void testRepositoryProtectorIsRejectedBeforeActivation()
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    TestConfig config = new TestConfig("embedded-repository-protector", container); //$NON-NLS-1$
    config.setRepositoryProtector(new DefaultRepositoryProtector());

    try
    {
      try
      {
        LifecycleUtil.activate(config);
        fail("Embedded CDO must reject repository protectors"); //$NON-NLS-1$
      }
      catch (IllegalStateException expected)
      {
        assertTrue(expected.getMessage().contains("Repository protectors are unsupported in Embedded CDO")); //$NON-NLS-1$
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
      LifecycleUtil.deactivate(container);
    }
  }

  private static InternalSession getCurrentEmbeddedServerSession()
  {
    return EmbeddedSession.get();
  }

  private static void exerciseConcurrentStoreContext(IStore store, CountDownLatch contextsInstalled, CountDownLatch continueOperations,
      CountDownLatch contextsClosed, AtomicReference<Throwable> failure)
  {
    try (AutoCloseable ignored = EmbeddedStoreContext.install(null, store))
    {
      IStoreAccessor accessor = StoreThreadLocal.getAccessor();
      if (!((MEMStore_UT)store).getStoreAccessors().contains(accessor))
      {
        throw new AssertionError("The thread did not retain its own store accessor"); //$NON-NLS-1$
      }

      contextsInstalled.countDown();
      if (!continueOperations.await(10, TimeUnit.SECONDS))
      {
        throw new AssertionError("The concurrent operation was not released"); //$NON-NLS-1$
      }

      if (StoreThreadLocal.getAccessor() != accessor)
      {
        throw new AssertionError("Another thread replaced this thread's accessor"); //$NON-NLS-1$
      }
    }
    catch (Throwable ex)
    {
      failure.compareAndSet(null, ex);
    }
    finally
    {
      StoreThreadLocal.remove();
      contextsClosed.countDown();
    }
  }

  public void testInitializedContainerRemainsUsable() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();

    try
    {
      assertEmbeddedRepositoryWorks(container, "embedded-initialized-container"); //$NON-NLS-1$
      assertNotNull(container.getFactory(AcceptorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
      assertNotNull(container.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  public void testGlobalContainerRemainsUsable() throws Exception
  {
    assertEmbeddedRepositoryWorks(IManagedContainer.INSTANCE, "embedded-global-container"); //$NON-NLS-1$
    assertNotNull(IManagedContainer.INSTANCE.getFactory(AcceptorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
    assertNotNull(IManagedContainer.INSTANCE.getFactory(ConnectorFactory.PRODUCT_GROUP, "jvm")); //$NON-NLS-1$
    assertNotNull(IManagedContainer.INSTANCE.getFactory(ServerProtocolFactory.PRODUCT_GROUP, "cdo")); //$NON-NLS-1$
  }

  private void assertEmbeddedRepositoryWorks(IManagedContainer container, String repositoryName) throws Exception
  {
    TestConfig config = new TestConfig(repositoryName, container);

    try
    {
      LifecycleUtil.activate(config);
      CDONet4jSession session = config.openClientSession();
      try
      {
        assertNotNull(session);
        String resourcePath = getResourcePath("/container-initialization"); //$NON-NLS-1$
        CDOTransaction writeTransaction = session.openTransaction();
        try
        {
          writeTransaction.createResource(resourcePath);
          writeTransaction.commit();
        }
        finally
        {
          LifecycleUtil.deactivate(writeTransaction);
        }

        CDOTransaction readTransaction = session.openTransaction();
        try
        {
          assertNotNull(readTransaction.getResource(resourcePath));
        }
        finally
        {
          LifecycleUtil.deactivate(readTransaction);
        }
      }
      finally
      {
        LifecycleUtil.deactivate(session);
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(config.clientContainer);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestConfig extends CDOEmbeddedRepositoryConfig
  {
    private final IManagedContainer container;

    private final boolean branching;

    private final boolean auditing;

    private final IModelConfig modelConfig;

    private final IManagedContainer clientContainer = ContainerUtil.createInitializedContainer();

    private IStore store;

    private OperationAuthorizer<ISession> operationAuthorizer;

    private PasswordCredentialsProvider[] credentialsProviders;

    private int credentialsProviderIndex;

    private boolean customSessionManager;

    private IPermissionManager initialPermissionManager;

    private IRepositoryProtector repositoryProtector;

    public TestConfig(String repositoryName, IManagedContainer container)
    {
      this(repositoryName, container, false);

    }

    public TestConfig(String repositoryName, IManagedContainer container, boolean branching)
    {
      this(repositoryName, container, branching, false, null);

    }

    public TestConfig(String repositoryName, IManagedContainer container, boolean branching, boolean auditing, IModelConfig modelConfig)
    {
      super(repositoryName);
      this.container = container;
      this.branching = branching;
      this.auditing = auditing;
      this.modelConfig = modelConfig;

    }

    @Override
    public IManagedContainer getContainer()
    {
      return container;

    }

    public void setOperationAuthorizer(OperationAuthorizer<ISession> operationAuthorizer)
    {
      this.operationAuthorizer = operationAuthorizer;

    }

    public void setCredentialsProviders(PasswordCredentialsProvider... credentialsProviders)
    {
      this.credentialsProviders = credentialsProviders;
    }

    public void setCustomSessionManager(boolean customSessionManager)
    {
      this.customSessionManager = customSessionManager;
    }

    public void setInitialPermissionManager(IPermissionManager initialPermissionManager)
    {
      this.initialPermissionManager = initialPermissionManager;
    }

    public void setRepositoryProtector(IRepositoryProtector repositoryProtector)
    {
      this.repositoryProtector = repositoryProtector;
    }

    @Override
    public CDONet4jSessionConfiguration createSessionConfiguration(IConnector connector, CDOBranchManager branchManager, CDORevisionManager revisionManager)
    {
      CDONet4jSessionConfiguration sessionConfiguration = super.createSessionConfiguration(connector, branchManager, revisionManager);
      if (credentialsProviders != null)
      {
        sessionConfiguration.setCredentialsProvider(credentialsProviders[credentialsProviderIndex++]);
      }
      return sessionConfiguration;

    }

    @Override
    public IStore createStore(IManagedContainer container)
    {
      return store == null ? MEMStoreUtil.createMEMStore() : store;

    }

    @Override
    public void initPackages(IRepository repository, List<EPackage> packages)
    {
      if (modelConfig != null)
      {
        packages.add(modelConfig.getModel1Package());

      }

    }

    @Override
    public IConnector createConnector(IManagedContainer container)
    {
      return JVMUtil.getConnector(clientContainer, "cdo_embedded_repo_" + getRepositoryName()); //$NON-NLS-1$

    }

    @Override
    public void initProperties(IManagedContainer container, Map<String, String> properties)
    {
      properties.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(branching));
      properties.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(auditing));

    }

    @Override
    protected void activateRepository(IRepository repository)
    {
      if (repositoryProtector != null)
      {
        ((InternalRepository)repository).setProtector(repositoryProtector);
      }
      if (customSessionManager)
      {
        SessionManager sessionManager = new SessionManager();
        if (initialPermissionManager != null)
        {
          sessionManager.setPermissionManager(initialPermissionManager);
        }

        ((InternalRepository)repository).setSessionManager(sessionManager);
      }
      if (operationAuthorizer != null)
      {
        ((InternalRepository)repository).addOperationAuthorizer(operationAuthorizer);

      }
      super.activateRepository(repository);

    }

  }

}
