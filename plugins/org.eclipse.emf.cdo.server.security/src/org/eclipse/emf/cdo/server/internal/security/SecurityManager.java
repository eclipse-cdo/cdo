/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.security;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDOProtocol.CommitNotificationInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
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
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.ArrayUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.HashBag;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.IPasswordCredentials;

import org.eclipse.emf.common.util.EList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class SecurityManager extends Lifecycle implements InternalSecurityManager
{
  private static final Map<IRepository, InternalSecurityManager> SECURITY_MANAGERS = new HashMap<IRepository, InternalSecurityManager>();

  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

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
      SECURITY_MANAGERS.remove(getRepository());
      SecurityManager.this.deactivate();
    }
  };

  private final IListener sessionManagerListener = new ContainerEventAdapter<ISession>()
  {
    @Override
    protected void onRemoved(IContainer<ISession> container, ISession session)
    {
      removeUserInfo(session);
    }
  };

  private final IAuthenticator authenticator = new Authenticator();

  private final IPermissionManager permissionManager = new PermissionManager();

  private final IRepository.WriteAccessHandler writeAccessHandler = new WriteAccessHandler();

  private final String realmPath;

  private final IManagedContainer container;

  private final Map<ISession, UserInfo> userInfos = new HashMap<ISession, UserInfo>();

  private final HashBag<PermissionImpl> permissionBag = new HashBag<PermissionImpl>();

  private final Object commitHandlerLock = new Object();

  private CommitHandler[] commitHandlers = {};

  private CommitHandler2[] commitHandlers2 = {};

  private PermissionImpl[] permissionArray = {};

  private InternalRepository repository;

  private IAcceptor acceptor;

  private IConnector connector;

  private CDONet4jSession systemSession;

  private CDOView view;

  private Realm realm;

  private CDOID realmID;

  public SecurityManager(String realmPath, IManagedContainer container)
  {
    this.realmPath = realmPath;
    this.container = container;
  }

  public final IManagedContainer getContainer()
  {
    return container;
  }

  public final String getRealmPath()
  {
    return realmPath;
  }

  public final IRepository getRepository()
  {
    return repository;
  }

  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
    if (isActive())
    {
      init();
    }
  }

  public Realm getRealm()
  {
    return realm;
  }

  public Role getRole(String id)
  {
    Role item = realm.getRole(id);
    if (item == null)
    {
      throw new SecurityException("Role " + id + " not found");
    }

    return item;
  }

  public Group getGroup(String id)
  {
    Group item = realm.getGroup(id);
    if (item == null)
    {
      throw new SecurityException("Group " + id + " not found");
    }

    return item;
  }

  public User getUser(String id)
  {
    User item = realm.getUser(id);
    if (item == null)
    {
      throw new SecurityException("User " + id + " not found");
    }

    return item;
  }

  public Role addRole(final String id)
  {
    final Role[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.addRole(id);
      }
    });

    return result[0];
  }

  public Group addGroup(final String id)
  {
    final Group[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.addGroup(id);
      }
    });

    return result[0];
  }

  public User addUser(final String id)
  {
    final User[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.addUser(id);
      }
    });

    return result[0];
  }

  public User addUser(final String id, final String password)
  {
    final User[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        UserPassword userPassword = SF.createUserPassword();
        userPassword.setEncrypted(new String(password));

        result[0] = realm.addUser(id);
        result[0].setPassword(userPassword);
      }
    });

    return result[0];
  }

  public User addUser(IPasswordCredentials credentials)
  {
    return addUser(credentials.getUserID(), new String(credentials.getPassword()));
  }

  public User setPassword(final String id, final String password)
  {
    final User[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.setPassword(id, password);
      }
    });

    return result[0];
  }

  public Role removeRole(final String id)
  {
    final Role[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.removeRole(id);
      }
    });

    return result[0];
  }

  public Group removeGroup(final String id)
  {
    final Group[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.removeGroup(id);
      }
    });

    return result[0];
  }

  public User removeUser(final String id)
  {
    final User[] result = { null };
    modify(new RealmOperation()
    {
      public void execute(Realm realm)
      {
        result[0] = realm.removeUser(id);
      }
    });

    return result[0];
  }

  public void read(RealmOperation operation)
  {
    checkActive();
    operation.execute(realm);
  }

  public void modify(RealmOperation operation)
  {
    modify(operation, false);
  }

  public void modify(RealmOperation operation, boolean waitUntilReadable)
  {
    checkActive();
    CDOTransaction transaction = systemSession.openTransaction();

    try
    {
      Realm transactionRealm = transaction.getObject(realm);
      operation.execute(transactionRealm);
      CDOCommitInfo commit = transaction.commit();

      if (waitUntilReadable)
      {
        view.waitForUpdate(commit.getTimeStamp());
      }
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

  public CommitHandler[] getCommitHandlers()
  {
    return commitHandlers;
  }

  public CommitHandler2[] getCommitHandlers2()
  {
    return commitHandlers2;
  }

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

    systemSession = config.openNet4jSession();
    CDOTransaction transaction = systemSession.openTransaction();

    boolean firstTime = !transaction.hasResource(realmPath);
    if (firstTime)
    {
      realm = createRealm();

      CDOResource resource = transaction.createResource(realmPath);
      resource.getContents().add(realm);

      OM.LOG.info("Security realm created in " + realmPath);
    }
    else
    {
      CDOResource resource = transaction.getResource(realmPath);
      realm = (Realm)resource.getContents().get(0);
      OM.LOG.info("Security realm loaded from " + realmPath);
    }

    try
    {
      transaction.commit();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      transaction.close();
    }

    view = systemSession.openView();
    realm = view.getObject(realm);
    realmID = realm.cdoID();

    InternalSessionManager sessionManager = repository.getSessionManager();
    sessionManager.setAuthenticator(authenticator);
    sessionManager.setPermissionManager(permissionManager);
    sessionManager.addListener(sessionManagerListener);
    repository.addHandler(writeAccessHandler);

    SECURITY_MANAGERS.put(repository, this);
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
    allReaderRole.getPermissions().add(
        SF.createFilterPermission(Access.READ, SF.createResourceFilter(".*", PatternStyle.REGEX)));

    Role allWriterRole = realm.addRole(Role.ALL_OBJECTS_WRITER);
    allWriterRole.getPermissions().add(
        SF.createFilterPermission(Access.WRITE, SF.createResourceFilter(".*", PatternStyle.REGEX)));

    Role treeReaderRole = realm.addRole(Role.RESOURCE_TREE_READER);
    treeReaderRole.getPermissions().add(
        SF.createFilterPermission(Access.READ, SF.createPackageFilter(EresourcePackage.eINSTANCE)));

    Role treeWriterRole = realm.addRole(Role.RESOURCE_TREE_WRITER);
    treeWriterRole.getPermissions().add(
        SF.createFilterPermission(Access.WRITE, SF.createPackageFilter(EresourcePackage.eINSTANCE)));

    Role adminRole = realm.addRole(Role.ADMINISTRATION);
    adminRole.getPermissions().add(
        SF.createFilterPermission(Access.WRITE, SF.createResourceFilter(realmPath, PatternStyle.EXACT, false)));
    adminRole.getPermissions().add(
        SF.createFilterPermission(Access.READ, SF.createResourceFilter(realmPath, PatternStyle.EXACT, true)));

    // Create groups

    Group adminsGroup = realm.addGroup(Group.ADMINISTRATORS);
    adminsGroup.getRoles().add(adminRole);

    realm.addGroup(Directory.USERS);

    // Create users

    User adminUser = realm.addUser(User.ADMINISTRATOR, "0000");
    adminUser.getGroups().add(adminsGroup);

    return realm;
  }

  protected Directory addDirectory(Realm realm, String name)
  {
    Directory directory = SF.createDirectory(name);
    realm.getItems().add(directory);
    return directory;
  }

  protected CDOPermission convertPermission(Access permission)
  {
    if (permission != null)
    {
      switch (permission)
      {
      case READ:
        return CDOPermission.READ;

      case WRITE:
        return CDOPermission.WRITE;
      }
    }

    return CDOPermission.NONE;
  }

  protected CDOPermission authorize(CDORevision revision, CDORevisionProvider revisionProvider,
      CDOBranchPoint securityContext, ISession session, Access defaultAccess, Permission[] permissions)
  {
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

  protected UserInfo getUserInfo(ISession session)
  {
    UserInfo userInfo;
    synchronized (userInfos)
    {
      userInfo = userInfos.get(session);
    }

    if (userInfo == null)
    {
      userInfo = addUserInfo(session);
    }

    return userInfo;
  }

  protected UserInfo addUserInfo(ISession session)
  {
    String userID = session.getUserID();
    User user = getUser(userID);
    UserInfo userInfo = new UserInfo(user);

    synchronized (userInfos)
    {
      userInfos.put(session, userInfo);

      Permission[] permissions = userInfo.getPermissions();
      for (int i = 0; i < permissions.length; i++)
      {
        Permission permission = permissions[i];
        permissionBag.add((PermissionImpl)permission);
      }

      // Atomic update
      permissionArray = permissionBag.toArray(new PermissionImpl[permissionBag.size()]);
    }

    return userInfo;
  }

  protected UserInfo removeUserInfo(ISession session)
  {
    UserInfo userInfo;
    synchronized (userInfos)
    {
      userInfo = userInfos.remove(session);

      if (userInfo != null)
      {
        Permission[] permissions = userInfo.getPermissions();
        for (int i = 0; i < permissions.length; i++)
        {
          Permission permission = permissions[i];
          permissionBag.remove(permission);
        }

        // Atomic update
        permissionArray = permissionBag.toArray(new PermissionImpl[permissionBag.size()]);
      }
    }

    return userInfo;
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
    userInfos.clear();
    permissionBag.clear();
    permissionArray = null;

    realm = null;
    realmID = null;

    systemSession.close();
    systemSession = null;
    view = null;

    connector.close();
    connector = null;

    acceptor.close();
    acceptor = null;

    super.doDeactivate();
  }

  public static InternalSecurityManager get(IRepository repository)
  {
    return SECURITY_MANAGERS.get(repository);
  }

  /**
   * @author Eike Stepper
   */
  private static final class UserInfo
  {
    private final User user;

    private final Permission[] permissions;

    public UserInfo(User user)
    {
      this.user = user;
      EList<Permission> allPermissions = user.getAllPermissions();
      permissions = allPermissions.toArray(new Permission[allPermissions.size()]);
    }

    public User getUser()
    {
      return user;
    }

    public Permission[] getPermissions()
    {
      return permissions;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Authenticator implements IAuthenticator
  {
    public void authenticate(String userID, char[] password) throws SecurityException
    {
      User user = getUser(userID);
      UserPassword userPassword = user.getPassword();

      if (userPassword != null)
      {
        String encrypted = userPassword.getEncrypted();
        if (!Arrays.equals(password, encrypted == null ? null : encrypted.toCharArray()))
        {
          throw new SecurityException("Access denied"); //$NON-NLS-1$
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class PermissionManager implements IPermissionManager
  {
    @Deprecated
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
    {
      throw new UnsupportedOperationException();
    }

    public CDOPermission getPermission(CDORevision revision, final CDOBranchPoint securityContext,
        final ISession session)
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

      InternalCDORevisionManager revisionManager = repository.getRevisionManager();
      CDORevisionProvider revisionProvider = new ManagedRevisionProvider(revisionManager, securityContext);

      PermissionUtil.initViewCreation(new ViewCreator()
      {
        public CDOView createView(CDORevisionProvider revisionProvider)
        {
          return CDOServerUtil.openView(session, securityContext, revisionProvider);
        }
      });

      try
      {
        return authorize(revision, revisionProvider, securityContext, session, null, null);
      }
      finally
      {
        PermissionUtil.doneViewCreation();
      }
    }

    public boolean hasAnyRule(ISession session, Set<? extends Object> rules)
    {
      String userID = session.getUserID();
      if (SYSTEM_USER_ID.equals(userID))
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
    public void handleTransactionBeforeCommitting(ITransaction transaction, final CommitContext commitContext,
        OMMonitor monitor) throws RuntimeException
    {
      if (transaction.getSessionID() == systemSession.getSessionID())
      {
        // Access through ISecurityManager.modify(RealmOperation)
        handleCommit(commitContext, null);
        ((InternalCommitContext)commitContext).setSecurityImpact(CommitNotificationInfo.IMPACT_REALM, null);
        return;
      }

      UserInfo userInfo = getUserInfo(transaction.getSession());
      User user = userInfo.getUser();

      handleCommit(commitContext, user);

      PermissionUtil.setUser(user.getId());
      PermissionUtil.initViewCreation(new ViewCreator()
      {
        public CDOView createView(CDORevisionProvider revisionProvider)
        {
          return CDOServerUtil.openView(commitContext);
        }
      });

      try
      {
        CDOBranchPoint securityContext = commitContext.getBranchPoint();
        ISession session = transaction.getSession();

        Access userDefaultAccess = user.getDefaultAccess();
        Permission[] userPermissions = userInfo.getPermissions();

        final InternalCDORevision[] revisions = commitContext.getDirtyObjects();
        final InternalCDORevisionDelta[] revisionDeltas = commitContext.getDirtyObjectDeltas();

        // Check permissions on the commit changes and detect realm modifications
        byte securityImpact = CommitNotificationInfo.IMPACT_NONE;
        for (int i = 0; i < revisions.length; i++)
        {
          InternalCDORevision revision = revisions[i];
          CDOPermission permission = authorize(revision, commitContext, securityContext, session, userDefaultAccess,
              userPermissions);

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
            CommitImpactContext commitImpactContext = new PermissionImpl.CommitImpactContext()
            {
              public CDORevision[] getNewObjects()
              {
                return commitContext.getNewObjects();
              }

              public CDORevision[] getDirtyObjects()
              {
                return revisions;
              }

              public CDORevisionDelta[] getDirtyObjectDeltas()
              {
                return revisionDeltas;
              }

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
                  impactedRules = new HashSet<Permission>();
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

    public void handleTransactionAfterCommitted(ITransaction transaction, final CommitContext commitContext,
        OMMonitor monitor)
    {
      handleCommitted(commitContext);
    }
  }
}
