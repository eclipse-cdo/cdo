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
import org.eclipse.net4j.internal.db.dml.Expression;
import org.eclipse.net4j.internal.db.dml.Parameter;
import org.eclipse.net4j.internal.db.dml.TableRef;
import org.eclipse.net4j.internal.db.dml.Template;
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
 * <p>
 * {@link #literal(Object)} values are checked by the selected adapter during compilation and may
 * not be portable across dialects; application values should generally be supplied through
 * {@link #param()}. SQL comparisons with a literal or bound NULL retain ordinary SQL comparison
 * semantics and are not rewritten to {@code IS NULL}.
 * </p>
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class SQL
{
  /**
   * The SQL NULL literal.
   *
   * @since 4.15
   */
  public static final SQLExpression NULL = literal(null);

  /**
   * The numeric SQL literal 0.
   *
   * @since 4.15
   */
  public static final SQLExpression ZERO = literal(0);

  /**
   * The numeric SQL literal 1.
   *
   * @since 4.15
   */
  public static final SQLExpression ONE = literal(1);

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
   * Begins an UPDATE definition for the supplied schema table. A complete assignment is required before the definition can be built. Without a WHERE clause, the UPDATE may modify every row in the table.
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
   * Begins a DELETE definition. It may be built directly or completed with a WHERE condition. Without a WHERE clause, the DELETE may remove every row in the table.
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
    return new Parameter(null, (Function<?, ?>)null);
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
    return new Parameter(Integer.valueOf(sqlType), (Function<?, ?>)null);
  }

  /**
   * Creates a new immutable logical parameter identity with a converter. Each factory call creates a distinct identity; reusing one instance in a definition preserves that identity across occurrences. The converter runs once for every occurrence bound with a non-null input, and its result is passed to default JDBC binding. Converter failures propagate to the caller.
   *
   * @param <JAVA> the input value type accepted by the converter
   * @param <JDBC> the converted value type passed to JDBC
   * @param converter converts each non-null application value before default JDBC binding; it is bypassed for null input and by an explicit setter override
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
   * @param converter converts each non-null application value before default JDBC binding; it is bypassed for null input and by an explicit setter override
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static <JAVA, JDBC> SQLParameter param(int sqlType, Function<JAVA, JDBC> converter)
  {
    return new Parameter(Integer.valueOf(sqlType), Objects.requireNonNull(converter, "converter"));
  }

  /**
   * Creates a new logical parameter whose setter performs complete JDBC binding for every occurrence.
   * The setter receives the original application value, including null, and is responsible for JDBC
   * NULL handling. The parameter retains the setter, and compiled statements retain the parameter;
   * captured objects may therefore remain reachable for their lifetime. Choose reuse, mutable state,
   * thread safety, and connection-specific or execution-specific dependencies to suit your use case.
   *
   * @param <JAVA> the application value type
   * @param setter the complete JDBC binding strategy
   * @return a new logical parameter identity
   * @throws NullPointerException if {@code setter} is {@code null}
   * @since 4.16
   */
  public static <JAVA> SQLParameter param(SQLValueSetter<JAVA> setter)
  {
    return new Parameter(null, Objects.requireNonNull(setter, "setter"));
  }

  /**
   * Creates a new logical parameter with JDBC type metadata and a complete JDBC value setter.
   * The type remains parameter metadata, but the setter owns the entire JDBC operation. Captured
   * objects may remain reachable through the parameter and compiled statement; callers choose
   * suitable reuse, mutable state, thread safety, and connection-specific dependencies.
   *
   * @param <JAVA> the application value type
   * @param sqlType the JDBC type metadata
   * @param setter the complete JDBC binding strategy
   * @return a new logical parameter identity
   * @throws NullPointerException if {@code setter} is {@code null}
   * @since 4.16
   */
  public static <JAVA> SQLParameter param(int sqlType, SQLValueSetter<JAVA> setter)
  {
    return new Parameter(Integer.valueOf(sqlType), Objects.requireNonNull(setter, "setter"));
  }

  /**
   * Creates an expression from an explicit literal value. Compilation supports null, strings,
   * characters, booleans, finite numeric values, and SQL date/time values, subject to validation
   * by the compiling adapter. Other Java types are rejected rather than rendered through arbitrary
   * {@code toString()} conversion. Mutable SQL date, time, and timestamp values are snapshotted when
   * this expression is created. Literals such as Boolean or SQL NULL may have dialect-specific
   * semantics; use parameters for portable application values. Comparisons to NULL are not
   * rewritten to {@code IS NULL}; use {@link #isNull(SQLExpression)} or
   * {@link #isNotNull(SQLExpression)} explicitly.
   *
   * @param value the literal value represented by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression literal(Object value)
  {
    return Expression.of(Expression.Kind.LITERAL, value);
  }

  /**
   * Uses a complete SELECT definition as a scalar expression. It must project exactly one column;
   * this is validated during compilation. Whether the query returns zero, one, or multiple rows is
   * determined by SQL and the database at execution time.
   *
   * @param query the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression scalar(SQLSubSelect query)
  {
    return Expression.of(Expression.Kind.SCALAR_SUBQUERY, Objects.requireNonNull(query, "query"));
  }

  /**
   * Creates an EXISTS condition over a complete SELECT definition. Unlike scalar and IN subqueries,
   * EXISTS does not require a single-column projection; width validation occurs during compilation.
   *
   * @param query the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition exists(SQLSubSelect query)
  {
    return Expression.condition(Expression.Kind.EXISTS, Objects.requireNonNull(query, "query"));
  }

  /**
   * Creates a NOT EXISTS condition over a complete SELECT definition. Unlike scalar and IN subqueries,
   * NOT EXISTS does not require a single-column projection; width validation occurs during compilation.
   *
   * @param query the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition notExists(SQLSubSelect query)
  {
    return Expression.condition(Expression.Kind.NOT_EXISTS, Objects.requireNonNull(query, "query"));
  }

  /**
   * Creates the COUNT(*) aggregate.
   *
   * @return a COUNT(*) expression
   * @since 4.15
   */
  public static SQLExpression count()
  {
    return Expression.of(Expression.Kind.COUNT_ALL);
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
    return Expression.of(Expression.Kind.COUNT, Objects.requireNonNull(expression, "expression"));
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
    return Expression.of(Expression.Kind.COUNT_DISTINCT, Objects.requireNonNull(expression, "expression"));
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
    return Expression.of(Expression.Kind.MIN, Objects.requireNonNull(expression, "expression"));
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
    return Expression.of(Expression.Kind.MAX, Objects.requireNonNull(expression, "expression"));
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
    return Expression.of(Expression.Kind.SUM, Objects.requireNonNull(expression, "expression"));
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
    return Expression.of(Expression.Kind.AVG, Objects.requireNonNull(expression, "expression"));
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
    return Expression.of(Expression.Kind.ADD, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.of(Expression.Kind.SUBTRACT, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.of(Expression.Kind.MULTIPLY, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.of(Expression.Kind.DIVIDE, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
  }

  /**
   * Creates an equality condition using ordinary SQL comparison semantics. If either operand is
   * SQL NULL, the result is not rewritten to {@code IS NULL}; use {@link #isNull(SQLExpression)}
   * for a NULL test.
   *
   * @param left the left operand
   * @param right the right operand
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition eq(SQLExpression left, SQLExpression right)
  {
    return Expression.condition(Expression.Kind.EQUALS, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.NOT_EQUALS, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.LESS_THAN, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.LESS_THAN_OR_EQUAL, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.GREATER_THAN, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.GREATER_THAN_OR_EQUAL, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.LIKE, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.BETWEEN, Objects.requireNonNull(expression, "expression"), Objects.requireNonNull(lower, "lower"),
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
    return Expression.condition(Expression.Kind.IS_NULL, Objects.requireNonNull(expression, "expression"));
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
    return Expression.condition(Expression.Kind.IS_NOT_NULL, Objects.requireNonNull(expression, "expression"));
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
    return Expression.condition(Expression.Kind.AND, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.OR, Objects.requireNonNull(left, "left"), Objects.requireNonNull(right, "right"));
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
    return Expression.condition(Expression.Kind.NOT, Objects.requireNonNull(condition, "condition"));
  }

  /**
   * Creates an IN condition whose values come from a complete SELECT definition. The subquery must
   * project exactly one column; this is validated during compilation.
   *
   * @param expression the SQL expression to wrap or aggregate
   * @param subSelect the complete SELECT definition used by this expression
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLCondition in(SQLExpression expression, SQLSubSelect subSelect)
  {
    return Expression.condition(Expression.Kind.IN_SUBQUERY, Objects.requireNonNull(expression, "expression"), Objects.requireNonNull(subSelect, "subSelect"));
  }

  /**
   * Begins a structured fragment. Raw text is trusted SQL, is not parsed for operator precedence,
   * and must not contain {@code ?}, even in a quoted string. Add identifiers and parameters through
   * their typed methods. Prefer structured expressions for operators; explicitly group fragment
   * expressions when their internal precedence matters.
   *
   * @return the corresponding staged builder
   * @since 4.15
   */
  public static SQLFragmentStep fragment()
  {
    return new FragmentBuilder();
  }

  /**
   * Wraps a structured fragment as an expression using the canonical factory. Structured operator
   * expressions allow compiler-managed precedence; raw fragment text is not parsed, so explicitly
   * group it where required.
   *
   * @param fragment the structured SQL fragment
   * @return the corresponding immutable SQL API value
   * @since 4.15
   */
  public static SQLExpression expression(SQLFragment fragment)
  {
    return Expression.of(Expression.Kind.FRAGMENT, Objects.requireNonNull(fragment, "fragment"));
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
    return Expression.condition(Expression.Kind.FRAGMENT, Objects.requireNonNull(fragment, "fragment"));
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
    return new Template(Objects.requireNonNull(fragment, "fragment"));
  }
}
