/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.UserManager;

/**
 * @author Eike Stepper
 */
public class SessionTest extends AbstractCDOTest
{
  private static final String USER_ID = "stepper"; //$NON-NLS-1$

  private static final char[] PASSWORD1 = "eike2007".toCharArray(); //$NON-NLS-1$

  private static final char[] PASSWORD2 = "invalid".toCharArray(); //$NON-NLS-1$

  public void testIsSupportingAudits() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(getRepository().isSupportingAudits(), session.repository().isSupportingAudits());
    session.close();
  }

  public void testNoAuthentication() throws Exception
  {
    IRepository repository = getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD1)));

    CDOSession session = openSession("authrepo");
    assertEquals(null, session.getUserID());
    assertEquals(null, repository.getSessionManager().getSessions()[0].getUserID());
    session.close();
  }

  public void testWithAuthentication() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    IRepository repository = getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD1)));

    CDOSession session = openSession("authrepo");
    assertEquals(USER_ID, session.getUserID());
    assertEquals(USER_ID, repository.getSessionManager().getSessions()[0].getUserID());
    session.close();
  }

  public void testWithAuthenticationNoCredentialsProvider() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationNoCredentials() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER, new PasswordCredentialsProvider(null));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationWrongCredentials() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, PASSWORD2)));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationNoUserID() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(null, PASSWORD2)));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }

  public void testWithAuthenticationNoPassword() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD1);

    getTestProperties().put(RepositoryConfig.PROP_TEST_USER_MANAGER, userManager);
    getRepository("authrepo");

    getTestProperties().put(SessionConfig.PROP_TEST_CREDENTIALS_PROVIDER,
        new PasswordCredentialsProvider(new PasswordCredentials(USER_ID, null)));

    try
    {
      openSession("authrepo");
      fail("RemoteException expected");
    }
    catch (RemoteException success)
    {
      assertEquals(SecurityException.class, success.getCause().getClass());
    }
  }
}
