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

import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public interface IDBAdapter
{
  public static final IRegistry<String, IDBAdapter> REGISTRY = DBAdapterRegistry.INSTANCE;

  public String getName();

  public String getVersion();

  public Driver getJDBCDriver();

  public void createTables(IDBTable[] tables, Connection connection) throws DBException;

  public void createTable(IDBTable table, Statement statement) throws DBException;
}
