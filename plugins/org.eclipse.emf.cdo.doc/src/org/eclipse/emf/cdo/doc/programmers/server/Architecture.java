/*
 * Copyright (c) 2011-2013, 2016, 2021, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * Server Architecture
 * <p>
 * A CDO server hosts one or more {@link IRepository repositories}. Each repository combines model and history
 * services with a store and a protocol boundary; applications normally configure, observe, and customize the
 * repository rather than either boundary. The managed container supplies the factories and named elements that
 * assemble those parts.
 * <p>
 * The diagram shows the architectural separation. It is deliberately not a deployment topology:
 * <p align="center">{@image repository-architecture.png}
 * <p>
 * A typical request starts when a client connector reaches a server acceptor and creates a server session. The session
 * opens a server view or transaction on a repository. A read then uses the revision manager and store; a query is
 * dispatched to a registered query handler; a commit passes through supported validation/interception and conflict
 * handling before the store persists its change set. The resulting commit information and notifications update
 * interested clients. The protocol carries these requests and responses, but its individual indications are not a
 * stable application extension seam.
 * <p>
 * Repository managers divide responsibilities: the package registry supplies model metadata; the branch manager
 * resolves branches and points; the revision manager loads current or historical object data; the commit-info manager
 * exposes commit metadata; the session manager tracks connected clients; the locking manager coordinates explicit
 * repository locks; and, when enabled, the unit manager handles bounded model subtrees. Repository handlers and
 * protectors intercept supported operations; they do not replace the store or repository factory.
 * <p>
 * This guide uses normal API and intentional extension SPI. In particular, {@code internal} repository, session,
 * commit-manager, protocol-indication, and store classes are implementation details and are not server application
 * extension points.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Architecture
{
  /**
   * Repository Core
   * <p>
 * A running repository owns the package registry, {@link CDOBranchManager branch manager},
   * {@link CDORevisionManager revision manager}, {@link CDOCommitInfoManager commit-info manager},
 * {@link ISessionManager session manager}, and {@link ILockingManager locking manager}; it can also expose a unit
 * manager and commit handlers according to its configuration. These services expose state that applications can
 * observe; repository handlers and protectors are the supported ways to influence work. A server application should
 * retain manager objects only while the repository is active and should not retain closed session or view instances as
 * live contexts.
   * Continue with {@link Doc02_ServerApplicationAndStartup startup and shutdown}, the
   * {@link Doc03_ManagedContainer managed-container lifecycle}, and
   * {@link Doc05_CreatingAndConfiguringRepositories repository creation}. For live server contexts and observation, see
   * {@link Doc06_RepositoryServicesAndEvents}; for supported interception, see
   * {@link Doc07_RepositoryHandlersAndCommitProcessing}. Security/query extensions and synchronization/transfer are
   * covered in {@link Doc08_SecurityQueriesAndSpecializedExtensions} and
   * {@link Doc09_AdvancedServerIntegration}.
   */
  public class RepositoryCore
  {
  }

  /**
   * Store Boundary
   * <p>
 * {@link IStore} is the persistence boundary for revisions, commit data, and large objects. Its capabilities determine
 * whether a repository can offer facilities such as auditing and branching. Choose and configure an existing store at the repository boundary; a
   * custom store and {@code IStoreAccessor} implementation are expert persistence SPI work, not normal server
   * application programming.
   */
  public class StoreBoundary
  {
  }

  /**
   * Protocol and Transport Boundary
   * <p>
   * {@link ISessionProtocol} separates the CDO repository from wire communication. CDO's supplied server integration
   * uses Net4j acceptors and connectors: an {@link IAcceptor acceptor} receives client connections and an
   * {@link IConnector connector} carries the resulting protocol traffic. Server applications commonly configure an
   * acceptor through the container, but do not implement protocol indications.
   */
  public class ProtocolBoundary
  {
  }

  /**
   * Container and Lifecycle
   * <p>
   * The {@link IManagedContainer managed container} locates factories and creates or retains named server elements.
   * In an OSGi server, bundle registration supplies much of that wiring. Standalone and embedded applications prepare
   * and populate a container explicitly. In both cases, activation and deactivation define component lifetime.
   *
   * @see Doc02_ServerApplicationAndStartup
   * @see Doc03_ManagedContainer
   */
  public class ContainerAndLifecycle
  {
  }
}
