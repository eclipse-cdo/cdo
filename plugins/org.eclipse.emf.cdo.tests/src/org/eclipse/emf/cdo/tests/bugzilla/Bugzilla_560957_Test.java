/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.WrappedException;

import java.util.concurrent.CountDownLatch;

/**
 * Bug 560957 - Deadlock when using CDOMergingConflictResolver.
 *
 * @author Eike Stepper
 */
public class Bugzilla_560957_Test extends AbstractCDOTest
{
  public void testDeadlockOnViewGetBranch() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.createResource(getResourcePath("resource" + transaction1.getViewID()));
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.createResource(getResourcePath("resource" + transaction2.getViewID()));
    transaction2.commit();

    CountDownLatch allThreadsCalledCommit = new CountDownLatch(2);
    CountDownLatch allThreadsAccessedOtherBranchPoint = new CountDownLatch(2);

    class Committer extends Thread
    {
      private final CDOTransaction transaction;

      private final CDOTransaction otherTransaction;

      public Committer(CDOTransaction transaction, CDOTransaction otherTransaction)
      {
        super("COMMITTER-" + transaction.getViewID());
        setDaemon(true);

        this.transaction = transaction;
        this.otherTransaction = otherTransaction;

        session.addListener(event -> {
          if (event instanceof CDOSessionInvalidationEvent)
          {
            sessionInvalidated();
          }
        });
      }

      @Override
      public void run()
      {
        // Make transaction dirty.
        CDOResource resource = transaction.getResource(getResourcePath("resource" + transaction.getViewID()));
        resource.getContents().add(getModel1Factory().createCompany());

        try
        {
          transaction.commit();
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }

        allThreadsAccessedOtherBranchPoint.countDown();
      }

      private void sessionInvalidated()
      {
        allThreadsCalledCommit.countDown();
        await(allThreadsCalledCommit);

        // Call the methods on the other transaction that can lead to deadlock.
        otherTransaction.getBranch();
        otherTransaction.getTimeStamp();
        otherTransaction.isHistorical();
      }
    }

    new Committer(transaction1, transaction2).start();
    new Committer(transaction2, transaction1).start();
    await(allThreadsAccessedOtherBranchPoint);
  }
}
