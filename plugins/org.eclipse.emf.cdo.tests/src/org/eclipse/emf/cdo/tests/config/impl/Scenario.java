/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019, 2020, 2024 Eike Stepper (Loehne, Germany) and others.
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
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Scenario implements IScenario
{
  public static final String STATE_FILE = "cdo_config_test.state";

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
