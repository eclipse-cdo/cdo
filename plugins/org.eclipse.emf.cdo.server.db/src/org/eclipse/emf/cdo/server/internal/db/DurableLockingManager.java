/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaAlreadyExistsException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;

import org.eclipse.net4j.db.Batch;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DurableLockingManager extends Lifecycle implements IBranchDeletionSupport
{
  private DBStore store;

  private final LockAreasTable lockAreas;

  private final LocksTable locks;

  private IDBTable lockAreasTable;

  private IDBTable locksTable;

  private String sqlInsertLockArea;

  private String sqlSelectLockArea;

  private String sqlSelectAllLockAreas;

  private String sqlSelectLockAreas;

  private String sqlDeleteLockArea;

  private String sqlDeleteLockAreas;

  private String sqlSelectLocks;

  private String sqlSelectLock;

  private String sqlInsertLock;

  private String sqlUpdateLock;

  private String sqlDeleteLock;

  private String sqlDeleteLocks;

  public DurableLockingManager(DBStore store)
  {
    this.store = store;
    lockAreas = new LockAreasTable(store);
    locks = new LocksTable(store);
  }

  public synchronized LockArea createLockArea(DBStoreAccessor accessor, String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks)
  {
    if (durableLockingID == null)
    {
      durableLockingID = getNextDurableLockingID(accessor);
    }
    else
    {
      // If the caller is specifying the ID, make sure there is no area with this ID yet
      //
      try
      {
        getLockArea(accessor, durableLockingID);
        throw new LockAreaAlreadyExistsException(durableLockingID);
      }
      catch (LockAreaNotFoundException good)
      {
      }
    }

    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlInsertLockArea, ReuseProbability.LOW);

    try
    {
      stmt.setString(1, durableLockingID);
      stmt.setString(2, userID);
      stmt.setInt(3, branchPoint.getBranch().getID());
      stmt.setLong(4, branchPoint.getTimeStamp());
      stmt.setBoolean(5, readOnly);

      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }

    if (!locks.isEmpty())
    {
      insertLocks(accessor, durableLockingID, locks);
    }

    commit(accessor);

    return CDOLockUtil.createLockArea(durableLockingID, userID, branchPoint, readOnly, locks);
  }

  private void insertLocks(DBStoreAccessor accessor, String durableLockingID, Map<CDOID, LockGrade> locks)
  {
    IIDHandler idHandler = store.getIDHandler();
    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlInsertLock, ReuseProbability.MEDIUM);

    try
    {
      stmt.setString(1, durableLockingID);

      for (Map.Entry<CDOID, LockGrade> entry : locks.entrySet())
      {
        CDOID id = entry.getKey();
        int grade = entry.getValue().getValue();

        idHandler.setCDOID(stmt, 2, id);
        stmt.setInt(3, grade);

        DBUtil.update(stmt, true);
      }
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

  public LockArea getLockArea(DBStoreAccessor accessor, String durableLockingID) throws LockAreaNotFoundException
  {
    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlSelectLockArea, ReuseProbability.MEDIUM);
    ResultSet resultSet = null;

    try
    {
      stmt.setString(1, durableLockingID);

      resultSet = stmt.executeQuery();
      if (!resultSet.next())
      {
        throw new LockAreaNotFoundException(durableLockingID);
      }

      String userID = resultSet.getString(1);
      int branchID = resultSet.getInt(2);
      long timeStamp = resultSet.getLong(3);
      boolean readOnly = resultSet.getBoolean(4);

      return makeLockArea(accessor, durableLockingID, userID, branchID, timeStamp, readOnly);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  public void getLockAreas(DBStoreAccessor accessor, String userIDPrefix, Handler handler)
  {
    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = null;
    ResultSet resultSet = null;

    try
    {
      if (userIDPrefix.length() == 0)
      {
        stmt = connection.prepareStatement(sqlSelectAllLockAreas, ReuseProbability.MEDIUM);
      }
      else
      {
        stmt = connection.prepareStatement(sqlSelectLockAreas, ReuseProbability.MEDIUM);
        stmt.setString(1, userIDPrefix + "%");
      }

      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        String durableLockingID = resultSet.getString(1);
        String userID = resultSet.getString(2);
        int branchID = resultSet.getInt(3);
        long timeStamp = resultSet.getLong(4);
        boolean readOnly = resultSet.getBoolean(5);

        LockArea area = makeLockArea(accessor, durableLockingID, userID, branchID, timeStamp, readOnly);
        if (!handler.handleLockArea(area))
        {
          break;
        }
      }
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  public void deleteLockArea(DBStoreAccessor accessor, String durableLockingID)
  {
    unlockWithoutCommit(accessor, durableLockingID);

    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlDeleteLockArea, ReuseProbability.LOW);

    try
    {
      stmt.setString(1, durableLockingID);
      DBUtil.update(stmt, true);
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(stmt);
    }

    commit(accessor);
  }

  public void updateLockArea(DBStoreAccessor accessor, LockArea area)
  {
    String areaID = area.getDurableLockingID();
    unlockWithoutCommit(accessor, areaID);
    insertLocks(accessor, areaID, area.getLocks());
    commit(accessor);
  }

  public void lock(DBStoreAccessor accessor, String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    changeLocks(accessor, durableLockingID, type, objectsToLock, true);
  }

  public void unlock(DBStoreAccessor accessor, String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    if (objectsToUnlock == null)
    {
      unlockWithoutCommit(accessor, durableLockingID);
      commit(accessor);
    }
    else
    {
      changeLocks(accessor, durableLockingID, type, objectsToUnlock, false);
    }
  }

  private void unlockWithoutCommit(DBStoreAccessor accessor, String durableLockingID)
  {
    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlDeleteLocks, ReuseProbability.MEDIUM);

    try
    {
      stmt.setString(1, durableLockingID);
      DBUtil.update(stmt, false);
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
  protected void doActivate() throws Exception
  {
    super.doActivate();
    lockAreas.activate();
    locks.activate();

    // Lock areas

    sqlInsertLockArea = "INSERT INTO " + lockAreas + "(" + lockAreas.id + "," + lockAreas.userID + "," + lockAreas.viewBranch + "," + lockAreas.viewTime + ","
        + lockAreas.readOnly + ") VALUES (?, ?, ?, ?, ?)";

    sqlSelectLockArea = "SELECT " + lockAreas.userID + "," + lockAreas.viewBranch + "," + lockAreas.viewTime + "," + lockAreas.readOnly + " FROM " + lockAreas
        + " WHERE " + lockAreas.id + "=?";

    sqlSelectAllLockAreas = "SELECT " + lockAreas.id + "," + lockAreas.userID + "," + lockAreas.viewBranch + "," + lockAreas.viewTime + "," + lockAreas.readOnly
        + " FROM " + lockAreas;

    sqlSelectLockAreas = sqlSelectAllLockAreas + " WHERE " + lockAreas.userID + " LIKE ?";

    sqlDeleteLockArea = "DELETE FROM " + lockAreas + " WHERE " + lockAreas.id + "=?";

    sqlDeleteLockAreas = "DELETE FROM " + lockAreas + " WHERE EXISTS (SELECT * FROM " + locks + " WHERE " + locks + "." + locks.areaID + "=" + lockAreas + "."
        + lockAreas.id + ")";

    // Locks

    sqlSelectLocks = "SELECT " + locks.objectID + "," + locks.lockGrade + " FROM " + locks + " WHERE " + locks.areaID + "=?";

    sqlSelectLock = "SELECT " + locks.lockGrade + " FROM " + locks + " WHERE " + locks.areaID + "=? AND " + locks.objectID + "=?";

    sqlInsertLock = "INSERT INTO " + locks + "(" + locks.areaID + "," + locks.objectID + "," + locks.lockGrade + ") VALUES (?, ?, ?)";

    sqlUpdateLock = "UPDATE " + locks + " SET " + locks.lockGrade + "=? " + " WHERE " + locks.areaID + "=? AND " + locks.objectID + "=?";

    sqlDeleteLock = "DELETE FROM " + locks + " WHERE " + locks.areaID + "=? AND " + locks.objectID + "=?";

    sqlDeleteLocks = "DELETE FROM " + locks + " WHERE " + locks.areaID + "=?";
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    locks.deactivate();
    lockAreas.deactivate();
    super.doDeactivate();
  }

  private String getNextDurableLockingID(DBStoreAccessor accessor)
  {
    for (;;)
    {
      String durableLockingID = CDOLockUtil.createDurableLockingID();

      try
      {
        getLockArea(accessor, durableLockingID); // Check uniqueness
        // Not unique; try once more...
      }
      catch (LockAreaNotFoundException ex)
      {
        return durableLockingID;
      }
    }
  }

  private LockArea makeLockArea(DBStoreAccessor accessor, String durableLockingID, String userID, int branchID, long timeStamp, boolean readOnly)
  {
    InternalCDOBranchManager branchManager = store.getRepository().getBranchManager();
    CDOBranchPoint branchPoint = branchManager.getBranch(branchID).getPoint(timeStamp);
    Map<CDOID, LockGrade> lockMap = getLockMap(accessor, durableLockingID);
    return CDOLockUtil.createLockArea(durableLockingID, userID, branchPoint, readOnly, lockMap);
  }

  private Map<CDOID, LockGrade> getLockMap(DBStoreAccessor accessor, String durableLockingID)
  {
    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlSelectLocks, ReuseProbability.MEDIUM);
    ResultSet resultSet = null;

    try
    {
      stmt.setString(1, durableLockingID);
      resultSet = stmt.executeQuery();

      IIDHandler idHandler = store.getIDHandler();
      Map<CDOID, LockGrade> lockMap = CDOIDUtil.createMap();

      while (resultSet.next())
      {
        CDOID id = idHandler.getCDOID(resultSet, 1);
        int grade = resultSet.getInt(2);

        lockMap.put(id, LockGrade.get(grade));
      }

      return lockMap;
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  private void changeLocks(DBStoreAccessor accessor, String durableLockingID, LockType type, Collection<? extends Object> keys, boolean on)
  {
    if (keys.isEmpty())
    {
      return;
    }

    String sqlInsertOrDelete = on ? sqlInsertLock : sqlDeleteLock;

    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmtSelect = connection.prepareStatement(sqlSelectLock, ReuseProbability.MEDIUM);
    IDBPreparedStatement stmtInsertOrDelete = connection.prepareStatement(sqlInsertOrDelete, ReuseProbability.MEDIUM);
    IDBPreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdateLock, ReuseProbability.MEDIUM);
    ResultSet resultSet = null;

    InternalLockManager lockManager = store.getRepository().getLockingManager();
    IIDHandler idHandler = store.getIDHandler();

    try
    {
      stmtSelect.setString(1, durableLockingID);
      stmtInsertOrDelete.setString(1, durableLockingID);
      stmtUpdate.setString(2, durableLockingID);

      for (Object key : keys)
      {
        CDOID id = lockManager.getLockKeyID(key);
        idHandler.setCDOID(stmtSelect, 2, id);
        resultSet = stmtSelect.executeQuery();

        LockGrade oldGrade = LockGrade.NONE;
        if (resultSet.next())
        {
          oldGrade = LockGrade.get(resultSet.getInt(1));
        }

        LockGrade newGrade = oldGrade.getUpdated(type, on);
        if (on && oldGrade == LockGrade.NONE)
        {
          idHandler.setCDOID(stmtInsertOrDelete, 2, id);
          stmtInsertOrDelete.setInt(3, newGrade.getValue());
          DBUtil.update(stmtInsertOrDelete, true);
        }
        else if (!on && newGrade == LockGrade.NONE)
        {
          idHandler.setCDOID(stmtInsertOrDelete, 2, id);
          DBUtil.update(stmtInsertOrDelete, true);
        }
        else
        {
          stmtUpdate.setInt(1, newGrade.getValue());
          idHandler.setCDOID(stmtUpdate, 3, id);
          DBUtil.update(stmtUpdate, true);
        }
      }

      connection.commit();
    }
    catch (SQLException e)
    {
      throw new DBException(e);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmtUpdate);
      DBUtil.close(stmtInsertOrDelete);
      DBUtil.close(stmtSelect);
    }
  }

  @Override
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList)
  {
    // Delete the lock areas.
    batch.add("DELETE FROM " + lockAreas + " WHERE " + lockAreas.viewBranch + " IN (" + idList + ")");

    // Delete the locks.
    batch.add("DELETE FROM " + locks + " WHERE " + locks.areaID + " NOT IN (SELECT " + lockAreas.id + " FROM " + lockAreas + " WHERE " + lockAreas.id + "="
        + locks.areaID + ")");
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    DBUtil.serializeTable(out, connection, lockAreasTable, null, null);
    DBUtil.serializeTable(out, connection, locksTable, null, null);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    monitor.begin(4);

    try
    {
      // Delete all non-empty lock areas
      DBUtil.update(connection, sqlDeleteLockAreas);
      monitor.worked();

      DBUtil.deserializeTable(in, connection, lockAreasTable, monitor.fork());

      DBUtil.clearTable(connection, locksTable);
      monitor.worked();

      DBUtil.deserializeTable(in, connection, locksTable, monitor.fork());
    }
    finally
    {
      monitor.done();
    }
  }

  private static void commit(DBStoreAccessor accessor)
  {
    try
    {
      IDBConnection connection = accessor.getDBConnection();
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LockAreasTable extends DBStoreTable
  {
    public IDBField id;

    public IDBField userID;

    public IDBField viewBranch;

    public IDBField viewTime;

    public IDBField readOnly;

    public LockAreasTable(IDBStore store)
    {
      super(store, NAMES.LOCK_AREAS);
    }

    @Override
    protected void firstActivate(IDBTable table)
    {
      id = table.addField(NAMES.ID, DBType.VARCHAR, true);
      userID = table.addField(NAMES.USER_ID, DBType.VARCHAR);
      viewBranch = table.addField(NAMES.VIEW_BRANCH, DBType.INTEGER);
      viewTime = table.addField(NAMES.VIEW_TIME, DBType.BIGINT);
      readOnly = table.addField(NAMES.READ_ONLY, DBType.BOOLEAN);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);
      table.addIndex(IDBIndex.Type.NON_UNIQUE, userID);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      id = table.getField(NAMES.ID);
      userID = table.getField(NAMES.USER_ID);
      viewBranch = table.getField(NAMES.VIEW_BRANCH);
      viewTime = table.getField(NAMES.VIEW_TIME);
      readOnly = table.getField(NAMES.READ_ONLY);
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String LOCK_AREAS = name("cdo_lock_areas");

      private static final String ID = name("id");

      private static final String USER_ID = name("user_id");

      private static final String VIEW_BRANCH = name("view_branch");

      private static final String VIEW_TIME = name("view_time");

      private static final String READ_ONLY = name("read_only");

      private static String name(String name)
      {
        return DBUtil.name(name, LockAreasTable.class);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LocksTable extends DBStoreTable
  {
    public IDBField areaID;

    public IDBField objectID;

    public IDBField lockGrade;

    public LocksTable(IDBStore store)
    {
      super(store, NAMES.LOCKS);
    }

    @Override
    protected void firstActivate(IDBTable table)
    {
      int idLength = store().getIDColumnLength();
      DBType idType = store().getIDHandler().getDBType();

      areaID = table.addField(NAMES.AREA_ID, DBType.VARCHAR, true);
      objectID = table.addField(NAMES.OBJECT_ID, idType, idLength, true);
      lockGrade = table.addField(NAMES.LOCK_GRADE, DBType.INTEGER);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, areaID, objectID);
      table.addIndex(IDBIndex.Type.NON_UNIQUE, areaID);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      areaID = table.getField(NAMES.AREA_ID);
      objectID = table.getField(NAMES.OBJECT_ID);
      lockGrade = table.getField(NAMES.LOCK_GRADE);
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String LOCKS = name("cdo_locks");

      private static final String AREA_ID = name("area_id");

      private static final String OBJECT_ID = name("object_id");

      private static final String LOCK_GRADE = name("lock_grade");

      private static String name(String name)
      {
        return DBUtil.name(name, LocksTable.class);
      }
    }
  }
}
