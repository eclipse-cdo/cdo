/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBSchema;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DBSchema implements IDBSchema
{
  private String name;

  private Map<String, DBTable> tables = new HashMap();

  private boolean locked;

  public DBSchema(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public DBTable addTable(String name)
  {
    assertUnlocked();
    if (tables.containsKey(name))
    {
      throw new IllegalStateException("DBTable exists: " + name);
    }

    DBTable table = new DBTable(this, name);
    tables.put(name, table);
    return table;
  }

  public DBTable getTable(String name)
  {
    return tables.get(name);
  }

  public DBTable[] getTables()
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

  public void create(IDBAdapter dbAdapter, DataSource dataSource)
  {
    try
    {
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      for (DBTable table : tables.values())
      {
        dbAdapter.createTable(table, statement);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public String toString()
  {
    return name;
  }

  void assertUnlocked()
  {
    if (locked)
    {
      throw new IllegalStateException("DBSchema locked: " + name);
    }
  }
}
