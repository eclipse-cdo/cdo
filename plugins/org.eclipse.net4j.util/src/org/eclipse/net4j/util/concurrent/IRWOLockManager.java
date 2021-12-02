/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * A {@link IRWLockManager read/write lock manager} that supports {@link IRWLockManager.LockType#OPTION write option}
 * locks.
 *
 * @author Caspar De Groot
 * @since 3.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRWOLockManager<OBJECT, CONTEXT> extends IRWLockManager<OBJECT, CONTEXT>
{
  /**
   * @since 3.16
   */
  public static final Collection<?> ALL_OBJECTS = null;

  /**
   * @since 3.16
   */
  public static final LockType ALL_LOCK_TYPES = null;

  /**
   * @since 3.16
   */
  public static final int ALL_LOCKS = -1;

  /**
   * @since 3.16
   */
  public static final long NO_TIMEOUT = -1;

  /**
   * @since 3.16
   */
  public long getModCount();

  /**
   * Adds locks of the given lockType, owned by the given context to the given objects.
   *
   * @param context The lock context to add from the <code>objects</code>. Must not be <code>null</code>.
   * @param objects The objects to lock. Must not be <code>null</code>.
   * @param lockType The type of lock to add to the <code>objects</code>. Must not be <code>null</code>.
   * @param count The number of locks to add to each of the <code>objects</code>.
   * @param timeout The period in milliseconds after that a {@link TimeoutRuntimeException} is thrown if some or all of the
   *        <code>objects</code> could not be locked,  or {@link #NO_TIMEOUT} to attempt forever to acquire the requested locks.
   * @param deltaHandler A handler that is notified with each delta in a {@link LockState lock state}, or <code>null</code> if no such notification is needed.
   *        The handler is notified at most once per delta, but it can happen that the handler is notified before the lock operation finally fails
   *        with one of the specified exceptions. The notification handling should be fast because notifications occur while the calling thread is synchronized on this lock manager.
   * @param stateHandler A handler that is notified with each new {@link LockState lock state}, or <code>null</code> if no such notification is needed..
   *        The handler is notified at most once per lock state, but it can happen that the handler is notified before the lock operation finally fails
   *        with one of the specified exceptions. The notification handling should be fast because notifications occur while the calling thread is synchronized on this lock manager.
   * @return The new {@link #getModCount() modification count}.
   * @throws InterruptedException If the calling thread is interrupted.
   * @throws TimeoutRuntimeException If the timeout period has expired and some or all of the <code>objects</code> could not be locked.
   * @since 3.16
   */
  public long lock(CONTEXT context, Collection<? extends OBJECT> objects, LockType lockType, int count, long timeout, //
      LockDeltaHandler<OBJECT, CONTEXT> deltaHandler, Consumer<LockState<OBJECT, CONTEXT>> stateHandler) //
      throws InterruptedException, TimeoutRuntimeException;

  /**
   * Removes locks of the given lockType, owned by the given context from the given objects.
   *
   * @param context The lock context to remove from the <code>objects</code>. Must not be <code>null</code>.
   * @param objects The objects to unlock, or {@link #ALL_OBJECTS} to unlock all objects of the <code>context</code>.
   * @param lockType The type of lock to remove from the <code>objects</code>, or {@link #ALL_LOCK_TYPES} to remove the locks of all types.
   * @param count The number of locks to remove from each of the <code>objects</code>, or {@link #ALL_LOCKS} to remove all locks.
   * @param deltaHandler A handler that is notified with each delta in a {@link LockState}, or <code>null</code> if no such notification is needed.
   * @param stateHandler A handler that is notified with each new {@link LockState}, or <code>null</code> if no such notification is needed.
   * @since 3.16
   */
  public long unlock(CONTEXT context, Collection<? extends OBJECT> objects, LockType lockType, int count, //
      LockDeltaHandler<OBJECT, CONTEXT> deltaHandler, Consumer<LockState<OBJECT, CONTEXT>> stateHandler);

  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> lock2(LockType lockType, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout)
      throws InterruptedException;

  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> unlock2(LockType lockType, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock);

  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> unlock2(CONTEXT context, Collection<? extends OBJECT> objectsToUnlock);

  @Deprecated
  public List<LockState<OBJECT, CONTEXT>> unlock2(CONTEXT context);

  @Override
  @Deprecated
  public void lock(LockType lockType, CONTEXT context, Collection<? extends OBJECT> objectsToLock, long timeout) throws InterruptedException;

  @Override
  @Deprecated
  public void lock(LockType lockType, CONTEXT context, OBJECT objectToLock, long timeout) throws InterruptedException;

  @Override
  @Deprecated
  public void unlock(LockType lockType, CONTEXT context, Collection<? extends OBJECT> objectsToUnlock);

  @Override
  @Deprecated
  public void unlock(CONTEXT context);

  /**
   * @author Eike Stepper
   * @since 3.16
   */
  @FunctionalInterface
  public interface LockDeltaHandler<OBJECT, CONTEXT>
  {
    public void handleLockDelta(CONTEXT context, OBJECT object, LockType lockType, int oldCount, int newCount);
  }
}
