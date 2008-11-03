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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.util.CDOPackageRegistry;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;

/**
 * @author Eike Stepper
 */
public interface CDOSessionConfiguration
{
  public IConnector getConnector();

  public void setConnector(IConnector connector);

  public String getRepositoryName();

  public void setRepositoryName(String repositoryName);

  public boolean isLegacySupportEnabled();

  public void setLegacySupportEnabled(boolean enabled);

  public IFailOverStrategy getFailOverStrategy();

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy);

  public CDOPackageRegistry getPackageRegistry();

  public void setPackageRegistry(CDOPackageRegistry packageRegistry);

  public void setSelfPopulatingPackageRegistry();

  public void setDemandPopulatingPackageRegistry();

  public CDOSession openSession();

  public boolean isSessionOpen();
}
