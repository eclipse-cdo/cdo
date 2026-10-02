/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IRepository.ReadAccessHandler;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.ICommitConflictResolver;
import org.eclipse.emf.cdo.spi.server.ObjectWriteAccessHandler;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Repository Handlers and Commit Processing
 * <p>
 * Repository handlers are the focused supported interception points for application behavior. They are not a reason
 * to implement a store. Install a handler with {@link IRepository#addHandler(IRepository.Handler)} and remove that
 * same instance when the owning extension stops.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc07_RepositoryHandlersAndCommitProcessing
{
  /**
   * Read and Write Access
   * <p>
   * A {@link ReadAccessHandler} receives the exact requested revisions and a separate list of optimizer-supplied
   * additional revisions. If any requested revision is forbidden, throw to reject the request; the handler cannot
   * silently remove only part of that array. It may remove speculative additional revisions that the client did not
   * request. Keep checks bounded because they execute on the read path.
   * <p>
   * A {@link WriteAccessHandler} runs before a transaction reaches the store and, after a successful persistence,
   * again in its after-commit callback. The before callback receives the server transaction, commit context, and
   * progress monitor. Use {@link WriteAccessHandler.TransactionValidationException} for semantic validation; its
   * message is returned to the client. The commit context is for inspection; if a supported mutation is needed, use
   * its documented {@code modify(...)} operation rather than altering its internal state directly. The after callback
   * is observation-only and must not mutate the context. Both callbacks are on the commit path and should avoid slow
   * work. {@link ObjectWriteAccessHandler} is the supported SPI base for object-level write checks.
   */
  public class AccessHandlers
  {
  }

  /**
   * Commit Information and Conflicts
   * <p>
   * A write handler's after-commit callback observes the specific transaction and commit context immediately after its
   * store commit. A {@link CDOCommitInfoHandler} registered with the repository's commit-info manager observes completed
   * commit-info records, which is a better seam for indexing or auditing that is organized around commit metadata.
   * Neither callback should perform unbounded work inline; copy the required immutable values and submit expensive
   * processing to an application executor. {@link ICommitConflictResolver} is an expert SPI for resolving eligible
   * transaction conflicts and is configured as part of repository setup. Its contract exposes commit-context SPI, so
   * use it only when policy cannot be expressed through ordinary validation and client conflict handling.
   */
  public class CommitObservationAndConflicts
  {
  }

  /**
   * Application-facing Commit Flow
   * <p>
   * A client transaction arrives with authenticated session and transaction context. Repository protection and
   * authorization decide whether the user may read or write; before-commit handlers apply application validation;
   * conflict policy handles eligible concurrent changes; and the configured store persists the accepted change. The
   * repository then publishes commit information and update notifications, while after-commit handlers observe the
   * result. These are conceptual application phases; exact signal and store-internal sequencing is not a contract.
   * The snippets show a read gate, a validation rule, and completed-commit observation.
   * <p>
   * {@link #installReadCheck(IRepository) Read check: InstallReadCheck.java}
   * <p>
   * {@link #requireCommitComment(IRepository) Semantic validation: RequireCommitComment.java}
   * <p>
   * {@link #observeCommitInfo(IRepository, Consumer) Commit observation: ObserveCommitInfo.java}
   */
  public class CommitFlow
  {
  }

  /**
   * Installs a read-access handler that rejects unauthenticated revision delivery.
   *
   * @snip
   */
  public ReadAccessHandler installReadCheck(IRepository repository)
  {
    ReadAccessHandler handler = new ReadAccessHandler()
    {
      @Override
      public void handleRevisionsBeforeSending(ISession session, CDORevision[] revisions, List<CDORevision> additionalRevisions)
      {
        if (session.getUserID() == null)
        {
          throw new SecurityException("Authentication is required");
        }
      }
    };

    repository.addHandler(handler);
    return handler;
  }

  /**
   * Rejects commits without a non-empty commit comment.
   *
   * @snip
   */
  public WriteAccessHandler requireCommitComment(IRepository repository)
  {
    WriteAccessHandler handler = new WriteAccessHandler()
    {
      @Override
      public void handleTransactionBeforeCommitting(ITransaction transaction, IStoreAccessor.CommitContext commitContext, OMMonitor monitor)
      {
        String comment = commitContext.getCommitComment();
        if (comment == null || comment.trim().isEmpty())
        {
          throw new TransactionValidationException("A commit comment is required");
        }
      }

      @Override
      public void handleTransactionAfterCommitted(ITransaction transaction, IStoreAccessor.CommitContext commitContext, OMMonitor monitor)
      {
        // Keep the after-commit callback observation-only and quick.
      }
    };

    repository.addHandler(handler);
    return handler;
  }

  /**
   * Registers a commit-info observer. The consumer should enqueue any slow processing and the caller must remove the
   * returned handler when the application extension stops.
   *
   * @snip
   */
  public CDOCommitInfoHandler observeCommitInfo(IRepository repository, Consumer<CDOCommitInfo> enqueue)
  {
    CDOCommitInfoHandler handler = enqueue::accept;
    repository.getCommitInfoManager().addCommitInfoHandler(handler);
    return handler;
  }
}
