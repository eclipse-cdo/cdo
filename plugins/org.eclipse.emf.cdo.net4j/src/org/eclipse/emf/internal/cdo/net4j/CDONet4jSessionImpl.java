/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/226778
 *    Simon McDuff - http://bugs.eclipse.org/230832
 *    Simon McDuff - http://bugs.eclipse.org/233490
 *    Simon McDuff - http://bugs.eclipse.org/213402
 *    Victor Roldan Betancort - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j;

import org.eclipse.emf.internal.cdo.net4j.bundle.OM;
import org.eclipse.emf.internal.cdo.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionImpl extends CDOSessionImpl implements org.eclipse.emf.cdo.net4j.CDOSession
{
  private IFailOverStrategy failOverStrategy;

  public CDONet4jSessionImpl()
  {
  }

  public void setFailOverStrategy(IFailOverStrategy failOverStrategy)
  {
    this.failOverStrategy = failOverStrategy;
  }

  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  protected CDOClientProtocol createSessionProtocol()
  {
    CDOClientProtocol protocol = new CDOClientProtocol();
    protocol.setInfraStructure(this);
    protocol.setFailOverStrategy(options().getFailOverStrategy());
    return protocol;
  }

  /**
   * @author Eike Stepper
   */
  protected class OptionsImpl extends org.eclipse.emf.internal.cdo.session.CDOSessionImpl.OptionsImpl implements
      org.eclipse.emf.cdo.net4j.CDOSession.Options
  {
    private int commitTimeout = OM.PREF_COMMIT_MONITOR_TIMEOUT_SECONDS.getValue();

    private int progressInterval = OM.PREF_COMMIT_MONITOR_PROGRESS_SECONDS.getValue();

    public OptionsImpl()
    {
    }

    public IFailOverStrategy getFailOverStrategy()
    {
      return failOverStrategy;
    }

    public CDOClientProtocol getProtocol()
    {
      CDOSessionProtocol protocol = getSessionProtocol();
      if (protocol instanceof DelegatingSessionProtocol)
      {
        protocol = ((DelegatingSessionProtocol)protocol).getDelegate();
      }

      return (CDOClientProtocol)protocol;
    }

    public int getCommitTimeout()
    {
      return commitTimeout;
    }

    public synchronized void setCommitTimeout(int commitTimeout)
    {
      this.commitTimeout = commitTimeout;
    }

    public int getProgressInterval()
    {
      return progressInterval;
    }

    public synchronized void setProgressInterval(int progressInterval)
    {
      this.progressInterval = progressInterval;
    }
  }
}
