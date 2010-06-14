/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 300149: Support remote cross referencing with a convenient API on the client and SPI on the server for the stores to
 * implement https://bugs.eclipse.org/bugs/show_bug.cgi?id=300149
 * 
 * @author Victor Roldan Betancort
 */
@Deprecated
public class XREFQueryTest extends AbstractCDOTest
{
  private static final String XREF_QUERY_TARGET_ID_ARGUMENT = "targetID";

  public void testCrossReferenceMultivalueEReferenceQuery() throws Exception
  {
    msg("Opening session");

    CDOSession session1 = openSession();

    msg("Opening transaction");
    CDOTransaction transaction1 = session1.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction1.createResource("/test1");

    msg("Creating test data");
    Supplier supplier1 = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
    supplier1.getPurchaseOrders().add(purchaseOrder1);
    supplier1.getPurchaseOrders().add(purchaseOrder2);
    supplier1.getPurchaseOrders().add(purchaseOrder3);
    supplier1.getPurchaseOrders().add(purchaseOrder4);

    resource.getContents().add(supplier1);
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    resource.getContents().add(purchaseOrder3);
    resource.getContents().add(purchaseOrder4);
    transaction1.commit();

    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    CDOQuery query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, ((CDOObject)supplier1).cdoID());

    msg("Executing XREF Query");
    List<CDOID> results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(4, results.size());
    for (CDOID cdoid : results)
    {
      CDOObject cdoObject = view.getObject(cdoid);
      assertTrue(cdoObject instanceof PurchaseOrder);
    }
  }

  public void testCrossReferenceSimpleEReferenceQuery() throws Exception
  {
    msg("Opening session");

    CDOSession session1 = openSession();

    msg("Opening transaction");
    CDOTransaction transaction1 = session1.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction1.createResource("/test1");

    msg("Creating test data");
    Supplier supplier1 = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
    supplier1.getPurchaseOrders().add(purchaseOrder1);
    supplier1.getPurchaseOrders().add(purchaseOrder2);
    supplier1.getPurchaseOrders().add(purchaseOrder3);
    supplier1.getPurchaseOrders().add(purchaseOrder4);

    resource.getContents().add(supplier1);
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    resource.getContents().add(purchaseOrder3);
    resource.getContents().add(purchaseOrder4);
    transaction1.commit();

    CDOID referenceID = ((CDOObject)supplier1).cdoID();
    CDOID queryID1 = ((CDOObject)purchaseOrder1).cdoID();
    CDOID queryID2 = ((CDOObject)purchaseOrder2).cdoID();
    CDOID queryID3 = ((CDOObject)purchaseOrder3).cdoID();
    CDOID queryID4 = ((CDOObject)purchaseOrder4).cdoID();

    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    msg("Executing XREF Query 1");

    CDOQuery query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID1);
    List<CDOID> results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(1, results.size());
    CDOObject cdoObject = view.getObject(results.get(0));
    assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));

    msg("Executing XREF Query 2");

    query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID2);
    results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(1, results.size());
    cdoObject = view.getObject(results.get(0));
    assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));

    msg("Executing XREF Query 3");

    query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID3);
    results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(1, results.size());
    cdoObject = view.getObject(results.get(0));
    assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));

    msg("Executing XREF Query 4");

    query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID4);
    results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(1, results.size());
    cdoObject = view.getObject(results.get(0));
    assertTrue(CDOIDUtil.equals(cdoObject.cdoID(), referenceID));

  }

  public void testCrossReferenceWithFeatureQuery() throws Exception
  {
    msg("Opening session");

    CDOSession session1 = openSession();

    msg("Opening transaction");
    CDOTransaction transaction1 = session1.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction1.createResource("/test1");

    msg("Creating test data");
    Supplier supplier1 = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
    supplier1.getPurchaseOrders().add(purchaseOrder1);
    supplier1.getPurchaseOrders().add(purchaseOrder2);
    supplier1.getPurchaseOrders().add(purchaseOrder3);
    supplier1.getPurchaseOrders().add(purchaseOrder4);

    resource.getContents().add(supplier1);
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    resource.getContents().add(purchaseOrder3);
    resource.getContents().add(purchaseOrder4);
    transaction1.commit();

    CDOID querycdoID = ((CDOObject)supplier1).cdoID();

    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    CDOQuery query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, querycdoID);
    query.setParameter("provideFeatureName", Boolean.TRUE);

    msg("Executing XREF Query");

    List<Object> results = query.getResult(Object.class);

    assertFalse(results.isEmpty());

    msg("Constructing test EStructuralFeature.Setting");

    Collection<CDOID> matchedIdsPerEClass = new ArrayList<CDOID>();
    Collection<EStructuralFeature.Setting> settings = new ArrayList<EStructuralFeature.Setting>();

    for (Object object : results)
    {
      if (object instanceof CDOID)
      {
        matchedIdsPerEClass.add((CDOID)object);
      }
      else if (object instanceof String)
      {
        for (CDOID cdoId : matchedIdsPerEClass)
        {
          settings.add(new CDOSetting(view, cdoId, (String)object));
        }
        matchedIdsPerEClass.clear();
      }

    }

    msg("Checking query result");

    for (EStructuralFeature.Setting setting : settings)
    {
      CDOObject crossReferencedEObject = (CDOObject)setting.get(true);
      assertTrue(CDOIDUtil.equals(crossReferencedEObject.cdoID(), querycdoID));
    }
  }

  public void testCrossReferenceWithFeatureQueryUsingCDOUtil() throws Exception
  {
    msg("Opening session");

    CDOSession session1 = openSession();

    msg("Opening transaction");
    CDOTransaction transaction1 = session1.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction1.createResource("/test1");

    msg("Creating test data");
    Supplier supplier1 = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder1 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder2 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder3 = getModel1Factory().createPurchaseOrder();
    PurchaseOrder purchaseOrder4 = getModel1Factory().createPurchaseOrder();
    supplier1.getPurchaseOrders().add(purchaseOrder1);
    supplier1.getPurchaseOrders().add(purchaseOrder2);
    supplier1.getPurchaseOrders().add(purchaseOrder3);
    supplier1.getPurchaseOrders().add(purchaseOrder4);

    resource.getContents().add(supplier1);
    resource.getContents().add(purchaseOrder1);
    resource.getContents().add(purchaseOrder2);
    resource.getContents().add(purchaseOrder3);
    resource.getContents().add(purchaseOrder4);
    transaction1.commit();

    CDOID querycdoID = ((CDOObject)supplier1).cdoID();

    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    CDOObject cdoObject = view.getObject(querycdoID);

    Collection<EStructuralFeature.Setting> settings = getCrossReferences(cdoObject);

    for (EStructuralFeature.Setting setting : settings)
    {
      CDOObject crossReferencedEObject = (CDOObject)setting.get(true);
      assertTrue(CDOIDUtil.equals(crossReferencedEObject.cdoID(), querycdoID));
    }
  }

  public void testEmptyCrossReferenceQuery() throws Exception
  {
    msg("Opening session");

    CDOSession session1 = openSession();

    msg("Opening transaction");
    CDOTransaction transaction1 = session1.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction1.createResource("/test1");

    msg("Creating test data");
    Supplier supplier1 = getModel1Factory().createSupplier();

    resource.getContents().add(supplier1);
    transaction1.commit();

    CDOID queryID = ((CDOObject)supplier1).cdoID();

    session1.close();

    /******************/

    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    CDOQuery query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, queryID);

    msg("Executing XREF Query");
    List<CDOID> results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(0, results.size());
  }

  public void testInexistentTargetForCrossReferenceQuery() throws Exception
  {
    CDOSession session2 = openSession();
    CDOView view = session2.openView();
    CDOQuery query = view.createQuery("XREF", null);
    query.setParameter(XREF_QUERY_TARGET_ID_ARGUMENT, CDOIDUtil.createLong(10000));

    msg("Executing XREF Query");
    List<CDOID> results = query.getResult(CDOID.class);

    msg("Checking query result");

    assertEquals(0, results.size());
  }

  /**
   * Determines all the incoming cross references for the specified object.
   * <p>
   * Behaves much like org.eclipse.emf.ecore.util.EcoreUtil.CrossReferencer.find(Collection<?>)
   * 
   * @see EcoreUtil.CrossReferencer
   * @since 3.0
   */
  private static Collection<Setting> getCrossReferences(CDOObject object)
  {
    CDOState state = object.cdoState();
    if (state == CDOState.TRANSIENT || state == CDOState.PREPARED)
    {
      throw new IllegalArgumentException("Object is transient");
    }

    CDOView view = object.cdoView();
    CDOQuery query = view.createQuery("XREF", null);
    query.setParameter("provideFeatureName", true);
    query.setParameter("targetID", object.cdoID());

    List<Object> results = query.getResult(Object.class);

    Collection<CDOID> matchedIdsPerEClass = new ArrayList<CDOID>();
    Collection<EStructuralFeature.Setting> settings = new ArrayList<EStructuralFeature.Setting>();

    for (Object result : results)
    {
      if (result instanceof CDOID)
      {
        matchedIdsPerEClass.add((CDOID)result);
      }
      else if (result instanceof String)
      {
        for (CDOID cdoId : matchedIdsPerEClass)
        {
          settings.add(new CDOSetting(view, cdoId, (String)result));
        }
        matchedIdsPerEClass.clear();
      }
    }
    return settings;
  }

  /**
   * A Setting implementation that get both source and target EObjects lazily
   * 
   * @author Victor Roldan Betancort
   */
  private static class CDOSetting implements Setting
  {
    private EStructuralFeature feature = null;

    private String featureName;

    private CDOView view;

    private CDOID eObjectId;

    private CDOObject eObject = null;

    public CDOSetting(CDOView view, CDOID eObjectId, String featureName)
    {
      this.featureName = featureName;
      this.view = view;
      this.eObjectId = eObjectId;
    }

    public Object get(boolean resolve)
    {
      return getEObject().eGet(getEStructuralFeature());
    }

    public EObject getEObject()
    {
      if (eObject == null)
      {
        eObject = view.getObject(eObjectId);
      }
      return eObject;
    }

    public EStructuralFeature getEStructuralFeature()
    {
      if (feature == null)
      {
        feature = getEObject().eClass().getEStructuralFeature(featureName);
      }
      return feature;
    }

    public boolean isSet()
    {
      return getEObject().eIsSet(getEStructuralFeature());
    }

    public void set(Object newValue)
    {
      getEObject().eSet(getEStructuralFeature(), newValue);
    }

    public void unset()
    {
      getEObject().eUnset(getEStructuralFeature());
    }
  }

}
