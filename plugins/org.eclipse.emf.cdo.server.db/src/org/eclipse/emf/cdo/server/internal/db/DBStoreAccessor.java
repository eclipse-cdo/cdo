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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.StoreAccessor;
import org.eclipse.emf.cdo.protocol.model.CDOClass;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStoreAccessor extends StoreAccessor implements IDBStoreAccessor
{
  private Connection connection;

  private Statement statement;

  protected DBStoreAccessor(DBStore store, ISession session) throws DBException
  {
    super(store, session);
    initConnection();
  }

  protected DBStoreAccessor(DBStore store, IView view) throws DBException
  {
    super(store, view);
    initConnection();
  }

  protected void initConnection()
  {
    try
    {
      connection = getStore().getDBConnectionProvider().getConnection();
      connection.setAutoCommit(isReader());
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  protected void doRelease() throws DBException
  {
    DBUtil.close(statement);
    DBUtil.close(connection);
  }

  @Override
  public DBStore getStore()
  {
    return (DBStore)super.getStore();
  }

  public Connection getConnection()
  {
    return connection;
  }

  public Statement getStatement()
  {
    if (statement == null)
    {
      try
      {
        statement = getConnection().createStatement();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    return statement;
  }

  /**
   * TODO Move this somehow to DBAdapter
   */
  protected Boolean getBoolean(Object value)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof Boolean)
    {
      return (Boolean)value;
    }

    if (value instanceof Number)
    {
      return ((Number)value).intValue() != 0;
    }

    throw new IllegalArgumentException("Not a boolean value: " + value);
  }

  protected Set<IDBTable> mapPackages(CDOPackage... cdoPackages)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoPackages != null && cdoPackages.length != 0)
    {
      for (CDOPackage cdoPackage : cdoPackages)
      {
        Set<IDBTable> tables = mapClasses(cdoPackage.getClasses());
        affectedTables.addAll(tables);
      }
    }

    return affectedTables;
  }

  protected Set<IDBTable> mapClasses(CDOClass... cdoClasses)
  {
    Set<IDBTable> affectedTables = new HashSet<IDBTable>();
    if (cdoClasses != null && cdoClasses.length != 0)
    {
      IMappingStrategy mappingStrategy = getStore().getMappingStrategy();
      for (CDOClass cdoClass : cdoClasses)
      {
        IClassMapping mapping = mappingStrategy.getClassMapping(cdoClass);
        if (mapping != null)
        {
          affectedTables.addAll(mapping.getAffectedTables());
        }
      }
    }

    return affectedTables;
  }
}
