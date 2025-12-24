/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_283985_1_Test extends AbstractCDOTest
{
  public void testBugzilla283985_PlainSingle() throws CommitException
  {
    runWithClosure(new SingleItemClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        for (OrderDetail detail : details)
        {
          Order order = (Order)detail.eContainer();
          order.getOrderDetails().remove(detail);
          target.getOrderDetails().add(detail);
        }
      }
    });
  }

  public void testBugzilla283985_PlainMultiple() throws CommitException
  {
    runWithClosure(new MultiItemClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        for (OrderDetail detail : details)
        {
          Order order = (Order)detail.eContainer();
          order.getOrderDetails().remove(detail);
          target.getOrderDetails().add(detail);
        }
      }
    });
  }

  public void testBugzilla283985_DragAndDropSingle() throws CommitException
  {
    runWithClosure(new SingleItemClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        float location = 0.5f;
        int operations = 7;
        int operation = 2;

        Command cmd = DragAndDropCommand.create(domain, target, location, operations, operation, details);
        domain.getCommandStack().execute(cmd);
      }
    });
  }

  public void testBugzilla283985_DragAndDropMultiple() throws CommitException
  {
    runWithClosure(new MultiItemClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        float location = 0.5f;
        int operations = 7;
        int operation = 2;

        Command cmd = DragAndDropCommand.create(domain, target, location, operations, operation, details);
        domain.getCommandStack().execute(cmd);
      }
    });
  }

  public void testBugzilla283985_DragAndDropMultipleMultiparent() throws CommitException
  {
    runWithClosure(new MultiItemMultiParentClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        float location = 0.5f;
        int operations = 7;
        int operation = 2;

        Command cmd = DragAndDropCommand.create(domain, target, location, operations, operation, details);
        domain.getCommandStack().execute(cmd);
      }
    });
  }

  public void testBugzilla283985_RemoveAndAddSingle() throws CommitException
  {
    runWithClosure(new SingleItemClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        Command dragCmd = RemoveCommand.create(domain, details);
        Command dropCmd = AddCommand.create(domain, target, null, details);
        domain.getCommandStack().execute(dragCmd);
        domain.getCommandStack().execute(dropCmd);
      }
    });
  }

  public void testBugzilla283985_RemoveAndAddMultiple() throws CommitException
  {
    runWithClosure(new MultiItemClosure()
    {
      @Override
      public void test(EditingDomain domain, Collection<OrderDetail> details, Order target)
      {
        Command dragCmd = RemoveCommand.create(domain, details);
        Command dropCmd = AddCommand.create(domain, target, null, details);
        domain.getCommandStack().execute(dragCmd);
        domain.getCommandStack().execute(dropCmd);
      }
    });
  }

  private void runWithClosure(IClosure closure) throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1"));
    r1.getContents().clear();

    // Set things up
    Model1Factory factory = getModel1Factory();
    Order order1 = factory.createPurchaseOrder();
    Order order2 = factory.createPurchaseOrder();
    Order order3 = factory.createPurchaseOrder();
    OrderDetail detail1 = factory.createOrderDetail();
    OrderDetail detail2 = factory.createOrderDetail();
    OrderDetail detail3 = factory.createOrderDetail();
    OrderDetail detail4 = factory.createOrderDetail();
    OrderDetail detail5 = factory.createOrderDetail();
    OrderDetail detail6 = factory.createOrderDetail();
    OrderDetail detail7 = factory.createOrderDetail();
    OrderDetail detail8 = factory.createOrderDetail();
    OrderDetail detail9 = factory.createOrderDetail();
    OrderDetail detail10 = factory.createOrderDetail();
    OrderDetail detail11 = factory.createOrderDetail();
    OrderDetail detail12 = factory.createOrderDetail();
    order1.getOrderDetails().add(detail1);
    order1.getOrderDetails().add(detail2);
    order1.getOrderDetails().add(detail3);
    order1.getOrderDetails().add(detail4);
    order2.getOrderDetails().add(detail5);
    order2.getOrderDetails().add(detail6);
    order2.getOrderDetails().add(detail7);
    order2.getOrderDetails().add(detail8);
    order3.getOrderDetails().add(detail9);
    order3.getOrderDetails().add(detail10);
    order3.getOrderDetails().add(detail11);
    order3.getOrderDetails().add(detail12);
    r1.getContents().add(order1);
    r1.getContents().add(order2);
    r1.getContents().add(order3);

    // Commit so that everything gets a CDOID
    tx.commit();

    // Create some versions
    detail1.setPrice(10.0f);
    tx.commit();
    detail1.setPrice(20.0f);
    tx.commit();

    // Ask closure to pick items to be moved
    //
    Collection<OrderDetail> details = closure
        .pickDetails(new OrderDetail[] { detail1, detail2, detail3, detail4, detail5, detail6, detail7, detail8, detail9, detail10, detail11, detail12, });

    // Keep references to the current CDOIDs and versions, to be used in the test
    CDOID[] idsBefore = new CDOID[details.size()];
    int[] versionsBefore = new int[details.size()];
    int i = 0;
    for (OrderDetail detail : details)
    {
      idsBefore[i] = CDOUtil.getCDOObject(detail).cdoID();
      versionsBefore[i] = CDOUtil.getCDOObject(detail).cdoRevision().getVersion();
      i++;
    }

    // Bring the resourceset under an editing domain
    ResourceSet rs = tx.getResourceSet();
    AdapterFactory adapterFact = new ReflectiveItemProviderAdapterFactory();
    CommandStack cs = new SaneCommandStack();
    EditingDomain domain = new AdapterFactoryEditingDomain(adapterFact, cs, rs);

    // Delegate test operation to the closure
    //
    closure.test(domain, details, order3);

    // Are the order details' CDOIDs still the same?
    //
    i = 0;
    for (OrderDetail detail : details)
    {
      CDOID idAfter = CDOUtil.getCDOObject(detail).cdoID();
      CDOID idBefore = idsBefore[i];
      assertEquals(idBefore, idAfter);

      int versionAfter = CDOUtil.getCDOObject(detail).cdoRevision().getVersion();
      int versionBefore = versionsBefore[i];
      assertEquals(versionBefore, versionAfter);

      i++;
    }

    tx.commit();

    // Check again
    //
    i = 0;
    for (OrderDetail detail : details)
    {
      CDOID idAfter = CDOUtil.getCDOObject(detail).cdoID();
      CDOID idBefore = idsBefore[i];
      assertEquals(idBefore, idAfter);

      int versionAfter = CDOUtil.getCDOObject(detail).cdoRevision().getVersion();
      int versionBefore = versionsBefore[i];
      assertEquals(versionBefore + 1, versionAfter);

      i++;
    }

    session.refresh();

    // Check again
    //
    i = 0;
    for (OrderDetail detail : details)
    {
      CDOID idAfter = CDOUtil.getCDOObject(detail).cdoID();
      CDOID idBefore = idsBefore[i];
      assertEquals(idBefore, idAfter);

      int versionAfter = CDOUtil.getCDOObject(detail).cdoRevision().getVersion();
      int versionBefore = versionsBefore[i];
      assertEquals(versionBefore + 1, versionAfter);

      i++;
    }

    tx.close();
    session.close();
  }

  public void testEReferencesCorrupted() throws CommitException
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
    session.getPackageRegistry().putEPackage(model4interfacesPackage.eINSTANCE);

    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.createResource(getResourcePath("/test/1"));

    GenRefMultiContained container1 = model4Factory.eINSTANCE.createGenRefMultiContained();
    GenRefMultiContained container2 = model4Factory.eINSTANCE.createGenRefMultiContained();
    ContainedElementNoOpposite element = model4Factory.eINSTANCE.createContainedElementNoOpposite();
    GenRefMultiNonContained referee = model4Factory.eINSTANCE.createGenRefMultiNonContained();

    container1.getElements().add(element);
    referee.getElements().add(element);

    res.getContents().add(container1);
    res.getContents().add(container2);
    res.getContents().add(referee);

    tx.commit();

    container1.getElements().remove(element);
    container2.getElements().add(element);

    tx.commit();
    tx.close();

    // === NEW TX ===

    tx = session.openTransaction();
    res = tx.getResource(getResourcePath("/test/1"));

    container1 = (GenRefMultiContained)res.getContents().get(0);
    container2 = (GenRefMultiContained)res.getContents().get(1);
    referee = (GenRefMultiNonContained)res.getContents().get(2);

    try
    {
      element = (ContainedElementNoOpposite)referee.getElements().get(0);
    }
    catch (ObjectNotFoundException e)
    {
      fail("Should not have thrown ObjectNotFoundException");
    }

    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());
  }

  // TODO Fix bug 344072
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testAddRemoveSequence() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1"));
    r1.getContents().clear();

    // Set things up
    Model1Factory factory = getModel1Factory();
    Order order1 = factory.createPurchaseOrder();
    Order order2 = factory.createPurchaseOrder();
    OrderDetail detail1 = factory.createOrderDetail();
    r1.getContents().add(order1);
    r1.getContents().add(order2);
    order2.getOrderDetails().add(detail1);
    tx.commit();

    order2.getOrderDetails().remove(detail1);
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(detail1).cdoState());

    order1.getOrderDetails().add(detail1);
    assertEquals(true, order1.getOrderDetails().contains(detail1));
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(detail1).cdoState());

    order1.getOrderDetails().remove(detail1);
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(detail1).cdoState());

    order2.getOrderDetails().add(detail1);
    assertEquals(true, order2.getOrderDetails().contains(detail1));
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(detail1).cdoState());

    order2.getOrderDetails().remove(detail1);
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(detail1).cdoState());

    order1.getOrderDetails().add(detail1);
    assertEquals(true, order1.getOrderDetails().contains(detail1));
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(detail1).cdoState());

    order1.getOrderDetails().remove(detail1);
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(detail1).cdoState());

    tx.commit();

    order1.getOrderDetails().add(detail1);
    assertEquals(true, order1.getOrderDetails().contains(detail1));
    order1.getOrderDetails().remove(detail1);

    order2.getOrderDetails().add(detail1);
    assertEquals(true, order2.getOrderDetails().contains(detail1));
    order2.getOrderDetails().remove(detail1);

    order1.getOrderDetails().add(detail1);
    assertEquals(true, order1.getOrderDetails().contains(detail1));
    order1.getOrderDetails().remove(detail1);

    tx.close();
    session.close();
  }

  public void testCanDaDmoreThanOnce() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1"));
    r1.getContents().clear();

    // Set things up
    Model1Factory factory = getModel1Factory();
    Order order1 = factory.createPurchaseOrder();
    Order order2 = factory.createPurchaseOrder();
    OrderDetail detail1 = factory.createOrderDetail();
    order1.getOrderDetails().add(detail1);
    r1.getContents().add(order1);
    r1.getContents().add(order2);

    // Commit so that everything gets a CDOID
    tx.commit();

    // Bring the resourceset under an editing domain
    ResourceSet rs = tx.getResourceSet();
    AdapterFactory adapterFact = new ReflectiveItemProviderAdapterFactory();
    CommandStack cs = new SaneCommandStack();
    EditingDomain domain = new AdapterFactoryEditingDomain(adapterFact, cs, rs);

    float location = 0.5f;
    int operations = 7;
    int operation = 2;
    Collection<OrderDetail> coll = Collections.singleton(detail1);

    assertSame(order1, detail1.eContainer());

    // Drag and drop #1
    Command cmd = DragAndDropCommand.create(domain, order2, location, operations, operation, coll);
    domain.getCommandStack().execute(cmd);
    assertSame(order2, detail1.eContainer());

    // Drag and drop #2
    cmd = DragAndDropCommand.create(domain, order1, location, operations, operation, coll);
    domain.getCommandStack().execute(cmd);
    assertSame(order1, detail1.eContainer());
    tx.commit();
    assertSame(order1, detail1.eContainer());

    assertSame(CDOState.CLEAN, CDOUtil.getCDOObject(detail1).cdoState());

    // Drag and drop #3
    cmd = DragAndDropCommand.create(domain, order2, location, operations, operation, coll);
    domain.getCommandStack().execute(cmd);
    assertSame(order2, detail1.eContainer());

    tx.commit();
    tx.close();
    session.close();
  }

  public void testCanReattachDirtyObject() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.getOrCreateResource(getResourcePath("/r1"));
    r1.getContents().clear();

    // Set things up
    Model1Factory factory = getModel1Factory();
    Order order1 = factory.createPurchaseOrder();
    Order order2 = factory.createPurchaseOrder();
    OrderDetail detail1 = factory.createOrderDetail();
    r1.getContents().add(order1);
    r1.getContents().add(order2);
    order1.getOrderDetails().add(detail1);
    tx.commit();

    // Make the order detail dirty
    final float PRICE = 10.0f;
    detail1.setPrice(PRICE);

    // Now move it
    order1.getOrderDetails().remove(detail1);
    order2.getOrderDetails().add(detail1);
    tx.commit();

    // See if the price update was incorporated in the commit
    {
      CDOView view = session.openView();
      CDOResource res = view.getResource(getResourcePath("/r1"));
      Order order2prime = (Order)res.getContents().get(1);
      OrderDetail od1 = order2prime.getOrderDetails().get(0);
      assertEquals(PRICE, od1.getPrice());
      view.close();
    }
  }

  /**
   * @author Caspar De Groot
   */
  private static class SaneCommandStack extends BasicCommandStack
  {
    @Override
    protected void handleError(Exception exception)
    {
      throw new WrappedException(exception);
    }
  }

  /**
   * @author Caspar De Groot
   */
  private interface IClosure
  {
    public void test(EditingDomain domain, Collection<OrderDetail> details, Order target);

    public Collection<OrderDetail> pickDetails(OrderDetail[] orderDetails);
  }

  /**
   * @author Caspar De Groot
   */
  private static abstract class SingleItemClosure implements IClosure
  {
    @Override
    public Collection<OrderDetail> pickDetails(OrderDetail[] orderDetails)
    {
      return Collections.singleton(orderDetails[0]);
    }
  }

  /**
   * @author Caspar De Groot
   */
  private static abstract class MultiItemClosure implements IClosure
  {
    @Override
    public Collection<OrderDetail> pickDetails(OrderDetail[] orderDetails)
    {
      List<OrderDetail> details = new LinkedList<>();
      details.add(orderDetails[0]);
      details.add(orderDetails[1]);
      return details;
    }
  }

  /**
   * @author Caspar De Groot
   */
  private static abstract class MultiItemMultiParentClosure implements IClosure
  {
    @Override
    public Collection<OrderDetail> pickDetails(OrderDetail[] orderDetails)
    {
      List<OrderDetail> details = new LinkedList<>();
      details.add(orderDetails[1]);
      details.add(orderDetails[2]);
      details.add(orderDetails[5]);
      details.add(orderDetails[6]);
      return details;
    }
  }
}
