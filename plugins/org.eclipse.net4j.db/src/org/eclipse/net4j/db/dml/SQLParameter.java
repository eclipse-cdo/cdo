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
 * A converter runs once per occurrence for each non-null input value. Null input bypasses the converter
 * and follows typed SQL NULL binding. A specialized binder supplied to {@link SQLStatement} takes
 * precedence over conversion and default setters and receives the original input. Converter exceptions propagate to the
 * binding caller, and converted values are never stored on the parameter or compiled statement.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLParameter extends SQLExpression
{
}
