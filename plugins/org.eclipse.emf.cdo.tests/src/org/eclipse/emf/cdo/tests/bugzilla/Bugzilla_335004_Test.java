/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

/**
 * @author Eike Stepper
 */
public class Bugzilla_335004_Test extends AbstractCDOTest
{
  public void testDuplicatePackageInSystem() throws CommitException
  {
    String nsURI = getModel1Package().getNsURI();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));
      resource.getContents().add(getModel1Factory().createAddress());
      transaction.commit();
      session.close();
    }

    assertNotNull(EPackage.Registry.INSTANCE.get(nsURI));
    Object oldPackage = EPackage.Registry.INSTANCE.remove(nsURI);

    try
    {
      assertNull(EPackage.Registry.INSTANCE.get(nsURI));

      CDOSession session = openSession();
      session.options().setGeneratedPackageEmulationEnabled(true);

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("test")); // We are loading nsURI from the server

      EObject remoteObject = resource.getContents().get(0);
      EObject localObject = getModel1Factory().createAddress();

      try
      {
        resource.getContents().add(localObject);
        assertSame(remoteObject.eClass(), localObject.eClass());
        fail("IllegalStateException expected");
      }
      catch (IllegalStateException expected)
      {
        // Success
      }
    }
    finally
    {
      if (oldPackage != null)
      {
        EPackage.Registry.INSTANCE.put(nsURI, oldPackage);
      }
    }
  }
}
