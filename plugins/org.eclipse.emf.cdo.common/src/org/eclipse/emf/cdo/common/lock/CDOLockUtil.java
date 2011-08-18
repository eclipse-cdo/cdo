/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockChangeInfoImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockOwnerImpl;
import org.eclipse.emf.cdo.internal.common.lock.CDOLockStateImpl;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

/**
 * Various static methods that may help with classes related to CDO locks.
 * 
 * @author Caspar De Groot
 * @since 4.1
 */
public final class CDOLockUtil
{
  private CDOLockUtil()
  {
  }

  public static CDOLockState createLockState(Object target)
  {
    return new CDOLockStateImpl(target);
  }

  public static CDOLockState createLockState(LockState<Object, ? extends CDOCommonView> lockState)
  {
    CheckUtil.checkArg(lockState, "lockState");

    InternalCDOLockState cdoLockState = new CDOLockStateImpl(lockState.getLockedObject());

    for (CDOCommonView view : lockState.getReadLockOwners())
    {
      int sessionID = view.getSession().getSessionID();
      int viewID = view.getViewID();
      CDOLockOwner owner = new CDOLockOwnerImpl(sessionID, viewID);
      cdoLockState.addReadLockOwner(owner);
    }

    CDOCommonView writeLockOwner = lockState.getWriteLockOwner();
    if (writeLockOwner != null)
    {
      CDOLockOwner owner = createLockOwner(writeLockOwner);
      cdoLockState.setWriteLockOwner(owner);
    }

    CDOCommonView writeOptionOwner = lockState.getWriteOptionOwner();
    if (writeOptionOwner != null)
    {
      CDOLockOwner owner = createLockOwner(writeOptionOwner);
      cdoLockState.setWriteOptionOwner(owner);
    }

    return cdoLockState;
  }

  public static CDOLockOwner createLockOwner(CDOCommonView view)
  {
    CDOCommonSession session = view.getSession();
    if (session != null)
    {
      int sessionID = session.getSessionID();
      int viewID = view.getViewID();
      return new CDOLockOwnerImpl(sessionID, viewID);
    }
    return CDOLockOwner.UNKNOWN;
  }

  public static CDOLockChangeInfo createLockChangeInfo(long timestamp, CDOLockOwner lockOwner, CDOBranch branch,
      Operation op, CDOLockState[] cdoLockStates)
  {
    return new CDOLockChangeInfoImpl(branch.getPoint(timestamp), lockOwner, cdoLockStates, op);
  }

  public static CDOLockChangeInfo createLockChangeInfo(long timestamp, CDOCommonView view, CDOBranch viewedBranch,
      Operation op, CDOLockState[] cdoLockStates)
  {
    CDOLockOwner lockOwner = createLockOwner(view);
    return createLockChangeInfo(timestamp, lockOwner, viewedBranch, op, cdoLockStates);
  }
}
