/*
 * Copyright (c) 2008-2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoChangeSets;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoCommitInfos;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoDurableLocking;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoHandleRevisions;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoLargeObjects;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoQueryXRefs;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoRawAccess;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig.Net4j;
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
import org.eclipse.emf.cdo.tests.model6.Model6Factory;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import junit.framework.TestResult;

/**
 * @author Eike Stepper
 */
public abstract class ConfigTest extends AbstractOMTest implements IConstants
{
  private static int dynamicPackageCounter;

  private transient int dynamicPackageNumber;

  private IScenario scenario;

  private boolean defaultScenario;

  private Properties homeProperties;

  private Map<String, Object> testProperties;

  public ConfigTest()
  {
  }

  public ExecutorService getExecutorService()
  {
    return Config.getExecutorService();
  }

  public boolean hasDefaultScenario()
  {
    return defaultScenario;
  }

  public synchronized IScenario getScenario()
  {
    if (scenario == null)
    {
      defaultScenario = true;
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

  public synchronized Map<String, Object> getTestProperties()
  {
    if (testProperties == null)
    {
      testProperties = new HashMap<>();
    }

    return testProperties;
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Repository /////////////////////////////////////

  /**
   * @category Repository
   */
  public IRepositoryConfig getRepositoryConfig()
  {
    IScenario scenario = getScenario();
    return scenario.getRepositoryConfig();
  }

  /**
   * @category Repository
   */
  public Map<String, String> getRepositoryProperties()
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig.getRepositoryProperties();
  }

  /**
   * @category Repository
   */
  public boolean hasServerContainer()
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig.hasServerContainer();
  }

  /**
   * @category Repository
   */
  public IManagedContainer getServerContainer()
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig.getServerContainer();
  }

