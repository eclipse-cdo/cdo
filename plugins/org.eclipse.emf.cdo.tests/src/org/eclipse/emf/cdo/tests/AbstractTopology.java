/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;


import org.eclipse.emf.cdo.client.ResourceManager;

import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.net4j.spring.Container;
import org.eclipse.net4j.spring.impl.ContainerImpl;

import org.osgi.framework.Bundle;

import java.net.URL;


public abstract class AbstractTopology implements ITopology
{
  protected static final String NET4J_LOCATION = "META-INF/net4j/net4j.xml";

  protected static final String NET4J_CLIENT_LOCATION = "META-INF/net4j/client/net4j-client.xml";

  protected static final String NET4J_SERVER_LOCATION = "META-INF/net4j/server/net4j-server.xml";

  protected static final String NET4J_EMBEDDED_LOCATION = "META-INF/net4j/embedded/net4j-embedded.xml";

  protected static final String CDO_CLIENT_LOCATION = "META-INF/cdo/client/cdo-client.xml";

  protected static final String CDO_SERVER_LOCATION = "META-INF/cdo/server/cdo-server.xml";

  private String bundleLocation;

  private Container cdoClient;

  protected AbstractTopology()
  {
  }

  public void start() throws Exception
  {
    CDOTestPlugin plugin = CDOTestPlugin.getPlugin();
    Bundle bundle = plugin.getBundle();
    URL url = bundle.getEntry("/");
    bundleLocation = FileLocator.toFileURL(url).getFile();
  }

  public void stop() throws Exception
  {
    cdoClient.stop();
    cdoClient = null;
  }

  public ResourceManager createResourceManager(ResourceSet resourceSet)
  {
    ResourceManager resourceManager = (ResourceManager) cdoClient.getBean("resourceManager");
    resourceManager.setResourceSet(resourceSet);

    try
    {
      resourceManager.start();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }

    return resourceManager;
  }

  protected void createCDOClient(String name, Container parent)
  {
    cdoClient = createContainer(name, CDO_CLIENT_LOCATION, parent);
  }

  protected ContainerImpl createContainer(String name, String location, Container parent)
  {
    return new ContainerImpl(bundleLocation, location, name, parent, CDOTestPlugin.class
        .getClassLoader());
  }
}
