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
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
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

  public void flush(OMMonitor monitor)
  {
    // Do nothing
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
      CDORevisionData data = revision.data();
      builder.append(", ");
      builder.append(getStoreAccessor().getStore().getMetaID(revision.getEClass()));
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
  protected void doUpdateAttributes(String tableName, long cdoid, int newVersion, long created,
      List<Pair<IAttributeMapping, Object>> attributeChanges, boolean hasFullRevisionInfo)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(" = ");
    builder.append(created);

    for (Pair<IAttributeMapping, Object> attributeChange : attributeChanges)
    {
      IAttributeMapping attributeMapping = attributeChange.getElement1();
      builder.append(", ");
      builder.append(attributeMapping.getField());
      builder.append(" = ");
      attributeMapping.appendValue(builder, attributeChange.getElement2());
    }

    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" = ");
    builder.append(cdoid);

    sqlUpdate(builder.toString());
  }

  @Override
  protected void doUpdateAttributes(String tableName, long cdoid, int newVersion, long created, long newContainerId,
      int newContainingFeatureId, long newResourceId, List<Pair<IAttributeMapping, Object>> attributeChanges,
      boolean hasFullRevisionInfo)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.ATTRIBUTES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_CREATED);
    builder.append(" = ");
    builder.append(created);

    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_CONTAINER);
    builder.append(" = ");
    builder.append(newContainerId);

    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_FEATURE);
    builder.append(" = ");
    builder.append(newContainingFeatureId);

    builder.append(", ");
    builder.append(CDODBSchema.ATTRIBUTES_RESOURCE);
    builder.append(" = ");
    builder.append(newResourceId);

    for (Pair<IAttributeMapping, Object> attributeChange : attributeChanges)
    {
      IAttributeMapping attributeMapping = attributeChange.getElement1();
      builder.append(", ");
      builder.append(attributeMapping.getField());
      builder.append(" = ");
      attributeMapping.appendValue(builder, attributeChange.getElement2());
    }

    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" = ");
    builder.append(cdoid);

    sqlUpdate(builder.toString());
  }

  @Override
  protected void doDeleteAttributes(String name, long cdoid)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM ");
    builder.append(name);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.ATTRIBUTES_ID);
    builder.append(" = ");
    builder.append(cdoid);
    sqlUpdate(builder.toString());
  }

  @Override
  protected void doUpdateRevisedForReplace(String table, long revisedStamp, long cdoid, int version)
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
  protected void doUpdateRevisedForDetach(String table, long revisedStamp, long cdoid)
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
  protected void doInsertReferenceRow(String tableName, long metaID, long cdoid, int newVersion, long target, int index)
  {
    move1up(tableName, metaID, cdoid, newVersion, index, -1);
    doInsertReference(tableName, metaID, cdoid, newVersion, index, target);
  }

  @Override
  protected void doInsertReference(String table, long metaID, long source, int version, int i, long target)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(table);
    builder.append(" VALUES (");
    if (metaID != 0)
    {
      builder.append(metaID);
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

  @Override
  protected void doMoveReferenceRow(String tableName, long metaID, long cdoid, int newVersion, int oldPosition,
      int newPosition)
  {
    if (oldPosition == newPosition)
    {
      return;
    }

    // move element away temporarily
    updateOneIndex(tableName, metaID, cdoid, newVersion, oldPosition, -1);

    // move elements in between
    if (oldPosition < newPosition)
    {
      move1down(tableName, metaID, cdoid, newVersion, oldPosition, newPosition);
    }
    else
    {
      // oldPosition > newPosition -- equal case is handled above
      move1up(tableName, metaID, cdoid, newVersion, newPosition, oldPosition);
    }

    // move temporary element to new position
    updateOneIndex(tableName, metaID, cdoid, newVersion, -1, newPosition);
  }

  @Override
  protected void doDeleteReferences(String name, long metaID, long cdoid)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM ");
    builder.append(name);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append(" = ");
    builder.append(cdoid);
    if (metaID != 0)
    {
      builder.append(" AND ");
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append(" = ");
      builder.append(metaID);
    }
    sqlUpdate(builder.toString());
  }

  @Override
  protected void doRemoveReferenceRow(String tableName, long metaID, long cdoid, int index, int newVersion)
  {
    deleteReferenceRow(tableName, metaID, cdoid, index);
    move1down(tableName, metaID, cdoid, newVersion, index, -1);
  }

  @Override
  protected void doUpdateReferenceVersion(String tableName, long cdoid, int newVersion)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append(" = ");
    builder.append(cdoid);
    sqlUpdate(builder.toString());
  }

  @Override
  protected void doUpdateReference(String tableName, long metaID, long sourceId, int newVersion, int index,
      long targetId)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.REFERENCES_TARGET);
    builder.append(" = ");
    builder.append(targetId);
    builder.append(", ");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append(" = ");
    builder.append(sourceId);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" = ");
    builder.append(index);

    if (metaID != 0)
    {
      builder.append(" AND ");
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append(" = ");
      builder.append(metaID);
    }

    sqlUpdate(builder.toString());
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
  protected ResultSet doSelectRevisionReferences(String tableName, long sourceId, int version, long metaID, String where)
      throws SQLException
  {

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT ");
    builder.append(CDODBSchema.REFERENCES_TARGET);
    builder.append(" FROM ");
    builder.append(tableName);
    builder.append(" WHERE ");
    if (metaID != 0)
    {
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append("=");
      builder.append(metaID);
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

  private void deleteReferenceRow(String name, long metaID, long cdoid, int index)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("DELETE FROM ");
    builder.append(name);
    builder.append(" WHERE ");
    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append(" = ");
    builder.append(cdoid);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" = ");
    builder.append(index);
    if (metaID != 0)
    {
      builder.append(" AND ");
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append(" = ");
      builder.append(metaID);
    }
    sqlUpdate(builder.toString());
  }

  /**
   * Move references upwards to make room at position <code>index</code>. If <code>upperIndex</code> is <code>-1</code>,
   * then all indices beginning with <code>index</code> are moved. Else, only indexes starting with <code>index</code>
   * and ending with <code>upperIndex - 1</code> are moved up.
   */
  private void move1up(String tableName, long metaID, long cdoid, int newVersion, int index, int upperIndex)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" = ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append("+1 ,");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(" WHERE ");
    if (metaID != 0)
    {
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append("=");
      builder.append(metaID);
      builder.append(" AND ");
    }

    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append("=");
    builder.append(cdoid);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" >= ");
    builder.append(index);

    if (upperIndex != -1)
    {
      builder.append(" AND ");
      builder.append(CDODBSchema.REFERENCES_IDX);
      builder.append(" < ");
      builder.append(upperIndex);
    }

    sqlUpdate(builder.toString());
  }

  /**
   * Move references downwards to close a gap at position <code>index</code>. If <code>upperIndex</code> is
   * <code>-1</code>, then all indices beginning with <code>index + 1</code> are moved. Else, only indexes starting with
   * <code>index + 1</code> and ending with <code>upperIndex</code> are moved down.
   */
  private void move1down(String tableName, long metaID, long cdoid, int newVersion, int index, int upperIndex)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" = ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append("-1 ,");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(" WHERE ");
    if (metaID != 0)
    {
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append("=");
      builder.append(metaID);
      builder.append(" AND ");
    }

    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append("=");
    builder.append(cdoid);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" > ");
    builder.append(index);

    if (upperIndex != -1)
    {
      builder.append(" AND ");
      builder.append(CDODBSchema.REFERENCES_IDX);
      builder.append(" <= ");
      builder.append(upperIndex);
    }

    sqlUpdate(builder.toString());
  }

  private void updateOneIndex(String tableName, long metaID, long cdoid, int newVersion, int oldIndex, int newIndex)
  {
    StringBuilder builder = new StringBuilder();

    builder.append("UPDATE ");
    builder.append(tableName);
    builder.append(" SET ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" = ");
    builder.append(newIndex);
    builder.append(", ");
    builder.append(CDODBSchema.REFERENCES_VERSION);
    builder.append(" = ");
    builder.append(newVersion);
    builder.append(" WHERE ");
    if (metaID != 0)
    {
      builder.append(CDODBSchema.REFERENCES_FEATURE);
      builder.append("=");
      builder.append(metaID);
      builder.append(" AND ");
    }

    builder.append(CDODBSchema.REFERENCES_SOURCE);
    builder.append("=");
    builder.append(cdoid);
    builder.append(" AND ");
    builder.append(CDODBSchema.REFERENCES_IDX);
    builder.append(" = ");
    builder.append(oldIndex);

    sqlUpdate(builder.toString());
  }
}
