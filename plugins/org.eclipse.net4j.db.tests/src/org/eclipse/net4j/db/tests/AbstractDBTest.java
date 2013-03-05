/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public static final String SCHEMA_NAME = "test";

  private IDBAdapter adapter;

  private IDBConnectionProvider connectionProvider;

  private Connection internalConnection;

  @Override
  protected void doSetUp() throws Exception
  {
    adapter = createAdapter();

    DataSource dataSource = createDataSource();
    connectionProvider = DBUtil.createConnectionProvider(dataSource);
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

  public void testSchemaEmpty() throws Exception
  {
    IDBSchema schema = DBUtil.readSchema(SCHEMA_NAME, getConnection());
    assertEquals(true, schema.isEmpty());
  }

  public void testSchemaCreation() throws Exception
  {
    IDBDatabase database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    assertEquals(true, database.getSchema().isLocked());
    assertEquals(true, database.getSchema().isEmpty());

    IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    assertEquals(false, schemaTransaction.getSchema().isLocked());
    assertEquals(true, schemaTransaction.getSchema().isEmpty());
    assertEquals(database.getSchema(), schemaTransaction.getSchema());

    IDBTable table1 = schemaTransaction.getSchema().addTable("table1");
    IDBField field11 = table1.addField("field1", DBType.INTEGER);
    IDBField field12 = table1.addField("field2", DBType.VARCHAR, 64);
    IDBField field13 = table1.addField("field3", DBType.BOOLEAN);
    IDBIndex index11 = table1.addIndex("index1", IDBIndex.Type.PRIMARY_KEY, field11, field12);
    IDBIndex index12 = table1.addIndex("index2", IDBIndex.Type.UNIQUE, field11, field12);
    IDBIndex index13 = table1.addIndex("index3", IDBIndex.Type.NON_UNIQUE, field12);

    IDBTable table2 = schemaTransaction.getSchema().addTable("table2");
    IDBField field21 = table2.addField("field1", DBType.INTEGER);
    IDBField field22 = table2.addField("field2", DBType.VARCHAR, 64);
    IDBField field23 = table2.addField("field3", DBType.BOOLEAN);
    IDBIndex index21 = table2.addIndex("index1", IDBIndex.Type.PRIMARY_KEY, field21, field22);
    IDBIndex index22 = table2.addIndex("index2", IDBIndex.Type.UNIQUE, field21, field22);
    IDBIndex index23 = table2.addIndex("index3", IDBIndex.Type.NON_UNIQUE, field22);

    schemaTransaction.commit();
    assertEquals(true, database.getSchema().isLocked());
    assertEquals(false, database.getSchema().isEmpty());
    assertEquals(2, database.getSchema().getTables().length);

    assertEquals(table1, database.getSchema().getTables()[0]);
    assertEquals(table1.getFieldCount(), database.getSchema().getTables()[0].getFieldCount());
    assertEquals(field11, database.getSchema().getTables()[0].getField(0));
    assertEquals(field12, database.getSchema().getTables()[0].getField(1));
    assertEquals(field13, database.getSchema().getTables()[0].getField(2));
    assertEquals(table1.getIndexCount(), database.getSchema().getTables()[0].getIndexCount());
    assertEquals(index11, database.getSchema().getTables()[0].getIndex(0));
    assertEquals(index11.getType(), database.getSchema().getTables()[0].getIndex(0).getType());
    assertEquals(index12, database.getSchema().getTables()[0].getIndex(1));
    assertEquals(index12.getType(), database.getSchema().getTables()[0].getIndex(1).getType());
    assertEquals(index13, database.getSchema().getTables()[0].getIndex(2));
    assertEquals(index13.getType(), database.getSchema().getTables()[0].getIndex(2).getType());

    assertEquals(table2, database.getSchema().getTables()[1]);
    assertEquals(table2.getFieldCount(), database.getSchema().getTables()[1].getFieldCount());
    assertEquals(field21, database.getSchema().getTables()[1].getField(0));
    assertEquals(field22, database.getSchema().getTables()[1].getField(1));
    assertEquals(field23, database.getSchema().getTables()[1].getField(2));
    assertEquals(table2.getIndexCount(), database.getSchema().getTables()[1].getIndexCount());
    assertEquals(index21, database.getSchema().getTables()[1].getIndex(0));
    assertEquals(index21.getType(), database.getSchema().getTables()[1].getIndex(0).getType());
    assertEquals(index22, database.getSchema().getTables()[1].getIndex(1));
    assertEquals(index22.getType(), database.getSchema().getTables()[1].getIndex(1).getType());
    assertEquals(index23, database.getSchema().getTables()[1].getIndex(2));
    assertEquals(index23.getType(), database.getSchema().getTables()[1].getIndex(2).getType());
  }

  public void testSchemaAddition() throws Exception
  {
    // Init database
    IDBDatabase database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();

    IDBTable table1 = schemaTransaction.getSchema().addTable("table1");
    IDBField field11 = table1.addField("field1", DBType.INTEGER);
    IDBField field12 = table1.addField("field2", DBType.VARCHAR, 64);
    IDBField field13 = table1.addField("field3", DBType.BOOLEAN);
    IDBIndex index11 = table1.addIndex("index1", IDBIndex.Type.PRIMARY_KEY, field11, field12);
    IDBIndex index12 = table1.addIndex("index2", IDBIndex.Type.UNIQUE, field11, field12);
    IDBIndex index13 = table1.addIndex("index3", IDBIndex.Type.NON_UNIQUE, field12);

    schemaTransaction.commit();
    assertEquals(1, database.getSchema().getTables().length);

    // Reload database
    database = DBUtil.openDatabase(adapter, connectionProvider, SCHEMA_NAME);
    assertEquals(true, database.getSchema().isLocked());
    assertEquals(false, database.getSchema().isEmpty());
    assertEquals(1, database.getSchema().getTables().length);

    IDBTable table = database.getSchema().getTable("table1");
    field11 = table.getField("field1");
    field12 = table.getField("field2");
    field13 = table.getField("field3");
    index11 = table.getIndex("index1");
    index12 = table.getIndex("index2");
    index13 = table.getIndex("index3");
    assertNotNull(field11);
    assertNotNull(field12);
    assertNotNull(field13);
    assertNotNull(index11);
    assertNotNull(index12);
    assertNotNull(index13);

    schemaTransaction = database.openSchemaTransaction();
    assertEquals(true, database.getSchema().isLocked());
    assertEquals(false, database.getSchema().isEmpty());
    assertEquals(1, database.getSchema().getTables().length);

    IDBTable table2 = schemaTransaction.getSchema().addTable("table2");
    IDBField field21 = table2.addField("field1", DBType.INTEGER);
    IDBField field22 = table2.addField("field2", DBType.VARCHAR, 64);
    IDBField field23 = table2.addField("field3", DBType.BOOLEAN);
    IDBIndex index21 = table2.addIndex("index1", IDBIndex.Type.PRIMARY_KEY, field21, field22);
    IDBIndex index22 = table2.addIndex("index2", IDBIndex.Type.UNIQUE, field21, field22);
    IDBIndex index23 = table2.addIndex("index3", IDBIndex.Type.NON_UNIQUE, field22);

    IDBSchemaDelta schemaDelta = schemaTransaction.getSchemaDelta();

    schemaTransaction.commit();
    assertEquals(true, database.getSchema().isLocked());
    assertEquals(false, database.getSchema().isEmpty());
    assertEquals(2, database.getSchema().getTables().length);

    assertEquals(table1, database.getSchema().getTables()[0]);
    assertEquals(table1.getFieldCount(), database.getSchema().getTables()[0].getFieldCount());
    assertEquals(field11, database.getSchema().getTables()[0].getField(0));
    assertEquals(field12, database.getSchema().getTables()[0].getField(1));
    assertEquals(field13, database.getSchema().getTables()[0].getField(2));
    assertEquals(table1.getIndexCount(), database.getSchema().getTables()[0].getIndexCount());
    assertEquals(index11, database.getSchema().getTables()[0].getIndex(0));
    assertEquals(index11.getType(), database.getSchema().getTables()[0].getIndex(0).getType());
    assertEquals(index12, database.getSchema().getTables()[0].getIndex(1));
    assertEquals(index12.getType(), database.getSchema().getTables()[0].getIndex(1).getType());
    assertEquals(index13, database.getSchema().getTables()[0].getIndex(2));
    assertEquals(index13.getType(), database.getSchema().getTables()[0].getIndex(2).getType());

    assertEquals(table2, database.getSchema().getTables()[1]);
    assertEquals(table2.getFieldCount(), database.getSchema().getTables()[1].getFieldCount());
    assertEquals(field21, database.getSchema().getTables()[1].getField(0));
    assertEquals(field22, database.getSchema().getTables()[1].getField(1));
    assertEquals(field23, database.getSchema().getTables()[1].getField(2));
    assertEquals(table2.getIndexCount(), database.getSchema().getTables()[1].getIndexCount());
    assertEquals(index21, database.getSchema().getTables()[1].getIndex(0));
    assertEquals(index21.getType(), database.getSchema().getTables()[1].getIndex(0).getType());
    assertEquals(index22, database.getSchema().getTables()[1].getIndex(1));
    assertEquals(index22.getType(), database.getSchema().getTables()[1].getIndex(1).getType());
    assertEquals(index23, database.getSchema().getTables()[1].getIndex(2));
    assertEquals(index23.getType(), database.getSchema().getTables()[1].getIndex(2).getType());
  }
}
