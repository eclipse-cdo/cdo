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
package org.eclipse.emf.internal.cdo.session.remote;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

import org.eclipse.net4j.util.container.Container;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionManagerImpl extends Container<CDORemoteSession> implements InternalCDORemoteSessionManager
{
  private InternalCDOSession localSession;

  private boolean forceSubscription;

  private boolean subscribed;

  private Map<Integer, CDORemoteSession> remoteSessions = new HashMap<Integer, CDORemoteSession>();

  public CDORemoteSessionManagerImpl(InternalCDOSession localSession)
  {
    this.localSession = localSession;
  }

  public InternalCDOSession getLocalSession()
  {
    return localSession;
  }

  public synchronized CDORemoteSession getRemoteSession(int sessionId)
  {
    return remoteSessions.get(sessionId);
  }

  public synchronized CDORemoteSession[] getRemoteSessions()
  {
    return remoteSessions.values().toArray(new CDORemoteSession[remoteSessions.size()]);
  }

  public CDORemoteSession[] getElements()
  {
    return getRemoteSessions();
  }

  public synchronized boolean isSubscribed()
  {
    return subscribed;
  }

  public synchronized boolean isForceSubscription()
  {
    return forceSubscription;
  }

  public synchronized void setForceSubscription(boolean forceSubscription)
  {
    this.forceSubscription = forceSubscription;
    if (forceSubscription && !subscribed)
    {
      subscribe();
    }
  }

  private void subscribe()
  {
  }
}
