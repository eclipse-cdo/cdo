/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.RepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;

/**
 * The Managed Container
 * <p>
 * An {@link IManagedContainer} is the CDO and Net4j registry for factories and named runtime elements. A factory is
 * addressed by its product group and type; an element also has a description. The container can create an element on
 * demand, retain one supplied by {@link IManagedContainer#putElement(String, String, String, Object)}, and notify
 * container listeners when elements are added or removed.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_ManagedContainer
{
  /**
   * Factories, Product Groups, and Lookup
   * <p>
   * Register an {@link IFactory factory} before asking the container to create its product. Use
   * {@link IManagedContainer#getProductGroups()} and {@link IManagedContainer#getFactoryTypes(String)} for discovery,
   * and use a component's factory constants rather than literal keys. Creation resolves the selected factory and its
   * dependencies; lookup can activate the resulting lifecycle object. Repositories use
   * {@link RepositoryFactory#PRODUCT_GROUP} and are normally obtained with {@link CDOServerUtil#getRepository(IManagedContainer, String)}.
   */
  public class FactoriesAndElements
  {
  }

  /**
   * Lifecycle and Configuration
   * <p>
   * A container is also a lifecycle object. Populate it before activation when possible, then let component activation
   * acquire dependencies. {@link IManagedContainer#loadElements(java.io.InputStream)} supports container
   * configuration; server XML configuration is normally handled by the repository configurator instead. Avoid sharing
   * mutable application objects under ambiguous descriptions, and remove or deactivate owned elements during teardown.
   */
  public class LifecycleAndConfiguration
  {
  }

  /**
   * Repository Lookup Example
   * <p>
   * This lookup reuses a repository already registered under its repository-factory identity. The returned repository
   * remains container-owned; an extension should remove listeners and handlers that it added before shutdown.
   * {@link #findRepository(IManagedContainer, String) FindRepository.java}
   */
  public class Examples
  {
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
