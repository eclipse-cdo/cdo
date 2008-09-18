/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

/**
 * @author Eike Stepper
 */
public class MetaTest extends AbstractCDOTest
{
  public void testMetaReference() throws Exception
  {
    {
      CDOSession session = openModel3Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource("/res");

      MetaRef metaRef = getModel3Factory().createMetaRef();
      metaRef.setEPackageRef(Model3Package.eINSTANCE);
      res.getContents().add(metaRef);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource("/res");

      MetaRef metaRef = (MetaRef)res.getContents().get(0);
      assertEquals(Model3Package.eINSTANCE, metaRef.getEPackageRef());
    }
  }
}
