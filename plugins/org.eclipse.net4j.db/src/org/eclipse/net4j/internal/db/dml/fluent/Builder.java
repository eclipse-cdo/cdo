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
package org.eclipse.net4j.internal.db.dml.fluent;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLOrderItem;
import org.eclipse.net4j.db.dml.SQLSelectItem;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.fluent.SQLBuildStep;
import org.eclipse.net4j.db.dml.fluent.SQLComparisonStep;
import org.eclipse.net4j.db.dml.fluent.SQLDeleteStep;
import org.eclipse.net4j.db.dml.fluent.SQLDeleteWhereStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertAssignmentStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertColumnsStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertSetStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertStartStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertValuesStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectBuildStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectFromStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectGroupByStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectHavingStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectJoinStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectJoinedStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectOrderByStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectProjectionStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectSourceStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectWhereStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateAssignmentStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateSetStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateStartStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateWhereStep;
import org.eclipse.net4j.internal.db.dml.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Creates staged builders backed by a shared mutable construction state. Each build operation
 * snapshots that state into an immutable adapter-independent definition.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class Builder
{
  private Builder()
  {
  }

  public static SQLSelectProjectionStep select(boolean distinct, SQLSelectItem first, SQLSelectItem... others)
  {
    Objects.requireNonNull(first, "first");
    State state = new State(Template.Kind.SELECT, null);
    SelectProjection step = new SelectProjection(state);
    state.start(step);
    state.add(step, Template.Operation.Kind.PROJECTION, first);

    for (SQLSelectItem other : others)
    {
      state.add(step, Template.Operation.Kind.PROJECTION, Objects.requireNonNull(other, "projection item"));
    }

    if (distinct)
    {
      state.add(step, Template.Operation.Kind.DISTINCT);
    }

    return step;
  }

  public static SQLUpdateStartStep update(IDBTable table)
  {
    State state = new State(Template.Kind.UPDATE, Objects.requireNonNull(table, "table"));
    UpdateStart step = new UpdateStart(state);
    state.start(step);
    return step;
  }

  public static SQLInsertStartStep insert(IDBTable table)
  {
    State state = new State(Template.Kind.INSERT, Objects.requireNonNull(table, "table"));
    InsertStart step = new InsertStart(state);
    state.start(step);
    return step;
  }

  public static SQLDeleteStep delete(IDBTable table)
  {
    State state = new State(Template.Kind.DELETE, Objects.requireNonNull(table, "table"));
    Delete step = new Delete(state);
    state.start(step);
    return step;
  }

  private static void addAll(State state, Object stage, Template.Operation.Kind operation, Object first, Object[] others)
  {
    state.add(stage, operation, Objects.requireNonNull(first, "first"));

    for (Object other : others)
    {
      state.add(stage, operation, Objects.requireNonNull(other, operation + " item"));
    }
  }

  /**
   * Internal state for the State stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class State
  {
    private final Template.Kind kind;

    private final IDBTable table;

    private final List<Template.Operation> operations = new ArrayList<>();

    private Object activeStage;

    public State(Template.Kind kind, IDBTable table)
    {
      this.kind = kind;
      this.table = table;
    }

    public void start(Object stage)
    {
      if (activeStage != null)
      {
        throw new IllegalStateException("A builder stage is already active");
      }

      activeStage = stage;
    }

    public void check(Object stage)
    {
      if (activeStage != stage)
      {
        throw new IllegalStateException("This builder stage is no longer active");
      }
    }

    public void add(Object stage, Template.Operation.Kind operation, Object... operands)
    {
      check(stage);
      operations.add(new Template.Operation(operation, operands));
    }

    public <STAGE> STAGE advance(Object stage, STAGE next)
    {
      check(stage);
      activeStage = next;
      return next;
    }

    public Template build(Object stage)
    {
      check(stage);
      return new Template(kind, table, operations.toArray(new Template.Operation[operations.size()]));
    }
  }

  /**
   * Internal state for the BuildStage stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class BuildStage implements SQLBuildStep
  {
    protected final State state;

    public BuildStage(State state)
    {
      this.state = state;
    }

    @Override
    public Template build()
    {
      return state.build(this);
    }
  }

  /**
   * Internal state for the SelectBuildStage stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectBuildStage extends BuildStage implements SQLSelectBuildStep
  {
    public SelectBuildStage(State state)
    {
      super(state);
    }

    @Override
    public SQLSubSelect subSelect()
    {
      return state.build(this).asSubSelect();
    }
  }

  /**
   * Internal state for the SelectProjection stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectProjection extends BuildStage implements SQLSelectProjectionStep
  {
    public SelectProjection(State state)
    {
      super(state);
    }

    @Override
    public SQLSelectProjectionStep distinct()
    {
      state.add(this, Template.Operation.Kind.DISTINCT);
      return this;
    }

    @Override
    public SQLSelectFromStep from(IDBTable first, IDBTable... others)
    {
      addAll(state, this, Template.Operation.Kind.FROM, first, others);
      return state.advance(this, new SelectFrom(state));
    }

    @Override
    public SQLSelectFromStep from(SQLTableRef first, SQLTableRef... others)
    {
      addAll(state, this, Template.Operation.Kind.FROM, first, others);
      return state.advance(this, new SelectFrom(state));
    }
  }

  /**
   * Internal state for the SelectSource stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectSource extends SelectBuildStage implements SQLSelectSourceStep
  {
    public SelectSource(State state)
    {
      super(state);
    }

    @Override
    public SQLSelectJoinStep join(IDBTable table)
    {
      return joinTable(Template.Operation.Kind.JOIN, table);
    }

    @Override
    public SQLSelectJoinStep join(SQLTableRef table)
    {
      return joinTable(Template.Operation.Kind.JOIN, table);
    }

    @Override
    public SQLSelectJoinStep leftJoin(IDBTable table)
    {
      return joinTable(Template.Operation.Kind.LEFT_JOIN, table);
    }

    @Override
    public SQLSelectJoinStep leftJoin(SQLTableRef table)
    {
      return joinTable(Template.Operation.Kind.LEFT_JOIN, table);
    }

    private SQLSelectJoinStep joinTable(Template.Operation.Kind operation, Object table)
    {
      state.add(this, operation, Objects.requireNonNull(table, "table"));
      return state.advance(this, new SelectJoin(state));
    }

    @Override
    public SQLComparisonStep<SQLSelectWhereStep> where(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.WHERE);
      Comparison<SQLSelectWhereStep> comparison = new Comparison<>(state, left, () -> new SelectWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLSelectWhereStep where(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.WHERE, Objects.requireNonNull(condition, "condition"));
      return state.advance(this, new SelectWhere(state));
    }

    @Override
    public SQLSelectGroupByStep groupBy(SQLExpression first, SQLExpression... others)
    {
      addAll(state, this, Template.Operation.Kind.GROUP_BY, first, others);
      return state.advance(this, new SelectGroupBy(state));
    }

    @Override
    public SQLSelectOrderByStep orderBy(SQLOrderItem first, SQLOrderItem... others)
    {
      addAll(state, this, Template.Operation.Kind.ORDER_BY, first, others);
      return state.advance(this, new SelectOrderBy(state));
    }

    @Override
    public SQLSelectOrderByStep orderByDesc(SQLExpression expression)
    {
      state.add(this, Template.Operation.Kind.ORDER_BY_DESC, Objects.requireNonNull(expression, "expression"));
      return state.advance(this, new SelectOrderBy(state));
    }
  }

  /**
   * Internal state for the SelectFrom stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectFrom extends SelectSource implements SQLSelectFromStep
  {
    public SelectFrom(State state)
    {
      super(state);
    }

    @Override
    public SQLSelectFromStep from(IDBTable first, IDBTable... others)
    {
      addAll(state, this, Template.Operation.Kind.FROM, first, others);
      return this;
    }

    @Override
    public SQLSelectFromStep from(SQLTableRef first, SQLTableRef... others)
    {
      addAll(state, this, Template.Operation.Kind.FROM, first, others);
      return this;
    }
  }

  /**
   * Internal state for the SelectJoined stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectJoined extends SelectSource implements SQLSelectJoinedStep
  {
    public SelectJoined(State state)
    {
      super(state);
    }
  }

  /**
   * Internal state for the SelectJoin stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectJoin implements SQLSelectJoinStep
  {
    private final State state;

    public SelectJoin(State state)
    {
      this.state = state;
    }

    @Override
    public SQLComparisonStep<SQLSelectJoinedStep> on(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.ON);
      Comparison<SQLSelectJoinedStep> comparison = new Comparison<>(state, left, () -> new SelectJoined(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLSelectJoinedStep on(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.ON, Objects.requireNonNull(condition, "condition"));
      return state.advance(this, new SelectJoined(state));
    }
  }

  /**
   * Internal state for the SelectWhere stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectWhere extends SelectBuildStage implements SQLSelectWhereStep
  {
    public SelectWhere(State state)
    {
      super(state);
    }

    @Override
    public SQLComparisonStep<SQLSelectWhereStep> and(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.AND);
      Comparison<SQLSelectWhereStep> comparison = new Comparison<>(state, left, () -> new SelectWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLComparisonStep<SQLSelectWhereStep> or(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.OR);
      Comparison<SQLSelectWhereStep> comparison = new Comparison<>(state, left, () -> new SelectWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLSelectWhereStep and(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.AND, Objects.requireNonNull(condition, "condition"));
      return this;
    }

    @Override
    public SQLSelectWhereStep or(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.OR, Objects.requireNonNull(condition, "condition"));
      return this;
    }

    @Override
    public SQLSelectGroupByStep groupBy(SQLExpression first, SQLExpression... others)
    {
      addAll(state, this, Template.Operation.Kind.GROUP_BY, first, others);
      return state.advance(this, new SelectGroupBy(state));
    }

    @Override
    public SQLSelectOrderByStep orderBy(SQLOrderItem first, SQLOrderItem... others)
    {
      addAll(state, this, Template.Operation.Kind.ORDER_BY, first, others);
      return state.advance(this, new SelectOrderBy(state));
    }

    @Override
    public SQLSelectOrderByStep orderByDesc(SQLExpression expression)
    {
      state.add(this, Template.Operation.Kind.ORDER_BY_DESC, Objects.requireNonNull(expression, "expression"));
      return state.advance(this, new SelectOrderBy(state));
    }
  }

  /**
   * Internal state for the SelectGroupBy stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectGroupBy extends SelectBuildStage implements SQLSelectGroupByStep
  {
    public SelectGroupBy(State state)
    {
      super(state);
    }

    @Override
    public SQLComparisonStep<SQLSelectHavingStep> having(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.HAVING);
      Comparison<SQLSelectHavingStep> comparison = new Comparison<>(state, left, () -> new SelectHaving(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLSelectHavingStep having(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.HAVING, Objects.requireNonNull(condition, "condition"));
      return state.advance(this, new SelectHaving(state));
    }

    @Override
    public SQLSelectOrderByStep orderBy(SQLOrderItem first, SQLOrderItem... others)
    {
      addAll(state, this, Template.Operation.Kind.ORDER_BY, first, others);
      return state.advance(this, new SelectOrderBy(state));
    }

    @Override
    public SQLSelectOrderByStep orderByDesc(SQLExpression expression)
    {
      state.add(this, Template.Operation.Kind.ORDER_BY_DESC, Objects.requireNonNull(expression, "expression"));
      return state.advance(this, new SelectOrderBy(state));
    }
  }

  /**
   * Internal state for the SelectHaving stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectHaving extends SelectBuildStage implements SQLSelectHavingStep
  {
    public SelectHaving(State state)
    {
      super(state);
    }

    @Override
    public SQLComparisonStep<SQLSelectHavingStep> and(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.AND);
      Comparison<SQLSelectHavingStep> comparison = new Comparison<>(state, left, () -> new SelectHaving(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLComparisonStep<SQLSelectHavingStep> or(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.OR);
      Comparison<SQLSelectHavingStep> comparison = new Comparison<>(state, left, () -> new SelectHaving(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLSelectHavingStep and(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.AND, Objects.requireNonNull(condition, "condition"));
      return this;
    }

    @Override
    public SQLSelectHavingStep or(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.OR, Objects.requireNonNull(condition, "condition"));
      return this;
    }

    @Override
    public SQLSelectOrderByStep orderBy(SQLOrderItem first, SQLOrderItem... others)
    {
      addAll(state, this, Template.Operation.Kind.ORDER_BY, first, others);
      return state.advance(this, new SelectOrderBy(state));
    }

    @Override
    public SQLSelectOrderByStep orderByDesc(SQLExpression expression)
    {
      state.add(this, Template.Operation.Kind.ORDER_BY_DESC, Objects.requireNonNull(expression, "expression"));
      return state.advance(this, new SelectOrderBy(state));
    }
  }

  /**
   * Internal state for the SelectOrderBy stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class SelectOrderBy extends SelectBuildStage implements SQLSelectOrderByStep
  {
    public SelectOrderBy(State state)
    {
      super(state);
    }

    @Override
    public SQLSelectOrderByStep thenBy(SQLOrderItem item)
    {
      state.add(this, Template.Operation.Kind.THEN_BY, Objects.requireNonNull(item, "item"));
      return this;
    }

    @Override
    public SQLSelectOrderByStep thenByDesc(SQLExpression expression)
    {
      state.add(this, Template.Operation.Kind.THEN_BY_DESC, Objects.requireNonNull(expression, "expression"));
      return this;
    }
  }

  /**
   * Internal state for the Comparison stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class Comparison<NEXT> implements SQLComparisonStep<NEXT>
  {
    private final State state;

    private final SQLExpression left;

    private final Supplier<NEXT> nextFactory;

    public Comparison(State state, SQLExpression left, Supplier<NEXT> nextFactory)
    {
      this.state = state;
      this.left = left;
      this.nextFactory = nextFactory;
    }

    private NEXT compare(Template.Operation.Kind operator, Object... operands)
    {
      for (Object operand : operands)
      {
        Objects.requireNonNull(operand, "comparison operand");
      }

      Object[] values = new Object[operands.length + 1];
      values[0] = left;
      System.arraycopy(operands, 0, values, 1, operands.length);
      state.add(this, operator, values);
      return state.advance(this, nextFactory.get());
    }

    @Override
    public NEXT eq(SQLExpression right)
    {
      return compare(Template.Operation.Kind.EQ, right);
    }

    @Override
    public NEXT ne(SQLExpression right)
    {
      return compare(Template.Operation.Kind.NE, right);
    }

    @Override
    public NEXT lt(SQLExpression right)
    {
      return compare(Template.Operation.Kind.LT, right);
    }

    @Override
    public NEXT le(SQLExpression right)
    {
      return compare(Template.Operation.Kind.LE, right);
    }

    @Override
    public NEXT gt(SQLExpression right)
    {
      return compare(Template.Operation.Kind.GT, right);
    }

    @Override
    public NEXT ge(SQLExpression right)
    {
      return compare(Template.Operation.Kind.GE, right);
    }

    @Override
    public NEXT like(SQLExpression right)
    {
      return compare(Template.Operation.Kind.LIKE, right);
    }

    @Override
    public NEXT between(SQLExpression lower, SQLExpression upper)
    {
      return compare(Template.Operation.Kind.BETWEEN, lower, upper);
    }

    @Override
    public NEXT in(SQLSubSelect query)
    {
      return compare(Template.Operation.Kind.IN, query);
    }

    @Override
    public NEXT isNull()
    {
      return compare(Template.Operation.Kind.IS_NULL);
    }

    @Override
    public NEXT isNotNull()
    {
      return compare(Template.Operation.Kind.IS_NOT_NULL);
    }
  }

  /**
   * Internal state for the UpdateStart stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class UpdateStart implements SQLUpdateStartStep
  {
    private final State state;

    public UpdateStart(State state)
    {
      this.state = state;
    }

    @Override
    public SQLUpdateAssignmentStep set(IDBField column)
    {
      state.check(this);
      return state.advance(this, new UpdateAssignment(state, column));
    }
  }

  /**
   * Internal state for the UpdateAssignment stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class UpdateAssignment implements SQLUpdateAssignmentStep
  {
    private final State state;

    private final IDBField column;

    public UpdateAssignment(State state, IDBField column)
    {
      this.state = state;
      this.column = Objects.requireNonNull(column, "column");
    }

    @Override
    public SQLUpdateSetStep to(SQLExpression value)
    {
      state.add(this, Template.Operation.Kind.SET, column, Objects.requireNonNull(value, "value"));
      return state.advance(this, new UpdateSet(state));
    }
  }

  /**
   * Internal state for the UpdateSet stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class UpdateSet extends BuildStage implements SQLUpdateSetStep
  {
    public UpdateSet(State state)
    {
      super(state);
    }

    @Override
    public SQLUpdateAssignmentStep set(IDBField column)
    {
      state.check(this);
      return state.advance(this, new UpdateAssignment(state, column));
    }

    @Override
    public SQLComparisonStep<SQLUpdateWhereStep> where(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.WHERE);
      Comparison<SQLUpdateWhereStep> comparison = new Comparison<>(state, left, () -> new UpdateWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLUpdateWhereStep where(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.WHERE, Objects.requireNonNull(condition, "condition"));
      return state.advance(this, new UpdateWhere(state));
    }
  }

  /**
   * Internal state for the UpdateWhere stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class UpdateWhere extends BuildStage implements SQLUpdateWhereStep
  {
    public UpdateWhere(State state)
    {
      super(state);
    }

    @Override
    public SQLComparisonStep<SQLUpdateWhereStep> and(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.AND);
      Comparison<SQLUpdateWhereStep> comparison = new Comparison<>(state, left, () -> new UpdateWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLComparisonStep<SQLUpdateWhereStep> or(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.OR);
      Comparison<SQLUpdateWhereStep> comparison = new Comparison<>(state, left, () -> new UpdateWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLUpdateWhereStep and(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.AND, Objects.requireNonNull(condition, "condition"));
      return this;
    }

    @Override
    public SQLUpdateWhereStep or(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.OR, Objects.requireNonNull(condition, "condition"));
      return this;
    }
  }

  /**
   * Internal state for the InsertStart stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class InsertStart implements SQLInsertStartStep
  {
    private final State state;

    public InsertStart(State state)
    {
      this.state = state;
    }

    @Override
    public SQLInsertColumnsStep columns(IDBField first, IDBField... others)
    {
      addAll(state, this, Template.Operation.Kind.COLUMNS, first, others);
      return state.advance(this, new InsertColumns(state));
    }

    @Override
    public SQLInsertValuesStep values(SQLExpression first, SQLExpression... others)
    {
      addAll(state, this, Template.Operation.Kind.VALUES, first, others);
      return state.advance(this, new InsertValues(state));
    }

    @Override
    public SQLInsertAssignmentStep set(IDBField column)
    {
      state.check(this);
      return state.advance(this, new InsertAssignment(state, column));
    }
  }

  /**
   * Internal state for the InsertColumns stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class InsertColumns implements SQLInsertColumnsStep
  {
    private final State state;

    public InsertColumns(State state)
    {
      this.state = state;
    }

    @Override
    public SQLInsertValuesStep values(SQLExpression first, SQLExpression... others)
    {
      addAll(state, this, Template.Operation.Kind.VALUES, first, others);
      return state.advance(this, new InsertValues(state));
    }

    @Override
    public SQLBuildStep select(SQLSubSelect source)
    {
      state.add(this, Template.Operation.Kind.INSERT_SELECT, Objects.requireNonNull(source, "source"));
      return state.advance(this, new BuildStage(state));
    }
  }

  /**
   * Internal state for the InsertAssignment stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class InsertAssignment implements SQLInsertAssignmentStep
  {
    private final State state;

    private final IDBField column;

    public InsertAssignment(State state, IDBField column)
    {
      this.state = state;
      this.column = Objects.requireNonNull(column, "column");
    }

    @Override
    public SQLInsertSetStep to(SQLExpression value)
    {
      state.add(this, Template.Operation.Kind.SET, column, Objects.requireNonNull(value, "value"));
      return state.advance(this, new InsertSet(state));
    }
  }

  /**
   * Internal state for the InsertSet stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class InsertSet extends BuildStage implements SQLInsertSetStep
  {
    public InsertSet(State state)
    {
      super(state);
    }

    @Override
    public SQLInsertAssignmentStep set(IDBField column)
    {
      state.check(this);
      return state.advance(this, new InsertAssignment(state, column));
    }
  }

  /**
   * Internal state for the InsertValues stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class InsertValues extends BuildStage implements SQLInsertValuesStep
  {
    public InsertValues(State state)
    {
      super(state);
    }
  }

  /**
   * Internal state for the Delete stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class Delete extends BuildStage implements SQLDeleteStep
  {
    public Delete(State state)
    {
      super(state);
    }

    @Override
    public SQLComparisonStep<SQLDeleteWhereStep> where(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.WHERE);
      Comparison<SQLDeleteWhereStep> comparison = new Comparison<>(state, left, () -> new DeleteWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLDeleteWhereStep where(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.WHERE, Objects.requireNonNull(condition, "condition"));
      return state.advance(this, new DeleteWhere(state));
    }
  }

  /**
   * Internal state for the DeleteWhere stage.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  private static class DeleteWhere extends BuildStage implements SQLDeleteWhereStep
  {
    public DeleteWhere(State state)
    {
      super(state);
    }

    @Override
    public SQLComparisonStep<SQLDeleteWhereStep> and(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.AND);
      Comparison<SQLDeleteWhereStep> comparison = new Comparison<>(state, left, () -> new DeleteWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLComparisonStep<SQLDeleteWhereStep> or(SQLExpression left)
    {
      Objects.requireNonNull(left, "left");
      state.add(this, Template.Operation.Kind.OR);
      Comparison<SQLDeleteWhereStep> comparison = new Comparison<>(state, left, () -> new DeleteWhere(state));
      return state.advance(this, comparison);
    }

    @Override
    public SQLDeleteWhereStep and(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.AND, Objects.requireNonNull(condition, "condition"));
      return this;
    }

    @Override
    public SQLDeleteWhereStep or(SQLCondition condition)
    {
      state.add(this, Template.Operation.Kind.OR, Objects.requireNonNull(condition, "condition"));
      return this;
    }
  }
}
