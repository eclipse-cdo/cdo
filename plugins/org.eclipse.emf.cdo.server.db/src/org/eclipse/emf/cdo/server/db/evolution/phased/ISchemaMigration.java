/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.evolution.phased;

import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import java.sql.SQLException;

/**
 * Interface to complement {@link IMappingStrategy} with schema migration capabilities.
 *
 * @author Eike Stepper
 * @since 4.14
 * @noreference This package is currently considered <i>provisional</i>.
 * @noimplement This package is currently considered <i>provisional</i>.
 * @noextend This package is currently considered <i>provisional</i>.
 */
public interface ISchemaMigration extends IMappingStrategy
{
  /**
   * Migrates the database schema to match the new models in the given context.
   */
  public void migrateSchema(Context context, IDBStoreAccessor accessor) throws SQLException;

  /**
   * Indicates whether the mapping strategy supports schema migration.
   *
   * @author Eike Stepper
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public static final class SchemaMigrationNotSupportedException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a SchemaMigrationNotSupportedException.
     */
    public SchemaMigrationNotSupportedException()
    {
      super("Schema migration not supported");
    }
  }
}
