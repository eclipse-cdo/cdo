/*
 * Copyright (c) 2008-2013, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractDBTest extends AbstractOMTest
{
  public static final String SCHEMA_NAME = "TEST";

  private IDBAdapter adapter;

  private IDBConnectionProvider connectionProvider;

  private Connection internalConnection;

  @Override
  protected void doSetUp() throws Exception
  {
    adapter = createAdapter();

    DataSource dataSource = createDataSource();
    connectionProvider = adapter.createConnectionProvider(dataSource);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    if (internalConnection != null)
    {
      DBUtil.dropAllTables(internalConnection, null);
      DBUtil.close(internalConnection);
      internalConnection = null;
    }

    connectionProvider = null;
    adapter = null;
  }

  protected abstract IDBAdapter createAdapter();

  protected abstract DataSource createDataSource();

  private Connection getConnection()
  {
    if (internalConnection == null)
    {
      internalConnection = connectionProvider.getConnection();
    }

    return internalConnection;
  }

  public void _testDBTypes() throws Exception
  {
    IDBSchema schema = DBUtil.createSchema("testDBTypes");
    DBType[] dbTypes = DBType.values();

    int count = 0;
    int i = 0;
    for (DBType dbType : dbTypes)
    {
      IDBTable table = schema.addTable("table_" + i);
      table.addField("field", dbType);
      ++count;

      if (adapter.isTypeIndexable(dbType))
      {
        IDBTable idx_table = schema.addTable("idx_table" + i);
        IDBField idx_field = idx_table.addField("field", dbType);
        idx_table.addIndex(IDBIndex.Type.NON_UNIQUE, idx_field);
        ++count;

        IDBTable uni_table = schema.addTable("uni_table" + i);
        IDBField uni_field = uni_table.addField("field", dbType);
        uni_table.addIndex(IDBIndex.Type.UNIQUE, uni_field);
        ++count;

        IDBTable pk_table = schema.addTable("pk_table" + i);
        IDBField pk_field = pk_table.addField("field", dbType);
        pk_table.addIndex(IDBIndex.Type.PRIMARY_KEY, pk_field);
        ++count;
      }

      ++i;
    }

    Set<IDBTable> tables = schema.create(adapter, getConnection());
    assertEquals(count, tables.size());
  }

  public void testFindElement() throws Exception
  {
    IDBSchema schema1 = DBUtil.createSchema(SCHEMA_NAME);
    IDBTable table1 = schema1.addTable("table");
    IDBField field11 = table1.addField("field1", DBType.INTEGER, true);
    IDBField field12 = table1.addField("field2", DBType.VARCHAR, 64, true);
    IDBField field13 = table1.addField("field3", DBType.BOOLEAN);
    IDBIndex index11 = table1.addIndex("index1", IDBIndex.Type.PRIMARY_KEY, field11, field12);
    IDBIndex index12 = table1.addIndex("index2", IDBIndex.Type.UNIQUE, field11, field12);
    IDBIndex index13 = table1.addIndex("index3", IDBIndex.Type.NON_UNIQUE, field12);

    IDBSchema schema2 = DBUtil.createSchema(SCHEMA_NAME);
    IDBTable table2 = schema2.addTable("table");
    IDBField field21 = table2.addField("field1", DBType.INTEGER, true);
    IDBField field22 = table2.addField("field2", DBType.VARCHAR, 64, true);
    IDBField field23 = table2.addField("field3", DBType.BOOLEAN);
    IDBIndex index21 = table2.addIndex("index1", IDBIndex.Type.PRIMARY_KEY, field21, field22);
    IDBIndex index22 = table2.addIndex("index2", IDBIndex.Type.UNIQUE, field21, field22);
    IDBIndex index23 = table2.addIndex("index3", IDBIndex.Type.NON_UNIQUE, field22);

    assertSame(schema2, schema2.findElement(schema1));
    assertSame(table2, schema2.findElement(table1));
    assertSame(field21, schema2.findElement(field11));
    assertSame(field22, schema2.findElement(field12));
    assertSame(field23, schema2.findElement(field13));
    assertSame(index21, schema2.findElement(index11));
    assertSame(index22, schema2.findElement(index12));
    assertSame(index23, schema2.findElement(index13));
  }

  public void testReadSchema() throws Exception
  {
    IDBSchema schema = DBUtil.readSchema(adapter, getConnection(), SCHEMA_NAME);
    assertEquals(true, schema.isEmpty());
  }

  public void testCreateSchema() throws Exception
  {
    IDBDatabase database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    IDBSchema schema = database.getSchema();
    assertEquals(true, schema.isLocked());
    assertEquals(true, schema.isEmpty());

    IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    IDBSchema workingCopy = schemaTransaction.getWorkingCopy();
    assertEquals(false, workingCopy.isLocked());
    assertEquals(true, workingCopy.isEmpty());
    assertEquals(schema, workingCopy);

    IDBTable table1 = workingCopy.addTable("table1");
    IDBField field11 = table1.addField("field11", DBType.INTEGER, true);
    IDBField field12 = table1.addField("field12", DBType.VARCHAR, 64, true);
    IDBField field13 = table1.addField("field13", DBType.BOOLEAN);
    IDBIndex index11 = table1.addIndex("index11", IDBIndex.Type.PRIMARY_KEY, field11, field12);
    IDBIndex index12 = table1.addIndex("index12", IDBIndex.Type.UNIQUE, field11, field12);
    IDBIndex index13 = table1.addIndex("index13", IDBIndex.Type.NON_UNIQUE, field12);

    IDBTable table2 = workingCopy.addTable("table2");
    IDBField field21 = table2.addField("field21", DBType.INTEGER, true);
    IDBField field22 = table2.addField("field22", DBType.VARCHAR, 64, true);
    IDBField field23 = table2.addField("field23", DBType.BOOLEAN);
    IDBIndex index21 = table2.addIndex("index21", IDBIndex.Type.PRIMARY_KEY, field21, field22);
    IDBIndex index22 = table2.addIndex("index22", IDBIndex.Type.UNIQUE, field21, field22);
    IDBIndex index23 = table2.addIndex("index23", IDBIndex.Type.NON_UNIQUE, field22);

    schemaTransaction.commit();
    assertEquals(true, schema.isLocked());
    assertEquals(false, schema.isEmpty());
    assertEquals(2, schema.getTables().length);

    IDBTable table1Schema = schema.getTable("table1");
    IDBTable table2Schema = schema.getTable("table2");
    assertEquals(table1, table1Schema);
    assertEquals(table1.getFieldCount(), table1Schema.getFieldCount());
    assertEquals(field11, table1Schema.getField(0));
    assertEquals(field12, table1Schema.getField(1));
    assertEquals(field13, table1Schema.getField(2));
    assertEquals(table1.getIndexCount(), table1Schema.getIndexCount());
    assertEquals(index11, table1Schema.getIndex("index11"));
    assertEquals(index11.getType(), table1Schema.getIndex("index11").getType());
    assertEquals(index12, table1Schema.getIndex("index12"));
    assertEquals(index12.getType(), table1Schema.getIndex("index12").getType());
    assertEquals(index13, table1Schema.getIndex("index13"));
    assertEquals(index13.getType(), table1Schema.getIndex("index13").getType());

    assertEquals(table2, table2Schema);
    assertEquals(table2.getFieldCount(), table2Schema.getFieldCount());
    assertEquals(field21, table2Schema.getField(0));
    assertEquals(field22, table2Schema.getField(1));
    assertEquals(field23, table2Schema.getField(2));
    assertEquals(table2.getIndexCount(), table2Schema.getIndexCount());
    assertEquals(index21, table2Schema.getIndex("index21"));
    assertEquals(index21.getType(), table2Schema.getIndex("index21").getType());
    assertEquals(index22, table2Schema.getIndex("index22"));
    assertEquals(index22.getType(), table2Schema.getIndex("index22").getType());
    assertEquals(index23, table2Schema.getIndex("index23"));
    assertEquals(index23.getType(), table2Schema.getIndex("index23").getType());
  }

  public void testChangeSchema() throws Exception
  {
    // Init database
    IDBDatabase database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    IDBSchema schema = database.getSchema();

    IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    IDBSchema workingCopy = schemaTransaction.getWorkingCopy();

    IDBTable table1 = workingCopy.addTable("table1");
    IDBField field11 = table1.addField("field11", DBType.INTEGER, true);
    IDBField field12 = table1.addField("field12", DBType.VARCHAR, 64, true);
    IDBField field13 = table1.addField("field13", DBType.BOOLEAN);
    IDBIndex index11 = table1.addIndex("index11", IDBIndex.Type.PRIMARY_KEY, field11, field12);
    IDBIndex index12 = table1.addIndex("index12", IDBIndex.Type.UNIQUE, field11, field12);
    IDBIndex index13 = table1.addIndex("index13", IDBIndex.Type.NON_UNIQUE, field12);

    schemaTransaction.commit();
    assertEquals(1, schema.getTables().length);

    // Reload database
    IDBDatabase database2 = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    IDBSchema schema2 = database2.getSchema();
    DBUtil.dump(schema2);
    assertEquals(true, schema2.isLocked());
    assertEquals(false, schema2.isEmpty());
    assertEquals(1, schema2.getTables().length);

    IDBTable table = schema2.getTable("table1");
    field11 = table.getField("field11");
    field12 = table.getField("field12");
    field13 = table.getField("field13");
    index11 = table.getIndex("index11");
    index12 = table.getIndex("index12");
    index13 = table.getIndex("index13");
    assertNotNull(field11);
    assertNotNull(field12);
    assertNotNull(field13);
    assertNotNull(index11);
    assertNotNull(index12);
    assertNotNull(index13);

    schemaTransaction = database2.openSchemaTransaction();
    workingCopy = schemaTransaction.getWorkingCopy();
    assertEquals(true, schema2.isLocked());
    assertEquals(false, schema2.isEmpty());
    assertEquals(1, schema2.getTables().length);

    IDBTable table2 = workingCopy.addTable("table2");
    IDBField field21 = table2.addField("field21", DBType.INTEGER, true);
    IDBField field22 = table2.addField("field22", DBType.VARCHAR, 64, true);
    IDBField field23 = table2.addField("field23", DBType.BOOLEAN);
    IDBIndex index21 = table2.addIndex("index21", IDBIndex.Type.PRIMARY_KEY, field21, field22);
    IDBIndex index22 = table2.addIndex("index22", IDBIndex.Type.UNIQUE, field21, field22);
    IDBIndex index23 = table2.addIndex("index23", IDBIndex.Type.NON_UNIQUE, field22);
    IDBTable table1Reload = schema2.getTable("table1");
    IDBTable table2Reload;
    assertEquals(table1, table1Reload);
    assertEquals(table1.getFieldCount(), table1Reload.getFieldCount());
    assertEquals(field11, table1Reload.getField(0));
    assertEquals(field12, table1Reload.getField(1));
    assertEquals(field13, table1Reload.getField(2));
    assertEquals(table1.getIndexCount(), table1Reload.getIndexCount());
    assertEquals(index11, table1Reload.getIndex("index11"));
    assertEquals(index11.getType(), table1Reload.getIndex("index11").getType());
    assertEquals(index12, table1Reload.getIndex("index12"));
    assertEquals(index12.getType(), table1Reload.getIndex("index12").getType());
    assertEquals(index13, table1Reload.getIndex("index13"));
    assertEquals(index13.getType(), table1Reload.getIndex("index13").getType());

    IDBSchemaDelta delta = schemaTransaction.commit();
    DBUtil.dump(delta);
    assertEquals(true, schema2.isLocked());
    assertEquals(false, schema2.isEmpty());
    assertEquals(2, schema2.getTables().length);

    table2Reload = schema2.getTable("table2");

    assertEquals(table1, table1Reload);
    assertEquals(table1.getFieldCount(), table1Reload.getFieldCount());
    assertEquals(field11, table1Reload.getField(0));
    assertEquals(field12, table1Reload.getField(1));
    assertEquals(field13, table1Reload.getField(2));
    assertEquals(table1.getIndexCount(), table1Reload.getIndexCount());
    assertEquals(index11, table1Reload.getIndex("index11"));
    assertEquals(index11.getType(), table1Reload.getIndex("index11").getType());
    assertEquals(index12, table1Reload.getIndex("index12"));
    assertEquals(index12.getType(), table1Reload.getIndex("index12").getType());
    assertEquals(index13, table1Reload.getIndex("index13"));
    assertEquals(index13.getType(), table1Reload.getIndex("index13").getType());

    assertEquals(table2, table2Reload);
    assertEquals(table2.getFieldCount(), table2Reload.getFieldCount());
    assertEquals(field21, table2Reload.getField(0));
    assertEquals(field22, table2Reload.getField(1));
    assertEquals(field23, table2Reload.getField(2));
    assertEquals(table2.getIndexCount(), table2Reload.getIndexCount());
    assertEquals(index21, table2Reload.getIndex("index21"));
    assertEquals(index21.getType(), table2Reload.getIndex("index21").getType());
    assertEquals(index22, table2Reload.getIndex("index22"));
    assertEquals(index22.getType(), table2Reload.getIndex("index22").getType());
    assertEquals(index23, table2Reload.getIndex("index23"));
    assertEquals(index23.getType(), table2Reload.getIndex("index23").getType());
  }

  public void testEnsureSchema() throws Exception
  {
    {
      // Init database
      IDBDatabase database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
      IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
      IDBSchema workingCopy = schemaTransaction.getWorkingCopy();

      IDBTable table1 = workingCopy.addTable("table1");
      table1.addField("field11", DBType.INTEGER, true);
      table1.addField("field12", DBType.VARCHAR, 64, true);
      table1.addField("field13", DBType.BOOLEAN);

      schemaTransaction.commit();
    }

    IDBSchema newSchema = DBUtil.createSchema("DIFFERENT_NAME");
    IDBTable table2 = newSchema.addTable("table2");
    IDBField field21 = table2.addField("field21", DBType.INTEGER, true);
    IDBField field22 = table2.addField("field22", DBType.VARCHAR, 64, true);
    table2.addField("field23", DBType.BOOLEAN);
    table2.addIndex("index21", IDBIndex.Type.PRIMARY_KEY, field21, field22);
    table2.addIndex("index22", IDBIndex.Type.UNIQUE, field21, field22);
    table2.addIndex("index23", IDBIndex.Type.NON_UNIQUE, field22);

    // Reload database
    IDBDatabase database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    schemaTransaction.ensureSchema(newSchema);
    schemaTransaction.commit();

    IDBSchema schema = database.getSchema();
    assertEquals(true, schema.isLocked());
    assertEquals(false, schema.isEmpty());
    assertEquals(SCHEMA_NAME, schema.getName());
    assertEquals(2, schema.getTables().length);

    DBUtil.dump(schema);
  }
}
