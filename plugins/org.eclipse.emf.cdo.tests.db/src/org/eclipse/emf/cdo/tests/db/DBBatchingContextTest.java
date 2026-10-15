/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.BatchingContext;
import org.eclipse.emf.cdo.server.internal.db.StringIDHandler;
import org.eclipse.emf.cdo.server.internal.db.UUIDHandler;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLDefaultSetter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLValueSetter;
import org.eclipse.net4j.db.h2.H2Adapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Focused tests for commit-scoped pending-work coordination.
 *
 * @author Eike Stepper
 */
public class DBBatchingContextTest extends TestCase
{
  public void testIDAndTypeMappingsAreValueSetters()
  {
    SQLValueSetter<CDOID> idSetter = new StringIDHandler(null);
    SQLValueSetter<Object> typeSetter = asValueSetter(null);

    assertNotNull(idSetter);
    assertNull(typeSetter);
    assertTrue(SQLValueSetter.class.isAssignableFrom(IIDHandler.class));
    assertTrue(SQLValueSetter.class.isAssignableFrom(ITypeMapping.class));
    assertTrue(SQLDefaultSetter.class.isAssignableFrom(ITypeMapping.class));
  }

  public void testTypeMappingBindsItsDefaultAndSuppliedValueThroughDistinctMethods() throws SQLException
  {
    List<String> calls = new ArrayList<>();
    ITypeMapping mapping = (ITypeMapping)Proxy.newProxyInstance(DBBatchingContextTest.class.getClassLoader(), new Class<?>[] { ITypeMapping.class },
        (proxy, method, args) -> {
          if ("setDefaultValue".equals(method.getName())) //$NON-NLS-1$
          {
            calls.add("default(" + args[1] + ")"); //$NON-NLS-1$ //$NON-NLS-2$
          }
          else if ("setValue".equals(method.getName())) //$NON-NLS-1$
          {
            calls.add("value(" + args[1] + "," + args[2] + ")"); //$NON-NLS-1$ //$NON-NLS-2$
          }

          return method.getReturnType() == Void.TYPE ? null : State.defaultValue(method.getReturnType());
        });
    IDBTable table = DBUtil.createSchema("typeMappingBinding", false, false).addTable("item"); //$NON-NLS-1$
    SQLStatement sql = SQL.selectAll() //
        .from(table) //
        .where(table.addField("id", DBType.INTEGER)).eq(SQL.param()) //
        .and(table.addField("name", DBType.VARCHAR)).eq(SQL.param()) //
        .build().compile(new H2Adapter());
    PreparedStatement stmt = (PreparedStatement)Proxy.newProxyInstance(DBBatchingContextTest.class.getClassLoader(), new Class<?>[] { PreparedStatement.class },
        (proxy, method, args) -> method.getReturnType() == Void.TYPE ? null : State.defaultValue(method.getReturnType()));
    SQLParameterBinder binder = sql.binder(stmt);

    assertSame(binder, binder.bindParameter(mapping).bindParameter("supplied", mapping));
    assertEquals(List.of("default(1)", "value(2,supplied)"), calls);
    assertEquals(3, binder.getNextIndex());
  }

  private static SQLValueSetter<Object> asValueSetter(ITypeMapping mapping)
  {
    return mapping;
  }

  public void testStringIDSQLLiteralEscapesApostrophes()
  {
    StringBuilder sqlLiteral = new StringBuilder();
    new StringIDHandler(null).appendCDOID(sqlLiteral, CDOIDUtil.createString("meta'id"));

    assertEquals("'meta''id'", sqlLiteral.toString());

    sqlLiteral.setLength(0);
    new StringIDHandler(null).appendCDOID(sqlLiteral, CDOID.NULL);

    assertEquals("'null'", sqlLiteral.toString());
  }

  public void testUUIDExternalIDSQLLiteralEscapesApostrophes()
  {
    StringBuilder sqlLiteral = new StringBuilder();
    new UUIDHandler(null).appendCDOID(sqlLiteral, CDOIDUtil.createExternal("urn:cdo:test/o'hara"));

    assertEquals("'urn:cdo:test/o''hara'", sqlLiteral.toString());

    sqlLiteral.setLength(0);
    new UUIDHandler(null).appendCDOID(sqlLiteral, CDOID.NULL);

    assertEquals("'0'", sqlLiteral.toString());
  }

  public void testCompiledStatementUsesExistingConnectionPreparationAndBatchManagement()
  {
    IDBTable table = DBUtil.createSchema("batching", false, false).addTable("item");
    IDBStatementCapture capture = new IDBStatementCapture();
    IDBConnection connection = (IDBConnection)Proxy.newProxyInstance(DBBatchingContextTest.class.getClassLoader(), new Class<?>[] { IDBConnection.class },
        capture);
    BatchingContext context = new BatchingContext(connection, 10, 100);
    SQLStatement sql = SQL.insertInto(table) //
        .columns(table.addField("id", DBType.INTEGER)) //
        .values(SQL.param()) //
        .build() //
        .compile(new H2Adapter());

    BatchedStatement statement = context.createStatement(sql, ReuseProbability.HIGH, "test"); //$NON-NLS-1$

    assertEquals(sql.getSQL(), capture.sql);
    assertSame(ReuseProbability.HIGH, capture.reuseProbability);
    assertEquals(0, context.getPendingCount());

    context.discardStatement(statement);
  }

