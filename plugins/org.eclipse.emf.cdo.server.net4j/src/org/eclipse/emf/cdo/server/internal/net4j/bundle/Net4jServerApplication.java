/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.bundle;

import org.eclipse.net4j.TransportConfigurator;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OSGiApplication;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class Net4jServerApplication extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

  private IAcceptor[] acceptors;

  public Net4jServerApplication()
  {
    super(ID);
  }

  @Override
  protected void doStart() throws Exception
  {
    super.doStart();
    OM.LOG.info("Net4j Server starting"); //$NON-NLS-1$
    File configFile = OMPlatform.INSTANCE.getConfigFile("cdo-server.xml"); //$NON-NLS-1$
    if (configFile != null && configFile.exists())
    {
      TransportConfigurator net4jConfigurator = new TransportConfigurator(IPluginContainer.INSTANCE);
      acceptors = net4jConfigurator.configure(configFile);
      if (acceptors == null || acceptors.length == 0)
      {
        OM.LOG.warn("No Net4j acceptors configured" + configFile.getAbsolutePath()); //$NON-NLS-1$
      }
    }
    else
    {
      OM.LOG.warn("Net4j Server configuration not found: " + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    OM.LOG.info("Net4j Server started"); //$NON-NLS-1$
  }

  @Override
  protected void doStop() throws Exception
  {
    OM.LOG.info("Net4j Server stopping"); //$NON-NLS-1$
    if (acceptors != null)
    {
      for (IAcceptor acceptor : acceptors)
      {
        acceptor.close();
      }
    }

    OM.LOG.info("Net4j Server stopped"); //$NON-NLS-1$
    super.doStop();
  }
}
