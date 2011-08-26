/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_351393_Test extends AbstractCDOTest
{
  public void testExtRef() throws Exception
  {
    skipStoreWithoutExternalReferences();

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("test", new XMIResourceFactoryImpl());
    URI uri = URI.createURI("test:///tmp/file.xmi?" + "12345678901234567890" + // 41
        "12345678901234567890" + // 61
        "12345678901234567890" + // 81
        "12345678901234567890" + // 101
        "12345678901234567890" + // 121
        "12345678901234567890" + // 141
        "12345678901234567890" + // 161
        "12345678901234567890" + // 181
        "12345678901234567890" + // 201
        "12345678901234567890" + // 221
        "12345678901234567890" + // 241
        "12345678901234567890" + // 261
        "12345678901234567890" + // 281
        "12345678901234567890" + // 301
        "12345678901234567890"); // 321 characters (> 256)

    Resource extRes = resourceSet.createResource(uri);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test1"));

    resourceSet.getResource(resource.getURI(), false);

    OrderDetail od1 = getModel1Factory().createOrderDetail();
    Product1 p1 = getModel1Factory().createProduct1();
    p1.getOrderDetails().add(od1);

    resource.getContents().add(p1);
    extRes.getContents().add(od1);

    transaction.commit();
  }
}
