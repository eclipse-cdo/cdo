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
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.Store;
import org.eclipse.emf.cdo.protocol.model.CDOType;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.ConnectionProvider;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBSchema;
import org.eclipse.net4j.db.IDBTable;
import org.eclipse.net4j.internal.db.DBSchema;
import org.eclipse.net4j.util.ImplementationError;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DBStore extends Store implements IDBStore
{
  private static final String TYPE = "db";

  private IMappingStrategy mappingStrategy;

  private IDBAdapter dbAdapter;

  private ConnectionProvider connectionProvider;

  private IDBSchema schema;

  private int nextPackageID;

  private int nextClassID;

  private int nextFeatureID;

  public DBStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter, ConnectionProvider connectionProvider)
  {
    super(TYPE);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("dbAdapter is null");
    }

    if (connectionProvider == null)
    {
      throw new IllegalArgumentException("connectionProvider is null");
    }

    this.mappingStrategy = mappingStrategy;
    this.dbAdapter = dbAdapter;
    this.connectionProvider = connectionProvider;
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public IDBAdapter getDBAdapter()
  {
    return dbAdapter;
  }

  public ConnectionProvider getConnectionProvider()
  {
    return connectionProvider;
  }

  public IDBSchema getSchema()
  {
    if (schema == null)
    {
      schema = createSchema();
    }

    return schema;
  }

  public boolean hasAuditingSupport()
  {
    return true;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasEfficientTypeLookup()
  {
    return mappingStrategy.hasEfficientTypeLookup();
  }

  public DBStoreAccessor getReader(ISession session) throws DBException
  {
    return new DBStoreAccessor(this, session);
  }

  public DBStoreAccessor getWriter(IView view) throws DBException
  {
    return new DBStoreAccessor(this, view);
  }

  public int getNextPackageID()
  {
    return nextPackageID++;
  }

  public int getNextClassID()
  {
    return nextClassID++;
  }

  public int getNextFeatureID()
  {
    return nextFeatureID++;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    activateOrDeactivate(true);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    activateOrDeactivate(false);
    super.doDeactivate();
  }

  protected void activateOrDeactivate(boolean started)
  {
    Repository repository = (Repository)getRepository();
    Connection connection = connectionProvider.getConnection();

    try
    {
      if (started)
      {
        activateStore(repository, connection);
      }
      else
      {
        deactivateStore(repository, connection);
      }
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  protected void activateStore(Repository repository, Connection connection)
  {
    Set<IDBTable> createdTables = CDODBSchema.INSTANCE.create(dbAdapter, connectionProvider);
    if (createdTables.contains(CDODBSchema.REPOSITORY))
    {
      // First start
      DBUtil.insertRow(connection, dbAdapter, CDODBSchema.REPOSITORY, repository.getName(), repository.getUUID(), 1,
          System.currentTimeMillis(), 0, 0, 0);

      MappingStrategy mappingStrategy = (MappingStrategy)getMappingStrategy();
      IClassMapping resourceClassMapping = mappingStrategy.getResourceClassMapping();
      Set<IDBTable> tables = resourceClassMapping.getAffectedTables();
      if (dbAdapter.createTables(tables, connection).size() != tables.size())
      {
        throw new DBException("CDOResource tables not completely created");
      }
    }
    else
    {
      // Restart
      repository.setNextOIDValue(DBUtil.selectMaximum(connection, CDODBSchema.REPOSITORY_NEXT_CDOID));
      repository.setNextMetaIDValue(DBUtil.selectMaximum(connection, CDODBSchema.REPOSITORY_NEXT_METAID));

      StringBuilder builder = new StringBuilder();
      builder.append("UPDATE ");
      builder.append(CDODBSchema.REPOSITORY);
      builder.append(" SET ");
      builder.append(CDODBSchema.REPOSITORY_STARTS.getName());
      builder.append("=");
      builder.append(CDODBSchema.REPOSITORY_STARTS.getName());
      builder.append("+1, ");
      builder.append(CDODBSchema.REPOSITORY_STARTED.getName());
      builder.append("=");
      builder.append(System.currentTimeMillis());
      builder.append(", ");
      builder.append(CDODBSchema.REPOSITORY_STOPPED.getName());
      builder.append("=0, ");
      builder.append(CDODBSchema.REPOSITORY_NEXT_CDOID.getName());
      builder.append("=0, ");
      builder.append(CDODBSchema.REPOSITORY_NEXT_METAID.getName());
      builder.append("=0");

      String sql = builder.toString();
      int count = DBUtil.update(connection, sql);
      if (count == 0)
      {
        throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY);
      }
    }

    nextPackageID = DBUtil.selectMaximum(connection, CDODBSchema.PACKAGES_ID) + 1;
    nextClassID = DBUtil.selectMaximum(connection, CDODBSchema.CLASSES_ID) + 1;
    nextFeatureID = DBUtil.selectMaximum(connection, CDODBSchema.FEATURES_ID) + 1;
  }

  protected void deactivateStore(Repository repository, Connection connection)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(CDODBSchema.REPOSITORY);
    builder.append(" SET ");
    builder.append(CDODBSchema.REPOSITORY_STOPPED.getName());
    builder.append("=");
    builder.append(System.currentTimeMillis());
    builder.append(", ");
    builder.append(CDODBSchema.REPOSITORY_NEXT_CDOID.getName());
    builder.append("=");
    builder.append(repository.getNextOIDValue());
    builder.append(", ");
    builder.append(CDODBSchema.REPOSITORY_NEXT_METAID.getName());
    builder.append("=");
    builder.append(repository.getNextMetaIDValue());

    String sql = builder.toString();
    int count = DBUtil.update(connection, sql);
    if (count == 0)
    {
      throw new DBException("No row updated in table " + CDODBSchema.REPOSITORY);
    }
  }

  protected IDBSchema createSchema()
  {
    String name = getRepository().getName();
    return new DBSchema(name);
  }

  public static DBType getDBType(CDOType type)
  {
    if (type == CDOType.BOOLEAN || type == CDOType.BOOLEAN_OBJECT)
    {
      return DBType.BOOLEAN;
    }
    else if (type == CDOType.BYTE || type == CDOType.BYTE_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.CHAR || type == CDOType.CHARACTER_OBJECT)
    {
      return DBType.CHAR;
    }
    else if (type == CDOType.DATE)
    {
      return DBType.DATE;
    }
    else if (type == CDOType.DOUBLE || type == CDOType.DOUBLE_OBJECT)
    {
      return DBType.DOUBLE;
    }
    else if (type == CDOType.FLOAT || type == CDOType.FLOAT_OBJECT)
    {
      return DBType.FLOAT;
    }
    else if (type == CDOType.INT || type == CDOType.INTEGER_OBJECT)
    {
      return DBType.INTEGER;
    }
    else if (type == CDOType.LONG || type == CDOType.LONG_OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.OBJECT)
    {
      return DBType.BIGINT;
    }
    else if (type == CDOType.SHORT || type == CDOType.SHORT_OBJECT)
    {
      return DBType.SMALLINT;
    }
    else if (type == CDOType.STRING)
    {
      return DBType.VARCHAR;
    }

    throw new ImplementationError("Unrecognized CDOType: " + type);
  }
}
