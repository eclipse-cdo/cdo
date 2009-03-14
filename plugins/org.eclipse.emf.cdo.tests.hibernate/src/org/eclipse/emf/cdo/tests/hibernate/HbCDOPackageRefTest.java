/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.IOUtil;

import reference.ReferenceFactory;
import reference.ReferencePackage;
import interface_.InterfacePackage;

/**
 * @author Eike Stepper
 */
public class HbCDOPackageRefTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/my/test/resource";

  public HbCDOPackageRefTest()
  {
    StoreRepositoryProvider.setInstance(HbStoreRepositoryProvider.getInstance());
  }

  public void testOnlyReference() throws Exception
  {
    try
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(InterfacePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(ReferencePackage.eINSTANCE);

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(RESOURCE_PATH);
      resource.getContents().add(ReferenceFactory.eINSTANCE.createReference());
      transaction.commit();
    }
    catch (Exception ex)
    {
      IOUtil.print(ex);
      throw ex;
    }
  }
}
