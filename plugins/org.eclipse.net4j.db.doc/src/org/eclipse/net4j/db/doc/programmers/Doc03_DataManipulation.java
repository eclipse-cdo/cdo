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

import java.sql.SQLException;
import java.sql.Types;

/**
 * Data Manipulation
 * <p>
 * Net4j DB's DML API builds structured SELECT, INSERT, UPDATE, and DELETE definitions from schema objects. It
 * separates statement construction from adapter-specific SQL rendering and from connection-bound JDBC work. The
 * public API is in {@code org.eclipse.net4j.db.dml} and its staged builder contracts are in
 * {@code org.eclipse.net4j.db.dml.fluent}. Builders make clause order explicit; immutable templates can be compiled
 * for a particular adapter and then prepared on a connection.
 * <p>
 * The examples assume schema tables and fields already exist. Define and evolve those through
 * {@link Doc02_DataDefinition}; database, connection, and statement ownership are introduced in
 * {@link Doc01_GeneralUtilities}. SQL fragments are trusted SQL text, while values should be parameters. This API
 * does not provide result-to-object mapping or promise that every construct is supported identically by every SQL
 * dialect.
 * <p>
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_DataManipulation
{
  /**
   * Definitions, Expressions, and Builder Stages
   * <p>
   * {@link SQL} is the factory for definitions and expressions. {@code SQLExpression} represents a value,
   * {@code SQLCondition} a boolean predicate, and {@code SQLSubSelect} a complete SELECT usable as a nested query.
   * The fluent interfaces constrain which operation is available next: SELECT progresses from projection to source
   * and optional clauses; comparisons require a right-hand operand; assignments require a value; joins require an
   * ON condition. Retain the current stage only while building. Stale stages reject mutation after the builder has
   * advanced.
   * <p>
   * The builder's {@code build()} method captures an adapter-neutral {@link SQLTemplate}. The template is an
   * immutable snapshot, so later builder operations do not alter it. Compile it with an {@link IDBAdapter} to obtain
   * an {@link SQLStatement} containing SQL and parameter occurrence metadata.
   *
   * @author Eike Stepper
   */
  public class DefinitionsAndStages
  {
  }

  /**
   * SELECT, Aliases, and Joins
   * <p>
   * Projections and predicates use schema fields as expressions. A table occurrence can be aliased with
   * {@link IDBTable#as(String)}; qualify its columns with {@code SQLTableRef.column(field)}. This is distinct from
   * {@code expression.as("label")}, which aliases a projected value. The API supports projections, wildcard
   * selection, multiple FROM sources, INNER and LEFT joins, WHERE, GROUP BY, HAVING, and ORDER BY.
   * <p>
   * {@link #selectRows(IDBAdapter, IDBTable, IDBField, IDBField) SelectRows.java}
   * {@link #joinRows(IDBTable, IDBField, IDBTable, IDBField) JoinRows.java}
   *
   * @author Eike Stepper
   */
  public class SelectQueries
  {
    /**
     * @snip
     */
    public SQLStatement selectRows(IDBAdapter adapter, IDBTable table, IDBField id, IDBField name)
    {
      return SQL.select(id, name) //
          .from(table) //
          .where(id).eq(SQL.param()) //
          .compile(adapter);
    }

    /**
     * @snip
     */
    public SQLTemplate joinRows(IDBTable objects, IDBField objectID, IDBTable details, IDBField detailObjectID)
    {
      SQLTableRef o = objects.as("o");
      SQLTableRef d = details.as("d");

      return SQL.select(o.column(objectID), d.column(detailObjectID)) //
          .from(o) //
          .leftJoin(d) //
          .on(o.column(objectID)).eq(d.column(detailObjectID)) //
          .build();
    }
  }

  /**
   * Conditions, Aggregates, and Subqueries
   * <p>
   * Conditions can be composed using AND, OR, and NOT and can be assembled before they are supplied to a builder.
   * Comparison factories cover equality and ordering, LIKE, BETWEEN, and IN. Aggregate expressions include COUNT,
   * COUNT DISTINCT, MIN, MAX, SUM, and AVG. Complete SELECT definitions can be wrapped as scalar, EXISTS, or IN
   * subqueries. Keep each condition complete before combining it; this makes optional filters explicit and keeps
   * parameter order tied to the final SQL rendering.
   * <p>
   * {@link #groupedQuery(IDBAdapter, IDBTable, IDBField, IDBField) GroupedQuery.java}
   *
   * @author Eike Stepper
   */
  public class ConditionsAndSubqueries
  {
    /**
     * @snip
     */
    public SQLStatement groupedQuery(IDBAdapter adapter, IDBTable table, IDBField category, IDBField created)
    {
      SQLCondition active = SQL.eq(category, SQL.param());
      SQLCondition recent = SQL.ge(created, SQL.param());

      return SQL.select(category, SQL.count().as("total")) //
          .from(table) //
          .where(active.and(recent)) //
          .groupBy(category) //
          .having(SQL.count()).gt(SQL.param()) //
          .orderBy(SQL.asc(category)) //
          .compile(adapter);
    }

    /**
     * @snip
     */
    public SQLTemplate selectWithSubquery(IDBTable objects, IDBField id, IDBTable details, IDBField detailObjectID, IDBField status)
    {
      SQLSubSelect matchingIDs = SQL.select(detailObjectID) //
          .from(details) //
          .where(status).eq(SQL.param()) //
          .subSelect();

      return SQL.select(id) //
          .from(objects) //
          .where(id).in(matchingIDs) //
          .build();
    }
  }

  /**
   * INSERT, UPDATE, and DELETE
   * <p>
   * INSERT can use the table's physical column order, an explicit column list paired positionally with values, or
   * named assignments. A values-only INSERT must provide one value for each physical table column in that order.
   * UPDATE requires at least one complete assignment. DELETE can be built without a WHERE clause, so include a
   * predicate whenever the operation must be restricted. As with SELECT, build a template once when the definition
   * will be compiled more than once.
   * <p>
   * {@link #insertTemplate(IDBTable, IDBField, IDBField) InsertTemplate.java}
   * {@link #updateTemplate(IDBTable, IDBField, IDBField) UpdateTemplate.java}
   * {@link #deleteTemplate(IDBTable, IDBField) DeleteTemplate.java}
   *
   * @author Eike Stepper
   */
  public class DataChanges
  {
    /**
     * @snip
     */
    public SQLTemplate insertTemplate(IDBTable table, IDBField id, IDBField name)
    {
      return SQL.insertInto(table) //
          .columns(id, name) //
          .values(SQL.param(), SQL.param()) //
          .build();
    }

    /**
     * @snip
     */
    public SQLTemplate updateTemplate(IDBTable table, IDBField id, IDBField name)
    {
      return SQL.update(table) //
          .set(name).to(SQL.param()) //
          .where(id).eq(SQL.param()) //
          .build();
    }

    /**
     * @snip
     */
    public SQLTemplate deleteTemplate(IDBTable table, IDBField id)
    {
      return SQL.deleteFrom(table) //
          .where(id).eq(SQL.param()) //
          .build();
    }
  }

  /**
   * Templates, Compilation, and Adapter Behavior
   * <p>
   * A template contains structured identifiers, expressions, and parameter references without retaining a
   * connection. Compile it with the target adapter to produce database-specific SQL. The compiler quotes schema
   * identifiers through the adapter, including names represented as structured table and column references. A
   * template can therefore be compiled independently for multiple adapters; arbitrary SQL text inside a fragment is
   * copied as supplied and is not rewritten into a dialect-neutral form.
   * <p>
   * The compiled statement retains no connection or JDBC statement. Preparing delegates to
   * {@link IDBConnection#prepareStatement(String, ReuseProbability)} using the requested reuse hint. Preparation is
   * separate from binding and execution.
   * <p>
   * {@link #compileForAdapter(IDBAdapter, IDBTable, IDBField) CompileForAdapter.java}
   *
   * @author Eike Stepper
   */
  public class Compilation
  {
    /**
     * @snip
     */
    public SQLStatement compileForAdapter(IDBAdapter adapter, IDBTable table, IDBField id)
    {
      SQLTemplate template = SQL.selectAll() //
          .from(table) //
          .where(id).eq(SQL.param()) //
          .build();

      return template.compile(adapter);
    }
  }

  /**
   * Parameters and Binding
   * <p>
   * Every {@code SQL.param()} creates a logical parameter identity. Each use renders as one JDBC placeholder
   * occurrence. Positional binding supplies exactly one value for each occurrence in SQL order. Binding by
   * {@link SQLParameter} identity sets every occurrence that refers to that same object. If an identity occurs more
   * than once, the supplied value is bound at each occurrence in generated SQL order.
   * <p>
   * The varargs count is checked before setters run, but a later converter, binder, or JDBC failure may happen after
   * earlier positions have been set; binding is not atomic. JDBC parameter indexes are one-based. An unknown
   * parameter identity is rejected.
   * <p>
   * {@link #bindByIdentity(IDBAdapter, IDBConnection, IDBTable, IDBField, IDBField) BindByIdentity.java}
   *
   * @author Eike Stepper
   */
  public class ParameterBinding
  {
    /**
     * @snip
     */
    public void bindByIdentity(IDBAdapter adapter, IDBConnection connection, IDBTable table, IDBField id, IDBField name) throws SQLException
    {
      SQLParameter shared = SQL.param();
      SQLStatement statement = SQL.selectAll() //
          .from(table) //
          .where(id).eq(shared) //
          .or(name).eq(shared) //
          .compile(adapter);

      IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.HIGH);

      try
      {
        statement.bindParameter(prepared, shared, "Ada");

        try (IDBResultSet resultSet = prepared.executeQuery())
        {
          while (resultSet.next())
          {
            // Read the current row through the result-set API.
          }
        }
      }
      finally
      {
        prepared.close();
      }
    }
  }

  /**
   * NULL Values, Converters, and Custom Binders
   * <p>
   * A parameter created with {@code SQL.param(int)} carries a JDBC {@link Types} code. It controls typed
   * NULL handling and typed {@code setObject}. Without an explicit code, NULL typing is inferred from a directly
   * associated schema field and the adapter used to compile the statement. The inference does not propagate through
   * arbitrary functions, arithmetic, fragments, or aggregate results. When no type can be established, binding fails
   * rather than guessing.
   * <p>
   * A converter transforms a non-null Java value before default JDBC binding; null input bypasses conversion, and a
   * converter result of null follows the same typed-NULL rules. An explicit {@link SQLParameterBinder} takes
   * precedence and receives the original value, including null, at each occurrence. Use it for JDBC-specific setters
   * or external type handlers.
   * <p>
   * {@link #bindTypedNull(IDBAdapter, IDBConnection, IDBTable, IDBField) BindTypedNull.java}
   * {@link #insertPayload(IDBAdapter, IDBConnection, IDBTable, IDBField, byte[]) InsertPayload.java}
   *
   * @author Eike Stepper
   */
  public class SpecializedBinding
  {
    /**
     * @snip
     */
    public void bindTypedNull(IDBAdapter adapter, IDBConnection connection, IDBTable table, IDBField name) throws SQLException
    {
      SQLParameter nullableName = SQL.param(Types.VARCHAR);
      SQLStatement statement = SQL.selectAll() //
          .from(table) //
          .where(name).eq(nullableName) //
          .compile(adapter);

      IDBPreparedStatement prepared = statement.prepare(connection, ReuseProbability.LOW);

      try
      {
        statement.bindParameter(prepared, nullableName, null);
      }
      finally
      {
        prepared.close();
      }
    }

    /**
     * @snip
     */
    public void insertPayload(IDBAdapter adapter, IDBConnection connection, IDBTable table, IDBField payload, byte[] bytes) throws SQLException
    {
      SQLParameter parameter = SQL.param();
      SQLParameterBinder<byte[]> binder = (statement, index, value) -> statement.setBytes(index, value);
      SQLStatement insert = SQL.insertInto(table) //
          .set(payload).to(parameter) //
          .compile(adapter);

      IDBPreparedStatement prepared = insert.prepare(connection, ReuseProbability.LOW);

      try
      {
        insert.bindParameter(prepared, parameter, bytes, binder);
        prepared.executeUpdate();
      }
      finally
      {
        prepared.close();
      }
    }
  }

  /**
   * Structured Fragments and Dynamic Conditions
   * <p>
   * A fragment combines trusted SQL text with structured table, column, expression, condition, nested-fragment, and
   * parameter parts. Raw SQL is not parsed or escaped. Never concatenate untrusted values into raw text; represent
   * values with parameters and identifiers with structured references. Raw fragment text must not contain a question
   * mark, even inside a quoted literal, because each placeholder must be represented by a structured parameter so
   * that occurrence metadata remains aligned with JDBC positions.
   * <p>
   * For optional predicates, compose complete conditions before passing them to the builder. This avoids retaining a
   * partially completed comparison stage and makes the final parameter ordering visible in one definition.
   * <p>
   * {@link #buildFragment(IDBTable, IDBField, IDBField) BuildFragment.java}
   * {@link #dynamicFilter(IDBTable, IDBField, IDBField, boolean) DynamicFilter.java}
   *
   * @author Eike Stepper
   */
  public class FragmentsAndDynamicQueries
  {
    /**
     * @snip
     */
    public SQLTemplate buildFragment(IDBTable table, IDBField id, IDBField name)
    {
      SQLParameter parameter = SQL.param();
      SQLFragment fragment = SQL.fragment() //
          .sql("SELECT ").column(id) //
          .sql(" FROM ").table(table) //
          .sql(" WHERE ").column(name).sql(" = ").param(parameter) //
          .build();

      return SQL.build(fragment);
    }

    /**
     * @snip
     */
    public SQLTemplate dynamicFilter(IDBTable table, IDBField status, IDBField created, boolean includeDate)
    {
      SQLCondition filter = SQL.eq(status, SQL.param());

      if (includeDate)
      {
        filter = filter.and(SQL.ge(created, SQL.param()));
      }

      return SQL.selectAll() //
          .from(table) //
          .where(filter) //
          .build();
    }
  }

  /**
   * Executing Statements and Resource Lifecycle
   * <p>
   * Prepare the compiled statement on the connection that will execute it, bind all required values, then execute
   * through the prepared statement. Close result sets before closing the prepared statement. Close the statement when
   * finished even when execution or row processing fails. A template or compiled statement can be reused, but a JDBC
   * statement belongs to its connection and must not outlive that connection.
   * <p>
   * SQL generation, binding, and execution fail at different boundaries: compilation may reject an invalid
   * definition, binding may fail due to conversion or JDBC setters, and execution may fail with a vendor SQL error.
   * These failures do not implicitly roll back a caller-managed transaction. Transaction ownership remains with the
   * application code using the connection.
   *
   * @author Eike Stepper
   */
  public class ExecutionAndLifecycle
  {
  }

  /**
   * Limits and Extension Points
   * <p>
   * Use adapter behavior for identifier quoting and inferred NULL JDBC types, a value converter for a pure Java
   * transformation, and a parameter binder when the JDBC setter itself must be specialized. The compiler handles
   * only constructs represented by the public DML API and does not infer arbitrary expression types, map result
   * rows, parameterize raw fragments automatically, or generate DDL. Database syntax support still depends on the
   * target adapter and server. The earlier temporary DML guide's material on stages, query forms, joins, aggregates,
   * subqueries, insert variants, fragments, adapter compilation, parameter identity/order, NULL inference, binders,
   * and statement ownership is now expressed here as Java-source documentation.
   *
   * @author Eike Stepper
   */
  public class LimitsAndExtensions
  {
  }
}
