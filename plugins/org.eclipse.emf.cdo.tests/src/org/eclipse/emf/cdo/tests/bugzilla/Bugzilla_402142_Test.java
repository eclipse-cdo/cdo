/*
 * Copyright (c) 2013, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Steve Monnier - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import java.util.ArrayList;

/**
 * Bug 385268.
 *
 * @author Steve Monnier
 */
public class Bugzilla_402142_Test extends AbstractCDOTest
{
  /**
   * Scenario #1
   * <ol>
   * <li>User A : Create a CDOResource and add a Company root and commit
   * <li>User B : Lock the Company element
   * <li>User A : Ask if Company element is lockedByOther -> OK, it's locked
   * </ol>
   */
  public void testCheckLockByOther() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setLockNotificationEnabled(true);

    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    final CDOLock writeLock = CDOUtil.getCDOObject(company).cdoWriteLock();
    assertEquals(false, writeLock.isLockedByOthers());

    lockCompanyRemotely();
    assertNoTimeout(writeLock::isLockedByOthers);

    transaction.commit();
  }

  /**
   * Scenario #2
   * <ol>
   * <li>User A : Create a CDOResource and add a Company root and commit
   * <li>User A : Lock and Unlock the Company element
   * <li>User B : Lock the Company element
   * <li>User A : Ask if Company element is lockedByOther -> OK, it's locked
   * </ol>
   */
  public void testCheckLockByOtherAfterLockUnlock() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setLockNotificationEnabled(true);

    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    final CDOLock writeLock = CDOUtil.getCDOObject(company).cdoWriteLock();
    writeLock.lock(DEFAULT_TIMEOUT);
    writeLock.unlock();
    assertEquals(false, writeLock.isLockedByOthers());

    lockCompanyRemotely();
    assertNoTimeout(writeLock::isLockedByOthers);

    transaction.commit();
  }

  /**
   * Scenario #3
   * <ol>
   * <li>Create a local resource and a CDOResource
   * <li>Add an element "company1" to CDOResource
   * <li>Add an element "company2" to the local resource
   * <li>Add a Customer to "comapny1" commit
   * <li>Move the customer to the local resource
   * <li>Lock "company1" and customer (to avoid conflict from a distant user)
   * <li>Commit
   * <li>Move back the customer to "company1"
   * <li>Add lock to customer (lock on new object)
   * <li>Commit -> The new object should not have the lockstate of the former one
   * </ol>
   */
  public void testLockUnlockOnElementMovedBetweenResources() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setLockNotificationEnabled(true);

    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    Customer customer = getModel1Factory().createCustomer();
    company.getCustomers().add(customer);
    transaction.commit();

    ResourceSet resourceSet = transaction.getResourceSet();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model1", new XMLResourceFactoryImpl());
    URI localURI = URI.createFileURI(createTempFile(getName(), ".model1").getCanonicalPath());
    Resource resource2 = resourceSet.createResource(localURI);
    Company company2 = getModel1Factory().createCompany();
    resource2.getContents().add(company2);
    company2.getCustomers().add(customer); // Move the customer to the local resource (detach)

    ArrayList<CDOObject> objectsToLock = new ArrayList<>();
    objectsToLock.add(CDOUtil.getCDOObject(company));
    objectsToLock.add(CDOUtil.getCDOObject(customer));
    transaction.lockObjects(objectsToLock, LockType.WRITE, DEFAULT_TIMEOUT);

    transaction.commit();

    company.getCustomers().add(customer); // Move the customer to the CDO resource (attach new)
    objectsToLock.add(CDOUtil.getCDOObject(customer)); // New object
    transaction.lockObjects(objectsToLock, LockType.WRITE, DEFAULT_TIMEOUT);

    transaction.commit();
  }

  private void lockCompanyRemotely() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/my/resource"));
    CDOLock writeLock = CDOUtil.getCDOObject(resource.getContents().get(0)).cdoWriteLock();
    writeLock.lock(DEFAULT_TIMEOUT);
  }
}
