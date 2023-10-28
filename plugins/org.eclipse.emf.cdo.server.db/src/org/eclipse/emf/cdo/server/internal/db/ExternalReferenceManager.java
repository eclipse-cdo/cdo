/*
 * Copyright (c) 2009-2013, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Stefan Winkler
 */
/**
 * @author Eike Stepper
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
  private transient String sqlSelectByLongID;

  @ExcludeFromDump
  private transient String sqlSelectByURI;

  @ExcludeFromDump
  private transient String sqlInsert;

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
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelectByLongID, ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      stmt.setLong(1, mappedId);

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
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelectByURI, ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      stmt.setString(1, uri);

      resultSet = stmt.executeQuery();
      if (resultSet.next())
      {
        return resultSet.getLong(1);
      }

      // Not found ...
      return NULL;
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

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    String where = " WHERE " + committime + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, table(), null, where);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    DBUtil.deserializeTable(in, connection, table(), monitor);
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

    String sql = "SELECT MIN(" + id + ") FROM " + table;

    IDBStoreAccessor writer = store().getWriter(null);
    IDBPreparedStatement stmt = writer.getDBConnection().prepareStatement(sql, ReuseProbability.LOW);
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
    sqlInsert = "INSERT INTO " + table + "(" + id + "," + uri + "," + committime + ") VALUES (?, ?, ?)";
    sqlSelectByURI = "SELECT " + id + " FROM " + table + " WHERE " + uri + "=?";
    sqlSelectByLongID = "SELECT " + uri + " FROM " + table + " WHERE " + id + "=?";
  }

  private long insertNew(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long newMappedID = lastMappedID.decrementAndGet();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsert, ReuseProbability.MEDIUM);

    try
    {
      stmt.setLong(1, newMappedID);
      stmt.setString(2, uri);
      stmt.setLong(3, commitTime);

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
