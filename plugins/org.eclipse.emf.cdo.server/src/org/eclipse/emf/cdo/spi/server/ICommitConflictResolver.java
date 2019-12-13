/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import java.util.AbstractList;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.8
 */
public interface ICommitConflictResolver
{
  /**
   * When this method is called, the {@link CommitContext#getOldRevisions()} map is filled with the latest valid revisions, chunks ensured.
   */
  public CDOChangeSetData resolveConflicts(IStoreAccessor.CommitContext commitContext, List<InternalCDORevisionDelta> conflicts);

  /**
   * @author Eike Stepper
   */
  public static class Merging implements ICommitConflictResolver
  {
    @Override
    public CDOChangeSetData resolveConflicts(IStoreAccessor.CommitContext commitContext, List<InternalCDORevisionDelta> conflicts)
    {
      CDOBranchPoint sourceEndPoint = commitContext.getBranchPoint();
      CDOBranch branch = sourceEndPoint.getBranch();
      CDOBranchPoint startPoint = branch.getPoint(commitContext.getLastUpdateTime());
      CDOBranchPoint targetEndPoint = branch.getPoint(commitContext.getTransaction().getRepository().getLastCommitTimeStamp());

      CDOChangeSet source = createSourceChangeSet(commitContext, startPoint, sourceEndPoint);
      CDOChangeSet target = createTargetChangeSet(commitContext, startPoint, targetEndPoint);
      CDOMerger merger = createMerger();

      try
      {
        CDOChangeSetData result = merger.merge(target, source);
        conflicts.clear();
        return result;
      }
      catch (ConflictException ex)
      {
        CDOChangeSetData result = ex.getResult();
        for (CDORevisionKey revisionKey : result.getChangedObjects())
        {
          conflicts.remove(revisionKey);
        }
      }

      return null;
    }

    protected CDOMerger createMerger()
    {
      return new DefaultCDOMerger.PerFeature.ManyValued();
    }

    private CDOChangeSet createSourceChangeSet(final IStoreAccessor.CommitContext commitContext, CDOBranchPoint startPoint, CDOBranchPoint endPoint)
    {
      List<CDOIDAndVersion> newObjects = new AbstractList<CDOIDAndVersion>()
      {
        private final InternalCDORevision[] revisions = commitContext.getNewObjects();

        private final int size = revisions.length;

        @Override
        public int size()
        {
          return size;
        }

        @Override
        public CDOIDAndVersion get(int index)
        {
          return revisions[index];
        }
      };

      List<CDORevisionKey> changedObjects = new AbstractList<CDORevisionKey>()
      {
        private final InternalCDORevisionDelta[] revisionDeltas = commitContext.getDirtyObjectDeltas();

        private final int size = revisionDeltas.length;

        @Override
        public int size()
        {
          return size;
        }

        @Override
        public CDORevisionKey get(int index)
        {
          return revisionDeltas[index];
        }
      };

      List<CDOIDAndVersion> detachedObjects = new AbstractList<CDOIDAndVersion>()
      {
        private final CDOID[] ids = commitContext.getDetachedObjects();

        private final CDOBranchVersion[] branchVersions = commitContext.getDetachedObjectVersions();

        private final int size = ids.length;

        @Override
        public int size()
        {
          return size;
        }

        @Override
        public CDOIDAndVersion get(int index)
        {
          return CDOIDUtil.createIDAndVersion(ids[index], branchVersions[index].getVersion());
        }
      };

      CDOChangeSetData sourceChangeSetData = CDORevisionUtil.createChangeSetData(newObjects, changedObjects, detachedObjects);
      return CDORevisionUtil.createChangeSet(startPoint, endPoint, sourceChangeSetData);
    }

    private CDOChangeSet createTargetChangeSet(IStoreAccessor.CommitContext commitContext, CDOBranchPoint startPoint, CDOBranchPoint endPoint)
    {
      InternalRepository repository = (InternalRepository)commitContext.getTransaction().getRepository();
      CDOChangeSetData targetChangeSetData = repository.getChangeSet(startPoint, endPoint);
      return CDORevisionUtil.createChangeSet(startPoint, endPoint, targetChangeSetData);
    }
  }
}
