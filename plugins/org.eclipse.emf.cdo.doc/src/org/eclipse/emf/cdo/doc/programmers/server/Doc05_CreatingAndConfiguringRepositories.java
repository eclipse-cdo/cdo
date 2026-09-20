/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.Collections;

/**
 * Creating and Configuring Repositories
 * <p>
 * A repository has a stable name, an existing {@link IStore store}, repository properties, and a lifecycle. Use
 * {@link CDOServerUtil} for simple embedded setup, then register the repository in its
 * {@link IManagedContainer managed container}. Production XML configuration and its complete store/acceptor syntax
 * are described by {@link Doc01_ConfiguringRepositories}, not repeated here.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc05_CreatingAndConfiguringRepositories
{
  /**
   * Programmatic Repository Setup
   * <p>
   * {@link CDOServerUtil#createRepository(String, IStore, java.util.Map)} creates a repository with its name, store,
   * and properties. {@link CDOServerUtil#addRepository(IManagedContainer, IRepository)} registers and activates it.
   * Set initial packages before activation when a repository must know packages immediately; model packages can also
   * be supplied on demand. Deactivate an application-owned repository during application shutdown.
   */
  public class ProgrammaticSetup
  {
  }

  /**
   * MEMStore and Embedded Repositories
   * <p>
   * {@link MEMStoreUtil} is suitable for an in-memory repository used by an embedded application, test, or short-lived
   * service. {@code CDOEmbeddedRepositoryConfig} is the higher-level embedded-server API: subclasses provide a store,
   * properties, optional initial packages, and can open a local client session. Its activation manages the embedded
   * repository and JVM acceptor lifecycle.
   * {@link #createMemoryRepository(IManagedContainer) CreateMemoryRepository.java}
   */
  public class EmbeddedRepositories
  {
  }

  /**
   * Existing Persistent Stores
   * <p>
   * For a DBStore, configure the supplied DB adapter, datasource, and mapping strategy at the application or server
   * configuration level, then attach the resulting store to the repository in the same way. Store capabilities govern
   * auditing, branching, and ID behavior. Database installation, credentials, schema operation, and tuning remain
   * {@link Doc01_ConfiguringRepositories Operator's Guide concerns}. Do not implement a store merely to customize repository behavior; use the handlers and
   * services in the later articles instead.
   */
  public class PersistentStoreConfiguration
  {
  }

  /**
   * Creates, registers, and activates an in-memory repository for an embedded application.
   *
   * @snip
   */
  public IRepository createMemoryRepository(IManagedContainer container)
  {
    IStore store = MEMStoreUtil.createMEMStore();
    IRepository repository = CDOServerUtil.createRepository("inventory", store, Collections.emptyMap());
    CDOServerUtil.addRepository(container, repository);
    return repository;
  }
}
