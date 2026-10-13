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
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBDelta.ChangeKind;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;

/**
 * Data Definition
 * <p>
 * The DDL API models the intended structure of a database independently from a particular JDBC connection. A schema
 * is a mutable metadata tree of tables, fields, and indexes; an adapter translates that model into database-specific
 * SQL. This distinction lets an application define its desired structure once, compare it with another schema, and
 * apply supported changes through a controlled schema transaction.
 * <p>
 * Use a schema model as a declaration of structure, not as a live database catalog. Mutating a model changes metadata
 * in memory. Database state changes only when a create/drop operation or an adapter-backed schema transaction runs.
 * For connection, adapter, and resource ownership fundamentals see {@link Doc01_GeneralUtilities}; for SQL that reads
 * or changes row data see {@link Doc03_DataManipulation}.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_DataDefinition
{
  /**
   * Modeling Schemas
   * <p>
   * {@link DBUtil#createSchema(String)} creates a client-side schema model. Add tables, fields, and indexes through
   * {@link IDBSchema} and {@link IDBTable}. Field order is significant for legacy table-level INSERT generation and
   * for physical column ordering; explicitly listing columns in DML is generally clearer. Indexes distinguish
   * primary keys, unique indexes, and non-unique indexes. Keep names and types aligned with the target database and
   * use the selected {@link IDBAdapter} to account for vendor capabilities.
   * <p>
   * Case sensitivity and qualified table-name behavior are schema-model choices. They affect lookup/comparison and
   * name handling; they do not create a database schema by themselves. {@link DBType} supplies logical database types,
   * while the adapter may adapt a type or reject a capability such as indexing a particular type.
   * <p>
   * {@link #defineSchema() DefineSchema.java}
   *
   * @author Eike Stepper
   */
  public class ModelingSchemas
  {
    /**
     * @snip
     */
    public IDBSchema defineSchema()
    {
      IDBSchema schema = DBUtil.createSchema("APP");

      IDBTable customer = schema.addTable("CUSTOMER");
      IDBField id = customer.addField("ID", DBType.INTEGER, true);
      customer.addField("NAME", DBType.VARCHAR, 160, false);
      customer.addIndex("PK_CUSTOMER", IDBIndex.Type.PRIMARY_KEY, id);

      return schema;
    }
  }

  /**
   * Creating, Reading, and Exporting DDL
   * <p>
   * {@link IDBSchema#create(IDBAdapter, java.sql.Connection)} asks the adapter to create the schema's table
   * definitions on the supplied connection and returns the tables that were created. Overloads accept a
   * {@code DataSource} or an {@code IDBConnectionProvider} when the caller wants the operation to acquire a
   * connection. Adapter-generated DDL is database-specific, and database permissions, existing objects, and adapter
   * support determine success. Creation is not an idempotent ensure operation: use schema comparison/evolution for
   * reconciling an existing database.
   * <p>
   * {@link DBUtil#readSchema(IDBAdapter, java.sql.Connection, String)} reads catalog metadata into a model. Reading
   * observes database state; it does not update that state. {@link IDBSchema#export(java.sql.Connection,
   * java.io.PrintStream)} exports schema information for diagnostics or scripts; do not treat export output as a
   * portable migration language.
   *
   * @author Eike Stepper
   */
  public class CreatingAndInspectingSchemas
  {
  }

  /**
   * Schema Comparison
   * <p>
   * {@link IDBSchema#compare(IDBSchema)} produces an {@link IDBSchemaDelta} describing differences between models.
   * The comparison is a metadata operation: it issues no DDL. A delta is a tree whose nodes identify a schema, table,
   * field, index, index-field, or property and classify each node as {@link ChangeKind#ADD}, {@link ChangeKind#REMOVE},
   * or {@link ChangeKind#CHANGE}. Applying a delta to an {@link IDBSchema} changes that model in memory. Executing it
   * against a database requires an adapter update operation or a schema transaction.
   * <p>
   * Deltas are useful for review and policy. Inspect a delta before applying it when removals, type changes, or
   * constraint changes could affect existing data. A comparison tells you structural differences, not whether the
   * database can safely perform a particular migration or whether existing values satisfy new constraints.
   * <p>
   * {@link #compareSchemas(IDBSchema, IDBSchema) CompareSchemas.java}
   *
   * @author Eike Stepper
   */
  public class SchemaComparison
  {
    /**
     * @snip
     */
    public IDBSchemaDelta compareSchemas(IDBSchema desired, IDBSchema current)
    {
      return desired.compare(current);
    }
  }

  /**
   * Schema Evolution and Schema Transactions
   * <p>
   * {@link IDBDatabase#openSchemaTransaction()} creates an isolated working copy of the database's current schema.
   * Edit that working copy, review {@link IDBSchemaTransaction#getSchemaDelta()}, then call
   * {@link IDBSchemaTransaction#commit()} to ask the adapter to update the physical schema and publish the new model.
   * Closing without committing abandons the working changes. The schema transaction may acquire its own connection
   * at commit time, or can be associated with an existing {@code IDBConnection} through the database/connection
   * overload. This is a schema lifecycle, not a replacement for an application's JDBC row-data transaction.
   * <p>
   * {@link IDBSchemaTransaction#ensureSchema(IDBSchema)} merges a desired schema into the working copy and returns
   * the delta it proposes. Its default policy allows additions and schema-level changes, while removals and ordinary
   * element changes are ignored. This conservative default avoids silently dropping tables or changing existing
   * columns. A custom {@code IDBDeltaVisitor.Filter.Policy} can allow or forbid specific delta kinds; choose it
   * deliberately, inspect the returned delta, and commit only when the proposed migration is appropriate. The
   * adapter still controls whether and how each operation is supported by the target database.
   * <p>
   * {@link #evolveSchema(IDBDatabase) EvolveSchema.java}
   *
   * @author Eike Stepper
   */
  public class SchemaEvolution
  {
    /**
     * @snip
     */
    public IDBSchemaDelta evolveSchema(IDBDatabase database)
    {
      IDBSchema desired = DBUtil.createSchema("APP");
      IDBTable customer = desired.addTable("CUSTOMER");
      IDBField id = customer.addField("ID", DBType.INTEGER, true);
      customer.addField("NAME", DBType.VARCHAR, 160, false);
      customer.addIndex("PK_CUSTOMER", IDBIndex.Type.PRIMARY_KEY, id);

      IDBSchemaTransaction transaction = database.openSchemaTransaction();

      try
      {
        IDBSchemaDelta proposed = transaction.ensureSchema(desired);
        IDBSchemaDelta applied = transaction.commit();
        return applied == null ? proposed : applied;
      }
      finally
      {
        transaction.close();
      }
    }
  }

  /**
   * Delta Policies and Review
   * <p>
   * A delta visitor traverses the typed hierarchy and can accept, ignore, or reject changes under a policy. Policies
   * can be scoped by {@code DeltaType}, {@code ChangeKind}, or their combination; a frozen policy cannot be edited.
   * The standard ensure policy is intentionally conservative, so adding a non-null field to a populated table may
   * still require an explicit migration plan or a database-specific default strategy. A metadata delta is not a data
   * backfill plan.
   * <p>
   * Review schema transaction results and adapter failures as part of deployment. DDL transactional behavior varies
   * by database. Although the schema transaction coordinates update and model publication, a database may commit DDL
   * implicitly or support only a subset of alterations; do not assume a failed multi-step migration has been fully
   * rolled back on every vendor.
   *
   * @author Eike Stepper
   */
  public class DeltaPoliciesAndReview
  {
  }
}
