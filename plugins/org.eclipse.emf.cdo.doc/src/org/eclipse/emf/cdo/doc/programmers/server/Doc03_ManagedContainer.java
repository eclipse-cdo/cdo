/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;

import java.util.function.Consumer;

/**
 * The Managed Container
 * <p>
 * An {@link IManagedContainer} combines a factory registry with a registry of named runtime elements. A product group
 * identifies a service family, a factory type selects an implementation, and a description carries the instance key
 * or configuration (for example, a TCP endpoint). A lookup that needs an absent product can create it through the
 * matching factory; a put retains an already-created object. Container events report additions and removals. The
 * container coordinates lifecycle for elements it owns, including dependencies acquired by factory products. Choose
 * the shared OSGi container for bundle-managed server applications and an independent initialized container for a
 * standalone application. Do not deactivate the shared plugin container.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_ManagedContainer
{
  /**
   * Factories, Product Groups, and Lookup
   * <p>
   * Register an {@link IFactory factory} before asking a raw, unprepared container to create its product. An
   * {@link IManagedContainer#registerFactory(IFactory) registered factory} is selected by its own product group and type; an
   * OSGi application's declarative factories are contributed by bundles and become available through the plugin
   * container. Use {@link IManagedContainer#getProductGroups()} and {@link IManagedContainer#getFactoryTypes(String)}
   * for discovery, and use component constants rather than literal keys. {@code getElement} resolves a factory,
   * creates the product if necessary, retains it under the group/type/description key, and activates lifecycle
   * products. {@code getElementOrNull} only returns an existing element; {@code putElement} retains a caller-created
   * element. A factory can obtain dependencies from the same container, which makes container ownership extend to the
   * assembled runtime graph.
   * <p>
   * Repositories use {@link RepositoryFactory#PRODUCT_GROUP} and are normally obtained with
   * {@link CDOServerUtil#getRepository(IManagedContainer, String)}. The factory creates a repository; the container
   * locates and owns the registered instance. The short lookup form is shown in
   * {@link #findRepository(IManagedContainer, String) RepositoryLookup.java}.
   */
  public class FactoriesAndElements
  {
  }

  /**
   * Lifecycle and Configuration
   * <p>
   * {@link ContainerUtil#createContainer()} returns an empty, inactive {@code ManagedContainer}; it has no CDO/Net4j
   * factory contributions until the application registers or prepares them. For ordinary standalone use,
   * {@link ContainerUtil#createInitializedContainer()} creates a distinct container, loads available platform and
   * declarative factory/element-processor contributions, initializes it, and activates it for immediate lookup.
   * “Initialized” does not mean that the application's repositories or acceptors already exist: factories are ready,
   * while products are created as requested. The convenience method is current; do not call deprecated
   * {@code ContainerUtil.prepareContainer}, {@code CDOServerUtil.prepareContainer}, or component-specific preparation
   * methods on its result. A deliberately raw container can instead be prepared through the supported OMBundle
   * mechanism and activated by its owner. In OSGi, the shared plugin container is managed globally.
   * <p>
   * Products created through the container are activated as they are created and stopped when removed or when the
   * owner deactivates. Products inserted with {@code putElement} are retained and become container-owned; do not
   * independently close a product after transferring ownership. For server XML, use the repository configurator
   * described in {@link Doc02_ServerApplicationAndStartup}, not generic container configuration. Deactivate an
   * application-owned container at shutdown so its elements and dependencies are released.
   */
  public class LifecycleAndConfiguration
  {
  }

  /**
   * Standalone Container Example
   * <p>
   * The initialized container is the owner of the acceptor created through it. The caller's work runs while that
   * acceptor is active; deactivating the container releases it even when the work fails.
   * {@link #withStandaloneAcceptor(String, Consumer) WithStandaloneAcceptor.java}
   */
  public class Examples
  {
  }

  /**
   * Creates an independent initialized container, starts a named TCP acceptor, runs server work, and deactivates the
   * container that owns the acceptor.
   *
   * @param endpoint the host-and-port description accepted by the TCP acceptor factory
   * @param serverWork application work performed while the acceptor is active
   * @snip
   */
  public void withStandaloneAcceptor(String endpoint, Consumer<ITCPAcceptor> serverWork)
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    try
    {
      ITCPAcceptor acceptor = TCPUtil.getAcceptor(container, endpoint);
      serverWork.accept(acceptor);
    }
    finally
    {
      container.deactivate();
    }
  }

  /**
   * Locates a repository that has already been registered in the supplied container.
   *
   * @snip
   */
  public IRepository findRepository(IManagedContainer container, String repositoryName)
  {
    return CDOServerUtil.getRepository(container, repositoryName);
  }
}
