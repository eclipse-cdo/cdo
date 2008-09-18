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
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EPackage;

import java.text.MessageFormat;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTest extends TestCase implements ContainerProvider, SessionProvider, ModelProvider
{
  private ContainerConfig containerConfig;

  private SessionConfig sessionConfig;

  private ModelConfig modelTypeConfig;

  public ConfigTest()
  {
  }

  public ConfigTest(String name)
  {
    super(name);
  }

  public ContainerConfig getContainerConfig()
  {
    return containerConfig;
  }

  public void setContainerConfig(ContainerConfig containerConfig)
  {
    this.containerConfig = containerConfig;
  }

  public SessionConfig getSessionConfig()
  {
    return sessionConfig;
  }

  public void setSessionConfig(SessionConfig sessionConfig)
  {
    this.sessionConfig = sessionConfig;
  }

  public ModelConfig getModelTypeConfig()
  {
    return modelTypeConfig;
  }

  public void setModelTypeConfig(ModelConfig modelTypeConfig)
  {
    this.modelTypeConfig = modelTypeConfig;
  }

  public IManagedContainer getClientContainer()
  {
    return containerConfig.getClientContainer();
  }

  public IManagedContainer getServerContainer()
  {
    return containerConfig.getServerContainer();
  }

  public IConnector getConnector()
  {
    return sessionConfig.getConnector();
  }

  public CDOSession openMangoSession()
  {
    return sessionConfig.openMangoSession();
  }

  public CDOSession openModel1Session()
  {
    return sessionConfig.openModel1Session();
  }

  public CDOSession openModel2Session()
  {
    return sessionConfig.openModel2Session();
  }

  public CDOSession openModel3Session()
  {
    return sessionConfig.openModel3Session();
  }

  public CDOSession openSession(EPackage package1)
  {
    return sessionConfig.openSession(package1);
  }

  public MangoFactory getMangoFactory()
  {
    return modelTypeConfig.getMangoFactory();
  }

  public MangoPackage getMangoPackage()
  {
    return modelTypeConfig.getMangoPackage();
  }

  public Model1Factory getModel1Factory()
  {
    return modelTypeConfig.getModel1Factory();
  }

  public Model1Package getModel1Package()
  {
    return modelTypeConfig.getModel1Package();
  }

  public Model2Factory getModel2Factory()
  {
    return modelTypeConfig.getModel2Factory();
  }

  public Model2Package getModel2Package()
  {
    return modelTypeConfig.getModel2Package();
  }

  public Model3Factory getModel3Factory()
  {
    return modelTypeConfig.getModel3Factory();
  }

  public Model3Package getModel3Package()
  {
    return modelTypeConfig.getModel3Package();
  }

  public model4Factory getModel4Factory()
  {
    return modelTypeConfig.getModel4Factory();
  }

  public model4Package getModel4Package()
  {
    return modelTypeConfig.getModel4Package();
  }

  public model4interfacesPackage getModel4InterfacesPackage()
  {
    return modelTypeConfig.getModel4InterfacesPackage();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}[{1}, {2}, {3}]", getName(), containerConfig, sessionConfig, modelTypeConfig);
  }

  @Override
  protected void runTest() throws Throwable
  {
    try
    {
      super.runTest();
    }
    catch (SkipTestException ex)
    {
      OM.LOG.info("Skipped test: " + this);
    }
    catch (Throwable t)
    {
      t.printStackTrace(IOUtil.OUT());
      throw t;
    }
  }

  protected void skipConfig(Config config)
  {
    skipTest(modelTypeConfig == config);
    skipTest(sessionConfig == config);
  }

  protected static void skipTest(boolean skip)
  {
    if (skip)
    {
      throw new SkipTestException();
    }
  }

  protected static void skipTest()
  {
    skipTest(true);
  }

  /**
   * @author Eike Stepper
   */
  private static final class SkipTestException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
  }
}
