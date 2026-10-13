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
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLExpression;
import org.eclipse.net4j.db.dml.SQLFragment;
import org.eclipse.net4j.db.dml.SQLParameter;
import org.eclipse.net4j.db.dml.SQLParameterBinder;
import org.eclipse.net4j.db.dml.SQLSubSelect;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.db.dml.SQLTemplate;
import org.eclipse.net4j.db.dml.fluent.SQLDeleteStep;
import org.eclipse.net4j.db.dml.fluent.SQLDeleteWhereStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertAssignmentStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertSetStep;
import org.eclipse.net4j.db.dml.fluent.SQLInsertStartStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectFromStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectJoinStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectJoinedStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectOrderByStep;
import org.eclipse.net4j.db.dml.fluent.SQLSelectWhereStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateAssignmentStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateSetStep;
import org.eclipse.net4j.db.dml.fluent.SQLUpdateStartStep;
import org.eclipse.net4j.internal.db.dml.Definition;
import org.eclipse.net4j.internal.db.dml.Expression;
import org.eclipse.net4j.internal.db.dml.Fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * Verifies the public DML contracts and the adapter-independent structures produced by Phase 1.
 * These tests deliberately stop before SQL compilation and JDBC execution.
 *
 * @author Eike Stepper
 */
public class SQLDMLApiTest extends TestCase
{
  public void testExpressionCompositionAndParameterIdentity()
  {
    Schema schema = createSchema("dmlExpressionTest");
    SQLSubSelect subSelect = SQL.select(schema.id) //
        .from(schema.table) //
        .subSelect();
    SQLParameter reused = SQL.param();
    SQLParameter distinct = SQL.param();

    assertNotSame(reused, distinct);
    assertTrue(subSelect instanceof SQLTemplate);
    assertFalse(subSelect instanceof SQLExpression);

    Expression addition = (Expression)SQL.add(schema.id, SQL.literal(1));
    assertEquals("+", addition.getOperation());
    assertSame(schema.id, addition.getOperands().get(0));

    SQLCondition combined = SQL.eq(addition, SQL.literal(2)) //
        .and(SQL.isNull(schema.name).not());
    Expression combinedNode = (Expression)combined;
    assertEquals("AND", combinedNode.getOperation());
    assertEquals("=", ((Expression)combinedNode.getOperands().get(0)).getOperation());
    assertEquals("NOT", ((Expression)combinedNode.getOperands().get(1)).getOperation());

    Definition where = definition(SQL.selectAll() //
        .from(schema.table) //
        .where(schema.id).eq(reused) //
        .or(schema.name).eq(reused) //
        .build());
    List<Definition.Operation> operations = operations(where);
    assertEquals(Arrays.asList("projection", "from", "where", "=", "or", "="), names(operations));
    assertSame(reused, operations.get(3).getOperands()[1]);
    assertSame(reused, operations.get(5).getOperands()[1]);
  }

  public void testIndependentBuildersAndImmutableSnapshots()
  {
    Schema schema = createSchema("dmlSnapshotTest");
    SQLTemplate firstBuilder = SQL.select(schema.id).from(schema.table).build();
    SQLTemplate secondBuilder = SQL.select(schema.name).from(schema.table).build();
    Definition firstDefinition = definition(firstBuilder);
    Definition secondDefinition = definition(secondBuilder);

    assertSame(schema.id, operations(firstDefinition).get(0).getOperands()[0]);
    assertSame(schema.name, operations(secondDefinition).get(0).getOperands()[0]);

    SQLUpdateStartStep update = SQL.update(schema.table);
    SQLUpdateSetStep assignments = update.set(schema.id).to(SQL.literal(1));
    Definition before = definition(assignments.build());
    SQLUpdateAssignmentStep pending = assignments.set(schema.name);
    SQLTemplate afterTemplate = pending.to(SQL.literal("changed")).build();
    Definition after = definition(afterTemplate);

    assertEquals(Arrays.asList("set"), names(operations(before)));
    assertEquals(Arrays.asList("set", "set"), names(operations(after)));
    assertNotSame(before, after);

    try
    {
      before.getParts().clear();
      fail("A built template must expose an immutable operation list");
    }
    catch (UnsupportedOperationException expected)
    {
      // Expected: template snapshots cannot be edited through their public collections.
    }
  }

