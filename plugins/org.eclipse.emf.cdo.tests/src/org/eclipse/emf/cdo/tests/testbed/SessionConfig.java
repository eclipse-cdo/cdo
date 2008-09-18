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
package org.eclipse.emf.cdo.tests.testbed;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.TCPUtil;

import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public abstract class SessionConfig extends Config implements SessionProvider
{
  public static final String DIMENSION = "session";

  public static final SessionConfig[] CONFIGS = { TCP.INSTANCE, JVM.INSTANCE };

  public SessionConfig(String name)
  {
    super(DIMENSION, name);
  }

  public CDOSession openMangoSession()
  {
    return openSession(getCurrentTest().getMangoPackage());
  }

  public CDOSession openModel1Session()
  {
    return openSession(getCurrentTest().getMangoPackage());
  }

  public CDOSession openModel2Session()
  {
    return openSession(getCurrentTest().getMangoPackage());
  }

  public CDOSession openModel3Session()
  {
    return openSession(getCurrentTest().getMangoPackage());
  }

  public CDOSession openSession(EPackage ePackage)
  {
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(REPOSITORY_NAME);
    return configuration.openSession();
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends SessionConfig
  {
    public static final String NAME = "TCP";

    public static final TCP INSTANCE = new TCP();

    public static final String CONNECTOR_HOST = "localhost";

    public TCP()
    {
      super(NAME);
    }

    public IConnector getConnector()
    {
      return TCPUtil.getConnector(getCurrentTest().getClientContainer(), CONNECTOR_HOST);
    }

    @Override
    protected void setUp() throws Exception
    {
      super.setUp();
      TCPUtil.prepareContainer(getCurrentTest().getClientContainer());
      TCPUtil.prepareContainer(getCurrentTest().getClientContainer());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class JVM extends SessionConfig
  {
    public static final String NAME = "JVM";

    public static final JVM INSTANCE = new JVM();

    public static final String CONNECTOR_NAME = "default";

    public JVM()
    {
      super(NAME);
    }

    public IConnector getConnector()
    {
      return JVMUtil.getConnector(getCurrentTest().getClientContainer(), CONNECTOR_NAME);
    }

    @Override
    protected void setUp() throws Exception
    {
      super.setUp();
      TCPUtil.prepareContainer(getCurrentTest().getClientContainer());
      TCPUtil.prepareContainer(getCurrentTest().getClientContainer());
    }
  }
}
