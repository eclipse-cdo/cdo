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
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IObjectTypeCache;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public class ObjectTypeCache extends Lifecycle implements IObjectTypeCache
{
  private IMappingStrategy mappingStrategy;

  private IDBTable table;

  private IDBField idField;

  private IDBField typeField;

  private transient Object initializeLock = new Object();

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
    Statement statement = accessor.getJDBCDelegate().getStatement();
    initialize(statement);

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(typeField);
    builder.append(" FROM ");
    builder.append(table);
    builder.append(" WHERE ");
    builder.append(idField);
    builder.append("=");
    builder.append(CDOIDUtil.getLong(id));
    String sql = builder.toString();
    DBUtil.trace(sql);

    ResultSet resultSet = null;

    try
    {
      resultSet = statement.executeQuery(sql);
      if (!resultSet.next())
      {
        DBUtil.trace("ClassID for CDOID " + id + " not found");
        return null;
      }

      long classID = resultSet.getLong(1);
      EClass eClass = (EClass)mappingStrategy.getStore().getMetaInstance(classID);
      return new CDOClassifierRef(eClass);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  public final void putObjectType(IDBStoreAccessor accessor, CDOID id, EClass type)
  {
    Statement statement = accessor.getJDBCDelegate().getStatement();
    initialize(statement);

    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");
    builder.append(CDOIDUtil.getLong(id));
    builder.append(", ");
    builder.append(accessor.getStore().getMetaID(type));
    builder.append(")");
    String sql = builder.toString();
    DBUtil.trace(sql);

    try
    {
      statement.execute(sql);
      if (statement.getUpdateCount() != 1)
      {
        throw new DBException("Object type not inserted: " + id + " -> " + type);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public final void removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    Statement statement = accessor.getJDBCDelegate().getStatement();
    initialize(statement);

    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM ");
    builder.append(table);
    builder.append(" WHERE ");
    builder.append(idField);
    builder.append(" = ");
    builder.append(CDOIDUtil.getLong(id));
    String sql = builder.toString();
    DBUtil.trace(sql);

    try
    {
      statement.execute(sql);
      if (statement.getUpdateCount() != 1)
      {
        throw new DBException("Object type could not be deleted: " + id);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  private void initialize(Statement statement)
  {
    synchronized (initializeLock)
    {
      if (table == null)
      {
        IDBSchema schema = mappingStrategy.getStore().getDBSchema();
        table = schema.addTable(CDODBSchema.CDO_OBJECTS);
        idField = table.addField(CDODBSchema.ATTRIBUTES_ID, DBType.BIGINT);
        typeField = table.addField(CDODBSchema.ATTRIBUTES_CLASS, DBType.BIGINT);
        table.addIndex(IDBIndex.Type.PRIMARY_KEY, idField);

        IDBAdapter dbAdapter = mappingStrategy.getStore().getDBAdapter();
        dbAdapter.createTable(table, statement);
      }
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(mappingStrategy, "mappingStrategy");
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
