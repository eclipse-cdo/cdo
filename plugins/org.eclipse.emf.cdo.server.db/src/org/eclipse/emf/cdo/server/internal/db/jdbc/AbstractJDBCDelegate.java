/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreChunkReader.Chunk;
import org.eclipse.emf.cdo.server.db.IAttributeMapping;
import org.eclipse.emf.cdo.server.db.IClassMapping;
import org.eclipse.emf.cdo.server.db.IDBStoreChunkReader;
import org.eclipse.emf.cdo.server.db.IJDBCDelegate;
import org.eclipse.emf.cdo.server.db.IReferenceMapping;
import org.eclipse.emf.cdo.server.internal.db.FeatureServerInfo;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.collection.MoveableList;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
  private IDBConnectionProvider connectionProvider;

  private boolean readOnly;

  private Connection connection;

  private Statement statement;

  public AbstractJDBCDelegate()
  {
  }

  public IDBConnectionProvider getConnectionProvider()
  {
    return connectionProvider;
  }

  public void setConnectionProvider(IDBConnectionProvider connectionProvider)
  {
    checkInactive();
    this.connectionProvider = connectionProvider;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly)
  {
    checkInactive();
    this.readOnly = readOnly;
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

  public final void insertAttributes(CDORevision revision, IClassMapping classMapping)
  {
    doInsertAttributes(classMapping.getTable().getName(), revision, classMapping.getAttributeMappings(), classMapping
        .hasFullRevisionInfo());
  }

  public final void updateAttributes(CDORevision revision, IClassMapping classMapping)
  {
    doUpdateAttributes(classMapping.getTable().getName(), revision, classMapping.getAttributeMappings(), classMapping
        .hasFullRevisionInfo());
  }

  public void deleteAttributes(CDOID id, IClassMapping classMapping)
  {
    doDeleteAttributes(classMapping.getTable().getName(), CDOIDUtil.getLong(id));
  }

  public final void insertReference(CDORevision sourceRevision, int index, CDOID targetId,
      IReferenceMapping referenceMapping)
  {
    doInsertReference(referenceMapping.getTable().getName(), referenceMapping.isWithFeature() ? FeatureServerInfo
        .getDBID(referenceMapping.getFeature()) : 0, CDOIDUtil.getLong(sourceRevision.getID()), sourceRevision
        .getVersion(), index, CDOIDUtil.getLong(targetId));
  }

  public void deleteReferences(CDOID id, IReferenceMapping referenceMapping)
  {
    doDeleteReferences(referenceMapping.getTable().getName(), referenceMapping.isWithFeature() ? FeatureServerInfo
        .getDBID(referenceMapping.getFeature()) : 0, CDOIDUtil.getLong(id));
  }

  public final void updateRevisedForReplace(CDORevision revision, IClassMapping classMapping)
  {
    doUpdateRevisedForReplace(classMapping.getTable().getName(), revision.getCreated() - 1, CDOIDUtil.getLong(revision
        .getID()), revision.getVersion() - 1);
  }

  public final void updateRevisedForDetach(CDOID id, long revised, IClassMapping classMapping)
  {
    doUpdateRevisedForDetach(classMapping.getTable().getName(), revised, CDOIDUtil.getLong(id));
  }

  public final boolean selectRevisionAttributes(CDORevision revision, IClassMapping classMapping, String where)
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
        InternalCDORevision rev = (InternalCDORevision)revision;
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

  public void selectRevisionReferences(CDORevision revision, IReferenceMapping referenceMapping, int referenceChunk)
  {
    MoveableList<Object> list = ((InternalCDORevision)revision).getList(referenceMapping.getFeature());

    CDOID source = revision.getID();
    long sourceId = CDOIDUtil.getLong(source);
    int version = revision.getVersion();

    ResultSet resultSet = null;
    try
    {
      resultSet = doSelectRevisionReferences(referenceMapping.getTable().getName(), sourceId, version, referenceMapping
          .isWithFeature() ? FeatureServerInfo.getDBID(referenceMapping.getFeature()) : 0, "");

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
      resultSet = doSelectRevisionReferences(referenceMapping.getTable().getName(), sourceId, version, referenceMapping
          .isWithFeature() ? FeatureServerInfo.getDBID(referenceMapping.getFeature()) : 0, where);

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

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    connection = connectionProvider.getConnection();
    connection.setAutoCommit(readOnly);
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
  protected abstract void doUpdateAttributes(String name, CDORevision revision,
      List<IAttributeMapping> attributeMappings, boolean hasFullRevisionInfo);

  /**
   * Delete an attribute row.
   */
  protected abstract void doDeleteAttributes(String name, long cdoid1);

  /**
   * Insert a reference row. Note: this is likely to be replaced by an implementation that supports storing multiple
   * references in one batch.
   */
  protected abstract void doInsertReference(String tableName, int dbId, long source, int version, int i, long target);

  /**
   * Delete all references of a particular CDOID.
   */
  protected abstract void doDeleteReferences(String name, int dbId, long cdoid);

  /**
   * Set the revised date of a cdoid (the cdoid is to be detached)
   */
  protected abstract void doUpdateRevisedForDetach(String tableName, long revised, long cdoid);

  /**
   * Set the revised date of a specific revision's previous version (the previous version is to be replaced).
   */
  protected abstract void doUpdateRevisedForReplace(String tableName, long revisedStamp, long cdoid, int version);

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
  protected abstract ResultSet doSelectRevisionReferences(String tableName, long sourceId, int version,
      int dbFeatureID, String where) throws SQLException;

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
}
