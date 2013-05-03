/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.util.CommitException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Bug 390185.
 *
 * @author Eike Stepper
 */
public class Bugzilla_390185_Test extends AbstractCDOTest
{
  private static final int THREADS = 5;

  private static final int TRANSACTIONS_PER_THREAD = 100;

  public void testIvalidationDeadlock() throws Exception
  {
    CDOSession session = openSession();

    CountDownLatch latch = new CountDownLatch(THREADS);
    List<Actor> actors = new ArrayList<Actor>(THREADS);
    for (int i = 0; i < THREADS; i++)
    {
      Actor actor = new Actor(session, latch, i);
      actors.add(actor);
    }

    if (!latch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS))
    {
      throw new TimeoutException();
    }
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
      }

      latch.countDown();
    }

    private void attemptCommit(CDOTransaction transaction)
    {
      for (;;)
      {
        Category category = getModel1Factory().createCategory();
        category.setName("category-" + System.currentTimeMillis());

        try
        {
          synchronized (transaction)
          {
            CDOResource res = transaction.getOrCreateResource(getResourcePath("/res-" + nr));
            res.getContents().add(category);
            transaction.commit();
            break;
          }
        }
        catch (CommitException ex)
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
