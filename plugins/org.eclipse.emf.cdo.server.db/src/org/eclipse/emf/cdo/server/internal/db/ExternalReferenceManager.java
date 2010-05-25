/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Eike Stepper - maintenance
 *    Victor Roldan - 289237: [DB] [maintenance] Support external references
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
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

  private AtomicLong lastMappedId = new AtomicLong(0);

  @ExcludeFromDump
  private transient String sqlSelectByLongId;

  @ExcludeFromDump
  private transient String sqlSelectByHash;

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

  public long mapExternalReference(IDBStoreAccessor accessor, CDOIDExternal id)
  {
    String uri = id.getURI();
    long result = lookup(accessor, uri);
    if (result < 0)
    {
      // mapping found
      return result;
    }

    return insertNew(accessor, uri);
  }

  public CDOIDExternal unmapExternalReference(IDBStoreAccessor accessor, long mappedId)
  {
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlSelectByLongId, ReuseProbability.HIGH);
      stmt.setLong(1, mappedId);
      rs = stmt.executeQuery();

      if (!rs.next())
      {
        OM.LOG.error("External ID " + mappedId + " not found. Database inconsistent!");
        throw new IllegalStateException("External ID " + mappedId + " not found. Database inconsistent!");
      }

      String uri = rs.getString(1);
      return CDOIDUtil.createExternal(uri);
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
        lastMappedId.set(result.getLong(1));
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

    sqlInsert = "INSERT INTO " + CDODBSchema.EXTERNAL_REFS + " VALUES (?,?,?)"; //$NON-NLS-1$ //$NON-NLS-2$
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(CDODBSchema.EXTERNAL_REFS);
    builder.append("(");
    builder.append(CDODBSchema.EXTERNAL_ID);
    builder.append(",");
    builder.append(CDODBSchema.EXTERNAL_HASHCODE);
    builder.append(",");
    builder.append(CDODBSchema.EXTERNAL_URI);
    builder.append(") VALUES (?,?,?)");

    sqlInsert = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_URI);
    builder.append(","); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_ID);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_REFS);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_HASHCODE);
    builder.append(" =? "); //$NON-NLS-1$

    sqlSelectByHash = builder.toString();

    builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_URI);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_REFS);
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(CDODBSchema.EXTERNAL_ID);
    builder.append(" =? "); //$NON-NLS-1$

    sqlSelectByLongId = builder.toString();
  }

  private long insertNew(IDBStoreAccessor accessor, String uri)
  {
    long newMappedId = lastMappedId.decrementAndGet();

    PreparedStatement stmt = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlInsert, ReuseProbability.MEDIUM);
      stmt.setLong(1, newMappedId);
      stmt.setInt(2, uri.hashCode());
      stmt.setString(3, uri);

      CDODBUtil.sqlUpdate(stmt, true);
      return newMappedId;
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

  private long lookup(IDBStoreAccessor accessor, String uri)
  {
    int hash = uri.hashCode();
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = accessor.getStatementCache().getPreparedStatement(sqlSelectByHash, ReuseProbability.HIGH);
      stmt.setInt(1, hash);
      rs = stmt.executeQuery();

      while (rs.next())
      {
        if (rs.getString(1).equals(uri))
        {
          return rs.getLong(2);
        }
      }

      // not found ...
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
