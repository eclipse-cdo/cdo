/*
 * Copyright (c) 2023, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.mapping.IBranchDeletionSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.SubBranchInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.db.Batch;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.db.dml.SQL;
import org.eclipse.net4j.db.dml.SQLStatement;
import org.eclipse.net4j.util.ConsumerWithException;
import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class DBStoreTables extends Lifecycle
{
  private final PropertiesTable properties;

  private final PackageUnitsTable packageUnits;

  private final PackageInfosTable packageInfos;

  private final BranchesTable branches;

  private final TagsTable tags;

  private final LobsTable lobs;

  public DBStoreTables(IDBStore store)
  {
    properties = new PropertiesTable(store);
    packageUnits = new PackageUnitsTable(store);
    packageInfos = new PackageInfosTable(store);
    branches = new BranchesTable(store);
    tags = new TagsTable(store);
    lobs = new LobsTable(store);
  }

  public PropertiesTable properties()
  {
    return properties;
  }

  public PackageUnitsTable packageUnits()
  {
    return packageUnits;
  }

  public PackageInfosTable packageInfos()
  {
    return packageInfos;
  }

  public BranchesTable branches()
  {
    return branches;
  }

  public TagsTable tags()
  {
    return tags;
  }

  public LobsTable lobs()
  {
    return lobs;
  }

  @Override
  protected void doActivate() throws Exception
  {
    properties.activate();
    packageUnits.activate();
    packageInfos.activate();
    branches.activate();
    tags.activate();
    lobs.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    lobs.deactivate();
    tags.deactivate();
    branches.deactivate();
    packageInfos.deactivate();
    packageUnits.deactivate();
    properties.deactivate();
  }

  /**
   * @author Eike Stepper
   */
  public static final class PropertiesTable extends DBStoreTable
  {
    private IDBField name;

    private IDBField value;

    @ExcludeFromDump
    private transient SQLStatement sqlDeleteProperties;

    @ExcludeFromDump
    private transient SQLStatement sqlInsertProperties;

    @ExcludeFromDump
    private transient SQLStatement sqlSelectProperties;

    @ExcludeFromDump
    private transient SQLStatement sqlSelectAllProperties;

    public PropertiesTable(IDBStore store)
    {
      super(store, NAMES.PROPERTIES);
    }

    public Map<String, String> getPersistentProperties(Set<String> names)
    {
      IDBConnection connection = getConnection();
      IDBPreparedStatement stmt = null;
      String sql = null;

      try
      {
        Map<String, String> result = new HashMap<>();
        boolean allProperties = ObjectUtil.isEmpty(names);
        if (allProperties)
        {
          stmt = DBUtil.prepare(sqlSelectAllProperties, connection, ReuseProbability.MEDIUM);
          ResultSet resultSet = null;

          try
          {
            resultSet = stmt.executeQuery();

            while (resultSet.next())
            {
              String key = resultSet.getString(1);
              String value = resultSet.getString(2);
              result.put(key, value);
            }
          }
          finally
          {
            DBUtil.close(resultSet);
          }
        }
        else
        {
          stmt = DBUtil.prepare(sqlSelectProperties, connection, ReuseProbability.MEDIUM);

          for (String name : names)
          {
            sqlSelectProperties.bindAll(stmt, name);
            ResultSet resultSet = null;

            try
            {
              resultSet = stmt.executeQuery();
              if (resultSet.next())
              {
                String value = resultSet.getString(1);
                result.put(name, value);
              }
            }
            finally
            {
              DBUtil.close(resultSet);
            }
          }
        }

        return result;
      }
      catch (SQLException ex)
      {
        throw new DBException(ex, sql);
      }
      finally
      {
        DBUtil.close(stmt);
        DBUtil.close(connection);
      }
    }

    public void setPersistentProperties(Map<String, String> properties)
    {
      IDBConnection connection = getConnection();
      IDBPreparedStatement deleteStmt = DBUtil.prepare(sqlDeleteProperties, connection, ReuseProbability.MEDIUM);
      IDBPreparedStatement insertStmt = DBUtil.prepare(sqlInsertProperties, connection, ReuseProbability.MEDIUM);
      String msg = null;

      try
      {
        for (Map.Entry<String, String> entry : properties.entrySet())
        {
          String name = entry.getKey();
          String value = entry.getValue();

          msg = sqlDeleteProperties.getSQL();
          sqlDeleteProperties.bindAll(deleteStmt, name);
          deleteStmt.executeUpdate();

          msg = sqlInsertProperties.getSQL();
          sqlInsertProperties.bindAll(insertStmt, name, value);
          insertStmt.executeUpdate();
        }

        msg = "COMMIT";
        connection.commit();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex, msg);
      }
      finally
      {
        DBUtil.close(insertStmt);
        DBUtil.close(deleteStmt);
        DBUtil.close(connection);
      }
    }

    public void removePersistentProperties(Set<String> names)
    {
      IDBConnection connection = getConnection();
      IDBPreparedStatement stmt = DBUtil.prepare(sqlDeleteProperties, connection, ReuseProbability.MEDIUM);

      try
      {
        for (String name : names)
        {
          sqlDeleteProperties.bindAll(stmt, name);
          stmt.executeUpdate();
        }

        connection.commit();
      }
      catch (SQLException ex)
      {
        throw new DBException(ex, sqlDeleteProperties.getSQL());
      }
      finally
      {
        DBUtil.close(stmt);
        DBUtil.close(connection);
      }
    }

    @Override
    protected void firstActivate(IDBTable table)
    {
      name = table.addField(NAMES.NAME, DBType.VARCHAR, 255, true);
      value = table.addField(NAMES.VALUE, DBType.LONGVARCHAR);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, name);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      name = table.getField(NAMES.NAME);
      value = table.getField(NAMES.VALUE);
    }

    @Override
    protected void initSQL(IDBTable table)
    {
      sqlDeleteProperties = SQL.deleteFrom(table) //
          .where(name).eq(SQL.param()) //
          .compile(adapter());

      sqlInsertProperties = SQL.insertInto(table) //
          .columns(name, value) //
          .values(SQL.param(), SQL.param()) //
          .compile(adapter());

      sqlSelectProperties = SQL.select(value) //
          .from(table) //
          .where(name).eq(SQL.param()) //
          .compile(adapter());

      sqlSelectAllProperties = SQL.select(name, value) //
          .from(table) //
          .compile(adapter());
    }

    public static String tableName()
    {
      return NAMES.PROPERTIES;
    }

    public static String sqlSelectProperty(IDBAdapter dbAdapter, String schemaName)
    {
      String tableName = dbAdapter.quoteIdentifier(NAMES.PROPERTIES);
      if (schemaName != null)
      {
        tableName = dbAdapter.quoteIdentifier(schemaName) + '.' + tableName;
      }

      return "SELECT " + dbAdapter.quoteIdentifier(NAMES.VALUE) + " FROM " + tableName + " WHERE " + dbAdapter.quoteIdentifier(NAMES.NAME) + "=?";
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String PROPERTIES = name("cdo_properties");

      private static final String NAME = name("name");

      private static final String VALUE = name("value");

      private static String name(String name)
      {
        return DBUtil.name(name, PropertiesTable.class);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class PackageUnitsTable extends DBStoreTable
  {
    private IDBField id;

    private IDBField originalType;

    private IDBField timeStamp;

    private IDBField packageData;

    @ExcludeFromDump
    private transient SQLStatement sqlSelectPackageData;

    public PackageUnitsTable(IDBStore store)
    {
      super(store, NAMES.PACKAGE_UNITS);
    }

    public IDBField id()
    {
      return id;
    }

    public IDBField originalType()
    {
      return originalType;
    }

    public IDBField timeStamp()
    {
      return timeStamp;
    }

    public IDBField packageData()
    {
      return packageData;
    }

    public byte[] loadPackageUnitBytes(Connection connection, InternalCDOPackageUnit packageUnit)
    {
      PreparedStatement stmt = null;
      ResultSet resultSet = null;

      try
      {
        stmt = connection.prepareStatement(sqlSelectPackageData.getSQL());
        sqlSelectPackageData.bind(stmt, packageUnit.getID());
        resultSet = stmt.executeQuery();
        return resultSet.next() ? resultSet.getBytes(1) : null;
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
    protected void firstActivate(IDBTable table)
    {
      id = table.addField(NAMES.ID, DBType.VARCHAR, 255, true);
      originalType = table.addField(NAMES.ORIGINAL_TYPE, DBType.INTEGER);
      timeStamp = table.addField(NAMES.TIME_STAMP, DBType.BIGINT);
      packageData = table.addField(NAMES.PACKAGE_DATA, DBType.BLOB);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      id = table.getField(NAMES.ID);
      originalType = table.getField(NAMES.ORIGINAL_TYPE);
      timeStamp = table.getField(NAMES.TIME_STAMP);
      packageData = table.getField(NAMES.PACKAGE_DATA);
    }

    @Override
    protected void initSQL(IDBTable table)
    {
      sqlSelectPackageData = SQL.select(packageData) //
          .from(table) //
          .where(id).eq(SQL.param()) //
          .compile(adapter());
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String PACKAGE_UNITS = name("cdo_package_units");

      private static final String ID = name("id");

      private static final String ORIGINAL_TYPE = name("original_type");

      private static final String TIME_STAMP = name("time_stamp");

      private static final String PACKAGE_DATA = name("package_data");

      private static String name(String name)
      {
        return DBUtil.name(name, PackageUnitsTable.class);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class PackageInfosTable extends DBStoreTable
  {
    private IDBField uri;

    private IDBField parent;

    private IDBField unit;

    public PackageInfosTable(IDBStore store)
    {
      super(store, NAMES.PACKAGE_INFOS);
    }

    public IDBField uri()
    {
      return uri;
    }

    public IDBField parent()
    {
      return parent;
    }

    public IDBField unit()
    {
      return unit;
    }

    @Override
    protected void firstActivate(IDBTable table)
    {
      uri = table.addField(NAMES.URI, DBType.VARCHAR, 255, true);
      parent = table.addField(NAMES.PARENT, DBType.VARCHAR, 255);
      unit = table.addField(NAMES.UNIT, DBType.VARCHAR, 255);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, uri);
      table.addIndex(IDBIndex.Type.NON_UNIQUE, parent);
      table.addIndex(IDBIndex.Type.NON_UNIQUE, unit);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      uri = table.getField(NAMES.URI);
      parent = table.getField(NAMES.PARENT);
      unit = table.getField(NAMES.UNIT);
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String PACKAGE_INFOS = name("cdo_package_infos");

      private static final String URI = name("uri");

      private static final String PARENT = name("parent");

      private static final String UNIT = name("unit");

      private static String name(String name)
      {
        return DBUtil.name(name, PackageInfosTable.class);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class BranchesTable extends DBStoreTable
  {
    private IDBField id;

    private IDBField name;

    private IDBField baseID;

    private IDBField baseTime;

    @ExcludeFromDump
    private transient SQLStatement sqlCreateBranch;

    @ExcludeFromDump
    private transient SQLStatement sqlLoadBranch;

    @ExcludeFromDump
    private transient SQLStatement sqlRenameBranch;

    @ExcludeFromDump
    private transient SQLStatement sqlLoadSubBranches;

    @ExcludeFromDump
    private transient SQLStatement sqlLoadBranches;

    public BranchesTable(IDBStore store)
    {
      super(store, NAMES.BRANCHES);
    }

    public IDBField id()
    {
      return id;
    }

    public Pair<Integer, Long> createBranch(IDBConnection connection, int branchID, BranchInfo branchInfo)
    {
      if (branchID == BranchLoader.NEW_BRANCH)
      {
        branchID = ((DBStore)store()).getNextBranchID();
      }
      else if (branchID == BranchLoader.NEW_LOCAL_BRANCH)
      {
        branchID = ((DBStore)store()).getNextLocalBranchID();
      }

      IDBPreparedStatement stmt = DBUtil.prepare(sqlCreateBranch, connection, ReuseProbability.LOW);

      try
      {
        sqlCreateBranch.bindAll(stmt, branchID, branchInfo.getName(), branchInfo.getBaseBranchID(), branchInfo.getBaseTimeStamp());
        DBUtil.update(stmt, true);

        connection.commit();
        return Pair.create(branchID, branchInfo.getBaseTimeStamp());
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

    public BranchInfo loadBranch(IDBConnection connection, int branchID)
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlLoadBranch, connection, ReuseProbability.HIGH);
      ResultSet resultSet = null;

      try
      {
        sqlLoadBranch.bind(stmt, branchID);
        resultSet = stmt.executeQuery();

        if (!resultSet.next())
        {
          throw new DBException("Branch with ID " + branchID + " does not exist");
        }

        String name = resultSet.getString(1);
        int baseBranchID = resultSet.getInt(2);
        long baseTimeStamp = resultSet.getLong(3);
        return new BranchInfo(name, baseBranchID, baseTimeStamp);
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

    public SubBranchInfo[] loadSubBranches(IDBConnection connection, int baseID)
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlLoadSubBranches, connection, ReuseProbability.HIGH);
      ResultSet resultSet = null;

      try
      {
        sqlLoadSubBranches.bind(stmt, baseID);
        resultSet = stmt.executeQuery();

        List<SubBranchInfo> result = new ArrayList<>();

        while (resultSet.next())
        {
          int id = resultSet.getInt(1);
          String name = resultSet.getString(2);
          long baseTimeStamp = resultSet.getLong(3);
          result.add(new SubBranchInfo(id, name, baseTimeStamp));
        }

        return result.toArray(new SubBranchInfo[result.size()]);
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

    public int loadBranches(IDBConnection connection, int startID, int endID, CDOBranchHandler handler)
    {
      int count = 0;
      IDBPreparedStatement stmt = DBUtil.prepare(sqlLoadBranches, connection, ReuseProbability.HIGH);
      ResultSet resultSet = null;

      InternalCDOBranchManager branchManager = (InternalCDOBranchManager)store().getRepository().getBranchManager();

      try
      {
        sqlLoadBranches.bindAll(stmt, startID, endID > 0 ? endID : Integer.MAX_VALUE);
        resultSet = stmt.executeQuery();

        while (resultSet.next())
        {
          int branchID = resultSet.getInt(1);
          String name = resultSet.getString(2);
          int baseBranchID = resultSet.getInt(3);
          long baseTimeStamp = resultSet.getLong(4);

          InternalCDOBranch branch = branchManager.getBranch(branchID, new BranchInfo(name, baseBranchID, baseTimeStamp));
          handler.handleBranch(branch);
          ++count;
        }

        return count;
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

    public CDOBranch[] deleteBranches(IDBStoreAccessor accessor, int branchID, OMMonitor monitor)
    {
      DBStore store = (DBStore)store();
      IDBConnection connection = accessor.getDBConnection();

      Set<CDOBranch> branches = store.getRepository().getBranchManager().getBranches(branchID);
      String idList = buildIDList(branches);

      try (Batch batch = new Batch(connection))
      {
        IDBAdapter dbAdapter = store.getDBAdapter();

        // Delete the branches.
        batch.add("DELETE FROM " + dbAdapter.quoteTableName(getTable()) //
            + " WHERE " + dbAdapter.quoteIdentifier(id.getName()) //
            + " IN (" + idList + ")");

        // Delete the tags.
        TagsTable tags = store.tables().tags();
        batch.add("DELETE FROM " + dbAdapter.quoteTableName(tags.getTable()) //
            + " WHERE " + dbAdapter.quoteIdentifier(tags.branch().getName()) //
            + " IN (" + idList + ")");

        // Delete the revisions, type mappings, and list elements.
        IMappingStrategy mappingStrategy = store.getMappingStrategy();
        if (mappingStrategy instanceof IBranchDeletionSupport)
        {
          ((IBranchDeletionSupport)mappingStrategy).deleteBranches(accessor, batch, idList);
        }

        // Delete the commit infos.
        CommitInfoTable commitInfoTable = store.getCommitInfoTable();
        if (commitInfoTable != null)
        {
          commitInfoTable.deleteBranches(accessor, batch, idList);
        }

        // Delete the locks and lock areas.
        store.getDurableLockingManager().deleteBranches(accessor, batch, idList);

        monitor.begin();
        Async async = monitor.forkAsync();

        try
        {
          batch.execute();
          connection.commit();
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

      return branches.toArray(new CDOBranch[branches.size()]);
    }

    public void renameBranch(IDBConnection connection, int branchID, String oldName, String newName)
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlRenameBranch, connection, ReuseProbability.LOW);

      try
      {
        sqlRenameBranch.bind(stmt, newName, branchID);
        DBUtil.update(stmt, true);

        connection.commit();
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
    protected void firstActivate(IDBTable table)
    {
      id = table.addField(NAMES.ID, DBType.INTEGER, true);
      name = table.addField(NAMES.NAME, DBType.VARCHAR);
      baseID = table.addField(NAMES.BASE_ID, DBType.INTEGER);
      baseTime = table.addField(NAMES.BASE_TIME, DBType.BIGINT);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      id = table.getField(NAMES.ID);
      name = table.getField(NAMES.NAME);
      baseID = table.getField(NAMES.BASE_ID);
      baseTime = table.getField(NAMES.BASE_TIME);
    }

    @Override
    protected void initSQL(IDBTable table)
    {
      sqlCreateBranch = SQL.insertInto(table) //
          .columns(id, name, baseID, baseTime) //
          .values(SQL.param(), SQL.param(), SQL.param(), SQL.param()) //
          .compile(adapter());

      sqlLoadBranch = SQL.select(name, baseID, baseTime) //
          .from(table) //
          .where(id).eq(SQL.param()) //
          .compile(adapter());

      sqlRenameBranch = SQL.update(table) //
          .set(name).to(SQL.param()) //
          .where(id).eq(SQL.param()) //
          .compile(adapter());

      sqlLoadSubBranches = SQL.select(id, name, baseTime) //
          .from(table) //
          .where(baseID).eq(SQL.param()) //
          .compile(adapter());

      sqlLoadBranches = SQL.select(id, name, baseID, baseTime) //
          .from(table) //
          .where(id).between(SQL.param(), SQL.param()) //
          .orderBy(id) //
          .compile(adapter());
    }

    private static String buildIDList(Set<CDOBranch> branches)
    {
      StringBuilder builder = new StringBuilder();

      for (CDOBranch branch : branches)
      {
        StringUtil.appendSeparator(builder, ", ");
        builder.append(branch.getID());
      }

      return builder.toString();
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String BRANCHES = name("cdo_branches");

      private static final String ID = name("id");

      private static final String NAME = name("name");

      private static final String BASE_ID = name("base_id");

      private static final String BASE_TIME = name("base_time");

      private static String name(String name)
      {
        return DBUtil.name(name, BranchesTable.class);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TagsTable extends DBStoreTable
  {
    private IDBField name;

    private IDBField branch;

    private IDBField timestamp;

    @ExcludeFromDump
    private transient SQLStatement sqlCreateTag;

    @ExcludeFromDump
    private transient SQLStatement sqlRenameTag;

    @ExcludeFromDump
    private transient SQLStatement sqlMoveTag;

    @ExcludeFromDump
    private transient SQLStatement sqlDeleteTag;

    @ExcludeFromDump
    private transient SQLStatement sqlLoadTags;

    @ExcludeFromDump
    private transient SQLStatement sqlLoadTag;

    public TagsTable(IDBStore store)
    {
      super(store, NAMES.TAGS);
    }

    public IDBField branch()
    {
      return branch;
    }

    public CDOBranchPoint changeTag(IDBConnection connection, AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
    {
      switch (InternalCDOBranchManager.getTagChangeKind(oldName, newName, branchPoint))
      {
      case CREATED:
        execSQL(connection, sqlCreateTag, //
            stmt -> sqlCreateTag.bindAll(stmt, newName, branchPoint.getBranch().getID(), branchPoint.getTimeStamp()));
        break;

      case RENAMED:
        execSQL(connection, sqlRenameTag, stmt -> sqlRenameTag.bindAll(stmt, newName, oldName));
        break;

      case MOVED:
        execSQL(connection, sqlMoveTag, stmt -> sqlMoveTag.bindAll(stmt, branchPoint.getBranch().getID(), branchPoint.getTimeStamp(), oldName));
        break;

      case DELETED:
        execSQL(connection, sqlDeleteTag, stmt -> sqlDeleteTag.bindAll(stmt, oldName));
        break;
      }

      // Repository.changeTag() takes care of the proper result value;
      return null;
    }

    public void loadTags(IDBConnection connection, String name, Consumer<BranchInfo> handler)
    {
      boolean single = name != null;
      SQLStatement sql = single ? sqlLoadTag : sqlLoadTags;
      IDBPreparedStatement stmt = DBUtil.prepare(sql, connection, ReuseProbability.LOW);
      ResultSet resultSet = null;

      try
      {
        if (single)
        {
          sql.bind(stmt, name);
        }

        resultSet = stmt.executeQuery();

        while (resultSet.next())
        {
          int c = 0;

          if (!single)
          {
            name = resultSet.getString(++c);
          }

          int branchID = resultSet.getInt(++c);
          long timeStamp = resultSet.getLong(++c);
          handler.accept(new BranchInfo(name, branchID, timeStamp));
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
    protected void firstActivate(IDBTable table)
    {
      name = table.addField(NAMES.NAME, DBType.VARCHAR, true);
      branch = table.addField(NAMES.BRANCH, DBType.INTEGER);
      timestamp = table.addField(NAMES.TIMESTAMP, DBType.BIGINT);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, name);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      name = table.getField(NAMES.NAME);
      branch = table.getField(NAMES.BRANCH);
      timestamp = table.getField(NAMES.TIMESTAMP);
    }

    @Override
    protected void initSQL(IDBTable table)
    {
      sqlCreateTag = SQL.insertInto(table) //
          .columns(name, branch, timestamp) //
          .values(SQL.param(), SQL.param(), SQL.param()) //
          .compile(adapter());

      sqlRenameTag = SQL.update(table) //
          .set(name).to(SQL.param()) //
          .where(name).eq(SQL.param()) //
          .compile(adapter());

      sqlMoveTag = SQL.update(table) //
          .set(branch).to(SQL.param()) //
          .set(timestamp).to(SQL.param()) //
          .where(name).eq(SQL.param()) //
          .compile(adapter());

      sqlDeleteTag = SQL.deleteFrom(table) //
          .where(name).eq(SQL.param()) //
          .compile(adapter());

      sqlLoadTags = SQL.select(name, branch, timestamp) //
          .from(table) //
          .compile(adapter());

      sqlLoadTag = SQL.select(branch, timestamp) //
          .from(table) //
          .where(name).eq(SQL.param()) //
          .compile(adapter());
    }

    private static void execSQL(IDBConnection connection, SQLStatement sql, ConsumerWithException<IDBPreparedStatement, SQLException> preparer)
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sql, connection, ReuseProbability.LOW);

      try
      {
        if (preparer != null)
        {
          preparer.accept(stmt);
        }

        DBUtil.update(stmt, true);
        connection.commit();
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

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String TAGS = name("cdo_tags");

      private static final String NAME = name("name");

      private static final String BRANCH = name("branch");

      private static final String TIMESTAMP = name("timestamp");

      private static String name(String name)
      {
        return DBUtil.name(name, TagsTable.class);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class LobsTable extends DBStoreTable
  {
    private static final int LOB_ID_LENGTH = //
        OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.server.internal.db.DBStoreTables.LobsTable.LOB_ID_LENGTH", 64);

    private IDBField id;

    private IDBField size;

    private IDBField bdata;

    private IDBField cdata;

    private IDBField refs;

    @ExcludeFromDump
    private transient SQLStatement sqlQueryLobs;

    @ExcludeFromDump
    private transient SQLStatement sqlHandleLobs;

    @ExcludeFromDump
    private transient SQLStatement sqlLoadLob;

    @ExcludeFromDump
    private transient SQLStatement sqlWriteBlob;

    @ExcludeFromDump
    private transient SQLStatement sqlWriteClob;

    @ExcludeFromDump
    private transient SQLStatement sqlResetRefs;

    @ExcludeFromDump
    private transient SQLStatement sqlDeleteOrphans;

    public LobsTable(IDBStore store)
    {
      super(store, NAMES.LOBS);
    }

    public void queryLobs(IDBConnection connection, List<byte[]> ids)
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlQueryLobs, connection, ReuseProbability.MEDIUM);
      ResultSet resultSet = null;

      try
      {
        for (Iterator<byte[]> it = ids.iterator(); it.hasNext();)
        {
          byte[] id = it.next();
          sqlQueryLobs.bind(stmt, HexUtil.bytesToHex(id));

          try
          {
            resultSet = stmt.executeQuery();
            if (!resultSet.next())
            {
              it.remove();
            }
          }
          finally
          {
            DBUtil.close(resultSet);
          }
        }
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

    public void loadLob(IDBConnection connection, byte[] id, OutputStream out) throws IOException
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlLoadLob, connection, ReuseProbability.MEDIUM);
      ResultSet resultSet = null;

      try
      {
        sqlLoadLob.bind(stmt, HexUtil.bytesToHex(id));
        resultSet = stmt.executeQuery();
        resultSet.next();

        long size = resultSet.getLong(1);
        InputStream inputStream = resultSet.getBinaryStream(2);

        if (resultSet.wasNull())
        {
          Reader reader = resultSet.getCharacterStream(3);
          IOUtil.copyCharacter(reader, new OutputStreamWriter(out), size);
        }
        else
        {
          IOUtil.copyBinary(inputStream, out, size);
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

    public void handleLobs(IDBConnection connection, long fromTime, long toTime, CDOLobHandler handler) throws IOException
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlHandleLobs, connection, ReuseProbability.LOW);
      ResultSet resultSet = null;

      try
      {
        resultSet = stmt.executeQuery();

        while (resultSet.next())
        {
          byte[] id = HexUtil.hexToBytes(resultSet.getString(1));
          long size = resultSet.getLong(2);
          InputStream inputStream = resultSet.getBinaryStream(3);

          if (resultSet.wasNull())
          {
            Reader reader = resultSet.getCharacterStream(4);
            Writer out = handler.handleClob(id, size);
            if (out != null)
            {
              try
              {
                IOUtil.copyCharacter(reader, out, size);
              }
              finally
              {
                IOUtil.close(out);
              }
            }
          }
          else
          {
            OutputStream out = handler.handleBlob(id, size);
            if (out != null)
            {
              try
              {
                IOUtil.copyBinary(inputStream, out, size);
              }
              finally
              {
                IOUtil.close(out);
              }
            }
          }
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

    public void writeBlob(IDBConnection connection, byte[] id, long size, InputStream inputStream) throws IOException
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlWriteBlob, connection, ReuseProbability.MEDIUM);

      try
      {
        sqlWriteBlob.binder(stmt) //
            .bind(HexUtil.bytesToHex(id), size) //
            .bindParameter(inputStream, (statement, index, stream) -> statement.setBinaryStream(index, stream, (int)size));

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

    public void writeClob(IDBConnection connection, byte[] id, long size, Reader reader) throws IOException
    {
      IDBPreparedStatement stmt = DBUtil.prepare(sqlWriteClob, connection, ReuseProbability.MEDIUM);

      try
      {
        sqlWriteClob.binder(stmt) //
            .bind(HexUtil.bytesToHex(id), size) //
            .bindParameter(reader, (statement, index, input) -> statement.setCharacterStream(index, input, (int)size));

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

    public void resetRefs(Connection connection)
    {
      DBUtil.execute(connection, sqlResetRefs.getSQL());
    }

    public int updateRefs(Connection connection, IDBField dataField)
    {
      IDBTable dataTable = dataField.getTable();
      IDBTable lobsTable = getTable();

      IDBAdapter dbAdapter = store().getDBAdapter();
      String lobsTableName = dbAdapter.quoteTableName(lobsTable);
      String refsName = dbAdapter.quoteIdentifier(refs.getName());
      String idName = dbAdapter.quoteIdentifier(id.getName());
      String dataTableName = dbAdapter.quoteTableName(dataTable);
      String dataFieldName = dbAdapter.quoteIdentifier(dataField.getName());
      String sql = "UPDATE " + lobsTableName //
          + " SET " + refsName + "=" + refsName + //
          "+(SELECT COUNT(*) FROM " + dataTableName //
          + " WHERE " + dataFieldName //
          + " LIKE " + lobsTableName + "." + dbAdapter.sqlConcat(idName, "'-%'") + ")";

      return DBUtil.update(connection, sql);
    }

    public int deleteOrphans(Connection connection, boolean dryRun)
    {
      if (dryRun)
      {
        IDBAdapter dbAdapter = store().getDBAdapter();
        return DBUtil.getRowCount(connection, dbAdapter.quoteTableName(getTable()) + " WHERE " + dbAdapter.quoteIdentifier(refs.getName()) + "=0");
      }

      return DBUtil.update(connection, sqlDeleteOrphans.getSQL());
    }

    @Override
    protected void firstActivate(IDBTable table)
    {
      id = table.addField(NAMES.ID, DBType.VARCHAR, LOB_ID_LENGTH, true);
      size = table.addField(NAMES.SIZE, DBType.BIGINT);
      bdata = table.addField(NAMES.BDATA, DBType.BLOB);
      cdata = table.addField(NAMES.CDATA, DBType.CLOB);
      refs = table.addField(NAMES.REFS, DBType.INTEGER);

      table.addIndex(IDBIndex.Type.PRIMARY_KEY, id);
    }

    @Override
    protected void reActivate(IDBTable table)
    {
      id = table.getField(NAMES.ID);
      size = table.getField(NAMES.SIZE);
      bdata = table.getField(NAMES.BDATA);
      cdata = table.getField(NAMES.CDATA);
      refs = table.getField(NAMES.REFS);
    }

    @Override
    protected void initSQL(IDBTable table)
    {
      sqlQueryLobs = SQL.select(SQL.ONE) //
          .from(table) //
          .where(id).eq(SQL.param()) //
          .compile(adapter());

      sqlHandleLobs = SQL.select(id, size, bdata, cdata) //
          .from(table) //
          .compile(adapter());

      sqlLoadLob = SQL.select(size, bdata, cdata) //
          .from(table) //
          .where(id).eq(SQL.param()) //
          .compile(adapter());

      sqlWriteBlob = SQL.insertInto(table) //
          .columns(id, size, bdata) //
          .values(SQL.param(), SQL.param(), SQL.param()) //
          .compile(adapter());

      sqlWriteClob = SQL.insertInto(table) //
          .columns(id, size, cdata) //
          .values(SQL.param(), SQL.param(), SQL.param()) //
          .compile(adapter());

      sqlResetRefs = SQL.update(table) //
          .set(refs).to(SQL.ZERO) //
          .compile(adapter());

      sqlDeleteOrphans = SQL.deleteFrom(table) //
          .where(refs).eq(SQL.ZERO) //
          .compile(adapter());
    }

    public static String tableName()
    {
      return NAMES.LOBS;
    }

    public static String idName()
    {
      return NAMES.ID;
    }

    public static String sizeName()
    {
      return NAMES.SIZE;
    }

    public static String refsName()
    {
      return NAMES.REFS;
    }

    /**
     * @author Eike Stepper
     */
    private static final class NAMES
    {
      private static final String LOBS = name("cdo_lobs");

      private static final String ID = name("id");

      private static final String SIZE = name("lsize");

      private static final String BDATA = name("bdata");

      private static final String CDATA = name("cdata");

      private static final String REFS = name("nrefs");

      private static String name(String name)
      {
        return DBUtil.name(name, LobsTable.class);
      }
    }
  }
}
