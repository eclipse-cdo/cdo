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

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.db.ddl.delta.IDBTableDelta;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Data Definition
 * <p>
 * Net4j DB models database structure as metadata, then delegates physical DDL to the selected adapter. That separation
 * gives an application one schema vocabulary for a standalone desired model, a catalog snapshot, or the managed
 * schema owned by {@link IDBDatabase}. The model itself is not the database: adding a field to an ordinary schema
 * changes Java metadata, while creating or evolving a managed schema requires a database operation.
 * <p>
 * Schema evolution also connects Article 1's managed connections and caches. A schema transaction updates the
 * managed model through the adapter and then coordinates cache invalidation across the database's managed
 * connections. This coordination does not make every vendor's DDL transactional or place all physical DDL under the
 * cache-coordination lock. For the broader architecture, see {@link Doc01_GeneralUtilities}; for row-level SQL, see
 * {@link Doc03_DataManipulation}.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_DataDefinition
{
  /**
   * Standalone and Managed Schema Models
   * <p>
   * {@link DBUtil#createSchema(String)} creates an independent, mutable metadata model. It is appropriate for
   * defining an application's desired tables before opening a database. {@link DBUtil#readSchema(IDBAdapter,
   * Connection, String)} obtains a catalog snapshot through an adapter. When {@link DBUtil#openDatabase} creates a
   * managed database, its implementation reads a schema and locks that model; callers should use
   * {@link IDBDatabase#getSchema()} for inspection and use an {@link IDBSchemaTransaction} working copy for edits.
   * <p>
   * A standalone schema can be copied, compared, exported, created, or dropped using the overload appropriate to
   * the application. Those methods call the supplied adapter and may use the supplied connection, data source, or
   * provider. They perform actual DDL only when an operation explicitly asks for creation or dropping. They do not
   * create a coordinated managed database merely because the schema model is passed to them.
   * <p>
   * {@link #defineSalesSchema() DefineSalesSchema.java}
   *
   * @author Eike Stepper
   */
  public class SchemaModel
  {
    /**
     * @snip
     */
    public void defineSalesSchema()
    {
      IDBSchema sales = DBUtil.createSchema("SALES");

      IDBTable customer = sales.addTable("CUSTOMER");
      IDBField customerID = customer.addField("ID", DBType.INTEGER, true);
      customer.addField("NAME", DBType.VARCHAR, 160, true);
      customer.addField("EMAIL", DBType.VARCHAR, 254, false);
      customer.addIndex("PK_CUSTOMER", IDBIndex.Type.PRIMARY_KEY, customerID);
      customer.addIndex("UQ_CUSTOMER_EMAIL", IDBIndex.Type.UNIQUE, "EMAIL");

      IDBTable orders = sales.addTable("ORDERS");
      IDBField orderID = orders.addField("ID", DBType.BIGINT, true);
      IDBField orderCustomerID = orders.addField("CUSTOMER_ID", DBType.INTEGER, true);
      orders.addField("TOTAL", DBType.DECIMAL, 12, 2, true);
      orders.addField("STATUS", DBType.VARCHAR, 24, true);
      orders.addIndex("PK_ORDERS", IDBIndex.Type.PRIMARY_KEY, orderID);
      orders.addIndex("IX_ORDERS_CUSTOMER", IDBIndex.Type.NON_UNIQUE, orderCustomerID);
    }
  }

  /**
   * Tables, Fields, Indexes, and Types
   * <p>
   * A table owns ordered fields and indexes. A field records a logical {@link DBType}, precision, scale, and
   * nullability. An index records its kind (primary key, unique, or non-unique) and ordered fields. The model checks
   * structural relationships such as whether an index field belongs to its table; it does not prove that a target
   * database accepts every type, name, precision, or index form. The adapter supplies type adaptation, identifier
   * rules, maximum lengths, and vendor-specific DDL.
   * <p>
   * Use descriptive, stable names and model constraints explicitly. For example, {@code CUSTOMER.EMAIL} can have a
   * unique index, while {@code ORDERS.CUSTOMER_ID} can have a non-unique index for common customer order lookups.
   * A model-level index does not itself create or verify a physical index.
   *
   * @author Eike Stepper
   */
  public class TablesFieldsAndIndexes
  {
  }

  /**
   * Creating, Reading, and Comparing Schemas
   * <p>
   * {@link IDBSchema#create(IDBAdapter, DataSource)} delegates creation to the adapter. The overloads accepting a
   * JDBC connection or {@code IDBConnectionProvider} let the caller choose resource ownership. Creation returns the
   * tables created; it is not a reconciliation operation and should not be treated as idempotent. To inspect an
   * existing catalog, read it through the adapter. To determine differences, compare two schema models. Comparison
   * is in-memory and emits a typed {@link IDBSchemaDelta}; it executes no SQL.
   * <p>
   * A delta can be inspected, visited under a policy, or applied to a schema model. Applying it to a model changes
   * that model only. The adapter's {@code updateSchema} operation is what maps a delta to database-specific DDL.
   * Review removals, type changes, and constraint changes before applying them to production data.
   * <p>
   * {@link #inspectCurrentSchema(IDBAdapter, Connection) InspectCurrentSchema.java}
   *
   * @author Eike Stepper
   */
  public class SchemaInspectionAndComparison
  {
    /**
     * @snip
     */
    public void inspectCurrentSchema(IDBAdapter adapter, Connection connection) throws SQLException
    {
      // Inputs: an adapter and an open JDBC connection for the catalog being inspected.
      IDBSchema actual = DBUtil.readSchema(adapter, connection, "SALES");
      IDBSchema desired = DBUtil.createSchema("SALES");

      IDBTable customer = desired.addTable("CUSTOMER");
      IDBField customerID = customer.addField("ID", DBType.INTEGER, true);
      customer.addField("NAME", DBType.VARCHAR, 160, true);
      customer.addIndex("PK_CUSTOMER", IDBIndex.Type.PRIMARY_KEY, customerID);

      IDBSchemaDelta delta = desired.compare(actual);
      System.out.println("Tables differing: " + delta.getTableDeltaCount());

      for (IDBTableDelta tableDelta : delta.getTableDeltasSortedByName())
      {
        System.out.println(tableDelta.getChangeKind() + " " + tableDelta.getName());
      }
    }
  }

  /**
   * Schema Transactions and Working Copies
   * <p>
   * {@link IDBDatabase#openSchemaTransaction()} snapshots the current managed schema into a working copy. Edit the
   * copy, inspect {@link IDBSchemaTransaction#getSchemaDelta()}, and commit the transaction to invoke the adapter's
   * update operation. Closing an uncommitted transaction abandons the working copy. A transaction can instead be
   * opened through {@link IDBConnection#openSchemaTransaction()}; this associates its schema update with that
   * existing managed connection. In that form, schema transaction {@code commit()} commits the connection after the
   * update/completion path. In the database-opened form, Net4j obtains a managed connection for the update and
   * commits it through its connection-execution helper.
   * <p>
   * Neither form is an ordinary application transaction around arbitrary row updates. Do not assume that JDBC
   * rollback can undo DDL: databases differ in whether DDL is transactional, whether it causes an implicit commit,
   * and which alterations they support. Keep application data migration and schema changes as explicit deployment
   * steps when they need ordering or recovery logic.
   * <p>
   * The shared base schema already contains {@code CUSTOMER.EMAIL} and {@code ORDERS.STATUS}. These examples evolve
   * it by adding {@code CUSTOMER.LOYALTY_CODE} and {@code ORDERS.PROMOTION_CODE} fields.
   * <p>
   * {@link #addLoyaltyCodeColumn(IDBDatabase) AddLoyaltyCodeColumn.java}
   * {@link #addPromotionCodeColumn(IDBConnection) AddPromotionCodeColumn.java}
   *
   * @author Eike Stepper
   */
  public class SchemaTransactions
  {
    /**
     * @snip
     */
    public void addLoyaltyCodeColumn(IDBDatabase database)
    {
      // Input: an open database whose managed schema includes CUSTOMER.
      try (IDBSchemaTransaction transaction = database.openSchemaTransaction())
      {
        IDBTable customer = transaction.getWorkingCopy().getTable("CUSTOMER");
        customer.addField("LOYALTY_CODE", DBType.VARCHAR, 32, false);

        IDBSchemaDelta delta = transaction.getSchemaDelta();
        System.out.println("Proposed schema delta has " + delta.getTableDeltaCount() + " table changes");
        transaction.commit();
      }
    }

    /**
     * @snip
     */
    public void addPromotionCodeColumn(IDBConnection connection)
    {
      // Input: an open managed connection; close all its prepared statements before committing this schema change.
      try (IDBSchemaTransaction transaction = connection.openSchemaTransaction())
      {
        IDBTable orders = transaction.getWorkingCopy().getTable("ORDERS");
        orders.addField("PROMOTION_CODE", DBType.VARCHAR, 32, false);

        transaction.commit();
      }
    }
  }

  /**
   * Ensure Policies and Migration Review
   * <p>
   * {@link IDBSchemaTransaction#ensureSchema(IDBSchema)} compares a desired model with the transaction's working
   * copy, filters the delta under a policy, copies allowed changes, applies them to the working copy, and returns the
   * accepted delta. The default policy allows additions and a schema-level change; removals and ordinary element
   * changes are ignored. This is deliberately conservative. An explicit policy can allow or forbid combinations of
   * delta type and change kind, but allowing a change does not make it safe for existing data or supported by every
   * adapter.
   * <p>
   * Before commit, inspect the proposed delta and plan any required data backfill. Adding a non-null field to a
   * populated table may fail unless the database or migration process supplies a value for existing rows. A structural
   * delta does not describe data conversion, default-value rollout, or application compatibility across versions.
   * In the base adapter, the delta is applied to managed in-memory metadata before adapter DDL is executed. The
   * transaction completion path then coordinates invalidation of managed connection caches as explained in
   * {@link Doc01_GeneralUtilities.SchemaAccessAndCaching}.
   *
   * @author Eike Stepper
   */
  public class EnsureAndMigrationPolicies
  {
  }

  /**
   * Limitations and Failure Boundaries
   * <p>
   * Adapter support determines how table creation, field alterations, indexes, and drops are rendered. The physical
   * database determines accepted syntax, DDL transaction behavior, locking, and preservation of data. Net4j DB's
   * managed schema model is metadata, not a replacement for migration tooling or a database catalog. If an adapter
   * operation fails after some DDL has run, the framework's JDBC rollback attempt cannot establish a portable claim
   * that all physical changes were undone. Confirm the resulting catalog and data state using the target database's
   * supported inspection tools before retrying a deployment.
   *
   * @author Eike Stepper
   */
  public class DDLBoundaries
  {
  }
}
