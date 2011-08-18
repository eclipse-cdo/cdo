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
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestListener2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOLocksChangedEvent;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Caspar De Groot
 */
public class OfflineLockingTest extends AbstractSyncingTest
{
  public void testLockAndUnlockThrough() throws Exception
  {
    CDOSession masterSession = openSession(getRepository().getName() + "_master");

    CDOSession cloneSession = openSession();
    waitForOnline(cloneSession.getRepositoryInfo());

    CDOTransaction cloneTx = cloneSession.openTransaction();
    cloneTx.enableDurableLocking(true);

    CDOResource res = cloneTx.createResource(getResourcePath("test"));
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    cloneTx.commit();
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    CDOView masterView = masterSession.openView();
    masterView.options().setLockNotificationEnabled(true);
    TestListener2 masterViewListener = new TestListener2(CDOLocksChangedEvent.class);
    masterView.addListener(masterViewListener);
    CDOObject cdoCompanyOnMaster = masterView.getObject(cdoCompany.cdoID());

    cdoCompany.cdoWriteLock().lock();
    masterViewListener.waitFor(1);
    assertEquals(true, cdoCompanyOnMaster.cdoWriteLock().isLockedByOthers());

    cdoCompany.cdoWriteLock().unlock();
    masterViewListener.waitFor(2);
    assertEquals(false, cdoCompanyOnMaster.cdoWriteLock().isLockedByOthers());

    cloneSession.close();
    masterSession.close();
  }

  public void testMasterLocks_ArrivalInClone() throws Exception
  {
  }
}
