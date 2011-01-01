/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;
import org.eclipse.emf.internal.cdo.analyzer.CDOFetchRuleManagerThreadLocal;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * See bug 202063
 * 
 * @author Simon McDuff
 */
public class FetchRuleAnalyzerTest extends AbstractCDOTest
{
  public void testLoadObject() throws Exception
  {
    ArrayList<CDOObject> listOfCompany = new ArrayList<CDOObject>();

    {
      // disableConsole();
      msg("Opening session");
      CDOSession session = openSession();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test2");

      msg("Creating supplier");
      for (int i = 0; i < 10; i++)
      {
        Company company = getModel1Factory().createCompany();
        company.setCity("CITY" + String.valueOf(i));

        for (int j = 0; j < 10; j++)
        {
          PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
          company.getPurchaseOrders().add(purchaseOrder);

          Supplier supplier = getModel1Factory().createSupplier();

          // Should it detect supplier to make it persistent...

          resource.getContents().add(supplier);
          purchaseOrder.setSupplier(supplier);

          SalesOrder salesOrder = getModel1Factory().createSalesOrder();
          company.getSalesOrders().add(salesOrder);
        }

        resource.getContents().add(company);
        listOfCompany.add(CDOUtil.getCDOObject(company));
      }

      transaction.commit();
      // session.close();
      enableConsole();
    }

    msg("Opening session");
    InternalCDOSession session = (InternalCDOSession)openSession();
    session.setFetchRuleManager(new CDOFetchRuleManagerThreadLocal());

    msg("Opening transaction");
    InternalCDOTransaction transaction = (InternalCDOTransaction)session.openTransaction();
    CDOFeatureAnalyzerModelBased featureanalyzerModelBased = new CDOFeatureAnalyzerModelBased();
    transaction.setFeatureAnalyzer(featureanalyzerModelBased);
    transaction.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(10));

    msg("Getting resource");

    for (CDOObject companyObject : listOfCompany)
    {
      Company company = (Company)transaction.getObject(companyObject.cdoID(), true);
      for (PurchaseOrder purchaseOrder : company.getPurchaseOrders())
      {
        purchaseOrder.getSupplier();
      }
    }

    // Number of fetch should be 20.
    assertEquals(20, featureanalyzerModelBased.getFetchCount());

    List<CDOFetchRule> fetchRules = featureanalyzerModelBased.getFetchRules(null);

    assertEquals(2, fetchRules.size());

    CDOFetchRule fetchRule1 = fetchRules.get(0);
    EClass eClass = fetchRule1.getEClass();
    assertEquals(getModel1Package().getCompany(), eClass);
    assertEquals(1, fetchRule1.getFeatures().size());
    assertEquals(getModel1Package().getCompany_PurchaseOrders().getName(), fetchRule1.getFeatures().get(0).getName());

    CDOFetchRule fetchRule2 = fetchRules.get(1);
    EClass ePurchaseOrder = fetchRule2.getEClass();
    assertEquals(getModel1Package().getPurchaseOrder(), ePurchaseOrder);
    assertEquals(1, fetchRule2.getFeatures().size());
    assertEquals(getModel1Package().getPurchaseOrder_Supplier().getName(), fetchRule2.getFeatures().get(0).getName());

    transaction.close();
    // TODO session.close();
  }
}
