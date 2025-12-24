/*
 * Copyright (c) 2014 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;

/**
 * Bug 429709 - Setting list values by index twice causes DanglingReferenceException.
 *
 * @author Eike Stepper
 */
public class Bugzilla_429709_Test extends AbstractCDOTest
{
  public void testContainmentSetSet() throws Exception
  {
    Supplier s0 = getModel1Factory().createSupplier();
    Supplier s1 = getModel1Factory().createSupplier();
    Supplier s2 = getModel1Factory().createSupplier();

    Company company = getModel1Factory().createCompany();
    EList<Supplier> suppliers = company.getSuppliers();
    suppliers.add(s0);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    resource.getContents().add(company);
    transaction.commit();

    suppliers.set(0, s1);
    suppliers.set(0, s2);
    transaction.commit();
  }

  public void testContainmentAddSet() throws Exception
  {
    Supplier s0 = getModel1Factory().createSupplier();
    Supplier s1 = getModel1Factory().createSupplier();
    Supplier s2 = getModel1Factory().createSupplier();

    Company company = getModel1Factory().createCompany();
    EList<Supplier> suppliers = company.getSuppliers();
    suppliers.add(s0);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    resource.getContents().add(company);
    transaction.commit();

    suppliers.add(s1);
    suppliers.set(1, s2);
    transaction.commit();
  }

  public void testContainmentAddAddSet() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    EList<Supplier> suppliers = company.getSuppliers();
    suppliers.add(getModel1Factory().createSupplier());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    resource.getContents().add(company);
    transaction.commit();

    suppliers.add(0, getModel1Factory().createSupplier()); // This supplier will end up at index 1
    suppliers.add(0, getModel1Factory().createSupplier());

    suppliers.set(1, getModel1Factory().createSupplier()); // Replace supplier at index 1
    transaction.commit();
  }
}
