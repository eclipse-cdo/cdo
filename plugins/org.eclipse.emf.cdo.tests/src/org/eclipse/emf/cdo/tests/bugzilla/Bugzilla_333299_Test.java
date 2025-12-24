/*
 * Copyright (c) 2010-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
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
  @Requires(IModelConfig.CAPABILITY_LEGACY)
  public void testMoveEcoreElement() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));

      EPackage ePackage = createUniquePackage();

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
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));

      EPackage ePackage = (EPackage)resource.getContents().get(0);
      ePackage.getEClassifiers().remove(0);

      transaction.commit();
      session.close();
    }
  }
}
