/*
 * Copyright (c) 2013, 2015, 2018-2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alex Lagarde - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.PatternStyle;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionChangeRecorder;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;

import java.util.concurrent.CountDownLatch;

/**
 * Bug 417483 - [Security] Issues in invalidation when missing write Permission.
 * <p>
 * Tests ensuring that the permissions are correctly computed, no matter what passive update mode is chosen.
 *
 * @author Alex Lagarde
 */
@CleanRepositoriesBefore(reason = "TEST_SECURITY_MANAGER")
@CleanRepositoriesAfter(reason = "TEST_SECURITY_MANAGER")
public class Bugzilla_417483_Test extends AbstractCDOTest
{
  private static final SecurityFactory SF = SecurityFactory.eINSTANCE;

  private static final String REALM_PATH = "/security";

  private static final IPasswordCredentials CREDENTIALS = new PasswordCredentials("Stepper", "12345");

  private static final IPasswordCredentials CREDENTIALS_READ_ONLY = new PasswordCredentials("Lagarde", "54321");

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager(REALM_PATH, getServerContainer());

    // Start repository
    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, securityManager);
    getRepository();

    securityManager.modify(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user = realm.addUser(CREDENTIALS);
        user.getRoles().add(realm.getRole("All Objects Writer"));

        User userReadOnly = realm.addUser(CREDENTIALS_READ_ONLY);
        userReadOnly.getRoles().add(realm.getRole("All Objects Reader"));
      }
    });
  }

  public void testPassiveUpdates_Invalidations() throws Exception
  {
    doPassiveUpdates(PassiveUpdateMode.INVALIDATIONS, false);
  }

  public void testPassiveUpdates_InvalidationsWithEditingDomain() throws Exception
  {
    doPassiveUpdates(PassiveUpdateMode.INVALIDATIONS, true);
  }

  public void testPassiveUpdates_Changes() throws Exception
  {
    doPassiveUpdates(PassiveUpdateMode.CHANGES, false);
  }

  public void testPassiveUpdates_Changes_WithEditingDomain() throws Exception
  {
    doPassiveUpdates(PassiveUpdateMode.CHANGES, true);
  }

  public void testPassiveUpdates_Additions() throws Exception
  {
    doPassiveUpdates(PassiveUpdateMode.ADDITIONS, false);
  }

  public void testPassiveUpdates_Additions_WithEditingDomain() throws Exception
  {
    doPassiveUpdates(PassiveUpdateMode.ADDITIONS, true);
  }

  protected void doPassiveUpdates(PassiveUpdateMode passiveUpdateMode, boolean withEditingDomain) throws Exception
  {
    // Both users connect to the repository
    CDOSession session = openSession(CREDENTIALS);
    CDOTransaction transaction = session.openTransaction();

    CDOSession sessionReadOnly = openSession(CREDENTIALS_READ_ONLY);
    sessionReadOnly.options().setPassiveUpdateEnabled(true);
    sessionReadOnly.options().setPassiveUpdateMode(passiveUpdateMode);

    ResourceSet resourceSetReadOnly;
    if (withEditingDomain)
    {
      resourceSetReadOnly = createTransactionalEditingDomain().getResourceSet();
    }
    else
    {
      resourceSetReadOnly = new ResourceSetImpl();
    }

    CDOTransaction transactionReadOnly = sessionReadOnly.openTransaction(resourceSetReadOnly);
    assertEquals(CREDENTIALS.getUserID(), session.getUserID());
    assertEquals(CREDENTIALS_READ_ONLY.getUserID(), sessionReadOnly.getUserID());

    // User with write permission creates a resource and commits
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    assertEquals(true, isWritable(resource));

    Category category = getModel1Factory().createCategory();
    resource.getContents().add(category);
    commitAndSync(transaction, transactionReadOnly);

    // User without write permission should be able to integrate changes without permission issues
    CDOResource resourceReadOnly = transactionReadOnly.getResource(getResourcePath("/res"));
    assertEquals(1, resourceReadOnly.getContents().size());
    assertEquals("User should not have write permission on resource", false, isWritable(resourceReadOnly));

    // => Trigger loading of resource root so that invalidation are sent
    Category categoryReadOnly = (Category)resourceReadOnly.getContents().get(0);
    assertEquals("User should not have write permission on element", false, isWritable(categoryReadOnly));

    // User with write permission modifies the resource root and commits
    category.getProducts().add(getModel1Factory().createProduct1());
    category.setName("RENAMED");

    CountDownLatch latch = new CountDownLatch(1);
    transactionReadOnly.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof CDOViewInvalidationEvent)
        {
          latch.countDown();
        }
      }
    });

    transaction.commit();

    // User without write permission should be able to integrate changes without permission issues
    await(latch);

    assertEquals("User should not have write permission on resource", false, isWritable(resourceReadOnly));
    assertEquals("User should not have write permission on element", false, isWritable(categoryReadOnly));
  }

  /**
   * Creates a {@link TransactionalEditingDomain} which has a {@link TransactionChangeRecorder} that does not throw an {@link IllegalStateException} if model is modified through a {@link CDODeltaNotification}.
   */
  private TransactionalEditingDomain createTransactionalEditingDomain()
  {
    return new TransactionalEditingDomainImpl(null)
    {
      @Override
      protected TransactionChangeRecorder createChangeRecorder(ResourceSet rset)
      {
        return new TransactionChangeRecorder(this, rset)
        {
          @Override
          public void notifyChanged(Notification notification)
          {
            if (!(notification instanceof CDODeltaNotification))
            {
              super.notifyChanged(notification);
            }
          }
        };
      }
    };
  }

  public void testLoad_NoPermission() throws Exception
  {
    IPasswordCredentials credentials = new PasswordCredentials("user", "password");
    String protectedResource = getResourcePath("/protected");

    getSecurityManager().modify(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user = realm.addUser(credentials);

        Role role = realm.addRole(protectedResource + " Reader");
        role.getPermissions().add(SF.createFilterPermission(Access.READ, //
            SF.createResourceFilter(protectedResource, PatternStyle.EXACT, true).setModelObjects(false)));

        user.getRoles().add(role);
      }
    });

    {
      // Init model with write full access
      CDOSession session = openSession(CREDENTIALS);
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(protectedResource);
      resource.getContents().add(getModel1Factory().createCompany());
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession(credentials);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(protectedResource);
    assertEquals(CDOPermission.READ, resource.cdoPermission());

    Company company = (Company)resource.getContents().get(0);
    assertEquals(CDOPermission.NONE, CDOUtil.getCDOObject(company).cdoPermission());
  }

  public void testLoad_ReadPermission() throws Exception
  {
    IPasswordCredentials credentials = new PasswordCredentials("user", "password");
    String protectedResource = getResourcePath("/protected");

    getSecurityManager().modify(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user = realm.addUser(credentials);

        Role role = realm.addRole(protectedResource + " Reader");
        role.getPermissions().add(SF.createFilterPermission(Access.READ, //
            SF.createResourceFilter(protectedResource, PatternStyle.TREE, true)));

        user.getRoles().add(role);
      }
    });

    {
      // Init model with write full access
      CDOSession session = openSession(CREDENTIALS);
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(protectedResource);
      resource.getContents().add(getModel1Factory().createCompany());
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession(credentials);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(protectedResource);
    assertEquals(CDOPermission.READ, resource.cdoPermission());

    Company company = (Company)resource.getContents().get(0);
    assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(company).cdoPermission());
  }

  public void testCommit_NoPermission() throws Exception
  {
    IPasswordCredentials credentials = new PasswordCredentials("user", "password");
    String protectedResource = getResourcePath("/protected");

    getSecurityManager().modify(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user = realm.addUser(credentials);

        Role role = realm.addRole(protectedResource + " Writer");
        role.getPermissions().add(SF.createFilterPermission(Access.WRITE, //
            SF.createResourceFilter(protectedResource, PatternStyle.EXACT, true).setModelObjects(false)));

        user.getRoles().add(role);
      }
    });

    CDOSession session = openSession(credentials);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(protectedResource);
    assertEquals(CDOPermission.WRITE, resource.cdoPermission());

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(company).cdoPermission());

    transaction.commit();
    assertEquals(CDOPermission.NONE, CDOUtil.getCDOObject(company).cdoPermission());
  }

  public void testCommit_ReadPermission() throws Exception
  {
    IPasswordCredentials credentials = new PasswordCredentials("user", "password");
    String protectedResource = getResourcePath("/protected");

    getSecurityManager().modify(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user = realm.addUser(credentials);

        Role role = realm.addRole(protectedResource + " Writer");
        role.getPermissions().add(SF.createFilterPermission(Access.WRITE, //
            SF.createResourceFilter(protectedResource, PatternStyle.EXACT, true).setModelObjects(false)));
        role.getPermissions().add(SF.createFilterPermission(Access.READ, //
            SF.createResourceFilter(protectedResource, PatternStyle.EXACT, false).setModelObjects(true)));

        user.getRoles().add(role);
      }
    });

    CDOSession session = openSession(credentials);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(protectedResource);
    assertEquals(CDOPermission.WRITE, resource.cdoPermission());

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(company).cdoPermission());

    transaction.commit();
    assertEquals(CDOPermission.READ, CDOUtil.getCDOObject(company).cdoPermission());
  }

  public void testCommit_WritePermission() throws Exception
  {
    IPasswordCredentials credentials = new PasswordCredentials("user", "password");
    String protectedResource = getResourcePath("/protected");

    getSecurityManager().modify(new ISecurityManager.RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        User user = realm.addUser(credentials);

        Role role = realm.addRole(protectedResource + " Writer");
        role.getPermissions().add(SF.createFilterPermission(Access.WRITE, //
            SF.createResourceFilter(protectedResource, PatternStyle.EXACT, true).setModelObjects(true)));

        user.getRoles().add(role);
      }
    });

    CDOSession session = openSession(credentials);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(protectedResource);
    assertEquals(CDOPermission.WRITE, resource.cdoPermission());

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(company).cdoPermission());

    transaction.commit();
    assertEquals(CDOPermission.WRITE, CDOUtil.getCDOObject(company).cdoPermission());
  }

  public void testRemoveUser() throws Exception
  {
    IPasswordCredentials admin = new PasswordCredentials(User.ADMINISTRATOR, "0000");
    CDOSession session = openSession(admin);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(REALM_PATH);

    Realm realm = (Realm)resource.getContents().get(0);
    realm.removeUser(CREDENTIALS.getUserID());

    transaction.commit();

    session.close();
    session = openSession(admin);
    transaction = session.openTransaction();
    resource = transaction.getResource(REALM_PATH);
    realm = (Realm)resource.getContents().get(0);

    User user = realm.getUser(CREDENTIALS.getUserID());
    assertEquals(null, user);
  }

  private ISecurityManager getSecurityManager()
  {
    InternalRepository repository = getRepository();
    return SecurityManagerUtil.getSecurityManager(repository);
  }

  private boolean isWritable(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDORevision revision = cdoObject.cdoRevision(true);
    return revision.isWritable();
  }
}
