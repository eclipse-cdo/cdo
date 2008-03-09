/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractDBTest extends AbstractOMTest
{
  protected IDBAdapter dbAdapter;

  protected IDBConnectionProvider dbConnectionProvider;

  @Override
  protected void doSetUp() throws Exception
  {
    dbAdapter = createDBAdapter();
    dbConnectionProvider = createDBConnectionProvider();
  }

  @Override
  protected void doTearDown() throws Exception
  {
  }

  protected abstract IDBAdapter createDBAdapter();

  protected abstract IDBConnectionProvider createDBConnectionProvider();

  protected Connection getConnection()
  {
    return dbConnectionProvider.getConnection();
  }

  public void testDBTypes() throws Exception
  {
    IDBSchema schema = DBUtil.createSchema("testDBTypes");
    DBType[] dbTypes = DBType.values();

    int i = 0;
    for (DBType dbType : dbTypes)
    {
      IDBTable table = schema.addTable("table_" + i);
      table.addField("field", dbType);

      IDBTable idx_table = schema.addTable("idx_table" + i);
      IDBField idx_field = idx_table.addField("field", dbType);
      idx_table.addIndex(IDBIndex.Type.NON_UNIQUE, idx_field);

      IDBTable uni_table = schema.addTable("uni_table" + i);
      IDBField uni_field = uni_table.addField("field", dbType);
      uni_table.addIndex(IDBIndex.Type.UNIQUE, uni_field);

      IDBTable pk_table = schema.addTable("pk_table" + i);
      IDBField pk_field = pk_table.addField("field", dbType);
      pk_table.addIndex(IDBIndex.Type.PRIMARY_KEY, pk_field);

      ++i;
    }

    Set<IDBTable> tables = schema.create(dbAdapter, dbConnectionProvider);
    assertEquals(dbTypes.length * 4, tables.size());
  }
}
