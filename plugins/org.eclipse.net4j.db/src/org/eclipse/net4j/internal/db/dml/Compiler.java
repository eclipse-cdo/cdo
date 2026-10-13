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

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLColumnRef;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Renders immutable DML definitions with adapter-specific identifier quoting and occurrence-order
 * parameter metadata.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class Compiler
{
  private Compiler()
  {
  }

  /**
   * Compiles one immutable structural definition for a specific database adapter.
   *
   * @param definition the ordered DML definition to compile
   * @param adapter the adapter used for identifier quoting
   * @return an immutable SQL statement with ordered parameter occurrence metadata
   */
  public static SQLStatement compile(Template definition, IDBAdapter adapter)
  {
    Objects.requireNonNull(definition, "definition");
    Renderer renderer = new Renderer(Objects.requireNonNull(adapter, "adapter"));
    return renderer.compile(definition);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Renderer
  {
    private final IDBAdapter adapter;

    private final StringBuilder sql = new StringBuilder();

    private final List<ParameterOccurrence> occurrences = new ArrayList<>();

    public Renderer(IDBAdapter adapter)
    {
      this.adapter = adapter;
    }

    public SQLStatement compile(Template definition)
    {
      switch (definition.getKind())
      {
      case SELECT:
        renderSelect(definition);
        break;

      case INSERT:
        renderInsert(definition);
        break;

      case UPDATE:
        renderUpdate(definition);
        break;

      case DELETE:
        renderDelete(definition);
        break;

      case FRAGMENT:
        renderFragment(definition.getFragment());
        break;

      default:
        throw invalid(definition.getKind().name(), "unknown definition kind");
      }

      return new CompiledStatement(sql.toString(), occurrences, adapter);
    }

    private void renderSelect(Template definition)
    {
      List<Template.Operation> operations = operations(definition);
      List<Object> projection = new ArrayList<>();
      List<Object> from = new ArrayList<>();
      List<Join> joins = new ArrayList<>();
      List<Template.Operation> trailing = new ArrayList<>();

      boolean distinct = false;
      boolean joinStarted = false;
      boolean whereSeen = false;
      boolean groupSeen = false;
      boolean havingSeen = false;
      boolean orderSeen = false;
      boolean thenSeen = false;

      int phase = 0;
      int index = 0;

      while (index < operations.size())
      {
        Template.Operation operation = operations.get(index);

        Template.Operation.Kind kind = operation.getKind();
        String name = kind.getDescription();

        if (kind == Template.Operation.Kind.PROJECTION)
        {
          if (phase != 0)
          {
            throw invalid("projection", "projection operations must precede FROM and later clauses");
          }

          projection.add(one(operation, "projection"));
          index++;
        }
        else if (kind == Template.Operation.Kind.DISTINCT)
        {
          if (phase != 0 || projection.isEmpty())
          {
            throw invalid("DISTINCT", "DISTINCT must follow the projection and precede FROM");
          }

          requireArity(operation, 0);
          distinct = true;
          index++;
        }
        else if (kind == Template.Operation.Kind.FROM)
        {
          if (phase > 1 || joinStarted)
          {
            throw invalid("FROM", "FROM sources must precede joins and later clauses");
          }

          phase = 1;
          from.add(one(operation, "FROM"));
          index++;
        }
        else if (isJoin(kind))
        {
          if (phase > 1 || from.isEmpty())
          {
            throw invalid(name, "JOIN requires preceding FROM sources and must precede later clauses");
          }

          phase = 1;
          joinStarted = true;
          Object table = one(operation, name);
          index++;

          if (index >= operations.size() || operations.get(index).getKind() != Template.Operation.Kind.ON)
          {
            throw invalid(name, "JOIN is missing its ON condition");
          }

          int conditionEnd = findConditionEnd(operations, index);
          joins.add(new Join(kind, table, new ArrayList<>(operations.subList(index, conditionEnd))));
          index = conditionEnd;
        }
        else
        {
          int operationPhase = selectPhase(kind);
          if (operationPhase == 0)
          {
            trailing.add(operation);
            index++;
            continue;
          }

          if (operationPhase < phase)
          {
            throw invalid(name, "clause appears out of SELECT order");
          }

          if (kind == Template.Operation.Kind.WHERE && whereSeen)
          {
            throw invalid(name, "WHERE may appear only once");
          }

          if (kind == Template.Operation.Kind.GROUP_BY && havingSeen)
          {
            throw invalid(name, "GROUP BY must precede HAVING");
          }

          if (kind == Template.Operation.Kind.HAVING && (!groupSeen || havingSeen))
          {
            throw invalid(name, "HAVING requires a preceding GROUP BY and may appear only once");
          }

          if (isThenBy(kind) && !orderSeen)
          {
            throw invalid(name, "thenBy requires a preceding ORDER BY");
          }

          if (isOrderBy(kind) && thenSeen)
          {
            throw invalid(name, "ORDER BY cannot follow thenBy");
          }

          whereSeen |= kind == Template.Operation.Kind.WHERE;
          groupSeen |= kind == Template.Operation.Kind.GROUP_BY;
          havingSeen |= kind == Template.Operation.Kind.HAVING;
          orderSeen |= isOrderBy(kind);
          thenSeen |= isThenBy(kind);
          phase = operationPhase;
          trailing.add(operation);

          index++;
        }
      }

      if (projection.isEmpty())
      {
        throw invalid("SELECT", "projection is missing");
      }

      sql.append("SELECT ");

      if (distinct)
      {
        sql.append("DISTINCT ");
      }

      renderCommaSeparated(projection, this::renderSelectItem);

      if (!from.isEmpty())
      {
        sql.append(" FROM ");

        if (joins.isEmpty())
        {
          renderCommaSeparated(from, this::renderTable);
        }
        else
        {
          if (from.size() == 1)
          {
            renderTable(from.get(0));
          }
          else
          {
            sql.append('(');

            for (int i = 0; i < from.size(); i++)
            {
              if (i > 0)
              {
                sql.append(" CROSS JOIN ");
              }

              renderTable(from.get(i));
            }

            sql.append(')');
          }

          for (Join join : joins)
          {
            sql.append(join.operation == Template.Operation.Kind.LEFT_JOIN ? " LEFT JOIN " : " INNER JOIN ");

            renderTable(join.table);
            ConditionResult condition = parseCondition(join.condition, 0, true);

            if (condition.nextIndex != join.condition.size())
            {
              throw invalid(join.operation.getDescription(), "unexpected operation after ON condition");
            }

            sql.append(" ON ").append(condition.sql);
          }
        }
      }
      else if (!joins.isEmpty())
      {
        throw invalid("JOIN", "a JOIN requires a FROM source");
      }

      renderTrailing(trailing);
    }

    private void renderTrailing(List<Template.Operation> operations)
    {
      int index = 0;

      while (index < operations.size())
      {
        Template.Operation operation = operations.get(index);

        Template.Operation.Kind kind = operation.getKind();
        String name = kind.getDescription();
        if (kind == Template.Operation.Kind.WHERE || kind == Template.Operation.Kind.HAVING)
        {
          ConditionResult condition = parseCondition(operations, index, false);
          sql.append(kind == Template.Operation.Kind.WHERE ? " WHERE " : " HAVING ").append(condition.sql);
          index = condition.nextIndex;
        }
        else if (kind == Template.Operation.Kind.GROUP_BY)
        {
          sql.append(hasGroupByBefore(operations, index) ? ", " : " GROUP BY ");
          renderCommaSeparated(operands(operation, "GROUP BY"), value -> renderExpression(value, 0, false));
          index++;
        }
        else if (kind == Template.Operation.Kind.ORDER_BY)
        {
          sql.append(index > 0 && hasOrderByBefore(operations, index) ? ", " : " ORDER BY ");
          renderOrderItem(one(operation, "ORDER BY"));
          index++;
        }
        else if (kind == Template.Operation.Kind.ORDER_BY_DESC)
        {
          sql.append(index > 0 && hasOrderByBefore(operations, index) ? ", " : " ORDER BY ");
          renderExpression(one(operation, "ORDER BY"), 0, false);
          sql.append(" DESC");
          index++;
        }
        else if (kind == Template.Operation.Kind.THEN_BY)
        {
          sql.append(", ");
          renderOrderItem(one(operation, "ORDER BY"));
          index++;
        }
        else if (kind == Template.Operation.Kind.THEN_BY_DESC)
        {
          sql.append(", ");
          renderExpression(one(operation, "ORDER BY"), 0, false);
          sql.append(" DESC");
          index++;
        }
        else
        {
          throw invalid(name, "operation is not valid in a SELECT clause");
        }
      }
    }

    private boolean hasOrderByBefore(List<Template.Operation> operations, int index)
    {
      for (int i = 0; i < index; i++)
      {
        Template.Operation.Kind kind = operations.get(i).getKind();
        if (isOrderBy(kind) || isThenBy(kind))
        {
          return true;
        }
      }

      return false;
    }

    private boolean hasGroupByBefore(List<Template.Operation> operations, int index)
    {
      for (int i = 0; i < index; i++)
      {
        if (operations.get(i).getKind() == Template.Operation.Kind.GROUP_BY)
        {
          return true;
        }
      }

      return false;
    }

    private ConditionResult parseCondition(List<Template.Operation> operations, int markerIndex, boolean joinCondition)
    {
      Template.Operation marker = operations.get(markerIndex);
      String markerName = marker.getKind().getDescription();

      List<String> terms = new ArrayList<>();
      List<String> connectors = new ArrayList<>();
      int index = markerIndex + 1;

      terms.add(renderConditionTerm(marker, operations, index, markerName));

      if (marker.getOperands().length == 0)
      {
        index++;
      }

      while (index < operations.size())
      {
        Template.Operation operation = operations.get(index);

        Template.Operation.Kind kind = operation.getKind();
        String name = kind.getDescription();
        if (isClauseStart(kind) || joinCondition && isJoin(kind))
        {
          break;
        }

        if (kind != Template.Operation.Kind.AND && kind != Template.Operation.Kind.OR)
        {
          throw invalid(markerName, "unexpected operation " + name + " in condition");
        }

        connectors.add(kind == Template.Operation.Kind.AND ? "AND" : "OR");
        terms.add(renderConditionTerm(operation, operations, index + 1, markerName));
        index += operation.getOperands().length == 0 ? 2 : 1;
      }

      StringBuilder result = new StringBuilder(terms.get(0));

      for (int i = 0; i < connectors.size(); i++)
      {
        result.append(' ').append(connectors.get(i)).append(' ').append(terms.get(i + 1));
      }

      return new ConditionResult(result.toString(), index);
    }

    private String renderConditionTerm(Template.Operation operation, List<Template.Operation> operations, int followingIndex, String clause)
    {
      Object[] operands = operation.getOperands();
      if (operands.length == 1 && operands[0] instanceof SQLCondition)
      {
        return renderCondition((SQLCondition)operands[0], 3);
      }

      if (operands.length == 0)
      {
        if (followingIndex >= operations.size())
        {
          throw invalid(clause, "comparison is missing");
        }

        Template.Operation comparison = operations.get(followingIndex);
        if (!isComparison(comparison.getKind()))
        {
          throw invalid(clause, "expected a comparison but found " + comparison.getKind().getDescription());
        }

        return renderRecordedComparison(comparison);
      }

      throw invalid(clause, "condition marker has invalid operands");
    }

    private String renderRecordedComparison(Template.Operation operation)
    {
      Object[] operands = operation.getOperands();
      Template.Operation.Kind kind = operation.getKind();
      int expected = kind == Template.Operation.Kind.BETWEEN //
          ? 3 //
          : kind == Template.Operation.Kind.IS_NULL || kind == Template.Operation.Kind.IS_NOT_NULL ? 1 : 2;
      requireArity(operation, expected);

      StringBuilder result = new StringBuilder();
      IDBField leftType = fieldOf(operands[0]);
      IDBField rightType = expected > 1 ? fieldOf(operands[1]) : null;
      renderExpression(operands[0], 4, false, result, leftType == null ? rightType : null);
      appendConditionOperator(result, kind, operands, 1);
      return result.toString();
    }

    private void appendConditionOperator(StringBuilder target, Template.Operation.Kind kind, Object[] operands, int firstRight)
    {
      switch (kind)
      {
      case IS_NULL:
        target.append(" IS NULL");
        break;

      case IS_NOT_NULL:
        target.append(" IS NOT NULL");
        break;

      case BETWEEN:
        target.append(" BETWEEN ");
        renderExpression(operands[firstRight], 4, false, target, fieldOf(operands[0]));
        target.append(" AND ");
        renderExpression(operands[firstRight + 1], 4, false, target, fieldOf(operands[0]));
        break;

      case IN:
        target.append(" IN ");
        renderSubqueryOperand(operands[firstRight], target);
        break;

      default:
        target.append(' ').append(kind.getSqlOperator()).append(' ');
        renderExpression(operands[firstRight], 4, false, target, fieldOf(operands[0]));
        break;
      }
    }

    private void appendConditionOperator(StringBuilder target, Expression.Kind kind, Object[] operands, int firstRight)
    {
      switch (kind)
      {
      case IS_NULL:
        target.append(" IS NULL");
        break;

      case IS_NOT_NULL:
        target.append(" IS NOT NULL");
        break;

      case BETWEEN:
        target.append(" BETWEEN ");
        renderExpression(operands[firstRight], 4, false, target, fieldOf(operands[0]));
        target.append(" AND ");
        renderExpression(operands[firstRight + 1], 4, false, target, fieldOf(operands[0]));
        break;

      case IN_SUBQUERY:
        target.append(" IN ");
        renderSubqueryOperand(operands[firstRight], target);
        break;

      default:
        target.append(' ').append(kind.getSqlOperator()).append(' ');
        renderExpression(operands[firstRight], 4, false, target, fieldOf(operands[0]));
        break;
      }
    }

    private IDBField fieldOf(Object value)
    {
      if (value instanceof IDBField)
      {
        return (IDBField)value;
      }

      if (value instanceof SQLColumnRef)
      {
        return ((SQLColumnRef)value).getColumn();
      }

      return null;
    }

    private void renderSubqueryOperand(Object operand, StringBuilder target)
    {
      if (!(operand instanceof SQLSubSelect))
      {
        throw invalid("IN", "expected a SELECT subquery");
      }

      target.append('(');
      renderSubquery((SQLSubSelect)operand, target, "IN subquery");
      target.append(')');
    }

    private void renderInsert(Template definition)
    {
      IDBTable table = definition.getTable();

      List<Template.Operation> operations = operations(definition);
      List<IDBField> columns = new ArrayList<>();
      List<Object> values = new ArrayList<>();

      boolean explicitColumns = false;
      boolean assignments = false;
      boolean valuesStarted = false;

      for (Template.Operation operation : operations)
      {
        switch (operation.getKind())
        {
        case COLUMNS:
          if (valuesStarted || assignments)
          {
            throw invalid("INSERT columns", "columns must be specified once before values");
          }

          explicitColumns = true;
          for (Object column : operands(operation, "INSERT columns"))
          {
            columns.add(field(column, table, "INSERT column"));
          }

          break;

        case VALUES:
          if (assignments)
          {
            throw invalid("INSERT values", "values cannot be mixed with assignments");
          }

          valuesStarted = true;
          values.addAll(operands(operation, "INSERT values"));
          break;

        case SET:
          if (valuesStarted || explicitColumns)
          {
            throw invalid("INSERT assignment", "assignment form cannot be mixed with columns or values");
          }

          assignments = true;
          Object[] assignment = operation.getOperands();
          requireArity(operation, 2);
          columns.add(field(assignment[0], table, "INSERT assignment target"));
          values.add(assignment[1]);
          break;

        default:
          throw invalid(operation.getKind().getDescription(), "operation is not valid in INSERT");
        }
      }

      if (values.isEmpty())
      {
        throw invalid("INSERT", "values are missing");
      }

      if (!explicitColumns && !assignments)
      {
        for (IDBField field : table.getFields())
        {
          columns.add(field);
        }

        if (values.size() != columns.size())
        {
          throw invalid("INSERT values", "value count " + values.size() + " does not match table column count " + columns.size());
        }
      }
      else if (columns.size() != values.size())
      {
        throw invalid("INSERT values", "value count " + values.size() + " does not match column count " + columns.size());
      }

      ensureUnique(columns, "INSERT columns");
      sql.append("INSERT INTO ");
      renderTable(table);

      if (explicitColumns || assignments)
      {
        sql.append(" (");
        renderCommaSeparated(columns, this::renderColumnName);
        sql.append(')');
      }

      sql.append(" VALUES (");

      for (int i = 0; i < values.size(); i++)
      {
        if (i > 0)
        {
          sql.append(", ");
        }

        renderExpression(values.get(i), 0, false, sql, columns.get(i));
      }

      sql.append(')');
    }

    private void renderUpdate(Template definition)
    {
      IDBTable table = definition.getTable();

      List<Template.Operation> operations = operations(definition);
      List<Assignment> assignments = new ArrayList<>();

      int whereIndex = -1;

      for (int i = 0; i < operations.size(); i++)
      {
        Template.Operation operation = operations.get(i);
        if (operation.getKind() == Template.Operation.Kind.SET)
        {
          Object[] values = operation.getOperands();
          requireArity(operation, 2);
          assignments.add(new Assignment(field(values[0], table, "UPDATE assignment target"), values[1]));
        }
        else if (operation.getKind() == Template.Operation.Kind.WHERE)
        {
          whereIndex = i;
          break;
        }
        else
        {
          throw invalid(operation.getKind().getDescription(), "operation is not valid in UPDATE");
        }
      }

      if (assignments.isEmpty())
      {
        throw invalid("UPDATE", "at least one assignment is required");
      }

      List<IDBField> columns = new ArrayList<>();

      for (Assignment assignment : assignments)
      {
        columns.add(assignment.column);
      }

      ensureUnique(columns, "UPDATE assignments");
      sql.append("UPDATE ");
      renderTable(table);
      sql.append(" SET ");

      for (int i = 0; i < assignments.size(); i++)
      {
        if (i > 0)
        {
          sql.append(", ");
        }

        Assignment assignment = assignments.get(i);
        renderColumnName(assignment.column);
        sql.append(" = ");
        renderExpression(assignment.value, 4, false, sql, assignment.column);
      }

      if (whereIndex >= 0)
      {
        ConditionResult condition = parseCondition(operations, whereIndex, false);
        if (condition.nextIndex != operations.size())
        {
          throw invalid("UPDATE", "unexpected operation after WHERE condition");
        }

        sql.append(" WHERE ").append(condition.sql);
      }
    }

    private void renderDelete(Template definition)
    {
      IDBTable table = definition.getTable();

      List<Template.Operation> operations = operations(definition);
      sql.append("DELETE FROM ");
      renderTable(table);

      if (!operations.isEmpty())
      {
        if (operations.size() == 0 || operations.get(0).getKind() != Template.Operation.Kind.WHERE)
        {
          throw invalid("DELETE", "only a WHERE condition is supported");
        }

        ConditionResult condition = parseCondition(operations, 0, false);
        if (condition.nextIndex != operations.size())
        {
          throw invalid("DELETE", "unexpected operation after WHERE condition");
        }

        sql.append(" WHERE ").append(condition.sql);
      }
    }

    private void renderSubquery(SQLSubSelect subSelect, StringBuilder target)
    {
      renderSubquery(subSelect, target, null);
    }

    private void renderSubquery(SQLSubSelect subSelect, StringBuilder target, String singleColumnContext)
    {
      if (!(subSelect instanceof Template.SelectSubquery))
      {
        throw invalid("subquery", "unsupported SQLSubSelect implementation " + subSelect.getClass().getName());
      }

      Template definition = ((Template.SelectSubquery)subSelect).getTemplate();
      if (singleColumnContext != null)
      {
        validateSingleColumnProjection(definition, singleColumnContext);
      }

      Renderer nested = new Renderer(adapter);
      SQLStatement statement = nested.compile(definition);
      target.append(statement.getSQL());
      occurrences.addAll(nested.occurrences);
    }

    private void validateSingleColumnProjection(Template definition, String context)
    {
      int width = projectionWidth(definition);
      if (width != 1)
      {
        String actualWidth = width < 0 ? "unknown" : String.valueOf(width);
        throw invalid(context, "expected exactly one projected column but found " + actualWidth);
      }
    }

    private int projectionWidth(Template definition)
    {
      List<Object> projections = new ArrayList<>();
      List<Object> sources = new ArrayList<>();

      for (Template.Operation operation : definition.getOperations())
      {
        switch (operation.getKind())
        {
        case PROJECTION:
          Collections.addAll(projections, operation.getOperands());
          break;

        case FROM:
        case JOIN:
        case LEFT_JOIN:
          Object[] operands = operation.getOperands();
          if (operands.length != 1)
          {
            return -1;
          }

          sources.add(operands[0]);
          break;

        default:
          break;
        }
      }

      if (projections.isEmpty())
      {
        return -1;
      }

      int width = 0;

      for (Object projection : projections)
      {
        int itemWidth = projectionItemWidth(projection, sources);
        if (itemWidth < 0 || width > Integer.MAX_VALUE - itemWidth)
        {
          return -1;
        }

        width += itemWidth;
      }

      return width;
    }

    private int projectionItemWidth(Object item, List<Object> sources)
    {
      if (item instanceof Expression)
      {
        Expression expression = (Expression)item;
        List<Object> operands = expression.getOperands();

        if (expression.getKind() == Expression.Kind.ALIAS)
        {
          return operands.isEmpty() ? -1 : projectionItemWidth(operands.get(0), sources);
        }

        if (expression.getKind() == Expression.Kind.ALL)
        {
          if (operands.size() != 1)
          {
            return -1;
          }

          Object tableRef = operands.get(0);
          if (tableRef != null)
          {
            return tableWidth(tableRef);
          }

          if (sources.isEmpty())
          {
            return -1;
          }

          int width = 0;

          for (Object source : sources)
          {
            int sourceWidth = tableWidth(source);
            if (sourceWidth < 0 || width > Integer.MAX_VALUE - sourceWidth)
            {
              return -1;
            }

            width += sourceWidth;
          }

          return width;
        }

        return 1;
      }

      return item instanceof SQLColumnRef || item instanceof IDBField ? 1 : -1;
    }

    private int tableWidth(Object tableReference)
    {
      IDBTable table;

      if (tableReference instanceof SQLTableRef)
      {
        table = ((SQLTableRef)tableReference).getTable();
      }
      else if (tableReference instanceof IDBTable)
      {
        table = (IDBTable)tableReference;
      }
      else
      {
        return -1;
      }

      if (table == null)
      {
        return -1;
      }

      int fieldCount = table.getFieldCount();
      return fieldCount < 0 ? -1 : fieldCount;
    }

    private void renderSelectItem(Object item)
    {
      if (!(item instanceof SQLSelectItem))
      {
        throw invalid("projection", "unsupported projection item " + typeName(item));
      }

      renderExpression(item, 0, false);
    }

    private void renderOrderItem(Object item)
    {
      if (item instanceof Expression.SQLOrderItemImpl)
      {
        Expression.SQLOrderItemImpl order = (Expression.SQLOrderItemImpl)item;
        if (!"ASC".equals(order.getDirection()) && !"DESC".equals(order.getDirection()))
        {
          throw invalid("ORDER BY", "unsupported direction " + order.getDirection());
        }

        renderExpression(order.getExpression(), 0, false);
        sql.append(' ').append(order.getDirection());
      }
      else if (item instanceof SQLExpression)
      {
        renderExpression(item, 0, false);
        sql.append(" ASC");
      }
      else
      {
        throw invalid("ORDER BY", "unsupported ordering item " + typeName(item));
      }
    }

    private void renderExpression(Object value, int parentPrecedence, boolean right)
    {
      renderExpression(value, parentPrecedence, right, sql);
    }

    private void renderExpression(Object value, int parentPrecedence, boolean right, StringBuilder target)
    {
      renderExpression(value, parentPrecedence, right, target, null);
    }

    private void renderExpression(Object value, int parentPrecedence, boolean right, StringBuilder target, IDBField inferredField)
    {
      if (value instanceof SQLParameter)
      {
        target.append('?');
        DBType inferredType = inferredField == null ? null : inferredField.getType();
        occurrences.add(new ParameterOccurrence((SQLParameter)value, inferredType));
        return;
      }

      if (value instanceof SQLColumnRef)
      {
        renderColumn((SQLColumnRef)value, target);
        return;
      }

      if (value instanceof IDBField)
      {
        renderColumnName(value, target);
        return;
      }

      if (value instanceof SQLFragment)
      {
        renderFragment((SQLFragment)value, target);
        return;
      }

      if (value instanceof SQLSubSelect)
      {
        target.append('(');
        renderSubquery((SQLSubSelect)value, target);
        target.append(')');
        return;
      }

      if (!(value instanceof Expression))
      {
        throw invalid("expression", "unsupported expression " + typeName(value));
      }

      Expression expression = (Expression)value;
      Expression.Kind operation = expression.getKind();
      String operationName = operation.getDescription();
      List<Object> operands = expression.getOperands();
      int precedence = precedence(operation);

      boolean parentheses = precedence < parentPrecedence || right && precedence == parentPrecedence && isArithmetic(operation);
      if (parentheses)
      {
        target.append('(');
      }

      switch (operation)
      {
      case COLUMN:
        if (operands.size() != 2 || !(operands.get(0) instanceof IDBField))
        {
          throw invalid("COLUMN", "expected a schema field and optional table reference");
        }

        SQLTableRef tableRef = (SQLTableRef)operands.get(1);
        renderColumn((IDBField)operands.get(0), tableRef, target);
        break;

      case ALL:
        if (operands.size() != 1)
        {
          throw invalid("ALL", "expected an optional table reference");
        }

        if (operands.get(0) != null)
        {
          if (!(operands.get(0) instanceof SQLTableRef))
          {
            throw invalid("ALL", "expected a table reference");
          }

          renderTableQualifier((SQLTableRef)operands.get(0), target);
          target.append('.');
        }

        target.append('*');
        break;

      case ALIAS:
        if (operands.size() != 2 || !(operands.get(1) instanceof String))
        {
          throw invalid("AS", "expected an expression and result-column alias");
        }

        renderExpression(operands.get(0), 0, false, target);
        target.append(" AS ").append(adapter.quoteIdentifier((String)operands.get(1)));
        break;

      case LITERAL:
        if (operands.size() != 1)
        {
          throw invalid("LITERAL", "expected one literal value");
        }

        renderLiteral(operands.get(0), target);
        break;

      case COUNT_ALL:
        requireExpressionArity(operation, operands, 0);
        target.append("COUNT(*)");
        break;

      case COUNT:
      case COUNT_DISTINCT:
      case MIN:
      case MAX:
      case SUM:
      case AVG:
        requireExpressionArity(operation, operands, 1);
        target.append(operation == Expression.Kind.COUNT_DISTINCT ? "COUNT(DISTINCT " : operationName + "(");
        renderExpression(operands.get(0), 0, false, target);
        target.append(')');
        break;

      case SCALAR_SUBQUERY:
        requireExpressionArity(operation, operands, 1);
        if (!(operands.get(0) instanceof SQLSubSelect))
        {
          throw invalid(operationName, "expected a SELECT subquery");
        }

        target.append('(');
        renderSubquery((SQLSubSelect)operands.get(0), target, "scalar subquery");
        target.append(')');
        break;

      case EXISTS:
      case NOT_EXISTS:
        requireExpressionArity(operation, operands, 1);
        if (!(operands.get(0) instanceof SQLSubSelect))
        {
          throw invalid(operationName, "expected a SELECT subquery");
        }

        target.append(operation == Expression.Kind.NOT_EXISTS ? "NOT EXISTS (" : "EXISTS (");
        renderSubquery((SQLSubSelect)operands.get(0), target);
        target.append(')');
        break;

      case FRAGMENT:
        requireExpressionArity(operation, operands, 1);
        if (!(operands.get(0) instanceof SQLFragment))
        {
          throw invalid(operationName, "expected a structured SQL fragment");
        }

        renderFragment((SQLFragment)operands.get(0), target);
        break;

      case ADD:
      case SUBTRACT:
      case MULTIPLY:
      case DIVIDE:
        requireExpressionArity(operation, operands, 2);
        renderExpression(operands.get(0), precedence, false, target);
        target.append(' ').append(operation.getSqlOperator()).append(' ');
        renderExpression(operands.get(1), precedence, true, target);
        break;

      case EQUALS:
      case NOT_EQUALS:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LIKE:
      case BETWEEN:
      case IN_SUBQUERY:
      case IS_NULL:
      case IS_NOT_NULL:
        renderConditionExpression(operation, operands, target);
        break;

      case AND:
      case OR:
        requireExpressionArity(operation, operands, 2);
        renderExpression(operands.get(0), precedence, false, target);
        target.append(operation == Expression.Kind.AND ? " AND " : " OR ");
        renderExpression(operands.get(1), precedence, true, target);
        break;

      case NOT:
        requireExpressionArity(operation, operands, 1);
        target.append("NOT ");
        renderExpression(operands.get(0), precedence, true, target);
        break;

      default:
        throw invalid(operationName, "unknown expression operation");
      }

      if (parentheses)
      {
        target.append(')');
      }
    }

    private void renderConditionExpression(Expression.Kind operation, List<Object> operands, StringBuilder target)
    {
      int arity = operation == Expression.Kind.BETWEEN ? 3 : operation == Expression.Kind.IS_NULL || operation == Expression.Kind.IS_NOT_NULL ? 1 : 2;
      requireExpressionArity(operation, operands, arity);

      Object[] values = operands.toArray();
      IDBField leftType = fieldOf(values[0]);
      IDBField rightType = arity > 1 ? fieldOf(values[1]) : null;
      renderExpression(values[0], 4, false, target, leftType == null ? rightType : null);
      appendConditionOperator(target, operation, values, 1);
    }

    private String renderCondition(SQLCondition condition, int parentPrecedence)
    {
      StringBuilder result = new StringBuilder();
      renderExpression(condition, parentPrecedence, false, result);
      return result.toString();
    }

    private void renderFragment(SQLFragment fragment)
    {
      renderFragment(fragment, sql);
    }

    private void renderFragment(SQLFragment fragment, StringBuilder target)
    {
      if (!(fragment instanceof Fragment))
      {
        throw invalid("fragment", "unsupported SQLFragment implementation " + fragment.getClass().getName());
      }

      for (Object part : ((Fragment)fragment).getParts())
      {
        if (part instanceof String)
        {
          String text = (String)part;
          if (text.indexOf('?') >= 0)
          {
            throw invalid("fragment",
                "raw SQL text must not contain '?'; represent parameter placeholders with SQL.param() and structured fragment parameter tokens");
          }

          target.append(text);
        }
        else if (part instanceof IDBTable || part instanceof SQLTableRef)
        {
          renderTable(part, target);
        }
        else if (part instanceof SQLParameter || part instanceof IDBField || part instanceof SQLExpression)
        {
          renderExpression(part, 0, false, target);
        }
        else if (part instanceof SQLFragment)
        {
          renderFragment((SQLFragment)part, target);
        }
        else
        {
          throw invalid("fragment", "unsupported structured part " + typeName(part));
        }
      }
    }

    private void renderLiteral(Object literal, StringBuilder target)
    {
      if (literal == null)
      {
        target.append("NULL");
      }
      else if (literal instanceof String || literal instanceof Character)
      {
        String text = String.valueOf(literal).replace("'", "''");
        target.append('\'').append(text).append('\'');
      }
      else if (literal instanceof Boolean)
      {
        target.append(((Boolean)literal).booleanValue() ? "TRUE" : "FALSE");
      }
      else if (literal instanceof Byte || literal instanceof Short || literal instanceof Integer || literal instanceof Long || literal instanceof BigInteger
          || literal instanceof BigDecimal)
      {
        target.append(literal.toString());
      }
      else if (literal instanceof Float || literal instanceof Double)
      {
        double number = ((Number)literal).doubleValue();
        if (!Double.isFinite(number))
        {
          throw invalid("LITERAL", "non-finite floating-point values are not portable SQL literals");
        }

        target.append(literal.toString());
      }
      else if (literal instanceof Date)
      {
        target.append("DATE '").append(literal).append('\'');
      }
      else if (literal instanceof Time)
      {
        target.append("TIME '").append(literal).append('\'');
      }
      else if (literal instanceof Timestamp)
      {
        target.append("TIMESTAMP '").append(literal.toString().replace("'", "''")).append('\'');
      }
      else
      {
        throw invalid("LITERAL", "unsupported literal type " + literal.getClass().getName());
      }
    }

    private void renderTable(Object value)
    {
      renderTable(value, sql);
    }

    private void renderTable(Object value, StringBuilder target)
    {
      if (value instanceof SQLTableRef)
      {
        SQLTableRef ref = (SQLTableRef)value;
        renderPhysicalTable(ref.getTable(), target);

        if (ref.getAlias() != null)
        {
          target.append(" AS ").append(adapter.quoteIdentifier(ref.getAlias()));
        }
      }
      else if (value instanceof IDBTable)
      {
        renderPhysicalTable((IDBTable)value, target);
      }
      else
      {
        throw invalid("table", "expected a schema table or table reference");
      }
    }

    private void renderPhysicalTable(IDBTable table, StringBuilder target)
    {
      Objects.requireNonNull(table, "table");

      IDBSchema schema = table.getSchema();
      if (schema != null && schema.isQualifiedTableNames())
      {
        target.append(adapter.quoteIdentifier(schema.getName())).append('.');
      }

      target.append(adapter.quoteIdentifier(table.getName()));
    }

    private void renderTableQualifier(SQLTableRef tableRef, StringBuilder target)
    {
      if (tableRef.getAlias() != null)
      {
        target.append(adapter.quoteIdentifier(tableRef.getAlias()));
      }
      else
      {
        renderPhysicalTable(tableRef.getTable(), target);
      }
    }

    private void renderColumn(SQLColumnRef column, StringBuilder target)
    {
      renderColumn(column.getColumn(), column.getTableRef(), target);
    }

    private void renderColumn(IDBField column, SQLTableRef tableRef, StringBuilder target)
    {
      if (tableRef != null)
      {
        if (column.getTable() != tableRef.getTable())
        {
          throw invalid("column", "column " + column.getName() + " does not belong to its table reference");
        }

        renderTableQualifier(tableRef, target);
        target.append('.');
      }

      renderColumnName(column, target);
    }

    private void renderColumnName(Object value)
    {
      renderColumnName(value, sql);
    }

    private void renderColumnName(Object value, StringBuilder target)
    {
      if (!(value instanceof IDBField))
      {
        throw invalid("column", "expected a schema field");
      }

      target.append(adapter.quoteIdentifier(((IDBField)value).getName()));
    }

    private void requireExpressionArity(Expression.Kind operation, List<Object> operands, int expected)
    {
      if (operands.size() != expected)
      {
        throw invalid(operation.getDescription(), "expected " + expected + " operands but found " + operands.size());
      }
    }

    private void renderCommaSeparated(List<?> values, StringRenderer render)
    {
      for (int i = 0; i < values.size(); i++)
      {
        if (i > 0)
        {
          sql.append(", ");
        }

        render.render(values.get(i));
      }
    }

    private static int selectPhase(Template.Operation.Kind kind)
    {
      if (kind == Template.Operation.Kind.WHERE)
      {
        return 2;
      }

      if (kind == Template.Operation.Kind.GROUP_BY)
      {
        return 3;
      }

      if (kind == Template.Operation.Kind.HAVING)
      {
        return 4;
      }

      if (isOrderBy(kind) || isThenBy(kind))
      {
        return 5;
      }

      return 0;
    }

    private static int findConditionEnd(List<Template.Operation> operations, int markerIndex)
    {
      int index = markerIndex + 1;

      if (operations.get(markerIndex).getOperands().length == 0)
      {
        index++;
      }

      while (index < operations.size())
      {
        Template.Operation.Kind kind = operations.get(index).getKind();
        String name = kind.getDescription();
        if (isClauseStart(kind) || isJoin(kind))
        {
          break;
        }

        if (kind == Template.Operation.Kind.AND || kind == Template.Operation.Kind.OR)
        {
          index += operations.get(index).getOperands().length == 0 ? 2 : 1;
        }
        else
        {
          throw invalid("JOIN ON", "unexpected operation " + name + " in condition");
        }
      }

      return index;
    }

    private static List<Template.Operation> operations(Template definition)
    {
      return definition.getOperations();
    }

    private static Object one(Template.Operation operation, String construct)
    {
      requireArity(operation, 1);
      return operation.getOperands()[0];
    }

    private static List<Object> operands(Template.Operation operation, String construct)
    {
      Object[] values = operation.getOperands();
      if (values.length == 0)
      {
        throw invalid(construct, "at least one operand is required");
      }

      List<Object> result = new ArrayList<>();
      Collections.addAll(result, values);
      return result;
    }

    private static void requireArity(Template.Operation operation, int expected)
    {
      if (operation.getOperands().length != expected)
      {
        throw invalid(operation.getKind().getDescription(), "expected " + expected + " operands but found " + operation.getOperands().length);
      }
    }

    private static IDBField field(Object value, IDBTable table, String construct)
    {
      if (!(value instanceof IDBField))
      {
        throw invalid(construct, "expected a schema field");
      }

      IDBField field = (IDBField)value;
      if (field.getTable() != table)
      {
        throw invalid(construct, "column " + field.getName() + " does not belong to target table " + table.getName());
      }

      return field;
    }

    private static void ensureUnique(List<IDBField> fields, String construct)
    {
      Set<IDBField> unique = Collections.newSetFromMap(new IdentityHashMap<IDBField, Boolean>());

      for (IDBField field : fields)
      {
        if (!unique.add(field))
        {
          throw invalid(construct, "duplicate column " + field.getName());
        }
      }
    }

    private static boolean isJoin(Template.Operation.Kind kind)
    {
      return kind == Template.Operation.Kind.JOIN || kind == Template.Operation.Kind.LEFT_JOIN;
    }

    private static boolean isClauseStart(Template.Operation.Kind kind)
    {
      return kind == Template.Operation.Kind.WHERE || kind == Template.Operation.Kind.GROUP_BY //
          || kind == Template.Operation.Kind.HAVING || isOrderBy(kind) || isThenBy(kind);
    }

    private static boolean isOrderBy(Template.Operation.Kind kind)
    {
      return kind == Template.Operation.Kind.ORDER_BY || kind == Template.Operation.Kind.ORDER_BY_DESC;
    }

    private static boolean isThenBy(Template.Operation.Kind kind)
    {
      return kind == Template.Operation.Kind.THEN_BY || kind == Template.Operation.Kind.THEN_BY_DESC;
    }

    private static boolean isComparison(Template.Operation.Kind kind)
    {
      switch (kind)
      {
      case EQ:
      case NE:
      case LT:
      case LE:
      case GT:
      case GE:
      case LIKE:
      case BETWEEN:
      case IN:
      case IS_NULL:
      case IS_NOT_NULL:
        return true;

      default:
        return false;
      }
    }

    private static boolean isComparison(Expression.Kind kind)
    {
      switch (kind)
      {
      case EQUALS:
      case NOT_EQUALS:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LIKE:
      case BETWEEN:
      case IN_SUBQUERY:
      case IS_NULL:
      case IS_NOT_NULL:
        return true;

      default:
        return false;
      }
    }

    private static boolean isArithmetic(Expression.Kind kind)
    {
      return kind == Expression.Kind.ADD || kind == Expression.Kind.SUBTRACT || kind == Expression.Kind.MULTIPLY || kind == Expression.Kind.DIVIDE;
    }

    private static int precedence(Expression.Kind kind)
    {
      if (kind == Expression.Kind.FRAGMENT)
      {
        return 0;
      }

      if (kind == Expression.Kind.OR)
      {
        return 1;
      }

      if (kind == Expression.Kind.AND)
      {
        return 2;
      }

      if (kind == Expression.Kind.NOT)
      {
        return 3;
      }

      if (isComparison(kind) || kind == Expression.Kind.EXISTS || kind == Expression.Kind.NOT_EXISTS)
      {
        return 4;
      }

      if (kind == Expression.Kind.ADD || kind == Expression.Kind.SUBTRACT)
      {
        return 5;
      }

      if (kind == Expression.Kind.MULTIPLY || kind == Expression.Kind.DIVIDE)
      {
        return 6;
      }

      return 10;
    }

    private static String typeName(Object value)
    {
      return value == null ? "null" : value.getClass().getName();
    }

    private static IllegalArgumentException invalid(String construct, String message)
    {
      return new IllegalArgumentException("Invalid SQL " + construct + ": " + message);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Join
  {
    private final Template.Operation.Kind operation;

    private final Object table;

    private final List<Template.Operation> condition;

    public Join(Template.Operation.Kind operation, Object table, List<Template.Operation> condition)
    {
      this.operation = operation;
      this.table = table;
      this.condition = condition;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ConditionResult
  {
    private final String sql;

    private final int nextIndex;

    public ConditionResult(String sql, int nextIndex)
    {
      this.sql = sql;
      this.nextIndex = nextIndex;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Assignment
  {
    private final IDBField column;

    private final Object value;

    public Assignment(IDBField column, Object value)
    {
      this.column = column;
      this.value = value;
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  private interface StringRenderer
  {
    public void render(Object value);
  }
}
