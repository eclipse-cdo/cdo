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
import org.eclipse.emf.cdo.internal.server.LockingManager.LockStateCollector;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Caspar De Groot
 */
public class LockStateIndication extends CDOServerReadIndication
{
  private final LockStateCollector existingLockStates = new LockStateCollector();

  private InternalLockManager lockManager;

  private CDOBranch branch;

  private int prefetchDepth = CDOLockState.DEPTH_NONE;

  private CDORevisionProvider revisionProvider;

  public LockStateIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOCK_STATE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalRepository repository = getRepository();
    CDOBranchManager branchManager = repository.getBranchManager();
    lockManager = repository.getLockingManager();

    int branchID = in.readXInt();
    branch = branchManager.getBranch(branchID);

    int idsLength = in.readXInt();
    if (idsLength < 0)
    {
      idsLength = -idsLength;
      prefetchDepth = in.readXInt();

      CDORevisionManager revisionManager = repository.getRevisionManager();
      revisionProvider = new ManagedRevisionProvider(revisionManager, branch.getHead());
    }

    if (idsLength == 0)
    {
      lockManager.getLockStates(existingLockStates);
    }
    else
    {
      int depth = prefetchDepth >= CDOLockState.DEPTH_NONE ? prefetchDepth : Integer.MAX_VALUE;

      for (int i = 0; i < idsLength; i++)
      {
        CDOID id = in.readCDOID();
        prefetchLockStates(depth, id);
      }
    }
  }

  private void prefetchLockStates(int depth, CDOID id)
  {
    addLockState(id);

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

  private void addLockState(CDOID id)
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
    out.writeCDOLockStates(existingLockStates, null);
  }
}
