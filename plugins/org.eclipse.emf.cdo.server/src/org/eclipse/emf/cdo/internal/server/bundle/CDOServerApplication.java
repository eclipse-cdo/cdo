/*
 * Copyright (c) 2007-2012, 2018-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension3;
import org.eclipse.emf.cdo.spi.server.IAppExtension4;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMBundle.TranslationSupport;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiApplication;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOServerApplication extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

  public static final String PROP_CONFIGURATOR_TYPE = "org.eclipse.emf.cdo.server.repositoryConfiguratorType";

  public static final String PROP_CONFIGURATOR_DESCRIPTION = "org.eclipse.emf.cdo.server.repositoryConfiguratorDescription";

  public static final String PROP_BROWSER_PORT = "org.eclipse.emf.cdo.server.browser.port"; //$NON-NLS-1$

  private static final String TITLE = "CDO server";

  private static final TranslationSupport TS = OM.BUNDLE.getTranslationSupport();

  private IRepository[] repositories;

  private List<IAppExtension> extensions = new ArrayList<>();

  public CDOServerApplication()
  {
    super(ID);
  }

  protected RepositoryConfigurator getConfigurator(IManagedContainer container)
  {
    String type = OMPlatform.INSTANCE.getProperty(PROP_CONFIGURATOR_TYPE, RepositoryConfigurator.Factory.Default.TYPE);
    String description = OMPlatform.INSTANCE.getProperty(PROP_CONFIGURATOR_DESCRIPTION);
    return (RepositoryConfigurator)container.getElement(RepositoryConfigurator.Factory.PRODUCT_GROUP, type, description);
  }

  protected IManagedContainer getApplicationContainer()
  {
    return getContainer();
  }

  @Override
  protected void doStart() throws Exception
  {
    OM.LOG.info(TS.getString("CDOServerApplication.starting", TITLE)); //$NON-NLS-1$
    super.doStart();

    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml"); //$NON-NLS-1$
    if (configFile != null && configFile.exists())
    {
      IManagedContainer container = getApplicationContainer();
      RepositoryConfigurator repositoryConfigurator = getConfigurator(container);

      repositories = repositoryConfigurator.configure(configFile);
      if (repositories == null || repositories.length == 0)
      {
        OM.LOG.warn(TS.getString("CDOServerApplication.noRepos") + " " + configFile.getAbsolutePath()); //$NON-NLS-1$
      }

      String port = OMPlatform.INSTANCE.getProperty(PROP_BROWSER_PORT);
      if (port != null)
      {
        container.getElement("org.eclipse.emf.cdo.server.browsers", "default", port); //$NON-NLS-1$ //$NON-NLS-2$
      }

      startExtensions(configFile);
    }
    else
    {
      OM.LOG.warn(TS.getString("CDOServerApplication.noConfig") + " " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    OM.LOG.info(TS.getString("CDOServerApplication.started", TITLE)); //$NON-NLS-1$
  }

  @Override
  protected void doStop() throws Exception
  {
    OM.LOG.info(TS.getString("CDOServerApplication.stopping", TITLE)); //$NON-NLS-1$
    Collections.reverse(extensions);

    for (IAppExtension extension : extensions)
    {
      try
      {
        if (extension instanceof IAppExtension5)
        {
          IAppExtension5 named = (IAppExtension5)extension;
          OM.LOG.info(TS.getString("AppExtension.stopping", named.getName())); //$NON-NLS-1$
        }

        if (extension instanceof IAppExtension3)
        {
          IAppExtension3 extension3 = (IAppExtension3)extension;
          extension3.stop(repositories);
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
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }

    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        LifecycleUtil.deactivate(repository);
      }
    }

    IManagedContainer container = getApplicationContainer();
    container.deactivate();

    super.doStop();
    OM.LOG.info(TS.getString("CDOServerApplication.stopped", TITLE)); //$NON-NLS-1$
  }

  private void startExtensions(File configFile)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, IAppExtension.EXT_POINT);

    for (IConfigurationElement element : elements)
    {
      if ("appExtension".equals(element.getName())) //$NON-NLS-1$
      {
        try
        {
          IAppExtension extension = (IAppExtension)element.createExecutableExtension("class"); //$NON-NLS-1$
          extensions.add(extension);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }

    extensions.sort(IAppExtension4.COMPARATOR);

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
          IAppExtension3 extension3 = (IAppExtension3)extension;
          extension3.start(repositories, configFile);
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

  public static IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
