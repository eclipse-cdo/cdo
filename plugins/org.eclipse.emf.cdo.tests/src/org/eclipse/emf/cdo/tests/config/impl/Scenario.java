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

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.CaseInsensitiveStringSet;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
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
    switch (normalize(name))
    {
    case "MEM_JVM_NATIVE":
      return new Scenario(IConstants.MEM, IConstants.JVM, IConstants.NATIVE);
    case "MEM_AUDITS_JVM_NATIVE":
      return new Scenario(IConstants.MEM_AUDITS, IConstants.JVM, IConstants.NATIVE);
    case "MEM_BRANCHES_JVM_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.JVM, IConstants.NATIVE);
    case "MEM_BRANCHES_UUIDS_JVM_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES_UUIDS, IConstants.JVM, IConstants.NATIVE);
    case "MEM_OFFLINE_JVM_NATIVE":
      return new Scenario(IConstants.MEM_OFFLINE, IConstants.JVM, IConstants.NATIVE);
    case "MEM_EMBEDDED_BRANCHES_EMBEDDED_NATIVE":
      return new Scenario(IConstants.MEM_EMBEDDED_BRANCHES, IConstants.EMBEDDED, IConstants.NATIVE);
    case "MEM_JVM_LEGACY":
      return new Scenario(IConstants.MEM, IConstants.JVM, IConstants.LEGACY);
    case "MEM_AUDITS_JVM_LEGACY":
      return new Scenario(IConstants.MEM_AUDITS, IConstants.JVM, IConstants.LEGACY);
    case "MEM_BRANCHES_JVM_LEGACY":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.JVM, IConstants.LEGACY);
    case "MEM_BRANCHES_TCP_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.TCP, IConstants.NATIVE);
    case "MEM_BRANCHES_SSL_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.SSL, IConstants.NATIVE);
    case "MEM_WS_NATIVE":
      return new Scenario(IConstants.MEM, IConstants.WS, IConstants.NATIVE);
    case "MEM_AUDITS_WS_NATIVE":
      return new Scenario(IConstants.MEM_AUDITS, IConstants.WS, IConstants.NATIVE);
    case "MEM_BRANCHES_WS_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.WS, IConstants.NATIVE);
    case "MEM_BRANCHES_UUIDS_WS_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES_UUIDS, IConstants.WS, IConstants.NATIVE);
    case "MEM_WSS_NATIVE":
      return new Scenario(IConstants.MEM, IConstants.WSS, IConstants.NATIVE);
    case "MEM_AUDITS_WSS_NATIVE":
      return new Scenario(IConstants.MEM_AUDITS, IConstants.WSS, IConstants.NATIVE);
    case "MEM_BRANCHES_WSS_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.WSS, IConstants.NATIVE);
    case "MEM_BRANCHES_UUIDS_WSS_NATIVE":
      return new Scenario(IConstants.MEM_BRANCHES_UUIDS, IConstants.WSS, IConstants.NATIVE);
    case "MEM_WS_LEGACY":
      return new Scenario(IConstants.MEM, IConstants.WS, IConstants.LEGACY);
    case "MEM_AUDITS_WS_LEGACY":
      return new Scenario(IConstants.MEM_AUDITS, IConstants.WS, IConstants.LEGACY);
    case "MEM_BRANCHES_WS_LEGACY":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.WS, IConstants.LEGACY);
    case "MEM_WSS_LEGACY":
      return new Scenario(IConstants.MEM, IConstants.WSS, IConstants.LEGACY);
    case "MEM_AUDITS_WSS_LEGACY":
      return new Scenario(IConstants.MEM_AUDITS, IConstants.WSS, IConstants.LEGACY);
    case "MEM_BRANCHES_WSS_LEGACY":
      return new Scenario(IConstants.MEM_BRANCHES, IConstants.WSS, IConstants.LEGACY);
    default:
      throw unknown(IConstants.TEST_SCENARIO_PROPERTY, name,
          "MEM_JVM_NATIVE, MEM_AUDITS_JVM_NATIVE, MEM_BRANCHES_JVM_NATIVE, MEM_BRANCHES_UUIDS_JVM_NATIVE, "
              + "MEM_OFFLINE_JVM_NATIVE, MEM_EMBEDDED_BRANCHES_EMBEDDED_NATIVE, MEM_JVM_LEGACY, "
              + "MEM_AUDITS_JVM_LEGACY, MEM_BRANCHES_JVM_LEGACY, MEM_BRANCHES_TCP_NATIVE, "
              + "MEM_BRANCHES_SSL_NATIVE, MEM_WS_NATIVE, MEM_AUDITS_WS_NATIVE, MEM_BRANCHES_WS_NATIVE, "
              + "MEM_BRANCHES_UUIDS_WS_NATIVE, MEM_WSS_NATIVE, MEM_AUDITS_WSS_NATIVE, MEM_BRANCHES_WSS_NATIVE, "
              + "MEM_BRANCHES_UUIDS_WSS_NATIVE, MEM_WS_LEGACY, MEM_AUDITS_WS_LEGACY, MEM_BRANCHES_WS_LEGACY, "
              + "MEM_WSS_LEGACY, MEM_AUDITS_WSS_LEGACY, MEM_BRANCHES_WSS_LEGACY");
    }
  }

  private static IRepositoryConfig createRepository(String name)
  {
    switch (normalize(name))
    {
    case "MEM":
      return IConstants.MEM;
    case "MEM_AUDITS":
      return IConstants.MEM_AUDITS;
    case "MEM_BRANCHES":
      return IConstants.MEM_BRANCHES;
    case "MEM_BRANCHES_UUIDS":
      return IConstants.MEM_BRANCHES_UUIDS;
    case "MEM_OFFLINE":
      return IConstants.MEM_OFFLINE;
    case "MEM_EMBEDDED_BRANCHES":
      return IConstants.MEM_EMBEDDED_BRANCHES;
    default:
      throw unknown(IConstants.TEST_REPOSITORY_PROPERTY, name, "MEM, MEM_AUDITS, MEM_BRANCHES, MEM_BRANCHES_UUIDS, MEM_OFFLINE, MEM_EMBEDDED_BRANCHES");
    }
  }

  private static ISessionConfig createSession(String name)
  {
    switch (normalize(name))
    {
    case "EMBEDDED":
      return IConstants.EMBEDDED;
    case "JVM":
      return IConstants.JVM;
    case "TCP":
      return IConstants.TCP;
    case "SSL":
      return IConstants.SSL;
    case "WS":
      return IConstants.WS;
    case "WSS":
      return IConstants.WSS;
    default:
      throw unknown(IConstants.TEST_SESSION_PROPERTY, name, "EMBEDDED, JVM, TCP, SSL, WS, WSS");
    }
  }

  private static IModelConfig createModel(String name)
  {
    switch (normalize(name))
    {
    case "NATIVE":
      return IConstants.NATIVE;
    case "LEGACY":
      return IConstants.LEGACY;
    default:
      throw unknown(IConstants.TEST_MODEL_PROPERTY, name, "NATIVE, LEGACY");
    }
  }

  private static String normalize(String value)
  {
    return value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
  }

  private static IllegalArgumentException unknown(String property, String value, String accepted)
  {
    return new IllegalArgumentException("Unknown value '" + value + "' for " + property + ". Accepted values: " + accepted);
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
