/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.transaction.TransactionException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Concurrency problem: attribute of enumeration type not updated correctly between two clients
 * <p>
 * 
 * @author Simon McDuff
 * @see bug 273565
 */
public class Bugzilla_273565_Test extends AbstractCDOTest
{
  /**
   * Thread A: Update the value to 3 and 2 only if the value is at 1.<br>
   * Thread B: Update the value to 1 only if the value is at 2.
   * <p>
   * Thread B will load objects.<br>
   * But at the same time will update remote changes.<br>
   * Causing not to have the latest version.
   * 
   * @see bug 273565
   */
  public void testBugzilla_273565() throws Exception
  {
    final CountDownLatch start = new CountDownLatch(1);
    final boolean[] done = { false };
    final Exception exception[] = { null };

    final OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(2);

    final CDOSession session = openModel1Session();
    final CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(orderDetail);
    transaction.commit();

    final CDOID id = CDOUtil.getCDOObject(orderDetail).cdoID();
    Thread threadA = new Thread()
    {
      @Override
      public void run()
      {
        CDOSession session = openModel1Session();
        CDOTransaction transaction = session.openTransaction();
        OrderDetail orderDetail = (OrderDetail)transaction.getObject(id);

        try
        {
          start.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
          while (!done[0])
          {
            while (orderDetail.getPrice() != 1 && !done[0])
            {
              sleep(1);
            }

            try
            {
              orderDetail.setPrice(3);
              transaction.commit();
            }
            catch (TransactionException ex)
            {
              transaction.rollback();
              continue;
            }

            try
            {
              orderDetail.setPrice(2);
              transaction.commit();
            }
            catch (TransactionException ex)
            {
              transaction.rollback();
              continue;
            }
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          exception[0] = ex;
        }
        finally
        {
          session.close();
        }
      }
    };

    threadA.setDaemon(true);
    threadA.start();

    Thread threadB = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          start.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
          for (int i = 0; i < 50 && exception[0] == null; i++)
          {
            try
            {
              orderDetail.setPrice(1);
              transaction.commit();
            }
            catch (TransactionException ex)
            {
              transaction.rollback();
              continue;
            }

            while (orderDetail.getPrice() != 2)
            {
              sleep(1);
            }
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          exception[0] = ex;
        }

        done[0] = true;
      }
    };

    threadB.setDaemon(true);
    threadB.start();

    start.countDown();
    threadA.join(DEFAULT_TIMEOUT);
    threadB.join(DEFAULT_TIMEOUT);

    if (exception[0] != null)
    {
      throw exception[0];
    }
  }

  /**
   * @see bug 273565
   */
  public void testBugzilla_273565_Lock() throws Exception
  {
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(2);

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(orderDetail);
    transaction.commit();

    final CDOID id = CDOUtil.getCDOObject(orderDetail).cdoID();
    final CountDownLatch start = new CountDownLatch(1);
    final Exception exception[] = { null };

    class Modifier extends Thread
    {
      private float price;

      public Modifier(float price)
      {
        setDaemon(true);
        this.price = price;
      }

      @Override
      public void run()
      {
        CDOSession session = openModel1Session();
        CDOTransaction transaction = session.openTransaction();
        OrderDetail orderDetail = (OrderDetail)transaction.getObject(id);

        try
        {
          start.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
          for (int i = 0; i < 50 && exception[0] == null; i++)
          {
            CDOUtil.getCDOObject(orderDetail).cdoWriteLock().lock();
            orderDetail.setPrice(price);
            transaction.commit();
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          exception[0] = ex;
        }
        finally
        {
          session.close();
        }
      }
    }

    Modifier threadA = new Modifier(3);
    threadA.start();

    Modifier threadB = new Modifier(1);
    threadB.start();

    start.countDown();
    threadA.join(DEFAULT_TIMEOUT);
    threadB.join(DEFAULT_TIMEOUT);

    if (exception[0] != null)
    {
      throw exception[0];
    }
  }

  /**
   * @see bug 273565
   */
  public void testBugzilla_273565_NoThreads() throws Exception
  {
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(1);

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(orderDetail);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    OrderDetail orderDetail2 = transaction2.getObject(orderDetail);

    CDOUtil.getCDOObject(orderDetail).cdoWriteLock().lock();
    orderDetail.setPrice(2);

    boolean locked = CDOUtil.getCDOObject(orderDetail2).cdoWriteLock().tryLock(DEFAULT_TIMEOUT_EXPECTED,
        TimeUnit.MILLISECONDS);
    assertEquals(false, locked);
  }
}
