/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.evolution.Evolution;
import org.eclipse.emf.cdo.evolution.EvolutionFactory;
import org.eclipse.emf.cdo.evolution.Model;
import org.eclipse.emf.cdo.evolution.Release;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.spi.evolution.AbstractMigrationContext;
import org.eclipse.emf.cdo.server.spi.evolution.EvolutionSupport;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;

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

import java.io.File;

/**
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "Needs clean package registry")
public class EvolutionTest extends AbstractCDOTest
{
  private Model createEvolution(String modelPath)
  {
    String modelName = URI.createFileURI(modelPath).trimFileExtension().lastSegment();
    String tempFolder = createTempFolder(getTestMethodName() + "-").toString();
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet();

    Evolution evolution = EvolutionFactory.eINSTANCE.createEvolution();
    URI evolutionURI = URI.createFileURI(tempFolder).appendSegment(modelName + ".evolution");
    Resource evolutionResource = resourceSet.createResource(evolutionURI);
    evolutionResource.getContents().add(evolution);

    File modelFile = new File(tempFolder, modelName + ".ecore");
    IOUtil.copyFile(new File(modelPath), modelFile);
    URI modelURI = URI.createFileURI(modelFile.toString());
    Model model = evolution.addModel(modelURI);

    IOUtil.OUT().println("Evolution: " + evolutionURI.path());
    IOUtil.OUT().println("Model:     " + modelURI.path());
    IOUtil.OUT().println();

    evolution.ensureIDs();
    evolution.save();

    return model;
  }

  private void migrate(Release release)
  {
    AbstractMigrationContext context = new AbstractMigrationContext(release)
    {
      public void log(Object msg)
      {
        IOUtil.OUT().println(msg);
      }
    };

    EvolutionSupport evolutionSupport = (EvolutionSupport)getRepository().getStore().getReader(null);
    StoreThreadLocal.setAccessor(evolutionSupport);

    try
    {
      context.migrate(evolutionSupport, new Monitor());
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  public void testInitialRelease() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    Release v1 = model.getEvolution().createRelease();
    migrate(v1);

    CDOSession session = openSession();

    String nsURI = model.getRootPackage().getNsURI();
    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject company = new SessionPackage(nsURI).create("Company");
    eSet(company, "name", "ESC");

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(company);
    transaction.commit();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("res"));
    EObject company2 = resource2.getContents().get(0);
    assertEquals("ESC", eGet(company2, "name"));
  }

  public void testAddClass() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    EPackage ePackage = model.getRootPackage();

    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Employee");
    eClass.getESuperTypes().add((EClass)ePackage.getEClassifier("Address"));

    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName("salary");
    eAttribute.setEType(EcorePackage.Literals.EDOUBLE);

    ePackage.getEClassifiers().add(eClass);

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    String nsURI = model.getRootPackage().getNsURI();
    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject employee = new SessionPackage(nsURI).create("Employee");
    eSet(employee, "name", "Eike");

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(employee);
    transaction.commit();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("res"));
    EObject employee2 = resource2.getContents().get(0);
    assertEquals("Eike", eGet(employee2, "name"));
  }

  public void testRenameClass() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");
    addressClass.setName("Addressable");

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    String nsURI = model.getRootPackage().getNsURI();
    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject addressable = new SessionPackage(nsURI).create("Addressable");
    eSet(addressable, "name", "Somebody");

    EObject customer = new SessionPackage(nsURI).create("Customer");
    eSet(customer, "name", "Eike");

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(addressable);
    resource.getContents().add(customer);
    transaction.commit();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    CDOResource resource2 = transaction2.getOrCreateResource(getResourcePath("res"));

    EObject addressable2 = resource2.getContents().get(0);
    assertEquals("Addressable", addressable2.eClass().getName());
    assertEquals("Somebody", eGet(addressable2, "name"));

    EObject customer2 = resource2.getContents().get(1);
    assertEquals("Customer", customer2.eClass().getName());
    assertEquals("Eike", eGet(customer2, "name"));
    session2.close();

    ePackage = model.getRootPackage();
    addressClass = (EClass)ePackage.getEClassifier("Addressable");
    addressClass.setName("AddressableXXX");

    Release v3 = evolution.createRelease();
    migrate(v3);

    CDOSession session3 = openSession();
    CDOTransaction transaction3 = session3.openTransaction();
    CDOResource resource3 = transaction3.getOrCreateResource(getResourcePath("res"));

    EObject addressable3 = resource3.getContents().get(0);
    assertEquals("AddressableXXX", addressable3.eClass().getName());
    assertEquals("Somebody", eGet(addressable3, "name"));

    EObject customer3 = resource3.getContents().get(1);
    assertEquals("Customer", customer3.eClass().getName());
    assertEquals("Eike", eGet(customer3, "name"));
    session3.close();
  }

  public void testAddAttribute_SingleValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    EObject customer0 = new SessionPackage(nsURI).create("Customer");
    resource0.getContents().add(customer0);
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");

    EAttribute zipCodeAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    zipCodeAttribute.setName("zipCode");
    zipCodeAttribute.setEType(EcorePackage.Literals.EINT);

    addressClass.getEStructuralFeatures().add(zipCodeAttribute);

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject customer = new SessionPackage(nsURI).create("Customer");
    eSet(customer, "zipCode", 32584);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(customer);
    transaction.commit();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject customer2 = transaction2.getObject(customerID);
    assertEquals(32584, eGet(customer2, "zipCode"));
  }

  public void testAddAttribute_ManyValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    EObject customer0 = new SessionPackage(nsURI).create("Customer");
    resource0.getContents().add(customer0);
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");

    EAttribute zipCodesAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    zipCodesAttribute.setName("zipCodes");
    zipCodesAttribute.setEType(EcorePackage.Literals.EINT);
    zipCodesAttribute.setUpperBound(-1);

    addressClass.getEStructuralFeatures().add(zipCodesAttribute);

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject customer = new SessionPackage(nsURI).create("Customer");
    eAdd(customer, "zipCodes", 32584);
    eAdd(customer, "zipCodes", 10243);
    eAdd(customer, "zipCodes", 10777);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(customer);
    transaction.commit();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject customer2 = transaction2.getObject(customerID);
    assertEquals(32584, (int)(Integer)eGet(customer2, "zipCodes", 0));
    assertEquals(10243, (int)(Integer)eGet(customer2, "zipCodes", 1));
    assertEquals(10777, (int)(Integer)eGet(customer2, "zipCodes", 2));
  }

  public void testRenameAttribute_SingleValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    EObject customer0 = new SessionPackage(nsURI).create("Customer");
    resource0.getContents().add(customer0);
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");
    addressClass.getEStructuralFeature("city").setName("zipCodeAndCity");

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject customer = new SessionPackage(nsURI).create("Customer");
    eSet(customer, "zipCodeAndCity", "32584 Loehne");

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(customer);
    transaction.commit();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject customer2 = transaction2.getObject(customerID);
    assertEquals("32584 Loehne", eGet(customer2, "zipCodeAndCity"));
  }

  public void testRenameAttribute_ManyValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    SessionPackage sessionPackage0 = new SessionPackage(nsURI);
    EObject company0 = sessionPackage0.create("Company");
    resource0.getContents().add(company0);
    eAdd(company0, "customers", sessionPackage0.create("Customer"));
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass companyClass = (EClass)ePackage.getEClassifier("Company");
    companyClass.getEStructuralFeature("customers").setName("esteemedCustomers");

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    SessionPackage sessionPackage = new SessionPackage(nsURI);
    EObject company = sessionPackage.create("Company");
    eAdd(company, "esteemedCustomers", sessionPackage.create("Customer"));
    eAdd(company, "esteemedCustomers", sessionPackage.create("Customer"));
    eAdd(company, "esteemedCustomers", sessionPackage.create("Customer"));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(company);
    transaction.commit();
    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject company2 = transaction2.getObject(companyID);
    EList<EObject> esteemedCustomers2 = eList(company2, "esteemedCustomers");
    assertEquals("Customer", esteemedCustomers2.get(0).eClass().getName());
    assertEquals("Customer", esteemedCustomers2.get(1).eClass().getName());
    assertEquals("Customer", esteemedCustomers2.get(2).eClass().getName());
  }

  public void testAddReference_SingleValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    EObject customer0 = new SessionPackage(nsURI).create("Customer");
    resource0.getContents().add(customer0);
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");

    EReference siteReference = EcoreFactory.eINSTANCE.createEReference();
    siteReference.setName("site");
    siteReference.setEType(addressClass);
    siteReference.setContainment(true);

    addressClass.getEStructuralFeatures().add(siteReference);

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    SessionPackage sessionPackage = new SessionPackage(nsURI);
    EObject customer = sessionPackage.create("Customer");
    eSet(customer, "site", sessionPackage.create("Customer"));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(customer);
    transaction.commit();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject customer2 = transaction2.getObject(customerID);
    EObject site2 = eGet(customer2, "site");
    assertEquals("Customer", site2.eClass().getName());
  }

  public void testAddReference_ManyValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    EObject customer0 = new SessionPackage(nsURI).create("Customer");
    resource0.getContents().add(customer0);
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");

    EReference sitesReference = EcoreFactory.eINSTANCE.createEReference();
    sitesReference.setName("sites");
    sitesReference.setEType(addressClass);
    sitesReference.setContainment(true);
    sitesReference.setUpperBound(-1);

    addressClass.getEStructuralFeatures().add(sitesReference);

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    SessionPackage sessionPackage = new SessionPackage(nsURI);
    EObject customer = sessionPackage.create("Customer");
    eAdd(customer, "sites", sessionPackage.create("Customer"));
    eAdd(customer, "sites", sessionPackage.create("Customer"));
    eAdd(customer, "sites", sessionPackage.create("Customer"));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(customer);
    transaction.commit();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject customer2 = transaction2.getObject(customerID);
    assertEquals(3, eList(customer2, "sites").size());
  }

  public void testChangeAttributeType_SingleValued() throws Exception
  {
    // InternalRepository repository = getRepository();
    // IDBStore store = (IDBStore)repository.getStore();
    // IDBSchema schema = store.getDatabase().getSchema();
    // IMappingStrategy mappingStrategy = store.getMappingStrategy();

    Model model = createEvolution("evolution/model1.ecore");
    String nsURI = model.getRootPackage().getNsURI();
    Evolution evolution = model.getEvolution();

    Release v1 = evolution.createRelease();
    migrate(v1);

    CDOSession session0 = openSession();
    CDOTransaction transaction0 = session0.openTransaction();
    CDOResource resource0 = transaction0.getOrCreateResource(getResourcePath("res"));
    EObject customer0 = new SessionPackage(nsURI).create("Customer");
    resource0.getContents().add(customer0);
    transaction0.commit();
    session0.close();

    EPackage ePackage = model.getRootPackage();
    EClass addressClass = (EClass)ePackage.getEClassifier("Address");
    addressClass.getEStructuralFeature("city").setEType(EcorePackage.Literals.EINT);

    Release v2 = evolution.createRelease();
    migrate(v2);

    CDOSession session = openSession();

    CDOPackageUnit packageUnit = session.getPackageRegistry().getPackageUnit(nsURI);
    assertNotNull(packageUnit);

    EObject customer = new SessionPackage(nsURI).create("Customer");
    eSet(customer, "city", 32584);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("res"));
    resource.getContents().add(customer);
    transaction.commit();
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    session.close();

    CDOSession session2 = openSession();
    CDOTransaction transaction2 = session2.openTransaction();
    EObject customer2 = transaction2.getObject(customerID);
    assertEquals(32584, eGet(customer2, "city"));
  }

  public void _testNewPackageVersionWithSameNSURI() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testNewTransientFeature() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testAdditionalIndexes() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testRenameUnsettableFeature() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testChangeAttributeToSingleValued() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testChangeAttributeToManyValued() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testChangeReferenceToSingleValued() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testChangeReferenceToManyValued() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testChangeAttributeToReference() throws Exception
  {
    throw new UnsupportedOperationException();
  }

  public void _testChangeReferenceToAttribute() throws Exception
  {
    throw new UnsupportedOperationException();
  }
}
