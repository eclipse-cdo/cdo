/*
 * Copyright (c) 2013-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.internal.security.bundle.OM;
import org.eclipse.emf.cdo.server.security.ISecurityManager.RealmOperation;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager.CommitHandler;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.3
 */
public class HomeFolderHandler implements InternalSecurityManager.CommitHandler2, IExecutorServiceProvider
{
  public static final String DEFAULT_HOME_FOLDER = "/home";

  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

  private final String homeFolder;

  private ExecutorService executorService;

  public HomeFolderHandler(String homeFolder)
  {
    this.homeFolder = homeFolder == null || homeFolder.length() == 0 ? DEFAULT_HOME_FOLDER : homeFolder;
  }

  public HomeFolderHandler()
  {
    this(null);
  }

  public String getHomeFolder()
  {
    return homeFolder;
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return executorService;
  }

  public void setExecutorService(ExecutorService executorService)
  {
    this.executorService = executorService;
  }

  @Override
  public void init(InternalSecurityManager securityManager, boolean firstTime)
  {
    if (firstTime)
    {
      EList<User> users = securityManager.getRealm().getAllUsers();
      if (!users.isEmpty())
      {
        List<String> userIDs = new BasicEList<>();
        for (User user : users)
        {
          userIDs.add(user.getId());
        }

        handleUsers(securityManager, userIDs, true);
      }
    }
  }

  protected void initRole(Role role)
  {
    role.getPermissions().add(SF.createFilterPermission(Access.WRITE, //
        SF.createResourceFilter(homeFolder + "/${user}", PatternStyle.TREE, false)));

    role.getPermissions().add(SF.createFilterPermission(Access.READ, //
        SF.createResourceFilter(homeFolder, PatternStyle.EXACT, true)));
  }

  @Override
  public void handleCommit(final InternalSecurityManager securityManager, CommitContext commitContext, User user)
  {
    // Do nothing
  }

  @Override
  public void handleCommitted(final InternalSecurityManager securityManager, CommitContext commitContext)
  {
    List<String> userIDs = null;

    InternalCDORevision[] newObjects = commitContext.getNewObjects();
    for (int i = 0; i < newObjects.length; i++)
    {
      InternalCDORevision newObject = newObjects[i];
      EClass eClass = newObject.getEClass();
      if (eClass == SecurityPackage.Literals.USER)
      {
        String userID = (String)newObject.getValue(SecurityPackage.Literals.ASSIGNEE__ID);
        if (userID != null)
        {
          if (userIDs == null)
          {
            userIDs = new BasicEList<>();
          }

          userIDs.add(userID);
        }
      }
    }

    if (userIDs != null)
    {
      final List<String> list = userIDs;
      long commitTime = commitContext.getBranchPoint().getTimeStamp();

      CDOView view = securityManager.getRealm().cdoView();
      view.runAfterUpdate(commitTime, new Runnable()
      {
        @Override
        public void run()
        {
          handleUsers(securityManager, list, false);
        }
      });
    }
  }

  protected void handleUsers(final InternalSecurityManager securityManager, final List<String> userIDs, final boolean init)
  {
    executorService.submit(new Runnable()
    {
      @Override
      public void run()
      {
        securityManager.modify(new RealmOperation()
        {
          @Override
          public void execute(Realm realm)
          {
            String roleID = "Home Folder " + homeFolder;
            Role role;

            if (init)
            {
              role = realm.addRole(roleID);
              initRole(role);
            }
            else
            {
              role = realm.getRole(roleID);
              if (role == null)
              {
                OM.LOG.warn("Role '" + roleID + "' not found in " + HomeFolderHandler.this);
                return;
              }
            }

            CDOTransaction transaction = (CDOTransaction)realm.cdoView();

            for (String userID : userIDs)
            {
              try
              {
                User user = realm.getUser(userID);
                if (user == null)
                {
                  OM.LOG.warn("User '" + userID + "' not found in " + HomeFolderHandler.this);
                }
                else
                {
                  handleUser(transaction, realm, role, user);
                }
              }
              catch (Exception ex)
              {
                OM.LOG.error(ex);
              }
            }
          }
        });
      }
    });
  }

  protected void handleUser(CDOTransaction transaction, Realm realm, Role role, User user) throws Exception
  {
    EList<Role> roles = user.getRoles();
    roles.add(role);

    String path = getHomeFolder() + "/" + user.getId();
    transaction.getOrCreateResourceFolder(path);
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + homeFolder + "]";
  }

  /**
   * Creates {@link CommitHandler} instances.
   *
   * @author Eike Stepper
   */
  public static class Factory extends InternalSecurityManager.CommitHandler.Factory implements ContainerAware
  {
    private IManagedContainer container;

    public Factory()
    {
      super("home");
    }

    @Override
    public void setManagedContainer(IManagedContainer container)
    {
      this.container = container;
    }

    @Override
    public CommitHandler create(String homeFolder) throws ProductCreationException
    {
      ExecutorService executorService = ExecutorServiceFactory.get(container);

      HomeFolderHandler handler = new HomeFolderHandler(homeFolder);
      handler.setExecutorService(executorService);
      return handler;
    }
  }
}
