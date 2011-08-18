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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Caspar De Groot
 */
public class OfflineLockingTest extends AbstractSyncingTest
{
  public void testLockAndUnlockThrough() throws Exception
  {
    // InternalRepository clone = getRepository();
    // InternalRepository master = getRepository(clone.getName() + "_master");

    CDOSession masterSession = openSession(getRepository().getName() + "_master");
    // CDOTransaction masterTx = masterSession.openTransaction();

    CDOSession cloneSession = openSession();
    waitForOnline(cloneSession.getRepositoryInfo());

    CDOTransaction cloneTx = cloneSession.openTransaction();
    cloneTx.enableDurableLocking(true);

    CDOResource res = cloneTx.createResource(getResourcePath("test"));
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    cloneTx.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    cdoCompany.cdoWriteLock().lock();

    CDOView masterView = masterSession.openView();
    CDOObject cdoCompanyOnMaster = masterView.getObject(cdoCompany.cdoID());
    assertEquals(true, cdoCompanyOnMaster.cdoWriteLock().isLockedByOthers());

    // int viewID = cloneTx.getViewID();
    // master.getLockManager().hasLock(LockType.WRITE, context, objectToLock);

    cdoCompany.cdoWriteLock().unlock();
    assertEquals(false, cdoCompanyOnMaster.cdoWriteLock().isLockedByOthers());

    cloneSession.close();
    masterSession.close();
  }

  public void testMasterLocks_ArrivalInClone() throws Exception
  {

  }
}
