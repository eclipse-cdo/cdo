/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper            - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Victor Roldan Betancort
 */
public class ResourceModificationTrackingTest extends AbstractCDOTest
{
  public void testResourceModificationTracking() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    CDOResource resource2 = transaction.createResource(getResourcePath("/my/resource2"));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction.commit();

    // Modification tracking is NOT enabled by default
    company.setName("foobar");
    assertEquals(false, resource1.isModified());
    assertEquals(false, resource2.isModified());
    transaction.commit();

    // Enable modification tracking in a particular resource
    resource1.setTrackingModification(true);
    company.setName("foobar2");
    assertEquals(true, resource1.isModified());
    assertEquals(false, resource2.isModified());

    transaction.commit();
    assertEquals(false, resource1.isModified());
    assertEquals(false, resource2.isModified());

    // Perform changes in a resource that does not track changes
    // but is in the same CDOView as one that does have it activated

    resource2.getContents().add(getModel1Factory().createCompany());
    assertEquals(false, resource2.isModified());

    transaction.commit();
    assertEquals(false, resource2.isModified());
  }

  public void testResourceModificationTracking2() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    transaction.commit();

    resource1.setTrackingModification(true);
    assertEquals(false, resource1.isModified());

    resource1.getContents().add(company);
    assertEquals(true, resource1.isModified());

    resource1.getContents().remove(company);
    assertEquals(true, resource1.isModified());
  }

  /**
   * Check Resource modification for NEW Resources
   */
  public void testNewResourceModificationTracking() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    resource1.setTrackingModification(true);
    assertEquals(false, resource1.isModified());

    Company company = getModel1Factory().createCompany();
    company.setName("foobar");
    resource1.getContents().add(company);
    assertEquals(true, resource1.isModified());

    transaction.commit();
    assertEquals(false, resource1.isModified());
  }

  /**
   * Check Resource modification for NEW Resources
   */
  public void testNewResourceModificationTracking2() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    resource1.setTrackingModification(true);
    assertEquals(false, resource1.isModified());

    resource1.getContents().add(company);
    assertEquals(true, resource1.isModified());

    resource1.getContents().remove(company);
    assertEquals(true, resource1.isModified());
  }

  /**
   * Check Resource modification for NEW Resources
   */
  public void testIgnoreChangesBeforeTrackingEnablement() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    resource1.setTrackingModification(false);
    assertEquals(false, resource1.isModified());

    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    resource1.setTrackingModification(true);
    assertEquals(false, resource1.isModified());

    company.setName("foobar");
    assertEquals(true, resource1.isModified());

    transaction.commit();
    assertEquals(false, resource1.isModified());
  }

  /**
   * This test is wrong. Undo is not spec'ed !!!
   * <p>
   * If a NEW Resource gets its content removed, then we shall consider its back to isModified = false
   */
  public void _testNewResourceModificationTrackingUndo() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    resource1.setTrackingModification(true);
    assertEquals(false, resource1.isModified());

    Company company1 = getModel1Factory().createCompany();
    company1.setName("foobar1");
    Company company2 = getModel1Factory().createCompany();
    company2.setName("foobar2");
    resource1.getContents().add(company1);
    resource1.getContents().add(company2);
    assertEquals(true, resource1.isModified());

    resource1.getContents().remove(company1);
    assertEquals(true, resource1.isModified());

    resource1.getContents().remove(company2);
    assertEquals(false, resource1.isModified());
  }

  public void testRollbackOperation() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);
    transaction.commit();

    // Enable modification tracking
    resource1.setTrackingModification(true);
    company.setName("foobar2");
    assertEquals(true, resource1.isModified());

    transaction.rollback();
    assertEquals(false, resource1.isModified());
  }

  public void testPartialRollbackOperation() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();
    resource1.getContents().add(company1);
    resource1.getContents().add(company2);
    transaction.commit();

    // Enable modification tracking
    resource1.setTrackingModification(true);
    CDOSavepoint savePoint1 = transaction.setSavepoint();
    company1.setName("foobar2");
    CDOSavepoint savePoint2 = transaction.setSavepoint();
    company2.setName("foobar2");
    assertEquals(true, resource1.isModified());

    savePoint2.rollback();
    assertEquals(true, resource1.isModified());

    savePoint1.rollback();
    assertEquals(false, resource1.isModified());
  }

  public void testResourceModificationTrackingNotification() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource1"));
    resource.setTrackingModification(true);
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    // Add a Listener
    TestAdapter testAdapter = new TestAdapter(resource);
    company.setName("foobar");
    assertEquals(Resource.RESOURCE__IS_MODIFIED, testAdapter.getNotifications()[0].getFeatureID(null));
    transaction.commit();
  }

}
