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
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.IDBResultSet;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.SQLTemplate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Data Manipulation
 * <p>
 * Net4j DB's structured DML API turns schema-aware definitions into prepared JDBC statements through a clear
 * lifecycle: schema metadata → {@link SQLTemplate} → adapter-compiled {@link SQLStatement} → managed
 * {@link IDBPreparedStatement} → JDBC execution. The template and compiled statement are independent of a particular
 * connection or JDBC statement and can be retained as reusable definitions. The adapter controls compilation;
 * {@link IDBConnection} controls preparation and its local statement cache.
 * <p>
 * The fluent interfaces constrain clause order and keep identifiers connected to schema metadata. Parameter objects
 * separate values from SQL text. This improves composition and avoids concatenating user values, but raw SQL fragment
 * text remains trusted input and is not made portable or safe automatically. For database/schema setup see
 * {@link Doc02_DataDefinition}; for the managed connection and schema-coordination model see
 * {@link Doc01_GeneralUtilities}.
 * <p>
 * The examples use a {@code SALES} schema containing {@code CUSTOMER(ID, NAME, EMAIL)} and
 * {@code ORDERS(ID, CUSTOMER_ID, TOTAL, STATUS)}. Establish {@code adapter}, {@code connection}, and the listed
 * schema table/field objects as application-owned inputs where a snippet says it expects them. The method declaration
 * is not shown when a snippet is embedded, so each such example identifies its inputs in a code comment.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_DataManipulation
{
  /**
   * Definitions and Fluent Stages
   * <p>
   * {@link SQL} is the factory for expressions, conditions, fragments, parameters, and statement builders.
   * {@code SQLExpression} represents a value; {@link SQLCondition} represents a predicate; a complete
   * {@link SQLSubSelect} can participate in another expression. The staged interfaces in
   * {@code org.eclipse.net4j.db.dml.fluent} expose only the next legal operation. For example, a comparison stage
   * must receive its right operand before the builder can proceed to later SELECT clauses.
   * <p>
   * Builders are mutable while being assembled. Once advanced, an earlier stage is stale and cannot be used to
   * mutate the builder. Calling {@code build()} captures an immutable adapter-neutral template snapshot; a later
   * valid builder operation does not alter an earlier snapshot.
   *
   * @author Eike Stepper
   */
  public class DefinitionsAndStages
  {
  }

  /**
   * SELECT with Parameters
   * <p>
   * A SELECT projects fields or expressions from one or more schema tables. Bind values as parameters rather than
   * inserting them into SQL text. The compiled statement contains a placeholder at each parameter occurrence; the
   * application binds those occurrences and then executes the prepared statement. Close the result set and prepared
   * statement before closing the managed connection. The connection's cache can reuse a returned statement on a
   * later preparation.
   * <p>
   * The example expects the application to supply the adapter, managed connection, and {@code CUSTOMER.ID} field.
   * {@link #findCustomers(IDBAdapter, IDBConnection, IDBTable, IDBField) FindCustomers.java}
   *
   * @author Eike Stepper
   */
  public class SelectQueries
  {
    /**
     * @snip
     */
    public void findCustomers(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID) throws SQLException
    {
      // Inputs: registered adapter, open managed connection, and CUSTOMER table/ID field.
      SQLParameter requestedID = SQL.param();
      SQLTemplate template = SQL.selectAll() //
          .from(customer) //
          .where(customerID).eq(requestedID) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH))
      {
        statement.bind(prepared, 42);

        try (IDBResultSet rows = prepared.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Customer ID: " + rows.getInt("ID"));
          }
        }
      }
    }
  }

  /**
   * Explicit Conditions and Optional Filters
   * <p>
   * Conditions are immutable values that can be named, combined, and supplied to a builder. This is useful when
   * predicates are conditional: compose only complete conditions, then attach the result at the WHERE stage. The
   * order in which the final condition tree renders determines positional parameter order. Grouping the conditions
   * explicitly also makes the intended boolean logic visible in code.
   * <p>
   * The example expects {@code ORDERS} plus its {@code STATUS} and {@code TOTAL} fields.
   * {@link #findOrders(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField, boolean) FindOrders.java}
   *
   * @author Eike Stepper
   */
  public class ConditionsAndComposition
  {
    /**
     * @snip
     */
    public void findOrders(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField status, IDBField total, boolean filterByMinimum)
        throws SQLException
    {
      // Inputs: adapter, managed connection, ORDERS table, STATUS and TOTAL fields, and the filter choice.
      SQLParameter statusValue = SQL.param();
      SQLCondition filter = SQL.eq(status, statusValue);
      SQLParameter minimumValue = null;

      if (filterByMinimum)
      {
        minimumValue = SQL.param();
        filter = filter.and(SQL.ge(total, minimumValue));
      }

      SQLTemplate template = SQL.selectAll() //
          .from(orders) //
          .where(filter) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH))
      {
        if (filterByMinimum)
        {
          statement.bind(prepared, "OPEN", new BigDecimal("100.00"));
        }
        else
        {
          statement.bind(prepared, "OPEN");
        }

        try (IDBResultSet rows = prepared.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Order total: " + rows.getBigDecimal("TOTAL"));
          }
        }
      }
    }
  }

  /**
   * Aliases, Joins, Ordering, and Grouping
   * <p>
   * A {@link SQLTableRef} represents one table occurrence and can qualify its columns. This matters when a query
   * joins a table to itself or when two joined tables have fields with the same name. An alias on a table reference
   * differs from an alias on a projected expression: the former qualifies source columns; the latter labels an
   * output value. SELECT builders also provide ordering, grouping, and HAVING stages. Supported join and ordering
   * forms still have to be accepted by the target database.
   * <p>
   * This example expects the {@code CUSTOMER} and {@code ORDERS} tables with their key fields.
   * {@link #customerOrderJoin(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBTable, IDBField) CustomerOrderJoin.java}
   * The aggregate example groups orders by status and filters the grouped result with HAVING:
   * {@link #summarizeOrders(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField) SummarizeOrders.java}
   *
   * @author Eike Stepper
   */
  public class JoinsAndGrouping
  {
    /**
     * @snip
     */
    public void customerOrderJoin(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBTable orders,
        IDBField orderCustomerID) throws SQLException
    {
      // Inputs: adapter, managed connection, CUSTOMER.ID, and ORDERS.CUSTOMER_ID.
      SQLTableRef c = customer.as("c");
      SQLTableRef o = orders.as("o");
      SQLTemplate template = SQL.select(c.column(customerID), o.column(orderCustomerID)) //
          .from(c) //
          .join(o) //
          .on(c.column(customerID)).eq(o.column(orderCustomerID)) //
          .orderBy(SQL.asc(c.column(customerID))) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW); //
          IDBResultSet rows = prepared.executeQuery())
      {
        while (rows.next())
        {
          System.out.println("Customer/order key: " + rows.getInt(1) + "/" + rows.getInt(2));
        }
      }
    }

    /**
     * @snip
     */
    public void summarizeOrders(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField status, IDBField total) throws SQLException
    {
      // Inputs: adapter, managed connection, ORDERS table, STATUS and TOTAL fields.
      SQLParameter selectedStatus = SQL.param();
      SQLTemplate template = SQL.select(status, SQL.count().as("ORDER_COUNT"), SQL.sum(total).as("GROSS_TOTAL")) //
          .from(orders) //
          .where(status).eq(selectedStatus) //
          .groupBy(status) //
          .having(SQL.count()).gt(SQL.param()) //
          .orderBy(SQL.asc(status)) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bind(prepared, "CLOSED", 0);

        try (IDBResultSet rows = prepared.executeQuery())
        {
          while (rows.next())
          {
            System.out.println(rows.getString("STATUS") + ": " + rows.getLong("ORDER_COUNT"));
          }
        }
      }
    }
  }

  /**
   * Subqueries and Aggregates
   * <p>
   * A complete SELECT can be converted to {@link SQLSubSelect} and used in an IN, EXISTS, or scalar context. Aggregate
   * expressions can appear in projections and HAVING clauses. The subquery's parameters participate in the outer
   * statement's rendered occurrence order, so positional binding must account for the complete compiled statement.
   * Prefer structured subqueries when their shape is supported; a fragment is not needed merely to nest a SELECT.
   * <p>
   * The example expects {@code CUSTOMER.ID}, {@code ORDERS.CUSTOMER_ID}, and {@code ORDERS.STATUS}.
   * {@link #customersWithOpenOrders(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBTable, IDBField, IDBField)
   * CustomersWithOpenOrders.java}
   *
   * @author Eike Stepper
   */
  public class Subqueries
  {
    /**
     * @snip
     */
    public void customersWithOpenOrders(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBTable orders,
        IDBField orderCustomerID, IDBField status) throws SQLException
    {
      // Inputs: adapter, connection, CUSTOMER.ID, ORDERS.CUSTOMER_ID, and ORDERS.STATUS.
      SQLParameter openStatus = SQL.param();
      SQLSubSelect openCustomers = SQL.select(orderCustomerID) //
          .from(orders) //
          .where(status).eq(openStatus) //
          .subSelect();
      SQLTemplate template = SQL.select(customerID) //
          .from(customer) //
          .where(customerID).in(openCustomers) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bind(prepared, "OPEN");

        try (IDBResultSet rows = prepared.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Customer with open order: " + rows.getInt(1));
          }
        }
      }
    }
  }

  /**
   * INSERT, UPDATE, and DELETE
   * <p>
   * An explicit INSERT column list pairs each column with the value at the same position. The values-only form instead
   * depends on the table's physical field order and needs one value for each column. UPDATE requires at least one
   * assignment. DELETE permits an absent WHERE clause, so make the scope of destructive operations explicit. These
   * builders create definitions; compile and prepare them before binding and executing.
   * <p>
   * The examples expect the relevant table and fields from the shared SALES model.
   * {@link #insertCustomer(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField) InsertCustomer.java}
   * {@link #copyOrders(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField, IDBTable, IDBField, IDBField) InsertSelect.java}
   * {@link #updateOrderStatus(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField) UpdateOrderStatus.java}
   * {@link #deleteOrder(IDBAdapter, IDBConnection, IDBTable, IDBField) DeleteOrder.java}
   *
   * @author Eike Stepper
   */
  public class DataChanges
  {
    /**
     * @snip
     */
    public void insertCustomer(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name) throws SQLException
    {
      // Inputs: adapter, connection, CUSTOMER table and its ID/NAME fields.
      SQLParameter idValue = SQL.param();
      SQLParameter nameValue = SQL.param();
      SQLTemplate template = SQL.insertInto(customer) //
          .columns(customerID, name) //
          .values(idValue, nameValue) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bind(prepared, 501, "Ada Lovelace");
        prepared.executeUpdate();
        connection.commit();
      }
      catch (SQLException | RuntimeException ex)
      {
        connection.rollback();
        throw ex;
      }
    }

    /**
     * @snip
     */
    public void copyOrders(IDBAdapter adapter, IDBConnection connection, IDBTable sourceOrders, IDBField sourceID, IDBField sourceCustomerID,
        IDBTable targetOrders, IDBField targetID, IDBField targetCustomerID) throws SQLException
    {
      // Inputs: adapter, connection, source ORDERS.ID/CUSTOMER_ID and matching target fields.
      SQLParameter customerID = SQL.param();
      SQLStatement statement = SQL.insertInto(targetOrders) //
          .columns(targetID, targetCustomerID) //
          .select(SQL.select(sourceID, sourceCustomerID).from(sourceOrders).where(sourceCustomerID).eq(customerID).subSelect()) //
          .build().compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bind(prepared, 501);
        prepared.executeUpdate();
      }
    }

    /**
     * @snip
     */
    public void updateOrderStatus(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField orderID, IDBField status) throws SQLException
    {
      // Inputs: adapter, connection, ORDERS table and its ID/STATUS fields.
      SQLTemplate template = SQL.update(orders) //
          .set(status).to(SQL.param()) //
          .where(orderID).eq(SQL.param()) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH))
      {
        statement.bind(prepared, "SHIPPED", 9001L);
        prepared.executeUpdate();
        connection.commit();
      }
      catch (SQLException | RuntimeException ex)
      {
        connection.rollback();
        throw ex;
      }
    }

    /**
     * @snip
     */
    public void deleteOrder(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField orderID) throws SQLException
    {
      // Inputs: adapter, connection, ORDERS table, and ORDERS.ID. This delete is intentionally restricted to one key.
      SQLTemplate template = SQL.deleteFrom(orders) //
          .where(orderID).eq(SQL.param()) //
          .build();
      SQLStatement statement = template.compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bind(prepared, 9001L);
        prepared.executeUpdate();
        connection.commit();
      }
      catch (SQLException | RuntimeException ex)
      {
        connection.rollback();
        throw ex;
      }
    }
  }

  /**
   * Templates and Adapter Compilation
   * <p>
   * A {@link SQLTemplate} is an immutable, adapter-neutral snapshot. Compile it with the target adapter to obtain
   * {@link SQLStatement}: the adapter controls identifier quoting, SQL rendering for represented operations, and the
   * JDBC type used for inferred typed NULL values. A compiled statement can be reused without retaining a connection
   * or prepared statement. Compile the same template for another adapter when another dialect is needed. This does
   * not rewrite arbitrary SQL fragment text into portable SQL.
   *
   * @author Eike Stepper
   */
  public class TemplatesAndCompilation
  {
  }

  /**
   * Positional and Identity-Based Binding
   * <p>
   * Each {@code SQL.param()} call creates a distinct logical parameter. Positional binding supplies one value per
   * rendered occurrence in SQL order. Identity binding targets one parameter object and sets each occurrence that
   * refers to that same object. JDBC indexes remain one-based when binding a single position directly. The number of
   * positional values is validated before setters run, but a failure during a later setter does not undo earlier
   * values already placed on the statement.
   * <p>
   * This example demonstrates both forms: two distinct placeholders are bound positionally, then one shared logical
   * parameter appears twice on VARCHAR columns and is bound by identity.
   * {@link #bindParameters(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField, IDBField) BindParameters.java}
   *
   * @author Eike Stepper
   */
  public class ParameterBinding
  {
    /**
     * @snip
     */
    public void bindParameters(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name, IDBField email)
        throws SQLException
    {
      // Inputs: adapter, connection, CUSTOMER.ID, CUSTOMER.NAME, and CUSTOMER.EMAIL.
      SQLParameter idParameter = SQL.param();
      SQLParameter nameParameter = SQL.param();
      SQLStatement positional = SQL.selectAll() //
          .from(customer) //
          .where(customerID).eq(idParameter) //
          .and(name).eq(nameParameter) //
          .build().compile(adapter);

      try (IDBPreparedStatement first = positional.prepare(connection, ReuseProbability.LOW))
      {
        positional.bind(first, 501, "Ada Lovelace");
        try (IDBResultSet rows = first.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Matched customer: " + rows.getInt("ID"));
          }
        }
      }

      SQLParameter sharedID = SQL.param();
      SQLStatement identity = SQL.selectAll() //
          .from(customer) //
          .where(name).eq(sharedID) //
          .or(email).eq(sharedID) //
          .build().compile(adapter);

      try (IDBPreparedStatement second = identity.prepare(connection, ReuseProbability.LOW))
      {
        identity.bindParameter(second, sharedID, "Ada");
        try (IDBResultSet rows = second.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Matched by shared value: " + rows.getInt("ID"));
          }
        }
      }
    }
  }

  /**
   * Typed NULL Values and Custom Binders
   * <p>
   * {@code SQL.param(int)} takes a JDBC {@code Types} code. It controls typed NULL binding and typed
   * {@code setObject}. Without it, NULL typing can be inferred when a parameter is directly associated with a schema
   * field; the compiled adapter supplies the JDBC type code for that field's {@link DBType}. The compiler does not
   * propagate a field type through every arbitrary expression. If it cannot establish a type for a NULL, binding
   * fails rather than guessing.
   * <p>
   * A converter transforms non-null values before default binding. Null input bypasses a converter, and a converter
   * that produces null follows typed-NULL rules. An explicit {@link SQLParameterBinder} takes precedence and controls
   * the JDBC setter for each occurrence, receiving the original value even when it is null.
   * <p>
   * The binary binder below is a separate specialized example: it assumes an application-defined {@code ATTACHMENT}
   * table with a binary {@code PAYLOAD} field and does not extend the shared {@code SALES} schema.
   * <p>
   * {@link #setCustomerEmailToNull(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField) SetCustomerEmailToNull.java}
   * {@link #insertBinaryValue(IDBAdapter, IDBConnection, IDBTable, IDBField, byte[]) InsertBinaryValue.java}
   *
   * @author Eike Stepper
   */
  public class TypedAndCustomBinding
  {
    /**
     * @snip
     */
    public void setCustomerEmailToNull(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField email) throws SQLException
    {
      // Inputs: adapter, connection, CUSTOMER table and ID/EMAIL fields.
      SQLParameter missingEmail = SQL.param(Types.VARCHAR);
      SQLStatement statement = SQL.update(customer) //
          .set(email).to(missingEmail) //
          .where(customerID).eq(SQL.param()) //
          .build() //
          .compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bind(prepared, null, 501);
        prepared.executeUpdate();
        connection.commit();
      }
      catch (SQLException | RuntimeException ex)
      {
        connection.rollback();
        throw ex;
      }
    }

    /**
     * @snip
     */
    public void insertBinaryValue(IDBAdapter adapter, IDBConnection connection, IDBTable attachment, IDBField payload, byte[] bytes) throws SQLException
    {
      // Inputs: adapter, connection, an ATTACHMENT table with a binary PAYLOAD field, and the bytes to store.
      SQLParameter payloadValue = SQL.param();
      SQLParameterBinder<byte[]> binaryBinder = (statement, index, value) -> statement.setBytes(index, value);
      SQLStatement insert = SQL.insertInto(attachment) //
          .set(payload).to(payloadValue) //
          .build().compile(adapter);

      try (IDBPreparedStatement prepared = insert.prepare(connection, ReuseProbability.LOW))
      {
        insert.bindParameter(prepared, payloadValue, bytes, binaryBinder);
        prepared.executeUpdate();
        connection.commit();
      }
      catch (SQLException | RuntimeException ex)
      {
        connection.rollback();
        throw ex;
      }
    }
  }

  /**
   * Reusing a Template and Managed Prepared Statement
   * <p>
   * Reuse has two distinct scopes. The immutable template or compiled statement can be retained by an application
   * independently of a connection. A prepared JDBC statement is connection-bound and checked out only for its use;
   * closing it returns it to that connection's cache, subject to cache policy and capacity. {@link ReuseProbability}
   * is a hint to the Net4j statement cache, not a guarantee that a particular JDBC handle will be reused.
   * <p>
   * For long-running application code, reuse the definition and prepare/close around each operation. This promptly
   * releases the schema read token associated with a checkout, allowing a queued schema change to progress.
   * {@link #repeatCustomerLookup(IDBAdapter, IDBConnection, IDBTable, IDBField) RepeatCustomerLookup.java}
   *
   * @author Eike Stepper
   */
  public class ReuseAndLifecycle
  {
    /**
     * @snip
     */
    public void repeatCustomerLookup(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID) throws SQLException
    {
      // Inputs: adapter, connection, CUSTOMER table and ID field.
      SQLParameter idValue = SQL.param();
      SQLTemplate template = SQL.selectAll() //
          .from(customer) //
          .where(customerID).eq(idValue) //
          .build();
      SQLStatement statement = template.compile(adapter);

      for (int id : new int[] { 501, 502, 503 })
      {
        try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH))
        {
          statement.bind(prepared, id);

          try (IDBResultSet rows = prepared.executeQuery())
          {
            while (rows.next())
            {
              System.out.println("Customer " + rows.getInt("ID") + ": " + rows.getString("NAME"));
            }
          }
        }
      }
    }
  }

  /**
   * Advanced Composition with SQL Fragments
   * <p>
   * Fragments combine trusted SQL text with structured tables, columns, expressions, conditions, and parameters.
   * Raw text is neither parsed nor escaped. Never concatenate untrusted values into it. Represent a value with a
   * structured parameter and an identifier with a table or column reference. Raw fragment text must not include a
   * question mark, even inside a quoted literal: every placeholder must be represented by a parameter object so
   * compiled occurrence metadata continues to match JDBC parameter positions.
   * <p>
   * Use fragments for a SQL shape the structured builders cannot express, while keeping identifiers and values
   * structured where possible. Fragments do not turn vendor-specific syntax into portable SQL.
   * {@link #searchByNameFragment(IDBAdapter, IDBConnection, IDBTable, IDBField) SearchByNameFragment.java}
   *
   * @author Eike Stepper
   */
  public class Fragments
  {
    /**
     * @snip
     */
    public void searchByNameFragment(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name) throws SQLException
    {
      // Inputs: adapter, connection, CUSTOMER table and NAME field.
      SQLParameter nameValue = SQL.param();
      SQLFragment fragment = SQL.fragment() //
          .sql("SELECT ").column(name) //
          .sql(" FROM ").table(customer) //
          .sql(" WHERE ").column(name).sql(" = ").param(nameValue) //
          .build();
      SQLStatement statement = SQL.build(fragment).compile(adapter);

      try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
      {
        statement.bindParameter(prepared, nameValue, "Ada Lovelace");

        try (IDBResultSet rows = prepared.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Customer name: " + rows.getString(1));
          }
        }
      }
    }
  }

  /**
   * Portability Boundaries
   * <p>
   * The compiler renders the structured operations represented in its API and delegates identifier quoting and
   * type-specific behavior to the adapter. It does not rewrite arbitrary fragment text, validate business rules,
   * map result rows to objects, infer types through all expression trees, or generate DDL. SQL syntax support remains
   * bounded by the adapter and target database. Keep portable definitions structured and isolate vendor-specific
   * syntax in explicit adapter-aware code paths.
   *
   * @author Eike Stepper
   */
  public class Portability
  {
  }
}
