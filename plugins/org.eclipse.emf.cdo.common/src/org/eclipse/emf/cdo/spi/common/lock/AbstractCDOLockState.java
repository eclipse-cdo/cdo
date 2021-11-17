/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;

import org.eclipse.net4j.util.ObjectUtil;

import java.util.Objects;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.15
 * @noextend This class is not intended to be extended by clients.
 */
public abstract class AbstractCDOLockState implements CDOLockState
{
  public AbstractCDOLockState()
  {
  }

  @Override
  public CDOBranch getBranch()
  {
    return CDOLockUtil.getLockedObjectBranch(getLockedObject());
  }

  @Override
  public CDOID getID()
  {
    return CDOLockUtil.getLockedObjectID(getLockedObject());
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(getLockedObject());
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (!(obj instanceof CDOLockState))
    {
      return false;
    }

    CDOLockState other = (CDOLockState)obj;
    if (!getLockedObject().equals(other.getLockedObject()))
    {
      return false;
    }

    if (getWriteLockOwner() != other.getWriteLockOwner())
    {
      return false;
    }

    if (getWriteOptionOwner() != other.getWriteOptionOwner())
    {
      return false;
    }

    if (!getReadLockOwners().equals(other.getReadLockOwners()))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockState[lockedObject=");
    builder.append(getLockedObject());

    Set<CDOLockOwner> readLockOwners = getReadLockOwners();
    builder.append(", readLockOwners=");

    if (!ObjectUtil.isEmpty(readLockOwners))
    {
      boolean first = true;
      for (CDOLockOwner lockOwner : readLockOwners)
      {
        appendLockOwner(builder, lockOwner, first);
        first = false;
      }
    }
    else
    {
      builder.append("NONE");
    }

    CDOLockOwner writeLockOwner = getWriteLockOwner();
    builder.append(", writeLockOwner=");
    builder.append(writeLockOwner != null ? writeLockOwner : "NONE");

    CDOLockOwner writeOptionOwner = getWriteOptionOwner();
    builder.append(", writeOptionOwner=");
    builder.append(writeOptionOwner != null ? writeOptionOwner : "NONE");

    builder.append("]");
    return builder.toString();
  }

  private static void appendLockOwner(StringBuilder builder, CDOLockOwner lockOwner, boolean first)
  {
    if (!first)
    {
      builder.append("+");
    }

    String durableLockingID = lockOwner.getDurableLockingID();
    builder.append('[');

    if (durableLockingID != null)
    {
      builder.append(durableLockingID);
    }
    else
    {
      builder.append(lockOwner.getSessionID());
      builder.append(':');
      builder.append(lockOwner.getViewID());
    }

    builder.append(']');
  }
}
