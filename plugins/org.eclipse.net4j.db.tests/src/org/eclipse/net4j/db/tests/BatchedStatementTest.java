/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.internal.db.BatchedStatementImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

/**
 * Focused tests for the explicit lifecycle and accounting of {@link BatchedStatement}.
 *
 * @author Eike Stepper
 */
public class BatchedStatementTest extends TestCase
{
  public void testPendingAndAutomaticFlush() throws Exception
  {
    State state = new State();
    BatchedStatement statement = createStatement(state, 2);

    statement.executeUpdate();
    assertEquals(1, statement.getPendingCount());
    assertEquals(0, statement.getExecutionCount());

    statement.executeUpdate();
    assertEquals(0, statement.getPendingCount());
    assertEquals(1, statement.getExecutionCount());
    assertEquals(2, state.executedEntries);
    statement.close();
  }

  public void testExplicitFlushAndReuse() throws Exception
  {
    State state = new State();
    BatchedStatement statement = createStatement(state, 10);

    assertEquals(0, statement.flush());
    statement.executeUpdate();
    assertEquals(1, statement.flush());
    assertEquals(0, statement.getPendingCount());
    assertEquals(1, statement.getExecutionCount());

    statement.executeUpdate();
    statement.flush();
    assertEquals(2, statement.getExecutionCount());
    statement.close();
  }

  public void testClearBatchResetsPendingState() throws Exception
  {
    State state = new State();
    BatchedStatement statement = createStatement(state, 10);

    statement.executeUpdate();
    statement.clearBatch();
    assertEquals(0, statement.getPendingCount());
    assertEquals(0, statement.getExecutionCount());
    statement.close();
  }

  public void testSuccessNoInfoIsUnknownButSuccessful() throws Exception
  {
    State state = new State();
    state.results = new int[] { Statement.SUCCESS_NO_INFO, 1 };
    BatchedStatement statement = createStatement(state, 10);

    statement.executeUpdate();
    statement.executeUpdate();
    statement.flush();

    assertEquals(1, statement.getTotalResult());
    assertEquals(1, statement.getUnknownResultCount());
    assertEquals(0, statement.getPendingCount());
    statement.close();
  }

  public void testExecuteFailedIsRejected() throws Exception
  {
    State state = new State();
    state.results = new int[] { Statement.EXECUTE_FAILED };
    BatchedStatement statement = createStatement(state, 10);
    statement.executeUpdate();

    try
    {
      statement.flush();
      fail("Expected failed batch");
    }
    catch (RuntimeException expected)
    {
      // DBException is the existing failure contract.
    }
    finally
    {
      statement.close();
    }

    assertEquals(1, state.executeBatchCount);
  }

  public void testSQLExceptionDoesNotExecuteAgainOnClose() throws Exception
  {
    State state = new State();
    state.failure = new SQLException("injected");
    BatchedStatement statement = createStatement(state, 10);
    statement.executeUpdate();

    try
    {
      statement.flush();
      fail("Expected failed batch");
    }
    catch (SQLException expected)
    {
      // Expected.
    }

    statement.close();
    assertEquals(1, state.executeBatchCount);
    assertEquals(0, statement.getPendingCount());
  }

  private static BatchedStatement createStatement(State state, int batchSize)
  {
    PreparedStatement delegate = (PreparedStatement)Proxy.newProxyInstance(BatchedStatementTest.class.getClassLoader(),
        new Class<?>[] { PreparedStatement.class }, state);
    return new BatchedStatementImpl(delegate, batchSize);
  }

  private static final class State implements InvocationHandler
  {
    private int pendingEntries;

    private int executedEntries;

    private int executeBatchCount;

    private SQLException failure;

    private int[] results;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      switch (method.getName())
      {
      case "addBatch": //$NON-NLS-1$
        ++pendingEntries;
        return null;

      case "executeBatch": //$NON-NLS-1$
        ++executeBatchCount;
        if (failure != null)
        {
          throw failure;
        }

        int count = pendingEntries;
        pendingEntries = 0;
        executedEntries += count;
        if (results != null)
        {
          int[] configuredResults = results;
          results = null;
          return configuredResults;
        }

        int[] successfulResults = new int[count];
        for (int i = 0; i < count; i++)
        {
          successfulResults[i] = 1;
        }

        return successfulResults;

      case "clearBatch": //$NON-NLS-1$
        pendingEntries = 0;
        return null;

      case "getConnection": //$NON-NLS-1$
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

      if (type == char.class)
      {
        return (char)0;
      }

      throw new AssertionError(type);
    }
  }
}
