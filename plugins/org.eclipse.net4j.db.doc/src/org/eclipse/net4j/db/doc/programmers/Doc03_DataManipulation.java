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
import org.eclipse.net4j.db.dml.SQLDefaultSetter;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.SQLTemplate;
import org.eclipse.net4j.db.dml.SQLValueSetter;
import org.eclipse.net4j.db.dml.fluent.SQLInStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectWhereStep;
import org.eclipse.net4j.db.h2.H2Adapter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Data Manipulation
 * <p>
 * Net4j DB's structured DML API represents statements as schema-aware definitions, compiles them for an adapter, and
 * binds application values to prepared JDBC statements. A template is independent of a connection. Its compiled
 * statement records rendered SQL and ordered parameter occurrences, while a managed connection prepares and caches
 * JDBC statements. The caller owns each prepared statement checkout and its result sets.
 * <p>
 * The fluent interfaces constrain clause order and keep identifiers connected to schema metadata. Parameter objects
 * keep values separate from SQL text. Structured fragments can combine raw SQL with structured expressions, but raw
 * fragment text remains trusted input and is not made portable or safe automatically.
 * <p>
 * Most examples use a {@code SALES} schema containing {@code CUSTOMER(ID, NAME, EMAIL)} and
 * {@code ORDERS(ID, CUSTOMER_ID, TOTAL, STATUS)}. Snippets accept schema objects and managed resources as method
 * arguments; they do not create a database or define an application's transaction policy.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_DataManipulation
{
  /**
   * Introduction
   * <p>
   * Structured DML separates statement shape from values and connection state. This makes definitions composable and
   * reusable while leaving connection ownership and transaction boundaries with the application.
   *
   * @author Eike Stepper
   */
  public class Introduction
  {
    /**
     * Concepts and Architecture
     * <p>
     * {@link SQL} creates expressions, conditions, fragments, parameters, and statement builders. An
     * {@link SQLTemplate} is the immutable adapter-neutral definition captured by {@code build()}. Compiling it with
     * an {@link IDBAdapter} produces an {@link SQLStatement} containing rendered SQL and metadata for each JDBC
     * parameter occurrence. An {@link IDBConnection} prepares that SQL and returns a managed
     * {@link IDBPreparedStatement}; execution and result reading use JDBC through the Net4j DB wrappers.
     * <p>
     * An {@code SQLExpression} represents a value and an {@link SQLCondition} represents a predicate. Staged
     * interfaces in {@code org.eclipse.net4j.db.dml.fluent} expose the next legal operation. Builders are mutable
     * while assembled, but an earlier stage becomes stale after advancing. Each call to {@code build()} captures its
     * own snapshot, so later valid changes do not rewrite an earlier template.
     *
     * @author Eike Stepper
     */
    public class ConceptsAndArchitecture
    {
    }

    /**
     * The DML Lifecycle
     * <p>
     * A typical operation creates a template from schema objects, compiles it once for the selected adapter,
     * prepares it on a managed connection, binds all required values, executes it, and closes the result set and
     * statement. Closing a managed prepared statement returns it to that connection's cache. A template or compiled
     * statement can be reused independently of a checked-out JDBC statement.
     * <p>
     * Binding calls can change some JDBC positions before a later converter or setter fails. Net4j DB validates
     * structural ranges before starting a sequence, but it cannot undo JDBC side effects already performed by an
     * earlier binding operation. Transaction rollback remains the application's responsibility.
     *
     * @author Eike Stepper
     */
    public class DMLLifecycle
    {
    }

    /**
     * Definitions, Templates, and Compilation
     * <p>
     * Build a {@link SQLTemplate} without selecting a database connection. Compilation uses the adapter for SQL
     * rendering and identifier quoting, and records an inferred {@link DBType} for each parameter occurrence when its
     * schema context provides one. An explicit JDBC target type remains part of the parameter definition and is
     * honored during binding. Default NULL binding is a later phase: when an inferred type is known, the adapter
     * selects the JDBC NULL code through {@code getJDBCTypeForNull()}. Compilation cannot translate vendor-specific
     * raw fragment text into another dialect. The same template can be compiled for another adapter.
     *
     * @author Eike Stepper
     */
    public class DefinitionsTemplatesAndCompilation
    {
    }
  }

  /**
   * Basic Statements
   * <p>
   * The statement builders cover common SELECT and data-change forms. Keep values as parameters and represent tables
   * and columns with schema metadata so the compiler can render identifiers for the selected adapter.
   *
   * @author Eike Stepper
   */
  public class BasicStatements
  {
    /**
     * SELECT
     * <p>
     * A SELECT projects fields or expressions. The example compiles a key lookup, binds the key, executes it, and
     * closes both managed resources. The returned statement can be prepared again for another lookup.
     *
     * @author Eike Stepper
     */
    public class Select
    {
      /**
       * @snip
       */
      public void findCustomers(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID) throws SQLException
      {
        // Inputs: registered adapter, open managed connection, CUSTOMER table, and CUSTOMER.ID field.
        SQLParameter requestedID = SQL.param();
        SQLStatement statement = SQL.selectAll() //
            .from(customer) //
            .where(customerID).eq(requestedID) //
            .compile(adapter);

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
     * INSERT and INSERT SELECT
     * <p>
     * An explicit column list pairs each value with its named destination column. INSERT SELECT instead copies a
     * query result into the target table; parameters in the SELECT remain ordinary compiled occurrences.
     *
     * @author Eike Stepper
     */
    public class InsertAndInsertSelect
    {
      /**
       * @snip
       */
      public void insertCustomer(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name) throws SQLException
      {
        // Inputs: adapter, connection, CUSTOMER table and its ID/NAME fields.
        SQLTemplate template = SQL.insertInto(customer) //
            .columns(customerID, name) //
            .values(SQL.param(), SQL.param()) //
            .build();
        SQLStatement statement = template.compile(adapter);

        try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
        {
          statement.bindAll(prepared, 501, "Ada Lovelace");
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
        // Inputs: adapter, connection, source ORDERS.ID/CUSTOMER_ID, and matching target fields.
        SQLParameter customerID = SQL.param();
        SQLStatement statement = SQL.insertInto(targetOrders) //
            .columns(targetID, targetCustomerID) //
            .select(SQL.select(sourceID, sourceCustomerID) //
                .from(sourceOrders) //
                .where(sourceCustomerID).eq(customerID) //
                .subSelect()) //
            .compile(adapter);

        try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
        {
          statement.bind(prepared, 501);
          prepared.executeUpdate();
        }
      }
    }

    /**
     * UPDATE
     * <p>
     * An UPDATE names assigned fields in {@code set()} and limits affected rows with {@code where()}. The values are
     * bound in rendered occurrence order: the new status first, then the order key.
     *
     * @author Eike Stepper
     */
    public class Update
    {
      /**
       * @snip
       */
      public void updateOrderStatus(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField orderID, IDBField status) throws SQLException
      {
        // Inputs: adapter, connection, ORDERS table and its ID/STATUS fields.
        SQLStatement statement = SQL.update(orders) //
            .set(status).to(SQL.param()) //
            .where(orderID).eq(SQL.param()) //
            .compile(adapter);

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
    }

    /**
     * DELETE
     * <p>
     * A DELETE may omit its WHERE clause and affect every row. Prefer an explicit restrictive condition when removing
     * a single record, as this key-based example does.
     *
     * @author Eike Stepper
     */
    public class Delete
    {
      /**
       * @snip
       */
      public void deleteOrder(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField orderID) throws SQLException
      {
        // Inputs: adapter, connection, ORDERS table, and ORDERS.ID. This delete is restricted to one key.
        SQLStatement statement = SQL.deleteFrom(orders) //
            .where(orderID).eq(SQL.param()) //
            .compile(adapter);

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
     * Structured Expressions, IN Lists, and Table Providers
     * <p>
     * Use {@link SQL#abs(SQLExpression)} for a structured absolute-value expression, and use the IN factories or
     * fluent value-list stage for ordered membership values. An {@code IDBTable.Provider} lets a domain wrapper
     * supply its primary schema table directly to DML construction; the table is resolved when the API call is made.
     *
     * @author Eike Stepper
     */
    public class StructuredExpressionsAndProviders
    {
      /**
       * Replaces a raw ABS function fragment while retaining column metadata for parameter type inference.
       *
       * @snip
       */
      public SQLStatement selectByAbsoluteVersion(IDBAdapter adapter, IDBTable table, IDBField version)
      {
        return SQL.selectAll() //
            .from(table) //
            .where(SQL.abs(version)).eq(SQL.param()) //
            .compile(adapter);
      }

      /**
       * Uses fixed, iterable, and incrementally assembled IN value lists. The staged list remains part of the
       * enclosing SELECT until it is completed with {@code end()}.
       *
       * @snip
       */
      public SQLStatement selectForFixedRevisions(IDBAdapter adapter, IDBTable table, IDBField revision)
      {
        return SQL.selectAll() //
            .from(table) //
            .where(revision).in(SQL.literal(1), SQL.param()) //
            .compile(adapter);
      }

      /**
       * Builds an IN list from an iterable, traversed once by the fluent operation.
       *
       * @snip
       */
      public SQLStatement selectForRevisions(IDBAdapter adapter, IDBTable table, IDBField revision, Iterable<? extends SQLExpression> values)
      {
        return SQL.selectAll() //
            .from(table) //
            .where(revision).in(values) //
            .compile(adapter);
      }

      /**
       * Adds values to a statement's incomplete IN list and resumes the normal SELECT stage after completion.
       *
       * @snip
       */
      public SQLStatement selectForRevisionsIncrementally(IDBAdapter adapter, IDBTable table, IDBField revision, Iterable<? extends SQLExpression> values)
      {
        SQLInStep<SQLSelectWhereStep> pending = SQL.selectAll() //
            .from(table) //
            .where(revision).in();

        pending.addAll(values);

        return pending //
            .end() //
            .compile(adapter);
      }

      /**
       * Passes a domain table wrapper directly to a DML factory and SELECT source stage.
       *
       * @snip
       */
      public SQLStatement selectFromProvider(IDBAdapter adapter, IDBTable.Provider lockAreas, IDBField id)
      {
        return SQL.select(id) //
            .from(lockAreas) //
            .where(id).eq(SQL.param()) //
            .compile(adapter);
      }
    }
  }

  /**
   * Queries and Composition
   * <p>
   * Conditions, table references, subqueries, and fragments allow a query to be assembled from reusable parts.
   * Structured elements retain metadata for rendering and binding; raw fragment text remains opaque to the compiler.
   *
   * @author Eike Stepper
   */
  public class QueriesAndComposition
  {
    /**
     * Conditions and Optional Filters
     * <p>
     * Conditions are immutable values that can be named and combined before attaching them to a WHERE stage. The
     * final condition tree determines SQL order and therefore positional binding order. Build only complete
     * conditions, and add optional predicates only when their values are part of the request.
     *
     * @author Eike Stepper
     */
    public class ConditionsAndOptionalFilters
    {
      /**
       * @snip
       */
      public void findOrders(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField status, IDBField total, boolean filterByMinimum)
          throws SQLException
      {
        // Inputs: adapter, connection, ORDERS table, STATUS/TOTAL fields, and the filter choice.
        SQLParameter statusValue = SQL.param();
        SQLCondition filter = SQL.eq(status, statusValue);
        SQLParameter minimumValue = null;

        if (filterByMinimum)
        {
          minimumValue = SQL.param();
          filter = filter.and(SQL.ge(total, minimumValue));
        }

        SQLStatement statement = SQL.selectAll() //
            .from(orders) //
            .where(filter) //
            .compile(adapter);

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
     * Aliases and Joins
     * <p>
     * A {@link SQLTableRef} represents one occurrence of a table and can qualify its columns. Table aliases distinguish
     * source columns in joins; aliases on projected expressions label output values instead.
     *
     * @author Eike Stepper
     */
    public class AliasesAndJoins
    {
      /**
       * @snip
       */
      public void customerOrderJoin(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBTable orders,
          IDBField orderCustomerID) throws SQLException
      {
        // Inputs: adapter, connection, CUSTOMER.ID, and ORDERS.CUSTOMER_ID.
        SQLTableRef c = customer.as("c");
        SQLTableRef o = orders.as("o");
        SQLStatement statement = SQL.select(c.column(customerID), o.column(orderCustomerID)) //
            .from(c) //
            .join(o) //
            .on(c.column(customerID)).eq(o.column(orderCustomerID)) //
            .orderBy(SQL.asc(c.column(customerID))) //
            .compile(adapter);

        try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW); //
            IDBResultSet rows = prepared.executeQuery())
        {
          while (rows.next())
          {
            System.out.println("Customer/order key: " + rows.getInt(1) + "/" + rows.getInt(2));
          }
        }
      }
    }

    /**
     * Aggregates, Grouping, and HAVING
     * <p>
     * Aggregate expressions can be projected and used in HAVING. This query groups orders by status and applies a
     * parameterized minimum count after grouping. Aggregate and grouping support still depends on the target SQL
     * dialect and adapter.
     *
     * @author Eike Stepper
     */
    public class AggregatesGroupingAndHaving
    {
      /**
       * @snip
       */
      public void summarizeOrders(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField status, IDBField total) throws SQLException
      {
        // Inputs: adapter, connection, ORDERS table, STATUS and TOTAL fields.
        SQLStatement statement = SQL.select(status, SQL.count().as("ORDER_COUNT"), SQL.sum(total).as("GROSS_TOTAL")) //
            .from(orders) //
            .where(status).eq(SQL.param()) //
            .groupBy(status) //
            .having(SQL.count()).gt(SQL.param()) //
            .orderBy(SQL.asc(status)) //
            .compile(adapter);

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
     * Subqueries
     * <p>
     * A complete SELECT can be converted to {@link SQLSubSelect} and used in supported IN, EXISTS, or scalar contexts.
     * Its parameters join the outer statement's rendered occurrence order. A subquery is preferable to raw SQL when
     * the structured API can express the required shape.
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
        SQLSubSelect openCustomers = SQL.select(orderCustomerID) //
            .from(orders) //
            .where(status).eq(SQL.param()) //
            .subSelect();

        SQLStatement statement = SQL.select(customerID) //
            .from(customer) //
            .where(customerID).in(openCustomers) //
            .compile(adapter);

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
     * SQL Fragments
     * <p>
     * Fragments fill a gap when a query shape is not represented by the structured builders. Keep tables, columns,
     * and parameter tokens structured inside a fragment. Raw text is neither parsed nor escaped, and must not contain
     * question marks; represent every placeholder with a parameter token so occurrence metadata remains aligned with
     * JDBC indexes.
     *
     * @author Eike Stepper
     */
    public class SQLFragments
    {
      /**
       * @snip
       */
      public void searchByNameFragment(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name) throws SQLException
      {
        // Inputs: adapter, connection, CUSTOMER table and NAME field.
        // The raw SQL tokens are trusted syntax.
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
  }

  /**
   * Parameter Binding
   * <p>
   * A logical parameter is a reusable identity in a definition; compilation creates one occurrence record for each
   * place it appears in rendered SQL. The occurrence supplies the physical JDBC index and any type inferred from its
   * local context. Binding APIs address values by JDBC order, JDBC index, or logical identity.
   *
   * @author Eike Stepper
   */
  public class ParameterBinding
  {
    /**
     * Fundamentals
     * <p>
     * Sequential calls bind occurrence order. Indexed calls address one physical placeholder. Identity calls find all
     * occurrences of the same {@link SQLParameter} object. These choices matter when a logical parameter is reused in
     * different field contexts, because each physical occurrence can infer a different {@link DBType}.
     *
     * @author Eike Stepper
     */
    public class Fundamentals
    {
      /**
       * Parameters and Occurrences
       * <p>
       * Calling {@code SQL.param()} creates a logical parameter object. If that object is used twice, compilation
       * creates two occurrences with separate JDBC indexes. A field comparison or field assignment may let the
       * compiler infer a type for an occurrence. That type belongs to the occurrence, not to the logical parameter;
       * the same identity can therefore have a VARCHAR occurrence and an INTEGER occurrence.
       *
       * @author Eike Stepper
       */
      public class ParametersAndOccurrences
      {
      }

      /**
       * Positional and Identity-Based Binding
       * <p>
       * Positional values are assigned in rendered JDBC order. Binding by JDBC index changes one occurrence, while
       * binding by parameter identity changes every occurrence of that exact object in rendered order. The examples
       * preserve the earlier customer lookup and repeated-identity patterns.
       *
       * @author Eike Stepper
       */
      public class PositionalAndIdentityBinding
      {
        /**
         * @snip
         */
        public void bindPositionalParameters(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name)
            throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER.ID and CUSTOMER.NAME.
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(customerID).eq(SQL.param()) //
              .and(name).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            int bound = statement.bind(prepared, 501, "Ada Lovelace");
            System.out.println("Bound occurrences: " + bound);
          }
        }

        /**
         * @snip
         */
        public void bindRepeatedParameter(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name, IDBField email) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table, and its NAME/EMAIL fields.
          SQLParameter sharedValue = SQL.param();
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(name).eq(sharedValue) //
              .or(email).eq(sharedValue) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindParameter(prepared, sharedValue, "ada@example.test");
          }
        }
      }

      /**
       * Sequential and Cursor-Based Binding
       * <p>
       * {@code bind()} accepts a partial prefix and returns the number of values it bound. {@code bindAll()} requires
       * the complete placeholder count. {@code bindFrom()} starts a partial sequence at a specified one-based index.
       * A {@link SQLParameterBinder} owns an independent cursor: {@code bind()} advances after success,
       * {@code getNextIndex()} reports the next JDBC position, {@code skip()} moves without binding, and
       * {@code from(index)} repositions absolutely. The final legal position is parameter count plus one and accepts
       * only an empty sequence.
       *
       * @author Eike Stepper
       */
      public class SequentialAndCursorBinding
      {
        /**
         * @snip
         */
        public void bindCompleteAndPartialSequences(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name)
            throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table and ID/NAME fields.
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(customerID).eq(SQL.param()) //
              .and(name).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            int partialCount = statement.bind(prepared, 501);
            int completeCount = statement.bindAll(prepared, 502, "Ada");
            System.out.println("Partial count: " + partialCount + "; complete count: " + completeCount);
          }
        }

        /**
         * @snip
         */
        public void bindFromPosition(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name, IDBField email)
            throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table, and ID/NAME/EMAIL fields.
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(customerID).eq(SQL.param()) //
              .and(name).eq(SQL.param()) //
              .and(email).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bind(prepared, 501);
            int boundFromSecond = statement.bindFrom(prepared, 2, "Ada", "ada@example.test");
            System.out.println("Bound from position 2: " + boundFromSecond);
          }
        }

        /**
         * @snip
         */
        public void bindWithCursor(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table and ID/NAME fields.
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(customerID).eq(SQL.param()) //
              .and(name).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            SQLParameterBinder binder = statement.binder(prepared);
            binder.bind(501);

            System.out.println("Next position: " + binder.getNextIndex());
            binder.bind("Ada");
          }
        }

        /**
         * @snip
         */
        public void navigateBindingCursor(IDBAdapter adapter, IDBConnection connection, IDBTable orders, IDBField orderID, IDBField customerID, IDBField total,
            IDBField status) throws SQLException
        {
          // Inputs: adapter, connection, ORDERS table and its ID, CUSTOMER_ID, TOTAL, and STATUS fields.
          SQLStatement statement = SQL.selectAll() //
              .from(orders) //
              .where(orderID).eq(SQL.param()) //
              .and(customerID).eq(SQL.param()) //
              .and(total).eq(SQL.param()) //
              .and(status).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            SQLParameterBinder binder = statement.binder(prepared);
            binder //
                .bind(9001L) //
                .skip() //
                .skip(1) //
                .bind("OPEN");

            System.out.println("Next position: " + binder.getNextIndex());
            binder //
                .from(2) //
                .bind(501) //
                .bind(new BigDecimal("100.00"));
          }
        }
      }
    }

    /**
     * Types and Default Binding
     * <p>
     * The compiler infers a logical database type only when the occurrence is connected to a known field context,
     * such as a direct field comparison, BETWEEN bound, or INSERT/UPDATE assignment. Arbitrary expressions,
     * projections, and raw fragment tokens may not provide enough context. Explicit JDBC types can handle NULL where
     * inference is unavailable.
     *
     * @author Eike Stepper
     */
    public class TypesAndDefaultBinding
    {
      /**
       * DBType Inference
       * <p>
       * Type inference is local to the compiled occurrence. A parameter assigned to a field or compared directly
       * with a field inherits that field's {@link DBType}. BETWEEN uses the known field for both bounds. A parameter
       * in an arbitrary projection or a fragment does not necessarily have an inferable type. For an untyped NULL,
       * structured binding rejects the value if no occurrence type is available.
       *
       * @author Eike Stepper
       */
      public class DBTypeInference
      {
      }

      /**
       * Default JDBC Setters
       * <p>
        * Ordinary inferred values pass through {@code CompiledStatement} to {@code IDBAdapter.bindParameter()}.
        * {@code DBAdapter} chooses a typed setter only for a supported DBType and compatible Java value. A non-NULL
        * incompatible combination falls back to generic {@code setObject()}; it does not silently convert the Java
        * value. Known-type NULL uses {@code bindParameterNull()} and {@code getJDBCTypeForNull()}. For unknown-type
        * non-NULL values, Java-type dispatch is controlled by {@code useJavaTypeForUnknownParameterType()}, which
        * defaults to {@code false}.
        * <p>
        * <table>
        * <caption>Default DBType and Java-type mappings</caption>
        * <tr><th>DBType</th><th>Required Java value type</th><th>Default JDBC setter</th></tr>
        * <tr><td>{@code BOOLEAN}, {@code BIT}</td><td>{@code Boolean}</td><td>{@code setBoolean()}</td></tr>
        * <tr><td>{@code TINYINT}</td><td>{@code Byte}</td><td>{@code setByte()}</td></tr>
        * <tr><td>{@code SMALLINT}</td><td>{@code Short}</td><td>{@code setShort()}</td></tr>
        * <tr><td>{@code INTEGER}</td><td>{@code Integer}</td><td>{@code setInt()}</td></tr>
        * <tr><td>{@code BIGINT}</td><td>{@code Long}</td><td>{@code setLong()}</td></tr>
        * <tr><td>{@code FLOAT}, {@code REAL}</td><td>{@code Float}</td><td>{@code setFloat()}</td></tr>
        * <tr><td>{@code DOUBLE}</td><td>{@code Double}</td><td>{@code setDouble()}</td></tr>
        * <tr><td>{@code NUMERIC}, {@code DECIMAL}</td><td>{@code BigDecimal}</td><td>{@code setBigDecimal()}</td></tr>
        * <tr><td>{@code CHAR}, {@code VARCHAR}, {@code LONGVARCHAR}</td><td>{@code String}</td><td>{@code setString()}</td></tr>
        * <tr><td>{@code BINARY}, {@code VARBINARY}, {@code LONGVARBINARY}</td><td>{@code byte[]}</td><td>{@code setBytes()}</td></tr>
        * </table>
        * BLOB, CLOB, and temporal DBTypes have no automatic specialized mapping in this dispatch. In particular,
        * {@code BLOB} does not select {@code setBytes()} merely because a byte array is supplied.
       *
       * @author Eike Stepper
       */
      public class DefaultJDBCSetters
      {
        /**
         * @snip
         */
        public void bindInferredNull(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField email) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table, and its EMAIL and ID fields. EMAIL supplies the NULL's
          // inferred DBType.
          SQLStatement statement = SQL.update(customer) //
              .set(email).to(SQL.param()) //
              .where(customerID).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindAll(prepared, (Object)null, 501);
          }
        }
      }

      /**
       * NULL and Explicit JDBC Types
       * <p>
       * {@code SQL.param(sqlType)} stores a JDBC {@code Types} code. For a non-NULL value, the compiled statement
       * calls {@code setObject(index, value, sqlType)}; for NULL it calls {@code setNull(index, sqlType)}. This is
       * different from a custom {@link SQLValueSetter}: {@code SQL.param(Types.BLOB)} does not request
       * {@code PreparedStatement.setBytes()}. Use a configured setter such as {@code SQL.param(PreparedStatement::setBytes)}
       * when the complete binding operation must use that JDBC method.
       *
       * @author Eike Stepper
       */
      public class NullAndExplicitJDBCTypes
      {
        /**
         * @snip
         */
        public void bindExplicitlyTypedNull(IDBAdapter adapter, IDBConnection connection, IDBTable customer) throws SQLException
        {
          // Inputs: adapter, connection, and CUSTOMER.
          // A projection parameter has no field from which to infer DBType.
          SQLExpression projectedValue = SQL.param(Types.VARCHAR);
          SQLStatement statement = SQL.select(projectedValue) //
              .from(customer) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindAll(prepared, (Object)null);
          }
        }
      }
    }

    /**
     * Custom Binding Strategies
     * <p>
     * A converter transforms an application value and leaves JDBC binding to the adapter. A setter receives the
     * original value and performs the complete JDBC operation. Call-specific strategies override configured ones for
     * one operation. The diagram shows how {@code CompiledStatement} resolves those strategies before default binding.
     * <p>
     * {@image binding-strategy-resolution.svg}
     *
     * @author Eike Stepper
     */
    public class CustomBindingStrategies
    {
      /**
       * Value Converters
       * <p>
       * A converter maps a non-NULL application value to the value supplied to default binding. NULL input bypasses
       * conversion and follows the occurrence's explicit or inferred JDBC NULL typing. Converter exceptions propagate
       * to the caller.
       *
       * @author Eike Stepper
       */
      public class ValueConverters
      {
        /**
         * @snip
         */
        public void bindConvertedValue(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table and NAME field.
          SQLParameter trimmedName = SQL.param((String value) -> value.trim());
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(name).eq(trimmedName) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bind(prepared, " Ada Lovelace ");
          }
        }

        /**
         * @snip
         */
        public void overrideValueConverter(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table and NAME field.
          SQLParameter configuredTrim = SQL.param((String value) -> value.trim());
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(name).eq(configuredTrim) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindParameter(prepared, 1, " ada ", String::toUpperCase);
          }
        }
      }

      /**
       * SQLValueSetter
       * <p>
       * A configured or explicit {@link SQLValueSetter} owns the full JDBC operation and receives the original
       * application value, including NULL. It bypasses converter and adapter binding for that occurrence. The default
       * byte-array mapping covers BINARY, VARBINARY, and LONGVARBINARY, not BLOB. {@code SQL.param(Types.BLOB)} selects
       * explicit target typing through {@code setObject(..., Types.BLOB)} or {@code setNull(..., Types.BLOB)}; it does
       * not select {@code setBytes()}. An explicitly configured {@link SQLValueSetter} guarantees the chosen JDBC
       * operation and can be useful for driver-specific BLOB compatibility, although drivers do not universally
       * require {@code setBytes()} for BLOB.
       *
       * @author Eike Stepper
       */
      public class SQLValueSetters
      {
        /**
         * @snip
         */
        public void bindWithConfiguredSetter(IDBAdapter adapter, IDBConnection connection, IDBTable attachment, IDBField payload, byte[] bytes)
            throws SQLException
        {
          // Inputs: adapter, connection, ATTACHMENT table whose PAYLOAD is modeled as DBType.BLOB, and bytes to store.
          SQLParameter payloadValue = SQL.param(PreparedStatement::setBytes);
          SQLStatement statement = SQL.insertInto(attachment) //
              .set(payload).to(payloadValue) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindParameter(prepared, payloadValue, bytes);
            prepared.executeUpdate();
          }
        }

        /**
         * @snip
         */
        public void overrideValueSetter(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table and NAME field.
          SQLParameter parameter = SQL.param((String value) -> value.trim());
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(name).eq(parameter) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindParameter(prepared, parameter, "Ada", //
                (stmt, index, value) -> stmt.setString(index, "[" + value + "]"));
          }
        }
      }

      /**
       * Overrides and Precedence
       * <p>
       * A call-specific setter runs first and receives the original value. Otherwise a call-specific converter, when
       * supplied, transforms the value and takes precedence over a parameter-configured setter. Without a call-level
       * strategy, the configured setter runs before a configured converter. If no setter handles the value, the
       * explicit JDBC type controls typed setObject/setNull; otherwise default adapter binding handles it.
       * Converters are applied only to non-NULL values. A setter and converter cannot both be configured on one
       * {@link SQLParameter}.
       * <p>
       * The order in this table is precedence for one binding operation. A selected setter performs the complete
       * operation and exits the default path.
       * <table>
       * <caption>Binding strategy precedence</caption>
       * <tr><th>Order</th><th>Strategy</th><th>Effect</th></tr>
       * <tr><td>1</td><td>Explicit SQLValueSetter</td><td>Uses the supplied setter and original value; bypasses remaining strategies.</td></tr>
       * <tr><td>2</td><td>Explicit converter</td><td>Converts non-NULL value; overrides configured strategy, then continues to JDBC binding.</td></tr>
       * <tr><td>3</td><td>Configured SQLValueSetter</td><td>Uses the parameter setter and original value; bypasses remaining strategies.</td></tr>
       * <tr><td>4</td><td>Configured converter</td><td>Converts non-NULL value, then continues to JDBC binding.</td></tr>
       * <tr><td>5</td><td>Explicit JDBC target type</td><td>Uses setObject(value, type) or setNull(type).</td></tr>
       * <tr><td>6</td><td>Default adapter binding</td><td>Uses inferred DBType and adapter policy for NULL or non-NULL values.</td></tr>
       * </table>
       * An explicit converter therefore overrides a configured setter for that call, while a call-specific setter
       * overrides both. Explicit JDBC types apply only when a setter has not already handled the value.
       *
       * @author Eike Stepper
       */
      public class OverridesAndPrecedence
      {
      }

      /**
       * SQLDefaultSetter
       * <p>
       * An {@link SQLDefaultSetter} receives a statement and JDBC index, but no application value. It performs the
       * complete binding operation for an implementation-defined default. That default is not necessarily NULL or the
       * SQL column's {@code DEFAULT} expression. It may be a domain value such as an empty byte array. This strategy
       * is separate from binding a supplied value through {@link SQLValueSetter} or the default adapter path.
       *
       * @author Eike Stepper
       */
      public class SQLDefaultSetters
      {
        /**
         * @snip
         */
        public void bindImplementationDefault(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name, IDBField email)
            throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table, and NAME/EMAIL fields.
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(name).eq(SQL.param()) //
              .and(email).eq(SQL.param()) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            SQLValueSetter<String> valueSetter = PreparedStatement::setString;
            SQLDefaultSetter defaultSetter = (stmt, index) -> stmt.setString(index, "unassigned");

            SQLParameterBinder binder = statement.binder(prepared);
            binder //
                .bindParameter("Ada", valueSetter) //
                .bindParameter(defaultSetter);
          }
        }

        /**
         * @snip
         */
        public void bindDefaultByIdentity(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField name, IDBField email) throws SQLException
        {
          // Inputs: adapter, connection, CUSTOMER table, and NAME/EMAIL fields.
          SQLParameter shared = SQL.param();
          SQLStatement statement = SQL.selectAll() //
              .from(customer) //
              .where(name).eq(shared) //
              .or(email).eq(shared) //
              .compile(adapter);

          try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
          {
            statement.bindDefault(prepared, shared, (stmt, index) -> stmt.setString(index, "unassigned"));
          }
        }
      }
    }
  }

  /**
   * DBAdapter Customization
   * <p>
   * {@code IDBAdapter.bindParameter()} is the public binding hook used for ordinary bindings. Its interface default
   * is deliberately generic for independent implementations. {@code DBAdapter} supplies a typed implementation and
   * protected per-setter extension points for adapters that need to change one JDBC operation without replacing the
   * type-dispatch algorithm.
   *
   * @author Eike Stepper
   */
  public class DBAdapterCustomization
  {
    /**
     * Default Binding Architecture
     * <p>
     * The SQL definition assigns values to logical parameters. Compilation records each physical occurrence and its
     * inferred DBType. {@code CompiledStatement} applies selected converters, handles explicit JDBC target types, and
     * delegates every remaining value, including typed NULL, to {@code IDBAdapter.bindParameter()}. The generic
     * interface default uses {@code setObject} except for known-type NULL, which uses
     * {@code getJDBCTypeForNull()}. {@code DBAdapter} dispatches supported compatible pairs to typed setters. This
     * keeps occurrence logic in the compiler and JDBC customization in the adapter.
     * <p>
     * The diagram shows the adapter's independent default-binding decisions, including its direct-call handling for
     * unknown-type NULL and the opt-in Java-type fallback for unknown non-NULL values.
     * <p>
     * {@image default-jdbc-parameter-binding.svg}
     *
     * @author Eike Stepper
     */
    public class DefaultBindingArchitecture
    {
      /**
       * @snip
       */
      public void bindDefaultScalarParameters(IDBAdapter adapter, IDBConnection connection, IDBTable customer, IDBField customerID, IDBField name)
          throws SQLException
      {
        // Inputs: adapter, connection, CUSTOMER table, and ID/NAME fields.
        // DBAdapter selects typed setters for these pairs.
        SQLStatement statement = SQL.selectAll() //
            .from(customer) //
            .where(customerID).eq(SQL.param()) //
            .and(name).eq(SQL.param()) //
            .compile(adapter);

        try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
        {
          statement.bind(prepared, Integer.valueOf(501), "Ada");
        }
      }
    }

    /**
     * Unknown Parameter Types
     * <p>
     * {@code DBAdapter.useJavaTypeForUnknownParameterType()} defaults to {@code false}. Under that conservative
     * policy, an unknown non-NULL value uses {@code setObject}. If enabled, supported Java scalar classes select the
     * same typed hooks used for known compatible DBType/value pairs; unsupported classes still use {@code setObject}.
     * The policy does not change known-type dispatch, and unknown NULL remains generic object binding at the adapter
     * level. Structured DML separately rejects unknown untyped NULL before delegation.
     *
     * @author Eike Stepper
     */
    public class UnknownParameterTypes
    {
      /**
       * @snip
       */
      public void bindUnknownParameterType(IDBConnection connection, IDBTable customer) throws SQLException
      {
        // Inputs: connection and CUSTOMER table.
        // This projection comparison has no field-derived parameter DBType.
        SQLTemplate template = SQL.selectAll() //
            .from(customer) //
            .where(SQL.eq(SQL.param(), SQL.ONE)) //
            .build();

        IDBAdapter conservative = new H2Adapter();
        IDBAdapter javaTypeFallback = new H2Adapter()
        {
          @Override
          public boolean useJavaTypeForUnknownParameterType()
          {
            return true;
          }
        };

        SQLStatement conservativeStatement = template.compile(conservative);

        try (IDBPreparedStatement prepared = conservativeStatement.prepare(connection, ReuseProbability.LOW))
        {
          // DBAdapter uses setObject.
          conservativeStatement.bind(prepared, Integer.valueOf(42));
        }

        SQLStatement fallbackStatement = template.compile(javaTypeFallback);

        try (IDBPreparedStatement prepared = fallbackStatement.prepare(connection, ReuseProbability.LOW))
        {
          // DBAdapter uses setInt for this supported Java type.
          fallbackStatement.bind(prepared, Integer.valueOf(42));
        }
      }
    }

    /**
     * Protected Binding Extension Points
     * <p>
     * {@code DBAdapter} exposes exactly twelve protected overridable methods. The default implementation makes the
     * matching JDBC call. Most adapters need no override; a database-specific adapter can change one operation while
      * retaining the common mapping and fallback behavior. The Boolean example is hypothetical: it models a backend
      * whose Boolean values are represented as integer 0/1 parameters. The H2Adapter subclass is a concrete,
      * compilable illustration of the extension mechanism; it does not imply that H2 generally requires integer
      * Boolean binding. Adapters should normally retain {@code setBoolean()} unless their backend or driver gives a
      * concrete reason to change it.
     * <table>
     * <caption>DBAdapter binding extension points</caption>
     * <tr><th>Method</th><th>Default JDBC call</th></tr>
     * <tr><td>{@code bindParameterBoolean}</td><td>{@code setBoolean(index, value)}</td></tr>
     * <tr><td>{@code bindParameterByte}</td><td>{@code setByte(index, value)}</td></tr>
     * <tr><td>{@code bindParameterShort}</td><td>{@code setShort(index, value)}</td></tr>
     * <tr><td>{@code bindParameterInt}</td><td>{@code setInt(index, value)}</td></tr>
     * <tr><td>{@code bindParameterLong}</td><td>{@code setLong(index, value)}</td></tr>
     * <tr><td>{@code bindParameterFloat}</td><td>{@code setFloat(index, value)}</td></tr>
     * <tr><td>{@code bindParameterDouble}</td><td>{@code setDouble(index, value)}</td></tr>
     * <tr><td>{@code bindParameterBigDecimal}</td><td>{@code setBigDecimal(index, value)}</td></tr>
     * <tr><td>{@code bindParameterString}</td><td>{@code setString(index, value)}</td></tr>
     * <tr><td>{@code bindParameterBytes}</td><td>{@code setBytes(index, value)}</td></tr>
     * <tr><td>{@code bindParameterObject}</td><td>{@code setObject(index, value)}</td></tr>
     * <tr><td>{@code bindParameterNull}</td><td>{@code setNull(index, getJDBCTypeForNull(type))}</td></tr>
     * </table>
     *
     * @author Eike Stepper
     */
    public class ProtectedBindingExtensionPoints
    {
      /**
       * @snip
       */
      public void customizeBooleanBinding(IDBConnection connection, IDBTable customer, IDBField active) throws SQLException
      {
        // Inputs: connection, CUSTOMER table, and a BOOLEAN ACTIVE field.
        // This hypothetical backend expects integer Boolean values.
        IDBAdapter adapter = new H2Adapter()
        {
          @Override
          protected void bindParameterBoolean(PreparedStatement stmt, int index, boolean value) throws SQLException
          {
            stmt.setInt(index, value ? 1 : 0);
          }
        };

        SQLStatement statement = SQL.selectAll() //
            .from(customer) //
            .where(active).eq(SQL.param()) //
            .compile(adapter);

        try (IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW))
        {
          statement.bind(prepared, Boolean.TRUE);
        }
      }
    }
  }

  /**
   * Statement Lifecycle and Reuse
   * <p>
   * Reuse the adapter-neutral template or compiled statement across operations. Prepare and close a managed JDBC
   * statement for each checkout so its connection can return the statement to its cache and release associated
   * resources.
   *
   * @author Eike Stepper
   */
  public class StatementLifecycleAndReuse
  {
    /**
     * Templates and Compiled Statements
     * <p>
     * A template captures the builder's immutable definition. The adapter-specific compiled statement retains its SQL
     * and occurrence metadata, but no connection or prepared statement. Reuse either definition for repeated
     * executions.
     *
     * @author Eike Stepper
     */
    public class TemplatesAndCompiledStatements
    {
    }

    /**
     * Preparing, Executing, and Closing
     * <p>
     * Preparing checks out a connection-bound managed statement. Use try-with-resources for that statement and any
     * result set. The connection's cache may reuse the JDBC object after close; {@link ReuseProbability} is a cache
     * hint and does not guarantee object identity.
     *
     * @author Eike Stepper
     */
    public class PreparingExecutingAndClosing
    {
    }

    /**
     * Partial Binding and Statement Reuse
     * <p>
     * Partial binding changes only the positions supplied in that operation. Untouched positions may still contain
     * values from an earlier use of a reused JDBC statement. Rebind every position required by the next execution.
     * Range and value-count checks happen before a sequence starts, but a later setter failure does not roll back
     * earlier JDBC side effects.
     *
     * @author Eike Stepper
     */
    public class PartialBindingAndStatementReuse
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
            statement.bindAll(prepared, id);

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
  }

  /**
   * Portability and Limitations
   * <p>
   * The compiler renders operations represented by the structured API and delegates identifier quoting and typed
   * binding behavior to the adapter. It does not rewrite arbitrary fragment text, validate business rules, map result
   * rows to objects, infer every parameter type through arbitrary expression trees, or generate DDL. SQL syntax and
   * join support remain bounded by the adapter and target database. Keep portable portions structured and isolate
   * vendor-specific syntax where it is needed.
   *
   * @author Eike Stepper
   */
  public class PortabilityAndLimitations
  {
  }
}
