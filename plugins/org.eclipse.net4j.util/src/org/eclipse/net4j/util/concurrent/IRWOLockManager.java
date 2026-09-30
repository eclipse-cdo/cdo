/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange.DeltaHandler;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * @param <OBJECT> the locked object type
 * @param <CONTEXT> the lock context type
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
   *        The handler is notified at most once per delta, but it can happen that the handler is notified before the lock LockChangeOperation finally fails
   *        with one of the specified exceptions. The notification handling should be fast because notifications occur while the calling thread is synchronized on this lock manager.
   * @param stateHandler A handler that is notified with each new {@link LockState lock state}, or <code>null</code> if no such notification is needed..
   *        The handler is notified at most once per lock state, but it can happen that the handler is notified before the lock LockChangeOperation finally fails
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

  /**
   * Applies a set of lock and unlock mutations as one lock-manager operation.
   * <p>
   * Implementations must apply multi-change sets without exposing a partially applied state while waiting for locks.
   * A successful non-empty call increments the modification count once. An empty set leaves the manager unchanged.
   *
   * @param context the non-null owner of the requested lock changes
   * @param changes the ordered, non-null changes to apply
   * @param deltaHandler notified for each effective lock-count change, or {@code null}
   * @param stateHandler notified with the final state of each affected lock state, or {@code null}
   * @return the resulting modification count
   * @throws InterruptedException if waiting for a requested lock is interrupted
   * @throws TimeoutRuntimeException if a requested lock cannot be acquired before its timeout
   * @since 3.31
   */
  public long changeLocks(CONTEXT context, List<? extends LockChange<OBJECT>> changes, //
      DeltaHandler<OBJECT, CONTEXT> deltaHandler, Consumer<LockState<OBJECT, CONTEXT>> stateHandler) //
      throws InterruptedException, TimeoutRuntimeException;

  /**
   * An immutable request to lock or unlock objects for one context. A {@code null} object collection means all
   * objects owned by the context and is valid only for an {@link Operation#UNLOCK} change. For unlock changes, a
   * {@code null} lock type means all lock types and {@link #ALL_LOCKS} means all lock counts.
   *
   * @param <OBJECT> the locked object type
   * @author Eike Stepper
   * @since 3.31
   */
  public static final class LockChange<OBJECT>
  {
    private final Operation operation;

    private final Collection<? extends OBJECT> objects;

    private final LockType lockType;

    private final int count;

    private final long timeout;

    /**
     * Creates one immutable lock mutation request.
     *
     * @param operation whether to add or remove locks
     * @param objects the objects, or {@code null} for all objects owned by the context on an unlock
     * @param lockType the lock type, or {@code null} for all lock types on an unlock
     * @param count the number of lock counts, or {@link IRWOLockManager#ALL_LOCKS} to remove all counts
     * @param timeout the lock acquisition timeout in milliseconds, or {@link IRWOLockManager#NO_TIMEOUT}; ignored for unlocks
     */
    public LockChange(Operation operation, Collection<? extends OBJECT> objects, LockType lockType, int count, long timeout)
    {
      if (operation == null)
      {
        throw new IllegalArgumentException("operation == null");
      }

      if (operation == Operation.LOCK && (objects == null || lockType == null || count < 0))
      {
        throw new IllegalArgumentException("Invalid LOCK change");
      }

      if (operation == Operation.UNLOCK && count < ALL_LOCKS)
      {
        throw new IllegalArgumentException("count < ALL_LOCKS");
      }

      this.operation = operation;
      this.objects = objects == null ? null : Collections.unmodifiableList(new ArrayList<>(objects));
      this.lockType = lockType;
      this.count = count;
      this.timeout = timeout;
    }

    /**
     * Returns whether this request acquires locks.
     *
     * @return {@code true} for a {@link Operation#LOCK} request
     */
    public boolean isLock()
    {
      return operation == Operation.LOCK;
    }

    /**
     * Returns whether this request releases locks.
     *
     * @return {@code true} for a {@link Operation#UNLOCK} request
     */
    public boolean isUnlock()
    {
      return operation == Operation.UNLOCK;
    }

    /**
     * Returns the requested operation.
     *
     * @return the operation
     */
    public Operation getOperation()
    {
      return operation;
    }

    /**
     * Returns the immutable object collection, or {@code null} for all objects owned by the context.
     *
     * @return the objects, or {@code null}
     */
    public Collection<? extends OBJECT> getObjects()
    {
      return objects;
    }

    /**
     * Returns the lock type, or {@code null} to unlock all types.
     *
     * @return the lock type, or {@code null}
     */
    public LockType getLockType()
    {
      return lockType;
    }

    /**
     * Returns the requested lock count.
     *
     * @return the count, or {@link IRWOLockManager#ALL_LOCKS} for an unlock-all request
     */
    public int getCount()
    {
      return count;
    }

    /**
     * Returns the requested acquisition timeout.
     *
     * @return the timeout in milliseconds, or {@link IRWOLockManager#NO_TIMEOUT}
     */
    public long getTimeout()
    {
      return timeout;
    }

    /**
     * Creates a request to acquire a lock on each object in the given collection.
     *
     * @param <OBJECT> the locked object type
     * @param objects the objects to lock; must not be {@code null}
     * @param lockType the lock type to acquire
     * @param count the number of reentrant locks to acquire
     * @param timeout the maximum wait in milliseconds, or {@link IRWOLockManager#NO_TIMEOUT}
     * @return the immutable lock request
     */
    public static <OBJECT> LockChange<OBJECT> lock(Collection<? extends OBJECT> objects, LockType lockType, int count, long timeout)
    {
      return new LockChange<>(Operation.LOCK, objects, lockType, count, timeout);
    }

    /**
     * Creates a request to release locks from the given objects.
     *
     * @param <OBJECT> the locked object type
     * @param objects the objects to unlock, or {@code null} for all objects owned by the context
     * @param lockType the lock type to release, or {@code null} for all types
     * @param count the number of locks to release, or {@link IRWOLockManager#ALL_LOCKS}
     * @return the immutable unlock request
     */
    public static <OBJECT> LockChange<OBJECT> unlock(Collection<? extends OBJECT> objects, LockType lockType, int count)
    {
      return new LockChange<>(Operation.UNLOCK, objects, lockType, count, NO_TIMEOUT);
    }

    /**
     * The kind of mutation represented by a {@link LockChange}.
     *
     * @author Eike Stepper
     * @since 3.31
     */
    public enum Operation
    {
      /**
       * Adds locks to the requested objects.
       */
      LOCK,

      /**
       * Removes locks from the requested objects.
       */
      UNLOCK
    }

    /**
     * Receives one effective delta from a {@link #changeLocks} call.
     *
     * @param <OBJECT> the locked object type
     * @param <CONTEXT> the lock owner type
     * @author Eike Stepper
     * @since 3.31
     */
    @FunctionalInterface
    public interface DeltaHandler<OBJECT, CONTEXT>
    {
      /**
       * Handles one effective lock-count change in a lock mutation batch.
       *
       * @param operation whether the change adds or removes locks
       * @param context the owner whose lock count changed
       * @param object the affected object
       * @param lockType the affected lock type
       * @param oldCount the lock count before the change
       * @param newCount the lock count after the change
       */
      public void handleLockDelta(Operation operation, CONTEXT context, OBJECT object, LockType lockType, int oldCount, int newCount);
    }
  }

  /**
   * Receives notifications about changes to locks.
   *
   * @author Eike Stepper
   * @since 3.16
   * @param <OBJECT> the locked object type
   * @param <CONTEXT> the lock context type
   */
  @FunctionalInterface
  public interface LockDeltaHandler<OBJECT, CONTEXT>
  {
    public void handleLockDelta(CONTEXT context, OBJECT object, LockType lockType, int oldCount, int newCount);
  }

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
}
