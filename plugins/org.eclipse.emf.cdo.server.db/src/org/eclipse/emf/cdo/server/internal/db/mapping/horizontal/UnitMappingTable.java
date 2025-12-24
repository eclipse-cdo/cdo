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
 *    Stefan Winkler - bug 259402
 *    Stefan Winkler - redesign (prepared statements)
 *    Stefan Winkler - bug 276926
 */
package org.eclipse.emf.cdo.server.internal.db.mapping.horizontal;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.IMetaDataManager;
import org.eclipse.emf.cdo.server.db.mapping.IClassMappingUnitSupport;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBStoreTable;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnection;
import org.eclipse.net4j.db.IDBPreparedStatement;
import org.eclipse.net4j.db.IDBPreparedStatement.ReuseProbability;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class UnitMappingTable extends DBStoreTable
{
  private static final int WRITE_UNIT_MAPPING_BATCH_SIZE = 100000;

  private String sqlSelectRoots;

  private String sqlInsertMappings;

  private String sqlSelectClasses;

  private IDBField elem;

  private IDBField unit;

  public UnitMappingTable(IDBStore store)
  {
    super(store, NAMES.UNITS);
  }

  public final IDBField elem()
  {
    return elem;
  }

  public final IDBField unit()
  {
    return unit;
  }

  public List<CDOID> readUnitRoots(IDBStoreAccessor accessor)
  {
    List<CDOID> rootIDs = new ArrayList<>();
    IIDHandler idHandler = store().getIDHandler();
    Statement stmt = null;

    try
    {
      stmt = accessor.getDBConnection().createStatement();

      if (DBUtil.isTracerEnabled())
      {
        DBUtil.trace(stmt.toString());
      }

      ResultSet resultSet = stmt.executeQuery(sqlSelectRoots);
      while (resultSet.next())
      {
        CDOID rootID = idHandler.getCDOID(resultSet, 1);
        rootIDs.add(rootID);
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

    return rootIDs;
  }

  public void readUnitRevisions(IDBStoreAccessor accessor, IView view, CDOID rootID, CDORevisionHandler revisionHandler, OMMonitor monitor)
  {
    IIDHandler idHandler = store().getIDHandler();
    IMappingStrategy mappingStrategy = store().getMappingStrategy();
    IMetaDataManager metaDataManager = store().getMetaDataManager();

    long timeStamp = view.isHistorical() ? view.getTimeStamp() : store().getRepository().getTimeStamp();
    CDOBranchPoint branchPoint = view.getBranch().getPoint(timeStamp);

    IDBConnection connection = accessor.getDBConnection();
    IDBPreparedStatement stmt = connection.prepareStatement(sqlSelectClasses, ReuseProbability.HIGH);

    int jdbcFetchSize = store().getJDBCFetchSize();
    int oldFetchSize = -1;

    try
    {
      idHandler.setCDOID(stmt, 1, rootID);

      oldFetchSize = stmt.getFetchSize();
      stmt.setFetchSize(jdbcFetchSize);
      ResultSet resultSet = stmt.executeQuery();

      while (resultSet.next())
      {
        CDOID classID = idHandler.getCDOID(resultSet, 1);
        EClass eClass = (EClass)metaDataManager.getMetaInstance(classID);

        IClassMappingUnitSupport classMapping = (IClassMappingUnitSupport)mappingStrategy.getClassMapping(eClass);
        classMapping.readUnitRevisions(accessor, branchPoint, rootID, revisionHandler);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      if (oldFetchSize != -1)
      {
        try
        {
          stmt.setFetchSize(oldFetchSize);
        }
        catch (SQLException ex)
        {
          throw new DBException(ex);
        }
      }

      DBUtil.close(stmt);
    }
  }

  public BatchedStatement initUnit(IDBStoreAccessor accessor, long timeStamp, IView view, CDOID rootID, CDORevisionHandler revisionHandler,
      Set<CDOID> initializedIDs, OMMonitor monitor)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBConnection connection = accessor.getDBConnection();
    BatchedStatement stmt = DBUtil.batched(connection.prepareStatement(sqlInsertMappings, ReuseProbability.HIGH), WRITE_UNIT_MAPPING_BATCH_SIZE);

    try
    {
      CDORevision revision = view.getRevision(rootID);

      initUnit(stmt, view, rootID, revisionHandler, initializedIDs, timeStamp, idHandler, revision, monitor);
      return stmt;
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      // Don't close the statement; that's done later in finishUnit().
    }
  }

  private void initUnit(BatchedStatement stmt, IView view, CDOID rootID, CDORevisionHandler revisionHandler, Set<CDOID> initializedIDs, long timeStamp,
      IIDHandler idHandler, CDORevision revision, OMMonitor monitor) throws SQLException
  {
    revisionHandler.handleRevision(revision);

    CDOID id = revision.getID();
    initializedIDs.add(id);

    writeUnitMapping(stmt, rootID, timeStamp, idHandler, id);

    List<CDORevision> children = CDORevisionUtil.getChildRevisions(revision, view, true);
    for (CDORevision child : children)
    {
      initUnit(stmt, view, rootID, revisionHandler, initializedIDs, timeStamp, idHandler, child, monitor);
    }
  }

  public void finishUnit(BatchedStatement stmt, CDOID rootID, List<CDOID> ids, long timeStamp)
  {
    IIDHandler idHandler = store().getIDHandler();
    Connection connection = null;

    try
    {
      connection = stmt.getConnection();

      for (CDOID id : ids)
      {
        writeUnitMapping(stmt, rootID, timeStamp, idHandler, id);
      }
    }
    catch (SQLException ex)
    {
      DBUtil.rollbackSilently(connection);
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(stmt);
    }

    try
    {
      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void writeUnitMappings(IDBStoreAccessor accessor, Map<CDOID, CDOID> unitMappings, long timeStamp)
  {
    IIDHandler idHandler = store().getIDHandler();
    IDBConnection connection = accessor.getDBConnection();
    BatchedStatement stmt = DBUtil.batched(connection.prepareStatement(sqlInsertMappings, ReuseProbability.HIGH), WRITE_UNIT_MAPPING_BATCH_SIZE);

    try
    {
      for (Map.Entry<CDOID, CDOID> entry : unitMappings.entrySet())
      {
        CDOID id = entry.getKey();
        CDOID rootID = entry.getValue();
        writeUnitMapping(stmt, rootID, timeStamp, idHandler, id);
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

  private void writeUnitMapping(BatchedStatement stmt, CDOID rootID, long timeStamp, IIDHandler idHandler, CDOID id) throws SQLException
  {
    idHandler.setCDOID(stmt, 1, id);
    idHandler.setCDOID(stmt, 2, rootID);
    stmt.executeUpdate();
  }

  @Override
  protected void firstActivate(IDBTable table)
  {
    DBType idType = store().getIDHandler().getDBType();
    int idLength = store().getIDColumnLength();

    elem = table.addField(NAMES.ELEM, idType, idLength, true);
    unit = table.addField(NAMES.UNIT, idType, idLength);

    table.addIndex(IDBIndex.Type.PRIMARY_KEY, elem);
    table.addIndex(IDBIndex.Type.NON_UNIQUE, unit);
  }

  @Override
  protected void reActivate(IDBTable table)
  {
    elem = table.getField(NAMES.ELEM);
    unit = table.getField(NAMES.UNIT);
  }

  @Override
  protected void initSQL(IDBTable table)
  {
    ObjectTypeTable objects = ((AbstractHorizontalMappingStrategy)store().getMappingStrategy()).objects();

    sqlSelectRoots = "SELECT DISTINCT " + unit + " FROM " + table;

    sqlInsertMappings = "INSERT INTO " + table + " (" + elem + ", " + unit + ") VALUES (?, ?)";

    sqlSelectClasses = "SELECT " + objects.clazz() + ", COUNT(" + elem + ") FROM " + table + ", " + objects + " WHERE " + elem + "=" + objects.id() + " AND "
        + unit + "=? GROUP BY " + objects.clazz();
  }

  /**
   * @author Eike Stepper
   */
  private static final class NAMES
  {
    private static final String UNITS = name("cdo_units"); //$NON-NLS-1$

    private static final String ELEM = name("cdo_elem"); //$NON-NLS-1$

    private static final String UNIT = name("cdo_unit"); //$NON-NLS-1$

    private static String name(String name)
    {
      return DBUtil.name(name, UnitMappingTable.class);
    }
  }
}
