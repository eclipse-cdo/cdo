/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.teneo.PersistenceOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Tests with a separate annotations.xml.
 * 
 * @author Martin Taal
 */
public class HibernateExternalAnnotationTest extends AbstractCDOTest
{
  private final static String REPOSITORY2_NAME = "repo2";

  @Override
  protected void doSetUp() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    final String persistenceXML = "org/eclipse/emf/cdo/tests/hibernate/external_model1_4.persistence.xml";
    hbConfig.getAdditionalProperties().put(PersistenceOptions.PERSISTENCE_XML, persistenceXML);

    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    final IRepositoryConfig repConfig = getRepositoryConfig();
    final HibernateConfig hbConfig = (HibernateConfig)repConfig;
    hbConfig.getAdditionalProperties().clear();
    super.doTearDown();
  }

  public void testOneXMIResourceManyViewsOnOneResourceSet() throws Exception
  {
    byte[] dataOfresD = null;
    getRepository(REPOSITORY2_NAME);

    {
      CDOSession sessionA = openSession();
      CDOSession sessionB = openSession(REPOSITORY2_NAME);
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

      sessionA.getPackageRegistry().putEPackage(getModel1Package());
      sessionA.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);
      sessionB.getPackageRegistry().putEPackage(getModel1Package());
      sessionB.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);

      CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
      CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

      CDOResource resA = transactionA1.createResource("/resA");
      CDOResource resB = transactionB1.createResource("/resB");

      EList<Resource> resources = resourceSet.getResources();
      assertEquals(4, resources.size());

      CDOResource resC = transactionA1.createResource("/resC");
      assertNotNull(resC);
      assertEquals(5, resources.size());

      Resource resD = resourceSet.createResource(URI.createURI("test://1"));
      assertEquals(6, resources.size());
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

      // transSet.commit();
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

      CDOSession session2 = openSession(REPOSITORY2_NAME);
      CDOTransaction transaction2 = session2.openTransaction(resourceSet);

      CDOViewSet set = CDOUtil.getViewSet(resourceSet);
      assertNotNull(set);

      resourceSet.getPackageRegistry().put(getModel1Package().getNsURI(), getModel1Package());
      resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());

      Resource resD = resourceSet.createResource(URI.createURI("test://1"));
      resD.load(new ByteArrayInputStream(dataOfresD), null);

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
      assertEquals(transaction, CDOUtil.getCDOObject(pO).cdoView());
      assertEquals(supplierD, pO.getSupplier());
    }
  }
}
