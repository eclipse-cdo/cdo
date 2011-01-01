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
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;

import javax.sql.DataSource;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IDBSchema extends IDBSchemaElement
{
  public IDBTable addTable(String name) throws DBException;

  /**
   * @since 4.0
   */
  public IDBTable removeTable(String name) throws DBException;

  public IDBTable getTable(String name);

  public IDBTable[] getTables();

  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException;

  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException;

  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException;

  public void drop(IDBAdapter dbAdapter, Connection connection) throws DBException;

  public void drop(IDBAdapter dbAdapter, DataSource dataSource) throws DBException;

  public void drop(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException;

  public void export(Connection connection, PrintStream out) throws DBException;

  public void export(DataSource dataSource, PrintStream out) throws DBException;

  public void export(IDBConnectionProvider connectionProvider, PrintStream out) throws DBException;
}
