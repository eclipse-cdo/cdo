/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDONestedTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionScope;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeClosedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeOpenedEvent;

import org.eclipse.emf.internal.cdo.transaction.CDOFileTransactionImpl;
import org.eclipse.emf.internal.cdo.transaction.CDONestedTransactionImpl;
import org.eclipse.emf.internal.cdo.transaction.DelegatingCDOTransactionImpl;

import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.emf.spi.cdo.DelegatingCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
