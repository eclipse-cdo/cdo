/*
 * Copyright (c) 2007-2014, 2016, 2019, 2021-2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings
 *    Stefan Winkler - 249610: [DB] Support external references (Implementation)
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.ILobCleanup;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStore.CanHandleClientAssignedIDs;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.ddl.IDBSchema;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * The main entry point to the API of CDO's proprietary object/relational mapper.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBStore extends IStore, IDBConnectionProvider, ILobCleanup, CanHandleClientAssignedIDs
{
  /**
   * @since 2.0
   */
  public IMappingStrategy getMappingStrategy();

  /**
   * @since 4.0
   */
  public IIDHandler getIDHandler();

  /**
   * @since 4.2
   */
  public IDBDatabase getDatabase();

  public IDBAdapter getDBAdapter();

  public IDBSchema getDBSchema();

  /**
   * @since 4.2
   */
  public int getIDColumnLength();

  /**
   * @since 4.4
   */
  public int getJDBCFetchSize();

  /**
   * @since 4.2
   */
  public Map<String, String> getProperties();

  /**
   * @since 4.2
   */
  public void visitAllTables(Connection connection, TableVisitor visitor);

  /**
   * Get the meta data manager associated with this DBStore.
   *
   * @since 2.0
   */
  public IMetaDataManager getMetaDataManager();

  /**
   * @since 2.0
   */
  @Override
  public IDBStoreAccessor getReader(ISession session);

  /**
   * @since 2.0
   */
  @Override
  public IDBStoreAccessor getWriter(ITransaction transaction);

  /**
   * Called back from {@link IDBStore#visitAllTables(Connection, TableVisitor)} for all tables in the database.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  @FunctionalInterface
  public interface TableVisitor
  {
    public void visitTable(Connection connection, String name) throws SQLException;
  }

  /**
   * Contains symbolic constants that specify valid keys of {@link IRepository#getProperties() DB store properties}.
   *
   * @author Eike Stepper
   * @since 4.0
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Props
  {
    /**
     * The name of the DB schema to use for the repository.
     * If omitted the {@link IDBAdapter#getDefaultSchemaName(Connection) default schema name} of the repository's
     * {@link IDBAdapter DB adapter} is used as the schema name.
     *
     * @since 4.13
     */
    public static final String SCHEMA_NAME = "schemaName"; //$NON-NLS-1$

    /**
     * Whether to qualify table names with the {@link #SCHEMA_NAME schema name}.
     *
     * @since 4.13
     */
    public static final String PREPEND_SCHEMA_NAME = "prependSchemaName"; //$NON-NLS-1$

    /**
     * Whether to create the schema with the specified {@link #SCHEMA_NAME name} at startup time, if it does not exist.
     *
     * @since 4.13
     */
    public static final String CREATE_SCHEMA_IF_NEEDED = "createSchemaIfNeeded"; //$NON-NLS-1$

    /**
     * Period at which to execute an SQL statement to keep DB connection alive, in minutes.
     */
    public static final String CONNECTION_KEEPALIVE_PERIOD = "connectionKeepAlivePeriod"; //$NON-NLS-1$

    /**
     * Number of additional attempts to connect to the DB after initial connection failure.
     *
     * @since 4.12
     */
    public static final String CONNECTION_RETRY_COUNT = "connectionRetryCount"; //$NON-NLS-1$

    /**
     * Number of seconds to wait before additional attempts to connect to the DB after initial connection failure.
     *
     * @since 4.12
     */
    public static final String CONNECTION_RETRY_SECONDS = "connectionRetrySeconds"; //$NON-NLS-1$

    /**
     * @since 4.2
     */
    public static final String ID_COLUMN_LENGTH = "idColumnLength"; //$NON-NLS-1$

    /**
     * @since 4.2
     */
    public static final String READER_POOL_CAPACITY = "readerPoolCapacity"; //$NON-NLS-1$

    /**
     * @since 4.2
     */
    public static final String WRITER_POOL_CAPACITY = "writerPoolCapacity"; //$NON-NLS-1$

    /**
     * @since 4.3
     */
    public static final String FIELD_CONSTRUCTION_TRACKING = "fieldConstructionTracking"; //$NON-NLS-1$

    /**
     * @since 4.4
     */
    public static final String DROP_ALL_DATA_ON_ACTIVATE = "dropAllDataOnActivate"; //$NON-NLS-1$

    /**
     * @since 4.4
     */
    public static final String JDBC_FETCH_SIZE = "jdbcFetchSize"; //$NON-NLS-1$

    /**
     * Can have the following values:
     * <ul>
     * <li>"DISABLED" - No model evolution support</li>
     * <li>"GUARD" - Prevent model changes only</li>
     * <li>"DRY" - Evolve models without persisting changes</li>
     * <li>"EVOLVE" - Evolve models automatically (default)</li>
     * @since 4.14
     */
    public static final String MODEL_EVOLUTION_MODE = "modelEvolutionMode"; //$NON-NLS-1$

    /**
     * @since 4.14
     */
    public static final String MODEL_EVOLUTION_SUPPORT_TYPE = "modelEvolutionSupportType"; //$NON-NLS-1$

    /**
     * @since 4.14
     */
    public static final String MODEL_EVOLUTION_SUPPORT_DESCRIPTION = "modelEvolutionSupportDescription"; //$NON-NLS-1$

    /**
     * @author Eike Stepper
     * @since 4.14
     */
    public enum ModelEvolutionMode
    {
      DISABLED, GUARD, DRY, EVOLVE;

      public static ModelEvolutionMode parse(String str)
      {
        if (str != null)
        {
          for (ModelEvolutionMode mode : values())
          {
            if (mode.name().equalsIgnoreCase(str))
            {
              return mode;
            }
          }
        }

        return null;
      }
    }
  }
}
