/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOTransactionHandler;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.util.CDOTransactionHandlerImpl;

import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

import java.util.ArrayList;
import java.util.List;

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

    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(handler);

    CDOResource resource1 = transaction.getOrCreateResource("/test1");

    assertEquals(true, handler.getListOfAddingObject().contains(resource1));

    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    resource1.getContents().add(order);
    assertEquals(true, handler.getListOfAddingObject().contains(order));
    assertEquals(true, handler.getListOfAddingObject().contains(orderDetail));

    transaction.close();
    session.close();
  }

  public void testAttachingResourceVeto() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(new CDOTransactionHandlerImpl()
    {
      @Override
      public void attachingObject(CDOTransaction transaction, CDOObject object)
      {
        veto();
      }
    });

    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    try
    {
      transaction.getOrCreateResource("/test1");
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
    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();

    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource("/test1");
    transaction.addHandler(new CDOTransactionHandlerImpl()
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

    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(handler);

    CDOResource resource1 = transaction.getOrCreateResource("/test1");
    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    resource1.getContents().add(order);
    assertEquals(true, handler.getListOfAddingObject().contains(order));
    assertEquals(true, handler.getListOfAddingObject().contains(orderDetail));

    msg("Remove Object");
    order.getOrderDetails().remove(orderDetail);
    assertEquals(true, handler.getListOfDetachingObject().contains(orderDetail));

    resource1.delete(null);
    assertEquals(true, handler.getListOfDetachingObject().contains(resource1));

    transaction.close();
    session.close();
  }

  public void testDetachingObjectVeto() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(new CDOTransactionHandlerImpl()
    {
      @Override
      public void detachingObject(CDOTransaction transaction, CDOObject object)
      {
        veto();
      }
    });

    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource("/test1");
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

    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(handler);

    CDOResource resource1 = transaction.getOrCreateResource("/test1");
    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    resource1.getContents().add(order);
    assertEquals(true, handler.getListOfAddingObject().contains(order));
    assertEquals(true, handler.getListOfAddingObject().contains(orderDetail));

    msg("Modifying Object");
    assertEquals(false, handler.getListOfModifyinObject().contains(orderDetail));
    orderDetail.setPrice(1.0f);
    assertEquals(true, handler.getListOfModifyinObject().contains(orderDetail));
    transaction.close();
    session.close();
  }

  public void testModifyingResourceVeto() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(new CDOTransactionHandlerImpl()
    {
      @Override
      public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
      {
        veto();
      }
    });

    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource("/test1");

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

  public void testModifyingObjectVeto() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();

    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    order.getOrderDetails().add(orderDetail);

    CDOResource resource = transaction.getOrCreateResource("/test1");
    resource.getContents().add(order);

    transaction.addHandler(new CDOTransactionHandlerImpl()
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

    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(handler);

    transaction.getOrCreateResource("/test1");
    transaction.commit();
    assertEquals(1, handler.getNumberOfCommit());

    transaction.close();
    session.close();
  }

  public void testRollbacked() throws Exception
  {
    CDOAccumulateTransactionHandler handler = new CDOAccumulateTransactionHandler();

    CDOSession session = openModel1Session();
    CDOTransactionImpl transaction = (CDOTransactionImpl)session.openTransaction();
    transaction.addHandler(handler);

    transaction.getOrCreateResource("/test1");

    transaction.rollback();
    assertEquals(1, handler.getNumberOfRollback());

    transaction.close();
    session.close();
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
    private List<CDOObject> listOfAddingObject = new ArrayList<CDOObject>();

    private List<CDOObject> listOfDetachingObject = new ArrayList<CDOObject>();

    private List<CDOObject> listOfModifyinObject = new ArrayList<CDOObject>();

    private int numberOfCommit = 0;

    private int numberOfRollback = 0;

    public CDOAccumulateTransactionHandler()
    {
    }

    public void clear()
    {
      listOfAddingObject.clear();
      listOfDetachingObject.clear();
      listOfModifyinObject.clear();
      numberOfCommit = 0;
      numberOfRollback = 0;
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

    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      listOfAddingObject.add(object);
    }

    public void committingTransaction(CDOTransaction transaction)
    {
      numberOfCommit++;
    }

    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      listOfDetachingObject.add(object);
    }

    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      listOfModifyinObject.add(object);
    }

    public void rolledBackTransaction(CDOTransaction transaction)
    {
      numberOfRollback++;
    }
  }
}
