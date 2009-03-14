/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/202725
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.internal.server.protocol.CDOServerProtocol;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.SessionCreationException;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SessionManager extends Container<ISession> implements ISessionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, SessionManager.class);

  private IRepository repository;

  private Map<Integer, Session> sessions = new HashMap<Integer, Session>();

  private int lastSessionID;

  /**
   * @since 2.0
   */
  public SessionManager()
  {
  }

  /**
   * @since 2.0
   */
  public IRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 2.0
   */
  public void setRepository(IRepository repository)
  {
    this.repository = repository;
  }

  public Session[] getSessions()
  {
    synchronized (sessions)
    {
      return sessions.values().toArray(new Session[sessions.size()]);
    }
  }

  /**
   * @since 2.0
   */
  public Session getSession(int sessionID)
  {
    checkActive();
    synchronized (sessions)
    {
      return sessions.get(sessionID);
    }
  }

  public ISession[] getElements()
  {
    return getSessions();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (sessions)
    {
      return sessions.isEmpty();
    }
  }

  /**
   * @since 2.0
   */
  public Session openSession(CDOServerProtocol protocol) throws SessionCreationException
  {
    int id = ++lastSessionID;
    if (TRACER.isEnabled())
    {
      TRACER.trace("Opening session " + id);
    }

    Session session = new Session(this, protocol, id);
    synchronized (sessions)
    {
      sessions.put(id, session);
    }

    fireElementAddedEvent(session);
    handleRemoteSessionNotification(CDOProtocolConstants.REMOTE_SESSION_OPENED, session);
    return session;
  }

  public void sessionClosed(Session session)
  {
    int sessionID = session.getSessionID();
    ISession removeSession = null;
    synchronized (sessions)
    {
      removeSession = sessions.remove(sessionID);
    }

    if (removeSession != null)
    {
      fireElementRemovedEvent(session);
      handleRemoteSessionNotification(CDOProtocolConstants.REMOTE_SESSION_CLOSED, session);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    Session[] activeSessions = getSessions();
    for (int i = 0; i < activeSessions.length; i++)
    {
      LifecycleUtil.deactivate(activeSessions[i]);
    }

    super.doDeactivate();
  }

  /**
   * @since 2.0
   */
  public void handleCommitNotification(long timeStamp, CDOPackageUnit[] packageUnits, List<CDOIDAndVersion> dirtyIDs,
      List<CDOID> detachedObjects, List<CDORevisionDelta> deltas, Session excludedSession)
  {
    for (Session session : getSessions())
    {
      if (session != excludedSession)
      {
        session.handleCommitNotification(timeStamp, packageUnits, dirtyIDs, detachedObjects, deltas);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void handleRemoteSessionNotification(byte opcode, Session excludedSession)
  {
    for (Session session : getSessions())
    {
      if (session != excludedSession && session.isSubscribed())
      {
        session.handleRemoteSessionNotification(opcode, excludedSession);
      }
    }
  }
}
