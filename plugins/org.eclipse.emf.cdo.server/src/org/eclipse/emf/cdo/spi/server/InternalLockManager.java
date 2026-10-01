/*
 * Copyright (c) 2009-2012, 2014-2016, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalSession.LockChangeResult;

import org.eclipse.net4j.util.concurrent.IRWOLockManager;
import org.eclipse.net4j.util.concurrent.IRWOLockManager.LockChange.DeltaHandler;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The type of the to-be-locked objects is either {@link CDOIDAndBranch} or {@link CDOID}, depending on whether
 * branching is supported by the repository or not.
 * <p>
 * The following features are supported in addition to {@link IRWOLockManager}:
 * <ul>
 * <li> Recursive locking
 * <li> Distinction between explicit and implicit locking
 * <li> Durable locking
 * </ul>
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalLockManager extends IRWOLockManager<Object, IView>, ILockingManager
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  /**
   * Same as calling {@link #getLockKey(CDOID, CDOBranch)} with <code>null</code> as branch.
   *
   * @since 4.25
   */
  public Object getLockKey(CDOID id);

  /**
   * Returns the lock key for the given ID. If the repository supports branching, the returned key is of type {@link CDOIDAndBranch},
   * otherwise it is of type {@link CDOID}.
   *
   * @param id     the ID of the object to be locked.
   * @param branch the branch on which the object to be locked resides. May be <code>null</code> to indicate the main branch (for example if the repository does not support branching).
   * @since 4.0
   */
  public Object getLockKey(CDOID id, CDOBranch branch);

  /**
   * @since 4.0
   * @see #getLockKey(CDOID)
   * @see #getLockKey(CDOID, CDOBranch)
   */
  public CDOID getLockKeyID(Object key);

  /**
   * @since 4.15
   * @see #getLockKey(CDOID)
   * @see #getLockKey(CDOID, CDOBranch)
   */
  public CDOBranch getLockKeyBranch(Object key);

  /**
   * @since 4.0
   */
  public Map<CDOID, LockGrade> getLocks(IView view);

  /**
   * @since 4.15
   */
  public long lock(IView view, Collection<? extends Object> objects, LockType lockType, int count, long timeout, //
      boolean recursive, boolean explicit, //
      LockDeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler) //
      throws InterruptedException, TimeoutRuntimeException;

  /**
   * @since 4.15
   */
  public long unlock(IView view, Collection<? extends Object> objects, LockType lockType, int count, //
      boolean recursive, boolean explicit, //
      LockDeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler);

  /**
   * Applies one ordered set of lock changes under one in-memory write-access scope, then persists explicit durable
   * lock changes after releasing that access.
   *
   * @param view the lock owner
   * @param changes the ordered lock and unlock requests
   * @param recursive whether object collections are expanded to their contained objects
   * @param explicit whether changes must also be persisted for a durable view
   * @param deltaHandler receives the effective lock-count changes
   * @param stateHandler receives the final state of affected objects
   * @return the generic lock-manager modification count
   * @throws InterruptedException if a requested lock wait is interrupted
   * @throws TimeoutRuntimeException if a requested lock cannot be acquired before its timeout
   * @since 4.27
   */
  public long changeLocks(IView view, List<? extends LockChange<Object>> changes, boolean recursive, boolean explicit, //
      DeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler) throws InterruptedException, TimeoutRuntimeException;

  /**
   * Applies a lock change and returns its exact sequencing reservation together with the generic
   * lock-manager modification count.
   *
   * @param view the lock owner
   * @param changes the ordered lock and unlock requests
   * @param recursive whether object collections are expanded to their contained objects
   * @param explicit whether changes must also be persisted for a durable view
   * @param deltaHandler receives the effective lock-count changes
   * @param stateHandler receives the final state of affected objects
   * @return the result for this exact operation
   * @throws InterruptedException if a requested lock wait is interrupted
   * @throws TimeoutRuntimeException if a requested lock cannot be acquired before its timeout
   * @since 4.27
   */
  public LockChangeOperationResult changeLocksWithReservation(IView view, List<? extends LockChange<Object>> changes, boolean recursive, boolean explicit, //
      DeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler) throws InterruptedException, TimeoutRuntimeException;

  /**
   * Reserves a lock-change sequencing position for each open session in the given set.
   *
   * @param sessions the sessions that may receive the change
   * @param forceFull whether each resulting notification must contain the full change
   * @return an aggregate reservation whose per-session results become available after completion
   * @since 4.27
   */
  public LockChangeReservationSet reserveLockChange(InternalSession[] sessions, boolean forceFull);

  /**
   * Changes the durable locking ID of a view and returns the exact lock-change count assigned to
   * the resulting owner remap for that view's session.
   *
   * @param view the view whose durable locking ID is changed
   * @param durableLockingID the new durable locking ID, or {@code null} to disable durable locking
   * @return the exact session lock-change count for the owner remap, or zero if no owner remap occurred
   * @since 4.27
   */
  public long setDurableLockingID(InternalView view, String durableLockingID);

  /**
   * Returns a consistent snapshot of the lock states relevant to a session, paired with that
   * session's exact lock-change sequence baseline.
   *
   * @param session the session for which relevant lock states are selected
   * @return the relevant lock states and their sequence baseline
   * @since 4.27
   */
  public LockStateSnapshot snapshotLockStates(InternalSession session);

  /**
   * @since 4.0
   */
  public LockArea createLockArea(InternalView view);

  /**
   * @since 4.1
   */
  public LockArea createLockArea(InternalView view, String lockAreaID);

  /**
   * @since 4.1
   */
  public void updateLockArea(LockArea lockArea);

  /**
   * @since 4.15
   */
  public void openView(ISession session, int viewID, boolean readOnly, String durableLockingID, Consumer<IView> viewConsumer,
      BiConsumer<CDOID, LockGrade> lockConsumer);

  /**
   * @since 4.1
   * @see #getLockKey(CDOID)
   * @see #getLockKey(CDOID, CDOBranch)
   */
  public LockGrade getLockGrade(Object key);

  /**
   * @since 4.1
   * @see #getLockKey(CDOID)
   * @see #getLockKey(CDOID, CDOBranch)
   */
  public LockState<Object, IView> getLockState(Object key);

  /**
   * @since 4.15
   * @see #getLockKey(CDOID)
   * @see #getLockKey(CDOID, CDOBranch)
   */
  public void getLockStates(Collection<Object> keys, BiConsumer<Object, LockState<Object, IView>> consumer);

  /**
   * @since 4.15
   */
  public void getLockStates(Consumer<LockState<Object, IView>> consumer);

  /**
   * Returns the set of views that own a lock of one of the specified types on the given key.
   * If no lock types are specified, all lock types are considered.
   * If no view owns a lock of the specified types, an empty set is returned.
   *
   * @since 4.25
   * @see #getLockKey(CDOID)
   * @see #getLockKey(CDOID, CDOBranch)
   */
  public Set<IView> getLockOwners(Object key, LockType... lockTypes);

  /**
   * @since 4.25
   */
  public IView getLockOwner(String durableLockingID);

  /**
   * @since 4.1
   */
  public void reloadLocks();

  /**
   * An aggregate of per-session reservations for one lock change.
   *
   * @author Eike Stepper
   * @since 4.27
   */
  public interface LockChangeReservationSet
  {
    /**
     * Completes this reservation with the resulting change, canceling the entry for the excluded
     * session when the notification is not delivered to that session.
     *
     * @param info the completed lock change
     * @param excludedSession the session whose entry must be cancelled, or {@code null}
     * @since 4.27
     */
    public void complete(CDOLockChangeInfo info, InternalSession excludedSession);

    /**
     * Cancels all entries in this reservation.
     *
     * @since 4.27
     */
    public void cancel();

    /**
     * Waits for and returns this lock change's result for the given session.
     *
     * @param session the recipient session
     * @return the exact sequence count and projected change, or {@code null} if no reservation was made
     * @since 4.27
     */
    public LockChangeResult getResult(InternalSession session);
  }

  /**
   * The generic lock-manager modification count and optional reservation produced by one operation.
   *
   * @author Eike Stepper
   * @since 4.27
   */
  public interface LockChangeOperationResult
  {
    /**
     * Returns the generic lock-manager modification count.
     *
     * @since 4.27
     */
    public long getModCount();

    /**
     * Returns the reservation produced by this operation, or {@code null} when it had no
     * client-visible lock change.
     *
     * @since 4.27
     */
    public LockChangeReservationSet getReservation();
  }

  /**
   * A consistent lock-state snapshot for one session.
   *
   * @author Eike Stepper
   * @since 4.27
   */
  public interface LockStateSnapshot
  {
    /**
     * Returns the session's exact lock-change sequence baseline represented by this snapshot.
     *
     * @since 4.27
     */
    public long getLockModCount();

    /**
     * Returns the lock states relevant to the session when this snapshot was taken.
     *
     * @since 4.27
     */
    public List<CDOLockState> getLockStates();
  }

  @Deprecated
  public List<LockState<Object, IView>> getLockStates();

  @Deprecated
  public void setLockState(Object key, LockState<Object, IView> lockState);

  @Deprecated
  public Object getLockEntryObject(Object key);

  @Deprecated
  public void lock(boolean explicit, LockType type, IView view, Collection<? extends Object> objects, long timeout) throws InterruptedException;

  @Override
  @Deprecated
  public void lock(LockType type, IView context, Collection<? extends Object> objectsToLock, long timeout) throws InterruptedException;

  @Override
  @Deprecated
  public void lock(LockType type, IView context, Object objectToLock, long timeout) throws InterruptedException;

  @Deprecated
  public List<LockState<Object, IView>> lock2(boolean explicit, LockType type, IView view, Collection<? extends Object> objects, boolean recursive,
      long timeout) throws InterruptedException;

  @Override
  @Deprecated
  public List<LockState<Object, IView>> lock2(LockType type, IView context, Collection<? extends Object> objectsToLock, long timeout)
      throws InterruptedException;

  @Deprecated
  public void unlock(boolean explicit, LockType type, IView view, Collection<? extends Object> objects);

  @Deprecated
  public void unlock(boolean explicit, IView view);

  @Override
  @Deprecated
  public void unlock(LockType type, IView context, Collection<? extends Object> objectsToUnlock);

  @Override
  @Deprecated
  public void unlock(IView context);

  @Deprecated
  public List<LockState<Object, IView>> unlock2(boolean explicit, IView view);

  @Deprecated
  public List<LockState<Object, IView>> unlock2(boolean explicit, LockType type, IView view, Collection<? extends Object> objects, boolean recursive);

  @Override
  @Deprecated
  public List<LockState<Object, IView>> unlock2(LockType type, IView context, Collection<? extends Object> objectsToUnlock);

  @Override
  @Deprecated
  public List<LockState<Object, IView>> unlock2(IView context, Collection<? extends Object> objectsToUnlock);

  @Override
  @Deprecated
  public List<LockState<Object, IView>> unlock2(IView context);

  @Override
  @Deprecated
  public long lock(IView context, Collection<? extends Object> objects, LockType lockType, int count, long timeout,
      LockDeltaHandler<Object, IView> deltaHandler, Consumer<LockState<Object, IView>> stateHandler) throws InterruptedException, TimeoutRuntimeException;

  @Override
  @Deprecated
  public long unlock(IView context, Collection<? extends Object> objects, LockType lockType, int count, LockDeltaHandler<Object, IView> deltaHandler,
      Consumer<LockState<Object, IView>> stateHandler);

  @Deprecated
  public IView openView(ISession session, int viewID, boolean readOnly, String durableLockingID);
}
