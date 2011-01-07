/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

/**
 * Bug 321699 - CDOViewImpl.getObject(CDOID, boolean) can return wrong object for temporary IDs.
 * 
 * @author Caspar De Groot
 */
public class Bugzilla_321699_Test extends AbstractCDOTest
{
  public void test() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource("/r1");
    msg("Pre-commit ID of resource = " + resource.cdoID());

    CDOObject fetchedObject = tx.getObject(resource.cdoID());
    assertSame(resource, fetchedObject);

    tx.commit();

    msg("Post-commit ID of resource = " + resource.cdoID());

    Customer customer = getModel1Factory().createCustomer();
    resource.getContents().add(customer);
    CDOID customerID = CDOUtil.getCDOObject(customer).cdoID();
    msg("Pre-commit ID of customer = " + customerID);

    fetchedObject = tx.getObject(customerID);

    msg("Object fetched for customerID = " + fetchedObject);

    assertSame(CDOUtil.getEObject(customer), CDOUtil.getEObject(fetchedObject));

    tx.close();
    session.close();
  }
}
