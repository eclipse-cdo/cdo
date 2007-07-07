/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.InvalidationRequest;
import org.eclipse.emf.cdo.server.ISession;

/**
 * @author Eike Stepper
 */
public class Session implements ISession
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  public Session(SessionManager sessionManager, CDOServerProtocol protocol, int sessionID)
  {
    this.sessionManager = sessionManager;
    this.protocol = protocol;
    this.sessionID = sessionID;
  }

  public SessionManager getSessionManager()
  {
    return sessionManager;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public CDOServerProtocol getProtocol()
  {
    return protocol;
  }

  public void notifyInvalidation(long timeStamp, CDORevisionImpl[] dirtyObjects)
  {
    try
    {
      new InvalidationRequest(protocol.getChannel(), timeStamp, dirtyObjects).send();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }
}
