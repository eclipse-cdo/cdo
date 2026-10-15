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
 * Performs the complete JDBC operation for setting the default value of one parameter occurrence without receiving an
 * application value. The default is determined by the implementation and need not be the SQL column's {@code DEFAULT}
 * expression; it may be a domain-specific value, a typed NULL, or another representation. Default-value binding is a
 * distinct operation from passing {@code null} to a {@link SQLValueSetter}. A cursor invokes the setter immediately and
 * does not retain it after the call.
 *
 * @author Eike Stepper
 * @since 4.15
 */
@FunctionalInterface
public interface SQLDefaultSetter
{
  /**
   * Sets one parameter occurrence to its implementation-defined default at the supplied one-based JDBC position.
   *
   * @param stmt the prepared statement receiving the binding
   * @param index the one-based JDBC parameter position
   * @throws SQLException if the JDBC driver reports a binding failure
   */
  public void setDefaultValue(PreparedStatement stmt, int index) throws SQLException;
}
