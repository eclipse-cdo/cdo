/*
 * Copyright (c) 2010-2014, 2016, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - redesign (prepared statements)
 *    Stefan Winkler - bug 276926
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.internal.db.BatchingContext;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTable;
import org.eclipse.emf.cdo.server.internal.db.IObjectTypeMapper;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class ObjectTypeTable extends DBStoreTable implements IObjectTypeMapper
{
  private static final boolean INDEX_CLASS_COLUMN = OMPlatform.INSTANCE
      .isProperty("org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.ObjectTypeTable.INDEX_CLASS_COLUMN"); //$NON-NLS-1$

  private IDBField id;

  private IDBField clazz;

  private IDBField created;

  private String sqlDelete;

  private String sqlInsert;

  private String sqlSelect;

  private boolean supportsSavepoints;

  private int batchSize;

  public ObjectTypeTable(IDBStore store)
  {
    super(store, MappingNames.CDO_OBJECTS);
  }

  public final IDBField id()
  {
    return id;
  }

  public final IDBField clazz()
  {
    return clazz;
  }

  @Override
  public final EClass getObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlSelect, ReuseProbability.MAX);

    try
    {
      idHandler.setCDOID(stmt, 1, id);

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      ResultSet resultSet = stmt.executeQuery();

      if (!resultSet.next())
      {
        if (DBUtil.isTracerEnabled())
        {
          DBUtil.trace("ClassID for CDOID " + id + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return null;
      }

      CDOID metaID = idHandler.getCDOID(resultSet, 1);
      return (EClass)store().getMetaDataManager().getMetaInstance(metaID);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public final boolean putObjectType(IDBStoreAccessor accessor, long timeStamp, CDOID id, EClass type)
  {
    IDBStore store = store();
    IIDHandler idHandler = store.getIDHandler();
    IDBAdapter dbAdapter = store.getDBAdapter();

    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlInsert, ReuseProbability.MAX);
    Savepoint savepoint = null;

    try
    {
      CDOID metaID = store.getMetaDataManager().getMetaID(type, timeStamp);

      idHandler.setCDOID(stmt, 1, id);
      idHandler.setCDOID(stmt, 2, metaID);
      stmt.setLong(3, timeStamp);

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      if (dbAdapter.isDuplicateKeyTransactionAbort())
      {
        savepoint = stmt.getConnection().setSavepoint();
      }

      int result = stmt.executeUpdate();
      if (savepoint != null)
      {
        stmt.getConnection().releaseSavepoint(savepoint);
      }
      if (result != 1)
      {
        throw new DBException("Object type could not be inserted: " + id); //$NON-NLS-1$
      }

      return true;
    }
    catch (SQLException ex)
    {
      if (dbAdapter.isDuplicateKeyException(ex))
      {
        // Unique key violation can occur in rare cases (merging new objects from other branches)
        if (savepoint != null)
        {
          try
          {
            stmt.getConnection().rollback(savepoint);
            stmt.getConnection().releaseSavepoint(savepoint);
          }
          catch (SQLException rollbackException)
          {
            ex.addSuppressed(rollbackException);
            throw new DBException(ex);
          }
        }

        return false;
      }

      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  /**
   * Inserts the object types of a homogeneous new-revision group.
   * <p>
   * Each bounded chunk is first classified with one portable {@code IN} query. Known IDs retain the existing
   * synchronous insertion path. IDs that are absent at query time are inserted through one batch protected by a
   * savepoint. A concurrent insert or any other batch failure rolls the complete chunk back and replays all of its
   * candidate entries through {@link #putObjectType(IDBStoreAccessor, long, CDOID, EClass)}.
   *
   * @param accessor
   *          the store accessor that owns the current transaction
   * @param revisions
   *          the homogeneous new-revision group
   * @param type
   *          the EClass shared by the revisions
   */
  public final void putObjectTypes(IDBStoreAccessor accessor, InternalCDORevision[] revisions, EClass type)
  {
    if (batchSize <= 0 || revisions.length <= 1 || !supportsSavepoints)
    {
      for (InternalCDORevision revision : revisions)
      {
        putObjectType(accessor, revision.getTimeStamp(), revision.getID(), type);
      }

      return;
    }

    for (int start = 0; start < revisions.length; start += batchSize)
    {
      int end = Math.min(start + batchSize, revisions.length);
      putObjectTypeChunk(accessor, revisions, start, end, type, batchSize);
    }
  }

  private void putObjectTypeChunk(IDBStoreAccessor accessor, InternalCDORevision[] revisions, int start, int end, EClass type, int batchSize)
  {
    recordDiagnosticCounter(accessor, "ObjectType.prequeryChunk", 1); //$NON-NLS-1$

    Set<CDOID> existingIDs = queryExistingIDs(accessor, revisions, start, end);
    List<InternalCDORevision> candidates = new ArrayList<>(end - start);

    for (int i = start; i < end; i++)
    {
      InternalCDORevision revision = revisions[i];

      if (existingIDs.contains(revision.getID()))
      {
        putObjectType(accessor, revision.getTimeStamp(), revision.getID(), type);
      }
      else
      {
        candidates.add(revision);
      }
    }

    if (!candidates.isEmpty())
    {
      insertObjectTypeBatch(accessor, candidates, type, batchSize);
    }
  }

  private Set<CDOID> queryExistingIDs(IDBStoreAccessor accessor, InternalCDORevision[] revisions, int start, int end)
  {
    StringBuilder builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(id);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(id);
    builder.append(" IN ("); //$NON-NLS-1$

    for (int i = start; i < end; i++)
    {
      if (i != start)
      {
        builder.append(", "); //$NON-NLS-1$
      }

      builder.append('?');
    }

    builder.append(')');

    IIDHandler idHandler = store().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(builder.toString(), ReuseProbability.HIGH);
    ResultSet resultSet = null;

    try
    {
      int column = 1;

      for (int i = start; i < end; i++)
      {
        idHandler.setCDOID(stmt, column++, revisions[i].getID());
      }

      resultSet = stmt.executeQuery();
      Set<CDOID> result = new HashSet<>();

      while (resultSet.next())
      {
        result.add(idHandler.getCDOID(resultSet, 1));
      }

      return result;
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

  private void insertObjectTypeBatch(IDBStoreAccessor accessor, List<InternalCDORevision> candidates, EClass type, int batchSize)
  {
    Connection connection = accessor.getConnection();
    Savepoint savepoint;

    try
    {
      savepoint = connection.setSavepoint();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    IDBPreparedStatement preparedStatement = accessor.getDBConnection().prepareStatement(sqlInsert, ReuseProbability.MAX);
    BatchedStatement stmt = DBUtil.batched(preparedStatement, batchSize);

    try
    {
      IIDHandler idHandler = store().getIDHandler();

      for (InternalCDORevision revision : candidates)
      {
        int column = 1;
        idHandler.setCDOID(stmt, column++, revision.getID());
        CDOID metaID = store().getMetaDataManager().getMetaID(type, revision.getTimeStamp());
        idHandler.setCDOID(stmt, column++, metaID);
        stmt.setLong(column, revision.getTimeStamp());
        stmt.executeUpdate();
      }

      stmt.flush();
      validateBatchResult(stmt, candidates.size());
      connection.releaseSavepoint(savepoint);

      recordDiagnosticCounter(accessor, "ObjectType.insertEntries", candidates.size()); //$NON-NLS-1$
      recordDiagnosticCounter(accessor, "ObjectType.insertExecutions", stmt.getExecutionCount()); //$NON-NLS-1$
    }
    catch (SQLException | DBException ex)
    {
      try
      {
        rollbackBatch(connection, savepoint, ex);
      }
      finally
      {
        discardBatch(stmt);
      }

      recordDiagnosticCounter(accessor, "ObjectType.batchRollbackReplay", 1); //$NON-NLS-1$

      for (InternalCDORevision revision : candidates)
      {
        putObjectType(accessor, revision.getTimeStamp(), revision.getID(), type);
      }
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  @Override
  public final boolean removeObjectType(IDBStoreAccessor accessor, CDOID id)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sqlDelete, ReuseProbability.MAX);

    try
    {
      idHandler.setCDOID(stmt, 1, id);

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      int result = stmt.executeUpdate();
      return result == 1;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }
  }

  public final void handleObjectTypes(IDBStoreAccessor accessor, ObjectTypeHandler handler) throws SQLException
  {
    try (Statement stmt = accessor.getConnection().createStatement(); //
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT(" + clazz + ") FROM " + table()))
    {
      while (rs.next())
      {
        IIDHandler idHandler = store().getIDHandler();
        String raw = idHandler.getStringValue(rs, 1);
        CDOID metaID = idHandler.getCDOID(rs, 1);

        IMetaDataManager metaDataManager = store().getMetaDataManager();
        String uri = metaDataManager.getMetaURI(metaID);

        handler.handleObjectType(raw, metaID, uri);
      }
    }
  }

  @Override
  public CDOID getMaxID(Connection connection, IIDHandler idHandler)
  {
    Statement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.createStatement();
      resultSet = stmt.executeQuery("SELECT MAX(" + id + ") FROM " + table());

      if (resultSet.next())
      {
        return idHandler.getCDOID(resultSet, 1);
      }

      return null;
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

  @Override
  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    String where = " WHERE " + created + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, table(), null, where);
  }

  @Override
  public void rawImport(Connection connection, CDODataInput in, OMMonitor monitor) throws IOException
  {
    DBUtil.deserializeTable(in, connection, table(), monitor);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    // Pre-determine whether savepoints are supported.
    Connection connection = null;

    try
    {
      connection = store().getConnection();
      supportsSavepoints = connection.getMetaData().supportsSavepoints();
    }
    catch (SQLException | AbstractMethodError ex)
    {
      supportsSavepoints = false;
    }
    finally
    {
      DBUtil.close(connection);
    }

    // Initialize the batchSize to use from the configuration.
    Map<String, String> properties = store().getProperties();
    int legacyDefault = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.server.db.LIST_BATCH_SIZE", //$NON-NLS-1$
        BatchingContext.DEFAULT_STATEMENT_BATCH_SIZE);
    int statementBatchSize = getIntProperty(properties, IDBStore.Props.BATCH_STATEMENT_SIZE, legacyDefault);
    batchSize = getIntProperty(properties, IDBStore.Props.OBJECT_TYPE_BATCH_SIZE, statementBatchSize);
  }

  @Override
  protected void firstActivate(IDBTable table)
  {
    DBType idType = store().getIDHandler().getDBType();
    int idLength = store().getIDColumnLength();

    id = table.addField(MappingNames.ATTRIBUTES_ID, idType, idLength, true);
    clazz = table.addField(MappingNames.ATTRIBUTES_CLASS, idType, idLength);
    created = table.addField(MappingNames.ATTRIBUTES_CREATED, DBType.BIGINT);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);

    if (INDEX_CLASS_COLUMN || store().getRepository().isSupportingUnits())
    {
      table.addIndex(IDBIndex.Type.NON_UNIQUE, clazz);
    }
  }

  @Override
  protected void reActivate(IDBTable table)
  {
    id = table.getField(MappingNames.ATTRIBUTES_ID);
    clazz = table.getField(MappingNames.ATTRIBUTES_CLASS);
    created = table.getField(MappingNames.ATTRIBUTES_CREATED);
  }

  @Override
  protected void initSQL(IDBTable table)
  {
    sqlSelect = "SELECT " + clazz + " FROM " + table + " WHERE " + id + "=?";
    sqlInsert = "INSERT INTO " + table + "(" + id + "," + clazz + "," + created + ") VALUES (?, ?, ?)";
    sqlDelete = "DELETE FROM " + table + " WHERE " + id + "=?";
  }

  private static void discardBatch(BatchedStatement stmt)
  {
    try
    {
      stmt.clearBatch();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  private static void recordDiagnosticCounter(IDBStoreAccessor accessor, String name, long value)
  {
    if (BatchingContext.STATISTICS_ENABLED)
    {
      accessor.getBatchingContext().recordDiagnosticCounter(name, value);
    }
  }

  private static void validateBatchResult(BatchedStatement stmt, int expectedCount)
  {
    int knownResult = stmt.getTotalResult();
    int unknownResultCount = stmt.getUnknownResultCount();

    if (knownResult > expectedCount || unknownResultCount == 0 && knownResult != expectedCount || knownResult + unknownResultCount < expectedCount)
    {
      throw new DBException("Object type batch did not insert all entries"); //$NON-NLS-1$
    }
  }

  private static void rollbackBatch(Connection connection, Savepoint savepoint, Exception failure)
  {
    try
    {
      connection.rollback(savepoint);
      connection.releaseSavepoint(savepoint);
    }
    catch (SQLException ex)
    {
      failure.addSuppressed(ex);
      throw new DBException(failure);
    }
  }

  private static int getIntProperty(Map<String, String> properties, String key, int defaultValue)
  {
    if (properties != null)
    {
      String value = properties.get(key);
      if (value != null)
      {
        return Integer.parseInt(value);
      }
    }

    return defaultValue;
  }

  /**
   * Functional interface for handling object types.
   *
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface ObjectTypeHandler
  {
    /**
     * Handles an object type.
     *
     * @param raw The raw string representation of the object type.
     *        This value is obtained directly from the database and converted to a Java string by the {@link IIDHandler}.
     *        The resulting Java string can be used in database queries. It is quoted if necessary.
     * @param metaID The meta ID of the object type.
     * @param uri The URI of the object type as stored in the cdo_ext_refs table.
     */
    public void handleObjectType(String raw, CDOID metaID, String uri);
  }
}
