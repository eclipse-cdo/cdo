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

import java.util.Objects;

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
   */
  public SQLSelectProjectionStep distinct();

  /**
   * Adds one or more comma-separated table occurrences. This stage permits repeated additions until a JOIN is started.
   *
   * @param first the first table occurrence in the FROM clause
   * @param others additional table occurrences appended in comma-separated order
   *
   * @return the next valid fluent stage
   */
  public SQLSelectFromStep from(IDBTable first, IDBTable... others);

  /**
   * Adds provider-supplied tables in order, resolving each provider immediately.
   *
   * @param first the first table provider
   * @param others additional table providers
   * @return the next valid fluent stage
   * @throws NullPointerException if any provider or supplied table is {@code null}
   */
  public default SQLSelectFromStep from(IDBTable.Provider first, IDBTable.Provider... others)
  {
    Objects.requireNonNull(first, "first");
    Objects.requireNonNull(others, "others");

    IDBTable[] tables = new IDBTable[others.length + 1];
    tables[0] = Objects.requireNonNull(first.getTable(), "table");

    for (int i = 0; i < others.length; i++)
    {
      tables[i + 1] = Objects.requireNonNull(Objects.requireNonNull(others[i], "provider").getTable(), "table");
    }

    IDBTable[] remaining = new IDBTable[tables.length - 1];
    System.arraycopy(tables, 1, remaining, 0, remaining.length);
    return from(tables[0], remaining);
  }

  /**
   * Adds one or more comma-separated table occurrences. This stage permits repeated additions until a JOIN is started.
   *
   * @param first the first table occurrence in the FROM clause
   * @param others additional table occurrences appended in comma-separated order
   *
   * @return the next valid fluent stage
   */
  public SQLSelectFromStep from(SQLTableRef first, SQLTableRef... others);
}
