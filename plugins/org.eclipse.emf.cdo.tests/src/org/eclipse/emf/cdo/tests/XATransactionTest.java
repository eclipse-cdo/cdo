/*
 * Copyright (c) 2008-2013, 2016 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.internal.cdo.session.SessionUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Date;

/**
 * @author Simon McDuff
 */
public class XATransactionTest extends AbstractCDOTest
{
  static final public String REPOSITORY2_NAME = "repo2";

  public void testRollback() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    SessionUtil.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
    CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    supplier.getPurchaseOrders().add(purchaseOrder);
    resB.getContents().add(supplier);
    resA.getContents().add(purchaseOrder);

    assertNew(resA, transactionA1);
    assertNew(resB, transactionB1);

    xaTransaction.rollback();

    assertTransient(resA);
    assertTransient(resB);

    xaTransaction.commit();
  }

  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testRollback_AfterSetpoint() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    SessionUtil.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
    CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    supplier.getPurchaseOrders().add(purchaseOrder);
    resB.getContents().add(supplier);
    resA.getContents().add(purchaseOrder);

    assertNew(resA, transactionA1);
    assertNew(resB, transactionB1);

    CDOUserSavepoint savepoint1 = xaTransaction.setSavepoint();

    purchaseOrder.setDate(new Date());
    supplier.setCity("OTTAWA");

    CDOUserSavepoint savepoint2 = xaTransaction.setSavepoint();

    savepoint1.rollback();

    assertNew(resA, transactionA1);
    assertNew(resB, transactionB1);

    assertEquals(null, supplier.getCity());
    assertEquals(null, purchaseOrder.getDate());
    assertEquals(supplier, purchaseOrder.getSupplier());

    try
    {
      savepoint2.rollback();
      fail("Should have an exception");
    }
    catch (Exception ex)
    {
    }

    xaTransaction.commit();
  }

  // XXX disabled because of Bug 290097
  @Skips("Postgresql")
  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testCommitFromTransactionDisabled() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    {
      CDOSession sessionA = openSession();
      sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionA.getPackageRegistry().putEPackage(getModel4Package());

      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      sessionB.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionB.getPackageRegistry().putEPackage(getModel4Package());

      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
      CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

      ((InternalCDOTransaction)transactionA1).setTransactionStrategy(null);
      GenRefSingleNonContained objectFromResA = getModel4Factory().createGenRefSingleNonContained();
      GenRefSingleNonContained objectFromResB = getModel4Factory().createGenRefSingleNonContained();

      objectFromResA.setElement(objectFromResB);
      resA.getContents().add(objectFromResA);
      resB.getContents().add(objectFromResB);

      CDOXATransaction transSet = CDOUtil.createXATransaction();
      transSet.setAllowRequestFromTransactionEnabled(false);
      transSet.add(CDOUtil.getViewSet(resourceSet));

      try
      {
        transactionA1.commit();
        fail("CommitException expected");
      }
      catch (CommitException exp)
      {
      }

      transSet.commit();
    }
  }

  // Skip this test until the problems with XATransactions are solved.
  @Skips({ IModelConfig.CAPABILITY_LEGACY, IRepositoryConfig.CAPABILITY_UUIDS, "DB" })
  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testNotUsingXATransaction_Exception() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    {
      CDOSession sessionA = openSession();
      sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionA.getPackageRegistry().putEPackage(getModel4Package());

      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      sessionB.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionB.getPackageRegistry().putEPackage(getModel4Package());

      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transactionA = sessionA.openTransaction(resourceSet);
      CDOTransaction transactionB = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA.createResource(getResourcePath("/resA"));
      CDOResource resB = transactionB.createResource(getResourcePath("/resB"));
      GenRefSingleNonContained objectFromResA = getModel4Factory().createGenRefSingleNonContained();
      GenRefSingleNonContained objectFromResB = getModel4Factory().createGenRefSingleNonContained();

      objectFromResA.setElement(objectFromResB);
      resA.getContents().add(objectFromResA);
      resB.getContents().add(objectFromResB);

      try
      {
        transactionA.commit(); // Would not fail with IDGenerationLocation.CLIENT!
        fail("CommitException expected");
      }
      catch (CommitException expected)
      {
      }

      CDOXATransaction transSet = CDOUtil.createXATransaction();
      transSet.add(CDOUtil.getViewSet(resourceSet));

      transactionA.commit();
    }
  }

  // XXX disabled because of Bug 290097
  @Skips("Postgresql")
  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void test_ExceptionInReadingStream() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    SessionUtil.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
    CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    supplier.getPurchaseOrders().add(purchaseOrder);
    resB.getContents().add(supplier);
    resA.getContents().add(purchaseOrder);

    assertNew(resA, transactionA1);
    assertNew(resB, transactionB1);

    xaTransaction.commit();
    SpecialPurchaseOrder order = getModel2Factory().createSpecialPurchaseOrder();
    resB.getContents().add(order);
    try
    {
      xaTransaction.commit();
    }
    catch (Exception ignore)
    {
    }
  }

  // Skip this test until the problems with XATransactions are solved.
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void _test_ExceptionInWrite() throws Exception
  {
    getRepository(REPOSITORY2_NAME);
    getRepository("repo3");

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);
    CDOSession sessionC = openSession("repo3");

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    SessionUtil.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
    CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    supplier.getPurchaseOrders().add(purchaseOrder);
    resB.getContents().add(supplier);
    resA.getContents().add(purchaseOrder);

    assertNew(resA, transactionA1);
    assertNew(resB, transactionB1);

    xaTransaction.commit();

    CDOTransaction transactionC1 = sessionC.openTransaction();
    sessionC.getPackageRegistry().putEPackage(getModel1Package());

    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    CDOResource resC = transactionC1.createResource(getResourcePath("/resC"));
    resC.getContents().add(purchaseOrder3);
    supplier.getPurchaseOrders().add(purchaseOrder3);
    purchaseOrder.setDate(new Date());

    try
    {
      xaTransaction.commit();
      fail("Should fail");
    }
    catch (Exception ignore)
    {
    }

    assertEquals(false, CDOUtil.getCDOObject(supplier).cdoWriteLock().isLocked());
    assertEquals(false, CDOUtil.getCDOObject(purchaseOrder).cdoWriteLock().isLocked());

    xaTransaction.rollback();

    transactionA1.waitForUpdate(transactionC1.getLastCommitTime(), DEFAULT_TIMEOUT);
    assertEquals(null, purchaseOrder.getDate());
    assertEquals(1, supplier.getPurchaseOrders().size());
  }
}
