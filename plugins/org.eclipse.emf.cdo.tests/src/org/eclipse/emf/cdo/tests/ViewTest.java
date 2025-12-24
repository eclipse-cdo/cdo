/*
 * Copyright (c) 2008-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ref.ReferenceType;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * @author Eike Stepper
 */
public class ViewTest extends AbstractCDOTest
{
  public void testDeactivateWithSession() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(true, LifecycleUtil.isActive(session));
    assertEquals(false, session.isClosed());

    CDOView view = session.openView();
    assertEquals(true, LifecycleUtil.isActive(view));
    assertEquals(false, view.isClosed());

    session.close();
    assertEquals(false, LifecycleUtil.isActive(session));
    assertEquals(true, session.isClosed());

    assertEquals(false, LifecycleUtil.isActive(view));
    assertEquals(true, view.isClosed());
  }

  public void testHasResource() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(getResourcePath("/test1"));
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    assertEquals(true, view.hasResource(getResourcePath("/test1")));
    assertEquals(false, view.hasResource(getResourcePath("/test2")));
    session.close();
  }

  public void testIsObjectRegisteredWithNull() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();
    assertEquals(false, view.isObjectRegistered(null));
    session.close();
  }

  public void testGetOrCreateResource() throws Exception
  {
    String id;
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      transaction.commit();
      id = resource.cdoID().toString();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    assertEquals(id, transaction.getOrCreateResource(getResourcePath("/test1")).cdoID().toString());
    assertNotSame(id, transaction.getOrCreateResource(getResourcePath("/test2")).cdoID().toString());
    session.close();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testUniqueResourceContents() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
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

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(2, 2));

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();
    for (int i = 100; i < 110; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Company " + i);
      contents.add(company);
    }

    CDORevisionData revision = resource.cdoRevision().data();
    EStructuralFeature contentsFeature = EresourcePackage.eINSTANCE.getCDOResource_Contents();
    assertEquals(true, revision.get(contentsFeature, 99) instanceof CDOElementProxy);
    assertEquals(false, revision.get(contentsFeature, 100) instanceof CDOElementProxy);
    session.close();
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testNonUniqueResourceContents() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
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

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(2, 2));

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();
    for (int i = 100; i < 110; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("Company " + i);
      contents.add(company);
    }

    CDORevisionData revision = resource.cdoRevision().data();
    EStructuralFeature contentsFeature = EresourcePackage.eINSTANCE.getCDOResource_Contents();
    assertEquals(false, revision.get(contentsFeature, 0) instanceof CDOElementProxy);
    assertEquals(false, revision.get(contentsFeature, 1) instanceof CDOElementProxy);
    assertEquals(true, revision.get(contentsFeature, 2) instanceof CDOElementProxy);
    assertEquals(true, revision.get(contentsFeature, 99) instanceof CDOElementProxy);
    assertEquals(false, revision.get(contentsFeature, 100) instanceof CDOElementProxy);
    session.close();
  }

  public void testExternalResourceSet() throws Exception
  {
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction(resourceSet);
      transaction.createResource(getResourcePath("/test1"));
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    assertEquals(true, view.hasResource(getResourcePath("/test1")));
    assertEquals(false, view.hasResource(getResourcePath("/test2")));
    session.close();
  }

  public void testContextify() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    transaction.commit();

    CDOView view = session.openView();
    CDOResource resource2 = view.getObject(resource);
    assertEquals(getResourcePath("/test1"), resource2.getPath());
    session.close();
  }

  public void testContextifyDifferentRepository() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    transaction.commit();

    getRepository("repo2");
    CDOSession session2 = openSession("repo2");
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

  public void testContextifySameRepository() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    transaction.commit();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource object = view.getObject(resource);
    assertNotSame(resource, object);
    assertEquals(resource.cdoID(), object.cdoID());

    session.close();
    session2.close();
  }

  public void testCacheReferences() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.createResource(getResourcePath("/my/test1"));
    transaction.commit();

    transaction.createResource(getResourcePath("/my/test2"));
    transaction.createResource(getResourcePath("/my/test3"));
    transaction.createResource(getResourcePath("/my/test4"));
    transaction.createResource(getResourcePath("/my/test5"));

    boolean done;
    done = transaction.options().setCacheReferenceType(ReferenceType.SOFT);
    assertEquals(false, done);

    done = transaction.options().setCacheReferenceType(null);
    assertEquals(false, done);

    done = transaction.options().setCacheReferenceType(ReferenceType.STRONG);
    assertEquals(true, done);

    done = transaction.options().setCacheReferenceType(ReferenceType.SOFT);
    assertEquals(true, done);

    done = transaction.options().setCacheReferenceType(ReferenceType.WEAK);
    assertEquals(true, done);

    done = transaction.options().setCacheReferenceType(null);
    assertEquals(true, done);

    session.close();
  }

  public void testViewNotifiesDeactivation()
  {
    CDOSession session = openSession();
    CDOView view = session.openView();

    final boolean[] deactivated = { false };
    view.addListener(new LifecycleEventAdapter()
    {
      @Override
      protected void onDeactivated(ILifecycle lifecycle)
      {
        deactivated[0] = true;
      }
    });

    view.close();
    assertEquals(true, deactivated[0]);
    session.close();
  }
}