  public void testSelectDistinctAliasesWildcardsJoinAndSubselectOrder()
  {
    Schema schema = createSchema("dmlSelectTest");
    IDBTable otherTable = schema.schema.addTable("other_objects");
    IDBField otherID = otherTable.addField("id", DBType.INTEGER);
    IDBField label = otherTable.addField("label", DBType.VARCHAR);
    SQLTableRef objects = schema.table.as("o");
    SQLTableRef other = SQL.as(otherTable, "x");
    SQLSubSelect nested = SQL.select(otherID) //
        .from(otherTable) //
        .where(label).eq(SQL.param()) //
        .subSelect();

    SQLSelectFromStep select = SQL.select(SQL.all(objects), SQL.count(schema.id).as("total")) //
        .distinct() //
        .from(objects) //
        .from(otherTable);
    Definition definition = definition(select.leftJoin(other) //
        .on(objects.column(schema.id)).eq(other.column(otherID)) //
        .where(schema.id).in(nested) //
        .groupBy(schema.name) //
        .having(SQL.count()).gt(SQL.param()) //
        .orderBy(schema.name, SQL.desc(SQL.count(schema.id))) //
        .thenBy(SQL.asc(label)) //
        .build());

    List<Definition.Operation> operations = operations(definition);
    assertEquals(Arrays.asList("projection", "projection", "distinct", "from", "from", "LEFT_JOIN", "on", "=", "where", "IN", "groupBy", "having", ">",
        "orderBy", "orderBy", "thenBy"), names(operations));
    assertEquals("ALL", ((Expression)operations.get(0).getOperands()[0]).getOperation());
    assertSame(objects, ((Expression)operations.get(0).getOperands()[0]).getOperands().get(0));
    assertEquals("AS", ((Expression)operations.get(1).getOperands()[0]).getOperation());
    assertSame(nested, operations.get(9).getOperands()[1]);
    assertTrue(operations(definition((SQLTemplate)nested)).size() > 0);
  }

  public void testAllInsertFormsUpdateAndDelete()
  {
    Schema schema = createSchema("dmlWriteTest");

    Definition values = definition(SQL.insertInto(schema.table) //
        .values(SQL.param(), SQL.param()) //
        .build());
    assertEquals(Arrays.asList("values", "values"), names(operations(values)));

    Definition columnsAndValues = definition(SQL.insertInto(schema.table) //
        .columns(schema.id, schema.name) //
        .values(SQL.param(), SQL.param()) //
        .build());
    assertEquals(Arrays.asList("columns", "columns", "values", "values"), names(operations(columnsAndValues)));

    SQLInsertStartStep insert = SQL.insertInto(schema.table);
    SQLInsertSetStep assignments = insert.set(schema.id).to(SQL.param()) //
        .set(schema.name).to(SQL.param());
    Definition setInsert = definition(assignments.build());
    assertEquals(Arrays.asList("set", "set"), names(operations(setInsert)));

    Definition update = definition(SQL.update(schema.table) //
        .set(schema.name).to(SQL.add(schema.name, SQL.literal("!"))) //
        .where(schema.id).eq(SQL.param()) //
        .build());
    assertEquals(Arrays.asList("set", "where", "="), names(operations(update)));

    SQLDeleteStep delete = SQL.deleteFrom(schema.table);
    assertEquals(0, operations(definition(delete.build())).size());
    assertEquals(Arrays.asList("where", "="), names(operations(definition(delete.where(schema.id).eq(SQL.param()).build()))));
  }

  public void testFragmentsKeepTypedPartsSeparateFromTrustedText()
  {
    Schema schema = createSchema("dmlFragmentTest");
    SQLParameter parameter = SQL.param();
    SQLFragment fragment = SQL.fragment().sql("SELECT ") //
        .column(schema.id) //
        .sql(" FROM ") //
        .table(schema.table) //
        .sql(" WHERE ") //
        .column(schema.name) //
        .sql(" = ") //
        .param(parameter) //
        .build();
    Fragment internal = (Fragment)fragment;

    assertEquals(Arrays.asList("SELECT ", schema.id, " FROM ", schema.table, " WHERE ", schema.name, " = ", parameter), internal.getParts());
    assertSame(fragment, ((Expression)fragment.expression()).getOperands().get(0));
    assertSame(fragment, ((Expression)fragment.condition()).getOperands().get(0));
    assertSame(fragment, definition(SQL.build(fragment)).getParts().get(0));

    try
    {
      internal.getParts().clear();
      fail("A built fragment must expose an immutable part list");
    }
    catch (UnsupportedOperationException expected)
    {
      // Expected: fragment parts are immutable after build.
    }
  }

