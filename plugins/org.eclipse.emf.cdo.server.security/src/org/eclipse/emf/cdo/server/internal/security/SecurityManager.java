/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.ClassPermission;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.PackagePermission;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
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

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IUserManager;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SecurityManager extends Lifecycle implements InternalSecurityManager
{
  private IListener repositoryListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onActivated(ILifecycle lifecycle)
    {
      init();
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  private final IUserManager userManager = new UserManager();

  private final IPermissionManager permissionManager = new PermissionManager();

  private final IRepository.WriteAccessHandler writeAccessHandler = new WriteAccessHandler();

  private final List<CommitHandler> commitHandlers = new ArrayList<CommitHandler>();

  private final String realmPath;

  private final IManagedContainer container;

  private final Map<String, User> users = new HashMap<String, User>();

  private InternalRepository repository;

  private IAcceptor acceptor;

  private IConnector connector;

  private CDOTransaction transaction;

  private Realm realm;

  private EList<SecurityItem> newUsers;

  private EList<SecurityItem> newGroups;

  private EList<SecurityItem> newRoles;

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
    init();
  }

  public Realm getRealm()
  {
    return realm;
  }

  public User getUser(String userID)
  {
    synchronized (users)
    {
      User user = users.get(userID);
      if (user == null)
      {
        EList<SecurityItem> items = realm.getItems();
        user = RealmUtil.findUser(items, userID);
        if (user == null)
        {
          throw new SecurityException("User " + userID + " not found");
        }

        users.put(userID, user);
      }

      return user;
    }
  }

  public void modify(RealmOperation operation)
  {
    synchronized (transaction)
    {
      operation.execute(realm);

      try
      {
        transaction.commit();
      }
      catch (CommitException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  public CommitHandler[] getCommitHandlers()
  {
    synchronized (commitHandlers)
    {
      return commitHandlers.toArray(new CommitHandler[commitHandlers.size()]);
    }
  }

  public void addCommitHandler(CommitHandler handler)
  {
    checkInactive();
    synchronized (commitHandlers)
    {
      if (!commitHandlers.contains(handler))
      {
        commitHandlers.add(handler);
      }
    }
  }

  public void removeCommitHandler(CommitHandler handler)
  {
    checkInactive();
    synchronized (commitHandlers)
    {
      commitHandlers.remove(handler);
    }
  }

  protected void initCommitHandlers(boolean firstTime)
  {
    for (CommitHandler handler : getCommitHandlers())
    {
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
    for (CommitHandler handler : getCommitHandlers())
    {
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

  protected void init()
  {
    if (!isActive() || repository == null)
    {
      return;
    }

    repository.addListener(repositoryListener);
    if (!LifecycleUtil.isActive(repository))
    {
      return;
    }

    String repositoryName = repository.getName();
    String acceptorName = repositoryName + "_security";

    acceptor = Net4jUtil.getAcceptor(container, "jvm", acceptorName);
    connector = Net4jUtil.getConnector(container, "jvm", acceptorName);

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repositoryName);

    CDONet4jSession session = config.openNet4jSession();
    transaction = session.openTransaction();

    boolean firstTime = !transaction.hasResource(realmPath);
    if (firstTime)
    {
      CDOResource resource = transaction.createResource(realmPath);
      realm = createRealm();
      resource.getContents().add(realm);
    }
    else
    {
      CDOResource resource = transaction.getResource(realmPath);
      realm = (Realm)resource.getContents().get(0);
    }

    initCommitHandlers(firstTime);

    try
    {
      transaction.commit();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }

    InternalSessionManager sessionManager = repository.getSessionManager();
    sessionManager.setUserManager(userManager);
    sessionManager.setPermissionManager(permissionManager);
    repository.addHandler(writeAccessHandler);
  }

  protected Realm createRealm()
  {
    Realm realm = SecurityFactory.eINSTANCE.createRealm();
    realm.setName("Security Realm");

    // Create directories

    Directory users = SecurityFactory.eINSTANCE.createDirectory();
    users.setName("Users");
    realm.getItems().add(users);
    newUsers = users.getItems();

    Directory groups = SecurityFactory.eINSTANCE.createDirectory();
    groups.setName("Groups");
    realm.getItems().add(groups);
    newGroups = groups.getItems();

    Directory roles = SecurityFactory.eINSTANCE.createDirectory();
    roles.setName("Roles");
    realm.getItems().add(roles);
    newRoles = roles.getItems();

    // Create items

    User admin = SecurityFactory.eINSTANCE.createUser();
    admin.setId("Administrator");
    newUsers.add(admin);

    UserPassword adminPassword = SecurityFactory.eINSTANCE.createUserPassword();
    adminPassword.setEncrypted("0000");
    admin.setPassword(adminPassword);

    Group admins = SecurityFactory.eINSTANCE.createGroup();
    admins.setId("Administrators");
    admins.getUsers().add(admin);
    newGroups.add(admins);

    // Create administration role

    Role administration = SecurityFactory.eINSTANCE.createRole();
    administration.setId("Administration");
    administration.getAssignees().add(admins);
    newRoles.add(administration);

    PackagePermission allResources = SecurityFactory.eINSTANCE.createPackagePermission();
    allResources.setAccess(Access.READ);
    administration.getPermissions().add(allResources);
    allResources.setApplicablePackage(EresourcePackage.eINSTANCE);

    for (EClassifier eClassifier : SecurityPackage.eINSTANCE.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        if (eClass.isInterface() || eClass.isAbstract() || eClass == SecurityPackage.Literals.USER_PASSWORD)
        {
          continue;
        }

        ClassPermission permission = SecurityFactory.eINSTANCE.createClassPermission();
        permission.setAccess(Access.WRITE);
        administration.getPermissions().add(permission);
        permission.setApplicableClass(eClass);
      }
    }

    return realm;
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
      CDOBranchPoint securityContext, User user)
  {
    CDOPermission result = convertPermission(user.getDefaultAccess());
    if (result == CDOPermission.WRITE)
    {
      return result;
    }

    for (Permission permission : user.getAllPermissions())
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

    transaction.getSession().close();
    transaction = null;

    connector.close();
    connector = null;

    acceptor.close();
    acceptor = null;

    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class UserManager implements IUserManager
  {
    public void addUser(final String userID, final char[] password)
    {
      modify(new RealmOperation()
      {
        public void execute(Realm realm)
        {
          UserPassword userPassword = SecurityFactory.eINSTANCE.createUserPassword();
          userPassword.setEncrypted(new String(password));

          User user = SecurityFactory.eINSTANCE.createUser();
          user.setId(userID);
          user.setPassword(userPassword);

          realm.getItems().add(user);
        }
      });
    }

    public void removeUser(final String userID)
    {
      modify(new RealmOperation()
      {
        public void execute(Realm realm)
        {
          User user = getUser(userID);
          EcoreUtil.remove(user);
        }
      });
    }

    public byte[] encrypt(String userID, byte[] data, String algorithmName, byte[] salt, int count)
        throws SecurityException
    {
      User user = getUser(userID);
      UserPassword userPassword = user.getPassword();
      String encrypted = userPassword == null ? null : userPassword.getEncrypted();
      char[] password = encrypted == null ? null : encrypted.toCharArray();
      if (password == null)
      {
        throw new SecurityException("No password: " + userID);
      }

      try
      {
        return SecurityUtil.encrypt(data, password, algorithmName, salt, count);
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
  }

  /**
   * @author Eike Stepper
   */
  private final class PermissionManager implements IPermissionManager
  {
    public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
    {
      User user = getUser(userID);

      InternalCDORevisionManager revisionManager = repository.getRevisionManager();
      CDORevisionProvider revisionProvider = new ManagedRevisionProvider(revisionManager, securityContext);

      return SecurityManager.this.getPermission(revision, revisionProvider, securityContext, user);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class WriteAccessHandler implements IRepository.WriteAccessHandler
  {
    public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext,
        OMMonitor monitor) throws RuntimeException
    {
      CDOBranchPoint securityContext = commitContext.getBranchPoint();
      String userID = commitContext.getUserID();
      User user = getUser(userID);

      handleCommit(commitContext, user);

      permissionRevisionsBeforeCommitting(commitContext, securityContext, user, commitContext.getNewObjects());
      permissionRevisionsBeforeCommitting(commitContext, securityContext, user, commitContext.getDirtyObjects());
    }

    private void permissionRevisionsBeforeCommitting(CommitContext commitContext, CDOBranchPoint securityContext,
        User user, InternalCDORevision[] revisions)
    {
      for (InternalCDORevision revision : revisions)
      {
        CDOPermission permission = getPermission(revision, commitContext, securityContext, user);
        if (permission != CDOPermission.WRITE)
        {
          throw new SecurityException("User " + user + " is not allowed to write to " + revision);
        }
      }
    }

    /**
     * @deprecated Not used.
     */
    @Deprecated
    public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
    {
    }
  }
}
