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
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLDefaultSetter;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.spi.db.DelegatingDBAdapter;

import org.h2.jdbcx.JdbcDataSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * Verifies JDBC preparation and parameter binding without requiring a database connection.
 *
 * @author Eike Stepper
 */
public class SQLDMLBindingTest extends TestCase
{
  public void testPreparationDelegatesUnchangedSQLAndReusePolicy() throws SQLException
  {
    Schema schema = schema();
    String sql = "SELECT 1 FROM \"item\"";
    final Object[] arguments = new Object[2];
    IDBPreparedStatement expected = proxy(IDBPreparedStatement.class, (proxy, method, args) -> null);
    IDBConnection connection = proxy(IDBConnection.class, (proxy, method, args) -> {
      if ("prepareStatement".equals(method.getName()))
      {
        arguments[0] = args[0];
        arguments[1] = args[1];
        return expected;
      }

      return defaultValue(method.getReturnType());
    });

    SQLStatement statement = SQL.select(SQL.ONE) //
        .from(schema.table) //
        .build() //
        .compile(new H2Adapter());
    IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH);
    assertSame(expected, prepared);
    assertEquals(sql, arguments[0]);
    assertSame(ReuseProbability.HIGH, arguments[1]);
  }

  public void testPreparationPreservesScrollableResultSetOptions() throws SQLException
  {
    Schema schema = schema();
    Object[] arguments = new Object[4];
    IDBPreparedStatement expected = proxy(IDBPreparedStatement.class, (proxy, method, args) -> null);
    IDBConnection connection = proxy(IDBConnection.class, (proxy, method, args) -> {
      if ("prepareStatement".equals(method.getName()) && args.length == 4)
      {
        System.arraycopy(args, 0, arguments, 0, args.length);
        return expected;
      }

      return defaultValue(method.getReturnType());
    });
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .build() //
        .compile(new H2Adapter());

    IDBPreparedStatement prepared = statement.prepare(connection, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ReuseProbability.MEDIUM);

    assertSame(expected, prepared);
    assertEquals(statement.getSQL(), arguments[0]);
    assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, arguments[1]);
    assertEquals(ResultSet.CONCUR_READ_ONLY, arguments[2]);
    assertSame(ReuseProbability.MEDIUM, arguments[3]);
  }

  public void testPositionalAndIdentityBindingAndPartialAndStrictCounts() throws Exception
  {
    Schema schema = schema();
    SQLParameter repeated = SQL.param();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(repeated).or(schema.name).eq(repeated) //
        .build() //
        .compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();

    assertEquals(2, statement.bind(jdbc, 7, "seven"));
    assertEquals("setInt(1,7)", recorder.calls.get(0));
    assertEquals("setString(2,seven)", recorder.calls.get(1));
    recorder.calls.clear();
    statement.bindParameter(jdbc, repeated, 42);
    assertEquals("setInt(1,42)", recorder.calls.get(0));
    assertEquals("setObject(2,42)", recorder.calls.get(1));

    recorder.calls.clear();
    statement.bindParameter(jdbc, repeated, null, (s, index, value) -> recorder.calls.add("custom(" + index + "," + value + ")"));
    assertEquals("custom(1,null)", recorder.calls.get(0));
    assertEquals("custom(2,null)", recorder.calls.get(1));

    recorder.calls.clear();
    try
    {
      statement.bindAll(jdbc, 1);
      fail("Expected a complete value-count failure");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Expected 2"));
      assertTrue(recorder.calls.isEmpty());
    }
  }

  public void testBindFromRangesReturnCountsAndNullVarargs() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param()).and(schema.name).eq(SQL.param()) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();

    assertEquals(0, statement.bind(jdbc));
    assertEquals(0, statement.bindFrom(jdbc, 3));
    assertEquals(1, statement.bindFrom(jdbc, 2, (Object)null));
    assertEquals("setNull(2," + new H2Adapter().getJDBCTypeForNull(DBType.VARCHAR) + ")", recorder.calls.get(0));
    recorder.calls.clear();

    try
    {
      statement.bindFrom(jdbc, 2, "second", "overflow");
      fail("Expected sequence range validation");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(recorder.calls.isEmpty());
    }

    try
    {
      statement.bindFrom(jdbc, 0);
      fail("Expected invalid start position");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }

    try
    {
      statement.bind(jdbc, (Object[])null);
      fail("Expected null varargs validation");
    }
    catch (NullPointerException expected)
    {
      // Expected.
    }

    byte[] bytes = { 1, 2 };
    SQLStatement arrayStatement = SQL.selectAll().from(schema.table).where(schema.name).eq(SQL.param()).build().compile(new H2Adapter());
    assertEquals(1, arrayStatement.bind(jdbc, bytes));
    assertEquals("setObject(1," + bytes + ")", recorder.calls.get(0));
  }

  public void testConfiguredSetterAndOverridesReplaceConfiguredStrategy() throws Exception
  {
    Schema schema = schema();
    List<String> calls = new ArrayList<>();
    SQLParameter configuredSetter = SQL.param((PreparedStatement stmt, int index, String value) -> calls.add("configured(" + value + ")"));
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.name).eq(configuredSetter).or(schema.name).eq(configuredSetter) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();

    statement.bind(recorder.statement(), "x", "y");
    assertEquals(List.of("configured(x)", "configured(y)"), calls);
    calls.clear();

    statement.bind(recorder.statement(), (Object)null, (Object)null);
    assertEquals(List.of("configured(null)", "configured(null)"), calls);
    assertTrue(recorder.calls.isEmpty());
    calls.clear();

    statement.bindParameter(recorder.statement(), configuredSetter, null,
        (PreparedStatement stmt, int index, String value) -> calls.add("override(" + index + "," + value + ")"));
    assertEquals(List.of("override(1,null)", "override(2,null)"), calls);
    assertTrue(recorder.calls.isEmpty());

    calls.clear();
    statement.bindParameter(recorder.statement(), 1, "abc", String::toUpperCase);
    assertEquals("setString(1,ABC)", recorder.calls.get(0));
    assertTrue(calls.isEmpty());
  }

  public void testBooleanBindingUsesSetBooleanAtConfiguredAndCursorPositions() throws Exception
  {
    Schema schema = schema();
    SQLParameter booleanParameter = SQL.param((PreparedStatement stmt, int index, Boolean value) -> stmt.setBoolean(index, value));
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(booleanParameter).or(schema.id).eq(booleanParameter).or(schema.id).eq(SQL.param()) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    SQLParameterBinder binder = statement.binder(recorder.statement());

    binder.bind(true, false).bindParameter(true, PreparedStatement::setBoolean);

    assertEquals(List.of("setBoolean(1,true)", "setBoolean(2,false)", "setBoolean(3,true)"), recorder.calls);
    assertEquals(4, binder.getNextIndex());
  }

  public void testAdapterBindsKnownScalarTypesWithTypedSetters() throws Exception
  {
    IDBTable table = DBUtil.createSchema("binding", false, false).addTable("scalar_types");
    DBType[] types = { DBType.BOOLEAN, DBType.BIT, DBType.TINYINT, DBType.SMALLINT, DBType.INTEGER, DBType.BIGINT, DBType.FLOAT, DBType.REAL, DBType.DOUBLE,
        DBType.NUMERIC, DBType.DECIMAL, DBType.CHAR, DBType.VARCHAR, DBType.LONGVARCHAR, DBType.BINARY, DBType.VARBINARY, DBType.LONGVARBINARY };
    IDBField[] fields = new IDBField[types.length];
    SQLExpression[] parameters = new SQLExpression[types.length];
    for (int i = 0; i < types.length; i++)
    {
      fields[i] = table.addField("value" + i, types[i]);
      parameters[i] = SQL.param();
    }

    SQLStatement statement = SQL.insertInto(table) //
        .columns(fields[0], Arrays.copyOfRange(fields, 1, fields.length)) //
        .values(parameters[0], Arrays.copyOfRange(parameters, 1, parameters.length)) //
        .build().compile(new H2Adapter());
    byte[] bytes = { 1, 2 };
    Object[] values = { Boolean.TRUE, Boolean.FALSE, Byte.valueOf((byte)3), Short.valueOf((short)4), Integer.valueOf(5), Long.valueOf(6), Float.valueOf(7.0f),
        Float.valueOf(8.0f), Double.valueOf(9.0), new BigDecimal("10.1"), new BigDecimal("11.1"), "c", "v", "long", bytes, bytes, bytes };
    Recorder recorder = new Recorder();

    statement.bind(recorder.statement(), values);

    assertEquals("setBoolean(1,true)", recorder.calls.get(0));
    assertEquals("setBoolean(2,false)", recorder.calls.get(1));
    assertEquals("setByte(3,3)", recorder.calls.get(2));
    assertEquals("setShort(4,4)", recorder.calls.get(3));
    assertEquals("setInt(5,5)", recorder.calls.get(4));
    assertEquals("setLong(6,6)", recorder.calls.get(5));
    assertEquals("setFloat(7,7.0)", recorder.calls.get(6));
    assertEquals("setFloat(8,8.0)", recorder.calls.get(7));
    assertEquals("setDouble(9,9.0)", recorder.calls.get(8));
    assertEquals("setBigDecimal(10,10.1)", recorder.calls.get(9));
    assertEquals("setBigDecimal(11,11.1)", recorder.calls.get(10));
    assertEquals("setString(12,c)", recorder.calls.get(11));
    assertEquals("setString(13,v)", recorder.calls.get(12));
    assertEquals("setString(14,long)", recorder.calls.get(13));
    assertTrue(recorder.calls.get(14).startsWith("setBytes(15,"));
    assertTrue(recorder.calls.get(15).startsWith("setBytes(16,"));
    assertTrue(recorder.calls.get(16).startsWith("setBytes(17,"));
  }

  public void testAdapterBindingPoliciesForUnknownAndIncompatibleTypes() throws Exception
  {
    Schema schema = schema();
    Recorder defaultRecorder = new Recorder();
    assertFalse(new H2Adapter().useJavaTypeForUnknownParameterType());
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.ONE)).build().compile(new H2Adapter()).bind(defaultRecorder.statement(), 12);
    assertEquals("setObject(1,12)", defaultRecorder.calls.get(0));

    H2Adapter javaTypeAdapter = new H2Adapter()
    {
      @Override
      public boolean useJavaTypeForUnknownParameterType()
      {
        return true;
      }
    };
    assertTrue(javaTypeAdapter.useJavaTypeForUnknownParameterType());
    Recorder javaTypeRecorder = new Recorder();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.ONE)).build().compile(javaTypeAdapter).bind(javaTypeRecorder.statement(), true);
    assertEquals("setBoolean(1,true)", javaTypeRecorder.calls.get(0));

    Recorder scalarRecorder = new Recorder();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.ONE)).build().compile(javaTypeAdapter).bind(scalarRecorder.statement(),
        Integer.valueOf(13));
    assertEquals("setInt(1,13)", scalarRecorder.calls.get(0));

    Recorder unsupportedRecorder = new Recorder();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.ONE)).build().compile(javaTypeAdapter).bind(unsupportedRecorder.statement(), new Object());
    assertTrue(unsupportedRecorder.calls.get(0).startsWith("setObject(1,"));

    Recorder incompatibleRecorder = new Recorder();
    SQL.selectAll().from(schema.table).where(schema.id).eq(SQL.param()).build().compile(new H2Adapter()).bind(incompatibleRecorder.statement(),
        Long.valueOf(12));
    assertEquals("setObject(1,12)", incompatibleRecorder.calls.get(0));
  }

  public void testAdapterBindingHookReceivesInferredNullAndExplicitTypeBypassesIt() throws Exception
  {
    final List<String> bindings = new ArrayList<>();
    H2Adapter adapter = new H2Adapter()
    {
      @Override
      public void bindParameter(PreparedStatement stmt, int index, DBType type, Object value) throws SQLException
      {
        bindings.add(index + ":" + type + ":" + value);
        super.bindParameter(stmt, index, type, value);
      }
    };
    Schema schema = schema();
    Recorder inferredRecorder = new Recorder();
    SQL.selectAll().from(schema.table).where(schema.id).eq(SQL.param()).build().compile(adapter).bind(inferredRecorder.statement(), new Object[] { null });

    assertEquals(List.of("1:INTEGER:null"), bindings);
    assertEquals("setNull(1," + adapter.getJDBCTypeForNull(DBType.INTEGER) + ")", inferredRecorder.calls.get(0));

    Recorder explicitRecorder = new Recorder();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(Types.VARCHAR), schema.id)).build().compile(adapter).bind(explicitRecorder.statement(),
        new Object[] { null });
    assertEquals(List.of("1:INTEGER:null"), bindings);
    assertEquals("setNull(1," + Types.VARCHAR + ")", explicitRecorder.calls.get(0));
  }

  public void testDBAdapterProtectedBindingExtensionPoints() throws Exception
  {
    final List<String> extensions = new ArrayList<>();
    H2Adapter adapter = new H2Adapter()
    {
      @Override
      public boolean useJavaTypeForUnknownParameterType()
      {
        return true;
      }

      @Override
      protected void bindParameterBoolean(PreparedStatement stmt, int index, boolean value) throws SQLException
      {
        extensions.add("boolean:" + index + ":" + value);
        super.bindParameterBoolean(stmt, index, value);
      }

      @Override
      protected void bindParameterInt(PreparedStatement stmt, int index, int value) throws SQLException
      {
        extensions.add("int:" + index + ":" + value);
        super.bindParameterInt(stmt, index, value);
      }

      @Override
      protected void bindParameterObject(PreparedStatement stmt, int index, Object value) throws SQLException
      {
        extensions.add("object:" + index + ":" + value);
        super.bindParameterObject(stmt, index, value);
      }

      @Override
      protected void bindParameterNull(PreparedStatement stmt, int index, DBType type) throws SQLException
      {
        extensions.add("null:" + index + ":" + type);
        super.bindParameterNull(stmt, index, type);
      }
    };
    Recorder recorder = new Recorder();

    adapter.bindParameter(recorder.statement(), 1, DBType.BOOLEAN, Boolean.TRUE);
    adapter.bindParameter(recorder.statement(), 2, null, Boolean.FALSE);
    adapter.bindParameter(recorder.statement(), 3, DBType.INTEGER, Integer.valueOf(3));
    adapter.bindParameter(recorder.statement(), 4, DBType.INTEGER, Long.valueOf(4));
    adapter.bindParameter(recorder.statement(), 5, DBType.INTEGER, null);
    adapter.bindParameter(recorder.statement(), 6, null, null);
    adapter.bindParameter(recorder.statement(), 7, null, new Object());

    assertEquals(List.of("boolean:1:true", "boolean:2:false", "int:3:3", "object:4:4", "null:5:INTEGER", "object:6:null"), extensions.subList(0, 6));
    assertTrue(extensions.get(6).startsWith("object:7:"));
    assertEquals("setBoolean(1,true)", recorder.calls.get(0));
    assertEquals("setBoolean(2,false)", recorder.calls.get(1));
    assertEquals("setInt(3,3)", recorder.calls.get(2));
    assertEquals("setObject(4,4)", recorder.calls.get(3));
    assertTrue(recorder.calls.get(4).startsWith("setNull(5,"));
    assertEquals("setObject(6,null)", recorder.calls.get(5));
    assertTrue(recorder.calls.get(6).startsWith("setObject(7,"));
  }

  public void testDelegatingAdapterForwardsBindingPolicyAndOperation() throws Exception
  {
    final List<String> calls = new ArrayList<>();
    H2Adapter delegate = new H2Adapter()
    {
      @Override
      public void bindParameter(PreparedStatement stmt, int index, DBType type, Object value) throws SQLException
      {
        calls.add(index + ":" + type + ":" + value);
        stmt.setString(index, "delegated");
      }

      @Override
      public boolean useJavaTypeForUnknownParameterType()
      {
        return true;
      }
    };
    DelegatingDBAdapter adapter = new DelegatingDBAdapter(delegate);
    assertTrue(adapter.useJavaTypeForUnknownParameterType());
    Schema schema = schema();
    Recorder recorder = new Recorder();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.ONE)).build().compile(adapter).bind(recorder.statement(), "value");

    assertEquals(List.of("1:null:value"), calls);
    assertEquals("setString(1,delegated)", recorder.calls.get(0));

    calls.clear();
    SQLParameter configuredSetter = SQL.param((PreparedStatement stmt, int index, String value) -> stmt.setInt(index, value.length()));
    SQLStatement configuredStatement = SQL.selectAll().from(schema.table).where(SQL.eq(configuredSetter, SQL.ONE)).build().compile(adapter);
    Recorder configuredRecorder = new Recorder();
    configuredStatement.bind(configuredRecorder.statement(), "abc");
    assertTrue(calls.isEmpty());
    assertEquals("setInt(1,3)", configuredRecorder.calls.get(0));

    SQLStatement explicitStatement = SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.ONE)).build().compile(adapter);
    Recorder explicitRecorder = new Recorder();
    explicitStatement.bindParameter(explicitRecorder.statement(), 1, "x", (stmt, index, value) -> stmt.setBoolean(index, true));
    assertTrue(calls.isEmpty());
    assertEquals("setBoolean(1,true)", explicitRecorder.calls.get(0));
  }

  public void testCursorStateAndFailures() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param()).and(schema.name).eq(SQL.param()) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    SQLParameterBinder binder = statement.binderFrom(jdbc, 2);
    assertEquals(2, binder.getNextIndex());
    assertSame(binder, binder.bind("second"));
    assertEquals(3, binder.getNextIndex());
    assertSame(binder, binder.from(1));
    assertEquals(1, binder.getNextIndex());
    assertSame(binder, binder.from(2));
    assertEquals(2, binder.getNextIndex());
    assertSame(binder, binder.from(1));
    assertSame(binder, binder.bindParameter("first", String::toUpperCase));
    assertEquals(2, binder.getNextIndex());

    try
    {
      binder.bind("valid", "overflow");
      fail("Expected range validation");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(2, binder.getNextIndex());
    }

    try
    {
      binder.bindParameter("failure", value -> {
        throw new IllegalStateException("converter failure");
      });
      fail("Expected converter failure");
    }
    catch (IllegalStateException expected)
    {
      assertEquals("converter failure", expected.getMessage());
      assertEquals(2, binder.getNextIndex());
    }

    SQLParameterBinder independent = statement.binder(jdbc);
    assertEquals(1, independent.getNextIndex());
    assertEquals(2, binder.getNextIndex());

    assertSame(binder, binder.from(3).bind());
    assertEquals(3, binder.getNextIndex());
    try
    {
      binder.bind("past end");
      fail("Expected end-position validation");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(3, binder.getNextIndex());
    }

    try
    {
      binder.from(4);
      fail("Expected invalid cursor position");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(3, binder.getNextIndex());
    }

    assertSame(binder, binder.from(1));
    assertEquals(1, binder.getNextIndex());
    assertSame(binder, binder.bind(17));
    assertEquals(2, binder.getNextIndex());

    SQLParameter failingConverter = SQL.param((String value) -> {
      throw new IllegalStateException("later conversion failed");
    });
    SQLStatement partialStatement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param()).and(schema.name).eq(failingConverter) //
        .build().compile(new H2Adapter());
    Recorder partialRecorder = new Recorder();
    SQLParameterBinder partialBinder = partialStatement.binder(partialRecorder.statement());
    try
    {
      partialBinder.bind(11, "bad");
      fail("Expected later converter failure");
    }
    catch (IllegalStateException expected)
    {
      assertEquals("later conversion failed", expected.getMessage());
      assertEquals(1, partialBinder.getNextIndex());
      assertEquals(1, partialRecorder.calls.size());
      assertEquals("setInt(1,11)", partialRecorder.calls.get(0));
    }
  }

  public void testCursorActionsAndFailureState() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param()).and(schema.name).eq(SQL.param()).and(schema.id).eq(SQL.param()).and(schema.name).eq(SQL.param()) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    SQLParameterBinder binder = statement.binderFrom(recorder.statement(), 2);
    List<Integer> actionIndices = new ArrayList<>();

    assertSame(binder, binder.bindParameter((stmt, index) -> {
      actionIndices.add(index);
      stmt.setNull(index, Types.VARCHAR);
    }));
    assertEquals(List.of(2), actionIndices);
    assertEquals(3, binder.getNextIndex());
    assertEquals("setNull(2,12)", recorder.calls.get(0));

    assertSame(binder, binder.bind("middle").bindParameter((stmt, index) -> {
      actionIndices.add(index);
      stmt.setString(index, "default");
    }));
    assertEquals(5, binder.getNextIndex());
    assertEquals(List.of(2, 4), actionIndices);
    assertEquals("setObject(3,middle)", recorder.calls.get(1));
    assertEquals("setString(4,default)", recorder.calls.get(2));

    binder.from(5);
    try
    {
      binder.bindParameter((stmt, index) -> fail("Action must not run beyond the final parameter"));
      fail("Expected end-position validation");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(5, binder.getNextIndex());
      assertEquals(2, actionIndices.size());
    }

    binder.from(3);
    try
    {
      binder.bindParameter((stmt, index) -> {
        throw new SQLException("action failure");
      });
      fail("Expected action failure");
    }
    catch (SQLException expected)
    {
      assertEquals("action failure", expected.getMessage());
      assertEquals(3, binder.getNextIndex());
    }

    try
    {
      binder.bindParameter((SQLDefaultSetter)null);
      fail("Expected null action validation");
    }
    catch (NullPointerException expected)
    {
      assertEquals(3, binder.getNextIndex());
    }
  }

  public void testCursorSkipMovesWithoutBindingAndValidatesAtomically() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param()).and(schema.name).eq(SQL.param()).and(schema.id).eq(SQL.param()) //
        .and(schema.name).eq(SQL.param()).and(schema.id).eq(SQL.param()).and(schema.name).eq(SQL.param()).and(schema.id).eq(SQL.param()) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    SQLParameterBinder binder = statement.binder(jdbc);

    assertSame(binder, binder.skip());
    assertEquals(2, binder.getNextIndex());
    assertSame(binder, binder.bind("two"));
    assertEquals(3, binder.getNextIndex());
    assertSame(binder, binder.skip(0));
    assertSame(binder, binder.skip(2));
    assertEquals(5, binder.getNextIndex());
    assertEquals(1, recorder.calls.size());

    assertSame(binder, binder.bindParameter((stmt, index) -> stmt.setNull(index, Types.VARCHAR)));
    assertEquals(6, binder.getNextIndex());
    assertSame(binder, binder.skip());
    assertEquals(7, binder.getNextIndex());
    assertSame(binder, binder.bindParameter("last", (stmt, index, value) -> stmt.setString(index, value)));
    assertEquals(8, binder.getNextIndex());
    assertEquals("setString(2,two)", recorder.calls.get(0));
    assertEquals("setNull(5,12)", recorder.calls.get(1));
    assertEquals("setString(7,last)", recorder.calls.get(2));

    SQLParameterBinder independent = statement.binder(jdbc);
    assertEquals(1, independent.getNextIndex());
    assertEquals(8, binder.getNextIndex());
    int callCount = recorder.calls.size();

    assertSame(binder, binder.from(4).skip(1));
    assertEquals(5, binder.getNextIndex());
    assertEquals(callCount, recorder.calls.size());
    assertSame(binder, binder.from(6).skip(2));
    assertEquals(8, binder.getNextIndex());
    assertEquals(callCount, recorder.calls.size());
    assertSame(binder, binder.from(8).skip(0));
    assertEquals(8, binder.getNextIndex());

    for (int count : new int[] { -1, 1, 2, Integer.MAX_VALUE })
    {
      try
      {
        binder.skip(count);
        fail("Expected invalid skip count " + count);
      }
      catch (IllegalArgumentException expected)
      {
        assertEquals(8, binder.getNextIndex());
        assertEquals(callCount, recorder.calls.size());
      }
    }

    try
    {
      binder.skip();
      fail("Expected skip beyond the final cursor position");
    }
    catch (IllegalArgumentException expected)
    {
      assertEquals(8, binder.getNextIndex());
    }
  }

  public void testBindDefaultPositionalValidationAndOccurrenceIdentity() throws Exception
  {
    Schema schema = schema();
    SQLParameter repeated = SQL.param((PreparedStatement stmt, int index, Object value) -> fail("Default binding must not invoke the configured value setter"));
    SQLParameter distinct = SQL.param();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(repeated).or(schema.id).eq(distinct).or(schema.id).eq(repeated) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    List<Integer> indices = new ArrayList<>();
    SQLDefaultSetter setter = (stmt, index) -> {
      indices.add(index);
      stmt.setString(index, "default-" + index);
    };

    statement.bindDefault(jdbc, 1, setter);
    statement.bindDefault(jdbc, 3, setter);
    assertEquals(List.of(1, 3), indices);
    assertEquals("setString(1,default-1)", recorder.calls.get(0));
    assertEquals("setString(3,default-3)", recorder.calls.get(1));

    indices.clear();
    statement.bindDefault(jdbc, repeated, setter);
    assertEquals(List.of(1, 3), indices);
    indices.clear();
    statement.bindDefault(jdbc, distinct, setter);
    assertEquals(List.of(2), indices);

    indices.clear();
    try
    {
      statement.bindDefault(jdbc, SQL.param(), setter);
      fail("Expected absent identity validation");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(indices.isEmpty());
    }

    for (int index : new int[] { 0, 4 })
    {
      try
      {
        statement.bindDefault(jdbc, index, setter);
        fail("Expected invalid positional index " + index);
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(indices.isEmpty());
      }
    }

    try
    {
      statement.bindDefault(null, 1, setter);
      fail("Expected null statement validation");
    }
    catch (NullPointerException expected)
    {
      assertTrue(indices.isEmpty());
    }

    try
    {
      statement.bindDefault(jdbc, 1, (SQLDefaultSetter)null);
      fail("Expected null setter validation");
    }
    catch (NullPointerException expected)
    {
      assertTrue(indices.isEmpty());
    }

    try
    {
      statement.bindDefault(jdbc, (SQLParameter)null, setter);
      fail("Expected null parameter validation");
    }
    catch (NullPointerException expected)
    {
      assertTrue(indices.isEmpty());
    }
  }

  public void testBindDefaultIdentityPreservesEarlierBindingsOnFailure() throws Exception
  {
    Schema schema = schema();
    SQLParameter repeated = SQL.param();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(repeated).or(schema.name).eq(SQL.param()).or(schema.id).eq(repeated) //
        .build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    List<Integer> attemptedIndices = new ArrayList<>();

    try
    {
      statement.bindDefault(recorder.statement(), repeated, (stmt, index) -> {
        attemptedIndices.add(index);
        stmt.setInt(index, index);
        if (index == 3)
        {
          throw new SQLException("later default failure");
        }
      });
      fail("Expected later default setter failure");
    }
    catch (SQLException expected)
    {
      assertEquals("later default failure", expected.getMessage());
      assertEquals(List.of(1, 3), attemptedIndices);
      assertEquals("setInt(1,1)", recorder.calls.get(0));
      assertEquals("setInt(3,3)", recorder.calls.get(1));
    }
  }

  public void testParameterAccessorsPreserveOccurrenceOrderAndIdentity()
  {
    Schema schema = schema();
    SQLParameter repeated = SQL.param();
    SQLParameter distinct = SQL.param();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(repeated).or(schema.name).eq(distinct).or(schema.id).eq(repeated) //
        .build() //
        .compile(new H2Adapter());

    assertEquals(3, statement.getParameterCount());
    assertSame(repeated, statement.getParameter(1));
    assertSame(distinct, statement.getParameter(2));
    assertSame(repeated, statement.getParameter(3));
    assertEquals(3, statement.getParameters().size());
    assertSame(repeated, statement.getParameters().get(0));
    assertSame(distinct, statement.getParameters().get(1));
    assertSame(repeated, statement.getParameters().get(2));

    try
    {
      statement.getParameters().clear();
      fail("Expected an immutable parameter list");
    }
    catch (UnsupportedOperationException expected)
    {
      // Expected.
    }
  }

  public void testParameterAccessorsForNoParametersAndInvalidIndices()
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .build() //
        .compile(new H2Adapter());

    assertTrue(statement.getParameters().isEmpty());

    for (int index : new int[] { 0, -1, 1 })
    {
      try
      {
        statement.getParameter(index);
        fail("Expected invalid index failure for " + index);
      }
      catch (IllegalArgumentException expected)
      {
        assertTrue(expected.getMessage().contains("outside"));
      }
    }
  }

  public void testZeroParametersInvalidIndicesAndAbsentIdentity() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .build() //
        .compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    statement.bind(jdbc);
    assertTrue(recorder.calls.isEmpty());

    try
    {
      statement.bindParameter(jdbc, 1, "invalid");
      fail("Expected invalid index failure");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("outside"));
      assertTrue(recorder.calls.isEmpty());
    }

    SQLParameter absent = SQL.param();
    try
    {
      statement.bindParameter(jdbc, absent, "absent");
      fail("Expected absent identity failure");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("not present"));
      assertTrue(recorder.calls.isEmpty());
    }
  }

  public void testTypedAndInferredNullAndUnknownNull() throws Exception
  {
    Schema schema = schema();
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param()) //
        .build() //
        .compile(new H2Adapter()).bind(jdbc, new Object[] { null });
    assertTrue(recorder.calls.get(0).startsWith("setNull(1,"));

    recorder.calls.clear();
    SQL.selectAll() //
        .from(schema.table) //
        .where(SQL.eq(SQL.param(Types.VARCHAR), SQL.ONE)) //
        .build() //
        .compile(new H2Adapter()).bind(jdbc, new Object[] { null });
    assertEquals("setNull(1,12)", recorder.calls.get(0));

    recorder.calls.clear();
    SQL.selectAll() //
        .from(schema.table) //
        .where(SQL.eq(SQL.param(Types.INTEGER), SQL.ONE)) //
        .build() //
        .compile(new H2Adapter()).bind(jdbc, 9);
    assertEquals("setObject(1,9,4)", recorder.calls.get(0));

    recorder.calls.clear();
    try
    {
      SQL.selectAll() //
          .from(schema.table) //
          .where(SQL.eq(SQL.param(), SQL.ONE)) //
          .build() //
          .compile(new H2Adapter()).bind(jdbc, new Object[] { null });
      fail("Expected unresolved NULL type failure");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Cannot bind NULL"));
      assertTrue(recorder.calls.isEmpty());
    }
  }

  public void testComparisonFieldsInferNullTypeInEitherOperandDirection() throws Exception
  {
    Schema schema = schema();
    H2Adapter adapter = new H2Adapter();
    int expectedType = adapter.getJDBCTypeForNull(schema.id.getType());
    SQLParameter rightParameter = SQL.param();
    SQLParameter leftParameter = SQL.param();
    Recorder rightRecorder = new Recorder();
    Recorder leftRecorder = new Recorder();

    SQLCondition fieldOnLeft = SQL.eq(schema.id, rightParameter);
    SQLCondition fieldOnRight = SQL.eq(leftParameter, schema.id);
    SQL.selectAll() //
        .from(schema.table) //
        .where(fieldOnLeft) //
        .build() //
        .compile(adapter).bind(rightRecorder.statement(), new Object[] { null });
    SQL.selectAll() //
        .from(schema.table) //
        .where(fieldOnRight) //
        .build() //
        .compile(adapter).bind(leftRecorder.statement(), new Object[] { null });

    assertEquals("setNull(1," + expectedType + ")", rightRecorder.calls.get(0));
    assertEquals("setNull(1," + expectedType + ")", leftRecorder.calls.get(0));

    Recorder explicitRecorder = new Recorder();
    SQLCondition explicitType = SQL.eq(SQL.param(Types.VARCHAR), schema.id);
    SQL.selectAll() //
        .from(schema.table) //
        .where(explicitType) //
        .build() //
        .compile(adapter).bind(explicitRecorder.statement(), new Object[] { null });
    assertEquals("setNull(1," + Types.VARCHAR + ")", explicitRecorder.calls.get(0));
  }

  public void testRepeatedIdentityKeepsEachFieldType() throws Exception
  {
    Schema schema = schema();
    H2Adapter adapter = new H2Adapter();
    SQLParameter shared = SQL.param();
    SQLCondition condition = SQL.eq(schema.name, shared) //
        .and(SQL.eq(shared, schema.id));
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(condition) //
        .build() //
        .compile(adapter);
    Recorder recorder = new Recorder();

    statement.bindParameter(recorder.statement(), shared, null);

    assertEquals("setNull(1," + adapter.getJDBCTypeForNull(DBType.VARCHAR) + ")", recorder.calls.get(0));
    assertEquals("setNull(2," + adapter.getJDBCTypeForNull(DBType.INTEGER) + ")", recorder.calls.get(1));

    recorder.calls.clear();
    statement.bindParameter(recorder.statement(), shared, "42");
    assertEquals("setString(1,42)", recorder.calls.get(0));
    assertEquals("setObject(2,42)", recorder.calls.get(1));
  }

  public void testRepeatedIdentityConvertsOncePerOccurrenceWithoutCaching() throws Exception
  {
    Schema schema = schema();
    final int[] conversionCount = { 0 };
    SQLParameter shared = SQL.param((String value) -> value + "-" + ++conversionCount[0]);
    SQLCondition condition = SQL.eq(schema.name, shared) //
        .or(SQL.eq(schema.name, shared));
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(condition) //
        .build() //
        .compile(new H2Adapter());
    Recorder recorder = new Recorder();

    statement.bindParameter(recorder.statement(), shared, "first");
    statement.bindParameter(recorder.statement(), shared, "second");

    assertEquals(4, conversionCount[0]);
    assertEquals("setString(1,first-1)", recorder.calls.get(0));
    assertEquals("setString(2,first-2)", recorder.calls.get(1));
    assertEquals("setString(1,second-3)", recorder.calls.get(2));
    assertEquals("setString(2,second-4)", recorder.calls.get(3));
  }

  public void testConverterAndSpecializedBinderPrecedenceIncludingNull() throws Exception
  {
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    Schema schema = schema();
    SQLParameter converted = SQL.param((String value) -> value.toUpperCase());
    SQL.selectAll() //
        .from(schema.table) //
        .where(schema.name).eq(converted) //
        .build() //
        .compile(new H2Adapter()).bind(jdbc, "hello");
    assertEquals("setString(1,HELLO)", recorder.calls.get(0));

    recorder.calls.clear();
    SQLStatement nullConverter = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(SQL.param(Types.INTEGER, value -> null)) //
        .build().compile(new H2Adapter());
    nullConverter.bind(jdbc, 3);
    assertEquals("setNull(1,4)", recorder.calls.get(0));

    recorder.calls.clear();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.name).eq(converted) //
        .build() //
        .compile(new H2Adapter());
    statement.bindParameter(jdbc, 1, (String)null, (s, index, value) -> recorder.calls.add("custom(" + index + "," + value + ")"));
    assertEquals("custom(1,null)", recorder.calls.get(0));

    recorder.calls.clear();
    try
    {
      statement.bindParameter(jdbc, 1, "x", (s, index, value) -> {
        throw new SQLException("binder failure");
      });
      fail("Expected binder SQLException");
    }
    catch (SQLException expected)
    {
      assertEquals("binder failure", expected.getMessage());
    }
  }

  public void testCompiledStatementCanBindDifferentStatementsAndValues() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll() //
        .from(schema.table) //
        .where(schema.name).eq(SQL.param()) //
        .build() //
        .compile(new H2Adapter());
    Recorder first = new Recorder();
    Recorder second = new Recorder();
    statement.bind(first.statement(), "first");
    statement.bind(second.statement(), "second");
    assertEquals("setString(1,first)", first.calls.get(0));
    assertEquals("setString(1,second)", second.calls.get(0));
  }

  public void testH2RoundTripThroughNet4jConnection() throws Exception
  {
    IDBAdapter adapter = new H2Adapter();
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:dml_phase3;DB_CLOSE_DELAY=-1");
    IDBDatabase database = DBUtil.openDatabase(adapter, adapter.createConnectionProvider(dataSource), "PUBLIC");
    IDBConnection connection = database.getConnection();
    IDBTable table = DBUtil.createSchema("PUBLIC", false, false).addTable("DML_PHASE3");
    IDBField id = table.addField("ID", DBType.INTEGER);
    IDBField name = table.addField("NAME", DBType.VARCHAR);
    IDBTable copy = DBUtil.createSchema("PUBLIC", false, false).addTable("DML_PHASE3_COPY");
    IDBField copyID = copy.addField("ID", DBType.INTEGER);
    IDBField copyName = copy.addField("NAME", DBType.VARCHAR);

    try
    {
      try (Statement createTable = connection.createStatement())
      {
        createTable.executeUpdate("CREATE TABLE DML_PHASE3 (ID INTEGER PRIMARY KEY, NAME VARCHAR(40))");
        createTable.executeUpdate("CREATE TABLE DML_PHASE3_COPY (ID INTEGER PRIMARY KEY, NAME VARCHAR(40))");
      }

      SQLStatement insert = SQL.insertInto(table) //
          .columns(id, name) //
          .values(SQL.param(), SQL.param()) //
          .build() //
          .compile(adapter);
      IDBPreparedStatement insertStatement = insert.prepare(connection, ReuseProbability.LOW);
      try
      {
        insert.bind(insertStatement, 17, "net4j");
        assertEquals(1, insertStatement.executeUpdate());
      }
      finally
      {
        insertStatement.close();
      }

      SQLStatement insertSelect = SQL.insertInto(copy) //
          .columns(copyID, copyName) //
          .select(SQL.select(id, name) //
              .from(table) //
              .where(id).eq(SQL.param()) //
              .subSelect())
          .build().compile(adapter);
      IDBPreparedStatement insertSelectStatement = insertSelect.prepare(connection, ReuseProbability.LOW);
      try
      {
        insertSelect.bind(insertSelectStatement, 17);
        assertEquals(1, insertSelectStatement.executeUpdate());
      }
      finally
      {
        insertSelectStatement.close();
      }

      SQLStatement select = SQL.select(copyName) //
          .from(copy) //
          .where(copyID).eq(SQL.param()) //
          .build() //
          .compile(adapter);
      IDBPreparedStatement selectStatement = select.prepare(connection, ReuseProbability.LOW);
      try
      {
        select.bind(selectStatement, 17);
        ResultSet resultSet = selectStatement.executeQuery();
        try
        {
          assertTrue(resultSet.next());
          assertEquals("net4j", resultSet.getString(1));
          assertFalse(resultSet.next());
        }
        finally
        {
          resultSet.close();
        }
      }
      finally
      {
        selectStatement.close();
      }

      SQLParameter repeatedID = SQL.param();
      SQLStatement selectRepeated = SQL.select(copyName) //
          .from(copy) //
          .where(SQL.eq(copyID, repeatedID).or(SQL.eq(copyID, repeatedID))) //
          .build().compile(adapter);
      IDBPreparedStatement repeatedStatement = selectRepeated.prepare(connection, ReuseProbability.LOW);
      try
      {
        selectRepeated.bindDefault(repeatedStatement, repeatedID, (stmt, index) -> stmt.setInt(index, 17));
        ResultSet resultSet = repeatedStatement.executeQuery();
        try
        {
          assertTrue(resultSet.next());
          assertEquals("net4j", resultSet.getString(1));
          assertFalse(resultSet.next());
        }
        finally
        {
          resultSet.close();
        }
      }
      finally
      {
        repeatedStatement.close();
      }
    }
    finally
    {
      database.close();
    }
  }

  private static Schema schema()
  {
    IDBTable table = DBUtil.createSchema("binding", false, false).addTable("item");
    return new Schema(table, table.addField("id", DBType.INTEGER), table.addField("name", DBType.VARCHAR));
  }

  private static <T> T proxy(Class<T> type, InvocationHandler handler)
  {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler));
  }

  private static Object defaultValue(Class<?> type)
  {
    if (!type.isPrimitive() || type == Void.TYPE)
    {
      return null;
    }

    if (type == Boolean.TYPE)
    {
      return false;
    }

    if (type == Character.TYPE)
    {
      return '\0';
    }

    return 0;
  }

  /**
   * Holds the fields used in one binding test.
   *
   * @author Eike Stepper
   */
  private static final class Schema
  {
    private final IDBTable table;

    private final IDBField id;

    private final IDBField name;

    private Schema(IDBTable table, IDBField id, IDBField name)
    {
      this.table = table;
      this.id = id;
      this.name = name;
    }
  }

  /**
   * Records JDBC setter method names and arguments.
   *
   * @author Eike Stepper
   */
  private static final class Recorder implements InvocationHandler
  {
    private final List<String> calls = new ArrayList<>();

    private PreparedStatement statement()
    {
      return proxy(PreparedStatement.class, this);
    }

    @Override
    public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args)
    {
      if (method.getName().startsWith("set"))
      {
        calls.add(method.getName() + "(" + args[0] + "," + args[1] + (args.length > 2 ? "," + args[2] : "") + ")");
      }

      return defaultValue(method.getReturnType());
    }
  }
}
