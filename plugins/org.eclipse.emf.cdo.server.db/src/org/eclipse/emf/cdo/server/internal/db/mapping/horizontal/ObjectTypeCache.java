/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 *    Stefan Winkler - redesign (prepared statements)
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.IObjectTypeCache;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class ObjectTypeCache extends Lifecycle implements IObjectTypeCache
{
  private IMappingStrategy mappingStrategy;

  private IDBTable table;

  private IDBField idField;

  private IDBField typeField;

  private transient Object initializeLock = new Object();

  private String sqlDelete;

  private String sqlInsert;

  private String sqlSelect;

  private IMetaDataManager metaDataManager;

  public ObjectTypeCache()
  {
  }

  public IMappingStrategy getMappingStrategy()
  {
    return mappingStrategy;
  }

  public void setMappingStrategy(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
  }

  public final CDOClassifierRef getObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    Connection connection = accessor.getConnection();
    initialize(connection);

    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlSelect, ReuseProbability.MAX);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      DBUtil.trace(stmt.toString());
      ResultSet resultSet = stmt.executeQuery();

      if (!resultSet.next())
      {
        DBUtil.trace("ClassID for CDOID " + id + " not found");
        return null;
      }

      long classID = resultSet.getLong(1);
      EClass eClass = (EClass)metaDataManager.getMetaInstance(classID);
      return new CDOClassifierRef(eClass);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  public final void putObjectType(IDBStoreAccessor accessor, CDOID id, EClass type)
  {
    Connection connection = accessor.getConnection();
    initialize(connection);

    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlInsert, ReuseProbability.MAX);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      stmt.setLong(2, metaDataManager.getMetaID(type));
      DBUtil.trace(stmt.toString());
      int result = stmt.executeUpdate();

      if (result != 1)
      {
        throw new DBException("Object type could not be deleted: " + id);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  public final void removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    Connection connection = accessor.getConnection();
    initialize(connection);

    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlDelete, ReuseProbability.MAX);
      stmt.setLong(1, CDOIDUtil.getLong(id));
      DBUtil.trace(stmt.toString());
      int result = stmt.executeUpdate();

      if (result != 1)
      {
        throw new DBException("Object type could not be deleted: " + id);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  private void initialize(Connection connection)
  {
    // TODO - is there a better way to initialize this
    // e.g. doActivate() - only problem there is to get hold of a statement ....
    synchronized (initializeLock)
    {
      if (table == null)
      {
        IDBSchema schema = mappingStrategy.getStore().getDBSchema();
        table = schema.addTable(CDODBSchema.CDO_OBJECTS);
        idField = table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT);
        typeField = table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.BIGINT);
        table.addIndex(IDBIndex.Type.UNIQUE, idField);

        IDBAdapter dbAdapter = mappingStrategy.getStore().getDBAdapter();

        Statement statement = null;
        try
        {
          statement = connection.createStatement();
          dbAdapter.createTable(table, statement);
        }
        catch (SQLException ex)
        {
          throw new DBException(ex);
        }
        finally
        {
          DBUtil.close(statement);
        }
      }

      sqlSelect = "SELECT " + typeField.getName() + " FROM " + table.getName() + " WHERE " + idField.getName() + " = ?";

      sqlInsert = "INSERT INTO " + table.getName() + " VALUES (?,?)";

      sqlDelete = "DELETE FROM " + table.getName() + " WHERE " + idField.getName() + " = ?";
    }
  }

  public long getMaxId(Connection connection)
  {
    initialize(connection);
    return DBUtil.selectMaximumLong(connection, idField);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(mappingStrategy, "mappingStrategy");
  }

  @Override
  protected void doActivate() throws Exception
  {
    metaDataManager = getMappingStrategy().getStore().getMetaDataManager();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    table = null;
    idField = null;
    typeField = null;
    super.doDeactivate();
  }

}
