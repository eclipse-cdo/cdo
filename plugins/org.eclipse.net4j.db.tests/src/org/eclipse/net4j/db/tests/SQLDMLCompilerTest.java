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
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.SQLTemplate;
import org.eclipse.net4j.db.dml.fluent.SQLSelectFromStep;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.db.mysql.MYSQLAdapter;
import org.eclipse.net4j.internal.db.dml.CompiledStatement;
import org.eclipse.net4j.internal.db.dml.Definition;
import org.eclipse.net4j.internal.db.dml.Fragment;
import org.eclipse.net4j.spi.db.DelegatingDBAdapter;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * Verifies adapter-specific DML compilation without opening a database connection.
 *
 * @author Eike Stepper
 */
public class SQLDMLCompilerTest extends TestCase
{
  private final IDBAdapter ansiAdapter = new H2Adapter();

  public void testSelectInsertUpdateAndDelete()
  {
    Schema schema = schema("sales", "people", false);
    SQLParameter idParameter = SQL.param();
    SQLTemplate select = SQL.select(schema.id, schema.name) //
        .from(schema.table) //
        .where(schema.id).eq(idParameter) //
        .build();

    SQLStatement statement = select.compile(ansiAdapter);
    assertEquals("SELECT \"id\", \"name\" FROM \"people\" WHERE \"id\" = ?", statement.getSQL());
    assertEquals(1, statement.getParameterCount());
    assertSame(idParameter, occurrences(statement).get(0));

    SQLStatement implicitInsert = SQL.insertInto(schema.table).values(SQL.param(), SQL.literal("Ada")).build().compile(ansiAdapter);
    assertEquals("INSERT INTO \"people\" VALUES (?, 'Ada')", implicitInsert.getSQL());
    assertEquals(1, implicitInsert.getParameterCount());
    assertEquals("INSERT INTO \"people\" (\"id\", \"name\") VALUES (?, ?)",
        SQL.insertInto(schema.table).columns(schema.id, schema.name).values(SQL.param(), SQL.param()).build().compile(ansiAdapter).getSQL());
    assertEquals("INSERT INTO \"people\" (\"name\", \"id\") VALUES (?, ?)",
        SQL.insertInto(schema.table).set(schema.name).to(SQL.param()).set(schema.id).to(SQL.param()).build().compile(ansiAdapter).getSQL());

    assertEquals("UPDATE \"people\" SET \"name\" = ?, \"id\" = \"id\" + 1 WHERE \"id\" = ?", SQL.update(schema.table).set(schema.name).to(SQL.param())
        .set(schema.id).to(SQL.add(schema.id, SQL.literal(1))).where(schema.id).eq(SQL.param()).build().compile(ansiAdapter).getSQL());
    assertEquals("DELETE FROM \"people\" WHERE \"id\" = ?",
        SQL.deleteFrom(schema.table).where(schema.id).eq(SQL.param()).build().compile(ansiAdapter).getSQL());
    assertEquals("DELETE FROM \"people\"", SQL.deleteFrom(schema.table).build().compile(ansiAdapter).getSQL());
  }

  public void testDistinctAliasesQualifiedColumnsAndWildcards()
  {
    Schema schema = schema("hr", "employees", true);
    SQLTableRef employee = schema.table.as("e");
    SQLTemplate template = SQL.selectDistinct(SQL.all(employee), SQL.count(schema.id).as("total")) //
        .from(employee) //
        .build();

    assertEquals("SELECT DISTINCT \"e\".*, COUNT(\"id\") AS \"total\" FROM \"hr\".\"employees\" AS \"e\"", template.compile(ansiAdapter).getSQL());
    assertEquals("SELECT * FROM \"hr\".\"employees\"", SQL.selectAll().from(schema.table).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT \"e\".\"id\" FROM \"hr\".\"employees\" AS \"e\"",
        SQL.select(employee.column(schema.id)).from(employee).build().compile(ansiAdapter).getSQL());
  }

