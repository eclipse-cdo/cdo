/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepository.ReadAccessHandler;
import org.eclipse.emf.cdo.server.IRepository.WriteAccessHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.ICommitConflictResolver;
import org.eclipse.emf.cdo.spi.server.ObjectWriteAccessHandler;

import java.util.List;

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
   * A {@link ReadAccessHandler} receives revisions before they are sent to a client. It rejects the requested set by
   * throwing a runtime exception, and may remove optimizer-supplied additional revisions. A
   * {@link WriteAccessHandler} runs before a transaction reaches the store and again after a successful commit. The
   * before callback can reject semantic validation with {@link WriteAccessHandler.TransactionValidationException}; its
   * message reaches the client. The after callback is observation-only: it must not alter the commit context.
   * {@link ObjectWriteAccessHandler} is the supported SPI base for object-level write checks.
   */
  public class AccessHandlers
  {
  }

  /**
   * Commit Information and Conflicts
   * <p>
   * Register a {@link CDOCommitInfoHandler} with the repository's commit-info manager to observe commit information
   * after it is available. {@link ICommitConflictResolver} is an expert SPI for conflict resolution and is configured
   * as part of repository setup; its contract intentionally exposes commit-context SPI, so it should be used only when
   * merge policy cannot be expressed through ordinary transaction validation.
   */
  public class CommitObservationAndConflicts
  {
  }

  /**
   * Application-facing Commit Flow
   * <p>
   * A client transaction arrives with session and transaction context. Repository security and write handlers can
   * validate or veto it; conflict policy can resolve eligible conflicts; the configured store persists the accepted
   * change; and commit information plus after-commit handlers observe the result. Exact indication and store-internal
   * sequencing is deliberately not an application contract.
   * {@link #installReadCheck(IRepository) InstallReadCheck.java}
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
}
