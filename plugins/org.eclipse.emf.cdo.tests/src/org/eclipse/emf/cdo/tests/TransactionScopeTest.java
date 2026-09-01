/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver3;
import org.eclipse.emf.cdo.transaction.CDONestedTransaction;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionScope;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeClosedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeOpenedEvent;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.internal.cdo.transaction.CDOFileTransactionImpl;
import org.eclipse.emf.internal.cdo.transaction.CDONestedTransactionImpl;
import org.eclipse.emf.internal.cdo.transaction.DelegatingCDOTransactionImpl;

import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.DelegatingCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Verifies the closed-nesting lifecycle contract independently of any generated model implementation. The tests use
 * only transaction and scope handles and are therefore applicable to both Native and Legacy model configurations.
 *
 * @author Eike Stepper
 */
public class TransactionScopeTest extends AbstractCDOTest
{
  public void testDelegatingHierarchyIsExplicit() throws Exception
  {
    assertEquals(DelegatingCDOTransactionImpl.class, CDOFileTransactionImpl.class.getSuperclass());
    assertEquals(DelegatingCDOTransactionImpl.class, CDONestedTransactionImpl.class.getSuperclass());
    assertFalse(InternalCDOTransaction.class.isAssignableFrom(DelegatingCDOTransactionImpl.class));
    assertFalse(InternalCDOTransaction.class.isAssignableFrom(CDOFileTransactionImpl.class));
    assertFalse(InternalCDOTransaction.class.isAssignableFrom(CDONestedTransactionImpl.class));
  }

  public void testNavigationAndNestedCommit() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope outer = transaction.openScope();
      CDOTransactionScope inner = outer.openScope();

      assertEquals(1, outer.getDepth());
      assertEquals(2, inner.getDepth());
      assertEquals(null, outer.getOuterScope());
      assertEquals(inner, outer.getInnerScope());
      assertEquals(outer, inner.getOuterScope());
      assertEquals(outer, transaction.getOutermostScope());
      assertEquals(inner, transaction.getInnermostScope());
      assertEquals(2, transaction.getScopes().size());

      try
      {
        transaction.getScopes().clear();
        fail("UnsupportedOperationException expected");
      }
      catch (UnsupportedOperationException expected)
      {
        // SUCCESS
      }

