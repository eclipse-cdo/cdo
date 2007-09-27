/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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

import org.eclipse.net4j.IAcceptor;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiApplication;

import org.eclipse.internal.net4j.Net4jConfigurator;

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
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml");
    if (configFile != null && configFile.exists())
    {
      RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(IPluginContainer.INSTANCE);
      repositories = repositoryConfigurator.configure(configFile);

      Net4jConfigurator net4jConfigurator = new Net4jConfigurator(IPluginContainer.INSTANCE);
      acceptors = net4jConfigurator.configure(configFile);
    }
    else
    {
      OM.LOG.warn("CDO server configuration not found: " + configFile.getAbsolutePath());
    }
  }

  @Override
  protected void doStop() throws Exception
  {
    if (acceptors != null)
    {
      for (IAcceptor acceptor : acceptors)
      {
        LifecycleUtil.deactivate(acceptor);
      }
    }

    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        LifecycleUtil.deactivate(repository);
      }
    }

    super.doStop();
  }
}
