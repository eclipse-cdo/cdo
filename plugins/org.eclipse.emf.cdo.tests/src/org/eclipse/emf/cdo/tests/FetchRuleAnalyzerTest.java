/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.protocol.model.CDOClassImpl;
import org.eclipse.emf.cdo.protocol.analyzer.CDOFetchRule;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;
import org.eclipse.emf.internal.cdo.analyzer.CDOFetchRuleManagerThreadLocal;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=202063
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
      CDOSession session = openModel1Session();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test2");

      msg("Creating supplier");
      for (int i = 0; i < 10; i++)
      {
        Company company = Model1Factory.eINSTANCE.createCompany();
        company.setCity("CITY" + String.valueOf(i));

        for (int j = 0; j < 10; j++)
        {
          PurchaseOrder purchaseOrder = Model1Factory.eINSTANCE.createPurchaseOrder();
          company.getPurchaseOrders().add(purchaseOrder);

          Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

          // Should it detect supplier to make it persistent...

          resource.getContents().add(supplier);
          purchaseOrder.setSupplier(supplier);

          SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
          company.getSalesOrders().add(salesOrder);
        }

        resource.getContents().add(company);
        listOfCompany.add(company);
      }

      transaction.commit();
      // session.close();
      enableConsole();
    }

    msg("Opening session");
    CDOSessionImpl session = (CDOSessionImpl)openModel1Session();

    session.getRevisionManager().setRuleManager(new CDOFetchRuleManagerThreadLocal());

    msg("Opening transaction");
    CDOTransactionImpl transaction = session.openTransaction(new ResourceSetImpl());

    CDOFeatureAnalyzerModelBased featureanalyzerModelBased = new CDOFeatureAnalyzerModelBased();
    transaction.setFeatureAnalyzer(featureanalyzerModelBased);
    transaction.setLoadRevisionCollectionChunkSize(10);

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
    EClass eClass = ModelUtil.getEClass((CDOClassImpl)fetchRule1.getCDOClass(), session.getPackageRegistry());
    assertEquals(Model1Package.eINSTANCE.getCompany(), eClass);
    assertEquals(1, fetchRule1.getFeatures().size());
    assertEquals(Model1Package.eINSTANCE.getCompany_PurchaseOrders().getName(), fetchRule1.getFeatures().get(0)
        .getName());

    CDOFetchRule fetchRule2 = fetchRules.get(1);
    EClass ePurchaseOrder = ModelUtil.getEClass((CDOClassImpl)fetchRule2.getCDOClass(), session.getPackageRegistry());
    assertEquals(Model1Package.eINSTANCE.getPurchaseOrder(), ePurchaseOrder);
    assertEquals(1, fetchRule2.getFeatures().size());
    assertEquals(Model1Package.eINSTANCE.getPurchaseOrder_Supplier().getName(), fetchRule2.getFeatures().get(0)
        .getName());

    transaction.close();
    // TODO session.close();
  }
}
