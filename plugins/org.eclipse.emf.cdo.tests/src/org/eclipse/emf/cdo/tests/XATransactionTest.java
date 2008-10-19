/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOXATransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.Date;

/**
 * @author Simon McDuff
 */
public class XATransactionTest extends AbstractCDOTest
{
  final static public String REPOSITORY2_NAME = "repo2";

  public void testRollback() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    CDOSessionImpl.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource("/resA");
    CDOResource resB = transactionB1.createResource("/resB");

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

  public void testRollback_AfterSetpoint() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    CDOSessionImpl.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource("/resA");
    CDOResource resB = transactionB1.createResource("/resB");

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

    supplier.getPurchaseOrders().add(purchaseOrder);
    resB.getContents().add(supplier);
    resA.getContents().add(purchaseOrder);

    assertNew(resA, transactionA1);
    assertNew(resB, transactionB1);

    CDOSavepoint savepoint1 = xaTransaction.setSavepoint();

    purchaseOrder.setDate(new Date());
    supplier.setCity("OTTAWA");

    CDOSavepoint savepoint2 = xaTransaction.setSavepoint();

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

  public void testCommitFromTransactionDisabled() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    {
      CDOSession sessionA = openSession();
      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      ResourceSet resourceSet = new ResourceSetImpl();
      sessionA.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      CDOTransactionImpl transactionA1 = (CDOTransactionImpl)sessionA.openTransaction(resourceSet);
      CDOTransactionImpl transactionB1 = (CDOTransactionImpl)sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
      CDOResource resB = transactionB1.createResource("/resB");

      transactionA1.setTransactionStrategy(null);
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
        fail("We should have an exception");
      }
      catch (TransactionException exp)
      {

      }
      transSet.commit();
    }
  }

  public void testNotUsingXATransaction_Exception() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    {
      CDOSession sessionA = openSession();
      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      ResourceSet resourceSet = new ResourceSetImpl();
      sessionA.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      CDOTransactionImpl transactionA1 = (CDOTransactionImpl)sessionA.openTransaction(resourceSet);
      CDOTransactionImpl transactionB1 = (CDOTransactionImpl)sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
      CDOResource resB = transactionB1.createResource("/resB");
      GenRefSingleNonContained objectFromResA = getModel4Factory().createGenRefSingleNonContained();
      GenRefSingleNonContained objectFromResB = getModel4Factory().createGenRefSingleNonContained();

      objectFromResA.setElement(objectFromResB);
      resA.getContents().add(objectFromResA);
      resB.getContents().add(objectFromResB);

      try
      {
        transactionA1.commit();
        fail("We should have an exception");
      }
      catch (TransactionException exp)
      {
      }
      CDOXATransaction transSet = CDOUtil.createXATransaction();
      transSet.add(CDOUtil.getViewSet(resourceSet));

      transactionA1.commit();
    }
  }
}
