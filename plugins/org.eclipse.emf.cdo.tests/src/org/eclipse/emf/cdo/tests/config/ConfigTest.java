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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRevisionManager;
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

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.AbstractOMTest;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EPackage;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTest extends AbstractOMTest implements ConfigConstants, ContainerProvider,
    RepositoryProvider, SessionProvider, ModelProvider
{
  public ConfigTest()
  {
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Container //////////////////////////////////////

  /**
   *@category Container
   */
  public static final ContainerConfig DEFAULT_CONTAINER_CONFIG = COMBINED;

  /**
   *@category Container
   */
  private ContainerConfig containerConfig;

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
  public boolean hasClientContainer()
  {
    return containerConfig.hasClientContainer();
  }

  /**
   *@category Container
   */
  public boolean hasServerContainer()
  {
    return containerConfig.hasServerContainer();
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
  public static final RepositoryConfig DEFAULT_REPOSITORY_CONFIG = MEM;

  /**
   *@category Repository
   */
  private RepositoryConfig repositoryConfig;

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
  public static final SessionConfig DEFAULT_SESSION_CONFIG = TCP;

  /**
   *@category Session
   */
  private SessionConfig sessionConfig;

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
  public void startTransport() throws Exception
  {
    sessionConfig.startTransport();
  }

  /**
   *@category Session
   */
  public void stopTransport() throws Exception
  {
    sessionConfig.stopTransport();
  }

  /**
   *@category Session
   */
  public IAcceptor getAcceptor()
  {
    return sessionConfig.getAcceptor();
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
  public CDOSession openEagerSession()
  {
    return sessionConfig.openEagerSession();
  }

  /**
   *@category Session
   */
  public CDOSession openLazySession()
  {
    return sessionConfig.openLazySession();
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
  public static final ModelConfig DEFAULT_MODEL_CONFIG = NATIVE;

  /**
   *@category Model
   */
  private ModelConfig modelConfig;

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
    return MessageFormat.format("{0}.{1} [{2}, {3}, {4}, {5}]", getClass().getSimpleName(), getName(), containerConfig,
        repositoryConfig, sessionConfig, modelConfig);
  }

  protected ContainerConfig filterContainerConfig(ContainerConfig config)
  {
    if (config == null)
    {
      config = DEFAULT_CONTAINER_CONFIG;
    }

    return config;
  }

  protected RepositoryConfig filterRepositoryConfig(RepositoryConfig config)
  {
    if (config == null)
    {
      config = DEFAULT_REPOSITORY_CONFIG;
    }

    return config;
  }

  protected SessionConfig filterSessionConfig(SessionConfig config)
  {
    if (config == null)
    {
      config = DEFAULT_SESSION_CONFIG;
    }

    return config;
  }

  protected ModelConfig filterModelConfig(ModelConfig config)
  {
    if (config == null)
    {
      config = DEFAULT_MODEL_CONFIG;
    }

    return config;
  }

  protected void skipConfig(Config config)
  {
    skipTest(containerConfig == config || repositoryConfig == config || sessionConfig == config
        || modelConfig == config);
  }

  protected void skipUnlessConfig(Config config)
  {
    skipTest(containerConfig != config && repositoryConfig != config && sessionConfig != config
        && modelConfig != config);
  }

  protected void removeAllRevisions(IRevisionManager revisionManager)
  {
    CDORevisionCache cache = ((RevisionManager)revisionManager).getCache();
    for (CDORevision revision : cache.getRevisions())
    {
      cache.removeRevision(revision.getID(), revision.getVersion());
    }
  }

  public void restartConfigs() throws Exception
  {
    IOUtil.OUT().println("RESTARTING CONFIGURATIONS");
    stopTransport();
    tearDownConfigs();
    setupConfigs();
    startTransport();
    IOUtil.OUT().println("RESTARTING CONFIGURATIONS - FINISHED");
  }

  @Override
  public void setUp() throws Exception
  {
    containerConfig = filterContainerConfig(containerConfig);
    repositoryConfig = filterRepositoryConfig(repositoryConfig);
    sessionConfig = filterSessionConfig(sessionConfig);
    modelConfig = filterModelConfig(modelConfig);
    super.setUp();
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    setupConfigs();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    tearDownConfigs();
    super.doTearDown();
  }

  private void setupConfigs() throws Exception
  {
    setUpConfig(containerConfig);
    setUpConfig(repositoryConfig);
    setUpConfig(sessionConfig);
    setUpConfig(modelConfig);
  }

  private void setUpConfig(Config config) throws Exception
  {
    config.setCurrentTest(this);
    config.setUp();
  }

  private void tearDownConfigs() throws Exception
  {
    tearDownConfig(modelConfig);
    tearDownConfig(sessionConfig);
    tearDownConfig(repositoryConfig);
    tearDownConfig(containerConfig);
  }

  private void tearDownConfig(Config config) throws Exception
  {
    config.tearDown();
    config.setCurrentTest(null);
  }
}
