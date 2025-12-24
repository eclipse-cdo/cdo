/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * unset of CDO does not work correctly
 * <p>
 * See bug 302233
 *
 * @author Martin Fluegge
 */
public class Bugzilla_302233_Test extends AbstractCDOTest
{
  public void testUnset() throws Exception
  {
    Order order = getModel1Factory().createPurchaseOrder();
    EStructuralFeature feature = getModel1Package().getOrder_OrderDetails();
    assertEquals(false, order.eIsSet(feature));

    order.eUnset(feature);

    for (int i = 0; i < 10; i++)
    {
      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      order.getOrderDetails().add(orderDetail);
    }

    order.eUnset(feature);

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(order);

    for (int i = 0; i < 10; i++)
    {
      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      order.getOrderDetails().add(orderDetail);
    }

    transaction.commit();

    order.eUnset(feature);
    assertEquals(false, order.eIsSet(getModel1Package().getOrder_OrderDetails()));

    session.close();
  }

  public void testUnset2() throws Exception
  {
    EReference feature = getModel1Package().getCompany_Categories();
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(company);
    transaction.commit();

    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());

    company.eUnset(feature);
    assertEquals(false, company.eIsSet(feature));

    transaction.commit();
    session.close();
  }

  public void testRemove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());

    resource.getContents().remove(3);
    resource.getContents().remove(2);
    resource.getContents().remove(1);
    resource.getContents().remove(0);

    transaction.commit();
    session.close();
  }
}
