/*
 * Copyright (c) 2014, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model1.legacy.Model1Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

/**
 * @author Leonid
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_430836_Test extends AbstractCDOTest
{
  public void testXRefQueryOnBranchOtherThenMain() throws Exception
  {
    internalTestWithMaxResults();
  }

  private void internalTestWithMaxResults() throws ConcurrentAccessException, CommitException
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction();

    Supplier mainBranchSupplier = Model1Factory.eINSTANCE.createSupplier();
    PurchaseOrder po = Model1Factory.eINSTANCE.createPurchaseOrder();
    po.setSupplier(mainBranchSupplier);

    CDOResource resource = transaction.createResource(getResourcePath("main_po.po"));
    resource.getContents().add(po);
    resource.getContents().add(mainBranchSupplier);

    transaction.commit();
    transaction.close();

    CDOBranch newBranch = mainBranch.createBranch(getBranchName("new_branch"));
    transaction = session.openTransaction(newBranch);

    PurchaseOrder po2 = Model1Factory.eINSTANCE.createPurchaseOrder();
    po2.setSupplier(transaction.getObject(mainBranchSupplier));

    resource = transaction.createResource(getResourcePath("branch_po.po"));
    resource.getContents().add(po2);

    transaction.commit();
    transaction.close();

    CDOTransaction tx = session.openTransaction(newBranch);
    assertEquals(2, tx.queryXRefs(CDOUtil.getCDOObject(tx.getObject(mainBranchSupplier))).size());
  }
}
