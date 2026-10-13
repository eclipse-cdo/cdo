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

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;

/**
 * A table occurrence in a statement, optionally carrying a SQL table alias.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLTableRef
{
  /**
   * Returns the schema table represented by this occurrence.
   *
   * @return the referenced schema table
   */
  public IDBTable getTable();

  /**
   * Returns the SQL alias, or {@code null} when this occurrence is unaliased.
   *
   * @return the table alias
   */
  public String getAlias();

  /**
   * Refers to a column through this table occurrence. The field must belong to the referenced schema table; compilation
   * qualifies the column with this occurrence's alias, when present.
   *
   * @param column a column belonging to the referenced table
   * @return a qualified column reference
   */
  public SQLColumnRef column(IDBField column);
}