  public void testJoinsRespectSourceAndFluentOrder()
  {
    Schema schema = schema("joins", "a", true);
    IDBField firstID = schema.id;
    IDBTable second = schema.schema.addTable("b");
    IDBTable third = schema.schema.addTable("c");
    IDBField thirdID = third.addField("id", DBType.INTEGER);
    IDBTable fourth = schema.schema.addTable("d");
    IDBField fourthID = fourth.addField("id", DBType.INTEGER);
    SQLTableRef a = schema.table.as("a");
    SQLTableRef b = second.as("b");
    SQLTableRef c = third.as("c");
    SQLTableRef d = fourth.as("d");

    SQLTemplate template = SQL.selectAll() //
        .from(a) //
        .from(b) //
        .join(c).on(a.column(firstID)).eq(c.column(thirdID)) //
        .leftJoin(d).on(d.column(fourthID)).eq(c.column(thirdID)) //
        .build();

    assertEquals(
        "SELECT * FROM (\"joins\".\"a\" AS \"a\" CROSS JOIN \"joins\".\"b\" AS \"b\") INNER JOIN \"joins\".\"c\" AS \"c\" ON \"a\".\"id\" = \"c\".\"id\" LEFT JOIN \"joins\".\"d\" AS \"d\" ON \"d\".\"id\" = \"c\".\"id\"",
        template.compile(ansiAdapter).getSQL());
  }

  public void testConditionAndArithmeticPrecedence()
  {
    Schema schema = schema("logic", "items", false);
    SQLParameter first = SQL.param();
    SQLParameter second = SQL.param();
    SQLParameter third = SQL.param();
    SQLTemplate template = SQL.selectAll().from(schema.table).where(SQL.eq(schema.id, first).or(SQL.eq(schema.name, second)))
        .and(SQL.eq(SQL.multiply(SQL.add(schema.id, SQL.literal(2)), schema.id), third)).build();

    assertEquals("SELECT * FROM \"items\" WHERE (\"id\" = ? OR \"name\" = ?) AND (\"id\" + 2) * \"id\" = ?", template.compile(ansiAdapter).getSQL());
    assertEquals(Arrays.asList(first, second, third), occurrences(template.compile(ansiAdapter)));

    SQLCondition nested = SQL.not(SQL.eq(schema.id, first).or(SQL.isNull(schema.name)));
    assertEquals("SELECT * FROM \"items\" WHERE NOT (\"id\" = ? OR \"name\" IS NULL)",
        SQL.selectAll().from(schema.table).where(nested).build().compile(ansiAdapter).getSQL());
  }

  public void testGroupingAggregatesAndOrdering()
  {
    Schema schema = schema("report", "events", false);
    SQLTemplate template = SQL.select(schema.name, SQL.count().as("event_count")) //
        .from(schema.table) //
        .groupBy(schema.name, schema.id) //
        .having(SQL.count(schema.id)).gt(SQL.param()) //
        .orderBy(SQL.asc(schema.name), SQL.desc(SQL.sum(schema.id))) //
        .thenByDesc(SQL.max(schema.id)) //
        .build();

    assertEquals(
        "SELECT \"name\", COUNT(*) AS \"event_count\" FROM \"events\" GROUP BY \"name\", \"id\" HAVING COUNT(\"id\") > ? ORDER BY \"name\" ASC, SUM(\"id\") DESC, MAX(\"id\") DESC",
        template.compile(ansiAdapter).getSQL());
  }

  public void testExpressionAndConditionFactories()
  {
    Schema schema = schema("operators", "values", false);
    SQLParameter parameter = SQL.param();
    SQLSubSelect subquery = SQL.select(schema.id).from(schema.table).subSelect();
    SQLTemplate aggregates = SQL
        .select(SQL.countDistinct(schema.id), SQL.min(schema.id), SQL.max(schema.id), SQL.sum(schema.id), SQL.avg(schema.id),
            SQL.subtract(schema.id, SQL.literal(1)), SQL.divide(schema.id, SQL.literal(2))) //
        .from(schema.table) //
        .build();

    assertEquals("SELECT COUNT(DISTINCT \"id\"), MIN(\"id\"), MAX(\"id\"), SUM(\"id\"), AVG(\"id\"), \"id\" - 1, \"id\" / 2 FROM \"values\"",
        aggregates.compile(ansiAdapter).getSQL());

    SQLCondition predicates = SQL.and(SQL.ne(schema.id, parameter), SQL.or(SQL.lt(schema.id, parameter), SQL.le(schema.id, parameter)))
        .and(SQL.gt(schema.id, parameter)).and(SQL.ge(schema.id, parameter)).and(SQL.like(schema.name, SQL.literal("A%")))
        .and(SQL.between(schema.id, SQL.literal(1), SQL.literal(9))).and(SQL.isNull(schema.name).not()).and(SQL.isNotNull(schema.id))
        .and(SQL.in(schema.id, subquery));
    SQLStatement statement = SQL.selectAll().from(schema.table).where(predicates).build().compile(ansiAdapter);

    assertEquals(
        "SELECT * FROM \"values\" WHERE (\"id\" <> ? AND (\"id\" < ? OR \"id\" <= ?) AND \"id\" > ? AND \"id\" >= ? AND \"name\" LIKE 'A%' AND \"id\" BETWEEN 1 AND 9 AND NOT \"name\" IS NULL AND \"id\" IS NOT NULL AND \"id\" IN (SELECT \"id\" FROM \"values\"))",
        statement.getSQL());
    assertEquals(Arrays.asList(parameter, parameter, parameter, parameter, parameter), occurrences(statement));
    assertEquals("SELECT * FROM \"values\" WHERE \"id\" IN (SELECT \"id\" FROM \"values\")",
        SQL.selectAll().from(schema.table).where(schema.id).in(subquery).build().compile(ansiAdapter).getSQL());
  }

