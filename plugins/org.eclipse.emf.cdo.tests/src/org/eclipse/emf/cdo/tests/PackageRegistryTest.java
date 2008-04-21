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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.mango.Value;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class PackageRegistryTest extends AbstractCDOTest
{
  public void testGeneratedPackage() throws Exception
  {
    {
      // Create resource in session 1
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, true);
      session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = Model1Factory.eINSTANCE.createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    {
      // Load resource in session 2
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      Company company = (Company)res.getContents().get(0);
      assertEquals("Eike", company.getName());
    }
  }

  public void testCommitTwoPackages() throws Exception
  {
    {
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, true);
      session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
      session.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      SpecialPurchaseOrder specialPurchaseOrder = Model2Factory.eINSTANCE.createSpecialPurchaseOrder();
      specialPurchaseOrder.setDiscountCode("12345");
      res.getContents().add(specialPurchaseOrder);
      transaction.commit();
    }

    {
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      SpecialPurchaseOrder specialPurchaseOrder = (SpecialPurchaseOrder)res.getContents().get(0);
      assertEquals("12345", specialPurchaseOrder.getDiscountCode());
    }
  }

  public void testCommitUnrelatedPackage() throws Exception
  {
    {
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, true);
      session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      PurchaseOrder purchaseOrder = Model1Factory.eINSTANCE.createPurchaseOrder();
      res.getContents().add(purchaseOrder);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
      session.getPackageRegistry().putEPackage(MangoPackage.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      Value value = MangoFactory.eINSTANCE.createValue();
      value.setName("V0");
      res.getContents().add(value);

      transaction.commit();
      session.close();
    }
  }

  public void testCommitNestedPackages() throws Exception
  {
    CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, true);
    session.getPackageRegistry().putEPackage(Model3Package.eINSTANCE);
    assertEquals(2, session.getPackageRegistry().size());

    try
    {
      session.getPackageRegistry().putEPackage(SubpackagePackage.eINSTANCE);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException success)
    {
    }

    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res");

    Class1 class1 = Model3Factory.eINSTANCE.createClass1();
    res.getContents().add(class1);
    transaction.commit();

    CDOPackage model3Package = session.getPackageManager().lookupPackage(Model3Package.eINSTANCE.getNsURI());
    assertEquals(8, model3Package.getMetaIDRange().size());
    assertNotNull(model3Package.getEcore());

    CDOPackage subPackage = session.getPackageManager().lookupPackage(SubpackagePackage.eINSTANCE.getNsURI());
    assertNull(subPackage.getMetaIDRange());
    assertNull(subPackage.getEcore());
  }

  public void testCommitCircularPackages() throws Exception
  {
    {
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, true);
      session.getPackageRegistry().putEPackage(Model3Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Class1 class1 = Model3Factory.eINSTANCE.createClass1();
      res.getContents().add(class1);
      transaction.commit();
    }

    {
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      Class1 class1 = (Class1)res.getContents().get(0);
      assertNotNull(class1);
    }
  }

  /**
   * TODO Fix testDynamicPackage()
   */
  public void _testDynamicPackage() throws Exception
  {
    {
      // Obtain model
      EPackage model1 = loadModel("model1.ecore");
      // EClass supplierClass = (EClass)model1.getEClassifier("Supplier");
      // EStructuralFeature firstFeature =
      // supplierClass.getEStructuralFeatures().get(0);

      EClass companyClass = (EClass)model1.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");

      // Create resource in session 1
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
      session.getPackageRegistry().putEPackage(model1);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      CDOObject company = transaction.newInstance(companyClass);
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    {
      // Load resource in session 2
      CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      CDOObject company = (CDOObject)res.getContents().get(0);
      EClass companyClass = company.eClass();
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
      String name = (String)company.eGet(nameAttribute);
      assertEquals("Eike", name);
    }
  }

  private static EPackage loadModel(String fileName) throws IOException
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
    Resource resource = resourceSet.getResource(URI.createFileURI(fileName), true);
    return (EPackage)resource.getContents().get(0);
  }
}
