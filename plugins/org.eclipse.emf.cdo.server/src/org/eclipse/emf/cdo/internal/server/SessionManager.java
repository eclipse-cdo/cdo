/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 202725
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.DiffieHellman;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.UserManagerAuthenticator;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class SessionManager extends Container<ISession> implements InternalSessionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, SessionManager.class);

  private InternalRepository repository;

  private DiffieHellman.Server authenticationServer;

  private IAuthenticator authenticator;

  private IPermissionManager permissionManager;

  private final Map<Integer, InternalSession> sessions = new HashMap<Integer, InternalSession>();

  private final AtomicInteger lastSessionID = new AtomicInteger();

  /**
   * @since 2.0
   */
  public SessionManager()
  {
  }

  /**
   * @since 2.0
   */
  public InternalRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 2.0
   */
  public void setRepository(InternalRepository repository)
  {
    checkInactive();
    this.repository = repository;
  }

  @Deprecated
  public IUserManager getUserManager()
  {
    if (authenticator instanceof UserManagerAuthenticator)
    {
      return ((UserManagerAuthenticator)authenticator).getUserManager();
    }

    return null;
  }

  @Deprecated
  public void setUserManager(IUserManager userManager)
  {
    UserManagerAuthenticator userManagerAuthenticator = new UserManagerAuthenticator();
    userManagerAuthenticator.setUserManager(userManager);

    setAuthenticator(userManagerAuthenticator);
  }

  public DiffieHellman.Server getAuthenticationServer()
  {
    return authenticationServer;
  }

  public void setAuthenticationServer(DiffieHellman.Server authenticationServer)
  {
    this.authenticationServer = authenticationServer;
  }

  public IAuthenticator getAuthenticator()
  {
    return authenticator;
  }

  public void setAuthenticator(IAuthenticator authenticator)
  {
    this.authenticator = authenticator;
    if (isActive() && authenticator != null)
    {
      initAuthentication();
    }
  }

  public IPermissionManager getPermissionManager()
  {
    return permissionManager;
  }

  public void setPermissionManager(IPermissionManager permissionManager)
  {
    this.permissionManager = permissionManager;
  }

  public InternalSession[] getSessions()
  {
    synchronized (sessions)
    {
      return sessions.values().toArray(new InternalSession[sessions.size()]);
    }
  }

  /**
   * @since 2.0
   */
  public InternalSession getSession(int sessionID)
  {
    checkActive();
    synchronized (sessions)
    {
      return sessions.get(sessionID);
    }
  }

  public InternalSession[] getElements()
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
  public InternalSession openSession(ISessionProtocol sessionProtocol)
  {
    int id = lastSessionID.incrementAndGet();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Opening session " + id); //$NON-NLS-1$
    }

    String userID = authenticateUser(sessionProtocol);
    InternalSession session = createSession(id, userID, sessionProtocol);
    LifecycleUtil.activate(session);

    synchronized (sessions)
    {
      sessions.put(id, session);
    }

    fireElementAddedEvent(session);
    sendRemoteSessionNotification(session, CDOProtocolConstants.REMOTE_SESSION_OPENED);
    return session;
  }

  protected InternalSession createSession(int id, String userID, ISessionProtocol protocol)
  {
    return new Session(this, protocol, id, userID);
  }

  public void sessionClosed(InternalSession session)
  {
    int sessionID = session.getSessionID();
    InternalSession removeSession = null;
    synchronized (sessions)
    {
      removeSession = sessions.remove(sessionID);
    }

    if (removeSession != null)
    {
      fireElementRemovedEvent(session);
      sendRemoteSessionNotification(session, CDOProtocolConstants.REMOTE_SESSION_CLOSED);
    }
  }

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
  {
    for (InternalSession session : getSessions())
    {
      try
      {
        session.sendRepositoryTypeNotification(oldType, newType);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(session, ex);
      }
    }
  }

  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState,
      CDOID rootResourceID)
  {
    for (InternalSession session : getSessions())
    {
      try
      {
        session.sendRepositoryStateNotification(oldState, newState, rootResourceID);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(session, ex);
      }
    }
  }

  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendBranchNotification(branch);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    sendCommitNotification(sender, commitInfo, true);
  }

  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo, boolean clearResourcePathCache)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendCommitNotification(commitInfo, clearResourcePathCache);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  public void sendLockNotification(InternalSession sender, CDOLockChangeInfo lockChangeInfo)
  {
    for (InternalSession session : getSessions())
    {
      if (session == sender || session.options().getLockNotificationMode() == LockNotificationMode.OFF)
      {
        continue;
      }

      try
      {
        session.sendLockNotification(lockChangeInfo);
      }
      catch (Exception ex)
      {
        handleNotificationProblem(session, ex);
      }
    }
  }

  /**
   * @since 2.0
   */
  public void sendRemoteSessionNotification(InternalSession sender, byte opcode)
  {
    try
    {
      for (InternalSession session : getSessions())
      {
        if (session != sender && session.isSubscribed())
        {
          try
          {
            session.sendRemoteSessionNotification(sender, opcode);
          }
          catch (Exception ex)
          {
            handleNotificationProblem(session, ex);
          }
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.warn("A problem occured while notifying other sessions", ex);
    }
  }

  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message,
      int[] recipients)
  {
    List<Integer> result = new ArrayList<Integer>();
    for (int i = 0; i < recipients.length; i++)
    {
      InternalSession recipient = getSession(recipients[i]);

      try
      {
        if (recipient != null && recipient.isSubscribed())
        {
          recipient.sendRemoteMessageNotification(sender, message);
          result.add(recipient.getSessionID());
        }
      }
      catch (Exception ex)
      {
        handleNotificationProblem(recipient, ex);
      }
    }

    return result;
  }

  protected void handleNotificationProblem(InternalSession session, Throwable t)
  {
    OM.LOG.warn("A problem occured while notifying session " + session, t);
  }

  protected String authenticateUser(ISessionProtocol protocol) throws SecurityException
  {
    if (protocol == null)
    {
      return null;
    }

    if (authenticationServer == null || authenticator == null)
    {
      return null;
    }

    try
    {
      Challenge challenge = authenticationServer.getChallenge();
      Response response = protocol.sendAuthenticationChallenge(challenge);
      if (response == null)
      {
        throw new NotAuthenticatedException();
      }

      ByteArrayInputStream baos = new ByteArrayInputStream(authenticationServer.handleResponse(response));
      ExtendedDataInputStream stream = new ExtendedDataInputStream(baos);
      String userID = stream.readString();
      char[] password = stream.readString().toCharArray();

      authenticator.authenticate(userID, password);
      return userID;
    }
    catch (SecurityException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      Throwable cause = ex.getCause();
      if (cause instanceof SecurityException)
      {
        throw (SecurityException)cause;
      }

      throw new SecurityException(ex);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    initAuthentication();
  }

  protected void initAuthentication()
  {
    if (authenticator != null)
    {
      if (authenticationServer == null)
      {
        authenticationServer = new DiffieHellman.Server();
      }

      LifecycleUtil.activate(authenticationServer);
      LifecycleUtil.activate(authenticator);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(authenticator);
    LifecycleUtil.deactivate(authenticationServer);

    for (InternalSession session : getSessions())
    {
      LifecycleUtil.deactivate(session);
    }

    super.doDeactivate();
  }
}
