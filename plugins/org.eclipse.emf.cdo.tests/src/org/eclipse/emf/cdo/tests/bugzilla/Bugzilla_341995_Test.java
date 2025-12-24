/*
 * Copyright (c) 2011-2013, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.util.TestSessionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.concurrent.TimeUnit;

/**
 * Bug 341995:
 *
 * @author Caspar De Groot
 */
public class Bugzilla_341995_Test extends AbstractCDOTest
{
  public void test() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test"));

    Model1Factory factory = getModel1Factory();
    Category category = factory.createCategory();
    resource.getContents().add(category);
    transaction.commit();

    CDOObject cdoCategory = CDOUtil.getCDOObject(category);
    msg(cdoCategory.cdoRevision().getVersion());

    long delay = 2000L;

    TestSessionManager sessionManager = (TestSessionManager)getRepository().getSessionManager();
    sessionManager.setCommitNotificationDelay(delay);

    try
    {
      doSecondSessionAsync();
      await(sessionManager.getDelayLatch()); // Wait until the delay commences

      long time1 = System.currentTimeMillis();

      // Attempt the lock; this must block for a while, because it needs to receive
      // the commitNotification from the commit in the other session, which we are
      // artificially delaying
      cdoCategory.cdoWriteLock().lock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

      long timeTaken = System.currentTimeMillis() - time1;

      // We verify that there really was a delay
      assertEquals("timeTaken == " + timeTaken, true, timeTaken >= delay - 10);

      transaction.close();
      session.close();
    }
    finally
    {
      sessionManager.setCommitNotificationDelay(0L);
    }
  }

  private void doSecondSessionAsync() throws CommitException
  {
    Runnable r = new Runnable()
    {
      @Override
      public void run()
      {
        CDOSession session = openSession();
        CDOTransaction transaction = session.openTransaction();
        CDOResource resource = transaction.getResource(getResourcePath("test"));

        Category category = (Category)resource.getContents().get(0);
        category.setName("dirty");

        CDOCommitInfo info;

        try
        {
          info = transaction.commit();
        }
        catch (CommitException ex)
        {
          throw new RuntimeException(ex);
        }

        msg(info.getTimeStamp());

        transaction.close();
        session.close();
      }
    };

    Thread thread = new Thread(r);
    thread.setDaemon(true);
    thread.start();
  }
}
