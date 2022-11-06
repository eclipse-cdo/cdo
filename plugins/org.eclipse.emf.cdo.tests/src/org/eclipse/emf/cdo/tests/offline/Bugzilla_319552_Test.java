/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * "Attempt to modify historical revision" on raw replication.
 * <p>
 * See bug 319552
 *
 * @author Pascal Lehmann
 * @since 4.0
 */
public class Bugzilla_319552_Test extends AbstractSyncingTest
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

    final Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.setCommitComment("resource with one company created on clone");
    transaction.commit();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    // do some online changes to increase the revision.
    final Company masterCompany = (Company)masterTransaction.getObject(CDOUtil.getCDOObject(company).cdoID());

    masterCompany.setName("revision2");
    masterTransaction.commit();

    masterCompany.setName("revision3");
    masterTransaction.commit();

    masterCompany.setName("revision4");
    masterTransaction.commit();

    // go online again.
    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    company.setName("revision5");
    transaction.commit();

    // do a change online.
    masterCompany.getName();
    company.getName();

    // check revision versions.
    assertNoTimeout(() -> CDOUtil.getCDOObject(masterCompany).cdoRevision().getVersion() == CDOUtil.getCDOObject(company).cdoRevision().getVersion());
  }
}
