/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.server.internal.lissome.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.SubBranchInfo;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class IndexReader implements IDBConnectionProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.INDEX, IndexReader.class);

  protected Index index;

  protected IDGenerationLocation idGenerationLocation;

  protected boolean supportingAudits;

  protected boolean supportingBranches;

  protected Connection connection;

  protected PreparedStatement[] queryResourcesStatements = new PreparedStatement[4];

  protected PreparedStatement[] readRevisionStatements = new PreparedStatement[2];

  protected PreparedStatement readRevisionByVersionStatement;

  protected PreparedStatement[] handleRevisionsStatements = new PreparedStatement[32];

  protected PreparedStatement loadSubBranchesStatement;

  protected PreparedStatement loadBranchesStatement;

  protected PreparedStatement[] loadCommitInfosStatements = new PreparedStatement[8];

  public IndexReader(Index index)
  {
    this.index = index;

    idGenerationLocation = index.getIDGenerationLocation();
    supportingAudits = index.isSupportingAudits();
    supportingBranches = index.isSupportingBranches();

    connection = index.getConnection();
  }

  public Index getIndex()
  {
    return index;
  }

  public LissomeStore getStore()
  {
    return index.getStore();
  }

  @Override
  public Connection getConnection()
  {
    return connection;
  }

  protected int setParameters(PreparedStatement stmt, int column, CDOBranchPoint branchPoint) throws SQLException
  {
    if (supportingBranches)
    {
      int branchID = branchPoint.getBranch().getID();
      stmt.setInt(++column, branchID);
    }

    if (supportingAudits)
    {
      long timeStamp = branchPoint.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        stmt.setLong(++column, timeStamp);
        stmt.setLong(++column, timeStamp);
      }
    }

    return column;
  }

  public void queryResources(IStoreAccessor.QueryResourcesContext context)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("queryResources: {0}", context); //$NON-NLS-1$
    }

    ResultSet resultSet = null;

    try
    {
      boolean historical = context.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
      boolean exactMatch = context.exactMatch();
      int stmtIndex = (historical ? 0 : 1) + (exactMatch ? 0 : 2);

      PreparedStatement stmt = queryResourcesStatements[stmtIndex];
      if (stmt == null)
      {
        String sql = index.objects.sqlQueryResources(historical, exactMatch);
        stmt = connection.prepareStatement(sql);
        queryResourcesStatements[stmtIndex] = stmt;
      }

      int column = 0;

      CDOID folderID = context.getFolderID();
      index.setCDOID(stmt, ++column, folderID);

      String name = context.getName();
      if (name == null)
      {
        name = CDOURIUtil.SEGMENT_SEPARATOR;
      }
      else if (!exactMatch)
      {
        name += "%";
      }

      stmt.setString(++column, name);
      setParameters(stmt, column, context);

      index.trace(TRACER, stmt);
      resultSet = stmt.executeQuery();
      while (resultSet.next())
      {
        index.trace(TRACER, resultSet);

        CDOID id = index.getCDOID(resultSet, 1);
        if (!context.addResource(id))
        {
          // No more results allowed
          break;
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
    }
  }

  protected RevisionInfo readRevision(PreparedStatement stmt) throws SQLException
  {
    ResultSet resultSet = null;

    try
    {
      index.trace(TRACER, stmt);
      resultSet = stmt.executeQuery();

      if (resultSet.next())
      {
        index.trace(TRACER, resultSet);

        RevisionInfo revisionInfo = new RevisionInfo();
        revisionInfo.setPointer(resultSet.getLong(1));

        if (supportingAudits)
        {
          revisionInfo.setRevised(resultSet.getLong(2));
        }
        else
        {
          revisionInfo.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);
        }

        return revisionInfo;
      }

      return null;
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  public RevisionInfo readRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("readRevision: {0}, {1}", id, branchPoint); //$NON-NLS-1$
    }

    try
    {
      boolean historical = branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE;
      int stmtIndex = historical ? 0 : 1;

      PreparedStatement stmt = readRevisionStatements[stmtIndex];
      if (stmt == null)
      {
        String sql = index.objects.sqlReadRevision(historical);
        stmt = connection.prepareStatement(sql);
        readRevisionStatements[stmtIndex] = stmt;
      }

      int column = 0;
      index.setCDOID(stmt, ++column, id);
      setParameters(stmt, column, branchPoint);

      return readRevision(stmt);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public RevisionInfo readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("readRevisionByVersion: {0}, {1}", id, branchVersion); //$NON-NLS-1$
    }

    try
    {
      if (readRevisionByVersionStatement == null)
      {
        String sql = index.objects.sqlReadRevisionByVersion();
        readRevisionByVersionStatement = connection.prepareStatement(sql);
      }

      index.setCDOID(readRevisionByVersionStatement, 1, id);
      readRevisionByVersionStatement.setInt(2, branchVersion.getBranch().getID());
      readRevisionByVersionStatement.setInt(3, Math.abs(branchVersion.getVersion()));

      return readRevision(readRevisionByVersionStatement);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, RevisionInfo.Handler handler)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("handleRevisions: {0}, {1}, {2}, {3}", eClass, branch, timeStamp, exactTime); //$NON-NLS-1$
    }

    ResultSet resultSet = null;

    try
    {
      boolean withClass = eClass != null;
      boolean withBranch = branch != null && supportingBranches;
      boolean withTime = timeStamp != CDOBranchPoint.INVALID_DATE;
      boolean historical = timeStamp != CDOBranchPoint.UNSPECIFIED_DATE;

      int stmtIndex = (withClass ? 0 : 1) + (withBranch ? 0 : 2) + (withTime ? 0 : 4) + (exactTime ? 0 : 8) + (historical ? 0 : 16);

      PreparedStatement stmt = handleRevisionsStatements[stmtIndex];
      if (stmt == null)
      {
        String sql = index.objects.sqlHandleRevisions(withClass, withBranch, withTime, exactTime, historical);
        stmt = connection.prepareStatement(sql);
        handleRevisionsStatements[stmtIndex] = stmt;
      }

      int column = 0;
      if (withClass)
      {
        int cid = getStore().getMetaID(eClass);
        stmt.setInt(++column, cid);
      }

      if (withBranch)
      {
        int branchID = branch.getID();
        stmt.setInt(++column, branchID);
      }

      if (withTime)
      {
        if (exactTime)
        {
          if (historical)
          {
            stmt.setLong(++column, timeStamp);
          }
        }
        else
        {
          if (historical)
          {
            stmt.setLong(++column, timeStamp);
            stmt.setLong(++column, timeStamp);
          }
        }
      }

      index.trace(TRACER, stmt);
      resultSet = stmt.executeQuery();

      RevisionInfo revisionInfo = new RevisionInfo();
      revisionInfo.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);

      while (resultSet.next())
      {
        index.trace(TRACER, resultSet);

        CDOID id = index.getCDOID(resultSet, 1);
        revisionInfo.setPointer(resultSet.getLong(2));

        if (supportingAudits)
        {
          revisionInfo.setRevised(resultSet.getLong(3));
        }

        handler.handleRevisionInfo(id, revisionInfo);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  public BranchInfo loadBranch(int branchID)
  {
    // TODO: implement IndexReader.loadBranch(branchID)
    throw new UnsupportedOperationException();
    // return null;
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("loadSubBranches: {0}", branchID); //$NON-NLS-1$
    }

    ResultSet resultSet = null;

    try
    {
      if (loadSubBranchesStatement == null)
      {
        String sql = index.branches.sqlLoadSubBranches();
        loadSubBranchesStatement = connection.prepareStatement(sql);
      }

      loadSubBranchesStatement.setInt(1, branchID);

      index.trace(TRACER, loadSubBranchesStatement);
      resultSet = loadSubBranchesStatement.executeQuery();

      List<SubBranchInfo> result = new ArrayList<SubBranchInfo>();
      while (resultSet.next())
      {
        index.trace(TRACER, resultSet);

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
    }
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler handler)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("loadBranches: {0}, {1}", startID, endID); //$NON-NLS-1$
    }

    InternalCDOBranchManager branchManager = getStore().getRepository().getBranchManager();
    ResultSet resultSet = null;

    try
    {
      if (loadBranchesStatement == null)
      {
        String sql = index.branches.sqlLoadBranches();
        loadBranchesStatement = connection.prepareStatement(sql);
      }

      loadBranchesStatement.setInt(1, startID);
      loadBranchesStatement.setInt(2, endID);

      index.trace(TRACER, loadBranchesStatement);
      resultSet = loadBranchesStatement.executeQuery();

      int count = 0;
      while (resultSet.next())
      {
        index.trace(TRACER, resultSet);

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
    }
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, PointerHandler handler)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("loadCommitInfos: {0}, {1}, {2}", branch, startTime, endTime); //$NON-NLS-1$
    }

    ResultSet resultSet = null;

    try
    {
      boolean withBranch = branch != null;
      boolean withStartTime = startTime != CDOBranchPoint.UNSPECIFIED_DATE;
      boolean withEndTime = endTime != CDOBranchPoint.UNSPECIFIED_DATE;

      int stmtIndex = (withBranch ? 0 : 1) + (withStartTime ? 0 : 2) + (withEndTime ? 0 : 4);

      PreparedStatement stmt = loadCommitInfosStatements[stmtIndex];
      if (stmt == null)
      {
        String sql = index.commitInfos.sqlLoadCommitInfos(withBranch, withStartTime, withEndTime);
        stmt = connection.prepareStatement(sql);
        loadCommitInfosStatements[stmtIndex] = stmt;
      }

      int column = 0;

      if (withBranch)
      {
        int branchID = branch.getID();
        stmt.setInt(++column, branchID);
      }

      if (withStartTime)
      {
        stmt.setLong(++column, startTime);
      }

      if (withEndTime)
      {
        stmt.setLong(++column, endTime);
      }

      index.trace(TRACER, stmt);
      resultSet = stmt.executeQuery();

      while (resultSet.next())
      {
        index.trace(TRACER, resultSet);

        long pointer = resultSet.getLong(1);
        handler.handlePointer(pointer);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface PointerHandler
  {
    public void handlePointer(long pointer);
  }

  /**
   * @author Eike Stepper
   */
  public static final class RevisionInfo
  {
    private long pointer;

    private long revised;

    public RevisionInfo(long pointer, long revised)
    {
      this.pointer = pointer;
      this.revised = revised;
    }

    public RevisionInfo()
    {
    }

    public long getPointer()
    {
      return pointer;
    }

    public void setPointer(long pointer)
    {
      this.pointer = pointer;
    }

    public long getRevised()
    {
      return revised;
    }

    public void setRevised(long revised)
    {
      this.revised = revised;
    }

    @Override
    public String toString()
    {
      return "RevisionInfo[pointer=" + pointer + ", revised=" + revised + "]";
    }

    /**
     * @author Eike Stepper
     */
    public interface Handler
    {
      public void handleRevisionInfo(CDOID id, IndexReader.RevisionInfo info);
    }
  }
}
