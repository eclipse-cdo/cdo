/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

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

  public int createBranch(BranchInfo branchInfo)
  {
    return getStore().createBranch(branchInfo);
  }

  public BranchInfo loadBranch(int branchID)
  {
    return getStore().loadBranch(branchID);
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return getStore().loadSubBranches(branchID);
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

  /**
   * @since 2.0
   */
  public void commit(OMMonitor monitor)
  {
    // Do nothing
  }

  @Override
  public void write(CommitContext context, OMMonitor monitor)
  {
    MEMStore store = getStore();
    synchronized (store)
    {
      super.write(context, monitor);
    }
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
    getStore().addRevision(revision);
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

    InternalCDORevision newRevision = (InternalCDORevision)revision.copy();
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

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    if ("TEST".equals(info.getQueryLanguage())) //$NON-NLS-1$
    {
      return testQueryHandler;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public void refreshRevisions()
  {
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
}
