/*
 * Copyright (c) 2009-2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.bundle;

import org.eclipse.emf.cdo.spi.server.IAppExtension4;

import org.eclipse.net4j.TransportConfigurator;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class Net4jAppExtension implements IAppExtension4
{
  private IAcceptor[] acceptors;

  public Net4jAppExtension()
  {
  }

  @Override
  public int getPriority()
  {
    return PRIORITY_NETWORK;
  }

  @Override
  public void start(File configFile) throws Exception
  {
    OM.LOG.info("Net4j extension starting"); //$NON-NLS-1$

    TransportConfigurator net4jConfigurator = new TransportConfigurator(getContainer());
    acceptors = net4jConfigurator.configure(configFile);
    if (acceptors == null || acceptors.length == 0)
    {
      OM.LOG.warn("No Net4j acceptors configured" + configFile.getAbsolutePath()); //$NON-NLS-1$
    }

    OM.LOG.info("Net4j extension started"); //$NON-NLS-1$
  }

  @Override
  public void stop() throws Exception
  {
    OM.LOG.info("Net4j extension stopping"); //$NON-NLS-1$

    if (acceptors != null)
    {
      for (IAcceptor acceptor : acceptors)
      {
        acceptor.close();
      }
    }

    OM.LOG.info("Net4j extension stopped"); //$NON-NLS-1$
  }

  public static IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
