/*
 * Copyright (c) 2011, 2012, 2014-2017, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Maxime Porhel (Obeo) - bug 574275
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Caspar De Groot
 */
public class LockStateIndication extends CDOServerReadIndication
{
  private static final Config REVISION_LOADING_CONFIG = new Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, 0);

  private InternalLockManager.LockStateQuery query;

  private CDOBranch branch;

  private int prefetchDepth = CDOLockState.DEPTH_NONE;

  private CDORevisionProvider revisionProvider;

  private Map<CDOID, Integer> targetIDs;

  public LockStateIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalRepository repository = getRepository();
    CDOBranchManager branchManager = repository.getBranchManager();
    InternalLockManager lockManager = repository.getLockingManager();

    int branchID = in.readXInt();
    branch = branchManager.getBranch(branchID);

    int idsLength = in.readXInt();
    if (idsLength < 0)
    {
      idsLength = -idsLength;
      prefetchDepth = in.readXInt();

      CDORevisionManager revisionManager = repository.getRevisionManager();
      revisionProvider = new LockStateRevisionProvider(revisionManager, branch.getHead());
    }

    if (idsLength == 0)
    {
      query = lockManager.snapshotLockStates(getSession(), branch, null);
    }
    else
    {
      targetIDs = new LinkedHashMap<>();
      int depth = prefetchDepth >= CDOLockState.DEPTH_NONE ? prefetchDepth : Integer.MAX_VALUE;

      for (int i = 0; i < idsLength; i++)
      {
        CDOID id = in.readCDOID();
        prefetchLockStates(depth, id);
      }

      query = lockManager.snapshotLockStates(getSession(), branch, targetIDs.keySet());
    }
  }

  private void prefetchLockStates(int depth, CDOID id)
  {
    Integer previousDepth = targetIDs.get(id);
    if (previousDepth != null && previousDepth >= depth)
    {
      return;
    }

    targetIDs.put(id, depth);

    if (depth > CDOLockState.DEPTH_NONE)
    {
      --depth;

      InternalCDORevision revision = (InternalCDORevision)revisionProvider.getRevision(id);
      if (revision == null)
      {
        return;
      }

      CDOClassInfo classInfo = revision.getClassInfo();
      for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
      {
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isContainment())
          {
            Object value = revision.getValue(reference);
            if (value instanceof CDOID)
            {
              prefetchLockStates(depth, (CDOID)value);
            }
            else if (value instanceof Collection<?>)
            {
              Collection<?> c = (Collection<?>)value;
              for (Object e : c)
              {
                // If this revision was loaded with referenceChunk != UNCHUNKED,
                // then some elements might be uninitialized, i.e. not
                // instanceof CDOID. (See bug 339313.)
                if (e instanceof CDOID)
                {
                  prefetchLockStates(depth, (CDOID)e);
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeXLong(query.getLockModCount());
    out.writeCDOLockStates(query.getLockStates(), null);
  }

  /**
   * Provides revisions for lock-state prefetch with only containment lists materialized. Lock-state traversal needs
   * containment structure, but none of the unrelated feature values.
   *
   * @author Eike Stepper
   */
  private final class LockStateRevisionProvider extends ManagedRevisionProvider
  {
    public LockStateRevisionProvider(CDORevisionManager revisionManager, CDOBranchPoint branchPoint)
    {
      super(revisionManager, branchPoint);
    }

    @Override
    public CDORevision getRevision(CDOID id)
    {
      InternalCDORevision revision = revisionManager.getRevision(id, branchPoint, REVISION_LOADING_CONFIG);
      if (revision != null)
      {
        for (EStructuralFeature feature : revision.getClassInfo().getAllPersistentContainments())
        {
          if (feature.isMany())
          {
            CDOList list = revision.getListOrNull(feature);
            if (list != null && !list.isFullyLoaded())
            {
              getRepository().ensureChunk(revision, feature, 0, list.size());
            }
          }
        }
      }

      return revision;
    }
  }
}
