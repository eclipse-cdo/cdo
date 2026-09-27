/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.Access;
import org.eclipse.net4j.util.concurrent.CriticalSection.LockedCriticalSection;
import org.eclipse.net4j.util.concurrent.DelegableReentrantLock;
import org.eclipse.net4j.util.concurrent.NonFairReentrantLock;
import org.eclipse.net4j.util.ref.ReferenceType;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Tests the reentrant lock and monitor-safety behavior of CDO views.
 *
 * @author Eike Stepper
 */
public class ViewSynchronizationTest extends AbstractCDOTest
{
  public void testLockIdentityAndReentrancy()
  {
    CDOTransaction transaction = openSession().openTransaction();
    InternalCDOView view = (InternalCDOView)transaction;
    LockedCriticalSection sync = (LockedCriticalSection)view.sync();
    assertTrue(sync.getLock() instanceof NonFairReentrantLock);

    try (Access access = view.access())
    {
      assertSame(sync.getLock(), access.getLock());
    }

    sync.run(() -> {
      assertEquals(1, ((NonFairReentrantLock)sync.getLock()).getHoldCount());

      try (Access access = view.access())
      {
        assertEquals(2, ((NonFairReentrantLock)sync.getLock()).getHoldCount());
      }
    });

    assertFalse(((NonFairReentrantLock)sync.getLock()).isLocked());
  }

  public void testNextViewLockIsPreserved()
  {
    NonFairReentrantLock expectedLock = new NonFairReentrantLock();
    CDOUtil.setNextViewLock(expectedLock);

    try
    {
      InternalCDOView view = (InternalCDOView)openSession().openTransaction();
      assertSame(expectedLock, ((LockedCriticalSection)view.sync()).getLock());
      try (Access access = view.access())
      {
        assertSame(expectedLock, access.getLock());
      }
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }
  }

  public void testCacheReferenceTypeReplacementAcquiresViewLockOnce()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOView.Options options = view.options();
    int lockCalls = lock.getLockCalls();

