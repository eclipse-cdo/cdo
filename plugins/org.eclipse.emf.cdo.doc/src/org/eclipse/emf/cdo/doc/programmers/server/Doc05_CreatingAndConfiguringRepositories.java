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

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ContainerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Creating and Configuring Repositories
 * <p>
 * A repository has a stable name within its container, a configured {@link IStore store}, repository properties,
 * optional initial package definitions, and a lifecycle. The application chooses a store and properties, creates the
 * repository, applies initial packages before activation, and registers it in the owning {@link IManagedContainer}.
 * {@link CDOServerUtil#addRepository(IManagedContainer, IRepository)} retains and activates it; the container owns its
 * shutdown. The store determines persistence and capabilities, while XML/server configuration composes the same
 * pieces declaratively. Production XML and database operation syntax are described by
 * {@link Doc01_ConfiguringRepositories}.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc05_CreatingAndConfiguringRepositories
{
  /**
   * Programmatic Repository Setup
   * <p>
   * {@link CDOServerUtil#createRepository(String, IStore, Map)} creates an inactive repository from its container
   * name, store and property map. The name is the lookup identity within that container and should remain stable for
   * configuration and client connection. Properties configure repository behavior such as auditing, branching,
   * locking and ID generation; use {@link IRepository.Props} constants and confirm that the selected store supports
   * the requested feature. Set {@link IRepository#setInitialPackages(EPackage...) initial packages} before activation
   * when model metadata must be available from startup. Otherwise package registration is governed by repository mode
   * and security policy.
   * <p>
   * {@link CDOServerUtil#addRepository(IManagedContainer, IRepository)} assigns the container when needed, retains
   * the repository under the repository-factory group/type/name, and activates it. That transfers lifecycle ownership
   * to the container. A repository that is never registered must be explicitly deactivated by its creator if it was
   * activated separately. At shutdown, stop extensions that use the repository and then deactivate the owning
   * container. Capability checks should use the repository's advertised information; do not infer support from a
   * requested property alone.
   */
  public class ProgrammaticSetup
  {
  }

  /**
   * MEMStore and Embedded Repositories
   * <p>
   * {@link MEMStoreUtil} is suitable for an in-memory repository used by an embedded application, test, or short-lived
   * service. Its model data is not a durable substitute for a persistent store. {@code CDOEmbeddedRepositoryConfig}
   * is the higher-level embedded-server API: subclasses provide a store, properties, and optional initial packages,
   * and can open a local client session. Its activation manages the embedded repository and JVM acceptor lifecycle. Use
   * direct repository APIs when the application already owns container and transport assembly; use embedded
   * configuration when it wants that complete local-server lifecycle packaged together. An embedded convenience API
   * can own the repository/container and optionally open a local client session; direct APIs are appropriate when the
   * application already owns those boundaries.
   * {@link #withMemoryRepository(String, EPackage[], Consumer) WithMemoryRepository.java}
   */
  public class EmbeddedRepositories
  {
  }

  /**
   * Existing Persistent Stores
   * <p>
   * A persistent store changes how the {@link IStore} is created and configured, not the repository registration
   * lifecycle. Configure the selected DB adapter, datasource, and mapping strategy through application setup or the
   * server XML store element, then pass the resulting store to {@code createRepository} (or let the repository
   * configurator do so). Repository properties still describe repository behavior; store configuration describes
   * persistence. Database installation, credentials, schema operation, and tuning remain
   * {@link Doc01_ConfiguringRepositories Operator's Guide concerns}. Do not implement a store merely to customize
   * repository behavior; use handlers and services instead.
   */
  public class PersistentStoreConfiguration
  {
  }

  /**
   * Creates, registers, activates, uses, and shuts down an in-memory repository. This method owns the container and
   * therefore deactivates it after the operation even when application work throws.
   *
   * @param repositoryUUID stable UUID configured for this example repository
   * @param initialPackages packages that must be registered before the repository is activated
   * @param operation application work performed while the repository is active
   * @snip
   */
  public void withMemoryRepository(String repositoryUUID, EPackage[] initialPackages, Consumer<IRepository> operation)
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    try
    {
      IStore store = MEMStoreUtil.createMEMStore();
      Map<String, String> properties = new HashMap<>();
      properties.put(IRepository.Props.OVERRIDE_UUID, repositoryUUID);

      IRepository repository = CDOServerUtil.createRepository("inventory", store, properties);
      if (initialPackages.length != 0)
      {
        repository.setInitialPackages(initialPackages);
      }

      CDOServerUtil.addRepository(container, repository);
      operation.accept(repository);
    }
    finally
    {
      container.deactivate();
    }
  }
}
