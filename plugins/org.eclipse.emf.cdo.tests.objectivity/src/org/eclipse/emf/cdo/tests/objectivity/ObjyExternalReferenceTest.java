/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.DanglingReferenceException;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author Simon McDuff
 */
public class ObjyExternalReferenceTest extends AbstractCDOTest
{
  final static public String REPOSITORY2_NAME = "repo2";

  public void testExternalWithDynamicEObject() throws Exception
  {
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

      CDOSession sessionA = openSession();
      sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionA.getPackageRegistry().putEPackage(getModel4Package());
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
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
  }

  public void testExternalWithEClass() throws Exception
  {
    {
      ResourceSet resourceSet = new ResourceSetImpl();

      CDOSession sessionA = openSession();
      sessionA.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
      sessionA.getPackageRegistry().putEPackage(getModel4Package());
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
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
      CDOResource resA = transactionA1.getResource("/resA");

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
      CDOResource resA = transactionA1.createResource("/resA");
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
      CDOResource resA = transactionA1.getResource("/resA");

      GenRefSingleNonContained objectFromResA = (GenRefSingleNonContained)resA.getContents().get(0);
      assertEquals(getModel1Package(), objectFromResA.getElement());
      transactionA1.commit();
    }
  }

  public void testOneXMIResourceManyViewsOnOneResourceSet() throws Exception
  {
    return;

    /*
     * byte[] dataOfresD = null; getRepository(REPOSITORY2_NAME); { CDOSession sessionA = openSession(); CDOSession
     * sessionB = openSession(REPOSITORY2_NAME); ResourceSet resourceSet = new ResourceSetImpl();
     * resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());
     * sessionA.getPackageRegistry().putEPackage(getModel1Package());
     * sessionA.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);
     * sessionB.getPackageRegistry().putEPackage(getModel1Package());
     * sessionB.getPackageRegistry().putEPackage(Model2Package.eINSTANCE); CDOTransaction transactionA1 =
     * sessionA.openTransaction(resourceSet); CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);
     * CDOResource resA = transactionA1.createResource("/resA"); CDOResource resB =
     * transactionB1.createResource("/resB"); EList<Resource> resources = resourceSet.getResources(); assertEquals(4,
     * resources.size()); CDOResource resC = transactionA1.createResource("/resC"); assertNotNull(resC); assertEquals(5,
     * resources.size()); Resource resD = resourceSet.createResource(URI.createURI("test://1")); assertEquals(6,
     * resources.size()); assertEquals(false, resD instanceof CDOResource); Company companyA =
     * getModel1Factory().createCompany(); companyA.setName("VALUEA"); Company companyB =
     * getModel1Factory().createCompany(); companyB.setName("VALUEB"); Company companyD =
     * getModel1Factory().createCompany(); companyD.setName("VALUED"); resD.getContents().add(companyD);
     * resA.getContents().add(companyA); resB.getContents().add(companyB); Supplier supplier =
     * getModel1Factory().createSupplier(); PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
     * supplier.getPurchaseOrders().add(purchaseOrder); resD.getContents().add(supplier);
     * resA.getContents().add(purchaseOrder); CDOXATransaction transSet = CDOUtil.createXATransaction();
     * transSet.add(CDOUtil.getViewSet(resourceSet)); transactionA1.commit(); ByteArrayOutputStream outputStream = new
     * ByteArrayOutputStream(); resD.save(outputStream, null); dataOfresD = outputStream.toByteArray(); }
     * clearCache(getRepository().getRevisionManager()); { ResourceSet resourceSet = new ResourceSetImpl(); CDOSession
     * session = openSession(); CDOTransaction transaction = session.openTransaction(resourceSet); CDOSession session2 =
     * openSession(REPOSITORY2_NAME); CDOTransaction transaction2 = session2.openTransaction(resourceSet); CDOViewSet
     * set = CDOUtil.getViewSet(resourceSet); assertNotNull(set);
     * resourceSet.getPackageRegistry().put(getModel1Package().getNsURI(), getModel1Package());
     * resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());
     * Resource resD = resourceSet.createResource(URI.createURI("test://1")); resD.load(new
     * ByteArrayInputStream(dataOfresD), null); CDOResource resA = transaction.getResource("/resA"); CDOResource resB =
     * transaction2.getResource("/resB"); Company companyA = (Company)resA.getContents().get(0); Company companyB =
     * (Company)resB.getContents().get(0); Company companyD = (Company)resD.getContents().get(0);
     * assertNotSame(resA.getURI(), resB.getURI()); assertNotSame(resA.getPath(), "/resA");
     * assertNotSame(resB.getPath(), "/resB"); assertNotSame(resA.cdoView(), transaction2);
     * assertNotSame(resB.cdoView(), transaction); assertEquals("VALUEA", companyA.getName()); assertEquals("VALUEB",
     * companyB.getName()); assertEquals("VALUED", companyD.getName()); Supplier supplierD =
     * (Supplier)resD.getContents().get(1); PurchaseOrder pO = supplierD.getPurchaseOrders().get(0);
     * assertEquals(transaction, CDOUtil.getCDOObject(pO).cdoView()); assertEquals(supplierD, pO.getSupplier()); }
     */
  }