  public void testPublicStagesExposeOnlyValidCompletionOperations() throws Exception
  {
    assertMethodAbsent(SQLInsertAssignmentStep.class, "build");
    assertMethodAbsent(SQLInsertAssignmentStep.class, "compile");
    assertMethodAbsent(SQLUpdateAssignmentStep.class, "build");
    assertMethodAbsent(SQLUpdateAssignmentStep.class, "compile");
    assertMethodAbsent(SQLSelectJoinStep.class, "build");
    assertMethodAbsent(SQLSelectJoinStep.class, "compile");
    assertMethodAbsent(SQLSelectJoinedStep.class, "from", IDBTable.class, IDBTable[].class);
  }

  public void testBinderRemainsClientImplementable()
  {
    SQLParameterBinder<String> binder = (statement, index, value) -> assertEquals("bound", value);
    try
    {
      binder.bind(null, 1, "bound");
    }
    catch (SQLException ex)
    {
      throw new AssertionError(ex);
    }
  }

  public void testBuilderStageLifecycle()
  {
    Schema schema = createSchema("dmlLifecycleTest");
    IDBTable otherTable = schema.schema.addTable("other_objects");
    IDBField otherID = otherTable.addField("id", DBType.INTEGER);

    SQLSelectFromStep from = SQL.selectAll().from(schema.table);
    SQLSelectJoinStep join = from.leftJoin(otherTable);
    SQLSelectJoinedStep joined = join.on(schema.id).eq(otherID);
    assertIllegalState(() -> from.from(otherTable));
    assertIllegalState(() -> join.on(SQL.eq(schema.id, otherID)));
    assertEquals(Arrays.asList("projection", "from", "LEFT_JOIN", "on", "="), names(operations(definition(joined.build()))));

    SQLSelectWhereStep where = SQL.selectAll().from(schema.table).where(schema.id).eq(SQL.param());
    SQLSelectOrderByStep orderBy = where.orderBy(SQL.asc(schema.id));
    assertIllegalState(() -> where.and(SQL.isNull(schema.name)));
    Definition firstOrder = definition(orderBy.build());
    assertEquals(names(operations(firstOrder)), names(operations(definition(orderBy.build()))));
    orderBy.thenByDesc(schema.name).thenBy(SQL.asc(otherID));
    assertEquals(Arrays.asList("projection", "from", "where", "=", "orderBy"), names(operations(firstOrder)));
    Definition repeatedOrder = definition(orderBy.build());
    assertEquals(Arrays.asList("projection", "from", "where", "=", "orderBy", "thenByDesc", "thenBy"), names(operations(repeatedOrder)));
    assertEquals(names(operations(repeatedOrder)), names(operations(definition(orderBy.build()))));

    SQLUpdateStartStep update = SQL.update(schema.table);
    SQLUpdateAssignmentStep assignment = update.set(schema.id);
    assertIllegalState(() -> update.set(schema.name));
    SQLUpdateSetStep updated = assignment.to(SQL.literal(1));
    SQLUpdateAssignmentStep nextAssignment = updated.set(schema.name);
    assertIllegalState(() -> updated.set(schema.id));
    nextAssignment.to(SQL.literal("value")).build();

    SQLInsertStartStep insert = SQL.insertInto(schema.table);
    SQLInsertAssignmentStep insertAssignment = insert.set(schema.id);
    assertIllegalState(() -> insert.values(SQL.param()));
    insertAssignment.to(SQL.param()).set(schema.name).to(SQL.param()).build();

    SQLDeleteStep delete = SQL.deleteFrom(schema.table);
    SQLDeleteWhereStep deleteWhere = delete.where(SQL.eq(schema.id, SQL.param()));
    assertIllegalState(() -> delete.where(SQL.isNull(schema.name)));
    deleteWhere.and(SQL.isNull(schema.name)).or(SQL.ne(schema.id, SQL.literal(0))).build();
  }

  public void testRequiredOperandsRejectNullAndLiteralNullRemainsSupported()
  {
    Schema schema = createSchema("dmlNullValidationTest");

    assertNullPointer(() -> SQL.select(null));
    assertNullPointer(() -> SQL.as((SQLExpression)null, "result"));
    assertNullPointer(() -> SQL.add(schema.id, null));
    assertNullPointer(() -> SQL.eq(schema.id, null));
    assertNullPointer(() -> SQL.between(schema.id, SQL.literal(1), null));
    assertNullPointer(() -> SQL.exists(null));
    assertNullPointer(() -> SQL.fragment().column((IDBField)null));
    assertNullPointer(() -> SQL.selectAll().from(schema.table).where((SQLExpression)null));
    assertNullPointer(() -> SQL.update(schema.table).set(schema.id).to(null));
    assertNullPointer(() -> SQL.deleteFrom(schema.table).where((SQLCondition)null));
    assertNullPointer(() -> SQL.selectAll().from(schema.table).groupBy(schema.id).orderByDesc(null));
    assertNotNull(SQL.literal(null));
  }

