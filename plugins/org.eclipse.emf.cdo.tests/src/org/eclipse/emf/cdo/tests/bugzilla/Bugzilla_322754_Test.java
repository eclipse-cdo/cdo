/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.DanglingReferenceException;

/**
 * Bug 322754 - NullPointerException after deleting a resource
 * 
 * @author Eike Stepper
 */
public class Bugzilla_322754_Test extends AbstractCDOTest
{
  public void testResourceDelete() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/r1");

    msg("Fill and commit a resource");
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    resource.getContents().add(getModel1Factory().createCompany());
    transaction.commit();

    msg("Delete and commit the resource");
    resource.delete(null);
    transaction.commit();
  }

  public void testResourceDeleteWithDanglingReferences() throws Exception
  {
    Product1 product = getModel1Factory().createProduct1();
    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("/r1");
    resource1.getContents().add(product);

    CDOResource resource2 = transaction.createResource("/r2");
    resource2.getContents().add(orderDetail);

    transaction.commit();

    msg("Delete and commit the resource");
    resource1.delete(null);

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      assertInstanceOf(DanglingReferenceException.class, expected.getCause());
    }
  }
}
