/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.model.resource.CDOContentsFeature;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.common.revision.CDOReferenceProxy;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Company;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 */
public class ViewTest extends AbstractCDOTest
{
  public void testHasResource() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/test1");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    assertEquals(true, view.hasResource("/test1"));
    assertEquals(false, view.hasResource("/test2"));
    session.close();
  }

  public void testGetOrCreateResource() throws Exception
  {
    String id;
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      transaction.commit();
      id = resource.cdoID().toString();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    assertEquals(id, transaction.getOrCreateResource("/test1").cdoID().toString());
    assertNotSame(id, transaction.getOrCreateResource("/test2").cdoID().toString());
    session.close();
  }

  public void testUniqueResourceContents() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      EList<EObject> contents = resource.getContents();
      for (int i = 0; i < 100; i++)
      {
        Company company = getModel1Factory().createCompany();
        company.setName("Company " + i);
        contents.add(company);
      }

      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(2);

    CDOTransaction transaction = session.openTransaction();
    transaction.setUniqueResourceContents(true);

    CDOResource resource = transaction.getResource("/test1");
    EList<EObject> contents = resource.getContents();
    for (int i = 100; i < 110; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Company " + i);
      contents.add(company);
    }

    CDORevisionData revision = resource.cdoRevision().getData();
    CDOResourcePackage resourcePackage = session.getPackageManager().getCDOResourcePackage();
    CDOContentsFeature contentsFeature = resourcePackage.getCDOResourceClass().getCDOContentsFeature();
    assertEquals(true, revision.get(contentsFeature, 99) instanceof CDOReferenceProxy);
    assertEquals(false, revision.get(contentsFeature, 100) instanceof CDOReferenceProxy);
    session.close();
  }

  public void testNonUniqueResourceContents() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      EList<EObject> contents = resource.getContents();
      for (int i = 0; i < 100; i++)
      {
        Company company = getModel1Factory().createCompany();
        company.setName("Company " + i);
        contents.add(company);
      }

      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(2);

    CDOTransaction transaction = session.openTransaction();
    transaction.setUniqueResourceContents(false);

    CDOResource resource = transaction.getResource("/test1");
    EList<EObject> contents = resource.getContents();
    for (int i = 100; i < 110; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Company " + i);
      contents.add(company);
    }

    CDORevisionData revision = resource.cdoRevision().getData();
    CDOResourcePackage resourcePackage = session.getPackageManager().getCDOResourcePackage();
    CDOContentsFeature contentsFeature = resourcePackage.getCDOResourceClass().getCDOContentsFeature();
    assertEquals(false, revision.get(contentsFeature, 0) instanceof CDOReferenceProxy);
    assertEquals(false, revision.get(contentsFeature, 1) instanceof CDOReferenceProxy);
    assertEquals(true, revision.get(contentsFeature, 2) instanceof CDOReferenceProxy);
    assertEquals(true, revision.get(contentsFeature, 99) instanceof CDOReferenceProxy);
    assertEquals(false, revision.get(contentsFeature, 100) instanceof CDOReferenceProxy);
    session.close();
  }

  public void testExternalResourceSet() throws Exception
  {
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction(resourceSet);
      transaction.createResource("/test1");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    assertEquals(true, view.hasResource("/test1"));
    assertEquals(false, view.hasResource("/test2"));
    session.close();
  }

  public void testContextify() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    transaction.commit();

    CDOView view = session.openView();
    CDOResource resource2 = view.getObject(resource);
    assertEquals("/test1", resource2.getPath());
    session.close();
  }

  public void testContextifyDifferentSession() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    transaction.commit();

    CDOSession session2 = openModel1Session();
    CDOView view = session2.openView();
    try
    {
      view.getObject(resource);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException success)
    {
    }
    finally
    {
      session.close();
      session2.close();
    }
  }
}
