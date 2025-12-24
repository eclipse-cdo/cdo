/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.db;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.server.internal.lissome.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class IndexWriter extends IndexReader
{
  private static final ContextTracer TRACER = new ContextTracer(OM.INDEX, IndexWriter.class);

  protected PreparedStatement addCommitInfoStatement;

  protected PreparedStatement addObjectStatement;

  protected PreparedStatement reviseOldRevisionsStatement;

  protected PreparedStatement createBranchStatement;

  private Set<PreparedStatement> batches = new HashSet<>();

  public IndexWriter(Index index)
  {
    super(index);

    try
    {
      connection.setAutoCommit(false);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void commit()
  {
    try
    {
      for (PreparedStatement stmt : batches)
      {
        stmt.executeBatch();
      }

      connection.commit();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      batches.clear();
    }
  }

  protected void execute(PreparedStatement stmt) throws SQLException
  {
    index.trace(TRACER, stmt);
    stmt.addBatch();
    batches.add(stmt);
  }

  public void addCommitInfo(CDOBranchPoint branchPoint, long pointer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("addCommitInfo: {0}, {1}", branchPoint, pointer); //$NON-NLS-1$
    }

    try
    {
      if (addCommitInfoStatement == null)
      {
        String sql = index.commitInfos.sqlAddCommitInfo();
        addCommitInfoStatement = connection.prepareStatement(sql);
      }

      int column = 0;

      long timeStamp = branchPoint.getTimeStamp();
      addCommitInfoStatement.setLong(++column, timeStamp);

      if (supportingBranches)
      {
        CDOBranch branch = branchPoint.getBranch();
        int branchID = branch.getID();
        addCommitInfoStatement.setInt(++column, branchID);
      }

      addCommitInfoStatement.setLong(++column, pointer);
      execute(addCommitInfoStatement);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void addObjects(InternalCDORevision[] newRevisions, long[] pointers)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("addObjects: {0}, {1}", Arrays.asList(newRevisions), Arrays.asList(pointers)); //$NON-NLS-1$
    }

    try
    {
      if (addObjectStatement == null)
      {
        String sql = index.objects.sqlAddRevision();
        addObjectStatement = connection.prepareStatement(sql);
      }

      for (int i = 0; i < newRevisions.length; i++)
      {
        InternalCDORevision revision = newRevisions[i];
        long pointer = pointers[i];

        boolean detached = revision instanceof DetachedCDORevision;
        int column = 0;

        CDOID oid = revision.getID();
        index.setCDOID(addObjectStatement, ++column, oid);

        if (supportingBranches)
        {
          int branchID = revision.getBranch().getID();
          addObjectStatement.setInt(++column, branchID);
        }

        if (supportingAudits)
        {
          long time = revision.getTimeStamp();
          addObjectStatement.setLong(++column, time);

          long revised = revision.getRevised();
          addObjectStatement.setLong(++column, revised);

          int version = revision.getVersion();
          if (detached)
          {
            version = -version;
          }

          addObjectStatement.setInt(++column, version);
        }

        int cid = getStore().getMetaID(revision.getEClass());
        addObjectStatement.setInt(++column, cid);

        CDOID containerID = detached ? CDOID.NULL : (CDOID)revision.getContainerID();
        index.setCDOID(addObjectStatement, ++column, containerID);

        if (!detached && revision.isResourceNode())
        {
          String name = (String)revision.data().get(EresourcePackage.Literals.CDO_RESOURCE_NODE__NAME, 0);
          if (name == null)
          {
            name = CDOURIUtil.SEGMENT_SEPARATOR;
          }

          addObjectStatement.setString(++column, name);
        }
        else
        {
          addObjectStatement.setNull(++column, DBType.VARCHAR.getCode());
        }

        addObjectStatement.setLong(++column, pointer);

        execute(addObjectStatement);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void updateObjects(InternalCDORevision[] newRevisions, long[] pointers)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("updateObjects: {0}, {1}", Arrays.asList(newRevisions), Arrays.asList(pointers)); //$NON-NLS-1$
    }

    if (supportingAudits)
    {
      reviseOldRevisions(newRevisions);
      addObjects(newRevisions, pointers);
    }
    else
    {
      // XXX
    }
  }

  protected void reviseOldRevisions(InternalCDORevision[] newRevisions)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("reviseOldRevisions: {0}", Arrays.asList(newRevisions)); //$NON-NLS-1$
    }

    try
    {
      if (reviseOldRevisionsStatement == null)
      {
        String sql = index.objects.sqlReviseOldRevisions();
        reviseOldRevisionsStatement = connection.prepareStatement(sql);
      }

      for (int i = 0; i < newRevisions.length; i++)
      {
        InternalCDORevision revision = newRevisions[i];
        int column = 0;

        long revised = revision.getTimeStamp() - 1;
        reviseOldRevisionsStatement.setLong(++column, revised);

        CDOID oid = revision.getID();
        index.setCDOID(reviseOldRevisionsStatement, ++column, oid);

        if (supportingBranches)
        {
          int branchID = revision.getBranch().getID();
          reviseOldRevisionsStatement.setInt(++column, branchID);
        }

        int version = Math.abs(revision.getVersion()) - 1;
        reviseOldRevisionsStatement.setInt(++column, version);

        execute(reviseOldRevisionsStatement);
      }
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void detachObjects(CDOBranchPoint branchPoint, CDOID[] ids, InternalCDORevision[] detachedRevisions, long[] detachedObjectPointers)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("detachObjects: {0}, {1}", Arrays.asList(ids), branchPoint); //$NON-NLS-1$
    }

    if (supportingAudits)
    {
      updateObjects(detachedRevisions, detachedObjectPointers);
    }
    else
    {
      // TODO: implement IndexWriter.detachObjects(ids, types, branchPoint, pointers)
      throw new UnsupportedOperationException();

      // long[] pointers = new long[ids.length];
      // for (int i = 0; i < ids.length; i++)
      // {
      // CDOID id = ids[i];
      // pointers[i] = readPointerNonAudit(id);
      //
      // // XXX Delete revision
      // }
    }
  }

  protected long readPointerNonAudit(CDOID id)
  {
    // TODO: implement IndexWriter.readPointerNonAudit(id)
    throw new UnsupportedOperationException();
  }

  public void createBranch(int branchID, String name, CDOBranchPoint base, long pointer)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("createBranch: {0}, {1}, {2}", branchID, name, base); //$NON-NLS-1$
    }

    try
    {
      if (createBranchStatement == null)
      {
        String sql = index.branches.sqlCreateBranch();
        createBranchStatement = connection.prepareStatement(sql);
      }

      createBranchStatement.setInt(1, branchID);
      createBranchStatement.setString(2, name);
      createBranchStatement.setInt(3, base.getBranch().getID());
      createBranchStatement.setLong(4, base.getTimeStamp());

      execute(createBranchStatement);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }
}
