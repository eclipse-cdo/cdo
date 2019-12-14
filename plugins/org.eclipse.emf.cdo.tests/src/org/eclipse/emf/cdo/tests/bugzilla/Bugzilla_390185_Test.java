/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitConflictException;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Bug 390185: Deadlock on multiple concurrent transactions.
 *
 * @author Eike Stepper
 */
public class Bugzilla_390185_Test extends AbstractCDOTest
{
  private static final int THREADS = 4;

  private static final int TRANSACTIONS_PER_THREAD = 40;

  public void testInvalidationDeadlock() throws Exception
  {
    CDOSession session = openSession();

    CountDownLatch latch = new CountDownLatch(THREADS);
    List<Actor> actors = new ArrayList<>(THREADS);
    for (int i = 0; i < THREADS; i++)
    {
      Actor actor = new Actor(session, latch, i);
      actors.add(actor);
    }

    await(latch);
  }

  /**
   * @author Eike Stepper
   */
  private class Actor extends Thread
  {
    private CDOSession session;

    private CountDownLatch latch;

    private int nr;

    public Actor(CDOSession session, CountDownLatch latch, int nr)
    {
      super("Actor-" + nr);
      this.session = session;
      this.latch = latch;
      this.nr = nr;
      start();
    }

    @Override
    public void run()
    {
      for (int i = 0; i < TRANSACTIONS_PER_THREAD; i++)
      {
        CDOTransaction transaction = session.openTransaction();
        attemptCommit(transaction);
        transaction.close();
        ConcurrencyUtil.sleep(2);
      }

      latch.countDown();
    }

    private void attemptCommit(final CDOTransaction transaction)
    {
      for (;;)
      {
        final Category category = getModel1Factory().createCategory();
        category.setName("category-" + System.currentTimeMillis());

        try
        {
          transaction.syncExec(new Callable<Object>()
          {
            @Override
            public Object call() throws Exception
            {
              CDOResource res = transaction.getOrCreateResource(getResourcePath("/res-" + nr));
              res.getContents().add(category);
              transaction.commit();

              return null;
            }
          });

          break;
        }
        catch (CommitConflictException ex)
        {
          transaction.rollback();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          break;
        }
      }

      transaction.close();
    }
  }
}
