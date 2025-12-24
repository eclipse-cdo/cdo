/*
 * Copyright (c) 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.UserManager;

/**
 * Bug 427773 - [Security] BadPaddingException on opening an authenticated CDOSession.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_427773_Test extends AbstractCDOTest
{
  private static final int NUMBER_OF_THREADS = 50;

  private static final IPasswordCredentials USER_1_CREDENTIALS = new PasswordCredentials("user1", "12345");

  @Override
  public void setUp() throws Exception
  {
    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_1_CREDENTIALS.getUserID(), USER_1_CREDENTIALS.getPassword());
    getTestProperties().put(RepositoryConfig.PROP_TEST_AUTHENTICATOR, userManager);

    super.setUp();
  }

  @CleanRepositoriesAfter(reason = "Repository not used again")
  public void testParallelAuthenticatedSessionOpening() throws Exception
  {
    getRepository();

    CDOSessionOpener[] sessionOpeners = new CDOSessionOpener[NUMBER_OF_THREADS];
    Thread[] sessionOpenerThreads = new Thread[NUMBER_OF_THREADS];

    for (int i = 0; i < NUMBER_OF_THREADS; i++)
    {
      sessionOpeners[i] = new CDOSessionOpener();
    }

    for (int i = 0; i < NUMBER_OF_THREADS; i++)
    {
      msg("Session " + i);
      Thread sessionOpenerThread = new Thread(sessionOpeners[i]);
      sessionOpenerThread.start();
      sessionOpenerThreads[i] = sessionOpenerThread;
    }

    for (int i = 0; i < NUMBER_OF_THREADS; i++)
    {
      sessionOpenerThreads[i].join(DEFAULT_TIMEOUT);
      sessionOpeners[i].close();

      Exception exception = sessionOpeners[i].getException();
      if (exception != null)
      {
        throw exception;
      }
    }
  }

  /**
   * @author Esteban Dugueperoux
   */
  private final class CDOSessionOpener implements Runnable
  {
    private CDOSession session;

    private Exception exception;

    public CDOSessionOpener()
    {
    }

    public Exception getException()
    {
      return exception;
    }

    @Override
    public void run()
    {
      try
      {
        session = openSession(USER_1_CREDENTIALS);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        exception = ex;
      }
    }

    public void close()
    {
      if (session != null)
      {
        session.close();
      }
    }
  }
}
