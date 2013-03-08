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

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.RunnableWithConnection;
import org.eclipse.net4j.db.IDBSchemaTransaction;
import org.eclipse.net4j.internal.db.ddl.delta.DBSchemaDelta;
import org.eclipse.net4j.spi.db.DBAdapter;
import org.eclipse.net4j.spi.db.DBSchema;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
public final class DBSchemaTransaction implements IDBSchemaTransaction, RunnableWithConnection<DBSchemaDelta>
{
  private DBDatabase database;

  private DBTransaction transaction;

  private DBSchema oldSchema;

  private DBSchema schema;

  public DBSchemaTransaction(DBDatabase database)
  {
    this.database = database;

    oldSchema = database.getSchema();
    schema = new DBSchema(oldSchema);
  }

  public DBDatabase getDatabase()
  {
    return database;
  }

  public DBTransaction getTransaction()
  {
    return transaction;
  }

  public void setTransaction(DBTransaction getTransaction)
  {
    transaction = getTransaction;
  }

  public DBSchema getSchema()
  {
    return schema;
  }

  public DBSchemaDelta getSchemaDelta()
  {
    return (DBSchemaDelta)schema.compare(oldSchema);
  }

  public DBSchemaDelta commit()
  {
    if (transaction == null)
    {
      return DBUtil.execute(database.getConnectionProvider(), this);
    }

    try
    {
      Connection connection = transaction.getConnection();
      return run(connection);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public DBSchemaDelta run(Connection connection) throws SQLException
  {
    DBSchemaDelta delta = getSchemaDelta();

    try
    {
      oldSchema.unlock();

      DBAdapter adapter = database.getAdapter();
      adapter.updateSchema(connection, oldSchema, delta);
    }
    finally
    {
      oldSchema.lock();
      close();
    }

    return delta;
  }

  public void close()
  {
    if (!isClosed())
    {
      database.closeSchemaTransaction();
      transaction = null;
      oldSchema = null;
      schema = null;
    }
  }

  public boolean isClosed()
  {
    return schema == null;
  }
}
