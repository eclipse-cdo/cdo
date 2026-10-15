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

import java.sql.SQLException;
import java.util.function.Function;

/**
 * A mutable cursor that binds successive values to one prepared statement using one compiled statement.
 * Each cursor has an independent position. Failed operations leave the cursor at its starting position,
 * although JDBC effects from earlier values in a failed sequence may already have occurred. The compiled
 * statement remains immutable and does not retain cursors. Callers are responsible for positions and
 * stale JDBC parameter values when they bind only part of a reused statement.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLParameterBinder
{
  /**
   * Binds a sequence at the current position and advances after the whole sequence succeeds. The full
   * range is validated before JDBC is changed. A converter or JDBC failure leaves the cursor position
   * unchanged, although earlier JDBC positions in the sequence may already have been bound.
   *
   * @param values values in placeholder order; a single null value is passed as {@code (Object)null}
   * @return this cursor
   * @throws IllegalArgumentException if the sequence exceeds the remaining parameter range
   * @throws NullPointerException if {@code values} is {@code null}
   * @throws SQLException if a converter or JDBC setter fails
   * @since 4.15
   */
  public SQLParameterBinder bind(Object... values) throws SQLException;

  /**
   * Binds one value with a complete JDBC setter and advances after success. The setter replaces the
   * parameter's configured strategy and receives the original value including null.
   *
   * @param <JAVA> the application value type
   * @param value the application value, possibly {@code null}
   * @param setter the complete JDBC binding operation
   * @return this cursor
   * @throws SQLException if the setter reports a binding failure
   * @throws IllegalArgumentException if the cursor is at the end position
   * @throws NullPointerException if {@code setter} is {@code null}
   * @since 4.16
   */
  public <JAVA> SQLParameterBinder bindParameter(JAVA value, SQLValueSetter<? super JAVA> setter) throws SQLException;

  /**
   * Sets the current parameter to its implementation-defined default and advances after success. The setter does not
   * receive an application value; its default may be domain-specific and need not be the SQL column's {@code DEFAULT}
   * expression. This operation is distinct from binding a supplied {@code null} value.
   *
   * @param setter the complete JDBC default-value binding operation
   * @return this cursor
   * @throws SQLException if the setter reports a binding failure
   * @throws IllegalArgumentException if the cursor is at the end position
   * @throws NullPointerException if {@code setter} is {@code null}
   * @since 4.16
   */
  public SQLParameterBinder bindParameter(SQLDefaultSetter setter) throws SQLException;

  /**
   * Converts one value and applies standard JDBC binding, then advances after success. The explicit
   * converter replaces any configured parameter strategy; null input bypasses conversion and uses the
   * parameter's explicit or inferred JDBC NULL type.
   *
   * @param <JAVA> the application value type
   * @param <JDBC> the converted JDBC value type
   * @param value the application value, possibly {@code null}
   * @param converter the conversion applied to non-null values
   * @return this cursor
   * @throws SQLException if JDBC reports a binding failure
   * @throws IllegalArgumentException if the cursor is at the end position
   * @throws NullPointerException if {@code converter} is {@code null}
   * @since 4.16
   */
  public <JAVA, JDBC> SQLParameterBinder bindParameter(JAVA value, Function<JAVA, JDBC> converter) throws SQLException;

  /**
   * Returns the one-based position of the next parameter to bind.
   *
   * @return the next JDBC parameter position
   */
  public int getNextIndex();

  /**
   * Repositions this cursor to a valid one-based parameter position. The position may be from 1 through
   * parameter count plus one; the final position permits empty binding sequences only.
   *
   * @param index the position from 1 through parameter count plus one
   * @return this cursor
   * @throws IllegalArgumentException if the position is outside the valid range
   * @since 4.16
   */
  public SQLParameterBinder from(int index);

  /**
   * Advances this cursor by one parameter position without binding that position. The same cursor is returned.
   * Skipped positions remain unbound by this call and may retain old values when the prepared statement is reused;
   * use another binding operation to provide their values. Unlike {@link #from(int)}, this operation only moves
   * forward. The final valid position is parameter count plus one; skipping from it is invalid.
   *
   * @return this cursor
   * @throws IllegalArgumentException if advancing would move past the final valid position
   * @since 4.16
   */
  public SQLParameterBinder skip();

  /**
   * Advances this cursor by a nonnegative number of parameter positions without binding or clearing those positions.
   * A zero count is a valid no-op. The same cursor is returned. Positions skipped this way must be bound separately
   * if they need values; a reused prepared statement may retain previous JDBC values. This relative forward movement
   * is distinct from the absolute repositioning, forward or backward, provided by {@link #from(int)}. The destination
   * may be the final valid position, parameter count plus one, but not beyond it.
   *
   * @param count the nonnegative number of positions to skip
   * @return this cursor
   * @throws IllegalArgumentException if {@code count} is negative, overflows the valid range, or moves past the final
   *           valid position
   * @since 4.16
   */
  public SQLParameterBinder skip(int count);
}
