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

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLTableRef;

/**
 * A SELECT whose projection is complete and whose source clause is still being assembled.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLSelectProjectionStep
{
  /**
   * Marks the projection as DISTINCT before a source is selected.
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectProjectionStep distinct();

  /**
   * Adds one or more comma-separated table occurrences. This stage permits repeated additions until a JOIN is started.
   *
   * @param first the first table occurrence in the FROM clause
   * @param others additional table occurrences appended in comma-separated order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectFromStep from(IDBTable first, IDBTable... others);

  /**
   * Adds one or more comma-separated table occurrences. This stage permits repeated additions until a JOIN is started.
   *
   * @param first the first table occurrence in the FROM clause
   * @param others additional table occurrences appended in comma-separated order
   *
   * @return the next valid fluent stage
   * @since 4.15
   */
  public SQLSelectFromStep from(SQLTableRef first, SQLTableRef... others);
}
