/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.internal.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.TransportConfigurator;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiApplication;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class CDOServerApplication extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app";

  private IRepository[] repositories;

  private IAcceptor[] acceptors;

  public CDOServerApplication()
  {
    super(ID);
  }

  @Override
  protected void doStart() throws Exception
  {
    super.doStart();
    OM.LOG.info("CDO Server starting");
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml");
    if (configFile != null && configFile.exists())
    {
      RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(IPluginContainer.INSTANCE);
      repositories = repositoryConfigurator.configure(configFile);
      if (repositories == null || repositories.length == 0)
      {
        OM.LOG.warn("No repositories configured");
      }

      TransportConfigurator net4jConfigurator = new TransportConfigurator(IPluginContainer.INSTANCE);
      acceptors = net4jConfigurator.configure(configFile);
      if (acceptors == null || acceptors.length == 0)
      {
        OM.LOG.warn("No acceptors configured");
      }
    }
    else
    {
      OM.LOG.warn("CDO server configuration not found: " + configFile.getAbsolutePath());
    }

    OM.LOG.info("CDO Server started");
  }

  @Override
  protected void doStop() throws Exception
  {
    OM.LOG.info("CDO Server stopping");
    if (acceptors != null)
    {
      for (IAcceptor acceptor : acceptors)
      {
        acceptor.close();
      }
    }

    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        LifecycleUtil.deactivate(repository);
      }
    }

    OM.LOG.info("CDO Server stopped");
    super.doStop();
  }
}
