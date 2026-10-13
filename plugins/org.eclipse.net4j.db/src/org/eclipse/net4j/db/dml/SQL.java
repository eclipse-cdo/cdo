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
package org.eclipse.net4j.db.dml;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.fluent.SQLDeleteStep;
import org.eclipse.net4j.db.dml.fluent.SQLFragmentStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertStartStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectProjectionStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateStartStep;
import org.eclipse.net4j.internal.db.dml.Definition;
import org.eclipse.net4j.internal.db.dml.Expression;
import org.eclipse.net4j.internal.db.dml.Parameter;
import org.eclipse.net4j.internal.db.dml.TableRef;
import org.eclipse.net4j.internal.db.dml.fluent.Builder;
import org.eclipse.net4j.internal.db.dml.fluent.FragmentBuilder;

import java.sql.Types;
import java.util.Objects;
import java.util.function.Function;

/**
 * Canonical factories for SQL expressions, definitions, references, and fluent statements. Builders
 * preserve their operations as immutable templates; compiling a template renders adapter-specific
 * SQL text and ordered parameter occurrences for preparation and JDBC parameter binding.
 * <p>
 * A typical SELECT chain keeps schema fields as expressions and parameters explicit:
 * </p>
 * <pre>
 * SQL.select(SQL.all(table.as("t")), SQL.count(id).as("total")) //
 *     .from(table.as("t")) //
 *     .where(id).eq(SQL.param()) //
 *     .groupBy(name) //
 *     .orderByDesc(SQL.count(id)) //
 *     .build();
 * </pre>
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class SQL
{
  private SQL()
  {
  }

  /**
   * Begins a SELECT definition whose projection contains the required first item and any additional items in the supplied order.
   *
   * @param first the required first item; it also determines the item category for this call
   * @param others additional items, preserved in their supplied order
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLSelectProjectionStep select(SQLSelectItem first, SQLSelectItem... others)
  {
    return Builder.select(false, first, others);
  }

  /**
   * Begins a SELECT DISTINCT definition; all supplied projection items participate in duplicate elimination.
   *
   * @param first the required first item; it also determines the item category for this call
   * @param others additional items, preserved in their supplied order
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLSelectProjectionStep selectDistinct(SQLSelectItem first, SQLSelectItem... others)
  {
    return Builder.select(true, first, others);
  }

  /**
   * Begins a SELECT with an unqualified wildcard projection.
   *
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLSelectProjectionStep selectAll()
  {
    return select(all());
  }

  /**
   * Begins an UPDATE definition for the supplied schema table. A complete assignment is required before the definition can be built.
   *
   * @param table the existing schema table
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLUpdateStartStep update(IDBTable table)
  {
    return Builder.update(table);
  }

  /**
   * Begins an INSERT definition. Choose a values list, explicit columns and values, or dynamic column assignments.
   *
   * @param table the existing schema table
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLInsertStartStep insertInto(IDBTable table)
  {
    return Builder.insert(table);
  }

  /**
   * Begins a DELETE definition. It may be built directly or completed with a WHERE condition.
   *
   * @param table the existing schema table
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLDeleteStep deleteFrom(IDBTable table)
  {
    return Builder.delete(table);
  }

  /**
   * Creates an unaliased occurrence of a schema table for use in a statement.
   *
   * @param table the existing schema table
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLTableRef table(IDBTable table)
  {
    return new TableRef(Objects.requireNonNull(table, "table"), null);
  }

  /**
   * Creates a table occurrence with a SQL alias. This is a table alias, distinct from a result-column alias.
   *
   * @param table the existing schema table
   * @param alias the SQL alias for this table occurrence
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLTableRef as(IDBTable table, String alias)
  {
    return new TableRef(Objects.requireNonNull(table, "table"), Objects.requireNonNull(alias, "alias"));
  }

  /**
   * Associates a result-column label with an expression in a projection.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @param alias the result-column label
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLSelectItem as(SQLExpression expression, String alias)
  {
    return Expression.alias(Objects.requireNonNull(expression, "expression"), Objects.requireNonNull(alias, "alias"));
  }

  /**
   * Creates an unqualified reference to a schema column. Use a table reference’s column method when qualification is needed.
   *
   * @param column the existing schema column
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLColumnRef column(IDBField column)
  {
    return new Expression.Column(Objects.requireNonNull(column, "column"), null);
  }

  /**
   * Creates a wildcard projection; the overload with a table reference qualifies the wildcard to that table occurrence.
   *
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLSelectItem all()
  {
    return Expression.all(null);
  }

  /**
   * Creates a wildcard projection; the overload with a table reference qualifies the wildcard to that table occurrence.
   *
   * @param table the existing schema table
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLSelectItem all(SQLTableRef table)
  {
    return Expression.all(Objects.requireNonNull(table, "table"));
  }

  /**
   * Wraps an expression as an ascending ORDER BY item.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLOrderItem asc(SQLExpression expression)
  {
    return Expression.order("ASC", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Wraps an expression as a descending ORDER BY item.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLOrderItem desc(SQLExpression expression)
  {
    return Expression.order("DESC", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates a new immutable logical parameter identity. Each appearance of the parameter in a
   * compiled statement becomes a JDBC placeholder occurrence; reusing one instance preserves its
   * identity across those occurrences.
   *
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLParameter param()
  {
    return new Parameter(null, null);
  }

  /**
   * Creates a new immutable logical parameter identity with an explicit JDBC type. Each factory call creates a distinct identity; reusing one instance in a definition preserves that identity across occurrences.
   *
   * @param sqlType the {@link Types} code used by typed {@code setObject} calls and for NULL binding
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLParameter param(int sqlType)
  {
    return new Parameter(Integer.valueOf(sqlType), null);
  }

  /**
   * Creates a new immutable logical parameter identity with a converter. Each factory call creates a distinct identity; reusing one instance in a definition preserves that identity across occurrences. The converter runs once for every occurrence bound with a non-null input, and its result is passed to default JDBC binding. Converter failures propagate to the caller.
   *
   * @param <JAVA> the input value type accepted by the converter
   * @param <JDBC> the converted value type passed to JDBC
   * @param converter converts each non-null application value before default JDBC binding; it is bypassed for null input and by a specialized binder
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static <JAVA, JDBC> SQLParameter param(Function<JAVA, JDBC> converter)
  {
    return new Parameter(null, Objects.requireNonNull(converter, "converter"));
  }

  /**
   * Creates a new immutable logical parameter identity with an explicit JDBC type and converter. Each factory call creates a distinct identity; reusing one instance in a definition preserves that identity across occurrences. The converter runs once for every occurrence bound with a non-null input, and its result is passed to default JDBC binding. Converter failures propagate to the caller.
   *
   * @param sqlType the {@link Types} code used by typed {@code setObject} calls and for NULL binding
   * @param <JAVA> the input value type accepted by the converter
   * @param <JDBC> the converted value type passed to JDBC
   * @param converter converts each non-null application value before default JDBC binding; it is bypassed for null input and by a specialized binder
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static <JAVA, JDBC> SQLParameter param(int sqlType, Function<JAVA, JDBC> converter)
  {
    return new Parameter(Integer.valueOf(sqlType), Objects.requireNonNull(converter, "converter"));
  }

  /**
   * Creates an expression from an explicit literal value. Compilation supports null, strings,
   * characters, booleans, finite numeric values, and SQL date/time values. Other Java types are
   * rejected rather than rendered through arbitrary {@code toString()} conversion.
   *
   * @param value the literal value represented by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression literal(Object value)
  {
    return Expression.of("LITERAL", value);
  }

  /**
   * Uses a complete SELECT definition as a scalar expression.
   *
   * @param query the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression scalar(SQLSubSelect query)
  {
    return Expression.of("SCALAR", Objects.requireNonNull(query, "query"));
  }

  /**
   * Creates an EXISTS condition over a complete SELECT definition.
   *
   * @param query the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition exists(SQLSubSelect query)
  {
    return Expression.condition("EXISTS", Objects.requireNonNull(query, "query"));
  }

  /**
   * Creates a NOT EXISTS condition over a complete SELECT definition.
   *
   * @param query the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition notExists(SQLSubSelect query)
  {
    return Expression.condition("NOT_EXISTS", Objects.requireNonNull(query, "query"));
  }

  /**
   * Creates the COUNT(*) aggregate.
   *
   * @return a COUNT(*) expression
   * @since 4.15
   */
  public static SQLExpression count()
  {
    return Expression.of("COUNT_ALL");
  }

  /**
   * Creates COUNT over the non-NULL values produced by the supplied expression.
   *
   * @param expression the required expression whose non-NULL values are counted
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression count(SQLExpression expression)
  {
    return Expression.of("COUNT", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates COUNT(DISTINCT expression).
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression countDistinct(SQLExpression expression)
  {
    return Expression.of("COUNT_DISTINCT", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates a MIN aggregate over the supplied expression.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression min(SQLExpression expression)
  {
    return Expression.of("MIN", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates a MAX aggregate over the supplied expression.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression max(SQLExpression expression)
  {
    return Expression.of("MAX", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates a SUM aggregate over the supplied expression.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression sum(SQLExpression expression)
  {
    return Expression.of("SUM", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates an AVG aggregate over the supplied expression.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression avg(SQLExpression expression)
  {
    return Expression.of("AVG", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates a binary addition expression.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression add(SQLExpression left, SQLExpression right)
  {
    return Expression.of("+", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a binary subtraction expression.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression subtract(SQLExpression left, SQLExpression right)
  {
    return Expression.of("-", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a binary multiplication expression.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression multiply(SQLExpression left, SQLExpression right)
  {
    return Expression.of("*", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a binary division expression.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression divide(SQLExpression left, SQLExpression right)
  {
    return Expression.of("/", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates an equality condition.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition eq(SQLExpression left, SQLExpression right)
  {
    return Expression.condition("=", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates an inequality condition.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition ne(SQLExpression left, SQLExpression right)
  {
    return Expression.condition("<>", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a less-than condition.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition lt(SQLExpression left, SQLExpression right)
  {
    return Expression.condition("<", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a less-than-or-equal condition.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition le(SQLExpression left, SQLExpression right)
  {
    return Expression.condition("<=", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a greater-than condition.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition gt(SQLExpression left, SQLExpression right)
  {
    return Expression.condition(">", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a greater-than-or-equal condition.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition ge(SQLExpression left, SQLExpression right)
  {
    return Expression.condition(">=", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a LIKE condition. Pattern escaping remains the responsibility of the supplied expression and database dialect.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition like(SQLExpression left, SQLExpression right)
  {
    return Expression.condition("LIKE", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a BETWEEN condition with inclusive lower and upper bounds according to SQL semantics.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @param lower the inclusive lower bound expression
   * @param upper the inclusive upper bound expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition between(SQLExpression expression, SQLExpression lower, SQLExpression upper)
  {
    return Expression.condition("BETWEEN", Objects.requireNonNull(expression, "expression"), Objects.requireNonNull(lower, "lower"),
        Objects.requireNonNull(upper, "upper"));
  }

  /**
   * Creates an IS NULL condition.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition isNull(SQLExpression expression)
  {
    return Expression.condition("IS_NULL", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates an IS NOT NULL condition.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition isNotNull(SQLExpression expression)
  {
    return Expression.condition("IS_NOT_NULL", Objects.requireNonNull(expression, "expression"));
  }

  /**
   * Creates a condition that combines both supplied conditions with AND.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition and(SQLCondition left, SQLCondition right)
  {
    return Expression.condition("AND", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a condition that combines both supplied conditions with OR.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition or(SQLCondition left, SQLCondition right)
  {
    return Expression.condition("OR", Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates a negated condition.
   *
   * @param condition the complete condition to compose or wrap
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition not(SQLCondition condition)
  {
    return Expression.condition("NOT", Objects.requireNonNull(condition, "condition"));
  }

  /**
   * Creates an IN condition whose values come from a complete SELECT definition.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @param subSelect the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition in(SQLExpression expression, SQLSubSelect subSelect)
  {
    return Expression.condition("IN_SUB", Objects.requireNonNull(expression, "expression"), Objects.requireNonNull(subSelect, "subSelect"));
  }

  /**
   * Begins a structured fragment. Raw text is trusted SQL; identifiers and parameters should be supplied through their typed methods.
   *
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLFragmentStep fragment()
  {
    return new FragmentBuilder();
  }

  /**
   * Wraps a structured fragment as an expression using the canonical factory.
   *
   * @param fragment the structured SQL fragment
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression expression(SQLFragment fragment)
  {
    return Expression.of("FRAGMENT", Objects.requireNonNull(fragment, "fragment"));
  }

  /**
   * Wraps a structured fragment as a condition using the canonical factory.
   *
   * @param fragment the structured SQL fragment
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition condition(SQLFragment fragment)
  {
    return Expression.condition("FRAGMENT", Objects.requireNonNull(fragment, "fragment"));
  }

  /**
   * Treats the fragment as a complete template definition. This differs from the fragment builder’s build method, which returns the fragment itself.
   *
   * @param fragment the structured SQL fragment
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLTemplate build(SQLFragment fragment)
  {
    return new Definition("FRAGMENT", null, Objects.requireNonNull(fragment, "fragment"));
  }
}