  public void testFragmentsAsExpressionsAndConditions()
  {
    Schema schema = schema("fragments", "parts", false);
    SQLParameter projectionParameter = SQL.param();
    SQLParameter conditionParameter = SQL.param();
    SQLFragment projection = SQL.fragment().sql("COALESCE(").param(projectionParameter).sql(", 0)").build();
    SQLFragment condition = SQL.fragment().column(schema.id).sql(" = ").param(conditionParameter).build();

    SQLStatement statement = SQL.select(projection.expression()).from(schema.table).where(condition.condition()).build().compile(ansiAdapter);
    assertEquals("SELECT COALESCE(?, 0) FROM \"parts\" WHERE \"id\" = ?", statement.getSQL());
    assertEquals(Arrays.asList(projectionParameter, conditionParameter), occurrences(statement));
  }

  public void testSubqueriesFragmentsAndRenderedParameterOrder()
  {
    Schema schema = schema("nested", "records", false);
    SQLParameter selectParameter = SQL.param();
    SQLParameter conditionParameter = SQL.param();
    SQLParameter fragmentParameter = SQL.param();
    SQLSubSelect subquery = SQL.select(schema.id).from(schema.table).where(schema.name).eq(conditionParameter).subSelect();
    SQLFragment inner = SQL.fragment().sql("COALESCE(").expression(SQL.scalar(subquery)).sql(", ").param(fragmentParameter).sql(")").build();
    SQLParameter finalParameter = SQL.param();
    SQLFragment outer = SQL.fragment().sql("(").fragment(inner).sql(" = ").column(schema.id).sql(") AND ").param(selectParameter).sql(" = ")
        .param(finalParameter).build();

    SQLStatement fragmentStatement = SQL.build(outer).compile(ansiAdapter);
    assertEquals("(COALESCE((SELECT \"id\" FROM \"records\" WHERE \"name\" = ?), ?) = \"id\") AND ? = ?", fragmentStatement.getSQL());
    assertEquals(Arrays.asList(conditionParameter, fragmentParameter, selectParameter, finalParameter), occurrences(fragmentStatement));
    assertEquals(4, fragmentStatement.getParameterCount());

    SQLTemplate exists = SQL.selectAll().from(schema.table).where(SQL.exists(subquery).or(SQL.notExists(subquery))).build();
    SQLStatement existsStatement = exists.compile(ansiAdapter);
    assertEquals(
        "SELECT * FROM \"records\" WHERE (EXISTS (SELECT \"id\" FROM \"records\" WHERE \"name\" = ?) OR NOT EXISTS (SELECT \"id\" FROM \"records\" WHERE \"name\" = ?))",
        existsStatement.getSQL());
    assertEquals(Arrays.asList(conditionParameter, conditionParameter), occurrences(existsStatement));
  }

  public void testLiteralsNullAndRepeatedLogicalParameterOccurrences()
  {
    Schema schema = schema("values", "literal_values", false);
    SQLParameter shared = SQL.param(12, value -> value);
    SQLTemplate template = SQL
        .select(SQL.literal("O'Brien"), SQL.literal(null), SQL.literal(true), SQL.literal(new BigDecimal("12.50")), SQL.literal(Date.valueOf("2026-10-12"))) //
        .from(schema.table) //
        .where(SQL.eq(schema.id, shared).or(SQL.eq(schema.id, shared))) //
        .build();
    SQLStatement statement = template.compile(ansiAdapter);

    assertEquals("SELECT 'O''Brien', NULL, TRUE, 12.50, DATE '2026-10-12' FROM \"literal_values\" WHERE (\"id\" = ? OR \"id\" = ?)", statement.getSQL());
    assertEquals(2, statement.getParameterCount());
    assertSame(shared, occurrences(statement).get(0));
    assertSame(shared, occurrences(statement).get(1));
    assertEquals("SELECT * FROM \"literal_values\" WHERE \"name\" = NULL",
        SQL.selectAll().from(schema.table).where(SQL.eq(schema.name, SQL.literal(null))).build().compile(ansiAdapter).getSQL());
  }

