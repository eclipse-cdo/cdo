/*
 * Copyright (c) 2012, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.concurrent.CountDownLatch;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_340709_Test extends AbstractCDOTest
{
  private transient CountDownLatch latch = new CountDownLatch(1);

  public void test() throws Exception
  {
    CDOSession session = openSession();

    {
      CDOTransaction transaction = session.openTransaction();
      addTransactionListener(transaction);

      CDOResource resource = transaction.createResource(getResourcePath("test"));

      Category category = getModel1Factory().createCategory();
      category.setName("name1");

      resource.getContents().add(category);
      transaction.commit();
    }

    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("test"));

      Category category = (Category)resource.getContents().get(0);
      category.setName("name2");

      transaction.commit();
    }
  }

  private void addTransactionListener(final CDOTransaction transaction)
  {
    transaction.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        System.err.println(event);
        if (event instanceof CDOViewInvalidationEvent)
        {
          handleNotify(transaction);

          await(latch);
        }
      }

      private void handleNotify(final CDOTransaction transaction)
      {
        Thread handleNotifyThread = new Thread(new Runnable()
        {
          @Override
          public void run()
          {
            msg("DEAD");
            transaction.getRootResource();

            msg("LOCK");
            latch.countDown();
          }
        });

        handleNotifyThread.start();
      }
    });
  }
}
