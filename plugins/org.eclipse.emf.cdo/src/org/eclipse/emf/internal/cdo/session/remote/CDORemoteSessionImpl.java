/*
 * Copyright (c) 2009-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session.remote;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSession;
import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDORemoteSessionImpl implements InternalCDORemoteSession
{
  private InternalCDORemoteSessionManager manager;

  private int sessionID;

  private String userID;

  private boolean subscribed;

  public CDORemoteSessionImpl(InternalCDORemoteSessionManager manager, int sessionID, String userID)
  {
    this.manager = manager;
    this.sessionID = sessionID;
    this.userID = userID;
  }

  @Override
  public InternalCDORemoteSessionManager getManager()
  {
    return manager;
  }

  @Override
  public int getSessionID()
  {
    return sessionID;
  }

  @Override
  public String getUserID()
  {
    return userID;
  }

  @Override
  public boolean isSubscribed()
  {
    return subscribed;
  }

  @Override
  public void setSubscribed(boolean subscribed)
  {
    this.subscribed = subscribed;
  }

  @Override
  public boolean sendMessage(CDORemoteSessionMessage message)
  {
    if (!isSubscribed())
    {
      return false;
    }

    return manager.sendMessage(message, this).size() == 1;
  }

  @Override
  public int compareTo(CDORemoteSession obj)
  {
    int result = userID.compareTo(obj.getUserID());
    if (result == 0)
    {
      result = Integer.valueOf(sessionID).compareTo(obj.getSessionID());
    }

    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORemoteSession)
    {
      CDORemoteSession that = (CDORemoteSession)obj;
      return manager == that.getManager() && sessionID == that.getSessionID();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return manager.hashCode() ^ sessionID;
  }

  @Override
  public String toString()
  {
    String repo = manager.getLocalSession().getRepositoryInfo().getName();
    String user = userID == null ? repo : userID + "@" + repo;
    return MessageFormat.format("{0} ({1})", user, sessionID); //$NON-NLS-1$
  }
}
