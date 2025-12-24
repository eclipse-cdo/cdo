/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Laurent Redor (Obeo) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.SecurityFactory;
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
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.security.PasswordCredentials;

/**
 * Bug 501607: [Security] "Security realm integrity violation: An object may not circularly
 * contain itself" thrown when modifying several elements and Realm in first position
 *
 * @author Laurent Redor (Obeo)
 */
@CleanRepositoriesBefore(reason = "Security manager installed on repository")
@CleanRepositoriesAfter(reason = "Security manager installed on repository")
public class Bugzilla_501607_Test extends AbstractCDOTest
{
  /**
   * Ensure that there is no CommitException thrown during the modification.
   *
   * @throws Exception
   *             In case of problem
   */
  public void testCommitChangesWhenModifyingRealmFirst() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Firstly, modify the Realm
    Realm realm = getRealm(transaction);
    User createdUser = SecurityFactory.eINSTANCE.createUser();
    createdUser.setId("lredor");
    realm.getItems().add(createdUser);
    // Then make another modification on element contained in Realm
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
