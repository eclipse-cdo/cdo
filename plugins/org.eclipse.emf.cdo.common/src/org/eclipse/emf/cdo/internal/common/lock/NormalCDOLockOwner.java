/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.ref.Interner;

/**
 * @author Eike Stepper
 */
public final class NormalCDOLockOwner implements CDOLockOwner
{
  private static final NormalInterner INTERNER = new NormalInterner();

  private final int sessionID;

  private final int viewID;

  private NormalCDOLockOwner(int sessionID, int viewID)
  {
    this.sessionID = sessionID;
    this.viewID = viewID;
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
    return null;
  }

  @Override
  public boolean isDurableView()
  {
    return false;
  }

  @Override
  public int hashCode()
  {
    return getHashCode(sessionID, viewID);
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
    builder.append(']');
    return builder.toString();
  }

  public static NormalCDOLockOwner create(int sessionID, int viewID)
  {
    return INTERNER.intern(sessionID, viewID);
  }

  private static int getHashCode(int sessionID, int viewID)
  {
    return 31 * (31 + sessionID) + viewID;
  }

  /**
   * @author Eike Stepper
   */
  private static final class NormalInterner extends Interner<NormalCDOLockOwner>
  {
    public synchronized NormalCDOLockOwner intern(int sessionID, int viewID)
    {
      int hashCode = getHashCode(sessionID, viewID);
      for (Entry<NormalCDOLockOwner> entry = getEntry(hashCode); entry != null; entry = entry.getNextEntry())
      {
        NormalCDOLockOwner lockOwner = entry.get();
        if (lockOwner != null && lockOwner.sessionID == sessionID && lockOwner.viewID == viewID)
        {
          return lockOwner;
        }
      }

      NormalCDOLockOwner id = new NormalCDOLockOwner(sessionID, viewID);
      addEntry(createEntry(id, hashCode));
      return id;
    }

    @Override
    protected int hashCode(NormalCDOLockOwner lockOwner)
    {
      return getHashCode(lockOwner.sessionID, lockOwner.viewID);
    }
  }
}
