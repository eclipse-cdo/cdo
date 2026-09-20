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
 * A CDO server hosts one or more {@link IRepository repositories}. Each repository combines a generic CDO core with
 * a store and a protocol boundary; applications normally configure, observe, and customize the repository rather than
 * either boundary. The managed container supplies the factories and named elements that assemble those parts.
 * <p>
 * The diagram shows the architectural separation. It is deliberately not a deployment topology:
 * <p align="center">{@image repository-architecture.png}
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
   * {@link ISessionManager session manager}, and {@link ILockingManager locking manager}. These services expose the
   * state that applications can observe; repository handlers and protectors are the supported ways to influence work.
   * The following articles introduce startup, configuration, services, events, and handlers in that order.
   */
  public class RepositoryCore
  {
  }

  /**
   * Store Boundary
   * <p>
   * {@link IStore} is the physical persistence boundary. Its capabilities determine whether a repository can offer
   * facilities such as auditing and branching. Choose and configure an existing store at the repository boundary; a
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
