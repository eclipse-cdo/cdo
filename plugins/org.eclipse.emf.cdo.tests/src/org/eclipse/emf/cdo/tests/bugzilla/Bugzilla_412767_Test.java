/*
 * Copyright (c) 2013, 2016, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.Collections;

/**
 * Bug 412767: "IllegalArgumentException: id is null on" on ResourceSet.getResource() with connection aware URI with prefetch query.
 *
 * @author Esteban Dugueperoux
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
public class Bugzilla_412767_Test extends AbstractCDOTest
{
  public void testConnectionAwareURIWithPrefetch() throws Exception
  {
    getRepository();

    URI sharedResourceURI = createRemoteResource();
    URI sharedResourceURIWithPrefetch = sharedResourceURI.appendQuery(sharedResourceURI.query() + "&" + CDOResource.PREFETCH_PARAMETER + "=true");

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(CDONet4jUtil.PROTOCOL_TCP, CDOResourceFactory.INSTANCE);

    CDOResource sharedResource = (CDOResource)resourceSet.getResource(sharedResourceURIWithPrefetch, true);
    assertEquals(CDOState.PROXY, sharedResource.cdoState());
  }

  public void testConnectionAwareURIWithPrefetchAfter() throws Exception
  {
    getRepository();

    URI sharedResourceURI = createRemoteResource();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(CDONet4jUtil.PROTOCOL_TCP, CDOResourceFactory.INSTANCE);

    CDOResource sharedResource = (CDOResource)resourceSet.getResource(sharedResourceURI, true);
    sharedResource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    assertEquals(CDOState.PROXY, sharedResource.cdoState());
  }

  private URI createRemoteResource() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put(CDONet4jUtil.PROTOCOL_TCP, CDOResourceFactory.INSTANCE);

    URI sharedResourceURI = URI
        .createURI(CDONet4jUtil.PROTOCOL_TCP + "://localhost:2036/" + RepositoryConfig.REPOSITORY_NAME + getResourcePath("/sharedResource"))
        .appendQuery(CDOURIData.TRANSACTIONAL_PARAMETER + "=true");
    Resource sharedResource = resourceSet.createResource(sharedResourceURI);

    Company sharedCompany = getModel1Factory().createCompany();
    sharedCompany.setName("sharedCompany");

    Customer sharedCustomer = getModel1Factory().createCustomer();
    sharedCustomer.setName("sharedCustomer");

    Supplier sharedSupplier = getModel1Factory().createSupplier();
    sharedSupplier.setName("sharedSupplier");

    SalesOrder sharedSalesOrder = getModel1Factory().createSalesOrder();
    PurchaseOrder sharedPurchaseOrder = getModel1Factory().createPurchaseOrder();

    sharedCompany.getCustomers().add(sharedCustomer);
    sharedCompany.getSuppliers().add(sharedSupplier);
    sharedCompany.getSalesOrders().add(sharedSalesOrder);
    sharedCompany.getPurchaseOrders().add(sharedPurchaseOrder);

    sharedResource.getContents().add(sharedCompany);
    sharedResource.save(Collections.emptyMap());
    return sharedResourceURI;
  }
}
