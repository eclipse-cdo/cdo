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
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
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
public abstract class StoreAccessor extends Lifecycle implements IStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StoreAccessor.class);

  private List<CommitContext> commitContexts = new ArrayList<CommitContext>();

  private Store store;

  private Object context;

  private boolean reader;

  private StoreAccessor(Store store, Object context, boolean reader)
  {
    this.store = store;
    this.context = context;
    this.reader = reader;
  }

  protected StoreAccessor(Store store, ISession session)
  {
    this(store, session, true);
  }

  protected StoreAccessor(Store store, ITransaction transaction)
  {
    this(store, transaction, false);
  }

  public Store getStore()
  {
    return store;
  }

  public boolean isReader()
  {
    return reader;
  }

  /**
   * @since 3.0
   */
  public InternalSession getSession()
  {
    if (context instanceof ITransaction)
    {
      return (InternalSession)((ITransaction)context).getSession();
    }

    return (InternalSession)context;
  }

  public ITransaction getTransaction()
  {
    if (context instanceof ITransaction)
    {
      return (ITransaction)context;
    }

    return null;
  }

  void setContext(Object context)
  {
    this.context = context;
  }

  /**
   * @since 3.0
   */
  public CDOID readResourceID(CDOID folderID, String name, CDOBranchPoint branchPoint)
  {
    QueryResourcesContext.ExactMatch context = Store.createExactMatchContext(folderID, name, branchPoint);
    queryResources(context);
    return context.getResourceID();
  }

  /**
   * @since 3.0
   */
  public CDOCommitData loadCommitData(long timeStamp)
  {
    CommitDataRevisionHandler handler = new CommitDataRevisionHandler(this, timeStamp);
    return handler.getCommitData();
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

    boolean deltas = store.getSupportedChangeFormats().contains(IStore.ChangeFormat.DELTA);

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

    store.setLastCommitTime(latest);
    store.setLastNonLocalCommitTime(latestNonLocal);
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

  public final void release()
  {
    store.releaseAccessor(this);
    commitContexts.clear();
  }

  /**
   * Add ID mappings for all new objects of a transaction to the commit context. The implementor must, for each new
   * object of the commit context, determine a permanent CDOID and make it known to the context by calling
   * {@link CommitContext#addIDMapping(CDOIDTemp, CDOID)}.
   * 
   * @since 3.0
   */
  protected abstract void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor);

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

  @Override
  protected abstract void doActivate() throws Exception;

  @Override
  protected abstract void doDeactivate() throws Exception;

  protected abstract void doPassivate() throws Exception;

  protected abstract void doUnpassivate() throws Exception;

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class CommitDataRevisionHandler implements CDORevisionHandler
  {
    private IStoreAccessor storeAccessor;

    private long timeStamp;

    private InternalCDORevisionManager revisionManager;

    private List<CDOPackageUnit> newPackageUnits = new ArrayList<CDOPackageUnit>();

    private List<CDOIDAndVersion> newObjects = new ArrayList<CDOIDAndVersion>();

    private List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();

    private List<CDOIDAndVersion> detachedObjects = new ArrayList<CDOIDAndVersion>();

    public CommitDataRevisionHandler(IStoreAccessor storeAccessor, long timeStamp)
    {
      this.storeAccessor = storeAccessor;
      this.timeStamp = timeStamp;

      InternalStore store = (InternalStore)storeAccessor.getStore();
      InternalRepository repository = store.getRepository();
      revisionManager = repository.getRevisionManager();

      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
      InternalCDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits(timeStamp, timeStamp);
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        if (!packageUnit.isSystem())
        {
          newPackageUnits.add(packageUnit);
        }
      }
    }

    public CDOCommitData getCommitData()
    {
      storeAccessor.handleRevisions(null, null, timeStamp, true, this);
      return new CDOCommitDataImpl(newPackageUnits, newObjects, changedObjects, detachedObjects);
    }

    /**
     * @since 4.0
     */
    public boolean handleRevision(CDORevision rev)
    {
      if (rev.getTimeStamp() != timeStamp)
      {
        throw new IllegalArgumentException("Invalid revision time stamp: "
            + CDOCommonUtil.formatTimeStamp(rev.getTimeStamp()));
      }

      if (rev instanceof DetachedCDORevision)
      {
        detachedObjects.add(rev);
      }
      else
      {
        InternalCDORevision revision = (InternalCDORevision)rev;
        CDOID id = revision.getID();
        CDOBranch branch = revision.getBranch();
        int version = revision.getVersion();
        if (version > CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion oldVersion = branch.getVersion(version - 1);
          InternalCDORevision oldRevision = revisionManager.getRevisionByVersion(id, oldVersion, CDORevision.UNCHUNKED,
              true);
          InternalCDORevisionDelta delta = revision.compare(oldRevision);
          changedObjects.add(delta);
        }
        else
        {
          InternalCDORevision oldRevision = getRevisionFromBase(id, branch);
          if (oldRevision != null)
          {
            InternalCDORevisionDelta delta = revision.compare(oldRevision);
            changedObjects.add(delta);
          }
          else
          {
            InternalCDORevision newRevision = revision.copy();
            newRevision.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);
            newObjects.add(newRevision);
          }
        }
      }

      return true;
    }

    private InternalCDORevision getRevisionFromBase(CDOID id, CDOBranch branch)
    {
      if (branch.isMainBranch())
      {
        return null;
      }

      CDOBranchPoint base = branch.getBase();
      InternalCDORevision revision = revisionManager.getRevision(id, base, CDORevision.UNCHUNKED,
          CDORevision.DEPTH_NONE, true);
      if (revision == null)
      {
        revision = getRevisionFromBase(id, base.getBranch());
      }

      return revision;
    }
  }
}
