/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.SessionUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class InitialTest extends AbstractCDOTest
{
  public void testTransientObject() throws Exception
  {
    final Date date = new Date();

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();
    assertTransient(supplier);

    msg("Setting name");
    supplier.setName("Stepper");
    assertTransient(supplier);

    msg("Verifying name");
    assertEquals("Stepper", supplier.getName());
    assertTransient(supplier);

    msg("Creating purchaseOrder");
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    assertTransient(purchaseOrder);

    msg("Setting date");
    purchaseOrder.setDate(date);
    assertTransient(purchaseOrder);

    msg("Verifying date");
    assertEquals(date, purchaseOrder.getDate());
    assertTransient(purchaseOrder);

    msg("Setting supplier");
    purchaseOrder.setSupplier(supplier);
    assertTransient(supplier);
    assertTransient(purchaseOrder);

    msg("Verifying supplier");
    assertEquals(supplier, purchaseOrder.getSupplier());
    assertTransient(supplier);
    assertTransient(purchaseOrder);
  }

  public void testTransientResource() throws Exception
  {
    final URI uri = URI.createURI("cdo:/test1");

    msg("Creating resourceSet");
    ResourceSet resourceSet = new ResourceSetImpl();
    SessionUtil.prepareResourceSet(resourceSet);

    msg("Creating resource");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertTransient(resource);

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();
    assertTransient(supplier);
    assertEquals(null, supplier.eContainer());

    msg("Verifying contents");
    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);
    assertEquals(true, contents.isEmpty());
    assertEquals(0, contents.size());
    assertTransient(resource);

    msg("Adding supplier");
    contents.add(supplier);
    assertTransient(resource);
    assertTransient(supplier);
    assertContent(resource, supplier);

    assertEquals(true, resourceSet.getResources().contains(resource));
    resource.delete(null);
    assertEquals(false, resourceSet.getResources().contains(resource));
    assertTransient(supplier);
  }

  public void testOpenSession() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();
    assertNotNull(session);
    assertEquals(false, session.isClosed());
    assertEquals(RepositoryConfig.REPOSITORY_NAME, session.getRepositoryInfo().getName());
    assertEquals(RepositoryConfig.REPOSITORY_NAME, session.getRepositoryInfo().getUUID());
  }

  public void testStartTransaction() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    assertNotNull(transaction);
    assertEquals(session, transaction.getSession());
  }

  public void testAttachResource() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    assertNew(resource, transaction);
    assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")),
        resource.getURI());
    ResourceSet expected = transaction.getResourceSet();
    ResourceSet actual = resource.getResourceSet();
    assertEquals(expected, actual);
  }

  public void testAttachObject() throws Exception
  {
    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Adding supplier");
    contents.add(supplier);
    assertNew(supplier, transaction);
    assertEquals(transaction, CDOUtil.getCDOObject(supplier).cdoView());
    assertEquals(resource, CDOUtil.getCDOObject(supplier).cdoDirectResource());
    assertEquals(0, CDOUtil.getCDOObject(supplier).cdoRevision().getVersion());
    assertEquals(resource, supplier.eResource());
    assertEquals(null, supplier.eContainer());
  }

  public void testCommitNew() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    CDOCommitInfo commit = transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(supplier).cdoState());
    assertEquals(1, CDOUtil.getCDOObject(supplier).cdoRevision().getVersion());
    assertCreatedTime(resource, commit.getTimeStamp());
    assertCreatedTime(supplier, commit.getTimeStamp());
  }

  public void testReadResourceClean() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    long commitTime = transaction.commit().getTimeStamp();

    msg("Getting supplier");
    EList<EObject> contents = resource.getContents();
    Supplier s = (Supplier)contents.get(0);
    assertEquals(1, CDOUtil.getCDOObject(s).cdoRevision().getVersion());
    assertEquals(supplier, s);
    assertCreatedTime(s, commitTime);
  }

  public void testReadObjectClean() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();

    msg("Getting supplier");
    Supplier s = (Supplier)resource.getContents().get(0);
    assertEquals(1, CDOUtil.getCDOObject(s).cdoRevision().getVersion());

    msg("Verifying name");
    assertEquals("Stepper", s.getName());
  }

  public void testWriteClean() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();

    msg("Getting supplier");
    Supplier s = (Supplier)resource.getContents().get(0);

    msg("Setting name");
    s.setName("Eike");
    assertEquals("Eike", s.getName());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(supplier).cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());
  }

  public void testCommitDirty() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    resource.getContents().add(supplier);

    CDOCommitInfo commit = transaction.commit();
    long commitTime1 = commit.getTimeStamp();
    assertCreatedTime(supplier, commitTime1);

    supplier.setName("Eike");

    long commitTime2 = transaction.commit().getTimeStamp();
    assertEquals(true, commitTime1 < commitTime2);
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(supplier).cdoState());
    assertCreatedTime(supplier, commitTime2);
  }

  public void testGetResource() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Supplier supplier = getModel1Factory().createSupplier();

      msg("Setting name");
      supplier.setName("Stepper");

      msg("Adding supplier");
      resource.getContents().add(supplier);

      msg("Committing");
      transaction.commit();
      enableConsole();
    }

    {
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Getting resource");
      CDOResource resource = transaction.getResource(getResourcePath("/test1"), true);
      assertNotNull(resource);
      assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")),
          resource.getURI());
      assertEquals(transaction.getResourceSet(), resource.getResourceSet());
      assertEquals(1, transaction.getResourceSet().getResources().size());
      assertEquals(CDOState.CLEAN, resource.cdoState());
      assertEquals(transaction, resource.cdoView());
      assertNotNull(resource.cdoRevision());
    }

    {
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Getting resource");
      CDOResource resource = (CDOResource)transaction.getResourceSet().getResource(
          CDOURIUtil.createResourceURI(transaction, getResourcePath("/test1")), true);
      assertNotNull(resource);
      assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")),
          resource.getURI());
      assertEquals(transaction.getResourceSet(), resource.getResourceSet());
      assertEquals(1, transaction.getResourceSet().getResources().size());
      assertEquals(CDOState.CLEAN, resource.cdoState());
      assertEquals(transaction, resource.cdoView());
      assertNotNull(resource.cdoRevision());
    }
  }

  public void testGetContents() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Supplier supplier = getModel1Factory().createSupplier();

      msg("Setting name");
      supplier.setName("Stepper");

      msg("Adding supplier");
      resource.getContents().add(supplier);

      msg("Committing");
      transaction.commit();
      enableConsole();
    }

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);

    Supplier supplier = (Supplier)contents.get(0);
    assertEquals("Stepper", supplier.getName());
  }

  public void testReadObjectProxy() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Supplier supplier = getModel1Factory().createSupplier();

      msg("Setting name");
      supplier.setName("Stepper");

      msg("Adding supplier");
      resource.getContents().add(supplier);

      msg("Committing");
      transaction.commit();
      enableConsole();
    }

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Getting supplier");
    Supplier s = (Supplier)contents.get(0);
    assertNotNull(s);

    msg("Verifying name");
    assertEquals("Stepper", s.getName());
  }

  public void testReadTransientValue() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Product1 product = getModel1Factory().createProduct1();

      msg("Setting name");
      product.setDescription("DESCRIPTION");
      product.setName("McDuff");

      msg("Adding supplier");
      resource.getContents().add(product);

      assertEquals("DESCRIPTION", product.getDescription());

      msg("Committing");
      transaction.commit();
      enableConsole();
    }

    msg("Opening transaction");
    CDOView view = session.openView();

    msg("Getting resource");
    CDOResource resource = view.getResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Getting supplier");
    Product1 s = (Product1)contents.get(0);
    assertNotNull(s);

    msg("Verifying name");
    assertEquals("McDuff", s.getName());

    assertNull(s.getDescription());

    s.setDescription("HELLO");

    assertEquals("HELLO", s.getDescription());
  }

  public void testLoadResource() throws Exception
  {
    {
      // disableConsole();
      msg("Opening session");
      CDOSession session = openSession();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Supplier supplier = getModel1Factory().createSupplier();

      msg("Setting name");
      supplier.setName("Stepper");

      msg("Adding supplier");
      resource.getContents().add(supplier);

      msg("Committing");
      transaction.commit();
      // XXX session.close();
      // enableConsole();
    }

    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    assertNotNull(resource);

    URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1"));
    URI resourceURI = resource.getURI();
    assertEquals(uri, resourceURI);
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(transaction, resource.cdoView());
    assertNotNull(resource.cdoRevision());
  }

  public void testLoadObject() throws Exception
  {
    {
      // disableConsole();
      msg("Opening session");
      CDOSession session = openSession();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating supplier");
      Supplier supplier = getModel1Factory().createSupplier();

      msg("Setting name");
      supplier.setName("Stepper");

      msg("Adding supplier");
      resource.getContents().add(supplier);

      msg("Committing");
      transaction.commit();
      // XXX session.close();
      enableConsole();
    }

    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Getting supplier");
    Supplier s = (Supplier)contents.get(0);
    assertNotNull(s);

    msg("Verifying name");
    assertEquals("Stepper", s.getName());
  }

  /**
   * bug 226317
   */
  public void testMultipleInheritence() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    OrderAddress orderAddress = getModel1Factory().createOrderAddress();
    resource.getContents().add(orderAddress);

    assertEquals(getModel1Package().getAddress_City().getFeatureID(), getModel1Package().getOrderDetail_Price()
        .getFeatureID());

    orderAddress.setCity("ALLO");
    orderAddress.setPrice(2.8f);
    orderAddress.setTestAttribute(true);

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    resource.getContents().add(orderDetail);
    orderDetail.setPrice(3f);

    transaction.commit();

    orderAddress.setCity("ALLO");

    transaction.commit();
    session.close();

    session = openSession();

    msg("Opening transaction");
    transaction = session.openTransaction();
    orderAddress = (OrderAddress)CDOUtil.getEObject(transaction.getObject(CDOUtil.getCDOObject(orderAddress).cdoID(),
        true));

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    orderAddress.setPrice(2.8f);
    transaction.commit();
    session.close();

    session = openSession();

    transaction = session.openTransaction();
    orderAddress = (OrderAddress)CDOUtil.getEObject(transaction.getObject(CDOUtil.getCDOObject(orderAddress).cdoID(),
        true));

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    orderAddress.setPrice(2.8f);

    session.close();
  }

  public void testNullReference() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openSession();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      msg("Creating orderDetail");
      OrderDetail orderDetail = getModel1Factory().createOrderDetail();

      msg("Setting price");
      orderDetail.setPrice(4.75f);

      msg("Adding orderDetail");
      resource.getContents().add(orderDetail);

      msg("Committing");
      transaction.commit();
      session.close();
    }

    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Getting supplier");
    OrderDetail orderDetail = (OrderDetail)contents.get(0);
    assertNotNull(orderDetail);

    msg("Verifying price");
    assertEquals(4.75f, orderDetail.getPrice());

    msg("Verifying product");
    assertEquals(null, orderDetail.getProduct());
  }

  public void testDirectResourceEMF() throws Exception
  {
    Resource resource1 = new XMLResourceImpl();

    EPackage p = EcoreFactory.eINSTANCE.createEPackage();
    EClass c = EcoreFactory.eINSTANCE.createEClass();

    resource1.getContents().add(p);
    p.getEClassifiers().add(c);

    assertEquals(null, ((InternalEObject)c).eDirectResource());
    assertEquals(resource1, ((InternalEObject)p).eDirectResource());
  }

  public void testDirectResource() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.getOrCreateResource(getResourcePath("/test1"));
    // Resource resource1 = new XMIResourceImpl();

    Category cat1 = getModel1Factory().createCategory();
    assertTransient(cat1);

    Category cat2 = getModel1Factory().createCategory();
    assertTransient(cat2);

    // resource1.getContents().add(cat2);
    resource1.getContents().add(cat1);
    cat1.getCategories().add(cat2);

    assertEquals(null, CDOUtil.getCDOObject(cat2).cdoDirectResource());
    assertEquals(resource1, CDOUtil.getCDOObject(cat1).cdoDirectResource());
    assertEquals(null, ((InternalEObject)cat2).eDirectResource());
    assertEquals(resource1, ((InternalEObject)cat1).eDirectResource());

    transaction.close();
    session.close();
  }

  public void testResourceAccessor() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    msg("Creating supplier");
    Supplier supplier = getModel1Factory().createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    URI supplierTempURI = EcoreUtil.getURI(supplier);

    msg("Retrieving supplier from URI before commit");
    EObject supplier1 = transaction.getResourceSet().getEObject(supplierTempURI, true);

    assertEquals(supplier, CDOUtil.getEObject(supplier1));

    msg("Committing");
    transaction.commit();

    URI supplierURI = EcoreUtil.getURI(supplier);

    msg("Retrieving supplier from URI after commit");
    EObject supplierFromURI = transaction.getResourceSet().getEObject(supplierURI, true);

    assertEquals(supplier, CDOUtil.getEObject(supplierFromURI));

    EObject supplierAfterCommit2 = transaction.getResourceSet().getEObject(supplierTempURI, true);
    if (session.getRepositoryInfo().getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      assertEquals(null, supplierAfterCommit2);
    }
    else
    {
      assertEquals(supplier, supplierAfterCommit2);
    }
  }
}
