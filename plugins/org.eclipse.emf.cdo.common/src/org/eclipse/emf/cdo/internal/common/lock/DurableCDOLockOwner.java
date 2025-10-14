/*
 * Copyright (c) 2021, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.lock.CDOLockOwner;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.ref.Interner;

/**
 * @author Eike Stepper
 */
public final class DurableCDOLockOwner implements CDOLockOwner
{
  private static final DurableInterner INTERNER = new DurableInterner();

  private static final boolean SHOW_DURABLE_LOCKING_ID = OMPlatform.INSTANCE
      .isProperty("org.eclipse.emf.cdo.internal.common.lock.DurableCDOLockOwner.SHOW_DURABLE_LOCKING_ID");

  private final int sessionID;

  private final int viewID;

  private final String durableLockingID;

  private DurableCDOLockOwner(int sessionID, int viewID, String durableLockingID)
  {
    this.sessionID = sessionID;
    this.viewID = viewID;
    this.durableLockingID = durableLockingID;
  }

  @Override
  public int getSessionID()
  {
    return sessionID;
  }

  @Override
  public int getViewID()
  {
    return viewID;
  }

  @Override
  public String getDurableLockingID()
  {
    return durableLockingID;
  }

  @Override
  public boolean isDurableView()
  {
    return true;
  }

  @Override
  public int hashCode()
  {
    return getHashCode(sessionID, viewID, durableLockingID);
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == this;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockOwner[");
    builder.append(sessionID);
    builder.append(':');
    builder.append(viewID);
    builder.append(':');
    builder.append(SHOW_DURABLE_LOCKING_ID ? durableLockingID : "durable");
    builder.append(']');
    return builder.toString();
  }

  public static DurableCDOLockOwner create(int sessionID, int viewID, String durableLockingID)
  {
    return INTERNER.intern(sessionID, viewID, durableLockingID.intern());
  }

  private static int getHashCode(int sessionID, int viewID, String durableLockingID)
  {
    return 31 * (31 * (31 + sessionID) + viewID + durableLockingID.hashCode());
  }

  /**
   * @author Eike Stepper
   */
  private static final class DurableInterner extends Interner<DurableCDOLockOwner>
  {
    public synchronized DurableCDOLockOwner intern(int sessionID, int viewID, String durableLockingID)
    {
      int hashCode = getHashCode(sessionID, viewID, durableLockingID);
      for (Entry<DurableCDOLockOwner> entry = getEntry(hashCode); entry != null; entry = entry.getNextEntry())
      {
        DurableCDOLockOwner lockOwner = entry.get();
        if (lockOwner != null && lockOwner.sessionID == sessionID && lockOwner.viewID == viewID && lockOwner.durableLockingID == durableLockingID)
        {
          return lockOwner;
        }
      }

      DurableCDOLockOwner id = new DurableCDOLockOwner(sessionID, viewID, durableLockingID);
      addEntry(createEntry(id, hashCode));
      return id;
    }

    @Override
    protected int hashCode(DurableCDOLockOwner lockOwner)
    {
      return getHashCode(lockOwner.sessionID, lockOwner.viewID, lockOwner.durableLockingID);
    }
  }
}
