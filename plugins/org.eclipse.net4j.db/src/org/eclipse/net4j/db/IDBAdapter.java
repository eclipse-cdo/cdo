/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IDBAdapter
{
  public static final IRegistry<String, IDBAdapter> REGISTRY = DBAdapterRegistry.INSTANCE;

  public String getName();

  public String getVersion();

  public Driver getJDBCDriver();

  public DataSource createJDBCDataSource();

  public Set<IDBTable> createTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException;

  public boolean createTable(IDBTable table, Statement statement) throws DBException;

  public Collection<IDBTable> dropTables(Iterable<? extends IDBTable> tables, Connection connection) throws DBException;

  public boolean dropTable(IDBTable table, Statement statement);

  public String[] getReservedWords();

  public boolean isReservedWord(String word);

  /**
   * @since 2.0
   */
  public int getMaxTableNameLength();

  /**
   * @since 2.0
   */
  public int getMaxFieldNameLength();

  public void appendValue(StringBuilder builder, IDBField field, Object value);

  public boolean isTypeIndexable(DBType type);
}
