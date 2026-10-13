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
package org.eclipse.net4j.internal.db.dml;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLColumnRef;
import org.eclipse.net4j.db.dml.SQLTableRef;

import java.util.Objects;

/**
 * Immutable reference to a table occurrence in a statement.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class TableRef implements SQLTableRef
{
  private final IDBTable table;

  private final String alias;

  public TableRef(IDBTable table, String alias)
  {
    this.table = Objects.requireNonNull(table, "table");
    this.alias = alias;
  }

  @Override
  public IDBTable getTable()
  {
    return table;
  }

  @Override
  public String getAlias()
  {
    return alias;
  }

  @Override
  public SQLColumnRef column(IDBField column)
  {
    return new Expression.Column(Objects.requireNonNull(column, "column"), this);
  }

  @Override
  public String toString()
  {
    return alias == null ? table.getName() : table.getName() + " AS " + alias;
  }
}
