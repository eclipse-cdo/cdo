/*
 * Copyright (c) 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.junit.Assert;

import java.util.Collections;

/**
 * Bug 467174 to test lock state and revision prefetch.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_467174_Test extends AbstractCDOTest
{
  public void testCDOObject_LockStateAndRevisionPrefetch() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    transaction1.options().setLockNotificationEnabled(true);
    new CDOLockStatePrefetcher(transaction1, false);

    CDOResource resource1 = transaction1.createResource(getResourcePath("test1.model1"));
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    resource1.getContents().add(company);
    resource1.save(Collections.emptyMap());

    CDOObject companyCDOObject = CDOUtil.getCDOObject(company);
    CDOID companyCDOID = companyCDOObject.cdoID();
    CDOObject categoryCDOObject = CDOUtil.getCDOObject(category);
    CDOID categoryCDOID = categoryCDOObject.cdoID();

    CDOSession session2 = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.eAdapters().add(new AdapterImpl()
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        Object newValue = msg.getNewValue();
        if (newValue instanceof CDOResource)
        {
          ((CDOResource)newValue).cdoPrefetch(CDORevision.DEPTH_INFINITE);
        }
      }
    });

    CDOTransaction transaction2 = session2.openTransaction(resourceSet);
    transaction2.options().setLockNotificationEnabled(true);
    new CDOLockStatePrefetcher(transaction2, false);

    transaction1.lockObjects(Collections.singletonList(categoryCDOObject), LockType.WRITE, 1);
    transaction1.lockObjects(Collections.singletonList(companyCDOObject), LockType.WRITE, 1);

    CDOObject companyCDOObjectFromTx2 = transaction2.getObject(companyCDOID);
    CDOObject categoryCDOObjectFromTx2 = transaction2.getObject(categoryCDOID);
    Assert.assertTrue(companyCDOObjectFromTx2.cdoWriteLock().isLockedByOthers());
    Assert.assertTrue(categoryCDOObjectFromTx2.cdoWriteLock().isLockedByOthers());
  }
}
