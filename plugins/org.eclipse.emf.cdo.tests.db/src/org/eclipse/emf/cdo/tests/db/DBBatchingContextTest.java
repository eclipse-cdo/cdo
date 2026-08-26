/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.internal.db.DBBatchingContext;

import org.eclipse.net4j.db.BatchedStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;

/**
 * Focused tests for commit-scoped pending-work coordination.
 *
 * @author Eike Stepper
 */
public class DBBatchingContextTest extends TestCase
{
  public void testPhaseFlushClearsPendingWork()
  {
    DBBatchingContext context = new DBBatchingContext(3, 100);
    State state = new State();
    BatchedStatement statement = createStatement(state);
    context.manage(statement);

    add(context, statement, 2);
    assertEquals(2, context.getPendingCount());
    context.flushPhase();

    assertEquals(0, context.getPendingCount());
    assertEquals(1, state.executionCount);
    assertEquals(1, context.getPhaseFlushCount());
  }

  public void testLargestStatementIsFlushedAtGlobalLimit()
  {
    DBBatchingContext context = new DBBatchingContext(10, 4);
    State first = new State();
    State second = new State();
    BatchedStatement firstStatement = createStatement(first);
    BatchedStatement secondStatement = createStatement(second);
    context.manage(firstStatement);
    context.manage(secondStatement);

    add(context, firstStatement, 3);
    add(context, secondStatement, 3);

    assertEquals(0, first.pendingCount);
    assertEquals(3, second.pendingCount);
    assertEquals(3, context.getPendingCount());
    assertEquals(1, context.getCapacityFlushCount());
  }

  public void testEqualCapacityUsesRegistrationOrder()
  {
    DBBatchingContext context = new DBBatchingContext(10, 4);
    State first = new State();
    State second = new State();
    BatchedStatement firstStatement = createStatement(first);
    BatchedStatement secondStatement = createStatement(second);
    context.manage(firstStatement);
    context.manage(secondStatement);

    add(context, firstStatement, 2);
    add(context, secondStatement, 2);
    assertEquals(0, first.pendingCount);
    assertEquals(2, second.pendingCount);
  }

  public void testFinalFlushAndDiscard()
  {
    DBBatchingContext context = new DBBatchingContext(10, 100);
    State state = new State();
    BatchedStatement statement = createStatement(state);
    context.manage(statement);
    add(context, statement, 1);

    context.flushFinal();
    assertEquals(0, context.getPendingCount());
    assertEquals(1, context.getFinalFlushCount());

    add(context, statement, 2);
    context.discard();
    assertEquals(0, state.pendingCount);
    assertEquals(0, context.getPendingCount());
  }

  private static void add(DBBatchingContext context, BatchedStatement statement, int count)
  {
    for (int i = 0; i < count; i++)
    {
      try
      {
        statement.addBatch();
      }
      catch (Exception ex)
      {
        throw new AssertionError(ex);
      }

      context.afterAdd(statement);
    }
  }

  private static BatchedStatement createStatement(State state)
  {
    return (BatchedStatement)Proxy.newProxyInstance(DBBatchingContextTest.class.getClassLoader(), new Class<?>[] { BatchedStatement.class }, state);
  }

  private static final class State implements InvocationHandler
  {
    private int pendingCount;

    private int executionCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
      switch (method.getName())
      {
      case "addBatch": //$NON-NLS-1$
        ++pendingCount;
        return null;

      case "flush": //$NON-NLS-1$
        int result = pendingCount;
        pendingCount = 0;
        if (result != 0)
        {
          ++executionCount;
        }

        return result;

      case "getPendingCount": //$NON-NLS-1$
        return pendingCount;

      case "getExecutionCount": //$NON-NLS-1$
        return executionCount;

      case "getBatchSize": //$NON-NLS-1$
      case "getBatchCount": //$NON-NLS-1$
      case "getTotalResult": //$NON-NLS-1$
      case "getUnknownResultCount": //$NON-NLS-1$
        return 0;

      case "clearBatch": //$NON-NLS-1$
        pendingCount = 0;
        return null;

      case "close": //$NON-NLS-1$
        return null;

      default:
        return defaultValue(method.getReturnType());
      }
    }

    private static Object defaultValue(Class<?> type)
    {
      if (!type.isPrimitive())
      {
        return null;
      }

      if (type == boolean.class)
      {
        return false;
      }

      if (type == char.class)
      {
        return (char)0;
      }

      if (type == byte.class)
      {
        return (byte)0;
      }

      if (type == short.class)
      {
        return (short)0;
      }

      if (type == int.class)
      {
        return 0;
      }

      if (type == long.class)
      {
        return 0L;
      }

      if (type == float.class)
      {
        return 0F;
      }

      if (type == double.class)
      {
        return 0D;
      }

      throw new AssertionError(type);
    }
  }
}
