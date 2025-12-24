/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.List;

/**
 * @author Leonid Ripeynih
 */
@CleanRepositoriesBefore(reason = "Query result counting")
public class Bugzilla_467616_Test extends AbstractCDOTest
{
  private static final String PO_REPORT = "self.purchaseOrders -> collect(po | Tuple{ purchaseOrder = po, lineCount = po.orderDetails -> size() })";

  public void testTupleOCLQuery() throws ConcurrentAccessException, CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/resource"));

    Company company = Model1Factory.eINSTANCE.createCompany();
    company.setName("Company");

    company.getPurchaseOrders().add(createPo(1));
    company.getPurchaseOrders().add(createPo(2));

    resource.getContents().add(company);

    transaction.commit();
    transaction.close();

    CDOView view = session.openView();
    Company createdCompany = view.getObject(company);
    CDOQuery query = view.createQuery("ocl", PO_REPORT, CDOUtil.getCDOObject(createdCompany).cdoID());

    List<Object[]> result = query.getResult(Object[].class);

    assertEquals(new Object[] { createdCompany.getPurchaseOrders().get(0), 1 }, result.get(0));
    assertEquals(new Object[] { createdCompany.getPurchaseOrders().get(1), 2 }, result.get(1));

    view.close();
  }

  private PurchaseOrder createPo(int detailCount)
  {
    PurchaseOrder po = Model1Factory.eINSTANCE.createPurchaseOrder();
    for (int i = 0; i < detailCount; ++i)
    {
      po.getOrderDetails().add(Model1Factory.eINSTANCE.createOrderDetail());
    }
    return po;
  }
}
