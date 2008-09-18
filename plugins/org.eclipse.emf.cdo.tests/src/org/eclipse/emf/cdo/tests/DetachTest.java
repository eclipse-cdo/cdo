/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import junit.framework.Assert;

/**
 * @author Simon McDuff
 */
public class DetachTest extends AbstractCDOTest
{
  public void testNewObjectDeletion() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company c1 = getModel1Factory().createCompany();
    c1.setName("Test");
    resource.getContents().add(c1);

    final URI uriC1 = EcoreUtil.getURI(c1);

    assertEquals(c1, transaction.getResourceSet().getEObject(uriC1, false));

    resource.getContents().remove(0); // remove object by index

    try
    {
      transaction.getResourceSet().getEObject(uriC1, false);
      fail("Object shouldn't be available anymore");
    }
    catch (Exception ex)
    {

    }

    transaction.commit();
  }

  public void testCleanObjectDeletion() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company c1 = getModel1Factory().createCompany();
    c1.setName("Test");
    resource.getContents().add(c1);
    transaction.commit(); // (1)

    final URI uriC1 = EcoreUtil.getURI(c1);

    assertEquals(c1, transaction.getResourceSet().getEObject(uriC1, false));

    resource.getContents().remove(c1);

    assertTransient(c1);

    // We should not be able to access that objects
    try
    {
      transaction.getResourceSet().getEObject(uriC1, false);
      fail("Cannot access objects detach");
    }
    catch (Exception excp)
    {

    }

    transaction.commit();

    assertTransient(c1);

    try
    {
      transaction.getResourceSet().getEObject(uriC1, false);
      fail("Cannot access objects detach");
    }
    catch (Exception excp)
    {

    }

  }

  public void testSavePointNewObjectDeletion() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company c1 = getModel1Factory().createCompany();
    c1.setName("Test");
    resource.getContents().add(c1);

    testSavePointObjectDeletion(transaction, resource);
  }

  public void testSavePointCleanObjectDeletion() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company c1 = getModel1Factory().createCompany();
    c1.setName("Test");
    resource.getContents().add(c1);

    URI uriC1 = EcoreUtil.getURI(c1);

    assertEquals(c1, transaction.getResourceSet().getEObject(uriC1, false));

    transaction.commit();

    testSavePointObjectDeletion(transaction, resource);

  }

  private void testSavePointObjectDeletion(CDOTransaction transaction, CDOResource resource)
  {
    Company c1 = (Company)resource.getContents().get(0);

    URI uriC1 = EcoreUtil.getURI(c1);

    c1.setName("SIMON");

    CDOSavepoint savepoint = transaction.setSavepoint();

    resource.getContents().remove(0); // remove object by index

    CDOSavepoint savepoint2 = transaction.setSavepoint();
    try
    {
      transaction.getResourceSet().getEObject(uriC1, false);
      fail("Object shouldn't be available anymore");
    }
    catch (Exception ex)
    {

    }
    savepoint2.rollback();

    assertEquals("SIMON", c1.getName());

    try
    {
      transaction.getResourceSet().getEObject(uriC1, false);
      fail("Object shouldn't be available anymore");
    }
    catch (Exception ex)
    {

    }

    savepoint.rollback();

    assertEquals("SIMON", c1.getName());
    assertEquals(c1, transaction.getResourceSet().getEObject(uriC1, false));

    transaction.commit();
  }

  public void testKeepValue() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");

      Company c1 = getModel1Factory().createCompany();
      c1.setName("Test");
      resource.getContents().add(c1);

      transaction.commit();
    }

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource("/my/resource");

    Company c1 = (Company)resource.getContents().get(0); // remove object by index

    assertEquals("Test", c1.getName());

    resource.getContents().remove(0);

    assertEquals("Test", c1.getName());

    transaction.commit();

    assertEquals("Test", c1.getName());
  }

  private void detachResource(ResourceSet rset, CDOResource resource) throws Exception
  {
    Order order = getModel1Factory().createOrder();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    resource.getContents().add(order);
    order.getOrderDetails().add(orderDetail);

    assertActive(resource);
    Assert.assertEquals(1, CDOUtil.getViewSet(rset).getViews().length);
    Assert.assertEquals(1, rset.getResources().size());

    resource.delete(null);

    assertTransient(resource);

    Assert.assertEquals(1, CDOUtil.getViewSet(rset).getViews().length);
    Assert.assertEquals(0, rset.getResources().size());
  }

  public void testDetachNewResource() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    ResourceSet rset = transaction.getResourceSet();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");
    detachResource(rset, resource);

  }

  public void testDetachPersistedResource() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    ResourceSet rset = transaction.getResourceSet();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    transaction.commit();
    CDOID resourceID = resource.cdoID();
    detachResource(rset, resource);

    assertEquals(true, transaction.getDetachedObjects().contains(resourceID));
  }

}