  /**
   * @category Repository
   */
  public InternalRepository getRepository(String name, boolean activate)
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig.getRepository(name, activate);
  }

  /**
   * @category Repository
   */
  public InternalRepository getRepository(String name)
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig.getRepository(name);
  }

  /**
   * @category Repository
   */
  public InternalRepository getRepository()
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    return repositoryConfig.getRepository(IRepositoryConfig.REPOSITORY_NAME);
  }

  /**
   * @category Repository
   */
  public void registerRepository(IRepository repository)
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();
    repositoryConfig.registerRepository((InternalRepository)repository);
  }

  /**
   * @category Repository
   */
  public InternalRepository restartRepository()
  {
    return restartRepository(IRepositoryConfig.REPOSITORY_NAME);
  }

  /**
   * @category Repository
   */
  public InternalRepository restartRepository(String name)
  {
    IRepositoryConfig repositoryConfig = getRepositoryConfig();

    try
    {
      repositoryConfig.setRestarting(true);
      InternalRepository repo = getRepository(name);
      LifecycleUtil.deactivate(repo);
      return getRepository(name);
    }
    finally
    {
      repositoryConfig.setRestarting(false);
    }
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Session ////////////////////////////////////////

  /**
   * @category Session
   */
  public ISessionConfig getSessionConfig()
  {
    IScenario scenario = getScenario();
    return scenario.getSessionConfig();
  }

  /**
   * @category Session
   */
  public boolean hasClientContainer()
  {
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.hasClientContainer();
  }

  /**
   * @category Session
   */
  public IManagedContainer getClientContainer()
  {
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.getClientContainer();
  }

  /**
   * @category Session
   */
  public void startTransport() throws Exception
  {
    ISessionConfig sessionConfig = getSessionConfig();
    sessionConfig.startTransport();
  }

  /**
   * @category Session
   */
  public String getTransportType() throws Exception
  {
    ISessionConfig sessionConfig = getSessionConfig();
    if (sessionConfig instanceof Net4j)
    {
      Net4j net4j = (Net4j)sessionConfig;
      return net4j.getTransportType();
    }

    return null;
  }

  /**
   * @category Session
   */
  public void stopTransport() throws Exception
  {
    ISessionConfig sessionConfig = getSessionConfig();
    sessionConfig.stopTransport();
  }

  /**
   * @category Session
   */
  public String getURIProtocol()
  {
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.getURIProtocol();
  }

  /**
   * @category Session
   */
  public String getURIPrefix()
  {
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.getURIPrefix();
  }

  /**
   * @category Session
   */
  public CDOSession openSession()
  {
    determineCodeLink();
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.openSession();
  }

  /**
   * @category Session
   */
  public CDOSession openSession(String repositoryName)
  {
    determineCodeLink();
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.openSession(repositoryName);
  }

  /**
   * @category Session
   */
  public CDOSession openSession(CDOSessionConfiguration configuration)
  {
    determineCodeLink();
    ISessionConfig sessionConfig = getSessionConfig();
    return sessionConfig.openSession(configuration);
  }

  /**
   * @category Session
   */
  public CDOSession openSession(IPasswordCredentials credentials)
  {
    if (credentials != null)
    {
      IPasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(credentials);
      getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, credentialsProvider);
    }
    else
    {
      getTestProperties().remove(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER);
    }

    return openSession();
  }

  // /////////////////////////////////////////////////////////////////////////
  // //////////////////////// Model //////////////////////////////////////////

  /**
   * @category Model
   */
  public IModelConfig getModelConfig()
  {
    IScenario scenario = getScenario();
    return scenario.getModelConfig();
  }

  /**
   * @category Model
   */
  public MangoFactory getMangoFactory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getMangoFactory();
  }

  /**
   * @category Model
   */
  public MangoPackage getMangoPackage()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getMangoPackage();
  }

  /**
   * @category Model
   */
  public Model1Factory getModel1Factory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel1Factory();
  }

  /**
   * @category Model
   */
  public Model1Package getModel1Package()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel1Package();
  }

  /**
   * @category Model
   */
  public Model2Factory getModel2Factory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel2Factory();
  }

  /**
   * @category Model
   */
  public Model2Package getModel2Package()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel2Package();
  }

  /**
   * @category Model
   */
  public Model3Factory getModel3Factory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel3Factory();
  }

  /**
   * @category Model
   */
  public Model3Package getModel3Package()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel3Package();
  }

  /**
   * @category Model
   */
  public SubpackageFactory getModel3SubpackageFactory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel3SubPackageFactory();
  }

  /**
   * @category Model
   */
  public SubpackagePackage getModel3SubpackagePackage()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel3SubPackagePackage();
  }

  /**
   * @category Model
   */
  public model4Factory getModel4Factory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel4Factory();
  }

  /**
   * @category Model
   */
  public model4Package getModel4Package()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel4Package();
  }

  /**
   * @category Model
   */
  public model4interfacesPackage getModel4InterfacesPackage()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel4InterfacesPackage();
  }

  /**
   * @category Model
   */
  public Model5Factory getModel5Factory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel5Factory();
  }

  /**
   * @category Model
   */
  public Model5Package getModel5Package()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel5Package();
  }

  /**
   * @category Model
   */
  public Model6Factory getModel6Factory()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel6Factory();
  }

  /**
   * @category Model
   */
  public Model6Package getModel6Package()
  {
    IModelConfig modelConfig = getModelConfig();
    return modelConfig.getModel6Package();
  }

  // /////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////

  public boolean isValid()
  {
    return true;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}.{1} [{2}, {3}, {4}]", getClass().getSimpleName(), getName(), getRepositoryConfig(), getSessionConfig(), getModelConfig());
  }

  @Override
  public TestResult run()
  {
    defaultScenario = false;

    try
    {
      return super.run();
    }
    catch (ConfigTestException ex)
    {
      throw ex;
    }
    catch (Throwable ex)
    {
      throw new ConfigTestException("Error in " + this, ex);
    }
  }

  @Override
  public void runBare() throws Throwable
  {
    defaultScenario = false;

    try
    {
      super.runBare();
    }
    catch (AssertionError error)
    {
      throw error;
    }
    catch (Throwable ex)
    {
      // We wrap anything that is not a test failure
      throw new ConfigTestException("Error in " + this, ex);
    }
  }

  /**
   * Constructs a test-specific resource path of the format "/TestClass_testMethod/resourceName". Using this instead of
   * (just) a hardcoded name for the test resource, ensures that the test method is isolated from all others at the
   * resource level.
   *
   * @param resourceName
   *          the test-local name of the resource or <code>null</code> for the path of the test-local root resource.
   * @return the full path of the resource.
   */
  public final String getResourcePath(String resourceName)
  {
    StringBuilder builder = new StringBuilder();
    builder.append('/');
    builder.append(getClass().getSimpleName()); // Name of this test class
    builder.append('_');
    builder.append(getName()); // Name of the executing test method

    if (resourceName == null || resourceName.length() == 0 || resourceName.charAt(0) != '/')
    {
      builder.append('/');
    }

    if (resourceName != null)
    {
      builder.append(resourceName);
    }

    return builder.toString();
  }

  /**
   * Constructs a test-specific EPackage of the format "pkg123_name". Using this instead of
   * (just) a hardcoded name for the test package, ensures that the test method is isolated from all others.
   *
   * @param name
   *          the test-local name of the package or <code>null</code> .
   * @return the created package.
   * @see #createUniquePackage()
   */
  public final EPackage createUniquePackage(String name)
  {
    if (dynamicPackageNumber == 0)
    {
      dynamicPackageNumber = ++dynamicPackageCounter;
    }

    StringBuilder builder = new StringBuilder();
    builder.append("pkg");
    builder.append(dynamicPackageNumber);

    if (name != null)
    {
      builder.append('_');
      builder.append(name);
    }

    final String uniqueName = builder.toString();

    EPackage ePackage = EMFUtil.createEPackage(uniqueName, uniqueName, "http://" + uniqueName);
    ePackage.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        if (msg.isTouch())
        {
          return;
        }

        Object feature = msg.getFeature();
        if (feature == EcorePackage.Literals.EPACKAGE__NS_PREFIX || feature == EcorePackage.Literals.EPACKAGE__NS_URI
            || feature == EcorePackage.Literals.ENAMED_ELEMENT__NAME)
        {
          throw new ImplementationError("Don't change the unique package " + uniqueName);
        }
      }
    });

    return ePackage;
  }

  /**
   * @see #createUniquePackage(String)
   */
  public final EPackage createUniquePackage()
  {
    return createUniquePackage(null);
  }

  protected boolean isConfig(Config config)
  {
    return ObjectUtil.equals(getRepositoryConfig(), config) //
        || ObjectUtil.equals(getSessionConfig(), config) //
        || ObjectUtil.equals(getModelConfig(), config);
  }

  protected void skipStoreWithoutQueryXRefs()
  {
    skipTest(getRepository().getStore() instanceof NoQueryXRefs);
  }

  protected void skipStoreWithoutLargeObjects()
  {
    skipTest(getRepository().getStore() instanceof NoLargeObjects);
  }

  protected void skipStoreWithoutHandleRevisions()
  {
    skipTest(getRepository().getStore() instanceof NoHandleRevisions);
  }

  protected void skipStoreWithoutRawAccess()
  {
    skipTest(getRepository().getStore() instanceof NoRawAccess);
  }

  protected void skipStoreWithoutChangeSets()
  {
    skipTest(getRepository().getStore() instanceof NoChangeSets);
  }

  protected void skipStoreWithoutCommitInfos()
  {
    skipTest(getRepository().getStore() instanceof NoCommitInfos);
  }

  protected void skipStoreWithoutDurableLocking()
  {
    skipTest(getRepository().getStore() instanceof NoDurableLocking);
  }

  protected void clearCache(CDORevisionManager revisionManager)
  {
    ((InternalCDORevisionManager)revisionManager).getCache().clear();
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
    IScenario scenario;

    try
    {
      scenario = Scenario.load();
      if (scenario == null)
      {
        scenario = Scenario.getDefault();
      }
    }
    catch (Exception ex)
    {
      scenario = Scenario.getDefault();
    }

    return scenario;
  }

  protected String getTestMethodName()
  {
    return getName();
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
    try
    {
      getScenario().tearDown();

      if (testProperties != null)
      {
        testProperties.clear();
        testProperties = null;
      }

      if (homeProperties != null)
      {
        homeProperties.clear();
        homeProperties = null;
      }
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    try
    {
      super.doTearDown();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }
  }

  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.TYPE, ElementType.METHOD })
  public @interface CleanRepositoriesBefore
  {
    String reason();
  }

  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.TYPE, ElementType.METHOD })
  public @interface CleanRepositoriesAfter
  {
    String reason();
  }

  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.TYPE, ElementType.METHOD })
  public @interface Requires
  {
    String[] value();
  }

  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.TYPE, ElementType.METHOD })
  public @interface Skips
  {
    String[] value();
  }
}
