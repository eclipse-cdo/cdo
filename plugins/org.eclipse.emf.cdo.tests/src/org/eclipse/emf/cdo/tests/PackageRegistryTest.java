/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.mango.Value;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.SubpackageFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.internal.cdo.CDOFactoryImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

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
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  public void testCommitTwoPackages() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel1Package());
      session.getPackageRegistry().putEPackage(getModel2Package());
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      SpecialPurchaseOrder specialPurchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
      specialPurchaseOrder.setDiscountCode("12345");
      res.getContents().add(specialPurchaseOrder);
      transaction.commit();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    SpecialPurchaseOrder specialPurchaseOrder = (SpecialPurchaseOrder)res.getContents().get(0);
    assertEquals("12345", specialPurchaseOrder.getDiscountCode());
  }

  public void testCommitUnrelatedPackage() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
      res.getContents().add(purchaseOrder);

      transaction.commit();
      session.close();
    }

    CDOSession session = openMangoSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Value value = getMangoFactory().createValue();
    value.setName("V0");
    res.getContents().add(value);

    transaction.commit();
    session.close();
  }

  public void testCommitNestedPackages() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(2, session.getPackageRegistry().size());

    session.getPackageRegistry().putEPackage(getModel3SubpackagePackage());
    assertEquals(4, session.getPackageRegistry().size());

    session.close();
  }

  public void testCommitTopLevelPackages() throws Exception
  {
    CDOSession session = openSession();
    assertEquals(2, session.getPackageRegistry().size());

    session.getPackageRegistry().putEPackage(getModel3Package());
    assertEquals(4, session.getPackageRegistry().size());

    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res");

    Class1 class1 = getModel3Factory().createClass1();
    res.getContents().add(class1);
    transaction.commit();

    EPackage model3Package = session.getPackageRegistry().getEPackage(getModel3Package().getNsURI());
    assertNotNull(model3Package);
    session.close();
  }

  public void testLoadNestedPackages() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(getModel3Package());

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Class1 class1 = getModel3Factory().createClass1();
      res.getContents().add(class1);
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    EPackage model3Package = session.getPackageRegistry().getEPackage(getModel3Package().getNsURI());
    assertNotNull(model3Package);

    EPackage subPackage = session.getPackageRegistry().getEPackage(getModel3SubpackagePackage().getNsURI());
    assertNotNull(subPackage);
    session.close();
  }

  public void testCommitCircularPackages() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(Model3Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.createResource("/res1");
      CDOResource res2 = transaction.createResource("/res2");

      Class1 class1 = getModel3Factory().createClass1();
      Class2 class2 = SubpackageFactory.eINSTANCE.createClass2();
      class1.getClass2().add(class2);

      res1.getContents().add(class1);
      res2.getContents().add(class2);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.getResource("/res1");

      Class1 class1 = (Class1)res1.getContents().get(0);
      assertNotNull(class1);
      Class2 class2 = class1.getClass2().get(0);
      assertNotNull(class2);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res2 = transaction.getResource("/res2");

    Class2 class2 = (Class2)res2.getContents().get(0);
    assertNotNull(class2);
    Class1 class1 = class2.getClass1().get(0);
    assertNotNull(class1);
  }

  public void testEagerPackageRegistry() throws Exception
  {
    {
      // Create resource in session 1
      CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
      configuration.setConnector(getConnector());
      configuration.setRepositoryName(IRepositoryConfig.REPOSITORY_NAME);

      CDOSession session = configuration.openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  public void testLazyPackageRegistry() throws Exception
  {
    {
      // Create resource in session 1
      CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
      configuration.setConnector(getConnector());
      configuration.setRepositoryName(IRepositoryConfig.REPOSITORY_NAME);

      CDOSession session = configuration.openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  /**
   * Bug 249383: Dynamic models in the global EPackage.Registry are not committed
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=249383
   */
  public void _testGlobalDynamicPackageEager() throws Exception
  {
    EPackage p = EcoreFactory.eINSTANCE.createEPackage();
    p.setName("dynamic");
    p.setNsPrefix("dynamic");
    p.setNsURI("http://dynamic");

    EClass c = EcoreFactory.eINSTANCE.createEClass();
    c.setName("DClass");

    p.getEClassifiers().add(c);
    EPackage.Registry.INSTANCE.put(p.getNsURI(), p);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res");

    EFactory factory = p.getEFactoryInstance();
    EObject object = factory.create(c);

    res.getContents().add(object);
    transaction.commit();
    session.close();
  }

  /**
   * Bug 249383: Dynamic models in the global EPackage.Registry are not committed
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=249383
   */
  public void testGlobalDynamicPackageLazy() throws Exception
  {
    EPackage p = EcoreFactory.eINSTANCE.createEPackage();
    p.setName("dynamic");
    p.setNsPrefix("dynamic");
    p.setNsURI("http://dynamic");

    EClass c = EcoreFactory.eINSTANCE.createEClass();
    c.setName("DClass");

    p.getEClassifiers().add(c);
    CDOFactoryImpl.prepareDynamicEPackage(p);
    EPackage.Registry.INSTANCE.put(p.getNsURI(), p);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource("/res");

    EFactory factory = p.getEFactoryInstance();
    EObject object = factory.create(c);

    res.getContents().add(object);
    transaction.commit();
    session.close();
  }

  public void testDynamicPackageFactory() throws Exception
  {
    // -Dorg.eclipse.emf.ecore.EPackage.Registry.INSTANCE=org.eclipse.emf.ecore.impl.CDOPackageRegistryImpl

    {
      EPackage model1 = loadModel("model1.ecore");
      EClass companyClass = (EClass)model1.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
      Resource model1Resource = model1.eResource();

      // Create resource in session 1
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model1);
      assertEquals(model1Resource, model1.eResource());

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      EFactory factory = model1.getEFactoryInstance();
      EObject company = factory.create(companyClass);
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
      session.close();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    CDOObject company = (CDOObject)res.getContents().get(0);
    EClass companyClass = company.eClass();
    EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
    String name = (String)company.eGet(nameAttribute);
    assertEquals("Eike", name);
    session.close();
  }

  public void testDynamicPackageNewInstance() throws Exception
  {
    {
      EPackage model1 = loadModel("model1.ecore");
      EClass companyClass = (EClass)model1.getEClassifier("Company");
      EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");

      // Create resource in session 1
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(model1);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      CDOObject company = (CDOObject)EcoreUtil.create(companyClass);
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
      session.close();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource("/res");

    CDOObject company = (CDOObject)res.getContents().get(0);
    EClass companyClass = company.eClass();
    EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
    String name = (String)company.eGet(nameAttribute);
    assertEquals("Eike", name);
    session.close();
  }

  private static EPackage loadModel(String fileName) throws IOException
  {
    URI uri = URI.createURI("file://" + fileName);
    XMIResource resource = new XMIResourceImpl(uri);
    resource.setEncoding("UTF-8");
    resource.load(OM.BUNDLE.getInputStream(fileName), null);
    return (EPackage)resource.getContents().get(0);
  }
}
