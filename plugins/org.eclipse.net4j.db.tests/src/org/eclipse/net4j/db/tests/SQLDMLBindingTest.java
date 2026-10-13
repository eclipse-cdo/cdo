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
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.h2.H2Adapter;

import org.h2.jdbcx.JdbcDataSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
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

    SQLStatement statement = SQL.select(SQL.literal(1)).from(schema.table).build().compile(new H2Adapter());
    IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH);
    assertSame(expected, prepared);
    assertEquals(sql, arguments[0]);
    assertSame(ReuseProbability.HIGH, arguments[1]);
  }

  public void testPositionalAndIdentityBindingAndCountValidation() throws Exception
  {
    Schema schema = schema();
    SQLParameter repeated = SQL.param();
    SQLStatement statement = SQL.selectAll().from(schema.table).where(schema.id).eq(repeated).or(schema.name).eq(repeated).build().compile(new H2Adapter());
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();

    statement.bind(jdbc, 7, "seven");
    assertEquals("setObject(1,7)", recorder.calls.get(0));
    assertEquals("setObject(2,seven)", recorder.calls.get(1));
    recorder.calls.clear();
    statement.bindParameter(jdbc, repeated, 42);
    assertEquals("setObject(1,42)", recorder.calls.get(0));
    assertEquals("setObject(2,42)", recorder.calls.get(1));

    recorder.calls.clear();
    statement.bindParameter(jdbc, repeated, null, (s, index, value) -> recorder.calls.add("custom(" + index + "," + value + ")"));
    assertEquals("custom(1,null)", recorder.calls.get(0));
    assertEquals("custom(2,null)", recorder.calls.get(1));

    recorder.calls.clear();
    try
    {
      statement.bind(jdbc, 1);
      fail("Expected a positional value-count failure");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("Expected 2"));
      assertTrue(recorder.calls.isEmpty());
    }
  }

  public void testZeroParametersInvalidIndicesAndAbsentIdentity() throws Exception
  {
    Schema schema = schema();
    SQLStatement statement = SQL.selectAll().from(schema.table).build().compile(new H2Adapter());
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
    SQL.selectAll().from(schema.table).where(schema.id).eq(SQL.param()).build().compile(new H2Adapter()).bind(jdbc, new Object[] { null });
    assertTrue(recorder.calls.get(0).startsWith("setNull(1,"));

    recorder.calls.clear();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(Types.VARCHAR), SQL.literal(1))).build().compile(new H2Adapter()).bind(jdbc,
        new Object[] { null });
    assertEquals("setNull(1,12)", recorder.calls.get(0));

    recorder.calls.clear();
    SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(Types.INTEGER), SQL.literal(1))).build().compile(new H2Adapter()).bind(jdbc, 9);
    assertEquals("setObject(1,9,4)", recorder.calls.get(0));

    recorder.calls.clear();
    try
    {
      SQL.selectAll().from(schema.table).where(SQL.eq(SQL.param(), SQL.literal(1))).build().compile(new H2Adapter()).bind(jdbc, new Object[] { null });
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
    SQL.selectAll().from(schema.table).where(fieldOnLeft).build().compile(adapter).bind(rightRecorder.statement(), new Object[] { null });
    SQL.selectAll().from(schema.table).where(fieldOnRight).build().compile(adapter).bind(leftRecorder.statement(), new Object[] { null });

    assertEquals("setNull(1," + expectedType + ")", rightRecorder.calls.get(0));
    assertEquals("setNull(1," + expectedType + ")", leftRecorder.calls.get(0));

    Recorder explicitRecorder = new Recorder();
    SQLCondition explicitType = SQL.eq(SQL.param(Types.VARCHAR), schema.id);
    SQL.selectAll().from(schema.table).where(explicitType).build().compile(adapter).bind(explicitRecorder.statement(), new Object[] { null });
    assertEquals("setNull(1," + Types.VARCHAR + ")", explicitRecorder.calls.get(0));
  }

  public void testRepeatedIdentityKeepsEachFieldType() throws Exception
  {
    Schema schema = schema();
    H2Adapter adapter = new H2Adapter();
    SQLParameter shared = SQL.param();
    SQLCondition condition = SQL.eq(schema.name, shared) //
        .and(SQL.eq(shared, schema.id));
    SQLStatement statement = SQL.selectAll().from(schema.table).where(condition).build().compile(adapter);
    Recorder recorder = new Recorder();

    statement.bindParameter(recorder.statement(), shared, null);

    assertEquals("setNull(1," + adapter.getJDBCTypeForNull(DBType.VARCHAR) + ")", recorder.calls.get(0));
    assertEquals("setNull(2," + adapter.getJDBCTypeForNull(DBType.INTEGER) + ")", recorder.calls.get(1));
  }

  public void testRepeatedIdentityConvertsOncePerOccurrenceWithoutCaching() throws Exception
  {
    Schema schema = schema();
    final int[] conversionCount = { 0 };
    SQLParameter shared = SQL.param((String value) -> value + "-" + ++conversionCount[0]);
    SQLCondition condition = SQL.eq(schema.name, shared) //
        .or(SQL.eq(schema.name, shared));
    SQLStatement statement = SQL.selectAll().from(schema.table).where(condition).build().compile(new H2Adapter());
    Recorder recorder = new Recorder();

    statement.bindParameter(recorder.statement(), shared, "first");
    statement.bindParameter(recorder.statement(), shared, "second");

    assertEquals(4, conversionCount[0]);
    assertEquals("setObject(1,first-1)", recorder.calls.get(0));
    assertEquals("setObject(2,first-2)", recorder.calls.get(1));
    assertEquals("setObject(1,second-3)", recorder.calls.get(2));
    assertEquals("setObject(2,second-4)", recorder.calls.get(3));
  }

  public void testConverterAndSpecializedBinderPrecedenceIncludingNull() throws Exception
  {
    Recorder recorder = new Recorder();
    PreparedStatement jdbc = recorder.statement();
    Schema schema = schema();
    SQLParameter converted = SQL.param((String value) -> value.toUpperCase());
    SQL.selectAll().from(schema.table).where(schema.name).eq(converted).build().compile(new H2Adapter()).bind(jdbc, "hello");
    assertEquals("setObject(1,HELLO)", recorder.calls.get(0));

    recorder.calls.clear();
    SQLStatement nullConverter = SQL.selectAll().from(schema.table).where(schema.id).eq(SQL.param(Types.INTEGER, value -> null)).build()
        .compile(new H2Adapter());
    nullConverter.bind(jdbc, 3);
    assertEquals("setNull(1,4)", recorder.calls.get(0));

    recorder.calls.clear();
    SQLStatement statement = SQL.selectAll().from(schema.table).where(schema.name).eq(converted).build().compile(new H2Adapter());
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
    SQLStatement statement = SQL.selectAll().from(schema.table).where(schema.name).eq(SQL.param()).build().compile(new H2Adapter());
    Recorder first = new Recorder();
    Recorder second = new Recorder();
    statement.bind(first.statement(), "first");
    statement.bind(second.statement(), "second");
    assertEquals("setObject(1,first)", first.calls.get(0));
    assertEquals("setObject(1,second)", second.calls.get(0));
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

    try
    {
      try (Statement createTable = connection.createStatement())
      {
        createTable.executeUpdate("CREATE TABLE DML_PHASE3 (ID INTEGER PRIMARY KEY, NAME VARCHAR(40))");
      }

      SQLStatement insert = SQL.insertInto(table).columns(id, name).values(SQL.param(), SQL.param()).build().compile(adapter);
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

      SQLStatement select = SQL.select(name).from(table).where(id).eq(SQL.param()).build().compile(adapter);
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
