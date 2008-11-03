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
import org.eclipse.emf.cdo.util.CDOPackageRegistry;
import org.eclipse.emf.cdo.util.CDOUtil;

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

  private boolean legacySupportEnabled;

  private IFailOverStrategy failOverStrategy;

  private CDOPackageRegistryImpl packageRegistry;

  private boolean activateOnOpen = true;

  public CDOSessionConfigurationImpl()
  {
  }

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    checkNotOpen();
    this.connector = connector;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName)
  {
    checkNotOpen();
    this.repositoryName = repositoryName;
  }

  public boolean isLegacySupportEnabled()
  {
    return legacySupportEnabled;
  }

  public void setLegacySupportEnabled(boolean legacySupportEnabled)
  {
    checkNotOpen();
    this.legacySupportEnabled = legacySupportEnabled;
  }

  public IFailOverStrategy getFailOverStrategy()
  {
    return failOverStrategy;
  }

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    checkNotOpen();
    this.failOverStrategy = failOverStrategy;
  }

  /**
   * @since 1.0
   */
  public CDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  /**
   * @since 1.0
   */
  public void setPackageRegistry(CDOPackageRegistry packageRegistry)
  {
    checkNotOpen();
    this.packageRegistry = (CDOPackageRegistryImpl)packageRegistry;
  }

  public void setSelfPopulatingPackageRegistry()
  {
    setPackageRegistry(CDOUtil.createSelfPopulatingPackageRegistry());
  }

  public void setDemandPopulatingPackageRegistry()
  {
    setPackageRegistry(CDOUtil.createDemandPopulatingPackageRegistry());
  }

  public boolean isActivateOnOpen()
  {
    return activateOnOpen;
  }

  public void setActivateOnOpen(boolean activateOnOpen)
  {
    checkNotOpen();
    this.activateOnOpen = activateOnOpen;
  }

  public CDOSession openSession()
  {
    if (!isSessionOpen())
    {
      session = new CDOSessionImpl();
      session.setConnector(connector);
      session.setRepositoryName(repositoryName);
      session.setLegacySupportEnabled(legacySupportEnabled);
      session.setFailOverStrategy(failOverStrategy);
      session.setPackageRegistry(packageRegistry);

      if (activateOnOpen)
      {
        session.activate();
      }
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

  private void checkNotOpen()
  {
    if (isSessionOpen())
    {
      throw new IllegalStateException("Session is already open");
    }
  }
}
