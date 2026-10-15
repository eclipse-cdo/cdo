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

/**
 * An immutable logical parameter identity. Reusing one instance in a definition represents multiple
 * JDBC placeholder occurrences that can be bound together by identity. {@link SQL#param(int)} supplies
 * an explicit JDBC type for typed NULL values and typed {@code setObject}; otherwise NULL uses the
 * field type inferred for each occurrence and translated by the adapter used during compilation.
 * A parameter may have no strategy, a converter, or a {@link SQLValueSetter}; converters and setters
 * are mutually exclusive configurations. Converters run once per occurrence for each non-null input
 * and their results use standard JDBC binding. Null input bypasses converters and follows typed SQL
 * NULL binding. A setter owns the complete JDBC operation and receives the original input, including
 * null. Call-specific setter and converter overrides replace the configured strategy for that binding
 * operation only. Converter exceptions propagate, and converted values are never stored on the parameter
 * or compiled statement.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLParameter extends SQLExpression
{
}
