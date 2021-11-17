/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockAreaImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockChangeInfoImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockStateImpl;
import org.eclipse.emf.cdo.internal.common.lock.DurableCDOLockOwner;
import org.eclipse.emf.cdo.internal.common.lock.NormalCDOLockOwner;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

/**
 * Various static methods that may help with classes related to CDO locks.
 *
 * @author Caspar De Groot
 * @since 4.1
 */
public final class CDOLockUtil
{
  /**
   * @since 4.14
   */
  public static final int DURABLE_SESSION_ID = 0;

  /**
   * @since 4.14
   */
  public static final int DURABLE_VIEW_ID = 0;

  private CDOLockUtil()
  {
  }

  /**
   * @since 4.12
   */
  public static CDOID getLockedObjectID(Object lockedObject)
  {
    if (lockedObject instanceof CDOID)
    {
      return (CDOID)lockedObject;
    }

    if (lockedObject instanceof CDOIDAndBranch)
    {
      return ((CDOIDAndBranch)lockedObject).getID();
    }

    return null;
  }

  /**
   * @since 4.12
   */
  public static CDOBranch getLockedObjectBranch(Object lockedObject)
  {
    if (lockedObject instanceof CDOIDAndBranch)
    {
      return ((CDOIDAndBranch)lockedObject).getBranch();
    }

    return null;
  }

  /**
   * @since 4.15
   */
  public static int indexOf(CDOLockOwner[] lockOwners, CDOLockOwner lockOwner)
  {
    for (int i = 0; i < lockOwners.length; i++)
    {
      if (lockOwners[i] == lockOwner)
      {
        return i;
      }
    }

    return -1;
  }

  public static CDOLockState copyLockState(CDOLockState lockState)
  {
    return ((CDOLockStateImpl)lockState).copy();
  }

  /**
   * @since 4.12
   */
  public static CDOLockState copyLockState(CDOLockState lockState, Object lockedObject)
  {
    return ((CDOLockStateImpl)lockState).copy(lockedObject);
  }

  /**
   * @since 4.15
   */
  public static CDOLockState convertLockState(LockState<Object, ? extends CDOCommonView> lockState)
  {
    InternalCDOLockState cdoLockState = new CDOLockStateImpl(lockState.getLockedObject());

    for (CDOCommonView view : lockState.getReadLockOwners())
    {
      cdoLockState.addReadLockOwner(createLockOwner(view));
    }

    CDOCommonView writeLockOwner = lockState.getWriteLockOwner();
    if (writeLockOwner != null)
    {
      cdoLockState.setWriteLockOwner(createLockOwner(writeLockOwner));
    }

    CDOCommonView writeOptionOwner = lockState.getWriteOptionOwner();
    if (writeOptionOwner != null)
    {
      cdoLockState.setWriteOptionOwner(createLockOwner(writeOptionOwner));
    }

    return cdoLockState;
  }

  public static CDOLockState createLockState(Object target)
  {
    return new CDOLockStateImpl(target);
  }

  /**
   * @deprecated As of 4.15 use {@link #convertLockState(LockState)}.
   */
  @Deprecated
  public static CDOLockState createLockState(LockState<Object, ? extends CDOCommonView> lockState)
  {
    return convertLockState(lockState);
  }

  public static CDOLockOwner createLockOwner(CDOCommonView view)
  {
    int sessionID = view.getSessionID();
    int viewID = view.getViewID();
    String durableLockingID = view.getDurableLockingID();
    return createLockOwner(sessionID, viewID, durableLockingID);
  }

  /**
   * @since 4.14
   */
  public static CDOLockOwner createLockOwner(int sessionID, int viewID, String durableLockingID)
  {
    if (durableLockingID != null)
    {
      return DurableCDOLockOwner.create(sessionID, viewID, durableLockingID);
    }

    return NormalCDOLockOwner.create(sessionID, viewID);
  }

  /**
   * @deprecated As of 4.15 use the faster {@link #createLockChangeInfo(CDOBranchPoint, CDOLockOwner, Operation, LockType, Collection)} method.
   */
  @Deprecated
  public static CDOLockChangeInfo createLockChangeInfo(long timestamp, CDOLockOwner lockOwner, CDOBranch branch, Operation op, LockType lockType,
      CDOLockState[] newLockStates)
  {
    return createLockChangeInfo(branch.getPoint(timestamp), lockOwner, op, lockType, Arrays.asList(newLockStates));
  }

  /**
   * @since 4.15
   */
  public static CDOLockChangeInfo createLockChangeInfo(CDOBranchPoint branchPoint, CDOLockOwner lockOwner, Operation op, LockType lockType,
      Collection<? extends CDOLockState> newLockStates)
  {
    return new CDOLockChangeInfoImpl(branchPoint, lockOwner, op, lockType, newLockStates);
  }

  public static CDOLockChangeInfo createLockChangeInfo()
  {
    return new CDOLockChangeInfoImpl();
  }

  public static CDOLockChangeInfo createLockChangeInfo(long timestamp, CDOCommonView view, CDOBranch viewedBranch, Operation op, LockType lockType,
      CDOLockState[] newLockStates)
  {
    CDOLockOwner lockOwner = createLockOwner(view);
    return createLockChangeInfo(timestamp, lockOwner, viewedBranch, op, lockType, newLockStates);
  }

  public static LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    return new CDOLockAreaImpl(durableLockingID, userID, branchPoint, readOnly, locks);
  }

  public static LockArea createLockArea(String durableLockingID)
  {
    return new CDOLockAreaImpl(durableLockingID);
  }

  public static String createDurableLockingID()
  {
    return createDurableLockingID(LockArea.DEFAULT_DURABLE_LOCKING_ID_BYTES);
  }

  public static String createDurableLockingID(int bytes)
  {
    byte[] buffer = new byte[bytes];

    Random random = new Random(System.currentTimeMillis());
    random.nextBytes(buffer);

    return HexUtil.bytesToHex(buffer);
  }
}
