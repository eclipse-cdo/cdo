/*
 * Copyright (c) 2011, 2012, 2014, 2016, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchAdjustable;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockChangeInfo;

import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Caspar De Groot
 */
public final class CDOLockChangeInfoImpl extends AbstractCDOLockChangeInfo implements CDOBranchAdjustable
{
  private static final INotifier SOURCE = new Notifier();

  private static final long serialVersionUID = 1L;

  private CDOBranchPoint branchPoint;

  private final CDOLockOwner lockOwner;

  private final CDOLockDelta[] lockDeltas;

  private final CDOLockState[] lockStates;

  private final boolean isAdministrative;

  private final boolean isInvalidateAll;

  public CDOLockChangeInfoImpl(CDOBranchPoint branchPoint, CDOLockOwner lockOwner, Collection<CDOLockDelta> lockDeltas, Collection<CDOLockState> lockStates,
      boolean isAdministrative)
  {
    super(SOURCE);
    this.branchPoint = branchPoint;
    this.lockOwner = lockOwner;
    this.lockDeltas = lockDeltas.toArray(new CDOLockDelta[lockDeltas.size()]);
    this.lockStates = lockStates.toArray(new CDOLockState[lockStates.size()]);
    this.isAdministrative = isAdministrative;
    isInvalidateAll = false;
  }

  public CDOLockChangeInfoImpl()
  {
    super(null);
    lockOwner = null;
    lockDeltas = null;
    lockStates = null;
    isAdministrative = false;
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
  public CDOLockOwner getLockOwner()
  {
    return lockOwner;
  }

  @Override
  public CDOLockDelta[] getLockDeltas()
  {
    return lockDeltas;
  }

  @Override
  public CDOLockState[] getLockStates()
  {
    return lockStates;
  }

  @Override
  public boolean isInvalidateAll()
  {
    return isInvalidateAll;
  }

  @Override
  public boolean isAdministrative()
  {
    return isAdministrative;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("CDOLockChangeInfo[branchPoint=");
    builder.append(branchPoint);

    if (isInvalidateAll)
    {
      builder.append(", invalidateAll");
    }
    else
    {
      builder.append(", deltas=");
      builder.append(Arrays.asList(lockDeltas));
    }

    builder.append("]");
    return builder.toString();
  }
}
