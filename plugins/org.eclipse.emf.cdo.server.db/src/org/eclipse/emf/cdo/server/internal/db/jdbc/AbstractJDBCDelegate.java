/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Eike Stepper - maintenance
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402    
 */
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.mapping.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IReferenceMapping;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This abstract implementation of the {@link IJDBCDelegate} interface is used to translate CDO-related objects to base
 * types (e.g. CDOIDs to long) and then to delegate the database execution part to the doXYZ methods which should be
 * implemented by extenders. The purpose of this is to provide several JDBC strategies (e.g. simple or prepared
 * statement database access) depending on the requirements, but a single point of interpreting and transforming
 * CDO-internal structures. The JDBCDelegate also keeps open one database connection and one statement which can be used
 * to perform operations on the database. Extenders may, but don't have to, use the statement to perform operations.
 * 
 * @author Stefan Winkler
 * @since 2.0
 */
public abstract class AbstractJDBCDelegate extends Lifecycle implements IJDBCDelegate
{
  private IDBStoreAccessor storeAccessor;

  private Connection connection;

  private Statement statement;

  public AbstractJDBCDelegate()
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    connection = getConnectionProvider().getConnection();
    connection.setAutoCommit(isReadOnly());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    DBUtil.close(statement);
    statement = null;

    DBUtil.close(connection);
    connection = null;

