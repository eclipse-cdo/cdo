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
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * Internal capability for mappings that can create their physical schema before transactional data is written.
 */
interface ISchemaPreparable
{
  /**
   * Prepares the physical schema of the mapping.
   * <p>
   * This method is called before any transactional data is written to the database.
   * Implementations should create any necessary tables, indexes, or other database objects required by the mapping.
   */
  public void prepareSchema(IDBStoreAccessor accessor);
}
