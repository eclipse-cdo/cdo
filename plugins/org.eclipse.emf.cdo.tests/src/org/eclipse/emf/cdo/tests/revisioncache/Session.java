/*
 * Copyright (c) 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.mem.IMEMStore;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;

/**
 * A class that holds a single CDOSession. It starts an IRepository that uses a memstore and a client that connects to
 * it by JVM transport
 *
 * @author Andre Dietisheim
 */
public class Session extends Lifecycle
{
  private static final String CONNECTOR_NAME = "server1";

  private static final String REPO_NAME = "repo1";

  private IManagedContainer serverContainer;

  private IJVMAcceptor acceptor;

  private IMEMStore store;

  private IRepository repository;

  private IManagedContainer clientContainer;

  private IJVMConnector connector;

  private CDOSession session;

  public Session()
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    createRepository(REPO_NAME);
    session = createSession();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    LifecycleUtil.deactivate(session);
    LifecycleUtil.deactivate(connector);
    LifecycleUtil.deactivate(repository);
    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(clientContainer);
    LifecycleUtil.deactivate(serverContainer);
  }

  /**
   * Returns the session held by this class and registers the given packages to it.
   *
   * @param ePackages
   *          the e packages
   * @return the session
   * @see CDOSession
   */
  public CDOSession getSession(EPackage... ePackages)
  {
    CDOSession session = getSession();
    for (EPackage ePackage : ePackages)
    {
      session.getPackageRegistry().putEPackage(ePackage);
    }

    return session;
  }

  /**
   * Returns the session this class holds.
   *
   * @return the session
   */
  public CDOSession getSession()
  {
    CheckUtil.checkState(session != null, "Session not activated!"); //$NON-NLS-1$
    return session;
  }

  private CDOSession createSession()
  {
    clientContainer = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(clientContainer);
    JVMUtil.prepareContainer(clientContainer);
    CDONet4jUtil.prepareContainer(clientContainer);
    LifecycleUtil.activate(clientContainer);

    // Create configuration
    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    connector = JVMUtil.getConnector(clientContainer, CONNECTOR_NAME);
    configuration.setConnector(connector);
    configuration.setRepositoryName(REPO_NAME);

    return configuration.openNet4jSession();
  }

  /**
   * Creates the repository for this session holder. It initializes the acceptor and store used within this session
   * holder
   *
   * @param repositoryName
   *          the repository name
   */
  protected void createRepository(String repositoryName)
  {
    serverContainer = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(serverContainer); // Register Net4j factories
    JVMUtil.prepareContainer(serverContainer);
    CDONet4jServerUtil.prepareContainer(serverContainer);
    LifecycleUtil.activate(serverContainer);

    acceptor = JVMUtil.getAcceptor(serverContainer, CONNECTOR_NAME);
    store = MEMStoreUtil.createMEMStore();
    repository = CDOServerUtil.createRepository(repositoryName, store, null);
    CDOServerUtil.addRepository(serverContainer, repository);
  }
}