  public void testAdapterSpecificQuotingAndImmutableRepeatedCompilation()
  {
    Schema schema = schema("my\"schema", "odd\"table", true);
    SQLTemplate template = SQL.select(schema.table.as("a`b").column(schema.id)).from(schema.table.as("a`b")).build();
    SQLStatement ansi = template.compile(ansiAdapter);
    SQLStatement mysql = template.compile(new MYSQLAdapter());

    assertEquals("SELECT \"a`b\".\"id\" FROM \"my\"\"schema\".\"odd\"\"table\" AS \"a`b\"", ansi.getSQL());
    assertEquals("SELECT `a``b`.`id` FROM `my\"schema`.`odd\"table` AS `a``b`", mysql.getSQL());
    assertEquals(ansi.getSQL(), template.compile(ansiAdapter).getSQL());
  }

  public void testCompiledStatementAndParameterMetadataAreImmutableSnapshots()
  {
    Schema schema = schema("snapshot", "rows", false);
    SQLParameter parameter = SQL.param();
    SQLSelectFromStep builder = SQL.select(schema.id).from(schema.table);
    SQLTemplate template = builder.build();
    SQLStatement compiled = template.compile(ansiAdapter);

    builder.from(schema.table);
    assertEquals("SELECT \"id\" FROM \"rows\"", compiled.getSQL());
    assertEquals("SELECT \"id\" FROM \"rows\"", template.compile(ansiAdapter).getSQL());
    assertEquals("SELECT \"id\" FROM \"rows\", \"rows\"", builder.build().compile(ansiAdapter).getSQL());

    SQLTemplate withParameter = SQL.select(parameter).from(schema.table).build();
    SQLStatement firstCompilation = withParameter.compile(ansiAdapter);
    SQLStatement secondCompilation = withParameter.compile(ansiAdapter);
    assertEquals("SELECT ? FROM \"rows\"", firstCompilation.getSQL());
    assertEquals(1, firstCompilation.getParameterCount());
    assertEquals(1, secondCompilation.getParameterCount());
    assertSame(parameter, occurrences(firstCompilation).get(0));
    assertSame(parameter, occurrences(secondCompilation).get(0));
    try
    {
      occurrences(withParameter.compile(ansiAdapter)).clear();
      fail("Parameter occurrence metadata must be immutable");
    }
    catch (UnsupportedOperationException expected)
    {
      // Expected: a compiled statement is an immutable snapshot.
    }
  }

  public void testInvalidLiteralsAndInsertDefinitionsAreRejected()
  {
    Schema schema = schema("invalid", "target", false);
    SQLTemplate unsupported = SQL.select(SQL.literal(new Object())).from(schema.table).build();
    assertIllegalArgument(() -> unsupported.compile(ansiAdapter), "unsupported literal type");

    Definition mismatch = new Definition("INSERT", schema.table, new Definition.Operation("columns", schema.id),
        new Definition.Operation("values", SQL.param(), SQL.param()));
    assertIllegalArgument(() -> mismatch.compile(ansiAdapter), "does not match column count");

    Definition duplicate = new Definition("INSERT", schema.table, new Definition.Operation("columns", schema.id, schema.id),
        new Definition.Operation("values", SQL.param(), SQL.param()));
    assertIllegalArgument(() -> duplicate.compile(ansiAdapter), "duplicate column");

    Definition unknown = new Definition("SELECT", null, new Definition.Operation("projection", SQL.all()),
        new Definition.Operation("projection", SQL.literal(0)), new Definition.Operation("unknown"));
    assertIllegalArgument(() -> unknown.compile(ansiAdapter), "operation is not valid");
  }

  public void testQuotingDisabledBehaviorAndDelegation()
  {
    IDBAdapter mysql = new MYSQLAdapter();
    String expected = DBUtil.DISABLE_QUOTED_NAMES ? "name`part" : "`name``part`";
    assertEquals(expected, mysql.quoteIdentifier("name`part"));
    assertEquals(expected, new DelegatingDBAdapter(mysql).quoteIdentifier("name`part"));
  }

