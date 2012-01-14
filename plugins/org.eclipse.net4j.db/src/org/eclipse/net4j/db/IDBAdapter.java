/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 289445
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.internal.db.DBAdapterRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;

/**
 * Abstracts all aspects of a database that are vendor-specific.
 * 
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

  public boolean isTypeIndexable(DBType type);

  /**
   * Provide a way for the DBAdapter to override unsupported DB types with replacements. The default implementation just
   * returns the given type. Subclasses may override single types with replacements.
   * 
   * @since 3.0
   */
  public DBType adaptType(DBType type);

  /**
   * Check if a character is valid as first character. (e.g., underscores are forbidden as first character in Derby
   * elements.
   * 
   * @since 4.0
   */
  public boolean isValidFirstChar(char ch);

  /**
   * Check if an exception indicates a constraint violation (duplicate key)
   * 
   * @since 4.0
   */
  public boolean isDuplicateKeyException(SQLException ex);
}
