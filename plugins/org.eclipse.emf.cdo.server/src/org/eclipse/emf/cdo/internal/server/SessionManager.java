/*
 * Copyright (c) 2007-2013, 2015-2017, 2019-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 202725
 *    Christian W. Damus (CEA LIST) - bug 399306
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.IAuthenticationProtocol;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTopic;
import org.eclipse.emf.cdo.spi.server.InternalTopicManager;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.CredentialsUpdateOperation;
import org.eclipse.net4j.util.security.DiffieHellman;
import org.eclipse.net4j.util.security.DiffieHellman.Client.Response;
import org.eclipse.net4j.util.security.DiffieHellman.Server.Challenge;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.IAuthenticator2;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.SecurityUtil;
import org.eclipse.net4j.util.security.UserManagerAuthenticator;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class SessionManager extends Container<ISession> implements InternalSessionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, SessionManager.class);

  private static final int ONE_TIME_LOGIN_TOKEN_LENGTH = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.internal.server.SessionManager.ONE_TIME_LOGIN_TOKEN_LENGTH", 2048);

  private static final long ONE_TIME_LOGIN_TOKEN_TIMEOUT = OMPlatform.INSTANCE
      .getProperty("org.eclipse.emf.cdo.internal.server.SessionManager.ONE_TIME_LOGIN_TOKEN_TIMEOUT", 10 * 1000);

  private static final Random ONE_TIME_LOGIN_TOKEN_GENERATOR = new Random(System.currentTimeMillis());

  private InternalRepository repository;

  private DiffieHellman.Server authenticationServer;

  private IAuthenticator authenticator;

  private IPermissionManager permissionManager;

  private final InternalTopicManager topicManager = new TopicManager(this);

  private final Map<Integer, InternalSession> sessions = new HashMap<>();

  private final AtomicInteger lastSessionID = new AtomicInteger();

  @ExcludeFromDump
  private final Map<OneTimeLoginToken, Long> oneTimeLoginTokens = Collections.synchronizedMap(new HashMap<>());

  private final Map<InternalSession, List<CommitNotificationInfo>> commitNotificationInfoQueues = new HashMap<>();

  private final IListener sessionListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      synchronized (commitNotificationInfoQueues)
      {
        commitNotificationInfoQueues.remove(lifecycle);
      }
    }
  };

  private InternalSession[] sessionsArray = {};

  /**
   * @since 2.0
   */
  public SessionManager()
  {
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalRepository getRepository()
  {
    return repository;
  }

  /**
   * @since 2.0
   */
  @Override
  public void setRepository(InternalRepository repository)
  {
    checkInactive();
    this.repository = repository;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return repository.getExecutorService();
  }

  @Override
  @Deprecated
  public IUserManager getUserManager()
  {
    if (authenticator instanceof UserManagerAuthenticator)
    {
      return ((UserManagerAuthenticator)authenticator).getUserManager();
    }

    return null;
  }

  @Override
  @Deprecated
  public void setUserManager(IUserManager userManager)
  {
    UserManagerAuthenticator userManagerAuthenticator = new UserManagerAuthenticator();
    userManagerAuthenticator.setUserManager(userManager);

    setAuthenticator(userManagerAuthenticator);
  }

  @Override
  public DiffieHellman.Server getAuthenticationServer()
  {
    return authenticationServer;
  }

  @Override
  public void setAuthenticationServer(DiffieHellman.Server authenticationServer)
  {
    this.authenticationServer = authenticationServer;
  }

  @Override
  public IAuthenticator getAuthenticator()
  {
    return authenticator;
  }

  @Override
  public void setAuthenticator(IAuthenticator authenticator)
  {
    this.authenticator = authenticator;
    if (isActive() && authenticator != null)
    {
      initAuthentication();
    }
  }

  @Override
  public IPermissionManager getPermissionManager()
  {
    return permissionManager;
  }

  @Override
  public void setPermissionManager(IPermissionManager permissionManager)
  {
    this.permissionManager = permissionManager;
  }

  @Override
  public InternalTopicManager getTopicManager()
  {
    return topicManager;
  }

  @Override
  public InternalSession[] getSessions()
  {
    return sessionsArray;
  }

  /**
   * This method is (and must be) called while holding the "sessions" monitor lock.
   */
  private void buildSessionsArray()
  {
    sessionsArray = sessions.values().toArray(new InternalSession[sessions.size()]);

    Arrays.sort(sessionsArray, (s1, s2) -> {
      // Iterating system sessions first is very important for the SecurityManager.
      // In particular realmView.waitForUpdate(lastRealmModification) may deadlock without this order.
      int sys1 = IRepository.SYSTEM_USER_ID.equals(s1.getUserID()) ? 0 : 1;
      int sys2 = IRepository.SYSTEM_USER_ID.equals(s2.getUserID()) ? 0 : 1;

      int result = Integer.compare(sys1, sys2);
      if (result == 0)
      {
        long t1 = s1.getOpeningTime();
        long t2 = s2.getOpeningTime();

        result = Long.compare(t1, t2);
      }

      return result;
    });
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalSession getSession(int sessionID)
  {
    checkActive();
    synchronized (sessions)
    {
      return sessions.get(sessionID);
    }
  }

  @Override
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
  @Override
  public InternalSession openSession(ISessionProtocol sessionProtocol)
  {
    return openSession(sessionProtocol, 0);
  }

  /**
   * @since 2.0
   */
  @Override
  public InternalSession openSession(ISessionProtocol sessionProtocol, int sessionID)
  {
    return openSession(sessionProtocol, sessionID, null);
  }

  @Override
  public InternalSession openSession(ISessionProtocol sessionProtocol, int sessionID, Consumer<InternalSession> sessionInitializer)
  {
    return openSession(sessionProtocol, sessionID, sessionInitializer, null);
  }

  @Override
  public InternalSession openSession(ISessionProtocol sessionProtocol, int sessionID, Consumer<InternalSession> sessionInitializer, byte[] oneTimeLoginToken)
  {
    int id;

    if (sessionID == 0)
    {
      // FIXME: Attack vector: Exhaust session IDs!
      id = lastSessionID.incrementAndGet();

      if (TRACER.isEnabled())
      {
        TRACER.trace("Opening session " + id); //$NON-NLS-1$
      }
    }
    else
    {
      id = sessionID;

      if (TRACER.isEnabled())
      {
        TRACER.trace("Reopening session " + id); //$NON-NLS-1$
      }
    }

    String userID = null;

    if (oneTimeLoginToken != null)
    {
      Long deadline = oneTimeLoginTokens.remove(new OneTimeLoginToken(oneTimeLoginToken));
      if (deadline == null || deadline < repository.getTimeStamp())
      {
        throw new SecurityException("Access denied");
      }
    }
    else
    {
      userID = authenticateUser(sessionProtocol);
    }

    InternalSession session = createSession(id, userID, sessionProtocol);

    if (sessionInitializer != null)
    {
      sessionInitializer.accept(session);
    }

    LifecycleUtil.activate(session);

    synchronized (sessions)
    {
      repository.executeOutsideStartCommit(() -> {
        long openingTime = repository.getTimeStamp();
        session.setOpeningTime(openingTime);

        long firstUpdateTime = repository.getLastCommitTimeStamp();
        session.setFirstUpdateTime(firstUpdateTime);

        sessions.put(id, session);
        buildSessionsArray();
      });
    }

    sendRemoteSessionNotification(session, CDOProtocolConstants.REMOTE_SESSION_OPENED);
    fireElementAddedEvent(session);
    return session;
  }

  protected InternalSession createSession(int id, String userID, ISessionProtocol protocol)
  {
    return new Session(this, protocol, id, userID);
  }

  @Override
  public byte[] generateOneTimeLoginToken()
  {
    byte[] bytes = new byte[ONE_TIME_LOGIN_TOKEN_LENGTH];
    synchronized (ONE_TIME_LOGIN_TOKEN_GENERATOR)
    {
      ONE_TIME_LOGIN_TOKEN_GENERATOR.nextBytes(bytes);
    }

    long deadline = repository.getTimeStamp() + ONE_TIME_LOGIN_TOKEN_TIMEOUT;
    oneTimeLoginTokens.put(new OneTimeLoginToken(bytes), deadline);
    return bytes;
  }

  @Override
  public void sessionClosed(InternalSession session)
  {
    int sessionID = session.getSessionID();
    InternalSession removedSession;
    Set<InternalSession> recipients = new HashSet<>();

    synchronized (sessions)
    {
      removedSession = sessions.remove(sessionID);
      if (removedSession != null)
      {
        buildSessionsArray();

        for (InternalSession remainingSession : sessions.values())
        {
          if (remainingSession.isSubscribed())
          {
            recipients.add(remainingSession);
          }
        }

        List<InternalTopic> affectedTopics = topicManager.sessionClosed(removedSession);
        for (InternalTopic affectedTopic : affectedTopics)
        {
          for (InternalSession affectedSession : affectedTopic.getSessions())
          {
            recipients.add(affectedSession);
          }
        }
      }
    }

    if (removedSession != null)
    {
      if (!recipients.isEmpty())
      {
        sendRemoteSessionNotification(removedSession, recipients, null, CDOProtocolConstants.REMOTE_SESSION_CLOSED);
      }

      fireElementRemovedEvent(removedSession);
    }
  }

  @Override
  public void openedOnClientSide(InternalSession session)
  {
    processQueuedCommitNotifications(session);
  }

  @Override
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

  @Override
  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  @Override
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState, CDOID rootResourceID)
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

  @Override
  @Deprecated
  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch)
  {
    sendBranchNotification(sender, branch, ChangeKind.CREATED);
  }

  @Deprecated
  @Override
  public void sendBranchNotification(InternalSession sender, InternalCDOBranch branch, ChangeKind changeKind)
  {
    sendBranchNotification(sender, changeKind, branch);
  }

  @Override
  public void sendBranchNotification(InternalSession sender, ChangeKind changeKind, CDOBranch... branches)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendBranchNotification(changeKind, branches);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  @Override
  public void sendTagNotification(InternalSession sender, int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendTagNotification(modCount, oldName, newName, branchPoint);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  @Override
  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo, boolean clearResourcePathCache)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void sendCommitNotification(CommitNotificationInfo info)
  {
    CDOCommonSession sender = info.getSender();
    InternalSession[] sessions = getSessions();

    for (InternalSession session : sessions)
    {
      if (session != sender || info.isModifiedByServer())
      {
        if (session.isOpenOnClientSide())
        {
          processQueuedCommitNotifications(session);
          doSendCommitNotification(session, info);
        }
        else
        {
          queueCommitNotification(session, info);
        }
      }
    }
  }

  private void doSendCommitNotification(InternalSession session, CommitNotificationInfo info)
  {
    try
    {
      session.sendCommitNotification(info);
    }
    catch (Exception ex)
    {
      handleNotificationProblem(session, ex);
    }
  }

  private void queueCommitNotification(InternalSession session, CommitNotificationInfo info)
  {
    synchronized (commitNotificationInfoQueues)
    {
      List<CommitNotificationInfo> queue = commitNotificationInfoQueues.get(session);
      if (queue == null)
      {
        queue = new ArrayList<>();
        commitNotificationInfoQueues.put(session, queue);

        session.addListener(sessionListener);
      }

      queue.add(info);
    }
  }

  private void processQueuedCommitNotifications(InternalSession session)
  {
    List<CommitNotificationInfo> queue;
    synchronized (commitNotificationInfoQueues)
    {
      queue = commitNotificationInfoQueues.remove(session);
    }

    if (queue != null && !session.isClosed())
    {
      session.removeListener(sessionListener);

      for (CommitNotificationInfo queuedInfo : queue)
      {
        doSendCommitNotification(session, queuedInfo);
      }
    }
  }

  @Override
  public void sendLockNotification(InternalSession sender, CDOLockChangeInfo lockChangeInfo)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender && session.options().getLockNotificationMode() != LockNotificationMode.OFF)
      {
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
  }

  @Override
  public void sendLockOwnerRemappedNotification(InternalSession sender, CDOBranch branch, CDOLockOwner oldOwner, CDOLockOwner newOwner)
  {
    for (InternalSession session : getSessions())
    {
      if (session != sender)
      {
        try
        {
          session.sendLockOwnerRemappedNotification(branch, oldOwner, newOwner);
        }
        catch (Exception ex)
        {
          handleNotificationProblem(session, ex);
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  public void sendRemoteSessionNotification(InternalSession sender, byte opcode)
  {
    List<InternalSession> recipients = new ArrayList<>();
    for (InternalSession session : getSessions())
    {
      if (session != sender && session.isSubscribed())
      {
        recipients.add(session);
      }
    }

    sendRemoteSessionNotification(sender, recipients, null, opcode);
  }

  @Override
  public void sendRemoteSessionNotification(InternalSession sender, Collection<InternalSession> recipients, InternalTopic topic, byte opcode)
  {
    if (recipients == null)
    {
      recipients = new ArrayList<>();
      for (InternalSession session : topic.getSessions())
      {
        recipients.add(session);
      }
    }

    try
    {
      for (InternalSession session : recipients)
      {
        if (session != sender)
        {
          try
          {
            session.sendRemoteSessionNotification(sender, topic, opcode);
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

  @Override
  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message, InternalTopic topic)
  {
    List<Integer> result = new ArrayList<>();
    for (InternalSession recipient : topic.getSessions())
    {
      try
      {
        if (recipient != null && recipient != sender)
        {
          recipient.sendRemoteMessageNotification(sender, topic, message);
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

  @Override
  public List<Integer> sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message, int[] recipients)
  {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < recipients.length; i++)
    {
      InternalSession recipient = getSession(recipients[i]);

      try
      {
        if (recipient != null && recipient.isSubscribed())
        {
          recipient.sendRemoteMessageNotification(sender, null, message);
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
    if (session.isClosed())
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("A problem occured while notifying session " + session, t);
      }
    }
    else
    {
      OM.LOG.warn("A problem occured while notifying session " + session, t);
    }
  }

  @Override
  public String authenticateUser(IAuthenticationProtocol protocol) throws SecurityException
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
        throw notAuthenticated();
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(authenticationServer.handleResponse(response));

      ExtendedDataInputStream stream = new ExtendedDataInputStream(bais);
      String userID = stream.readString();
      char[] password = SecurityUtil.toCharArray(stream.readString());

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
  public void changeUserCredentials(IAuthenticationProtocol sessionProtocol, String userID)
  {
    changeUserCredentials(sessionProtocol, userID, CredentialsUpdateOperation.CHANGE_PASSWORD);
  }

  @Override
  public void resetUserCredentials(IAuthenticationProtocol sessionProtocol, String userID)
  {
    changeUserCredentials(sessionProtocol, userID, CredentialsUpdateOperation.RESET_PASSWORD);
  }

  protected void changeUserCredentials(IAuthenticationProtocol sessionProtocol, String userID, CredentialsUpdateOperation operation)
  {

    if (sessionProtocol == null)
    {
      return;
    }

    if (authenticationServer == null || authenticator == null)
    {
      return;
    }

    if (!(authenticator instanceof IAuthenticator2))
    {
      throw new SecurityException("Current authenticator does not permit password updates"); //$NON-NLS-1$
    }

    try
    {
      Challenge challenge = authenticationServer.getChallenge();
      Response response = sessionProtocol.sendCredentialsChallenge(challenge, userID, operation);
      if (response == null)
      {
        throw notAuthenticated();
      }

      ByteArrayInputStream baos = new ByteArrayInputStream(authenticationServer.handleResponse(response));
      ExtendedDataInputStream stream = new ExtendedDataInputStream(baos);

      if (operation == CredentialsUpdateOperation.RESET_PASSWORD)
      {
        String adminID = stream.readString();
        char[] adminPassword = SecurityUtil.toCharArray(stream.readString());
        if (!ObjectUtil.equals(userID, stream.readString()))
        {
          throw new SecurityException("Attempt to reset password of a different user than requested"); //$NON-NLS-1$
        }

        char[] newPassword = SecurityUtil.toCharArray(stream.readString());

        // this will throw if the current credentials are not authenticated as an administrator
        ((IAuthenticator2)authenticator).resetPassword(adminID, adminPassword, userID, newPassword);
      }
      else
      {
        userID = stream.readString(); // user can change any password that she can authenticate on the old password
        char[] password = SecurityUtil.toCharArray(stream.readString());
        char[] newPassword = SecurityUtil.toCharArray(stream.readString());

        // this will throw if the "old password" provided by the user is not correct
        ((IAuthenticator2)authenticator).updatePassword(userID, password, newPassword);
      }
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
    LifecycleUtil.activate(topicManager);
    initAuthentication();
  }

  protected void initAuthentication()
  {
    if (authenticator != null)
    {
      if (authenticationServer == null)
      {
        authenticationServer = new DiffieHellman.Server(repository.getUUID());
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

    LifecycleUtil.deactivate(topicManager);
    super.doDeactivate();
  }

  @SuppressWarnings("deprecation")
  private SecurityException notAuthenticated()
  {
    // Existing clients may expect this deprecated exception type
    return new org.eclipse.emf.cdo.common.util.NotAuthenticatedException();
  }

  /**
   * @author Eike Stepper
   */
  private static final class OneTimeLoginToken
  {
    private final byte[] bytes;

    private final int hash;

    public OneTimeLoginToken(byte[] bytes)
    {
      this.bytes = bytes;
      hash = Arrays.hashCode(bytes);
    }

    @Override
    public int hashCode()
    {
      return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      OneTimeLoginToken other = (OneTimeLoginToken)obj;
      return Arrays.equals(bytes, other.bytes);
    }
  }
}
