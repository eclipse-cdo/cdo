/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.db4o;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.server.db4o.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.LongIDStoreAccessor;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStoreAccessor extends LongIDStoreAccessor
{
  private ObjectContainer objectContainer;

  public DB4OStoreAccessor(DB4OStore store, ISession session)
  {
    super(store, session);
  }

  public DB4OStoreAccessor(DB4OStore store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Override
  public DB4OStore getStore()
  {
    return (DB4OStore)super.getStore();
  }

  public ObjectContainer getObjectContainer()
  {
    return objectContainer;
  }

  public void setObjectContainer(ObjectContainer objectContainer)
  {
    this.objectContainer = objectContainer;
  }

  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    return packageUnit.getEPackages(true);
  }

  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    // TODO Where is this monitor worked on?
    monitor.begin(packageUnits.length);

    try
    {
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        PrimitivePackageUnit primitivePackageUnit = PrimitivePackageUnit.getPrimitivePackageUnit(getStore(),
            packageUnit);
        getObjectContainer().store(primitivePackageUnit);
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  public Collection<InternalCDOPackageUnit> readPackageUnits()
  {
    Collection<PrimitivePackageUnit> primitivePackageUnits = getObjectContainer().query(PrimitivePackageUnit.class);
    List<InternalCDOPackageUnit> result = new ArrayList<InternalCDOPackageUnit>();
    for (PrimitivePackageUnit primitivePackageUnit : primitivePackageUnits)
    {
      InternalCDOPackageUnit packageUnit = PrimitivePackageUnit.getPackageUnit(getStore().getRepository()
          .getPackageRegistry(), primitivePackageUnit);
      result.add(packageUnit);
    }
    return result;
  }

  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache)
  {
    InternalCDORevision revision = getRevisionFromContainer(branchPoint.getBranch().getID(), id);
    cache.addRevision(revision);
    return revision;
  }

  public void queryResources(QueryResourcesContext context)
  {
    final long folderID = CDOIDUtil.getLong(context.getFolderID());
    final String name = context.getName();
    final boolean exactMatch = context.exactMatch();

    ObjectSet<PrimitiveRevision> revisionObjectSet = getObjectContainer().query(new Predicate<PrimitiveRevision>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(PrimitiveRevision primitiveRevision)
      {
        if (!primitiveRevision.isResourceNode())
        {
          return false;
        }
        // is Root resource
        if (primitiveRevision.isRootResource())
        {
          return false;
        }

        if (PrimitiveRevision.compareIDObject(primitiveRevision.getContainerID(), folderID))
        {
          String candidateName = (String)primitiveRevision.getValues().get(EresourcePackage.CDO_RESOURCE__NAME);
          if (exactMatch)
          {
            if (candidateName != null && candidateName.equals(name))
            {
              return true;
            }
          }
          else
          {
            // provided name is prefix of the resource name
            if (candidateName != null && candidateName.startsWith(name))
            {
              return true;
            }
          }
        }

        return false;
      }

    });

    for (PrimitiveRevision revision : revisionObjectSet)
    {
      if (!context.addResource(PrimitiveRevision.getIDFromObject(revision.getId())))
      {
        // No more results allowed
        break;
      }
    }

  }

  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature)
  {
    throw new UnsupportedOperationException("not yet implemented");
  }

  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    setObjectContainer(getStore().openClient());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    getObjectContainer().close();
    setObjectContainer(null);
  }

  @Override
  protected void doPassivate() throws Exception
  {
    getObjectContainer().rollback();
  }

  @Override
  protected void doUnpassivate() throws Exception
  {
  }

  @Override
  protected void rollback(CommitContext commitContext)
  {
    getObjectContainer().rollback();
  }

  private InternalCDORevision getRevisionFromContainer(int branchId, CDOID id)
  {
    PrimitiveRevision lastRevision = QueryUtil.getLastPrimitiveRevision(getObjectContainer(), branchId, id);

    // Revision does not exist. Return null to signal inexistent Revision
    if (lastRevision == null)
    {
      return null;
    }
    return PrimitiveRevision.getRevision(getStore(), lastRevision);
  }

  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache)
  {
    PrimitiveRevision revisionByVersion = QueryUtil.getPrimitiveRevisionByVersion(getObjectContainer(), id,
        branchVersion.getBranch().getID(), branchVersion.getVersion());

    // Revision does not exist. Return null to signal inexistent Revision
    if (revisionByVersion == null)
    {
      return null;
    }
    InternalCDORevision revision = PrimitiveRevision.getRevision(getStore(), revisionByVersion);
    cache.addRevision(revision);
    return revision;
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, CDORevisionHandler handler)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public Set<CDOID> readChangeSet(CDOChangeSetSegment... segments)
  {
    return null;
  }

  public void queryXRefs(final QueryXRefsContext context)
  {
    final int branchID = context.getBranch().getID();

    for (final CDOID target : context.getTargetObjects().keySet())
    {
      final long targetID = CDOIDUtil.getLong(target);

      for (final EClass eClass : context.getSourceCandidates().keySet())
      {
        final String eClassName = eClass.getName();
        final String nsURI = eClass.getEPackage().getNsURI();
        final List<EReference> eReferences = context.getSourceCandidates().get(eClass);
        getObjectContainer().query(new Predicate<PrimitiveRevision>()
        {
          private static final long serialVersionUID = 1L;

          private boolean moreResults = true;

          @Override
          public boolean match(PrimitiveRevision primitiveRevision)
          {
            if (!moreResults)
            {
              return false;
            }
            if (!primitiveRevision.getClassName().equals(eClassName))
            {
              return false;
            }

            if (!primitiveRevision.getNsURI().equals(nsURI))
            {
              return false;
            }

            if (!(primitiveRevision.getBranchID() == branchID))
            {
              return false;
            }

            for (EReference eReference : eReferences)
            {
              Object obj = primitiveRevision.getValues().get(eReference.getFeatureID());
              if (obj instanceof List)
              {
                List<?> list = (List<?>)obj;
                if (list.contains(targetID))
                {
                  moreResults = context.addXRef(target, PrimitiveRevision.getIDFromObject(primitiveRevision.getId()),
                      eReference, 0);
                }
              }
              else if (obj instanceof CDOID)
              {
                if (CDOIDUtil.getLong((CDOID)obj) == targetID)
                {
                  moreResults = context.addXRef(target, PrimitiveRevision.getIDFromObject(primitiveRevision.getId()),
                      eReference, 0);
                }
              }
            }

            return false;
          }
        });

      }

    }
  }

  public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
      throws IOException
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public int createBranch(int branchID, BranchInfo branchInfo)
  {
    return 0;
  }

  public BranchInfo loadBranch(int branchID)
  {
    return null;
  }

  public SubBranchInfo[] loadSubBranches(int branchID)
  {
    return null;
  }

  public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler)
  {
    return 0;
  }

  public void loadCommitInfos(final CDOBranch branch, final long startTime, final long endTime,
      CDOCommitInfoHandler handler)
  {
    ObjectSet<CommitInfo> resultSet = getObjectContainer().query(new Predicate<CommitInfo>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(CommitInfo info)
      {
        if (startTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() < startTime)
        {
          return false;
        }

        if (endTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() > endTime)
        {
          return false;
        }

        if (branch != null && !(info.getBranchID() == branch.getID()))
        {
          return false;
        }

        return true;
      }

    });
    InternalRepository repository = getStore().getRepository();
    InternalCDOCommitInfoManager commitInfoManager = repository.getCommitInfoManager();
    InternalCDOBranchManager branchManager = repository.getBranchManager();

    // Although not specified in the API, the test-case suite
    // suggests CommitInfos should be returned ordered by timeStamp

    List<CommitInfo> infos = new ArrayList<CommitInfo>();
    infos.addAll(resultSet);
    Collections.sort(infos, new Comparator<CommitInfo>()
    {

      public int compare(CommitInfo arg0, CommitInfo arg1)
      {
        return arg0.getTimeStamp() <= arg1.getTimeStamp() ? -1 : 1;
      }
    });
    for (CommitInfo info : infos)
    {
      info.handle(branchManager, commitInfoManager, handler);
    }
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getObjectContainer().commit();
    }
    catch (Exception e)
    {
      OM.LOG.error(e);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeCommitInfo(CDOBranch branch, long timeStamp, String userID, String comment, OMMonitor monitor)
  {
    CommitInfo commitInfo = new CommitInfo(branch.getID(), timeStamp, userID, comment);
    writeObject(commitInfo, monitor);
  }

  @Override
  protected void writeRevisions(InternalCDORevision[] revisions, CDOBranch branch, OMMonitor monitor)
  {
    try
    {
      monitor.begin(revisions.length);
      for (InternalCDORevision revision : revisions)
      {
        writeRevision(revision, monitor.fork());
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected void checkDuplicateResources(CDORevision revision) throws IllegalStateException
  {
    final long folderID = CDOIDUtil.getLong((CDOID)revision.data().getContainerID());
    final long revisionID = CDOIDUtil.getLong(revision.getID());
    final String name = (String)revision.data().get(EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 0);

    ObjectSet<PrimitiveRevision> resultSet = getObjectContainer().query(new Predicate<PrimitiveRevision>()
    {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean match(PrimitiveRevision revision)
      {
        if (revision.isResourceNode() && PrimitiveRevision.compareIDObject(revision.getContainerID(), folderID))
        {
          String candidateName = (String)revision.getValues().get(EresourcePackage.CDO_RESOURCE__NAME);
          if (StringUtil.compare(name, candidateName) == 0)
          {
            if (!PrimitiveRevision.compareIDObject(revision.getId(), revisionID))
            {
              return true;
            }
          }
        }

        return false;
      }

    });

    if (!resultSet.isEmpty())
    {
      throw new IllegalStateException("Duplicate resource or folder: " + name + " in folder " + folderID); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  protected void writeRevision(InternalCDORevision revision, OMMonitor monitor)
  {
    Async async = null;
    monitor.begin(10);
    try
    {
      try
      {
        async = monitor.forkAsync();
        if (revision.isResourceFolder() || revision.isResource())
        {
          checkDuplicateResources(revision);
        }
      }
      finally
      {
        if (async != null)
        {
          async.stop();
        }
      }

      // If revision is in the store, remove old, store new
      PrimitiveRevision revisionAlreadyInStore = QueryUtil.getLastPrimitiveRevision(getObjectContainer(), revision
          .getBranch().getID(), revision.getID());
      if (revisionAlreadyInStore != null)
      {
        QueryUtil.removeRevisionFromContainer(getObjectContainer(), revision.getBranch().getID(), revision.getID());
      }

      PrimitiveRevision primitiveRevision = PrimitiveRevision.getPrimitiveRevision(revision);
      writeObject(primitiveRevision, monitor);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void writeObject(Object object, OMMonitor monitor)
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      getObjectContainer().store(object);
    }
    catch (Throwable t)
    {
      OM.LOG.error(t);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }

  @Override
  protected void writeRevisionDeltas(InternalCDORevisionDelta[] revisionDeltas, CDOBranch branch, long created,
      OMMonitor monitor)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  protected void detachObjects(CDOID[] detachedObjects, CDOBranch branch, long timeStamp, OMMonitor monitor)
  {
    // TODO Work on monitor
    for (CDOID id : detachedObjects)
    {
      QueryUtil.removeRevisionFromContainer(getObjectContainer(), branch.getID(), id);
    }
  }

}
