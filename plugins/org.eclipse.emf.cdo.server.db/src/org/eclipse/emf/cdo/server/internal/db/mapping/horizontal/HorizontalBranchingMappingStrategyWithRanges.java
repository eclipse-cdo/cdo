/*
 * Copyright (c) 2010-2013, 2016-2020, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - major refactoring
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IListMapping;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.DBUtil.DeserializeRowHandler;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLCondition;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.db.dml.SQLTableRef;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class HorizontalBranchingMappingStrategyWithRanges extends HorizontalBranchingMappingStrategy
{
  private boolean copyOnBranch;

  public HorizontalBranchingMappingStrategyWithRanges()
  {
  }

  @Override
  public boolean hasDeltaSupport()
  {
    return true;
  }

  public boolean shallCopyOnBranch()
  {
    return copyOnBranch;
  }

  @Override
  protected IClassMapping doCreateClassMapping(EClass eClass)
  {
    return new HorizontalBranchingClassMapping(this, eClass);
  }

  @Override
  public IListMapping doCreateListMapping(EClass containingClass, EStructuralFeature feature)
  {
    return new BranchingListTableMappingWithRanges(this, containingClass, feature);
  }

  @Override
  protected void rawExportList(CDODataOutput out, IDBConnection connection, IListMapping listMapping, IDBTable attrTable, String attrSuffix,
      long fromCommitTime, long toCommitTime) throws IOException
  {
    super.rawExportList(out, connection, listMapping, attrTable, attrSuffix);

    for (IDBTable table : listMapping.getDBTables())
    {
      rawExportListPostProcess(out, connection, attrTable, fromCommitTime, toCommitTime, table);
    }
  }

  private void rawExportListPostProcess(CDODataOutput out, IDBConnection connection, IDBTable attrTable, long fromCommitTime, long toCommitTime, IDBTable table)
      throws IOException
  {
    IDBField listID = table.getField(MappingNames.LIST_REVISION_ID);
    IDBTable listTable = listID.getTable();
    IDBField listBranch = listTable.getField(MappingNames.LIST_REVISION_BRANCH);
    IDBField listVersionAdded = listTable.getField(MappingNames.LIST_REVISION_VERSION_ADDED);
    IDBField listVersionRemoved = listTable.getField(MappingNames.LIST_REVISION_VERSION_REMOVED);
    IDBField listIndex = listTable.getField(MappingNames.LIST_IDX);
    IDBField attrID = attrTable.getField(MappingNames.ATTRIBUTES_ID);
    IDBTable attributesTable = attrID.getTable();
    IDBField attrBranch = attributesTable.getField(MappingNames.ATTRIBUTES_BRANCH);
    IDBField attrVersion = attributesTable.getField(MappingNames.ATTRIBUTES_VERSION);
    IDBField attrCreated = attributesTable.getField(MappingNames.ATTRIBUTES_CREATED);

    SQLTableRef listRef = SQL.as(listTable, "l_t"); //$NON-NLS-1$
    SQLTableRef attrRef = SQL.as(attributesTable, "a_t"); //$NON-NLS-1$
    SQLCondition condition = SQL.eq(listRef.column(listID), attrRef.column(attrID)).and(SQL.eq(listRef.column(listVersionRemoved), attrRef.column(attrVersion)))
        .and(SQL.eq(listRef.column(listBranch), attrRef.column(attrBranch)))
        .and(SQL.between(attrRef.column(attrCreated), SQL.literal(fromCommitTime), SQL.literal(toCommitTime)))
        .and(SQL.isNotNull(listRef.column(listVersionRemoved)));
    SQLStatement sql = SQL.select(listRef.column(listID) //
        , listRef.column(listBranch), listRef.column(listVersionAdded), listRef.column(listVersionRemoved), listRef.column(listIndex)) //
        .from(listRef) //
        .from(attrRef) //
        .where(condition) //
        .compile(getDBAdapter());
    DBUtil.trace(sql.getSQL());

    IDBPreparedStatement stmt = DBUtil.prepare(sql, connection, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ReuseProbability.MEDIUM);

    IIDHandler idHandler = getStore().getIDHandler();
    ResultSet resultSet = null;

    try
    {
      resultSet = stmt.executeQuery();

      // Write resultSet size for progress monitoring
      int size = DBUtil.getRowCount(resultSet);
      out.writeXInt(size);
      if (size == 0)
      {
        return;
      }

      while (resultSet.next())
      {
        CDOID source = idHandler.getCDOID(resultSet, 1);
        int branch = resultSet.getInt(2);
        int versionAdded = resultSet.getInt(3);
        int versionRemoved = resultSet.getInt(4);
        int idx = resultSet.getInt(5);

        out.writeCDOID(source);
        out.writeXInt(branch);
        out.writeXInt(versionAdded);
        out.writeXInt(versionRemoved);
        out.writeXInt(idx);
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
  protected void rawImportList(CDODataInput in, IDBConnection connection, IListMapping listMapping, OMMonitor monitor) throws IOException
  {
    Collection<IDBTable> tables = listMapping.getDBTables();
    int size = tables.size();
    if (size == 0)
    {
      return;
    }

    monitor.begin(2 * size);

    try
    {
      super.rawImportList(in, connection, listMapping, monitor.fork(size));

      for (IDBTable table : tables)
      {
        rawImportListPostProcess(in, connection, table, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  private void rawImportListPostProcess(CDODataInput in, IDBConnection connection, IDBTable table, OMMonitor monitor) throws IOException
  {
    int size = in.readXInt();
    if (size == 0)
    {
      return;
    }

    IDBField versionRemovedColumn = table.getField(MappingNames.LIST_REVISION_VERSION_REMOVED);
    IDBField sourceColumn = table.getField(MappingNames.LIST_REVISION_ID);
    IDBField branchColumn = table.getField(MappingNames.LIST_REVISION_BRANCH);
    IDBField versionAddedColumn = table.getField(MappingNames.LIST_REVISION_VERSION_ADDED);
    IDBField indexColumn = table.getField(MappingNames.LIST_IDX);

    SQLStatement sql = SQL.update(table) //
        .set(versionRemovedColumn).to(SQL.param()) //
        .where(SQL.eq(sourceColumn, SQL.param()).and(SQL.eq(branchColumn, SQL.param())).and(SQL.eq(versionAddedColumn, SQL.param()))
            .and(SQL.eq(indexColumn, SQL.param()))) //
        .compile(getDBAdapter());
    DBUtil.trace(sql.getSQL());

    IDBPreparedStatement stmt = DBUtil.prepare(sql, connection, ReuseProbability.MEDIUM);
    IIDHandler idHandler = getStore().getIDHandler();

    monitor.begin(1 + 2 * size);

    try
    {
      monitor.worked();

      for (int row = 0; row < size; row++)
      {
        CDOID source = in.readCDOID();
        int branch = in.readXInt();
        int versionAdded = in.readXInt();
        int versionRemoved = in.readXInt();
        int idx = in.readXInt();

        sql.bindParameter(stmt, 1, versionRemoved);
        sql.bindParameter(stmt, 2, source, idHandler);
        sql.bindParameter(stmt, 3, branch);
        sql.bindParameter(stmt, 4, versionAdded);
        sql.bindParameter(stmt, 5, idx);

        stmt.addBatch();
        monitor.worked();
      }

      Async async = monitor.forkAsync(size);

      try
      {
        stmt.executeBatch();
      }
      finally
      {
        async.stop();
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
      monitor.done();
    }
  }

  protected String getListJoinForPostProcess(String attrTable, String listTable)
  {
    String join = getListJoinBasic(attrTable, listTable);
    return modifyListJoin2(attrTable, listTable, join, true, true);
  }

  @Override
  protected String modifyListJoin(String attrTable, String listTable, String join, boolean forRawExport)
  {
    return modifyListJoin2(attrTable, listTable, join, forRawExport, false);
  }

  private String modifyListJoin2(String attrTable, String listTable, String join, boolean forRawExport, boolean forPostProcess)
  {
    join += " AND " + listTable + ".";

    if (forRawExport)
    {
      if (forPostProcess)
      {
        join += getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_VERSION_REMOVED);
      }
      else
      {
        join += getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_VERSION_ADDED);
      }

      join += "=" + attrTable + "." + getDBAdapter().quoteIdentifier(MappingNames.ATTRIBUTES_VERSION);
    }
    else
    {
      join += getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_VERSION_ADDED);
      join += "<=" + attrTable + "." + getDBAdapter().quoteIdentifier(MappingNames.ATTRIBUTES_VERSION);
      join += " AND (" + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_VERSION_REMOVED);
      join += " IS NULL OR " + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_VERSION_REMOVED);
      join += ">" + attrTable + "." + getDBAdapter().quoteIdentifier(MappingNames.ATTRIBUTES_VERSION) + ")";
    }

    join += " AND " + attrTable + "." + getDBAdapter().quoteIdentifier(MappingNames.ATTRIBUTES_BRANCH);
    join += "=" + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_BRANCH);

    if (forRawExport && !forPostProcess)
    {
      join += " ORDER BY " + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_ID);
      join += ", " + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_BRANCH);
      join += ", " + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_REVISION_VERSION_ADDED);
      join += ", " + listTable + "." + getDBAdapter().quoteIdentifier(MappingNames.LIST_IDX);
    }

    return join;
  }

  @Override
  protected DeserializeRowHandler getImportListHandler()
  {
    return new ImportListHandler();
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    String value = getProperties().get(CDODBUtil.PROP_COPY_ON_BRANCH);
    copyOnBranch = value == null ? false : Boolean.valueOf(value);
  }

  /**
   * @author Eike Stepper
   */
  private final class ImportListHandler implements DeserializeRowHandler
  {
    private final IIDHandler idHandler = getStore().getIDHandler();

    @ExcludeFromDump
    private transient SQLStatement sql;

    private IDBPreparedStatement stmt;

    @Override
    public void handleRow(ExtendedDataInput in, Connection connection, IDBField[] fields, Object[] values) throws SQLException, IOException
    {
      int versionAdded = DBUtil.asInt(values[2]);
      if (versionAdded == CDOBranchVersion.FIRST_VERSION)
      {
        return;
      }

      if (stmt == null)
      {
        IDBTable table = fields[0].getTable();
        IDBField versionRemoved = table.getField(MappingNames.LIST_REVISION_VERSION_REMOVED);

        sql = SQL.update(table) //
            .set(versionRemoved).to(SQL.param()) //
            .where(SQL.eq(table.getField(MappingNames.LIST_REVISION_ID), SQL.param()) //
                .and(SQL.eq(table.getField(MappingNames.LIST_REVISION_BRANCH), SQL.param())) //
                .and(SQL.eq(table.getField(MappingNames.LIST_IDX), SQL.param())) //
                .and(SQL.lt(table.getField(MappingNames.LIST_REVISION_VERSION_ADDED), SQL.param())) //
                .and(SQL.isNull(versionRemoved))) //
            .compile(getDBAdapter());

        stmt = DBUtil.prepare(sql, (IDBConnection)connection, ReuseProbability.MEDIUM);
      }

      Object sourceID = values[0];
      int branch = DBUtil.asInt(values[1]);
      int index = DBUtil.asInt(values[4]);

      sql.bindParameter(stmt, 1, versionAdded);
      sql.bindParameter(stmt, 2, sourceID, idHandler::setCDOIDRaw);
      sql.bindParameter(stmt, 3, branch);
      sql.bindParameter(stmt, 4, index);
      sql.bindParameter(stmt, 5, versionAdded);

      stmt.addBatch();
    }

    @Override
    public void done(boolean successful) throws SQLException, IOException
    {
      if (stmt != null)
      {
        try
        {
          if (successful)
          {
            stmt.executeBatch();
          }
        }
        finally
        {
          DBUtil.close(stmt);
          stmt = null;
        }
      }
    }
  }
}
