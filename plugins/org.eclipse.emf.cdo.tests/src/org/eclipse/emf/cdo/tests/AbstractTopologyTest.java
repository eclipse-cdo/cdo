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


import org.eclipse.emf.cdo.client.CDOResource;
import org.eclipse.emf.cdo.client.ResourceManager;
import org.eclipse.emf.cdo.client.impl.CDOResourceFactoryImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

import javax.sql.DataSource;

import junit.framework.TestCase;


public abstract class AbstractTopologyTest extends TestCase
{
  public static final String CDO_TEST_MODE_KEY = "cdo.test.mode";

  public static final String CLIENT_SEPARATED_SERVER_MODE = "client-separated-server";

  public static final String CLIENT_SERVER_MODE = "client-server";

  public static final String CLIENT_MODE = "client";

  public static final String EMBEDDED_MODE = "embedded";

  private ITopology topology;

  @Override
  protected void setUp() throws Exception
  {
    System.out.println("=========================================================================");
    System.out.println("TC_START " + getName());
    System.out.println("=========================================================================");

    super.setUp();
    topology = createTopology();
    topology.start();
  }

  @Override
  protected void tearDown() throws Exception
  {
    JdbcTemplate jdbc = getJdbcTemplate();
    wipeDatabase(jdbc);

    topology.stop();
    super.tearDown();

    System.out.println("=========================================================================");
    System.out.println("TC_END " + getName());
    System.out.println("=========================================================================");
    System.out.println();
  }

  protected void wipeDatabase(JdbcTemplate jdbc)
  {
    jdbc.execute("DROP TABLE CDO_ATTRIBUTE");
    jdbc.execute("DROP TABLE CDO_CLASS");
    jdbc.execute("DROP TABLE CDO_CONTENT");
    jdbc.execute("DROP TABLE CDO_OBJECT");
    jdbc.execute("DROP TABLE CDO_PACKAGE");
    jdbc.execute("DROP TABLE CDO_REFERENCE");
    jdbc.execute("DROP TABLE CDO_RESOURCE");
  }

  protected ITopology getTopology()
  {
    return topology;
  }

  protected DataSource getDataSource()
  {
    return topology.getDataSource();
  }

  protected JdbcTemplate getJdbcTemplate()
  {
    return new JdbcTemplate(getDataSource());
  }

  protected ResourceManager createResourceManager(ResourceSet resourceSet)
  {
    return topology.createResourceManager(resourceSet);
  }

  protected ResourceManager createResourceManager()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    return topology.createResourceManager(resourceSet);
  }

  protected CDOResource createResource(String path)
  {
    ResourceManager resourceManager = createResourceManager();
    URI uri = CDOResourceFactoryImpl.formatURI(path);
    return (CDOResource) resourceManager.createResource(uri);
  }

  protected CDOResource getResource(String path)
  {
    ResourceManager resourceManager = createResourceManager();
    URI uri = CDOResourceFactoryImpl.formatURI(path);
    return (CDOResource) resourceManager.getResource(uri, true);
  }

  protected EObject loadRoot(String path) throws IOException
  {
    CDOResource resource = getResource(path);
    return (EObject) resource.getContents().get(0);
  }

  protected CDOResource saveRoot(EObject root, String path) throws IOException
  {
    CDOResource resource = createResource(path);
    resource.getContents().add(root);
    resource.save(null);
    return resource;
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
    return System.getProperty(CDO_TEST_MODE_KEY, EMBEDDED_MODE).toLowerCase();
  }
}
