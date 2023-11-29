/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2018, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 399306
 *    Christian W. Damus (CEA LIST) - bug 418454
 *    Christian W. Damus (CEA LIST) - bug 399487
 *    Laurent Redor (Obeo) - bug 501607
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.security.PermissionUtil;
import org.eclipse.emf.cdo.internal.security.ViewCreator;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;
import org.eclipse.emf.cdo.security.impl.PermissionImpl;
import org.eclipse.emf.cdo.security.util.AuthorizationContext;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.ObjectWriteAccessHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ArrayUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.RunnableWithException;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.spi.cdo.InternalCDOSessionInvalidationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class SecurityManager extends Lifecycle implements InternalSecurityManager
{
  private static final Map<IRepository, InternalSecurityManager> SECURITY_MANAGERS = Collections.synchronizedMap(new HashMap<>());

  private static final long REALM_UPDATE_TIMEOUT = OMPlatform.INSTANCE.getProperty("org.eclipse.emf.cdo.server.security.REALM_UPDATE_TIMEOUT", 8000L);

  private static final boolean DISABLE_DETACH_CHECKS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.security.DISABLE_DETACH_CHECKS");

  private static final boolean ALLOW_EMPTY_PASSWORDS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.server.security.ALLOW_EMPTY_PASSWORDS");

  private static final Consumer<String> EMPTY_PASSWORD_PREVENTER = pw -> {
    if (StringUtil.isEmpty(pw))
    {
      throw new SecurityException("Password is empty");
    }
  };

  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

  private final IRepositoryProtector protector = new DelegatingProtector();

  private final IListener repositoryListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onActivated(ILifecycle lifecycle)
    {
      init();
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      repository.setProtector(null);
      unregister(repository);
      deactivate();
    }
  };

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

  private final IListener realmInvalidationListener = new IListener()
  {
    private boolean clearUserInfos;

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof InternalCDOSessionInvalidationEvent)
      {
        InternalCDOSessionInvalidationEvent e = (InternalCDOSessionInvalidationEvent)event;
        if (e.getSecurityImpact() == CommitNotificationInfo.IMPACT_REALM)
        {
          clearUserInfos = true;
        }
      }
      else if (event instanceof CDOViewInvalidationEvent)
      {
        if (clearUserInfos)
        {
          clearUserInfos(true);
          clearUserInfos = false;
        }
      }
    }
  };

  // private final IAuthenticator authenticator = new Authenticator();

  private final IPermissionManager permissionManager = new PermissionManager();

  private final IRepository.WriteAccessHandler writeAccessHandler = new WriteAccessHandler();

  private final String realmPath;

  private final IManagedContainer container;

  private final ConcurrentMap<InternalRepository, SecondaryRepository> secondaryRepositories = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, UserInfo> userInfos = new ConcurrentHashMap<>();

  private final HashBag<PermissionImpl> permissionBag = new HashBag<>();

  private final Object commitHandlerLock = new Object();

  private CommitHandler[] commitHandlers = {};

  private CommitHandler2[] commitHandlers2 = {};

  private PermissionImpl[] permissionArray = {};

  private Consumer<String> passwordValidator = ALLOW_EMPTY_PASSWORDS ? null : EMPTY_PASSWORD_PREVENTER;

  private InternalRepository repository;

  private IAcceptor acceptor;

  private IConnector connector;

  private CDONet4jSession realmSession;

  private CDOView realmView;

  private Realm realm;

  private CDOID realmID;

  private volatile Long lastRealmModification;

  private Object lastRealmModificationLock = new Object();

  private boolean firstTime;

  public SecurityManager(String realmPath, IManagedContainer container)
  {
    this.realmPath = realmPath;
    this.container = container;
  }

  @Override
  public final IManagedContainer getContainer()
  {
    return container;
  }

  @Override
  public final String getRealmPath()
  {
    return realmPath;
  }

  @Override
  public final InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
    if (isActive())
    {
      init();
    }
  }

  @Override
  public InternalRepository[] getSecondaryRepositories()
  {
    return secondaryRepositories.keySet().toArray(new InternalRepository[0]);
  }

  @Override
  public void addSecondaryRepository(InternalRepository repository)
  {
    addSecondaryRepository(repository, null);
  }

  @Override
  public void addSecondaryRepository(InternalRepository repository, Map<String, Object> authorizationContext)
  {
    secondaryRepositories.computeIfAbsent(repository, k -> new SecondaryRepository(k, authorizationContext));
  }

  @Override
  public void removeSecondaryRepository(InternalRepository repository)
  {
    SecondaryRepository secondaryRepository = secondaryRepositories.remove(repository);
    if (secondaryRepository != null)
    {
      secondaryRepository.dispose();
    }
  }

  public Consumer<String> getPasswordValidator()
  {
    return passwordValidator;
  }

  public void setPasswordValidator(Consumer<String> passwordValidator)
  {
    checkInactive();
    this.passwordValidator = passwordValidator;
  }

  public void validatePassword(String password)
  {
    if (passwordValidator != null)
    {
      passwordValidator.accept(password);
    }
  }

  @Override
  public Realm getRealm()
  {
    return realm;
  }

  @Override
  public Role getRole(String id)
  {
    Role item = realm.getRole(id);
    if (item == null)
    {
      throw new SecurityException("Role " + id + " not found");
    }

    return item;
  }

  @Override
  public Group getGroup(String id)
  {
    Group item = realm.getGroup(id);
    if (item == null)
    {
      throw new SecurityException("Group " + id + " not found");
    }

    return item;
  }

  @Override
  public User getUser(String id)
  {
    User item = realm.getUser(id);
    if (item == null)
    {
      throw new SecurityException("User " + id + " not found");
    }

    return item;
  }

  @Override
  public Role addRole(String id)
  {
    Role[] result = { null };
    modify(realm -> result[0] = realm.addRole(id));
    return result[0];
  }

  @Override
  public Group addGroup(String id)
  {
    Group[] result = { null };
    modify(realm -> result[0] = realm.addGroup(id));
    return result[0];
  }

  @Override
  public User addUser(String id)
  {
    User[] result = { null };
    modify(realm -> result[0] = realm.addUser(id));
    return result[0];
  }

  @Override
  public User addUser(String id, String password)
  {
    User[] result = { null };
    modify(realm -> {
      result[0] = realm.addUser(id);

      if (!StringUtil.isEmpty(password))
      {
        UserPassword userPassword = SF.createUserPassword();
        userPassword.setEncrypted(new String(password));
        result[0].setPassword(userPassword);
      }
    });

    return result[0];
  }

  @Override
  public User addUser(IPasswordCredentials credentials)
  {
    return addUser(credentials.getUserID(), SecurityUtil.toString(credentials.getPassword()));
  }

  @Override
  public User setPassword(String id, String password)
  {
    validatePassword(password);

    User[] result = { null };
    modify(realm -> result[0] = realm.setPassword(id, password));
    return result[0];
  }

  @Override
  public Role removeRole(String id)
  {
    Role[] result = { null };
    modify(realm -> result[0] = realm.removeRole(id));
    return result[0];
  }

  @Override
  public Group removeGroup(String id)
  {
    Group[] result = { null };
    modify(realm -> result[0] = realm.removeGroup(id));
    return result[0];
  }

  @Override
  public User removeUser(String id)
  {
    User[] result = { null };
    modify(realm -> result[0] = realm.removeUser(id));
    return result[0];
  }

  @Override
  public void read(RealmOperation operation)
  {
    checkReady();
    operation.execute(realm);
  }

  @Override
  public void modify(RealmOperation operation)
  {
    modify(operation, false);
  }

  @Override
  public void modify(RealmOperation operation, boolean waitUntilReadable)
  {
    modifyWithInfo(operation, waitUntilReadable);
  }

  @Override
  public CDOCommitInfo modifyWithInfo(RealmOperation operation, boolean waitUntilReadable)
  {
    checkReady();
    CDOTransaction transaction = realmSession.openTransaction();

    try
    {
      Realm transactionRealm = transaction.getObject(realm);
      operation.execute(transactionRealm);
      CDOCommitInfo info = transaction.commit();

      if (waitUntilReadable)
      {
        if (!realmView.waitForUpdate(info.getTimeStamp(), REALM_UPDATE_TIMEOUT))
        {
          throw new TimeoutRuntimeException();
        }
      }

      return info;
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      transaction.close();
    }
  }

  @Override
  public CommitHandler[] getCommitHandlers()
  {
    return commitHandlers;
  }

  @Override
  public CommitHandler2[] getCommitHandlers2()
  {
    return commitHandlers2;
  }

  @Override
  public void addCommitHandler(CommitHandler handler)
  {
    checkInactive();
    synchronized (commitHandlerLock)
    {
      commitHandlers = ArrayUtil.add(commitHandlers, handler);

      if (handler instanceof CommitHandler2)
      {
        commitHandlers2 = ArrayUtil.add(commitHandlers2, (CommitHandler2)handler);
      }
    }
  }

  @Override
  public void removeCommitHandler(CommitHandler handler)
  {
    checkInactive();
    synchronized (commitHandlerLock)
    {
      commitHandlers = ArrayUtil.remove(commitHandlers, handler);

      if (handler instanceof CommitHandler2)
      {
        commitHandlers2 = ArrayUtil.remove(commitHandlers2, (CommitHandler2)handler);
      }
    }
  }

  protected void initCommitHandlers(boolean firstTime)
  {
    CommitHandler[] handlers = getCommitHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      CommitHandler handler = handlers[i];

      try
      {
        handler.init(this, firstTime);
        OM.LOG.info("Security realm handled by " + handler);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  protected void handleCommit(CommitContext commitContext, User user)
  {
    CommitHandler[] handlers = getCommitHandlers();
    for (int i = 0; i < handlers.length; i++)
    {
      CommitHandler handler = handlers[i];

      try
      {
        handler.handleCommit(this, commitContext, user);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  protected void handleCommitted(CommitContext commitContext)
  {
    CommitHandler2[] handlers = getCommitHandlers2();
    for (int i = 0; i < handlers.length; i++)
    {
      CommitHandler2 handler = handlers[i];

      try
      {
        handler.handleCommitted(this, commitContext);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * Commit-handlers can call back into the security manager to read/modify the realm
   * while the security manager is in the process of initializing, so cannot strictly
   * check for active state to assert that we are ready.
   */
  protected void checkReady()
  {
    if (realm == null || realmSession == null)
    {
      // If I have no realm or session, I am probably inactive, so this will throw
      checkActive();
    }
  }

  protected void init()
  {
    if (realm != null)
    {
      // Already initialized
      return;
    }

    if (repository == null)
    {
      // Cannot initialize
      return;
    }

    repository.setProtector(protector);
    repository.addListener(repositoryListener);
    if (!LifecycleUtil.isActive(repository))
    {
      // Cannot initialize now
      return;
    }

    String repositoryName = repository.getName();
    String acceptorName = repositoryName + "_security";

    acceptor = Net4jUtil.getAcceptor(container, "jvm", acceptorName);
    connector = Net4jUtil.getConnector(container, "jvm", acceptorName);

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repositoryName);
    config.setUserID(SYSTEM_USER_ID);

    realmSession = config.openNet4jSession();
    realmSession.options().setGeneratedPackageEmulationEnabled(true);
    realmSession.addListener(realmInvalidationListener);

    CDOTransaction initialTransaction = realmSession.openTransaction();

    firstTime = !initialTransaction.hasResource(realmPath);
    if (firstTime)
    {
      realm = createRealm();

      CDOResource resource = initialTransaction.createResource(realmPath);
      resource.getContents().add(realm);

      OM.LOG.info("Security realm created in " + realmPath);
    }
    else
    {
      CDOResource resource = initialTransaction.getResource(realmPath);
      realm = (Realm)resource.getContents().get(0);
      OM.LOG.info("Security realm loaded from " + realmPath);
    }

    try
    {
      initialTransaction.commit();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      initialTransaction.close();
    }

    realmView = realmSession.openView();
    realmView.addListener(realmInvalidationListener);

    realm = realmView.getObject(realm);
    realmID = realm.cdoID();

    InternalSessionManager sessionManager = repository.getSessionManager();
    sessionManager.setAuthenticator(this);
    sessionManager.setPermissionManager(permissionManager);
    sessionManager.addListener(sessionManagerListener);
    repository.addHandler(writeAccessHandler);

    register(repository);
    initCommitHandlers(firstTime);
  }

  protected Realm createRealm()
  {
    Realm realm = SF.createRealm("Security Realm");
    realm.setDefaultRoleDirectory(addDirectory(realm, Directory.ROLES));
    realm.setDefaultGroupDirectory(addDirectory(realm, Directory.GROUPS));
    realm.setDefaultUserDirectory(addDirectory(realm, Directory.USERS));

    // Create roles

    Role allReaderRole = realm.addRole(Role.ALL_OBJECTS_READER);
    allReaderRole.getPermissions().add(SF.createFilterPermission(Access.READ, SF.createResourceFilter(".*", PatternStyle.REGEX)));

    Role allWriterRole = realm.addRole(Role.ALL_OBJECTS_WRITER);
    allWriterRole.getPermissions().add(SF.createFilterPermission(Access.WRITE, SF.createResourceFilter(".*", PatternStyle.REGEX)));

    Role normalReaderRole = realm.addRole(Role.NORMAL_OBJECTS_READER);
    normalReaderRole.getPermissions()
        .add(SF.createFilterPermission(Access.READ, SF.createNotFilter(SF.createResourceFilter(realmPath, PatternStyle.EXACT, false))));

    Role normalWriterRole = realm.addRole(Role.NORMAL_OBJECTS_WRITER);
    normalWriterRole.getPermissions()
        .add(SF.createFilterPermission(Access.WRITE, SF.createNotFilter(SF.createResourceFilter(realmPath, PatternStyle.EXACT, false))));

    Role treeReaderRole = realm.addRole(Role.RESOURCE_TREE_READER);
    treeReaderRole.getPermissions().add(SF.createFilterPermission(Access.READ, SF.createPackageFilter(EresourcePackage.eINSTANCE)));

    Role treeWriterRole = realm.addRole(Role.RESOURCE_TREE_WRITER);
    treeWriterRole.getPermissions().add(SF.createFilterPermission(Access.WRITE, SF.createPackageFilter(EresourcePackage.eINSTANCE)));

    Role adminRole = SecurityManagerUtil.addResourceRole(realm, Role.ADMINISTRATION, realmPath, true);

    // Create groups

    Group adminsGroup = realm.addGroup(Group.ADMINISTRATORS);
    adminsGroup.getRoles().add(adminRole);

    realm.addGroup(Directory.USERS);

    // Create users

    User adminUser = realm.addUser(User.ADMINISTRATOR, User.ADMINISTRATOR_DEFAULT_PASSWORD);
    adminUser.getGroups().add(adminsGroup);

    return realm;
  }

  protected Directory addDirectory(Realm realm, String name)
  {
    Directory directory = SF.createDirectory(name);
    realm.getItems().add(directory);
    return directory;
  }

  protected CDOPermission convertPermission(Access access)
  {
    if (access != null)
    {
      switch (access)
      {
      case READ:
        return CDOPermission.READ;

      case WRITE:
        return CDOPermission.WRITE;
      }
    }

    return CDOPermission.NONE;
  }

  protected CDOPermission authorize(CDORevision revision, CDOBranchPoint securityContext, ISession session)
  {
    IRepository repository = session.getRepository();
    CDORevisionManager revisionManager = repository.getRevisionManager();
    CDORevisionProvider revisionProvider = new ManagedRevisionProvider(revisionManager, securityContext);

    PermissionUtil.initViewCreation(new ViewCreator()
    {
      @Override
      public CDOView createView(CDORevisionProvider revisionProvider)
      {
        CDOView view = CDOServerUtil.openView(session, securityContext, revisionProvider);
        view.getSession().options().setGeneratedPackageEmulationEnabled(true);
        return view;
      }
    });

    try
    {
      CDOPermission permission = authorize(revision, revisionProvider, securityContext, session, null, null);
      // System.out.println("Loading from " + session + ": " + permission + " --> " + revision);
      return permission;
    }
    finally
    {
      PermissionUtil.doneViewCreation();
    }
  }

  protected CDOPermission authorize(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, ISession session,
      Access defaultAccess, Permission[] permissions)
  {
    waitForRealmUpdate(securityContext);

    boolean setUser = defaultAccess == null;
    if (setUser)
    {
      UserInfo userInfo = getUserInfo(session);
      User user = userInfo.getUser();

      defaultAccess = user.getDefaultAccess();
      permissions = userInfo.getPermissions();

      PermissionUtil.setUser(user.getId());
    }

    try
    {
      CDOPermission result = convertPermission(defaultAccess);
      if (result == CDOPermission.WRITE)
      {
        return result;
      }

      for (int i = 0; i < permissions.length; i++)
      {
        Permission permission = permissions[i];

        CDOPermission p = convertPermission(permission.getAccess());
        if (p.ordinal() <= result.ordinal())
        {
          // Avoid expensive calls to Permission.isApplicable() if the permission wouldn't increase
          continue;
        }

        if (permission.isApplicable(revision, revisionProvider, securityContext))
        {
          result = p;
          if (result == CDOPermission.WRITE)
          {
            return result;
          }
        }
      }

      return result;
    }
    finally
    {
      if (setUser)
      {
        PermissionUtil.setUser(null);
      }
    }
  }

  protected void authorizeCommit(CommitContext commitContext, UserInfo userInfo)
  {
    PermissionUtil.setUser(userInfo.getUserId());
    PermissionUtil.initViewCreation(xxx -> CDOServerUtil.openView(commitContext));

    try
    {
      CDOBranchPoint securityContext = commitContext.getBranchPoint();

      ITransaction transaction = commitContext.getTransaction();
      ISession session = transaction.getSession();

      Access userDefaultAccess = userInfo.getDefaultAccess();
      Permission[] userPermissions = userInfo.getPermissions();

      if (!DISABLE_DETACH_CHECKS)
      {
        CDOID[] detachedObjects = commitContext.getDetachedObjects();
        if (detachedObjects != null && detachedObjects.length != 0)
        {
          for (int i = 0; i < detachedObjects.length; i++)
          {
            CDOID id = detachedObjects[i];
            CDORevision revision = transaction.getRevision(id);

            CDOPermission permission = authorize(revision, transaction, securityContext, session, userDefaultAccess, userPermissions);
            if (permission != CDOPermission.WRITE)
            {
              throw new SecurityException("User " + commitContext.getUserID() + " is not allowed to detach " + revision);
            }
          }
        }
      }

      InternalCDORevision[] revisions = commitContext.getDirtyObjects();
      InternalCDORevisionDelta[] revisionDeltas = commitContext.getDirtyObjectDeltas();

      // Check permissions on the commit changes and detect realm modifications
      byte securityImpact = CommitNotificationInfo.IMPACT_NONE;
      for (int i = 0; i < revisions.length; i++)
      {
        InternalCDORevision revision = revisions[i];

        CDOPermission permission = authorize(revision, commitContext, securityContext, session, userDefaultAccess, userPermissions);
        if (permission != CDOPermission.WRITE)
        {
          throw new SecurityException("User " + commitContext.getUserID() + " is not allowed to write to " + revision);
        }

        if (securityImpact != CommitNotificationInfo.IMPACT_REALM)
        {
          InternalCDORevisionDelta revisionDelta = revisionDeltas[i];
          if (CDORevisionUtil.isContained(revisionDelta.getID(), realmID, transaction)) // Use "before commit" state
          {
            securityImpact = CommitNotificationInfo.IMPACT_REALM;
          }
        }
      }

      // Determine permissions that are impacted by the commit changes
      Set<Permission> impactedRules = null;
      if (securityImpact != CommitNotificationInfo.IMPACT_REALM)
      {
        PermissionImpl[] assignedPermissions = permissionArray; // Thread-safe
        if (assignedPermissions.length != 0)
        {
          PermissionImpl.CommitImpactContext commitImpactContext = new PermissionImpl.CommitImpactContext()
          {
            @Override
            public CDORevision[] getNewObjects()
            {
              return commitContext.getNewObjects();
            }

            @Override
            public CDORevision[] getDirtyObjects()
            {
              return revisions;
            }

            @Override
            public CDORevisionDelta[] getDirtyObjectDeltas()
            {
              return revisionDeltas;
            }

            @Override
            public CDOID[] getDetachedObjects()
            {
              return commitContext.getDetachedObjects();
            }
          };

          for (int i = 0; i < assignedPermissions.length; i++)
          {
            PermissionImpl permission = assignedPermissions[i];
            if (permission.isImpacted(commitImpactContext))
            {
              if (impactedRules == null)
              {
                impactedRules = new HashSet<>();
              }

              impactedRules.add(permission);
            }
          }

          if (impactedRules != null)
          {
            securityImpact = CommitNotificationInfo.IMPACT_PERMISSIONS;
          }
        }
      }

      ((InternalCommitContext)commitContext).setSecurityImpact(securityImpact, impactedRules);
    }
    finally
    {
      PermissionUtil.setUser(null);
      PermissionUtil.doneViewCreation();
    }
  }

  protected void sessionAdded(ISession session)
  {
    String userID = session.getUserID();
    if (userID != null)
    {
      UserInfo result = userInfos.computeIfAbsent(userID, k -> {
        User user = getUser(k);
        UserInfo userInfo = new UserInfo(user);
        updatePermissions(userInfo, true);
        return userInfo;
      });

      result.addSessionRef();
    }
  }

  protected void sessionRemoved(ISession session)
  {
    String userID = session.getUserID();
    if (userID != null)
    {
      userInfos.computeIfPresent(userID, (k, v) -> {
        if (v.removeSessionRef())
        {
          return null;
        }

        return v;
      });
    }
  }

  protected UserInfo getUserInfo(ISession session)
  {
    String userID = session.getUserID();

    UserInfo userInfo = userInfos.get(userID);
    if (userInfo == null)
    {
      throw new IllegalStateException("No user info for " + userID);
    }

    return userInfo;
  }

  protected void clearUserInfos(boolean rebuild)
  {
    synchronized (permissionBag)
    {
      permissionBag.clear();
      permissionArray = null;

      if (rebuild)
      {
        for (UserInfo userInfo : userInfos.values())
        {
          userInfo.rebuildPermissions();
          updatePermissions(userInfo, false);
        }

        updatePermissions(null, true);
      }
      else
      {
        userInfos.clear();
      }
    }
  }

  protected void updatePermissions(UserInfo userInfo, boolean updateArray)
  {
    Permission[] permissions = userInfo == null ? null : userInfo.getPermissions();

    synchronized (permissionBag)
    {
      if (permissions != null)
      {
        for (int i = 0; i < permissions.length; i++)
        {
          Permission permission = permissions[i];
          permissionBag.add((PermissionImpl)permission);
        }
      }

      if (updateArray)
      {
        // Atomic update
        permissionArray = permissionBag.toArray(new PermissionImpl[permissionBag.size()]);
      }
    }
  }

  protected void denyAccess() throws SecurityException
  {
    throw new SecurityException("Access denied"); //$NON-NLS-1$
  }

  @Override
  public void authenticate(String userID, char[] password) throws SecurityException
  {
    try
    {
      User user = getUser(userID);
      if (user != null && !user.isLocked())
      {
        UserPassword userPassword = user.getPassword();

        String encrypted = userPassword == null ? null : userPassword.getEncrypted();
        if (Arrays.equals(password, SecurityUtil.toCharArray(encrypted)))
        {
          // Access granted.
          return;
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
  public void updatePassword(String userID, char[] oldPassword, char[] newPassword)
  {
    authenticate(userID, oldPassword);
    setPassword(userID, SecurityUtil.toString(newPassword));
  }

  @Override
  public void resetPassword(String adminID, char[] adminPassword, String userID, char[] newPassword)
  {
    authenticate(adminID, adminPassword);

    User admin = getUser(adminID);
    if (!isAdministrator(admin))
    {
      throw new SecurityException("Password reset requires administrator privilege"); //$NON-NLS-1$
    }

    setPassword(userID, SecurityUtil.toString(newPassword));
  }

  @Override
  public boolean isAdministrator(String userID)
  {
    Realm realm = getRealm();
    if (realm != null)
    {
      // Can't be an administrator if there isn't a realm
      // (but then where did we get the user ID?)
      User user = realm.getUser(userID);
      return user != null && isAdministrator(user);
    }

    return false;
  }

  protected final boolean isAdministrator(User user)
  {
    // An administrator is one that has write permission on the realm resource
    Realm realm = getRealm();
    if (realm != null)
    {
      // Can't be an administrator if there isn't a realm
      CDORevision revision = realm.cdoRevision();
      CDORevisionProvider revisionProvider = realm.cdoView();
      CDOBranchPoint securityContext = realm.cdoView();

      for (Permission permission : user.getAllPermissions())
      {
        if (permission.getAccess() == Access.WRITE && permission.isApplicable(revision, revisionProvider, securityContext))
        {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    init();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    clearUserInfos(false);

    realm = null;
    realmID = null;

    realmSession.close();
    realmSession = null;
    realmView = null;

    connector.close();
    connector = null;

    acceptor.close();
    acceptor = null;

    super.doDeactivate();
  }

  private void rememberRealmCommit(CDOBranchPoint commitBranchPoint)
  {
    synchronized (lastRealmModificationLock)
    {
      lastRealmModification = commitBranchPoint.getTimeStamp();
    }
  }

  private void waitForRealmUpdate(CDOBranchPoint securityContext)
  {
    if (lastRealmModification != null)
    {
      long updateTime;

      synchronized (lastRealmModificationLock)
      {
        if (lastRealmModification != null)
        {
          updateTime = lastRealmModification;
          lastRealmModification = null;
        }
        else
        {
          updateTime = CDOBranchPoint.UNSPECIFIED_DATE;
        }
      }

      if (updateTime != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        long contextTime = securityContext.getTimeStamp();
        if (contextTime == CDOBranchPoint.UNSPECIFIED_DATE || contextTime < updateTime)
        {
          if (!realmView.waitForUpdate(updateTime, REALM_UPDATE_TIMEOUT))
          {
            throw new TimeoutRuntimeException();
          }
        }
      }
    }
  }

  private void register(InternalRepository repository)
  {
    if (SECURITY_MANAGERS.putIfAbsent(repository, this) != null)
    {
      throw new IllegalStateException("A security manager is already associated with repository " + repository);
    }
  }

  private void unregister(InternalRepository repository)
  {
    SECURITY_MANAGERS.remove(repository);
  }

  public static InternalSecurityManager get(IRepository repository)
  {
    return SECURITY_MANAGERS.get(repository);
  }

  /**
   * @author Eike Stepper
   */
  private static final class UserInfo extends AtomicInteger
  {
    private static final long serialVersionUID = 1L;

    private final User user;

    private Permission[] permissions;

    public UserInfo(User user)
    {
      this.user = user;
      rebuildPermissions();
    }

    public User getUser()
    {
      return user;
    }

    public String getUserId()
    {
      return user.getId();
    }

    public Access getDefaultAccess()
    {
      return user.getDefaultAccess();
    }

    public Permission[] getPermissions()
    {
      return permissions;
    }

    public void rebuildPermissions()
    {
      EList<Permission> allPermissions = user.getAllPermissions();
      permissions = allPermissions.toArray(new Permission[allPermissions.size()]);
    }

    public synchronized void addSessionRef()
    {
      incrementAndGet();
    }

    public synchronized boolean removeSessionRef()
    {
      return decrementAndGet() == 0;
    }

    @Override
    public String toString()
    {
      return "UserInfo[user=" + getUserId() + ", refs=" + super.toString() + "]";
    }
  }

  // /**
  // * @author Eike Stepper
  // */
  // private final class Authenticator implements IAuthenticator2
  // {
  // public Authenticator()
  // {
  // }
  //
  // @Override
  // public void authenticate(String userID, char[] password) throws SecurityException
  // {
  // SecurityManager.this.authenticate(userID, password);
  // }
  //
  // @Override
  // public void updatePassword(String userID, char[] oldPassword, char[] newPassword)
  // {
  // SecurityManager.this.updatePassword(userID, oldPassword, newPassword);
  // }
  //
  // @Override
  // public void resetPassword(String adminID, char[] adminPassword, String userID, char[] newPassword)
  // {
  // SecurityManager.this.resetPassword(adminID, adminPassword, userID, newPassword);
  // }
  //
  // @Override
  // public boolean isAdministrator(String userID)
  // {
  // return SecurityManager.this.isAdministrator(userID);
  // }
  // }

  /**
   * @author Eike Stepper
   */
  private final class PermissionManager implements IPermissionManager
  {
    public PermissionManager()
    {
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
      String userID = session.getUserID();
      if (SYSTEM_USER_ID.equals(userID))
      {
        return CDOPermission.WRITE;
      }

      if (revision.getEClass() == SecurityPackage.Literals.USER_PASSWORD)
      {
        return CDOPermission.NONE;
      }

      return authorize(revision, securityContext, session);
    }

    @Override
    public boolean hasAnyRule(ISession session, Set<? extends Object> rules)
    {
      String userID = session.getUserID();
      if (SYSTEM_USER_ID.equals(userID))
      {
        return false;
      }

      if (ObjectUtil.isEmpty(rules))
      {
        return false;
      }

      UserInfo userInfo = getUserInfo(session);
      Permission[] userPermissions = userInfo.getPermissions();
      for (int i = 0; i < userPermissions.length; i++)
      {
        Permission userPermission = userPermissions[i];
        if (rules.contains(userPermission))
        {
          return true;
        }
      }

      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteAccessHandler implements IRepository.WriteAccessHandler
  {
    private final IRepository.WriteAccessHandler realmValidationHandler = new RealmValidationHandler();

    public WriteAccessHandler()
    {
    }

    @Override
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
    {
      if (transaction.getSessionID() == realmSession.getSessionID())
      {
        // Access through ISecurityManager.modify(RealmOperation)
        handleCommit(commitContext, null);
        ((InternalCommitContext)commitContext).setSecurityImpact(CommitNotificationInfo.IMPACT_REALM, null);
        return;
      }

      UserInfo userInfo = getUserInfo(transaction.getSession());

      handleCommit(commitContext, userInfo.getUser());
      authorizeCommit(commitContext, userInfo);

      if (commitContext.getSecurityImpact() == CommitNotificationInfo.IMPACT_REALM)
      {
        // Validate changes to the realm
        realmValidationHandler.handleTransactionBeforeCommitting(transaction, commitContext, monitor);
      }
    }

    @Override
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
      if (commitContext.getSecurityImpact() == CommitNotificationInfo.IMPACT_REALM)
      {
        CDOBranchPoint commitBranchPoint = commitContext.getBranchPoint();

        rememberRealmCommit(commitBranchPoint);
      }

      handleCommitted(commitContext);
    }
  }

  /**
   * A write-access handler that checks changes about to be committed to the security realm
   * against its well-formedness rules, and rejects the commit if there are any integrity
   * errors.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  private final class RealmValidationHandler extends ObjectWriteAccessHandler
  {
    private final EValidator realmValidator = EValidator.Registry.INSTANCE.getEValidator(SecurityPackage.eINSTANCE);

    public RealmValidationHandler()
    {
    }

    @Override
    protected void handleTransactionBeforeCommitting(OMMonitor monitor) throws RuntimeException
    {
      BasicDiagnostic diagnostic = new BasicDiagnostic();
      Map<Object, Object> context = createValidationContext();

      boolean realmChecked = false;
      for (EObject object : getDirtyObjects())
      {
        if (object.eClass().getEPackage() == SecurityPackage.eINSTANCE)
        {
          validate(object, diagnostic, context);
          realmChecked |= object instanceof Realm;
        }
      }

      for (EObject object : getNewObjects())
      {
        if (object.eClass().getEPackage() == SecurityPackage.eINSTANCE)
        {
          validate(object, diagnostic, context);
          // The realm cannot be new
        }
      }

      if (!realmChecked)
      {
        // Check it, because it has some wide-ranging integrity constraints
        validate(getView().getObject(realmID), diagnostic, context);
      }
    }

    protected Map<Object, Object> createValidationContext()
    {
      Map<Object, Object> result = new java.util.HashMap<>();
      CommitContext commitContext = getCommitContext();

      // Supply the revision-provider and branch point required by realm validation
      result.put(CDORevisionProvider.class, commitContext);
      result.put(CDOBranchPoint.class, commitContext.getBranchPoint());

      return result;
    }

    protected void validate(EObject object, DiagnosticChain diagnostics, Map<Object, Object> context)
    {
      realmValidator.validate(object, diagnostics, context);

      Diagnostic error = getError(diagnostics);
      if (error != null)
      {
        throw new TransactionValidationException("Security realm integrity violation: " + error.getMessage());
      }
    }

    protected Diagnostic getError(DiagnosticChain diagnostics)
    {
      Diagnostic diagnostic = (Diagnostic)diagnostics;
      if (diagnostic.getSeverity() >= Diagnostic.ERROR)
      {
        for (Diagnostic child : diagnostic.getChildren())
        {
          if (child.getSeverity() >= Diagnostic.ERROR)
          {
            return child;
          }
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class SecondaryRepository extends LifecycleEventAdapter implements IPermissionManager, IRepository.WriteAccessHandler
  {
    private final InternalRepository delegate;

    private final Map<String, Object> authorizationContext;

    public SecondaryRepository(InternalRepository delegate, Map<String, Object> authorizationContext)
    {
      this.delegate = delegate;
      this.authorizationContext = authorizationContext == null ? null : new HashMap<>(authorizationContext);

      InternalSessionManager sessionManager = delegate.getSessionManager();
      sessionManager.setAuthenticator(SecurityManager.this);
      sessionManager.setPermissionManager(this);
      sessionManager.addListener(sessionManagerListener);

      delegate.addListener(this);
      delegate.addHandler(this);
      register(delegate);
    }

    @Override
    @Deprecated
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
    {
      return null;
    }

    @Override
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, ISession session)
    {
      try
      {
        AuthorizationContext.set(authorizationContext);
        return permissionManager.getPermission(revision, securityContext, session);
      }
      finally
      {
        AuthorizationContext.set(null);
      }
    }

    @Override
    public boolean hasAnyRule(ISession session, Set<? extends Object> rules)
    {
      return permissionManager.hasAnyRule(session, rules);
    }

    @Override
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
    {
      InternalCDOPackageUnit[] newPackageUnits = commitContext.getNewPackageUnits();
      if (!ObjectUtil.isEmpty(newPackageUnits))
      {
        handleNewPackageUnits(newPackageUnits);
      }

      UserInfo userInfo = getUserInfo(transaction.getSession());

      try
      {
        AuthorizationContext.set(authorizationContext);
        authorizeCommit(commitContext, userInfo);
      }
      finally
      {
        AuthorizationContext.set(null);
      }
    }

    @Override
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
      // Do nothing.
    }

    public void dispose()
    {
      unregister(delegate);
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      removeSecondaryRepository(delegate);
    }

    private void handleNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits)
    {
      InternalCDOPackageRegistry realmPackageRegistry = getRepository().getPackageRegistry(false);
      List<InternalCDOPackageUnit> unknownPackageUnits = new ArrayList<>();

      for (InternalCDOPackageUnit packageUnit : newPackageUnits)
      {
        String nsURI = packageUnit.getID();
        if (realmPackageRegistry.getPackageUnit(nsURI) == null)
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
            synchronized (realmPackageRegistry)
            {
              realmPackageRegistry.putPackageUnits(unitArray, CDOPackageUnit.State.LOADED);
            }

            commitRealmPackageUnits(unitArray);
          });
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }

    private void commitRealmPackageUnits(InternalCDOPackageUnit[] packageUnits)
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
  }

  /**
   * @author Eike Stepper
   */
  private final class DelegatingProtector implements IRepositoryProtector
  {
    private final UserAuthenticator authenticator = new DelegatingAuthenticator();

    public DelegatingProtector()
    {
    }

    @Override
    public IManagedContainer getContainer()
    {
      return container;
    }

    @Override
    public boolean isFirstTime()
    {
      return firstTime;
    }

    @Override
    public IRepository getRepository()
    {
      return repository;
    }

    @Override
    public void setRepository(IRepository repository)
    {
      // Do nothing.
    }

    @Override
    public IRepository[] getSecondaryRepositories()
    {
      return SecurityManager.this.getSecondaryRepositories();
    }

    @Override
    public void addSecondaryRepository(IRepository repository)
    {
      SecurityManager.this.addSecondaryRepository((InternalRepository)repository);
    }

    @Override
    public void removeSecondaryRepository(IRepository repository)
    {
      SecurityManager.this.removeSecondaryRepository((InternalRepository)repository);
    }

    @Override
    public UserAuthenticator getUserAuthenticator()
    {
      return authenticator;
    }

    @Override
    public void setUserAuthenticator(UserAuthenticator userAuthenticator)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public AuthorizationStrategy getAuthorizationStrategy()
    {
      // Repository.setProtector() relies on null being returned here!
      return null;
    }

    @Override
    public void setAuthorizationStrategy(AuthorizationStrategy authorizationStrategy)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public RevisionAuthorizer[] getRevisionAuthorizers()
    {
      return new RevisionAuthorizer[0];
    }

    @Override
    public void addRevisionAuthorizer(RevisionAuthorizer authorizer)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeRevisionAuthorizer(RevisionAuthorizer authorizer)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public CommitHandler[] getCommitHandlers()
    {
      return new CommitHandler[0];
    }

    @Override
    public void addCommitHandler(CommitHandler handler)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeCommitHandler(CommitHandler handler)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public UserInfo getUserInfo(String userID)
    {
      return null;
    }

    @Override
    public boolean isEmpty()
    {
      return true;
    }

    @Override
    public UserInfo[] getElements()
    {
      return new UserInfo[0];
    }

    @Override
    public void addListener(IListener listener)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(IListener listener)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasListeners()
    {
      return false;
    }

    @Override
    public IListener[] getListeners()
    {
      return new IListener[0];
    }

    /**
     * @author Eike Stepper
     */
    private final class DelegatingAuthenticator extends UserAuthenticator implements UserAuthenticator.PasswordChangeSupport, IUserManager
    {
      public DelegatingAuthenticator()
      {
      }

      @Override
      public boolean isAdministrator(String userID)
      {
        return SecurityManager.this.isAdministrator(userID);
      }

      @Override
      public UserInfo authenticateUser(String userID, char[] password)
      {
        throw new UnsupportedOperationException();
      }

      @Override
      public void updatePassword(String userID, char[] oldPassword, char[] newPassword)
      {
        SecurityManager.this.updatePassword(userID, oldPassword, newPassword);
      }

      @Override
      public void resetPassword(String adminID, char[] adminPassword, String userID, char[] newPassword)
      {
        SecurityManager.this.resetPassword(adminID, adminPassword, userID, newPassword);
      }

      @Override
      public void resetPassword(String userID, char[] newPassword)
      {
        setPassword(userID, SecurityUtil.toString(newPassword));
      }

      @Override
      public void addUser(String userID, char[] password)
      {
        SecurityManager.this.addUser(userID, SecurityUtil.toString(password));
      }

      @Override
      public void removeUser(String userID)
      {
        SecurityManager.this.removeUser(userID);
      }

      @Override
      public byte[] encrypt(String userID, byte[] data, String algorithmName, byte[] salt, int count) throws SecurityException
      {
        char[] password = getPassword(userID);
        if (password == null)
        {
          return data;
        }

        try
        {
          return SecurityUtil.pbeEncrypt(data, password, algorithmName, salt, count);
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new SecurityException(ex);
        }
      }

      private char[] getPassword(String userID)
      {
        User user = getUser(userID);
        if (user == null || user.isLocked())
        {
          throw new SecurityException();
        }

        UserPassword userPassword = user.getPassword();
        String encrypted = userPassword == null ? null : userPassword.getEncrypted();
        return SecurityUtil.toCharArray(encrypted);
      }
    }
  }
}
