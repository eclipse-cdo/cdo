/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;

import java.util.concurrent.CountDownLatch;

/**
 * Bug 560957 - Deadlock when using CDOMergingConflictResolver.
 *
 * @author Eike Stepper
 */
public class Bugzilla_560957_Test extends AbstractCDOTest
{
  public void _testDeadlockOnViewGetBranch10000() throws Exception
  {
    for (int i = 0; i < 10000; i++)
    {
      System.out.println("Run " + i);
      run(i);
    }
  }

  public void testDeadlockOnViewGetBranch() throws Exception
  {
    run(1);
  }

  private void run(int run) throws ConcurrentAccessException, CommitException
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    transaction1.createResource(getResourcePath("resource-" + run + "-" + transaction1.getViewID()));
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    transaction2.createResource(getResourcePath("resource-" + run + "-" + transaction2.getViewID()));
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
        CDOResource resource = transaction.getResource(getResourcePath("resource-" + run + "-" + transaction.getViewID()));
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
        if (getName().equals(currentThread().getName()))
        {
          allThreadsCalledCommit.countDown();

          try
          {
            await(allThreadsCalledCommit, 1000);
          }
          catch (TimeoutRuntimeException ex)
          {
            // The await() call in the above try-block is not normal client code.
            // It rather tries to produce a very specific timing that's needed for the test logic.
            // It also creates an unusual coupling between the two committing clients that can cause
            // an unusual race condition in SessionInvalidator.scheduleInvalidations().
            // As this is not a situation that can occur in real clients and not the one we want to
            // test here we'll just ignore it and go on...
          }

          // Call the methods on the other transaction that can lead to deadlock.
          otherTransaction.getBranch();
          otherTransaction.getTimeStamp();
          otherTransaction.isHistorical();
        }
      }
    }

    new Committer(transaction1, transaction2).start();
    new Committer(transaction2, transaction1).start();
    await(allThreadsAccessedOtherBranchPoint);
    session.close();
  }
}
