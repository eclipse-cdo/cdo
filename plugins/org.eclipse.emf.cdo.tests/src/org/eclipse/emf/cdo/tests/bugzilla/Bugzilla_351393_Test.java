/*
 * Copyright (c) 2011-2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Bug 351393: [DB] Make the URI column in the table "cdo_ext_refs" larger
 *
 * @author Stefan Winkler
 */
public class Bugzilla_351393_Test extends AbstractCDOTest
{
  // Ext-Refs with client-side UUIDs are stored "in sito", where the tests use even less chars.
  @Skips(IRepositoryConfig.CAPABILITY_UUIDS)
  @Requires(IRepositoryConfig.CAPABILITY_EXTERNAL_REFS)
  public void testExtRef() throws Exception
  {
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
    p1.setName("test1");
    p1.getOrderDetails().add(od1);

    resource.getContents().add(p1);
    extRes.getContents().add(od1);

    transaction.commit();
  }
}
