/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_333299_Test extends AbstractCDOTest
{
  public void testMoveEcoreElement() throws CommitException
  {
    skipUnlessConfig(LEGACY);

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");

      EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();

      EClass eClass = EcoreFactory.eINSTANCE.createEClass();
      eClass.setName("clazz1");
      EReference eReference = EcoreFactory.eINSTANCE.createEReference();
      eReference.setName("eReference");

      eClass.getEReferences().add(eReference);

      resource.getContents().add(ePackage);
      ePackage.getEClassifiers().add(eClass);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/res1");

      EPackage ePackage = (EPackage)resource.getContents().get(0);
      ePackage.getEClassifiers().remove(0);

      transaction.commit();
      session.close();
    }
  }
}
