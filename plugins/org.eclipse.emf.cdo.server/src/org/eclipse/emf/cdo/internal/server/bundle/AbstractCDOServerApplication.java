/*
 * Copyright (c) 2007-2012, 2018-2022, 2025-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension3;
import org.eclipse.emf.cdo.spi.server.IAppExtension4;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.emf.internal.cdo.util.ExtensionRegistryBootstrap;

import org.eclipse.net4j.util.PluginUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle.TranslationSupport;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the shared configuration, extension, repository, and container lifecycle for CDO server applications.
 * <p>
 * Concrete entry points provide only their process or framework integration. The application uses the global managed
 * container by default so existing factories and components that access {@link IManagedContainer#INSTANCE} continue
 * to participate in the same server instance.
 *
 * @author Eike Stepper
 */
public abstract class AbstractCDOServerApplication extends Lifecycle
{
  /**
   * The system property that controls the title used in application logs.
   */
  public static final String PROP_TITLE = "org.eclipse.emf.cdo.server.title"; //$NON-NLS-1$

  /**
   * The system property that selects the server XML configuration file.
   */
  public static final String PROP_CONFIG_FILE_NAME = "org.eclipse.emf.cdo.server.configFileName"; //$NON-NLS-1$

  /**
   * The system property that selects the repository configurator factory type.
   */
  public static final String PROP_CONFIGURATOR_TYPE = "org.eclipse.emf.cdo.server.repositoryConfiguratorType"; //$NON-NLS-1$

  /**
   * The system property that selects the repository configurator description.
   */
  public static final String PROP_CONFIGURATOR_DESCRIPTION = "org.eclipse.emf.cdo.server.repositoryConfiguratorDescription"; //$NON-NLS-1$

  /**
   * The system property that enables the server browser and selects its port.
   */
  public static final String PROP_BROWSER_PORT = "org.eclipse.emf.cdo.server.browser.port"; //$NON-NLS-1$

  /**
   * The system property that selects the server browser factory type.
   */
  public static final String PROP_BROWSER_TYPE = "org.eclipse.emf.cdo.server.browser.type"; //$NON-NLS-1$

  private static final TranslationSupport TS = OM.BUNDLE.getTranslationSupport();

  private final List<IAppExtension> earlyExtensions = new ArrayList<>();

  private final List<IAppExtension> extensions = new ArrayList<>();

  private IRepository[] repositories;

  public AbstractCDOServerApplication()
  {
  }

  /**
   * Returns the configured server title used in lifecycle log messages.
   *
   * @return the configured title, or {@code "CDO server"} when no title is set
   */
  protected String getTitle()
  {
    return OMPlatform.INSTANCE.getProperty(PROP_TITLE, "CDO server"); //$NON-NLS-1$
  }

  /**
   * Resolves the server configuration file from the platform configuration area.
   *
   * @return the configured file, or {@code null} if the platform cannot resolve it
   */
  protected File getConfigFile()
  {
    String configFileName = OMPlatform.INSTANCE.getProperty(PROP_CONFIG_FILE_NAME, "cdo-server.xml"); //$NON-NLS-1$
    return OMPlatform.INSTANCE.getConfigFile(configFileName);
  }

  /**
   * Looks up the repository configurator selected by the application properties.
   *
   * @param container the managed container used for product lookup
   * @return the configured repository configurator
   */
  protected RepositoryConfigurator getConfigurator(IManagedContainer container)
  {
    String type = OMPlatform.INSTANCE.getProperty(PROP_CONFIGURATOR_TYPE, RepositoryConfigurator.Factory.Default.TYPE);
    String description = OMPlatform.INSTANCE.getProperty(PROP_CONFIGURATOR_DESCRIPTION);
    return (RepositoryConfigurator)container.getElement(RepositoryConfigurator.Factory.PRODUCT_GROUP, type, description);
  }

