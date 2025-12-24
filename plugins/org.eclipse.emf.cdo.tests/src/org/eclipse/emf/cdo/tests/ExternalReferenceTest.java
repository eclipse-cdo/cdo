/*
 * Copyright (c) 2008-2013, 2015-2018, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadRevisionsRequest;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.DanglingReferenceException;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 */
@Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
public class ExternalReferenceTest extends AbstractCDOTest
{
  private static final String REPOSITORY_B_NAME = "repo2";

  public void testExternalReference() throws Exception
  {
    getRepository(REPOSITORY_B_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY_B_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transactionA = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA.createResource(getResourcePath("/resA"));
    CDOResource resB = transactionB.createResource(getResourcePath("/resB"));

    Customer customerA = getModel1Factory().createCustomer();
    resA.getContents().add(customerA);
    transactionA.commit();

    SalesOrder salesOrderB = getModel1Factory().createSalesOrder();
    salesOrderB.setCustomer(customerA);
    resB.getContents().add(salesOrderB);
    transactionB.commit();

    System.out.println();
  }

  public void testExternalWithDynamicEObject() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

    CDOSession sessionA = openSession();
    sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
    sessionA.getPackageRegistry().putEPackage(getModel4Package());
    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
    Resource resD = resourceSet.createResource(URI.createURI("test://1"));

    EPackage schoolPackage = createDynamicEPackage();
    resourceSet.getPackageRegistry().put(schoolPackage.getNsURI(), schoolPackage);

    EClass eClass = (EClass)schoolPackage.getEClassifier("SchoolBook");
    EObject schoolbook = EcoreUtil.create(eClass);

    GenRefSingleNonContained objectFromResA = getModel4Factory().createGenRefSingleNonContained();
    objectFromResA.setElement(schoolbook);
    resD.getContents().add(schoolbook);

    resA.getContents().add(objectFromResA);
    transactionA1.commit();
  }

  public void testExternalWithEClass() throws Exception
  {
    {
      ResourceSet resourceSet = new ResourceSetImpl();

      CDOSession sessionA = openSession();
      sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionA.getPackageRegistry().putEPackage(getModel4Package());
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
      GenRefSingleNonContained objectFromResA = getModel4Factory().createGenRefSingleNonContained();
      objectFromResA.setElement(getModel1Package().getAddress());
      resA.getContents().add(objectFromResA);
      transactionA1.commit();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession sessionA = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getPackageRegistry().put(getModel1Package().getNsURI(), getModel1Package());

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.getResource(getResourcePath("/resA"));

      GenRefSingleNonContained objectFromResA = (GenRefSingleNonContained)resA.getContents().get(0);
      assertEquals(getModel1Package().getAddress(), objectFromResA.getElement());
      transactionA1.commit();
    }
  }

