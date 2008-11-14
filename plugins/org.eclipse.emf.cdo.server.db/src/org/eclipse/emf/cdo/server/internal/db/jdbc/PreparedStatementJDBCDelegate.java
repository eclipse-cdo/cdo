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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author Stefan WInkler
 * @since 2.0
 */
public class PreparedStatementJDBCDelegate extends AbstractJDBCDelegate
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, PreparedStatementJDBCDelegate.class);

  private static final String SQL_UPDATE_REVISE_VERSION = " SET " + CDODBSchema.ATTRIBUTES_REVISED + " = ? WHERE "
      + CDODBSchema.ATTRIBUTES_ID + " = ? AND " + CDODBSchema.ATTRIBUTES_VERSION + " = ?";

  private static final String SQL_UPDATE_REVISE_UNREVISED = " SET " + CDODBSchema.ATTRIBUTES_REVISED + " = ? WHERE "
      + CDODBSchema.ATTRIBUTES_ID + " = ? AND " + CDODBSchema.ATTRIBUTES_REVISED + " = 0";

  private static final String SQL_INSERT_REFERENCE_WITH_DBID = " VALUES (?, ?, ?, ?, ?)";

  private static final String SQL_INSERT_REFERENCE = " VALUES (?, ?, ?, ?)";

  public PreparedStatementJDBCDelegate()
  {
  }

  @Override
  protected void doInsertAttributes(String tableName, CDORevision rev, List<IAttributeMapping> attributeMappings,
      boolean withFullRevisionInfo)
  {
    PreparedStatement stmt = null;
    StringBuilder sql = new StringBuilder();
    InternalCDORevision revision = (InternalCDORevision)rev;

    if (attributeMappings == null)
    {
      attributeMappings = Collections.emptyList();
    }

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
    int col = 1;

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("{0} ({1})", sql.toString(), revision.toString());
      }

      stmt = getConnection().prepareStatement(sql.toString());
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
        stmt.setObject(col++, value);
      }

      stmt.execute();
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

  @Override
  protected void doInsertReference(String tableName, int dbID, long source, int version, int index, long target)
  {
    PreparedStatement stmt = null;

    try
    {
      StringBuilder sql = new StringBuilder("INSERT INTO ");
      sql.append(tableName);
      sql.append(dbID != 0 ? SQL_INSERT_REFERENCE_WITH_DBID : SQL_INSERT_REFERENCE);

      int idx = 1;
      stmt = getConnection().prepareStatement(sql.toString());
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
        TRACER.format("{0} ({1},{2},{3},{4},{5})", sql.toString(), dbID, source, version, index, target);
      }

      stmt.execute();
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

  @Override
  protected void doUpdateRevised(String tableName, long revisedStamp, long cdoid, int version)
  {
    PreparedStatement stmt = null;

    try
    {
      StringBuilder sql = new StringBuilder("UPDATE ");
      sql.append(tableName);
      sql.append(SQL_UPDATE_REVISE_VERSION);

      stmt = getConnection().prepareStatement(sql.toString());
      stmt.setLong(1, revisedStamp);
      stmt.setLong(2, cdoid);
      stmt.setInt(3, version);
      if (TRACER.isEnabled())
      {
        TRACER.format("{0} ({1},{2},{3})", sql.toString(), revisedStamp, cdoid, version);
      }

      stmt.execute();
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

  @Override
  protected void doUpdateRevised(String tableName, long revisedStamp, long cdoid)
  {
    PreparedStatement stmt = null;

    try
    {
      StringBuilder sql = new StringBuilder("UPDATE ");
      sql.append(tableName);
      sql.append(SQL_UPDATE_REVISE_UNREVISED);

      stmt = getConnection().prepareStatement(sql.toString());
      stmt.setLong(1, revisedStamp);
      stmt.setLong(2, cdoid);
      if (TRACER.isEnabled())
      {
        TRACER.format("{0} ({1},{2})", sql.toString(), revisedStamp, cdoid);
      }

      stmt.execute();
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

  @Override
  protected ResultSet doSelectRevisionAttributes(String tableName, long revisionId,
      List<IAttributeMapping> attributeMappings, boolean hasFullRevisionInfo, String where) throws SQLException
  {
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

    PreparedStatement pstmt = getConnection().prepareStatement(sql);
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

    PreparedStatement pstmt = getConnection().prepareStatement(sql);
    int idx = 1;

    if (dbFeatureID != 0)
    {
      pstmt.setInt(idx++, dbFeatureID);
    }

    pstmt.setLong(idx++, sourceId);
    pstmt.setInt(idx++, version);
    return pstmt.executeQuery();
  }
}
