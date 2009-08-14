/**
 * Copyright (c) 2004 - 2009 Andre Dietisheim (Bern, Switzerland) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.revisioncache;

import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.server.IRepository;
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
 * @author Andre Dietisheim
 */
public class SessionFactory extends Lifecycle
{
  private static final String CONNECTOR_NAME = "server1";

  private static final String REPO_NAME = "repo1";

  private CDOSession session;

  private IManagedContainer serverContainer;

  private IJVMAcceptor acceptor;

  private IMEMStore store;

  private IRepository repository;

  private IManagedContainer clientContainer;

  private IJVMConnector connector;

  private CDOSessionConfiguration configuration;

  SessionFactory()
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    createRepository(REPO_NAME);
    configuration = prepareSession();
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

  protected CDOSession openSession(EPackage... ePackages)
  {
    CheckUtil.checkNull(configuration, "session configuration is null, factory is not activated.");

    CDOSession session = configuration.openSession();
    for (EPackage ePackage : ePackages)
    {
      session.getPackageRegistry().putEPackage(ePackage);
    }
    return session;
  }

  private CDOSessionConfiguration prepareSession()
  {
    clientContainer = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(clientContainer);
    JVMUtil.prepareContainer(clientContainer);
    CDONet4jUtil.prepareContainer(clientContainer);
    LifecycleUtil.activate(clientContainer);

    // Create configuration
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    connector = JVMUtil.getConnector(clientContainer, CONNECTOR_NAME);
    configuration.setConnector(connector);
    configuration.setRepositoryName(REPO_NAME);
    return configuration;
  }

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

  public CDOSession getSession()
  {
    return session;
  }
}
