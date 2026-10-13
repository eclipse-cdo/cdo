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
import org.eclipse.net4j.db.dml.SQLColumnRef;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLOrderItem;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.SQLTableRef;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Immutable structural representation of an expression node.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public class Expression implements SQLExpression
{
  private final String operation;

  private final List<Object> operands;

  public Expression(String operation, Object... operands)
  {
    this.operation = operation;
    this.operands = Collections.unmodifiableList(Arrays.asList(operands.clone()));
  }

  public String getOperation()
  {
    return operation;
  }

  public List<Object> getOperands()
  {
    return operands;
  }

  public static SQLExpression of(String operation, Object... operands)
  {
    return new Expression(operation, operands);
  }

  public static SQLCondition condition(String operation, Object... operands)
  {
    return new Condition(operation, operands);
  }

  public static SQLSelectItem all(SQLTableRef table)
  {
    return new Expression("ALL", table);
  }

  public static SQLSelectItem alias(SQLExpression expression, String alias)
  {
    return new Expression("AS", expression, alias);
  }

  public static SQLOrderItem order(String direction, SQLExpression expression)
  {
    return new SQLOrderItemImpl(direction, expression);
  }

  /**
   * An expression with boolean semantics.
   *
   * @author Eike Stepper
   */
  public static final class Condition extends Expression implements SQLCondition
  {
    public Condition(String operation, Object... operands)
    {
      super(operation, operands);
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
      super("COLUMN", column, tableRef);
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
}
