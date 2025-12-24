/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Cannot load resource on a previously cleared ResourceSet
 * <p>
 * See Bug 338921
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_338921_Test extends AbstractCDOTest
{
  public void testLoadResourceAfterSingleRemoval() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/Bug338921"));
    resource.getContents().add(company);

    transaction.commit();

    URI uri = resource.getURI();

    ResourceSet resourceSet = resource.getResourceSet();
    resourceSet.getResources().remove(resource);

    assertInvalid(resource);

    Resource resource2 = resourceSet.getResource(uri, true);
    assertNotNull(resource2);
    // forcing transition from PROXY to CLEAN. Revision is loaded
    resource2.getContents().size();
    assertClean((EObject)resource2, transaction);

  }

  public void testRemoveDirtyResourceOnResourceSetWithSingleResource() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/Bug338921"));
    resource.getContents().add(company);

    transaction.commit();

    company.setName("foobar");
    URI uri = resource.getURI();

    ResourceSet resourceSet = resource.getResourceSet();
    resourceSet.getResources().remove(resource);

    Resource resource2 = resourceSet.getResource(uri, true);
    assertNotNull(resource2);
    // forcing transition from PROXY to CLEAN. Revision is loaded
    resource2.getContents().size();
    assertClean((EObject)resource2, transaction);

  }

  public void testRemoveDirtyResourceOnResourceSetWithMultipleResource() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/Bug338921/res1"));
    CDOResource resource2 = transaction.createResource(getResourcePath("/Bug338921/res2"));
    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();

    resource1.getContents().add(company1);
    resource2.getContents().add(company2);

    transaction.commit();

    company1.setName("foo");
    company2.setName("bar");

    ResourceSet resourceSet = resource1.getResourceSet();

    resourceSet.getResources().remove(resource1);
    assertEquals(true, transaction.isDirty());
  }

  public void testLoadResourceAfterClearOnCleanResourceSet() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/Bug338921/res1"));
    transaction.createResource(getResourcePath("/Bug338921/res2"));
    resource1.getContents().add(company);

    transaction.commit();

    URI uri = resource1.getURI();
    company.setName("foobar");

    ResourceSet resourceSet = resource1.getResourceSet();
    resourceSet.getResources().clear();

    assertEquals(true, transaction.isDirty());
    assertInvalid(resource1);

    Resource resource3 = resourceSet.getResource(uri, true);
    assertNotNull(resource3);
    // forcing transition from PROXY to CLEAN. Revision is loaded
    resource3.getContents().size();
    assertClean((EObject)resource3, transaction);

  }

  public void testLoadResourceAfterClearOnDirtyResourceSet() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/Bug338921/res1"));
    CDOResource resource2 = transaction.createResource(getResourcePath("/Bug338921/res2"));
    resource1.getContents().add(company);

    transaction.commit();

    URI uri1 = resource1.getURI();
    URI uri2 = resource2.getURI();
    company.setName("foobar");

    ResourceSet resourceSet = resource1.getResourceSet();
    resourceSet.getResources().clear();

    assertEquals(true, transaction.isDirty());
    assertInvalid(resource1);
    assertInvalid(resource2);

    CDOResource resource3 = (CDOResource)resourceSet.getResource(uri1, true);
    CDOResource resource4 = (CDOResource)resourceSet.getResource(uri2, true);
    assertNotNull(resource3);
    assertNotNull(resource4);
    // forcing transition from PROXY to CLEAN. Revision is loaded
    resource3.getContents().size();
    assertClean(resource3, transaction);
    resource4.getContents().size();
    assertClean(resource4, transaction);
  }

}
