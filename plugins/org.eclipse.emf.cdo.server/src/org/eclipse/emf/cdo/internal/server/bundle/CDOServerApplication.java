/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.bundle;

import org.eclipse.emf.cdo.internal.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.internal.server.messages.Messages;
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
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

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
    OM.LOG.info(Messages.getString("CDOServerApplication.1")); //$NON-NLS-1$
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml"); //$NON-NLS-1$
    if (configFile != null && configFile.exists())
    {
      RepositoryConfigurator repositoryConfigurator = new RepositoryConfigurator(IPluginContainer.INSTANCE);
      repositories = repositoryConfigurator.configure(configFile);
      if (repositories == null || repositories.length == 0)
      {
        OM.LOG.warn(Messages.getString("CDOServerApplication.3")); //$NON-NLS-1$
      }

      TransportConfigurator net4jConfigurator = new TransportConfigurator(IPluginContainer.INSTANCE);
      acceptors = net4jConfigurator.configure(configFile);
      if (acceptors == null || acceptors.length == 0)
      {
        OM.LOG.warn(Messages.getString("CDOServerApplication.4")); //$NON-NLS-1$
      }
    }
    else
    {
      OM.LOG.warn(Messages.getString("CDOServerApplication.5") + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    OM.LOG.info(Messages.getString("CDOServerApplication.6")); //$NON-NLS-1$
  }

  @Override
  protected void doStop() throws Exception
  {
    OM.LOG.info(Messages.getString("CDOServerApplication.7")); //$NON-NLS-1$
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

    OM.LOG.info(Messages.getString("CDOServerApplication.8")); //$NON-NLS-1$
    super.doStop();
  }
}
