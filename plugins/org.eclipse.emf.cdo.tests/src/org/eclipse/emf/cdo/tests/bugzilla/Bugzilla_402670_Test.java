/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Steve Monnier - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Bug 402670.
 *
 * @author Steve Monnier
 */
public class Bugzilla_402670_Test extends AbstractCDOTest
{
  /**
   * This scenario validates that rollbacking a boolean value change doesn't throw an NPE.
   */
  public void testRollbackOfBooleanValueChange() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setPreferred(true);
    company.getSuppliers().add(supplier);
    transaction.commit();

    supplier.setPreferred(false);
    transaction.rollback();
  }
}
