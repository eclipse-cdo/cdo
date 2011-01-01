/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.spi.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBRowHandler;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.ddl.DBTable;

import javax.sql.DataSource;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBSchema extends DBSchemaElement implements IDBSchema
{
  private String name;

  private Map<String, DBTable> tables = new HashMap<String, DBTable>();

  private boolean locked;

  public DBSchema(String name)
  {
    this.name = name;
  }

  public String getFullName()
  {
    return name;
  }

  public IDBSchema getSchema()
  {
    return this;
  }

  public String getName()
  {
    return name;
  }

  /**
   * @since 2.0
   */
  public IDBTable addTable(String name) throws DBException
  {
    assertUnlocked();
    if (tables.containsKey(name))
    {
      throw new DBException("DBTable exists: " + name); //$NON-NLS-1$
    }

    DBTable table = new DBTable(this, name);
    tables.put(name, table);
    return table;
  }

  /**
   * @since 4.0
   */
  public IDBTable removeTable(String name)
  {
    assertUnlocked();
    return tables.remove(name);
  }

  /**
   * @since 2.0
   */
  public IDBTable getTable(String name)
  {
    return tables.get(name);
  }

  /**
   * @since 2.0
   */
  public IDBTable[] getTables()
  {
    return tables.values().toArray(new DBTable[tables.size()]);
  }

  public boolean isLocked()
  {
    return locked;
  }

  public boolean lock()
  {
    return locked = true;
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    return dbAdapter.createTables(tables.values(), connection);
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    return create(dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      if (connection == null)
      {
        throw new DBException("No connection available from " + connectionProvider); //$NON-NLS-1$
      }

      return create(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public void drop(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    dbAdapter.dropTables(tables.values(), connection);
  }

  public void drop(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    drop(dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  public void drop(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      drop(dbAdapter, connection);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public void export(Connection connection, PrintStream out) throws DBException
  {
    for (IDBTable table : getTables())
    {
      export(table, connection, out);
    }
  }

  private void export(final IDBTable table, Connection connection, final PrintStream out)
  {
    if (DBUtil.select(connection, new IDBRowHandler()
    {
      public boolean handle(int row, Object... values)
      {
        if (row == 0)
        {
          String tableName = table.getName();
          out.println(tableName);
          for (int i = 0; i < tableName.length(); i++)
          {
            out.print("="); //$NON-NLS-1$
          }

          out.println();
        }

        out.println(Arrays.asList(values));
        return true;
      }
    }, table.getFields()) > 0)

    {
      out.println();
    }
  }

  public void export(DataSource dataSource, PrintStream out) throws DBException
  {
    export(DBUtil.createConnectionProvider(dataSource), out);
  }

  public void export(IDBConnectionProvider connectionProvider, PrintStream out) throws DBException
  {
    Connection connection = null;

    try
    {
      connection = connectionProvider.getConnection();
      export(connection, out);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  public void assertUnlocked() throws DBException
  {
    if (locked)
    {
      throw new DBException("DBSchema locked: " + name); //$NON-NLS-1$
    }
  }
}