  public void testRawFragmentQuestionMarksAreRejected()
  {
    Schema schema = schema("raw_fragment", "items", false);
    assertIllegalArgument(() -> SQL.fragment().sql("?"), "represent parameter placeholders");
    assertIllegalArgument(() -> SQL.fragment().sql("'?'"), "represent parameter placeholders");
    SQLFragment hiddenRawQuestion = new Fragment(Arrays.<Object> asList("safe"))
    {
      @Override
      public List<Object> getParts()
      {
        return Arrays.<Object> asList("?");
      }
    };
    SQLTemplate nestedFragmentTemplate = SQL.select(SQL.literal(1)).from(schema.table).where(SQL.fragment().fragment(hiddenRawQuestion).build().condition())
        .build();
    assertIllegalArgument(() -> nestedFragmentTemplate.compile(ansiAdapter), "raw SQL text must not contain '?'");
    assertIllegalArgument(() -> new Fragment(Arrays.<Object> asList("?")), "represent parameter placeholders");

    SQLParameter parameter = SQL.param();
    SQLFragment fragment = SQL.fragment().sql("COALESCE(").param(parameter).sql(", 0)").build();
    SQLTemplate template = SQL.select(SQL.literal("?"), fragment.expression()).from(schema.table).build();
    SQLStatement statement = template.compile(ansiAdapter);
    assertEquals("SELECT '?', COALESCE(?, 0) FROM \"items\"", statement.getSQL());
    assertEquals(1, statement.getParameterCount());
    assertSame(parameter, occurrences(statement).get(0));
  }

  public void testParameterOccurrenceOrderingAcrossClauses()
  {
    Schema schema = schema("parameter_order", "items", false);
    IDBTable joinedTable = schema.schema.addTable("joined");
    SQLParameter joinParameter = SQL.param();
    SQLParameter whereParameter = SQL.param();
    SQLStatement joined = SQL.selectAll().from(schema.table).join(joinedTable).on(schema.id).eq(joinParameter).where(schema.id).eq(whereParameter).build()
        .compile(ansiAdapter);
    assertEquals("SELECT * FROM \"items\" INNER JOIN \"joined\" ON \"id\" = ? WHERE \"id\" = ?", joined.getSQL());
    assertEquals(Arrays.asList(joinParameter, whereParameter), occurrences(joined));

    SQLParameter insertFirst = SQL.param();
    SQLParameter insertSecond = SQL.param();
    SQLStatement insert = SQL.insertInto(schema.table).columns(schema.id, schema.name).values(insertFirst, insertSecond).build().compile(ansiAdapter);
    assertEquals("INSERT INTO \"items\" (\"id\", \"name\") VALUES (?, ?)", insert.getSQL());
    assertEquals(Arrays.asList(insertFirst, insertSecond), occurrences(insert));

    SQLParameter assignmentFirst = SQL.param();
    SQLParameter assignmentSecond = SQL.param();
    SQLParameter updateWhere = SQL.param();
    SQLStatement update = SQL.update(schema.table).set(schema.id).to(assignmentFirst).set(schema.name).to(assignmentSecond).where(schema.id).eq(updateWhere)
        .build().compile(ansiAdapter);
    assertEquals("UPDATE \"items\" SET \"id\" = ?, \"name\" = ? WHERE \"id\" = ?", update.getSQL());
    assertEquals(Arrays.asList(assignmentFirst, assignmentSecond, updateWhere), occurrences(update));

    SQLParameter havingParameter = SQL.param();
    SQLStatement having = SQL.select(schema.name).from(schema.table).groupBy(schema.name).having(SQL.count()).gt(havingParameter).build().compile(ansiAdapter);
    assertEquals("SELECT \"name\" FROM \"items\" GROUP BY \"name\" HAVING COUNT(*) > ?", having.getSQL());
    assertEquals(Arrays.asList(havingParameter), occurrences(having));
  }

  private static List<SQLParameter> occurrences(SQLStatement statement)
  {
    assertTrue(statement instanceof CompiledStatement);
    return ((CompiledStatement)statement).getParameterOccurrences();
  }

  private static void assertIllegalArgument(Runnable runnable, String expectedMessage)
  {
    try
    {
      runnable.run();
      fail("Compilation should reject the invalid definition");
    }
    catch (IllegalArgumentException ex)
    {
      assertTrue(ex.getMessage(), ex.getMessage().contains(expectedMessage));
    }
  }

  private static Schema schema(String name, String tableName, boolean qualified)
  {
    IDBSchema schema = DBUtil.createSchema(name, false, qualified);
    IDBTable table = schema.addTable(tableName);
    IDBField id = table.addField("id", DBType.INTEGER);
    IDBField nameField = table.addField("name", DBType.VARCHAR);
    return new Schema(schema, table, id, nameField);
  }

  /**
   * Holds the test schema and its fields.
   *
   * @author Eike Stepper
   */
  private static class Schema
  {
    private final IDBSchema schema;

    private final IDBTable table;

    private final IDBField id;

    private final IDBField name;

    public Schema(IDBSchema schema, IDBTable table, IDBField id, IDBField name)
    {
      this.schema = schema;
      this.table = table;
      this.id = id;
      this.name = name;
    }
  }
}
