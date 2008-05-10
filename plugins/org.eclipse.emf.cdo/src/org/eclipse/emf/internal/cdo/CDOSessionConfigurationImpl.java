/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;

import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;

/**
 * @author Eike Stepper
 */
public class CDOSessionConfigurationImpl implements CDOSessionConfiguration
{
  private CDOSessionImpl session;

  private IConnector connector;

  private String repositoryName;

  private boolean disableLegacyObjects;

  private IFailOverStrategy failOverStrategy;

  private CDOPackageRegistryImpl packageRegistry;

  public CDOSessionConfigurationImpl()
  {
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    this.connector = connector;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    this.repositoryName = repositoryName;
  }

  public boolean isDisableLegacyObjects()
  {
    return disableLegacyObjects;
  }

  public void setDisableLegacyObjects(boolean disableLegacyObjects)
  {
    this.disableLegacyObjects = disableLegacyObjects;
  }

  public IFailOverStrategy getFailOverStrategy()
  {
    return failOverStrategy;
  }

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    this.failOverStrategy = failOverStrategy;
  }

  public CDOPackageRegistryImpl getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(CDOPackageRegistryImpl packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public CDOSession openSession()
  {
    if (!isSessionOpen())
    {
      session = new CDOSessionImpl();
      session.setConnector(connector);
      session.setRepositoryName(repositoryName);
      session.setDisableLegacyObjects(disableLegacyObjects);
      session.setFailOverStrategy(failOverStrategy);
      session.setPackageRegistry(packageRegistry);
      session.activate();
    }

    return session;
  }

  public boolean isSessionOpen()
  {
    if (session == null)
    {
      return false;
    }

    if (session.isOpen())
    {
      return true;
    }

    session = null;
    return false;
  }
}
