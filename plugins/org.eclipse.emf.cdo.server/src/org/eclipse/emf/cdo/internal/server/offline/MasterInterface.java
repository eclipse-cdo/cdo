/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.offline;

import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;

/**
 * @author Eike Stepper
 */
public class MasterInterface extends Lifecycle
{
  private CDOSessionConfiguration sessionConfiguration;

  private CDOSession session;

  private IListener sessionListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      OM.LOG.info("Disconnected from master.");
      session.removeListener(sessionListener);
      session = null;
      connect();
    }
  };

  /**
   * TODO Make configurable
   */
  private int retryInterval = 3;

  public MasterInterface()
  {
  }

  public CDOSessionConfiguration getSessionConfiguration()
  {
    return sessionConfiguration;
  }

  public void setSessionConfiguration(CDOSessionConfiguration sessionConfiguration)
  {
    checkInactive();
    this.sessionConfiguration = sessionConfiguration;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(sessionConfiguration, "sessionConfiguration");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    connect();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    session.removeListener(sessionListener);
    session.close();
    session = null;
    super.doDeactivate();
  }

  private void connect()
  {
    while (session == null)
    {
      try
      {
        exitIfInactive();
        OM.LOG.info("Connecting to master...");
        session = sessionConfiguration.openSession();
      }
      catch (Exception ex)
      {
        exitIfInactive();
        OM.LOG.warn("Connection attempt failed. Retrying in " + retryInterval + " seconds...");
        ConcurrencyUtil.sleep(1000L * retryInterval); // TODO Respect deactivation
      }
    }

    OM.LOG.info("Connected to master");
    session.addListener(sessionListener);
    sync();
  }

  private void sync()
  {
    OM.LOG.info("Synchronizing with master...");

    OM.LOG.info("Synchronized with master.");
  }

  private void exitIfInactive()
  {
    switch (getLifecycleState())
    {
    case DEACTIVATING:
      throw new IllegalStateException("Deactivating");
    case INACTIVE:
      throw new IllegalStateException("Inactive");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static enum State
  {
    OFFLINE, SYNCING, ONLINE
  }
}
