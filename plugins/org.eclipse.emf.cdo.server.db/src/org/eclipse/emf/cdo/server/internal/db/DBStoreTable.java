/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBDatabase;
import org.eclipse.net4j.db.IDBDatabase.RunnableWithSchema;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 */
public abstract class DBStoreTable extends Lifecycle
{
  private final IDBStore store;

  private final String tableName;

  private IDBTable table;

  public DBStoreTable(IDBStore store, String tableName)
  {
    this.store = store;
    this.tableName = tableName;
  }

  public final IDBStore store()
  {
    return store;
  }

  public final IDBTable table()
  {
    return table;
  }

  @Override
  public String toString()
  {
    return table == null ? tableName : table.toString();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    IDBDatabase database = store.getDatabase();

    table = database.getSchema().getTable(tableName);
    if (table == null)
    {
      database.updateSchema(new RunnableWithSchema()
      {
        @Override
        public void run(IDBSchema schema)
        {
          table = schema.addTable(tableName);
          firstActivate(table);
        }
      });
    }
    else
    {
      reActivate(table);
    }

    initSQL(table);
  }

  protected abstract void firstActivate(IDBTable table);

  protected abstract void reActivate(IDBTable table);

  protected void initSQL(IDBTable table)
  {
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    table = null;
    super.doDeactivate();
  }

  protected final IDBConnection getConnection()
  {
    return store.getDatabase().getConnection();
  }

  protected static IDBStoreAccessor accessor()
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    if (accessor == null)
    {
      throw new IllegalStateException("Can only be called from within a valid IDBStoreAccessor context");
    }

    return (IDBStoreAccessor)accessor;
  }
}
