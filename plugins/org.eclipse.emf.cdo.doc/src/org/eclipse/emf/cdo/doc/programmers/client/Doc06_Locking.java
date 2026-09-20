/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Locking
 * <p>
 * CDO normally lets transactions edit their local state and detects concurrent changes when they commit. Explicit
 * locking is an additional repository-coordinated mechanism for workflows that need other views to stay out of the
 * way while an object is being inspected or changed. This chapter explains the lock types, their lifecycle, durable
 * locking, and the events and state APIs that applications can use.
 * <p>
 * A lock is owned by a {@link CDOView view}, and therefore indirectly by its {@link CDOSession session}; a transaction
 * is a read-write view. The repository's locking manager arbitrates locks between sessions and views. Locks apply to
 * CDO objects, including resource nodes, rather than to arbitrary local Java objects. A lock protects the relevant
 * repository object from conflicting remote lock operations and remote changes, but it is not a substitute for a
 * transaction commit, a local view critical section, or application-level authorization.
 * <p>
 * Explicit locking is not required for every transaction. Use normal transaction editing and commit-time conflict
 * detection when concurrent work is acceptable, and acquire explicit locks only when a business operation needs an
 * exclusion or reservation that should be visible to other sessions.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc06_Locking
{
  /**
   * Lock Types
   * <p>
   * CDO exposes {@link LockType#READ read}, {@link LockType#WRITE write}, and {@link LockType#OPTION write-option}
   * locks. Read locks are compatible with other read locks. A write lock is exclusive. A write-option lock is also
   * exclusive, prevents other views from obtaining a write lock, but still permits other views to obtain read locks;
   * it is useful for reserving the right to write later without blocking readers immediately.
   * <p>
   * The same types are available through {@link CDOView#lockObjects(java.util.Collection, LockType, long)} and through
   * the object-specific {@link CDOObject#cdoReadLock()}, {@link CDOObject#cdoWriteLock()}, and
   * {@link CDOObject#cdoWriteOption()} handles.
   */
  public class LockTypes
  {
    /**
     * Read Locks
     * <p>
     * A read lock coordinates readers with writers. Several views can hold a read lock on the same object, while a
     * write lock cannot be acquired until the incompatible read locks have been released.
     */
    public class ReadLocks
    {
      /**
       * @snip
       */
      public void acquireReadLock(CDOTransaction transaction, CDOObject object) throws InterruptedException
      {
        transaction.lockObjects(Collections.singleton(object), LockType.READ, 1000L);
        System.out.println("Read lock acquired for object: " + object);

        transaction.unlockObjects(Collections.singleton(object), LockType.READ);
        System.out.println("Read lock released for object: " + object);
      }
    }

    /**
     * Write Locks
     * <p>
     * A write lock is exclusive. Acquire it before a modification when the application must prevent other views from
     * changing the object or acquiring a conflicting lock during the business operation.
     */
    public class WriteLocks
    {
      /**
       * @snip
       */
      public void acquireWriteLock(CDOTransaction transaction, CDOObject object) throws InterruptedException
      {
        transaction.lockObjects(Collections.singleton(object), LockType.WRITE, 1000L);
        System.out.println("Write lock acquired for object: " + object);

        transaction.unlockObjects(Collections.singleton(object), LockType.WRITE);
        System.out.println("Write lock released for object: " + object);
      }
    }

    /**
     * Write Option Locks
     * <p>
     * A write-option lock reserves a future write. It excludes other write locks but not read locks, so it can be
     * used while preparing an edit without making the object unreadable to other views.
     */
    public class WriteOptionLocks
    {
      /**
       * @snip
       */
      public void acquireWriteOptionLock(CDOTransaction transaction, CDOObject object) throws InterruptedException
      {
        transaction.lockObjects(Collections.singleton(object), LockType.OPTION, 1000L);
        System.out.println("Write option lock acquired for object: " + object);

        transaction.unlockObjects(Collections.singleton(object), LockType.OPTION);
        System.out.println("Write option lock released for object: " + object);
      }
    }
  }

  /**
   * Acquiring, Releasing, and Inspecting Locks
   * <p>
   * {@link CDOView#lockObjects(java.util.Collection, LockType, long)} acquires all requested locks through the
   * repository and waits up to the supplied timeout, in milliseconds. The call is interruptible and reports an
   * interrupted acquisition through {@link InterruptedException}; a timed-out object-level acquisition through
   * {@link java.util.concurrent.TimeoutException}. The {@link CDOView#unlockObjects(java.util.Collection, LockType)}
   * and {@link CDOView#unlockObjects()} methods release selected or all locks owned by the view.
   * <p>
   * The object-specific {@link CDOLock} handles provide the same operations for one object, including
   * {@link CDOLock#tryLock(long, TimeUnit) tryLock}, {@link CDOLock#isLocked()}, and
   * {@link CDOLock#isLockedByOthers()}. The newer {@link CDOLock#acquire(long, TimeUnit, boolean)} form returns an
   * acquired-lock object that can be closed, which is convenient for a bounded scope. Always release locks in a
   * finally block (or close the acquired handle), including when the business operation fails.
   */
  public class AcquiringAndInspectingLocks
  {
    /**
     * Acquires a lock with a bounded wait and releases it reliably.
     *
     * @param object the object to protect
     * @throws Exception if acquisition is interrupted or times out
     * @snip
     */
    public void acquireWithTimeout(CDOObject object) throws Exception
    {
      CDOLock lock = object.cdoWriteLock();
      lock.lock(1, TimeUnit.SECONDS);

      try
      {
        System.out.println("Exclusive work on " + object);
      }
      finally
      {
        lock.unlock();
      }
    }

    /**
     * Checks local ownership and the currently known repository lock state.
     *
     * @param view the view used to inspect the object
     * @param object an object in the view
     * @return the current lock state, or {@code null} for a transient object
     * @snip
     */
    public CDOLockState inspectLockState(CDOView view, CDOObject object)
    {
      CDOLockState state = object.cdoLockState();
      if (state != null)
      {
        CDOLockOwner writeOwner = state.getWriteLockOwner();
        System.out.println("Held here: " + object.cdoWriteLock().isLocked());
        System.out.println("Held by another view: " + object.cdoWriteLock().isLockedByOthers());
        System.out.println("Write owner: " + writeOwner);
        System.out.println("Known states in view: " + view.getLockStatesOfObjects(Collections.singleton(object)).length);
      }

      return state;
    }
  }

  /**
   * Optimistic Locking
   * <p>
   * In current CDO terminology, ordinary transaction editing is optimistic: the transaction does not acquire an
   * explicit write lock for every object before changing it. It works against its local revisions, receives remote
   * invalidations, and checks the required implicit locks and revision state during {@link CDOTransaction#commit()}.
   * Concurrent changes can therefore produce a conflict or an {@link org.eclipse.emf.cdo.util.OptimisticLockingException}
   * rather than blocking the editor at the time of the first modification.
   * <p>
   * {@link CDOTransaction.Options#getOptimisticLockingTimeout()} controls how long commit waits for the implicit lock
   * acquisition used by that commit. It is not an explicit object-lock API. See
   * {@link Doc05_WorkingWithTransactions.CommittingChanges} and
   * {@link Doc05_WorkingWithTransactions.TransactionOptions} for commit and conflict handling.
   */
  public class OptimisticLocking
  {
  }

  /**
   * Pessimistic Locking
   * <p>
   * Explicit, or pessimistic, locking is appropriate when a user workflow must reserve an object before doing work:
   * for example, when an editor must not allow another editor to change the same object while a multi-step operation
   * is in progress. Acquire a write lock before modifying, or a read lock when the workflow must exclude writers while
   * it examines a stable state. Use a write-option lock when readers may continue but the next write must be reserved.
   * <p>
   * Lock acquisition is repository-mediated and can contend with other sessions. Use finite timeouts, handle
   * interruption and timeout as normal control flow, lock objects in a consistent order when acquiring several, and
   * release as soon as the protected operation ends. An explicit lock reduces a particular class of concurrent
   * changes; it does not remove the need to handle commit failures or permissions.
   */
  public class PessimisticLocking
  {
  }

  /**
   * Lock State
   * <p>
   * {@link CDOLockState} represents all known locks for one object. It exposes the set of read-lock owners and the
   * single write and write-option owners through {@link CDOLockState#getReadLockOwners()},
   * {@link CDOLockState#getWriteLockOwner()}, and {@link CDOLockState#getWriteOptionOwner()}. A
   * {@link CDOLockOwner} identifies the owning session and view and also reports the durable-locking ID and whether
   * the owner is currently a purely durable (not locally open) view.
   * <p>
   * {@link CDOObject#cdoLockState()} is convenient for a loaded object. A view can inspect multiple states with
   * {@link CDOView#getLockStatesOfObjects(java.util.Collection)} or {@link CDOView#getLockStates(java.util.Collection)}.
   * These are client-side, currently known states; use {@link CDOView#refreshLockStates(java.util.function.Consumer)}
   * when an application needs the latest states from the repository before making a decision.
   */
  public class LockState
  {
  }

  /**
   * Durable Locking
   * <p>
   * Durable locking persists the information needed to reopen a view, including its branch point, view kind, user
   * identity, and locks acquired while durable locking is enabled. The durable owner is identified by a durable-locking
   * ID rather than by the lifetime of one connected session. Consequently, closing a durable view or losing the client
   * connection can leave its locks represented by a purely durable view in the repository.
   * <p>
   * Call {@link CDOView#enableDurableLocking()} on a view or transaction and retain the returned ID. Reopen the same
   * view with {@link CDOSession#openView(String)} or {@link CDOSession#openTransaction(String)}. A repository/store must
   * support durable locking; otherwise enabling it fails. An unknown ID cannot be reopened. To end the durable lock
   * area, call {@link CDOView#disableDurableLocking(boolean)}; passing {@code true} also releases its locks, while
   * {@code false} removes durability without asking the view to release them.
   * <p>
   * Durable locking is useful for reconnectable editors and long-running ownership that must survive a normal client
   * disconnect. It is not a lease or an automatic conflict resolver: applications still need a recovery policy and
   * must explicitly release or reclaim the durable lock area when the workflow is finished.
   */
  public class DurableLocking
  {
    /**
     * Makes a transaction durable and resumes it by its durable-locking ID.
     *
     * @param transaction the transaction whose view will be made durable
     * @param object an object in the transaction to lock durably
     * @throws Exception if the repository cannot create or reopen the durable view
     * @snip
     */
    public void resumeDurableTransaction(CDOTransaction transaction, CDOObject object) throws Exception
    {
      String durableLockingID = transaction.enableDurableLocking();
      CDOSession session = transaction.getSession();
      object.cdoWriteLock().lock();
      transaction.close();

      CDOTransaction resumed = session.openTransaction(durableLockingID);
      try
      {
        System.out.println("Resumed durable transaction: " + resumed);
      }
      finally
      {
        resumed.disableDurableLocking(true);
        resumed.close();
      }
    }
  }

  /**
   * Lock Notifications and Events
   * <p>
   * A view can fire {@link CDOViewLocksChangedEvent} when lock changes from other views are received. A session can
   * fire {@link CDOSessionLocksChangedEvent} for remote repository notifications. To receive them, configure the
   * session's {@link CDOSession.Options#setLockNotificationMode(LockNotificationMode) lock notification mode} to
   * {@link LockNotificationMode#ALWAYS ALWAYS}, or use {@link LockNotificationMode#IF_REQUIRED_BY_VIEWS
   * IF_REQUIRED_BY_VIEWS} together with {@link CDOView.Options#setLockNotificationEnabled(boolean) view notification
   * enablement}. {@link LockNotificationMode#OFF OFF} disables delivery.
   * <p>
   * The events implement {@link org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo}; applications can inspect the
   * authoring lock owner, operations and lock types, affected IDs, deltas, and resulting lock states. The sender is
   * non-null for a local sender and null when the change came from a remote view.
   */
  public class LockNotifications
  {
    /**
     * Enables view lock notifications and observes lock changes.
     *
     * @param view the view to configure
     * @snip
     */
    public void listenForLockChanges(CDOView view)
    {
      view.options().setLockNotificationEnabled(true);
      view.addListener(new IListener()
      {
        @Override
        public void notifyEvent(IEvent event)
        {
          if (event instanceof CDOViewLocksChangedEvent)
          {
            CDOViewLocksChangedEvent lockEvent = (CDOViewLocksChangedEvent)event;
            System.out.println("Changed lock types: " + lockEvent.getLockTypes());
          }
        }
      });
    }
  }

  /**
   * Locking and Transactions
   * <p>
   * Because a transaction is a view, its explicit locks are owned by that transaction's view. By default,
   * {@link CDOTransaction.Options#isAutoReleaseLocksEnabled()} is {@code true}, so commit and root-transaction
   * rollback release its locks. Set {@link CDOTransaction.Options#setAutoReleaseLocksEnabled(boolean)} to
   * {@code false}, or configure exemptions, when locks must survive those operations. Closing an ordinary view or
   * transaction releases its ordinary locks; a durable view is the deliberate exception because its lock area can
   * remain after the client-side view closes.
   * <p>
   * A nested transaction scope shares the root transaction's view, session, cache, dirty state, and locks. Completing
   * or rolling back a scope does not create a repository commit and does not create a separate lock owner. See
   * {@link Doc05_WorkingWithTransactions.NestedTransactionScopes} for the scope lifecycle.
   */
  public class LockingAndTransactions
  {
  }

  /**
   * Locking and Reconnection
   * <p>
   * Ordinary locks are tied to the connected view and are released when that view is closed or its session is lost.
   * Applications that need to reconnect and reclaim locks must enable durable locking first, retain the returned
   * durable-locking ID, and reopen the view or transaction with that ID. On recovery, inspect the resulting
   * {@link CDOLockState lock states} and handle an unavailable or already-active durable view according to the
   * application policy. Durable state is repository-managed, so a repository that does not provide durable locking
   * cannot provide this recovery behavior.
   */
  public class LockingAndRecovery
  {
  }

  /**
   * Performance and Usage Guidance
   * <p>
   * Locks serialize work at the repository and create contention visible to other sessions. Prefer normal optimistic
   * transaction processing when conflicts are rare and recoverable. When explicit locking is justified, lock the
   * smallest useful set of objects, choose read locks instead of write locks when readers only need stability, use
   * option locks for a genuine write reservation, keep lock duration short, and always bound acquisition waits.
   * <p>
   * Do not confuse the view's local {@link CDOView#sync() critical section} with repository locking: the former
   * coordinates threads using one view, while the latter coordinates views and sessions through the repository.
   * Likewise, lock notifications are observations of lock-state changes, not a replacement for checking the current
   * state before acting.
   */
  public class UsageGuidance
  {
  }
}