    assertTrue(options.setCacheReferenceType(ReferenceType.WEAK));
    assertEquals("Cache reference type replacement must acquire only the outer view lock", lockCalls + 1, lock.getLockCalls());
  }

  public void testStoreScalarReadAcquisitionCount() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/lock-count"));
    EObject company = getModel1Factory().createCompany();
    EStructuralFeature nameFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("name");
    company.eSet(nameFeature, "acquisition-count");
    resource.getContents().add(company);
    transaction.commit();

    assertEquals("acquisition-count", company.eGet(nameFeature));
    int lockCalls = lock.getLockCalls();

    assertEquals("acquisition-count", company.eGet(nameFeature));
    int storeReadAcquisitions = lock.getLockCalls() - lockCalls;
    assertEquals("Store scalar read should acquire only its outer view lock", 1, storeReadAcquisitions);

    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    int stateMachineReadLockCalls = lock.getLockCalls();
    CDOStateMachine.INSTANCE.read(cdoObject);
    assertEquals("Independent StateMachine.read() must remain synchronized", stateMachineReadLockCalls + 1, lock.getLockCalls());
  }

  public void testStoreSingleReferenceReadAcquisitionCount() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/reference-lock-count"));
    Company company = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    salesOrder.setCustomer(customer);
    company.getCustomers().add(customer);
    company.getSalesOrders().add(salesOrder);
    resource.getContents().add(company);
    transaction.commit();

    InternalCDOView view = (InternalCDOView)transaction;
    InternalCDOObject customerCDOObject = (InternalCDOObject)CDOUtil.getCDOObject(customer);
    InternalCDOObject salesOrderCDOObject = (InternalCDOObject)CDOUtil.getCDOObject(salesOrder);
    assertSame(view, customerCDOObject.cdoView());
    assertSame(customerCDOObject, view.getObject(customerCDOObject.cdoID(), false));
    assertSame(salesOrderCDOObject, view.getObject(salesOrderCDOObject.cdoID(), false));
    assertSame(customer, salesOrder.getCustomer());

    lock.resetLockCalls();
    assertSame(customer, salesOrder.getCustomer());
    assertEquals("Warmed single-valued reference read acquisition count", 1, lock.getLockCalls());
    assertEquals("Warmed single-valued reference acquisition origins", List.of("CDOStoreImpl.get"), lock.getOrigins());

    assertSame(salesOrder, company.getSalesOrders().get(0));
    lock.resetLockCalls();
    assertSame(salesOrder, company.getSalesOrders().get(0));
    assertEquals("Warmed many-valued reference element read acquisition count", 1, lock.getLockCalls());
    assertEquals("Warmed many-valued reference element acquisition origin", List.of("CDOStoreImpl.get"), lock.getOrigins());
  }

  public void testRevisionPrefetchingPolicyGetterDoesNotAcquireViewLock()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    int lockCalls = lock.getLockCalls();
    assertNotNull(view.options().getRevisionPrefetchingPolicy());
    assertEquals("Revision prefetching policy getter must not acquire the view lock", lockCalls, lock.getLockCalls());
  }

  public void testRevisionPrefetchingPolicySetterAcquiresViewLock()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    int lockCalls = lock.getLockCalls();
    view.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(3));
    assertEquals("Revision prefetching policy setter must acquire the view lock once", lockCalls + 1, lock.getLockCalls());
  }

  public void testIndependentConvertIDToObjectAcquiresViewLock() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/independent-convert-id"));
    Company company = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();
    company.getCustomers().add(customer);
    resource.getContents().add(company);
    transaction.commit();

    InternalCDOView view = (InternalCDOView)transaction;
    InternalCDOObject customerCDOObject = (InternalCDOObject)CDOUtil.getCDOObject(customer);
    assertSame(customerCDOObject, view.getObject(customerCDOObject.cdoID(), false));

    lock.resetLockCalls();
    assertSame(customer, view.convertIDToObject(customerCDOObject.cdoID()));
    assertEquals("Independent convertIDToObject must acquire the view lock once", 1, lock.getLockCalls());
    assertEquals("Independent convertIDToObject acquisition origin", List.of("AbstractCDOView.convertIDToObject"), lock.getOrigins());
  }

  public void testFeatureAnalyzerGetterDoesNotAcquireViewLock()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOView.Options options = view.options();
    int lockCalls = lock.getLockCalls();

    assertSame(CDOFeatureAnalyzer.NOOP, options.getFeatureAnalyzer());
    assertEquals("Feature analyzer getter must not acquire the view lock", lockCalls, lock.getLockCalls());
  }

  public void testFeatureAnalyzerSetterAcquiresViewLock()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    int lockCalls = lock.getLockCalls();
    view.options().setFeatureAnalyzer(new TestFeatureAnalyzer());
    assertTrue("Feature analyzer setter must acquire the view lock", lock.getLockCalls() > lockCalls);
  }

  public void testIndependentResourcePathCacheClearAcquiresViewLock()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    int lockCalls = lock.getLockCalls();
    view.clearResourcePathCacheIfNecessary(null);
    assertEquals("Independent resource path cache clear must acquire the view lock", lockCalls + 1, lock.getLockCalls());
  }

  public void testStoreScalarWriteAcquisitionCount() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/lock-count-write"));
    EObject company = getModel1Factory().createCompany();
    EStructuralFeature nameFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("name");
    company.eSet(nameFeature, "acquisition-count");
    resource.getContents().add(company);
    transaction.commit();

    assertEquals("acquisition-count", company.eGet(nameFeature));
    AtomicBoolean modifyingHandlerCalled = new AtomicBoolean();
    AtomicBoolean modifyingHandlerHeldViewLock = new AtomicBoolean();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
      {
        modifyingHandlerCalled.set(true);
        modifyingHandlerHeldViewLock.set(lock.isHeldByCurrentThread());
      }
    });

    int lockCalls = lock.getLockCalls();

    company.eSet(nameFeature, "acquisition-count-updated");
    int storeWriteAcquisitions = lock.getLockCalls() - lockCalls;
    assertEquals("Built-in first scalar write should acquire only the outer view lock", 1, storeWriteAcquisitions);
    assertTrue("First-write transaction handler must still run", modifyingHandlerCalled.get());
    assertTrue("First-write transaction handler must still run under the outer view lock", modifyingHandlerHeldViewLock.get());

    company.eSet(nameFeature, "acquisition-count-rewritten");
    assertEquals("A second scalar write must preserve the dirty-object rewrite path", "acquisition-count-rewritten", company.eGet(nameFeature));

    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    CDOState originalState = cdoObject.cdoState();
    cdoObject.cdoInternalSetState(CDOState.INVALID_CONFLICT);
    int stateMachineWriteLockCalls = lock.getLockCalls();
    try
    {
      CDOStateMachine.INSTANCE.write(cdoObject, null);
      assertEquals("Independent StateMachine.write() must acquire the owning view lock", stateMachineWriteLockCalls + 1, lock.getLockCalls());
    }
    finally
    {
      cdoObject.cdoInternalSetState(originalState);
    }
  }

  public void testStoreNewObjectWriteAcquisitionCount() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/new-write-lock-count"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    EStructuralFeature nameFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("name");
    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    assertEquals(CDOState.NEW, cdoObject.cdoState());
    company.eGet(nameFeature);

    lock.resetLockCalls();
    company.eSet(nameFeature, "new-object-write");

    assertEquals("Measured write must leave the object NEW", CDOState.NEW, cdoObject.cdoState());
    assertEquals("NEW-object scalar write acquisition count", 1, lock.getLockCalls());
    assertEquals("NEW-object scalar write acquisition origin", List.of("CDOStoreImpl.set"), lock.getOrigins());
  }

  public void testStoreRewriteTransitionAcquisitionCount() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/rewrite-write-lock-count"));
    Company company = getModel1Factory().createCompany();
    EStructuralFeature nameFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("name");
    company.eSet(nameFeature, "clean-value");
    resource.getContents().add(company);
    transaction.commit();

    assertEquals("clean-value", company.eGet(nameFeature));
    company.eSet(nameFeature, "first-dirty-value");
    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    assertEquals(CDOState.DIRTY, cdoObject.cdoState());

    lock.resetLockCalls();
    company.eSet(nameFeature, "rewritten-value");

    assertEquals("Rewrite write must preserve DIRTY state", CDOState.DIRTY, cdoObject.cdoState());
    assertEquals("DIRTY-object rewrite acquisition count", 1, lock.getLockCalls());
    assertEquals("DIRTY-object rewrite acquisition origin", List.of("CDOStoreImpl.set"), lock.getOrigins());
  }

  public void testStoreRewriteUndoTransitionAcquisitionCount() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/rewrite-undo-lock-count"));
    Company company = getModel1Factory().createCompany();
    EStructuralFeature nameFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("name");
    company.setName("undo-original");
    resource.getContents().add(company);
    transaction.commit();

    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    assertEquals("Object must be clean after commit", CDOState.CLEAN, cdoObject.cdoState());
    assertFalse("Transaction must be clean after commit", transaction.isDirty());

    company.eSet(nameFeature, "undo-modified");
    assertEquals("Object must be dirty after A to B", CDOState.DIRTY, cdoObject.cdoState());
    assertTrue("Transaction must be dirty after A to B", transaction.isDirty());

    InternalCDOTransaction internalTransaction = (InternalCDOTransaction)transaction;
    assertEquals("Isolated test has one dirty object", 1, internalTransaction.getDirtyObjects().size());
    assertTrue("Isolated test has no new objects", internalTransaction.getNewObjects().isEmpty());
    assertTrue("Isolated test has no detached objects", internalTransaction.getDetachedObjects().isEmpty());

    AtomicBoolean undoHandlerCalled = new AtomicBoolean();
    AtomicBoolean undoHandlerHeldViewLock = new AtomicBoolean();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void undoingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
      {
        undoHandlerCalled.set(true);
        undoHandlerHeldViewLock.set(lock.isHeldByCurrentThread());
      }
    });

    lock.resetLockCalls();
    company.eSet(nameFeature, "undo-original");
    int undoWriteAcquisitions = lock.getLockCalls();
    List<String> undoWriteOrigins = new ArrayList<>(lock.getOrigins());

    assertEquals("Object must return to CLEAN after B to A", CDOState.CLEAN, cdoObject.cdoState());
    assertFalse("Isolated transaction must no longer be dirty after undo", transaction.isDirty());
    assertEquals("Feature value must be restored", "undo-original", company.eGet(nameFeature));
    assertTrue("Undo handler must run", undoHandlerCalled.get());
    assertTrue("Undo handler must run under the outer view lock", undoHandlerHeldViewLock.get());
    assertEquals("Undo rewrite acquisition count", 1, undoWriteAcquisitions);
    assertEquals("Undo rewrite acquisition origin", List.of("CDOStoreImpl.set"), undoWriteOrigins);
  }

  public void testRemainingStoreLockAcquisitionPaths() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/remaining-store-lock-paths"));
    Company company = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();
    Category category1 = getModel1Factory().createCategory();
    Category category2 = getModel1Factory().createCategory();
    Customer customer1 = getModel1Factory().createCustomer();
    Customer customer2 = getModel1Factory().createCustomer();
    SalesOrder salesOrder1 = getModel1Factory().createSalesOrder();
    SalesOrder salesOrder2 = getModel1Factory().createSalesOrder();
    SalesOrder salesOrder3 = getModel1Factory().createSalesOrder();
    company.getCategories().add(category1);
    company.getCategories().add(category2);
    company.getCustomers().add(customer1);
    company.getCustomers().add(customer2);
    salesOrder1.setCustomer(customer1);
    salesOrder2.setCustomer(customer2);
    company.getSalesOrders().add(salesOrder1);
    company.getSalesOrders().add(salesOrder2);
    resource.getContents().add(company);
    resource.getContents().add(company2);
    transaction.commit();

    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    lock.resetLockCalls();
    assertEquals(companyID, ((InternalCDOView)transaction).convertObjectToID(company, true));
    assertStoreLockAcquisitions(lock, "Independent convertObjectToID facade", "AbstractCDOView.convertObjectToID");

    lock.resetLockCalls();
    assertEquals(companyID, ((InternalCDOView)transaction).provideCDOID(company));
    assertStoreLockAcquisitions(lock, "provideCDOID", "CDOTransactionImpl.provideCDOID", "AbstractCDOView.provideCDOID", "AbstractCDOView.convertObjectToID");

    assertSame(company, category1.eContainer());
    lock.resetLockCalls();
    assertSame(company, category1.eContainer());
    assertStoreLockAcquisitions(lock, "EStore getContainer", "CDOStoreImpl.getContainer");

    assertSame(resource, category1.eResource());
    lock.resetLockCalls();
    assertSame(resource, category1.eResource());
    assertStoreLockAcquisitions(lock, "EStore getResource", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource");

    EStructuralFeature nameFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("name");
    company.eSet(nameFeature, "is-set");
    assertTrue(company.eIsSet(nameFeature));
    lock.resetLockCalls();
    assertTrue(company.eIsSet(nameFeature));
    assertStoreLockAcquisitions(lock, "EStore isSet", "CDOStoreImpl.isSet");

    assertTrue(company.getSalesOrders().contains(salesOrder1));
    assertFalse(company.getSalesOrders().contains(salesOrder3));
    assertFalse(company2.getSalesOrders().contains(salesOrder1));
    assertFalse(company.getSalesOrders().contains(null));
    assertSame(salesOrder1, transaction.getObject(CDOUtil.getCDOObject(salesOrder1).cdoID(), false));

    lock.resetLockCalls();
    assertTrue(company.getSalesOrders().contains(salesOrder1));
    assertStoreLockAcquisitions(lock, "EStore contains", "CDOStoreImpl.size", "CDOStoreImpl.contains");

    assertEquals(0, company.getSalesOrders().indexOf(salesOrder1));
    lock.resetLockCalls();
    assertEquals(0, company.getSalesOrders().indexOf(salesOrder1));
    assertStoreLockAcquisitions(lock, "EStore indexOf", "CDOStoreImpl.indexOf");

    assertEquals(1, company.getSalesOrders().lastIndexOf(salesOrder2));
    lock.resetLockCalls();
    assertEquals(1, company.getSalesOrders().lastIndexOf(salesOrder2));
    assertStoreLockAcquisitions(lock, "EStore lastIndexOf", "CDOStoreImpl.lastIndexOf");

    SalesOrder[] salesOrderArray = company.getSalesOrders().toArray(new SalesOrder[2]);
    assertEquals(2, salesOrderArray.length);
    lock.resetLockCalls();
    company.getSalesOrders().toArray(new SalesOrder[2]);
    assertStoreLockAcquisitions(lock, "Typed EStore toArray", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.toArray");

    lock.resetLockCalls();
    assertEquals(2, company.getSalesOrders().toArray().length);
    assertStoreLockAcquisitions(lock, "Untyped EStore toArray", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.toArray");

    lock.resetLockCalls();
    assertSame(category1, company.getCategories().move(1, 0));
    assertStoreLockAcquisitions(lock, "EStore list move", "CDOObjectImpl$CDOStoreEList.move");
    assertSame(category2, company.getCategories().get(0));
    assertSame(category1, company.getCategories().get(1));

    lock.resetLockCalls();
    assertSame(category1, company.getCategories().move(1, 1));
    assertStoreLockAcquisitions(lock, "EStore same-index list move", "CDOObjectImpl$CDOStoreEList.move", "CDOStoreImpl.get");
    assertSame(category2, company.getCategories().get(0));
    assertSame(category1, company.getCategories().get(1));

    lock.resetLockCalls();
    assertSame(category1, company.getCategories().move(0, 1));
    assertStoreLockAcquisitions(lock, "EStore list move toward beginning", "CDOObjectImpl$CDOStoreEList.move");
    assertSame(category1, company.getCategories().get(0));
    assertSame(category2, company.getCategories().get(1));

    lock.resetLockCalls();
    try
    {
      company.getCategories().move(2, 0);
      fail("Invalid move target index should fail");
    }
    catch (IndexOutOfBoundsException ex)
    {
      assertEquals("targetIndex=2, size=2", ex.getMessage());
    }
    assertStoreLockAcquisitions(lock, "EStore list move invalid target", "CDOObjectImpl$CDOStoreEList.move");

    lock.resetLockCalls();
    try
    {
      company.getCategories().move(0, 2);
      fail("Invalid move source index should fail");
    }
    catch (IndexOutOfBoundsException ex)
    {
      assertEquals("sourceIndex=2, size=2", ex.getMessage());
    }
    assertStoreLockAcquisitions(lock, "EStore list move invalid source", "CDOObjectImpl$CDOStoreEList.move");

    lock.resetLockCalls();
    company2.getCategories().add(category1);
    assertStoreLockAcquisitions(lock, "Containment move to another parent", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size", "CDOStoreImpl.contains",
        "CDOStoreImpl.size", "CDOStoreImpl.getContainer", "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.getContainer",
        "CDOObjectImpl$CDOStoreEList.basicRemove", "CDOStoreImpl.indexOf", "CDOStoreImpl.remove", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource",
        "CDOStoreImpl.getResource", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.getResource", "CDOStoreImpl.setContainer");

    lock.resetLockCalls();
    company.getCategories().clear();
    assertStoreLockAcquisitions(lock, "Containment-only list clear", "CDOObjectImpl$CDOStoreEList.clear", "CDOStoreImpl.size", "CDOStoreImpl.toArray",
        "CDOStoreImpl.clear", "CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource", "CDOStoreImpl.getResource",
        "CDOStateMachine.detach", "CDOStoreImpl.isSet", "CDOStoreImpl.isSet", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainer",
        "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.size",
        "CDOTransactionImpl.detachObject", "TransactionSegment$1.put");

    lock.resetLockCalls();
    customer1.getSalesOrders().remove(salesOrder1);
    assertStoreLockAcquisitions(lock, "EStore list remove", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.indexOf", "CDOStoreImpl.get",
        "CDOStoreImpl.unset");

    lock.resetLockCalls();
    customer2.getSalesOrders().add(salesOrder1);
    assertStoreLockAcquisitions(lock, "EStore list add", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size", "CDOStoreImpl.contains", "CDOStoreImpl.get",
        "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.set");

    lock.resetLockCalls();
    company.getSalesOrders().clear();
    List<String> clearExpectedOrigins = new ArrayList<>(
        List.of("CDOObjectImpl$CDOStoreEList.clear", "CDOStoreImpl.size", "CDOStoreImpl.toArray", "CDOStoreImpl.clear"));
    List<String> clearElementOrigins = List.of("CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource",
        "CDOStoreImpl.getResource", "CDOStateMachine.detach", "CDOStoreImpl.isSet", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainer",
        "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.indexOf", "CDOStoreImpl.set",
        "CDOStoreImpl.size", "CDOTransactionImpl.detachObject", "TransactionSegment$1.put");
    clearExpectedOrigins.addAll(clearElementOrigins);
    clearExpectedOrigins.addAll(clearElementOrigins);
    assertStoreLockAcquisitions(lock, "EStore list clear", clearExpectedOrigins.toArray(new String[0]));
    assertEquals(0, company.getSalesOrders().size());
  }

  public void testRemoveAcquisitionBreakdown() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/remove-acquisition-breakdown"));
    Category directCategory = getModel1Factory().createCategory();
    Category objectCategory = getModel1Factory().createCategory();
    Category absentCategory = getModel1Factory().createCategory();
    Product1 directProduct1 = getModel1Factory().createProduct1();
    Product1 directProduct2 = getModel1Factory().createProduct1();
    Product1 objectProduct1 = getModel1Factory().createProduct1();
    Product1 objectProduct2 = getModel1Factory().createProduct1();
    Product1 absentProduct = getModel1Factory().createProduct1();
    Product1 missingProduct = getModel1Factory().createProduct1();
    Customer customer = getModel1Factory().createCustomer();
    SalesOrder salesOrder = getModel1Factory().createSalesOrder();
    Customer directOppositeCustomer = getModel1Factory().createCustomer();
    SalesOrder directOppositeSalesOrder = getModel1Factory().createSalesOrder();
    Customer reverseCustomer = getModel1Factory().createCustomer();
    SalesOrder reverseSalesOrder = getModel1Factory().createSalesOrder();
    Customer callbackCustomer = getModel1Factory().createCustomer();
    SalesOrder callbackSalesOrder = getModel1Factory().createSalesOrder();
    resource.getContents()
        .addAll(List.of(directCategory, objectCategory, absentCategory, directProduct1, directProduct2, objectProduct1, objectProduct2, absentProduct,
            missingProduct, customer, salesOrder, directOppositeCustomer, directOppositeSalesOrder, reverseCustomer, reverseSalesOrder, callbackCustomer,
            callbackSalesOrder));

    directCategory.getTopProducts().add(directProduct1);
    directCategory.getTopProducts().add(directProduct2);
    objectCategory.getTopProducts().add(objectProduct1);
    objectCategory.getTopProducts().add(objectProduct2);
    absentCategory.getTopProducts().add(absentProduct);
    salesOrder.setCustomer(customer);
    directOppositeSalesOrder.setCustomer(directOppositeCustomer);
    reverseSalesOrder.setCustomer(reverseCustomer);
    callbackSalesOrder.setCustomer(callbackCustomer);
    transaction.commit();

    EReference topProductsFeature = (EReference)((EClass)getModel1Package().getEClassifier("Category")).getEStructuralFeature("topProducts");
    EReference salesOrdersFeature = (EReference)((EClass)getModel1Package().getEClassifier("Customer")).getEStructuralFeature("salesOrders");
    EReference customerFeature = (EReference)((EClass)getModel1Package().getEClassifier("SalesOrder")).getEStructuralFeature("customer");
    assertFalse(topProductsFeature.isContainment());
    assertNull(topProductsFeature.getEOpposite());
    assertTrue(salesOrdersFeature.isMany());
    assertSame(customerFeature, salesOrdersFeature.getEOpposite());
    assertFalse(customerFeature.isMany());
    assertFalse(customerFeature.isUnsettable());

    assertSame(directProduct1, directCategory.getTopProducts().get(0));
    assertSame(directProduct2, directCategory.getTopProducts().get(1));
    assertSame(objectProduct1, objectCategory.getTopProducts().get(0));
    assertSame(objectProduct2, objectCategory.getTopProducts().get(1));
    assertSame(absentProduct, absentCategory.getTopProducts().get(0));
    assertSame(salesOrder, customer.getSalesOrders().get(0));
    assertSame(callbackSalesOrder, callbackCustomer.getSalesOrders().get(0));

    InternalCDOView view = (InternalCDOView)transaction;
    assertSame(lock, ((LockedCriticalSection)view.sync()).getLock());
    assertSame(view, CDOUtil.getCDOObject(customer).cdoView());
    assertSame(view, CDOUtil.getCDOObject(salesOrder).cdoView());
    assertSame(view, CDOUtil.getCDOObject(directOppositeCustomer).cdoView());
    assertSame(view, CDOUtil.getCDOObject(directOppositeSalesOrder).cdoView());
    assertSame(view, CDOUtil.getCDOObject(reverseCustomer).cdoView());
    assertSame(view, CDOUtil.getCDOObject(reverseSalesOrder).cdoView());
    for (EObject object : List.of(directProduct1, directProduct2, objectProduct1, objectProduct2, absentProduct, missingProduct, salesOrder,
        directOppositeSalesOrder, callbackSalesOrder))
    {
      assertNotNull(view.getObject(CDOUtil.getCDOObject(object).cdoID(), false));
    }

    List<Notification> directRemoves = new ArrayList<>();
    AtomicBoolean directNotificationHeldLock = new AtomicBoolean(true);
    directCategory.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        directNotificationHeldLock.compareAndSet(true, lock.isHeldByCurrentThread());
        if (notification.getEventType() == Notification.REMOVE)
        {
          directRemoves.add(notification);
        }
      }
    });

    lock.resetLockCalls();
    assertSame(directProduct1, directCategory.getTopProducts().remove(0));
    assertStoreLockAcquisitions(lock, "Simple reference remove(int) with notification adapter", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.isSet");
    assertEquals(List.of(directProduct2), directCategory.getTopProducts());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(directCategory).cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(directProduct1).cdoState());
    assertEquals(1, directRemoves.size());
    assertSame(directCategory, directRemoves.get(0).getNotifier());
    assertSame(topProductsFeature, directRemoves.get(0).getFeature());
    assertSame(directProduct1, directRemoves.get(0).getOldValue());
    assertEquals(0, directRemoves.get(0).getPosition());
    assertTrue("REMOVE notification callback must run under the outer list lock", directNotificationHeldLock.get());

    lock.resetLockCalls();
    assertTrue(objectCategory.getTopProducts().remove(objectProduct1));
    assertStoreLockAcquisitions(lock, "Simple reference remove(Object), present", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.indexOf");
    assertEquals(List.of(objectProduct2), objectCategory.getTopProducts());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(objectCategory).cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(objectProduct1).cdoState());

    lock.resetLockCalls();
    assertFalse(absentCategory.getTopProducts().remove(missingProduct));
    assertStoreLockAcquisitions(lock, "Simple reference remove(Object), absent", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.indexOf",
        "CDOStoreImpl.size", "CDOStoreImpl.get");
    assertEquals(List.of(absentProduct), absentCategory.getTopProducts());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(absentCategory).cdoState());

    lock.resetLockCalls();
    assertTrue(customer.getSalesOrders().remove(salesOrder));
    assertStoreLockAcquisitions(lock, "Opposite reference remove(Object)", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.indexOf", "CDOStoreImpl.get",
        "CDOStoreImpl.unset");
    assertTrue(customer.getSalesOrders().isEmpty());

    lock.resetLockCalls();
    reverseSalesOrder.setCustomer(null);
    assertStoreLockAcquisitions(lock, "Opposite-side single-reference unset", "CDOStoreImpl.get", "CDOObjectImpl$CDOStoreEList.basicRemove",
        "CDOStoreImpl.indexOf", "CDOStoreImpl.remove", "CDOStoreImpl.set");
    assertNull(reverseSalesOrder.getCustomer());
    assertTrue(reverseCustomer.getSalesOrders().isEmpty());

    lock.resetLockCalls();
    assertSame(directOppositeSalesOrder, directOppositeCustomer.getSalesOrders().remove(0));
    assertStoreLockAcquisitions(lock, "Opposite reference remove(int)", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.get", "CDOStoreImpl.unset");
    assertTrue(directOppositeCustomer.getSalesOrders().isEmpty());
    assertNull(directOppositeSalesOrder.getCustomer());

    AtomicBoolean callbackHeldLock = new AtomicBoolean(true);
    AtomicInteger notificationCount = new AtomicInteger();
    List<Notification> callbackRemoves = new ArrayList<>();
    List<String> callbackEvents = new ArrayList<>();
    AdapterImpl adapter = new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        callbackHeldLock.compareAndSet(true, lock.isHeldByCurrentThread());
        notificationCount.incrementAndGet();
        callbackEvents.add((notification.getNotifier() == callbackSalesOrder ? "SalesOrder" : "Customer") + ":" + notification.getEventType());
        if (notification.getEventType() == Notification.REMOVE && notification.getNotifier() == callbackCustomer)
        {
          callbackRemoves.add(notification);
        }
      }
    };
    callbackCustomer.eAdapters().add(adapter);
    callbackSalesOrder.eAdapters().add(adapter);

    lock.resetLockCalls();
    assertTrue(callbackCustomer.getSalesOrders().remove(callbackSalesOrder));
    assertStoreLockAcquisitions(lock, "Opposite reference remove with adapter callbacks", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.indexOf",
        "CDOStoreImpl.isSet", "CDOStoreImpl.get", "CDOStoreImpl.unset");
    assertTrue("Inverse and notification adapter callbacks must run under the outer list lock", callbackHeldLock.get());
    assertTrue("The removal must dispatch at least one notification", notificationCount.get() > 0);
    assertEquals("The indexed opposite-list removal must dispatch one REMOVE", 1, callbackRemoves.size());
    assertSame(callbackSalesOrder, callbackRemoves.get(0).getOldValue());
    assertEquals(0, callbackRemoves.get(0).getPosition());
    assertEquals(List.of("SalesOrder:" + Notification.SET, "Customer:" + Notification.REMOVE), callbackEvents);
    assertTrue(customer.getSalesOrders().isEmpty());
  }

  public void testTransientStoreListRemoval()
  {
    Category category = getModel1Factory().createCategory();
    Product1 product = getModel1Factory().createProduct1();
    assertNull(CDOUtil.getCDOObject(category).cdoView());
    assertNull(CDOUtil.getCDOObject(product).cdoView());

    assertTrue(category.getTopProducts().add(product));
    assertTrue(category.getTopProducts().remove(product));
    assertTrue(category.getTopProducts().isEmpty());
  }

  public void testAddAcquisitionBreakdown() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/add-acquisition-breakdown"));
    Category simpleAddCategory = getModel1Factory().createCategory();
    Product1 simpleAddProduct = getModel1Factory().createProduct1();
    Category duplicateCategory = getModel1Factory().createCategory();
    Product1 duplicateProduct = getModel1Factory().createProduct1();
    Category indexedCategory = getModel1Factory().createCategory();
    Product1 indexedExistingProduct = getModel1Factory().createProduct1();
    Product1 indexedNewProduct = getModel1Factory().createProduct1();
    Category invalidCategory = getModel1Factory().createCategory();
    Category directUniqueCategory = getModel1Factory().createCategory();
    Product1 directUniqueProduct = getModel1Factory().createProduct1();
    Category directIndexedUniqueCategory = getModel1Factory().createCategory();
    Product1 directIndexedUniqueProduct = getModel1Factory().createProduct1();
    Customer oppositeCustomer = getModel1Factory().createCustomer();
    SalesOrder oppositeSalesOrder = getModel1Factory().createSalesOrder();
    Customer directOppositeCustomer = getModel1Factory().createCustomer();
    SalesOrder directOppositeSalesOrder = getModel1Factory().createSalesOrder();
    Customer reverseCustomer = getModel1Factory().createCustomer();
    SalesOrder reverseSalesOrder = getModel1Factory().createSalesOrder();
    Customer callbackCustomer = getModel1Factory().createCustomer();
    SalesOrder callbackSalesOrder = getModel1Factory().createSalesOrder();
    resource.getContents()
        .addAll(List.of(simpleAddCategory, simpleAddProduct, duplicateCategory, duplicateProduct, indexedCategory, indexedExistingProduct, indexedNewProduct,
            invalidCategory, directUniqueCategory, directUniqueProduct, directIndexedUniqueCategory, directIndexedUniqueProduct, oppositeCustomer,
            oppositeSalesOrder, directOppositeCustomer, directOppositeSalesOrder, reverseCustomer, reverseSalesOrder, callbackCustomer, callbackSalesOrder));
    duplicateCategory.getTopProducts().add(duplicateProduct);
    indexedCategory.getTopProducts().add(indexedExistingProduct);
    transaction.commit();

    InternalCDOView view = (InternalCDOView)transaction;
    EReference topProductsFeature = (EReference)((EClass)getModel1Package().getEClassifier("Category")).getEStructuralFeature("topProducts");
    assertTrue(topProductsFeature.isMany());
    assertTrue(topProductsFeature.isUnique());
    assertFalse(topProductsFeature.isContainment());
    assertNull(topProductsFeature.getEOpposite());
    assertSame(lock, ((LockedCriticalSection)view.sync()).getLock());
    assertSame(view, CDOUtil.getCDOObject(simpleAddCategory).cdoView());
    assertSame(view, CDOUtil.getCDOObject(simpleAddProduct).cdoView());
    assertSame(view, CDOUtil.getCDOObject(oppositeCustomer).cdoView());
    assertSame(view, CDOUtil.getCDOObject(oppositeSalesOrder).cdoView());
    assertEquals(List.of(duplicateProduct), duplicateCategory.getTopProducts());
    assertEquals(List.of(indexedExistingProduct), indexedCategory.getTopProducts());
    assertTrue(simpleAddCategory.getTopProducts().isEmpty());
    assertTrue(invalidCategory.getTopProducts().isEmpty());
    assertTrue(oppositeCustomer.getSalesOrders().isEmpty());
    assertTrue(reverseCustomer.getSalesOrders().isEmpty());
    assertTrue(callbackCustomer.getSalesOrders().isEmpty());
    assertNull(oppositeSalesOrder.getCustomer());

    lock.resetLockCalls();
    assertTrue(simpleAddCategory.getTopProducts().add(simpleAddProduct));
    assertStoreLockAcquisitions(lock, "Simple persistent add(Object)", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size", "CDOStoreImpl.contains",
        "CDOStoreImpl.size");
    assertEquals(List.of(simpleAddProduct), simpleAddCategory.getTopProducts());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(simpleAddCategory).cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(simpleAddProduct).cdoState());

    List<Notification> duplicateAdds = new ArrayList<>();
    duplicateCategory.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        if (notification.getEventType() == Notification.ADD)
        {
          duplicateAdds.add(notification);
        }
      }
    });
    lock.resetLockCalls();
    assertFalse(duplicateCategory.getTopProducts().add(duplicateProduct));
    assertStoreLockAcquisitions(lock, "Duplicate simple persistent add(Object)", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size",
        "CDOStoreImpl.contains");
    assertEquals(List.of(duplicateProduct), duplicateCategory.getTopProducts());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(duplicateCategory).cdoState());
    assertTrue("A duplicate add must not dispatch an ADD notification", duplicateAdds.isEmpty());

    lock.resetLockCalls();
    indexedCategory.getTopProducts().add(1, indexedNewProduct);
    assertStoreLockAcquisitions(lock, "Simple persistent indexed add", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size", "CDOStoreImpl.size",
        "CDOStoreImpl.contains", "CDOStoreImpl.get");
    assertEquals(List.of(indexedExistingProduct, indexedNewProduct), indexedCategory.getTopProducts());

    lock.resetLockCalls();
    try
    {
      invalidCategory.getTopProducts().add(1, simpleAddProduct);
      fail("An indexed add beyond size must fail");
    }
    catch (IndexOutOfBoundsException expected)
    {
      // Expected.
    }
    assertStoreLockAcquisitions(lock, "Invalid simple persistent indexed add", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size");
    assertTrue(invalidCategory.getTopProducts().isEmpty());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(invalidCategory).cdoState());

    lock.resetLockCalls();
    ((InternalEList<Product1>)directUniqueCategory.getTopProducts()).addUnique(directUniqueProduct);
    assertStoreLockAcquisitions(lock, "Direct simple addUnique(Object)", "CDOObjectImpl$CDOStoreEList.addUnique", "CDOStoreImpl.size");
    assertEquals(List.of(directUniqueProduct), directUniqueCategory.getTopProducts());

    lock.resetLockCalls();
    ((InternalEList<Product1>)directIndexedUniqueCategory.getTopProducts()).addUnique(0, directIndexedUniqueProduct);
    assertStoreLockAcquisitions(lock, "Direct simple addUnique(int, Object)", "CDOObjectImpl$CDOStoreEList.addUnique");
    assertEquals(List.of(directIndexedUniqueProduct), directIndexedUniqueCategory.getTopProducts());

    lock.resetLockCalls();
    assertTrue(oppositeCustomer.getSalesOrders().add(oppositeSalesOrder));
    assertStoreLockAcquisitions(lock, "Opposite persistent add(Object)", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size", "CDOStoreImpl.contains",
        "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.set");
    assertEquals(List.of(oppositeSalesOrder), oppositeCustomer.getSalesOrders());
    assertSame(oppositeCustomer, oppositeSalesOrder.getCustomer());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(oppositeCustomer).cdoState());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(oppositeSalesOrder).cdoState());

    lock.resetLockCalls();
    ((InternalEList<SalesOrder>)directOppositeCustomer.getSalesOrders()).addUnique(directOppositeSalesOrder);
    assertStoreLockAcquisitions(lock, "Direct opposite addUnique(Object)", "CDOObjectImpl$CDOStoreEList.addUnique", "CDOStoreImpl.size", "CDOStoreImpl.get",
        "CDOStoreImpl.set");
    assertEquals(List.of(directOppositeSalesOrder), directOppositeCustomer.getSalesOrders());
    assertSame(directOppositeCustomer, directOppositeSalesOrder.getCustomer());

    lock.resetLockCalls();
    reverseSalesOrder.setCustomer(reverseCustomer);
    assertStoreLockAcquisitions(lock, "Reverse-direction opposite setCustomer", "CDOStoreImpl.get", "CDOObjectImpl$CDOStoreEList.basicAdd", "CDOStoreImpl.size",
        "CDOStoreImpl.set");
    assertSame(reverseCustomer, reverseSalesOrder.getCustomer());
    assertEquals(List.of(reverseSalesOrder), reverseCustomer.getSalesOrders());

    AtomicBoolean callbackHeldLock = new AtomicBoolean(true);
    List<String> callbackEvents = new ArrayList<>();
    AdapterImpl adapter = new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification notification)
      {
        callbackHeldLock.compareAndSet(true, lock.isHeldByCurrentThread());
        callbackEvents.add((notification.getNotifier() == callbackSalesOrder ? "SalesOrder" : "Customer") + ":" + notification.getEventType());
      }
    };
    callbackCustomer.eAdapters().add(adapter);
    callbackSalesOrder.eAdapters().add(adapter);

    lock.resetLockCalls();
    assertTrue(callbackCustomer.getSalesOrders().add(callbackSalesOrder));
    assertStoreLockAcquisitions(lock, "Opposite persistent add(Object) with adapters", "CDOObjectImpl$CDOStoreEList.add", "CDOStoreImpl.size",
        "CDOStoreImpl.contains", "CDOStoreImpl.size", "CDOStoreImpl.isSet", "CDOStoreImpl.get", "CDOStoreImpl.set");
    assertSame(callbackCustomer, callbackSalesOrder.getCustomer());
    assertEquals(List.of("SalesOrder:" + Notification.SET, "Customer:" + Notification.ADD), callbackEvents);
    assertTrue("Inverse and notification adapter callbacks must run under the outer list lock", callbackHeldLock.get());
  }

  public void testBulkListMutationAcquisitionInventory() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/bulk-list-mutation-acquisition-inventory"));
    Category addAllCategory = getModel1Factory().createCategory();
    Product1 addAllProduct1 = getModel1Factory().createProduct1();
    Product1 addAllProduct2 = getModel1Factory().createProduct1();
    Category indexedAddAllCategory = getModel1Factory().createCategory();
    Product1 indexedExistingProduct = getModel1Factory().createProduct1();
    Product1 indexedAddAllProduct1 = getModel1Factory().createProduct1();
    Product1 indexedAddAllProduct2 = getModel1Factory().createProduct1();
    Category addAllUniqueCategory = getModel1Factory().createCategory();
    Product1 addAllUniqueProduct1 = getModel1Factory().createProduct1();
    Product1 addAllUniqueProduct2 = getModel1Factory().createProduct1();
    Category removeAllCategory = getModel1Factory().createCategory();
    Product1 removeAllProduct1 = getModel1Factory().createProduct1();
    Product1 removeAllProduct2 = getModel1Factory().createProduct1();
    Category retainAllCategory = getModel1Factory().createCategory();
    Product1 retainAllProduct1 = getModel1Factory().createProduct1();
    Product1 retainAllProduct2 = getModel1Factory().createProduct1();
    Customer oppositeAddAllCustomer = getModel1Factory().createCustomer();
    SalesOrder oppositeAddAllOrder1 = getModel1Factory().createSalesOrder();
    SalesOrder oppositeAddAllOrder2 = getModel1Factory().createSalesOrder();
    Customer oppositeRemoveAllCustomer = getModel1Factory().createCustomer();
    SalesOrder oppositeRemoveAllOrder1 = getModel1Factory().createSalesOrder();
    SalesOrder oppositeRemoveAllOrder2 = getModel1Factory().createSalesOrder();
    Customer oppositeRetainAllCustomer = getModel1Factory().createCustomer();
    SalesOrder oppositeRetainAllOrder1 = getModel1Factory().createSalesOrder();
    SalesOrder oppositeRetainAllOrder2 = getModel1Factory().createSalesOrder();

    indexedAddAllCategory.getTopProducts().add(indexedExistingProduct);
    removeAllCategory.getTopProducts().addAll(List.of(removeAllProduct1, removeAllProduct2));
    retainAllCategory.getTopProducts().addAll(List.of(retainAllProduct1, retainAllProduct2));
    oppositeRemoveAllCustomer.getSalesOrders().addAll(List.of(oppositeRemoveAllOrder1, oppositeRemoveAllOrder2));
    oppositeRetainAllCustomer.getSalesOrders().addAll(List.of(oppositeRetainAllOrder1, oppositeRetainAllOrder2));
    resource.getContents().addAll(List.of(addAllCategory, addAllProduct1, addAllProduct2, indexedAddAllCategory, indexedExistingProduct, indexedAddAllProduct1,
        indexedAddAllProduct2, addAllUniqueCategory, addAllUniqueProduct1, addAllUniqueProduct2, removeAllCategory, removeAllProduct1, removeAllProduct2,
        retainAllCategory, retainAllProduct1, retainAllProduct2, oppositeAddAllCustomer, oppositeAddAllOrder1, oppositeAddAllOrder2, oppositeRemoveAllCustomer,
        oppositeRemoveAllOrder1, oppositeRemoveAllOrder2, oppositeRetainAllCustomer, oppositeRetainAllOrder1, oppositeRetainAllOrder2));
    transaction.commit();

    lock.resetLockCalls();
    assertTrue(addAllCategory.getTopProducts().addAll(List.of(addAllProduct1, addAllProduct2)));
    assertStoreLockAcquisitions(lock, "Inventory simple addAll(Collection)", "CDOObjectImpl$CDOStoreEList.addAll", "CDOStoreImpl.size", "CDOStoreImpl.size",
        "CDOObjectImpl$CDOStoreEList.addAllUnique", "CDOStoreImpl.size", "CDOObjectImpl$CDOStoreEList.addAllUnique");

    lock.resetLockCalls();
    assertTrue(indexedAddAllCategory.getTopProducts().addAll(1, List.of(indexedAddAllProduct1, indexedAddAllProduct2)));
    assertStoreLockAcquisitions(lock, "Inventory simple addAll(index, Collection)", "CDOObjectImpl$CDOStoreEList.addAll", "CDOStoreImpl.size",
        "CDOStoreImpl.size", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOObjectImpl$CDOStoreEList.addAllUnique");

    lock.resetLockCalls();
    assertTrue(((InternalEList<Product1>)addAllUniqueCategory.getTopProducts()).addAllUnique(List.of(addAllUniqueProduct1, addAllUniqueProduct2)));
    assertStoreLockAcquisitions(lock, "Inventory simple addAllUnique(Collection)", "CDOObjectImpl$CDOStoreEList.addAllUnique", "CDOStoreImpl.size",
        "CDOObjectImpl$CDOStoreEList.addAllUnique");

    lock.resetLockCalls();
    assertTrue(oppositeAddAllCustomer.getSalesOrders().addAll(List.of(oppositeAddAllOrder1, oppositeAddAllOrder2)));
    assertStoreLockAcquisitions(lock, "Inventory opposite addAll(Collection)", "CDOObjectImpl$CDOStoreEList.addAll", "CDOStoreImpl.size", "CDOStoreImpl.size",
        "CDOObjectImpl$CDOStoreEList.addAllUnique", "CDOStoreImpl.size", "CDOObjectImpl$CDOStoreEList.addAllUnique", "CDOStoreImpl.get",
        "CDOObjectImpl$CDOStoreEList.inverseAdd", "CDOStoreImpl.get", "CDOStoreImpl.set", "CDOStoreImpl.get", "CDOObjectImpl$CDOStoreEList.inverseAdd",
        "CDOStoreImpl.get", "CDOStoreImpl.set");

    lock.resetLockCalls();
    assertTrue(removeAllCategory.getTopProducts().removeAll(List.of(removeAllProduct1, removeAllProduct2)));
    assertStoreLockAcquisitions(lock, "Inventory simple removeAll(Collection)", "CDOObjectImpl$CDOStoreEList.removeAll", "CDOStoreImpl.isSet",
        "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.size", "CDOStoreImpl.get",
        "CDOStoreImpl.remove", "CDOStoreImpl.get", "CDOStoreImpl.remove");

    lock.resetLockCalls();
    assertTrue(retainAllCategory.getTopProducts().retainAll(List.of(retainAllProduct1)));
    assertStoreLockAcquisitions(lock, "Inventory simple retainAll(Collection)", "CDOObjectImpl$CDOStoreEList.retainAll", "CDOStoreImpl.size",
        "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.size");

    lock.resetLockCalls();
    assertTrue(oppositeRemoveAllCustomer.getSalesOrders().removeAll(List.of(oppositeRemoveAllOrder1, oppositeRemoveAllOrder2)));
    assertStoreLockAcquisitions(lock, "Inventory opposite removeAll(Collection)", "CDOObjectImpl$CDOStoreEList.removeAll", "CDOStoreImpl.isSet",
        "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.size", "CDOStoreImpl.get",
        "CDOStoreImpl.remove", "CDOStoreImpl.get", "CDOStoreImpl.remove", "CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.get", "CDOStoreImpl.unset",
        "CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.get", "CDOStoreImpl.unset");

    lock.resetLockCalls();
    assertTrue(oppositeRetainAllCustomer.getSalesOrders().retainAll(List.of(oppositeRetainAllOrder1)));
    assertStoreLockAcquisitions(lock, "Inventory opposite retainAll(Collection)", "CDOObjectImpl$CDOStoreEList.retainAll", "CDOStoreImpl.size",
        "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOObjectImpl$CDOStoreEList.remove", "CDOStoreImpl.get", "CDOStoreImpl.unset",
        "CDOStoreImpl.size");
  }

  public void testStoreManyValuedUnsetAcquisitionCount() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/unset-many-lock-count"));
    Company company = getModel1Factory().createCompany();
    company.getCustomers().add(getModel1Factory().createCustomer());
    company.getCustomers().add(getModel1Factory().createCustomer());
    resource.getContents().add(company);
    transaction.commit();

    EStructuralFeature customersFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("customers");
    lock.resetLockCalls();
    company.eUnset(customersFeature);
    List<String> unsetExpectedOrigins = new ArrayList<>(List.of("CDOObjectImpl$CDOStoreEList.unset", "CDOStoreImpl.unset", "CDOObjectImpl$CDOStoreEList.clear",
        "CDOStoreImpl.size", "CDOStoreImpl.toArray", "CDOStoreImpl.clear"));
    List<String> detachedCustomerOrigins = List.of("CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource",
        "CDOStoreImpl.getResource", "CDOStateMachine.detach", "CDOStoreImpl.isSet", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainer",
        "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.size",
        "CDOTransactionImpl.detachObject", "TransactionSegment$1.put");
    unsetExpectedOrigins.addAll(detachedCustomerOrigins);
    unsetExpectedOrigins.addAll(detachedCustomerOrigins);
    assertStoreLockAcquisitions(lock, "EStore many-valued unset", unsetExpectedOrigins.toArray(new String[0]));
  }

  public void testClearAndUnsetAcquisitionScaling() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOTransaction transaction;
    try
    {
      transaction = openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = transaction.createResource(getResourcePath("/list-lock-scaling"));
    Company[] clearCompanies = new Company[4];
    Company[] unsetCompanies = new Company[4];
    Customer[][] customers = new Customer[4][];
    for (int size = 0; size < 4; size++)
    {
      clearCompanies[size] = getModel1Factory().createCompany();
      unsetCompanies[size] = getModel1Factory().createCompany();
      customers[size] = new Customer[size];
      Customer orderCustomer = getModel1Factory().createCustomer();
      resource.getContents().add(orderCustomer);

      for (int i = 0; i < size; i++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setCustomer(orderCustomer);
        clearCompanies[size].getSalesOrders().add(salesOrder);
        customers[size][i] = getModel1Factory().createCustomer();
        unsetCompanies[size].getCustomers().add(customers[size][i]);
      }

      resource.getContents().add(clearCompanies[size]);
      resource.getContents().add(unsetCompanies[size]);
    }

    transaction.commit();

    List<String> clearPrefix = List.of("CDOObjectImpl$CDOStoreEList.clear", "CDOStoreImpl.size", "CDOStoreImpl.toArray", "CDOStoreImpl.clear");
    List<String> clearElement = List.of("CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource",
        "CDOStoreImpl.getResource", "CDOStateMachine.detach", "CDOStoreImpl.isSet", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainer",
        "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.size", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.indexOf", "CDOStoreImpl.set",
        "CDOStoreImpl.size", "CDOTransactionImpl.detachObject", "TransactionSegment$1.put");

    for (int size = 0; size < 4; size++)
    {
      lock.resetLockCalls();
      clearCompanies[size].getSalesOrders().clear();
      List<String> expected = new ArrayList<>(clearPrefix);
      if (size == 0)
      {
        expected.add(2, "CDOStoreImpl.size");
      }

      for (int i = 0; i < size; i++)
      {
        expected.addAll(clearElement);
      }

      assertStoreLockAcquisitions(lock, "SalesOrder clear with " + size + " elements", expected.toArray(new String[0]));
    }

    EStructuralFeature customersFeature = ((EClass)getModel1Package().getEClassifier("Company")).getEStructuralFeature("customers");
    List<String> unsetPrefix = List.of("CDOObjectImpl$CDOStoreEList.unset", "CDOStoreImpl.unset", "CDOObjectImpl$CDOStoreEList.clear", "CDOStoreImpl.size",
        "CDOStoreImpl.toArray", "CDOStoreImpl.clear");
    List<String> unsetElement = List.of("CDOObjectImpl$CDOStoreEList.inverseRemove", "CDOStoreImpl.getContainer", "CDOStoreImpl.getResource",
        "CDOStoreImpl.getResource", "CDOStateMachine.detach", "CDOStoreImpl.isSet", "CDOStoreImpl.getResource", "CDOStoreImpl.getContainer",
        "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.get", "CDOStoreImpl.size", "CDOStoreImpl.size",
        "CDOTransactionImpl.detachObject", "TransactionSegment$1.put");

    for (int size = 0; size < 4; size++)
    {
      lock.resetLockCalls();
      unsetCompanies[size].eUnset(customersFeature);
      List<String> expected = new ArrayList<>(unsetPrefix);
      if (size == 0)
      {
        expected.add(4, "CDOStoreImpl.size");
      }

      for (int i = 0; i < size; i++)
      {
        expected.addAll(unsetElement);
      }

      assertStoreLockAcquisitions(lock, "Customer unset with " + size + " elements", expected.toArray(new String[0]));
    }
  }

  public void testColdObjectLoadAcquisitionCount() throws Exception
  {
    skipTest(!getModelConfig().isNative());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/cold-object-load-lock-count"));
    Company company = getModel1Factory().createCompany();
    company.setName("cold-object");
    resource.getContents().add(company);
    transaction.commit();
    CDOID id = CDOUtil.getCDOObject(company).cdoID();

    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    CDOView view;
    try
    {
      view = session.openView();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    lock.resetLockCalls();
    assertNotNull(view.getObject(id));
    assertStoreLockAcquisitions(lock, "Cold object load", "AbstractCDOView.getObject", "CDOViewImpl.getRevision", "AbstractCDOView.newInstance",
        "AbstractCDOView.cleanObject", "AbstractCDOView.registerObject", "CDOViewImpl$OptionsImpl.isLoadNotificationEnabled", "CDOStoreImpl.getResource",
        "CDOViewImpl.getRevision", "AbstractCDOView.getObject", "CDOViewImpl.getRevision", "AbstractCDOView.newInstance", "AbstractCDOView.cleanObject",
        "AbstractCDOView.registerObject", "CDOViewImpl$OptionsImpl.isLoadNotificationEnabled", "CDOStoreImpl.getResource", "CDOViewImpl.getRevision",
        "AbstractCDOView.newInstance", "AbstractCDOView.cleanObject", "CDOViewImpl$OptionsImpl.isLoadNotificationEnabled", "AbstractCDOView.registerObject",
        "CDOStoreImpl.getContainer", "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.get", "AbstractCDOView.getResource", "AbstractCDOView.attachResource",
        "AbstractCDOView.registerProxyResource2", "AbstractCDOView.getResourceNodeID", "AbstractCDOView.getResourceNodeID", "CDOViewImpl.getRevision",
        "AbstractCDOView.registerObject", "AbstractCDOView.cleanObject", "CDOViewImpl$OptionsImpl.isLoadNotificationEnabled", "AbstractCDOView.getObject",
        "CDOStoreImpl.getContainer", "CDOStoreImpl.getContainerFeatureID", "CDOStoreImpl.get");
  }

  private void assertStoreLockAcquisitions(CountingLock lock, String description, String... origins)
  {
    assertEquals(description + " acquisition count; origins=" + lock.getOrigins(), origins.length, lock.getLockCalls());
    assertEquals(description + " acquisition origins", List.of(origins), lock.getOrigins());
  }

  public void testIndependentGetCleanRevisionsAcquiresViewLock()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    InternalCDOTransaction transaction = (InternalCDOTransaction)view;
    int lockCalls = lock.getLockCalls();
    transaction.getCleanRevisions();
    assertEquals("Independent getCleanRevisions() must acquire the view lock", lockCalls + 1, lock.getLockCalls());
  }

  public void testIndependentRegisterFeatureDeltaAcquiresViewLockOnce() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = ((CDOTransaction)view).createResource(getResourcePath("/independent-register-delta-two"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    assertEquals(CDOState.NEW, cdoObject.cdoState());

    InternalCDOTransaction transaction = (InternalCDOTransaction)view;
    int lockCalls = lock.getLockCalls();
    transaction.registerFeatureDelta(cdoObject, null);
    assertEquals("Independent two-argument registerFeatureDelta() must acquire the view lock once", lockCalls + 1, lock.getLockCalls());
  }

  public void testIndependentRegisterFeatureDeltaWithCleanRevisionAcquiresViewLockOnce() throws Exception
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    InternalCDOView view;
    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    CDOResource resource = ((CDOTransaction)view).createResource(getResourcePath("/independent-register-delta-three"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(company);
    assertEquals(CDOState.NEW, cdoObject.cdoState());

    InternalCDOTransaction transaction = (InternalCDOTransaction)view;
    int lockCalls = lock.getLockCalls();
    transaction.registerFeatureDelta(cdoObject, null, null);
    assertEquals("Independent three-argument registerFeatureDelta() must acquire the view lock once", lockCalls + 1, lock.getLockCalls());
  }

  public void testDelegableViewLockIsPreserved()
  {
    CDOSession session = openSession();
    session.options().setDelegableViewLockEnabled(true);
    InternalCDOView view = (InternalCDOView)session.openTransaction();
    assertTrue(((LockedCriticalSection)view.sync()).getLock() instanceof DelegableReentrantLock);
  }

  public void testLexicalAccessPreservesCheckedExceptionAndUnlocks() throws Exception
  {
    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    IOException expected = new IOException("expected");

    try (Access access = view.access())
    {
      throw expected;
    }
    catch (IOException actual)
    {
      assertSame(expected, actual);
    }

    assertFalse(((NonFairReentrantLock)((LockedCriticalSection)view.sync()).getLock()).isLocked());
  }

  public void testMonitorSafetyCheckAndDeprecatedMethods()
  {
    InternalCDOView view = (InternalCDOView)openSession().openTransaction();

    synchronized (view)
    {
      try
      {
        view.access();
        fail("Intrinsic monitor use must be rejected by internal lexical access");
      }
      catch (UnsupportedOperationException expected)
      {
        assertTrue(expected.getMessage().contains("CDOView.sync()"));
      }

      try
      {
        view.sync().run(() -> fail("The callback must not run while the intrinsic monitor is held"));
        fail("Intrinsic monitor use must be rejected by public sync()");
      }
      catch (UnsupportedOperationException expected)
      {
        assertTrue(expected.getMessage().contains("CDOView.sync()"));
      }
    }

    assertDeprecatedLockingMethodFails(view, 0);
    assertDeprecatedLockingMethodFails(view, 1);
    assertDeprecatedLockingMethodFails(view, 2);
    assertDeprecatedLockingMethodFails(view, 3);
  }

  public void testMonitorSafetyCheckRunsBeforeLockAcquisition()
  {
    CountingLock lock = new CountingLock();
    CDOUtil.setNextViewLock(lock);

    try
    {
      InternalCDOView view = (InternalCDOView)openSession().openTransaction();
      assertSame(lock, ((LockedCriticalSection)view.sync()).getLock());
      int lockCalls = lock.getLockCalls();

      synchronized (view)
      {
        try
        {
          view.access();
          fail("Lexical access must be rejected before acquiring the view lock");
        }
        catch (UnsupportedOperationException expected)
        {
          assertTrue(expected.getMessage().contains("CDOView.sync()"));
        }

        try
        {
          view.sync().run(() -> fail("Public sync callback must not run while holding the intrinsic monitor"));
          fail("Public sync must reject intrinsic-monitor use before acquiring the view lock");
        }
        catch (UnsupportedOperationException expected)
        {
          assertTrue(expected.getMessage().contains("CDOView.sync()"));
        }
      }

      assertEquals(lockCalls, lock.getLockCalls());
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }
  }

  public void testInterruptedUpdateWaitPreservesWrapperAndInterrupt()
  {
    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    long updateTime = view.getLastUpdateTime() + 1;
    AtomicReference<Throwable> failure = new AtomicReference<>();
    AtomicReference<Boolean> interrupted = new AtomicReference<>(Boolean.FALSE);

    Thread waiter = new Thread(() -> {
      try
      {
        view.waitForUpdate(updateTime, 60000L);
      }
      catch (Throwable ex)
      {
        failure.set(ex);
        interrupted.set(Thread.currentThread().isInterrupted());
      }
    });

    waiter.start();
    waiter.interrupt();

    try
    {
      waiter.join(5000L);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      fail("Test thread was interrupted");
    }

    assertFalse(waiter.isAlive());
    assertTrue(failure.get() instanceof WrappedException);
    assertTrue(failure.get().getCause() instanceof InterruptedException);
    assertTrue(interrupted.get());
  }

  public void testUpdateConditionReleasesAndReacquiresViewLock()
  {
    AwaitObservedLock lock = new AwaitObservedLock();
    CDOUtil.setNextViewLock(lock);
    InternalCDOView view;

    try
    {
      view = (InternalCDOView)openSession().openTransaction();
    }
    finally
    {
      CDOUtil.setNextViewLock(null);
    }

    long updateTime = view.getLastUpdateTime() + 1;
    AtomicReference<Boolean> result = new AtomicReference<>();

    Thread waiter = new Thread(() -> result.set(view.waitForUpdate(updateTime, 30000L)));
    waiter.start();

    boolean awaitEntered = false;

    try
    {
      awaitEntered = lock.awaitEntered.await(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      fail("Test thread was interrupted");
    }

    view.setLastUpdateTime(updateTime);

    try
    {
      waiter.join(5000L);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
      fail("Test thread was interrupted");
    }

    assertFalse(waiter.isAlive());
    assertTrue("Condition.await() was not reached", awaitEntered);
    assertEquals(Boolean.TRUE, result.get());
  }

  @SuppressWarnings("deprecation")
  public void testLegacyLockingCompatibilityEnabled()
  {
    if (!Boolean.getBoolean("org.eclipse.emf.cdo.view.ENABLE_LEGACY_LOCKING_API"))
    {
      return;
    }

    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    Object monitor = view.getViewMonitor();
    assertNotSame(view, monitor);
    assertSame(monitor, view.getViewMonitor());

    Lock lock = view.getViewLock();
    view.lockView();

    try
    {
      assertTrue(((NonFairReentrantLock)lock).isHeldByCurrentThread());
    }
    finally
    {
      view.unlockView();
    }
  }

  public void testIntrinsicMonitorSafetyCheckCanBeDisabled()
  {
    if (!Boolean.getBoolean("org.eclipse.emf.cdo.view.DISABLE_INTRINSIC_MONITOR_CHECK"))
    {
      return;
    }

    InternalCDOView view = (InternalCDOView)openSession().openTransaction();
    synchronized (view)
    {
      view.sync().run(() -> {
        try (Access access = view.access())
        {
          assertSame(((LockedCriticalSection)view.sync()).getLock(), access.getLock());
        }
      });
    }
  }

  @SuppressWarnings("deprecation")
  private void assertDeprecatedLockingMethodFails(InternalCDOView view, int method)
  {
    try
    {
      switch (method)
      {
      case 0:
        view.getViewMonitor();
        break;

      case 1:
        view.getViewLock();
        break;

      case 2:
        view.lockView();
        break;

      default:
        view.unlockView();
        break;
      }

      fail("Deprecated view locking API must be disabled by default");
    }
    catch (UnsupportedOperationException expected)
    {
      assertTrue(expected.getMessage().contains("ENABLE_LEGACY_LOCKING_API"));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CountingLock extends NonFairReentrantLock
  {
    private static final long serialVersionUID = 1L;

    private final AtomicInteger lockCalls = new AtomicInteger();

    private final List<String> origins = new ArrayList<>();

    @Override
    public void lock()
    {
      lockCalls.incrementAndGet();
      for (StackTraceElement element : Thread.currentThread().getStackTrace())
      {
        String className = element.getClassName();
        if (className.startsWith("org.eclipse.emf.internal.cdo.") && !"access".equals(element.getMethodName()))
        {
          origins.add(className.substring(className.lastIndexOf('.') + 1) + "." + element.getMethodName());
          break;
        }
      }

      super.lock();
    }

    public int getLockCalls()
    {
      return lockCalls.get();
    }

    public void resetLockCalls()
    {
      lockCalls.set(0);
      origins.clear();
    }

    public List<String> getOrigins()
    {
      return origins;
    }

  }

  /**
   * @author Eike Stepper
   */
  private static final class TestFeatureAnalyzer implements CDOFeatureAnalyzer
  {
    @Override
    public void preTraverseFeature(CDOObject object, EStructuralFeature feature, int index)
    {
      // Do nothing.
    }

    @Override
    public void postTraverseFeature(CDOObject object, EStructuralFeature feature, int index, Object value)
    {
      // Do nothing.
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AwaitObservedLock extends NonFairReentrantLock
  {
    private static final long serialVersionUID = 1L;

    private final CountDownLatch awaitEntered = new CountDownLatch(1);

    @Override
    public Condition newCondition()
    {
      Condition delegate = super.newCondition();
      return new Condition()
      {
        @Override
        public void await() throws InterruptedException
        {
          delegate.await();
        }

        @Override
        public void awaitUninterruptibly()
        {
          delegate.awaitUninterruptibly();
        }

        @Override
        public long awaitNanos(long nanosTimeout) throws InterruptedException
        {
          return delegate.awaitNanos(nanosTimeout);
        }

        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException
        {
          awaitEntered.countDown();
          return delegate.await(time, unit);
        }

        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException
        {
          return delegate.awaitUntil(deadline);
        }

        @Override
        public void signal()
        {
          delegate.signal();
        }

        @Override
        public void signalAll()
        {
          delegate.signalAll();
        }
      };
    }
  }
}
