/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * Revision not revised on rawImport for deleted objects.
 * <p>
 * See bug 326047
 *
 * @author Pascal Lehmann
 * @since 4.0
 */
public class Bugzilla_326047_Test extends AbstractSyncingTest
{
  public void test() throws Exception
  {
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    CDOSession masterSession = openSession("master");
    CDOTransaction masterTransaction = masterSession.openTransaction();

    CDOSession session = openSession();

    // Doing this that client notifications are built upon RevisionDeltas instead of RevisionKeys.
    session.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    Customer customer = getModel1Factory().createCustomer();
    company.getCustomers().add(customer);
    resource.getContents().add(company);
    transaction.setCommitComment("resource with one company created on clone");
    transaction.commit();

    // setup another branch.
    final CDOBranch otherBranch = transaction.getBranch().createBranch("other");
    final CDOTransaction otherTransaction = session.openTransaction(otherBranch);

    Customer branchCustomer = otherTransaction.getObject(customer);
    assertNotSame(null, branchCustomer);

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    // change the name of the customer on branch offline.
    branchCustomer.setName("branch-offline");
    otherTransaction.commit();

    // delete the customer on the online transaction.
    Company masterCompany = (Company)masterTransaction.getObject(CDOUtil.getCDOObject(company).cdoID());
    masterCompany.getCustomers().remove(0);
    masterTransaction.commit();

    // go online again.
    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    Exception exception = null;

    try
    {
      // merge branch.
      CDOBranchPoint source = otherTransaction.getBranch().getHead();
      DefaultCDOMerger.PerFeature.ManyValued merger = new DefaultCDOMerger.PerFeature.ManyValued();
      transaction.merge(source, merger); // <-- this merge should generate a conflict.
      transaction.commit(); // <-- commit will fail, because no conflict thrown.
    }
    catch (Exception e)
    {
      exception = e;
    }

    assertInstanceOf(ConflictException.class, exception);
  }
}
