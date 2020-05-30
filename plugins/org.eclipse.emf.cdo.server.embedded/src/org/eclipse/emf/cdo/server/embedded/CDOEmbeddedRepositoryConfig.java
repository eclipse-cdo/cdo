/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.embedded;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.embedded.EmbeddedRepository;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.6
 */
public abstract class CDOEmbeddedRepositoryConfig extends Lifecycle
{
  private static final String JVM_ACCEPTOR_PREFIX = "cdo_embedded_repo_";

  private final String repositoryName;

  private EmbeddedRepository repository;

  public CDOEmbeddedRepositoryConfig(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  /**
   * @since 4.8
   */
  public final String getRepositoryName()
  {
    return repositoryName;
  }

  public final IRepository getRepository()
  {
    return repository;
  }

  public final CDONet4jSession openClientSession()
  {
    return repository.openClientSession();
  }

  /**
   * Subclasses may override.
   */
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  /**
   * Subclasses may override.
   */
  public void initPackages(IRepository repository, List<EPackage> packages)
  {
    for (String nsURI : new HashSet<>(EPackage.Registry.INSTANCE.keySet()))
    {
      if (isInitialPackage(repository, nsURI))
      {
        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsURI);
        packages.add(ePackage);
      }
    }
  }

  /**
   * Subclasses may override.
   */
  public boolean isInitialPackage(IRepository repository, String nsURI)
  {
    return false;
  }

  /**
   * Subclasses may override.
   */
  public void afterFirstStart(IRepository repository)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  public void afterReStart(IRepository repository)
  {
    // Do nothing.
  }

  /**
   * Subclasses may override.
   */
  public void modifySession(IRepository repository, CDONet4jSession session)
  {
    session.options().setCommitTimeout(Integer.MAX_VALUE);
  }

  /**
   * Subclasses may override.
   */
  public CDONet4jSessionConfiguration createSessionConfiguration(IConnector connector, CDOBranchManager branchManager, CDORevisionManager revisionManager)
  {
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);
    configuration.setSignalTimeout(Integer.MAX_VALUE);
    configuration.setBranchManager(branchManager);
    configuration.setRevisionManager(revisionManager);
    return configuration;
  }

  /**
   * Subclasses may override.
   */
  public IAcceptor createAcceptor(IManagedContainer container)
  {
    return JVMUtil.getAcceptor(container, JVM_ACCEPTOR_PREFIX + repositoryName);
  }

  /**
   * Subclasses may override.
   */
  public IConnector createConnector(IManagedContainer container)
  {
    return JVMUtil.getConnector(container, JVM_ACCEPTOR_PREFIX + repositoryName);
  }

  public abstract IStore createStore(IManagedContainer container);

  public abstract void initProperties(IManagedContainer container, Map<String, String> properties);

  protected void activateRepository(IRepository repository)
  {
    LifecycleUtil.activate(repository);
  }

  protected void deactivateRepository()
  {
    LifecycleUtil.deactivate(repository);
    repository = null;
  }

  @Override
  protected void doActivate() throws Exception
  {
    // Initialize container.
    IManagedContainer container = getContainer();

    // Initialize store.
    IStore store = createStore(container);

    // Initialize properties.
    Map<String, String> properties = new HashMap<>();
    properties.put(IRepository.Props.OVERRIDE_UUID, "");
    initProperties(container, properties);

    repository = new EmbeddedRepository(this);
    ((InternalRepository)repository).setContainer(container);
    ((InternalRepository)repository).setName(repositoryName);
    ((InternalRepository)repository).setStore((InternalStore)store);
    ((InternalRepository)repository).setProperties(properties);

    activateRepository(repository);

    repository.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        CDOEmbeddedRepositoryConfig.this.deactivate();
      }
    });
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    deactivateRepository();
  }
}
