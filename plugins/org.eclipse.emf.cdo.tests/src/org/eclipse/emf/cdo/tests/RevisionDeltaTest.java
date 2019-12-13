/*
 * Copyright (c) 2007-2012, 2015-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model3.NodeD;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

/**
 * See bug 201266
 *
 * @author Simon McDuff
 */
public class RevisionDeltaTest extends AbstractCDOTest
{
  public RevisionDeltaTest()
  {
  }

  public void testBasicRevisionDelta() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

    Company company1 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    company1.setName("TEST");
    InternalCDORevision rev1 = getCopyCDORevision(company1);

    // Test simple attributes
    company1.setName("TEST3");
    InternalCDORevision rev2 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta = rev2.compare(rev1);
    assertEquals(1, revisionDelta.getFeatureDeltas().size());
    CDOSetFeatureDelta setDelta = (CDOSetFeatureDelta)revisionDelta.getFeatureDeltas().get(0);
    assertEquals("TEST3", setDelta.getValue());

    // Test List clear
    company1.getCategories().clear();
    InternalCDORevision rev3 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta3 = rev3.compare(rev2);
    assertEquals(1, revisionDelta3.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta3List = (CDOListFeatureDeltaImpl)revisionDelta3.getFeatureDeltas().get(0);

    assertEquals(1, delta3List.getListChanges().size());
    assertEquals(true, delta3List.getListChanges().get(0) instanceof CDOClearFeatureDelta);

    // Test List Add
    for (int i = 0; i < 5; i++)
    {
      Category category = getModel1Factory().createCategory();
      company1.getCategories().add(category);
    }

    InternalCDORevision rev4 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta4 = rev4.compare(rev3);
    assertEquals(1, revisionDelta4.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta4List = (CDOListFeatureDeltaImpl)revisionDelta4.getFeatureDeltas().get(0);

    assertEquals(5, delta4List.getListChanges().size());
    assertEquals(true, delta4List.getListChanges().get(0) instanceof CDOAddFeatureDelta);
    transaction.rollback();
    transaction.close();
    session.close();
  }

  /**
   * Sending deltas doesn't adjust CDOIDs
   * <p>
   * See bug 214374
   */
  public void testBugzilla214374() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    Customer customer = getModel1Factory().createCustomer();
    salesOrder.setCustomer(customer);

    resource.getContents().add(customer);
    transaction.commit();
    transaction.close();

    CDOTransaction transaction2 = session.openTransaction();
    SalesOrder salesOrder2 = (SalesOrder)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(salesOrder).cdoID(), true));
    CDORevision salesRevision = CDOUtil.getCDOObject(salesOrder2).cdoRevision();
    EStructuralFeature customerFeature = getModel1Package().getSalesOrder_Customer();

    Object value = salesRevision.data().get(customerFeature, 0);
    assertEquals(true, value instanceof CDOID);
    transaction2.close();
    session.close();
  }

  /**
   * CDOView.getRevision() does not work for transactions/dirty objects (INVALID)
   * <p>
   * See bug 214431
   */
  public void testBugzilla214431() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    transaction.commit();

    salesOrder.setId(4711);
    assertNotSame(CDOUtil.getCDOObject(salesOrder).cdoRevision(),
        ((InternalCDOTransaction)transaction).getRevision(CDOUtil.getCDOObject(salesOrder).cdoID(), true));
    assertEquals(CDOUtil.getCDOObject(salesOrder).cdoRevision(), transaction.getDirtyObjects().get(CDOUtil.getCDOObject(salesOrder).cdoID()).cdoRevision());
    transaction.close();
    session.close();
  }

  /**
   * CDOView.getRevision() does not work for transactions/dirty objects (INVALID)
   * <p>
   * See bug 214374
   */
  public void testBugzilla214374_HandleClear() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Customer customer = getModel1Factory().createCustomer();
    resource.getContents().add(customer);

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);
    transaction.commit();

    salesOrder = getModel1Factory().createSalesOrder();
    customer = getModel1Factory().createCustomer();
    resource.getContents().add(salesOrder);
    resource.getContents().add(customer);
    salesOrder.setCustomer(customer);
    transaction.commit();
    transaction.close();
    session.close();
  }

  /**
   * java.lang.IllegalStateException with MEMStore
   * <p>
   * See bug 243282
   */
  public void testBugzilla243282_Exception() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Customer customer = getModel1Factory().createCustomer();
    resource.getContents().add(customer);

    transaction.commit();

    InternalCDORevisionManager revisionManager = getRepository().getRevisionManager();
    CDORevision revision = CDOUtil.getCDOObject(customer).cdoRevision();
    revisionManager.getCache().removeRevision(revision.getID(), revision);

    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    resource.getContents().add(salesOrder);
    customer.getSalesOrders().add(salesOrder);

    IStore store = getRepository().getStore();
    if (store instanceof MEMStore)
    {
      MEMStore memStore = (MEMStore)store;
      CDORevisionUtil.dumpAllRevisions(memStore.getAllRevisions(), IOUtil.OUT());
    }

    transaction.commit();
    transaction.close();
    session.close();
  }

  @SuppressWarnings("unused")
  public void testDetachWithXRef() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    NodeD a = getModel3Factory().createNodeD();
    NodeD b = getModel3Factory().createNodeD();
    NodeD c = getModel3Factory().createNodeD();

    a.getChildren().add(b);
    c.getOtherNodes().add(b);

    resource.getContents().add(a);
    resource.getContents().add(c);

    if (true)
    {
      transaction.commit();
      transaction.close();
      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("/test1"));
    }

    // Start test logic

    a = (NodeD)resource.getContents().get(0);
    b = a.getChildren().get(0);
    c = (NodeD)resource.getContents().get(1);

    a.getChildren().remove(0);
    Object[] cB = c.getOtherNodes().toArray();

    CDORevisionDelta aDelta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(a).cdoID());
    CDORevisionDelta cDelta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(c).cdoID());

    session.close();
  }

  @SuppressWarnings("unused")
  public void testDetachWithXRef_Remove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    NodeD a = getModel3Factory().createNodeD();
    NodeD b = getModel3Factory().createNodeD();
    NodeD c = getModel3Factory().createNodeD();

    a.getChildren().add(b);
    c.getOtherNodes().add(b);

    resource.getContents().add(a);
    resource.getContents().add(c);

    if (true)
    {
      transaction.commit();
      transaction.close();
      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("/test1"));
    }

    // Start test logic

    a = (NodeD)resource.getContents().get(0);
    b = a.getChildren().get(0);
    c = (NodeD)resource.getContents().get(1);

    a.getChildren().remove(0);
    c.getOtherNodes().remove(0);

    Object[] cB = c.getOtherNodes().toArray();

    CDORevisionDelta aDelta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(a).cdoID());
    CDORevisionDelta cDelta = transaction.getRevisionDeltas().get(CDOUtil.getCDOObject(c).cdoID());

    CDOListFeatureDelta list = (CDOListFeatureDelta)cDelta.getFeatureDelta(getModel3Package().getNodeD_OtherNodes());
    assertEquals(1, list.getListChanges().size());

    session.close();
  }

  public void testListRemoveMiddle()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.remove(5);
      }
    });
  }

  public void testListRemoveLast()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.remove(list.size() - 1);
      }
    });
  }

  public void testListRemoveFirst()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.remove(0);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListSimpleMove()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.move(2, 7);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListMoveToLast()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.move(2, list.size() - 1);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListMoveToFirst()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.move(2, 0);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListTwoIndependentMoves()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.move(1, 3);
        list.move(7, 4);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListTwoIntersectingMoves()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      public void doManipulations(EList<?> list)
      {
        list.move(1, 7);
        list.move(3, 4);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListInsertFirst()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry");
        l.add(0, company);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListInsertMiddle()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry");
        l.add(5, company);
      }
    });
  }

  public void testListInsertLast()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry");
        l.add(company);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListMultipleOpsWithClear()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;

        l.remove(4);
        l.remove(7);

        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(5, company);

        l.move(1, 2);
        l.move(7, 0);

        l.clear();

        company = getModel1Factory().createCompany();
        company.setName("NewEntry 2");
        l.add(company);

        company = getModel1Factory().createCompany();
        company.setName("NewEntry 3");
        l.add(company);

        company = getModel1Factory().createCompany();
        company.setName("NewEntry 4");
        l.add(0, company);

        l.move(1, 2);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testListMultipleOps()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;

        l.remove(7);

        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(5, company);

        l.move(1, 2);

        company = getModel1Factory().createCompany();
        company.setName("NewEntry 2");
        l.add(company);

        l.move(7, 0);

        company = getModel1Factory().createCompany();
        company.setName("NewEntry 3");
        l.add(company);

        l.remove(4);

        company = getModel1Factory().createCompany();
        company.setName("NewEntry 4");
        l.add(0, company);

        l.move(1, 2);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testMultipleInserts()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(7, company);
        company = getModel1Factory().createCompany();
        company.setName("NewEntry 2");
        l.add(12, company);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testInsertAndRemove()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(7, company);
        l.remove(12);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testInsertAndMove()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(7, company);
        l.move(12, 7);
      }
    });
  }

  public void testMoveAndDelete()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        l.move(12, 7);
        l.remove(12);
      }
    });
  }

  public void testInsertAndMoveAndRemove()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(7, company);
        l.move(12, 7);
        l.remove(12);
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testInsertAndSet()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        l.add(7, company);
        Company company2 = getModel1Factory().createCompany();
        company2.setName("NewEntry 2");
        l.set(7, company2);
        l.add(19, company); // <- needed because the set operation makes the company a dangling reference
      }
    });
  }

  @Skips(IRepositoryConfig.CAPABILITY_UNORDERED_LISTS)
  public void testSetAndRemove()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        Company company = getModel1Factory().createCompany();
        company.setName("NewEntry 1");
        Company oldCompany = l.get(7);
        l.set(7, company);
        l.add(19, oldCompany);
        l.remove(7); // <- needed because the set operation makes the company a dangling reference
      }
    });
  }

  public void testMultipleRemoves()
  {
    testStoreDelta(new ListManipulator()
    {
      @Override
      @SuppressWarnings("unchecked")
      public void doManipulations(EList<?> list)
      {
        EList<Company> l = (EList<Company>)list;
        l.remove(7);
        l.remove(12);
      }
    });
  }

  private InternalCDORevision getCopyCDORevision(Object object)
  {
    return (InternalCDORevision)CDOUtil.getCDOObject((EObject)object).cdoRevision().copy();
  }

  protected void testStoreDelta(ListManipulator manipulator)
  {
    BasicEList<Company> reference = new BasicEList<Company>();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      for (int i = 0; i < 20; i++)
      {
        String name = "company " + i;
        Company company = getModel1Factory().createCompany();
        company.setName(name);
        resource.getContents().add(company);

        company = getModel1Factory().createCompany();
        company.setName(name);
        reference.add(company);
      }

      try
      {
        transaction.commit();
      }
      catch (CommitException ex)
      {
        throw WrappedException.wrap(ex);
      }

      transaction.close();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      manipulator.doManipulations(resource.getContents());
      manipulator.doManipulations(reference);

      try
      {
        transaction.commit();
      }
      catch (CommitException ex)
      {
        throw WrappedException.wrap(ex);
      }

      transaction.close();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/test1"));

      assertEquals(reference.size(), resource.getContents().size());

      for (int i = 0; i < reference.size(); i++)
      {
        assertEquals(reference.get(i).getName(), ((Company)resource.getContents().get(i)).getName());
      }

      view.close();
      session.close();
    }
  }

  /**
   * @author Simon McDuff
   */
  protected static interface ListManipulator
  {
    public void doManipulations(EList<?> list);
  }
}
