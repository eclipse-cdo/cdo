/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.examples.server.internal;


import org.eclipse.net4j.examples.server.internal.ExampleServerPlugin;
import org.eclipse.net4j.spring.Container;
import org.eclipse.net4j.spring.ContainerCreationException;
import org.eclipse.net4j.spring.impl.ContainerImpl;
import org.eclipse.net4j.util.eclipse.AbstractPlugin;

import java.io.IOException;


/**
 * The main plugin class to be used in the desktop.
 */
public class CDOServerPlugin extends AbstractPlugin
{
  public static final String CONTEXT_PATH = "META-INF/";

  //The shared instance.
  private static CDOServerPlugin plugin;

  private static Container container;

  private static Container serverContainer;

  /**
   * The constructor.
   */
  public CDOServerPlugin()
  {
    if (plugin == null) plugin = this;
  }

  /**
   * Returns the shared instance.
   */
  public static CDOServerPlugin getDefault()
  {
    return plugin;
  }

  protected void doStart() throws Exception
  {
    getServerContainer();
  }

  protected void doStop() throws Exception
  {
    if (serverContainer != null)
    {
      serverContainer.stop();
      serverContainer = null;
    }

    if (container != null)
    {
      container.stop();
      container = null;
    }

    plugin = null;
  }

  public static Container getServerContainer()
  {
    if (serverContainer == null)
    {
      String baseResourcePath;

      try
      {
        baseResourcePath = getBundleLocation(getDefault().getBundle());
      }
      catch (IOException ex)
      {
        throw new ContainerCreationException("Error while computing location of bundle "
            + getDefault().getBundle(), ex);
      }

      String location = CONTEXT_PATH + "server.xml";
      String name = "server";
      Container parent = ExampleServerPlugin.getNet4jContainer();
      ClassLoader classLoader = getDefault().getClassLoader();
      serverContainer = new ContainerImpl(baseResourcePath, location, name, parent, classLoader);
    }

    return serverContainer;
  }
}
