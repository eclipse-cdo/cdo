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
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderAddress;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannel;

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
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    assertTransient(supplier);

    msg("Setting name");
    supplier.setName("Stepper");
    assertTransient(supplier);

    msg("Verifying name");
    assertEquals("Stepper", supplier.getName());
    assertTransient(supplier);

    msg("Creating purchaseOrder");
    PurchaseOrder purchaseOrder = Model1Factory.eINSTANCE.createPurchaseOrder();
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
    CDOUtil.prepareResourceSet(resourceSet);

    msg("Creating resource");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertTransient(resource);

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    assertTransient(supplier);
    assertEquals(null, supplier.eContainer());

    msg("Verifying contents");
    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);
    assertTrue(contents.isEmpty());
    assertEquals(0, contents.size());
    assertTransient(resource);

    msg("Adding supplier");
    contents.add(supplier);
    assertTransient(resource);
    assertTransient(supplier);
    assertContent(resource, supplier);
  }

  public void testOpenSession() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();
    assertNotNull(session);

    IChannel channel = session.getChannel();
    assertNotNull(channel);

    IBufferHandler receiveHandler = channel.getReceiveHandler();
    assertNotNull(receiveHandler);
  }

  public void testStartTransaction() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();
    assertNotNull(transaction);
    assertEquals(session, transaction.getSession());
  }

  public void testAttachResource() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");
    assertNew(resource, transaction);
    assertEquals(URI.createURI("cdo:/test1"), resource.getURI());
    ResourceSet expected = transaction.getResourceSet();
    ResourceSet actual = resource.getResourceSet();
    assertEquals(expected, actual);
  }

  public void testAttachView() throws Exception
  {
    final URI uri = URI.createURI("cdo:/test1");

    msg("Creating resourceSet");
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOUtil.prepareResourceSet(resourceSet);

    msg("Creating resource");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertTransient(resource);

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction(resourceSet);

    msg("Verifying resource");
    assertNew(resource, transaction);
    assertEquals(uri, resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
  }

  public void testAttachViewWithObject() throws Exception
  {
    final URI uri = URI.createURI("cdo:/test1");

    msg("Creating resourceSet");
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOUtil.prepareResourceSet(resourceSet);

    msg("Creating resource");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertTransient(resource);

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
    assertTransient(supplier);

    msg("Setting name");
    supplier.setName("Stepper");
    assertTransient(supplier);

    msg("Verifying name");
    assertEquals("Stepper", supplier.getName());
    assertTransient(supplier);

    msg("Adding supplier");
    EList<EObject> contents = resource.getContents();
    contents.add(supplier);
    assertTransient(resource);
    assertTransient(supplier);
    assertContent(resource, supplier);

    msg("Verifying supplier");
    contents = resource.getContents();
    assertNotNull(contents);
    assertEquals(1, contents.size());

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction(resourceSet);

    msg("Verifying resource");
    assertNew(resource, transaction);
    assertEquals(uri, resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());

    msg("Verifying contents");
    contents = resource.getContents();
    assertNotNull(contents);
    assertEquals(1, contents.size());

    msg("Verifying supplier");
    Supplier s = (Supplier)contents.get(0);
    assertNew(supplier, transaction);
    assertNew(resource, transaction);
    assertEquals(supplier, s);
    assertEquals(resource, s.cdoResource());
    assertEquals(null, s.eContainer());
  }

  public void testAttachObject() throws Exception
  {
    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Adding supplier");
    contents.add(supplier);
    assertNew(supplier, transaction);
    assertEquals(transaction, supplier.cdoView());
    assertEquals(resource, supplier.cdoResource());
    assertEquals(resource, supplier.eResource());
    assertEquals(null, supplier.eContainer());
  }

  public void testCommitNew() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, supplier.cdoState());
  }

  public void testReadResourceClean() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();

    msg("Getting supplier");
    EList<EObject> contents = resource.getContents();
    Supplier s = (Supplier)contents.get(0);
    assertEquals(supplier, s);
  }

  public void testReadObjectClean() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();

    msg("Getting supplier");
    Supplier s = (Supplier)resource.getContents().get(0);

    msg("Verifying name");
    assertEquals("Stepper", s.getName());
  }

  public void testWriteClean() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

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
    assertEquals(CDOState.DIRTY, supplier.cdoState());
    assertEquals(CDOState.CLEAN, resource.cdoState());
  }

  public void testCommitDirty() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    msg("Committing");
    transaction.commit();

    msg("Setting name");
    supplier.setName("Eike");

    msg("Committing");
    transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, supplier.cdoState());
  }

  public void testGetResource() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating supplier");
      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

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
    CDOResource resource = transaction.getResource("/test1");
    assertNotNull(resource);
    assertEquals(URI.createURI("cdo:/test1"), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(CDOState.PROXY, resource.cdoState());
    assertEquals(transaction, resource.cdoView());
    assertNull(resource.cdoRevision());
  }

  public void testGetContents() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating supplier");
      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

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
    CDOResource resource = transaction.getResource("/test1");

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();
    assertNotNull(contents);
  }

  public void testReadObjectProxy() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    {
      disableConsole();
      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating supplier");
      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

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
    CDOResource resource = transaction.getResource("/test1");

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Getting supplier");
    Supplier s = (Supplier)contents.get(0);
    assertNotNull(s);

    msg("Verifying name");
    assertEquals("Stepper", s.getName());
  }

  public void testLoadResource() throws Exception
  {
    {
      // disableConsole();
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating supplier");
      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

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
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource("/test1");
    assertNotNull(resource);
    assertEquals(URI.createURI("cdo:/test1"), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(CDOState.PROXY, resource.cdoState());
    assertEquals(transaction, resource.cdoView());
    assertNull(resource.cdoRevision());
  }

  public void testLoadObject() throws Exception
  {
    {
      // disableConsole();
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating supplier");
      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

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
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource("/test1");

    msg("Getting contents");
    EList<EObject> contents = resource.getContents();

    msg("Getting supplier");
    Supplier s = (Supplier)contents.get(0);
    assertNotNull(s);

    msg("Verifying name");
    assertEquals("Stepper", s.getName());
  }

  /**
   * http://bugs.eclipse.org/226317
   */
  public void testMultipleInheritence() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");
    OrderAddress orderAddress = Model1Factory.eINSTANCE.createOrderAddress();
    resource.getContents().add(orderAddress);

    assertEquals(Model1Package.eINSTANCE.getAddress_City().getFeatureID(), Model1Package.eINSTANCE
        .getOrderDetail_Price().getFeatureID());

    orderAddress.setCity("ALLO");
    orderAddress.setPrice(2.8f);
    orderAddress.setTestAttribute(true);

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();
    resource.getContents().add(orderDetail);
    orderDetail.setPrice(3f);

    transaction.commit();

    orderAddress.setCity("ALLO");

    transaction.commit();
    session.close();

    session = openModel1Session();

    msg("Opening transaction");
    transaction = session.openTransaction();
    orderAddress = (OrderAddress)transaction.getObject(orderAddress.cdoID(), true);

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    orderAddress.setPrice(2.8f);
    transaction.commit();
    session.close();

    session = openModel1Session();

    transaction = session.openTransaction();
    orderAddress = (OrderAddress)transaction.getObject(orderAddress.cdoID(), true);

    assertEquals(2.8f, orderAddress.getPrice());
    assertEquals("ALLO", orderAddress.getCity());

    orderAddress.setPrice(2.8f);

    session.close();
  }

  public void testNullReference() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Opening transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating orderDetail");
      OrderDetail orderDetail = Model1Factory.eINSTANCE.createOrderDetail();

      msg("Setting price");
      orderDetail.setPrice(4.75f);

      msg("Adding orderDetail");
      resource.getContents().add(orderDetail);

      msg("Committing");
      transaction.commit();
      session.close();
    }

    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Getting resource");
    CDOResource resource = transaction.getResource("/test1");

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
    CDOSession session = openModel1Session();
    CDOTransaction transaction = (CDOTransaction)session.openTransaction();

    CDOResource resource1 = transaction.getOrCreateResource("/test1");
    // Resource resource1 = new XMIResourceImpl();

    Category cat1 = Model1Factory.eINSTANCE.createCategory();
    assertTransient(cat1);

    Category cat2 = Model1Factory.eINSTANCE.createCategory();
    assertTransient(cat2);

    // resource1.getContents().add(cat2);
    resource1.getContents().add(cat1);
    cat1.getCategories().add(cat2);

    assertEquals(null, ((InternalEObject)cat2).eDirectResource());
    assertEquals(resource1, ((InternalEObject)cat1).eDirectResource());

    transaction.close();
    session.close();
  }

  public void testResourceAccessor() throws Exception
  {
    msg("Opening session");
    CDOSession session = openModel1Session();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Creating supplier");
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    msg("Setting name");
    supplier.setName("Stepper");

    msg("Adding supplier");
    resource.getContents().add(supplier);

    URI supplierTempURI = EcoreUtil.getURI(supplier);

    msg("Retrieving supplier from URI before commit");
    EObject supplier1 = transaction.getResourceSet().getEObject(supplierTempURI, true);

    assertEquals(supplier, supplier1);

    msg("Committing");
    transaction.commit();

    URI supplierURI = EcoreUtil.getURI(supplier);

    msg("Retrieving supplier from URI after commit");
    EObject supplierFromURI = transaction.getResourceSet().getEObject(supplierURI, true);

    assertEquals(supplier, supplierFromURI);

    try
    {
      EObject supplierAfterCommit2 = transaction.getResourceSet().getEObject(supplierTempURI, true);
      assertEquals(null, supplierAfterCommit2);
    }
    catch (IllegalStateException expected)
    {
    }
  }
}
