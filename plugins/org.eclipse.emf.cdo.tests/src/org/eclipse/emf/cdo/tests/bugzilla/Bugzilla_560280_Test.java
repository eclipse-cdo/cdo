/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
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

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bug 560280 - Possible deadlock during the session invalidation.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "Security manager installed on repository")
@CleanRepositoriesAfter(reason = "Security manager installed on repository")
public class Bugzilla_560280_Test extends AbstractCDOTest
{
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

  public void testDeadlockBetweenInvalidationAndCommit() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    Realm realm1 = getRealm(transaction1);
    Group group1 = realm1.getGroup("Users");

    AtomicReference<CDOTransaction> transactionUnderTest = new AtomicReference<>();
    AtomicBoolean controlUpdatePermissions = new AtomicBoolean();
    CountDownLatch reachedUpdatePermissions = new CountDownLatch(1);
    CountDownLatch allowUpdatePermissions = new CountDownLatch(1);

    CDONet4jSessionConfiguration configuration = new CDONet4jSessionConfigurationImpl()
    {
      @Override
      public InternalCDOSession createSession()
      {
        return new CDONet4jSessionImpl()
        {
          @Override
          protected Map<CDORevision, CDOPermission> updatePermissions(CDOCommitInfo commitInfo, InternalCDOView[] views)
          {
            if (controlUpdatePermissions.get())
            {
              reachedUpdatePermissions.countDown();
              await(allowUpdatePermissions);
            }

            return super.updatePermissions(commitInfo, views);
          }
        };
      }
    };

    CDONet4jSessionConfiguration template = (CDONet4jSessionConfiguration)((SessionConfig)getSessionConfig())
        .createSessionConfiguration(IRepositoryConfig.REPOSITORY_NAME);
    configuration.setConnector(template.getConnector());
    configuration.setRepositoryName(template.getRepositoryName());
    configuration.setRevisionManager(template.getRevisionManager());

    getTestProperties().put(SessionConfig.PROP_TEST_SESSION_CONFIGURATION, configuration);

    CDOSession sessionUnderTest = openSession();
    transactionUnderTest.set(sessionUnderTest.openTransaction());

    /*
     * Test Logic:
     */

    controlUpdatePermissions.set(true);

    group1.setId("ModernUsers");
    transaction1.commit();

    // Execute transactionUndertTest.commit() on a separate thread so that the deadlock doesn't freeze the test suite.
    CountDownLatch commitFinished = new CountDownLatch(1);
    new Thread("CommitterUnderTest")
    {
      @Override
      public void run()
      {
        await(reachedUpdatePermissions);

        CDOTransaction tx = transactionUnderTest.get();
        tx.syncExec(() -> {
          allowUpdatePermissions.countDown();

          try
          {
            Realm realmUnderTest = getRealm(tx);
            realmUnderTest.addUser("UserUnderTest", "abc");

            tx.commit();
            commitFinished.countDown();
          }
          catch (CommitException ex)
          {
            // This is really not expected now.
            ex.printStackTrace();
          }
        });
      }
    }.start();

    await(commitFinished);
  }

  Realm getRealm(CDOView view)
  {
    CDOResource resource = view.getResource("/security");
    return (Realm)resource.getContents().get(0);
  }
}
