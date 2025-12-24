/*
 * Copyright (c) 2011, 2012, 2018-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.util.LockTimeoutException;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;

/**
 * @author Simon McDuff
 * @since 4.0
 */
public class CDOLockImpl implements CDOLock
{
  public static final CDOLock NOOP = new NOOPLockImpl();

  private static final Set<CDOLockOwner> NO_OWNERS = Collections.emptySet();

  private final InternalCDOObject object;

  private final LockType type;

  public CDOLockImpl(InternalCDOObject object, LockType type)
  {
    this.object = object;
    this.type = type;
  }

  @Override
  public InternalCDOObject getObject()
  {
    return object;
  }

  @Override
  public LockType getType()
  {
    return type;
  }

  @Override
  public Set<CDOLockOwner> getOwners()
  {
    CDOLockState lockState = object.cdoLockState();

    switch (type)
    {
    case READ:
      return lockState.getReadLockOwners();

    case WRITE:
      CDOLockOwner writeLockOwner = lockState.getWriteLockOwner();
      return writeLockOwner == null ? NO_OWNERS : Collections.singleton(writeLockOwner);

    case OPTION:
      CDOLockOwner writeOptionOwner = lockState.getWriteOptionOwner();
      return writeOptionOwner == null ? NO_OWNERS : Collections.singleton(writeOptionOwner);

    default:
      throw new AssertionError();
    }
  }

  @Override
  public boolean isLocked()
  {
    return isLocked(false);
  }

  /**
   * @see org.eclipse.emf.cdo.CDOLock#isLockedByOthers()
   */
  @Override
  public boolean isLockedByOthers()
  {
    return isLocked(true);
  }

  @Override
  public CDOAcquiredLock acquire(long time, TimeUnit unit, boolean recursive) throws TimeoutException
  {
    lock(time, unit, recursive);
    return new CDOAcquiredLockImpl(this, recursive);
  }

  @Override
  public void lock(long time, TimeUnit unit, boolean recursive) throws TimeoutException
  {
    try
    {
      if (!tryLock(time, unit, recursive))
      {
        throw new TimeoutException();
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public void lock(long time, TimeUnit unit) throws TimeoutException
  {
    lock(time, unit, false);
  }

  @Override
  public void lock(long millis) throws TimeoutException
  {
    lock(millis, TimeUnit.MILLISECONDS);
  }

  @Override
  public void lock()
  {
    try
    {
      lock(NO_TIMEOUT);
    }
    catch (TimeoutException ex)
    {
      // Should not happen.
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public void lockInterruptibly() throws InterruptedException
  {
    lock();
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit, boolean recursive) throws InterruptedException
  {
    try
    {
      InternalCDOView view = object.cdoView();
      view.lockObjects(Collections.singletonList(object), type, unit.toMillis(time), recursive);
      return true;
    }
    catch (LockTimeoutException ex)
    {
      return false;
    }
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
  {
    return tryLock(time, unit, false);
  }

  @Override
  public boolean tryLock(long millis) throws InterruptedException
  {
    return tryLock(millis, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean tryLock()
  {
    try
    {
      return tryLock(NO_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex)
    {
      // Should not happen.
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public void unlock(boolean recursive)
  {
    InternalCDOView view = object.cdoView();
    view.unlockObjects(Collections.singletonList(object), type, recursive);
  }

  @Override
  public void unlock()
  {
    unlock(false);
  }

  @Override
  public Condition newCondition()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOLock[object={0}, type={1}]", object, type);
  }

  private boolean isLocked(boolean others)
  {
    InternalCDOView view = object.cdoView();
    return view.isObjectLocked(object, type, others);
  }

  /**
   * @author Eike Stepper
   */
  private static final class CDOAcquiredLockImpl implements CDOAcquiredLock
  {
    private final CDOLock lock;

    private final boolean recursive;

    public CDOAcquiredLockImpl(CDOLock lock, boolean recursive)
    {
      this.lock = lock;
      this.recursive = recursive;
    }

    @Override
    public CDOLock getLock()
    {
      return lock;
    }

    @Override
    public CDOObject getObject()
    {
      return lock.getObject();
    }

    @Override
    public LockType getType()
    {
      return lock.getType();
    }

    @Override
    public Set<CDOLockOwner> getOwners()
    {
      return lock.getOwners();
    }

    @Override
    public boolean isLocked()
    {
      return lock.isLocked();
    }

    @Override
    public boolean isLockedByOthers()
    {
      return lock.isLockedByOthers();
    }

    @Override
    public boolean isRecursive()
    {
      return recursive;
    }

    @Override
    public void close()
    {
      lock.unlock(recursive);
    }
  }

  /**
   * @author Simon McDuff
   */
  public static final class NOOPLockImpl implements CDOLock
  {
    private NOOPLockImpl()
    {
    }

    @Override
    public CDOObject getObject()
    {
      return null;
    }

    @Override
    public LockType getType()
    {
      return null;
    }

    @Override
    public boolean isLocked()
    {
      return false;
    }

    /**
     * @see org.eclipse.emf.cdo.CDOLock#isLockedByOthers()
     */
    @Override
    public boolean isLockedByOthers()
    {
      return false;
    }

    @Override
    public Set<CDOLockOwner> getOwners()
    {
      return NO_OWNERS;
    }

    @Override
    public CDOAcquiredLock acquire(long time, TimeUnit unit, boolean recursive) throws TimeoutException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void lock(long time, TimeUnit unit, boolean recursive) throws TimeoutException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void lock(long time, TimeUnit unit) throws TimeoutException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void lock(long millis) throws TimeoutException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void lock()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit, boolean recursive) throws InterruptedException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long millis) throws InterruptedException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void unlock(boolean recursive)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void unlock()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Condition newCondition()
    {
      throw new UnsupportedOperationException();
    }
  }
}
