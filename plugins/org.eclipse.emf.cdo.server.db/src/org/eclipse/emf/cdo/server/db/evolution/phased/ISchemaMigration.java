/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public boolean migrateSchema(Context context, IDBStoreAccessor accessor) throws SQLException;

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

  /**
   * Indicates that particular model changes are not allowed during model evolution.
   *
   * @author Eike Stepper
   * @noreference This package is currently considered <i>provisional</i>.
   * @noimplement This package is currently considered <i>provisional</i>.
   * @noextend This package is currently considered <i>provisional</i>.
   */
  public static final class ModelEvolutionNotAllowedException extends CDOException
  {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ModelEvolutionNotAllowedException.
     */
    public ModelEvolutionNotAllowedException(String message)
    {
      super(message);
    }
  }
}
