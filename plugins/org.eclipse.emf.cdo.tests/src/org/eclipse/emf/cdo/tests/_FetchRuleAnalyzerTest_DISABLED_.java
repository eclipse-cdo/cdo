/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.List;

/**
 * See bug 202063
 *
 * @author Simon McDuff
 */
public class _FetchRuleAnalyzerTest_DISABLED_ extends AbstractCDOTest
{
  public void testLoadObject() throws Exception
  {
    ArrayList<CDOObject> listOfCompany = new ArrayList<>();

    {
      // disableConsole();
      msg("Opening session");
      CDOSession session = openSession();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test2"));

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
    getTestProperties().put(SessionConfig.PROP_TEST_FETCH_RULE_MANAGER, CDOUtil.createThreadLocalFetchRuleManager());
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setFeatureAnalyzer(CDOUtil.createModelBasedFeatureAnalyzer());
    transaction.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(10));

    msg("Getting resource");
    for (CDOObject companyObject : listOfCompany)
    {
      Company company = (Company)CDOUtil.getEObject(transaction.getObject(companyObject.cdoID(), true));
      for (PurchaseOrder purchaseOrder : company.getPurchaseOrders())
      {
        purchaseOrder.getSupplier();
      }
    }

    // Number of fetch should be 20.
    assertEquals(20, new CDOFeatureAnalyzerModelBased().getFetchCount());

    List<CDOFetchRule> fetchRules = new CDOFeatureAnalyzerModelBased().getFetchRules(null);

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
