/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Simple test to check that the timestamp is indeed stored. https://bugs.eclipse.org/bugs/show_bug.cgi?id=361792
 *
 * @author Martin Taal
 */
public class HibernateTimeStampTest extends AbstractCDOTest
{

  public void testTimeStamp() throws Exception
  {
    msg("Opening session");
    CDOSession session = openSession();

    CDOID cdoId = null;
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      Product1 product = getModel1Factory().createProduct1();
      product.setDescription("Description");
      product.setName("name");
      resource.getContents().add(product);
      transaction.commit();
      cdoId = ((CDOObject)product).cdoID();
    }
    session.close();

    Thread.sleep(500);

    final long checkTimeStamp = System.currentTimeMillis();

    session = openSession();

    CDOTransaction transaction = session.openTransaction();
    final CDOObject cdoObject = transaction.getObject(cdoId);
    final long timeStamp = cdoObject.cdoRevision().getTimeStamp();
    if (checkTimeStamp < timeStamp)
    {
      fail("Timestamp not stored");
    }
    transaction.commit();
  }
}
