/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IContainerConfig;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;
import org.eclipse.emf.cdo.tests.model5.Model5Factory;
import org.eclipse.emf.cdo.tests.model5.Model5Package;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTest extends AbstractOMTest implements IConstants
{
  public ConfigTest()
  {
  }

  private IScenario scenario;

  public synchronized IScenario getScenario()
  {
    if (scenario == null)
    {
      setScenario(getDefaultScenario());
    }

    return scenario;
  }

  public synchronized void setScenario(IScenario scenario)
  {
    this.scenario = scenario;
    if (scenario != null)
    {
      scenario.setCurrentTest(this);
    }
  }

  private Properties homeProperties;

  public synchronized Properties getHomeProperties()
  {
    if (homeProperties == null)
    {
      homeProperties = new Properties();
      String home = System.getProperty("user.home");
      if (home != null)
      {
        File file = new File(home, ".cdo_config_test.properties");
        if (file.exists())
        {
          FileInputStream stream = IOUtil.openInputStream(file);

          try
          {
            homeProperties.load(stream);
          }
          catch (IOException ex)
          {
            throw WrappedException.wrap(ex);
          }
          finally
          {
            IOUtil.close(stream);
          }
        }
      }
    }

    return homeProperties;
  }

  public synchronized void setHomeProperties(Properties homeProperties)
  {
    this.homeProperties = homeProperties;
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Container //////////////////////////////////////

  /**
   *@category Container
   */
  public IContainerConfig getContainerConfig()
  {
    return getScenario().getContainerConfig();
  }

  /**
   *@category Container
   */
  public boolean hasClientContainer()
  {
    return getContainerConfig().hasClientContainer();
  }

  /**
   *@category Container
   */
  public boolean hasServerContainer()
  {
    return getContainerConfig().hasServerContainer();
  }

  /**
   *@category Container
   */
  public IManagedContainer getClientContainer()
  {
    return getContainerConfig().getClientContainer();
  }

  /**
   *@category Container
   */
  public IManagedContainer getServerContainer()
  {
    return getContainerConfig().getServerContainer();
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Repository /////////////////////////////////////

  /**
   *@category Repository
   */
  public IRepositoryConfig getRepositoryConfig()
  {
    return getScenario().getRepositoryConfig();
  }

  /**
   *@category Repository
   */
  public Map<String, String> getRepositoryProperties()
  {
    return getRepositoryConfig().getRepositoryProperties();
  }

  /**
   *@category Repository
   */
  public IRepository getRepository(String name)
  {
    return getRepositoryConfig().getRepository(name);
  }

  /**
   *@category Repository
   */
  public IRepository getRepository()
  {
    return getRepositoryConfig().getRepository(IRepositoryConfig.REPOSITORY_NAME);
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Session ////////////////////////////////////////

  /**
   *@category Session
   */
  public ISessionConfig getSessionConfig()
  {
    return getScenario().getSessionConfig();
  }

  /**
   *@category Session
   */
  public void startTransport() throws Exception
  {
    getSessionConfig().startTransport();
  }

  /**
   *@category Session
   */
  public void stopTransport() throws Exception
  {
    getSessionConfig().stopTransport();
  }

  /**
   *@category Session
   */
  public IAcceptor getAcceptor()
  {
    return getSessionConfig().getAcceptor();
  }

  /**
   *@category Session
   */
  public IConnector getConnector()
  {
    return getSessionConfig().getConnector();
  }

  /**
   *@category Session
   */
  public CDOSession openMangoSession()
  {
    return getSessionConfig().openMangoSession();
  }

  /**
   *@category Session
   */
  public CDOSession openModel1Session()
  {
    return getSessionConfig().openModel1Session();
  }

  /**
   *@category Session
   */
  public CDOSession openModel2Session()
  {
    return getSessionConfig().openModel2Session();
  }

  /**
   *@category Session
   */
  public CDOSession openModel3Session()
  {
    return getSessionConfig().openModel3Session();
  }

  /**
   *@category Session
   */
  public CDOSession openSession(EPackage ePackage)
  {
    return getSessionConfig().openSession(ePackage);
  }

  /**
   *@category Session
   */
  public CDOSession openSession(String repositoryName)
  {
    return getSessionConfig().openSession(repositoryName);
  }

  /**
   *@category Session
   */
  public CDOSession openSession()
  {
    return getSessionConfig().openSession();
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Model //////////////////////////////////////////

  /**
   *@category Model
   */
  public IModelConfig getModelConfig()
  {
    return getScenario().getModelConfig();
  }

  /**
   *@category Model
   */
  public MangoFactory getMangoFactory()
  {
    return getModelConfig().getMangoFactory();
  }

  /**
   *@category Model
   */
  public MangoPackage getMangoPackage()
  {
    return getModelConfig().getMangoPackage();
  }

  /**
   *@category Model
   */
  public Model1Factory getModel1Factory()
  {
    return getModelConfig().getModel1Factory();
  }

  /**
   *@category Model
   */
  public Model1Package getModel1Package()
  {
    return getModelConfig().getModel1Package();
  }

  /**
   *@category Model
   */
  public Model2Factory getModel2Factory()
  {
    return getModelConfig().getModel2Factory();
  }

  /**
   *@category Model
   */
  public Model2Package getModel2Package()
  {
    return getModelConfig().getModel2Package();
  }

  /**
   *@category Model
   */
  public Model3Factory getModel3Factory()
  {
    return getModelConfig().getModel3Factory();
  }

  /**
   *@category Model
   */
  public Model3Package getModel3Package()
  {
    return getModelConfig().getModel3Package();
  }

  /**
   *@category Model
   */
  public SubpackageFactory getModel3SubpackageFactory()
  {
    return getModelConfig().getModel3SubPackageFactory();
  }

  /**
   *@category Model
   */
  public SubpackagePackage getModel3SubpackagePackage()
  {
    return getModelConfig().getModel3SubPackagePackage();
  }

  /**
   *@category Model
   */
  public model4Factory getModel4Factory()
  {
    return getModelConfig().getModel4Factory();
  }

  /**
   *@category Model
   */
  public model4Package getModel4Package()
  {
    return getModelConfig().getModel4Package();
  }

  /**
   *@category Model
   */
  public model4interfacesPackage getModel4InterfacesPackage()
  {
    return getModelConfig().getModel4InterfacesPackage();
  }

  /**
   *@category Model
   */
  public Model5Factory getModel5Factory()
  {
    return getModelConfig().getModel5Factory();
  }

  /**
   *@category Model
   */
  public Model5Package getModel5Package()
  {
    return getModelConfig().getModel5Package();
  }

  // /////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////

  public Map<String, Object> getTestProperties()
  {
    return new HashMap<String, Object>();
  }

  public boolean isValid()
  {
    return true;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}.{1} [{2}, {3}, {4}, {5}]", getClass().getSimpleName(), getName(),
        getContainerConfig(), getRepositoryConfig(), getSessionConfig(), getModelConfig());
  }

  protected void skipConfig(Config config)
  {
    skipTest(getContainerConfig() == config || getRepositoryConfig() == config || getSessionConfig() == config
        || getModelConfig() == config);
  }

  protected void skipUnlessConfig(Config config)
  {
    skipTest(getContainerConfig() != config && getRepositoryConfig() != config && getSessionConfig() != config
        && getModelConfig() != config);
  }

  protected void clearCache(IRevisionManager revisionManager)
  {
    ((RevisionManager)revisionManager).getCache().clear();
  }

  public void restartScenario() throws Exception
  {
    IOUtil.OUT().println("RESTARTING SCENARIO");
    stopTransport();

    IScenario scenario = getScenario();
    scenario.tearDown();
    scenario.setUp();

    startTransport();
    IOUtil.OUT().println("RESTARTING SCENARIO - FINISHED");
  }

  protected IScenario getDefaultScenario()
  {
    IScenario scenario = Scenario.load();
    if (scenario == null)
    {
      scenario = Scenario.getDefault();
    }

    return scenario;
  }

  @Override
  public void setUp() throws Exception
  {
    getScenario();
    super.setUp();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    getScenario().setUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    getScenario().tearDown();
    super.doTearDown();
  }
}
