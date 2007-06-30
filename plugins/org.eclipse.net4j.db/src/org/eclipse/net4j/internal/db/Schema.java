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
import org.eclipse.net4j.db.ISchema;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Schema implements ISchema
{
  private String name;

  private Map<String, Table> tables = new HashMap();

  private boolean locked;

  public Schema(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public Table addTable(String name)
  {
    assertUnlocked();
    if (tables.containsKey(name))
    {
      throw new IllegalStateException("Table exists: " + name);
    }

    Table table = new Table(this, name);
    tables.put(name, table);
    return table;
  }

  public Table getTable(String name)
  {
    return tables.get(name);
  }

  public Table[] getTables()
  {
    return tables.values().toArray(new Table[tables.size()]);
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
      for (Table table : tables.values())
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
      throw new IllegalStateException("Schema locked: " + name);
    }
  }
}
