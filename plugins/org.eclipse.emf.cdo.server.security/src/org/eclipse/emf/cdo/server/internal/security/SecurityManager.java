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
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
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
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IUserManager;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class SecurityManager implements ISecurityManager, IUserManager, IPermissionManager,
    IRepository.WriteAccessHandler
{
  private final InternalRepository repository;

  private final String realmPath;

  private final IManagedContainer container;

  private IAcceptor acceptor;

  private IConnector connector;

  private CDOTransaction transaction;

  private Realm realm;

  private Map<String, User> users = new HashMap<String, User>();

  public SecurityManager(IRepository repository, String realmPath, IManagedContainer container)
  {
    this.repository = (InternalRepository)repository;
    this.realmPath = realmPath;
    this.container = container;

    init();
  }

  private void init()
  {
    String repositoryName = repository.getName();
    String acceptorName = repositoryName + "_security";

    acceptor = Net4jUtil.getAcceptor(container, "jvm", acceptorName);
    connector = Net4jUtil.getConnector(container, "jvm", acceptorName);

    CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
    config.setConnector(connector);
    config.setRepositoryName(repositoryName);

    CDONet4jSession session = config.openNet4jSession();
    transaction = session.openTransaction();

    CDOResource resource = transaction.getResource(realmPath);
    realm = (Realm)resource.getContents().get(0);

    // Wire up with repository
    InternalSessionManager sessionManager = repository.getSessionManager();
    sessionManager.setUserManager(this);
    sessionManager.setPermissionManager(this);
    repository.addHandler(this);
    repository.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        dispose();
      }
    });
  }

  private void dispose()
  {
    users.clear();
    realm = null;

    transaction.getSession().close();
    transaction = null;

    connector.close();
    connector = null;

    acceptor.close();
    acceptor = null;
  }

  public final IRepository getRepository()
  {
    return repository;
  }

  public final String getRealmPath()
  {
    return realmPath;
  }

  public final IManagedContainer getContainer()
  {
    return container;
  }

  public Realm getRealm()
  {
    return realm;
  }

  public Group getGroup(String groupID)
  {
    EList<SecurityItem> items = realm.getItems();
    Group group = RealmUtil.findGroup(items, groupID);
    if (group == null)
    {
      throw new SecurityException("Group " + groupID + " not found");
    }

    return group;
  }

  public Role getRole(String roleID)
  {
    EList<SecurityItem> items = realm.getItems();
    Role role = RealmUtil.findRole(items, roleID);
    if (role == null)
    {
      throw new SecurityException("Role " + roleID + " not found");
    }

    return role;
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
    String password = userPassword == null ? null : userPassword.getEncrypted();
    if (password != null)
    {
      // TODO
    }

    // TODO: implement SecurityManager.encrypt(userID, data, algorithmName, salt, count)
    throw new UnsupportedOperationException();
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

  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
  {
    User user = getUser(userID);
    CDORevisionProvider revisionProvider = new ManagedRevisionProvider(repository.getRevisionManager(), securityContext);
    return getPermission(revision, revisionProvider, securityContext, user);
  }

  public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      throws RuntimeException
  {
    CDOBranchPoint securityContext = commitContext.getBranchPoint();
    String userID = commitContext.getUserID();
    User user = getUser(userID);

    handleRevisionsBeforeCommitting(commitContext, securityContext, user, commitContext.getNewObjects());
    handleRevisionsBeforeCommitting(commitContext, securityContext, user, commitContext.getDirtyObjects());
  }

  private void handleRevisionsBeforeCommitting(CommitContext commitContext, CDOBranchPoint securityContext, User user,
      InternalCDORevision[] revisions)
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

  public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
  {
    // Do nothing
  }

  protected CDOPermission getPermission(CDORevision revision, CDORevisionProvider revisionProvider,
      CDOBranchPoint securityContext, User user)
  {
    // TODO: implement SecurityManager.getPermission(revision, revisionProvider, securityContext, user)
    throw new UnsupportedOperationException();
  }
}
