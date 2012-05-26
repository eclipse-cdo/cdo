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
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.User;
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

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.security.IUserManager;

import org.eclipse.emf.common.util.EList;

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

  private IManagedContainer container;

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

  public void addUser(String userID, char[] password)
  {
  }

  public void removeUser(String userID)
  {
  }

  public byte[] encrypt(String userID, byte[] data, String algorithmName, byte[] salt, int count)
      throws SecurityException
  {
    return null;
  }

  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext, String userID)
  {
    User user = getUser(userID);
    CDORevisionProvider revisionProvider = new ManagedRevisionProvider(repository.getRevisionManager(), securityContext);
    return getPermission(revision, revisionProvider, user);
  }

  public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      throws RuntimeException
  {
    String userID = commitContext.getUserID();
    User user = getUser(userID);

    for (InternalCDORevision revision : commitContext.getNewObjects())
    {
      getPermission(revision, commitContext, user);

    }
  }

  public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
  {
    // Do nothing
  }

  private User getUser(String userID)
  {
    synchronized (users)
    {
      User user = users.get(userID);
      if (user == null)
      {
        EList<SecurityItem> items = realm.getItems();
        user = RealmUtil.findUser(items, userID);
        if (user != null)
        {
          users.put(userID, user);
        }
      }

      return user;
    }
  }

  private CDOPermission getPermission(CDORevision revision, CDORevisionProvider revisionProvider, User user)
  {
    return null;
  }
}
