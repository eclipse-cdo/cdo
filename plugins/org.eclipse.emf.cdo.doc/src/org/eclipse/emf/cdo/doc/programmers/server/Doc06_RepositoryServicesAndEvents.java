/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IUnitManager;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.event.IListener;

/**
 * Repository Services and Events
 * <p>
 * A repository is a running service object. Its managers expose server-side state; its containers and lifecycle
 * notifiers expose meaningful changes. Observe those services from an application extension and unregister every
 * listener at shutdown.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc06_RepositoryServicesAndEvents
{
  /**
   * Services
   * <p>
   * {@link IRepository#getSessionManager()} exposes active {@link ISession sessions}. The branch, revision, and
   * commit-info managers ({@link CDOBranchManager}, {@link CDORevisionManager}, and {@link CDOCommitInfoManager})
   * provide repository history services. The package registry ({@link CDOPackageRegistry}),
   * {@link ILockingManager locking manager}, and optional {@link IUnitManager unit manager} complete the commonly
   * useful services. Inspect their capability and lifecycle contracts instead of assuming that every store supports
   * every historical or branching operation.
   */
  public class Services
  {
  }

  /**
   * Sessions, Views, and Transactions
   * <p>
   * An {@link ISession} is the server-side representation of a client session. Its {@link ISessionManager manager}
   * owns the session, and the session owns server {@link IView views}; {@link ITransaction} is the transaction form of
   * a view. They carry server-visible user, session, view, branch-point, and repository context. Applications may
   * inspect that context and use it in supported handlers, but should not retain closed sessions or invoke internal
   * commit machinery.
   */
  public class ServerSideContexts
  {
  }

  /**
   * Event Families
   * <p>
   * Repository and container lifecycle events identify activation and removal. The session manager emits container
   * events for session open and close; each session emits container events for views and transactions. Commit-info
   * handlers observe completed commit information. Branch and locking managers have their own notifier contracts.
   * Listener callbacks run in the notifier's execution path; keep them quick and hand off slow work instead of making
   * unsupported assumptions about a global callback thread.
   * {@link #observeSessions(IRepository) ObserveSessions.java}
   */
  public class Events
  {
  }

  /**
   * Installs and returns a session-manager listener so that the caller can later remove it.
   *
   * @snip
   */
  public IListener observeSessions(IRepository repository)
  {
    IListener listener = event -> {
      repository.getSessionManager().getSessions();
    };

    repository.getSessionManager().addListener(listener);
    return listener;
  }
}
