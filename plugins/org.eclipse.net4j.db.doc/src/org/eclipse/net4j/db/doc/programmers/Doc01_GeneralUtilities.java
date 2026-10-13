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
package org.eclipse.net4j.db.doc.programmers;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBResultSet;
import org.eclipse.net4j.db.ddl.IDBSchema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * General Utilities
 * <p>
 * Net4j DB is a small JDBC-based database framework used by Net4j and CDO as well as by standalone Java applications.
 * It supplies a database-adapter boundary, a schema model, managed connection and prepared-statement abstractions,
 * and utilities for recurring database work. It does not replace JDBC: JDBC still carries connections, transactions,
 * result sets, and vendor drivers. Net4j DB adds adapter-aware schema and statement facilities around those objects.
 * <p>
 * The responsibilities are intentionally split. The article {@link Doc02_DataDefinition} covers schema metadata,
 * DDL creation, comparison, and schema evolution. {@link Doc03_DataManipulation} covers structured SQL construction
 * and execution. This article establishes the infrastructure and ownership rules shared by both.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc01_GeneralUtilities
{
  /**
   * Framework Overview
   * <p>
   * The public entry points live primarily in {@code org.eclipse.net4j.db}, with schema types in
   * {@code org.eclipse.net4j.db.ddl} and the structured DML API in {@code org.eclipse.net4j.db.dml}. An
   * {@link IDBAdapter} supplies database-vendor behavior; {@link IDBDatabase} groups that adapter with a connection
   * provider, a schema model, and a statement cache. Connections and statements remain JDBC resources and should be
   * scoped to the operation that uses them.
   * <p>
   * The DB adapter registry is the normal way to select a contributed adapter. In an Eclipse runtime adapters may
   * be contributed through the {@code org.eclipse.net4j.db.dbAdapters} extension point. Standalone applications must
   * arrange for the adapter implementation to be registered. An adapter is a dialect and capability provider, not
   * a connection pool; connection acquisition belongs to a data source or connection provider.
   * <p>
   * {@link AdaptersAndTypes#findAdapter(String) FindAdapter.java}
   *
   * @author Eike Stepper
   */
  public class FrameworkOverview
  {
  }

  /**
   * Adapters and SQL Types
   * <p>
   * An adapter centralizes behavior that cannot be assumed to be identical across JDBC databases: schema reading,
   * type adaptation, type lengths and indexability, reserved words, identifier case rules, SQL generation for schema
   * changes, and recognition of selected SQL exceptions. The adapter also quotes individual identifiers and maps a
   * {@link DBType} to a JDBC type code when a typed SQL NULL is needed. The new DML compiler uses the adapter for
   * identifier quoting and NULL type mapping; it does not make arbitrary raw SQL fragments portable.
   * <p>
   * {@link DBUtil#getDBAdapter(String)} looks up the highest registered version for a name. Use the registry directly
   * for an exact name/version identity. {@link IDBAdapter#REGISTRY} is deprecated; use
   * {@code IDBAdapter.Registry.INSTANCE}. {@link DBType} describes the logical SQL types understood by Net4j DB,
   * while JDBC {@link Types} codes describe JDBC binding and metadata. Use adapter adaptation rather than
   * assuming every database supports every logical type unchanged.
   *
   * @author Eike Stepper
   */
  public class AdaptersAndTypes
  {
    /**
     * @snip
     */
    public IDBAdapter findAdapter(String adapterName)
    {
      IDBAdapter adapter = DBUtil.getDBAdapter(adapterName);
      if (adapter == null)
      {
        throw new IllegalArgumentException("No DB adapter is registered for " + adapterName);
      }

      return adapter;
    }
  }

  /**
   * Opening a Database
   * <p>
   * {@link DBUtil#openDatabase(IDBAdapter, IDBConnectionProvider, String)} creates the Net4j database facade. The
   * supplied provider owns the mechanics of obtaining JDBC connections; the database owns the connections it obtains
   * and its prepared-statement cache. Closing the database releases its managed resources. Do not close a connection
   * obtained from the database and then continue using it, and do not close the provider's underlying data source as
   * if it were owned by an individual operation.
   * <p>
   * A JDBC transaction and a schema transaction are different scopes. Application DML uses the connection's normal
   * JDBC transaction methods. Schema edits should use {@link IDBDatabase#openSchemaTransaction()} and are discussed
   * in {@link Doc02_DataDefinition.SchemaEvolution}. Database startup does not implicitly create every table in a
   * schema model.
   * <p>
   * {@link #useDatabase(IDBAdapter, IDBConnectionProvider) UseDatabase.java}
   *
   * @author Eike Stepper
   */
  public class OpeningAndManagingDatabases
  {
    /**
     * @snip
     */
    public void useDatabase(IDBAdapter adapter, IDBConnectionProvider provider) throws SQLException
    {
      IDBDatabase database = DBUtil.openDatabase(adapter, provider, null);

      try
      {
        IDBConnection connection = database.getConnection();

        try
        {
          connection.setAutoCommit(false);

          try
          {
            // Perform work through this connection.
            connection.commit();
          }
          catch (SQLException ex)
          {
            connection.rollback();
            throw ex;
          }
        }
        finally
        {
          connection.close();
        }
      }
      finally
      {
        database.close();
      }
    }
  }

  /**
   * Connections, Statements, and Ownership
   * <p>
   * {@link IDBConnection} extends JDBC {@link Connection} and adds the Net4j database association, schema-transaction
   * access, and prepared-statement caching. Prepare through {@link IDBConnection#prepareStatement(String,
   * ReuseProbability)} when using the Net4j-managed cache. A {@link ReuseProbability} is a hint about how likely the
   * SQL is to be prepared again; it is not a guarantee that a statement is cached.
   * <p>
   * {@link IDBPreparedStatement} extends JDBC prepared-statement behavior and is returned to the caller. The caller
   * binds and executes it, consumes and closes any result set, and closes the statement. A low reuse hint is
   * appropriate for one-off SQL, while repeatedly executed stable SQL can use a higher hint. Keep SQL construction
   * separate from execution; {@link Doc03_DataManipulation} shows the template and compiled-statement lifecycle.
   * <p>
   * {@link #query(IDBConnection, String) Query.java}
   *
   * @author Eike Stepper
   */
  public class PreparedStatementsAndCaching
  {
    /**
     * @snip
     */
    public void query(IDBConnection connection, String sql) throws SQLException
    {
      IDBPreparedStatement statement = connection.prepareStatement(sql, ReuseProbability.HIGH);

      try
      {
        try (IDBResultSet resultSet = statement.executeQuery())
        {
          while (resultSet.next())
          {
            // Read columns from the current row.
          }
        }
      }
      finally
      {
        statement.close();
      }
    }
  }

  /**
   * DBUtil in Practice
   * <p>
   * {@link DBUtil} groups convenience operations by use case rather than being a second database abstraction. It
   * creates database facades and connection providers, looks up adapters, creates/copies/reads schema models, manages
   * JDBC resource cleanup, and provides helpers for auto-commit, rollback, identifier names, and schema export. For
   * schema comparison and migration, prefer the schema and delta APIs described in
   * {@link Doc02_DataDefinition}. For structured new SQL, prefer {@link Doc03_DataManipulation}; legacy SQL string
   * helpers remain useful for existing code but should not be mistaken for a typed query builder.
   * <p>
   * The convenience {@code close(...)} overloads accept null and return a failure exception when quiet-close mode is
   * in effect; close failures are logged by default and can be configured to become {@link DBException}s. Prefer
   * try-with-resources for JDBC resources whose close contract is directly available, and use DBUtil's methods where
   * integration with existing Net4j DB cleanup behavior is useful.
   * <p>
   * {@link #readAndCopySchema(IDBAdapter, Connection) ReadAndCopySchema.java}
   *
   * @author Eike Stepper
   */
  public class DBUtilUseCases
  {
    /**
     * @snip
     */
    public IDBSchema readAndCopySchema(IDBAdapter adapter, Connection connection) throws SQLException
    {
      IDBSchema liveSchema = DBUtil.readSchema(adapter, connection, "APP");
      IDBSchema workingSchema = DBUtil.copySchema(liveSchema);
      return workingSchema;
    }
  }

  /**
   * Errors and Failure Handling
   * <p>
   * Net4j DB reports framework-level failures as unchecked {@link DBException}s in APIs that cannot expose checked
   * JDBC exceptions, while JDBC methods inherited by {@link IDBConnection} may still throw {@link SQLException}.
   * Adapter methods classify selected vendor exceptions, but callers should not assume every database error can be
   * translated into a portable category. Preserve the original cause when reporting failures.
   * <p>
   * When application work fails inside a manually controlled JDBC transaction, roll it back before releasing the
   * connection. Closing a Net4j connection with auto-commit disabled attempts a rollback as a final cleanup measure;
   * that cleanup is not a substitute for explicit transaction outcome handling. A {@link DBException} can wrap
   * adapter, provider, or JDBC failures, so inspect its cause for diagnostics.
   *
   * @author Eike Stepper
   */
  public class ErrorsAndFailures
  {
  }
}
