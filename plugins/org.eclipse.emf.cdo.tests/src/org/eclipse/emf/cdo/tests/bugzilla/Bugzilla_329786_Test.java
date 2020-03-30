/*
 * Copyright (c) 2012, 2015, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model1.Supplier;
import org.eclipse.emf.cdo.util.CDOURIData;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * @author Eike Stepper
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
public class Bugzilla_329786_Test extends AbstractCDOTest
{
  public void testConnectionAwareURI() throws Exception
  {
    getRepository();

    URI localURI = createLocalAndRemoteResource();
    accessRemoteObjectByLocalReferences(localURI);
  }

  private URI createLocalAndRemoteResource() throws Exception
  {
    ResourceSet resourceSet = createResourceSet();

    URI sharedURI = URI.createURI(CDONet4jUtil.PROTOCOL_TCP + "://localhost:2036/" + RepositoryConfig.REPOSITORY_NAME + getResourcePath("/sharedResource"))
        .appendQuery(CDOURIData.TRANSACTIONAL_PARAMETER + "=true");
    Resource sharedResource = resourceSet.createResource(sharedURI);

    URI localURI = URI.createFileURI(createTempFile(getName(), ".model1").getCanonicalPath());
    Resource localResource = resourceSet.createResource(localURI);

    Company localCompany = getModel1Factory().createCompany();
    localCompany.setName("localCompany");

    Company sharedCompany = getModel1Factory().createCompany();
    sharedCompany.setName("sharedCompany");

    Customer localCustomer = getModel1Factory().createCustomer();
    localCustomer.setName("localCustomer");

    Customer sharedCustomer = getModel1Factory().createCustomer();
    sharedCustomer.setName("sharedCustomer");

    Supplier localSupplier = getModel1Factory().createSupplier();
    localSupplier.setName("localSupplier");

    Supplier sharedSupplier = getModel1Factory().createSupplier();
    sharedSupplier.setName("sharedSupplier");

    SalesOrder sharedSalesOrder = getModel1Factory().createSalesOrder();
    SalesOrder localSalesOrder = getModel1Factory().createSalesOrder();
    PurchaseOrder sharedPurchaseOrder = getModel1Factory().createPurchaseOrder();
    PurchaseOrder localPurchaseOrder = getModel1Factory().createPurchaseOrder();

    sharedResource.getContents().add(sharedCompany);
    sharedCompany.getCustomers().add(sharedCustomer);
    sharedCompany.getSuppliers().add(sharedSupplier);
    sharedCompany.getSalesOrders().add(sharedSalesOrder);
    sharedCompany.getPurchaseOrders().add(sharedPurchaseOrder);

    localResource.getContents().add(localCompany);
    localCompany.getCustomers().add(localCustomer);
    localCompany.getSuppliers().add(localSupplier);
    localCompany.getSalesOrders().add(localSalesOrder);
    localCompany.getPurchaseOrders().add(localPurchaseOrder);

    localCustomer.getSalesOrders().add(sharedSalesOrder);
    localSupplier.getPurchaseOrders().add(sharedPurchaseOrder);

    sharedCustomer.getSalesOrders().add(localSalesOrder);
    sharedSupplier.getPurchaseOrders().add(localPurchaseOrder);

    sharedResource.save(Collections.emptyMap());
    localResource.save(Collections.emptyMap());
    return localURI;
  }

  private void accessRemoteObjectByLocalReferences(URI localURI)
  {
    ResourceSet resourceSet = createResourceSet();
    Resource localResource = resourceSet.getResource(localURI, true);
    assertEquals(true, localResource.getContents().get(0) instanceof Company);

    Company localCompany = (Company)localResource.getContents().get(0);
    assertEquals("localCompany", localCompany.getName());

    Customer localCustomer = localCompany.getCustomers().get(0);
    assertEquals(1, localCustomer.getSalesOrders().size());

    Supplier localSupplier = localCompany.getSuppliers().get(0);
    assertEquals(1, localSupplier.getPurchaseOrders().size());

    SalesOrder sharedSalesOrder = localCustomer.getSalesOrders().get(0);
    assertEquals(true, sharedSalesOrder.eResource() instanceof CDOResource);

    PurchaseOrder sharedPurchaseOrder = localSupplier.getPurchaseOrders().get(0);
    assertEquals(true, sharedPurchaseOrder.eResource() instanceof CDOResource);
  }

  private static ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    Registry registry = resourceSet.getResourceFactoryRegistry();
    registry.getProtocolToFactoryMap().put(CDONet4jUtil.PROTOCOL_TCP, CDOResourceFactory.INSTANCE);
    registry.getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());
    return resourceSet;
  }
}
