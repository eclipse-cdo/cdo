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
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.ServerInfo;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ref.ReferenceValueMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Stefan Winkler
 * @since 2.0
 */
public class PreparedStatementJDBCDelegate extends AbstractJDBCDelegate
{
  /**
   * Value for {@link #cachingFlag}: Guess if caching is needed
   */
  public static final String CACHE_STMTS_GUESS = "guess";

  /**
   * Value for {@link #cachingFlag}: Turn caching on
   */
  public static final String CACHE_STMTS_TRUE = "true";

  /**
   * Value for {@link #cachingFlag}: Turn caching off
   */
  public static final String CACHE_STMTS_FALSE = "false";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, PreparedStatementJDBCDelegate.class);

  private static final String SQL_UPDATE_REVISE_VERSION = " SET " + CDODBSchema.ATTRIBUTES_REVISED + " = ? WHERE "
      + CDODBSchema.ATTRIBUTES_ID + " = ? AND " + CDODBSchema.ATTRIBUTES_VERSION + " = ?";

  private static final String SQL_UPDATE_REVISE_UNREVISED = " SET " + CDODBSchema.ATTRIBUTES_REVISED + " = ? WHERE "
      + CDODBSchema.ATTRIBUTES_ID + " = ? AND " + CDODBSchema.ATTRIBUTES_REVISED + " = 0";

  private static final String SQL_INSERT_REFERENCE_WITH_DBID = " VALUES (?, ?, ?, ?, ?)";

  private static final String SQL_INSERT_REFERENCE = " VALUES (?, ?, ?, ?)";

  /**
   * Cache for preparedStatements used in diverse methods
   */
  private Map<CacheKey, WrappedPreparedStatement> statementCache = null;

  /**
   * Container for dirty statements. A statement is considered 'dirty', if addBatch was called, but not executeBatch.
   */
  private Map<CacheKey, PreparedStatement> dirtyStatements = null;

  /**
   * This flag determines, if prepared statements should be cached within this delegate. Its value is guessed in
   * {@link #postInitConnection()} based on the fact if the JDBC driver supports pooled statements. If it does, caching
   * in the delegate is unnecessary.
   */
  private boolean cacheStatements;

  private CachingEnablement cachingEnablement;

  public PreparedStatementJDBCDelegate()
  {
  }

  public CachingEnablement getCachingEnablement()
  {
    return cachingEnablement;
  }

  public void setCachingEnablement(CachingEnablement cachingEnablement)
  {
    checkInactive();
    this.cachingEnablement = cachingEnablement;
  }

  public void flush(OMMonitor monitor)
  {
    try
    {
      monitor.begin(dirtyStatements.size());
      for (Entry<CacheKey, PreparedStatement> entry : dirtyStatements.entrySet())
      {
        try
        {
          int[] results;
          Async async = monitor.forkAsync();

          try
          {
            results = entry.getValue().executeBatch();
          }
          finally
          {
            async.stop();
          }

          if (TRACER.isEnabled())
          {
            TRACER.format("Executing batch for {0} [{1}]", entry.getKey().toString(), entry.getValue());
          }

          for (int result : results)
          {
            checkState(result == 1, "Batch execution did not return '1' for " + entry.getKey().toString());
          }
        }
        catch (SQLException ex)
        {
          throw new DBException("Batch execution failed for " + entry.getKey().toString() + " [" + entry.getValue()
              + "]", ex);
        }
      }
    }
    finally
    {
      if (!cacheStatements)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Closing prepared statements.");
        }

        for (PreparedStatement ps : dirtyStatements.values())
        {
          DBUtil.close(ps);
        }
      }
      else
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Re-caching prepared statements.");
        }

        for (Entry<CacheKey, PreparedStatement> entry : dirtyStatements.entrySet())
        {
          cacheStatement(entry.getKey(), entry.getValue());
        }
      }

      dirtyStatements.clear();
      monitor.done();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    dirtyStatements = new ReferenceValueMap.Strong<CacheKey, PreparedStatement>();

    switch (cachingEnablement)
    {
    case ENABLED:
      cacheStatements = true;
      break;

    case DISABLED:
      cacheStatements = false;
      break;

    case GUESS:
      try
      {
        cacheStatements = !getConnection().getMetaData().supportsStatementPooling();
      }
      catch (SQLException ex)
      {
        OM.LOG.warn("Failed to guess JDBC statement pooling. Activating cache, just to be sure ...", ex);
        cacheStatements = true;
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("JDBC PreparedStatement caching is " + (cacheStatements ? "enabled." : "NOT enabled."));
    }

    if (cacheStatements)
    {
      // initialize cache ...
      statementCache = new ReferenceValueMap.Soft<CacheKey, WrappedPreparedStatement>();
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    for (PreparedStatement ps : dirtyStatements.values())
    {
      DBUtil.close(ps);
    }

    dirtyStatements.clear();
    if (cacheStatements)
    {
      for (WrappedPreparedStatement ps : statementCache.values())
      {
        DBUtil.close(ps.getWrappedStatement());
      }

      statementCache.clear();
    }

    super.doBeforeDeactivate();
  }

  /**
   * Implementation of the hook which is called after selects.
   */
  @Override
  protected void releaseStatement(Statement stmt)
  {
    // leave open cached statements
    if (!cacheStatements || !(stmt instanceof PreparedStatement))
    {
      super.releaseStatement(stmt);
    }

    // /* This code would guarantee that releaseStatement is only called
    // for cached statements. However this looks through the whole hashmap
    // and is thus too expensive to do in non-debugging mode. */
    //
    // else {
    // if(!selectStatementsCache.containsValue(stmt)) {
    // super.releaseStatement(stmt);
    // }
    // }
  }

  @Override
  protected void doInsertAttributes(String tableName, CDORevision rev, List<IAttributeMapping> attributeMappings,
      boolean withFullRevisionInfo)
  {
    boolean firstBatch = false;

    InternalCDORevision revision = (InternalCDORevision)rev;
    if (attributeMappings == null)
    {
      attributeMappings = Collections.emptyList();
    }

    PreparedStatement stmt = getDirtyStatement(StmtType.INSERT_ATTRIBUTES, tableName);
    if (stmt == null && cacheStatements)
    {
      firstBatch = true;
      stmt = getAndRemoveCachedStatement(StmtType.INSERT_ATTRIBUTES, tableName);
    }

    try
    {
      firstBatch = true;
      if (stmt == null)
      {
        StringBuilder sql = new StringBuilder();

        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" VALUES (?, ?, ");
        if (withFullRevisionInfo)
        {
          sql.append("?, ?, ?, ?, ?, ?");
        }

        for (int i = 0; i < attributeMappings.size(); i++)
        {
          sql.append(", ?");
        }

        sql.append(")");
        stmt = getConnection().prepareStatement(sql.toString());
      }

      int col = 1;
      if (TRACER.isEnabled())
      {
        TRACER.trace(stmt.toString());
      }

      stmt.setLong(col++, CDOIDUtil.getLong(revision.getID()));
      stmt.setInt(col++, revision.getVersion());
      if (withFullRevisionInfo)
      {
        stmt.setInt(col++, ServerInfo.getDBID(revision.getCDOClass()));
        stmt.setLong(col++, revision.getCreated());
        stmt.setLong(col++, revision.getRevised());
        stmt.setLong(col++, CDOIDUtil.getLong(revision.getResourceID()));
        stmt.setLong(col++, CDOIDUtil.getLong((CDOID)revision.getContainerID()));
        stmt.setInt(col++, revision.getContainingFeatureID());
      }

      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        Object value = attributeMapping.getRevisionValue(revision);

        if (value == null)
        {
          stmt.setNull(col++, attributeMapping.getField().getType().getCode());
        }
        else
        {
          stmt.setObject(col++, value);
        }
      }

      if (firstBatch)
      {
        addDirtyStatement(StmtType.INSERT_ATTRIBUTES, tableName, stmt);
      }

      stmt.addBatch();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
  }

  @Override
  protected void doInsertReference(String tableName, int dbID, long source, int version, int index, long target)
  {
    boolean firstBatch = false;

    PreparedStatement stmt = getDirtyStatement(StmtType.INSERT_REFERENCES, tableName);
    if (stmt == null && cacheStatements)
    {
      firstBatch = true;
      stmt = getAndRemoveCachedStatement(StmtType.INSERT_REFERENCES, tableName);
    }

    try
    {
      if (stmt == null)
      {
        firstBatch = true;
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName);
        sql.append(dbID != 0 ? SQL_INSERT_REFERENCE_WITH_DBID : SQL_INSERT_REFERENCE);
        stmt = getConnection().prepareStatement(sql.toString());
      }

      int idx = 1;
      if (dbID != 0)
      {
        stmt.setInt(idx++, dbID);
      }

      stmt.setLong(idx++, source);
      stmt.setInt(idx++, version);
      stmt.setInt(idx++, index);
      stmt.setLong(idx++, target);
      if (TRACER.isEnabled())
      {
        TRACER.trace(stmt.toString());
      }

      if (firstBatch)
      {
        addDirtyStatement(StmtType.INSERT_REFERENCES, tableName, stmt);
      }

      stmt.addBatch();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
  }

  @Override
  protected void doUpdateRevised(String tableName, long revisedStamp, long cdoid, int version)
  {
    PreparedStatement stmt = null;
    if (cacheStatements)
    {
      stmt = getCachedStatement(StmtType.REVISE_VERSION, tableName);
    }

    try
    {
      if (stmt == null)
      {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName);
        sql.append(SQL_UPDATE_REVISE_VERSION);
        stmt = getConnection().prepareStatement(sql.toString());

        if (cacheStatements)
        {
          cacheStatement(StmtType.REVISE_VERSION, tableName, stmt);
        }
      }

      stmt.setLong(1, revisedStamp);
      stmt.setLong(2, cdoid);
      stmt.setInt(3, version);
      if (TRACER.isEnabled())
      {
        TRACER.trace(stmt.toString());
      }

      stmt.execute();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      if (!cacheStatements)
      {
        DBUtil.close(stmt);
      }
    }
  }

  @Override
  protected void doUpdateRevised(String tableName, long revisedStamp, long cdoid)
  {
    PreparedStatement stmt = null;
    if (cacheStatements)
    {
      stmt = getCachedStatement(StmtType.REVISE_UNREVISED, tableName);
    }

    try
    {
      if (stmt == null)
      {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName);
        sql.append(SQL_UPDATE_REVISE_UNREVISED);
        stmt = getConnection().prepareStatement(sql.toString());

        if (cacheStatements)
        {
          cacheStatement(StmtType.REVISE_UNREVISED, tableName, stmt);
        }
      }

      stmt.setLong(1, revisedStamp);
      stmt.setLong(2, cdoid);
      if (TRACER.isEnabled())
      {
        TRACER.trace(stmt.toString());
      }

      stmt.execute();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      if (!cacheStatements)
      {
        DBUtil.close(stmt);
      }
    }
  }

  @Override
  protected ResultSet doSelectRevisionAttributes(String tableName, long revisionId,
      List<IAttributeMapping> attributeMappings, boolean hasFullRevisionInfo, String where) throws SQLException
  {
    // Because of the variable where clause, statement caching can not be
    // based on table names. Instead, we build the sql in any case and
    // use this as key (similar to what JDBC3 does ...).

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);

    if (hasFullRevisionInfo)
    {
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    }

    for (IAttributeMapping attributeMapping : attributeMappings)
    {
      builder.append(", ");
      builder.append(attributeMapping.getField());
    }

    builder.append(" FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("= ? AND (");
    builder.append(where);
    builder.append(")");
    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.format("{0} ({1})", sql, revisionId);
    }

    PreparedStatement pstmt = null;
    if (cacheStatements)
    {
      pstmt = getCachedStatement(StmtType.GENERAL, sql);
      if (pstmt == null)
      {
        pstmt = getConnection().prepareStatement(sql);
        cacheStatement(StmtType.GENERAL, sql, pstmt);
      }
    }
    else
    {
      /* no caching */
      pstmt = getConnection().prepareStatement(sql);
    }

    pstmt.setLong(1, revisionId);
    return pstmt.executeQuery();
  }

  @Override
  protected ResultSet doSelectRevisionReferences(String tableName, long sourceId, int version, int dbFeatureID,
      String where) throws SQLException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.REFERENCES_TARGET);
    builder.append(" FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");
    if (dbFeatureID != 0)
    {
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append("= ? AND ");
    }

    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append("= ? AND ");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append("= ? ");
    if (where != null)
    {
      builder.append(where);
    }

    builder.append(" ORDER BY ");
    builder.append(CDODBSchema.REFERENCES_IDX);

    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    PreparedStatement pstmt = null;
    if (cacheStatements)
    {
      pstmt = getCachedStatement(StmtType.GENERAL, sql);
      if (pstmt == null)
      {
        pstmt = getConnection().prepareStatement(sql);
        cacheStatement(StmtType.GENERAL, sql, pstmt);
      }
    }
    else
    {
      /* no caching */
      pstmt = getConnection().prepareStatement(sql);
    }

    int idx = 1;
    if (dbFeatureID != 0)
    {
      pstmt.setInt(idx++, dbFeatureID);
    }

    pstmt.setLong(idx++, sourceId);
    pstmt.setInt(idx++, version);
    return pstmt.executeQuery();
  }

  /**
   * Add a dirty statement to the dirty statements container.
   */
  private void addDirtyStatement(StmtType type, String subKey, PreparedStatement stmt)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding dirty statement: ({0},{1}) -> {2}", type, subKey, stmt);
    }

    dirtyStatements.put(new CacheKey(type, subKey), stmt);
  }

  /**
   * Query the dirty statements container.
   * 
   * @return
   */
  private PreparedStatement getDirtyStatement(StmtType type, String subKey)
  {
    return dirtyStatements.get(CacheKey.useOnce(type, subKey));
  }

  /**
   * Cache a prepared statement.
   */
  private void cacheStatement(StmtType type, String subKey, PreparedStatement stmt)
  {
    cacheStatement(new CacheKey(type, subKey), stmt);
  }

  /**
   * Cache a prepared statement with given key.
   */
  private void cacheStatement(CacheKey key, PreparedStatement stmt)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Adding cached statement: {0} -> {1}", key, stmt);
    }

    statementCache.put(key, new WrappedPreparedStatement(stmt));
  }

  /**
   * Query the cache of prepared statements.
   */
  private PreparedStatement getCachedStatement(StmtType type, String subKey)
  {
    WrappedPreparedStatement wrapped = statementCache.get(CacheKey.useOnce(type, subKey));
    if (wrapped == null)
    {
      return null;
    }

    PreparedStatement stmt = wrapped.getWrappedStatement();
    if (TRACER.isEnabled())
    {
      TRACER.format("Using cached statement: ({0},{1}) -> {2}", type, subKey, stmt);
    }

    return stmt;
  }

  private PreparedStatement getAndRemoveCachedStatement(StmtType type, String subKey)
  {
    WrappedPreparedStatement wrapped = statementCache.remove(CacheKey.useOnce(type, subKey));
    if (wrapped == null)
    {
      return null;
    }

    PreparedStatement stmt = wrapped.unwrapStatement();
    if (TRACER.isEnabled())
    {
      TRACER.format("Removing cached statement: ({0},{1}) -> {2}", type, subKey, stmt);
    }

    return stmt;
  }

  /**
   * @author Stefan Winkler
   */
  public static enum CachingEnablement
  {
    ENABLED, DISABLED, GUESS
  }

  /**
   * Statement type as first part of the statement cache key.
   * 
   * @author Stefan Winkler
   */
  private static enum StmtType
  {
    INSERT_ATTRIBUTES, INSERT_REFERENCES, REVISE_VERSION, REVISE_UNREVISED, GENERAL
  }

  /**
   * Convenience definition for Pair<StmtType, String>
   * 
   * @author Stefan Winkler
   */
  private static class CacheKey extends Pair<StmtType, String>
  {
    public CacheKey(StmtType type, String subKey)
    {
      super(type, subKey);
    }

    private static CacheKey useOnceKey = new CacheKey(StmtType.GENERAL, "");

    /**
     * Memory-resource-friendly method which uses a single static field, modifies it and returns it to be used at once,
     * e.g., to use it in a cache lookup. Do not store a reference to the result!!!
     */
    public static CacheKey useOnce(StmtType type, String subKey)
    {
      useOnceKey.setElement1(type);
      useOnceKey.setElement2(subKey);
      return useOnceKey;
    }
  }
}
