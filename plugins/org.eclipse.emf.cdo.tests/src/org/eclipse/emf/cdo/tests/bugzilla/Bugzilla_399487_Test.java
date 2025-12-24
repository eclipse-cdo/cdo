/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ValidationException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentials;

import java.util.Iterator;

/**
 * Bug 399487: [Security] Changes to the security realm should be verified before being applied
 *
 * @author Christian W. Damus (CEA LIST)
 */
@CleanRepositoriesBefore(reason = "Security manager installed on repository")
@CleanRepositoriesAfter(reason = "Security manager installed on repository")
public class Bugzilla_399487_Test extends AbstractCDOTest
{
  public void testCommitSafeChanges() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Realm realm = getRealm(transaction);
    realm.getGroup("Users").getUsers().add(realm.addUser("cdamus", "12345678"));

    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      fail("Commit rolled back: " + ex.getLocalizedMessage());
    }
  }

  public void testRemoveAdministratorAccess() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Realm realm = getRealm(transaction);
    Role admin = realm.getRole("Administration");
    for (Iterator<Permission> permissions = admin.getPermissions().iterator(); permissions.hasNext();)
    {
      if (permissions.next().getAccess() == Access.WRITE)
      {
        permissions.remove();
      }
    }

    try
    {
      transaction.commit();
      fail("Should have thrown ValidationException");
    }
    catch (ValidationException ex)
    {
      // Success
    }
    catch (CommitException ex)
    {
      fail("Commit rolled back for wrong reason: " + ex.getLocalizedMessage());
    }
  }

  public void testGroupInheritanceCycle() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Realm realm = getRealm(transaction);
    Group admins = realm.getGroup("Administrators");
    Group users = realm.getGroup("Users");
    admins.getInheritedGroups().add(users);
    users.getInheritedGroups().add(admins);

    try
    {
      transaction.commit();
      fail("Should have thrown ValidationException");
    }
    catch (ValidationException ex)
    {
      // Success
    }
    catch (CommitException ex)
    {
      fail("Commit rolled back for wrong reason: " + ex.getLocalizedMessage());
    }
  }

  @Override
  public void setUp() throws Exception
  {
    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new IPasswordCredentialsProvider()
    {

      @Override
      public boolean isInteractive()
      {
        return false;
      }

      @Override
      public IPasswordCredentials getCredentials()
      {
        return new PasswordCredentials(User.ADMINISTRATOR, "0000");
      }
    });

    super.doSetUp();

    // Create the security manager and attach it to the repository
    ISecurityManager securityManager = SecurityManagerUtil.createSecurityManager("/security", getServerContainer());
    getTestProperties().put(RepositoryConfig.PROP_TEST_SECURITY_MANAGER, securityManager);
    getRepository();
    LifecycleUtil.waitForActive(securityManager, 10000L);
  }

  Realm getRealm(CDOView view)
  {
    CDOResource resource = view.getResource("/security");
    return (Realm)resource.getContents().get(0);
  }
}
