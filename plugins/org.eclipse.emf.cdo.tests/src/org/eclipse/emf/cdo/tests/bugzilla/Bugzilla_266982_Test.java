/*
 * Copyright (c) 2009-2012, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * IllegalStateException in CDOStore.getRevision
 * <p>
 * See bug 266982
 *
 * @author Simon McDuff
 */
public class Bugzilla_266982_Test extends AbstractCDOTest
{
  public void testBugzilla_266982() throws Exception
  {
    final Customer customer = getModel1Factory().createCustomer();
    AtomicBoolean done = new AtomicBoolean();
    AtomicReference<Throwable> exception = new AtomicReference<>();
    CountDownLatch workerReady = new CountDownLatch(1);
    customer.setName("customer");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(customer);
    transaction.commit();

    Runnable changeObjects = new Runnable()
    {
      @Override
      public void run()
      {
        CDOSession workerSession = null;
        CDOTransaction workerTransaction = null;
        Throwable workerFailure = null;

        try
        {
          workerSession = openSession();
          workerTransaction = workerSession.openTransaction();
          Customer customerToLoad = (Customer)CDOUtil.getEObject(workerTransaction.getObject(CDOUtil.getCDOObject(customer).cdoID()));
          customerToLoad.getName();
          workerReady.countDown();

          while (!done.get())
          {
            // Could fail if the attach is not thread safe
            customerToLoad.getName();
          }

        }
        catch (Throwable ex)
        {
          workerFailure = ex;
          workerReady.countDown();
        }
        finally
        {
          try
          {
            if (workerTransaction != null)
            {
              workerTransaction.close();
            }
          }
          catch (Throwable cleanupFailure)
          {
            if (workerFailure == null)
            {
              workerFailure = cleanupFailure;
            }
            else
            {
              workerFailure.addSuppressed(cleanupFailure);
            }
          }

          try
          {
            if (workerSession != null)
            {
              workerSession.close();
            }
          }
          catch (Throwable cleanupFailure)
          {
            if (workerFailure == null)
            {
              workerFailure = cleanupFailure;
            }
            else
            {
              workerFailure.addSuppressed(cleanupFailure);
            }
          }

          if (workerFailure != null)
          {
            exception.set(workerFailure);
          }
        }
      }
    };

    Thread worker = new Thread(changeObjects, "Bugzilla 266982 reader"); //$NON-NLS-1$
    worker.start();

    Throwable failure = null;

    try
    {
      assertTrue("Reader thread did not become ready", workerReady.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$

      for (int i = 0; i < 500 && exception.get() == null; i++)
      {
        customer.setName("Ottawa" + i);
        transaction.commit();
      }
    }
    catch (Throwable ex)
    {
      failure = ex;
    }
    finally
    {
      done.set(true);

      try
      {
        worker.join(DEFAULT_TIMEOUT);

        if (worker.isAlive())
        {
          worker.interrupt();
          worker.join(DEFAULT_TIMEOUT);
        }

        assertFalse("Reader thread did not terminate", worker.isAlive()); //$NON-NLS-1$
      }
      catch (Throwable cleanupFailure)
      {
        if (failure == null)
        {
          failure = cleanupFailure;
        }
        else
        {
          failure.addSuppressed(cleanupFailure);
        }
      }
    }

    Throwable workerFailure = exception.get();
    if (workerFailure != null)
    {
      if (failure == null)
      {
        failure = workerFailure;
      }
      else
      {
        failure.addSuppressed(workerFailure);
      }
    }

    try
    {
      session.close();
    }
    catch (Throwable cleanupFailure)
    {
      if (failure == null)
      {
        failure = cleanupFailure;
      }
      else
      {
        failure.addSuppressed(cleanupFailure);
      }
    }

    if (failure instanceof Exception)
    {
      throw (Exception)failure;
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }
  }
}
