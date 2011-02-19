/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class StoreAccessor extends StoreAccessorBase
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StoreAccessor.class);

  private List<CommitContext> commitContexts = new ArrayList<CommitContext>();

  protected StoreAccessor(Store store, ISession session)
  {
    super(store, session);
  }

  protected StoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
  }

  /**
   * @since 3.0
   */
  public void write(InternalCommitContext context, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing transaction: {0}", getTransaction()); //$NON-NLS-1$
    }

    commitContexts.add(context);
    CDOBranch branch = context.getBranchPoint().getBranch();
    long timeStamp = context.getBranchPoint().getTimeStamp();
    long previousTimeStamp = context.getPreviousTimeStamp();
    String userID = context.getUserID();
    String commitComment = context.getCommitComment();

    boolean deltas = getStore().getSupportedChangeFormats().contains(IStore.ChangeFormat.DELTA);

    InternalCDOPackageUnit[] newPackageUnits = context.getNewPackageUnits();
    InternalCDORevision[] newObjects = context.getNewObjects();
    CDOID[] detachedObjects = context.getDetachedObjects();
    int dirtyCount = deltas ? context.getDirtyObjectDeltas().length : context.getDirtyObjects().length;

    try
    {
      monitor.begin(1 + newPackageUnits.length + 2 + newObjects.length + detachedObjects.length + dirtyCount);
      writeCommitInfo(branch, timeStamp, previousTimeStamp, userID, commitComment, monitor.fork());

      if (newPackageUnits.length != 0)
      {
        writePackageUnits(newPackageUnits, monitor.fork(newPackageUnits.length));
      }

      addIDMappings(context, monitor.fork());
      applyIDMappings(context, monitor);

      if (detachedObjects.length != 0)
      {
        detachObjects(detachedObjects, branch, timeStamp, monitor.fork(detachedObjects.length));
      }

      if (newObjects.length != 0)
      {
        writeRevisions(newObjects, branch, monitor.fork(newObjects.length));
      }

      if (dirtyCount != 0)
      {
        if (deltas)
        {
          writeRevisionDeltas(context.getDirtyObjectDeltas(), branch, timeStamp, monitor.fork(dirtyCount));
        }
        else
        {
          writeRevisions(context.getDirtyObjects(), branch, monitor.fork(dirtyCount));
        }
      }

      ExtendedDataInputStream in = context.getLobs();
      if (in != null)
      {
        try
        {
          int count = in.readInt();
          for (int i = 0; i < count; i++)
          {
            byte[] id = in.readByteArray();
            long size = in.readLong();
            if (size > 0)
            {
              writeBlob(id, size, in);
            }
            else
            {
              writeClob(id, -size, new InputStreamReader(in));
            }
          }
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }
    finally
    {
      monitor.done();
    }
  }

  /**
   * @since 3.0
   */
  public final void commit(OMMonitor monitor)
  {
    doCommit(monitor);

    long latest = CDORevision.UNSPECIFIED_DATE;
    long latestNonLocal = CDORevision.UNSPECIFIED_DATE;
    for (CommitContext commitContext : commitContexts)
    {
      CDOBranchPoint branchPoint = commitContext.getBranchPoint();
      long timeStamp = branchPoint.getTimeStamp();
      if (timeStamp > latest)
      {
        latest = timeStamp;
      }

      CDOBranch branch = branchPoint.getBranch();
      if (!branch.isLocal())
      {
        if (timeStamp > latestNonLocal)
        {
          latestNonLocal = timeStamp;
        }
      }
    }

    getStore().setLastCommitTime(latest);
    getStore().setLastNonLocalCommitTime(latestNonLocal);
  }

  /**
   * @since 3.0
   */
  protected abstract void doCommit(OMMonitor monitor);

  public void rollback()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Rolling back transaction: {0}", getTransaction()); //$NON-NLS-1$
    }

    for (CommitContext commitContext : commitContexts)
    {
      rollback(commitContext);
    }
  }

  protected abstract void rollback(CommitContext commitContext);

  @Override
  public void release()
  {
    super.release();
    commitContexts.clear();
  }

  /**
   * @since 3.0
   */
  protected void applyIDMappings(InternalCommitContext context, OMMonitor monitor)
  {
    context.applyIDMappings(monitor.fork());
  }

  /**
   * @since 4.0
   */
  protected abstract void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor);

  /**
   * @since 3.0
   */
  protected abstract void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor);

  /**
   * @since 3.0
   */
  protected abstract void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch,
      long created, OMMonitor monitor);

  /**
   * @since 3.0
   */
  protected abstract void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor);

  /**
   * @since 4.0
   */
  protected abstract void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException;

  /**
   * @since 4.0
   */
  protected abstract void writeClob(byte[] id, long size, Reader reader) throws IOException;
}
