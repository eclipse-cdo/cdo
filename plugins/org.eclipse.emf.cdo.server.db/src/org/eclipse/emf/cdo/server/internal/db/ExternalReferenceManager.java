/*
 * Copyright (c) 2009-2013, 2016, 2019, 2020, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Stefan Winkler - bug 249610: [DB] Support external references (Implementation)
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Stefan Winkler
 */
public class ExternalReferenceManager extends DBStoreTable
{
  private static final DBType DEFAULT_URI_COLUMN_TYPE = DBType.VARCHAR;

  private static final int DEFAULT_URI_COLUMN_LENGTH = 1024;

  private static final int NULL = 0;

  private IDBField id;

  private IDBField uri;

  private IDBField committime;

  private AtomicLong lastMappedID = new AtomicLong(NULL);

  @ExcludeFromDump
  private transient SQLStatement sqlSelectByLongID;

  @ExcludeFromDump
  private transient SQLStatement sqlSelectByURI;

  @ExcludeFromDump
  private transient SQLStatement sqlInsert;

  @ExcludeFromDump
  private transient SQLStatement sqlDeleteByURI;

  public ExternalReferenceManager(IDBStore store)
  {
    super(store, NAMES.EXTERNAL_REFS);
  }

  public long mapExternalReference(CDOIDExternal id, long commitTime)
  {
    return mapURI(accessor(), id.getURI(), commitTime);
  }

  public CDOIDExternal unmapExternalReference(long mappedId)
  {
    return CDOIDUtil.createExternal(unmapURI(accessor(), mappedId));
  }

  public long mapURI(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long result = lookupByURI(accessor, uri);
    if (result < NULL)
    {
      // mapping found
      return result;
    }

    return insertNew(accessor, uri, commitTime);
  }

  public String unmapURI(IDBStoreAccessor accessor, long mappedId)
  {
    IDBPreparedStatement stmt = DBUtil.prepare(sqlSelectByLongID, accessor.getDBConnection(), ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      sqlSelectByLongID.bindAll(stmt, mappedId);

      resultSet = stmt.executeQuery();
      if (!resultSet.next())
      {
        OM.LOG.error("External ID " + mappedId + " not found. Database inconsistent!");
        throw new IllegalStateException("External ID " + mappedId + " not found. Database inconsistent!");
      }

      return resultSet.getString(1);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  public long lookupByURI(IDBStoreAccessor accessor, String uri)
  {
    IDBPreparedStatement stmt = DBUtil.prepare(sqlSelectByURI, accessor.getDBConnection(), ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      sqlSelectByURI.bindAll(stmt, uri);

      resultSet = stmt.executeQuery();
      if (resultSet.next())
      {
        return resultSet.getLong(1);
      }

      // Not found ...
      return NULL;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  public boolean deleteByURI(Statement statement, String uriValue)
  {
    PreparedStatement deleteStatement = null;

    try
    {
      Connection connection = statement.getConnection();
      if (connection instanceof IDBConnection)
      {
        deleteStatement = DBUtil.prepare(sqlDeleteByURI, (IDBConnection)connection, ReuseProbability.LOW);
      }
      else
      {
        deleteStatement = connection.prepareStatement(sqlDeleteByURI.getSQL());
      }

      sqlDeleteByURI.bindAll(deleteStatement, uriValue);
      return deleteStatement.execute();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(deleteStatement);
    }
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    String committimeName = adapter().quoteIdentifier(committime.getName());
    String where = " WHERE " + committimeName + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, getTable(), null, where);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    DBUtil.deserializeTable(in, connection, getTable(), monitor);
  }

  @Override
  protected void firstActivate(IDBTable table)
  {
    DBType idColumnType = store().getIDHandler().getDBType();
    int idColumnLength = store().getIDColumnLength();

    Map<String, String> properties = store().getProperties();
    DBType uriColumnType = DBType.getTypeByKeyword(properties.getOrDefault("externalRefsURIColumnType", DEFAULT_URI_COLUMN_TYPE.getKeyword()));
    int uriColumnLength = Integer.parseInt(properties.getOrDefault("externalRefsURIColumnLength", Integer.toString(DEFAULT_URI_COLUMN_LENGTH)));

    id = table.addField(NAMES.ID, idColumnType, idColumnLength, true);
    uri = table.addField(NAMES.URI, uriColumnType, uriColumnLength);
    committime = table.addField(NAMES.COMMITTIME, DBType.BIGINT);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);
    table.addIndex(IDBIndex.Type.NON_UNIQUE, uri);
  }

  @Override
  protected void reActivate(IDBTable table)
  {
    id = table.getField(NAMES.ID);
    uri = table.getField(NAMES.URI);
    committime = table.getField(NAMES.COMMITTIME);

    SQLStatement sql = SQL.select(SQL.min(id)) //
        .from(id.getTable()) //
        .compile(adapter());

    IDBStoreAccessor writer = store().getWriter(null);
    IDBPreparedStatement stmt = DBUtil.prepare(sql, writer.getDBConnection(), ReuseProbability.LOW);
    ResultSet resultSet = null;

    try
    {
      resultSet = stmt.executeQuery();
      if (resultSet.next())
      {
        lastMappedID.set(resultSet.getLong(1));
      }

      // else: resultSet is empty => table is empty
      // and lastMappedId stays 0 - as initialized.
    }
    catch (SQLException ex)
    {
      DBUtil.rollbackSilently(writer.getDBConnection());
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
      writer.release();
    }
  }

  @Override
  protected void initSQL(IDBTable table)
  {
    sqlInsert = SQL.insertInto(table) //
        .columns(id, uri, committime) //
        .values(SQL.param(), SQL.param(), SQL.param()) //
        .compile(adapter());

    sqlSelectByURI = SQL.select(id) //
        .from(table) //
        .where(uri).eq(SQL.param()) //
        .compile(adapter());

    sqlSelectByLongID = SQL.select(uri) //
        .from(table) //
        .where(id).eq(SQL.param()) //
        .compile(adapter());

    sqlDeleteByURI = SQL.deleteFrom(table) //
        .where(uri).eq(SQL.param()) //
        .compile(adapter());
  }

  private long insertNew(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long newMappedID = lastMappedID.decrementAndGet();
    IDBPreparedStatement stmt = DBUtil.prepare(sqlInsert, accessor.getDBConnection(), ReuseProbability.MEDIUM);

    try
    {
      sqlInsert.bindAll(stmt, newMappedID, uri, commitTime);

      DBUtil.update(stmt, true);
      return newMappedID;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class NAMES
  {
    private static final String EXTERNAL_REFS = name("cdo_external_refs");

    private static final String ID = name("id");

    private static final String URI = name("uri");

    private static final String COMMITTIME = name("committime");

    private static String name(String name)
    {
      return DBUtil.name(name, ExternalReferenceManager.class);
    }
  }
}
