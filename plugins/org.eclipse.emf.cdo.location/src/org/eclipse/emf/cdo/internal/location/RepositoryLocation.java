/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.location;

import org.eclipse.emf.cdo.location.ICheckoutSource;
import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class RepositoryLocation extends Container<ICheckoutSource> implements IRepositoryLocation
{
  private static final String PROP_CONNECTOR_TYPE = "connector.type";

  private static final String PROP_CONNECTOR_DESCRIPTION = "connector.description";

  private static final String PROP_REPOSITORY_NAME = "repository.name";

  private RepositoryLocationManager manager;

  private String connectorType;

  private String connectorDescription;

  private String repositoryName;

  private ICheckoutSource[] elements = { new BranchCheckoutSource.Main(this) };

  public RepositoryLocation(RepositoryLocationManager manager, String connectorType, String connectorDescription,
      String repositoryName)
  {
    this.manager = manager;
    this.connectorType = connectorType;
    this.connectorDescription = connectorDescription;
    this.repositoryName = repositoryName;
    activate();
  }

  public RepositoryLocation(RepositoryLocationManager manager, InputStream in) throws IOException
  {
    Properties properties = new Properties();
    properties.load(in);

    this.manager = manager;
    connectorType = properties.getProperty(PROP_CONNECTOR_TYPE);
    connectorDescription = properties.getProperty(PROP_CONNECTOR_DESCRIPTION);
    repositoryName = properties.getProperty(PROP_REPOSITORY_NAME);
    activate();
  }

  public void write(OutputStream out) throws IOException
  {
    Properties properties = new Properties();
    properties.put(PROP_CONNECTOR_TYPE, connectorType);
    properties.put(PROP_CONNECTOR_DESCRIPTION, connectorDescription);
    properties.put(PROP_REPOSITORY_NAME, repositoryName);
    properties.store(out, "Repository Location");
  }

  public RepositoryLocationManager getManager()
  {
    return manager;
  }

  public String getConnectorType()
  {
    return connectorType;
  }

  public String getConnectorDescription()
  {
    return connectorDescription;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public ICheckoutSource[] getElements()
  {
    return elements;
  }

  public CDOSessionConfiguration createSessionConfiguration()
  {
    IConnector connector = getConnector();

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(getRepositoryName());
    return config;
  }

  public void delete()
  {
    if (manager != null)
    {
      manager.removeRepositoryLocation(this);
      manager = null;

      connectorType = null;
      connectorDescription = null;
      repositoryName = null;
    }
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof IRepositoryLocation)
    {
      IRepositoryLocation that = (IRepositoryLocation)obj;
      return ObjectUtil.equals(connectorType, that.getConnectorType())
          && ObjectUtil.equals(connectorDescription, that.getConnectorDescription())
          && ObjectUtil.equals(repositoryName, that.getRepositoryName());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(connectorType) ^ ObjectUtil.hashCode(connectorDescription)
        ^ ObjectUtil.hashCode(repositoryName);
  }

  @Override
  public String toString()
  {
    return connectorType + "://" + connectorDescription + "/" + repositoryName;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  private IConnector getConnector()
  {
    IManagedContainer container = getContainer();
    return Net4jUtil.getConnector(container, connectorType, connectorDescription);
  }
}
