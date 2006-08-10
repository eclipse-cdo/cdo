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

import junit.framework.TestCase;


public class AbstractIntegrationTest extends TestCase
{
  public static final String CLIENT_SEPARATED_SERVER_MODE = "client-separated-server";

  public static final String CLIENT_SERVER_MODE = "client-server";

  public static final String CLIENT_MODE = "client";

  public static final String EMBEDDED_MODE = "embedded";

  private ITopology topology;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    topology = createTopology();
    topology.start();
  }

  @Override
  protected void tearDown() throws Exception
  {
    topology.stop();
    super.tearDown();
  }

  protected ResourceManager createResourceManager(ResourceSet resourceSet)
  {
    return topology.createResourceManager(resourceSet);
  }

  protected ITopology createTopology()
  {
    String mode = getMode();
    if (EMBEDDED_MODE.equals(mode))
    {
      return new EmbeddedTopology();
    }

    if (CLIENT_MODE.equals(mode))
    {
      return new ClientTopology();
    }

    if (CLIENT_SERVER_MODE.equals(mode))
    {
      return new ClientServerTopology();
    }

    if (CLIENT_SEPARATED_SERVER_MODE.equals(mode))
    {
      return new ClientSeparatedServerTopology();
    }

    return null;
  }

  protected String getMode()
  {
    return System.getProperty("cdo.test.mode", EMBEDDED_MODE).toLowerCase();
  }
}
