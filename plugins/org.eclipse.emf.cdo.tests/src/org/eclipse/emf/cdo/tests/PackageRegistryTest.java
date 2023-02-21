/*
 * Copyright (c) 2007-2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 332912 - Caching subtype-relationships in the CDOPackageRegistry
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistryPopulator;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.bundle.OM;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model2.SpecialPurchaseOrder;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "To not have ModelXPackage in test already registred in CDOPackageRegistry")
public class PackageRegistryTest extends AbstractCDOTest
{
  public void testGeneratedPackage() throws Exception
  {
    {
      // Create resource in session 1
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

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
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      SpecialPurchaseOrder specialPurchaseOrder = getModel2Factory().createSpecialPurchaseOrder();
      specialPurchaseOrder.setDiscountCode("12345");
      res.getContents().add(specialPurchaseOrder);
      transaction.commit();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    SpecialPurchaseOrder specialPurchaseOrder = (SpecialPurchaseOrder)res.getContents().get(0);
    assertEquals("12345", specialPurchaseOrder.getDiscountCode());
  }

  public void testCommitUnrelatedPackage() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      PurchaseOrder purchaseOrder = getModel1Factory().createPurchaseOrder();
      res.getContents().add(purchaseOrder);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    MangoValue value = getMangoFactory().createMangoValue();
    value.setName("V0");
    res.getContents().add(value);

    transaction.commit();
    session.close();
  }

  public void testCommitNestedPackages() throws Exception
  {
    CDOSession session = openSession();
    int oldSize = session.getPackageRegistry().size();

    session.getPackageRegistry().putEPackage(getModel3SubpackagePackage());
    assertEquals(oldSize + 2, session.getPackageRegistry().size());

    session.close();
  }

  public void testCommitTopLevelPackages() throws Exception
  {
    CDOSession session = openSession();
    int oldSize = session.getPackageRegistry().size();

    session.getPackageRegistry().putEPackage(getModel3Package());
    assertEquals(oldSize + 2, session.getPackageRegistry().size());

    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res"));

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
      CDOResource res = transaction.createResource(getResourcePath("/res"));

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
      CDOResource res1 = transaction.createResource(getResourcePath("/res1"));
      CDOResource res2 = transaction.createResource(getResourcePath("/res2"));

      Class1 class1 = getModel3Factory().createClass1();
      Class2 class2 = getModel3SubpackageFactory().createClass2();
      class1.getClass2().add(class2);

      res1.getContents().add(class1);
      res2.getContents().add(class2);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.getResource(getResourcePath("/res1"));

      Class1 class1 = (Class1)res1.getContents().get(0);
      assertNotNull(class1);
      EList<Class2> class22 = class1.getClass2();
      Class2 class2 = class22.get(0);
      assertNotNull(class2);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res2 = transaction.getResource(getResourcePath("/res2"));

    Class2 class2 = (Class2)res2.getContents().get(0);
    assertNotNull(class2);
    Class1 class1 = class2.getClass1().get(0);
    assertNotNull(class1);
  }

  public void testPackageRegistry() throws Exception
  {
    {
      // Create resource in session 1
      CDOSession session = openSession(IRepositoryConfig.REPOSITORY_NAME);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  /**
   * Bug 249383: Dynamic models in the global EPackage.Registry are not committed bug 249383
   */
  public void testGlobalDynamicPackageEager() throws Exception
  {
    EPackage p = createUniquePackage();
    String nsURI = p.getNsURI();

    try
    {
      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      EPackage.Registry.INSTANCE.put(nsURI, p);

      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(p);
      p = session.getPackageRegistry().getEPackage(nsURI);

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      EFactory factory = p.getEFactoryInstance();
      EObject object = factory.create(c);

      res.getContents().add(object);
      transaction.commit();
      session.close();
    }
    finally
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  /**
   * Bug 249383: Dynamic models in the global EPackage.Registry are not committed bug 249383
   */
  public void testGlobalDynamicPackage() throws Exception
  {
    EPackage p = createUniquePackage();
    String nsURI = p.getNsURI();

    try
    {
      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      if (!isConfig(LEGACY))
      {
        CDOUtil.prepareDynamicEPackage(p);
      }

      EPackage.Registry.INSTANCE.put(nsURI, p);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      EFactory factory = p.getEFactoryInstance();
      EObject object = factory.create(c);

      res.getContents().add(object);
      transaction.commit();
      session.close();
    }
    catch (Exception ex)
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  public void testDynamicPackageLoaded() throws Exception
  {
    EPackage model1 = loadModel("model1.ecore");

    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(model1);

    EFactory modelFactory = model1.getEFactoryInstance(); // Must happen AFTER putEPackage!!!
    EObject object = modelFactory.create((EClass)model1.getEClassifier("Company"));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    resource.getContents().add(object);

    transaction.commit();
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
      CDOResource res = transaction.createResource(getResourcePath("/res"));

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
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    CDOObject company = CDOUtil.getCDOObject(res.getContents().get(0));
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
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      CDOObject company = CDOUtil.getCDOObject(EcoreUtil.create(companyClass));
      company.eSet(nameAttribute, "Eike");
      res.getContents().add(company);
      transaction.commit();
      session.close();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    CDOObject company = CDOUtil.getCDOObject(res.getContents().get(0));
    EClass companyClass = company.eClass();
    EAttribute nameAttribute = (EAttribute)companyClass.getEStructuralFeature("name");
    String name = (String)company.eGet(nameAttribute);
    assertEquals("Eike", name);
    session.close();
  }

  public void testDuplicatePackageRegistration() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();

    try
    {
      {
        CDOTransaction transaction = session1.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res1"));

        Company company = getModel1Factory().createCompany();
        company.setName("Company1");
        res.getContents().add(company);
        transaction.commit();
        sleep(1000); // Give session2 a chance to react
      }

      CDOPackageRegistry packageRegistry = session2.getPackageRegistry();
      Model1Package model1Package = getModel1Package();
      packageRegistry.putEPackage(model1Package);

      CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(model1Package);
      assertEquals(CDOPackageUnit.State.LOADED, packageUnit.getState());

      {
        CDOTransaction transaction = session2.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res2"));

        Company company = getModel1Factory().createCompany();
        company.setName("Company2");
        res.getContents().add(company);
        transaction.commit();
      }
    }
    finally
    {
      session1.close();
      session2.close();
    }
  }

  public void testReuseCommittedPackage() throws Exception
  {
    final long timeStamp;
    final CDOSession session1 = openSession();
    final CDOSession session2 = openSession();

    try
    {
      {
        CDOTransaction transaction = session1.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res1"));

        Company company = getModel1Factory().createCompany();
        company.setName("Company1");
        res.getContents().add(company);
        timeStamp = transaction.commit().getTimeStamp();
      }

      assertNoTimeout(() -> session2.getLastUpdateTime() >= timeStamp);

      {
        CDOTransaction transaction = session2.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res2"));

        Company company = getModel1Factory().createCompany();
        company.setName("Company2");
        res.getContents().add(company);
        transaction.commit();
      }
    }
    finally
    {
      session1.close();
      session2.close();
    }
  }

  public void testConcurrentPackageRegistration() throws Exception
  {
    CDOSession session1 = openSession();
    session1.getPackageRegistry().putEPackage(getModel1Package());

    CDOSession session2 = openSession();
    session2.getPackageRegistry().putEPackage(getModel1Package());

    try
    {
      {
        CDOTransaction transaction = session1.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res1"));

        Company company = getModel1Factory().createCompany();
        company.setName("Company1");
        res.getContents().add(company);
        transaction.commit();
        sleep(1000); // Give session2 a chance to react
      }

      CDOPackageRegistry packageRegistry = session2.getPackageRegistry();
      Model1Package model1Package = getModel1Package();

      CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(model1Package);
      assertEquals(CDOPackageUnit.State.LOADED, packageUnit.getState());

      {
        CDOTransaction transaction = session2.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res2"));

        Company company = getModel1Factory().createCompany();
        company.setName("Company2");
        res.getContents().add(company);
        transaction.commit();
      }
    }
    finally
    {
      session1.close();
      session2.close();
    }
  }

  /**
   * Bug 353246.
   * <p>
   * Cannot reproduce the problem with MEMStore because MEMStoreAccessor.writePackageUnits() is empty.
   */
  public void testConcurrentPackageRegistration2() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath("/res1"));
    transaction1.commit();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.createResource(getResourcePath("/res2"));
    transaction2.commit();

    session2.options().setPassiveUpdateEnabled(false);

    resource1.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    resource2.getContents().add(getModel1Factory().createCompany());
    transaction2.commit();
  }

  public void testPopulator() throws Exception
  {
    EPackage.Registry registry = new EPackageRegistryImpl();
    EPackage p = createUniquePackage();
    String nsURI = p.getNsURI();

    {
      EClass c = EcoreFactory.eINSTANCE.createEClass();
      c.setName("DClass");

      p.getEClassifiers().add(c);
      registry.put(nsURI, p);

      CDOSession session = openSession();
      CDOPackageRegistryPopulator.populate(registry, session.getPackageRegistry());

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  public void testPopulatorGlobal() throws Exception
  {
    EPackage.Registry registry = EPackage.Registry.INSTANCE;
    EPackage p = createUniquePackage();
    String nsURI = p.getNsURI();

    try
    {
      {
        EClass c = EcoreFactory.eINSTANCE.createEClass();
        c.setName("DClass");

        p.getEClassifiers().add(c);
        registry.put(nsURI, p);

        CDOSession session = openSession();
        CDOPackageRegistryPopulator.populate(registry, session.getPackageRegistry());

        CDOTransaction transaction = session.openTransaction();
        CDOResource res = transaction.createResource(getResourcePath("/res"));

        Company company = getModel1Factory().createCompany();
        company.setName("Eike");
        res.getContents().add(company);
        transaction.commit();
      }

      // Load resource in session 2
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource(getResourcePath("/res"));

      Company company = (Company)res.getContents().get(0);
      assertEquals("Eike", company.getName());
    }
    finally
    {
      EPackage.Registry.INSTANCE.remove(nsURI);
    }
  }

  public void testLaziness() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      Company company = getModel1Factory().createCompany();
      company.setName("Eike");
      res.getContents().add(company);
      transaction.commit();
    }

    // Load resource in session 2
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    Company company = (Company)res.getContents().get(0);
    assertEquals("Eike", company.getName());
  }

  public void testSubclassCacheInvalidation() throws IOException
  {
    ResourceSet rs = new ResourceSetImpl();
    rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
    Resource r1 = rs.createResource(URI.createURI("SubclassTest1.ecore"));
    r1.load(OM.BUNDLE.getInputStream("SubclassTest1.ecore"), null);
    EPackage p1 = (EPackage)r1.getContents().get(0);
    Resource r2 = rs.createResource(URI.createURI("SubclassTest2.ecore"));
    r2.load(OM.BUNDLE.getInputStream("SubclassTest2.ecore"), null);
    EPackage p2 = (EPackage)r2.getContents().get(0);

    CDOSession session = openSession();
    CDOPackageRegistry registry = session.getPackageRegistry();
    registry.putEPackage(p1);
    registry.putEPackage(p2);

    Map<EClass, List<EClass>> subTypes = registry.getSubTypes();

    assertSubtypes((EClass)p1.getEClassifier("RootClass"), subTypes,
        Arrays.asList("Child1", "Child4", "SubChild", "SubChild3", "Child5", "Child6", "SubChild2", "Child7", "Child8"));
    assertSubtypes((EClass)p1.getEClassifier("RootAbstractClass"), subTypes,
        Arrays.asList("Child2", "Child4", "SubChild", "SubChild3", "Child6", "SubChild2", "Child8"));
    assertSubtypes((EClass)p1.getEClassifier("RootInterface"), subTypes,
        Arrays.asList("Child3", "Child4", "SubChild", "SubChild3", "Child6", "SubChild2", "Child8"));
  }

  public void testSubclassCache() throws IOException
  {
    ResourceSet rs = new ResourceSetImpl();
    rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
    Resource r1 = rs.createResource(URI.createURI("SubclassTest1.ecore"));
    r1.load(OM.BUNDLE.getInputStream("SubclassTest1.ecore"), null);
    EPackage p1 = (EPackage)r1.getContents().get(0);
    Resource r2 = rs.createResource(URI.createURI("SubclassTest2.ecore"));
    r2.load(OM.BUNDLE.getInputStream("SubclassTest2.ecore"), null);
    EPackage p2 = (EPackage)r2.getContents().get(0);

    CDOSession session = openSession();
    CDOPackageRegistry registry = session.getPackageRegistry();
    registry.putEPackage(p1);
    Map<EClass, List<EClass>> subTypes = registry.getSubTypes();

    assertSubtypes((EClass)p1.getEClassifier("RootClass"), subTypes,
        Arrays.asList("Child1", "Child4", "SubChild", "SubChild3", "Child5", "Child6", "SubChild2"));
    assertSubtypes((EClass)p1.getEClassifier("RootAbstractClass"), subTypes, Arrays.asList("Child2", "Child4", "SubChild", "SubChild3", "Child6", "SubChild2"));
    assertSubtypes((EClass)p1.getEClassifier("RootInterface"), subTypes, Arrays.asList("Child3", "Child4", "SubChild", "SubChild3", "Child6", "SubChild2"));

    registry.putEPackage(p2);
    subTypes = registry.getSubTypes();

    assertSubtypes((EClass)p1.getEClassifier("RootClass"), subTypes,
        Arrays.asList("Child1", "Child4", "SubChild", "SubChild3", "Child5", "Child6", "SubChild2", "Child7", "Child8"));
    assertSubtypes((EClass)p1.getEClassifier("RootAbstractClass"), subTypes,
        Arrays.asList("Child2", "Child4", "SubChild", "SubChild3", "Child6", "SubChild2", "Child8"));
    assertSubtypes((EClass)p1.getEClassifier("RootInterface"), subTypes,
        Arrays.asList("Child3", "Child4", "SubChild", "SubChild3", "Child6", "SubChild2", "Child8"));
  }

  private void assertSubtypes(EClass eClass, Map<EClass, List<EClass>> subTypes, List<String> expected)
  {
    List<EClass> actual = subTypes.get(eClass);
    String[] actualArray = new String[actual.size()];
    for (int i = 0; i < actualArray.length; i++)
    {
      actualArray[i] = actual.get(i).getName();
    }

    String[] expectedArray = expected.toArray(new String[expected.size()]);

    Arrays.sort(actualArray);
    Arrays.sort(expectedArray);

    assertEquals(expectedArray, actualArray);
  }

  public static EPackage loadModel(String fileName) throws IOException
  {
    URI uri = URI.createURI(fileName);
    XMIResource resource = new XMIResourceImpl(uri);
    resource.setEncoding("UTF-8");
    resource.load(OM.BUNDLE.getInputStream(fileName), null);
    return (EPackage)resource.getContents().get(0);
  }
}
