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
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Architecture and Core Concepts
 * <p>
 * Net4j DB connects database dialects, a managed schema model, coordinated schema evolution, managed JDBC
 * connections, per-connection prepared-statement caches, and structured SQL construction. The parts are useful
 * individually, but their cooperation is what lets an application keep running managed queries while schema
 * metadata evolves and cached statements are invalidated coherently.
 * <p>
 * JDBC remains the execution substrate: drivers provide {@code Connection}, SQL execution, transactions, and result
 * sets. Net4j DB adds a database ownership boundary, adapter-directed metadata and DDL, statement lifecycle
 * coordination, and a DML compilation pipeline. It does not pool connections or replace database-server
 * transaction isolation. This article introduces the shared architecture; {@link Doc02_DataDefinition} explains
 * schema design and evolution, and {@link Doc03_DataManipulation} explains SQL construction and binding.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc01_GeneralUtilities
{
  /**
   * How the Pieces Fit Together
   * <p>
   * Start from {@link IDBDatabase}. It is created with an {@link IDBAdapter} and an
   * {@link IDBConnectionProvider}; it reads and locks a managed {@link IDBSchema}, creates and tracks
   * {@link IDBConnection}s, opens schema transactions, and coordinates schema-change notifications and cache
   * invalidation. Each managed connection wraps one JDBC connection and owns its own prepared-statement cache.
   * <p>
   * DML definitions use schema metadata to create an adapter-neutral {@link SQLTemplate}.
   * Compiling the template with the adapter produces an adapter-specific {@link SQLStatement}; preparing it on a managed connection checks out an
   * {@link IDBPreparedStatement} from that connection's cache or JDBC driver. The returned statement is still a
   * caller-managed resource.
   * <p>
   * The first diagram shows these relationships at the public API level. Arrows distinguish creation and ownership
   * from compilation and preparation. The database coordinates schema access across its managed connections but
   * does not own a single shared statement cache.
   * <p>
   * {@image net4j-db-architecture.svg}
   * <p>
   * Follow the lower path for query execution: schema-backed expressions are captured in a template, compiled by the
   * adapter, and prepared through a managed connection. The upper path shows that the same database instance owns
   * the managed schema snapshot and connection population, while each connection retains its own statement cache.
   *
   * @author Eike Stepper
   */
  public class Architecture
  {
  }

  /**
   * Adapters, Providers, and Database Instances
   * <p>
   * An {@link IDBAdapter} handles vendor-sensitive behavior: identifier quoting, logical type adaptation, schema
   * inspection and updates, and selected SQL-generation or exception-classification rules. It is not a pool and does
   * not supply the application's connection-management policy. An {@link IDBConnectionProvider} supplies JDBC
   * connections; applications commonly adapt a {@code DataSource} with the {@link DBUtil} helper method
   * {@code createConnectionProvider(DataSource)}.
   * <p>
   * Obtain contributed adapters from {@link DBUtil#getDBAdapter(String)} or the current
   * {@code IDBAdapter.Registry.INSTANCE}. In an Eclipse runtime the adapter registry can be populated by the
   * {@code org.eclipse.net4j.db.dbAdapters} extension point. Standalone applications must register or otherwise make
   * their adapter available. Adapter identity includes a name and version. The registry lookup by name selects the
   * highest registered version; select an exact identity when reproducible adapter behavior is important.
   * <p>
   * To resolve an adapter from an existing JDBC connection, call
   * {@code IDBAdapter.Registry.INSTANCE.getAdapter(connection)}. If the connection is an {@link IDBConnection} whose
   * {@link IDBConnection#getDatabase() database} has an adapter, that association is authoritative and is returned
   * directly. An ordinary {@link Connection} is resolved from {@link DatabaseMetaData#getDatabaseProductName()}
   * and any optional driver-name or URL constraints declared for matching adapters. A metadata access failure is
   * reported as {@link DBException}; an unknown or ambiguous product returns {@code null}. The lookup does not take
   * ownership of or close the supplied connection.
   * <p>
   * The {@code dbAdapters} extension point lets a contribution describe recognition without constructing its adapter.
   * Its optional {@code productNames} attribute lists comma-separated exact JDBC product names; when omitted, the
   * adapter name is used. Product names are trimmed and compared case-insensitively. A product name containing a comma
   * must be matched with {@code productNamePattern}. The optional
   * {@code productNamePattern} is a case-insensitive Java regular expression matched against the complete trimmed
   * product name, in addition to the exact names. Optional {@code driverNamePattern} and {@code urlPattern} regular
   * expressions constrain the JDBC driver name and URL respectively; when present, each value must be available and
   * match the complete trimmed value. Programmatic adapters provide the equivalent metadata by overriding
   * {@link IDBAdapter#getDatabaseProductNames()}, {@link IDBAdapter#getDatabaseProductNamePattern()},
   * {@link IDBAdapter#getDatabaseDriverNamePattern()}, and {@link IDBAdapter#getDatabaseURLPattern()}.
   * <p>
   * After metadata constraints are applied, a direct match between the product name and an adapter name takes priority
   * over aliases. If multiple versions of the selected adapter name are registered, the highest adapter version is
   * selected. If matches remain for different adapter names, the result is ambiguous and the lookup returns
   * {@code null}. Only the selected lazy descriptor is materialized. Adapter versions describe adapter
   * implementations; they are independent of the database product version and JDBC driver version.
   * <p>
   * {@link #openManagedDatabase OpenManagedDatabase.java} and {@link #resolveAdapter(Connection) ResolveAdapter.java}
   *
   * @author Eike Stepper
   */
  public class AdaptersAndDatabases
  {
    /**
     * @snip
     */
    public IDBAdapter resolveAdapter(Connection connection)
    {
      // Input: an open ordinary JDBC connection; this method leaves it open.
      IDBAdapter adapter = IDBAdapter.Registry.INSTANCE.getAdapter(connection);
      if (adapter == null)
      {
        throw new DBException("No unambiguous database adapter for JDBC connection");
      }

      return adapter;
    }

    /**
     * @snip
     */
    public void openManagedDatabase(String adapterName, IDBConnectionProvider provider)
    {
      // Inputs: a registered adapter name and a provider backed by the application's DataSource.
      IDBAdapter adapter = DBUtil.getDBAdapter(adapterName);
      if (adapter == null)
      {
        throw new DBException("No registered database adapter: " + adapterName);
      }

      try (IDBDatabase database = DBUtil.openDatabase(adapter, provider, null))
      {
        IDBSchema schema = database.getSchema();
        System.out.println("Connected to schema " + schema.getName() + " with " + schema.getTables().length + " tables");
      }
    }
  }

  /**
   * Managed Schema and Connections
   * <p>
   * A standalone schema made with {@link DBUtil#createSchema(String)} is an ordinary mutable metadata model. It does
   * not represent a live catalog until an API reads from or writes to a database. When {@link DBUtil#openDatabase}
   * constructs an {@link IDBDatabase}, the implementation reads the selected catalog through a connection and locks
   * that schema model. Callers should treat {@link IDBDatabase#getSchema()} as the database's managed snapshot, not
   * as an editable draft. For changes, open an {@link IDBSchemaTransaction}; its working copy is the intended edit
   * surface. See {@link Doc02_DataDefinition.SchemaModel}.
   * <p>
   * Each call to {@link IDBDatabase#getConnection()} asks the provider for a JDBC connection, lets the adapter
   * modify it, wraps it as an {@link IDBConnection}, and registers it with the database. The database can then
   * coordinate cache invalidation across those open connections. Closing the database closes its managed
   * connections. Close individual connections when finished even when the database itself is long-lived.
   * <p>
   * A connection created by the framework is put into manual-commit mode. Ordinary row changes therefore need an
   * explicit {@code commit()} or {@code rollback()}. This JDBC transaction is independent of the schema working
   * copy and its adapter-driven DDL lifecycle.
   * <p>
   * The following example executes and commits a customer update before closing its statement and managed
   * connection: {@link #updateCustomer(IDBDatabase, String) UpdateCustomer.java}.
   *
   * @author Eike Stepper
   */
  public class ManagedSchemasAndConnections
  {
    /**
     * @snip
     */
    public void updateCustomer(IDBDatabase database, String customerName) throws SQLException
    {
      // Input: an open managed database; this operation owns the connection it obtains.
      try (IDBConnection connection = database.getConnection())
      {
        IDBPreparedStatement statement = connection.prepareStatement("UPDATE CUSTOMER SET NAME = ? WHERE ID = ?", ReuseProbability.HIGH);

        try
        {
          statement.setString(1, customerName);
          statement.setInt(2, 42);
          statement.executeUpdate();
          connection.commit();
        }
        catch (SQLException | RuntimeException ex)
        {
          connection.rollback();
          throw ex;
        }
        finally
        {
          statement.close();
        }
      }
    }
  }

  /**
   * Coordinated Schema Access and Statement Caches
   * <p>
   * Net4j DB coordinates managed preparation with schema transaction completion so that a statement already in use
   * is not silently reused across a schema-cache invalidation. When {@code DBConnection.prepareStatement(...)} is
   * called, it first asks its database for a schema read-access token. The token is associated with the checked-out
   * {@code DBPreparedStatement}. It remains active for the statement's checkout lifetime, including execution and
   * result processing, and is released in the statement-close/return path. Returning a statement can put it into that
   * connection's local cache, but the token is released after the return operation.
   * <p>
   * When a nonempty schema delta reaches transaction completion, {@code DBDatabase.closeSchemaTransaction(...)}
   * requests exclusive schema write access. The queue admits compatible readers together, orders writers against
   * readers, and prevents later readers from continually passing a waiting writer. A writer waits until earlier
   * read tokens have ended. The wait is bounded by the implementation's schema-access timeout; a timeout is a
   * runtime failure, not a database-server lock timeout. A checked-out statement that is forgotten can therefore
   * delay schema completion until that timeout. Close each statement promptly, even when its SQL is likely to be
   * reused.
   * <p>
   * Once exclusive access reaches the head of the queue, the database invalidates each currently managed
   * connection's local cached statements, emits a schema-changed event carrying the delta, and then releases the
   * write token. A later managed preparation obtains a read token after this phase and cannot take a stale entry
   * from the cleared caches. The optional {@code DBConnection.checkOuts} structure is enabled only by
   * {@code VALIDATE_CHECKOUTS}; it adds validation at invalidation time and is not the synchronization mechanism.
   * The queue and tokens provide the coordination.
   * <p>
   * The second diagram separates this cache-coordination phase from physical DDL. In the current implementation,
   * {@code DBSchemaTransaction.run()} calls {@code IDBAdapter.updateSchema(...)} first. In the base
   * {@code DBAdapter}, {@code updateSchema} applies the delta to in-memory schema metadata before traversing it to
   * execute DDL. The transaction's {@code finally} path then calls completion with that delta even if the adapter
   * update throws, so a nonempty delta can still trigger cache invalidation and event delivery on a failing DDL
   * path. An event or the current in-memory model is not proof that the physical update succeeded. In the
   * connection-backed form, {@code commit()} calls JDBC {@code commit()} after {@code run()} returns; when
   * {@code run()} throws a checked SQL failure, the implementation attempts rollback instead. This ordering does not
   * put all DDL execution under the schema-access writer, establish server isolation, or guarantee identical DDL rollback
   * behavior across adapters. An adapter or database may have its own DDL behavior, including implicit commits.
   * <p>
   * Only preparations made through managed {@link IDBConnection}s participate. Raw JDBC connections/statements
   * acquired elsewhere bypass these tokens and cache invalidation.
   * <p>
   * {@image net4j-db-schema-coordination.svg}
   * <p>
   * The practical rule is simple: finish and close every statement before committing a schema change, especially
   * when the schema transaction is opened on that same connection. Otherwise the writer can wait on a read token
   * held by the caller itself until the queue timeout. After completion, prepare through the managed connection
   * again; the old cached statement objects have been discarded.
   * <p>
   * {@link #coordinateStatementAndSchemaChange(IDBDatabase, IDBAdapter) CoordinateStatementAndSchemaChange.java}
   *
   * @author Eike Stepper
   */
  public class SchemaAccessAndCaching
  {
    /**
     * @snip
     */
    public void coordinateStatementAndSchemaChange(IDBDatabase database, IDBAdapter adapter) throws SQLException
    {
      // Inputs: an open database whose managed schema contains CUSTOMER.ID.
      try (IDBConnection connection = database.getConnection())
      {
        try (IDBPreparedStatement beforeChange = connection.prepareStatement("SELECT ID FROM CUSTOMER WHERE ID = ?", ReuseProbability.HIGH))
        {
          beforeChange.setInt(1, 42);

          try (IDBResultSet rows = beforeChange.executeQuery())
          {
            while (rows.next())
            {
              System.out.println("Customer ID: " + rows.getInt(1));
            }
          }
        }

        try (IDBSchemaTransaction schemaTransaction = connection.openSchemaTransaction())
        {
          IDBTable customer = schemaTransaction.getWorkingCopy().getTable("CUSTOMER");
          customer.addField("LOYALTY_CODE", DBType.VARCHAR, 32, false);
          schemaTransaction.commit();
        }

        try (IDBPreparedStatement afterChange = connection.prepareStatement("SELECT ID FROM CUSTOMER WHERE ID = ?", ReuseProbability.HIGH))
        {
          afterChange.setInt(1, 42);
          try (IDBResultSet rows = afterChange.executeQuery())
          {
            while (rows.next())
            {
              System.out.println("Customer ID after schema change: " + rows.getInt(1));
            }
          }
        }
      }
    }
  }

  /**
   * Ownership, Transactions, and Failure Handling
   * <p>
   * A managed connection belongs to the {@link IDBDatabase} while open and should also be closed by the code that
   * acquired it. A prepared statement belongs to that connection; close it after closing its result sets. For a
   * query, closing the {@link IDBResultSet} does not return the prepared statement to its cache. For updates, decide
   * whether the surrounding JDBC transaction should commit or roll back before releasing the connection. The
   * connection's close path performs cleanup, but it is not a substitute for handling the operation's outcome.
   * <p>
   * A schema transaction is separate. {@link IDBDatabase#openSchemaTransaction()} opens one that obtains a
   * connection for its adapter update when committed; {@link IDBConnection#openSchemaTransaction()} associates the
   * schema transaction with that existing managed connection. In the latter case, the schema transaction's commit
   * path commits that connection after the adapter update and cache-coordination completion. Do not assume the
   * operation has portable atomic-DDL semantics. Close the schema transaction in a finally block; after a successful
   * commit it has already completed, so the subsequent close is safe cleanup.
   * <p>
   * Net4j wraps JDBC/provider/adapter failures as {@link DBException} in APIs whose contracts are unchecked, while
   * inherited JDBC methods can still throw {@code SQLException}. Preserve the cause and report which phase failed:
   * connection acquisition, prepare/bind, SQL execution, DDL update, coordination timeout, or commit. If schema
   * completion fails, do not infer the physical database state solely from the in-memory model or event; inspect the
   * target database using its supported tooling and adapter behavior.
   *
   * @author Eike Stepper
   */
  public class LifecycleAndFailures
  {
  }

  /**
   * DBUtil and Supporting Facilities
   * <p>
   * {@link DBUtil} provides focused helpers around the architecture: adapter lookup, connection-provider creation,
   * opening a database, creating/copying/reading schema models, SQL-type lookup, naming and identifier utilities,
   * transaction helpers, and quiet resource cleanup. Use managed database APIs where coordination and caching matter;
   * direct JDBC utilities remain appropriate for standalone metadata work or integrations that intentionally do not
   * use a managed database instance. {@link DBType} represents Net4j's logical SQL types; adapters decide how those
   * types map to a particular database. The current structured DML API is described in
   * {@link Doc03_DataManipulation}.
   *
   * @author Eike Stepper
   */
  public class Utilities
  {
  }
}
