/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Michael Taal
 */
public class HibernateBugzilla_416530_Test extends AbstractCDOTest
{

  // @Override
  // public void doSetUp() throws Exception
  // {
  // org.eclipse.emf.cdo.tests.model1.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(1);
  // org.eclipse.emf.cdo.tests.model1.legacy.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(1);
  // super.doSetUp();
  // }
  //
  // @Override
  // public void doTearDown() throws Exception
  // {
  // org.eclipse.emf.cdo.tests.model1.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(0);
  // org.eclipse.emf.cdo.tests.model1.legacy.Model1Package.eINSTANCE.getSupplier_Preferred().setLowerBound(0);
  // super.doTearDown();
  // }

  public void testCreatePersist() throws Exception
  {
    // step 1: create initial objects and add to resource
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      // get/create a resource
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      // clear any previous data
      resource.getContents().clear();

      for (int i = 0; i < 100; i++)
      {
        final Company address = getModel1Factory().createCompany();
        address.setCity("test"); //$NON-NLS-1$
        String addressName = "name " + System.currentTimeMillis(); //$NON-NLS-1$
        address.setName(addressName);
        address.setStreet("test"); //$NON-NLS-1$
        resource.getContents().add(address);
      }

      transaction.commit();
    }

    // step 2: create one additional object and add to resource
    // observations: for each object in the resource, the following add will cause a select for each of that object
    {
      final CDOSession session = openSession();
      final CDOTransaction transaction = session.openTransaction();
      // get/create a resource
      CDOResource resource = transaction.getResource(getResourcePath("/test1"));

      {
        final Company address = getModel1Factory().createCompany();
        address.setCity("test"); //$NON-NLS-1$
        String addressName = "name " + System.currentTimeMillis(); //$NON-NLS-1$
        address.setName(addressName);
        address.setStreet("test"); //$NON-NLS-1$
        resource.getContents().add(address);
      }

      transaction.commit();
    }
  }
}
