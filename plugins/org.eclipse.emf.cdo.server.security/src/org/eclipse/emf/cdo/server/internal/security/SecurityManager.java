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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
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
import org.eclipse.emf.cdo.security.Inclusion;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
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
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IAuthenticator;

import org.eclipse.emf.common.util.EList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SecurityManager extends Lifecycle implements InternalSecurityManager
{
  private static final Map<IRepository, InternalSecurityManager> SECURITY_MANAGERS = new HashMap<IRepository, InternalSecurityManager>();

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

  private final IAuthenticator authenticator = new Authenticator();

  private final IPermissionManager permissionManager = new PermissionManager();

  private final IRepository.WriteAccessHandler writeAccessHandler = new WriteAccessHandler();

  private final String realmPath;

  private final IManagedContainer container;

  private final Map<String, User> users = Collections.synchronizedMap(new HashMap<String, User>());

  private final Object commitHandlerLock = new Object();

  private CommitHandler[] commitHandlers = {};

  private CommitHandler2[] commitHandlers2 = {};

  private InternalRepository repository;

  private IAcceptor acceptor;

  private IConnector connector;

  private CDONet4jSession session;

  private CDOView view;

  private Realm realm;

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
    User item = users.get(id);
    if (item == null)
    {
      item = realm.getUser(id);
      if (item == null)
      {
        throw new SecurityException("User " + id + " not found");
      }

      users.put(id, item);
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
        UserPassword userPassword = SecurityFactory.eINSTANCE.createUserPassword();
        userPassword.setEncrypted(new String(password));

        result[0] = realm.addUser(id);
        result[0].setPassword(userPassword);
      }
    });

    return result[0];
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
    CDOTransaction transaction = session.openTransaction();

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

    session = config.openNet4jSession();
    CDOTransaction transaction = session.openTransaction();

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

    view = session.openView();
    realm = view.getObject(realm);

    InternalSessionManager sessionManager = repository.getSessionManager();
    sessionManager.setAuthenticator(authenticator);
    sessionManager.setPermissionManager(permissionManager);
    repository.addHandler(writeAccessHandler);

    SECURITY_MANAGERS.put(repository, this);
    initCommitHandlers(firstTime);
  }

  protected Realm createRealm()
  {
    final SecurityFactory factory = SecurityFactory.eINSTANCE;

    Realm realm = factory.createRealm("Security Realm");
    realm.setDefaultRoleDirectory(addDirectory(realm, "Roles"));
    realm.setDefaultGroupDirectory(addDirectory(realm, "Groups"));
    realm.setDefaultUserDirectory(addDirectory(realm, "Users"));

    // Create roles

    Role allReaderRole = realm.addRole("All Objects Reader");
    allReaderRole.getPermissions().add(factory.createFilterPermission(Access.READ, factory.createResourceFilter(".*")));

    Role allWriterRole = realm.addRole("All Objects Writer");
    allWriterRole.getPermissions()
        .add(factory.createFilterPermission(Access.WRITE, factory.createResourceFilter(".*")));

    Role treeReaderRole = realm.addRole("Resource Tree Reader");
    treeReaderRole.getPermissions().add(
        factory.createFilterPermission(Access.READ, factory.createPackageFilter(EresourcePackage.eINSTANCE)));

    Role treeWriterRole = realm.addRole("Resource Tree Writer");
    treeWriterRole.getPermissions().add(
        factory.createFilterPermission(Access.WRITE, factory.createPackageFilter(EresourcePackage.eINSTANCE)));

    Role adminRole = realm.addRole("Administration");
    adminRole.getPermissions()
        .add(
            factory.createFilterPermission(Access.WRITE,
                factory.createResourceFilter(realmPath, Inclusion.EXACT_AND_DOWN)));
    adminRole.getPermissions().add(
        factory.createFilterPermission(Access.READ, factory.createResourceFilter(realmPath, Inclusion.EXACT_AND_UP)));

    // Create groups

    Group adminsGroup = realm.addGroup("Administrators");
    adminsGroup.getRoles().add(adminRole);

    realm.addGroup("Users");

    // Create users

    User adminUser = realm.addUser("Administrator", "0000");
    adminUser.getGroups().add(adminsGroup);

    return realm;
  }

  protected Directory addDirectory(Realm realm, String name)
  {
    Directory directory = SecurityFactory.eINSTANCE.createDirectory(name);
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

  protected CDOPermission getPermission(CDORevision revision, CDORevisionProvider revisionProvider,
      CDOBranchPoint securityContext, ISession session, User user)
  {
    PermissionUtil.setUser(user.getId());

    try
    {
      CDOPermission result = convertPermission(user.getDefaultAccess());
      if (result == CDOPermission.WRITE)
      {
        return result;
      }

      EList<Permission> allPermissions = user.getAllPermissions();
      for (Permission permission : allPermissions)
      {
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
      PermissionUtil.setUser(null);
    }
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
    users.clear();
    realm = null;

    session.close();
    session = null;
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
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, ISession session)
    {
      String userID = session.getUserID();
      if (SYSTEM_USER_ID.equals(userID))
      {
        return CDOPermission.WRITE;
      }

      return doGetPermission(revision, securityContext, session, userID);
    }

    @Deprecated
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
    {
      if (SYSTEM_USER_ID.equals(userID))
      {
        return CDOPermission.WRITE;
      }

      return doGetPermission(revision, securityContext, null, userID);
    }

    private CDOPermission doGetPermission(CDORevision revision, final CDOBranchPoint securityContext,
        final ISession session, String userID)
    {
      if (revision.getEClass() == SecurityPackage.Literals.USER_PASSWORD)
      {
        return CDOPermission.NONE;
      }

      User user = getUser(userID);

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
        return SecurityManager.this.getPermission(revision, revisionProvider, securityContext, session, user);
      }
      finally
      {
        PermissionUtil.doneViewCreation();
      }
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
      if (transaction.getSessionID() == session.getSessionID())
      {
        // Access through ISecurityManager.modify(RealmOperation)
        handleCommit(commitContext, null);
        return;
      }

      CDOBranchPoint securityContext = commitContext.getBranchPoint();
      String userID = commitContext.getUserID();
      User user = getUser(userID);

      handleCommit(commitContext, user);

      PermissionUtil.initViewCreation(new ViewCreator()
      {
        public CDOView createView(CDORevisionProvider revisionProvider)
        {
          return CDOServerUtil.openView(commitContext);
        }
      });

      try
      {
        permissionRevisionsBeforeCommitting(commitContext, securityContext, user, commitContext.getDirtyObjects());
      }
      finally
      {
        PermissionUtil.doneViewCreation();
      }
    }

    private void permissionRevisionsBeforeCommitting(CommitContext commitContext, CDOBranchPoint securityContext,
        User user, InternalCDORevision[] revisions)
    {
      ISession session = commitContext.getTransaction().getSession();
      for (InternalCDORevision revision : revisions)
      {
        CDOPermission permission = getPermission(revision, commitContext, securityContext, session, user);
        if (permission != CDOPermission.WRITE)
        {
          throw new SecurityException("User " + user + " is not allowed to write to " + revision);
        }
      }
    }

    public void handleTransactionAfterCommitted(ITransaction transaction, final CommitContext commitContext,
        OMMonitor monitor)
    {
      handleCommitted(commitContext);
    }
  }
}
