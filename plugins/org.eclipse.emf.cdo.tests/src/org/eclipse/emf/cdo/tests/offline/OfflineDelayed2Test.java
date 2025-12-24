/*
 * Copyright (c) 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * @author Eike Stepper
 */
public class OfflineDelayed2Test extends AbstractSyncingTest
{
  @Override
  protected long getTestDelayed2CommitHandling()
  {
    return 1000L;
  }

  public void testCommitOrder() throws Exception
  {
    int nbrOfCommits = 5;

    InternalRepository master = getRepository("master");

    CDOSession masterSession = openSession(master.getName());
    CDOTransaction masterTransaction = masterSession.openTransaction();
    CDOResource masterResource = masterTransaction.createResource(getResourcePath("/my/resource"));

    Company masterCompany = getModel1Factory().createCompany();
    masterCompany.setName("Test");
    masterResource.getContents().add(masterCompany);

    masterTransaction.commit();

    CDOSession cloneSession = openSession();
    waitForOnline(cloneSession.getRepositoryInfo());

    for (int i = 0; i < nbrOfCommits; i++)
    {
      masterCompany.setName(Integer.toString(i));
      masterTransaction.commit();
    }

    sleep(1000);
    sleep(1000);
    sleep(1000);
    sleep(1000);
    sleep(1000);

    CDOTransaction cloneTransaction = cloneSession.openTransaction();
    CDOObject cloneCompany = CDOUtil.getCDOObject(cloneTransaction.getObject(CDOUtil.getCDOObject(masterCompany).cdoID()));
    assertEquals(nbrOfCommits + 1, cloneCompany.cdoRevision().getVersion());
  }
}
