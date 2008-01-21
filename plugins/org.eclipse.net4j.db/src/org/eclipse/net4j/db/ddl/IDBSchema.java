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
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBAdapter;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IDBSchema extends IDBSchemaElement
{
  public IDBTable addTable(String name) throws DBException;

  public IDBTable getTable(String name);

  public IDBTable[] getTables();

  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException;

  public Set<IDBTable> create(IDBAdapter dbAdapter, IDBConnectionProvider connectionProvider) throws DBException;

  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException;
}
