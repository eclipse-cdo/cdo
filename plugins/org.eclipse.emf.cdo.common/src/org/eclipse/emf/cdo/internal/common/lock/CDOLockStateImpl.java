/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
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
  private Object lockedObject;

  private Set<CDOLockOwner> readLockOwners = new HashSet<CDOLockOwner>();

  private CDOLockOwner writeLockOwner;

  private CDOLockOwner writeOptionOwner;

  public CDOLockStateImpl(Object lockedObject)
  {
    CheckUtil.checkArg(lockedObject, "lockedObject");
    CheckUtil.checkState(lockedObject instanceof CDOID || lockedObject instanceof CDOIDAndBranch,
        "lockedObject is of wrong type");
    this.lockedObject = lockedObject;
  }

  public CDOLockStateImpl copy()
  {
    checkDisposed();
    CDOLockStateImpl newLockState = new CDOLockStateImpl(lockedObject);
    for (CDOLockOwner owner : readLockOwners)
    {
      newLockState.readLockOwners.add(owner);
    }

    newLockState.writeLockOwner = writeLockOwner;
    newLockState.writeOptionOwner = writeOptionOwner;
    return newLockState;
  }

  public void updateFrom(Object object, CDOLockState source)
  {
    lockedObject = object;
    readLockOwners = source.getReadLockOwners();
    writeLockOwner = source.getWriteLockOwner();
    writeOptionOwner = source.getWriteOptionOwner();
  }

  public boolean isLocked(LockType lockType, CDOLockOwner lockOwner, boolean others)
  {
    if (lockedObject == null)
    {
      return false;
    }

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
    int n = readLockOwners.size();
    if (n == 0)
    {
      return false;
    }

    if (!others)
    {
      return readLockOwners.contains(by);
    }

    return true;
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
    if (lockedObject == null)
    {
      return Collections.emptySet();
    }

    return Collections.unmodifiableSet(readLockOwners);
  }

  public void addReadLockOwner(CDOLockOwner lockOwner)
  {
    checkDisposed();
    readLockOwners.add(lockOwner);
  }

  public boolean removeReadLockOwner(CDOLockOwner lockOwner)
  {
    checkDisposed();
    return readLockOwners.remove(lockOwner);
  }

  public CDOLockOwner getWriteLockOwner()
  {
    return writeLockOwner;
  }

  public void setWriteLockOwner(CDOLockOwner lockOwner)
  {
    checkDisposed();
    writeLockOwner = lockOwner;
  }

  public CDOLockOwner getWriteOptionOwner()
  {
    return writeOptionOwner;
  }

  public void setWriteOptionOwner(CDOLockOwner lockOwner)
  {
    checkDisposed();
    writeOptionOwner = lockOwner;
  }

  public Object getLockedObject()
  {
    return lockedObject;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (lockedObject == null ? 0 : lockedObject.hashCode());
    return result;
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

    if (!(obj instanceof CDOLockStateImpl))
    {
      return false;
    }

    CDOLockStateImpl other = (CDOLockStateImpl)obj;
    if (lockedObject == null)
    {
      if (other.lockedObject != null)
      {
        return false;
      }
    }
    else if (!lockedObject.equals(other.lockedObject))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockState\n  lockedObject=");
    builder.append(lockedObject);

    builder.append("\n  readLockOwners=");
    if (readLockOwners.size() > 0)
    {
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
    else
    {
      builder.append("NONE");
    }

    builder.append("\n  writeLockOwner=");
    builder.append(writeLockOwner != null ? writeLockOwner : "NONE");

    builder.append("\n  writeOptionOwner=");
    builder.append(writeOptionOwner != null ? writeOptionOwner : "NONE");

    return builder.toString();
  }

  public void dispose()
  {
    lockedObject = null;
    readLockOwners = null;
    writeLockOwner = null;
    writeOptionOwner = null;
  }

  private void checkDisposed()
  {
    if (lockedObject == null)
    {
      throw new IllegalStateException("Lock state is disposed");
    }
  }
}
