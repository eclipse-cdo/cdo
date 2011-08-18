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
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public class CDOLockStateImpl implements InternalCDOLockState
{
  private final Object lockedObject;

  private final Set<CDOLockOwner> readLockOwners = new HashSet<CDOLockOwner>();

  private CDOLockOwner writeLockOwner;

  private CDOLockOwner writeOptionOwner;

  public CDOLockStateImpl(Object lockedObject)
  {
    CheckUtil.checkArg(lockedObject, "lockedObject");
    this.lockedObject = lockedObject;
  }

  public boolean isLocked(LockType lockType, CDOLockOwner lockOwner, boolean others)
  {
    switch (lockType)
    {
    case READ:
      return isReadLocked(lockOwner, others);

    case WRITE:
      return isWriteLocked(lockOwner, others);

    case OPTION:
      return isOptionLocked(lockOwner, others);
    }

    return false;
  }

  private boolean isReadLocked(CDOLockOwner by, boolean others)
  {
    if (readLockOwners.size() == 0)
    {
      return false;
    }

    return readLockOwners.contains(by) ^ others;
  }

  private boolean isWriteLocked(CDOLockOwner by, boolean others)
  {
    if (writeLockOwner == null)
    {
      return false;
    }

    return writeLockOwner.equals(by) ^ others;
  }

  private boolean isOptionLocked(CDOLockOwner by, boolean others)
  {
    if (writeOptionOwner == null)
    {
      return false;
    }

    return writeOptionOwner.equals(by) ^ others;
  }

  public Set<CDOLockOwner> getReadLockOwners()
  {
    return Collections.unmodifiableSet(readLockOwners);
  }

  public CDOLockOwner getWriteLockOwner()
  {
    return writeLockOwner;
  }

  public void setWriteLockOwner(CDOLockOwner lockOwner)
  {
    writeLockOwner = lockOwner;
  }

  public CDOLockOwner getWriteOptionOwner()
  {
    return writeOptionOwner;
  }

  public void setWriteOptionOwner(CDOLockOwner lockOwner)
  {
    writeOptionOwner = lockOwner;
  }

  public Object getLockedObject()
  {
    return lockedObject;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockState[lockedObject=");
    builder.append(lockedObject);

    if (readLockOwners.size() > 0)
    {
      builder.append(", read=");
      boolean first = true;
      for (CDOLockOwner lockOwner : readLockOwners)
      {
        if (first)
        {
          first = false;
        }
        else
        {
          builder.append(", ");
        }

        builder.append(lockOwner);
      }

      builder.deleteCharAt(builder.length() - 1);
    }

    if (writeLockOwner != null)
    {
      builder.append(", write=");
      builder.append(writeLockOwner);
    }

    if (writeOptionOwner != null)
    {
      builder.append(", option=");
      builder.append(writeOptionOwner);
    }

    builder.append(']');
    return builder.toString();
  }

  public void addReadLockOwner(CDOLockOwner lockOwner)
  {
    readLockOwners.add(lockOwner);
  }

  public boolean removeReadLockOwner(CDOLockOwner lockOwner)
  {
    return readLockOwners.remove(lockOwner);
  }
}
