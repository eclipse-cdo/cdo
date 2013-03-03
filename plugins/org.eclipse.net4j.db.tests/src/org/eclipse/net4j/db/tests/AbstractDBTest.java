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
import org.eclipse.net4j.util.tests.AbstractOMTest;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractDBTest extends AbstractOMTest
{
  private IDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  private Connection internalConnection;

  @Override
  protected void doSetUp() throws Exception
  {
    dbAdapter = createDBAdapter();

    DataSource dataSource = createDataSource();
    dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
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

    dbConnectionProvider = null;
    dbAdapter = null;
  }

  protected abstract IDBAdapter createDBAdapter();

  protected abstract DataSource createDataSource();

  private Connection getConnection()
  {
    if (internalConnection == null)
    {
      internalConnection = dbConnectionProvider.getConnection();
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

      if (dbAdapter.isTypeIndexable(dbType))
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

    Set<IDBTable> tables = schema.create(dbAdapter, getConnection());
    assertEquals(count, tables.size());
  }

  public void testSchemaEmpty() throws Exception
  {
    IDBSchema schema = DBUtil.readSchema("test", getConnection());
    assertEquals(true, schema.isEmpty());
  }

  public void testSchemaAddition() throws Exception
  {
    IDBDatabase database = DBUtil.createDatabase(dbAdapter, dbConnectionProvider, "test");
    assertEquals(true, database.getSchema().isLocked());
    assertEquals(true, database.getSchema().isEmpty());

    IDBSchemaTransaction schemaTransaction = database.openSchemaTransaction();
    assertEquals(false, schemaTransaction.getSchema().isLocked());
    assertEquals(true, schemaTransaction.getSchema().isEmpty());
    assertEquals(database.getSchema().getName(), schemaTransaction.getSchema().getName());

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

    assertEquals(table1.getName(), database.getSchema().getTables()[0].getName());
    assertEquals(table1.getFieldCount(), database.getSchema().getTables()[0].getFieldCount());
    assertEquals(field11.getName(), database.getSchema().getTables()[0].getField(0).getName());
    assertEquals(field12.getName(), database.getSchema().getTables()[0].getField(1).getName());
    assertEquals(field13.getName(), database.getSchema().getTables()[0].getField(2).getName());
    assertEquals(table1.getIndexCount(), database.getSchema().getTables()[0].getIndexCount());
    assertEquals(index11.getName(), database.getSchema().getTables()[0].getIndex(0).getName());
    assertEquals(index11.getType(), database.getSchema().getTables()[0].getIndex(0).getType());
    assertEquals(index12.getName(), database.getSchema().getTables()[0].getIndex(1).getName());
    assertEquals(index12.getType(), database.getSchema().getTables()[0].getIndex(1).getType());
    assertEquals(index13.getName(), database.getSchema().getTables()[0].getIndex(2).getName());
    assertEquals(index13.getType(), database.getSchema().getTables()[0].getIndex(2).getType());

    assertEquals(table2.getName(), database.getSchema().getTables()[1].getName());
    assertEquals(table2.getFieldCount(), database.getSchema().getTables()[1].getFieldCount());
    assertEquals(field21.getName(), database.getSchema().getTables()[1].getField(0).getName());
    assertEquals(field22.getName(), database.getSchema().getTables()[1].getField(1).getName());
    assertEquals(field23.getName(), database.getSchema().getTables()[1].getField(2).getName());
    assertEquals(table2.getIndexCount(), database.getSchema().getTables()[1].getIndexCount());
    assertEquals(index21.getName(), database.getSchema().getTables()[1].getIndex(0).getName());
    assertEquals(index21.getType(), database.getSchema().getTables()[1].getIndex(0).getType());
    assertEquals(index22.getName(), database.getSchema().getTables()[1].getIndex(1).getName());
    assertEquals(index22.getType(), database.getSchema().getTables()[1].getIndex(1).getType());
    assertEquals(index23.getName(), database.getSchema().getTables()[1].getIndex(2).getName());
    assertEquals(index23.getType(), database.getSchema().getTables()[1].getIndex(2).getType());
  }
}
