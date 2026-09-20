/*
 * Copyright (c) 2025-2026 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOAdapter;
import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictChangedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeClosedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionScopeOpenedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewAdaptersNotifiedEvent;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.emf.common.notify.Adapter;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleEvent;

/**
 * Notifications and Event Handling
 * <p>
 * CDO applications observe change through two related but different mechanisms. EMF {@code Notification}s are emitted
 * by model objects to their adapters. Net4j {@link IEvent}s are emitted by sessions, views, transactions, and other
 * notifiers to registered {@link IListener listeners}. A repository commit received from another client is first a
 * session/view invalidation; it becomes object-level EMF notification only when the view is configured to deliver the
 * corresponding adapter notification.
 * <p>
 * This chapter explains how the mechanisms fit together, where to register listeners, and how to avoid confusing a
 * remote invalidation with a local edit or a transaction commit. Detailed configuration remains in
 * {@link Doc03_WorkingWithSessions}, {@link Doc04_WorkingWithViews}, {@link Doc05_WorkingWithTransactions}, and
 * {@link Doc06_Locking}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc09_NotificationsAndEventHandling
{
  /**
   * The Notification and Event Model
   * <p>
   * There are four useful levels of observation. A local model edit produces ordinary EMF notifications on the affected
   * objects. A transaction can additionally report its transition from clean to dirty and back to clean. A remote
   * commit is received as a session invalidation and distributed as a view invalidation. Finally, lifecycle, option,
   * permission, lock, and remote-session changes are CDO-specific events on their owning notifiers.
   * <p>
   * An invalidation says that the revision known by a view is no longer current; it is not itself an EMF feature delta.
   * Use {@link CDOViewInvalidationEvent#getRevisionDeltas()} when a view has a delta, and use adapters when the
   * application needs object-level notifications. These channels are complementary, not interchangeable.
   */
  public class NotificationAndEventModel
  {
  }

  /**
   * EMF Model Notifications
   * <p>
   * CDO objects are EMF objects. Applications can attach an EMF {@link Adapter adapter} to an object's adapter list and receive normal
   * EMF notifications for local changes. A local transaction edit is the right place to use an adapter when a component
   * needs the feature, old value, new value, or list-position information carried by an EMF notification.
   * <p>
   * Remote changes need separate care. Passive updates must be enabled for session and view invalidation events. By
   * default, a remote commit invalidates relevant cached revisions; it does not promise a detailed EMF delta to every
   * adapter. {@link CDOView.Options#setInvalidationNotificationEnabled(boolean)} can enable the synthetic
   * {@link CDOInvalidationNotification}, whose delta-related methods are unsupported. For detailed remote adapter
   * delivery, register matching adapters and enable a {@link CDOAdapterPolicy change-subscription policy} as described
   * in {@link CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)}.
   * <p>
   * An adapter only observes an object that is present and has that adapter, and a view may fetch or retain objects
   * lazily. Do not use the absence of an object notification as proof that the repository did not change; use the view
   * invalidation or refresh the object instead. Detachment and load notifications are separately configurable through
   * {@link CDOView.Options}.
   */
  public class EMFNotifications
  {
    /**
     * Installs an adapter on a CDO object.
     *
     * @param object the object on which the adapter is installed
     * @param adapter the adapter to install
     * @snip
     */
    public void listenToObject(CDOObject object, CDOAdapter adapter)
    {
      object.eAdapters().add(adapter);
      System.out.println("Adapter installed on " + object);
      // Remove this same adapter when the observing component is disposed.
    }
  }

  /**
   * CDO Events and Listeners
   * <p>
   * The Net4j event pattern is small: an {@link INotifier notifier} exposes add/remove-listener operations and invokes
   * {@link IListener#notifyEvent(IEvent)} with an event whose source is the notifier. Sessions, views, transactions,
   * option objects, and managers use this same pattern.
   * <p>
   * Keep the listener instance if it must later be removed. Filtering is normally done with {@code instanceof}, which
   * preserves subtype relationships. Each registration has its own removal obligation.
   */
  public class CDOEventsAndListeners
  {
    /**
     * Registers a view listener and returns the same listener for later removal.
     *
     * @param view the view to observe
     * @return the registered listener
     * @snip
     */
    public IListener registerViewListener(CDOView view)
    {
      IListener listener = event -> {
        if (event instanceof CDOViewInvalidationEvent)
        {
          CDOViewInvalidationEvent invalidation = (CDOViewInvalidationEvent)event;
          System.out.println("Invalidated objects: " + invalidation.getDirtyObjects().size());
        }
      };

      view.addListener(listener);
      return listener;
    }

    /**
     * Removes a previously registered listener.
     *
     * @param view the view from which to remove the listener
     * @param listener the exact listener instance returned by {@link #registerViewListener(CDOView)}
     */
    public void unregisterViewListener(CDOView view, IListener listener)
    {
      view.removeListener(listener);
    }
  }

  /**
   * Session Events
   * <p>
   * Register session listeners for events whose scope is the connection or repository view shared by all of the
   * session's views. The application-level categories are {@link CDOSessionInvalidationEvent} for received passive
   * updates, permission changes, and lock changes. Repository/session state and type changes, lifecycle events such as
   * {@link LifecycleEvent}, and remote-session manager events are also exposed by the corresponding public notifier.
   * Session option objects emit option events when their configuration changes.
   * <p>
   * A session invalidation is emitted after passive-update processing has received a commit notification. Its
   * {@link CDOSessionInvalidationEvent#isRemote()} flag distinguishes a remote commit from a local transaction, and
   * {@link CDOSessionInvalidationEvent#getLocalTransaction()} identifies the local transaction when applicable. See
   * {@link Doc03_WorkingWithSessions} for passive-update and session configuration details.
   */
  public class SessionEvents
  {
  }

  /**
   * View Events
   * <p>
   * View listeners are the most useful CDO-level observation point for model visibility. Important public event families
   * include {@link CDOViewInvalidationEvent}, {@link CDOViewAdaptersNotifiedEvent},
   * {@link CDOViewTargetChangedEvent}, and {@link CDOViewLocksChangedEvent}. A view can also report permissions,
   * durability, provider, and lifecycle changes. The invalidation event provides dirty and detached objects and may
   * provide revision deltas; the adapters-notified event marks completion of adapter delivery for the same update time.
   * <p>
   * A view's branch point and time-machine position determine which revision is visible. See
   * {@link Doc04_WorkingWithViews} for branch/time and passive-update configuration.
   */
  public class ViewEvents
  {
  }

  /**
   * Transaction Events
   * <p>
   * A {@link CDOTransactionStartedEvent} is fired when a transaction first becomes dirty. A
   * {@link CDOTransactionFinishedEvent} is fired when it becomes clean after a commit, rollback, or undo; use its
   * current {@link CDOTransactionFinishedEvent#getCause()} API rather than the deprecated type accessor. Conflict
   * events report objects entering or leaving the transaction's conflict state.
   * <p>
   * Current nested transactions also have {@link CDOTransactionScopeOpenedEvent} and
   * {@link CDOTransactionScopeClosedEvent}; a scope shares the root transaction's view and dirty state, and closing a
   * scope is not a repository commit. Transaction and view option objects report option changes through their own event
   * types. Commit details are returned by {@link CDOTransaction#commit()}, not by an invented generic "commit event".
   * See {@link Doc05_WorkingWithTransactions} for commit, rollback, conflict, and scope semantics.
   */
  public class TransactionEvents
  {
    /**
     * Observes transaction transitions useful to an editor.
     *
     * @param transaction the transaction to observe
     * @snip
     */
    public void listenToTransaction(CDOTransaction transaction)
    {
      transaction.addListener(event -> {
        if (event instanceof CDOTransactionStartedEvent)
        {
          System.out.println("Transaction became dirty");
        }
        else if (event instanceof CDOTransactionFinishedEvent)
        {
          CDOTransactionFinishedEvent finished = (CDOTransactionFinishedEvent)event;
          System.out.println("Transaction finished: " + finished.getCause());
        }
        else if (event instanceof CDOTransactionConflictChangedEvent)
        {
          CDOTransactionConflictChangedEvent conflict = (CDOTransactionConflictChangedEvent)event;
          System.out.println("Conflict count: " + conflict.getConflicts());
        }
      });
    }
  }

  /**
   * Remote Changes, Invalidations, and Subscriptions
   * <p>
   * When another transaction commits, a session configured for passive updates receives a commit notification. The
   * session emits a {@link CDOSessionInvalidationEvent}, and each eligible non-historical view incorporates the update
   * and emits a {@link CDOViewInvalidationEvent}. The view invalidation identifies modified and detached objects and may
   * contain revision deltas. It does not mean that every object has been eagerly reloaded; the current state is obtained
   * according to the view's update and loading policies. A view can also use this event family for a local rollback;
   * {@link CDOViewInvalidationEvent#LOCAL_ROLLBACK} identifies that case rather than a repository commit timestamp.
   * <p>
   * A transaction's local dirty objects are protected from being silently overwritten. A remote change can instead
   * produce a transaction conflict, which is observable through the transaction conflict APIs and events. Passive
   * update modes, invalidation policies, and refresh behavior are configured at the session/view level; link to
   * {@link Doc03_WorkingWithSessions.CreatingAndConfiguringSessions.PassiveUpdatesAndRefreshing} and
   * {@link Doc04_WorkingWithViews} for those choices.
   * <p>
   * Change subscriptions control which object/adapter pairs are registered for detailed server-side change delivery.
   * Add an adapter to an object, add a matching {@link CDOAdapterPolicy} to the view options, and remove the policy or
   * adapter when the observing component is disposed. Subscriptions can reduce unnecessary notification traffic, but
   * they do not replace passive updates, view invalidations, or normal cache loading. Temporary objects are subscribed
   * automatically after they become persistent.
   */
  public class RemoteChangesAndSubscriptions
  {
    /**
     * Enables detailed change subscriptions for adapters implementing {@link CDOAdapter}.
     *
     * @param view the view whose adapter policy is configured
     * @snip
     */
    public void enableCDOAdapterSubscriptions(CDOView view)
    {
      view.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.CDO);
    }
  }

  /**
   * Lock Notifications
   * <p>
   * Lock events are part of the same CDO event model but describe repository lock state, not model feature changes.
   * Views can emit {@link CDOViewLocksChangedEvent}; sessions can emit
   * {@link CDOSessionLocksChangedEvent}. Delivery depends on the session
   * lock-notification mode and, where applicable, the view lock-notification option. Use the current lock state before
   * acting and see {@link Doc06_Locking} for configuration, ownership, durable locking, and lock lifecycle semantics.
   */
  public class LockEvents
  {
  }

  /**
   * Event Threading and Delivery
   * <p>
   * Net4j notifier delivery is synchronous by default: a notifier invokes its listeners on the thread that fires the
   * event. CDO invalidation and remote-session processing can therefore invoke listeners on a CDO/network or
   * invalidation worker thread, while a local edit can invoke EMF adapters on the editing thread. Some notifiers may
   * configure a notification executor, so an application must not assume one universal callback thread.
   * <p>
   * Callbacks should be short and non-blocking. Do not wait for UI work, perform long-running I/O, or assume that a
   * callback owns a view critical section. If several view accesses must form one consistent observation, use the view's
   * {@link CDOView#sync() critical section}; then hand UI or expensive work to the appropriate application executor.
   * The session Javadoc specifically warns about deadlocks when adapter callbacks synchronously cross to a UI thread.
   */
  public class EventThreading
  {
  }

  /**
   * Listener Lifetime, Errors, and Ordering
   * <p>
   * Listener registration is strong unless a particular notifier documents otherwise. Remove listeners from the same
   * notifier when the component, view, transaction, or session is disposed. Remove object adapters as well, and remove
   * change-subscription policies when they are no longer needed. Closing a notifier stops its useful lifecycle, but it is
   * not a replacement for releasing application-held listener references or unregistering from longer-lived managers.
   * CDO does not require applications to use a weak-listener convention.
   * <p>
   * The common Net4j notifier catches ordinary listener exceptions, logs them, and continues with the other listeners;
   * applications should nevertheless catch expected failures and should never use listener exceptions as control flow.
   * A cancellation is a special framework mechanism and should not be introduced in ordinary application listeners.
   * <p>
   * Rely only on documented family-level ordering: a view invalidation is associated with the update it describes, and
   * {@link CDOViewAdaptersNotifiedEvent} can be used to observe completion of adapter notification for that update.
   * There is no general global ordering across all session, view, object, option, and transaction listeners.
   */
  public class LifetimeErrorsAndOrdering
  {
  }

  /**
   * Choosing the Right Mechanism
   * <p>
   * Use an EMF adapter when one model object needs feature-level local or subscribed remote notifications. Use a view
   * listener for invalidation, adapter-delivery completion, branch/time, permission, or lock changes. Use a session
   * listener for repository-wide passive updates, permissions, locks, lifecycle, and remote-session information. Use a
   * transaction listener for dirty/clean transitions, conflicts, and nested-scope lifecycle. Use a change subscription
   * when selected objects need detailed remote adapter delivery and the associated server traffic is justified. Use
   * lock-specific events only for lock state; they do not describe model changes.
   * <p>
   * In all cases, treat the event as a signal to inspect the current public state. An event is not a permission to skip
   * view synchronization, conflict handling, transaction checks, or listener cleanup.
   */
  public class ChoosingTheRightMechanism
  {
  }
}