  /**
   * Returns the managed container used by this application.
   *
   * @return the global managed container by default
   */
  protected IManagedContainer getApplicationContainer()
  {
    return IManagedContainer.INSTANCE;
  }

  /**
   * Discovers and orders application extensions from the Eclipse Extension Registry.
   *
   * @param container the application container assigned to container-aware extensions
   */
  protected void readExtensions(IManagedContainer container)
  {
    ExtensionRegistryBootstrap.init();
    IConfigurationElement[] elements = PluginUtil.getConfigurationElements(OM.BUNDLE_ID, IAppExtension.EXT_POINT);
    elements = PluginUtil.removePredecessors(elements);

    for (IConfigurationElement element : elements)
    {
      if ("appExtension".equals(element.getName())) //$NON-NLS-1$
      {
        try
        {
          IAppExtension extension = PluginUtil.instantiate(element);
          if (extension instanceof ContainerAware)
          {
            ((ContainerAware)extension).setManagedContainer(container);
          }

          if (extension instanceof IAppExtension5 && ((IAppExtension5)extension).startBeforeRepositories())
          {
            earlyExtensions.add(extension);
          }
          else
          {
            extensions.add(extension);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    earlyExtensions.sort(IAppExtension4.COMPARATOR);
    extensions.sort(IAppExtension4.COMPARATOR);
  }

  @Override
  protected void doActivate() throws Exception
  {
    OM.LOG.info(TS.getString("CDOServerApplication.starting", getTitle())); //$NON-NLS-1$

    try
    {
      File configFile = getConfigFile();
      if (configFile != null && configFile.exists())
      {
        IManagedContainer container = getApplicationContainer();
        readExtensions(container);
        startExtensions(configFile, earlyExtensions);
        startRepositories(configFile, container);
        startBrowser(container);
        startExtensions(configFile, extensions);
      }
      else
      {
        OM.LOG.warn(TS.getString("CDOServerApplication.noConfig") + " " + (configFile == null ? "null" : configFile.getAbsolutePath())); //$NON-NLS-1$
      }

      OM.LOG.info(TS.getString("CDOServerApplication.started", getTitle())); //$NON-NLS-1$
    }
    catch (Exception | Error ex)
    {
      cleanupAfterActivationFailure(ex);
      throw ex;
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    OM.LOG.info(TS.getString("CDOServerApplication.stopping", getTitle())); //$NON-NLS-1$
    Throwable failure = null;

    try
    {
      stopExtensions(extensions);
    }
    catch (Exception | Error ex)
    {
      failure = appendFailure(failure, ex);
    }

    try
    {
      stopRepositories();
    }
    catch (Exception | Error ex)
    {
      failure = appendFailure(failure, ex);
    }

    try
    {
      stopExtensions(earlyExtensions);
    }
    catch (Exception | Error ex)
    {
      failure = appendFailure(failure, ex);
    }

    try
    {
      getApplicationContainer().deactivate();
    }
    catch (Exception | Error ex)
    {
      failure = appendFailure(failure, ex);
    }

    OM.LOG.info(TS.getString("CDOServerApplication.stopped", getTitle())); //$NON-NLS-1$

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }

    if (failure != null)
    {
      throw (Exception)failure;
    }
  }

  /**
   * Configures repositories using the existing XML repository configuration mechanism.
   *
   * @param configFile the server configuration file
   * @param container the application container
   * @throws ParserConfigurationException if the XML parser cannot be configured
   * @throws SAXException if the configuration XML is invalid
   * @throws IOException if the configuration cannot be read
   * @throws CoreException if repository extension creation fails
   */
  protected void startRepositories(File configFile, IManagedContainer container) throws ParserConfigurationException, SAXException, IOException, CoreException
  {
    RepositoryConfigurator repositoryConfigurator = getConfigurator(container);

    repositories = repositoryConfigurator.configure(configFile);
    if (repositories == null || repositories.length == 0)
    {
      OM.LOG.warn(TS.getString("CDOServerApplication.noRepos") + " " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }
  }

  /**
   * Starts the optional server browser when a browser port is configured.
   *
   * @param container the application container
   */
  protected void startBrowser(IManagedContainer container)
  {
    String port = OMPlatform.INSTANCE.getProperty(PROP_BROWSER_PORT);
    if (port != null)
    {
      String type = OMPlatform.INSTANCE.getProperty(PROP_BROWSER_TYPE, "default"); //$NON-NLS-1$
      container.getElement("org.eclipse.emf.cdo.server.browsers", type, port); //$NON-NLS-1$
    }
  }

  /**
   * Starts a group of application extensions, logging and containing individual extension failures as before.
   *
   * @param configFile the server configuration file
   * @param extensions the ordered extensions to start
   */
  protected void startExtensions(File configFile, List<IAppExtension> extensions)
  {
    for (IAppExtension extension : extensions)
    {
      try
      {
        if (extension instanceof IAppExtension5)
        {
          IAppExtension5 named = (IAppExtension5)extension;
          OM.LOG.info(TS.getString("AppExtension.starting", named.getName())); //$NON-NLS-1$
        }

        if (extension instanceof IAppExtension3)
        {
          ((IAppExtension3)extension).start(repositories, configFile);
        }
        else
        {
          extension.start(configFile);
        }

        if (extension instanceof IAppExtension5)
        {
          IAppExtension5 named = (IAppExtension5)extension;
          OM.LOG.info(TS.getString("AppExtension.started", named.getName())); //$NON-NLS-1$
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * Stops extensions in reverse order while preserving their established lifecycle callbacks.
   *
   * @param extensions the ordered extensions to stop
   */
  protected void stopExtensions(List<IAppExtension> extensions) throws Exception
  {
    Throwable failure = stopExtensions(extensions, null);
    if (failure instanceof Error)
    {
      throw (Error)failure;
    }

    if (failure != null)
    {
      throw (Exception)failure;
    }
  }

  /**
   * Deactivates each configured repository.
   */
  protected void stopRepositories() throws Exception
  {
    Throwable failure = null;

    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        try
        {
          Exception exception = LifecycleUtil.deactivate(repository);
          if (exception != null)
          {
            OM.LOG.error(exception);
            failure = appendFailure(failure, exception);
          }
        }
        catch (Exception | Error ex)
        {
          OM.LOG.error(ex);
          failure = appendFailure(failure, ex);
        }
      }
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }

    if (failure != null)
    {
      throw (Exception)failure;
    }
  }

  private void cleanupAfterActivationFailure(Throwable failure)
  {
    try
    {
      doDeactivate();
    }
    catch (Exception | Error cleanupFailure)
    {
      failure.addSuppressed(cleanupFailure);
      OM.LOG.error(cleanupFailure);
    }
  }

  private Throwable stopExtensions(List<IAppExtension> extensions, Throwable failure)
  {
    for (int i = extensions.size() - 1; i >= 0; --i)
    {
      IAppExtension extension = extensions.get(i);

      try
      {
        if (extension instanceof IAppExtension5)
        {
          IAppExtension5 named = (IAppExtension5)extension;
          OM.LOG.info(TS.getString("AppExtension.stopping", named.getName())); //$NON-NLS-1$
        }

        if (extension instanceof IAppExtension3)
        {
          ((IAppExtension3)extension).stop(repositories);
        }
        else
        {
          extension.stop();
        }

        if (extension instanceof IAppExtension5)
        {
          IAppExtension5 named = (IAppExtension5)extension;
          OM.LOG.info(TS.getString("AppExtension.stopped", named.getName())); //$NON-NLS-1$
        }
      }
      catch (Exception | Error ex)
      {
        OM.LOG.error(ex);
        failure = appendFailure(failure, ex);
      }
    }

    return failure;
  }

  private static Throwable appendFailure(Throwable failure, Throwable ex)
  {
    if (failure == null)
    {
      return ex;
    }

    failure.addSuppressed(ex);
    return failure;
  }
}
