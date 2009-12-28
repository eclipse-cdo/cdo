/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 226778
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.net4j.signal.ISignalProtocol;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

/**
 * @author Eike Stepper
 */
public class CDONet4jSessionImpl extends CDOSessionImpl implements org.eclipse.emf.cdo.net4j.CDOSession
{
  public CDONet4jSessionImpl(CDONet4jSessionConfigurationImpl configuration)
  {
    super(configuration);
  }

  @Override
  public CDONet4jSessionConfigurationImpl getConfiguration()
  {
    return (CDONet4jSessionConfigurationImpl)super.getConfiguration();
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return getConfiguration().getPackageRegistry();
  }

  public InternalCDORevisionManager getRevisionManager()
  {
    return getConfiguration().getRevisionManager();
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

    public ISignalProtocol<org.eclipse.emf.cdo.net4j.CDOSession> getProtocol()
    {
      CDOSessionProtocol protocol = getSessionProtocol();
      if (protocol instanceof DelegatingSessionProtocol)
      {
        protocol = ((DelegatingSessionProtocol)protocol).getDelegate();
      }

      @SuppressWarnings("unchecked")
      ISignalProtocol<CDOSession> signalProtocol = (ISignalProtocol<CDOSession>)protocol;
      return signalProtocol;
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
