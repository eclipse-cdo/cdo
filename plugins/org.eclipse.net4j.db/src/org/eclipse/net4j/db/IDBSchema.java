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
package org.eclipse.net4j.db;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IDBSchema extends IDBElement
{
  public IDBTable addTable(String name) throws DBException;

  public IDBTable getTable(String name);

  public IDBTable[] getTables();

  public Set<IDBTable> create(IDBAdapter dbAdapter, Connection connection) throws DBException;

  public Set<IDBTable> create(IDBAdapter dbAdapter, ConnectionProvider connectionProvider) throws DBException;

  public Set<IDBTable> create(IDBAdapter dbAdapter, DataSource dataSource) throws DBException;
}
