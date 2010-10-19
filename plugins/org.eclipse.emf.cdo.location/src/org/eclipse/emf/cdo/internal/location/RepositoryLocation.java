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
package org.eclipse.emf.cdo.internal.location;

import org.eclipse.emf.cdo.location.ICheckoutSource;
import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

/**
 * @author Eike Stepper
 */
public class RepositoryLocation extends Container<ICheckoutSource> implements IRepositoryLocation
{
  private static final long serialVersionUID = 1L;

  private RepositoryLocationManager manager;

  private String connectorType;

  private String connectorDescription;

  private String repositoryName;

  private ICheckoutSource[] elements = { new BranchCheckoutSource.Main(this) };

  public RepositoryLocation()
  {
  }

  public RepositoryLocation(RepositoryLocationManager manager, String connectorType, String connectorDescription,
      String repositoryName)
  {
    this.manager = manager;
    this.connectorType = connectorType;
    this.connectorDescription = connectorDescription;
    this.repositoryName = repositoryName;
    activate();
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

    org.eclipse.emf.cdo.net4j.CDOSessionConfiguration config = CDONet4jUtil.createSessionConfiguration();
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
