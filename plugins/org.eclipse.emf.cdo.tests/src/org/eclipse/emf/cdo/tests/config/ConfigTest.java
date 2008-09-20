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
package org.eclipse.emf.cdo.tests.config;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.server.IRepository;
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
import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.ecore.EPackage;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTest extends AbstractOMTest implements ContainerProvider, RepositoryProvider,
    SessionProvider, ModelProvider
{
  private ContainerConfig containerConfig;

  private RepositoryConfig repositoryConfig;

  private SessionConfig sessionConfig;

  private ModelConfig modelConfig;

  public ConfigTest()
  {
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Container //////////////////////////////////////

  /**
   *@category Container
   */
  public ContainerConfig getContainerConfig()
  {
    return containerConfig;
  }

  /**
   *@category Container
   */
  public void setContainerConfig(ContainerConfig containerConfig)
  {
    this.containerConfig = containerConfig;
  }

  /**
   *@category Container
   */
  public IManagedContainer getClientContainer()
  {
    return containerConfig.getClientContainer();
  }

  /**
   *@category Container
   */
  public IManagedContainer getServerContainer()
  {
    return containerConfig.getServerContainer();
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Repository /////////////////////////////////////

  /**
   *@category Repository
   */
  public RepositoryConfig getRepositoryConfig()
  {
    return repositoryConfig;
  }

  /**
   *@category Repository
   */
  public void setRepositoryConfig(RepositoryConfig repositoryConfig)
  {
    this.repositoryConfig = repositoryConfig;
  }

  /**
   *@category Repository
   */
  public Map<String, String> getRepositoryProperties()
  {
    return repositoryConfig.getRepositoryProperties();
  }

  /**
   *@category Repository
   */
  public IRepository getRepository(String name)
  {
    return repositoryConfig.getRepository(name);
  }

  /**
   *@category Repository
   */
  public IRepository getRepository()
  {
    return repositoryConfig.getRepository(REPOSITORY_NAME);
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Session ////////////////////////////////////////

  /**
   *@category Session
   */
  public SessionConfig getSessionConfig()
  {
    return sessionConfig;
  }

  /**
   *@category Session
   */
  public void setSessionConfig(SessionConfig sessionConfig)
  {
    this.sessionConfig = sessionConfig;
  }

  /**
   *@category Session
   */
  public IConnector getConnector()
  {
    return sessionConfig.getConnector();
  }

  /**
   *@category Session
   */
  public CDOSession openMangoSession()
  {
    return sessionConfig.openMangoSession();
  }

  /**
   *@category Session
   */
  public CDOSession openModel1Session()
  {
    return sessionConfig.openModel1Session();
  }

  /**
   *@category Session
   */
  public CDOSession openModel2Session()
  {
    return sessionConfig.openModel2Session();
  }

  /**
   *@category Session
   */
  public CDOSession openModel3Session()
  {
    return sessionConfig.openModel3Session();
  }

  /**
   *@category Session
   */
  public CDOSession openSession(EPackage ePackage)
  {
    return sessionConfig.openSession(ePackage);
  }

  /**
   *@category Session
   */
  public CDOSession openSession(String repositoryName)
  {
    return sessionConfig.openSession(repositoryName);
  }

  /**
   *@category Session
   */
  public CDOSession openSession()
  {
    return sessionConfig.openSession();
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Model //////////////////////////////////////////

  /**
   *@category Model
   */
  public ModelConfig getModelConfig()
  {
    return modelConfig;
  }

  /**
   *@category Model
   */
  public void setModelConfig(ModelConfig modelConfig)
  {
    this.modelConfig = modelConfig;
  }

  /**
   *@category Model
   */
  public MangoFactory getMangoFactory()
  {
    return modelConfig.getMangoFactory();
  }

  /**
   *@category Model
   */
  public MangoPackage getMangoPackage()
  {
    return modelConfig.getMangoPackage();
  }

  /**
   *@category Model
   */
  public Model1Factory getModel1Factory()
  {
    return modelConfig.getModel1Factory();
  }

  /**
   *@category Model
   */
  public Model1Package getModel1Package()
  {
    return modelConfig.getModel1Package();
  }

  /**
   *@category Model
   */
  public Model2Factory getModel2Factory()
  {
    return modelConfig.getModel2Factory();
  }

  /**
   *@category Model
   */
  public Model2Package getModel2Package()
  {
    return modelConfig.getModel2Package();
  }

  /**
   *@category Model
   */
  public Model3Factory getModel3Factory()
  {
    return modelConfig.getModel3Factory();
  }

  /**
   *@category Model
   */
  public Model3Package getModel3Package()
  {
    return modelConfig.getModel3Package();
  }

  /**
   *@category Model
   */
  public model4Factory getModel4Factory()
  {
    return modelConfig.getModel4Factory();
  }

  /**
   *@category Model
   */
  public model4Package getModel4Package()
  {
    return modelConfig.getModel4Package();
  }

  /**
   *@category Model
   */
  public model4interfacesPackage getModel4InterfacesPackage()
  {
    return modelConfig.getModel4InterfacesPackage();
  }

  // /////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////

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

  protected void skipConfig(Config config)
  {
    skipTest(modelConfig == config);
    skipTest(sessionConfig == config);
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    setUpConfig(containerConfig);
    setUpConfig(repositoryConfig);
    setUpConfig(sessionConfig);
    setUpConfig(modelConfig);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    tearDownConfig(modelConfig);
    tearDownConfig(sessionConfig);
    tearDownConfig(repositoryConfig);
    tearDownConfig(containerConfig);
    super.doTearDown();
  }

  private void setUpConfig(Config config) throws Exception
  {
    config.setCurrentTest(this);
    config.setUp();
  }

  private void tearDownConfig(Config config) throws Exception
  {
    config.tearDown();
    config.setCurrentTest(null);
  }
}
