/*
 * Copyright (c) 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;

/**
 * Test {@link CDORevisionUtil#getResourceNodePath(org.eclipse.emf.cdo.common.revision.CDORevision, org.eclipse.emf.cdo.common.revision.CDORevisionProvider)} with fragments.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_442178_Test extends AbstractCDOTest
{
  private String path;

  private String resource1Path;

  private String resource2Path;

  private CDOView view;

  private CDOResource resource1;

  private CDOResource resource2;

  private Company company;

  private Category category1;

  private Category category2;

  private Category category3;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();

    path = getResourcePath("folder");
    resource1Path = path + "/resource1.model1";
    resource2Path = path + "/resource2.model1";
    resource1 = tx.createResource(resource1Path);
    resource2 = tx.createResource(resource2Path);
    company = getModel1Factory().createCompany();
    category1 = getModel1Factory().createCategory();
    company.getCategories().add(category1);
    category2 = getModel1Factory().createCategory();
    category1.getCategories().add(category2);
    category3 = getModel1Factory().createCategory();
    category2.getCategories().add(category3);
    resource1.getContents().add(company);
    resource2.getContents().add(category2);
    tx.commit();
    assertEquals(resource2, category2.eResource());
    assertEquals(category1, category2.eContainer());
    tx.close();
    session.close();

    session = openSession();
    view = session.openView();

    resource1 = view.getResource(resource1Path);
    resource2 = view.getResource(resource2Path);
    company = (Company)resource1.getContents().get(0);
    category1 = company.getCategories().get(0);
    category2 = (Category)resource2.getContents().get(0);
    category3 = category2.getCategories().get(0);
    assertEquals(resource2, category2.eResource());
    assertEquals(category1, category2.eContainer());

    assertEquals(resource2, category2.eResource());
    assertEquals(category1, category2.eContainer());
  }

  public void testCDORevisionUtil_GetResourceNodePath_WithCDOResourceFolder() throws Exception
  {
    testCDORevisionUtil_GetResourceNodePath(resource1.getFolder(), path);
    testCDORevisionUtil_GetResourceNodePath(resource2.getFolder(), path);
  }

  public void testCDORevisionUtil_GetResourceNodePath_WithCDOResource() throws Exception
  {
    testCDORevisionUtil_GetResourceNodePath(resource1, resource1Path);
    testCDORevisionUtil_GetResourceNodePath(resource2, resource2Path);
  }

  public void testCDORevisionUtil_GetResourceNodePath_WithRootCDOObject() throws Exception
  {
    testCDORevisionUtil_GetResourceNodePath(company, resource1Path);
  }

  public void testCDORevisionUtil_GetResourceNodePath_WithNotRootCDOObject() throws Exception
  {
    testCDORevisionUtil_GetResourceNodePath(category1, resource1Path);
  }

  public void testCDORevisionUtil_GetResourceNodePath_WithRootCDOObjectInFragmentedResource() throws Exception
  {
    testCDORevisionUtil_GetResourceNodePath(category2, resource2Path);
  }

  public void testCDORevisionUtil_GetResourceNodePath_WithNotRootCDOObjectInFragmentedResource() throws Exception
  {
    testCDORevisionUtil_GetResourceNodePath(category3, resource2Path);
  }

  private void testCDORevisionUtil_GetResourceNodePath(EObject eObject, String expectedResourcePath)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
    String resourceNodePath = CDORevisionUtil.getResourceNodePath(cdoObject.cdoID(), view);
    assertEquals(expectedResourcePath, resourceNodePath);
    resourceNodePath = CDORevisionUtil.getResourceNodePath(cdoObject.cdoRevision(), view);
    assertEquals(expectedResourcePath, resourceNodePath);
  }
}
