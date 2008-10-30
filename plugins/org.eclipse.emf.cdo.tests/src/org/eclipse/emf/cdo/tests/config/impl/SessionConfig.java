/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EPackage;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class SessionConfig extends Config implements ISessionConfig
{
  public static final SessionConfig[] CONFIGS = { TCP.INSTANCE, JVM.INSTANCE };

  private static final long serialVersionUID = 1L;

  public SessionConfig(String name)
  {
    super(name);
  }

  public void startTransport() throws Exception
  {
    IAcceptor acceptor = getAcceptor();
    LifecycleUtil.activate(acceptor);

    IConnector connector = getConnector();
    LifecycleUtil.activate(connector);
  }

  public void stopTransport() throws Exception
  {
    ConfigTest currentTest = getCurrentTest();
    if (currentTest.hasClientContainer())
    {
      IConnector connector = getConnector();
      LifecycleUtil.deactivate(connector);
    }

    if (currentTest.hasServerContainer())
    {
      IAcceptor acceptor = getAcceptor();
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public CDOSession openMangoSession()
  {
    return openSession(getCurrentTest().getMangoPackage());
  }

  public CDOSession openModel1Session()
  {
    return openSession(getCurrentTest().getModel1Package());
  }

  public CDOSession openModel2Session()
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getCurrentTest().getModel2Package());
    return session;
  }

  public CDOSession openModel3Session()
  {
    return openSession(getCurrentTest().getModel3Package());
  }

  public CDOSession openEagerSession()
  {
    CDOSessionConfiguration configuration = createSessionConfiguration(IRepositoryConfig.REPOSITORY_NAME);
    configuration.setEagerPackageRegistry();
    return configuration.openSession();
  }

  public CDOSession openLazySession()
  {
    CDOSessionConfiguration configuration = createSessionConfiguration(IRepositoryConfig.REPOSITORY_NAME);
    configuration.setLazyPackageRegistry();
    return configuration.openSession();
  }

  public CDOSession openSession(EPackage ePackage)
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(ePackage);
    return session;
  }

  public CDOSession openSession(String repositoryName)
  {
    CDOSessionConfiguration configuration = createSessionConfiguration(repositoryName);
    return configuration.openSession();
  }

  public CDOSession openSession()
  {
    return openSession(IRepositoryConfig.REPOSITORY_NAME);
  }

  @Override
  public void tearDown() throws Exception
  {
    stopTransport();
    super.tearDown();
  }

  private CDOSessionConfiguration createSessionConfiguration(String repositoryName)
  {
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(repositoryName);
    return configuration;
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends SessionConfig
  {
    public static final String NAME = "TCP";

    public static final TCP INSTANCE = new TCP();

    public static final String CONNECTOR_HOST = "localhost";

    private static final long serialVersionUID = 1L;

    public TCP()
    {
      super(NAME);
    }

    public IAcceptor getAcceptor()
    {
      return TCPUtil.getAcceptor(getCurrentTest().getServerContainer(), null);
    }

    public IConnector getConnector()
    {
      return TCPUtil.getConnector(getCurrentTest().getClientContainer(), CONNECTOR_HOST);
    }

    @Override
    public void setUp() throws Exception
    {
      super.setUp();
      TCPUtil.prepareContainer(getCurrentTest().getClientContainer());
      TCPUtil.prepareContainer(getCurrentTest().getServerContainer());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class JVM extends SessionConfig
  {
    public static final String NAME = "JVM";

    public static final JVM INSTANCE = new JVM();

    public static final String ACCEPTOR_NAME = "default";

    private static final long serialVersionUID = 1L;

    public JVM()
    {
      super(NAME);
    }

    public IAcceptor getAcceptor()
    {
      return JVMUtil.getAcceptor(getCurrentTest().getServerContainer(), ACCEPTOR_NAME);
    }

    public IConnector getConnector()
    {
      return JVMUtil.getConnector(getCurrentTest().getClientContainer(), ACCEPTOR_NAME);
    }

    @Override
    public void setUp() throws Exception
    {
      super.setUp();
      JVMUtil.prepareContainer(getCurrentTest().getClientContainer());
      JVMUtil.prepareContainer(getCurrentTest().getServerContainer());
    }

    @Override
    public boolean isValid(Set<IConfig> configs)
    {
      return !configs.contains(ContainerConfig.Separated.INSTANCE);
    }
  }
}
