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
package org.eclipse.emf.cdo.tests.topology;


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


public abstract class AbstractTopologyTest extends TestCase implements ITopologyConstants
{
  private String mode;

  private ITopology topology;

  public String getMode()
  {
    return mode;
  }

  public void setMode(String mode)
  {
    this.mode = mode;
  }

  public ITopology getTopology()
  {
    return topology;
  }

  public void setTopology(ITopology topology)
  {
    this.topology = topology;
  }

  @Override
  protected void setUp() throws Exception
  {
    if (topology == null) topology = createTopology();
    System.out.println("=========================================================================");
    System.out.println("TC_START " + getName() + " [" + topology.getName() + "]");
    System.out.println("=========================================================================");

    super.setUp();
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
    System.out.println("TC_END " + getName() + " [" + topology.getName() + "]");
    System.out.println("=========================================================================");
    System.out.println();
    System.out.println();
    System.out.println();
    System.out.println();
  }

  protected void wipeDatabase(JdbcTemplate jdbc)
  {
    dropTable(jdbc, "CDO_ATTRIBUTE");
    dropTable(jdbc, "CDO_CLASS");
    dropTable(jdbc, "CDO_CONTENT");
    dropTable(jdbc, "CDO_OBJECT");
    dropTable(jdbc, "CDO_PACKAGE");
    dropTable(jdbc, "CDO_REFERENCE");
    dropTable(jdbc, "CDO_RESOURCE");
  }

  protected void dropTable(JdbcTemplate jdbc, String tableName)
  {
    try
    {
      jdbc.execute("DROP TABLE " + tableName);
    }
    catch (Exception ignore)
    {
      ; // Intentionally left empty
    }
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

  protected CDOResource createResource(String path, ResourceManager resourceManager)
  {
    URI uri = CDOResourceFactoryImpl.formatURI(path);
    return (CDOResource) resourceManager.createResource(uri);
  }

  protected CDOResource createResource(String path)
  {
    return createResource(path, createResourceManager());
  }

  protected CDOResource getResource(String path, ResourceManager resourceManager)
  {
    URI uri = CDOResourceFactoryImpl.formatURI(path);
    return (CDOResource) resourceManager.getResource(uri, true);
  }

  protected CDOResource getResource(String path)
  {
    return getResource(path, createResourceManager());
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
    if (mode == null)
    {
      mode = System.getProperty(CDO_TEST_MODE_KEY, DEFAULT_MODE);
    }

    if (EMBEDDED_MODE.equalsIgnoreCase(mode))
    {
      return new EmbeddedTopology();
    }

    if (CLIENT_MODE.equalsIgnoreCase(mode))
    {
      return new ClientTopology();
    }

    if (CLIENT_SERVER_MODE.equalsIgnoreCase(mode))
    {
      return new ClientServerTopology();
    }

    if (CLIENT_SEPARATED_SERVER_MODE.equalsIgnoreCase(mode))
    {
      return new ClientSeparatedServerTopology();
    }

    fail("Topology not recognized: " + mode);
    return null; // Make compiler happy
  }

  protected void assertTrue(Object object)
  {
    assertNotNull(object);
    assertSame(Boolean.class, object.getClass());
    assertTrue(((Boolean) object).booleanValue());
  }

  protected void assertFalse(Object object)
  {
    assertNotNull(object);
    assertSame(Boolean.class, object.getClass());
    assertFalse(((Boolean) object).booleanValue());
  }
}
