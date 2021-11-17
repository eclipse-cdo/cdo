/*
 * Copyright (c) 2011, 2012, 2014, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchAdjustable;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Caspar De Groot
 */
public final class CDOLockChangeInfoImpl implements CDOLockChangeInfo, CDOBranchAdjustable
{
  private CDOBranchPoint branchPoint;

  private final CDOLockOwner lockOwner;

  private final Operation operation;

  private final LockType lockType;

  private final List<CDOLockState> lockStates;

  private final boolean isInvalidateAll;

  public CDOLockChangeInfoImpl(CDOBranchPoint branchPoint, CDOLockOwner lockOwner, Operation operation, LockType lockType,
      Collection<? extends CDOLockState> lockStates)
  {
    this.branchPoint = branchPoint;
    this.lockOwner = lockOwner;
    this.operation = operation;
    this.lockType = lockType;
    this.lockStates = Collections.unmodifiableList(lockStates instanceof List ? (List<? extends CDOLockState>)lockStates : new ArrayList<>(lockStates));
    isInvalidateAll = false;
  }

  public CDOLockChangeInfoImpl()
  {
    lockOwner = null;
    operation = null;
    lockType = null;
    lockStates = null;
    isInvalidateAll = true;
  }

  @Override
  public void adjustBranches(CDOBranchManager newBranchManager)
  {
    if (branchPoint != null)
    {
      CDOBranch branch = branchPoint.getBranch();
      if (branch != null)
      {
        branch = newBranchManager.getBranch(branch.getID());
        branchPoint = branch.getPoint(branchPoint.getTimeStamp());
      }
    }
  }

  @Override
  public CDOBranch getBranch()
  {
    return branchPoint == null ? null : branchPoint.getBranch();
  }

  @Override
  public long getTimeStamp()
  {
    return branchPoint == null ? CDOBranchPoint.UNSPECIFIED_DATE : branchPoint.getTimeStamp();
  }

  @Override
  public Operation getOperation()
  {
    return operation;
  }

  @Override
  public LockType getLockType()
  {
    return lockType;
  }

  @Override
  public CDOLockOwner getLockOwner()
  {
    return lockOwner;
  }

  @Deprecated
  @Override
  public CDOLockState[] getLockStates()
  {
    return lockStates.toArray(new CDOLockState[lockStates.size()]);
  }

  @Override
  public List<CDOLockState> getNewLockStates()
  {
    return lockStates;
  }

  @Override
  public boolean isInvalidateAll()
  {
    return isInvalidateAll;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("CDOLockChangeInfo[branchPoint=");
    builder.append(branchPoint);
    builder.append(", operation=");
    builder.append(operation);
    builder.append(", lockType=");
    builder.append(lockType == null ? "ALL" : lockType);
    builder.append(", lockOwner=");
    builder.append(lockOwner);
    builder.append(", lockStates=");
    builder.append(lockStates);
    builder.append(", invalidateAll=");
    builder.append(isInvalidateAll);
    builder.append("]");
    return builder.toString();
  }
}
