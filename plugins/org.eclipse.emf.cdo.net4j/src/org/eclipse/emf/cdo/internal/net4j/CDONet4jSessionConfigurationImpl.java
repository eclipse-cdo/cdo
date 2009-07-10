/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.CDOSession;

import org.eclipse.emf.internal.cdo.session.CDOSessionConfigurationImpl;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.signal.failover.NOOPFailOverStrategy;
import org.eclipse.net4j.util.CheckUtil;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionConfigurationImpl extends CDOSessionConfigurationImpl implements
    org.eclipse.emf.cdo.net4j.CDOSessionConfiguration
{
  private String repositoryName;

  private IConnector connector;

  private IFailOverStrategy failOverStrategy;

  public CDONet4jSessionConfigurationImpl()
  {
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

  public IConnector getConnector()
  {
    return connector;
  }

  public void setConnector(IConnector connector)
  {
    checkNotOpen();
    this.connector = connector;
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

  @Override
  public org.eclipse.emf.cdo.net4j.CDOSession openSession()
  {
    return (org.eclipse.emf.cdo.net4j.CDOSession)super.openSession();
  }

  @Override
  protected InternalCDOSession createSession()
  {
    if (isActivateOnOpen())
    {
      CheckUtil.checkState(connector != null ^ failOverStrategy != null,
          "Specify exactly one of connector or failOverStrategy"); //$NON-NLS-1$
    }

    CDONet4jSessionImpl session = new CDONet4jSessionImpl();
    session.setRepository(new TemporaryRepositoryName(repositoryName));
    if (connector != null)
    {
      session.setFailOverStrategy(new NOOPFailOverStrategy(connector));
    }
    else if (failOverStrategy != null)
    {
      session.setFailOverStrategy(failOverStrategy);
    }

    return session;
  }

  /**
   * @author Eike Stepper
   */
  private static final class TemporaryRepositoryName implements CDOSession.Repository
  {
    private String name;

    public TemporaryRepositoryName(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }

    public long getCreationTime()
    {
      throw new UnsupportedOperationException();
    }

    public long getCurrentTime()
    {
      throw new UnsupportedOperationException();
    }

    public long getCurrentTime(boolean forceRefresh)
    {
      throw new UnsupportedOperationException();
    }

    public String getUUID()
    {
      throw new UnsupportedOperationException();
    }

    public boolean isSupportingAudits()
    {
      throw new UnsupportedOperationException();
    }
  }
}