  public void testPhaseFlushClearsPendingWork()
  {
    BatchingContext context = new BatchingContext(null, 3, 100);
    State state = new State();
    BatchedStatement statement = createStatement(state);
    context.manageStatement(statement);

    add(context, statement, 2);
    assertEquals(2, context.getPendingCount());
    context.flushPhase();

    assertEquals(0, context.getPendingCount());
    assertEquals(1, state.executionCount);
    assertEquals(1, context.getPhaseFlushCount());
  }

  public void testLargestStatementIsFlushedAtGlobalLimit()
  {
    BatchingContext context = new BatchingContext(null, 10, 4);
    State first = new State();
    State second = new State();
    BatchedStatement firstStatement = createStatement(first);
    BatchedStatement secondStatement = createStatement(second);
    context.manageStatement(firstStatement);
    context.manageStatement(secondStatement);

    add(context, firstStatement, 3);
    add(context, secondStatement, 3);

    assertEquals(0, first.pendingCount);
    assertEquals(3, second.pendingCount);
    assertEquals(3, context.getPendingCount());
    assertEquals(1, context.getCapacityFlushCount());
  }

  public void testEqualCapacityUsesRegistrationOrder()
  {
    BatchingContext context = new BatchingContext(null, 10, 4);
    State first = new State();
    State second = new State();
    BatchedStatement firstStatement = createStatement(first);
    BatchedStatement secondStatement = createStatement(second);
    context.manageStatement(firstStatement);
    context.manageStatement(secondStatement);

    add(context, firstStatement, 2);
    add(context, secondStatement, 2);
    assertEquals(0, first.pendingCount);
    assertEquals(2, second.pendingCount);
  }

  public void testFinalFlushAndDiscard()
  {
    BatchingContext context = new BatchingContext(null, 10, 100);
    State state = new State();
    BatchedStatement statement = createStatement(state);
    context.manageStatement(statement);
    add(context, statement, 1);

    context.flushFinal();
    assertEquals(0, context.getPendingCount());
    assertEquals(1, context.getFinalFlushCount());

    add(context, statement, 2);
    context.discard();
    assertEquals(0, state.pendingCount);
    assertEquals(0, context.getPendingCount());
  }

  public void testDiscardCleansAllStatementsAfterCleanupFailure()
  {
    BatchingContext context = new BatchingContext(null, 10, 100);
    CleanupState firstState = new CleanupState(true, true);
    CleanupState secondState = new CleanupState(false, false);
    BatchedStatement first = createStatement(firstState);
    BatchedStatement second = createStatement(secondState);
    context.manageStatement(first);
    context.manageStatement(second);
    add(context, first, 1);
    add(context, second, 1);

    try
    {
      context.discard();
      fail("Expected cleanup failure");
    }
    catch (RuntimeException expected)
    {
      // The first cleanup failure is retained.
    }

    assertEquals(0, context.getPendingCount());
    assertTrue(firstState.closed);
    assertTrue(secondState.cleared);
    assertTrue(secondState.closed);
  }

  private static void add(BatchingContext context, BatchedStatement statement, int count)
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

      context.afterExecuteUpdate(statement);
    }
  }

  private static BatchedStatement createStatement(State state)
  {
    return createStatement((InvocationHandler)state);
  }

  private static BatchedStatement createStatement(InvocationHandler state)
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

  private static final class IDBStatementCapture implements InvocationHandler
  {
    private final Connection jdbcConnection = (Connection)Proxy.newProxyInstance(DBBatchingContextTest.class.getClassLoader(),
        new Class<?>[] { Connection.class }, (proxy, method, args) -> method.getReturnType() == void.class ? null : State.defaultValue(method.getReturnType()));

    private final IDBPreparedStatement preparedStatement = (IDBPreparedStatement)Proxy.newProxyInstance(DBBatchingContextTest.class.getClassLoader(),
        new Class<?>[] { IDBPreparedStatement.class }, (proxy, method, args) -> {
          if ("getConnection".equals(method.getName())) //$NON-NLS-1$
          {
            return jdbcConnection;
          }

          return method.getReturnType() == void.class ? null : State.defaultValue(method.getReturnType());
        });

    private String sql;

    private ReuseProbability reuseProbability;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
    {
      if ("prepareStatement".equals(method.getName())) //$NON-NLS-1$
      {
        sql = (String)args[0];
        reuseProbability = (ReuseProbability)args[1];
        return preparedStatement;
      }

      return method.getReturnType() == void.class ? null : State.defaultValue(method.getReturnType());
    }
  }

  private static final class CleanupState implements InvocationHandler
  {
    private final boolean failClear;

    private final boolean failClose;

    private boolean cleared;

    private boolean closed;

    private int pendingCount;

    private CleanupState(boolean failClear, boolean failClose)
    {
      this.failClear = failClear;
      this.failClose = failClose;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      switch (method.getName())
      {
      case "addBatch": //$NON-NLS-1$
        ++pendingCount;
        return null;

      case "getPendingCount": //$NON-NLS-1$
        return pendingCount;

      case "clearBatch": //$NON-NLS-1$
        cleared = true;
        pendingCount = 0;
        if (failClear)
        {
          throw new SQLException("injected clear failure");
        }

        return null;

      case "close": //$NON-NLS-1$
        closed = true;
        if (failClose)
        {
          throw new RuntimeException("injected close failure");
        }

        return null;

      default:
        return State.defaultValue(method.getReturnType());
      }
    }
  }
}
