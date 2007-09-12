/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOTransactionImpl;
import org.eclipse.emf.internal.cdo.analyzer.CDOFeatureAnalyzerModelBased;
import org.eclipse.emf.internal.cdo.analyzer.CDOFetchRuleManagerThreadLocal;

/**
 * @author Eike Stepper
 */
public class FetchRuleAnalyzerTest extends AbstractCDOTest
{
  public void testLoadObject() throws Exception
  {
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
          // I don't want to do resource.getContents().add(supplier)
          purchaseOrder.setSupplier(supplier);

          SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
          company.getSalesOrders().add(salesOrder);
        }

        resource.getContents().add(company);
      }

      transaction.commit();
      // XXX session.close();
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
    CDOResource resource = transaction.getResource("/test2");

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();
    for (EObject eObject : contents)
    {
      Company company = (Company)eObject;
      for (PurchaseOrder purchaseOrder : company.getPurchaseOrders())
      {
        purchaseOrder.getSupplier();
      }
    }

    assertEquals(featureanalyzerModelBased.getFetchCount(), 12);
  }
}
