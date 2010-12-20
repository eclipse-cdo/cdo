/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IExternalReferenceManager;
import org.eclipse.emf.cdo.server.db.IPreparedStatementCache.ReuseProbability;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Stefan Winkler
 */
public class ExternalReferenceManager extends Lifecycle implements IExternalReferenceManager.Internal
{
  private IDBStore store;

  private AtomicLong lastMappedID = new AtomicLong(0);

  @ExcludeFromDump
  private transient String sqlSelectByLongID;

  @ExcludeFromDump
  private transient String sqlSelectByURI;

  @ExcludeFromDump
  private transient String sqlInsert;

  public ExternalReferenceManager()
  {
  }

  public IDBStore getStore()
  {
    return store;
  }

  public void setStore(IDBStore store)
  {
    this.store = store;
  }

  public long mapExternalReference(IDBStoreAccessor accessor, CDOIDExternal id, long commitTime)
  {
    return mapURI(accessor, id.getURI(), commitTime);
  }

  public CDOIDExternal unmapExternalReference(IDBStoreAccessor accessor, long mappedId)
  {
    return CDOIDUtil.createExternal(unmapURI(accessor, mappedId));
  }

  public long mapURI(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long result = lookupByID(accessor, uri);
    if (result < DBStore.NULL)
    {
      // mapping found
      return result;
    }
  
    return insertNew(accessor, uri, commitTime);
  }

  public String unmapURI(IDBStoreAccessor accessor, long mappedId)
  {
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlSelectByLongID, ReuseProbability.HIGH);
      stmt.setLong(1, mappedId);
      rs = stmt.executeQuery();

      if (!rs.next())
      {
        OM.LOG.error("External ID " + mappedId + " not found. Database inconsistent!");
        throw new IllegalStateException("External ID " + mappedId + " not found. Database inconsistent!");
      }

      String uri = rs.getString(1);
      return uri;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(rs);
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(store, "Store is not set");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    IDBStoreAccessor reader = getStore().getReader(null);
    Connection connection = reader.getConnection();
    Statement statement = null;

    try
    {
      String sql = "SELECT MIN(" + CDODBSchema.EXTERNAL_ID + ") FROM " + CDODBSchema.EXTERNAL_REFS;

      statement = connection.createStatement();
      ResultSet result = statement.executeQuery(sql);

      if (result.next())
      {
        lastMappedID.set(result.getLong(1));
      }

      // else: resultSet is empty => table is empty
      // and lastMappedId stays 0 - as initialized.
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(statement);
      LifecycleUtil.deactivate(reader); // Don't let the null-context accessor go to the pool!
    }

    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(CDODBSchema.EXTERNAL_REFS);
    builder.append("(");
    builder.append(CDODBSchema.EXTERNAL_ID);
    builder.append(",");
    builder.append(CDODBSchema.EXTERNAL_URI);
    builder.append(",");
    builder.append(CDODBSchema.EXTERNAL_TIMESTAMP);
    builder.append(") VALUES (?, ?, ?)");
    sqlInsert = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_REFS);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_URI);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectByURI = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_URI);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_REFS);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_ID);
    builder.append("=?"); //$NON-NLS-1$
    sqlSelectByLongID = builder.toString();
  }

  private long insertNew(IDBStoreAccessor accessor, String uri, long commitTime)
  {
    long newMappedID = lastMappedID.decrementAndGet();
    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlInsert, ReuseProbability.MEDIUM);
      stmt.setLong(1, newMappedID);
      stmt.setString(2, uri);
      stmt.setLong(3, commitTime);

      CDODBUtil.sqlUpdate(stmt, true);
      return newMappedID;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }

  private long lookupByID(IDBStoreAccessor accessor, String uri)
  {
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlSelectByURI, ReuseProbability.HIGH);
      stmt.setString(1, uri);

      rs = stmt.executeQuery();

      if (rs.next())
      {
        return rs.getLong(1);
      }

      // Not found ...
      return 0;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(rs);
      accessor.getStatementCache().releasePreparedStatement(stmt);
    }
  }
}
