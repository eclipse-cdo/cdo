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
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.ServerInfo;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Stefan Winkler
 * @since 2.0
 */
public class StatementJDBCDelegate extends AbstractJDBCDelegate
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StatementJDBCDelegate.class);

  public StatementJDBCDelegate()
  {
  }

  @Override
  protected void doInsertAttributes(String table, CDORevision revision, List<IAttributeMapping> attributeMappings,
      boolean withFullRevisionInfo)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");

    builder.append(CDOIDUtil.getLong(revision.getID()));
    builder.append(", ");
    builder.append(revision.getVersion());

    if (withFullRevisionInfo)
    {
      CDORevisionData data = revision.getData();
      builder.append(", ");
      builder.append(ServerInfo.getDBID(revision.getCDOClass()));
      builder.append(", ");
      builder.append(revision.getCreated());
      builder.append(", ");
      builder.append(revision.getRevised());
      builder.append(", ");
      builder.append(CDOIDUtil.getLong(data.getResourceID()));
      builder.append(", ");
      builder.append(CDOIDUtil.getLong((CDOID)data.getContainerID()));
      builder.append(", ");
      builder.append(data.getContainingFeatureID());
    }

    if (attributeMappings != null)
    {
      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        builder.append(", ");
        attributeMapping.appendValue(builder, revision);
      }
    }

    builder.append(")");
    sqlUpdate(builder.toString());
  }

  @Override
  protected void doUpdateRevised(String table, long revisedStamp, long cdoid, int version)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(table);
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=");
    builder.append(revisedStamp);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    builder.append(cdoid);
    builder.append(" AND ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append("=");
    builder.append(version);
    sqlUpdate(builder.toString());
  }

  @Override
  protected void doUpdateRevised(String table, long revisedStamp, long cdoid)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(table);
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=");
    builder.append(revisedStamp);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    builder.append(cdoid);
    builder.append(" AND ");
    builder.append(CDODBSchema.ATTRIBUTES_REVISED);
    builder.append("=0");
    sqlUpdate(builder.toString());
  }

  @Override
  protected void doInsertReference(String table, int dbId, long source, int version, int i, long target)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");
    if (dbId != 0)
    {
      builder.append(dbId);
      builder.append(", ");
    }
    builder.append(source);
    builder.append(", ");
    builder.append(version);
    builder.append(", ");
    builder.append(i);
    builder.append(", ");
    builder.append(target);
    builder.append(")");
    sqlUpdate(builder.toString());
  }

  private int sqlUpdate(String sql) throws DBException
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    Statement statement = null;
    try
    {
      statement = getConnection().createStatement();
      return statement.executeUpdate(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      try
      {
        statement.close();
      }
      catch (SQLException e)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(e);
        }
      }
    }
  }

  @Override
  protected ResultSet doSelectRevisionAttributes(String tableName, long revisionId,
      List<IAttributeMapping> attributeMappings, boolean hasFullRevisionInfo, String where) throws SQLException
  {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    if (hasFullRevisionInfo)
    {
      builder.append(CDODBSchema.ATTRIBUTES_VERSION);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_CREATED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_REVISED);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
      builder.append(", ");
      builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    }
    else
    {
      if (attributeMappings == null)
      {
        // Only references return
        return null;
      }
    }

    if (attributeMappings != null)
    {
      for (IAttributeMapping attributeMapping : attributeMappings)
      {
        builder.append(", ");
        builder.append(attributeMapping.getField());
      }
    }

    builder.append(" FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append("=");
    builder.append(revisionId);
    builder.append(" AND (");
    builder.append(where);
    builder.append(")");
    String sql = builder.toString();
    if (TRACER.isEnabled())
    {
      TRACER.trace(sql);
    }

    return getStatement().executeQuery(sql);
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
      builder.append("=");
      builder.append(dbFeatureID);
      builder.append(" AND ");
    }

    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append("=");
    builder.append(sourceId);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append("=");
    builder.append(version);

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

    return getStatement().executeQuery(sql);
  }
}
