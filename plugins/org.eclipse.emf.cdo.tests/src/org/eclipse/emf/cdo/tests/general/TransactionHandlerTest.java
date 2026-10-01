/*
 * Copyright (c) 2008-2012, 2016, 2019, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOAsyncTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.util.CDOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Simon McDuff
 */
public class TransactionHandlerTest extends AbstractCDOTest
{
  private static final String VETO_MESSAGE = "Simulated veto";

  public TransactionHandlerTest()
  {
  }

  public void testAttachingObject() throws Exception
  {
    CDOAccumulateTransactionHandler handler = new CDOAccumulateTransactionHandler();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(handler);

    CDOResource resource1 = transaction.getOrCreateResource(getResourcePath("/test1"));

    assertEquals(true, handler.getListOfAddingObject().contains(resource1));

    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    resource1.getContents().add(order);
    assertEquals(true, handler.getListOfAddingObject().contains(CDOUtil.getCDOObject(order)));
    assertEquals(true, handler.getListOfAddingObject().contains(CDOUtil.getCDOObject(orderDetail)));

    transaction.close();
    session.close();
  }

  public void testAttachingResourceVeto() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void attachingObject(CDOTransaction transaction, CDOObject object)
      {
        veto();
      }
    });

    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    try
    {
      transaction.getOrCreateResource(getResourcePath("/test1"));
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException ex)
    {
      assertEquals(VETO_MESSAGE, ex.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public void testAttachingObjectVeto() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void attachingObject(CDOTransaction transaction, CDOObject object)
      {
        veto();
      }
    });

    try
    {
      resource.getContents().add(order);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException ex)
    {
      assertEquals(VETO_MESSAGE, ex.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public void testDetachingObject() throws Exception
  {
    CDOAccumulateTransactionHandler handler = new CDOAccumulateTransactionHandler();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(handler);

    CDOResource resource1 = transaction.getOrCreateResource(getResourcePath("/test1"));
    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    resource1.getContents().add(order);
    assertEquals(true, handler.getListOfAddingObject().contains(CDOUtil.getCDOObject(order)));
    assertEquals(true, handler.getListOfAddingObject().contains(CDOUtil.getCDOObject(orderDetail)));

    msg("Remove Object");
    order.getOrderDetails().remove(orderDetail);
    assertEquals(true, handler.getListOfDetachingObject().contains(CDOUtil.getCDOObject(orderDetail)));

    resource1.delete(null);
    assertEquals(true, handler.getListOfDetachingObject().contains(resource1));

    transaction.close();
    session.close();
  }

  public void testDetachingObjectVeto() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void detachingObject(CDOTransaction transaction, CDOObject object)
      {
        veto();
      }
    });

    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(order);

    try
    {
      order.getOrderDetails().remove(orderDetail);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException ex)
    {
      assertEquals(VETO_MESSAGE, ex.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public void testModifyingObject() throws Exception
  {
    CDOAccumulateTransactionHandler handler = new CDOAccumulateTransactionHandler();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(handler);

    CDOResource resource1 = transaction.getOrCreateResource(getResourcePath("/test1"));
    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    resource1.getContents().add(order);
    assertEquals(true, handler.getListOfAddingObject().contains(CDOUtil.getCDOObject(order)));
    assertEquals(true, handler.getListOfAddingObject().contains(CDOUtil.getCDOObject(orderDetail)));

    msg("Modifying Object");
    assertEquals(false, handler.getListOfModifyinObject().contains(orderDetail));
    orderDetail.setPrice(1.0f);
    assertEquals(true, handler.getListOfModifyinObject().contains(CDOUtil.getCDOObject(orderDetail)));
    transaction.close();
    session.close();
  }

  public void testModifyingResourceVeto() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
      {
        veto();
      }
    });

    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    try
    {
      transaction.getOrCreateResource(getResourcePath("/test1"));
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException ex)
    {
      assertEquals(VETO_MESSAGE, ex.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public void testModifyingObjectVeto() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Order order = getModel1Factory().createPurchaseOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
    resource.getContents().add(order);

    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
      {
        veto();
      }
    });

    try
    {
      orderDetail.setPrice(1.0f);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException ex)
    {
      assertEquals(VETO_MESSAGE, ex.getMessage());
    }
    finally
    {
      session.close();
    }
  }

  public void testCommitting() throws Exception
  {
    CDOAccumulateTransactionHandler handler = new CDOAccumulateTransactionHandler();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(handler);

    transaction.getOrCreateResource(getResourcePath("/test1"));
    transaction.commit();
    assertEquals(1, handler.getNumberOfCommit());

    transaction.close();
    session.close();
  }

  public void testRollbacked() throws Exception
  {
    CDOAccumulateTransactionHandler handler = new CDOAccumulateTransactionHandler();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.addTransactionHandler(handler);

    transaction.getOrCreateResource(getResourcePath("/test1"));

    transaction.rollback();
    assertEquals(1, handler.getNumberOfRollback());

    transaction.close();
    session.close();
  }

  public void testAsyncTransactionHandler() throws Exception
  {
    AtomicInteger attachingCount = new AtomicInteger();
    AtomicInteger detachingCount = new AtomicInteger();
    AtomicInteger modifyingCount = new AtomicInteger();
    CountDownLatch callbacksDone = new CountDownLatch(4);
    AtomicReference<Throwable> asyncFailure = new AtomicReference<>();
    ConcurrentLinkedQueue<Thread> callbackThreads = new ConcurrentLinkedQueue<>();
    Thread testThread = Thread.currentThread();
    CDOSession session = openSession();
    try
    {
      CDOTransaction transaction = session.openTransaction();

      Order order = getModel1Factory().createPurchaseOrder();
      Company company = getModel1Factory().createCompany();

      CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test1"));
      resource.getContents().add(company);

      CDOTransactionHandler delegate = new CDOTransactionHandler()
      {
        private void accessView()
        {
          if (Thread.currentThread() == testThread)
          {
            throw new IllegalStateException("Async transaction callback ran on the test thread");
          }

          company.getCity();
        }

        @Override
        public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
        {
          accessView();
          modifyingCount.incrementAndGet();
        }

        @Override
        public void detachingObject(CDOTransaction transaction, CDOObject object)
        {
          accessView();
          detachingCount.incrementAndGet();
        }

        @Override
        public void attachingObject(CDOTransaction transaction, CDOObject object)
        {
          accessView();
          attachingCount.incrementAndGet();
        }

        @Override
        public void rolledBackTransaction(CDOTransaction transaction)
        {
        }

        @Override
        public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
        }

        @Override
        public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
        {
        }
      };

      CDOAsyncTransactionHandler asyncHandler = new CDOAsyncTransactionHandler(delegate)
      {
        @Override
        protected void runAsync(Runnable runnable)
        {
          super.runAsync(() -> {
            callbackThreads.add(Thread.currentThread());

            try
            {
              runnable.run();
            }
            catch (Throwable ex)
            {
              asyncFailure.compareAndSet(null, ex);
            }
            finally
            {
              callbacksDone.countDown();
            }
          });
        }
      };

      transaction.addTransactionHandler(asyncHandler);
      resource.getContents().add(order); // 1 modif + 1 attach
      resource.getContents().remove(order); // 1 modif + 1 detach

      assertTrue("One or more expected asynchronous callbacks did not complete", callbacksDone.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

      for (Thread callbackThread : callbackThreads)
      {
        callbackThread.join(DEFAULT_TIMEOUT);
        assertFalse("Asynchronous callback thread did not terminate", callbackThread.isAlive());
      }

      Throwable failure = asyncFailure.get();
      if (failure != null)
      {
        throw new AssertionError("An asynchronous transaction callback failed", failure);
      }

      assertEquals(1, attachingCount.get());
      assertEquals(1, detachingCount.get());
      assertEquals(2, modifyingCount.get());
    }
    finally
    {
      session.close();
    }
  }

  protected void veto()
  {
    throw new IllegalStateException(VETO_MESSAGE);
  }

  /**
   * @author Simon McDuff
   */
  private static class CDOAccumulateTransactionHandler implements CDOTransactionHandler
  {
    private List<CDOObject> listOfAddingObject = new ArrayList<>();

    private List<CDOObject> listOfDetachingObject = new ArrayList<>();

    private List<CDOObject> listOfModifyinObject = new ArrayList<>();

    private int numberOfCommit;

    private int numberOfRollback;

    public CDOAccumulateTransactionHandler()
    {
    }

    public List<CDOObject> getListOfAddingObject()
    {
      return listOfAddingObject;
    }

    public List<CDOObject> getListOfDetachingObject()
    {
      return listOfDetachingObject;
    }

    public List<CDOObject> getListOfModifyinObject()
    {
      return listOfModifyinObject;
    }

    public int getNumberOfCommit()
    {
      return numberOfCommit;
    }

    public int getNumberOfRollback()
    {
      return numberOfRollback;
    }

    @Override
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      listOfAddingObject.add(object);
    }

    @Override
    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      numberOfCommit++;
    }

    @Override
    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      listOfDetachingObject.add(object);
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      listOfModifyinObject.add(object);
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
      numberOfRollback++;
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
    }
  }
}
