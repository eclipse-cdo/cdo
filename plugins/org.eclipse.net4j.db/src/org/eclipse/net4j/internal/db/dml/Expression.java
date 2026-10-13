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

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLColumnRef;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLOrderItem;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable expression node whose kind identifies its SQL meaning and whose operands retain its
 * structured children and literal values.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public class Expression implements SQLExpression
{
  /**
   * The semantic role of this expression, distinct from its operands and SQL rendering.
   */
  private final Kind kind;

  /**
   * Immutable structured children or literal values in their original order.
   */
  private final List<Object> operands;

  private static final int MAX_DIAGNOSTIC_DEPTH = 8;

  private static final int MAX_DIAGNOSTIC_ITEMS = 8;

  private static final int MAX_DIAGNOSTIC_LENGTH = 512;

  public Expression(Kind kind, Object... operands)
  {
    this.kind = Objects.requireNonNull(kind, "kind");
    Object[] operandCopy = Objects.requireNonNull(operands, "operands").clone();

    if (kind == Kind.LITERAL)
    {
      for (int i = 0; i < operandCopy.length; i++)
      {
        operandCopy[i] = copyTemporalLiteral(operandCopy[i]);
      }
    }

    this.operands = Collections.unmodifiableList(Arrays.asList(operandCopy));
  }

  public Kind getKind()
  {
    return kind;
  }

  public List<Object> getOperands()
  {
    if (kind == Kind.LITERAL)
    {
      List<Object> copy = new ArrayList<>(operands.size());

      for (Object operand : operands)
      {
        copy.add(copyTemporalLiteral(operand));
      }

      return Collections.unmodifiableList(copy);
    }

    return operands;
  }

  private static Object copyTemporalLiteral(Object value)
  {
    if (value instanceof Timestamp)
    {
      Timestamp timestamp = (Timestamp)value;
      Timestamp copy = new Timestamp(timestamp.getTime());
      copy.setNanos(timestamp.getNanos());
      return copy;
    }

    if (value instanceof Date)
    {
      return new Date(((Date)value).getTime());
    }

    if (value instanceof Time)
    {
      return new Time(((Time)value).getTime());
    }

    return value;
  }

  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    appendDiagnostic(this, result, 0);
    return result.toString();
  }

  private static void appendDiagnostic(Object value, StringBuilder result, int depth)
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

    if (value == null)
    {
      appendText(result, "null");
    }
    else if (value instanceof SQLParameter)
    {
      appendText(result, "Parameter@" + Integer.toHexString(System.identityHashCode(value)));
    }
    else if (value instanceof SQLColumnRef)
    {
      SQLColumnRef columnRef = (SQLColumnRef)value;
      IDBField column = columnRef.getColumn();
      SQLTableRef tableRef = columnRef.getTableRef();
      String tableName = tableRef == null ? getTableName(column) : tableRef.getAlias() == null ? tableRef.getTable().getName() : tableRef.getAlias();
      appendText(result, tableName == null ? column.getName() : tableName + "." + column.getName());
    }
    else if (value instanceof IDBField)
    {
      IDBField field = (IDBField)value;
      String tableName = field.getTable() == null ? null : field.getTable().getName();
      appendText(result, tableName == null ? field.getName() : tableName + "." + field.getName());
    }
    else if (value instanceof IDBTable)
    {
      appendText(result, ((IDBTable)value).getName());
    }
    else if (value instanceof SQLTableRef)
    {
      SQLTableRef tableRef = (SQLTableRef)value;
      appendDiagnostic(tableRef.getTable(), result, depth + 1);

      if (tableRef.getAlias() != null)
      {
        appendText(result, " AS " + tableRef.getAlias());
      }
    }
    else if (value instanceof Expression)
    {
      appendExpression((Expression)value, result, depth);
    }
    else if (value instanceof SQLSubSelect)
    {
      if (value instanceof Template.SelectSubquery)
      {
        appendText(result, ((Template.SelectSubquery)value).getTemplate().toString());
      }
      else
      {
        appendIdentity(value, result);
      }
    }
    else if (value instanceof SQLFragment)
    {
      if (value instanceof Fragment)
      {
        appendText(result, "FRAGMENT[parts=" + ((Fragment)value).getParts().size() + "]");
      }
      else
      {
        appendIdentity(value, result);
      }
    }
    else if (value instanceof Object[] || value.getClass().isArray())
    {
      int length = Array.getLength(value);
      appendText(result, "[");

      for (int i = 0; i < length && i < MAX_DIAGNOSTIC_ITEMS; i++)
      {
        if (i > 0)
        {
          appendText(result, ", ");
        }

        appendDiagnostic(Array.get(value, i), result, depth + 1);
      }

      if (length > MAX_DIAGNOSTIC_ITEMS)
      {
        appendText(result, ", ...");
      }

      appendText(result, "]");
    }
    else if (value instanceof Collection<?>)
    {
      Collection<?> values = (Collection<?>)value;
      appendText(result, "[");
      int index = 0;

      for (Object operand : values)
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

        appendDiagnostic(operand, result, depth + 1);
      }

      appendText(result, "]");
    }
    else if (value instanceof String)
    {
      appendText(result, "String");
    }
    else if (value instanceof Enum<?>)
    {
      appendText(result, ((Enum<?>)value).name());
    }
    else
    {
      appendText(result, value.getClass().getSimpleName());
    }
  }

  private static void appendExpression(Expression expression, StringBuilder result, int depth)
  {
    Kind kind = expression.kind;
    if (kind == Kind.LITERAL)
    {
      Object literal = expression.operands.isEmpty() ? null : expression.operands.get(0);
      appendText(result, "LITERAL[" + (expression.operands.isEmpty() ? "unknown" : literal == null ? "NULL" : literal.getClass().getSimpleName()) + "]");
      return;
    }

    if (kind == Kind.FRAGMENT)
    {
      Object fragment = expression.operands.isEmpty() ? null : expression.operands.get(0);
      if (fragment instanceof Fragment)
      {
        appendText(result, "FRAGMENT[parts=" + ((Fragment)fragment).getParts().size() + "]");
      }
      else
      {
        appendText(result, "FRAGMENT");
      }

      return;
    }

    if (kind == Kind.ALIAS)
    {
      appendText(result, "ALIAS[");

      if (!expression.operands.isEmpty())
      {
        appendDiagnostic(expression.operands.get(0), result, depth + 1);
      }

      if (expression.operands.size() > 1 && expression.operands.get(1) instanceof String)
      {
        appendText(result, " AS " + expression.operands.get(1));
      }

      appendText(result, "]");
      return;
    }

    appendText(result, kind.name());
    appendText(result, "[");

    for (int i = 0; i < expression.operands.size() && i < MAX_DIAGNOSTIC_ITEMS; i++)
    {
      if (i > 0)
      {
        appendText(result, ", ");
      }

      appendDiagnostic(expression.operands.get(i), result, depth + 1);
    }

    if (expression.operands.size() > MAX_DIAGNOSTIC_ITEMS)
    {
      appendText(result, ", ...");
    }

    appendText(result, "]");
  }

  private static void appendIdentity(Object value, StringBuilder result)
  {
    appendText(result, value.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(value)));
  }

  private static void appendText(StringBuilder result, String text)
  {
    int remaining = MAX_DIAGNOSTIC_LENGTH - result.length();
    if (remaining > 0)
    {
      result.append(text, 0, Math.min(text.length(), remaining));
    }
  }

  private static String getTableName(IDBField field)
  {
    IDBTable table = field.getTable();
    return table == null ? null : table.getName();
  }

  public static SQLExpression of(Kind kind, Object... operands)
  {
    return new Expression(kind, operands);
  }

  public static SQLCondition condition(Kind kind, Object... operands)
  {
    return new Condition(kind, operands);
  }

  public static SQLSelectItem all(SQLTableRef table)
  {
    return new Expression(Kind.ALL, table);
  }

  public static SQLSelectItem alias(SQLExpression expression, String alias)
  {
    return new Expression(Kind.ALIAS, expression, alias);
  }

  public static SQLOrderItem order(String direction, SQLExpression expression)
  {
    return new SQLOrderItemImpl(direction, expression);
  }

  /**
   * Enumerates expression meanings independently from template and clause operation kinds.
   * SQL operator spellings are retained as data only for kinds that render an operator token.
   *
   * @author Eike Stepper
   */
  public enum Kind
  {
    COLUMN("COLUMN"), //
    PARAMETER("PARAMETER"), //
    ALL("ALL"), //
    ALIAS("AS"), //
    LITERAL("LITERAL"), //
    SCALAR_SUBQUERY("SCALAR"), //
    EXISTS("EXISTS"), //
    NOT_EXISTS("NOT_EXISTS"), //
    COUNT_ALL("COUNT_ALL"), //
    COUNT("COUNT"), //
    COUNT_DISTINCT("COUNT_DISTINCT"), //
    MIN("MIN"), //
    MAX("MAX"), //
    SUM("SUM"), //
    AVG("AVG"), //
    ADD("+", "+"), //
    SUBTRACT("-", "-"), //
    MULTIPLY("*", "*"), //
    DIVIDE("/", "/"), //
    EQUALS("=", "="), //
    NOT_EQUALS("<>", "<>"), //
    LESS_THAN("<", "<"), //
    LESS_THAN_OR_EQUAL("<=", "<="), //
    GREATER_THAN(">", ">"), //
    GREATER_THAN_OR_EQUAL(">=", ">="), //
    LIKE("LIKE", "LIKE"), //
    BETWEEN("BETWEEN", "BETWEEN"), //
    IS_NULL("IS_NULL"), //
    IS_NOT_NULL("IS_NOT_NULL"), //
    IN_SUBQUERY("IN_SUB", "IN"), //
    AND("AND", "AND"), //
    OR("OR", "OR"), //
    NOT("NOT"), //
    FRAGMENT("FRAGMENT");

    private final String description;

    private final String sqlOperator;

    private Kind(String description)
    {
      this(description, null);
    }

    private Kind(String description, String sqlOperator)
    {
      this.description = description;
      this.sqlOperator = sqlOperator;
    }

    public String getDescription()
    {
      return description;
    }

    public String getSqlOperator()
    {
      if (sqlOperator == null)
      {
        throw new IllegalStateException("Expression kind is not an operator: " + description);
      }

      return sqlOperator;
    }

    @Override
    public String toString()
    {
      return description;
    }
  }

  /**
   * An expression with boolean semantics.
   *
   * @author Eike Stepper
   */
  public static final class Condition extends Expression implements SQLCondition
  {
    public Condition(Kind kind, Object... operands)
    {
      super(kind, operands);
    }
  }

  /**
   * A reference to a schema column, optionally qualified by a table occurrence.
   *
   * @author Eike Stepper
   */
  public static final class Column extends Expression implements SQLColumnRef
  {
    private final IDBField column;

    private final SQLTableRef tableRef;

    public Column(IDBField column, SQLTableRef tableRef)
    {
      super(Kind.COLUMN, column, tableRef);
      this.column = column;
      this.tableRef = tableRef;
    }

    @Override
    public IDBField getColumn()
    {
      return column;
    }

    @Override
    public SQLTableRef getTableRef()
    {
      return tableRef;
    }
  }

  /**
   * An ordering item with an explicit direction.
   *
   * @author Eike Stepper
   */
  public static final class SQLOrderItemImpl implements SQLOrderItem
  {
    private final String direction;

    private final SQLExpression expression;

    public SQLOrderItemImpl(String direction, SQLExpression expression)
    {
      this.direction = direction;
      this.expression = expression;
    }

    public String getDirection()
    {
      return direction;
    }

    public SQLExpression getExpression()
    {
      return expression;
    }
  }
}
