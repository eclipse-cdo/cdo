/*
 * Copyright (c) 2014, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitConflictException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * Bug 430794 - Using {@link CDOMergingConflictResolver} with {@link CDOSavepoint} provokes a {@link CommitConflictException} on server side.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_430794_Test extends AbstractCDOTest
{
  public void testConflictResolutionWithSavepoint() throws Exception
  {
    Supplier s0 = getModel1Factory().createSupplier();
    Supplier s1 = getModel1Factory().createSupplier();
    Supplier s2 = getModel1Factory().createSupplier();

    Company company = getModel1Factory().createCompany();
    EList<Supplier> suppliers = company.getSuppliers();
    suppliers.add(s0);

    CDOSession session1 = openSession();
    CDOTransaction transactionOfUser1 = session1.openTransaction();
    CDOResource resourceOfUser1 = transactionOfUser1.createResource(getResourcePath("/test1"));

    resourceOfUser1.getContents().add(company);
    transactionOfUser1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transactionOfUser2 = session2.openTransaction();
    transactionOfUser2.options().addConflictResolver(new CDOMergingConflictResolver());
    CDOResource resourceOfUser2 = transactionOfUser2.getResource(getResourcePath("/test1"));
    Company companyRefFromUser2 = (Company)resourceOfUser2.getContents().get(0);
    companyRefFromUser2.getSuppliers().add(s1);

    transactionOfUser2.setSavepoint();

    company.getSuppliers().add(s2);
    commitAndSync(transactionOfUser1, transactionOfUser2);

    assertTrue(transactionOfUser2.isDirty());
    assertEquals(3, companyRefFromUser2.getSuppliers().size());
    assertEquals(0, transactionOfUser2.getConflicts().size());
    assertEquals(1, transactionOfUser2.getNewObjects().size());

    commitAndSync(transactionOfUser2, transactionOfUser1);
  }
}
