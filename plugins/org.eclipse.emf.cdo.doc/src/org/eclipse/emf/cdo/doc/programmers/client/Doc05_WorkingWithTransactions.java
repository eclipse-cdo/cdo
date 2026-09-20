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

import org.eclipse.emf.cdo.doc.programmers.server.Doc07_RepositoryHandlersAndCommitProcessing;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOFileTransaction;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionContainer;
import org.eclipse.emf.cdo.transaction.CDOTransactionScope;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitConflictException;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.util.OptimisticLockingException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Working with Transactions
 * <p>
 * A {@link CDOTransaction transaction} is a read-write {@link CDOView view} on the current state of a repository
 * branch. It shares the view concepts explained in {@link Doc04_WorkingWithViews}, but additionally records local
 * model changes until they are committed or rolled back.
 * <p>
 * A root transaction commit is the operation that persists the effective changes in the repository. Savepoints and
 * nested scopes are client-side composition mechanisms; completing either one does not create a repository commit.
 * Server applications that validate or observe these commits use the supported handlers described in
 * {@link Doc07_RepositoryHandlersAndCommitProcessing}.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc05_WorkingWithTransactions
{
  /**
   * Creating and Managing Transactions
   * <p>
   * Transactions are opened from a {@link CDOSession session}. The session supplies the repository connection and
   * the transaction owns its view and resource set until it is closed. The common overload of
   * {@link CDOSession#openTransaction() openTransaction()} and the more specific overloads inherited from
   * {@link CDOTransactionContainer} allow an application to select a branch point,
   * resource set, or durable-locking identity.
   * <p>
   * A transaction remains usable after a successful commit, so an application can perform another unit of work or
   * close it. Closing the transaction releases its client-side resources; it does not replace commit or rollback.
   *
   * {@link #openTransaction(CDOSession) OpenTransaction.java}
   */
  public class CreatingAndManagingTransactions
  {
    /**
     * Opens a transaction, performs application work, commits it, and closes the transaction.
     *
     * @param session the already opened session
     * @throws Exception if the example work or commit fails
     * @snip
     */
    @SuppressWarnings("unused")
    public void openTransaction(CDOSession session) throws Exception
    {
      CDOTransaction transaction = session.openTransaction();

      try
      {
        Resource resource = transaction.getOrCreateResource("/example");
        // Modify the resource or its model contents here.

        transaction.setCommitComment("Update example model");
        transaction.commit();
      }
      finally
      {
        transaction.close();
      }
    }
  }

  /**
   * Local Changes and Dirty State
   * <p>
   * A transaction is dirty when it contains uncommitted changes. The public transaction API exposes separate maps
   * for new, detached, and modified objects through {@link CDOTransaction#getNewObjects()},
   * {@link CDOTransaction#getDetachedObjects()}, and {@link CDOTransaction#getDirtyObjects()}.
   * {@link CDOTransaction#getRevisionDeltas()} exposes the revision-level changes.
   * <p>
   * These collections describe the transaction's current local state. They are not a second repository history and
   * are cleared or reduced as changes are committed or rolled back.
   *
   * {@link #inspectDirtyState(CDOTransaction) InspectDirtyState.java}
   */
  public class LocalChangesAndDirtyState
  {
    /**
     * Inspects the categories of local changes before deciding whether to commit.
     *
     * @param transaction the transaction to inspect
     * @snip
     */
    public void inspectDirtyState(CDOTransaction transaction)
    {
      if (transaction.isDirty())
      {
        int newObjects = transaction.getNewObjects().size();
        int dirtyObjects = transaction.getDirtyObjects().size();
        int detachedObjects = transaction.getDetachedObjects().size();

        System.out.println("New: " + newObjects);
        System.out.println("Dirty: " + dirtyObjects);
        System.out.println("Detached: " + detachedObjects);
      }
    }
  }

  /**
   * Committing Changes
   * <p>
   * {@link CDOTransaction#commit()} sends the transaction's effective changes to the repository and returns a
   * {@link CDOCommitInfo commit-info} object. The transaction stays open after a successful commit. Commit comments
   * and arbitrary commit properties can be set with {@link CDOTransaction#setCommitComment(String)} and
   * {@link CDOTransaction#setCommitProperty(String, String)}; committed metadata can be observed through the
   * {@link CDOCommitInfoManager commit-info manager}.
   * <p>
   * A commit can fail with {@link CommitException}, including a {@link CommitConflictException} or an
   * {@link OptimisticLockingException}. Applications must not assume that acquiring explicit locks eliminates every
   * possible commit failure.
   *
   * {@link #commitChanges(CDOTransaction) CommitChanges.java}
   */
  public class CommittingChanges
  {
    /**
     * Commits local changes and reads the resulting commit metadata.
     *
     * @param transaction the transaction containing local changes
     * @return the commit information
     * @throws CommitException if the repository rejects or cannot complete the commit
     * @snip
     */
    public CDOCommitInfo commitChanges(CDOTransaction transaction) throws CommitException
    {
      transaction.setCommitComment("Update customer model");
      transaction.setCommitProperty("source", "customer-editor");
      return transaction.commit();
    }
  }

  /**
   * Commit Retry and Conflicts
   * <p>
   * {@link CDOTransaction#hasConflict()} and {@link CDOTransaction#getConflicts()} expose objects whose local
   * modifications conflict with remote changes. A configured {@link CDOConflictResolver
   * conflict resolver} can resolve conflicts during invalidation; otherwise an application commonly rolls back,
   * reapplies its business operation against the current view, and commits again.
   * <p>
   * The retry overloads of {@link CDOTransaction#commit(Runnable, int, IProgressMonitor)} and its
   * {@link Callable Callable} counterpart run the operation before each attempt. The integer is the total attempt
   * count, so {@code 3} permits one initial attempt and two retries. When a {@link ConcurrentAccessException}
   * occurs, CDO rolls back the failed attempt before trying again; other commit failures are not retried by this
   * overload. The operation supplied for retry must therefore be repeatable and must reapply the intended changes.
   *
   * {@link #commitWithRetry(CDOTransaction, Runnable) CommitWithRetry.java}
   */
  public class CommitRetryAndConflicts
  {
    /**
     * Performs a repeatable business operation with a bounded commit retry count.
     *
     * @param transaction the transaction to commit
     * @throws CommitException if committing ultimately fails
     * @param operation the repeatable business operation for each attempt
     * @throws ConcurrentAccessException if all attempts encounter concurrent access
     * @snip
     */
    public void commitWithRetry(CDOTransaction transaction, Runnable operation) throws CommitException, ConcurrentAccessException
    {
      transaction.commit(operation, 3, null);
    }
  }

  /**
   * Rolling Back Changes
   * <p>
   * {@link CDOTransaction#rollback()} removes all uncommitted changes from the root transaction and leaves the
   * transaction open for further work. It is different from rolling back a {@link CDOUserSavepoint savepoint}, which
   * retains the earlier part of the transaction, and from rolling back a {@link CDOTransactionScope scope}, which
   * affects only that scope and its descendants.
   *
   * {@link #rollbackChanges(CDOTransaction) RollbackChanges.java}
   */
  public class RollingBackChanges
  {
    /**
     * Discards the current uncommitted unit of work while retaining the transaction.
     *
     * @param transaction the transaction to roll back
     * @snip
     */
    public void rollbackChanges(CDOTransaction transaction)
    {
      if (transaction.isDirty())
      {
        transaction.rollback();
      }
    }
  }

  /**
   * Partial Commits
   * <p>
   * A transaction can restrict one commit to a set of {@link EObject committable objects} with
   * {@link CDOTransaction#setCommittables(Set)}. Objects not selected remain local and the transaction remains dirty;
   * dependencies required by the selected objects must also be included. This is useful for staged work, but it is a
   * deliberate consistency boundary and should not be used as a substitute for designing independent transactions.
   *
   * {@link #commitSelectedObjects(CDOTransaction, EObject) CommitSelectedObjects.java}
   */
  public class PartialCommits
  {
    /**
     * Commits one selected object while leaving other local changes for a later commit.
     *
     * @param transaction the transaction containing the changes
     * @param object the object and its required dependencies to commit
     * @throws CommitException if the selected commit fails
     * @snip
     */
    public void commitSelectedObjects(CDOTransaction transaction, EObject object) throws CommitException
    {
      transaction.setCommittables(Collections.singleton(object));
      transaction.commit();
    }
  }

  /**
   * Savepoints
   * <p>
   * {@link CDOTransaction#setSavepoint()} creates an in-memory client-side boundary in the transaction's change
   * history. {@link CDOUserSavepoint#rollback()} restores the transaction to that boundary by undoing changes made
   * after it, while keeping the root transaction active. Savepoints do not flush changes to disk or to the repository.
   * <p>
   * {@link CDOSavepoint} is the richer transaction-specific view of the same boundary. It exposes the objects and
   * revision deltas belonging to a savepoint, including {@link CDOSavepoint#getDirtyObjects()} and
   * {@link CDOSavepoint#getAllChangeSetData()}. Use those inspection APIs only when the application needs to reason
   * about the change segment itself.
   *
   * {@link #useSavepoint(CDOTransaction, Runnable, boolean) UseSavepoint.java}
   */
  public class Savepoints
  {
    /**
     * Uses a savepoint to isolate an optional part of a larger unit of work.
     *
     * @param transaction the active transaction
     * @param operation the optional operation to evaluate
     * @param accept whether to keep the operation's changes
     * @snip
     */
    public void useSavepoint(CDOTransaction transaction, Runnable operation, boolean accept)
    {
      CDOUserSavepoint savepoint = transaction.setSavepoint();

      try
      {
        operation.run();
        if (!accept)
        {
          savepoint.rollback();
        }
      }
      catch (RuntimeException ex)
      {
        savepoint.rollback();
        throw ex;
      }
    }
  }

  /**
   * Nested Transaction Scopes
   * <p>
   * {@link CDOTransaction#openScope()} creates a stack-disciplined scope inside the root transaction. A scope shares
   * the transaction's view, resource set, object identities, cache, dirty state, locks, and session. Its changes are
   * immediately visible in the containing transaction.
   * <p>
   * {@link CDOTransactionScope#commit()} accepts the scope into its parent but never persists anything to the
   * repository. {@link CDOTransactionScope#rollback()} restores the state at the scope boundary, and
   * {@link CDOTransactionScope#close()} rolls back an active scope. Only a later commit on the root
   * {@link CDOTransaction} creates the repository commit. Scopes may be nested and must be completed from the
   * innermost scope outward.
   * <p>
   * {@link CDOTransactionScope#asTransaction()} supplies a stable nested transaction facade for APIs that accept a
   * transaction. Commit operations on that facade are unsupported; the scope itself is completed with
   * {@link CDOTransactionScope#commit()}.
   *
   * {@link #runBusinessOperationInScope(CDOTransaction, Consumer) RunBusinessOperationInScope.java}
   */
  public class NestedTransactionScopes
  {
    /**
     * Runs a composable business operation in a scope and accepts it into the enclosing transaction.
     *
     * @param transaction the enclosing root transaction
     * @param operation the operation to run against the scope's transaction facade
     * @snip
     */
    public void runBusinessOperationInScope(CDOTransaction transaction, Consumer<CDOTransaction> operation)
    {
      try (CDOTransactionScope scope = transaction.openScope())
      {
        operation.accept(scope.asTransaction());
        scope.commit();
      }
    }
  }

  /**
   * Merge
   * <p>
   * {@link CDOTransaction#merge(CDOBranch, CDOMerger)} and the related branch-point overloads apply changes from a
   * source branch or branch point to the local transaction. They create local changes; the caller still decides when
   * to commit them. The returned {@link CDOChangeSetData} describes the applied change set. Merge conflicts are part
   * of the transaction conflict model and must be resolved before a successful commit.
   * <p>
   * Branch selection and historical branch points belong in the Branching and Versioning chapter. This section is
   * limited to the transaction side of applying and committing a merge.
   */
  public class Merging
  {
  }

  /**
   * Revert
   * <p>
   * {@link CDOTransaction#revertTo(CDOBranchPoint)} creates local changes that restore the transaction's model to a
   * specified historical branch point. Revert is not the same as {@link CDOTransaction#rollback() rollback}: rollback
   * discards uncommitted local work, whereas revert prepares a new change set that can itself be reviewed and committed.
   * It is also distinct from a savepoint rollback and from opening a historical read-only view.
   */
  public class RevertingChanges
  {
  }

  /**
   * Change Export and Import
   * <p>
   * {@link CDOTransaction#exportChanges(OutputStream)} serializes the transaction's local changes to an output
   * stream and {@link CDOTransaction#importChanges(InputStream, boolean)} applies serialized transaction
   * changes from an input stream. The boolean controls whether savepoints are reconstructed while importing. These
   * operations work with transaction changes; they are not repository commits, raw revision history exports, or
   * generic model serialization.
   * <p>
   * The file-backed transaction described below uses these APIs internally, but they can also be used directly when an
   * application controls the transfer stream.
   *
   * {@link #transferChanges(CDOTransaction, CDOTransaction, OutputStream, InputStream) TransferChanges.java}
   */
  public class ChangeExportAndImport
  {
    /**
     * Transfers changes through application-managed streams.
     *
     * @param source transaction whose local changes are exported
     * @param output output stream for the serialized changes
     * @param target transaction into which the changes are imported
     * @param input input stream containing serialized transaction changes
     * @throws IOException if stream processing fails
     * @snip
     */
    public void transferChanges(CDOTransaction source, CDOTransaction target, OutputStream output, InputStream input) throws IOException
    {
      source.exportChanges(output);
      target.importChanges(input, true);
    }
  }

  /**
   * File-Backed Transactions
   * <p>
   * {@link CDOFileTransaction} is the current public API for persisting uncommitted changes in its stable backing file
   * (available through {@link CDOFileTransaction#getFile()}) and later pushing them to the repository. Its normal
   * {@link CDOFileTransaction#commit()} persists the current uncommitted changes to that file and does not commit to
   * the repository; {@link CDOFileTransaction#push()} performs the repository commit and removes the persisted file
   * after success. The inherited rollback operation is unsupported, as are the inherited callable and runnable commit
   * overloads. File-backed transactions are therefore appropriate for explicit export-and-push workflows rather than
   * ordinary rollback-based editing.
   * <p>
   * The older {@code CDOPushTransaction} API is deprecated as of 4.30 in favor of {@link CDOFileTransaction} and is
   * not used in this example.
   *
   * {@link #useFileBackedTransaction(CDOTransaction) UseFileBackedTransaction.java}
   */
  public class FileBackedTransactions
  {
    /**
     * Exports local changes to a file and later pushes them to the repository.
     *
     * @param transaction the delegate transaction
     * @throws Exception if file creation, export, or push fails
     * @snip
     */
    public void useFileBackedTransaction(CDOTransaction transaction) throws Exception
    {
      CDOFileTransaction fileTransaction = CDOUtil.createFileTransaction(transaction);

      try
      {
        // Modify the delegate transaction here.
        fileTransaction.commit();

        // The file can be retained across application restarts before this step.
        fileTransaction.push();
      }
      finally
      {
        fileTransaction.close();
      }
    }
  }

  /**
   * Queries in Transactions
   * <p>
   * General querying is covered in the Views chapter. A transaction additionally offers
   * {@link CDOTransaction#createQuery(String, String, boolean)} and its context overload, whose
   * {@code considerDirtyState} argument controls whether the query considers the transaction's local uncommitted
   * changes. Use {@code true} only when the query is intended to describe the transaction's working state rather than
   * the repository state alone.
   *
   * {@link #queryDirtyState(CDOTransaction) QueryDirtyState.java}
   */
  public class TransactionQueries
  {
    /**
     * Creates a query that explicitly includes the transaction's local dirty state.
     *
     * @param transaction the transaction to query
     * @return the query configured to consider local changes
     * @snip
     */
    public CDOQuery queryDirtyState(CDOTransaction transaction)
    {
      return transaction.createQuery("ocl", "EObject.allInstances()", true);
    }
  }

  /**
   * Transaction Options
   * <p>
   * {@link CDOTransaction#options()} exposes the transaction-specific options in addition to the view options
   * described in the Views chapter. Application developers should normally consider three groups: conflict resolvers
   * for automatic handling of remote conflicts; optimistic-locking and commit-info timeouts for bounded commit
   * behavior; and automatic lock release, including its exemptions, for predictable lock ownership after commit or
   * rollback.
   * <p>
   * The options API is intentionally linked rather than duplicated here. Its Javadoc documents defaults and the
   * complete option set, including undo detection, stale-reference cleaning, and attached-revision handling.
   *
   * {@link #configureTransactionOptions(CDOTransaction) ConfigureTransactionOptions.java}
   */
  public class TransactionOptions
  {
    /**
     * Configures the options most commonly relevant to transaction behavior.
     *
     * @param transaction the transaction to configure
     * @snip
     */
    public void configureTransactionOptions(CDOTransaction transaction)
    {
      transaction.options().setOptimisticLockingTimeout(10000L);
      transaction.options().setCommitInfoTimeout(10000L);
      transaction.options().setAutoReleaseLocksEnabled(true);
    }
  }
}