  public void testExternalWithEPackage() throws Exception
  {
    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();

      sessionA.getPackageRegistry().putEPackage(getModel4Package());
      sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
      GenRefSingleNonContained objectFromResA = getModel4Factory().createGenRefSingleNonContained();
      objectFromResA.setElement(getModel1Package());
      resA.getContents().add(objectFromResA);
      transactionA1.commit();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getPackageRegistry().put(getModel1Package().getNsURI(), getModel1Package());

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.getResource(getResourcePath("/resA"));

      GenRefSingleNonContained objectFromResA = (GenRefSingleNonContained)resA.getContents().get(0);
      assertEquals(getModel1Package(), objectFromResA.getElement());
      transactionA1.commit();
    }
  }

  // XXX disabled because of Bug 290097
  @Skips("Postgresql")
  public void testOneXMIResourceManyViewsOnOneResourceSet() throws Exception
  {
    byte[] dataOfresD = null;
    getRepository(REPOSITORY_B_NAME);

    {
      CDOSession sessionA = openSession();
      CDOSession sessionB = openSession(REPOSITORY_B_NAME);
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

      sessionA.getPackageRegistry().putEPackage(getModel1Package());
      sessionA.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(getModel1Package());
      sessionB.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
      CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

      EList<Resource> resources = resourceSet.getResources();
      assertEquals(2, resources.size());// Bug 346636

      CDOResource resC = transactionA1.createResource(getResourcePath("/resC"));
      assertNotNull(resC);
      assertEquals(3, resources.size());// Bug 346636

      Resource resD = resourceSet.createResource(URI.createURI("test://1"));
      assertEquals(4, resources.size());// Bug 346636
      assertEquals(false, resD instanceof CDOResource);

      Company companyA = getModel1Factory().createCompany();
      companyA.setName("VALUEA");

      Company companyB = getModel1Factory().createCompany();
      companyB.setName("VALUEB");

      Company companyD = getModel1Factory().createCompany();
      companyD.setName("VALUED");

      resD.getContents().add(companyD);
      resA.getContents().add(companyA);
      resB.getContents().add(companyB);

      Supplier supplier = getModel1Factory().createSupplier();
      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();

      supplier.getPurchaseOrders().add(purchaseOrder);
      resD.getContents().add(supplier);
      resA.getContents().add(purchaseOrder);

      CDOXATransaction transSet = CDOUtil.createXATransaction();

      transSet.add(CDOUtil.getViewSet(resourceSet));

      transactionA1.commit();

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      resD.save(outputStream, null);
      dataOfresD = outputStream.toByteArray();
    }

    clearCache(getRepository().getRevisionManager());

    {
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      CDOSession session2 = openSession(REPOSITORY_B_NAME);
      CDOTransaction transaction2 = session2.openTransaction(resourceSet);

      CDOViewSet set = CDOUtil.getViewSet(resourceSet);
      assertNotNull(set);

      resourceSet.getPackageRegistry().put(getModel1Package().getNsURI(), getModel1Package());
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

      Resource resD = resourceSet.createResource(URI.createURI("test://1"));
      resD.load(new ByteArrayInputStream(dataOfresD), null);

      CDOResource resA = transaction.getResource(getResourcePath("/resA"));
      CDOResource resB = transaction2.getResource(getResourcePath("/resB"));
      Company companyA = (Company)resA.getContents().get(0);
      Company companyB = (Company)resB.getContents().get(0);
      Company companyD = (Company)resD.getContents().get(0);

      assertNotSame(resA.getURI(), resB.getURI());
      assertNotSame(resA.getPath(), "/resA");
      assertNotSame(resB.getPath(), "/resB");
      assertNotSame(resA.cdoView(), transaction2);
      assertNotSame(resB.cdoView(), transaction);

      assertEquals("VALUEA", companyA.getName());
      assertEquals("VALUEB", companyB.getName());
      assertEquals("VALUED", companyD.getName());

      Supplier supplierD = (Supplier)resD.getContents().get(1);
      PurchaseOrder pO = supplierD.getPurchaseOrders().get(0);
      assertEquals(transaction, CDOUtil.getCDOObject(pO).cdoView());
      assertEquals(supplierD, pO.getSupplier());
    }
  }

  // Skip this test until the problems with XATransactions are solved.
  // XXX disabled because of Bug 290097
  @Skips({ IModelConfig.CAPABILITY_LEGACY, "Postgresql" })
  public void testManyViewsOnOneResourceSet() throws Exception
  {
    getRepository(REPOSITORY_B_NAME);

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel1Package());

      CDOSession sessionB = openSession(REPOSITORY_B_NAME);

      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);
      CDOTransaction transactionB = sessionB.openTransaction(resourceSet);

      CDOResource res = transaction.createResource(getResourcePath("/resA"));
      CDOResource resB = transactionB.createResource(getResourcePath("/resB"));

      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
      Supplier supplier = getModel1Factory().createSupplier();
      supplier.getPurchaseOrders().add(purchaseOrder);

      resB.getContents().add(supplier);
      res.getContents().add(purchaseOrder);

      CDOXATransaction transSet = CDOUtil.createXATransaction();
      transSet.add(CDOUtil.getViewSet(resourceSet));

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    {
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOSession sessionA = openSession();
      CDOTransaction transactionA = sessionA.openTransaction(resourceSet);

      CDOSession sessionB = openSession(REPOSITORY_B_NAME);
      CDOTransaction transactionB = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA.getResource(getResourcePath("/resA"));
      assertNotNull(resA);

      CDOResource resB = transactionB.getResource(getResourcePath("/resB"));
      assertNotNull(resB);

      Supplier supplierB = (Supplier)resB.getContents().get(0);
      PurchaseOrder pO = supplierB.getPurchaseOrders().get(0);

      assertEquals(transactionA, CDOUtil.getCDOObject(pO).cdoView());

      assertEquals(transactionB, CDOUtil.getCDOObject(supplierB).cdoView());

      assertEquals(supplierB, pO.getSupplier());
      assertEquals(supplierB.getPurchaseOrders().get(0), pO);
    }
  }

  public void testObjectNotAttached() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource1 = transaction.createResource(getResourcePath("/test1"));

    msg("Adding company");
    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    supplier.getPurchaseOrders().add(purchaseOrder);

    resource1.getContents().add(supplier);

    try
    {
      msg("Committing");
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException success)
    {
      assertEquals(true, success.getCause() instanceof DanglingReferenceException);
      assertSame(purchaseOrder, ((DanglingReferenceException)success.getCause()).getTarget());
    }
  }

  public void testUsingObjectsBetweenSameTransaction() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    CDOTransaction transaction2 = session.openTransaction();

    CDOResource resource1 = transaction1.createResource(getResourcePath("/test1"));
    CDOResource resource2 = transaction2.createResource(getResourcePath("/test2"));

    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    supplier.getPurchaseOrders().add(purchaseOrder);

    resource1.getContents().add(supplier);
    resource2.getContents().add(purchaseOrder);

    transaction1.commit();
  }

  public void testWithXML() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("xml", new XMLResourceFactoryImpl());

    PurchaseOrder externalObject = getModel1Factory().createPurchaseOrder();
    Resource externalResource = resourceSet.createResource(URI.createFileURI("/com/foo/bar.xml"));
    externalResource.getContents().add(externalObject);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    Supplier supplier = getModel1Factory().createSupplier();
    EList<PurchaseOrder> purchaseOrders = supplier.getPurchaseOrders();
    purchaseOrders.add(externalObject);

    CDOResource resource = transaction.createResource(getResourcePath("/internal"));
    resource.getContents().add(supplier);
    transaction.commit();

    CDORevision salesOrderRevision = CDOUtil.getCDOObject(supplier).cdoRevision();
    Object externalReference = salesOrderRevision.data().get(getModel1Package().getSupplier_PurchaseOrders(), 0);
    assertInstanceOf(CDOIDExternal.class, externalReference);

    assertEquals(externalObject, purchaseOrders.get(0));
    assertEquals(0, purchaseOrders.indexOf(externalObject));
    assertEquals(0, purchaseOrders.lastIndexOf(externalObject));
    assertTrue(purchaseOrders.contains(externalObject));
    assertTrue(purchaseOrders.containsAll(Collections.singleton(externalObject))); // Bug 537081.
  }

  public void testWithXMLAndPrefetching() throws Exception
  {
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
      map.put("xml", new XMLResourceFactoryImpl());

      Resource externalResource = resourceSet.createResource(URI.createFileURI("/com/foo/bar.xml"));

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      Supplier supplier = getModel1Factory().createSupplier();
      EList<PurchaseOrder> purchaseOrders = supplier.getPurchaseOrders();

      for (int i = 0; i < 200; i++)
      {
        PurchaseOrder externalObject = getModel1Factory().createPurchaseOrder();
        externalResource.getContents().add(externalObject);

        purchaseOrders.add(externalObject);
      }

      CDOResource resource = transaction.createResource(getResourcePath("/internal"));
      resource.getContents().add(supplier);
      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    {
      ResourceSet resourceSet = new ResourceSetImpl();
      Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
      map.put("xml", new XMLResourceFactoryImpl());

      Resource externalResource = resourceSet.createResource(URI.createFileURI("/com/foo/bar.xml"));
      for (int i = 0; i < 200; i++)
      {
        PurchaseOrder externalObject = getModel1Factory().createPurchaseOrder();
        externalResource.getContents().add(externalObject);
      }

      CDOSession session = openSession();
      ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol();
      SignalCounter signalCounter = new SignalCounter(protocol);

      CDOTransaction transaction = session.openTransaction(resourceSet);
      transaction.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(10));

      CDOResource resource = transaction.getResource(getResourcePath("/internal"));
      Supplier supplier = (Supplier)resource.getContents().get(0);

      for (int i = 0; i < 200; i++)
      {
        PurchaseOrder externalObject = supplier.getPurchaseOrders().get(i);
        System.out.println(externalObject);
      }

      int count = signalCounter.getCountFor(LoadRevisionsRequest.class);
      assertEquals(3, count); // Resource + top folder + top object (supplier)
      protocol.removeListener(signalCounter);

      CDORevisionData data = CDOUtil.getCDOObject(supplier).cdoRevision().data();
      EReference reference = getModel1Package().getSupplier_PurchaseOrders();
      for (int i = 0; i < 200; i++)
      {
        Object value = data.get(reference, i);
        assertInstanceOf(CDOIDExternal.class, value);
      }
    }
  }

  @CleanRepositoriesBefore(reason = "Ref counting")
  public void testXRefExternalObject() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("xml", new XMLResourceFactoryImpl());

    PurchaseOrder externalObject = getModel1Factory().createPurchaseOrder();
    Resource externalResource = resourceSet.createResource(URI.createFileURI("/com/foo/bar.xml"));
    externalResource.getContents().add(externalObject);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    Supplier supplier = getModel1Factory().createSupplier();
    supplier.getPurchaseOrders().add(externalObject);

    CDOResource resource = transaction.createResource(getResourcePath("/internal"));
    resource.getContents().add(supplier);
    transaction.commit();

    CDOObject wrapper = CDOUtil.wrapExternalObject(externalObject, transaction);
    List<CDOObjectReference> xRefs = transaction.queryXRefs(wrapper);
    assertEquals(1, xRefs.size());
    assertEquals(supplier, xRefs.get(0).getSourceObject());
  }

  private EPackage createDynamicEPackage()
  {
    final EcoreFactory eFactory = EcoreFactory.eINSTANCE;
    final EcorePackage ePackage = EcorePackage.eINSTANCE;

    EClass schoolBookEClass = eFactory.createEClass();
    schoolBookEClass.setName("SchoolBook");

    // create a new attribute for this EClass
    EAttribute level = eFactory.createEAttribute();
    level.setName("level");
    level.setEType(ePackage.getEInt());
    schoolBookEClass.getEStructuralFeatures().add(level);

    // Create a new EPackage and add the new EClasses
    EPackage schoolPackage = createUniquePackage();
    schoolPackage.getEClassifiers().add(schoolBookEClass);
    return schoolPackage;
  }
}
