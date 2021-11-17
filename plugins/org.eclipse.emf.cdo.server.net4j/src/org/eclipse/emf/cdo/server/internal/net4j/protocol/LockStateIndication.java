/*
 * Copyright (c) 2011, 2012, 2014-2017, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Maxime Porhel (Obeo) - bug 574275
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Caspar De Groot
 */
public class LockStateIndication extends CDOServerReadIndication
{
  private Collection<CDOLockState> existingLockStates;

  private int prefetchDepth = CDOLockState.DEPTH_NONE;

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
    CDOBranch branch = branchManager.getBranch(branchID);

    existingLockStates = new ArrayList<>();
    CDORevisionProvider revisionProvider;

    int idsLength = in.readXInt();
    if (idsLength < 0)
    {
      idsLength = -idsLength;
      prefetchDepth = in.readXInt();

      CDORevisionManager revisionManager = repository.getRevisionManager();
      revisionProvider = new ManagedRevisionProvider(revisionManager, branch.getHead());
    }
    else
    {
      revisionProvider = null;
    }

    if (idsLength == 0)
    {
      Collection<LockState<Object, IView>> lockStates = lockManager.getLockStates();
      for (LockState<Object, IView> lockState : lockStates)
      {
        existingLockStates.add(CDOLockUtil.convertLockState(lockState));
      }
    }
    else
    {
      for (int i = 0; i < idsLength; i++)
      {
        CDOID id = in.readCDOID();
        prefetchLockStates(lockManager, prefetchDepth >= CDOLockState.DEPTH_NONE ? prefetchDepth : Integer.MAX_VALUE, id, branch, revisionProvider);
      }
    }
  }

  private void prefetchLockStates(InternalLockManager lockManager, int depth, CDOID id, CDOBranch branch, CDORevisionProvider revisionProvider)
  {
    addLockState(lockManager, id, branch);

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
              prefetchLockStates(lockManager, depth, (CDOID)value, branch, revisionProvider);
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
                  prefetchLockStates(lockManager, depth, (CDOID)e, branch, revisionProvider);
                }
              }
            }
          }
        }
      }
    }
  }

  private void addLockState(InternalLockManager lockManager, CDOID id, CDOBranch branch)
  {
    Object key = lockManager.getLockKey(id, branch);

    LockState<Object, IView> lockState = lockManager.getLockState(key);
    if (lockState != null)
    {
      existingLockStates.add(CDOLockUtil.convertLockState(lockState));
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeXInt(existingLockStates.size());
    for (CDOLockState lockState : existingLockStates)
    {
      out.writeCDOLockState(lockState);
    }
  }
}
