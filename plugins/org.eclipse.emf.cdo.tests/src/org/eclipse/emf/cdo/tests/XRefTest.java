/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Collections;
import java.util.List;

/**
 * 300149: Support remote cross referencing with a convenient API on the client and SPI on the server for the stores to
 * implement https://bugs.eclipse.org/bugs/show_bug.cgi?id=300149
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class XRefTest extends AbstractCDOTest
{
  public void testCrossReferenceMultivalueEReferenceQuery() throws Exception
  {
    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.getPurchaseOrders().add(purchaseOrder1);
    supplier.getPurchaseOrders().add(purchaseOrder2);
    supplier.getPurchaseOrders().add(purchaseOrder3);
    supplier.getPurchaseOrders().add(purchaseOrder4);

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();

    CDOResource resource = transaction.createResource("/test1");
    resource.getContents().add(supplier);
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    resource.getContents().add(purchaseOrder3);
    resource.getContents().add(purchaseOrder4);

    transaction.commit();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    List<CDOObjectReference> results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(supplier)));
    assertEquals(4, results.size());

    for (CDOObjectReference result : results)
    {
      CDOObject sourceObject = result.getSourceObject();
      assertInstanceOf(PurchaseOrder.class, CDOUtil.getEObject(sourceObject));
    }
  }

  // public void testCrossReferenceSimpleEReferenceQuery() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  // PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
  // supplier1.getPurchaseOrders().add(purchaseOrder1);
  // supplier1.getPurchaseOrders().add(purchaseOrder2);
  // supplier1.getPurchaseOrders().add(purchaseOrder3);
  // supplier1.getPurchaseOrders().add(purchaseOrder4);
  //
  // resource.getContents().add(supplier1);
  // resource.getContents().add(purchaseOrder1);
  // resource.getContents().add(purchaseOrder2);
  // resource.getContents().add(purchaseOrder3);
  // resource.getContents().add(purchaseOrder4);
  // transaction1.commit();
  //
  // CDOID referenceID = ((CDOObject)supplier1).cdoID();
  // CDOID queryID1 = ((CDOObject)purchaseOrder1).cdoID();
  // CDOID queryID2 = ((CDOObject)purchaseOrder2).cdoID();
  // CDOID queryID3 = ((CDOObject)purchaseOrder3).cdoID();
  // CDOID queryID4 = ((CDOObject)purchaseOrder4).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  //
  // msg("Executing XREF Query 1");
  //
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID1);
  // List<CDOID> results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // CDOObject cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // msg("Executing XREF Query 2");
  //
  // query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID2);
  // results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // msg("Executing XREF Query 3");
  //
  // query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID3);
  // results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // msg("Executing XREF Query 4");
  //
  // query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID4);
  // results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(1, results.size());
  // cdoObject = view.getObject(results.get(0));
  // assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));
  //
  // }
  //
  // public void testCrossReferenceWithFeatureQuery() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  // PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
  // supplier1.getPurchaseOrders().add(purchaseOrder1);
  // supplier1.getPurchaseOrders().add(purchaseOrder2);
  // supplier1.getPurchaseOrders().add(purchaseOrder3);
  // supplier1.getPurchaseOrders().add(purchaseOrder4);
  //
  // resource.getContents().add(supplier1);
  // resource.getContents().add(purchaseOrder1);
  // resource.getContents().add(purchaseOrder2);
  // resource.getContents().add(purchaseOrder3);
  // resource.getContents().add(purchaseOrder4);
  // transaction1.commit();
  //
  // CDOID querycdoID = ((CDOObject)supplier1).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, querycdoID);
  // query.setParameter("provideFeatureName", Boolean.TRUE);
  //
  // msg("Executing XREF Query");
  //
  // List<Object> results = query.getResult(Object.class);
  //
  // assertFalse(results.isEmpty());
  //
  // msg("Constructing test EStructuralFeature.Setting");
  //
  // Collection<CDOID> matchedIdsPerEClass = new ArrayList<CDOID>();
  // Collection<EStructuralFeature.Setting> settings = new ArrayList<EStructuralFeature.Setting>();
  //
  // for (Object object : results)
  // {
  // if (object instanceof CDOID)
  // {
  // matchedIdsPerEClass.add((CDOID)object);
  // }
  // else if (object instanceof String)
  // {
  // for (CDOID cdoId : matchedIdsPerEClass)
  // {
  // settings.add(new CDOSetting(view, cdoId, (String)object));
  // }
  // matchedIdsPerEClass.clear();
  // }
  //
  // }
  //
  // msg("Checking query result");
  //
  // for (EStructuralFeature.Setting setting : settings)
  // {
  // CDOObject crossReferencedEObject = (CDOObject)setting.get(true);
  // assertTrue(CDOIDUtil.equals(crossReferencedEObject.cdoID(), querycdoID));
  // }
  // }
  //
  // public void testCrossReferenceWithFeatureQueryUsingCDOUtil() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  // PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
  // PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
  // supplier1.getPurchaseOrders().add(purchaseOrder1);
  // supplier1.getPurchaseOrders().add(purchaseOrder2);
  // supplier1.getPurchaseOrders().add(purchaseOrder3);
  // supplier1.getPurchaseOrders().add(purchaseOrder4);
  //
  // resource.getContents().add(supplier1);
  // resource.getContents().add(purchaseOrder1);
  // resource.getContents().add(purchaseOrder2);
  // resource.getContents().add(purchaseOrder3);
  // resource.getContents().add(purchaseOrder4);
  // transaction1.commit();
  //
  // CDOID querycdoID = ((CDOObject)supplier1).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOObject cdoObject = view.getObject(querycdoID);
  //
  // Collection<EStructuralFeature.Setting> settings = getCrossReferences(cdoObject);
  //
  // for (EStructuralFeature.Setting setting : settings)
  // {
  // CDOObject crossReferencedEObject = (CDOObject)setting.get(true);
  // assertTrue(CDOIDUtil.equals(crossReferencedEObject.cdoID(), querycdoID));
  // }
  // }
  //
  // public void testEmptyCrossReferenceQuery() throws Exception
  // {
  // msg("Opening session");
  //
  // CDOSession session1 = openSession();
  //
  // msg("Opening transaction");
  // CDOTransaction transaction1 = session1.openTransaction();
  //
  // msg("Creating resource");
  // CDOResource resource = transaction1.createResource("/test1");
  //
  // msg("Creating test data");
  // Supplier supplier1 = getModel1Factory().createSupplier();
  //
  // resource.getContents().add(supplier1);
  // transaction1.commit();
  //
  // CDOID queryID = ((CDOObject)supplier1).cdoID();
  //
  // session1.close();
  //
  // /******************/
  //
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID);
  //
  // msg("Executing XREF Query");
  // List<CDOID> results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(0, results.size());
  // }
  //
  // public void testInexistentTargetForCrossReferenceQuery() throws Exception
  // {
  // CDOSession session2 = openSession();
  // CDOView view = session2.openView();
  // CDOQuery query = view.createQuery("XREF", null);
  // query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, CDOIDUtil.createLong(10000));
  //
  // msg("Executing XREF Query");
  // List<CDOID> results = query.getResult(CDOID.class);
  //
  // msg("Checking query result");
  //
  // assertEquals(0, results.size());
  // }
}
