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
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.eclipse.emf.ecore.EPackage;

import java.text.MessageFormat;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTest extends TestCase implements ContainerProvider, SessionProvider, ModelProvider
{
  private ContainerConfig containerConfig;

  private RepositoryConfig repositoryConfig;

  private SessionConfig sessionConfig;

  private ModelConfig modelConfig;

  private static boolean consoleEnabled;

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

  public RepositoryConfig getRepositoryConfig()
  {
    return repositoryConfig;
  }

  public void setRepositoryConfig(RepositoryConfig repositoryConfig)
  {
    this.repositoryConfig = repositoryConfig;
  }

  public SessionConfig getSessionConfig()
  {
    return sessionConfig;
  }

  public void setSessionConfig(SessionConfig sessionConfig)
  {
    this.sessionConfig = sessionConfig;
  }

  public ModelConfig getModelConfig()
  {
    return modelConfig;
  }

  public void setModelConfig(ModelConfig modelConfig)
  {
    this.modelConfig = modelConfig;
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
    return modelConfig.getMangoFactory();
  }

  public MangoPackage getMangoPackage()
  {
    return modelConfig.getMangoPackage();
  }

  public Model1Factory getModel1Factory()
  {
    return modelConfig.getModel1Factory();
  }

  public Model1Package getModel1Package()
  {
    return modelConfig.getModel1Package();
  }

  public Model2Factory getModel2Factory()
  {
    return modelConfig.getModel2Factory();
  }

  public Model2Package getModel2Package()
  {
    return modelConfig.getModel2Package();
  }

  public Model3Factory getModel3Factory()
  {
    return modelConfig.getModel3Factory();
  }

  public Model3Package getModel3Package()
  {
    return modelConfig.getModel3Package();
  }

  public model4Factory getModel4Factory()
  {
    return modelConfig.getModel4Factory();
  }

  public model4Package getModel4Package()
  {
    return modelConfig.getModel4Package();
  }

  public model4interfacesPackage getModel4InterfacesPackage()
  {
    return modelConfig.getModel4InterfacesPackage();
  }

  public Map<String, String> getProperties()
  {
    return null;
  }

  public boolean isValid()
  {
    return true;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}.{1} [{2}, {3}, {4}, {5}]", getClass().getSimpleName(), getName(), containerConfig,
        repositoryConfig, sessionConfig, modelConfig);
  }

  @Override
  public final void setUp() throws Exception
  {
    IOUtil.OUT().println("*******************************************************");
    IOUtil.OUT().println(this);
    IOUtil.OUT().println("*******************************************************");

    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);
    enableConsole();

    super.setUp();
    setUpConfig(containerConfig);
    setUpConfig(repositoryConfig);
    setUpConfig(sessionConfig);
    setUpConfig(modelConfig);
    doSetUp();

    IOUtil.OUT().println();
    IOUtil.OUT().println("------------------------ START ------------------------");
  }

  @Override
  public final void tearDown() throws Exception
  {
    IOUtil.OUT().println("------------------------- END -------------------------");
    IOUtil.OUT().println();

    doTearDown();
    setUpConfig(modelConfig);
    setUpConfig(sessionConfig);
    setUpConfig(repositoryConfig);
    setUpConfig(containerConfig);
    super.tearDown();

    IOUtil.OUT().println();
    IOUtil.OUT().println();
  }

  protected void setUpConfig(Config config) throws Exception
  {
    config.setCurrentTest(this);
    config.setUp();
  }

  protected void tearDownConfig(Config config) throws Exception
  {
    config.tearDown();
    config.setCurrentTest(null);
  }

  protected void doSetUp() throws Exception
  {
  }

  protected void doTearDown() throws Exception
  {
  }

  protected void enableConsole()
  {
    if (!consoleEnabled)
    {
      PrintTraceHandler.CONSOLE.setShortContext(true);
      OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
      OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
      OMPlatform.INSTANCE.setDebugging(true);
      consoleEnabled = true;
    }
  }

  protected void disableConsole()
  {
    if (consoleEnabled)
    {
      consoleEnabled = false;
      OMPlatform.INSTANCE.setDebugging(false);
      OMPlatform.INSTANCE.removeTraceHandler(PrintTraceHandler.CONSOLE);
      OMPlatform.INSTANCE.removeLogHandler(PrintLogHandler.CONSOLE);
    }
  }

  protected static void msg(Object m)
  {
    if (consoleEnabled)
    {
      IOUtil.OUT().println("--> " + m);
    }
  }

  protected static void sleep(long millis)
  {
    ConcurrencyUtil.sleep(millis);
  }

  protected static void assertActive(Object object)
  {
    assertEquals(true, LifecycleUtil.isActive(object));
  }

  protected static void assertInactive(Object object)
  {
    assertEquals(false, LifecycleUtil.isActive(object));
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
      OM.LOG.info("Skipped " + this);
    }
    catch (Throwable t)
    {
      t.printStackTrace(IOUtil.OUT());
      throw t;
    }
  }

  protected void skipConfig(Config config)
  {
    skipTest(modelConfig == config);
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
