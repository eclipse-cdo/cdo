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
import org.eclipse.emf.cdo.internal.server.RepositoryFactory;
import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
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

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOServerApplication.class);

  private IRepository[] repositories;

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
      if (TRACER.isEnabled()) TRACER.format("Configuring repositories from {0}", configFile.getAbsolutePath());
      RepositoryConfigurator configurator = new RepositoryConfigurator(IPluginContainer.INSTANCE);
      repositories = configurator.configure(configFile);
    }
    else
    {
      OM.LOG.warn("Repository config file not found: " + configFile.getAbsolutePath());
    }
  }

  @Override
  protected void doStop() throws Exception
  {
    if (repositories != null)
    {
      for (IRepository repository : repositories)
      {
        LifecycleUtil.deactivate(repository);
      }
    }

    Object[] elements = IPluginContainer.INSTANCE.getElements(RepositoryFactory.PRODUCT_GROUP);
    for (Object element : elements)
    {
      System.out.println("Container: " + element);
    }

    super.doStop();
  }
}
