/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockState;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public final class CDOLockStateImpl extends AbstractCDOLockState implements InternalCDOLockState
{
  private static final Set<CDOLockOwner> NO_LOCK_OWNERS = Collections.emptySet();

  private static final Class<CDOLockOwner[]> ARRAY_CLASS = CDOLockOwner[].class;

  private final Object lockedObject;

  private Object readLockOwners;

  private CDOLockOwner writeLockOwner;

  private CDOLockOwner writeOptionOwner;

  public CDOLockStateImpl(Object lockedObject)
  {
    assert lockedObject instanceof CDOID || lockedObject instanceof CDOIDAndBranch : "lockedObject is of wrong type";
    assert !CDOIDUtil.isNull(CDOLockUtil.getLockedObjectID(lockedObject)) : "lockedObject is null";
    this.lockedObject = lockedObject;
  }

  @Override
  public Object getLockedObject()
  {
    return lockedObject;
  }

  @Override
  public Set<CDOLockOwner> getReadLockOwners()
  {
    if (readLockOwners == null)
    {
      return NO_LOCK_OWNERS;
    }

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      Set<CDOLockOwner> result = new HashSet<>();
      for (CDOLockOwner owner : owners)
      {
        result.add(owner);
      }

      return Collections.unmodifiableSet(result);
    }

    return Collections.singleton((CDOLockOwner)readLockOwners);
  }

  @Override
  public CDOLockOwner getWriteLockOwner()
  {
    return writeLockOwner;
  }

  @Override
  public CDOLockOwner getWriteOptionOwner()
  {
    return writeOptionOwner;
  }

  @Override
  public boolean isLocked(LockType lockType, CDOLockOwner lockOwner, boolean others)
  {
    if (lockType == null)
    {
      return isReadLocked(lockOwner, others) || isWriteLocked(lockOwner, others) || isOptionLocked(lockOwner, others);
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
    if (readLockOwners == null)
    {
      return false;
    }

    int n;
    boolean contained;

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      n = owners.length;
      if (n == 0)
      {
        return false;
      }

      contained = CDOLockUtil.indexOf(owners, by) != -1;
    }
    else
    {
      n = 1;
      contained = readLockOwners == by;
    }

    if (others)
    {
      int ownCount = contained ? 1 : 0;
      return n > ownCount;
    }

    return contained;
  }

  private boolean isWriteLocked(CDOLockOwner by, boolean others)
  {
    if (writeLockOwner == null)
    {
      return false;
    }

    return writeLockOwner == by ^ others;
  }

  private boolean isOptionLocked(CDOLockOwner by, boolean others)
  {
    if (writeOptionOwner == null)
    {
      return false;
    }

    return writeOptionOwner == by ^ others;
  }

  @Override
  public void addReadLockOwner(CDOLockOwner lockOwner)
  {
    if (readLockOwners == null)
    {
      readLockOwners = lockOwner;
      return;
    }

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      if (CDOLockUtil.indexOf(owners, lockOwner) == -1)
      {
        int oldLength = owners.length;
        readLockOwners = new CDOLockOwner[oldLength + 1];
        System.arraycopy(owners, 0, readLockOwners, 0, oldLength);
        ((CDOLockOwner[])readLockOwners)[oldLength] = lockOwner;
      }

      return;
    }

    if (readLockOwners != lockOwner)
    {
      readLockOwners = new CDOLockOwner[] { (CDOLockOwner)readLockOwners, lockOwner };
    }
  }

  @Override
  public boolean removeReadLockOwner(CDOLockOwner lockOwner)
  {
    if (readLockOwners == null)
    {
      return false;
    }

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      int index = CDOLockUtil.indexOf(owners, lockOwner);
      if (index != -1)
      {
        int oldLength = owners.length;
        readLockOwners = new CDOLockOwner[oldLength - 1];

        if (index > 0)
        {
          System.arraycopy(owners, 0, readLockOwners, 0, index);
        }

        int rest = oldLength - index - 1;
        if (rest > 0)
        {
          System.arraycopy(owners, index + 1, readLockOwners, index, rest);
        }

        return true;
      }

      return false;
    }

    if (readLockOwners == lockOwner)
    {
      readLockOwners = null;
      return true;
    }

    return false;
  }

  @Override
  public void setWriteLockOwner(CDOLockOwner lockOwner)
  {
    writeLockOwner = lockOwner;
  }

  @Override
  public void setWriteOptionOwner(CDOLockOwner lockOwner)
  {
    writeOptionOwner = lockOwner;
  }

  @Override
  public boolean removeOwner(CDOLockOwner lockOwner)
  {
    boolean changed = removeReadLockOwner(lockOwner);

    if (writeLockOwner == lockOwner)
    {
      writeLockOwner = null;
      changed = true;
    }

    if (writeOptionOwner == lockOwner)
    {
      writeOptionOwner = null;
      changed = true;
    }

    return changed;
  }

  @Override
  public boolean remapOwner(CDOLockOwner oldLockOwner, CDOLockOwner newLockOwner)
  {
    boolean changed = false;

    if (readLockOwners != null)
    {
      if (readLockOwners == oldLockOwner)
      {
        readLockOwners = newLockOwner;
        changed = true;
      }

      if (readLockOwners.getClass() == ARRAY_CLASS)
      {
        CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
        int index = CDOLockUtil.indexOf(owners, oldLockOwner);
        if (index != -1)
        {
          ((CDOLockOwner[])readLockOwners)[index] = newLockOwner;
          changed = true;
        }
      }
    }

    if (writeLockOwner == oldLockOwner)
    {
      writeLockOwner = newLockOwner;
      changed = true;
    }

    if (writeOptionOwner == oldLockOwner)
    {
      writeOptionOwner = newLockOwner;
      changed = true;
    }

    return changed;
  }

  public CDOLockStateImpl copy()
  {
    return copy(lockedObject);
  }

  public CDOLockStateImpl copy(Object lockedObject)
  {
    CDOLockStateImpl newLockState = new CDOLockStateImpl(lockedObject);
    copyReadLockOwners(newLockState);
    newLockState.writeLockOwner = writeLockOwner;
    newLockState.writeOptionOwner = writeOptionOwner;
    return newLockState;
  }

  private void copyReadLockOwners(CDOLockStateImpl target)
  {
    if (readLockOwners != null && readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      target.readLockOwners = Arrays.copyOf(owners, owners.length);
      return;
    }

    target.readLockOwners = readLockOwners;
  }

  @Override
  @Deprecated
  public void updateFrom(Object object, CDOLockState source)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void updateFrom(CDOLockState source)
  {
    CDOLockStateImpl lockState = (CDOLockStateImpl)source;
    lockState.copyReadLockOwners(this);

    writeLockOwner = lockState.writeLockOwner;
    writeOptionOwner = lockState.writeOptionOwner;
  }

  @Deprecated
  @Override
  public void dispose()
  {
    throw new UnsupportedOperationException();
  }
}
