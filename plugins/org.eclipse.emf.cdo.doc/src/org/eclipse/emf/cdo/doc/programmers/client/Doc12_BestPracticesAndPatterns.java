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

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionScope;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.util.LockTimeoutException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Best Practices and Common Pitfalls
 * <p>
 * CDO applications combine EMF object access with session, view, transaction, repository, and network lifecycles.
 * The most serious failures usually happen at those boundaries: an object is used after its view has closed, a dirty
 * transaction is kept open across unrelated work, a callback performs blocking UI work, or a retry repeats a
 * non-repeatable operation. This chapter is a concise set of CDO-specific decisions and failure modes.
 * <p>
 * The detailed API references remain in {@link Doc03_WorkingWithSessions}, {@link Doc04_WorkingWithViews},
 * {@link Doc05_WorkingWithTransactions}, {@link Doc06_Locking}, {@link Doc07_LargeObjects},
 * {@link Doc08_BranchingAndVersioning}, {@link Doc09_NotificationsAndEventHandling},
 * {@link Doc10_IntegratingWithEMFAndOtherFrameworks}, and {@link Doc11_AdvancedTopics}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc12_BestPracticesAndPatterns
{
  /**
   * Own Sessions, Views, Transactions, and Resources Explicitly
   * <p>
   * Decide which application component owns each {@link CDOSession session}, view, transaction, and CDO-managed
   * {@link ResourceSet resource set}. The owner closes
   * it after the last dependent component has stopped using it. A transaction is also a view, but closing the transaction
   * is the transaction owner's responsibility; do not let a helper close a view or session that it did not open.
   * <p>
   * A resource set associated with a view is not an independent connection. CDO resources and objects obtained from it
   * must not be treated as usable after the owning view has closed. Likewise, a listener registration and an adapter
   * registration are application resources: retain the exact listener or adapter instance so it can be removed from the
   * same notifier. Readers and streams returned for CDO large objects should be closed with try-with-resources; views,
   * sessions, and transactions expose explicit {@code close()} lifecycle methods rather than being Java
   * {@code AutoCloseable} resources.
   * <p>
   * Close a transaction before its session, remove listeners before disposing the observed component, and release a LOB
   * stream before closing the view or session that supplies it. See {@link #commitOrRollback(CDOTransaction) commitOrRollback}
   * for a small failure-safe transaction boundary.
   */
  public class OwnershipAndLifecycle
  {
    /**
     * Commits a focused transaction or rolls it back after a commit failure, then closes it.
     *
     * @param transaction the transaction owned by the calling component
     * @throws CommitException if the commit fails
     * @snip
     */
    public void commitOrRollback(CDOTransaction transaction) throws CommitException
    {
      try
      {
        if (transaction.isDirty())
        {
          transaction.commit();
        }
      }
      catch (CommitException ex)
      {
        if (!transaction.isClosed() && transaction.isDirty())
        {
          transaction.rollback();
        }

        throw ex;
      }
      finally
      {
        if (!transaction.isClosed())
        {
          transaction.close();
        }
      }
    }
  }

  /**
   * Reuse Sessions, Keep Views Purposeful, and Bound Transactions
   * <p>
   * Reuse a session when its connector, repository, authentication, and passive-update policy are appropriate for the
   * component. Repeatedly opening and closing sessions adds connection and package/revision setup cost. Reuse a view
   * when its branch, time point, notification policy, and consistency requirements match; otherwise open a separate
   * view rather than silently changing the context of unrelated work.
   * <p>
   * Keep transactions focused on one coherent business operation. A long-lived dirty transaction holds local state,
   * increases the conflict window, and makes recovery after disconnects or user cancellation harder. Commit or roll back
   * deliberately, and check {@link CDOTransaction#isDirty()} and {@link CDOTransaction#hasConflict()} at boundaries.
   * {@link CDOTransactionScope scopes} are useful for composable operations: a scope can commit into its parent or roll
   * back to its opening state, but {@link CDOTransactionScope#commit()} is not a repository commit. Only the root
   * transaction commit persists changes.
   * <p>
   * Separate read-only views from editing transactions when a reader must not observe or mutate work-in-progress.
   * Detailed session and view lifecycle belongs to {@link Doc03_WorkingWithSessions} and {@link Doc04_WorkingWithViews}.
   */
  public class SessionViewAndTransactionBoundaries
  {
  }

  /**
   * Use Supported Concurrency Boundaries
   * <p>
   * CDO views and their model objects support concurrent single accesses. That does not make a sequence of reads atomic
   * with respect to invalidation. When several reads must describe one consistent observation, execute them through the
   * view's {@link CDOView#sync() critical section}. Keep the critical section short and do not hold it while waiting for
   * UI work, another thread, network I/O, or a long-running computation.
   * <p>
   * Sessions and views are not a reason to add application-wide synchronization around every call. Use repository locks
   * when the business rule requires coordination between clients, and use application locks only for application-owned
   * state. Listener callbacks can run on the thread that delivers the event; they should capture the relevant state and
   * hand expensive or UI work to an application executor. See {@link Doc04_WorkingWithViews}, {@link Doc06_Locking},
   * and {@link Doc09_NotificationsAndEventHandling} for the respective contracts.
   */
  public class ConcurrencyAndCallbacks
  {
  }

  /**
   * Choose Optimistic or Pessimistic Coordination Deliberately
   * <p>
   * Prefer normal optimistic transaction conflict detection when concurrent edits are uncommon or can be merged at the
   * domain level. Use explicit object locks when a short operation must exclude competing writers, and use durable locks
   * when ownership must survive a view or session lifetime and the repository's durable-locking support is appropriate.
   * A lock is coordination state, not a replacement for a transaction boundary or conflict policy.
   * <p>
   * Do not acquire broad or long-lived locks merely to avoid learning how conflicts work. Handle lock acquisition
   * timeouts as a decision for the caller, and release locks according to the transaction's configured lock policy.
   * Locking ownership, timeouts, and durable areas are covered in {@link Doc06_Locking}.
   */
  public class LockingChoices
  {
  }

  /**
   * Handle Conflicts and Retries as Business Decisions
   * <p>
   * Distinguish {@link ConcurrentAccessException} from a general {@link CommitException}. A concurrent-access failure
   * means that the operation must decide whether to refresh, merge, ask the user, or retry. A retry is safe only when the
   * operation is repeatable and its inputs are still valid. Bound the number of attempts and avoid an infinite loop that
   * turns contention into unbounded load.
   * <p>
   * Conflict resolvers can automate a known policy, but they do not make every domain merge correct. Reapply only the
   * repeatable part of a failed operation, inspect transaction conflict state, and preserve user input that cannot be
   * reconstructed. The transaction chapter's bounded retry example and conflict guidance in
   * {@link Doc05_WorkingWithTransactions} are the normative details.
   * <p>
   * {@link LockTimeoutException} and commit conflicts are different failures: one concerns lock acquisition, the other
   * concerns concurrent repository state. Do not handle both by blindly retrying the same mutation.
   */
  public class ConflictsAndRetries
  {
  }

  /**
   * Load Only What the Operation Needs
   * <p>
   * Treat loading as a cost decision. Prefer server-side queries when the task is to identify a bounded set of objects,
   * and prefer deliberate units when a known disjoint subtree is the working set. For graph traversal, choose collection
   * chunks and revision prefetch depth based on measured access patterns. Avoid accidental full traversal of a large
   * containment tree, infinite prefetch, and forcing full collection materialization for a list that is only sampled.
   * <p>
   * A cache hit is not the same as current repository truth: cache-aware revision requests describe where a revision may
   * be loaded from, while view invalidation and branch/time semantics determine what the view should observe. Use CDO
   * large-object types and streams for large payloads instead of materializing them into memory. See
   * {@link Doc07_LargeObjects} and {@link Doc11_AdvancedTopics} for the detailed loading and query APIs.
   */
  public class LoadingAndPerformance
  {
  }

  /**
   * Treat Remote Changes and Recovery as Asynchronous Failure Modes
   * <p>
   * A passive update and view invalidation signal that the visible state may have changed; it is not automatically a
   * feature-level delta for every adapter. Use subscriptions only for objects that need detailed remote adapter
   * delivery, remove subscriptions and listeners when the observing component ends, and move expensive callback work
   * off the delivery thread. The notification chapter explains the distinction between invalidation, adapters, and
   * CDO events in detail.
   * <p>
   * Reconnecting sessions can restore a connection, but they do not make an interrupted business operation idempotent.
   * An application must inspect session, view, and transaction state after a disconnect and decide whether to retry,
   * rebuild a transaction, or report failure. Never assume that a commit was not applied merely because the client lost
   * its response. Recovery configuration and recovery events are described in {@link Doc03_WorkingWithSessions} and
   * {@link Doc11_AdvancedTopics}.
   */
  public class RemoteChangesAndRecovery
  {
  }

  /**
   * Preserve Branch, History, and Integration Context
   * <p>
   * A revision version is meaningful together with its branch; it is not a globally unique object version. Keep the
   * branch and time point with identifiers used for history operations. A historical view is a read context, not a
   * revert operation, and a merge produces transaction changes that still require an explicit repository commit.
   * {@link CDOBranchVersion} therefore belongs in a branch-aware API rather than in a bare integer field.
   * <p>
   * CDO URIs, provider-created views, and CDO-managed resource sets carry ownership and context. Do not treat a CDO URI
   * as an ordinary platform file URI, do not assume a provider caches or closes views for you, and do not dispose a view's
   * resource set while EMF clients still use its resources. See {@link Doc08_BranchingAndVersioning} and
   * {@link Doc10_IntegratingWithEMFAndOtherFrameworks}.
   */
  public class ContextAndIntegration
  {
  }

  /**
   * Distinguish Failure Classes and Keep APIs Current
   * <p>
   * Handle commit failures, concurrent access, lock timeouts, transport disconnects, query failures, LOB I/O failures,
   * and closed-lifecycle errors according to their recovery meaning. Preserve causes and repository context in logs,
   * but do not expose credentials or sensitive URI user information. After a failure, query documented state such as
   * {@link CDOView#isClosed()}, transaction dirty/conflict state, and session lifecycle state before deciding what to do.
   * <p>
   * Prefer current public APIs. Avoid internal implementation packages and compatibility APIs marked deprecated, even when
   * an older example still compiles. A public SPI can be used for its documented extension purpose, but it should be
   * treated as an extension contract, not as ordinary application state. The integration and advanced-topics chapters
   * identify the current replacements for provider and loading patterns.
   */
  public class FailureHandlingAndAPIStability
  {
  }

  /**
   * Common CDO Pitfalls
   * <p>
   * Before shipping a client, check the boundaries that are easiest to get wrong:
   * <ul>
   * <li>Do not use objects, resources, or resource sets after their owning view or session has closed.</li>
   * <li>Do not confuse a nested scope commit with a repository commit.</li>
   * <li>Do not retry non-repeatable mutations or assume a lost commit response means that no commit happened.</li>
   * <li>Do not use broad locks where optimistic conflict handling is sufficient.</li>
   * <li>Do not interpret invalidations as universal feature-level notifications.</li>
   * <li>Do not traverse or prefetch an entire large graph without an explicit working-set reason.</li>
   * <li>Do not treat historical version numbers as globally meaningful.</li>
   * <li>Do not leak listeners, provider-created views, or LOB streams.</li>
   * </ul>
   * Each item is a CDO lifecycle or consistency rule, not a generic Java style preference. Use the linked subsystem
   * chapters when the application needs the precise option or recovery API.
   */
  public class CommonPitfalls
  {
  }
}
