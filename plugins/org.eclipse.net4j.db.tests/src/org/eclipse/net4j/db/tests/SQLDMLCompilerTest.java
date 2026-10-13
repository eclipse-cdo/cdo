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
import org.eclipse.net4j.db.dml.SQLExpression;
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
import org.eclipse.net4j.internal.db.dml.Fragment;
import org.eclipse.net4j.internal.db.dml.Template;
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
    assertEquals("SELECT COALESCE(?, 0) FROM \"parts\" WHERE (\"id\" = ?)", statement.getSQL());
    assertEquals(Arrays.asList(projectionParameter, conditionParameter), occurrences(statement));
  }

  public void testFragmentExpressionPrecedence()
  {
    Schema schema = schema("fragment_precedence", "items", false);
    SQLCondition fragmentOr = SQL.fragment().sql("a = 1 OR b = 2").build().condition();
    SQLCondition fragmentAnd = SQL.fragment().sql("a = 1 AND b = 2").build().condition();

    assertEquals("SELECT * FROM \"items\" WHERE (a = 1 OR b = 2) AND \"id\" = 3",
        SQL.selectAll().from(schema.table).where(fragmentOr).and(SQL.eq(schema.id, SQL.literal(3))).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT * FROM \"items\" WHERE (a = 1 AND b = 2) OR \"id\" = 3",
        SQL.selectAll().from(schema.table).where(fragmentAnd).or(SQL.eq(schema.id, SQL.literal(3))).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT * FROM \"items\" WHERE NOT (a = 1 OR b = 2)",
        SQL.selectAll().from(schema.table).where(SQL.not(fragmentOr)).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT * FROM \"items\" WHERE (a = 1 OR b = 2)", SQL.selectAll().from(schema.table).where(fragmentOr).build().compile(ansiAdapter).getSQL());

    SQLExpression expression = SQL.fragment().sql("left + right").build().expression();
    assertEquals("SELECT (left + right) + 1 FROM \"items\"",
        SQL.select(SQL.add(expression, SQL.literal(1))).from(schema.table).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT (left = 1 OR right = 2) = \"id\" FROM \"items\"", SQL
        .select(SQL.eq(SQL.fragment().sql("left = 1 OR right = 2").build().expression(), schema.id)).from(schema.table).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT \"id\" = (left = 1 OR right = 2) FROM \"items\"", SQL
        .select(SQL.eq(schema.id, SQL.fragment().sql("left = 1 OR right = 2").build().expression())).from(schema.table).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT left = 1 OR right = 2 FROM \"items\"",
        SQL.select(SQL.fragment().sql("left = 1 OR right = 2").build().expression()).from(schema.table).build().compile(ansiAdapter).getSQL());
  }

  public void testFragmentPrecedenceAcrossClausesAndParameterOrder()
  {
    Schema schema = schema("fragment_clauses", "items", false);
    IDBTable joined = schema.schema.addTable("joined_items");
    SQLCondition onCondition = SQL.fragment().sql("left_value = 1 OR right_value = 2").build().condition();

    assertEquals("SELECT * FROM \"items\" INNER JOIN \"joined_items\" ON (left_value = 1 OR right_value = 2)",
        SQL.selectAll().from(schema.table).join(joined).on(onCondition).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT COUNT(*) FROM \"items\" GROUP BY \"id\" HAVING (left_value = 1 OR right_value = 2)",
        SQL.select(SQL.count()).from(schema.table).groupBy(schema.id).having(onCondition).build().compile(ansiAdapter).getSQL());
    assertEquals("UPDATE \"items\" SET \"id\" = (left_value = 1 OR right_value = 2)", SQL.update(schema.table).set(schema.id)
        .to(SQL.fragment().sql("left_value = 1 OR right_value = 2").build().expression()).build().compile(ansiAdapter).getSQL());

    SQLFragment nested = SQL.fragment().sql("a OR b").build();
    assertEquals("prefix a OR b suffix", SQL.build(SQL.fragment().sql("prefix ").fragment(nested).sql(" suffix").build()).compile(ansiAdapter).getSQL());

    SQLParameter first = SQL.param();
    SQLParameter second = SQL.param();
    SQLParameter third = SQL.param();
    SQLFragment parameterizedCondition = SQL.fragment().sql("left_value = ").param(first).sql(" OR right_value = ").param(second).build();
    SQLStatement statement = SQL.selectAll().from(schema.table).where(parameterizedCondition.condition()).and(SQL.eq(schema.id, third)).build()
        .compile(ansiAdapter);
    assertEquals("SELECT * FROM \"items\" WHERE (left_value = ? OR right_value = ?) AND \"id\" = ?", statement.getSQL());
    assertEquals(3, statement.getParameterCount());
    assertSame(first, occurrences(statement).get(0));
    assertSame(second, occurrences(statement).get(1));
    assertSame(third, occurrences(statement).get(2));
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

  public void testScalarAndInSubqueriesRequireOneProjection()
  {
    Schema schema = schema("subquery_width", "records", false);
    SQLSubSelect oneColumn = SQL.select(schema.id).from(schema.table).subSelect();
    SQLSubSelect twoColumns = SQL.select(schema.id, schema.name).from(schema.table).subSelect();

    assertEquals("SELECT (SELECT \"id\" FROM \"records\") FROM \"records\"",
        SQL.select(SQL.scalar(oneColumn)).from(schema.table).build().compile(ansiAdapter).getSQL());
    assertIllegalArgument(() -> SQL.select(SQL.scalar(twoColumns)).from(schema.table).build().compile(ansiAdapter),
        "scalar subquery: expected exactly one projected column but found 2");

    assertEquals("SELECT * FROM \"records\" WHERE \"id\" IN (SELECT \"id\" FROM \"records\")",
        SQL.selectAll().from(schema.table).where(SQL.in(schema.id, oneColumn)).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT * FROM \"records\" WHERE \"id\" IN (SELECT \"id\" FROM \"records\")",
        SQL.selectAll().from(schema.table).where(schema.id).in(oneColumn).build().compile(ansiAdapter).getSQL());
    assertIllegalArgument(() -> SQL.selectAll().from(schema.table).where(SQL.in(schema.id, twoColumns)).build().compile(ansiAdapter),
        "IN subquery: expected exactly one projected column but found 2");
    assertIllegalArgument(() -> SQL.selectAll().from(schema.table).where(schema.id).in(twoColumns).build().compile(ansiAdapter),
        "IN subquery: expected exactly one projected column but found 2");

    assertEquals("SELECT * FROM \"records\" WHERE EXISTS (SELECT \"id\", \"name\" FROM \"records\")",
        SQL.selectAll().from(schema.table).where(SQL.exists(twoColumns)).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT * FROM \"records\" WHERE NOT EXISTS (SELECT \"id\", \"name\" FROM \"records\")",
        SQL.selectAll().from(schema.table).where(SQL.notExists(twoColumns)).build().compile(ansiAdapter).getSQL());
  }

  public void testSubqueryProjectionWidthsWithWildcardsAndExpressions()
  {
    Schema schema = schema("wildcard_width", "records", false);
    IDBTable oneColumnTable = schema.schema.addTable("one_column");
    IDBField oneColumn = oneColumnTable.addField("value", DBType.INTEGER);
    IDBTable threeColumnTable = schema.schema.addTable("three_column");
    IDBField first = threeColumnTable.addField("first", DBType.INTEGER);
    threeColumnTable.addField("second", DBType.VARCHAR);
    threeColumnTable.addField("third", DBType.BOOLEAN);

    SQLTableRef oneAlias = oneColumnTable.as("one");
    SQLTableRef threeAlias = threeColumnTable.as("three");
    SQLSubSelect qualifiedSingle = SQL.select(SQL.all(oneAlias)).from(oneAlias).subSelect();
    SQLSubSelect unqualifiedSingle = SQL.selectAll().from(oneColumnTable).subSelect();
    assertEquals("SELECT (SELECT \"one\".* FROM \"one_column\" AS \"one\") FROM \"records\"",
        SQL.select(SQL.scalar(qualifiedSingle)).from(schema.table).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT (SELECT * FROM \"one_column\") FROM \"records\"",
        SQL.select(SQL.scalar(unqualifiedSingle)).from(schema.table).build().compile(ansiAdapter).getSQL());

    SQLSubSelect qualifiedMultiple = SQL.select(SQL.all(threeAlias)).from(threeAlias).subSelect();
    SQLSubSelect unqualifiedMultiple = SQL.selectAll().from(threeColumnTable).subSelect();
    assertIllegalArgument(() -> SQL.select(SQL.scalar(qualifiedMultiple)).from(schema.table).build().compile(ansiAdapter),
        "scalar subquery: expected exactly one projected column but found 3");
    assertIllegalArgument(() -> SQL.selectAll().from(schema.table).where(SQL.in(schema.id, qualifiedMultiple)).build().compile(ansiAdapter),
        "IN subquery: expected exactly one projected column but found 3");
    assertIllegalArgument(() -> SQL.select(SQL.scalar(unqualifiedMultiple)).from(schema.table).build().compile(ansiAdapter),
        "scalar subquery: expected exactly one projected column but found 3");
    assertIllegalArgument(() -> SQL.selectAll().from(schema.table).where(schema.id).in(unqualifiedMultiple).build().compile(ansiAdapter),
        "IN subquery: expected exactly one projected column but found 3");

    SQLSubSelect mixed = SQL.select(oneColumn, SQL.all(oneColumnTable.as("mixed"))).from(oneColumnTable).subSelect();
    assertIllegalArgument(() -> SQL.select(SQL.scalar(mixed)).from(schema.table).build().compile(ansiAdapter),
        "scalar subquery: expected exactly one projected column but found 2");

    SQLSubSelect expression = SQL.select(SQL.add(oneColumn, SQL.literal(1))).from(oneColumnTable).subSelect();
    SQLSubSelect fragment = SQL.select(SQL.fragment().sql("COALESCE(value, 0)").build().expression()).from(oneColumnTable).subSelect();
    assertEquals("SELECT (SELECT \"value\" + 1 FROM \"one_column\") FROM \"records\"",
        SQL.select(SQL.scalar(expression)).from(schema.table).build().compile(ansiAdapter).getSQL());
    assertEquals("SELECT (SELECT COALESCE(value, 0) FROM \"one_column\") FROM \"records\"",
        SQL.select(SQL.scalar(fragment)).from(schema.table).build().compile(ansiAdapter).getSQL());

    SQLSubSelect joined = SQL.selectAll().from(oneAlias).join(threeAlias).on(oneAlias.column(oneColumn)).eq(threeAlias.column(first)).subSelect();
    assertIllegalArgument(() -> SQL.select(SQL.scalar(joined)).from(schema.table).build().compile(ansiAdapter),
        "scalar subquery: expected exactly one projected column but found 4");
    SQLSubSelect multipleSources = SQL.selectAll().from(oneAlias, oneColumnTable.as("one_again")).join(threeAlias).on(oneAlias.column(oneColumn))
        .eq(threeAlias.column(first)).subSelect();
    assertIllegalArgument(() -> SQL.select(SQL.scalar(multipleSources)).from(schema.table).build().compile(ansiAdapter),
        "scalar subquery: expected exactly one projected column but found 5");
    SQLSubSelect repeatedTable = SQL.selectAll().from(threeAlias).join(threeColumnTable.as("three_again")).on(threeAlias.column(first))
        .eq(threeColumnTable.as("three_again").column(first)).subSelect();
    assertIllegalArgument(() -> SQL.selectAll().from(schema.table).where(SQL.in(schema.id, repeatedTable)).build().compile(ansiAdapter),
        "IN subquery: expected exactly one projected column but found 6");
  }

  public void testSubqueryWidthValidationPreservesParameterOccurrences()
  {
    Schema schema = schema("subquery_parameters", "records", false);
    SQLParameter scalarParameter = SQL.param();
    SQLParameter outerParameter = SQL.param();
    SQLSubSelect scalarSubquery = SQL.select(schema.id).from(schema.table).where(schema.name).eq(scalarParameter).subSelect();
    SQLStatement scalar = SQL.select(SQL.scalar(scalarSubquery)).from(schema.table).where(schema.id).eq(outerParameter).build().compile(ansiAdapter);
    assertEquals("SELECT (SELECT \"id\" FROM \"records\" WHERE \"name\" = ?) FROM \"records\" WHERE \"id\" = ?", scalar.getSQL());
    assertEquals(2, scalar.getParameterCount());
    assertSame(scalarParameter, occurrences(scalar).get(0));
    assertSame(outerParameter, occurrences(scalar).get(1));

    SQLParameter inParameter = SQL.param();
    SQLSubSelect inSubquery = SQL.select(inParameter).from(schema.table).subSelect();
    SQLStatement in = SQL.selectAll().from(schema.table).where(schema.id).in(inSubquery).build().compile(ansiAdapter);
    assertEquals("SELECT * FROM \"records\" WHERE \"id\" IN (SELECT ? FROM \"records\")", in.getSQL());
    assertEquals(1, in.getParameterCount());
    assertSame(inParameter, occurrences(in).get(0));
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
    assertEquals(firstCompilation.getSQL(), firstCompilation.toString());
    assertEquals(1, firstCompilation.getParameterCount());
    assertEquals(1, secondCompilation.getParameterCount());
    assertSame(parameter, occurrences(firstCompilation).get(0));
    assertSame(parameter, occurrences(secondCompilation).get(0));
    assertEquals(1, occurrences(firstCompilation).size());
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

  public void testInvalidLiteralsAndInsertTemplatesAreRejected()
  {
    Schema schema = schema("invalid", "target", false);
    SQLTemplate unsupported = SQL.select(SQL.literal(new Object())).from(schema.table).build();
    assertIllegalArgument(() -> unsupported.compile(ansiAdapter), "unsupported literal type");

    Template mismatch = new Template(Template.Kind.INSERT, schema.table, new Template.Operation(Template.Operation.Kind.COLUMNS, schema.id),
        new Template.Operation(Template.Operation.Kind.VALUES, SQL.param(), SQL.param()));
    assertIllegalArgument(() -> mismatch.compile(ansiAdapter), "does not match column count");

    Template duplicate = new Template(Template.Kind.INSERT, schema.table, new Template.Operation(Template.Operation.Kind.COLUMNS, schema.id, schema.id),
        new Template.Operation(Template.Operation.Kind.VALUES, SQL.param(), SQL.param()));
    assertIllegalArgument(() -> duplicate.compile(ansiAdapter), "duplicate column");

    Template invalidOperation = new Template(Template.Kind.INSERT, schema.table, new Template.Operation(Template.Operation.Kind.PROJECTION, SQL.all()));
    assertIllegalArgument(() -> invalidOperation.compile(ansiAdapter), "operation is not valid");
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
