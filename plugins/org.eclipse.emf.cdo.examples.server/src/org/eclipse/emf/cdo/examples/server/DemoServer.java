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
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OSGiApplication;

/**
 * @author Eike Stepper
 */
public class DemoServer extends OSGiApplication
{
  public static final String ID = OM.BUNDLE_ID + ".app"; //$NON-NLS-1$

  public static final String PROP_BROWSER_PORT = OM.BUNDLE_ID + ".browser.port"; //$NON-NLS-1$

  private IRepository[] repositories;

  public DemoServer()
  {
    super(ID);
  }

  @Override
  protected void doStart() throws Exception
  {
    super.doStart();
    OM.LOG.info("Demo server starting");

    String port = System.getProperty(PROP_BROWSER_PORT);
    if (port != null)
    {
      IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.server.db.browsers", "default", port); //$NON-NLS-1$ //$NON-NLS-2$
    }

    OM.LOG.info("Demo server started");
  }

  @Override
  protected void doStop() throws Exception
  {
    OM.LOG.info("Demo server stopping");
    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        LifecycleUtil.deactivate(repository);
      }
    }

    OM.LOG.info("Demo server stopped");
    super.doStop();
  }
}
