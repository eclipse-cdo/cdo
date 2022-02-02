/*
 * Copyright (c) 2008-2012, 2014, 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.IRWOLockManager;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

/**
 * A read or write lock on an {@link CDOObject object} as returned by {@link CDOObject#cdoReadLock()} or
 * {@link CDOObject#cdoWriteLock()}.
 *
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOLock extends Lock, AutoCloseable
{
  /**
   * @since 4.15
   */
  public static final long NO_TIMEOUT = IRWOLockManager.NO_TIMEOUT;

  /**
   * @since 4.8
   */
  public CDOObject getObject();

  /**
   * @since 3.0
   */
  public LockType getType();

  /**
   * @since 4.15
   */
  public Set<CDOLockOwner> getOwners();

  /**
   * Returns <code>true</code> if this lock is currently held by the requesting {@link CDOView view}, <code>false</code>
   * otherwise.
   */
  public boolean isLocked();

  /**
   * Returns <code>true</code> if this lock is currently held by another {@link CDOView view} (i.e. any view different
   * from the requesting one), <code>false</code> otherwise.
   */
  public boolean isLockedByOthers();

  /**
   * @since 4.0
   */
  public void lock(long time, TimeUnit unit) throws TimeoutException;

  /**
   * @since 4.16
   */
  public void lock(long time, TimeUnit unit, boolean recursive) throws TimeoutException;

  /**
   * @since 4.0
   */
  public void lock(long millis) throws TimeoutException;

  /**
   * @since 4.0
   */
  public boolean tryLock(long millis) throws InterruptedException;

  /**
   * @since 4.16
   */
  public boolean tryLock(long time, TimeUnit unit, boolean recursive) throws InterruptedException;

  /**
   * @since 4.16
   */
  public CDOAcquiredLock acquire(long time, TimeUnit unit, boolean recursive) throws TimeoutException;

  /**
   * @since 4.16
   */
  public void unlock(boolean recursive);

  /**
   * @since 4.16
   */
  @Override
  default void close()
  {
    unlock();
  }

  @Deprecated
  public static final int WAIT = IRWLockManager.WAIT;

  @Deprecated
  public static final int NO_WAIT = IRWLockManager.NO_WAIT;

  /**
   * @author Eike Stepper
   * @since 4.16
   */
  public interface CDOAcquiredLock extends AutoCloseable
  {
    public CDOLock getLock();

    public CDOObject getObject();

    public LockType getType();

    public Set<CDOLockOwner> getOwners();

    public boolean isLocked();

    public boolean isLockedByOthers();

    public boolean isRecursive();

    @Override
    public void close();
  }
}
