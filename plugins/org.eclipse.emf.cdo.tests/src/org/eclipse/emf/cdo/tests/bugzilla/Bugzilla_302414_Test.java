/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import java.util.List;

/**
 * Bug 302414, bug 316713 - ArrayIndexOutOfBoundsException in CDOListFeatureDeltaImpl
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_302414_Test extends AbstractCDOTest
{
  public void test1() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource("/r1"); //$NON-NLS-1$

    Company company = Model1Factory.eINSTANCE.createCompany();
    r1.getContents().add(company);
    tx.commit();

    List<PurchaseOrder> list = company.getPurchaseOrders();

    PurchaseOrder foo = Model1Factory.eINSTANCE.createPurchaseOrder();
    list.add(foo);
    list.remove(foo);

    PurchaseOrder bar = Model1Factory.eINSTANCE.createPurchaseOrder();
    list.add(bar);

    for (int i = 0; i < 10; i++)
    {
      company.getPurchaseOrders().add(Model1Factory.eINSTANCE.createPurchaseOrder());
    }

    try
    {
      list.remove(bar);
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      e.printStackTrace();
      fail("Should not have thrown " + e.getClass().getName());
    }

    tx.close();
    session.close();
  }

  public void test2() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource r1 = tx.createResource("/r1"); //$NON-NLS-1$

    Company company = Model1Factory.eINSTANCE.createCompany();
    r1.getContents().add(company);
    tx.commit();

    List<PurchaseOrder> list = company.getPurchaseOrders();

    try
    {
      for (int i = 0; i < 20; i++)
      {
        PurchaseOrder foo1 = Model1Factory.eINSTANCE.createPurchaseOrder();
        PurchaseOrder foo2 = Model1Factory.eINSTANCE.createPurchaseOrder();
        list.add(foo1);
        list.add(foo2);
        list.remove(foo1);
      }
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      fail("Should not have thrown " + e.getClass().getName());
    }

    tx.close();
    session.close();
  }
}