  public void testManyViewsOnOneResourceSet() throws Exception
  {
    /*
     * getRepository(REPOSITORY2_NAME); { CDOSession sessionA = openSession();
     * sessionA.getPackageRegistry().putEPackage(getModel1Package()); CDOSession sessionB =
     * openSession(REPOSITORY2_NAME); ResourceSet resourceSet = new ResourceSetImpl(); CDOTransaction transactionA1 =
     * sessionA.openTransaction(resourceSet); CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);
     * CDOResource resA = transactionA1.createResource("/resA"); CDOResource resB =
     * transactionB1.createResource("/resB"); Supplier supplier = getModel1Factory().createSupplier(); PurchaseOrder
     * purchaseOrder = getModel1Factory().createPurchaseOrder(); supplier.getPurchaseOrders().add(purchaseOrder);
     * resB.getContents().add(supplier); resA.getContents().add(purchaseOrder); CDOXATransaction transSet =
     * CDOUtil.createXATransaction(); transSet.add(CDOUtil.getViewSet(resourceSet)); transactionA1.commit(); }
     * clearCache(getRepository().getRevisionManager()); { ResourceSet resourceSet = new ResourceSetImpl(); CDOSession
     * sessionA = openSession(); CDOTransaction transactionA = sessionA.openTransaction(resourceSet); CDOSession
     * sessionB = openSession(REPOSITORY2_NAME); CDOTransaction transactionB = sessionB.openTransaction(resourceSet);
     * CDOResource resA = transactionA.getResource("/resA"); assertNotNull(resA); CDOResource resB =
     * transactionB.getResource("/resB"); assertNotNull(resB); Supplier supplierB = (Supplier)resB.getContents().get(0);
     * PurchaseOrder pO = supplierB.getPurchaseOrders().get(0); assertEquals(transactionA,
     * CDOUtil.getCDOObject(pO).cdoView()); assertEquals(transactionB, CDOUtil.getCDOObject(supplierB).cdoView());
     * assertEquals(supplierB, pO.getSupplier()); assertEquals(supplierB.getPurchaseOrders().get(0), pO); }
     */
  }

  public void testObjectNotAttached() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource1 = transaction.createResource("/test1");

    msg("Adding company");
    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    supplier.getPurchaseOrders().add(purchaseOrder);

    resource1.getContents().add(supplier);

    try
    {
      msg("Committing");
      transaction.commit();
      fail("TransactionException expected");
    }
    catch (TransactionException success)
    {
      assertEquals(true, success.getCause() instanceof DanglingReferenceException);
      assertSame(purchaseOrder, ((DanglingReferenceException)success.getCause()).getTarget());
    }
  }

  public void testUsingObjectsBetweenSameTransaction() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    msg("Opening transaction");
    CDOTransaction transaction1 = session.openTransaction();
    CDOTransaction transaction2 = session.openTransaction();

    msg("Creating resource");
    CDOResource resource1 = transaction1.createResource("/test1");
    CDOResource resource2 = transaction2.createResource("/test2");

    msg("Adding company");
    Supplier supplier = getModel1Factory().createSupplier();
    PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
    supplier.getPurchaseOrders().add(purchaseOrder);

    resource1.getContents().add(supplier);
    resource2.getContents().add(purchaseOrder);

    msg("Committing");
    transaction1.commit();
  }

  private EPackage createDynamicEPackage()
  {
    final EcoreFactory efactory = EcoreFactory.eINSTANCE;
    final EcorePackage epackage = EcorePackage.eINSTANCE;

    EClass schoolBookEClass = efactory.createEClass();
    schoolBookEClass.setName("SchoolBook");

    // create a new attribute for this EClass
    EAttribute level = efactory.createEAttribute();
    level.setName("level");
    level.setEType(epackage.getEInt());
    schoolBookEClass.getEStructuralFeatures().add(level);

    // Create a new EPackage and add the new EClasses
    EPackage schoolPackage = efactory.createEPackage();
    schoolPackage.setName("elv");
    schoolPackage.setNsPrefix("elv");
    schoolPackage.setNsURI("http:///www.elver.org/School");
    schoolPackage.getEClassifiers().add(schoolBookEClass);
    return schoolPackage;
  }
}
