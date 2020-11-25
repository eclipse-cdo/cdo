/*
 * Copyright (c) 2007-2016, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
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
import org.eclipse.emf.internal.cdo.view.AbstractCDOView;

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
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.Date;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class InitialTest extends AbstractCDOTest
{
  public void testTransientObject() throws Exception
  {
    final Date date = new Date();

    Supplier supplier = getModel1Factory().createSupplier();
    assertTransient(supplier);

    supplier.setName("Stepper");
    assertTransient(supplier);

    assertEquals("Stepper", supplier.getName());
    assertTransient(supplier);

    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    assertTransient(purchaseOrder);

    purchaseOrder.setDate(date);
    assertTransient(purchaseOrder);

    assertEquals(date, purchaseOrder.getDate());
    assertTransient(purchaseOrder);

    purchaseOrder.setSupplier(supplier);
    assertTransient(supplier);
    assertTransient(purchaseOrder);

    assertEquals(supplier, purchaseOrder.getSupplier());
    assertTransient(supplier);
    assertTransient(purchaseOrder);
  }

  public void testTransientResource() throws Exception
  {
    final URI uri = URI.createURI("cdo://repo1/test1");

    ResourceSet resourceSet = new ResourceSetImpl();
    SessionUtil.prepareResourceSet(resourceSet);

    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertTransient(resource);

    Supplier supplier = getModel1Factory().createSupplier();
    assertTransient(supplier);
    assertEquals(null, supplier.eContainer());

    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);
    assertEquals(true, contents.isEmpty());
    assertEquals(0, contents.size());
    assertTransient(resource);

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
    CDOSession session = openSession();
    assertNotNull(session);
    assertEquals(false, session.isClosed());
    assertEquals(RepositoryConfig.REPOSITORY_NAME, session.getRepositoryInfo().getName());
    assertEquals(RepositoryConfig.REPOSITORY_NAME, session.getRepositoryInfo().getUUID());
  }

  public void testStartTransaction() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    assertNotNull(transaction);
    assertEquals(session, transaction.getSession());
  }

  public void testCreateResource() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    assertNew(resource, transaction);

    URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1"));
    assertEquals(uri, resource.getURI());

    ResourceSet expected = transaction.getResourceSet();
    ResourceSet actual = resource.getResourceSet();
    assertEquals(expected, actual);
  }

  public void testAttachObject() throws Exception
  {
    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    EList<EObject> contents = resource.getContents();
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
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier);

    assertEquals("Stepper", CDOUtil.getCDOObject(supplier).cdoRevision().data().get(getModel1Package().getAddress_Name(), 0));

    CDOCommitInfo commit = transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(supplier).cdoState());
    assertEquals(1, CDOUtil.getCDOObject(supplier).cdoRevision().getVersion());
    assertCreatedTime(resource, commit.getTimeStamp());
    assertCreatedTime(supplier, commit.getTimeStamp());
  }

  public void testCommitNewInverseList() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Category category = getModel1Factory().createCategory();
    category.setName("Category");
    resource.getContents().add(category);

    Product1 product = getModel1Factory().createProduct1();
    product.setName("Product1");
    category.getProducts().add(product);

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(47.11f);
    orderDetail.setProduct(product);
    resource.getContents().add(orderDetail);

    CDOCommitInfo commit = transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(orderDetail).cdoState());
    assertEquals(1, CDOUtil.getCDOObject(orderDetail).cdoRevision().getVersion());
    assertCreatedTime(resource, commit.getTimeStamp());
    assertCreatedTime(orderDetail, commit.getTimeStamp());
  }

  public void testReadResourceClean() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier);

    long commitTime = transaction.commit().getTimeStamp();

    EList<EObject> contents = resource.getContents();
    Supplier s = (Supplier)contents.get(0);
    assertEquals(1, CDOUtil.getCDOObject(s).cdoRevision().getVersion());
    assertEquals(supplier, s);
    assertCreatedTime(s, commitTime);
  }

  public void testReadObjectClean() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier);

    transaction.commit();

    Supplier s = (Supplier)resource.getContents().get(0);
    assertEquals(1, CDOUtil.getCDOObject(s).cdoRevision().getVersion());
    assertEquals("Stepper", s.getName());
  }

  public void testWriteClean() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");
    resource.getContents().add(supplier);

    transaction.commit();

    Supplier s = (Supplier)resource.getContents().get(0);
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

  public void testCommitDirtyInverseList() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Category category = getModel1Factory().createCategory();
    category.setName("Category");
    resource.getContents().add(category);

    Product1 product = getModel1Factory().createProduct1();
    product.setName("Product1");
    category.getProducts().add(product);

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setPrice(47.11f);
    orderDetail.setProduct(product);
    resource.getContents().add(orderDetail);

    CDOCommitInfo commit = transaction.commit();
    long commitTime1 = commit.getTimeStamp();
    assertCreatedTime(product, commitTime1);

    OrderDetail orderDetail2 = getModel1Factory().createOrderDetail();
    orderDetail2.setPrice(0.815f);
    orderDetail2.setProduct(product);
    resource.getContents().add(orderDetail2);

    long commitTime2 = transaction.commit().getTimeStamp();
    assertEquals(true, commitTime1 < commitTime2);
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(product).cdoState());
    assertCreatedTime(product, commitTime2);
  }

  public void testGetResource() throws Exception
  {
    CDOSession session = openSession();

    {
      disableConsole();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Stepper");
      resource.getContents().add(supplier);

      transaction.commit();
      enableConsole();
    }

    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"), true);
      assertNotNull(resource);
      assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")), resource.getURI());
      assertEquals(transaction.getResourceSet(), resource.getResourceSet());
      assertEquals(1, transaction.getResourceSet().getResources().size());
      assertEquals(transaction, resource.cdoView());
      assertEquals(CDOState.PROXY, resource.cdoState());
      assertEquals(null, resource.cdoRevision());

      assertEquals(1, resource.getContents().size()); // This loads the resource
      assertEquals(CDOState.CLEAN, resource.cdoState());
      assertNotNull(resource.cdoRevision());
    }

    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = (CDOResource)transaction.getResourceSet().getResource(CDOURIUtil.createResourceURI(transaction, getResourcePath("/test1")), true);
      assertNotNull(resource);
      assertEquals(URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1")), resource.getURI());
      assertEquals(transaction.getResourceSet(), resource.getResourceSet());
      assertEquals(1, transaction.getResourceSet().getResources().size());
      assertEquals(transaction, resource.cdoView());
      assertEquals(CDOState.PROXY, resource.cdoState());
      assertEquals(null, resource.cdoRevision());

      assertEquals(1, resource.getContents().size()); // This loads the resource
      assertEquals(CDOState.CLEAN, resource.cdoState());
      assertNotNull(resource.cdoRevision());
    }
  }

  public void testGetContents() throws Exception
  {
    CDOSession session = openSession();

    {
      disableConsole();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Stepper");
      resource.getContents().add(supplier);

      transaction.commit();
      enableConsole();
    }

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);

    Supplier supplier = (Supplier)contents.get(0);
    assertEquals("Stepper", supplier.getName());
  }

  public void testReadObjectProxy() throws Exception
  {
    CDOSession session = openSession();

    {
      disableConsole();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Stepper");
      resource.getContents().add(supplier);

      transaction.commit();
      enableConsole();
    }

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();

    Supplier s = (Supplier)contents.get(0);
    assertNotNull(s);
    assertEquals("Stepper", s.getName());
  }

  public void testReadTransientValue() throws Exception
  {
    CDOSession session = openSession();

    {
      disableConsole();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Product1 product = getModel1Factory().createProduct1();
      product.setDescription("DESCRIPTION");
      product.setName("McDuff");

      resource.getContents().add(product);

      assertEquals("DESCRIPTION", product.getDescription());

      transaction.commit();
      enableConsole();
    }

    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("/test1"));

    EList<EObject> contents = resource.getContents();
    Product1 s = (Product1)contents.get(0);
    assertNotNull(s);

    assertEquals("McDuff", s.getName());
    assertNull(s.getDescription());

    s.setDescription("HELLO");
    assertEquals("HELLO", s.getDescription());
  }

  public void testLoadResource() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Stepper");
      resource.getContents().add(supplier);

      transaction.commit();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    assertNotNull(resource);

    URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + getResourcePath("/test1"));
    URI resourceURI = resource.getURI();
    assertEquals(uri, resourceURI);
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(transaction, resource.cdoView());
    assertEquals(CDOState.PROXY, resource.cdoState());
    assertEquals(null, resource.cdoRevision());

    assertEquals(1, resource.getContents().size()); // This loads the resource
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertNotNull(resource.cdoRevision());
  }

  public void testLoadObject() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Supplier supplier = getModel1Factory().createSupplier();
      supplier.setName("Stepper");
      resource.getContents().add(supplier);

      transaction.commit();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();

    Supplier s = (Supplier)contents.get(0);
    assertNotNull(s);
    assertEquals("Stepper", s.getName());
  }

  public void testLoadObjectInverseList() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Category category = getModel1Factory().createCategory();
      category.setName("Category");
      resource.getContents().add(category);

      Product1 product = getModel1Factory().createProduct1();
      product.setName("Product1");
      category.getProducts().add(product);

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setPrice(47.11f);
      orderDetail.setProduct(product);
      resource.getContents().add(orderDetail);

      transaction.commit();
      session.close();
      clearCache(getRepository().getRevisionManager());
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();

    Category category = (Category)contents.get(0);
    assertNotNull(category);
    assertEquals("Category", category.getName());
    assertEquals(1, category.getProducts().size());

    Product1 product = category.getProducts().get(0);
    assertNotNull(product);
    assertEquals("Product1", product.getName());
    assertEquals(1, product.getOrderDetails().size());

    OrderDetail orderDetail = product.getOrderDetails().get(0);
    assertNotNull(orderDetail);
    assertEquals(47.11f, orderDetail.getPrice());
    assertEquals(product, orderDetail.getProduct());
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

    assertEquals(getModel1Package().getAddress_City().getFeatureID(), getModel1Package().getOrderDetail_Price().getFeatureID());

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
    transaction = session.openTransaction();

    orderAddress = (OrderAddress)CDOUtil.getEObject(transaction.getObject(CDOUtil.getCDOObject(orderAddress).cdoID(), true));
    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    orderAddress.setPrice(2.8f);
    transaction.commit();

    session.close();
    session = openSession();

    transaction = session.openTransaction();
    orderAddress = (OrderAddress)CDOUtil.getEObject(transaction.getObject(CDOUtil.getCDOObject(orderAddress).cdoID(), true));

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    orderAddress.setPrice(2.8f);
  }

  public void testNullReference() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setPrice(4.75f);
      resource.getContents().add(orderDetail);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();

    OrderDetail orderDetail = (OrderDetail)contents.get(0);
    assertNotNull(orderDetail);
    assertEquals(4.75f, orderDetail.getPrice());
    assertEquals(null, orderDetail.getProduct());
  }

  public void testDirectResourceEMF() throws Exception
  {
    Resource resource1 = new XMLResourceImpl();

    EPackage p = createUniquePackage();
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
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.setName("Stepper");

    resource.getContents().add(supplier);

    URI supplierTempURI = EcoreUtil.getURI(supplier);

    // Retrieving supplier from URI before commit
    EObject supplier1 = transaction.getResourceSet().getEObject(supplierTempURI, true);
    assertEquals(supplier, CDOUtil.getEObject(supplier1));

    transaction.commit();

    URI supplierURI = EcoreUtil.getURI(supplier);

    // Retrieving supplier from URI after commit
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

  @Skips(IModelConfig.CAPABILITY_LEGACY) // Legacy DOES eagerly load all references!
  public void testReferenceIntoDifferentResource() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    resource1.getContents().add(orderDetail);

    CDOResource resource2 = transaction.createResource(getResourcePath("/test2"));
    Company company = getModel1Factory().createCompany();
    resource2.getContents().add(company);
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    Product1 product = getModel1Factory().createProduct1();
    product.getOrderDetails().add(orderDetail);
    category.getProducts().add(product);

    transaction.commit();
    transaction.close();
    session.close();

    session = openSession();
    // session.getRevisionManager().addListener(e -> System.out.println(e));

    CDOView view = session.openView();
    resource1 = view.getResource(getResourcePath("/test1"));
    orderDetail = (OrderDetail)resource1.getContents().get(0);

    List<InternalCDOObject> objectsBefore = ((AbstractCDOView)view).getObjectsList();
    product = orderDetail.getProduct(); // Load revision.
    List<InternalCDOObject> objectsAfter = ((AbstractCDOView)view).getObjectsList();

    // Assert that no container objects are loaded.
    assertEquals(objectsBefore.size() + 1, objectsAfter.size());
  }
}
