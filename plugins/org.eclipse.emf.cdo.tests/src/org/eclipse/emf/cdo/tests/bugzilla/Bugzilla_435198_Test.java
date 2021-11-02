/*
 * Copyright (c) 2014, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Bug 435198 about {@link CDOResource} added to the {@link ResourceSet} even when we call {@link CDOView#getObject(CDOID)}
 * for an object contained in a {@link CDOResource} not yet added to the {@link ResourceSet}.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_435198_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  private CDOID companyID;

  private CDOID resourceCDOID;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();
    resourceCDOID = resource.cdoID();
    companyID = CDOUtil.getCDOObject(resource.getContents().get(0)).cdoID();
  }

  /**
   * Test origin of the bug, a {@link CDOView#getObject(CDOID)} call for an object contained in a {@link CDOResource} not yet added to the {@link ResourceSet}.
   */
  public void testAbstractCDOView_GetObject_ResourceSetAddition() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();

    CDOObject company = view.getObject(companyID);

    assertEquals(1, view.getResourceSet().getResources().size());
    assertEquals(view.getResourceSet().getResources().get(0), company.eResource());
  }

  /**
   * Test the simple working case, a {@link CDOView#getObject(CDOID)} with a CDOResource's id call add the {@link Resource} to the {@link ResourceSet}.
   */
  public void testAbstractCDOView_GetObjectWithCDOResourceId_ResourceSetAddition() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();

    CDOObject object = view.getObject(resourceCDOID);
    assertTrue(object instanceof CDOResource);
    CDOResource resource = (CDOResource)object;
    EObject company = resource.getContents().get(0);

    assertEquals(1, view.getResourceSet().getResources().size());
    assertEquals(view.getResourceSet().getResources().get(0), company.eResource());
  }

  /**
   * Test the simple working case, a {@link CDOView#getResource(String)} call add the {@link Resource} to the {@link ResourceSet}.
   */
  public void testAbstractCDOView_GetResource_ResourceSetAddition() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();

    CDOResource resource = view.getResource(getResourcePath(RESOURCE_NAME));
    EObject company = resource.getContents().get(0);

    assertEquals(1, view.getResourceSet().getResources().size());
    assertEquals(view.getResourceSet().getResources().get(0), company.eResource());
  }
}
