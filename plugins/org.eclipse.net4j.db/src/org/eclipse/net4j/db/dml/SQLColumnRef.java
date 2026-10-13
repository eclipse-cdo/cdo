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

/**
 * A column reference that may be qualified by a table occurrence.
 *
 * @author Eike Stepper
 * @since 4.15
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface SQLColumnRef extends SQLExpression
{
  /**
   * Returns the schema column represented by this reference.
   *
   * @return the referenced schema column
   */
  public IDBField getColumn();

  /**
   * Returns the qualifying table occurrence, or {@code null} for an unqualified column.
   *
   * @return the qualifying table reference
   */
  public SQLTableRef getTableRef();
}
