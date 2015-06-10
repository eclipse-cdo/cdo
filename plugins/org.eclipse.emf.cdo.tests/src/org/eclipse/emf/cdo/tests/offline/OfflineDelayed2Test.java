/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
    CDOObject cloneCompany = CDOUtil
        .getCDOObject(cloneTransaction.getObject(CDOUtil.getCDOObject(masterCompany).cdoID()));
    assertEquals(nbrOfCommits + 1, cloneCompany.cdoRevision().getVersion());
  }
}
