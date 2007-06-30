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
import org.eclipse.emf.cdo.internal.server.bundle.CDOServer;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.server.SessionManager;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SessionManagerImpl implements SessionManager
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_SESSION, SessionManagerImpl.class);

  private RepositoryImpl repository;

  private Map<Integer, SessionImpl> sessions = new HashMap();

  private int lastSessionID;

  public SessionManagerImpl(RepositoryImpl repository)
  {
    this.repository = repository;
  }

  public RepositoryImpl getRepository()
  {
    return repository;
  }

  public SessionImpl[] getSessions()
  {
    synchronized (sessions)
    {
      return sessions.values().toArray(new SessionImpl[sessions.size()]);
    }
  }

  public SessionImpl openSession(CDOServerProtocol protocol)
  {
    int id = ++lastSessionID;
    if (TRACER.isEnabled())
    {
      TRACER.trace("Opening session " + id);
    }

    SessionImpl session = new SessionImpl(this, protocol, id);
    synchronized (sessions)
    {
      sessions.put(id, session);
    }

    return session;
  }

  public void sessionClosed(SessionImpl session)
  {
  }

  public void notifyInvalidation(long timeStamp, CDORevisionImpl[] dirtyObjects, SessionImpl excludedSession)
  {
    for (SessionImpl session : getSessions())
    {
      if (session != excludedSession)
      {
        session.notifyInvalidation(timeStamp, dirtyObjects);
      }
    }
  }
}
