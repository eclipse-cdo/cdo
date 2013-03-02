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
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.IDBInstance;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class DBInstance extends DBElement implements IDBInstance
{
  private IDBAdapter dbAdapter;

  private IDBSchema dbSchema;

  private DBSchemaTransaction dbSchemaTransaction;

  private IDBConnectionProvider dbConnectionProvider;

  private final Set<IDBConnection> dbConnections = new HashSet<IDBConnection>();

  private int statementCacheCapacity = DEFAULT_STATEMENT_CACHE_CAPACITY;

  public DBInstance()
  {
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public IDBSchema getDBSchema()
  {
    return dbSchema;
  }

  public IDBSchemaTransaction getDBSchemaTransaction()
  {
    return dbSchemaTransaction;
  }

  public void setDBSchemaTransaction(DBSchemaTransaction dbSchemaTransaction)
  {
    this.dbSchemaTransaction = dbSchemaTransaction;
  }

  public IDBConnectionProvider getDBConnectionProvider()
  {
    return dbConnectionProvider;
  }

  public IDBConnection openDBConnection()
  {
    DBConnection dbConnection = new DBConnection(this);
    synchronized (dbConnections)
    {
      dbConnections.add(dbConnection);
    }

    fireEvent(new SingleDeltaContainerEvent<IDBConnection>(this, dbConnection, IContainerDelta.Kind.ADDED));
    return dbConnection;
  }

  public void closeDBConnection(DBConnection dbConnection)
  {
    synchronized (dbConnections)
    {
      dbConnections.remove(dbConnection);
    }

    fireEvent(new SingleDeltaContainerEvent<IDBConnection>(this, dbConnection, IContainerDelta.Kind.REMOVED));
  }

  public IDBConnection[] getDBConnections()
  {
    synchronized (dbConnections)
    {
      return dbConnections.toArray(new IDBConnection[dbConnections.size()]);
    }
  }

  public boolean isEmpty()
  {
    synchronized (dbConnections)
    {
      return dbConnections.isEmpty();
    }
  }

  public IDBConnection[] getElements()
  {
    return getDBConnections();
  }

  public int getStatementCacheCapacity()
  {
    return statementCacheCapacity;
  }

  public void setStatementCacheCapacity(int statementCacheCapacity)
  {
    this.statementCacheCapacity = statementCacheCapacity;
  }
}
