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
package org.eclipse.net4j.internal.db.dml;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.SQLTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable snapshot of an adapter-independent SQL definition. Schema objects and parameter
 * identities are referenced, not copied; later schema metadata changes may therefore affect a
 * subsequent compilation, while already compiled statements retain their SQL and metadata.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class Template implements SQLTemplate
{
  /**
   * The top-level statement form, independent of the individual clause operations.
   */
  private final Kind kind;

  /**
   * The INSERT, UPDATE, or DELETE target; SELECT sources remain in {@link #operations}.
   */
  private final IDBTable table;

  /**
   * Immutable ordered clauses for non-fragment templates, empty for FRAGMENT templates.
   */
  private final List<Operation> operations;

  /**
   * The structured content for FRAGMENT templates, and {@code null} for other kinds.
   */
  private final SQLFragment fragment;

  /**
   * Creates a SELECT, INSERT, UPDATE, or DELETE snapshot. The constructor enforces the statement
   * kind's table requirement and copies the ordered operation records.
   */
  public Template(Kind kind, IDBTable table, Operation... operations)
  {
    this.kind = Objects.requireNonNull(kind, "kind");
    if (kind == Kind.FRAGMENT)
    {
      throw new IllegalArgumentException("FRAGMENT templates must be created with a fragment");
    }

    boolean requiresTable = kind == Kind.INSERT || kind == Kind.UPDATE || kind == Kind.DELETE;
    if (requiresTable != (table != null))
    {
      throw new IllegalArgumentException(kind + (requiresTable ? " requires a table" : " must not have a table"));
    }

    this.table = table;
    Operation[] operationCopy = Objects.requireNonNull(operations, "operations").clone();

    for (Operation operation : operationCopy)
    {
      Objects.requireNonNull(operation, "operation");
    }

    this.operations = Collections.unmodifiableList(Arrays.asList(operationCopy));
    fragment = null;
  }

  /**
   * Creates a fragment-only snapshot with no statement operations or table target.
   */
  public Template(SQLFragment fragment)
  {
    kind = Kind.FRAGMENT;
    table = null;
    operations = Collections.emptyList();
    this.fragment = Objects.requireNonNull(fragment, "fragment");
  }

  public Kind getKind()
  {
    return kind;
  }

  public IDBTable getTable()
  {
    return table;
  }

  /**
   * Returns the immutable ordered operation snapshot. This list is empty for fragment templates.
   */
  public List<Operation> getOperations()
  {
    return operations;
  }

  /**
   * Returns the structured fragment for a FRAGMENT template, or {@code null} for other kinds.
   */
  public SQLFragment getFragment()
  {
    return fragment;
  }

  /**
   * Creates a SELECT-only subquery view over this immutable snapshot.
   */
  public SQLSubSelect asSubSelect()
  {
    if (kind != Kind.SELECT)
    {
      throw new IllegalStateException("Only SELECT templates can be used as subqueries");
    }

    return new SelectSubquery(this);
  }

  /**
   * Compiles this immutable definition using adapter-specific identifier quoting.
   *
   * @param adapter the database adapter used for rendering
   * @return the immutable compiled SQL statement
   */
  @Override
  public SQLStatement compile(IDBAdapter adapter)
  {
    return Compiler.compile(this, adapter);
  }

  @Override
  public String toString()
  {
    if (kind == Kind.FRAGMENT)
    {
      int partCount = fragment instanceof Fragment ? ((Fragment)fragment).getParts().size() : -1;
      return "FRAGMENT [parts=" + (partCount < 0 ? "unknown" : partCount) + "]";
    }

    String tableName = table == null ? "" : " " + table.getName();
    return kind + tableName + " [operations=" + operations.size() + "]";
  }

  /**
   * Statement kinds describe the top-level template form; operation kinds describe individual
   * ordered clauses within that form.
   *
   * @author Eike Stepper
   */
  public enum Kind
  {
    SELECT, //
    INSERT, //
    UPDATE, //
    DELETE, //
    FRAGMENT
  }

  /**
   * One immutable clause or comparison record in a template's ordered operation sequence.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  public static final class Operation
  {
    /**
     * The clause or comparison represented by this record.
     */
    private final Kind kind;

    /**
     * The operation's operands, copied on construction and access.
     */
    private final Object[] operands;

    public Operation(Kind kind, Object... operands)
    {
      this.kind = Objects.requireNonNull(kind, "kind");
      this.operands = Objects.requireNonNull(operands, "operands").clone();
    }

    public Kind getKind()
    {
      return kind;
    }

    public Object[] getOperands()
    {
      return operands.clone();
    }

    @Override
    public String toString()
    {
      StringBuilder result = new StringBuilder();
      appendOperation(this, result, 0);
      return result.toString();
    }

    private static void appendOperation(Operation operation, StringBuilder result, int depth)
    {
      if (result.length() >= MAX_DIAGNOSTIC_LENGTH)
      {
        return;
      }

      if (depth >= MAX_DIAGNOSTIC_DEPTH)
      {
        appendText(result, "...");
        return;
      }

      appendText(result, operation.kind.name());
      appendText(result, "[");

      for (int i = 0; i < operation.operands.length && i < MAX_DIAGNOSTIC_ITEMS; i++)
      {
        if (i > 0)
        {
          appendText(result, ", ");
        }

        appendOperand(operation.operands[i], result, depth + 1);
      }

      if (operation.operands.length > MAX_DIAGNOSTIC_ITEMS)
      {
        appendText(result, operation.operands.length == 0 ? "..." : ", ...");
      }

      appendText(result, "]");
    }

    private static void appendOperand(Object operand, StringBuilder result, int depth)
    {
      if (result.length() >= MAX_DIAGNOSTIC_LENGTH)
      {
        return;
      }

      if (depth >= MAX_DIAGNOSTIC_DEPTH)
      {
        appendText(result, "...");
      }
      else if (operand == null)
      {
        appendText(result, "null");
      }
      else if (operand instanceof SQLParameter)
      {
        appendText(result, parameterIdentity((SQLParameter)operand));
      }
      else if (operand instanceof Expression)
      {
        appendText(result, operand.toString());
      }
      else if (operand instanceof IDBField)
      {
        IDBField field = (IDBField)operand;
        appendText(result, field.getTable().getName() + "." + field.getName());
      }
      else if (operand instanceof IDBTable)
      {
        appendText(result, ((IDBTable)operand).getName());
      }
      else if (operand instanceof SQLTableRef)
      {
        if (operand instanceof TableRef)
        {
          appendText(result, operand.toString());
        }
        else
        {
          SQLTableRef tableRef = (SQLTableRef)operand;
          appendText(result, tableRef.getTable().getName());

          if (tableRef.getAlias() != null)
          {
            appendText(result, " AS " + tableRef.getAlias());
          }
        }
      }
      else if (operand instanceof Operation)
      {
        appendOperation((Operation)operand, result, depth);
      }
      else if (operand instanceof Template)
      {
        appendText(result, operand.toString());
      }
      else if (operand instanceof SQLSubSelect)
      {
        if (operand instanceof SelectSubquery)
        {
          appendText(result, ((SelectSubquery)operand).getTemplate().toString());
        }
        else
        {
          appendIdentity(operand, result);
        }
      }
      else if (operand instanceof SQLFragment)
      {
        if (operand instanceof Fragment)
        {
          appendText(result, "FRAGMENT[parts=" + ((Fragment)operand).getParts().size() + "]");
        }
        else
        {
          appendIdentity(operand, result);
        }
      }
      else if (operand.getClass().isArray())
      {
        int length = Array.getLength(operand);
        appendText(result, "[");

        for (int i = 0; i < length && i < MAX_DIAGNOSTIC_ITEMS; i++)
        {
          if (i > 0)
          {
            appendText(result, ", ");
          }

          appendOperand(Array.get(operand, i), result, depth + 1);
        }

        if (length > MAX_DIAGNOSTIC_ITEMS)
        {
          appendText(result, ", ...");
        }

        appendText(result, "]");
      }
      else if (operand instanceof Collection<?>)
      {
        Collection<?> values = (Collection<?>)operand;
        appendText(result, "[");
        int index = 0;

        for (Object value : values)
        {
          if (index >= MAX_DIAGNOSTIC_ITEMS)
          {
            appendText(result, ", ...");
            break;
          }

          if (index++ > 0)
          {
            appendText(result, ", ");
          }

          appendOperand(value, result, depth + 1);
        }

        appendText(result, "]");
      }
      else if (operand instanceof String)
      {
        appendText(result, "String");
      }
      else if (operand instanceof Enum<?>)
      {
        appendText(result, ((Enum<?>)operand).name());
      }
      else
      {
        appendText(result, operand.getClass().getSimpleName());
      }
    }

    private static String parameterIdentity(SQLParameter parameter)
    {
      return "Parameter@" + Integer.toHexString(System.identityHashCode(parameter));
    }

    private static void appendIdentity(Object object, StringBuilder result)
    {
      appendText(result, object.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(object)));
    }

    private static void appendText(StringBuilder result, String text)
    {
      int remaining = MAX_DIAGNOSTIC_LENGTH - result.length();
      if (remaining > 0)
      {
        result.append(text, 0, Math.min(text.length(), remaining));
      }
    }

    private static final int MAX_DIAGNOSTIC_DEPTH = 8;

    private static final int MAX_DIAGNOSTIC_ITEMS = 8;

    private static final int MAX_DIAGNOSTIC_LENGTH = 512;

    /**
     * Operation kinds identify clauses and comparisons; their labels are distinct from both
     * template kinds and expression kinds.
     *
     * @author Eike Stepper
     */
    public enum Kind
    {
      PROJECTION("projection"), //
      DISTINCT("distinct"), //
      FROM("from"), //
      JOIN("JOIN"), //
      LEFT_JOIN("LEFT_JOIN"), //
      ON("on"), //
      WHERE("where"), //
      GROUP_BY("groupBy"), //
      HAVING("having"), //
      ORDER_BY("orderBy"), //
      ORDER_BY_DESC("orderByDesc"), //
      THEN_BY("thenBy"), //
      THEN_BY_DESC("thenByDesc"), //
      AND("and"), //
      OR("or"), //
      EQ("="), //
      NE("<>"), //
      LT("<"), //
      LE("<="), //
      GT(">"), //
      GE(">="), //
      LIKE("LIKE"), //
      BETWEEN("BETWEEN"), //
      IN("IN"), //
      IS_NULL("IS NULL"), //
      IS_NOT_NULL("IS NOT NULL"), //
      COLUMNS("columns"), //
      VALUES("values"), //
      INSERT_SELECT("insertSelect"), //
      SET("set");

      private final String description;

      private Kind(String description)
      {
        this.description = description;
      }

      public String getDescription()
      {
        return description;
      }

      public String getSqlOperator()
      {
        switch (this)
        {
        case EQ:
        case NE:
        case LT:
        case LE:
        case GT:
        case GE:
        case LIKE:
          return description;

        default:
          throw new IllegalStateException("Not a binary comparison operator: " + description);
        }
      }

      @Override
      public String toString()
      {
        return description;
      }
    }
  }

  /**
   * A SELECT-only handle retaining the exact immutable template snapshot used as a subquery.
   * Its private constructor ensures non-SELECT templates cannot implement {@link SQLSubSelect}.
   *
   * @author Eike Stepper
   */
  public static final class SelectSubquery implements SQLSubSelect
  {
    private final Template template;

    private SelectSubquery(Template template)
    {
      this.template = template;
    }

    public Template getTemplate()
    {
      return template;
    }
  }
}
