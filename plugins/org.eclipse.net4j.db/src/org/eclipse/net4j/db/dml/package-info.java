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
 * For example, given schema fields {@code id}, {@code name}, an {@code IDBTable table}, an
 * {@code IDBAdapter adapter}, and an {@code IDBConnection connection}:
 * </p>
 * <pre>
 * SQLParameter idParameter = SQL.param();
 * SQLStatement definition = SQL.select(id, name) //
 *     .from(table) //
 *     .where(id).eq(idParameter) //
 *     .compile(adapter);
 * IDBPreparedStatement statement = definition.prepare(connection, ReuseProbability.HIGH);
 * definition.bindParameter(statement, idParameter, 42);
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
 * A parameter object is a logical identity; each use creates a JDBC occurrence. Positional binding supplies one
 * value per occurrence, while identity binding supplies one value to every occurrence of that exact object.
 * Explicit JDBC types and direct field metadata can type NULL values. A custom converter transforms non-NULL values;
 * an explicitly supplied {@link org.eclipse.net4j.db.dml.SQLParameterBinder} instead owns the JDBC setter operation.
 * For the complete guide, see the Net4j DB Framework Help contents.
 */
package org.eclipse.net4j.db.dml;
