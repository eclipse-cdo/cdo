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

/**
 * Structured SQL data manipulation for the Net4j DB framework. The {@link org.eclipse.net4j.db.dml.SQL} factories
 * create immutable expressions, conditions, references, parameters, and staged statement builders. A completed
 * builder produces an immutable {@link org.eclipse.net4j.db.dml.SQLTemplate}; compiling that template with an
 * {@link org.eclipse.net4j.db.IDBAdapter} produces adapter-specific SQL and ordered JDBC parameter occurrences.
 * The compiled {@link org.eclipse.net4j.db.dml.SQLStatement} is prepared through an existing
 * {@link org.eclipse.net4j.db.IDBConnection}, then bound and executed using Net4j and JDBC APIs.
 * <p>
 * {@link org.eclipse.net4j.db.dml.SQLParameter} is an immutable logical identity with at most one
 * default strategy. A {@link org.eclipse.net4j.db.dml.SQLValueSetter} performs the complete JDBC operation at one
 * position using a supplied Java value. {@link org.eclipse.net4j.db.dml.SQLDefaultSetter} performs the complete
 * operation for an implementation-defined default without receiving an application value; that default need not be a
 * SQL column's {@code DEFAULT} expression and is distinct from binding a supplied null. A {@code Function} converter
 * transforms a Java value before standard JDBC binding. {@link org.eclipse.net4j.db.dml.SQLStatement} is an immutable
 * compiled definition and binding coordinator; {@link org.eclipse.net4j.db.dml.SQLParameterBinder} is a separate
 * mutable cursor that manages sequential binding positions and dispatches to the appropriate setter. The compiled
 * statement can also bind defaults directly by position or by parameter identity with
 * {@code definition.bindDefault(statement, index, defaultSetter)} or
 * {@code definition.bindDefault(statement, parameter, defaultSetter)}. Cursor {@code skip()} and {@code skip(count)}
 * move forward without binding or clearing skipped positions; those positions must be handled separately when they
 * need current values, especially when a prepared statement is reused.
 * <p>
 * For example, given schema fields {@code id}, {@code name}, an {@code IDBTable table}, an
 * {@code IDBAdapter adapter}, and an {@code IDBConnection connection}:
 * </p>
 * <pre>
 * SQLParameter idParameter = SQL.param();
 * SQLParameter nameParameter = SQL.param();
 * SQLStatement definition = SQL.select(id, name) //
 *     .from(table) //
 *     .where(id).eq(idParameter).and(name).eq(nameParameter) //
 *     .compile(adapter);
 * IDBPreparedStatement statement = definition.prepare(connection, ReuseProbability.HIGH);
 * definition.bind(statement, 42); // Partial prefixes are allowed; returns the number bound.
 * definition.bindFrom(statement, 2, "Ada"); // Binds one value at position 2; returns 1.
 * definition.bindAll(statement, 42, "Ada"); // Requires both placeholder values; returns 2.
 * definition.bindParameter(statement, idParameter, 42); // Binds every matching identity occurrence.
 * definition.binder(statement).bind(42, "Ada"); // Independent stateful cursor.
 * definition.binderFrom(statement, 2).bind("Ada"); // Independent cursor starting at position 2.
 * try
 * {
 *   ResultSet results = statement.executeQuery();
 *   // Consume results here.
 * }
 * finally
 * {
 *   statement.close();
 * }
 * </pre>
 * Each use of a parameter creates a JDBC occurrence. Identity binding matches the exact parameter object and binds
 * every occurrence; positional binding affects only the requested occurrence. Explicit setters override converters
 * and default JDBC binding; explicit converters override configured parameter strategies; otherwise a configured
 * setter precedes a configured converter and then default binding. Setters receive null and own its JDBC handling.
 * Converters bypass null input, run once for each occurrence, and use the explicit or field-inferred type if they
 * produce null. Reusing a statement after partial binding can leave older JDBC values at untouched positions.
 * Binding is not atomic: a later converter or JDBC failure can follow earlier successful JDBC setters.
 * <p>
 * A configured setter may retain captured objects through the parameter and compiled statement. The API permits
 * stateful setters; callers choose whether reuse, mutable state, thread safety, and connection-specific or
 * execution-specific dependencies fit the setter's use.
 * <p>
 * Simple fluent condition chains follow SQL precedence: AND binds more tightly than OR, so
 * {@code where(a).eq(p1).or(b).eq(p2).and(c).eq(p3)} means {@code A OR (B AND C)}. To request
 * {@code (A OR B) AND C}, compose complete conditions with {@link org.eclipse.net4j.db.dml.SQLCondition#or(SQLCondition)}
 * and {@link org.eclipse.net4j.db.dml.SQLCondition#and(SQLCondition)}. The compiler preserves this
 * structured grouping and inserts necessary parentheses.
 * </p>
 * <p>
 * INSERT offers three alternatives: {@code values(...)} uses schema field order;
 * {@code columns(...).values(...)} maps values to an explicit column list; and
 * {@code set(column).to(value)} uses explicit named assignments. Do not mix these forms.
 * </p>
 * <p>
 * A direct expression in ORDER BY is ascending by default. Use {@code SQL.asc(expression)} or
 * {@code SQL.desc(expression)} for explicit direction; {@code orderByDesc()} and
 * {@code thenByDesc()} are convenience forms. Ordering items retain their supplied order.
 * </p>
 * <p>
 * Structured expressions such as {@code SQL.add()}, {@code SQL.multiply()}, {@code SQL.and()},
 * and {@code SQL.or()} retain semantic structure for precedence-aware parentheses. By contrast,
 * {@code SQL.fragment()} accepts trusted raw text that is not parsed; explicitly group fragment
 * expressions when needed. Raw text must not contain {@code ?}, even inside a quoted literal.
 * </p>
 * <p>
 * Scalar and IN subqueries must project exactly one column; EXISTS and NOT EXISTS have no such
 * width restriction. Width is checked at compilation. Repeated {@code from()} calls create
 * comma-separated sources unless followed by joins; when joins follow multiple sources, they are
 * grouped as a CROSS JOIN left operand, and subsequent joins associate left-to-right. Arbitrary
 * explicit JOIN-tree nesting is not supported.
 * </p>
 * <p>
 * SQL NULL comparisons keep SQL semantics: {@code column = NULL} is not rewritten. Use
 * {@code SQL.isNull(expression)} or {@code SQL.isNotNull(expression)} for NULL tests.
 * </p>
 * For the complete guide, see the Net4j DB Framework Help contents.
 */
package org.eclipse.net4j.db.dml;
