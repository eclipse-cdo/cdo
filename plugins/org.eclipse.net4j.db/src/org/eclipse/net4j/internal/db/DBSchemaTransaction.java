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
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.delta.IDBSchemaDelta;
import org.eclipse.net4j.spi.db.DBSchema;

import java.sql.Connection;

/**
 * @author Eike Stepper
 */
public final class DBSchemaTransaction extends DBElement implements IDBSchemaTransaction
{
  private DBConnection dbConnection;

  private IDBSchema dbSchema;

  public DBSchemaTransaction(DBConnection dbConnection)
  {
    this.dbConnection = dbConnection;

    IDBSchema oldSchema = dbConnection.getDBInstance().getDBSchema();
    dbSchema = new DBSchema(oldSchema);
  }

  public IDBConnection getDBConnection()
  {
    return dbConnection;
  }

  public IDBSchema getDBSchema()
  {
    return dbSchema;
  }

  public void commit()
  {
    DBInstance dbInstance = dbConnection.getDBInstance();

    IDBAdapter dbAdapter = dbInstance.getDBAdapter();
    Connection connection = dbConnection.getSQLConnection();

    IDBSchema oldSchema = dbInstance.getDBSchema();
    IDBSchemaDelta delta = dbSchema.compare(oldSchema);

    try
    {
      dbAdapter.updateSchema(delta, connection);
    }
    finally
    {
      close();
    }
  }

  public void close()
  {
    dbConnection.getDBInstance().setDBSchemaTransaction(null);
    dbConnection = null;
    dbSchema = null;
  }

  public boolean isClosed()
  {
    return dbConnection == null;
  }
}