    super.doDeactivate();
  }

  public IDBStoreAccessor getStoreAccessor()
  {
    return storeAccessor;
  }

  public void setStoreAccessor(IDBStoreAccessor storeAccessor)
  {
    checkInactive();
    this.storeAccessor = storeAccessor;
  }

  public IDBConnectionProvider getConnectionProvider()
  {
    return storeAccessor.getStore().getDBConnectionProvider();
  }

  public boolean isReadOnly()
  {
    return storeAccessor.isReader();
  }

  public final Connection getConnection()
  {
    return connection;
  }

  public final Statement getStatement()
  {
    if (statement == null)
    {
      try
      {
        statement = getConnection().createStatement();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    return statement;
  }

  public PreparedStatement getPreparedStatement(String sql)
  {
    try
    {
      return getConnection().prepareStatement(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public final void commit(OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getConnection().commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  public final void rollback()
  {
    try
    {
      getConnection().rollback();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public final void insertAttributes(InternalCDORevision revision, IClassMapping classMapping)
  {
    doInsertAttributes(classMapping.getTable().getName(), revision, classMapping.getAttributeMappings(), classMapping
        .hasFullRevisionInfo());
  }

  public final void updateAttributes(InternalCDORevision cdoRevision, IClassMapping classMapping)
  {
    InternalCDORevision revision = cdoRevision;

    List<IAttributeMapping> attributeMappings = classMapping.getAttributeMappings();
    if (attributeMappings == null)
    {
      attributeMappings = Collections.emptyList();
    }

    List<Pair<IAttributeMapping, Object>> attributeChanges = new ArrayList<Pair<IAttributeMapping, Object>>(
        attributeMappings.size());

    for (IAttributeMapping am : classMapping.getAttributeMappings())
    {
      attributeChanges.add(new Pair<IAttributeMapping, Object>(am, am.getRevisionValue(revision)));
    }

    updateAttributes(revision.getID(), revision.getVersion(), revision.getCreated(), (CDOID)revision.getContainerID(),
        revision.getContainingFeatureID(), revision.getResourceID(), attributeChanges, classMapping);
  }

  public void updateAttributes(CDOID id, int newVersion, long created, CDOID newContainerId,
      int newContainingFeatureId, CDOID newResourceId, List<Pair<IAttributeMapping, Object>> attributeChanges,
      IClassMapping classMapping)
  {
    doUpdateAttributes(classMapping.getTable().getName(), CDOIDUtil.getLong(id), newVersion, created, CDOIDUtil
        .getLong(newContainerId), newContainingFeatureId, CDOIDUtil.getLong(newResourceId), attributeChanges,
        classMapping.hasFullRevisionInfo());
  }

  public final void updateAttributes(CDOID id, int newVersion, long created,
      List<Pair<IAttributeMapping, Object>> attributeChanges, IClassMapping classMapping)
  {
    doUpdateAttributes(classMapping.getTable().getName(), CDOIDUtil.getLong(id), newVersion, created, attributeChanges,
        classMapping.hasFullRevisionInfo());
  }

  public final void updateRevisedForReplace(InternalCDORevision revision, IClassMapping classMapping)
  {
    doUpdateRevisedForReplace(classMapping.getTable().getName(), revision.getCreated() - 1, CDOIDUtil.getLong(revision
        .getID()), revision.getVersion() - 1);
  }

  public final void updateRevisedForDetach(CDOID id, long revised, IClassMapping classMapping)
  {
    doUpdateRevisedForDetach(classMapping.getTable().getName(), revised, CDOIDUtil.getLong(id));
  }

  public final void deleteAttributes(CDOID id, IClassMapping classMapping)
  {
    doDeleteAttributes(classMapping.getTable().getName(), CDOIDUtil.getLong(id));
  }

  public final void insertReference(CDOID id, int version, int index, CDOID targetId, IReferenceMapping referenceMapping)
  {
    doInsertReference(referenceMapping.getTable().getName(), getDBID(referenceMapping), CDOIDUtil.getLong(id), version,
        index, CDOIDUtil.getLong(targetId));
  }

  public void insertReferenceRow(CDOID id, int newVersion, int index, CDOID value, IReferenceMapping referenceMapping)
  {
    doInsertReferenceRow(referenceMapping.getTable().getName(), getDBID(referenceMapping), CDOIDUtil.getLong(id),
        newVersion, CDOIDUtil.getLong(value), index);
  }

  public void moveReferenceRow(CDOID id, int newVersion, int oldPosition, int newPosition,
      IReferenceMapping referenceMapping)
  {
    doMoveReferenceRow(referenceMapping.getTable().getName(), getDBID(referenceMapping), CDOIDUtil.getLong(id),
        newVersion, oldPosition, newPosition);
  }

  public void removeReferenceRow(CDOID id, int index, int newVersion, IReferenceMapping referenceMapping)
  {
    doRemoveReferenceRow(referenceMapping.getTable().getName(), getDBID(referenceMapping), CDOIDUtil.getLong(id),
        index, newVersion);
  }

  public final void deleteReferences(CDOID id, IReferenceMapping referenceMapping)
  {
    doDeleteReferences(referenceMapping.getTable().getName(), getDBID(referenceMapping), CDOIDUtil.getLong(id));
  }

  public void updateReference(CDOID id, int version, int index, CDOID targetId, IReferenceMapping referenceMapping)
  {
    doUpdateReference(referenceMapping.getTable().getName(), getDBID(referenceMapping), CDOIDUtil.getLong(id), version,
        index, CDOIDUtil.getLong(targetId));
  }

  public final void updateReferenceVersion(CDOID id, int newVersion, IReferenceMapping referenceMapping)
  {
    doUpdateReferenceVersion(referenceMapping.getTable().getName(), CDOIDUtil.getLong(id), newVersion);
  }

  public final boolean selectRevisionAttributes(InternalCDORevision revision, IClassMapping classMapping, String where)
  {
    List<IAttributeMapping> attributeMappings = classMapping.getAttributeMappings();
    if (attributeMappings == null)
    {
      attributeMappings = Collections.emptyList();
    }

    boolean withFullRevisionInfo = classMapping.hasFullRevisionInfo();
    ResultSet resultSet = null;
    try
    {
      resultSet = doSelectRevisionAttributes(classMapping.getTable().getName(), CDOIDUtil.getLong(revision.getID()),
          attributeMappings, withFullRevisionInfo, where);

      if (!resultSet.next())
      {
        return false;
      }

      int i = 0;
      if (withFullRevisionInfo)
      {
        InternalCDORevision rev = revision;
        rev.setVersion(resultSet.getInt(++i));
        rev.setCreated(resultSet.getLong(++i));
        rev.setRevised(resultSet.getLong(++i));
        rev.setResourceID(CDOIDUtil.createLong(resultSet.getLong(++i)));
        rev.setContainerID(CDOIDUtil.createLong(resultSet.getLong(++i)));
        rev.setContainingFeatureID(resultSet.getInt(++i));
      }

      if (attributeMappings != null)
      {
        for (IAttributeMapping attributeMapping : attributeMappings)
        {
          attributeMapping.extractValue(resultSet, ++i, revision);
        }
      }

      return true;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(resultSet);
    }
  }

  public void selectRevisionReferences(InternalCDORevision revision, IReferenceMapping referenceMapping,
      int referenceChunk)
  {
    MoveableList<Object> list = (revision).getList(referenceMapping.getFeature());

    CDOID source = revision.getID();
    long sourceId = CDOIDUtil.getLong(source);
    int version = revision.getVersion();

    ResultSet resultSet = null;
    try
    {
      resultSet = doSelectRevisionReferences(referenceMapping.getTable().getName(), sourceId, version,
          getDBID(referenceMapping), "");

      while (resultSet.next() && (referenceChunk == CDORevision.UNCHUNKED || --referenceChunk >= 0))
      {
        long target = resultSet.getLong(1);
        list.add(CDOIDUtil.createLong(target));
      }

      // TODO Optimize this?
      while (resultSet.next())
      {
        list.add(InternalCDORevision.UNINITIALIZED);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(resultSet);
    }
  }

  public void selectRevisionReferenceChunks(IDBStoreChunkReader chunkReader, List<Chunk> chunks,
      IReferenceMapping referenceMapping, String where)
  {
    CDOID source = chunkReader.getRevision().getID();
    long sourceId = CDOIDUtil.getLong(source);
    int version = chunkReader.getRevision().getVersion();

    ResultSet resultSet = null;

    try
    {
      resultSet = doSelectRevisionReferences(referenceMapping.getTable().getName(), sourceId, version,
          getDBID(referenceMapping), where);

      Chunk chunk = null;
      int chunkSize = 0;
      int chunkIndex = 0;
      int indexInChunk = 0;

      while (resultSet.next())
      {
        long target = resultSet.getLong(1);
        if (chunk == null)
        {
          chunk = chunks.get(chunkIndex++);
          chunkSize = chunk.size();
        }

        chunk.add(indexInChunk++, CDOIDUtil.createLong(target));
        if (indexInChunk == chunkSize)
        {
          chunk = null;
          indexInChunk = 0;
        }
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      close(resultSet);
    }
  }

  private long getDBID(IReferenceMapping referenceMapping)
  {
    if (referenceMapping.isWithFeature())
    {
      return storeAccessor.getStore().getMetaID(referenceMapping.getFeature());
    }

    return 0;
  }

  /**
   * Close the given result set and the statement, if this is needed (which is the case, iff the resultSet's statement
   * is not the one which is kept open by this instance).
   */
  private void close(ResultSet resultSet)
  {
    Statement stmt = null;

    try
    {
      stmt = resultSet.getStatement();
    }
    catch (Exception ex)
    {
      // Ignore
    }
    finally
    {
      DBUtil.close(resultSet);
    }

    // if the statement is one that has been created for the operation only
    // release it.
    if (stmt != statement)
    {
      releaseStatement(stmt);
    }
  }

  /**
   * Release a statement which has been used by the doSelectXxx implementations to create the respective ResultSet. This
   * must only be called with statements created by subclasses. Subclasses should override to handle special cases like
   * cached statements which are kept open.
   * 
   * @param stmt
   *          the statement to close
   */
  protected void releaseStatement(Statement stmt)
  {
    DBUtil.close(stmt);
  }

  /**
   * Insert an attribute row.
   */
  protected abstract void doInsertAttributes(String tableName, CDORevision revision,
      List<IAttributeMapping> attributeMappings, boolean withFullRevisionInfo);

  /**
   * Update an attribute row.
   */
  protected abstract void doUpdateAttributes(String name, long long1, int newVersion, long created,
      List<Pair<IAttributeMapping, Object>> attributeChanges, boolean hasFullRevisionInfo);

  /**
   * Update an attribute row including containment and resource attributes.
   */
  protected abstract void doUpdateAttributes(String name, long long1, int newVersion, long created,
      long newContainerId, int newContainingFeatureId, long newResourceId,
      List<Pair<IAttributeMapping, Object>> attributeChanges, boolean hasFullRevisionInfo);

  /**
   * Set the revised date of a cdoid (the cdoid is to be detached)
   */
  protected abstract void doUpdateRevisedForDetach(String tableName, long revised, long cdoid);

  /**
   * Set the revised date of a specific revision's previous version (the previous version is to be replaced).
   */
  protected abstract void doUpdateRevisedForReplace(String tableName, long revisedStamp, long cdoid, int version);

  /**
   * Delete an attribute row.
   */
  protected abstract void doDeleteAttributes(String name, long cdoid1);

  /**
   * Insert one reference of a particular CDOID and adjusts indexes.
   */
  protected abstract void doInsertReferenceRow(String tableName, long metaID, long cdoid, int newVersion, long l,
      int index);

  /**
   * Insert a reference row. Note: this is likely to be replaced by an implementation that supports storing multiple
   * references in one batch.
   */
  protected abstract void doInsertReference(String tableName, long metaID, long source, int version, int i, long target);

  /**
   * Update the target ID of one reference of a particular CDOID.
   */
  protected abstract void doUpdateReference(String name, long metaID, long sourceId, int newVersion, int index,
      long targetId);

  /**
   * Moves one reference of a particular CDOID and adjusts indexes.
   */
  protected abstract void doMoveReferenceRow(String tableName, long metaID, long cdoid, int newVersion,
      int oldPosition, int newPosition);

  /**
   * Delete all references of a particular CDOID.
   */
  protected abstract void doDeleteReferences(String tableName, long metaID, long cdoid);

  /**
   * Deletes one reference of a particular CDOID and adjusts indexes.
   * 
   * @param newVersion
   */
  protected abstract void doRemoveReferenceRow(String tableName, long metaID, long cdoid, int index, int newVersion);

  /**
   * Update all references of cdoid to newVersion
   */
  protected abstract void doUpdateReferenceVersion(String tableName, long cdoid, int newVersion);

  /**
   * Select a revision's attributes. The caller is resposible for closing resultSet and associated statement, if
   * appropriate.
   */
  protected abstract ResultSet doSelectRevisionAttributes(String tableName, long revisionId,
      List<IAttributeMapping> attributeMappings, boolean hasFullRevisionInfo, String where) throws SQLException;

  /**
   * Select a revision's references (or a part thereof) The caller is resposible for closing resultSet and associated
   * statement, if appropriate.
   */
  protected abstract ResultSet doSelectRevisionReferences(String tableName, long sourceId, int version, long metaID,
      String where) throws SQLException;
}
