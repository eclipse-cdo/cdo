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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Performs the complete JDBC binding operation for one application value at one parameter position.
 * The setter receives {@code null} values and is responsible for choosing the appropriate JDBC NULL
 * operation. Setters configured on parameters are retained by compiled statements, so captured
 * objects may remain reachable for the lifetime of those definitions. Callers choose suitable reuse,
 * mutable-state, thread-safety, and connection or execution-specific lifetimes.
 *
 * @param <JAVA> the application value type
 *
 * @author Eike Stepper
 * @since 4.15
 */
@FunctionalInterface
public interface SQLValueSetter<JAVA>
{
  /**
   * Sets one application value at the supplied one-based JDBC parameter position.
   *
   * @param stmt the prepared statement receiving the value
   * @param index the one-based parameter index
   * @param value the application value, possibly {@code null}
   * @throws SQLException if the JDBC driver reports a binding failure
   */
  public void setValue(PreparedStatement stmt, int index, JAVA value) throws SQLException;
}
