/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.RecoveringCDOSessionConfiguration;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Caspar De Groot
 */
public abstract class RecoveringCDOSessionConfigurationImpl extends CDONet4jSessionConfigurationImpl implements RecoveringCDOSessionConfiguration
{
  private IManagedContainer container;

  private boolean heartBeatEnabled = false;

  private long heartBeatPeriod = 1000L;

  private long heartBeatTimeout = 5000L;

  private long connectorTimeout = 10000L;

  public RecoveringCDOSessionConfigurationImpl(IManagedContainer container)
  {
    this.container = container;
  }

  protected IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public long getConnectorTimeout()
  {
    return connectorTimeout;
  }

  @Override
  public void setConnectorTimeout(long timeout)
  {
    connectorTimeout = timeout;
  }

  @Override
  public boolean isHeartBeatEnabled()
  {
    return heartBeatEnabled;
  }

  @Override
  public void setHeartBeatEnabled(boolean enabled)
  {
    heartBeatEnabled = enabled;
  }

  @Override
  public long getHeartBeatTimeout()
  {
    return heartBeatTimeout;
  }

  @Override
  public void setHeartBeatTimeout(long timeout)
  {
    heartBeatTimeout = timeout;
  }

  @Override
  public long getHeartBeatPeriod()
  {
    return heartBeatPeriod;
  }

  @Override
  public void setHeartBeatPeriod(long period)
  {
    heartBeatPeriod = period;
  }

  @Override
  protected void configureSession(InternalCDOSession session)
  {
    super.configureSession(session);

    if (heartBeatEnabled && (heartBeatPeriod == 0 || heartBeatTimeout == 0))
    {
      throw new IllegalStateException("Cannot use a heartbeat with zero value set for period or timeout.");
    }

    RecoveringCDOSessionImpl sessionImpl = (RecoveringCDOSessionImpl)session;
    sessionImpl.setContainer(getContainer());
    sessionImpl.setUseHeartBeat(heartBeatEnabled);
    sessionImpl.setHeartBeatPeriod(heartBeatPeriod);
    sessionImpl.setHeartBeatTimeout(heartBeatTimeout);
    sessionImpl.setConnectorTimeout(connectorTimeout);
  }
}
