/*
 * Copyright (c) 2023-2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserAuthenticator.PasswordChangeSupport;
import org.eclipse.emf.cdo.server.IRepositoryProtector.UserInfo;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator.TreeExtension;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IAuthenticator2;
import org.eclipse.net4j.util.security.SecurityUtil;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 4.20
 */
public class DefaultRepositoryProtector extends Container<UserInfo>
    implements IRepositoryProtector, IAuthenticator2, IPermissionManager, IRepository.WriteAccessHandler
{
  private static final String PROP_PROTECTOR_INITIALIZED = "org.eclipse.emf.cdo.server.protectorInitialized";

  private IRepository repository;

  private final ConcurrentMap<IRepository, SecondaryRepository> secondaryRepositories = new ConcurrentHashMap<>();

  private final MessageDigest passwordDigester;

  private final byte[] salt;

  private final Map<String, SessionUserInfo> sessionUserInfos = new HashMap<>();

  private final IListener sessionManagerListener = new ContainerEventAdapter<ISession>()
  {
    @Override
    protected void onAdded(IContainer<ISession> container, ISession session)
    {
      sessionAdded(session);
    }

    @Override
    protected void onRemoved(IContainer<ISession> container, ISession session)
    {
      sessionRemoved(session);
    }
  };

  private UserAuthenticator userAuthenticator;

  private AuthorizationStrategy authorizationStrategy;

  private final ConcurrentArray<RevisionAuthorizer> revisionAuthorizers = new ConcurrentArray<RevisionAuthorizer>()
  {
    @Override
    protected RevisionAuthorizer[] newArray(int length)
    {
      return new RevisionAuthorizer[length];
    }
  };

  private final ConcurrentArray<CommitHandler> commitHandlers = new ConcurrentArray<CommitHandler>()
  {
    @Override
    protected CommitHandler[] newArray(int length)
    {
      return new CommitHandler[length];
    }
  };

  private boolean firstTime;

  public DefaultRepositoryProtector()
  {
    try
    {
      passwordDigester = MessageDigest.getInstance("MD5");

      salt = new byte[32];
      new Random(System.currentTimeMillis()).nextBytes(salt);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public final boolean isFirstTime()
  {
    return firstTime;
  }

  @Override
  public final IManagedContainer getContainer()
  {
    return repository == null ? null : ((InternalRepository)repository).getContainer();
  }

  @Override
  public final IRepository getRepository()
  {
    return repository;
  }

  @Override
  public final void setRepository(IRepository repository)
  {
    checkInactive();
    this.repository = repository;
  }

  @Override
  public final IRepository[] getSecondaryRepositories()
  {
    return secondaryRepositories.keySet().toArray(new InternalRepository[0]);
  }

  @Override
  public final void addSecondaryRepository(IRepository repository)
  {
    secondaryRepositories.computeIfAbsent(repository, k -> new SecondaryRepository(k));
  }

  @Override
  public final void removeSecondaryRepository(IRepository repository)
  {
    SecondaryRepository secondaryRepository = secondaryRepositories.remove(repository);
    if (secondaryRepository != null)
    {
      secondaryRepository.dispose();
    }
  }

  @Override
  public final UserAuthenticator getUserAuthenticator()
  {
    return userAuthenticator;
  }

  @Override
  @InjectElement(name = "userAuthenticator", productGroup = UserAuthenticator.PRODUCT_GROUP)
  public final void setUserAuthenticator(UserAuthenticator userAuthenticator)
  {
    checkInactive();
    this.userAuthenticator = userAuthenticator;
  }

  @Override
  public final AuthorizationStrategy getAuthorizationStrategy()
  {
    return authorizationStrategy;
  }

  @Override
  @InjectElement(name = "authorizationStrategy", productGroup = AuthorizationStrategy.PRODUCT_GROUP, defaultFactoryType = AuthorizationStrategy.DEFAULT_TYPE)
  public final void setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy)
  {
    checkInactive();
    this.authorizationStrategy = authorizationStrategy;
  }

  @Override
  public final RevisionAuthorizer[] getRevisionAuthorizers()
  {
    return revisionAuthorizers.get();
  }

  @Override
  @InjectElement(name = "revisionAuthorizer", productGroup = RevisionAuthorizer.PRODUCT_GROUP, defaultFactoryType = IFactoryKey.DEFAULT_FACTORY_TYPE)
  public final void addRevisionAuthorizer(RevisionAuthorizer authorizer)
  {
    checkInactive();
    revisionAuthorizers.add(authorizer);
  }

  @Override
  public final void removeRevisionAuthorizer(RevisionAuthorizer authorizer)
  {
    checkInactive();
    revisionAuthorizers.remove(authorizer);
  }

  @Override
  public final CommitHandler[] getCommitHandlers()
  {
    return commitHandlers.get();
  }

  @Override
  @InjectElement(name = "commitHandler", productGroup = CommitHandler.PRODUCT_GROUP)
  public final void addCommitHandler(CommitHandler handler)
  {
    checkInactive();
    commitHandlers.add(handler);
  }

  @Override
  public final void removeCommitHandler(CommitHandler handler)
  {
    checkInactive();
    commitHandlers.remove(handler);
  }

  @Override
  public boolean isAdministrator(String userID)
  {
    checkActive();
    return userAuthenticator.isAdministrator(userID);
  }

  @Override
  public void updatePassword(String userID, char[] oldPassword, char[] newPassword)
  {
    checkActive();

    if (userAuthenticator instanceof PasswordChangeSupport)
    {
      ((PasswordChangeSupport)userAuthenticator).updatePassword(userID, oldPassword, newPassword);
      return;
    }

    throw new UnsupportedOperationException();
  }

  @Override
  public void resetPassword(String adminID, char[] adminPassword, String userID, char[] newPassword)
  {
    checkActive();

    if (userAuthenticator instanceof PasswordChangeSupport)
    {
      ((PasswordChangeSupport)userAuthenticator).resetPassword(adminID, adminPassword, userID, newPassword);
      return;
    }

    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (sessionUserInfos)
    {
      return sessionUserInfos.isEmpty();
    }
  }

  @Override
  public final UserInfo[] getElements()
  {
    List<UserInfo> userInfos = new ArrayList<>();

    synchronized (sessionUserInfos)
    {
      for (SessionUserInfo sessionUserInfo : sessionUserInfos.values())
      {
        userInfos.add(sessionUserInfo.userInfo);
      }
    }

    return userInfos.toArray(new UserInfo[userInfos.size()]);
  }

  @Override
  public void authenticate(String userID, char[] password) throws SecurityException
  {
    checkActive();

    try
    {
      UserInfo newUserInfo = userAuthenticator.authenticateUser(userID, password);
      UserInfo oldUserInfo = null;

      SessionUserInfo sessionUserInfo;
      synchronized (sessionUserInfos)
      {
        sessionUserInfo = sessionUserInfos.get(userID);

        if (newUserInfo != null)
        {
          // Authentication succeeded.

          if (sessionUserInfo != null)
          {
            oldUserInfo = sessionUserInfo.userInfo;
            if (oldUserInfo.equalsStructurally(newUserInfo))
            {
              oldUserInfo = null; // Don't fire event below.
            }
            else
            {
              sessionUserInfo.userInfo = newUserInfo;
            }
          }
          else
          {
            sessionUserInfo = new SessionUserInfo();
            sessionUserInfo.userInfo = newUserInfo;
            sessionUserInfos.put(userID, sessionUserInfo);
          }

          sessionUserInfo.passwordDigest = digestPassword(password);
        }
      }

      if (newUserInfo != null)
      {
        if (oldUserInfo != null)
        {
          fireEvent(new UserInfoChangedEvent(this, oldUserInfo, newUserInfo));
        }

        return; // Allow access.
      }

      // Authentication failed.
      if (sessionUserInfo != null)
      {
        byte[] digest = digestPassword(password);
        if (Arrays.equals(digest, sessionUserInfo.passwordDigest))
        {
          for (ISession session : sessionUserInfo.sessions)
          {
            OM.LOG.info("Closing session because user " + userID + " is no longer authenticated: " + session);

            try
            {
              session.close();
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
          }
        }
      }
    }
    catch (SecurityException ex)
    {
      OM.LOG.info(ex);
    }

    denyAccess();
  }

  @Override
  public UserInfo getUserInfo(String userID)
  {
    SessionUserInfo sessionUserInfo;
    synchronized (sessionUserInfos)
    {
      sessionUserInfo = sessionUserInfos.get(userID);
    }

    if (sessionUserInfo != null)
    {
      return sessionUserInfo.userInfo;
    }

    return null;
  }

  protected UserInfo getUserInfo(ISession session)
  {
    String userID = session.getUserID();
    if (userID != null)
    {
      return getUserInfo(userID);
    }

    return null;
  }

  protected void sessionAdded(ISession session)
  {
    String userID = session.getUserID();
    if (userID != null)
    {
      UserInfo userInfo = null;

      synchronized (sessionUserInfos)
      {
        SessionUserInfo sessionUserInfo = sessionUserInfos.get(userID);
        if (sessionUserInfo != null)
        {
          if (sessionUserInfo.addSession(session))
          {
            userInfo = sessionUserInfo.userInfo;
          }
        }
      }

      if (userInfo != null)
      {
        fireElementAddedEvent(userInfo);
      }
    }
  }

  protected void sessionRemoved(ISession session)
  {
    String userID = session.getUserID();
    if (userID != null)
    {
      UserInfo userInfo = null;

      synchronized (sessionUserInfos)
      {
        SessionUserInfo sessionUserInfo = sessionUserInfos.get(userID);
        if (sessionUserInfo != null)
        {
          if (sessionUserInfo.removeSession(session))
          {
            sessionUserInfos.remove(userID);
            userInfo = sessionUserInfo.userInfo;
          }
        }
      }

      if (userInfo != null)
      {
        fireElementRemovedEvent(userInfo);
      }
    }
  }

  @Override
  @Deprecated
  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, ISession session)
  {
    return getPermission(revision, securityContext, session, repository);
  }

  protected CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, ISession session, IRepository repository)
  {
    UserInfo userInfo = getUserInfo(session);

    CDORevisionProvider revisionProvider = securityContext == null ? null : //
        id -> ManagedRevisionProvider.provideRevision(session.getRevisionManager(), id, securityContext);

    return authorizeRevision(session, userInfo, securityContext, revisionProvider, revision);
  }

  @Override
  public boolean hasAnyRule(ISession session, Set<? extends Object> rules)
  {
    return false;
  }

  @Override
  public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
  {
    ISession session = transaction.getSession();
    UserInfo userInfo = getUserInfo(session);
    forEachCommitHandler(handler -> handler.beforeCommit(commitContext, userInfo));

    CDOBranchPoint securityContext = commitContext.getBranchPoint();
    authorizeCommit(session, userInfo, securityContext, transaction, commitContext);

    CommitSecurityImpact securityImpact = computeCommitSecurityImpact(commitContext);
    if (securityImpact != null)
    {
      ((InternalCommitContext)commitContext).setSecurityImpact(securityImpact.getSeverity(), securityImpact.getImpactedRules());
    }
  }

  @Override
  public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
  {
    ISession session = transaction.getSession();
    UserInfo userInfo = getUserInfo(session);
    forEachCommitHandler(handler -> handler.afterCommit(commitContext, userInfo));
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    checkState(repository, "repository"); //$NON-NLS-1$
    checkState(userAuthenticator, "userAuthenticator"); //$NON-NLS-1$

    if (authorizationStrategy == null)
    {
      String name = repository.getName();
      OM.LOG.warn("No authorization strategy specified for repository " + name);
      OM.LOG.warn("Granting WRITE access to all users of repository " + name);
      authorizationStrategy = new AuthorizationStrategy.Constant.Write();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    IStore store = repository.getStore();
    Map<String, String> properties = store.getPersistentProperties(PROP_PROTECTOR_INITIALIZED);
    firstTime = !Boolean.parseBoolean(properties.get(PROP_PROTECTOR_INITIALIZED));

    init(firstTime);

    if (firstTime)
    {
      properties.put(PROP_PROTECTOR_INITIALIZED, Boolean.TRUE.toString());
      store.setPersistentProperties(properties);
    }

    InternalSessionManager sessionManager = (InternalSessionManager)repository.getSessionManager();
    sessionManager.setAuthenticator(this);
    sessionManager.setPermissionManager(this);
    sessionManager.addListener(sessionManagerListener);
    repository.addHandler(this);

    userAuthenticator.setRepositoryProtector(this);
    LifecycleUtil.activate(userAuthenticator);

    authorizationStrategy.setRepositoryProtector(this);
    LifecycleUtil.activate(authorizationStrategy);

    for (RevisionAuthorizer revisionAuthorizer : getRevisionAuthorizers())
    {
      revisionAuthorizer.setRepositoryProtector(this);
      LifecycleUtil.activate(revisionAuthorizer);
    }

    for (CommitHandler commitHandler : getCommitHandlers())
    {
      commitHandler.setRepositoryProtector(this);
      LifecycleUtil.activate(commitHandler);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (CommitHandler commitHandler : getCommitHandlers())
    {
      LifecycleUtil.deactivate(commitHandler);
      commitHandler.setRepositoryProtector(null);
    }

    for (RevisionAuthorizer revisionAuthorizer : getRevisionAuthorizers())
    {
      LifecycleUtil.deactivate(revisionAuthorizer);
      revisionAuthorizer.setRepositoryProtector(null);
    }

    LifecycleUtil.deactivate(authorizationStrategy);
    authorizationStrategy.setRepositoryProtector(null);

    LifecycleUtil.deactivate(userAuthenticator);
    userAuthenticator.setRepositoryProtector(null);

    repository.removeHandler(this);

    InternalSessionManager sessionManager = (InternalSessionManager)repository.getSessionManager();
    if (sessionManager != null)
    {
      sessionManager.setAuthenticator(null);
      sessionManager.setPermissionManager(null);
      sessionManager.removeListener(sessionManagerListener);
    }
  }

  protected void init(boolean firstTime)
  {
  }

  protected boolean isReplicateSecondaryPackageUnits()
  {
    return false;
  }

  /**
   * @param securityContext Can be <code>null</code>, for example if authorization is caused by
   *          {@link CDORevisionManager#getRevisionByVersion(CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchVersion, int, boolean) getRevisionByVersion()}.
   * @param revisionProvider Can be <code>null</code>, for example if authorization is caused by
   *          {@link CDORevisionManager#getRevisionByVersion(CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchVersion, int, boolean) getRevisionByVersion()}.
   */
  protected CDOPermission authorizeRevision(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
      CDORevision revision)
  {
    CDOPermission result = authorizationStrategy.getInitialPermission();
    CDOPermission terminalPermission = authorizationStrategy.getTerminalPermission();

    for (RevisionAuthorizer authorizer : getRevisionAuthorizers())
    {
      if (authorizer.isDisabled())
      {
        continue;
      }

      CDOPermission permission = authorizer.authorizeRevision(session, userInfo, securityContext, revisionProvider, revision);
      if (permission != null)
      {
        switch (authorizer.getOperation())
        {
        case COMBINE:
          result = authorizationStrategy.getCombinedPermission(result, permission);
          break;

        case OVERRIDE:
          result = permission;
          break;

        case VETO:
          return permission;
        }

        if (!authorizationStrategy.test(result, terminalPermission))
        {
          // Break early if the terminal permission wouldn't change the result anymore.
          break;
        }
      }
    }

    return result;
  }

  protected void authorizeRevisionModification(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
      CDORevision revision, String operation) throws SecurityException
  {
    CDOPermission permission = authorizeRevision(session, userInfo, securityContext, revisionProvider, revision);
    if (permission != CDOPermission.WRITE)
    {
      throw new SecurityException("User " + userInfo.userID() + " is not allowed to " + operation + " " + revision);
    }
  }

  protected void authorizeCommit(ISession session, UserInfo userInfo, CDOBranchPoint securityContext, CDORevisionProvider revisionProvider,
      CommitContext commitContext) throws SecurityException
  {
    if (authorizationStrategy.isAuthorizeAttach())
    {
      InternalCDORevision[] attachedObjects = commitContext.getNewObjects();
      if (attachedObjects != null && attachedObjects.length != 0)
      {
        for (int i = 0; i < attachedObjects.length; i++)
        {
          CDORevision revision = attachedObjects[i];
          authorizeRevisionModification(session, userInfo, securityContext, revisionProvider, revision, "attach");
        }
      }
    }

    if (authorizationStrategy.isAuthorizeModify())
    {
      InternalCDORevision[] modifiedObjects = commitContext.getDirtyObjects();
      if (modifiedObjects != null && modifiedObjects.length != 0)
      {
        for (int i = 0; i < modifiedObjects.length; i++)
        {
          CDORevision revision = modifiedObjects[i];
          authorizeRevisionModification(session, userInfo, securityContext, revisionProvider, revision, "modify");
        }
      }
    }

    if (authorizationStrategy.isAuthorizeDetach())
    {
      CDOID[] detachedObjects = commitContext.getDetachedObjects();
      if (detachedObjects != null && detachedObjects.length != 0)
      {
        for (int i = 0; i < detachedObjects.length; i++)
        {
          CDOID id = detachedObjects[i];
          CDORevision revision = revisionProvider.getRevision(id);
          authorizeRevisionModification(session, userInfo, securityContext, revisionProvider, revision, "detach");
        }
      }
    }
  }

  protected CommitSecurityImpact computeCommitSecurityImpact(CommitContext commitContext)
  {
    return null;
  }

  protected void replicateSecondaryPackageUnits(InternalCDOPackageUnit[] newPackageUnits)
  {
    InternalRepository primaryRepository = (InternalRepository)repository;
    InternalCDOPackageRegistry primaryPackageRegistry = primaryRepository.getPackageRegistry(false);
    List<InternalCDOPackageUnit> unknownPackageUnits = new ArrayList<>();

    for (InternalCDOPackageUnit packageUnit : newPackageUnits)
    {
      String nsURI = packageUnit.getID();
      if (primaryPackageRegistry.getPackageUnit(nsURI) == null)
      {
        unknownPackageUnits.add((InternalCDOPackageUnit)CDOModelUtil.copyPackageUnit(packageUnit));
      }
    }

    if (!unknownPackageUnits.isEmpty())
    {
      InternalCDOPackageUnit[] unitArray = unknownPackageUnits.toArray(new InternalCDOPackageUnit[unknownPackageUnits.size()]);

      try
      {
        RunnableWithException.forkAndWait(() -> {
          synchronized (primaryPackageRegistry)
          {
            primaryPackageRegistry.putPackageUnits(unitArray, CDOPackageUnit.State.LOADED);
          }

          commitPrimaryPackageUnits(unitArray);
        });
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  protected void commitPrimaryPackageUnits(InternalCDOPackageUnit[] packageUnits)
  {
    IStoreAccessor writer = getRepository().getStore().getWriter(null);
    StoreThreadLocal.setAccessor(writer);

    try
    {
      writer.writePackageUnits(packageUnits, new Monitor());
      writer.commit(new Monitor());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  protected void denyAccess() throws SecurityException
  {
    throw new SecurityException("Access denied"); //$NON-NLS-1$
  }

  private byte[] digestPassword(char[] password)
  {
    if (password == null)
    {
      return null;
    }

    byte[] bytes = SecurityUtil.toString(password).getBytes();

    synchronized (passwordDigester)
    {
      passwordDigester.update(salt);
      return passwordDigester.digest(bytes);
    }
  }

  private void forEachCommitHandler(Consumer<CommitHandler> consumer)
  {
    for (CommitHandler handler : getCommitHandlers())
    {
      try
      {
        consumer.accept(handler);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class RepositoryConfiguratorExtension extends TreeExtension
  {
    public RepositoryConfiguratorExtension()
    {
    }

    @Override
    protected String configureRepository(InternalRepository repository, Tree config, Map<String, String> parameters, IManagedContainer container)
    {
      String type = config.attribute("type"); //$NON-NLS-1$
      if (StringUtil.isEmpty(type))
      {
        type = DEFAULT_TYPE;
      }

      IRepositoryProtector protector = container.createElement(IRepositoryProtector.PRODUCT_GROUP, type, config);
      repository.setProtector(protector);

      return "protected: " + type;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CommitSecurityImpact
  {
    public static final CommitSecurityImpact NONE = new CommitSecurityImpact(CommitNotificationInfo.IMPACT_NONE, null);

    public static final CommitSecurityImpact REALM = new CommitSecurityImpact(CommitNotificationInfo.IMPACT_REALM, null);

    private final byte severity;

    private final Set<? extends Object> impactedRules;

    public CommitSecurityImpact(byte severity, Set<? extends Object> impactedRules)
    {
      this.severity = severity;
      this.impactedRules = impactedRules;
    }

    public byte getSeverity()
    {
      return severity;
    }

    public Set<? extends Object> getImpactedRules()
    {
      return impactedRules;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class SessionUserInfo
  {
    private UserInfo userInfo;

    private byte[] passwordDigest;

    private final List<ISession> sessions = new ArrayList<>();

    public boolean addSession(ISession session)
    {
      boolean first = sessions.isEmpty();
      sessions.add(session);
      return first;
    }

    public boolean removeSession(ISession session)
    {
      sessions.remove(session);
      return sessions.isEmpty();
    }

    @Override
    public String toString()
    {
      return "SessionUserInfo[userInfo=" + userInfo + ", sessions=" + sessions + "]";
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SecondaryRepository extends LifecycleEventAdapter implements IPermissionManager, IRepository.WriteAccessHandler
  {
    private final IRepository delegate;

    public SecondaryRepository(IRepository delegate)
    {
      this.delegate = delegate;

      InternalSessionManager sessionManager = (InternalSessionManager)delegate.getSessionManager();
      sessionManager.setAuthenticator(DefaultRepositoryProtector.this);
      sessionManager.setPermissionManager(this);
      sessionManager.addListener(sessionManagerListener);

      delegate.addListener(this);
      delegate.addHandler(this);
    }

    @Override
    @Deprecated
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, ISession session)
    {
      return DefaultRepositoryProtector.this.getPermission(revision, securityContext, session, delegate);
    }

    @Override
    public boolean hasAnyRule(ISession session, Set<? extends Object> rules)
    {
      return DefaultRepositoryProtector.this.hasAnyRule(session, rules);
    }

    @Override
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
    {
      if (isReplicateSecondaryPackageUnits())
      {
        InternalCDOPackageUnit[] newPackageUnits = commitContext.getNewPackageUnits();
        if (!ObjectUtil.isEmpty(newPackageUnits))
        {
          replicateSecondaryPackageUnits(newPackageUnits);
        }
      }

      ISession session = transaction.getSession();
      UserInfo userInfo = getUserInfo(session);
      forEachCommitHandler(handler -> handler.beforeCommit(commitContext, userInfo));

      CDOBranchPoint securityContext = commitContext.getBranchPoint();
      authorizeCommit(session, userInfo, securityContext, transaction, commitContext);
    }

    @Override
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
      ISession session = transaction.getSession();
      UserInfo userInfo = getUserInfo(session);
      forEachCommitHandler(handler -> handler.afterCommit(commitContext, userInfo));
    }

    public void dispose()
    {
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      removeSecondaryRepository(delegate);
    }
  }
}
