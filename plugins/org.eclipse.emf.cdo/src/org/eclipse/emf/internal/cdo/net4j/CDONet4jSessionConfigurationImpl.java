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
package org.eclipse.emf.internal.cdo.net4j;

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
  private IConnector connector;

  private IFailOverStrategy failOverStrategy;

  public CDONet4jSessionConfigurationImpl()
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
    CheckUtil.checkState(connector != null ^ failOverStrategy != null,
        "Specify exactly one of connector or failOverStrategy");

    CDONet4jSessionImpl session = new CDONet4jSessionImpl();
    if (connector != null)
    {
      session.options().getProtocol().setFailOverStrategy(new NOOPFailOverStrategy(connector));
    }
    else
    {
      session.options().getProtocol().setFailOverStrategy(failOverStrategy);
    }

    return session;
  }
}
