/*
 * Copyright (c) 2008-2013, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.spi.db;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import javax.sql.DataSource;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @deprecated As of 4.2 call {@link DBUtil#createSchema(String)}, {@link DBUtil#readSchema(IDBAdapter, Connection, IDBSchema)},
 *    {@link DBUtil#readSchema(IDBAdapter, Connection, String)} or {@link DBUtil#copySchema(IDBSchema)}.
 */
@Deprecated
public class DBSchema extends org.eclipse.net4j.internal.db.ddl.DBSchema
{
  private static final long serialVersionUID = 1L;

  @Deprecated
  public DBSchema(String name)
  {
    super(name, false, false);
  }

  /**
   * @since 4.2
   */
  @Deprecated
  public DBSchema(IDBSchema source)
  {
    super(source);
  }

  /**
   * Constructor for deserialization.
   *
   * @since 4.2
   */
  @Deprecated
  protected DBSchema()
  {
  }

  @Deprecated
  @Override
  public IDBSchema getSchema()
  {
    return super.getSchema();
  }

  @Deprecated
  @Override
  public String getFullName()
  {
    return super.getFullName();
  }

  @Deprecated
  @Override
  public IDBTable addTable(String name) throws DBException
  {
    return super.addTable(name);
  }

  @Deprecated
  @Override
  public IDBTable removeTable(String name)
  {
    return super.removeTable(name);
  }

  @Deprecated
  @Override
  public IDBTable getTable(String name)
  {
    return super.getTable(name);
  }

  @Deprecated
  @Override
  public IDBTable[] getTables()
  {
    return super.getTables();
  }

  @Deprecated
  @Override
  public boolean isLocked()
  {
    return super.isLocked();
  }

  @Deprecated
  @Override
  public boolean lock()
  {
    return super.lock();
  }

  @Deprecated
  @Override
  public void assertUnlocked() throws DBException
  {
    super.assertUnlocked();
  }

  @Deprecated
  @Override
  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    return super.create(dbAdapter, connection);
  }

  @Deprecated
  @Override
  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    return super.create(dbAdapter, dataSource);
  }

  @Deprecated
  @Override
  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    return super.create(dbAdapter, connectionProvider);
  }

  @Deprecated
  @Override
  public void drop(IDBAdapter dbAdapter, Connection connection) throws DBException
  {
    super.drop(dbAdapter, connection);
  }

  @Deprecated
  @Override
  public void drop(IDBAdapter dbAdapter, DataSource dataSource) throws DBException
  {
    super.drop(dbAdapter, dataSource);
  }

  @Deprecated
  @Override
  public void drop(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException
  {
    super.drop(dbAdapter, connectionProvider);
  }

  @Deprecated
  @Override
  public void export(Connection connection, PrintStream out) throws DBException
  {
    super.export(connection, out);
  }

  @Deprecated
  @Override
  public void export(DataSource dataSource, PrintStream out) throws DBException
  {
    super.export(dataSource, out);
  }

  @Deprecated
  @Override
  public void export(IDBConnectionProvider connectionProvider, PrintStream out) throws DBException
  {
    super.export(connectionProvider, out);
  }

  @Deprecated
  @Override
  public String getName()
  {
    return super.getName();
  }

  @Deprecated
  @Override
  public String toString()
  {
    return super.toString();
  }

  @Deprecated
  @Override
  public void addListener(IListener listener)
  {
    super.addListener(listener);
  }

  @Deprecated
  @Override
  public void removeListener(IListener listener)
  {
    super.removeListener(listener);
  }

  @Deprecated
  @Override
  public boolean hasListeners()
  {
    return super.hasListeners();
  }

  @Deprecated
  @Override
  public IListener[] getListeners()
  {
    return super.getListeners();
  }

  @Deprecated
  @Override
  public void fireEvent()
  {
    super.fireEvent();
  }

  @Deprecated
  @Override
  public void fireEvent(IEvent event)
  {
    super.fireEvent(event);
  }

  @Deprecated
  @Override
  public void fireEvent(IEvent event, IListener[] listeners)
  {
    super.fireEvent(event, listeners);
  }

  @Deprecated
  @Override
  protected void fireThrowable(Throwable throwable)
  {
    super.fireThrowable(throwable);
  }

  @Deprecated
  @Override
  protected ExecutorService getNotificationService()
  {
    return super.getNotificationService();
  }

  @Deprecated
  @Override
  protected void firstListenerAdded()
  {
    super.firstListenerAdded();
  }

  @Deprecated
  @Override
  protected void lastListenerRemoved()
  {
    super.lastListenerRemoved();
  }

  @Override
  @Deprecated
  @SuppressWarnings("removal")
  protected void finalize() throws Throwable
  {
    super.finalize();
  }
}