  public void compileRepresentativeCallChains(IDBTable table, IDBTable otherTable, IDBField id, IDBField name, SQLSubSelect subSelect)
  {
    SQLTableRef first = table.as("t");
    SQLTableRef second = SQL.as(otherTable, "o");
    SQLSelectFromStep from = SQL.select(SQL.all(first), name, SQL.count(id).as("total")) //
        .distinct() //
        .from(first) //
        .from(otherTable);
    from.leftJoin(second) //
        .on(first.column(id)).eq(second.column(id)) //
        .where(id).in(subSelect) //
        .groupBy(name) //
        .having(SQL.count()).gt(SQL.param()) //
        .orderBy(name, SQL.desc(SQL.count(id))) //
        .thenBy(SQL.asc(id)) //
        .build();

    SQL.selectAll() //
        .from(table) //
        .where(SQL.eq(id, SQL.param()).or(SQL.isNull(name))) //
        .and(id).between(SQL.literal(1), SQL.literal(4)) //
        .build();

    SQLUpdateStartStep update = SQL.update(table);
    SQLUpdateSetStep updated = update.set(id).to(SQL.add(id, SQL.literal(1))) //
        .set(name).to(SQL.param());
    updated.where(id).eq(SQL.param()).build();

    SQLInsertStartStep insert = SQL.insertInto(table);
    insert.values(SQL.param(), SQL.param()).build();
    insert.columns(id, name).values(SQL.param(), SQL.param()).build();
    insert.set(id).to(SQL.param()).set(name).to(SQL.param()).build();

    SQL.deleteFrom(table).where(id).eq(SQL.param()).build();
    SQL.deleteFrom(table).build();
  }

  private static void assertMethodAbsent(Class<?> type, String name, Class<?>... parameterTypes) throws Exception
  {
    try
    {
      type.getMethod(name, parameterTypes);
      fail(type.getSimpleName() + " unexpectedly exposes " + name);
    }
    catch (NoSuchMethodException expected)
    {
      // The stage API excludes this operation by construction.
    }
  }

  private static void assertIllegalState(Runnable operation)
  {
    try
    {
      operation.run();
      fail("An obsolete fluent stage must reject mutation");
    }
    catch (IllegalStateException expected)
    {
      assertTrue(expected.getMessage().contains("no longer active"));
    }
  }

  private static void assertNullPointer(Runnable operation)
  {
    try
    {
      operation.run();
      fail("A required SQL operand must reject null");
    }
    catch (NullPointerException expected)
    {
      assertNotNull(expected.getMessage());
    }
  }

  private static Definition definition(SQLTemplate template)
  {
    assertTrue("The production builder must return its internal immutable definition", template instanceof Definition);
    return (Definition)template;
  }

  private static List<Definition.Operation> operations(Definition definition)
  {
    List<Definition.Operation> result = new ArrayList<>();

    for (Object part : definition.getParts())
    {
      assertTrue("Every statement part must retain its ordered operation structure", part instanceof Definition.Operation);
      result.add((Definition.Operation)part);
    }

    return result;
  }

  private static List<String> names(List<Definition.Operation> operations)
  {
    List<String> result = new ArrayList<>();

    for (Definition.Operation operation : operations)
    {
      result.add(operation.getName());
    }

    return result;
  }

  private static Schema createSchema(String name)
  {
    IDBSchema schema = DBUtil.createSchema(name);
    IDBTable table = schema.addTable("objects");
    IDBField id = table.addField("id", DBType.INTEGER);
    IDBField nameField = table.addField("name", DBType.VARCHAR);
    return new Schema(schema, table, id, nameField);
  }

  /**
   * Holds a schema and its columns for one test case.
   *
   * @author Eike Stepper
   */
  private static class Schema
  {
    private final IDBSchema schema;

    private final IDBTable table;

    private final IDBField id;

    private final IDBField name;

    public Schema(IDBSchema schema, IDBTable table, IDBField id, IDBField name)
    {
      this.schema = schema;
      this.table = table;
      this.id = id;
      this.name = name;
    }
  }
}
