/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_339461_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));

    Model1Factory factory = getModel1Factory();
    Company company = factory.createCompany();
    resource.getContents().add(company);

    SalesOrder order1 = factory.createSalesOrder();
    SalesOrder order2 = factory.createSalesOrder();
    OrderDetail detail = factory.createOrderDetail();
    order1.getOrderDetails().add(detail);
    resource.getContents().add(order1);
    resource.getContents().add(order2);
    tx.commit();

    order1.getOrderDetails().remove(detail);
    order2.getOrderDetails().add(detail);

    company.setName("dirty1"); // Ensures partial commit
    tx.setCommittables(createSet(order1, order2, detail));
    tx.commit();

    order2.getOrderDetails().remove(detail);
    order1.getOrderDetails().add(detail);

    tx.setCommittables(createSet(order1, order2, detail));
    tx.commit();

    tx.close();
    session.close();
  }

  private Set<EObject> createSet(EObject... objects)
  {
    Set<EObject> committables = new HashSet<>();
    for (EObject o : objects)
    {
      if (o == null)
      {
        throw new NullPointerException();
      }

      committables.add(o);
    }

    return committables;
  }
}
