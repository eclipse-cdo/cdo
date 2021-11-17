/*
 * Copyright (c) 2011, 2012, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.util.LockTimeoutException;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.Collections;
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
  public void lock()
  {
    try
    {
      InternalCDOView view = object.cdoView();
      view.lockObjects(Collections.singletonList(object), type, WAIT);
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public void lock(long time, TimeUnit unit) throws TimeoutException
  {
    try
    {
      if (!tryLock(time, unit))
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
  public void lock(long millis) throws TimeoutException
  {
    lock(millis, TimeUnit.MILLISECONDS);
  }

  @Override
  public boolean tryLock(long millis) throws InterruptedException
  {
    return tryLock(millis, TimeUnit.MILLISECONDS);
  }

  @Override
  public void lockInterruptibly() throws InterruptedException
  {
    lock();
  }

  @Override
  public Condition newCondition()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean tryLock()
  {
    try
    {
      InternalCDOView view = object.cdoView();
      view.lockObjects(Collections.singletonList(object), type, NO_WAIT);
      return true;
    }
    catch (LockTimeoutException ex)
    {
      return false;
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
  {
    try
    {
      InternalCDOView view = object.cdoView();
      view.lockObjects(Collections.singletonList(object), type, unit.toMillis(time));
      return true;
    }
    catch (LockTimeoutException ex)
    {
      return false;
    }
  }

  @Override
  public void unlock()
  {
    InternalCDOView view = object.cdoView();
    view.unlockObjects(Collections.singletonList(object), type);
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
    public Condition newCondition()
    {
      return null;
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
    public boolean tryLock(long millis) throws InterruptedException
    {
      return false;
    }

    @Override
    public boolean tryLock()
    {
      return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
    {
      return false;
    }

    @Override
    public void unlock()
    {
      throw new UnsupportedOperationException();
    }
  }
}
