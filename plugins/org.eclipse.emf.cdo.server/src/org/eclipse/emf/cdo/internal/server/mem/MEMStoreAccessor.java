/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.lob.CDOLob;
import org.eclipse.emf.cdo.common.model.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;

/**
 * @author Simon McDuff
 */
public class MEMStoreAccessor extends LongIDStoreAccessor
{
  private final IQueryHandler testQueryHandler = new IQueryHandler()
  {
    public void executeQuery(CDOQueryInfo info, IQueryContext queryContext)
    {
      List<Object> filters = new ArrayList<Object>();
      Object context = info.getParameters().get("context"); //$NON-NLS-1$
      Long sleep = (Long)info.getParameters().get("sleep"); //$NON-NLS-1$
      if (context != null)
      {
        if (context instanceof EClass)
        {
          final EClass eClass = (EClass)context;
          filters.add(new Object()
          {
            @Override
            public int hashCode()
            {
              return eClass.hashCode();
            }

            @Override
            public boolean equals(Object obj)
            {
              InternalCDORevision revision = (InternalCDORevision)obj;
              return revision.getEClass().equals(eClass);
            }
          });
        }
      }

      for (InternalCDORevision revision : getStore().getCurrentRevisions())
      {
        if (sleep != null)
        {
          try
          {
            Thread.sleep(sleep);
          }
          catch (InterruptedException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }

        boolean valid = true;

        for (Object filter : filters)
        {
          if (!filter.equals(revision))
          {
            valid = false;
            break;
          }
        }

        if (valid)
        {
          if (!queryContext.addResult(revision))
          {
            // No more results allowed
            break;
          }
        }
      }
    }
  };

  private List<InternalCDORevision> newRevisions = new ArrayList<InternalCDORevision>();

  public MEMStoreAccessor(MEMStore store, ISession session)
  {
    super(store, session);
  }

  /**
   * @since 2.0
   */
  public MEMStoreAccessor(MEMStore store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public MEMStore getStore()
  {
    return (MEMStore)super.getStore();
  }

  /**
   * @since 2.0
   */
  public MEMStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    return new MEMStoreChunkReader(this, revision, feature);
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    return Collections.emptySet();
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    throw new UnsupportedOperationException();
  }

  public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    return getStore().createBranch(branchID, branchInfo);
  }

  public BranchInfo loadBranch(int branchID)
  {
    return getStore().loadBranch(branchID);
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return getStore().loadSubBranches(branchID);
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    return getStore().loadBranches(startID, endID, branchHandler);
  }

