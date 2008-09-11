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

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.CDOXATransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.util.CDOUtil;

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
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author Simon McDuff
 */
public class ExternalReferenceTest extends AbstractCDOTest
{
  final static public String REPOSITORY2_NAME = "repo2";

   
  public void testExternalWithDynamicEObject() throws Exception
  {
    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("file", new XMIResourceFactoryImpl());

      EPackage schoolPackage = createDynamicEPackage();
      EClass classifier = (EClass)schoolPackage.getEClassifier("SchoolBook");
      
      EObject schoolbook = schoolPackage.getEFactoryInstance().create(classifier);
      sessionA.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
      Resource resD = resourceSet.createResource(URI.createFileURI("c:\\1.xml"));
      
      GenRefSingleNonContained objectFromResA = model4Factory.eINSTANCE.createGenRefSingleNonContained();
      objectFromResA.setElement(schoolbook);
      resD.getContents().add(schoolbook);
      
      resA.getContents().add(objectFromResA);
      
      transactionA1.commit();
    }
  }
  
  public void testExternalWithEClass() throws Exception
  {
    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();
      
      sessionA.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.createResource("/resA");
      GenRefSingleNonContained objectFromResA = model4Factory.eINSTANCE.createGenRefSingleNonContained();
      objectFromResA.setElement(Model1Package.eINSTANCE.getAddress());     
      resA.getContents().add(objectFromResA);      
      transactionA1.commit();
    }
    
    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getPackageRegistry().put(Model1Package.eINSTANCE.getNsURI(), Model1Package.eINSTANCE);
      
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.getResource("/resA");

      GenRefSingleNonContained objectFromResA = (GenRefSingleNonContained)resA.getContents().get(0);
      assertEquals(Model1Package.eINSTANCE.getAddress(), objectFromResA.getElement());
      transactionA1.commit();
    }    
  } 
  public void testExternalWithEPackage() throws Exception
  {
    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();
      
      sessionA.getPackageRegistry().putEPackage(model4Package.eINSTANCE);
      
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.createResource("/resA");
      GenRefSingleNonContained objectFromResA = model4Factory.eINSTANCE.createGenRefSingleNonContained();
      objectFromResA.setElement(Model1Package.eINSTANCE);     
      resA.getContents().add(objectFromResA);      
      transactionA1.commit();
    }
    
    {
      CDOSession sessionA = openSession();

      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getPackageRegistry().put(Model1Package.eINSTANCE.getNsURI(), Model1Package.eINSTANCE);
      
      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOResource resA = transactionA1.getResource("/resA");
     
      GenRefSingleNonContained objectFromResA = (GenRefSingleNonContained)resA.getContents().get(0);
      assertEquals(Model1Package.eINSTANCE, objectFromResA.getElement());
      transactionA1.commit();
    }    
  }    
  
  public void testOneXMIResourceManyViewsOnOneResourceSet() throws Exception
  {
    URI a = URI.createURI("file:/c:/temp");
    URI b = URI.createURI("file://c:/temp");
    URI c = URI.createURI("file:///c:/temp");

    System.out.println(a.authority());
    System.out.println(b.authority());
    System.out.println(c.authority());

    createRepository(REPOSITORY2_NAME);
    {
      CDOSession sessionA = openSession();
      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("file", new XMIResourceFactoryImpl());

      sessionA.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
      sessionA.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
      CDOResource resB = transactionB1.createResource("/resB");

      assertEquals(2, resourceSet.getResources().size());
      // assertEquals(resA, resourceSet.getResource(CDOUtil.createResourceURI(sessionA, "/resA"), false));
      // assertEquals(resB, resourceSet.getResource(CDOUtil.createResourceURI(sessionA, "/resB"), false));

      // CDOResource resC = (CDOResource)resourceSet.createResource(CDOUtil.createResourceURI(sessionA, "/resC"));
      // assertEquals(transactionA1, resC.cdoView());
      CDOResource resC = transactionA1.createResource("/resC");
      assertNotNull(resC);

      assertEquals(3, resourceSet.getResources().size());
      // assertEquals(resC, resourceSet.getResource(CDOUtil.createResourceURI(sessionA, "/resC"), false));

      Resource resD = resourceSet.createResource(URI.createFileURI("c:\\1.xml"));

      assertEquals(4, resourceSet.getResources().size());
      assertEquals(false, resD instanceof CDOResource);

      Company companyA = Model1Factory.eINSTANCE.createCompany();
      companyA.setName("VALUEA");

      Company companyB = Model1Factory.eINSTANCE.createCompany();
      companyB.setName("VALUEB");

      Company companyD = Model1Factory.eINSTANCE.createCompany();
      companyD.setName("VALUED");

      resD.getContents().add(companyD);
      resA.getContents().add(companyA);
      resB.getContents().add(companyB);

      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
      PurchaseOrder purchaseOrder = Model1Factory.eINSTANCE.createPurchaseOrder();

      supplier.getPurchaseOrders().add(purchaseOrder);
      resD.getContents().add(supplier);
      resA.getContents().add(purchaseOrder);

      CDOXATransaction transSet = CDOUtil.createXATransaction();

      transSet.add(CDOUtil.getViewSet(resourceSet));

      transactionA1.commit();

      resD.save(null);
    }

    {
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      CDOSession session2 = openSession(REPOSITORY2_NAME);
      CDOTransaction transaction2 = session2.openTransaction(resourceSet);

      CDOViewSet set = CDOUtil.getViewSet(resourceSet);
      assertNotNull(set);

      resourceSet.getPackageRegistry().put(Model1Package.eINSTANCE.getNsURI(), Model1Package.eINSTANCE);
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("file", new XMIResourceFactoryImpl());

      Resource resD = resourceSet.getResource(URI.createFileURI("c:\\1.xml"), true);
      CDOResource resA = transaction.getResource("/resA");
      CDOResource resB = transaction2.getResource("/resB");
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
      assertEquals(transaction, pO.cdoView());
      assertEquals(supplierD, pO.getSupplier());
    }
  }

  public void testManyViewsOnOneResourceSet() throws Exception
  {
    createRepository(REPOSITORY2_NAME);
    {
      CDOSession sessionA = openSession();
      CDOSession sessionB = openSession(REPOSITORY2_NAME);

      ResourceSet resourceSet = new ResourceSetImpl();

      sessionA.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
      CDOResource resB = transactionB1.createResource("/resB");

      Supplier supplier = Model1Factory.eINSTANCE.createSupplier();
      PurchaseOrder purchaseOrder = Model1Factory.eINSTANCE.createPurchaseOrder();

      supplier.getPurchaseOrders().add(purchaseOrder);
      resB.getContents().add(supplier);
      resA.getContents().add(purchaseOrder);

      CDOXATransaction transSet = CDOUtil.createXATransaction();
      transSet.add(CDOUtil.getViewSet(resourceSet));

      transactionA1.commit();
    }

    {
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOSession sessionA = openSession();
      CDOTransaction transactionA = sessionA.openTransaction(resourceSet);

      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      CDOTransaction transactionB = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA.getResource("/resA");
      assertNotNull(resA);

      CDOResource resB = transactionB.getResource("/resB");
      assertNotNull(resB);

      Supplier supplierB = (Supplier)resB.getContents().get(0);
      PurchaseOrder pO = supplierB.getPurchaseOrders().get(0);

      assertEquals(transactionA, pO.cdoView());

      assertEquals(transactionB, supplierB.cdoView());

      assertEquals(supplierB, pO.getSupplier());
      assertEquals(supplierB.getPurchaseOrders().get(0), pO);

    }
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
