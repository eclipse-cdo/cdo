/*
 * Copyright (c) 2011-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.spi.common.lock.AbstractCDOLockState;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public final class CDOLockStateImpl extends AbstractCDOLockState
{
  private static final Set<CDOLockOwner> NO_LOCK_OWNERS = Collections.emptySet();

  private static final Class<CDOLockOwner[]> ARRAY_CLASS = CDOLockOwner[].class;

  private Object readLockOwners;

  private CDOLockOwner writeLockOwner;

  private CDOLockOwner writeOptionOwner;

  public CDOLockStateImpl(Object lockedObject)
  {
    super(lockedObject);
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
  public boolean isLocked(LockType type, CDOLockOwner by, boolean others)
  {
    if (type == null)
    {
      return isReadLocked(by, others) || isWriteLocked(by, others) || isOptionLocked(by, others);
    }

    switch (type)
    {
    case READ:
      return isReadLocked(by, others);

    case WRITE:
      return isWriteLocked(by, others);

    case OPTION:
      return isOptionLocked(by, others);
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
  protected CDOLockDelta addReadOwner(CDOLockOwner owner)
  {
    if (readLockOwners == null)
    {
      readLockOwners = owner;
      return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, null, owner);
    }

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      if (CDOLockUtil.indexOf(owners, owner) == -1)
      {
        int oldLength = owners.length;
        readLockOwners = new CDOLockOwner[oldLength + 1];
        System.arraycopy(owners, 0, readLockOwners, 0, oldLength);
        ((CDOLockOwner[])readLockOwners)[oldLength] = owner;

        return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, null, owner);
      }

      return null;
    }

    if (readLockOwners != owner)
    {
      readLockOwners = new CDOLockOwner[] { (CDOLockOwner)readLockOwners, owner };
      return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, null, owner);
    }

    return null;
  }

  @Override
  protected CDOLockDelta addWriteOwner(CDOLockOwner owner)
  {
    if (writeLockOwner == null)
    {
      writeLockOwner = owner;
      return CDOLockUtil.createLockDelta(lockedObject, LockType.WRITE, null, owner);
    }

    return null;
  }

  @Override
  protected CDOLockDelta addOptionOwner(CDOLockOwner owner)
  {
    if (writeOptionOwner == null)
    {
      writeOptionOwner = owner;
      return CDOLockUtil.createLockDelta(lockedObject, LockType.OPTION, null, owner);
    }

    return null;
  }

  @Override
  protected CDOLockDelta removeReadOwner(CDOLockOwner owner)
  {
    if (readLockOwners == null)
    {
      return null;
    }

    if (readLockOwners.getClass() == ARRAY_CLASS)
    {
      CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
      int index = CDOLockUtil.indexOf(owners, owner);
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

        return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, owner, null);
      }

      return null;
    }

    if (readLockOwners == owner)
    {
      readLockOwners = null;
      return CDOLockUtil.createLockDelta(lockedObject, LockType.READ, owner, null);
    }

    return null;
  }

  @Override
  protected CDOLockDelta removeWriteOwner(CDOLockOwner owner)
  {
    if (writeLockOwner == owner)
    {
      writeLockOwner = null;
      return CDOLockUtil.createLockDelta(lockedObject, LockType.WRITE, owner, null);
    }

    return null;
  }

  @Override
  protected CDOLockDelta removeOptionOwner(CDOLockOwner owner)
  {
    if (writeOptionOwner == owner)
    {
      writeOptionOwner = null;
      return CDOLockUtil.createLockDelta(lockedObject, LockType.OPTION, owner, null);
    }

    return null;
  }

  @Override
  public CDOLockDelta[] remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    List<CDOLockDelta> deltas = null;

    if (readLockOwners != null)
    {
      if (readLockOwners == oldOwner)
      {
        readLockOwners = newOwner;
        deltas = CDOLockUtil.appendLockDelta(deltas, lockedObject, LockType.READ, oldOwner, newOwner);
      }
      else if (readLockOwners.getClass() == ARRAY_CLASS)
      {
        CDOLockOwner[] owners = (CDOLockOwner[])readLockOwners;
        int index = CDOLockUtil.indexOf(owners, oldOwner);
        if (index != -1)
        {
          ((CDOLockOwner[])readLockOwners)[index] = newOwner;
          deltas = CDOLockUtil.appendLockDelta(deltas, lockedObject, LockType.READ, oldOwner, newOwner);
        }
      }
    }

    if (writeLockOwner == oldOwner)
    {
      writeLockOwner = newOwner;
      deltas = CDOLockUtil.appendLockDelta(deltas, lockedObject, LockType.WRITE, oldOwner, newOwner);
    }

    if (writeOptionOwner == oldOwner)
    {
      writeOptionOwner = newOwner;
      deltas = CDOLockUtil.appendLockDelta(deltas, lockedObject, LockType.OPTION, oldOwner, newOwner);
    }

    return CDOLockUtil.toArray(deltas);
  }
}
