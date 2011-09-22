/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

/**
 * See bug 341995.
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_341995_Test extends AbstractCDOTest
{
  public void test() throws CommitException, InterruptedException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    Model1Factory factory = getModel1Factory();
    Category cat = factory.createCategory();
    resource.getContents().add(cat);
    tx.commit();

    CDOObject cdoCat = CDOUtil.getCDOObject(cat);
    msg(cdoCat.cdoRevision().getVersion());

    long delay = 2000L;
    TestSessionManager sessionManager = (TestSessionManager)getRepository().getSessionManager();
    sessionManager.setCommitNotificationDelay(delay);
    try
    {
      doSecondSessionAsync();
      sessionManager.getDelayLatch().await(); // Wait until the delay commences

      long time1 = System.currentTimeMillis();

      // Attempt the lock; this must block for a while, because it needs to receive
      // the commitNotification from the commit in the other session, which we are
      // artificially delaying
      cdoCat.cdoWriteLock().lock();

      long timeTaken = System.currentTimeMillis() - time1;

      // We verify that there really was a delay
      assertEquals("timeTaken == " + timeTaken, true, timeTaken >= delay);

      tx.close();
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
      public void run()
      {
        CDOSession session = openSession();
        CDOTransaction tx = session.openTransaction();
        CDOResource resource = tx.getResource(getResourcePath("test"));

        Category cat = (Category)resource.getContents().get(0);
        cat.setName("dirty");
        CDOCommitInfo info;
        try
        {
          info = tx.commit();
        }
        catch (CommitException ex)
        {
          throw new RuntimeException(ex);
        }

        msg(info.getTimeStamp());

        tx.close();
        session.close();
      }
    };
    new Thread(r).start();
  }
}
