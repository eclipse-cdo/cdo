/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Bug 563616 - DanglingReferenceException for unresolved EMF proxies at commit time.
 *
 * @author Eike Stepper
 */
public class Bugzilla_563616_Test extends AbstractCDOTest
{
  public void testImportProxyObject() throws Exception
  {
    // Create model.
    Customer customer = getModel1Factory().createCustomer();

    SalesOrder order = getModel1Factory().createSalesOrder();
    order.setCustomer(customer);

    // Create resources.
    ResourceSet resourceSet = createResourceSet();

    URI customerURI = URI.createFileURI(getTempName("test-customer-", ".xmi").getAbsolutePath());
    Resource customerResource = resourceSet.createResource(customerURI);
    customerResource.getContents().add(customer);

    URI orderURI = URI.createFileURI(getTempName("test-order-", ".xmi").getAbsolutePath());
    Resource orderResource = resourceSet.createResource(orderURI);
    orderResource.getContents().add(order);

    customerResource.save(null);
    orderResource.save(null);

    // Load order resource.
    resourceSet = createResourceSet();
    orderResource = resourceSet.getResource(orderURI, true);
    order = (SalesOrder)orderResource.getContents().get(0);

    // Assert that the customer is a proxy.
    InternalEObject proxy = (InternalEObject)order.eGet(getModel1Package().getSalesOrder_Customer(), false);
    assertTrue(proxy.eProxyURI() != null);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource cdoResource = transaction.createResource(getResourcePath("order"));
    cdoResource.getContents().add(order);

    // A DanglingReferenceException is thrown from AbstractCDOView.provideCDOID() if the proxy is not resolved.
    transaction.commit();
  }

  private ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    return resourceSet;
  }
}
