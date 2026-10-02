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
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IUnitManager;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.IAppExtension3;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Repository Services and Events
 * <p>
 * A repository is the lifetime boundary for server-side state. It owns a session manager; each server session owns
 * its open server views, including transactions. Managers attached to the repository expose history, model, lock,
 * and unit services. Notifiers report changes to these live objects. Observe them from an application extension, copy
 * the context needed by background work, and remove every listener before repository shutdown. Commit interception
 * belongs to {@link Doc07_RepositoryHandlersAndCommitProcessing}.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc06_RepositoryServicesAndEvents
{
  /**
   * Repository Services
   * <p>
   * {@link IRepository#getSessionManager()} owns active {@link ISession sessions}. Each session owns its {@link IView
   * server views}, and the {@link ITransaction transaction} subtype is the mutable commit context for a client
   * operation. The repository's {@link CDOBranchManager branch manager}, {@link CDORevisionManager revision manager},
   * and {@link CDOCommitInfoManager commit-info manager} expose history services; the {@link CDOPackageRegistry
   * package registry} supplies model metadata, the {@link ILockingManager locking manager} coordinates locks, and an
   * {@link IUnitManager unit manager} is available when repository configuration enables it. Check repository/store
   * capabilities before depending on optional history or unit behavior.
   */
  public class Services
  {
  }

  /**
   * Server Session, View, and Transaction Context
   * <p>
   * A server {@link ISession} is the repository-side connection identity and authenticated user context, not the
   * client's {@code CDOSession}. Its server {@link IView} instances carry view IDs and branch points; transactions
   * additionally represent mutable commit work. These objects are live runtime context, not durable records. On a
   * removal event, capture values such as session ID, user ID, view ID, branch and transaction kind before scheduling
   * work. Never queue a session, view, or transaction for later use after it can close.
   */
  public class ServerSideContexts
  {
  }

  /**
   * Events and Listener Lifetimes
   * <p>
   * The session manager emits container add/remove events for sessions; each session emits container events for views
   * and transactions. Repository lifecycle, branch-manager, and locking-manager notifications cover other transitions.
   * Commit-info handlers observe completed commit metadata; validation and commit interception are covered in
   * {@link Doc07_RepositoryHandlersAndCommitProcessing}. These are distinct event contracts, so inspect the event
   * type or use a container event adapter appropriate to the notifier.
   * <p>
   * Listener callbacks execute in the notifier's delivery path. Keep callbacks short: copy immutable scalar values and
   * pass them to an application-owned executor or consumer for slow persistence, network, or UI work. Coordinate that
   * executor's shutdown with the extension. Retain the exact listener instances; remove the session-manager listener
   * from its manager and each nested view listener from its session during extension stop. Removal events should also
   * detach nested listeners promptly, because a closed session is no longer a usable context.
   * {@link #createLifecycleObserver(Consumer) CreateLifecycleObserver.java}
   */
  public class Events
  {
  }

  /**
   * Installs session and nested view/transaction observers for each configured repository. It forwards scalar event
   * descriptions to the supplied sink and removes every listener when the extension stops.
   *
   * @param recordSink receives short descriptions; enqueue expensive work in the application-owned sink
   * @snip
   */
  public IAppExtension3 createLifecycleObserver(Consumer<String> recordSink)
  {
    return new IAppExtension3()
    {
      private final Map<IRepository, IListener> repositoryListeners = new IdentityHashMap<>();

      private final Map<ISession, IListener> sessionListeners = new IdentityHashMap<>();

      @Override
      public void start(File configFile)
      {
      }

      @Override
      public void start(IRepository[] repositories, File configFile)
      {
        try
        {
          for (IRepository repository : repositories)
          {
            IListener listener = new ContainerEventAdapter<ISession>()
            {
              @Override
              protected void onAdded(IContainer<ISession> container, ISession session)
              {
                int sessionID = session.getSessionID();
                String userID = session.getUserID();
                recordSink.accept("session opened id=" + sessionID + " user=" + userID);

                IListener viewListener = new ContainerEventAdapter<IView>()
                {
                  @Override
                  protected void onAdded(IContainer<IView> container, IView view)
                  {
                    recordSink.accept("view opened session=" + sessionID + " view=" + view.getViewID() + " transaction=" + (view instanceof ITransaction));
                  }

                  @Override
                  protected void onRemoved(IContainer<IView> container, IView view)
                  {
                    recordSink.accept("view closed session=" + sessionID + " view=" + view.getViewID() + " transaction=" + (view instanceof ITransaction));
                  }
                };

                synchronized (sessionListeners)
                {
                  sessionListeners.put(session, viewListener);
                }

                session.addListener(viewListener);
              }

              @Override
              protected void onRemoved(IContainer<ISession> container, ISession session)
              {
                int sessionID = session.getSessionID();
                String userID = session.getUserID();
                IListener viewListener;
                synchronized (sessionListeners)
                {
                  viewListener = sessionListeners.remove(session);
                }

                if (viewListener != null)
                {
                  session.removeListener(viewListener);
                }

                recordSink.accept("session closed id=" + sessionID + " user=" + userID);
              }
            };

            repositoryListeners.put(repository, listener);
            repository.getSessionManager().addListener(listener);
          }
        }
        catch (RuntimeException | Error ex)
        {
          removeListeners();
          throw ex;
        }
      }

      private void removeListeners()
      {
        for (Map.Entry<IRepository, IListener> entry : repositoryListeners.entrySet())
        {
          entry.getKey().getSessionManager().removeListener(entry.getValue());
        }

        repositoryListeners.clear();

        synchronized (sessionListeners)
        {
          for (Map.Entry<ISession, IListener> entry : sessionListeners.entrySet())
          {
            entry.getKey().removeListener(entry.getValue());
          }

          sessionListeners.clear();
        }
      }

      @Override
      public void stop(IRepository[] repositories)
      {
        removeListeners();
      }

      @Override
      public void stop()
      {
        removeListeners();
      }
    };
  }
}
