/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
public interface IRWLockManager<K, V>
{
  public static final int WAIT = 0;

  public static final int NO_WAIT = 1;

  public void lock(LockType type, V context, Collection<? extends K> objectsToLock, long timeout)
      throws InterruptedException;

  public void lock(LockType type, V context, K objectToLock, long timeout) throws InterruptedException;

  /**
   * Attempts to release for a given locktype, context and objects.
   * 
   * @throws IllegalMonitorStateException
   *           Unlocking objects without lock.
   */
  public void unlock(LockType type, V context, Collection<? extends K> objectsToUnlock);

  /**
   * Attempts to release all locks(read and write) for a given context.
   */
  public void unlock(V context);

  public boolean hasLock(LockType type, V context, K objectToLock);

  public boolean hasLockByOthers(LockType type, V context, K objectToLock);

  /**
   * @author Simon McDuff
   */
  public static enum LockType
  {
    WRITE, READ
  }
}
