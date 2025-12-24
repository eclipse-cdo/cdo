/*
 * Copyright (c) 2009-2012, 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.Collection;

/**
 * Support Multiple reads/no write and upgrade lock from read to write. Many context could request
 * {@link LockType#WRITE write} lock at the same time. It will privileges first context that has already a
 * {@link LockType#READ read} lock. If no one has any read lock, it's "first come first serve".
 *
 * @author Eike Stepper
 * @since 3.0
 */
public interface IRWLockManager<OBJECT, CONTEXT>
{
  @Deprecated
  public static final int WAIT = 0;

  @Deprecated
  public static final int NO_WAIT = 1;

  public void lock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout) throws InterruptedException;

  public void lock(LockType type, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException;

  /**
   * Attempts to release for a given lock type, context and objects.
   *
   * @throws IllegalMonitorStateException
   *           Unlocking objects without lock.
   */
  public void unlock(LockType type, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock);

  /**
   * Attempts to release all locks(read and write) for a given context.
   */
  public void unlock(CONTEXT context);

  public boolean hasLock(LockType type, CONTEXT context, OBJECT objectToLock);

  public boolean hasLockByOthers(LockType type, CONTEXT context, OBJECT objectToLock);

  /**
   * @author Simon McDuff
   */
  public static enum LockType
  {
    WRITE, READ,

    /**
     * @since 3.2
     */
    OPTION;
  }
}
