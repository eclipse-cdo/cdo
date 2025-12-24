/*
 * Copyright (c) 2021, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.15
 * @noextend This class is not intended to be extended by clients.
 */
public abstract class AbstractCDOLockState implements InternalCDOLockState
{
  protected Object lockedObject;

  public AbstractCDOLockState(Object lockedObject)
  {
    assert lockedObject instanceof CDOID || lockedObject instanceof CDOIDAndBranch : "lockedObject is of wrong type";
    assert !CDOIDUtil.isNull(CDOLockUtil.getLockedObjectID(lockedObject)) : "lockedObject is null";
    this.lockedObject = lockedObject;
  }

  @Override
  public final Object getLockedObject()
  {
    return lockedObject;
  }

  @Override
  public final CDOID getID()
  {
    return CDOLockUtil.getLockedObjectID(lockedObject);
  }

  @Override
  public void remapID(CDOID newID)
  {
    if (lockedObject instanceof CDOID)
    {
      lockedObject = newID;
    }
    else
    {
      lockedObject = CDOIDUtil.createIDAndBranch(newID, ((CDOIDAndBranch)lockedObject).getBranch());
    }
  }

  @Override
  public final CDOBranch getBranch()
  {
    return CDOLockUtil.getLockedObjectBranch(lockedObject);
  }

  @Override
  public final int hashCode()
  {
    return Objects.hashCode(lockedObject);
  }

  @Override
  public final boolean equals(Object obj)
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
    if (!lockedObject.equals(other.getLockedObject()))
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
  public final String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockState[lockedObject=");
    builder.append(lockedObject);

    Set<CDOLockOwner> readLockOwners = getReadLockOwners();
    builder.append(", readLockOwners=");

    if (!ObjectUtil.isEmpty(readLockOwners))
    {
      boolean first = true;
      for (CDOLockOwner lockOwner : readLockOwners)
      {
        AbstractCDOLockState.appendLockOwner(builder, lockOwner, first);
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

  @Override
  public final CDOLockDelta addOwner(CDOLockOwner owner, LockType type)
  {
    switch (type)
    {
    case READ:
      return addReadOwner(owner);

    case WRITE:
      return addWriteOwner(owner);

    case OPTION:
      return addOptionOwner(owner);

    default:
      throw new IllegalArgumentException("Illegal type: " + type);
    }
  }

  @Override
  public final CDOLockDelta removeOwner(CDOLockOwner owner, LockType type)
  {
    switch (type)
    {
    case READ:
      return removeReadOwner(owner);

    case WRITE:
      return removeWriteOwner(owner);

    case OPTION:
      return removeOptionOwner(owner);

    default:
      throw new IllegalArgumentException("Illegal type: " + type);
    }
  }

  @Override
  public final CDOLockDelta[] clearOwner(CDOLockOwner owner)
  {
    List<CDOLockDelta> deltas = null;
    deltas = CDOLockUtil.appendLockDelta(deltas, removeReadOwner(owner));
    deltas = CDOLockUtil.appendLockDelta(deltas, removeWriteOwner(owner));
    deltas = CDOLockUtil.appendLockDelta(deltas, removeOptionOwner(owner));
    return CDOLockUtil.toArray(deltas);
  }

  protected abstract CDOLockDelta addReadOwner(CDOLockOwner owner);

  protected abstract CDOLockDelta addWriteOwner(CDOLockOwner owner);

  protected abstract CDOLockDelta addOptionOwner(CDOLockOwner owner);

  protected abstract CDOLockDelta removeReadOwner(CDOLockOwner owner);

  protected abstract CDOLockDelta removeWriteOwner(CDOLockOwner owner);

  protected abstract CDOLockDelta removeOptionOwner(CDOLockOwner owner);

  @Override
  @Deprecated
  public final void addReadLockOwner(CDOLockOwner owner)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final boolean removeReadLockOwner(CDOLockOwner owner)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final void setWriteLockOwner(CDOLockOwner owner)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final void setWriteOptionOwner(CDOLockOwner owner)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final boolean removeOwner(CDOLockOwner owner)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final void updateFrom(CDOLockState source)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final void updateFrom(Object object, CDOLockState source)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public final void dispose()
  {
    throw new UnsupportedOperationException();
  }

  public static void appendLockOwner(StringBuilder builder, CDOLockOwner lockOwner, boolean first)
  {
    if (!first)
    {
      builder.append("+");
    }

    builder.append('[');
    appendLockOwner(builder, lockOwner);
    builder.append(']');
  }

  public static void appendLockOwner(StringBuilder builder, CDOLockOwner lockOwner)
  {
    builder.append(lockOwner.getSessionID());
    builder.append(':');
    builder.append(lockOwner.getViewID());

    String durableLockingID = lockOwner.getDurableLockingID();
    if (durableLockingID != null)
    {
      builder.append(':');
      builder.append(durableLockingID);
    }
  }
}
