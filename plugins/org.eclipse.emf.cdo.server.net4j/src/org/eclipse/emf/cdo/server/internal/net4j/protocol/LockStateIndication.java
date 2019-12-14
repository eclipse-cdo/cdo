/*
 * Copyright (c) 2011, 2012, 2014-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalView;

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
    int viewID = in.readXInt();
    InternalView view = getView(viewID);
    if (view == null)
    {
      throw new IllegalStateException("View not found");
    }

    InternalLockManager lockManager = getRepository().getLockingManager();

    existingLockStates = new ArrayList<>();
    int n = in.readXInt();
    if (n < 0)
    {
      n = -n;
      prefetchDepth = in.readXInt();
    }

    if (n == 0)
    {
      Collection<LockState<Object, IView>> lockStates = lockManager.getLockStates();
      for (LockState<Object, IView> lockState : lockStates)
      {
        existingLockStates.add(CDOLockUtil.createLockState(lockState));
      }
    }
    else
    {
      for (int i = 0; i < n; i++)
      {
        CDOID id = in.readCDOID();
        prefetchLockStates(prefetchDepth >= CDOLockState.DEPTH_NONE ? prefetchDepth : Integer.MAX_VALUE, id, view);
      }
    }
  }

  private void prefetchLockStates(int depth, CDOID id, InternalView view)
  {
    addLockState(id, view);

    if (depth > CDOLockState.DEPTH_NONE)
    {
      --depth;

      InternalCDORevision revision = (InternalCDORevision)view.getRevision(id);

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
              prefetchLockStates(depth, (CDOID)value, view);
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
                  prefetchLockStates(depth, (CDOID)e, view);
                }
              }
            }
          }
        }
      }
    }
  }

  private void addLockState(CDOID id, InternalView view)
  {
    Object key;

    if (getRepository().isSupportingBranches())
    {
      key = CDOIDUtil.createIDAndBranch(id, view.getBranch());
    }
    else
    {
      key = id;
    }

    LockState<Object, IView> lockState = getRepository().getLockingManager().getLockState(key);
    if (lockState != null)
    {
      existingLockStates.add(CDOLockUtil.createLockState(lockState));
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