      try
      {
        outer.commit();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      inner.commit();
      outer.commit();
      assertEquals(null, transaction.getInnermostScope());
    }
  }

  public void testRollbackAndCloseCascade() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope outer = transaction.openScope();
      CDOTransactionScope middle = outer.openScope();
      CDOTransactionScope inner = middle.openScope();

      outer.close();
      assertEquals(false, outer.isOpen());
      assertEquals(false, middle.isOpen());
      assertEquals(false, inner.isOpen());
      assertEquals(0, transaction.getScopes().size());

      outer.close();
      try
      {
        inner.rollback();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }
    }
  }

  public void testRootOperationsAndFacade() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope scope = transaction.openScope();
      CDONestedTransaction facade = scope.asTransaction();
      assertSame(facade, scope.asTransaction());
      assertSame(transaction.getResourceSet(), facade.getResourceSet());

      CDOTransactionScope inner = facade.openScope();
      assertSame(scope, inner.getOuterScope());
      inner.commit();

      try
      {
        transaction.commit();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      try
      {
        facade.commit();
        fail("UnsupportedOperationException expected");
      }
      catch (UnsupportedOperationException expected)
      {
        // SUCCESS
      }

      transaction.rollback();
      assertEquals(false, scope.isOpen());
      assertEquals(0, transaction.getScopes().size());
    }
  }

  public void testDelegatingTransactionResolution() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope scope = transaction.openScope();
      CDONestedTransaction facade = scope.asTransaction();

      assertSame(transaction, ((DelegatingCDOTransaction)facade).getDelegate());
      assertSame(transaction, DelegatingCDOTransaction.getEffectiveTransaction(facade));
      scope.rollback();
    }
  }

  public void testLifecycleEventsArePostStateAndOrdered() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      List<IEvent> events = new ArrayList<>();
      transaction.addListener(new IListener()
      {
        @Override
        public void notifyEvent(IEvent event)
        {
          if (event instanceof CDOTransactionScopeOpenedEvent || event instanceof CDOTransactionScopeClosedEvent)
          {
            events.add(event);
          }
        }
      });

      CDOTransactionScope outer = transaction.openScope();
      CDOTransactionScope inner = outer.openScope();
      outer.rollback();

      assertEquals(4, events.size());
      assertSame(transaction, events.get(0).getSource());
      assertSame(transaction, events.get(3).getSource());
      assertSame(inner, ((CDOTransactionScopeClosedEvent)events.get(2)).getScope());
      assertEquals(CDOTransactionScopeClosedEvent.Cause.ROLLED_BACK, ((CDOTransactionScopeClosedEvent)events.get(2)).getCause());
      assertSame(outer, ((CDOTransactionScopeClosedEvent)events.get(3)).getScope());
      assertEquals(0, transaction.getScopes().size());
    }
  }

  public void testDeepScopeCascadeAndNavigation() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      List<IEvent> events = new ArrayList<>();
      List<CDOTransactionScopeClosedEvent> closedEvents = new ArrayList<>();
      transaction.addListener(event -> {
        if (event instanceof CDOTransactionScopeOpenedEvent)
        {
          events.add(event);
        }
        else if (event instanceof CDOTransactionScopeClosedEvent)
        {
          events.add(event);
          closedEvents.add((CDOTransactionScopeClosedEvent)event);
        }
      });

      CDOTransactionScope outer = transaction.openScope();
      CDOTransactionScope middle = outer.openScope();
      CDOTransactionScope inner = middle.openScope();

      assertEquals(3, transaction.getScopes().size());
      assertSame(outer, transaction.getOutermostScope());
      assertSame(inner, transaction.getInnermostScope());
      assertSame(middle, outer.getInnerScope());
      assertSame(outer, middle.getOuterScope());
      assertSame(inner, middle.getInnerScope());
      assertSame(middle, inner.getOuterScope());

      List<CDOTransactionScope> snapshot = transaction.getScopes();
      try
      {
        snapshot.remove(0);
        fail("Immutable scope snapshot expected");
      }
      catch (UnsupportedOperationException expected)
      {
        // SUCCESS
      }

      try
      {
        transaction.commit();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      middle.rollback();

      assertEquals(2, closedEvents.size());
      assertSame(inner, closedEvents.get(0).getScope());
      assertSame(middle, closedEvents.get(1).getScope());
      assertEquals(CDOTransactionScopeClosedEvent.Cause.ROLLED_BACK, closedEvents.get(0).getCause());
      assertTrue(outer.isOpen());
      assertEquals(1, transaction.getScopes().size());

      outer.close();
      assertEquals(3, closedEvents.size());
      assertSame(outer, closedEvents.get(2).getScope());
      assertEquals(0, transaction.getScopes().size());
    }
  }

  public void testNestedFacadeCommitFamilyIsRejected() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDONestedTransaction facade = transaction.openScope().asTransaction();
      Callable<Object> callable = () -> null;

      assertUnsupported(() -> facade.commit());
      assertUnsupported(() -> facade.commit(null));
      assertUnsupported(() -> facade.commit(callable, 1, null));
      assertUnsupported(() -> facade.commit((Runnable)() -> {
      }, 1, null));
      assertUnsupported(() -> facade.commitAndClose(null, false));
    }
  }

  public void testNestedFacadeLifecycleAndAutomaticEventDetachment() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope scope = transaction.openScope();
      CDONestedTransaction facade = scope.asTransaction();
      List<IEvent> events = new ArrayList<>();
      facade.addListener(events::add);

      assertFalse(facade.isClosed());
      scope.commit();

      assertTrue(facade.isClosed());
      assertEquals(1, events.size());
      assertTrue(events.get(0) instanceof CDOTransactionScopeClosedEvent);
      assertSame(scope, ((CDOTransactionScopeClosedEvent)events.get(0)).getScope());
      assertSame(transaction, events.get(0).getSource());

      facade.addListener(events::add);
      assertTrue(facade.isClosed());
      transaction.close();
      assertEquals(1, events.size());
    }
  }

  public void testScopesCanBeCompletedAcrossThreadsInStackOrder() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      ExecutorService executor = Executors.newSingleThreadExecutor();

      try
      {
        CDOTransactionScope outer = transaction.openScope();
        CDOTransactionScope inner = executor.submit(outer::openScope).get();

        inner.commit();
        executor.submit(outer::rollback).get();
        assertEquals(0, transaction.getScopes().size());

        CDOTransactionScope committed = transaction.openScope();
        executor.submit(committed::commit).get();
        assertEquals(0, transaction.getScopes().size());

        CDOTransactionScope rolledBack = transaction.openScope();
        executor.submit(rolledBack::rollback).get();
        assertEquals(0, transaction.getScopes().size());
      }
      finally
      {
        executor.shutdownNow();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
      }
    }
  }

  public void testRootRollbackAndCloseFinalizeObservedNestedFacade() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope scope = transaction.openScope();
      CDONestedTransaction facade = scope.asTransaction();
      CountDownLatch rollback = new CountDownLatch(1);

      ExecutorService executor = Executors.newSingleThreadExecutor();

      try
      {
        Future<?> observer = executor.submit(() -> {
          try
          {
            rollback.await();
            assertTrue(facade.isClosed());
            assertEquals(0, facade.getScopes().size());
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new AssertionError(ex);
          }
        });

        transaction.rollback();
        rollback.countDown();
        observer.get();

        try
        {
          facade.openScope();
          fail("IllegalStateException expected");
        }
        catch (IllegalStateException expected)
        {
          // SUCCESS
        }
      }
      finally
      {
        rollback.countDown();
        executor.shutdownNow();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
      }

      CDOTransactionScope closeScope = transaction.openScope();
      CDONestedTransaction closeFacade = closeScope.asTransaction();
      transaction.close();
      assertFalse(closeScope.isOpen());
      assertTrue(closeFacade.isClosed());

      try
      {
        closeFacade.rollback();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }
    }
  }

  public void testReentrantScopeLifecycleListenerCanOpenAndCloseScope() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      AtomicBoolean reentered = new AtomicBoolean();
      AtomicReference<Throwable> failure = new AtomicReference<>();

      transaction.addListener(event -> {
        if (event instanceof CDOTransactionScopeOpenedEvent && reentered.compareAndSet(false, true))
        {
          try
          {
            transaction.getInnermostScope().commit();
          }
          catch (Throwable ex)
          {
            failure.set(ex);
          }
        }
      });

      transaction.openScope();
      assertNull(failure.get());
      assertEquals(0, transaction.getScopes().size());
    }
  }

  public void testPartialCommitRetainsChangesOutsideCommittedNestedHistory() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "partialNested"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/partialNested"));
      CDOResource deferredResource = transaction.createResource(getResourcePath("/partialNestedB"));
      transaction.commit();
      CDOTransactionScope scope = transaction.openScope();
      Company committed = getModel1Factory().createCompany();
      committed.setName("nested");
      resource.getContents().add(committed);
      scope.commit();

      Company deferred = getModel1Factory().createCompany();
      deferred.setName("root");
      deferredResource.getContents().add(deferred);
      Set<EObject> committables = new HashSet<>();
      committables.add(committed);
      committables.add(resource);
      transaction.setCommittables(committables);
      transaction.commit();

      assertFalse(transaction.getNewObjects().containsValue(CDOUtil.getCDOObject(committed)));
      assertNew(deferred, transaction);
      assertEquals("root", deferred.getName());

      committables.clear();
      committables.add(deferred);
      committables.add(deferredResource);
      transaction.setCommittables(committables);
      transaction.commit();
      assertFalse(transaction.isDirty());
    }
  }

  public void testNestedScopesShareTransactionLocksAcrossRollbackAndCommit() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "nestedLocks"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/nestedLocks"));
      Company company = (Company)resource.getContents().get(0);
      CDOLock lock = CDOUtil.getCDOObject(company).cdoWriteLock();
      transaction.options().setAutoReleaseLocksEnabled(false);

      lock.lock(DEFAULT_TIMEOUT);
      CDOTransactionScope scope = transaction.openScope();
      company.setName("rolledBack");
      scope.rollback();
      assertTrue(lock.isLocked());

      lock.unlock();
      assertFalse(lock.isLocked());
      scope = transaction.openScope();
      lock.lock(DEFAULT_TIMEOUT);
      assertTrue(lock.isLocked());
      company.setName("rolledBackDuringScope");
      scope.rollback();
      assertTrue(lock.isLocked());

      scope = transaction.openScope();
      company.setName("committed");
      scope.commit();
      assertTrue(lock.isLocked());

      transaction.options().setAutoReleaseLocksEnabled(true);
      transaction.commit();
      assertFalse(lock.isLocked());

      lock.lock(DEFAULT_TIMEOUT);
      scope = transaction.openScope();
      company.setName("committedThenRolledBack");
      scope.commit();
      transaction.rollback();
      assertFalse(lock.isLocked());
    }
  }

  public void testRetainedNestedHistoryAcrossRootCommitEpochs() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "nestedHistoryStress"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/nestedHistoryStress"));

      int emptyScopes = 1000;
      int changedScopes = 256;
      int epochs = 3;
      int expectedCompanies = 1;

      for (int epoch = 0; epoch < epochs; ++epoch)
      {
        for (int i = 0; i < emptyScopes; ++i)
        {
          CDOTransactionScope scope = transaction.openScope();
          scope.commit();
        }

        for (int i = 0; i < changedScopes; ++i)
        {
          CDOTransactionScope scope = transaction.openScope();
          Company company = getModel1Factory().createCompany();
          company.setName("epoch-" + epoch + "-" + i);
          resource.getContents().add(company);
          scope.commit();
        }

        expectedCompanies += changedScopes;
        assertEquals(changedScopes, transaction.getNewObjects().size());
        assertTrue(transaction.isDirty());

        transaction.commit();
        assertFalse(transaction.isDirty());
        assertEquals(0, transaction.getNewObjects().size());
        assertEquals(0, transaction.getDirtyObjects().size());
        assertEquals(0, transaction.getRevisionDeltas().size());
        assertEquals(expectedCompanies, resource.getContents().size());
      }
    }
  }

  public void testRootCommitRejectsScopeOpenedDuringPreCommit() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "commitScopeRace"))
    {
      CountDownLatch preCommitEntered = new CountDownLatch(1);
      CountDownLatch releasePreCommit = new CountDownLatch(1);

      transaction.options().addConflictResolver(new CDOConflictResolver3()
      {
        private CDOTransaction resolverTransaction;

        @Override
        public CDOTransaction getTransaction()
        {
          return resolverTransaction;
        }

        @Override
        public void setTransaction(CDOTransaction value)
        {
          resolverTransaction = value;
        }

        @Override
        public void resolveConflicts(Set<CDOObject> conflicts)
        {
        }

        @Override
        public boolean preCommit()
        {
          preCommitEntered.countDown();

          try
          {
            releasePreCommit.await();
          }
          catch (InterruptedException ex)
          {
            Thread.currentThread().interrupt();
            throw new AssertionError(ex);
          }

          return true;
        }
      });

      ExecutorService executor = Executors.newFixedThreadPool(2);

      try
      {
        Future<?> commit = executor.submit(() -> {
          try
          {
            transaction.commit();
            fail("IllegalStateException expected");
          }
          catch (CommitException ex)
          {
            assertTrue(ex.getCause() instanceof IllegalStateException);
          }
          catch (Exception ex)
          {
            throw new AssertionError(ex);
          }
        });

        preCommitEntered.await();

        Future<?> scope = executor.submit(() -> transaction.openScope());
        scope.get();
        releasePreCommit.countDown();

        commit.get();
        assertEquals(1, transaction.getScopes().size());
        transaction.getInnermostScope().close();
      }
      finally
      {
        executor.shutdownNow();
      }
    }
  }

  public void testExportIncludesChangesFromCommittedNestedScope() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction source = openCommittedCompanyTransaction(session, "exportNested"))
    {
      CDOResource resource = source.getResource(getResourcePath("/exportNested"));
      Company existing = (Company)resource.getContents().get(0);
      existing.setName("changed");

      CDOTransactionScope scope = source.openScope();
      Company added = getModel1Factory().createCompany();
      added.setName("added");
      resource.getContents().add(added);
      scope.commit();

      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      source.exportChanges(bytes);

      try (CDOTransaction target = session.openTransaction())
      {
        target.importChanges(new ByteArrayInputStream(bytes.toByteArray()), false);
        assertEquals(1, target.getNewObjects().size());

        CDOObject imported = target.getNewObjects().values().iterator().next();
        assertEquals("Company", imported.eClass().getName());
        assertEquals("added", ((Company)CDOUtil.getEObject(imported)).getName());
        assertEquals(2, target.getDirtyObjects().size());
        assertEquals(0, target.getScopes().size());
      }
    }
  }

  public void testNestedFacadeClosedWhenRootCloses() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    try
    {
      CDONestedTransaction facade = transaction.openScope().asTransaction();
      assertFalse(facade.isClosed());
      transaction.close();
      assertTrue(facade.isClosed());
    }
    finally
    {
      session.close();
    }
  }

  public void testPersistentChangeRollbackRestoresRootStateAndIdentity() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "persistentRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/persistentRollback"));
      Company company = (Company)resource.getContents().get(0);
      CDOObject cdoCompany = CDOUtil.getCDOObject(company);
      CDOID id = cdoCompany.cdoID();

      CDONestedTransaction nested = transaction.openScope().asTransaction();
      assertSame(cdoCompany, nested.getObject(id));

      company.setName("nested");
      assertTrue(transaction.isDirty());

      nested.getScope().rollback();

      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
      assertSame(cdoCompany, transaction.getObject(id));
      assertSame(cdoCompany, nested.getObject(id));
    }
  }

  public void testNewObjectRollbackCleansTemporaryIdentity() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOResource resource = transaction.createResource(getResourcePath("/newRollback"));
      transaction.commit();

      CDOTransactionScope scope = transaction.openScope();
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      CDOID id = CDOUtil.getCDOObject(company).cdoID();

      assertTrue(transaction.isDirty());
      scope.rollback();

      assertTransient(company);
      assertFalse(transaction.isDirty());
      assertNull(transaction.getObject(id, false));
    }
  }

  public void testChildCommitThenParentRollbackDiscardsAllNestedHistory() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "childThenParentRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/childThenParentRollback"));
      Company parentCompany = (Company)resource.getContents().get(0);

      CDOTransactionScope parent = transaction.openScope();
      parentCompany.setName("parent-before-child");

      CDOTransactionScope child = parent.openScope();
      Company childCompany = getModel1Factory().createCompany();
      resource.getContents().add(childCompany);
      childCompany.setName("child");
      child.commit();

      parentCompany.setName("parent-after-child");
      parent.rollback();

      assertEquals("original", parentCompany.getName());
      assertTransient(childCompany);
      assertFalse(transaction.isDirty());
    }
  }

  public void testChildCommitThenParentCommitDefersRepositoryCommit() throws Exception
  {
    try (CDOSession session = openSession())
    {
      try (CDOTransaction transaction = openCommittedCompanyTransaction(session, "childThenParentCommit"))
      {
        CDOResource resource = transaction.getResource(getResourcePath("/childThenParentCommit"));
        Company company = (Company)resource.getContents().get(0);
        CDOTransactionScope parent = transaction.openScope();
        CDOTransactionScope child = parent.openScope();

        company.setName("committed-later");
        child.commit();
        assertTrue(transaction.isDirty());
        parent.commit();
        assertTrue(transaction.isDirty());

        transaction.commit();
      }

      try (CDOTransaction verification = session.openTransaction())
      {
        CDOResource resource = verification.getResource(getResourcePath("/childThenParentCommit"));
        EObject company = resource.getContents().get(0);
        assertEquals("committed-later", company.eGet(getModel1Package().getAddress_Name()));
      }
    }
  }

  public void testPublicSavepointSurvivesNestedCommitAndRollsBackNestedChanges() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "savepointAcrossNestedCommit"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/savepointAcrossNestedCommit"));
      Company company = (Company)resource.getContents().get(0);
      company.setName("before-nested");
      CDOSavepoint savepoint = transaction.setSavepoint();

      CDOTransactionScope scope = transaction.openScope();
      company.setName("inside-nested");
      scope.commit();

      savepoint.rollback();

      assertEquals("before-nested", company.getName());
      assertTrue(transaction.isDirty());
      assertEquals(0, transaction.getScopes().size());
    }
  }

  public void testDetachReattachRollbackRestoresPersistentObject() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "detachReattachRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/detachReattachRollback"));
      Company company = (Company)resource.getContents().get(0);
      CDOID id = CDOUtil.getCDOObject(company).cdoID();
      CDOTransactionScope scope = transaction.openScope();

      resource.getContents().remove(company);
      resource.getContents().add(company);
      scope.rollback();

      assertEquals("original", company.getName());
      assertEquals(id, CDOUtil.getCDOObject(company).cdoID());
      assertTrue(resource.getContents().contains(company));
      assertClean(company, transaction);
      assertFalse(transaction.getDetachedObjects().containsKey(id));
    }
  }

  public void testDeepNestedCommitThenOuterRollbackDiscardsAllHistory() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "deepCommitRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/deepCommitRollback"));
      Company company = (Company)resource.getContents().get(0);
      CDOObject cdoCompany = CDOUtil.getCDOObject(company);

      CDOTransactionScope n1 = transaction.openScope();
      company.setName("A");
      CDOTransactionScope n2 = n1.openScope();
      company.setName("B");
      CDOTransactionScope n3 = n2.openScope();
      company.setName("C");
      n3.commit();
      n2.commit();
      company.setName("D");
      n1.rollback();

      assertEquals("original", company.getName());
      assertSame(cdoCompany, transaction.getObject(cdoCompany.cdoID()));
      assertFalse(transaction.isDirty());
    }
  }

  public void testDeepNestedRollbackThenContinuedParentCommitPersistsRetainedHistory() throws Exception
  {
    try (CDOSession session = openSession())
    {
      try (CDOTransaction transaction = openCommittedCompanyTransaction(session, "deepRollbackContinue"))
      {
        CDOResource resource = transaction.getResource(getResourcePath("/deepRollbackContinue"));
        Company company = (Company)resource.getContents().get(0);

        CDOTransactionScope n1 = transaction.openScope();
        company.setName("A");
        CDOTransactionScope n2 = n1.openScope();
        company.setName("B");
        CDOTransactionScope n3 = n2.openScope();
        company.setName("C");
        n3.rollback();
        assertEquals("B", company.getName());

        company.setName("D");
        n2.commit();
        company.setName("E");
        n1.commit();
        assertEquals("E", company.getName());
        transaction.commit();
      }

      try (CDOTransaction verification = session.openTransaction())
      {
        CDOResource resource = verification.getResource(getResourcePath("/deepRollbackContinue"));
        EObject company = resource.getContents().get(0);
        assertEquals("E", company.eGet(getModel1Package().getAddress_Name()));
      }
    }
  }

  public void testDeepNestedCommitThenRootRollbackRestoresCleanRoot() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "deepCommitRootRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/deepCommitRootRollback"));
      Company company = (Company)resource.getContents().get(0);

      CDOTransactionScope n1 = transaction.openScope();
      CDOTransactionScope n2 = n1.openScope();
      CDOTransactionScope n3 = n2.openScope();
      company.setName("nested");
      n3.commit();
      n2.commit();
      n1.commit();

      assertTrue(transaction.isDirty());
      transaction.rollback();

      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
    }
  }

  public void testNestedRollbackRestoresDirtyRootBaseline() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "dirtyBaseline"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/dirtyBaseline"));
      Company company = (Company)resource.getContents().get(0);
      company.setName("root");
      CDOTransactionScope scope = transaction.openScope();
      company.setName("nested");

      scope.rollback();

      assertEquals("root", company.getName());
      assertTrue(transaction.isDirty());
      assertDirty(company, transaction);
    }
  }

  public void testNestedTerminalOperationsFollowExistingContract() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = session.openTransaction())
    {
      CDOTransactionScope committed = transaction.openScope();
      committed.commit();
      try
      {
        committed.commit();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      CDOTransactionScope rolledBack = transaction.openScope();
      CDONestedTransaction facade = rolledBack.asTransaction();
      rolledBack.rollback();
      assertTrue(facade.isClosed());
      try
      {
        rolledBack.rollback();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      CDOTransactionScope closed = transaction.openScope();
      closed.close();
      closed.close();
      assertFalse(closed.isOpen());
    }
  }

  public void testDeepNestedCommitThenPublicSavepointRollback() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "deepSavepointRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/deepSavepointRollback"));
      Company company = (Company)resource.getContents().get(0);
      CDOSavepoint savepoint = transaction.setSavepoint();

      CDOTransactionScope n1 = transaction.openScope();
      CDOTransactionScope n2 = n1.openScope();
      company.setName("nested");
      n2.commit();
      n1.commit();

      savepoint.rollback();

      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
      assertEquals(0, transaction.getScopes().size());
    }
  }

  public void testRollbackAfterNestedCommitIsRejected() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "rollbackAfterCommit"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/rollbackAfterCommit"));
      Company company = (Company)resource.getContents().get(0);
      CDOTransactionScope scope = transaction.openScope();
      CDONestedTransaction facade = scope.asTransaction();
      company.setName("committed-to-root");
      scope.commit();

      try
      {
        scope.rollback();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      assertTrue(facade.isClosed());
      assertEquals("committed-to-root", company.getName());
      assertTrue(transaction.isDirty());
      transaction.rollback();
      assertEquals("original", company.getName());
    }
  }

  public void testCommitAfterNestedRollbackIsRejected() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "commitAfterRollback"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/commitAfterRollback"));
      Company company = (Company)resource.getContents().get(0);
      CDOTransactionScope scope = transaction.openScope();
      CDONestedTransaction facade = scope.asTransaction();
      company.setName("discarded");
      scope.rollback();

      try
      {
        scope.commit();
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // SUCCESS
      }

      assertTrue(facade.isClosed());
      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
    }
  }

  public void testDeepNestedAllCommitThenRootCommitPersistsOnce() throws Exception
  {
    try (CDOSession session = openSession())
    {
      CDOID temporaryID;
      try (CDOTransaction transaction = openCommittedCompanyTransaction(session, "deepAllCommit"))
      {
        CDOResource resource = transaction.getResource(getResourcePath("/deepAllCommit"));
        CDOTransactionScope n1 = transaction.openScope();
        Company companyA = getModel1Factory().createCompany();
        companyA.setName("A");
        resource.getContents().add(companyA);
        CDOID temporaryA = CDOUtil.getCDOObject(companyA).cdoID();
        CDOTransactionScope n2 = n1.openScope();
        Company companyB = getModel1Factory().createCompany();
        companyB.setName("B");
        resource.getContents().add(companyB);
        CDOID temporaryB = CDOUtil.getCDOObject(companyB).cdoID();
        CDOTransactionScope n3 = n2.openScope();
        Company companyC = getModel1Factory().createCompany();
        companyC.setName("C");
        resource.getContents().add(companyC);
        temporaryID = CDOUtil.getCDOObject(companyC).cdoID();
        n3.commit();
        n2.commit();
        n1.commit();

        assertEquals(0, transaction.getScopes().size());
        assertTrue(transaction.isDirty());
        assertNew(companyA, transaction);
        assertNew(companyB, transaction);
        assertNew(companyC, transaction);
        assertEquals(temporaryA, CDOUtil.getCDOObject(companyA).cdoID());
        assertEquals(temporaryB, CDOUtil.getCDOObject(companyB).cdoID());
        assertEquals(temporaryID, CDOUtil.getCDOObject(companyC).cdoID());
        assertTrue(transaction.getNewObjects().containsKey(temporaryA));
        assertTrue(transaction.getNewObjects().containsKey(temporaryB));
        assertTrue(transaction.getNewObjects().containsKey(temporaryID));
        transaction.commit();
        assertFalse(CDOUtil.getCDOObject(companyA).cdoID().isTemporary());
        assertFalse(CDOUtil.getCDOObject(companyB).cdoID().isTemporary());
        assertFalse(CDOUtil.getCDOObject(companyC).cdoID().isTemporary());
        assertFalse(transaction.isDirty());
      }

      try (CDOTransaction verification = session.openTransaction())
      {
        CDOResource resource = verification.getResource(getResourcePath("/deepAllCommit"));
        assertEquals(4, resource.getContents().size());
        Set<String> names = new HashSet<>();
        for (EObject object : resource.getContents())
        {
          names.add((String)object.eGet(getModel1Package().getAddress_Name()));
        }
        assertTrue(names.contains("original"));
        assertTrue(names.contains("A"));
        assertTrue(names.contains("B"));
        assertTrue(names.contains("C"));
      }
    }
  }

  public void testSavepointCreatedDuringChildIsTruncatedByParentRollback() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "savepointDuringChild"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/savepointDuringChild"));
      Company company = (Company)resource.getContents().get(0);
      CDOTransactionScope n1 = transaction.openScope();
      company.setName("A");
      CDOTransactionScope n2 = n1.openScope();
      company.setName("B");
      CDOSavepoint savepoint = transaction.setSavepoint();
      company.setName("C");
      n1.rollback();

      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
      assertEquals(0, transaction.getScopes().size());
      assertFalse(n1.isOpen());
      assertFalse(n2.isOpen());
      try
      {
        savepoint.rollback();
        fail("IllegalArgumentException expected");
      }
      catch (IllegalArgumentException expected)
      {
        // SUCCESS
      }
    }
  }

  public void testOlderPublicSavepointRollbackClosesActiveNestedScopes() throws Exception
  {
    try (CDOSession session = openSession(); CDOTransaction transaction = openCommittedCompanyTransaction(session, "olderSavepoint"))
    {
      CDOResource resource = transaction.getResource(getResourcePath("/olderSavepoint"));
      Company company = (Company)resource.getContents().get(0);
      CDOSavepoint savepoint = transaction.setSavepoint();
      CDOTransactionScope n1 = transaction.openScope();
      company.setName("A");
      CDOTransactionScope n2 = n1.openScope();
      company.setName("B");

      savepoint.rollback();

      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
      assertEquals(0, transaction.getScopes().size());
      assertFalse(n1.isOpen());
      assertFalse(n2.isOpen());
      assertTrue(savepoint.isValid());

      company.setName("usable");
      assertTrue(transaction.isDirty());
      transaction.rollback();
      assertEquals("original", company.getName());
      assertFalse(transaction.isDirty());
    }
  }

  private CDOTransaction openCommittedCompanyTransaction(CDOSession session, String path) throws Exception
  {
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/" + path));
    Company company = getModel1Factory().createCompany();
    company.setName("original");
    resource.getContents().add(company);
    transaction.commit();
    return transaction;
  }

  private static void assertUnsupported(RunnableWithException operation) throws Exception
  {
    try
    {
      operation.run();
      fail("UnsupportedOperationException expected");
    }
    catch (UnsupportedOperationException expected)
    {
      // SUCCESS
    }
  }
}