  public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    getStore().loadCommitInfos(branch, startTime, endTime, handler);
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    return getStore().readChangeSet(segments);
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    return getStore().getRevision(id, branchPoint);
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    return getStore().getRevisionByVersion(id, branchVersion);
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    getStore().handleRevisions(eClass, branch, timeStamp, exactTime, handler);
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doCommit(OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  public void write(InternalCommitContext context, OMMonitor monitor)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      super.write(context, monitor);
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment, OMMonitor monitor)
  {
    getStore().addCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment);
  }

  @Override
  protected void rollback(CommitContext context)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      for (InternalCDORevision revision : newRevisions)
      {
        store.rollbackRevision(revision);
      }
    }
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    for (InternalCDORevision revision : revisions)
    {
      writeRevision(revision);
    }
  }

  protected void writeRevision(InternalCDORevision revision)
  {
    newRevisions.add(revision);
    getStore().addRevision(revision, false);
  }

  /**
   * @since 2.0
   */
  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    for (InternalCDORevisionDelta revisionDelta : revisionDeltas)
    {
      writeRevisionDelta(revisionDelta, branch, created);
    }
  }

  /**
   * @since 2.0
   */
  protected void writeRevisionDelta(InternalCDORevisionDelta revisionDelta, CDOBranch branch, long created)
  {
    CDOID id = revisionDelta.getID();
    CDOBranchVersion version = revisionDelta.getBranch().getVersion(revisionDelta.getVersion());
    InternalCDORevision revision = getStore().getRevisionByVersion(id, version);
    if (revision.getVersion() != revisionDelta.getVersion())
    {
      throw new ConcurrentModificationException("Trying to update object " + id //$NON-NLS-1$
          + " that was already modified"); //$NON-NLS-1$
    }

    InternalCDORevision newRevision = revision.copy();
    newRevision.adjustForCommit(branch, created);

    revisionDelta.apply(newRevision);
    writeRevision(newRevision);
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    for (CDOID id : detachedObjects)
    {
      detachObject(id, branch, timeStamp);
    }
  }

  /**
   * @since 3.0
   */
  protected void detachObject(CDOID id, CDOBranch branch, long timeStamp)
  {
    getStore().detachObject(id, branch, timeStamp);
  }

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context)
  {
    getStore().queryResources(context);
  }

  public void queryXRefs(QueryXRefsContext context)
  {
    getStore().queryXRefs(context);
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    if ("TEST".equals(info.getQueryLanguage())) //$NON-NLS-1$
    {
      return testQueryHandler;
    }

    return null;
  }

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    getStore().rawExport(out, fromBranchID, toBranchID, fromCommitTime, toCommitTime);
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
      OMMonitor monitor) throws IOException
  {
    getStore().rawImport(in, fromBranchID, toBranchID, fromCommitTime, toCommitTime, monitor);
  }

  private RawStoreContext getRawStoreContext(Object object)
  {
    RawStoreContext context = (RawStoreContext)object;
    if (context == null)
    {
      context = new RawStoreContext();
    }

    return context;
  }

  public Object rawStore(InternalCDOPackageUnit[] packageUnits, Object context, OMMonitor monitor)
  {
    RawStoreContext rawStoreContext = getRawStoreContext(context);
    rawStoreContext.setPackageUnits(packageUnits);
    return rawStoreContext;
  }

  public Object rawStore(InternalCDORevision revision, Object context, OMMonitor monitor)
  {
    RawStoreContext rawStoreContext = getRawStoreContext(context);
    rawStoreContext.getRevisions().add(revision);
    return rawStoreContext;
  }

  public Object rawStore(CDOLob<?> lob, Object context, OMMonitor monitor)
  {
    // TODO: implement MEMStoreAccessor.rawStore(lob, context, monitor)
    throw new UnsupportedOperationException();
  }

  public void rawCommit(Object context, OMMonitor monitor)
  {
    RawStoreContext rawStoreContext = (RawStoreContext)context;
    if (rawStoreContext != null)
    {
      MEMStore store = getStore();
      synchronized (store)
      {
        writePackageUnits(rawStoreContext.getPackageUnits(), monitor);
        for (InternalCDORevision revision : rawStoreContext.getRevisions())
        {
          store.addRevision(revision, true);
        }
      }
    }
  }

  public void queryLobs(List<byte[]> ids)
  {
    getStore().queryLobs(ids);
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    getStore().handleLobs(fromTime, toTime, handler);
  }

  public void loadLob(byte[] id, OutputStream out) throws IOException
  {
    getStore().loadLob(id, out);
  }

  @Override
  protected void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    getStore().writeBlob(id, size, inputStream);
  }

  @Override
  protected void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    getStore().writeClob(id, size, reader);
  }

  @Override
  protected void doActivate() throws Exception
  {
    // Do nothing
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    newRevisions.clear();
  }

  @Override
  protected void doPassivate() throws Exception
  {
    // Pooling of store accessors not supported
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
    // Pooling of store accessors not supported
  }

  /**
   * @author Eike Stepper
   */
  private static final class RawStoreContext
  {
    private InternalCDOPackageUnit[] packageUnits = {};

    private List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();

    public RawStoreContext()
    {
    }

    public InternalCDOPackageUnit[] getPackageUnits()
    {
      return packageUnits;
    }

    public void setPackageUnits(InternalCDOPackageUnit[] packageUnits)
    {
      if (this.packageUnits.length != 0)
      {
        throw new IllegalStateException("Multiple calls to setPackageUnits() are forbidden");
      }

      this.packageUnits = packageUnits;
    }

    public List<InternalCDORevision> getRevisions()
    {
      return revisions;
    }
  }
}
