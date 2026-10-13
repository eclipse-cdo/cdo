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
package org.eclipse.net4j.db.dml.fluent;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLColumnRef;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLTableRef;

/**
 * A structured fragment builder. Raw SQL text is trusted input; identifiers and parameters are
 * represented as separate structured parts so a future compiler can process them safely.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLFragmentStep
{
  /**
   * Appends trusted SQL text to the fragment. Raw text is not parsed and must not contain {@code ?}, even inside a quoted SQL literal.
   * Represent every JDBC parameter placeholder with {@link SQL#param()} or a structured fragment parameter token.
   * Generated literals such as {@code SQL.literal("?")} are separate structured expressions and are allowed. This restriction keeps positional
   * and identity-based JDBC binding reliable in Phase 3.
   *
   * @param trustedSQL trusted SQL text, supplied as a literal
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep sql(String trustedSQL);

  /**
   * Appends a schema table identifier or table reference as a structured fragment part.
   *
   * @param table the schema table or table occurrence
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep table(IDBTable table);

  /**
   * Appends a schema table identifier or table reference as a structured fragment part.
   *
   * @param table the schema table or table occurrence
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep table(SQLTableRef table);

  /**
   * Appends a schema column or qualified column reference as a structured fragment part.
   *
   * @param column the existing schema column
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep column(IDBField column);

  /**
   * Appends a schema column or qualified column reference as a structured fragment part.
   *
   * @param column the existing schema column
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep column(SQLColumnRef column);

  /**
   * Appends the supplied logical parameter identity as a placeholder part.
   *
   * @param parameter the immutable parameter identity
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep param(SQLParameter parameter);

  /**
   * Appends a typed SQL expression as a fragment part.
   *
   * @param expression the expression used by this clause
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep expression(SQLExpression expression);

  /**
   * Appends a typed SQL condition as a fragment part.
   *
   * @param condition the complete condition to add
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep condition(SQLCondition condition);

  /**
   * Appends another structured fragment without converting it to raw text.
   *
   * @param fragment the fragment to append
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLFragmentStep fragment(SQLFragment fragment);

  /**
   * Captures the operations completed so far in a new immutable template. Later calls through a retained builder stage do not alter an already built template.
   *
   * @return an immutable definition snapshot
   * @since 4.15
   */
  public SQLFragment build();
}
