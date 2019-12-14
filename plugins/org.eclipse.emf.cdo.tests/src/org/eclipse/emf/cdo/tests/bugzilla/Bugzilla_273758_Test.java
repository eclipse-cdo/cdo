/*
 * Copyright (c) 2009-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.util.TestEMFUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Export resource miss all of the oneToMany references
 * <p>
 * See bug 273758
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_273758_Test extends AbstractCDOTest
{
  public void test_export_resource_contents() throws Exception
  {
    {
      // Create 3 root objects: 1 Product1, 2 OrderDetails.
      // Product1 will reference both OrderDetails

      Product1 product = getModel1Factory().createProduct1();
      product.setName("product");
      OrderDetail detail1 = getModel1Factory().createOrderDetail();
      detail1.setPrice(1.99f);
      OrderDetail detail2 = getModel1Factory().createOrderDetail();
      detail2.setPrice(9.99f);

      product.getOrderDetails().add(detail1);
      product.getOrderDetails().add(detail2);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      resource.getContents().add(product);
      resource.getContents().add(detail1);
      resource.getContents().add(detail2);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      Product1 product2 = (Product1)resource.getContents().get(0);
      // Check in memory Product1 EObject reference integrity
      assertEquals(2, product2.getOrderDetails().size());

      Collection<EObject> collection = EcoreUtil.copyAll(resource.getContents());
      List<EObject> sourceContents = new ArrayList<>(collection);

      // Target Resource
      File file = createTempFile("exportModel1", null);
      String tempFileURI = file.toURI().toString();
      TestEMFUtil.saveXMI(tempFileURI, sourceContents);

      List<EObject> list = TestEMFUtil.loadXMIMultiple(tempFileURI, Model1Package.eINSTANCE);
      Product1 product3 = (Product1)list.get(0);
      assertEquals(2, product3.getOrderDetails().size());
    }
  }
}
