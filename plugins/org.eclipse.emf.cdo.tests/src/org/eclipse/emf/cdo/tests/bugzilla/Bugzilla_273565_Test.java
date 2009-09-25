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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import junit.framework.Assert;

/**
 * Concurrency problem: attribute of enumeration type not updated correctly between two clients
 * <p>
 * See https://bugs.eclipse.org/273565
 * 
 * @author Simon McDuff
 */
public class Bugzilla_273565_Test extends AbstractCDOTest
{
  /**
   * Thread 1 : Update the value at 1 only when the value is at 2.<br>
   * Thread 2 : Update the value at 3 and 2 only when the value is at 1.<br>
   * Thread 1 will load objects... but at the same time will update remote changes... causing to not have the latest
   * version.
   */
  public void testBugzilla_273565() throws Exception
  {
    // TODO Clarify why this test sometimes enters infinite loop with this trace:
    // TCPSelector [TCPSelector] Writing java.nio.channels.SocketChannel[connected local=/127.0.0.1:2036
    // remote=/127.0.0.1:59580]
    skipConfig(TCP);

    final OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    final boolean done[] = new boolean[1];
    final Exception exception[] = new Exception[1];
    done[0] = false;
    orderDetail.setPrice(2);
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(orderDetail);
    transaction.commit();

    Runnable changeObjects = new Runnable()
    {
      public void run()
      {
        try
        {
          CDOSession session = openModel1Session();
          CDOTransaction transaction = session.openTransaction();
          OrderDetail orderDetail2 = (OrderDetail)transaction.getObject(CDOUtil.getCDOObject(orderDetail).cdoID());

          while (!done[0])
          {
            int counter = 0;
            while (orderDetail2.getPrice() != 1 && !done[0])
            {
              if (counter++ >= 20)
              {
                throw new IllegalStateException("Object should have changed");
              }

              Thread.sleep(100);
            }

            orderDetail2.setPrice(3);
            transaction.commit();
            orderDetail2.setPrice(2);
            transaction.commit();
          }

          transaction.close();
          session.close();
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    };

    new Thread(changeObjects).start();

    for (int i = 0; i < 50 && exception[0] == null; i++)
    {
      orderDetail.setPrice(1);
      transaction.commit();

      int counter = 0;
      while (orderDetail.getPrice() != 2)
      {
        if (counter++ >= 20)
        {
          throw new IllegalStateException("Object should have changed");
        }

        Thread.sleep(100);
      }
    }

    done[0] = true;
    if (exception[0] != null)
    {
      exception[0].printStackTrace();
      Assert.fail(exception[0].getMessage());
    }

    session.close();
  }

  public void _testBugzilla_273565_List() throws Exception
  {
    final OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    final Order order = getModel1Factory().createOrder();
    final boolean done[] = new boolean[1];
    final Exception exception[] = new Exception[1];
    done[0] = false;
    order.getOrderDetails().add(orderDetail);
    orderDetail.setPrice(2);
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(order);
    transaction.commit();

    Runnable changeObjects = new Runnable()
    {
      public void run()
      {
        try
        {
          CDOSession session = openModel1Session();
          CDOTransaction transaction = session.openTransaction();
          OrderDetail orderDetail2 = (OrderDetail)transaction.getObject(CDOUtil.getCDOObject(orderDetail).cdoID());

          while (!done[0])
          {
            int counter = 0;
            while (orderDetail2.getPrice() != 1 && !done[0])
            {
              if (counter++ >= 100)
              {
                throw new IllegalStateException("Object should have changed");
              }

              Thread.sleep(100);
            }

            transaction.getLock().lock();
            transaction.getLock().unlock();

            orderDetail2.setPrice(3);

            transaction.commit();
            orderDetail2.getOrder().getOrderDetails().remove(1);
            orderDetail2.setPrice(2);
            transaction.commit();
          }

          transaction.close();
          session.close();
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    };

    new Thread(changeObjects).start();

    for (int i = 0; i < 50 && exception[0] == null; i++)
    {
      orderDetail.setPrice(1);
      CDOUtil.getCDOObject(orderDetail.getOrder()).cdoWriteLock().lock();
      orderDetail.getOrder().getOrderDetails().add(getModel1Factory().createOrderDetail());
      transaction.commit();

      int counter = 0;
      while (orderDetail.getPrice() != 2)
      {
        if (counter++ >= 100)
        {
          throw new IllegalStateException("Object should have changed");
        }

        Thread.sleep(100);
      }

      transaction.getLock().lock();
      transaction.getLock().unlock();
    }

    done[0] = true;
    if (exception[0] != null)
    {
      exception[0].printStackTrace();
      Assert.fail(exception[0].getMessage());
    }

    session.close();
  }

  public void testBugzilla_273565_Lock() throws Exception
  {
    // TODO Clarify why this test sometimes enters infinite loop with this trace:
    // TCPSelector [TCPSelector] Writing java.nio.channels.SocketChannel[connected local=/127.0.0.1:2036
    // remote=/127.0.0.1:59580]
    // Update Could be related to bug 289584: Deadlock in CDOView that is now fixed!

    skipConfig(TCP);
    final OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    final boolean done[] = new boolean[1];
    final Exception exception[] = new Exception[1];
    done[0] = false;
    orderDetail.setPrice(2);
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(orderDetail);
    transaction.commit();

    Runnable changeObjects = new Runnable()
    {
      public void run()
      {
        try
        {
          CDOSession session = openModel1Session();
          CDOTransaction transaction = session.openTransaction();
          OrderDetail orderDetail2 = (OrderDetail)transaction.getObject(CDOUtil.getCDOObject(orderDetail).cdoID());

          while (!done[0])
          {
            CDOUtil.getCDOObject(orderDetail2).cdoWriteLock().lock();
            orderDetail2.setPrice(3);
            transaction.commit();
          }

          transaction.close();
          session.close();
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    };

    new Thread(changeObjects).start();

    for (int i = 0; i < 50 && exception[0] == null; i++)
    {
      CDOUtil.getCDOObject(orderDetail).cdoWriteLock().lock();
      orderDetail.setPrice(1);
      transaction.commit();
    }

    done[0] = true;
    if (exception[0] != null)
    {
      exception[0].printStackTrace();
      Assert.fail(exception[0].getMessage());
    }

    session.close();
  }
}
