/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019, 2020, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.bundle.OM;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CaseInsensitiveStringSet;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Scenario implements IScenario
{
  public static final String STATE_FILE = "cdo_config_test.state";

  private static final String[] SCENARIO_PROPERTIES = { //
      IConstants.TEST_SCENARIO_PROPERTY, //
      IConstants.TEST_REPOSITORY_PROPERTY, //
      IConstants.TEST_SESSION_PROPERTY, //
      IConstants.TEST_MODEL_PROPERTY };

  private static final long serialVersionUID = 1L;

  private IRepositoryConfig repositoryConfig;

  private ISessionConfig sessionConfig;

  private IModelConfig modelConfig;

  private transient Set<IConfig> configs;

  private transient ConfigTest currentTest;

  public Scenario()
  {
  }

  public Scenario(IRepositoryConfig repositoryConfig, ISessionConfig sessionConfig, IModelConfig modelConfig)
  {
    setRepositoryConfig(repositoryConfig);
    setSessionConfig(sessionConfig);
    setModelConfig(modelConfig);
  }

  @Override
  public IRepositoryConfig getRepositoryConfig()
  {
    return repositoryConfig;
  }

  @Override
  public Scenario setRepositoryConfig(IRepositoryConfig repositoryConfig)
  {
    configs = null;
    this.repositoryConfig = repositoryConfig;
    if (repositoryConfig != null)
    {
      repositoryConfig.setCurrentTest(currentTest);
    }

    return this;
  }

  @Override
  public ISessionConfig getSessionConfig()
  {
    return sessionConfig;
  }

  @Override
  public Scenario setSessionConfig(ISessionConfig sessionConfig)
  {
    configs = null;
    this.sessionConfig = sessionConfig;
    if (sessionConfig != null)
    {
      sessionConfig.setCurrentTest(currentTest);
    }

    return this;
  }

  @Override
  public IModelConfig getModelConfig()
  {
    return modelConfig;
  }

  @Override
  public Scenario setModelConfig(IModelConfig modelConfig)
  {
    configs = null;
    this.modelConfig = modelConfig;
    if (modelConfig != null)
    {
      modelConfig.setCurrentTest(currentTest);
    }

    return this;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Scenario[{0}, {1}, {2}]", //
        getRepositoryConfig(), getSessionConfig(), getModelConfig());
  }

  @Override
  public Set<IConfig> getConfigs()
  {
    if (configs == null)
    {
      configs = new HashSet<>();
      configs.add(getRepositoryConfig());
      configs.add(getSessionConfig());
      configs.add(getModelConfig());
    }

    return configs;
  }

  @Override
  public Set<String> getCapabilities()
  {
    Set<String> capabilities = new CaseInsensitiveStringSet();
    capabilities.add(IConfig.CAPABILITY_ALL);

    if (CheckUtil.SANITIZE_TIMEOUT)
    {
      capabilities.add(IConfig.CAPABILITY_SANITIZE_TIMEOUT);
    }

    repositoryConfig.initCapabilities(capabilities);
    sessionConfig.initCapabilities(capabilities);
    modelConfig.initCapabilities(capabilities);

    return capabilities;
  }

  @Override
  public boolean isValid()
  {
    Set<IConfig> configs = getConfigs();
    for (IConfig config : configs)
    {
      if (!config.isValid(configs))
      {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean alwaysCleanRepositories()
  {
    return false;
  }

  @Override
  public ConfigTest getCurrentTest()
  {
    return currentTest;
  }

  @Override
  public void setCurrentTest(ConfigTest currentTest)
  {
    this.currentTest = currentTest;
    if (repositoryConfig != null)
    {
      repositoryConfig.setCurrentTest(currentTest);
    }

    if (sessionConfig != null)
    {
      sessionConfig.setCurrentTest(currentTest);
    }

    if (modelConfig != null)
    {
      modelConfig.setCurrentTest(currentTest);
    }
  }

  @Override
  public void setUp() throws Exception
  {
    try
    {
      getRepositoryConfig().setUp();
    }
    finally
    {
      try
      {
        getSessionConfig().setUp();
      }
      finally
      {
        getModelConfig().setUp();
      }
    }
  }

  @Override
  public void tearDown() throws Exception
  {
    try
    {
      getModelConfig().tearDown();
    }
    catch (Exception ex)
    {
      currentTest.log(ex);
    }

    try
    {
      getSessionConfig().tearDown();
    }
    catch (Exception ex)
    {
      currentTest.log(ex);
    }

    try
    {
      getRepositoryConfig().tearDown();
    }
    catch (Exception ex)
    {
      currentTest.log(ex);
    }
  }

  @Override
  public void mainSuiteFinished()
  {
    try
    {
      getModelConfig().mainSuiteFinished();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    try
    {
      getSessionConfig().mainSuiteFinished();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }

    try
    {
      getRepositoryConfig().mainSuiteFinished();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
    }
  }

  @Override
  public void save()
  {
    File file = getStateFile();
    ObjectOutputStream stream = null;

    try
    {
      stream = new ObjectOutputStream(IOUtil.openOutputStream(file));
      stream.writeObject(this);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      IOUtil.close(stream);
    }
  }

  @SuppressWarnings("resource")
  public static IScenario load()
  {
    File file = getStateFile();
    if (file.exists())
    {
      FileInputStream stream = IOUtil.openInputStream(file);

      try
      {
        return (IScenario)new ObjectInputStream(stream).readObject();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
      finally
      {
        IOUtil.close(stream);
      }
    }

    return null;
  }

  /**
   * Creates a scenario from the externally supplied test configuration properties.
   * <p>
   * The complete scenario property takes precedence over the component properties, but mixing the two forms is
   * rejected. A {@code null} result means that no external override was supplied and allows callers to retain the
   * serialized-scenario fallback.
   *
   * @return the externally configured scenario, or {@code null} if no override is configured.
   * @throws IllegalArgumentException if the properties are incomplete, mixed, or name an unknown configuration.
   */
  public static IScenario createFromProperties()
  {
    String scenarioName = getProperty(IConstants.TEST_SCENARIO_PROPERTY);
    String repositoryName = getProperty(IConstants.TEST_REPOSITORY_PROPERTY);
    String sessionName = getProperty(IConstants.TEST_SESSION_PROPERTY);
    String modelName = getProperty(IConstants.TEST_MODEL_PROPERTY);

    if (scenarioName != null)
    {
      if (repositoryName != null || sessionName != null || modelName != null)
      {
        throw new IllegalArgumentException("Property " + IConstants.TEST_SCENARIO_PROPERTY + " must not be combined with " + IConstants.TEST_REPOSITORY_PROPERTY
            + ", " + IConstants.TEST_SESSION_PROPERTY + ", or " + IConstants.TEST_MODEL_PROPERTY);
      }

      return createScenario(scenarioName);
    }

    boolean hasRepository = repositoryName != null;
    boolean hasSession = sessionName != null;
    boolean hasModel = modelName != null;
    if (!hasRepository && !hasSession && !hasModel)
    {
      return null;
    }

    if (!hasRepository || !hasSession || !hasModel)
    {
      throw new IllegalArgumentException("Properties " + String.join(", ", SCENARIO_PROPERTIES) + " require either " + IConstants.TEST_SCENARIO_PROPERTY
          + " alone or all three component properties");
    }

    return new Scenario(createRepository(repositoryName), createSession(sessionName), createModel(modelName));
  }

  private static String getProperty(String name)
  {
    return System.getProperty(name);
  }

  private static IScenario createScenario(String name)
  {
    String[] parts = split(name, '/', IConstants.TEST_SCENARIO_PROPERTY, true);
    if (parts.length != 3)
    {
      throw new IllegalArgumentException("Property " + IConstants.TEST_SCENARIO_PROPERTY + " must contain exactly three components separated by '/'");
    }

    return new Scenario(createRepository(parts[0]), createSession(parts[1]), createModel(parts[2]));
  }

  private static IRepositoryConfig createRepository(String name)
  {
    return create(IConstants.REPOSITORY_CONFIGS, name, IRepositoryConfig.class, IConstants.TEST_REPOSITORY_PROPERTY);
  }

  private static ISessionConfig createSession(String name)
  {
    return create(IConstants.SESSION_CONFIGS, name, ISessionConfig.class, IConstants.TEST_SESSION_PROPERTY);
  }

  private static IModelConfig createModel(String name)
  {
    return create(IConstants.MODEL_CONFIGS, name, IModelConfig.class, IConstants.TEST_MODEL_PROPERTY);
  }

  private static <T> T create(String productGroup, String specification, Class<T> productType, String property)
  {
    prepareStandaloneFactories();
    String[] parts = split(specification, ':', property, true);
    if (parts.length > 2 || parts[0].length() == 0)
    {
      throw new IllegalArgumentException("Malformed configuration specification '" + specification + "' for " + property);
    }

    String type = parts[0];
    String description = parts.length == 2 ? parts[1] : null;
    try
    {
      T result = IPluginContainer.INSTANCE.getElementOrNull(productGroup, type, description);
      if (result == null || !productType.isInstance(result))
      {
        throw new IllegalArgumentException("Unknown factory type '" + type + "' for " + property);
      }

      return result;
    }
    catch (ProductCreationException ex)
    {
      throw new IllegalArgumentException("Could not create configuration '" + specification + "' for " + property, ex);
    }
  }

  private static boolean standaloneFactoriesPrepared;

  private static synchronized void prepareStandaloneFactories()
  {
    if (!OMPlatform.INSTANCE.isExtensionRegistryAvailable() && !standaloneFactoriesPrepared)
    {
      OM.BUNDLE.prepareContainer(IPluginContainer.INSTANCE);
      standaloneFactoriesPrepared = true;
    }
  }

  private static String[] split(String value, char separator, String property, boolean decode)
  {
    if (value == null || value.length() == 0)
    {
      throw new IllegalArgumentException("Empty configuration specification for " + property);
    }

    ArrayList<String> result = new ArrayList<>();
    int start = 0;
    boolean escaped = false;
    for (int i = 0; i < value.length(); i++)
    {
      char c = value.charAt(i);
      if (escaped)
      {
        escaped = false;
      }
      else if (c == '\\')
      {
        escaped = true;
      }
      else if (c == separator)
      {
        result.add(decode ? unescape(value.substring(start, i), separator, property) : value.substring(start, i));
        start = i + 1;
      }
    }

    if (escaped)
    {
      throw new IllegalArgumentException("Malformed escaping in '" + value + "' for " + property);
    }

    result.add(decode ? unescape(value.substring(start), separator, property) : value.substring(start));
    return result.toArray(new String[result.size()]);
  }

  private static String unescape(String value, char separator, String property)
  {
    try
    {
      return StringUtil.unescape(value, separator);
    }
    catch (RuntimeException ex)
    {
      throw new IllegalArgumentException("Malformed escaping in '" + value + "' for " + property, ex);
    }
  }

  public static File getStateFile()
  {
    String home = OMPlatform.INSTANCE.getProperty("user.home");
    if (home != null)
    {
      return new File(home, STATE_FILE);
    }

    return new File(STATE_FILE);
  }

  public static IScenario getDefault()
  {
    return Default.INSTANCE;
  }

  /**
   * @author Eike Stepper
   */
  private static final class Default extends Scenario
  {
    public static final IScenario INSTANCE = new Default();

    private static final long serialVersionUID = 1L;

    private Default()
    {
      setRepositoryConfig(IConstants.MEM_BRANCHES);
      setSessionConfig(IConstants.JVM);
      setModelConfig(IConstants.NATIVE);
    }
  }
}
