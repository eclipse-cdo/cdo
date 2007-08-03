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

import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDOIDProvider;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.internal.server.protocol.InvalidationRequest;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;
import org.eclipse.emf.cdo.server.ISession;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Session implements ISession, CDOIDProvider
{
  private SessionManager sessionManager;

  private CDOServerProtocol protocol;

  private int sessionID;

  private Set<CDOID> knownObjects = new HashSet();

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

  public CDOID provideCDOID(Object idObject)
  {
    CDOID id = (CDOID)idObject;
    if (knownObjects.contains(id))
    {
      return id;
    }

    knownObjects.add(id);
    CDOClassRef type = sessionManager.getRepository().getType(id);
    return CDOIDImpl.create(id.getValue(), type);
  }
}
