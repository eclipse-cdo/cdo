/*
 * Copyright (c) 2016, 2019, 2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.CommitInfoStorage;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.Batch;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBDatabase.RunnableWithSchema;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 * @since 4.6
 */
public class CommitInfoTable extends DBStoreTable implements IBranchDeletionSupport
{
  private boolean withMergeSource;

  private IDBField timeStamp;

  private IDBField previousTimeStamp;

  private IDBField branch;

  private IDBField user;

  private IDBField comment;

  private IDBField mergedBranch;

  private IDBField mergedTimeStamp;

  @ExcludeFromDump
  private transient SQLStatement sqlInsert;

  public CommitInfoTable(IDBStore store)
  {
    super(store, NAMES.COMMIT_INFOS);
  }

  public void writeCommitInfo(IDBStoreAccessor accessor, CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment,
      CDOBranchPoint mergeSource, OMMonitor monitor)
  {
    IDBPreparedStatement stmt = DBUtil.prepare(sqlInsert, accessor.getDBConnection(), ReuseProbability.HIGH);

    try
    {
      if (withMergeSource)
      {
        if (mergeSource != null)
        {
          sqlInsert.bind(stmt, timeStamp, previousTimeStamp, branch.getID(), userID, comment, mergeSource.getBranch().getID(), mergeSource.getTimeStamp());
        }
        else
        {
          sqlInsert.bind(stmt, timeStamp, previousTimeStamp, branch.getID(), userID, comment, (Object)null, (Object)null);
        }
      }
      else
      {
        sqlInsert.bind(stmt, timeStamp, previousTimeStamp, branch.getID(), userID, comment);
      }

      DBUtil.update(stmt, true);
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

  public void loadCommitInfos(IDBStoreAccessor accessor, CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    int count = CDOCommitInfoUtil.decodeCount(endTime);
    IDBAdapter dbAdapter = store().getDBAdapter();

    StringBuilder builder = new StringBuilder();
    builder.append("SELECT "); //$NON-NLS-1$
    builder.append(dbAdapter.quoteIdentifier(timeStamp.getName()));
    builder.append(", "); //$NON-NLS-1$
    builder.append(dbAdapter.quoteIdentifier(previousTimeStamp.getName()));
    builder.append(", "); //$NON-NLS-1$
    builder.append(dbAdapter.quoteIdentifier(user.getName()));
    builder.append(", "); //$NON-NLS-1$
    builder.append(dbAdapter.quoteIdentifier(comment.getName()));

    if (branch == null)
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(dbAdapter.quoteIdentifier(this.branch.getName()));
    }

    if (withMergeSource)
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(dbAdapter.quoteIdentifier(mergedBranch.getName()));
      builder.append(", "); //$NON-NLS-1$
      builder.append(dbAdapter.quoteIdentifier(mergedTimeStamp.getName()));
    }

    builder.append(" FROM "); //$NON-NLS-1$
    builder.append(dbAdapter.quoteTableName(getTable()));
    boolean where = false;

    if (branch != null)
    {
      builder.append(where ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      builder.append(dbAdapter.quoteIdentifier(this.branch.getName()));
      builder.append("="); //$NON-NLS-1$
      builder.append(branch.getID());
      where = true;
    }

    if (startTime != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(where ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      builder.append(dbAdapter.quoteIdentifier(timeStamp.getName()));
      builder.append(count < 0 ? "<=" : ">="); //$NON-NLS-1$
      builder.append(startTime);
      where = true;
    }

    if (endTime > CDOBranchPoint.UNSPECIFIED_DATE)
    {
      builder.append(where ? " AND " : " WHERE "); //$NON-NLS-1$ //$NON-NLS-2$
      builder.append(dbAdapter.quoteIdentifier(timeStamp.getName()));
      builder.append("<="); //$NON-NLS-1$
      builder.append(endTime);
      where = true;
    }

    builder.append(" ORDER BY "); //$NON-NLS-1$
    builder.append(dbAdapter.quoteIdentifier(timeStamp.getName()));
    builder.append(count < 0 || CDOBranchPoint.UNSPECIFIED_DATE <= endTime && endTime <= startTime ? " DESC" : " ASC"); //$NON-NLS-1$
    String sql = builder.toString();

    IDBPreparedStatement stmt = accessor.getDBConnection().prepareStatement(sql, ReuseProbability.LOW);
    ResultSet resultSet = null;

    InternalRepository repository = (InternalRepository)store().getRepository();
    InternalCDOBranchManager branchManager = repository.getBranchManager();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    count = Math.abs(count);

    try
    {
      resultSet = stmt.executeQuery();
      while (count-- > 0 && resultSet.next())
      {
        int column = 0;

        long timeStamp = resultSet.getLong(++column);
        long previousTimeStamp = resultSet.getLong(++column);
        String userID = resultSet.getString(++column);
        String comment = resultSet.getString(++column);

        CDOBranch infoBranch = branch;
        if (infoBranch == null)
        {
          int id = resultSet.getInt(++column);
          infoBranch = branchManager.getBranch(id);
        }

        CDOBranchPoint mergeSource = null;
        if (withMergeSource)
        {
          int id = resultSet.getInt(++column);
          if (!resultSet.wasNull())
          {
            InternalCDOBranch mergedBranch = branchManager.getBranch(id);

            long mergedTimeStamp = resultSet.getLong(++column);
            mergeSource = mergedBranch.getPoint(mergedTimeStamp);
          }
        }

        CDOCommitInfo commitInfo = commitInfoManager.createCommitInfo(infoBranch, timeStamp, previousTimeStamp, userID, comment, mergeSource, null);
        handler.handleCommitInfo(commitInfo);
      }
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
  public void deleteBranches(IDBStoreAccessor accessor, Batch batch, String idList)
  {
    IDBAdapter dbAdapter = store().getDBAdapter();
    String tableName = dbAdapter.quoteTableName(getTable());
    String branchName = dbAdapter.quoteIdentifier(branch.getName());
    String timeStampName = dbAdapter.quoteIdentifier(timeStamp.getName());
    String previousTimeStampName = dbAdapter.quoteIdentifier(previousTimeStamp.getName());

    // Delete the commit infos.
    batch.add("DELETE FROM " + tableName + " WHERE " + branchName + " IN (" + idList + ")");

    // Adjust the previous times.
    batch.add("UPDATE " + tableName + " upd SET " + previousTimeStampName + "=(SELECT MAX(" + timeStampName + ") FROM " + tableName + " WHERE " + timeStampName
        + "<upd." + timeStampName + ") WHERE " + timeStampName + " IN (SELECT " + timeStampName + " FROM " + tableName + " WHERE " + previousTimeStampName
        + " NOT IN (SELECT " + timeStampName + " FROM " + tableName + "))");
  }

  public void rawExport(Connection connection, CDODataOutput out, long fromCommitTime, long toCommitTime) throws IOException
  {
    out.writeBoolean(withMergeSource);

    String where = " WHERE " + store().getDBAdapter().quoteIdentifier(timeStamp.getName()) + " BETWEEN " + fromCommitTime + " AND " + toCommitTime;
    DBUtil.serializeTable(out, connection, getTable(), null, where);
  }

  public void rawImport(Connection connection, CDODataInput in, long fromCommitTime, long toCommitTime, OMMonitor monitor) throws IOException
  {
    boolean actualWithMergeSource = in.readBoolean();
    if (actualWithMergeSource != withMergeSource)
    {
      throw new IllegalStateException("Commit info data mismatch. Expected: " + (withMergeSource ? "with" : "without") + " merge source. Actual: "
          + (actualWithMergeSource ? "with" : "without") + " merge source.");
    }

    DBUtil.deserializeTable(in, connection, getTable(), monitor.fork());
  }

  public void repairAfterCrash(Connection connection)
  {
    long lastCommitTime = DBUtil.selectMaximumLong(connection, timeStamp);
    String branchName = store().getDBAdapter().quoteIdentifier(branch.getName());
    long lastNonLocalCommitTime = DBUtil.selectMaximumLong(connection, timeStamp, CDOBranch.MAIN_BRANCH_ID + "<=" + branchName);

    if (lastNonLocalCommitTime == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      lastNonLocalCommitTime = lastCommitTime;
    }

    DBStore store = (DBStore)store();
    store.setLastCommitTime(lastCommitTime);
    store.setLastNonLocalCommitTime(lastNonLocalCommitTime);
  }

  @Override
  protected void firstActivate(IDBTable table)
  {
    timeStamp = table.addField(NAMES.TIMESTAMP, DBType.BIGINT, true);
    previousTimeStamp = table.addField(NAMES.PREVIOUS_TIMESTAMP, DBType.BIGINT);
    branch = table.addField(NAMES.BRANCH, DBType.INTEGER);
    user = table.addField(NAMES.USER, DBType.VARCHAR);
    comment = table.addField(NAMES.COMMENT, DBType.VARCHAR);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, timeStamp);
    table.addIndex(IDBIndex.Type.NON_UNIQUE, branch);

    if (withMergeSource)
    {
      addMergeSourceFields(table);
    }
  }

  @Override
  protected void reActivate(IDBTable table)
  {
    timeStamp = table.getField(NAMES.TIMESTAMP);
    previousTimeStamp = table.getField(NAMES.PREVIOUS_TIMESTAMP);
    branch = table.getField(NAMES.BRANCH);
    user = table.getField(NAMES.USER);
    comment = table.getField(NAMES.COMMENT);
    mergedBranch = table.getField(NAMES.MERGED_BRANCH);
    mergedTimeStamp = table.getField(NAMES.MERGED_TIMESTAMP);

    if (withMergeSource && mergedBranch == null)
    {
      store().getDatabase().updateSchema(new RunnableWithSchema()
      {
        @Override
        public void run(IDBSchema schema)
        {
          IDBTable table = schema.getTable(NAMES.COMMIT_INFOS);
          addMergeSourceFields(table);
        }
      });
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    withMergeSource = store().getRepository().getCommitInfoStorage() == CommitInfoStorage.WITH_MERGE_SOURCE;

    super.doActivate();

    if (withMergeSource)
    {
      sqlInsert = SQL.insertInto(timeStamp.getTable()) //
          .columns(timeStamp, previousTimeStamp, branch, user, comment, mergedBranch, mergedTimeStamp) //
          .values(SQL.param(), SQL.param(), SQL.param(), SQL.param(), SQL.param(), SQL.param(), SQL.param()) //
          .compile(adapter());
    }
    else
    {
      sqlInsert = SQL.insertInto(timeStamp.getTable()) //
          .columns(timeStamp, previousTimeStamp, branch, user, comment) //
          .values(SQL.param(), SQL.param(), SQL.param(), SQL.param(), SQL.param()) //
          .compile(adapter());
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    sqlInsert = null;
    super.doDeactivate();
  }

  private void addMergeSourceFields(IDBTable table)
  {
    mergedBranch = table.addField(NAMES.MERGED_BRANCH, DBType.INTEGER);
    mergedTimeStamp = table.addField(NAMES.MERGED_TIMESTAMP, DBType.BIGINT);

    table.addIndex(IDBIndex.Type.NON_UNIQUE, mergedBranch, mergedTimeStamp);
  }

  /**
   * @author Eike Stepper
   */
  private static final class NAMES
  {
    private static final String COMMIT_INFOS = name("cdo_commit_infos"); //$NON-NLS-1$

    private static final String TIMESTAMP = name("commit_time"); //$NON-NLS-1$

    private static final String PREVIOUS_TIMESTAMP = name("previous_time");

    private static final String BRANCH = name("branch_id"); //$NON-NLS-1$

    private static final String USER = name("user_id"); //$NON-NLS-1$

    private static final String COMMENT = name("commit_comment"); //$NON-NLS-1$

    private static final String MERGED_BRANCH = name("merged_branch"); //$NON-NLS-1$

    private static final String MERGED_TIMESTAMP = name("merged_time"); //$NON-NLS-1$

    private static String name(String name)
    {
      return DBUtil.name(name, CommitInfoTable.class);
    }
  }
}
