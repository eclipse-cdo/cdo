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
import java.util.Locale;
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

  private DuplicatePolicy duplicatePolicy = DuplicatePolicy.SAFE;

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
   * Inserts the object types of a homogeneous bulk group of new revisions.
   * <p>
   * The normal class-mapping caller is expected to invoke this method only
   * when it has more than one revision for the same class and all revisions
   * represent first-version/new-object writes. That caller-side condition is
   * what makes the operation a bulk class-setup operation. It is valid for
   * Normal, Audit, and Branching mapping strategies; the method itself does
   * not depend on branch information.
   * <p>
   * Fresh store-allocated IDs are inserted directly through a batch because
   * their uniqueness is established by the allocation stage. For IDs without
   * that provenance, SAFE mode classifies each bounded chunk with one portable
   * {@code IN} query, inserts absent IDs through a savepoint-protected batch,
   * and replays the entries synchronously through
   * {@link #putObjectType(IDBStoreAccessor, long, CDOID, EClass)} after a
   * conflict. FAIL mode directly batches every candidate and propagates any
   * duplicate-key failure. This preserves duplicate and race semantics for
   * merge/import/backup/clone and other permanent-ID paths while allowing the
   * operator to select the lower-overhead contract when safe.
   * <p>
   * The method intentionally remains safe when called outside the preferred
   * bulk conditions. A single revision, a non-positive configured batch size,
   * or a database connection that does not support savepoints uses the
   * synchronous {@code putObjectType()} path for every revision. Raw or other
   * special write paths do not call this method, and callers that need the
   * exact single-row semantics can continue to call
   * {@link #putObjectType(IDBStoreAccessor, long, CDOID, EClass)} directly.
   * <p>
   * Thus, the caller enforces the grouping, first-version, and normal
   * class-mapping conditions; this method enforces the revision-count,
   * batch-size, and savepoint guards and handles duplicate/race fallback
   * internally.
   *
   * @param accessor the store accessor that owns the current transaction
   * @param revisions the homogeneous new-revision group supplied by the
   *          class-mapping bulk path
   * @param type the EClass shared by the revisions
   */
  public final void putObjectTypes(IDBStoreAccessor accessor, InternalCDORevision[] revisions, EClass type)
  {
    if (batchSize <= 1 || revisions.length <= 1)
    {
      recordDiagnosticCounter(accessor, "ObjectType.synchronousFallback", revisions.length); //$NON-NLS-1$
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
    /*
     * A chunk is deliberately divided before any database lookup is made. The storeAllocatedIDs set is explicit
     * provenance, populated while temporary IDs are mapped by the authoritative store. It is not inferred from the ID
     * representation, FIRST_VERSION, or the repository's branching mode. Consequently, IDs arriving from merge, import,
     * backup, clone, raw, or other external/permanent-ID paths remain in the unknown list and retain duplicate-safe
     * handling. FAIL applies to every candidate in this bulk operation. Fresh IDs are direct candidates in both
     * policies; FAIL additionally makes unknown IDs direct candidates because the operator has guaranteed their
     * uniqueness.
     */
    Set<CDOID> freshIDs = accessor.getStoreAllocatedIDs();
    List<InternalCDORevision> direct = new ArrayList<>(end - start);
    List<InternalCDORevision> unknown = new ArrayList<>(end - start);

    for (int i = start; i < end; i++)
    {
      InternalCDORevision revision = revisions[i];

      // A direct batch has no savepoint and no replay path. Any duplicate-key
      // error therefore propagates and aborts the surrounding commit.
      if (freshIDs.contains(revision.getID()) || duplicatePolicy == DuplicatePolicy.FAIL)
      {
        direct.add(revision);
      }
      else
      {
        unknown.add(revision);
      }
    }

    if (!direct.isEmpty())
    {
      // Direct rows are kept separate from SAFE rows so that a SAFE prequery
      // cannot accidentally weaken the fresh-ID fast path, and so that a
      // duplicate in a direct batch is never converted into a successful
      // synchronous replay.
      insertObjectTypeBatch(accessor, direct, type, batchSize, false);
      recordDiagnosticCounter(accessor, duplicatePolicy == DuplicatePolicy.FAIL ? "ObjectType.failDirectRows" : "ObjectType.freshDirectRows", direct.size()); //$NON-NLS-1$ //$NON-NLS-2$
      recordDiagnosticCounter(accessor, duplicatePolicy == DuplicatePolicy.FAIL ? "ObjectType.failDirectBatches" : "ObjectType.freshDirectBatches", 1); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (unknown.isEmpty())
    {
      // This is the common all-fresh case. No ObjectType prequery is needed.
      return;
    }

    if (!supportsSavepoints)
    {
      // SAFE duplicate recovery depends on rolling back a failed JDBC batch.
      // When the connection cannot provide savepoints, preserve the original
      // single-row behavior instead of attempting an unsafe batch.
      recordDiagnosticCounter(accessor, "ObjectType.synchronousFallback", unknown.size()); //$NON-NLS-1$

      for (InternalCDORevision revision : unknown)
      {
        putObjectType(accessor, revision.getTimeStamp(), revision.getID(), type);
      }

      return;
    }

    recordDiagnosticCounter(accessor, "ObjectType.safePrequery", 1); //$NON-NLS-1$
    recordDiagnosticCounter(accessor, "ObjectType.prequeryChunk", 1); //$NON-NLS-1$

    Set<CDOID> existingIDs = queryExistingIDs(accessor, unknown);
    List<InternalCDORevision> candidates = new ArrayList<>(unknown.size());

    // Existing rows are sent through putObjectType(), which retains its
    // established duplicate semantics. Only IDs absent from the prequery are
    // allowed into the SAFE batch; a race after this query is handled by the
    // savepoint/replay logic in insertObjectTypeBatch().
    for (InternalCDORevision revision : unknown)
    {
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
      insertObjectTypeBatch(accessor, candidates, type, batchSize, true);
    }
  }

  private Set<CDOID> queryExistingIDs(IDBStoreAccessor accessor, List<InternalCDORevision> revisions)
  {
    /*
     * Build one parameterized IN query for exactly the supplied SAFE candidates. This method answers only whether an
     * ObjectType row exists; it does not inspect revision versions or decide whether the row's class is compatible.
     * Those decisions remain in putObjectType(), preserving the existing single-item duplicate behavior. A parameter is
     * generated for every revision, including IDs that may be duplicated within the input. The database naturally
     * collapses repeated matches, while the caller continues to process every revision in input order. The list is
     * non-empty by construction because the caller invokes this method only after checking unknown.isEmpty().
     */
    StringBuilder builder = new StringBuilder("SELECT "); //$NON-NLS-1$
    builder.append(id);
    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(table());
    builder.append(" WHERE "); //$NON-NLS-1$
    builder.append(id);
    builder.append(" IN ("); //$NON-NLS-1$

    for (int i = 0; i < revisions.size(); i++)
    {
      if (i != 0)
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

      // Use the configured IIDHandler so Long, UUID, String, and external ID
      // encodings are bound exactly as they are for ObjectType inserts.
      for (int i = 0; i < revisions.size(); i++)
      {
        idHandler.setCDOID(stmt, column++, revisions.get(i).getID());
      }

      resultSet = stmt.executeQuery();
      Set<CDOID> result = new HashSet<>();

      // Return only IDs that actually have an ObjectType row. The returned
      // set is intentionally independent from the input list and is safe for
      // membership checks by the caller.
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
      // Both resources are closed on success and on every SQL failure. The
      // result set is closed first because it is owned by the statement.
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  private void insertObjectTypeBatch(IDBStoreAccessor accessor, List<InternalCDORevision> candidates, EClass type, int batchSize, boolean replayOnFailure)
  {
    /*
     * This method has two intentionally different contracts: - replayOnFailure == false is the direct path used for
     * fresh store-allocated IDs and for all candidates in FAIL mode. It does not create a savepoint, and every
     * SQL/batch failure is propagated as a database failure. A duplicate is therefore an invariant violation for the
     * commit. - replayOnFailure == true is the SAFE path for IDs whose provenance is unknown. The savepoint protects
     * the complete batch. If execution fails (most importantly because a concurrent writer inserted an ID after the
     * prequery), the batch is rolled back and every candidate is retried through putObjectType(), which has the
     * established duplicate handling and adapter-specific savepoint behavior. The caller has already enforced batchSize
     * > 1 and the desired chunk boundaries. This method therefore never creates a one-row JDBC batch.
     */
    Connection connection = accessor.getConnection();
    Savepoint savepoint = null;

    if (replayOnFailure)
    {
      // The savepoint must be taken before the first batched insert so that a
      // duplicate-key transaction-abort adapter (for example PostgreSQL) can
      // restore the connection to a usable state before replay.
      try
      {
        savepoint = connection.setSavepoint();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }

    IDBPreparedStatement preparedStatement = accessor.getDBConnection().prepareStatement(sqlInsert, ReuseProbability.MAX);
    BatchedStatement stmt = DBUtil.batched(preparedStatement, batchSize);

    try
    {
      IIDHandler idHandler = store().getIDHandler();

      for (InternalCDORevision revision : candidates)
      {
        // All rows in a call share the same EClass, but their timestamps may
        // differ. Resolve the metadata ID at each row's commit timestamp so
        // historical/evolving metadata keeps the same semantics as the
        // synchronous putObjectType() path.
        int column = 1;
        idHandler.setCDOID(stmt, column++, revision.getID());
        CDOID metaID = store().getMetaDataManager().getMetaID(type, revision.getTimeStamp());
        idHandler.setCDOID(stmt, column++, metaID);
        stmt.setLong(column, revision.getTimeStamp());
        stmt.executeUpdate();
      }

      stmt.flush();
      // Some JDBC drivers report SUCCESS_NO_INFO. validateBatchResult()
      // accepts those results but still rejects a batch that demonstrably did
      // not execute all candidate rows.
      validateBatchResult(stmt, candidates.size());

      if (savepoint != null)
      {
        connection.releaseSavepoint(savepoint);
      }

      recordDiagnosticCounter(accessor, "ObjectType.insertEntries", candidates.size()); //$NON-NLS-1$
      recordDiagnosticCounter(accessor, "ObjectType.insertExecutions", stmt.getExecutionCount()); //$NON-NLS-1$
    }
    catch (SQLException | DBException ex)
    {
      if (!replayOnFailure)
      {
        // Direct mode deliberately does not inspect duplicate exceptions or
        // attempt recovery. The enclosing commit must fail atomically.
        throw ex instanceof DBException ? (DBException)ex : new DBException(ex);
      }

      // SAFE mode must discard all partial batch effects before replaying.
      // Discard the JDBC batch even if rollback/release itself reports an
      // error; rollbackBatch() preserves that error as a suppressed cause.
      try
      {
        rollbackBatch(connection, savepoint, ex);
      }
      finally
      {
        discardBatch(stmt);
      }

      recordDiagnosticCounter(accessor, "ObjectType.safeReplayFallback", 1); //$NON-NLS-1$

      for (InternalCDORevision revision : candidates)
      {
        // Replay one row at a time to recover the prequery race and to retain
        // putObjectType()'s exact duplicate semantics for each ID.
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
    String policy = properties == null ? null : properties.get(IDBStore.Props.OBJECT_TYPE_DUPLICATE_POLICY);
    duplicatePolicy = policy == null ? DuplicatePolicy.SAFE : DuplicatePolicy.valueOf(policy.trim().toUpperCase(Locale.ROOT));
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
    if (BatchingContext.isStatisticsEnabled())
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

  private enum DuplicatePolicy
  {
    SAFE, FAIL
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
